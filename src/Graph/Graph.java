package Graph;

import java.util.LinkedList;
import java.util.Random;

/**
 * User: Adam Scarr
 * Date: 13/08/2008
 * Time: 22:13:05
 * description: A directed graph using adjacency lists.
 * todo: make me thread safe! lock the nodes so only one thread can use it at a time etc! should stop the tearing.
 */
public class Graph<NodeType, EdgeType> {
	private LinkedList<Node<NodeType, EdgeType>> nodes = new LinkedList<Node<NodeType, EdgeType>>();
	private static final Random rand = new Random();
	private float minX, maxX, minY, maxY;
	private float delta;
	private int usn;
	private boolean nodesWriteLocked = false;
	private int readLocks = 0;

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

	public LinkedList<Node<NodeType, EdgeType>> getNodes() {
		return nodes;
	}

	public float getDelta() {
		return delta;
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
		nodes.clear();
	}

	// forces an update of the graph (call me if youve only made changes to the connections and not created any new nodes)
	public void touch() {
		usn++;
	}

	// Creates a new node as part of this graph.
	public Node<NodeType, EdgeType> createNode() {
		Node<NodeType, EdgeType> n = new Node<NodeType, EdgeType>();
		nodes.add(n);
		usn++;

		return n;
	}

	// Creates a new node containing data as part of this graph.
	public Node<NodeType, EdgeType> createNode(NodeType data) {
		Node<NodeType, EdgeType> n = new Node<NodeType, EdgeType>(data);
		nodes.add(n);
		usn++;

		return n;
	}

	// Creates a graph of num randomly connected nodes
	public void generateRandomNodes(int num) {
		Node<NodeType, EdgeType> n;
		for(int i = 0; i < num; i++) {
			n = createNode();

			if(i > 0) {
				n.connectsTo(nodes.get(rand.nextInt(nodes.size())), null);
//				n.connectsFrom(nodes.get(rand.nextInt(nodes.size())));
			}
		}
		usn++;
	}

	// Locks the nodes for exclusive write access, preventing any other threads from accessing them.
	// If someone already has locked the nodes for read access wait until they are done.
	public synchronized void lockNodesRW() {
		while(readLocks > 0 || nodesWriteLocked) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		nodesWriteLocked = true;
	}

	public void unlockNodesRW() {
		nodesWriteLocked = false;
	}

	// Locks the nodes for read access, many threads can read at the same time, but if someone has the nodes
	// locked for write access we must wait.
	public synchronized void lockNodesRO() {
		while(nodesWriteLocked) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		readLocks++;
	}

	public void unlockNodesRO() {
		readLocks--;
	}

	// Updates all the nodes position based on a force based algorithm.
	public void updateNodes() {
		lockNodesRO();
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float delta = 0;

		for(Node<NodeType, EdgeType> n: nodes) {
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
		unlockNodesRO();
	}
}
