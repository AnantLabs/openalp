package Interface;

import Core.Lexicon;
import Core.Grammar;

import javax.swing.*;

import Graph.*;

import java.awt.*;

/**
 * User: Adam Scarr
 * Date: 14/08/2008
 * Time: 15:23:04
 * Description: todo
 */
public class RealtimeVisualizer extends JFrame {
	//----------------------------------------
	// Constructors
	//----------------------------------------

	public RealtimeVisualizer() {
		Lexicon lexicon = new Lexicon();
		Grammar grammar = new Grammar(lexicon);
		GraphView graphView = new GraphView(grammar.getGraph());
		Console console = new Console(grammar, lexicon);

		setLayout(new BorderLayout());
		add(graphView, BorderLayout.CENTER);
		add(console, BorderLayout.SOUTH);

		graphView.setDoubleBuffered(true);

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setMinimumSize(new Dimension(800, 600));
		setPreferredSize(new Dimension(1024, 768));

		setVisible(true);
	}
}
