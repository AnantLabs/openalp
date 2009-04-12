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

    /**
     * Constructor
     * @param lexicon The lexicon that will form the source of all words used in the grammar.
     */
	public Grammar(LexiconDAO lexicon) {
		graph = new Graph();
        tokenizer = new Tokenizer(lexicon);
        clear();
	}

    /**
     * Removes all nodes from the graph and resets its internal counters.
     */
    public void clear() {
        graph.lockNodesRW();
        graph.clear();
		start = new GrammarNode(new Token("START", "START", false, false, false, false, false, false));
		graph.addNode(start);
		totalSentences = 0;
        graph.unlockNodesRW();
	}

    /**
     * Getter
     * @return Returns the special 'start' token. All sentances start here.
     */
	public Node getStart() {
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
	public int getTotalSentences(){
		return totalSentences;
	}

    /**
     * Checks if a sentance has a valid path from start.
     * @param sentance The sentance to check.
     * @return the 'validity' of a sentance.
     */
	public float validateSentance(Sentance sentance) {
        graph.lockNodesRO();
        // TODO: Ask Dimitry about this, dosent seem right to need to create a new list just so
        //       Java knows that all elements implement a given interface.
        LinkedList<NodeFilter> filterList = new LinkedList<NodeFilter>(sentance);
        LinkedList<Node> path = start.getMatchedPath(filterList);

        // First check the path actually made it to the end...
        float validity = 1;
        if(path.size() > 0 && path.getLast().isTerminal()) {
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
        
        graph.unlockNodesRO();
        return validity;
	}

    /**
     * Calculates the validity of each tokenized sentance in the set
     * and returns the validity of the best.
     * @param sentances The tokenized sentances.
     * @return  The validity of the most-valid sentance.
     */
    public float validateSentances(LinkedList<Sentance> sentances) {
        float best = Float.NEGATIVE_INFINITY;

        for(Sentance sentance: sentances) {
            float validity = validateSentance(sentance);
            if(validity > best) {
                best = validity;
            }
        }

        return best;
    }

    /**
     * Calculates the validity of a sentance.
     * @param sentance The sentance to validate
     * @return the validity of the sentance.
     */
	public float calculateSentanceValidity(String sentance) {
		return validateSentances(tokenizer.tokenize(sentance.toLowerCase()));
	}

    /**
     * Parses a sentance, adding it to the grammar if it does not exist.
     * @param sentance  The sentance to parse.
     * @return true if the sentance is already valid, false if new paths were created.
     */

	public boolean parse(String sentance) {
		LinkedList<Sentance> sentances = tokenizer.tokenize(sentance.toLowerCase());

        // Check if there are any valid sentances that match this structure.
        boolean exists = false;
        Sentance bestSentance = null;
        float bestValidity = Float.NEGATIVE_INFINITY;
        for(Sentance tokenizedSentance: sentances) {
            float validity = validateSentance(tokenizedSentance);
            if(validity > bestValidity) {
                bestValidity = validity;
                bestSentance = tokenizedSentance;
            }

            if(validity > 0.0) {
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
    public void addPath(Sentance tokens) {
        graph.lockNodesRW();
        totalSentences++;
        Node pathStart = start;
        Node fork;
        Node lastNode = null;
        LinkedList<Node> matchedPath;
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
                fork = matchedPath.getLast();
            }

            chain = new Chain(this, tokens, fork);
            lastNode = chain.mergeInto(this);

        } while(tokens.size() > 0);

        graph.unlockNodesRW();
    }
        
}
