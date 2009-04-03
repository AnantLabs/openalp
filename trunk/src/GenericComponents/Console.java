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
 * @author      Adam Scarr
 * @since       r1
 **/

package GenericComponents;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.*;
import java.io.*;
import java.util.LinkedList;

public class Console extends JPanel implements ActionListener, KeyListener {
	private JTextField input = new JTextField();
	private JTextArea log = new JTextArea();
    private int nextSlot = MAX_SLOTS - 1;
	private int selectedSlot = 0;
	private static final int MAX_SLOTS = 10;
	private String[] command = new String[MAX_SLOTS];
	private static final int KEY_UP = 38;
	private static final int KEY_DOWN = 40;
    private LinkedList<CommandListener> commandListeners = new LinkedList<CommandListener>();
    private CommandListener defaultListener;

    //----------------------------------------
	// Constructors
	//----------------------------------------

	public Console() {
		input.addActionListener(this);

		log.setBackground(new Color(220, 220, 220));
		log.setRows(10);

        PrintStream aPrintStream = new PrintStream(new FilteredStream(new ByteArrayOutputStream()));
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

    public void addCommandListener(CommandListener listener) {
        commandListeners.add(listener);
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

    public void setDefaultListener(CommandListener defaultListener) {
        this.defaultListener = defaultListener;
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

        for(CommandListener commandListener: commandListeners) {
            if(word[0].equalsIgnoreCase(commandListener.getCommand())) {
                commandListener.runCommand(this, word, word.length);
            }
        }
	}

	public void actionPerformed(ActionEvent e) {
		String command = input.getText();
		
		addCommand(command);
        if(command.length() > 0) {
            if(command.charAt(0) == '\\' || command.charAt(0) == '/') {
                runCommand(command.substring(1));
            } else {
                String[] word = command.split(" ");
                defaultListener.runCommand(this, word, word.length);

            }
        }

		input.setText("");
	}
}
