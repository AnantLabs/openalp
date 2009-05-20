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
 *  A Lightweight swing component used for visualising a Graph.
 *
 *  Uses a real time force-based algorithm that looks quite nice. Nodes
 *  can be locked (no longer move based on force) by clicking and dragging
 *  them to wherever you want to place them, and unlocked by right clicking.
 *
 *  Currently has trouble with more complex graphs but we may be able to
 *  get around this by adding some repulsive force to edges.
 *
 * @author      Adam Scarr
 * @since       r1
 **/

package net.openalp.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.List;

public class GraphView extends JComponent implements Runnable, MouseMotionListener, MouseListener {
	private Graph graph;
	private Node dragNode = null;
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
        List<Node> nodes = graph.getNodes();
		synchronized(nodes) {
            for(Node n: nodes) {
                float dx = Math.abs(screenX(n.getX()) - x);
                float dy = Math.abs(screenY(n.getY()) - y);
                float distance = (float)Math.sqrt(dx*dx + dy*dy);

                if(distance < (n.getSize() / 2)) {
                    return n;
                }
            }

            return null;
        }
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

		synchronized(graph.getNodes()) {

            g.setFont(new Font("Verdana", Font.PLAIN, 8));
            // Then draw nodes over top.
            for(Node node: graph.getNodes()) {
                int x = screenX(node.getX());
                int y = screenY(node.getY());

                node.draw(x, y, g);

                for(Edge edge: node.getEdges()) {
                    connect(g, edge);
                }
            }
        }

	}

	//----------------------------------------
	// Mutators
	//----------------------------------------



	@SuppressWarnings({"InfiniteLoopStatement"})	
	public void run() {
		while(true) {
			graph.updateNodes();
            graph.updateNodes();
            graph.updateNodes();

//			if(graph.getDelta() < 0.01f) {
//				graph.waitForUpdate();
//				graph.updateNodes();
//			}

			repaint();
			
			try {
				Thread.sleep(1);
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
