package LanguageBuilder.commands;

import GenericComponents.CommandListener;
import GenericComponents.Console;
import Graph.Node;

/**
 * Author: Adam Scarr
 * Date: 03/04/2009
 * Time: 10:23:17 PM
 */
public class Set implements CommandListener {

    public String getCommand() {
        return "set";
    }

    public void runCommand(Console console, String[] argv, int argc) {


        if(argv.length < 3) {
            System.out.println("set varname value");
            System.out.println("");
            System.out.println("Available vars:");
            System.out.println("node.springyness - The force a spring has");
            System.out.println("node.springlynessfalloff - the amount a springs force decreases per jump");
            System.out.println("node.springlength - the length of a spring");
            System.out.println("node.springlengthextra - how much to add to a spring per jump.");
            return;
        }

        String var = argv[1];

        if(var.equalsIgnoreCase("node.springyness")) {
            Node.springyness = Float.valueOf(argv[2]);
        }

        else if(var.equalsIgnoreCase("node.springynessfalloff")) {
            Node.springeynessFalloff = Float.valueOf(argv[2]);
        }

        else if(var.equalsIgnoreCase("node.springlength")) {
            Node.springLength = Float.valueOf(argv[2]);
        }

        else if(var.equalsIgnoreCase("node.springLengthextra")) {
            Node.springLengthExtra = Float.valueOf(argv[2]);
        }

        else {
            System.out.println("Unknown command.");
        }



    }
}
