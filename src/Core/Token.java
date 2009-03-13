package Core;

import Graph.NodeFilter;

/**
 * User: Adam Scarr
 * Date: 14/08/2008
 * Time: 12:05:32
 * Description: A token is a word with is associated data ie. the word type, its perspective(3rd person 1st person)
 */
public class Token implements NodeFilter<GrammarNode> {
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

    public boolean matches(GrammarNode target) {
        return target.matches(this);
    }
}
