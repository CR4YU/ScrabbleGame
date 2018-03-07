package main.controller;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import main.game.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Controller for normal games.<br>
 *
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class NormalGameController extends GameController {

	// ******************************
	// Constants
	// ******************************

	/** Tile bag window .fxml file */
	private final String TILE_BAG_FXML = "../view/tileBag.fxml";

	/** Game end window .fxml file */
	private final String GAME_END_FXML = "../view/gameEnd.fxml";

	// **************************************************
	// FXML view fields
	// **************************************************

	/** List of players in score table */
	@FXML
	private VBox playersVBox;

	/** Current player Label */
	@FXML
	private Label currentPlayerLabel;

	/** Current player color Label indicator */
	@FXML
	private Label currentPlayerColorLabel;

	/** Feedback Label, show info about placed tiles */
	@FXML
	private Label checkMoveFeedbackLabel;

	/** Play Button */
	@FXML
	private Button playButton;

	/** Tile bag buttons to show available tiles */
	@FXML
	private Button tileBagButton;

	/** Pass Button */
	@FXML
	private Button passButton;

	/** Game history (logs) TextArea */
	@FXML
	private TextArea gameHistoryTextArea;

	// **************************************************
	// Fields
	// **************************************************

	/** The NormalGame instance */
	private NormalGame game;

	/** User move */
	private Move move;

	/** Previous move of any player */
	private List<TileGridPane> previousMove;

	/** Game thread to process game state */
	private Thread gameThread;


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Initial controller's method.
	 */
	public void initialize() {
		super.initialize();
		/* Create NormalGame instance */
		try {
			game = new NormalGame(MenuController.players, this);
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not init new game");
		}


		move = new Move();
		previousMove = new ArrayList<TileGridPane>();
		gameThread = new Thread(){
			public void run(){
				game.continueGame();
			}
		};
		gameThread.start();
	}


	/**
	 * Shows log in game history TextArea.
	 * @param text text to show
	 */
	public void logMove(String text){
		gameHistoryTextArea.appendText(text+"\n");
	}

	/**
	 * Show players in points table with their current scores.
	 */
	public void showPointsTable() {
		int i = 0;
		for (Player player : game.getPlayers()) {
			HBox playerHBox = (HBox)playersVBox.getChildren().get(i);
			((Label)(playerHBox.getChildren().get(0))).setBackground(new Background(new BackgroundFill(player.getColor(),CornerRadii.EMPTY, Insets.EMPTY)));
			((Label)(playerHBox.getChildren().get(1))).setText(player.getNick());
			((Label)(playerHBox.getChildren().get(2))).setText(""+player.getPoints());
			i++;
		}
	}


	/**
	 * Makes all placed tiles on board unable to remove
	 */
	private void commitTilesOnBoard() {

		/* Previous move no longer indicated */
		previousMove.forEach(tile -> tile.setAsJustPlaced(false));
		previousMove.clear();

		/* For every new tile on board make as just placed */
		for(Node node : mainGameBoardGridPane.getChildren()) {
			if(node instanceof TileGridPane){
				if(node.getOnMouseClicked() != null) {
					((TileGridPane) node).setAsJustPlaced(true);
					previousMove.add((TileGridPane)node);
				}
				/* Remove mouse events */
				node.setOnMouseClicked(null);
				node.setOnDragDetected(null);
			}
		}
	}

	/**
	 * Commit bot move on board as set as just placed.
	 * @param move bot move
	 */
	public void commitBotMove(Move move) {
		previousMove.forEach(tile -> tile.setAsJustPlaced(false));
		previousMove.clear();

		for(Tile tile : move.getTiles()) {
			TileGridPane tileGridPane = new TileGridPane(tile);
			tileGridPane.setAsJustPlaced(true);
			previousMove.add(tileGridPane);
			showTileOnBoard(tile.getRow(), tile.getColumn(), tileGridPane);
		}
	}

	/**
	 * Move clicked Tile from board to rack.
	 * @param tileGridPane tile to remove from board
	 */
	private void removeTileFromBoardToRack(TileGridPane tileGridPane) {
		((List<Node>)mainGameBoardGridPane.getChildren()).remove(tileGridPane);
		tileGridPane.setAsTemporary(false);
		if(tileGridPane.getTile().isStar()) {
			tileGridPane.displayStar();
			tileGridPane.getTile().setLetter('*');
		}
		rackHBox.getChildren().add(tileGridPane);
	}

	/**
	 * Show feedback after placing tile on board.
	 */
	public void showCheckMoveFeedback() {
		/* Get result of move */
		int result = game.checkMove(move);
		/* Disable PLAY button before checking */
		playButton.setDisable(true);
		switch(result) {
			case Game.BEGIN_ERROR :
				checkMoveFeedbackLabel.setText("You have to place tile at begin point");
				break;
			case Game.INVALID_WORD_ERROR :
				checkMoveFeedbackLabel.setText("It is not a valid word");
				break;
			case Game.INVALID_OPTIONAL_WORD_ERROR :
				checkMoveFeedbackLabel.setText("Optional words are not valid");
				break;
			case Game.NOT_CONNECTED_ERROR :
				checkMoveFeedbackLabel.setText("Tiles must be connected");
				break;
			case Game.ROW_COLUMN_ERROR :
				checkMoveFeedbackLabel.setText("Tiles must be lined up");
				break;
			case Game.EMPTY_WORD_ERROR :
				checkMoveFeedbackLabel.setText("Place your tiles on board");
				break;
			case Game.OTHER_ERROR :
				checkMoveFeedbackLabel.setText("Unknown problem, try again");
				break;
			default:
				/* Valid move. Player is able to click PLAY */
				checkMoveFeedbackLabel.setText("Play for " + result + " points");
				playButton.setDisable(false);
		}
	}

	/**
	 * Disable all UI elements.
	 */
	public void disableUI() {
		rackHBox.setVisible(false);
		checkMoveFeedbackLabel.setVisible(false);
		passButton.setDisable(true);
		tileBagButton.setDisable(true);
		playButton.setDisable(true);
		progressBar.setVisible(true);
	}

	/**
	 * Enable all UI elements.
	 */
	public void enableUI() {
		rackHBox.setVisible(true);
		checkMoveFeedbackLabel.setVisible(true);
		passButton.setDisable(false);
		tileBagButton.setDisable(false);
		progressBar.setVisible(false);
	}

	/**
	 * Show current player.
	 * @param player current player
	 */
	public void setCurrentPlayer(Player player) {
		currentPlayerLabel.setText(player.getNick());
		currentPlayerColorLabel.setBackground(new Background(new BackgroundFill(player.getColor(),CornerRadii.EMPTY, Insets.EMPTY)));
	}

	/**
	 * Fill rack with current player's available tiles.
	 * @param player the player
	 */
	public void setRackHBox(Player player) {
		rackHBox.getChildren().clear();

		for (Tile tile : player.getRack()) {
			TileGridPane tileGridPane = createTileGridPane(tile);
			rackHBox.getChildren().add(tileGridPane);
		}
	}

	/**
	 * Init TileGridPane
	 * @param tile needed Tile to create TileGridPane
	 * @return TileGridPane instance
	 */
	private TileGridPane createTileGridPane(Tile tile) {

		TileGridPane tileGridPane = new TileGridPane(tile);

		/* On drag detected event */
		setOnDragDetectedHandlerForTileGriPane(tileGridPane);

		/* After drag is done */
		tileGridPane.setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {
				/* If transfer was accepted */
				if (event.getTransferMode() == TransferMode.MOVE) {

					/* If tile was on board before */
					if (isOnBoard(tileGridPane)) {
						move.removeTile(tileGridPane.getTile());
						removeTileFromBoard(tileGridPane);
					}

					/* If star tile show letter choice dialog */
					if (tileGridPane.getTile().isStar()) {
						Character letter = getTileFromDialogWindow();
						if (letter != null) {
							tileGridPane.getTile().setLetter(letter);
							tileGridPane.setDisplayLetter(letter);
						}
					}

					showTileOnBoard(tileGridPane.getTile().getRow(), tileGridPane.getTile().getColumn(), tileGridPane);

					tileGridPane.setAsTemporary(true);
					move.addTile(tileGridPane.getTile());
					showCheckMoveFeedback();
				}
				event.consume();
			}
		});

		/**
		 * On click remove from board.
		 */
		tileGridPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if(isOnBoard(tileGridPane)){
					move.removeTile(tileGridPane.getTile());
					removeTileFromBoardToRack((TileGridPane)event.getSource());
					showCheckMoveFeedback();
				}
			}
		});

		return tileGridPane;
	}

	/**
	 * On PLAY clicked.
	 */
	@FXML
	public void handlePlayClicked() {
		commitTilesOnBoard();

		int points = game.checkMove(move);
		logMove(game.getCurrentPlayer().getNick() + " played for " + points + " points");

		gameThread = new Thread(){
			public void run(){
				game.makeMove(move);
				move = new Move();
			}
		};
		gameThread.start();
	}

	/**
	 * Show dialog with all remaining tiles.
	 */
	@FXML
	public void showTileBag() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainGameBoardGridPane.getScene().getWindow());
		FXMLLoader fxmlLoader = new FXMLLoader();
		dialog.setTitle("Your tile bag");
		fxmlLoader.setLocation(getClass().getResource(TILE_BAG_FXML));

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not load tile bag dialog");
			return;
		}

		ButtonBar buttonBar = (ButtonBar)dialog.getDialogPane().lookup(".button-bar");
		buttonBar.getButtons().forEach(b->b.setStyle(DIALOG_BUTTON_STYLE));

		TileBagDialogController controller = fxmlLoader.getController();
		controller.showTiles(game.getCurrentPlayer().getTileBag().getTiles());
		dialog.showAndWait();

	}

	/**
	 * Eng current game
	 */
	public void endGame() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainGameBoardGridPane.getScene().getWindow());
		FXMLLoader fxmlLoader = new FXMLLoader();
		dialog.setTitle("Game conclusion");
		fxmlLoader.setLocation(getClass().getResource(GAME_END_FXML));

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not load window");
			return;
		}

		ButtonBar buttonBar = (ButtonBar)dialog.getDialogPane().lookup(".button-bar");
		buttonBar.getButtons().forEach(b->b.setStyle(DIALOG_BUTTON_STYLE));

		GameEndController controller = fxmlLoader.getController();
		controller.showConclusionTable(game.getPlayers());

		dialog.showAndWait();
		openNewWindow(MENU_FXML);
	}

	/**
	 * Remove all temporary tiles from board.
	 */
	private void removeAllTemporaryTilesFromBoard() {
		Iterator<Node> i = mainGameBoardGridPane.getChildren().iterator();
		while (i.hasNext()) {
			Node node = i.next();
			if(node instanceof TileGridPane && node.getOnMouseClicked() != null) {
				i.remove();
			}
		}
	}


	/**
	 * Continue game when user click Pass.
	 */
	@FXML
	public void handlePassClicked() {
		move = new Move();

		removeAllTemporaryTilesFromBoard();
		logMove(game.getCurrentPlayer().getNick() + " has passed move");

		gameThread = new Thread(){
			public void run(){
				game.passMove();
			}
		};
		gameThread.start();
	}

	/**
	 * Back to menu.
	 */
	@FXML
	public void backToMenu() {
		game.setCurrentGameState(GameState.ENDED);
		openNewWindow(MENU_FXML);
	}


}
