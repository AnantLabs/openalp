package Core;

import Graph.Colored;
import Graph.Labeled;
import Graph.Drawable;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * User: Adam Scarr
 * Date: 14/08/2008
 * Time: 14:48:45
 * Description: All the data associated with a particular node in the grammar graph.
 */
public class GrammarNode implements Colored, Labeled, Drawable  {
	private String type;
	private boolean firstPerson, secondPerson, thirdPerson;
	private boolean pastTense, presentTense, futureTense;
	private static int diamater = 40;
	private static int bubbleSize = 15;

	//----------------------------------------
	// Constructors
	//----------------------------------------

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
		float radius = diamater / 2.0f;
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
		int radius = diamater / 2;
		Rectangle2D area = g.getFontMetrics().getStringBounds(getLabel(), g);
		int w = (int)area.getWidth();
		int h = (int)area.getHeight();

		g.setColor(getColor());
		g.fillOval(x - radius,  y - radius, diamater, diamater);
		g.setColor(Color.DARK_GRAY);
		g.drawOval(x - radius,  y - radius, diamater, diamater);

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
