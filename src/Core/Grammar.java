package Core;

import Graph.*;

import java.util.LinkedList;
import java.io.*;

/**
 * User: Adam Scarr
 * Date: 14/08/2008
 * Time: 14:43:37
 * Description: Creates and manages the grammar graph.
 */

public class Grammar {
	private Node<GrammarNode, GrammarEdge> start, end;
	private Graph<GrammarNode, GrammarEdge> graph;
	private Lexicon lexicon;
	private int totalSentences;

	//----------------------------------------
	// Constructors
	//----------------------------------------
	public Grammar(Lexicon lexicon) {
		graph = new Graph<GrammarNode, GrammarEdge>();
		start = graph.createNode(new GrammarNode(new Token("START", "START", false, false, false, false, false, false)));
		start.lock();
		end = graph.createNode(new GrammarNode(new Token("END", "END", false, false, false, false, false, false)));
		this.lexicon = lexicon;
		totalSentences = 0;
	}

	//----------------------------------------
	// Simple getters
	//----------------------------------------
	public Node<GrammarNode, GrammarEdge> getStart() {
		return start;
	}

	public Node<GrammarNode, GrammarEdge> getEnd() {
		return end;
	}

	public Graph getGraph() {
		return graph;
	}

	public int getTotalSentences(){
		return totalSentences;
	}

	//----------------------------------------
	// Non mutating logic
	//----------------------------------------
	// Checks if a given set of tokens is valid (there is a path from start to end.
	public float validate(LinkedList<Token> input) {
		return validateNode(start, input, 0);
	}

	public float validate(String sentance) {
		return validate(lexicon.tokenize(sentance.toLowerCase()));
	}


	// Returns true if there is a path from node to end.
   	public float validateNode(Node<GrammarNode, GrammarEdge> parentNode, LinkedList<Token> input, int offset) {
		for(Edge<GrammarNode, GrammarEdge> edge: parentNode.getOutgoingEdges()) {
			Node<GrammarNode, GrammarEdge> node;
			if(edge.getSrc().equals(parentNode)) {
				node = edge.getDest();
			} else {
				node = edge.getSrc();
			}

			if(node.getData().isEnd() && offset == input.size()) {
				System.out.println(edge.getData().getWeight() + " -> " + node.getData().getLabel());
				return edge.getData().getWeight();
			}

			//DO NOT remove first part of this if statemnt!
			if(input.size() > offset && node.getData().matches(input.get(offset))) {
				System.out.print(edge.getData().getWeight() + " -> " + node.getData().getLabel() + " -> ");
				float weight = validateNode(node, input, offset + 1);
				if(weight > 0) {
					return weight * (edge.getData().getWeight() + 0.5f);
				}
			}
		}

		return 0.0f;
	}

	//----------------------------------------
	// Mutators
	//----------------------------------------

	// Reads a text file and passes each line into parse, building the grammar.
	// Lines beginning with # are ignored.
	public void parseFile(String filename) {
		try {
			BufferedReader trainingSet = new BufferedReader(new FileReader(new File(filename)));
			String line;

			while ((line = trainingSet.readLine()) != null) {
				if(line.charAt(0) != '#') {
					parse(line);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find training set '" + filename + "': " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Reads a 'test' file: a file that contains a number of lines each representing an action to manipulate the grammar.
	// a: - adds a line
	// v: - checks that a line validates correctly
	// i: - checks that a line does not validate correctly.
	public boolean testFile(String filename) {
		boolean valid = true;
		try {
			BufferedReader trainingSet = new BufferedReader(new FileReader(new File(filename)));
			String line;

			while ((line = trainingSet.readLine()) != null) {
				switch(line.charAt(0)) {
					case 'a':
						parse(line.substring(2));
						break;

					case 'v':
						if(validate(line.substring(2)) == 0) {
							System.out.println("Error validating: " + line.substring(2));
							valid = false;
						}
						break;

					case 'i':
						if(validate(line.substring(2)) > 0) {
							System.out.println("Error invalidating: " + line.substring(2));
							valid = false;
						}
						break;

				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find test set '" + filename + "': " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return valid;
	}

	// Returns true if given sentance is valid.
	// Returns false if its not, and adds it to the grammar.
	public boolean parse(String sentance) {
		graph.lockNodesRW();
		LinkedList<Token> tokens = lexicon.tokenize(sentance.toLowerCase());

//		if(validate(tokens)) {
//			graph.unlockNodesRW();
//			totalSentences++;
//			return true;
//		}

		// Simple grammar add.
        boolean modified = addPath(tokens);
		graph.unlockNodesRW();
		totalSentences++;

        return modified;
    }

	// Searches from currentNode through all children (and childrens children, etc) to find the first node matching token
	// Returns the node matched, or null if not found.
	public Node<GrammarNode, GrammarEdge> findNext(Node<GrammarNode, GrammarEdge> currentNode, Token token){
		if(currentNode.getData().matches(token)){
			return currentNode;
		}
		else{

			LinkedList <Node<GrammarNode, GrammarEdge>> t = currentNode.getConnectedNodes();
			for(Node<GrammarNode, GrammarEdge> child : t){
				Node<GrammarNode, GrammarEdge> tmp = findNext(child, token);
				if (tmp != null){
					return tmp;
				}
			}
		}
		return null;
	}

	public void clear() {
		graph.clear();
		start = graph.createNode(new GrammarNode(new Token("START", "START", false, false, false, false, false, false)));
		start.lock();
		end = graph.createNode(new GrammarNode(new Token("END", "END", false, false, false, false, false, false)));
		totalSentences = 0;
	}

	// for adding new paths to the graph when a giving sentence
	// does not fully parse with the existing graph
	private class Chain {
		Chain(Node<GrammarNode, GrammarEdge> src, Node<GrammarNode, GrammarEdge> dest, int links) {
			this.src = src;
			this.dest = dest;
			this.links = links;
		}

		Node<GrammarNode, GrammarEdge> src;
		Node<GrammarNode, GrammarEdge> dest;
		int links;
	}

	/*
	* find the token that can be matched to the current graph,
	* this will be a recuorsive call through every child from the split
	*/
	public Chain makeChain(Node<GrammarNode, GrammarEdge> src, LinkedList<Token> tokens, int offset) {

		int limit = offset - 1;
		Node<GrammarNode, GrammarEdge> dest = null;

		//todo multiple instances of src == dest breaks the graph, codeMonkey(fix it) !seems to be ok now?
		while((dest == null || dest == src) && ++limit < tokens.size()) {
			dest = findNext(src, tokens.get(limit));                                   // todo: this is a stupid thing to do with a linked list!
		}

		if(dest == null){
			dest = end;
		}

		Node<GrammarNode, GrammarEdge> tmp = src;

		for(int k = offset; k < limit; k++){
			tmp = graph.createNode(new GrammarNode(tokens.get(k)));          // todo: this is a stupid thing to do with a linked list!
			src.connectsTo(tmp, new GrammarEdge(this));
			src = tmp;
		}

		graph.touch();             // Tell the graph weve re-arranged its nodes.


		tmp.connectsTo(dest, new GrammarEdge(this));

		return new Chain(src, dest, limit - offset);
	}

	// Creates a path from start to end that matches the given token array by creating only the nodes that are needed
	// If a match exists it will not create any nodes.
	// If no nodes (aside from start and finish) exist then it will create the whole chain.
	// Otherwise it will just create the smallest possible chain over the unmatched area.
	public boolean addPath(LinkedList<Token> tokens){
        boolean modified = false;
        Node<GrammarNode, GrammarEdge> cursor = start;

		if(start.getConnectedNodes().size() == 0) {                             // No nodes? we need a chain.
			makeChain(start, tokens, 0);
         modified = true;
      } else {
			for(int token = 0; token < tokens.size(); token++) {
				boolean found = false;
				for(Edge<GrammarNode, GrammarEdge> edge : cursor.getEdges()){        // Search the children for a match
					if(edge.getSrc() == cursor || !edge.isDirected()){
						Node<GrammarNode, GrammarEdge> child;
						if(edge.isDirected()){
							child = edge.getDest();
						} else {
							if(edge.getSrc() == cursor){
								child = edge.getDest();
							} else {
								child = edge.getSrc();
							}
						}

						if( child.getData().matches(tokens.get(token))){               // todo: this is a stupid thing to do with a linked list! (gets are slow!)
							cursor = child;
							found = true;
							edge.getData().incrementUsageCount();
							//System.out.println(child.getData().getLabel() + " : " + edge.getData().getUsageCount());
                  }
					}
				}

				if(!found) {                                                      // No match? we need a chain.
					Chain c = makeChain(cursor, tokens, token);
					cursor = c.dest;
					token += c.links;
				   modified = true;
			   }
			}

			for(Edge<GrammarNode, GrammarEdge> edge: cursor.getOutgoingEdges()) {
				if(edge.getDest().getData().getType().equals("END")) {
					edge.getData().incrementUsageCount();
				}
			}

		}
        return modified;
    }
}
