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
 *
 * @author      Adam Scarr
 * @author      Rowan Spence
 * @since       r1
 **/

package net.openalp.core;

import java.sql.*;
import java.util.LinkedList;

public class LexiconDAO {
	private static final String databaseURL = "jdbc:sqlite:data/lexicon.db";
	private Connection db;
	private PreparedStatement insert;
	private PreparedStatement delete;

     /**
     * Class constructor. Creates the database connection. If the database connection
     * fails it will exit the program.
     */
	public LexiconDAO() {
		try {
			Class.forName("org.sqlite.JDBC");

			// and connect
			db = DriverManager.getConnection(databaseURL);
			insert = db.prepareStatement("INSERT INTO lexicon (word, type, firstPerson, secondPerson, thirdPerson, pastTense, presentTense, futureTense) values (?, ?, ?, ?, ?, ?, ?, ?);");
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

     /**
     * Adds a new token to the lexicons backing store.
     * @param t The token to add.
     */
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

    /**
     * Removes a word from the dictionary.
     * @param word  The word to remove.
     */
	public void remove(String word) {
		try {
			delete.setString(1, word);
			delete.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Error removing '" + word + "' from lexicon: " + e.getMessage());
		}
	}

    /**
     * Dumps the entire lexicon as a simple wordlist.
     * @return The entire lexicon.
     */
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

    /**
     * Returns all instances of a word.
     * @param word  The word you want the token for.
     * @return  The token or UNDEF, if the word could not be found.
     */
	public LinkedList<Token> get(String word) {
		Statement s;
		ResultSet rs;
		try {
			s = db.createStatement();

			rs = s.executeQuery("SELECT * FROM lexicon WHERE word = '" + word + "';");

            LinkedList<Token> tokenList = new LinkedList<Token>();

            Token w;

			while(rs.next()) {
                w = new Token(word, rs.getString("type"), rs.getBoolean("firstPerson"), rs.getBoolean("secondPerson"), rs.getBoolean("thirdPerson"),
					rs.getBoolean("pastTense"), rs.getBoolean("presentTense"), rs.getBoolean("futureTense"));
				//System.err.println("LexiconDAO loaded: " + w);
                tokenList.add(w);
			}
			rs.close();

			return tokenList;
		} catch(SQLException e) {
			System.err.println("Error retreiving '" + word + "' from lexicon: " + e.getMessage());
		}

		return null;
	}
}
