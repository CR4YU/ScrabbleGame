package main.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller of window where user chooses letter.<br>
 * Shows window with letters GridPane.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class LetterChoiceDialogController {


	// **************************************************
	// Constants
	// **************************************************

	/** CSS style class of letter Button */
	private final String LETTER_BUTTON_STYLE_CLASS = "letterButton";

	/** Max column count of buttons in GridPane */
	private final int MAX_COLUMN = 6;


	// **************************************************
	// FXML view fields
	// **************************************************

	/** GridPane with letter buttons */
	@FXML
	private GridPane letterChoiceGridPane;


	// **************************************************
	// Fields
	// **************************************************

	/** Chosen letter by user */
	private Character chosenLetter;


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Init method of controller.
	 */
	public void initialize() {

		/* Letters from alphabet. Very workaround solution. Letters should not
		* be hardcoded*/
		char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();

		int row = 0, column = 0;
		for(char letter : alphabet) {
			Button tileButton = new Button(Character.toString(letter));
			tileButton.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					chosenLetter = ((Button)event.getSource()).getText().charAt(0);
					((Stage)letterChoiceGridPane.getScene().getWindow()).close();
				}
			});
			tileButton.getStyleClass().add(LETTER_BUTTON_STYLE_CLASS);
			letterChoiceGridPane.add(tileButton, column, row);
			column = (column == MAX_COLUMN-1)? 0 : column+1;
			row = (column == 0)? row + 1 : row;

		}
	}

	/**
	 * Get chosen letter from controller.
	 * @return chosen letter
	 */
	public char getChosenLetter() {
		return chosenLetter;
	}
}
