package net.openalp.languagebuilder.commands;

import net.openalp.generic.swing.CommandListener;
import net.openalp.generic.swing.Console;
import net.openalp.core.Grammar;
import net.openalp.core.encoding.GrammarEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: NecromancyBlack
 * Date: 21/05/2009
 * Time: 8:01:37 PM
 */
public class Save implements CommandListener {
    private GrammarEncoder saver;
     private final String FILEPATH = "./data/";

    public Save(Grammar grammar) {
        saver = new GrammarEncoder(grammar);
    }

    public String getCommand() {
        return "save";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        if(argv.length > 1) {
            if (saver.save(FILEPATH + argv[1])) {
                System.out.println("\n" + argv[1] + " saved.");
            }
            else {
                System.out.println("Failed to save " + argv[1]);
            }
        }
        else {
            System.out.println("Please enter a filename to save too.");
        }
    }
}
