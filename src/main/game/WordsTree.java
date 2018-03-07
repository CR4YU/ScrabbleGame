package main.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Class that represent dictionary tree with all words.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class WordsTree {

	// **************************************************
	// Fields
	// **************************************************

	/** Root */
	private Node root;

	/** Number of words in dictionary */
	private int wordsCount;

	/**
	 * Private class that represents Node.<br>
	 * Has HashMap with children and isLastLetter indicator.
	 */
	private class Node {
		private HashMap<Character, Node> children;
		private boolean isLastLetter = false;

		private Node() {
			children = new HashMap<>();
		}
	}

	/**
	 * Constructor that create new tree with just root.
	 */
	public WordsTree() {
		root = new Node();
	}


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Return root node.
	 * @return root
	 */
	public Node getRoot() {
		return root;
	}

	/**
	 * Returns words number in dictionary.
	 * @return words number
	 */
	public int getWordsCount() {
		return wordsCount;
	}

	/**
	 * Load dictionary from file.
	 * @param filePath file path
	 * @throws IOException thrown when loading fails
	 */
	public void loadWords(String filePath) throws IOException {

		if(filePath == null) {
			filePath = DataManager.DEFAULT_DICTIONARY_FILE;
		}

		Path path = Paths.get(filePath);
		BufferedReader br = Files.newBufferedReader(path);
		String line;

		while ((line = br.readLine()) != null) {
			addWord(line.toUpperCase());
		}
		br.close();

	}

	/**
	 * Add word to tree
	 * @param word word to add
	 * @return true if success
	 */
	public boolean addWord(String word) {
		if(!word.matches("[A-Z]+")) {
			return false;
		}
		Node currentNode = root;
		for(char letter : word.toCharArray()) {
			if(currentNode.children.containsKey(letter)){
				currentNode = currentNode.children.get(letter);
			} else {
				Node newNode = new Node();
				currentNode.children.put(letter, newNode);
				currentNode = newNode;
			}
		}
		if(!currentNode.isLastLetter){
			currentNode.isLastLetter = true;
			wordsCount++;
			return true;
		}
		return false;
	}

	/**
	 * Check that given word exists in tree
	 * @param word to search
	 * @return true if word is valid
	 */
	public boolean searchWord(String word) {
		Node currentNode = root;
		for(char letter : word.toCharArray()) {
			if(currentNode.children.containsKey(letter)){
				currentNode = currentNode.children.get(letter);
			} else {
				return false;
			}
		}
		return currentNode.isLastLetter;
	}

	/**
	 * Check that given path, starting from root, exist
	 * @param word path
	 * @return true if path exists
	 */
	public boolean existsPath(String word) {
		Node currentNode = root;
		for(char letter : word.toCharArray()) {
			if(currentNode.children.containsKey(letter)){
				currentNode = currentNode.children.get(letter);
			} else {
				return false;
			}
		}
		return true;
	}


}
