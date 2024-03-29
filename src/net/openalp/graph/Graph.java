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
 *  A class that wraps a set of graphing tools, designed to split some of the workload in
 *  OpenALP.
 *
 *  It should be thread safe and support loops, but if it breaks you can keep both peices.
 *
 * @author      Adam Scarr
 * @since       r1
 **/

package net.openalp.graph;

import java.util.LinkedList;
import java.util.Random;


public class Graph {
	private LinkedList<Node> nodes = new LinkedList<Node>();
	private static final Random rand = new Random();
	private float minX, maxX, minY, maxY;
	private float delta;
	private int usn;
    
    public Graph() {};

	//----------------------------------------
	// Simple getters
	//----------------------------------------
	public float getMaxY() {
		return maxY;
	}

	public float getMinX() {
		return minX;
	}

	public float getMaxX() {
		return maxX;
	}

	public float getMinY() {
		return minY;
	}

	public int size() {
		return nodes.size();
	}

	public Node getNode(int i) {
		return nodes.get(i);
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}

	public float getDelta() {
		return delta;
	}

    public void setMinX(float minX) {
        this.minX = minX;
    }

    public void setMaxX(float maxX) {
        this.maxX = maxX;
    }

    public void setMinY(float minY) {
        this.minY = minY;
    }

    public void setMaxY(float maxY) {
        this.maxY = maxY;
    }

    public void setDelta(float delta) {
        this.delta = delta;
    }

    public void setNodes(LinkedList<Node> nodes) {
        this.nodes = nodes;
    }

    //----------------------------------------
	// Non mutating logic
	//----------------------------------------

	// Causes the calling thread to sleep until the graph has changed.
	public void waitForUpdate() {
		int usn = this.usn;
		while(usn == this.usn) {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//----------------------------------------
	// Mutators
	//----------------------------------------

	public void clear() {
		synchronized(nodes) {
            nodes.clear();
        };
	}

	// forces an update of the graph (call me if youve only made changes to the connections and not created any new nodes)
	public void touch() {
		usn++;
	}

	// Creates a new node as part of this graph.
	public Node createNode() {
		Node n = new Node();
		nodes.add(n);
		usn++;

		return n;
	}

    public void addNode(Node node) {
        nodes.add(node);
        usn++;
    }

    public void connect(Edge e) {
        Node src = e.getSrc();
        Node dest = e.getDest();

        if(!src.hasChild(dest)) {
            if(dest.hasChild(src)) {
                dest.getEdgeTo(src).setDirected(false);
            } else {
                src.addEdge(e);
                dest.addEdge(e);
            }
        }
    }

	// Creates a graph of num randomly connected nodes
	public void generateRandomNodes(int num) {
		Node n;
		for(int i = 0; i < num; i++) {
			n = createNode();

			if(i > 0) {
                connect(new Edge(n, nodes.get(rand.nextInt(nodes.size()))));
			}
		}
		usn++;
	}

	// Updates all the nodes position based on a force based algorithm.
	public void updateNodes() {
		synchronized(nodes) {
            float maxX = Float.NEGATIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            float minX = Float.POSITIVE_INFINITY;
            float minY = Float.POSITIVE_INFINITY;
            float delta = 0;

            for(Node n: nodes) {
                delta += n.updatePosition(nodes);
                if(n.getX() < minX) { minX = n.getX(); }
                if(n.getX() > maxX) { maxX = n.getX(); }
                if(n.getY() < minY) { minY = n.getY(); }
                if(n.getY() > maxY) { maxY = n.getY(); }
            }

            this.maxX = maxX;
            this.maxY = maxY;
            this.minX = minX;
            this.minY = minY;
            this.delta = delta;
        }
	}
}
