package net.openalp.core;

import net.openalp.graph.Node;

import java.util.LinkedList;

/**
 * This file is part of OpenALP.
 * <p/>
 * OpenALP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * OpenALP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with OpenALP.  If not, see <a href='http://www.gnu.org/licenses/'>http://www.gnu.org/licenses/</a>.
 * <p/>
 *
 * A chain is a list of nodes, with each node connected to the next.
 * They are created during the processing of adding grammar, when
 * no more tokens can be matched to the existing graph.
 *
 * @author Adam Scarr
 * @since r55
 */
public class Chain extends LinkedList<Node> {
    private Node merge;
    private Node fork;

    /**
     * Creates a new chain starting at fork. It will also
     * mark the merge point if one exists. It will not join
     * the chain to the graph or change the graph in any way.
     *
     * The actual algorithm will simply search for the
     * first token in the sentance that exists in fork's decendants.
     *
     * @param grammar The grammar we are building
     * @param sentance The partially parsed sentance.
     * @param fork The point that the sentance broke from the graph.
     */
    public Chain(Grammar grammar, Sentence sentance, Node fork) {
        this.fork = fork;
        Node lastNode = null;
        Node node;
        boolean diverge = false;

        for(Token token: sentance) {
            // If a conjunction appears in a chain then it can only rejoin the grammar
            // when a sentance terminator appears.
            if(token.getType().equals("CONJ") || token.getType().equals("COMMA") || token.isTerminator()) {
                diverge = true;
            }

            if(!diverge) {
                merge = fork.findMatchingNode(token, true);
            }

            if(merge != null) {
                break;
            }
            node = new GrammarNode(token);
            grammar.getGraph().addNode(node);

            if(lastNode != null) {
                grammar.getGraph().connect(new GrammarEdge(lastNode, node, grammar));
            }

            add(node);

            lastNode = node;

        }

        // Remove the used sentance.
        for(int i = 0; i < size(); i++) {
            sentance.removeFirst();
        }
    }

    /**
     * @return The point that the chain rejoins the graph, or null if its terminal.
     */
    public Node getMergePoint() {
        return merge;
    }

    /**
     * @return The point that the chain left the graph.
     */
    public Node getForkPoint() {
        return fork;
    }

    /**
     * Merges a chain into a grammar and returns the last valid token which is
     * the merge point in most castes, the last token in terminal chains
     * and the fork point in zero length terminal cases.
     * @param grammar The grammar to use
     * @return The last node touched in the merge.
     */
    public Node mergeInto(Grammar grammar) {
        if(merge != null) {
            if(size() == 0) {
                grammar.getGraph().connect(new GrammarEdge(fork, merge, grammar));
            } else {
                grammar.getGraph().connect(new GrammarEdge(fork, getFirst(), grammar));
                grammar.getGraph().connect(new GrammarEdge(getLast(), merge, grammar));
            }

            return merge;
        } else {
            if(size() > 0) {
                grammar.getGraph().connect(new GrammarEdge(fork, getFirst(), grammar));
                return getLast();
            } else {
                return fork;
            }
        }
    }
}
