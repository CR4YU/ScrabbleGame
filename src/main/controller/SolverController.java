package main.controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import main.game.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Controller of Solver.<br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class SolverController extends GameController {

	// **************************************************
	// Constants
	// **************************************************

	/** Max column count of tiles in GridPane */
	private final int MAX_COLUMN = 5;

	// **************************************************
	// FXML view fields
	// **************************************************

	/** Button to run solver */
	@FXML
	private Button solveButton;

	/** Button to clear board */
	@FXML
	private Button clearBoardButton;

	/** All letter tiles GridPane */
	@FXML
	private GridPane allLettersGridPane;

	/** Feedback Label, shows info of solver's move points */
	@FXML
	private Label feedbackLabel;


	// **************************************************
	// Fields
	// **************************************************

	/** Solver instance */
	private Solver solver;

	/** Solver thread */
	private Thread solverThread;

	/** Move returned by Solver */
	private Move botMove;

	/** Current board situation */
	private List<Tile> board;

	/** Available tiles on rack */
	private List<Tile> rack;


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Init method of controller.<br>
	 * Preparing scene and board.
	 */
	public void initialize() {
		rack = new ArrayList<>();
		board = new ArrayList<>();
		try {
			solver = new Solver();
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not init solver");
		}
		prepareBoard();
		prepareScene();
	}


	/**
	 * Prepare scene.
	 */
	private void prepareScene() {

		/* Add tiles with all possible letters to left menu */
		List<Character> usedLetters = new ArrayList<>();
		int row = 0, column = 0;
		for(Tile tile : solver.getTileBag().getTiles()) {
			if(!usedLetters.contains(tile.getLetter())) {
				TileGridPane tileGridPane = createTileGridPane(tile);
				allLettersGridPane.add(tileGridPane, column, row);
				column = (column == MAX_COLUMN - 1)? 0 : column + 1;
				row = (column == 0)? row+1 : row;
				usedLetters.add(tile.getLetter());
			}
		}

		/* Set handler for drag over on rack event */
		rackHBox.setOnDragOver(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* Accept if less than than maximum rack size */
				if(rackHBox.getChildren().size() < DataManager.RACK_SIZE){
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				}

				event.consume();
			}
		});

		/* Set on drag dropped event */
		rackHBox.setOnDragDropped(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {

				((TileGridPane)event.getGestureSource()).setTarget(rackHBox);
				/* let the source know whether the string was successfully
				* transferred and used */
				event.setDropCompleted(true);

				event.consume();
			}
		});
	}


	/**
	 * Disable all UI elements.
	 */
	public void disableUI() {
		solveButton.setDisable(true);
		clearBoardButton.setDisable(true);
		allLettersGridPane.setDisable(true);
		rackHBox.setDisable(true);
		mainGameBoardGridPane.setDisable(true);
	}

	/**
	 * Enable all UI elements.
	 */
	public void enableUI() {
		solveButton.setDisable(false);
		clearBoardButton.setDisable(false);
		allLettersGridPane.setDisable(false);
		rackHBox.setDisable(false);
		mainGameBoardGridPane.setDisable(false);
	}


	private TileGridPane createTileGridPane(Tile tile) {

		TileGridPane tileGridPane = new TileGridPane(tile);

		/* On drag detected event */
		setOnDragDetectedHandlerForTileGriPane(tileGridPane);

		/* Set on drag done event */
		tileGridPane.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* If accepted by target */
				if (event.getTransferMode() == TransferMode.MOVE) {

					if (tileGridPane.getTarget() == null) {
						return;
					}

					/* Create copy of tile */
					Tile newTile = new Tile(tileGridPane.getTile());
					TileGridPane tileGridPaneCopy = createTileGridPane(newTile);

					/* If dragged on board */
					if (tileGridPane.getTarget() instanceof BoardLabel) {
						if (isOnBoard(tileGridPane)) {
							removeTileFromBoard(tileGridPane);
							board.remove(tileGridPane.getTile());
						}

						if (tileGridPane.getTile().isStar()) {
							Character letter = getTileFromDialogWindow();
							if (letter != null) {
								tileGridPaneCopy.getTile().setLetter(letter);
								tileGridPaneCopy.setDisplayLetter(letter);
							}
						}
						showTileOnBoard(tileGridPaneCopy.getTile().getRow(), tileGridPaneCopy.getTile().getColumn(), tileGridPaneCopy);
						board.add(tileGridPaneCopy.getTile());

					} else if (tileGridPane.getTarget().equals(rackHBox)) {
						/* Dragged on rack */
						addToRack(tileGridPaneCopy);
						rack.add(tileGridPaneCopy.getTile());
						tileGridPaneCopy.setOnDragDetected(null);
					}
				}
				event.consume();
			}
		});

		/* On click remove from board or rack */
		tileGridPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(isOnBoard(tileGridPane)){
					removeTileFromBoard((TileGridPane)event.getSource());
					board.remove(tileGridPane.getTile());
				} else {
					removeTileFromRack(tileGridPane);
					rack.remove(tileGridPane.getTile());
				}
			}
		});

		return tileGridPane;
	}

	/**
	 * Remove all tiles from board
	 */
	@FXML
	public void clearBoard() {
		Iterator<Node> i = mainGameBoardGridPane.getChildren().iterator();
		Predicate<Node> predicate = t-> t instanceof TileGridPane;
		mainGameBoardGridPane.getChildren().removeIf(predicate);
		board.clear();
	}

	/**
	 * Run solver. So you are cheater.
	 */
	@FXML
	public void solve() {
		disableUI();
		/* For all tiles on board set as placed */
		mainGameBoardGridPane.getChildren().forEach(node -> {
			if(node instanceof TileGridPane){
				((TileGridPane) node).setAsJustPlaced(false);
			}});

		runProgressBar(DataManager.getBotTime());
		/* Run thread of solver */
		solverThread = new Thread(){
			public void run(){
				botMove = solver.solve(board, new ArrayList(rack));
				Platform.runLater(()->{
					if(botMove == null) {
						feedbackLabel.setText("Solver was not able to find proper move");
					} else {
						feedbackLabel.setText("Solver has found move for "+botMove.getPoints()+ " points.");
						commitBotMove(botMove);
					}
					enableUI();
				});

			}
		};

		solverThread.start();
	}

	/**
	 * Add tile to rack.
	 * @param tile the tile
	 */
	private void addToRack(TileGridPane tile) {
		rackHBox.getChildren().add(tile);
	}

	/**
	 * Show bot move on board.
	 * @param move bot move
	 */
	private void commitBotMove(Move move) {
		move.getTiles().forEach(tile -> {
			TileGridPane tileGridPane = createTileGridPane(tile);
			tileGridPane.setAsJustPlaced(true);
			board.add(tileGridPane.getTile());
			mainGameBoardGridPane.add(tileGridPane, tile.getColumn(), tile.getRow());
		});
	}





}
