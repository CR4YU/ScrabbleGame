package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import main.game.Tile;

import java.util.List;

/**
 * Controller of tile bag letters window.<br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class TileBagDialogController {

	// **************************************************
	// Constants
	// **************************************************

	/** CSS style class for HBox containing tile and count */
	private final String TILE_HBOX_STYLE_CLASS = "tileHBox";

	/** CSS style class for tile label */
	private final String TILE_LABEL_STYLE_CLASS = "tileLabel";

	/** CSS style class for tile count label */
	private final String TILE_COUNT_LABEL_STYLE_CLASS = "tileCountLabel";

	/** Max column count in Grid Pane */
	private final int MAX_COLUMN = 6;

	// **************************************************
	// FXML view fields
	// **************************************************

	/** GridPane with tiles */
	@FXML
	private GridPane tileBagGridPane;


	// **************************************************
	// Methods
	// **************************************************


	/**
	 * Create GridPane with given letters from tile bag
	 * @param tilebag tile bag
	 */
	public void showTiles(List<Tile> tilebag) {
		/* Hardcoded alphabet */
		char[] alphabet = "*abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();

		int row = 0, column = 0;
		for(char letter : alphabet) {
			HBox tileHBox = new HBox();
			Label tileLabel = new Label(Character.toString(letter));
			Label countLabel = new Label(countLetter(tilebag, letter)+"");
			tileLabel.getStyleClass().add(TILE_LABEL_STYLE_CLASS);
			tileHBox.getStyleClass().add(TILE_HBOX_STYLE_CLASS);
			countLabel.getStyleClass().add(TILE_COUNT_LABEL_STYLE_CLASS);
			if(countLabel.getText().equals("0")) {
				tileLabel.setStyle("-fx-background-color: lightgray");
			} else {
				tileLabel.setStyle("-fx-background-color: #f2d7a9");
			}
			tileHBox.getChildren().add(tileLabel);
			tileHBox.getChildren().add(countLabel);

			tileBagGridPane.add(tileHBox, column, row);
			column = (column == MAX_COLUMN-1)? 0 : column+1;
			row = (column == 0)? row+1 : row;

		}
	}

	/**
	 * Count letter in tile bag
	 * @param tilebag tilebag with letters
	 * @param charBeingCounted char to count
	 * @return letter count
	 */
	public int countLetter(List<Tile> tilebag, char charBeingCounted) {
		int count = 0;
		for (Tile tile : tilebag) {
			if(tile.getLetter() == charBeingCounted) {
				count++;
			}
		}
		return count;
	}
}
