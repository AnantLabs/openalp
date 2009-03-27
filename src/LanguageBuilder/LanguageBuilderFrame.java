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
 *  The main frame for the language builder.
 *
 *  This is also the point where the application is set up and the core is initialised.
 *
 * @author      Adam Scarr
 * @since       r37
 **/

package LanguageBuilder;

import Core.LexiconDAO;
import Core.Grammar;
import javax.swing.*;
import Graph.GraphView;
import GenericComponents.Console;
import java.awt.*;
import LanguageBuilder.commands.*;

public class LanguageBuilderFrame extends JFrame {
    LexiconDAO lexicon = new LexiconDAO();
    Grammar grammar = new Grammar(lexicon);
    GraphView graphView = new GraphView(grammar.getGraph());
    Console console = new Console();

    public LanguageBuilderFrame() {
        setLayout(new BorderLayout());
        add(graphView, BorderLayout.CENTER);
        add(console, BorderLayout.SOUTH);

        initConsole();

        graphView.setDoubleBuffered(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setMinimumSize(new Dimension(800, 600));
        setPreferredSize(new Dimension(1024, 768));

        setVisible(true);
    }

    private void initConsole() {
        console.addCommandListener(new Add(grammar));
        console.addCommandListener(new Check(grammar));
        console.addCommandListener(new Define(lexicon));
        console.addCommandListener(new Exit());
        console.addCommandListener(new Help());
        console.addCommandListener(new Remove(lexicon));
        console.addCommandListener(new Reset(grammar));
        console.addCommandListener(new Search(lexicon));
        console.addCommandListener(new Test(grammar));
        console.addCommandListener(new Train(grammar));
        
        console.setDefaultListener(new Check(grammar));
    }
}
