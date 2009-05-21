/**
 *  This file is part of OpenALP.
 *
 *  OpenALP is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  OpenALP is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with OpenALP.  If not, see <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>.
 *
 *  Grammar allows the dynamic manipulation of the grammar, and includes the
 *  methods required to test a sentance against the derived grammar.
 *
 *  @author      Adam Scarr
 *  @author      Rowan Spence
 *  @since       r1
 **/

package net.openalp.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import net.openalp.graph.Edge;
import net.openalp.graph.Graph;
import net.openalp.graph.Node;
import net.openalp.graph.NodeFilter;

/**
 * The heart of OpenALP. The grammar wraps the graph and provides functions for adding and checking grammar.
 */
public class Grammar {
	private Node start;
	private Graph graph;
    private Tokenizer tokenizer;
	private int totalSentences;
    private LexiconDAO lexicon;


    public Grammar() { };

    /**
     * Constructor
     * @param lexicon The lexicon that will form the source of all words used in the grammar.
     */
	public Grammar(LexiconDAO lexicon) {
		graph = new Graph();
        tokenizer = new Tokenizer(this.lexicon = lexicon);
        clear();
	}

    /**
     * Removes all nodes from the graph and resets its internal counters.
     */
    public synchronized void clear() {
        graph.clear();
		start = new GrammarNode(new Token("START", "START", false, false, false, false, false, false));
		graph.addNode(start);
		totalSentences = 0;
	}

    /**
     * Getter
     * @return Returns the special 'start' token. All sentances start here.
     */
	public synchronized Node getStart() {
		return start;
	}

    /**
     * Getter
     * @return The graph that holdes the information about the structure of the grammar.
     */
	public Graph getGraph() {
		return graph;
	}

    /**
     * Getter
     * @return The number of sentances parsed.
     */
	public synchronized int getTotalSentences(){
		return totalSentences;
	}

    /**
     * Getter
     * @return The Lexicon being used.
     */
    public LexiconDAO getLexicon() {
        return lexicon;
    }

    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    public void setStart(Node start) {
        this.start = start;
    }

    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    public void setTokenizer(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public void setTotalSentences(int totalSentences) {
        this.totalSentences = totalSentences;
    }

    public void setLexicon(LexiconDAO lexicon) {
        this.lexicon = lexicon;
    }

    /**
     * Checks if a sentance has a valid path from start.
     * @param sentance The sentance to check.
     * @return the 'validity' of a sentance.
     */
	public ParseResult validateSentence(Sentence sentance) {
        // TODO: Ask Dimitry about this, dosent seem right to need to create a new list just so
        //Java knows that all elements implement a given interface.
        List<NodeFilter> filterList = new LinkedList<NodeFilter>(sentance);
        List<Node> path = start.getMatchedPath(filterList);
        ParseResult result = new ParseResult();

        // First check the path actually made it to the end...
        float validity = 1;
        if(path.size() > 0 && path.get(path.size() - 1).isTerminal()) {
            // Walk the path and calculate the validity.
            Node last = null;
            for(Node node: path) {
                Edge edge = node.getEdgeTo(last);

                if(edge != null) {
                    validity *= edge.getStrength();
                    ((GrammarEdge)edge).activate();
                }

                last = node;
            }

        } else {
            validity = 0;
        }

        result.setValidity(validity);
        
        return result;
	}

    /**
     * Validates a block of text.
     * @param text  The text to validate
     * @return  A parse result for every sentance, including any errors.
     */
    public List<ParseResult> validate(String text) {
        List<ParseResult> result = new Vector<ParseResult>();

        List<TokenizedSentence> tokenizingResult = tokenizer.tokenize(text.toLowerCase());

        for(TokenizedSentence sentence: tokenizingResult) {
            ParseResult parseResult = calculateSentenceValidity(sentence);
            parseResult.setStart(sentence.getStart());
            parseResult.setEnd(sentence.getEnd());
            System.out.println(parseResult.getStart() + " to " + parseResult.getEnd());
            result.add(parseResult);
        }



        return result;
    }

    /**
     * Calculates the validity of a sentance.
     * @param inputSentence The sentance to validate
     * @return the validity of the sentance.
     */
	public ParseResult calculateSentenceValidity(String inputSentence) {
		TokenizedSentence tokenizingResult = tokenizer.tokenize(inputSentence.toLowerCase()).get(0);
        return calculateSentenceValidity(tokenizingResult);
	}

    public ParseResult calculateSentenceValidity(TokenizedSentence tokenizedSentence) {
        ParseResult best = new ParseResult(Float.NEGATIVE_INFINITY);

        for(Sentence sentance: tokenizedSentence.getSentences()) {
            ParseResult result = validateSentence(sentance);
            if(result.getValidity() > best.getValidity()) {
                best = result;
            }
        }

        best.setTokenizingResult(tokenizedSentence);

        return best;
    }

    /**
     * Parses a sentance, adding it to the grammar if it does not exist.
     * @param sentance  The sentance to parse.
     * @return true if the sentance is already valid, false if new paths were created.
     */

	public boolean parse(String sentance) {
        List<TokenizedSentence> foo = tokenizer.tokenize(sentance.toLowerCase());
        //if(foo.isEmpty()) return false;
		TokenizedSentence tokenizingResult = foo.get(0);

        // Check if there are any valid sentances that match this structure.
        boolean exists = false;
        Sentence bestSentance = null;
        
        ParseResult best = new ParseResult(Float.NEGATIVE_INFINITY);
        
        for(Sentence tokenizedSentance: tokenizingResult.getSentences()) {
            ParseResult parseResult = validateSentence(tokenizedSentance);
            if(parseResult.getValidity() > best.getValidity()) {
                best = parseResult;
                bestSentance = tokenizedSentance;
            }

            if(parseResult.isValid()) {
                exists = true;
                addPath(tokenizedSentance);
            }
        }

        if(!exists) {
            addPath(bestSentance);
        }

		// Simple grammar add.

        return exists;
    }

    /**
     * Adds the given tokens to the grammar, creating as few nodes as possible.
     * It works by matching as many tokens as possible then when it finds a non-matching
     * token it creates the shortest chain possible to re-attach to the graph.
     * @param tokens    Tokenized sentance to add.
     */
    public void addPath(Sentence tokens) {
        synchronized(graph.getNodes()) {
            totalSentences++;
            Node pathStart = start;
            Node fork;
            Node lastNode = null;
            List<Node> matchedPath;
            Chain chain;

            do {
                // Consume as many tokens as we can.
                if(tokens.getFirst().matches(pathStart)) {
                    tokens.removeFirst();
                }
                LinkedList<NodeFilter> filterList = new LinkedList<NodeFilter>(tokens);
                matchedPath = pathStart.getMatchedPath(filterList);

                // Remove them from the token list and strengthen the paths.
                for(Node node: matchedPath) {
                    Edge edge = node.getEdgeTo(lastNode);
                    if(edge != null) {
                        ((GrammarEdge)edge).incrementUsageCount();
                    }
                    if(node != start && tokens.size() > 0) {
                        tokens.removeFirst();
                    }
                    lastNode = node;
                }

                if(matchedPath.size() == 0) {
                    fork = pathStart;
                } else {
                    fork = matchedPath.get(matchedPath.size() - 1);
                }

                chain = new Chain(this, tokens, fork);
                lastNode = chain.mergeInto(this);

            } while(tokens.size() > 0);

        }
    }
        
}
