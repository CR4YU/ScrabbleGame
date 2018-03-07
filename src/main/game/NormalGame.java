package main.game;

import javafx.application.Platform;
import main.controller.NormalGameController;

import java.io.IOException;
import java.util.List;

/**
 * Class that represents single game. <br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class NormalGame extends Game {

	// **************************************************
	// Constants
	// **************************************************

	/** Max pass count in row */
	private final int maxPassCount;

	// **************************************************
	// Fields
	// **************************************************

	/** Players */
	private List<Player> players;

	/** Current player */
	private Player currentPlayer;

	/** Current game state */
	private GameState currentGameState;

	/** Game controller responsible for interacting with user */
	private NormalGameController gameController;

	/** Current pass count in row */
	private int passCount = 0;

	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor. Creating game instance with given players and game controller.
	 * @param players players
	 * @param gameController game controller
	 * @throws IOException thrown when file loading fails
	 */
	public NormalGame(List<Player> players, NormalGameController gameController) throws IOException {
		super();
		this.gameController = gameController;

		this.players = players;
		maxPassCount = players.size() * DataManager.MAX_PASSES;

		/* Init tile bag for every player and game instance for bots */
		for (Player player : players) {
			player.setTileBag(new TileBag(DataManager.getGameVersion()));
			if(player instanceof PlayerBot) {
				((PlayerBot) player).setGame(this);
			}
		}
		/* Take first player as current player */
		currentPlayer = players.get(0);
		if(currentPlayer instanceof PlayerBot) {
			currentGameState = GameState.BOT_TURN;
		} else if (currentPlayer instanceof PlayerHuman) {
			currentGameState = GameState.PLAYER_TURN;
		}

	}

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Player was changed after last move. Game can be continued
	 */
	public void continueGame() {

		/* Refill rack of current player. If no more tiles the game should end */
		currentPlayer.refillRack();
		if(currentPlayer.getRack().isEmpty()) {
			currentGameState = GameState.ENDED;
			Platform.runLater(()->{
				gameController.endGame();
			});
		}

		/* Refresh points table in game controller */
		Platform.runLater(()->{
			gameController.showPointsTable();
			gameController.setCurrentPlayer(currentPlayer);
		});


		switch (currentGameState) {
			case ENDED:
				break;
			case PLAYER_TURN:
				/* If current player is human player just update UI elements */
				Platform.runLater(()->{
					gameController.showCheckMoveFeedback();
					gameController.setRackHBox(currentPlayer);
					gameController.enableUI();
				});

				/* Now wait for user play in controller*/
				break;
			case BOT_TURN:
				/* Bot turn. Disable UI elements and run progress bar */
				Platform.runLater(()->{
					gameController.disableUI();
					gameController.runProgressBar(DataManager.getBotTime());
				});

				Move move = ((PlayerBot)currentPlayer).getMove();
				if(currentGameState == GameState.ENDED) {
					break;
				}
				String nick = currentPlayer.getNick();
				if(move == null) {
					logPass(nick);
					passMove();
					break;
				}
				int points = checkMove(move);
				logMove(nick, points);
				commitMoveInGameController(move);

				makeMove(move);
				break;
		}
	}

	/**
	 * Pass move of current player and change to next.
	 */
	public void passMove() {
		passCount++;

		if (passCount == maxPassCount) {
			/* For every player decrement points by remaining tiles on rack value */
			players.forEach(player -> {
				player.getRack().forEach(tile -> {player.setPoints(player.getPoints()-tile.getPoints());});
			});
			/* Show conclusion table in game controller */
			Platform.runLater(()->{
				gameController.endGame();
			});
		} else {
			nextPlayer();
			continueGame();
		}
	}

	/**
	 * Commit move, change player and continue game.
	 * @param playerMove move to commit
	 */
	public void makeMove(Move playerMove) {
		passCount = 0;
		commitMove(playerMove);
		nextPlayer();
		continueGame();
	}

	/**
	 * Change to next player. Set proper game state.
	 */
	private void nextPlayer() {
		int playersCount = players.size();
		int currentPlayerIndex = players.indexOf(currentPlayer);
		if(currentPlayerIndex == playersCount - 1) {
			currentPlayer = players.get(0);
		} else {
			currentPlayer = players.get(currentPlayerIndex+1);
		}

		if(currentPlayer instanceof PlayerBot) {
			currentGameState = GameState.BOT_TURN;
		} else if (currentPlayer instanceof PlayerHuman) {
			currentGameState = GameState.PLAYER_TURN;
		}
	}

	/**
	 * Commit move on board.
	 * @param move the move
	 */
	public void commitMove(Move move) {
		/* We assume move is correct at this moment */
		currentPlayer.addPoints(checkMove(move));
		for(Tile tile : move.getTiles()) {
			board[tile.getRow()][tile.getColumn()] = tile;
			currentPlayer.getRack().remove(tile);
		}
		firstMoveDone = true;
	}

	/**
	 * Log in game controller that current player passed move.
	 * @param nick nick of player
	 */
	private void logPass(String nick) {
		Platform.runLater(()->{
			gameController.logMove(nick + " has passed move");

		});
	}

	/**
	 * Log in game controller that current player made a move.
	 * @param nick nick of player
	 * @param points score for move
	 */
	private void logMove(String nick, int points) {
		Platform.runLater(()->{
			gameController.logMove(nick + " played for " + points + " points");
		});
	}

	/**
	 * Create tiles in game controller and place them on board. Used after bot finds a move.
	 * @param move move to commit
	 */
	public void commitMoveInGameController(Move move) {
		Platform.runLater(()->{
			gameController.commitBotMove(move);
		});
	}

	/**
	 * Get current player.
	 * @return current player
	 */
	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	/**
	 * Get players.
	 * @return players
	 */
	public List<Player> getPlayers() {
		return players;
	}

	/**
	 * Set game state.
	 * @param currentGameState the game state
	 */
	public void setCurrentGameState(GameState currentGameState) {
		this.currentGameState = currentGameState;
	}

}
