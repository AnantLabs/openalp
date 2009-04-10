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

    public Chain(Grammar grammar, Sentance tokens, Node fork) {
        Node lastNode = null;
        Node node;
        boolean diverge = false;

        for(Token token: tokens) {
            // If a conjunction appears in a chain then it can only rejoin the grammar
            // when a sentance terminator appears.
            if(token.getType().equals("CONJ") || token.isTerminator()) {
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

        // Remove the used tokens.
        for(int i = 0; i < size(); i++) {
            tokens.removeFirst();
        }
    }

    public Node getMergePoint() {
        return merge;
    }
}
