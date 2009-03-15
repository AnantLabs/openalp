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
 *  A representation of a connection between two Nodes in the Grammar Graph.
 *  Used to collect statistics about the number of traversals.
 *
 *  The more times an edge is traversed the stronger it becomes, if a particular
 *  sentance structure has been seen once in 10,000 sentances then it may not
 *  even be valid. These statistics are very important for the 'fuzzy' logic
 *  part of OpenALP.
 *
 *  @author      Adam Scarr
 *  @author      Rowan Spence
 *  @see         Grammar
 *  @see         GrammarNode
 *  @since       r1
 **/

package Core;

import Graph.Colored;
import Graph.VariableStrength;
import java.awt.*;

public class GrammarEdge implements Colored, VariableStrength {
	private int usageCount = 1;
    private Grammar grammar;

    public GrammarEdge(Grammar grammar) {

        this.grammar = grammar;
    }

    public int getUsageCount() {
		return usageCount;
	}

    public float getWeight() {
	    float weight = usageCount / (float)grammar.getTotalSentences();
       if(weight > 1.0f) {
	       return 1.0f;
       } else {
	       return weight;
       }
    }

	public float getStrength() {
		return ((getWeight() - 0.5f) / 1.25f) + 1.0f;
	}

    public void incrementUsageCount(){
		usageCount++;
	}


    public Color getColor() {
        float weight = getWeight();
        return new Color(0.8f - weight * 0.6f, 0.2f + weight * 0.6f, 0.5f - Math.abs(weight - 0.5f));
    }
}
