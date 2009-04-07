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

package net.openalp.core;

import net.openalp.graph.Edge;
import net.openalp.graph.Node;

import java.awt.*;

public class GrammarEdge extends Edge {
	private int usageCount = 1;
    private Grammar grammar;
    private float activityFactor = 1.0f;

    public GrammarEdge(Node src, Node dest, Grammar grammar) {
        super(src, dest);
        this.grammar = grammar;
    }

    public GrammarEdge(Node src, Node dest, boolean directed, Grammar grammar) {
        super(src, dest, directed);
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
        activate();
	}

    public void activate() {
        activityFactor = 1.0f;
    }

    public void age() {
        activityFactor *= 0.99;
    }


    public Color getColor() {
        float weight = getWeight();
        age();

        float red = 0.8f - weight * 0.6f - activityFactor;
        float green =  0.2f + weight * 0.6f + (activityFactor / 2.0f);
        float blue = 0.5f - Math.abs(weight - 0.5f) + activityFactor;

        if(red > 1.0f) red = 1.0f;
        if(red < 0.0f) red = 0.0f;
        if(green > 1.0f) green = 1.0f;
        if(green < 0.0f) green = 0.0f;
        if(blue > 1.0f) blue = 1.0f;
        if(blue < 0.0f) blue = 0.0f;

        return new Color(red, green, blue);
    }
}
