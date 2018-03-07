package tests;

import main.game.DataManager;
import main.game.WordsTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class WordsTreeTest {

	private WordsTree tree;

	@BeforeEach
	public void initWordsTree() {
		tree = new WordsTree();
		try {
			tree.loadWords(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	@DisplayName("Root of tree should not be null")
	public void rootNotNullTest() {
		assertNotNull(tree.getRoot());
	}

	@Test
	@DisplayName("Should properly add all words to the tree")
	public void addAllWordsTest() {
		assertEquals(tree.getWordsCount(), 172820);
	}

	@Test
	@DisplayName("Should properly add one word to the tree")
	public void addWordTest() {
		assertEquals(tree.getWordsCount(), 172820);
		assertFalse(tree.searchWord("KELASQC"));
		assertTrue(tree.addWord("KELASQC"));
		assertEquals(tree.getWordsCount(), 172821);
		assertTrue(tree.searchWord("KELASQC"));
	}

	@Test
	@DisplayName("Should not be able to add not proper words and be able to add proper")
	public void addIncorrectWordsTest() {
		assertFalse(tree.addWord(""));
		assertFalse(tree.addWord(" "));
		assertFalse(tree.addWord("   "));
		assertFalse(tree.addWord("a"));
		assertFalse(tree.addWord("abc"));
		assertFalse(tree.addWord("down"));
		assertFalse(tree.addWord("DO IT"));

		tree.addWord("MYSAMPLEWORD");
		assertFalse(tree.addWord("MYSAMPLEWORD"));

		assertEquals(tree.getWordsCount(), 172821);
	}

	@Test
	@DisplayName("Should properly search all words in the tree")
	public void searchWordTest() {

		//read dictionary file and search all words from it
		try {
			Path path = Paths.get(DataManager.DEFAULT_DICTIONARY_FILE);
			BufferedReader br = Files.newBufferedReader(path);
			String line;
			while ((line = br.readLine()) != null) {
				assertTrue(tree.searchWord(line.toUpperCase()));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		//words should not be in tree
		assertFalse(tree.searchWord("RECOI"));
		assertFalse(tree.searchWord("ABC"));
		assertFalse(tree.searchWord("NAIMLES"));
		assertFalse(tree.searchWord("CAHECM"));
		assertFalse(tree.searchWord("PEAR LIER"));
		assertFalse(tree.searchWord(""));
	}

	@Test
	@DisplayName("Tree should be empty before loading dictionary")
	public void shouldBeEmptyTreeBeforeLoadingDictionaryTest() {
		tree = new WordsTree();
		assertEquals(tree.getWordsCount(), 0);
	}

	@Test
	@DisplayName("Tree should be empty after loading file that not exists")
	public void shouldBeEmptyTreeAfterFailedLoadingTest() {
		tree = new WordsTree();
		try {
			tree.loadWords("fileThatNotExists.txt");
		} catch (IOException e) {
		}
		assertEquals(tree.getWordsCount(), 0);
	}

}