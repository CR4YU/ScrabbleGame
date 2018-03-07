package main.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.game.*;

import java.io.IOException;

/**
 * Controller to handle user interactions.<br>
 * Must be extended. <br>
 * Contains methods for creating board, handle tile actions eg. placing on board or removing.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public abstract class GameController {

	// **************************************************
	// Constants
	// **************************************************

	/** Letter choice dialog window .fxml file */
	private final static String LETTER_CHOICE_FXML = "../view/letterChoiceDialog.fxml";

	/** Menu window .fxml file */
	protected final static String MENU_FXML = "../view/menu.fxml";

	/** CSS style for buttons in dialog window */
	protected final static String DIALOG_BUTTON_STYLE = "-fx-background-radius: 0; -fx-background-color: gray; " +
			"-fx-text-fill: white; -fx-font-family: 'Consolas'; -fx-font-size: 18; ";


	// **************************************************
	// FXML view fields
	// **************************************************

	/** Main game board GridPane where tiles goes on */
	@FXML
	protected GridPane mainGameBoardGridPane;

	/** Rack with available letters to put on board */
	@FXML
	protected HBox rackHBox;

	/** Progress bar shows the progress of bot move searching */
	@FXML
	protected ProgressBar progressBar;


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Disable UI during bot move searching.<br>
	 * Must be implemented
	 */
	protected abstract void disableUI();

	/**
	 * Enable UI after bot found move.<br>
	 * Must be implemented
	 */
	protected abstract void enableUI();

	/**
	 * Initial method of controller
	 */
	public void initialize() {
		/* Draw board */
		prepareBoard();
	}

	/**
	 * Draw and setup game board GridPane
	 */
	protected void prepareBoard() {

		BoardBonus[][] boardBonuses = null;
		try {
			/* Try to load board scheme from file */
			boardBonuses = DataManager.getBoardScheme();
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not load board scheme");
		}

		int size = DataManager.getBoardSize();

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				/* Create new BoardLabel with given BoardBonus */
				BoardLabel boardLabel = new BoardLabel(boardBonuses[j][i]);

				/* Set handler when user drags over */
				boardLabel.setOnDragOver(new EventHandler<DragEvent>() {
					public void handle(DragEvent event) {
						/* User drags over a tile, accept it */
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						event.consume();
					}
				});

				/* On drag entered highlight board label */
				boardLabel.setOnDragEntered(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						boardLabel.switchHighlight(true);
					}
				});

				/* On drag exited switch highlight off */
				boardLabel.setOnDragExited(new EventHandler<DragEvent>() {
					@Override
					public void handle(DragEvent event) {
						boardLabel.switchHighlight(false);
					}
				});

				/* Set handler when user drops the tile on boardLabel */
				boardLabel.setOnDragDropped(new EventHandler<DragEvent>() {
					public void handle(DragEvent event) {

						/* Get target's position on board */
						int row = GridPane.getRowIndex(boardLabel);
						int column = GridPane.getColumnIndex(boardLabel);

						/* Set position in source object */
						((TileGridPane)event.getGestureSource()).getTile().setColumn(column);
						((TileGridPane)event.getGestureSource()).getTile().setRow(row);
						/* Set the target in source object */
						((TileGridPane)event.getGestureSource()).setTarget(boardLabel);

				        /* Let the source know whether the tile was successfully
				         * transferred and used */
						event.setDropCompleted(true);

						event.consume();

					}
				});

				/* Add Label to game board GridPane */
				mainGameBoardGridPane.add(boardLabel, j, i);
			}
		}

	}

	/**
	 * Place a tile on board with given position.
	 * @param row the row on board
	 * @param column the column on board
	 * @param tileGridPane the tile
	 */
	protected void showTileOnBoard(int row, int column, Node tileGridPane) {
		if (row >= 0 && column >= 0){
			mainGameBoardGridPane.add(tileGridPane, column, row);
		}
	}

	/**
	 * Open new dialog window to allow user select letter for a tile
	 * @return selected letter
	 */
	protected Character getTileFromDialogWindow() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainGameBoardGridPane.getScene().getWindow());
		FXMLLoader fxmlLoader = new FXMLLoader();
		dialog.setTitle("Choose your letter");
		fxmlLoader.setLocation(getClass().getResource(LETTER_CHOICE_FXML));

		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			ErrorDialog.show(e, "Couldn't load letter choice dialog");
			return null;
		}
		dialog.showAndWait();
		LetterChoiceDialogController letterChoiceDialogController = fxmlLoader.getController();
		/* Get letter from controller */
		return letterChoiceDialogController.getChosenLetter();
	}

	/**
	 * Check whether given tile is present on board GridPane
	 * @param tile given TileGridPane object
	 * @return true if tile is on board GridPane
	 */
	protected boolean isOnBoard(TileGridPane tile) {
		return tile.getParent().equals(mainGameBoardGridPane);
	}

	/**
	 * Remove given tile from board GridPane
	 * @param tile TileGridPane to be removed from board
	 */
	protected void removeTileFromBoard(TileGridPane tile) {
		mainGameBoardGridPane.getChildren().remove(tile);
	}

	/**
	 * Remove given tile from rack HBox
	 * @param tile TileGridPane to be removed from rack
	 */
	protected void removeTileFromRack(TileGridPane tile) {
		rackHBox.getChildren().remove(tile);
	}

	/**
	 * Run progress bar with given time in seconds
	 * @param sec time in seconds to fill progress bar
	 */
	public synchronized void runProgressBar(int sec) {
		new Thread(() -> {
			for (int i=0; i<100; i++) {
				progressBar.setProgress((double)i / 100);
				try {
					Thread.sleep(sec * 10);
				} catch (InterruptedException e) {
					ErrorDialog.show(e, "Error with progress bar");
				}
			}
		}).start();
	}

	/**
	 * Set handler on drag detected for tile.
	 * @param tile the tile
	 */
	protected void setOnDragDetectedHandlerForTileGriPane(TileGridPane tile) {
		/* On drag detected handler */
		tile.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				/* Drag was detected, start a drag-and-drop gesture */
				Dragboard db = tile.startDragAndDrop(TransferMode.ANY);

				/* Put a string on a dragboard */
				ClipboardContent content = new ClipboardContent();
				content.putString(" ");
				db.setContent(content);

				event.consume();
			}
		});
	}

	/**
	 * Go to new window with given .fxml file
	 * @param fxmlFile .fxml file path
	 */
	protected void openNewWindow(String fxmlFile) {
		try{
			Parent parent = FXMLLoader.load(getClass().getResource(fxmlFile));
			Scene newScene = new Scene(parent);
			Stage appStage = (Stage) mainGameBoardGridPane.getScene().getWindow();
			appStage.setScene(newScene);
			appStage.show();
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not open window " + fxmlFile);
		}
	}

	/**
	 * Go back to menu window
	 */
	@FXML
	public void backToMenu() {
		openNewWindow(MENU_FXML);
	}

}
