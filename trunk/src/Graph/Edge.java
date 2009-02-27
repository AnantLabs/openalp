package Graph;

import Core.GrammarEdge;

import java.awt.*;

/**
 * User: Adam Scarr
 * Date: 13/08/2008
 * Time: 22:14:19
 * description: An edge connects two nodes in a graph
 */
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
        System.out.println(src.getLabel() + " -> " + dest.getLabel() + ": " + ((GrammarEdge)data).getUsageCount());
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
