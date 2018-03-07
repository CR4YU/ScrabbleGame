package main.game;

/**
 * Tile class. Has all needed fields like letter, points for tile and position on board.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class Tile {

	// **************************************************
	// Fields
	// **************************************************

	/** Row on board */
	private int row = -1;

	/** Column on board */
	private int column = -1;

	/** Letter on tile */
	private char letter;

	/** Points for tile */
	private int points;

	/** Is star/blank tile */
	private boolean isStar = false;

	/** Reference of original tile */
	private Tile copyOf;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameteterizd constructor. Creates Tile instance with given letter and points for it.
	 * @param letter the letter
	 * @param points points for letter
	 */
	public Tile(char letter, int points) {
		this.points = points;
		this.letter = letter;
		if(letter == '*') {
			isStar = true;
		}
	}

	/**
	 * Create new Tile as copy of given other Tile
	 * @param tile tile to copy
	 */
	public Tile(Tile tile) {
		 isStar = tile.isStar;
		 letter = tile.letter;
		 points = tile.points;
		 column = tile.column;
		 row = tile.row;
	}

	// **************************************************
	// Methods
	// **************************************************


	/**
	 * Get row.
	 * @return row
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Set row.
	 * @param row row
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * Get column.
	 * @return column
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Set column
	 * @param column column
	 */
	public void setColumn(int column) {
		this.column = column;
	}

	/**
	 * Get letter on tile.
	 * @return letter
	 */
	public char getLetter() {
		return letter;
	}

	/**
	 * Set letter on Tile.
	 * @param tile tile
	 */
	public void setLetter(char tile) {
		this.letter = tile;
	}

	/**
	 * Get points for Tile.
	 * @return points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Set points for Tile.
	 * @param points points
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Check that Tile is star/blank
	 * @return true if star
	 */
	public boolean isStar() {
		return isStar;
	}

	/**
	 * Get original reference
	 * @return original reference
	 */
	public Tile getCopyOf() {
		return copyOf;
	}

	/**
	 * Set original reference
	 * @param copyOf original reference
	 */
	public void setCopyOf(Tile copyOf) {
		this.copyOf = copyOf;
	}
}
