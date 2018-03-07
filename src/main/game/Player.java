package main.game;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;


/**
 * Class that represents player. <br>
 * Must be extended.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public abstract class Player {

	// **************************************************
	// Fields
	// **************************************************

	/** Player's nick */
	private String nick;

	/** Player's color */
	private Color color;

	/** Player's current score */
	private int points;

	/** Player's bag with tiles */
	private TileBag tileBag;

	/** Player's rack */
	private List<Tile> rack;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor with nick and color to create player.
	 * @param nick player's nick
	 * @param color player's color
	 */
	public Player(String nick, Color color) {
		this.nick = nick;
		this.color = color;
		rack = new ArrayList<Tile>();
	}


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Set player's tile bag.
	 * @param tileBag tile bag
	 */
	public void setTileBag(TileBag tileBag) {
		this.tileBag = tileBag;
	}

	/**
	 * Get points.
	 * @return points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * Set points.
	 * @param points points
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * Get tile bag.
	 * @return tile bag
	 */
	public TileBag getTileBag() {
		return tileBag;
	}

	/**
	 * Get tiles from rack.
	 * @return rack tiles
	 */
	public List<Tile> getRack() {
		return rack;
	}

	/**
	 * Set rack with tiles.
	 * @param rack list of tiles
	 */
	public void setRack(List<Tile> rack) {
		this.rack = rack;
	}

	/**
	 * Get player's nick.
	 * @return nick
	 */
	public String getNick() {
		return nick;
	}

	/**
	 * Get player's color.
	 * @return color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Add given amount of points.
	 * @param points to add
	 */
	public void addPoints(int points) {
		this.points += points;
	}

	/**
	 * Refill rack with tiles from tile bag.
	 */
	public void refillRack() {
		while (rack.size() < DataManager.RACK_SIZE && tileBag.getRemainingTilesCount() > 0) {
			rack.add(tileBag.grabTile());
		}
	}

}
