package net.openalp.languagebuilder.commands;

import net.openalp.core.encoding.GrammarDecoder;
import net.openalp.core.Grammar;
import net.openalp.generic.swing.Console;
import net.openalp.generic.swing.CommandListener;
import net.openalp.languagebuilder.LanguageBuilderFrame;

/**
 * Created by IntelliJ IDEA.
 * User: NecromancyBlack
 * Date: 21/05/2009
 * Time: 8:45:09 PM
 */
public class Load implements CommandListener {
    private GrammarDecoder loader = new GrammarDecoder();
    private LanguageBuilderFrame frame;

    public Load(LanguageBuilderFrame frame) {
        this.frame = frame;
    }

    public String getCommand() {
        return "load";
    }

    public void runCommand(Console console, String[] argv, int argc) {
        if(argv.length > 1) {
            Grammar grammar = (Grammar)loader.load(argv[1]);
            if (grammar != null) {
                frame.setGrammar(grammar);
                System.out.println(argv[1] + " loaded.");
            }
            else {
                System.out.println("Failed to load  " + argv[1]);
            }
        }
        else {
            System.out.println("Please enter a filename to load.");
        }

    }
}
