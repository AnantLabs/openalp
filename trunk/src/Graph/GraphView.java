package Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;

/**
 * User: Adam Scarr
 * Date: 13/08/2008
 * Time: 23:03:28
 * description: Swing component used for displaying a Graph
 */
public class GraphView extends JComponent implements Runnable, MouseMotionListener, MouseListener {
	private Graph graph;
	private Node dragNode = null;
	private static final Color labelColor = new Color(225, 225, 225);
	private static final int arrowWidth = 4;
	private static final int arrowLength = 7;

	//----------------------------------------
	// Constructors
	//----------------------------------------

	public GraphView(Graph graph) {
		this.graph = graph;
		graph.updateNodes();

		addMouseMotionListener(this);
		addMouseListener(this);

		new Thread(this).start();
	}

	//----------------------------------------
	// Non mutating logic
	//----------------------------------------

	// Converts x-axis coordinates from graph space to screen space.
	public int screenX(float graphX) {
		return (int)((graphX - graph.getMinX()) / (graph.getMaxX() - graph.getMinX()) * (getWidth() - 100)) + 50;
	}

	// Converts y-axis coordinates from graph space to screen space.
	public int screenY(float graphY) {
		return (int)((graphY - graph.getMinY()) / (graph.getMaxY() - graph.getMinY()) * (getHeight() - 100)) + 50;
	}

	// Converts x-axis coordinates from screen space to graph space.
	public float graphX(int screenX) {
		return ((float)(screenX - 50) / (float)(getWidth() - 100)) * (graph.getMaxX() - graph.getMinX()) + graph.getMinX();
	}

	// Converts y-axis coordinates from screen space to graph space.
	public float graphY(int screenY) {
		return ((float)(screenY - 50) / (float)(getHeight() - 100)) * (graph.getMaxY() - graph.getMinY()) + graph.getMinY();
	}

	// If there is a node under the cursor it returns it, otherwise it returns null.
	// We do some more suspiscious casting, but we dont really care about the data.
	@SuppressWarnings({"unchecked"})
	public Node getNodeAt(int x, int y) {
		graph.lockNodesRO();
		for(Node n: (LinkedList<Node>)graph.getNodes()) {
			float dx = Math.abs(screenX(n.getX()) - x);
			float dy = Math.abs(screenY(n.getY()) - y);
			float distance = (float)Math.sqrt(dx*dx + dy*dy);

			if(distance < (n.getSize() / 2)) {
				graph.unlockNodesRO();
				return n;
			}
		}
		graph.unlockNodesRO();

		return null;
	}

	// creates a link between two nodes, lots of vector math here...
	// First part calculates where the line ends based on the size of the nodes
	// if block calculates the position of the two 'base' points for the arrow.
	public void connect(Graphics g,Edge edge) {
		int dx = screenX(edge.getDest().getX()) - screenX(edge.getSrc().getX());
		int dy = screenY(edge.getDest().getY()) - screenY(edge.getSrc().getY());
		float mag = (float)Math.sqrt(dx * dx + dy * dy);
		float normalX = dx / mag;
		float normalY = dy / mag;
		int srcDiamater = edge.getSrc().getSize() / 2 + 1;
		int destDiamater = edge.getDest().getSize() / 2 + 1;

		int srcX = screenX(edge.getSrc().getX()) + (int)(normalX * srcDiamater);
		int srcY = screenY(edge.getSrc().getY()) + (int)(normalY * srcDiamater);
		int destX = screenX(edge.getDest().getX()) - (int)(normalX * destDiamater);
		int destY = screenY(edge.getDest().getY()) - (int)(normalY * destDiamater);

		g.setColor(edge.getColor());
		g.drawLine(srcX, srcY, destX, destY);

		if(edge.isDirected()) {
			int arrowLeftX = destX - (int)(normalX * arrowLength) + (int)(normalY * arrowWidth);
			int arrowLeftY = destY - (int)(normalY * arrowLength) + (int)(-normalX * arrowWidth);
			int arrowRightX = destX - (int)(normalX * arrowLength) - (int)(normalY * arrowWidth);
			int arrowRightY = destY - (int)(normalY * arrowLength) - (int)(-normalX * arrowWidth);

			Polygon arrow = new Polygon();
			arrow.addPoint(destX, destY);
			arrow.addPoint(arrowLeftX, arrowLeftY);
			arrow.addPoint(arrowRightX, arrowRightY);
			g.fillPolygon(arrow);
		}
	}

	@SuppressWarnings({"unchecked"})
	// We do a few ""Unsafe"" casts, but at this point we really dont care what the interernal data type is just
	// make sure not to use getData without doing some kind of introspection.
	public void paintComponent(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.DARK_GRAY);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		g.setColor(Color.GRAY);

		graph.lockNodesRO();
		// Draw edges

		g.setFont(new Font("Verdana", Font.PLAIN, 8));
		// Then draw nodes over top.
		for(Node node: (LinkedList<Node>)graph.getNodes()) {
			int x = screenX(node.getX());
			int y = screenY(node.getY());

			node.draw(x, y, g);

			for(Edge edge: (LinkedList<Edge>)node.getEdges()) {
				connect(g, edge);
			}
		}

		graph.unlockNodesRO();
	}

	//----------------------------------------
	// Mutators
	//----------------------------------------



	@SuppressWarnings({"InfiniteLoopStatement"})	
	public void run() {
		while(true) {
			graph.updateNodes();
			graph.updateNodes();

			if(graph.getDelta() < 0.01f) {
				graph.waitForUpdate();
				graph.updateNodes();
			}

			repaint();
			
			try {
				Thread.sleep(30);
			} catch(InterruptedException e) {
				System.out.print("Boom!");
			}
		}
	}

	//----------------------------------------
	// Entry point
	//----------------------------------------

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		Graph g = new Graph();
		g.generateRandomNodes(50);
		GraphView gp = new GraphView(g);

		frame.add(gp);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Dimension dim = new Dimension(800, 800);
		frame.setMinimumSize(dim);
		frame.setMaximumSize(dim);
		frame.setPreferredSize(dim);
		frame.setVisible(true);
		frame.repaint();
	}

	public void mouseDragged(MouseEvent event) {
		if(dragNode == null) {
			dragNode = getNodeAt(event.getX(), event.getY());
		} else {
			dragNode.setX(graphX(event.getX()));
			dragNode.setY(graphY(event.getY()));
			dragNode.lock();
			graph.touch();
		}
	}

	public void mouseMoved(MouseEvent event) {

	}

	public void mouseClicked(MouseEvent event) {
		if(event.getButton() == 3) {
			Node n = getNodeAt(event.getX(), event.getY());
			if(n != null) {
				n.unlock();
				System.out.println("Unlocked node!");
				graph.touch();
			}
		}
	}

	public void mousePressed(MouseEvent event) {

	}

	public void mouseReleased(MouseEvent event) {
	   dragNode = null;
	}

	public void mouseEntered(MouseEvent event) {

	}

	public void mouseExited(MouseEvent event) {

	}
}
