package Graph;

import Core.GrammarEdge;

import java.util.LinkedList;
import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Adam Scarr
 * Date: 13/08/2008
 * Time: 22:13:23
 * description: A graph node of Type.
 */
public class Node<NodeType, EdgeType> {
	private LinkedList<Edge<NodeType, EdgeType>> edges = new LinkedList<Edge<NodeType, EdgeType>>();
	private static final float springeness = 1.05f;
	private static final float springLength = 0.7f;
	private static final float repelClamp = 75.0f;
	private static final float falloff = 0.55f;
	private float attractionX, attractionY;
	private float repulsionX, repulsionY;
	private float x = (float)Math.random();
	private float y = (float)Math.random();
	private float dx = 0;
	private float dy = 0;
	public static final int size = 30;
	private boolean locked = false;
	private NodeType data;

	//----------------------------------------
	// Constructors
	//----------------------------------------

	public Node() {}

	public Node(NodeType data) {
		this.data = data;
	}

	//----------------------------------------
	// Simple getters
	//----------------------------------------

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public NodeType getData() {
		return data;
	}

	public boolean isLocked() {
		return locked;
	}

	public int getSize() {
		return locked ? (int)(size * 1.5): size;
	}

	public LinkedList<Edge<NodeType, EdgeType>> getEdges() {
		return edges;
	}

	//----------------------------------------
	// Non mutating logic
	//----------------------------------------

	// Returns a list of all edges that are connected in a direction that is traversable.
	public LinkedList<Edge<NodeType, EdgeType>> getOutgoingEdges() {
		LinkedList<Edge<NodeType, EdgeType>> outgoing = new LinkedList<Edge<NodeType, EdgeType>>();

		for(Edge<NodeType, EdgeType> edge: edges) {
			if(edge.getSrc() == this || (edge.getDest() == this && !edge.isDirected())) {
				outgoing.add(edge);
			}
		}

		return outgoing;
	}

	// Returns a list of all nodes that can be reached from this node.
	public LinkedList<Node<NodeType, EdgeType>> getConnectedNodes() {
		LinkedList<Node<NodeType, EdgeType>> connected = new LinkedList<Node<NodeType, EdgeType>>();

		for(Edge<NodeType, EdgeType> edge: edges) {
			if(edge.getSrc() == this) {
				connected.add(edge.getDest());
			} else if(edge.getDest() == this && !edge.isDirected()) {
				connected.add(edge.getSrc());
			}
		}

		return connected;
	}

	// If the data implements the Colored interface then use the colors they give us otherwise supply a nice default.
	// Todo: Add a generic colorization algorithim for non colored data.
	public Color getColor() {
		if(data == null) {
			return Color.WHITE;
		}

		Class[] interfaces = data.getClass().getInterfaces();

		for (Class i : interfaces) {
			if (i == Colored.class) {
				return ((Colored)data).getColor();
			}
		}

		return Color.GREEN;
	}

	// If the data implements the Labeled interface then use it, otherwise returns and empty string.
	public String getLabel() {
		if(data == null) {
			return "";
		}

		Class[] interfaces = data.getClass().getInterfaces();

		for (Class i : interfaces) {
			if(i == Labeled.class) {
				return ((Labeled)data).getLabel();
			}
		}

		return "";
	}

	public void draw(int x, int y, Graphics g) {
		if(data == null) {
			return;
		}

		Class[] interfaces = data.getClass().getInterfaces();

		for (Class i : interfaces) {
			if(i == Drawable.class) {
				((Drawable)data).draw(x, y, g);
				return;
			}
		}

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

	//----------------------------------------
	// Connectors
	//----------------------------------------

	// (this) --> (dest)
	public void connectsTo(Node<NodeType, EdgeType> dest, EdgeType edge) {
		for(Edge e: edges) {
			if(e.getSrc().equals(dest)) {                                // Link already exists in the other direction.
				e.setDirected(false);
				return;
			} else if(e.getDest().equals(dest)) {                        // Link already exists in this direction.
				return;
			}
		}

		Edge<NodeType, EdgeType> e = new Edge<NodeType, EdgeType>(this, dest, true,edge);

		edges.add(e);
		dest.edges.add(e);
	}

	// (src) --> (this)
	public void connectsFrom(Node<NodeType, EdgeType> src, EdgeType edge) {

		for(Edge e: edges) {
			if(e.getDest().equals(src)) {                                // Link already exists in the other direction.
				e.setDirected(false);
				return;
			} else if(e.getSrc().equals(src)) {                          // Link already exists in this direction.
				return;
			}
		}

		Edge<NodeType, EdgeType> e = new Edge<NodeType, EdgeType>(src, this, true,edge);

		edges.add(e);
		src.edges.add(e);
	}

	// (this) <--> (dest)
	public void connectsBothWays(Node<NodeType, EdgeType> dest, EdgeType edge) {

		for(Edge e: edges) {
			if(e.getDest().equals(dest) || e.getSrc().equals(dest)) {    // Link already exists in either direction.
				e.setDirected(false);
				return;
			}
		}

		Edge<NodeType, EdgeType> e = new Edge<NodeType, EdgeType>(dest, this, false,edge);

		edges.add(e);
		dest.edges.add(e);
	}

	//----------------------------------------
	// Mutators 
	//----------------------------------------

	// Updates the repulsive forces based on all the other nodes in the graph.
	public void calcRepulsion(LinkedList<Node<NodeType, EdgeType>> graphNodes) {
		repulsionX = 0;
		repulsionY = 0;

		for(Node q: graphNodes) {
			if(q != this) {
				float dx = Math.abs(x - q.x);
				float dy = Math.abs(y - q.y);
				float hyp = (float)Math.sqrt(dx * dx + dy * dy);
				float theta = (float)(Math.atan(dx/dy));
				int directionX = (x > q.x) ? -1 : 1;
				int directionY = (y > q.y) ? -1 : 1;
				float force = -10 / hyp;

				repulsionX += force * (float)(Math.sin(theta) / hyp) * directionX;
				repulsionY += force * (float)(Math.cos(theta) / hyp) * directionY;

				// There is a 'popcorn' like effect that happens when two nodes get too close to each other
				// probaby as a result of multiple springs pushing on it. To try and limit this the maximum
				// repulsion force is limited to a certain value.
				// Todo: logarithmic scaling between 0 and repelClamp rather then a hard clamp?
				if(repulsionY > repelClamp) {
					repulsionY = repelClamp;
				} else if (repulsionY < -repelClamp) {
					repulsionY = -repelClamp;
				}

				if(repulsionX > repelClamp) {
					repulsionX = repelClamp;
				} else if (repulsionX < -repelClamp) {
					repulsionX = -repelClamp;
				}
			}
		}
	}

	// update the attraction based on all connected nodes (either direction).
	public void calcAttraction() {
		int directionX, directionY;
		float dx, dy, hyp, theta;
		Node<NodeType, EdgeType> that;
		attractionX = 0;
		attractionY = 0;

		for(Edge<NodeType, EdgeType> edge: edges) {
			if(!(edge.getSrc() == this && edge.getDest() == this)) {
				if(this == edge.getSrc()) {
					that = edge.getDest();
				} else {
					that = edge.getSrc();
				}
				dx = Math.abs(this.x - that.x);
				dy = Math.abs(this.y - that.y);
				hyp = (float)Math.sqrt(dx * dx + dy * dy);
				theta = (float)(Math.atan(dx/dy));

				directionX = (this.x > that.x) ? -1 : 1;
				directionY = (this.y > that.y) ? -1 : 1;

				float force = -(springeness * edge.getEdgeStrength()) * (springLength - hyp);

				attractionX += force * (float)(Math.sin(theta) / hyp) * directionX;
				attractionY += force * (float)(Math.cos(theta) / hyp) * directionY;
			}
		}
	}

	// updates position based on the attraction and replusion
	public float updatePosition(LinkedList<Node<NodeType, EdgeType>> graphNodes) {
		if(!locked) {

			calcRepulsion(graphNodes);
			calcAttraction();
			float delta = Math.abs(repulsionX + attractionX) + Math.abs(repulsionY + attractionY);  // Its not really accurate but it is fast!
			dx += (repulsionX + attractionX);
			dy += (repulsionY + attractionY);
			x += dx;
			y += dy;

			dx *= falloff;
			dy *= falloff;

			return delta;
		} else {
			return 0.0f;
		}
	}

}
