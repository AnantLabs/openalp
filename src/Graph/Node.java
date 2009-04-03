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
 *  A Node in a Graph. Nodes can have many connections to other nodes (both directed and non-directed).
 *  You can modify the visual aspect of the nodes by implementing the interfaces
 *  in this package.
 *
 * @author      Adam Scarr
 * @see         Colored
 * @see         Drawable
 * @see         Labeled
 * @see         VariableStrength
 * @since       r1
 **/

package Graph;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Node {
	private LinkedList<Edge> edges = new LinkedList<Edge>();
	public static float springyness = 1.3f;
    public static float springeynessFalloff = 0.25f;
	public static float springLength = 1.0f;
    public static float springLengthExtra = 1.66f;
	private float x = (float)Math.random();
	private float y = (float)Math.random();
	private float dx = 0;
	private float dy = 0;
	private static final int size = 37;
	private boolean locked = false;
    private static final float dampening = 0.75f;


    //----------------------------------------
	// Constructors
	//----------------------------------------

	public Node() {}

	//----------------------------------------
	// Simple getters
	//----------------------------------------

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public boolean isLocked() {
		return locked;
	}

	public int getSize() {
		return locked ? (int)(size * 1.5): size;
	}

	public LinkedList<Edge> getEdges() {
		return edges;
	}

    /**
     * Returns the edge that connects this node to the destination node. Does not take into account
     * the direction of the edge.
     * @param destination The node you want the edge to.
     * @return an edge that connects to destination, or null if one does not exists.
     */
    public Edge getEdgeTo(Node destination) {
        for(Edge e: edges) {
            if(e.getDest() == destination || e.getSrc() == destination) {
                return e;
            }
        }

        return null;
    }

    // Searches from this node through the graph to find a node matching the filter.
    // If none is found it returns null, otherwise it returns the path to that node.
    public Node findMatchingNode(NodeFilter filter) {
        Queue<LinkedList<Node>> searchQueue = new ConcurrentLinkedQueue<LinkedList<Node>>();
        LinkedList<Node> examined = new LinkedList<Node>();

        LinkedList<Node> start = new LinkedList<Node>();
        start.add(this);

        searchQueue.add(start);

        LinkedList<Node> path;

        while((path = searchQueue.poll()) != null) {

            Node endNode = path.getLast();

            examined.add(endNode);

            //  If we have found a match return the path.
            if (filter.matches(endNode)) {
                return endNode;
            }

            // otherwise add all the child nodes for examinations.
            for(Node linkedNode: endNode.getConnectedNodes()) {
                if(!examined.contains(linkedNode)) {
                    LinkedList<Node> newPath = new LinkedList<Node>(path);
                    newPath.add(linkedNode);

                    searchQueue.add(newPath);
                }
            }
        }

        return null;
    }

    // Returns a path through where every node matches the filter for that step.
    // If we cannot find a complete path, the longest partial match will be returned.
    public LinkedList<Node> getMatchedPath(LinkedList<NodeFilter> filter) {
        Queue<LinkedList<Node>> searchQueue = new ConcurrentLinkedQueue<LinkedList<Node>>();
        LinkedList<Node> longestpath = new LinkedList<Node>();

        LinkedList<Node> start = new LinkedList<Node>();
        start.add(this);

        searchQueue.add(start);

        LinkedList<Node> path;
        int i = 0;
        while((path = searchQueue.poll()) != null) {
            Node lastNode = path.getLast();

            // We have consumed all our tokens, and they all match. Good Work!
            // Give the user his path.
            if(i >= filter.size()) {
                return path;
            }


            for(Node linkedNode: lastNode.getConnectedNodes()) {
                if(filter.get(i).matches(linkedNode)) {
                    LinkedList<Node> newPath = new LinkedList<Node>(path);
                    newPath.add(linkedNode);

                    searchQueue.add(newPath);

                    if(newPath.size() > longestpath.size()) {
                        longestpath = newPath;
                    }
                }
            }
            i++;
        }

        return longestpath;

    }

    // Attempts to find a path between this node and the destination node.
    // Makes use of a depth-first search so nearby entrys will be matched quickly.
    // Loop safe.
    // Returns null if no match is found.
    public LinkedList<Node> getPath(Node destination) {
        Queue<LinkedList<Node>> searchQueue = new ConcurrentLinkedQueue<LinkedList<Node>>();
        LinkedList<Node> examined = new LinkedList<Node>();

        LinkedList<Node> start = new LinkedList<Node>();
        start.add(this);

        searchQueue.add(start);

        LinkedList<Node> path;

        while((path = searchQueue.poll()) != null) {

            Node lastNode = path.getLast();

            examined.add(lastNode);

            if (lastNode == destination) {
                return path;
            }

            for(Node linkedNode: lastNode.getConnectedNodes()) {
                if(!examined.contains(linkedNode)) {
                    LinkedList<Node> newPath = new LinkedList<Node>(path);
                    newPath.add(linkedNode);

                    searchQueue.add(newPath);
                }
            }
        }

        return null;

    }

    public int getDistanceTo(Node destination) {
        Queue<LinkedList<Node>> searchQueue = new ConcurrentLinkedQueue<LinkedList<Node>>();
        LinkedList<Node> examined = new LinkedList<Node>();

        LinkedList<Node> start = new LinkedList<Node>();
        start.add(this);

        searchQueue.add(start);

        LinkedList<Node> path;

        while((path = searchQueue.poll()) != null) {

            Node lastNode = path.getLast();

            examined.add(lastNode);

            if (lastNode == destination) {
                return path.size() - 1;
            }

            for(Node linkedNode: lastNode.getConnectedNodes(false)) {
                if(!examined.contains(linkedNode)) {
                    LinkedList<Node> newPath = new LinkedList<Node>(path);
                    newPath.add(linkedNode);

                    searchQueue.add(newPath);
                }
            }
        }

        return 0;
    }

	//----------------------------------------
	// Non mutating logic
	//----------------------------------------

	// Returns a list of all edges that are connected in a direction that is traversable.
	public LinkedList<Edge> getOutgoingEdges() {
		LinkedList<Edge> outgoing = new LinkedList<Edge>();

		for(Edge edge: edges) {
			if(edge.getSrc() == this || (edge.getDest() == this && !edge.isDirected())) {
				outgoing.add(edge);
			}
		}

		return outgoing;
	}

    public LinkedList<Node> getConnectedNodes() {
        return getConnectedNodes(true);
    }

	// Returns a list of all nodes that can be reached from this node.
	public LinkedList<Node> getConnectedNodes(boolean directed) {
		LinkedList<Node> connected = new LinkedList<Node>();

		for(Edge edge: edges) {
			if(edge.getSrc() == this) {
				connected.add(edge.getDest());
			} else if(edge.getDest() == this && !(edge.isDirected() && directed)) {
				connected.add(edge.getSrc());
			}
		}

		return connected;
	}

	// If the data implements the Colored interface then use the colors they give us otherwise supply a nice default.
	// Todo: Add a generic colorization algorithim for non colored data.
	public Color getColor() {
		return Color.GREEN;
	}

    public String toString() {
        return getLabel();
    }

	// If the data implements the Labeled interface then use it, otherwise returns and empty string.
	public String getLabel() {
		return "";
	}

	public void draw(int x, int y, Graphics g) {
		Rectangle2D area = g.getFontMetrics().getStringBounds(getLabel(), g);
		int diamater = getSize();
		int radius = diamater / 2;
		int w = (int)area.getWidth();
		int h = (int)area.getHeight();

		g.setColor(getColor());
		g.fillOval(x - radius,  y - radius, diamater, diamater);
		g.setColor(Color.GRAY);
		g.drawOval(x - radius,  y - radius, diamater, diamater);
		
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x - w / 2 - 1, y - h / 2 - 3,w + 2, h + 2);
		g.setColor(Color.GRAY);
		g.drawRect(x - w / 2 - 1, y - h / 2 - 3,w + 2, h + 2);

		g.drawString(getLabel(), x - w / 2, y + h / 2 - 2);
	}

	//----------------------------------------
	// Simple setters
	//----------------------------------------

	public void lock() {
		locked = true;
	}

	public void unlock() {
		locked = false;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

    public void addEdge(Edge e) {
        if(!edges.contains(e)) {
            edges.add(e);
        }
    }

    public Force calcSpringForce(Node that) {
       // System.out.println(this.getLabel() + " -> " + that.getLabel());
        float dx = Math.abs(this.x - that.x);
        float dy = Math.abs(this.y - that.y);
        float hyp = (float)Math.sqrt(dx * dx + dy * dy);
        if(hyp == 0.0f) return new Force(0.0f, 0.0f);

        float theta = (float)(Math.atan(dx/dy));

        if(Float.isNaN(theta)) return new Force(0.0f, 0.0f);

        float directionX = (this.x > that.x) ? -1 : 1;
        float directionY = (this.y > that.y) ? -1 : 1;
        float distance = getDistanceTo(that);

        if(distance == 0) return new Force(0.0f, 0.0f);

        float force = -(springyness * (float)Math.pow(springeynessFalloff, distance)) * ((springLength * (distance + (springLengthExtra * (distance - 1)))) - hyp);


        Force f =  new Force(force * (float)(Math.sin(theta) / hyp) * directionX,
                             force * (float)(Math.cos(theta) / hyp) * directionY);

        if(distance > 1) {
            //System.out.println(f);
        }

        return f;
    }

	public float updatePosition(LinkedList<Node> nodes) {
        if(locked) return 0.0f;

        Force net = new Force(0,0);

        for(Node node: nodes) net.add(calcSpringForce(node));

        dx = (dx + net.getX()) * dampening;
        dy = (dy + net.getY()) * dampening;

        x += dx;
        y += dy;

        return (dx * dx) + (dy * dy);
    }


    public boolean hasChild(Node child) {
        for(Node node: getConnectedNodes()) {
            if(node == child) {
                return true;
            }
        }
        
        return false;
    }
}
