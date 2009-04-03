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

public class Edge {
	private Node src, dest;
	private boolean directed = true;

	//----------------------------------------
	// Constructors
	//----------------------------------------

	// Creates a new edge from src to dest, in that direction if its directed.
	public Edge(Node src, Node dest, boolean directed) {
		this.src = src;
		this.dest = dest;
		this.directed = directed;
	}

	// Creates a new directed edge from src to dest.
	public Edge(Node src, Node dest) {
		this.src = src;
		this.dest = dest;
    }

	//----------------------------------------
	// Simple getters
	//----------------------------------------
	public Node getDest() {
		return dest;
	}

	public Node getSrc() {
		return src;
	}

	public boolean isDirected() {
		return directed;
	}

	public float getStrength() {
		return 1.0f;
	}

//----------------------------------------
	// Simple setters
	//----------------------------------------

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

    public Color getColor() {
		return Color.BLUE;
	}
}
