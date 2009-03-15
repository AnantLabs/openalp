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
 *  This creates is a small application that allows some manual manipulation
 *  of a Grammar. Includes a display of the Grammar Graph and a console.
 *
 *  TODO: split this off into its own package, with a derived console and all its logic.
 *
 * @author      Adam Scarr
 * @since       r1
 **/


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
