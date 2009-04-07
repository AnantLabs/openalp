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
 *  A token is the smallest possible part of a sentance that carrys grammatical meaning.
 *  They carry the meaning of:
 *  <ul>
 *    <li>Part of Speed (nouns, verbs, adjectives, etc)</li>
 *    <li>Presence (first person, 2nd person, 3rd person)</li>
 *    <li>Tense (past, present, future)</li>
 *  </ul>
 *
 *  TODO: We may need to add a few more parts of speed like finite verbs, or add a finite flag...
 *
 * @author      Adam Scarr
 * @author      Rowan Spence
 * @since       r1
 **/

package net.openalp.core;

import net.openalp.graph.NodeFilter;
import net.openalp.graph.Node;

public class Token implements NodeFilter {
	private String value;
	private String type;
    private boolean firstPerson, secondPerson, thirdPerson;
    private boolean pastTense, presentTense, futureTense;

    //----------------------------------------
	// Constructors
	//----------------------------------------

	public Token(String value) {
		this.value = value;
		this.type = "UNDEF";
	}

	public Token(String word, String type, boolean firstPerson, boolean secondPerson, boolean thirdPerson,
	                                       boolean pastTense, boolean presentTense, boolean futureTense) {
		this.type = type;
		this.value = word;
		this.firstPerson = firstPerson;
		this.secondPerson = secondPerson;
		this.thirdPerson = thirdPerson;
		this.pastTense = pastTense;
		this.presentTense = presentTense;
		this.futureTense = futureTense;
    }

	//----------------------------------------
	// Simple getters
	//----------------------------------------

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("Token: ").append(value).append("\n");
		s.append("  Type: ").append(type).append("\n");
		s.append("  Flags: ");
		if(firstPerson) { s.append("1st "); }
		if(secondPerson) { s.append("2nd "); }
		if(thirdPerson) { s.append("3rd "); }
		if(pastTense) { s.append("PAST "); }
		if(presentTense) { s.append("PRESENT "); }
		if(futureTense) { s.append("FUTURE "); }
		s.append("\n");
		return s.toString();
	}

	public String getType() {
		return type;
	}

	public String  getValue() {
		return value;
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

    public boolean matches(Node target) {
        return ((GrammarNode)target).matches(this);
    }
}
