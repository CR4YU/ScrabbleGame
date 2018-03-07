package main.controller;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import main.game.BoardBonus;


/**
 * Represents a single place in board.<br>
 * Extends Label class.<br>
 * Responsible for own appearance.
 *
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class BoardLabel extends Label {

	// ******************************
	// Constants
	// ******************************

	/** Label text color*/
	private final static Color TEXT_COLOR = Color.WHITE;

	/** Begin place background color */
	private final static String BEGIN_BACKGROUND_COLOR = "purple";

	/** Double word place background color */
	private final static String DOUBLE_WORD_BACKGROUND_COLOR = "red";

	/** Triple word place background color */
	private final static String TRIPLE_WORD_BACKGROUND_COLOR = "orange";

	/** Double letter place background color */
	private final static String DOUBLE_LETTER_BACKGROUND_COLOR = "dodgerblue";

	/** Triple letter place background color */
	private final static String TRIPLE_LETTER_BACKGROUND_COLOR = "limegreen";

	/** Standard place background color */
	private final static String STANDARD_BACKGROUND_COLOR = "#f5f5f5";

	/** Highlighted place background color */
	private final static String HIGHLIGHT_BACKGROUND_COLOR = "#f2d7a9";

	/** CSS class for board label */
	private final static String BOARD_LABEL_STYLE_CLASS = "mainGameBoardLabel";


	// **************************************************
	// Fields
	// **************************************************

	/** BoardBonus of this label */
	private BoardBonus boardBonus;

	/** Primary background color */
	private String primaryBackground;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor with BoardBonus
	 *
	 * @param boardBonus bonus to set proper css class
	 */
	public BoardLabel(BoardBonus boardBonus) {
		super(boardBonus.toString());

		this.boardBonus = boardBonus;
		getStyleClass().add(BOARD_LABEL_STYLE_CLASS);
		setTextFill(TEXT_COLOR);

		setProperBackground();

	}

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Set background in CSS
	 */
	public void setProperBackground() {
		switch (boardBonus) {
			case STANDARD:
				primaryBackground = STANDARD_BACKGROUND_COLOR;
				break;
			case DOUBLE_WORD:
				primaryBackground = DOUBLE_WORD_BACKGROUND_COLOR;
				break;
			case TRIPLE_WORD:
				primaryBackground = TRIPLE_WORD_BACKGROUND_COLOR;
				break;
			case DOUBLE_LETTER:
				primaryBackground = DOUBLE_LETTER_BACKGROUND_COLOR;
				break;
			case TRIPLE_LETTER:
				primaryBackground = TRIPLE_LETTER_BACKGROUND_COLOR;
				break;
			case BEGIN:
				primaryBackground = BEGIN_BACKGROUND_COLOR;
		}
		setStyle("-fx-background-color: " + primaryBackground);
	}

	/**
	 * Set background for highlight color or primary
	 * @param highlighted set background as highlighted if true, standard if false
	 */
	public void switchHighlight(boolean highlighted) {
		if(highlighted) {
			setStyle("-fx-background-color: " + HIGHLIGHT_BACKGROUND_COLOR);
		} else {
			setStyle("-fx-background-color: " + primaryBackground);
		}
	}
}
