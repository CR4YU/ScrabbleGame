package main.controller;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import main.game.Tile;

/**
 * Represents a tile with GridPane with letter label and points label inside.<br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class TileGridPane extends GridPane {

	// **************************************************
	// Constants
	// **************************************************

	/** CSS style class for tile */
	private final String TILE_GRID_PANE_STYLE_CLASS = "tileGridPane";

	/** CSS style class for letter label in tile*/
	private final String LETTER_LABEL_STYLE_CLASS = "tileLetterLabel";

	/** CSS style class for points label in tile */
	private final String POINTS_LABEL_STYLE_CLASS = "tilePointsLabel";


	// **************************************************
	// Fields
	// **************************************************

	/** Tile instance inside */
	private Tile tile;

	/** Letter Label */
	private Label letterLabel;

	/** Points Label */
	private Label pointsLabel;

	/** Target of tile. Used during drag and drop transaction */
	private Node target;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor with tile.
	 * @param tile the tile needed to create TileGridPane
	 */
	public TileGridPane(Tile tile) {
		super();
		this.tile = tile;
		letterLabel = new Label("" + tile.getLetter());
		pointsLabel = new Label("" + tile.getPoints());

		add(letterLabel,1,1);
		add(pointsLabel,2,2);

		getColumnConstraints().add(new ColumnConstraints(12));
		getColumnConstraints().add(new ColumnConstraints(15));
		getColumnConstraints().add(new ColumnConstraints(5));

		getRowConstraints().add(new RowConstraints(12));
		getRowConstraints().add(new RowConstraints(15));
		getRowConstraints().add(new RowConstraints(5));
		getStyleClass().add(TILE_GRID_PANE_STYLE_CLASS);
		letterLabel.getStyleClass().add(LETTER_LABEL_STYLE_CLASS);
		pointsLabel.getStyleClass().add(POINTS_LABEL_STYLE_CLASS);

	}

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Get Tile.
	 * @return Tile
	 */
	public Tile getTile() {
		return tile;
	}

	/**
	 * Set Tile.
	 * @param tile Tile to set
	 */
	public void setTile(Tile tile) {
		this.tile = tile;
	}

	/**
	 * Set letter to being displayed on Tile.
	 * @param letter to set on label
	 */
	public void setDisplayLetter(char letter) {
		letterLabel.setText(Character.toString(letter));
	}

	/**
	 * Show star as letter.
	 */
	public void displayStar() {
		letterLabel.setText("*");
	}

	/**
	 * Set as temporary or not.
	 * @param temporary is temporary
	 */
	public void setAsTemporary(boolean temporary){
		if(temporary) {
			letterLabel.setTextFill(Color.GREY);
			pointsLabel.setTextFill(Color.GREY);
		} else {
			letterLabel.setTextFill(Color.BLACK);
			pointsLabel.setTextFill(Color.BLACK);
		}
	}

	/**
	 * Set as just placed or not
	 * @param justPlaced is just placed
	 */
	public void setAsJustPlaced(boolean justPlaced) {
		if (justPlaced) {
			letterLabel.setTextFill(Color.WHITE);
			pointsLabel.setTextFill(Color.WHITE);
		} else {
			letterLabel.setTextFill(Color.BLACK);
			pointsLabel.setTextFill(Color.BLACK);
		}
	}

	/**
	 * Get target
	 * @return target Node
	 */
	public Node getTarget() {
		return target;
	}

	/**
	 * Set target Node.
	 * @param target target Node
	 */
	public void setTarget(Node target) {
		this.target = target;
	}
}
