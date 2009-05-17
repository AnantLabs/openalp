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
 *  A GrammarNode represents an exact part of speech used in a particular context.
 *  For example a 3rd person present tense noun that follows a pronoun.
 *
 * @author      Adam Scarr
 * @author      Rowan Spence
 * @since       r1
 **/

package net.openalp.core;

import net.openalp.graph.Node;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class GrammarNode extends Node {
	private String type;
	private boolean firstPerson, secondPerson, thirdPerson;
	private boolean pastTense, presentTense, futureTense;
	private static int bubbleSize = 15;

	//----------------------------------------
	// Constructors
	//----------------------------------------
    public GrammarNode() { };

	public GrammarNode(Token t) {
		type = t.getType();
		firstPerson = t.isFirstPerson();
		secondPerson = t.isSecondPerson();
		thirdPerson = t.isThirdPerson();
		pastTense = t.isPastTense();
		presentTense = t.isPresentTense();
		futureTense = t.isFutureTense();
	}

	//----------------------------------------
	// Simple getters
	//----------------------------------------

	public String getType() {
		return type;
	}

	public boolean isEnd() {
		return type.equals("END");
	}

	public boolean isStart() {
		return type.equals("START");
	}

	public String getLabel() {
		return type;
	}

    public boolean isFirstPerson() {
        return firstPerson;
    }

    public boolean isSecondPerson() {
        return secondPerson;
    }

    public boolean isThirdPerson() {
        return thirdPerson;
    }

    public boolean isPastTense() {
        return pastTense;
    }

    public boolean isPresentTense() {
        return presentTense;
    }

    public boolean isFutureTense() {
        return futureTense;
    }

    public String toString() {
        return type;
    }

    //----------------------------------------
	// Simple setters
	//----------------------------------------

    public void setFirstPerson(boolean firstPerson) {
        this.firstPerson = firstPerson;
    }

    public void setSecondPerson(boolean secondPerson) {
        this.secondPerson = secondPerson;
    }

    public void setThirdPerson(boolean thirdPerson) {
        this.thirdPerson = thirdPerson;
    }

    public void setPastTense(boolean pastTense) {
        this.pastTense = pastTense;
    }

    public void setPresentTense(boolean presentTense) {
        this.presentTense = presentTense;
    }

    public void setFutureTense(boolean futureTense) {
        this.futureTense = futureTense;
    }
    //----------------------------------------
	// Non mutating logic
	//----------------------------------------

	public Color getColor() {
		if(type.equals("ADJ")) { return Color.BLUE; }
		if(type.equals("ART")) { return new Color(128, 0, 128); }
		if(type.equals("NOUN")) { return Color.WHITE; }
		if(type.equals("PROPER")) { return Color.WHITE; }
		if(type.equals("ADV")) { return Color.DARK_GRAY; }
		if(type.equals("VERB")) { return Color.LIGHT_GRAY; }
		if(type.equals("PREP")) { return Color.BLACK; }
		if(type.equals("COMMA")) { return Color.YELLOW; }
		if(type.equals("PERIOD")) { return Color.YELLOW; }

		if(type.equals("UNDEF")) { return Color.ORANGE; }
		if(type.equals("START")) { return Color.GREEN; }
		if(type.equals("END")) { return Color.RED; }
		return Color.RED;
	}

	//----------------------------------------
	// Simple setters
	//----------------------------------------

	public void setType(String type) {
		this.type = type;
	}

	// Returns if token is of the same type and for any flag set in the node it must be set in the token.
	public boolean matches(Token token) {
		return token.getType().equals(type) && !(firstPerson != token.isFirstPerson()) && !(secondPerson != token.isSecondPerson())&& !(thirdPerson != token.isThirdPerson()) &&
				                                 !(pastTense != token.isPastTense()) && !(presentTense != token.isPresentTense())&& !(futureTense != token.isFutureTense());
	}

	public void drawBubble(Graphics g, int centerX, int centerY, Color color, int angle, String label) {
		float radius = getSize() / 2.0f;
		int bubbleRadius = (int)(bubbleSize / 2.0f);

		int x = (int)(centerX + radius * Math.cos(Math.toRadians(angle)));
		int y = (int)(centerY - radius * Math.sin(Math.toRadians(angle)));

		g.setColor(color);
		g.fillOval(x - bubbleRadius, y - bubbleRadius, bubbleSize, bubbleSize);
		g.setColor(Color.DARK_GRAY);
		g.drawOval(x - bubbleRadius, y - bubbleRadius, bubbleSize, bubbleSize);
		Rectangle2D rect = g.getFontMetrics().getStringBounds(label, g);
		g.drawString(label, x - (int)rect.getWidth() / 2, y + (int)rect.getHeight() / 2);
	}

	public void draw(int x, int y, Graphics g) {
		int radius = getSize() / 2;
		Rectangle2D area = g.getFontMetrics().getStringBounds(getLabel(), g);
		int w = (int)area.getWidth();
		int h = (int)area.getHeight();

		g.setColor(getColor());
		g.fillOval(x - radius,  y - radius, getSize(), getSize());
		g.setColor(Color.DARK_GRAY);
		g.drawOval(x - radius,  y - radius, getSize(), getSize());

		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x - w / 2 - 1, y - h / 2 - 3,w + 2, h + 2);
		g.setColor(Color.GRAY);
		g.drawRect(x - w / 2 - 1, y - h / 2 - 3,w + 2, h + 2);

		g.drawString(getLabel(), x - w / 2, y + h / 2 - 2);

		if(firstPerson) drawBubble(g, x, y, Color.YELLOW, 150, "1st");
		if(secondPerson) drawBubble(g, x, y, Color.YELLOW, 90, "2nd");
		if(thirdPerson) drawBubble(g, x, y, Color.YELLOW, 30, "3rd");

		if(pastTense) drawBubble(g, x, y, Color.YELLOW, 210, "<-");
		if(presentTense) drawBubble(g, x, y, Color.YELLOW, 270, " \\/");
		if(futureTense) drawBubble(g, x, y, Color.YELLOW, 330, "->");

	}
}
