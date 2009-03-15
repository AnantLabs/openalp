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
 *  This lightweight swing component creates a text console with
 *  command history, and sends commands off to the appropriate classes.
 *
 *  TODO: Split the OpenALP logic out of here. Should be in a derived class somewhere. This should be generic.
 *
 * @author      Adam Scarr
 * @since       r1
 **/

package Interface;

import Core.Grammar;
import Core.Lexicon;
import Core.Token;
import Core.FileParser;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.io.*;

public class Console extends JPanel implements ActionListener, KeyListener {
	private Grammar grammar;
	private Lexicon lex;
	private JTextField input = new JTextField();
	private JTextArea log = new JTextArea();
	PrintStream aPrintStream  = new PrintStream(new FilteredStream (new ByteArrayOutputStream()));
	private int nextSlot = MAX_SLOTS - 1;
	private int selectedSlot = 0;
	private static final int MAX_SLOTS = 10;
	private String[] command = new String[MAX_SLOTS];
	private static final int KEY_UP = 38;
	private static final int KEY_DOWN = 40;

	//----------------------------------------
	// Constructors
	//----------------------------------------

	Console(Grammar grammar, Lexicon lex) {
		this.grammar = grammar;
		this.lex = lex;
		input.addActionListener(this);

		log.setBackground(new Color(220, 220, 220));
		log.setRows(10);

		System.setOut(aPrintStream);
//      System.setErr(aPrintStream);               // Comment out to hide debugging messages.

		JScrollPane logScrollPane = new JScrollPane(log);
		logScrollPane.setWheelScrollingEnabled(true);

		logScrollPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		log.setEditable(false);
		log.setAutoscrolls(true);

		input.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

		setLayout(new BorderLayout());
		add(logScrollPane, BorderLayout.CENTER);
		add(input, BorderLayout.SOUTH);
		input.addKeyListener(this);
	}

	private void addCommand(String c) {
		command[nextSlot] = c;
		while(--nextSlot < 0) nextSlot += MAX_SLOTS + 1;
		command[nextSlot] = "";
		selectedSlot = 0;
	}

	private String getSelectedSlot() {
		
		return this.command[(nextSlot + selectedSlot) % MAX_SLOTS];
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode() == KEY_UP) {
			selectedSlot++;
			input.setText(getSelectedSlot());
		}

		if(e.getKeyCode() == KEY_DOWN) {
			selectedSlot--;
			input.setText(getSelectedSlot());
		}
	}

	//----------------------------------------
	// Private classes
	//----------------------------------------

	// An output stream that writes to the log box.
	private class FilteredStream extends FilterOutputStream {
		public FilteredStream(OutputStream aStream) {
			super(aStream);
		}

		public void write(byte b[]) throws IOException {
			log.append(new String(b));
		}

      public void write(byte b[], int off, int len) throws IOException {
			log.append(new String(b, off, len));
		}
	}

	public void runCommand(String command) {
		String[] word = command.split(" ");
		String params = null;
		if(command.length() > word[0].length()) {
			params = command.substring(word[0].length() + 1);
		}

		if(word[0].equalsIgnoreCase("echo"))   { System.out.println(params); }
		if(word[0].equalsIgnoreCase("exit"))   { System.exit(0); }
		if(word[0].equalsIgnoreCase("add"))    { grammar.parse(params);}
		if(word[0].equalsIgnoreCase("define")) {
			int perspectiveMask = Integer.parseInt(word[3]);
			int tenseMask = Integer.parseInt(word[4]);
			
			lex.add(new Token(word[1], word[2], (perspectiveMask & 4) == 4, (perspectiveMask & 2) == 2,(perspectiveMask & 1) == 1,
					                              (tenseMask & 4) == 4, (tenseMask & 2) == 2, (tenseMask & 1) == 1));
		}
		if(word[0].equalsIgnoreCase("remove")) { lex.remove(word[1]); }
		if(word[0].equalsIgnoreCase("search")) {
			if(word.length > 1) {
				System.out.println(lex.get(word[1]));
			} else {
				System.out.println("Please give a word to search for from the lexicon:");
				System.out.println(lex);
			}
		}
		if(word[0].equalsIgnoreCase("train"))  {
            FileParser parser = new FileParser(grammar);
            parser.parseFile("data/" + word[1]);
        }

		if(word[0].equalsIgnoreCase("test"))   {
            FileParser parser = new FileParser(grammar);
            System.out.println(parser.testFile("data/" + word[1]));
        }

		if(word[0].equalsIgnoreCase("clear"))  { grammar.clear(); }

		if(word[0].equalsIgnoreCase("help") || word[0].equalsIgnoreCase("?")) {
			System.out.println("::help::");
			System.out.println("  add (sentance) - Create a new path in the grammar");
			System.out.println("  define (word) (type) - Create a word in the dictionary");
			System.out.println("  remove (word) - Removes a word from the dictionary");
			System.out.println("  search (word) - Finds a word in the dictionary");
			System.out.println("  test (filename) - Runs the given test file through the grammar");
			System.out.println("  train (filename) - Runs the given file into the parser building the grammar");
			System.out.println("  help - Displays this message");
			System.out.println("  exit - Leave this wonderfull program");
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = input.getText();
		
		addCommand(command);

		if(command.charAt(0) == '\\' || command.charAt(0) == '/') {
			runCommand(command.substring(1));
		} else {
			// Any text typed into the console directly will be checked against the grammar (not added)
			System.out.print("Checking '" + command + "': ");

			System.out.println(grammar.calculateSentanceValidity(command));
		}

		input.setText("");
	}
}
