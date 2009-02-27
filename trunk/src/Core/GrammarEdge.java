package Core;

import Graph.Colored;
import Graph.VariableStrength;

import java.awt.*;

/**
 * User: Necromancy Black
 * Date: 8/09/2008
 * Time: 11:27:11
 * Desc: For keeping track of edge usage for wieghts
 */
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
        System.out.println(weight);
        return new Color(0.8f - weight * 0.6f, 0.2f + weight * 0.6f, 0.5f - Math.abs(weight - 0.5f));
    }
}
