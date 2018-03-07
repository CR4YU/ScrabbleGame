package main.game;


import java.util.ArrayList;
import java.util.List;

/**
 * Class that represents single move. <br>
 * Contains list of Tiles and points for move after checking.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class Move {

	// **************************************************
	// Fields
	// **************************************************

	/** Tiles of move */
	private List<Tile> tiles;

	/** Points for move */
	private int points = -1;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Constructor to create new instance of Move.
	 */
	public Move() {
		tiles = new ArrayList<Tile>();
	}

	/**
	 * Parameterized constructor. Init move with ready list of Tiles.
	 * @param tiles tiles to init new move
	 */
	public Move(List<Tile> tiles) {
		this.tiles = tiles;
	}


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Add tile to move.
	 * @param tile Tile to add
	 */
	public void addTile(Tile tile) {
		tiles.add(tile);
	}

	/**
	 * Remove tile from move.
	 * @param tile Tile to remove
	 */
	public void removeTile(Tile tile) {
		tiles.remove(tile);
	}

	/**
	 * Get list of Tiles from move.
	 * @return list of Tiles
	 */
	public List<Tile> getTiles() {
		return tiles;
	}

	/**
	 * Set tiles.
	 * @param tiles list of Tiles
	 */
	public void setTiles(List<Tile> tiles) {
		this.tiles = tiles;
	}

	/**
	 * Get points from move.
	 * @return points for move
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Set points for move.
	 * @param points for move
	 */
	public void setPoints(int points) {
		this.points = points;
	}
}
