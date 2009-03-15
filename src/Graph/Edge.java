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
 *  Representation of the connection between two nodes. Edges may also have data if you want to add
 *  additional data to them.
 *
 * @author      Adam Scarr
 * @since       r1
 **/

package Graph;

import java.awt.*;

public class Edge<NodeType, EdgeType> {
	private Node<NodeType, EdgeType> src, dest;
	private boolean directed = true;
	private EdgeType data;

	//----------------------------------------
	// Constructors
	//----------------------------------------

	// Creates a new edge from src to dest, in that direction if its directed.
	Edge(Node<NodeType, EdgeType> src, Node<NodeType, EdgeType> dest, boolean directed, EdgeType init) {
		this.src = src;
		this.dest = dest;
		this.directed = directed;
		data = init;

	}

	// Creates a new directed edge from src to dest.
	Edge(Node<NodeType, EdgeType> src, Node<NodeType, EdgeType> dest, EdgeType init) {
		this.src = src;
		this.dest = dest;
        data = init;
    }

	//----------------------------------------
	// Simple getters
	//----------------------------------------
	public Node<NodeType, EdgeType> getDest() {
		return dest;
	}

	public Node<NodeType, EdgeType> getSrc() {
		return src;
	}

	public boolean isDirected() {
		return directed;
	}

	public EdgeType getData() {
		return data;
	}

	public float getEdgeStrength() {
		Class[] interfaces = data.getClass().getInterfaces();

		for (Class i : interfaces) {
			if (i == VariableStrength.class) {
				return ((VariableStrength)data).getStrength();
			}
		}

		return 1.0f;
	}

//----------------------------------------
	// Simple setters
	//----------------------------------------

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

    public Color getColor() {
        //System.out.println(src.getLabel() + " -> " + dest.getLabel() + ": " + ((GrammarEdge)data).getUsageCount());
        if(data == null) {
			return Color.WHITE;
		}

		Class[] interfaces = data.getClass().getInterfaces();

		for (Class i : interfaces) {
			if (i == Colored.class) {
				return ((Colored)data).getColor();
			}
		}

		return Color.BLUE;
	}
}
