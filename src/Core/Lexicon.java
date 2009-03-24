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
 *  The Lexicon forms the base of all known words. Currently stored in an SQLite database.
 * 
 *  TODO: Work needs to be done here to support returning multiple tokens for a given word, and the
 *  algorithms in grammar need to be updated to support it.
 *
 * @author      Adam Scarr
 * @author      Rowan Spence
 * @since       r1
 **/

package Core;

import java.sql.*;
import java.util.LinkedList;

public class Lexicon {
	private static final String databaseURL = "jdbc:sqlite:data/lexicon.db";
	private Connection db;
	private PreparedStatement insert;
	private PreparedStatement delete;

	// Connects to the databaseURL
	public Lexicon() {
		try {
			Class.forName("org.sqlite.JDBC");

			// and connect
			db = DriverManager.getConnection(databaseURL);
			insert = db.prepareStatement("INSERT INTO lexicon values (?, ?, ?, ?, ?, ?, ?, ?);");
			delete = db.prepareStatement("DELETE FROM lexicon WHERE word=?;");
//				select = db.prepareStatement("SELECT * FROM lexicon WHERE word=?;");

		} catch (SQLException e) {
			System.out.println("Database connection error: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		} catch (ClassNotFoundException e) {
			System.out.println("Error loading JDBC Driver: " + e.getMessage());
			System.exit(0);
		}
	}

	public void add(Token t) {
		try {
			insert.setString(1, t.getValue());
			insert.setString(2, t.getType());
			insert.setBoolean(3, t.isFirstPerson());
			insert.setBoolean(4, t.isSecondPerson());
			insert.setBoolean(5, t.isThirdPerson());
			insert.setBoolean(6, t.isPastTense());
			insert.setBoolean(7, t.isPresentTense());
			insert.setBoolean(8, t.isFutureTense());
			insert.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error adding '" + t + "' to lexicon: " + e.getMessage());
		}
	}

	public void remove(String word) {
		try {
			delete.setString(1, word);
			delete.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error removing '" + word + "' from lexicon: " + e.getMessage());
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		Statement s;
		ResultSet rs;
		try {
			s = db.createStatement();

			rs = s.executeQuery("SELECT word FROM lexicon;");
			while(rs.next()) {
				buffer.append(rs.getString("word")).append("\n");
			}
			rs.close();

		} catch(SQLException e) {
			System.err.println("Error dumping lexicon");
		}
		return buffer.toString();
	}

	// Gets all instances of word. Returns an UNDEF token if word does not exist or there are database issues.
	public Token get(String word) {
		Statement s;
		ResultSet rs;
		try {
			s = db.createStatement();

			rs = s.executeQuery("SELECT * FROM lexicon WHERE word = '" + word + "';");
			Token w = new Token(word);
			if(rs.next()) {
				w = new Token(word, rs.getString("type"), rs.getBoolean("firstPerson"), rs.getBoolean("secondPerson"), rs.getBoolean("thirdPerson"),
																		rs.getBoolean("pastTense"), rs.getBoolean("presentTense"), rs.getBoolean("futureTense"));
				//System.err.println("Lexicon loaded: " + w);
			}
			rs.close();

			return w;
		} catch(SQLException e) {
			System.err.println("Error retreiving '" + word + "' from lexicon: " + e.getMessage());
		}

		return null;
	}

	public LinkedList<Token> tokenize(String line) {
		String[] words = line.split(" ");
		LinkedList<Token> tokens = new LinkedList<Token>();

		for (String word : words) {
			Token w = get(word);

			if(w.getType().equals("UNDEF")) {
				// If we couldnt find the word perhaps it has a comma or period on the end?
				// Search this word for periods or commas (usually at the end of a word not by themselves.
				// todo: ownership on proper nouns ('s).
                char last = word.charAt(word.length() - 1);
                if(last == ',' || last == '.') {
                    w = get(word.substring(0, word.length() - 1));
                } else {
                    w = get(word);
                }

				if(w.getType().equals("UNDEF")) {
					 System.out.println("Could not find '" + w.getValue() + "' in lexicon.");
				}
				if(word.charAt(word.length() - 1) == '.') {
					tokens.add(get(word.substring(0, word.length() - 1)));
					tokens.add(new Token(".", "PERIOD", true, true, true, true, true, true));
				} else if(word.charAt(word.length() - 1) == ',') {
					tokens.add(get(word.substring(0, word.length() - 1)));
					tokens.add(new Token(",", "COMMA", true, true, true, true, true, true));
				}
			} else {
				tokens.add(w);
			}
		}

		return tokens;
	}


}
