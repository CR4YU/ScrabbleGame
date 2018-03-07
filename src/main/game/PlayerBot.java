package main.game;

import javafx.scene.paint.Color;

import java.util.*;

/**
 * Class that represents bot player. <br>
 * Most important method is 'getMove()' that return valid bot move.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class PlayerBot extends Player {

	// **************************************************
	// Constants
	// **************************************************

	/* Codes used by bot */
	private final int UP = 1;
	private final int DOWN = 2;
	private final int LEFT = 3;
	private final int RIGHT = 4;


	// **************************************************
	// Fields
	// **************************************************

	/** Bot's difficulty */
	private int difficulty;

	/** Game instance needed by bot */
	private Game game;



	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor. Create bot with given nick, color and difficulty.
	 * @param nick bot's nick
	 * @param color bot's color
	 * @param difficulty bot's difficulty
	 */
	public PlayerBot(String nick, Color color, int difficulty) {
		super(nick, color);
		this.difficulty = difficulty;
	}


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Main method of bot to get valid move if ones exist or null
	 * @return Move instance or null
	 */
	public Move getMove() {
		List<Move> allMoves = new ArrayList<Move>();
		int size = game.getBoardSize();

		Random rand = new Random();
		StringBuilder word;
		List<Tile> tilesFromRack;
		int minSensiblePoints = 1000;
		int maxPoints = 0;

		int beginRow = 0;
		int beginColumn = 0;

		if(!game.isFirstMoveDone()) {
			outer:
			for(int j=0; j<size; j++) {
				for (int i=0; i<size; i++ ) {
					if(game.getBoardBonuses()[j][i] == BoardBonus.BEGIN) {
						beginRow = j;
						beginColumn = i;
						break outer;
					}
				}
			}
		}


		long start = System.currentTimeMillis();
		long end;
		long delta;
		double seconds = 0.0d;

		/* While bot has time he tries to start from new place and build word */
		while (seconds <  DataManager.getBotTime() ) {

			tilesFromRack = new ArrayList<>(getRack());
			List<Tile> currentMoveTiles = new ArrayList<>();
			word = new StringBuilder();
			Collections.shuffle(tilesFromRack);

			int j; //start row
			int i; //start column

			if(game.isFirstMoveDone()){
				j = rand.nextInt(size);
				i = rand.nextInt(size);
				while( (game.getBoard()[j][i] == null && !game.hasNeighbour(j,i)) ) {
					j = rand.nextInt(size);
					i = rand.nextInt(size);
				}
				if(game.getBoard()[j][i] != null){
					word.append(game.getBoard()[j][i].getLetter());
				}
			} else {
				j = beginRow;
				i = beginColumn;
			}

			/* randomly choose horizontal word or vertical*/
			boolean horizontally = rand.nextBoolean();
			boolean canPrependLetters = true;
			boolean canAppendLetters = true;
			boolean firstTilePlaced = false;
			int startRow = j;
			int endRow = j;
			int startColumn = i;
			int endColumn = i;


			/* while tiles exist try to build word */
			while (!tilesFromRack.isEmpty() && (canAppendLetters || canPrependLetters)) {

				/* chose new tile from rack to insert*/
				Tile chosenTile = tilesFromRack.get(0);
				Tile newTile = new Tile(chosenTile);
				if(newTile.isStar()) {
					rollLetter(newTile);
				}
				newTile.setCopyOf(chosenTile);

				if(!firstTilePlaced && game.getBoard()[j][i] == null) {
					newTile.setColumn(i);
					newTile.setRow(j);
					word.append(newTile.getLetter());
					firstTilePlaced = true;
				} else {
					/* go left if horizontally or up if not, should be able to prepend letters */
					if (rand.nextBoolean() && canPrependLetters) {
						/* if horizontal word go on the left*/
						if (horizontally) {
							if (startColumn <= 0) {
								canPrependLetters = false;
							} else {
								startColumn = attemptToPlaceTile(startRow, startColumn, LEFT, size, newTile, word);
							}
						} else {
							if (startRow <= 0) {
								canPrependLetters = false;
							} else {
								startRow = attemptToPlaceTile(startRow, startColumn, UP, size, newTile, word);
							}
						}
					} else if (canAppendLetters) {
						/* go right or down */
						if (horizontally) {
							if (endColumn >= size - 1) {
								canAppendLetters = false;
							} else {
								endColumn = attemptToPlaceTile(endRow, endColumn, RIGHT, size, newTile, word);
							}
						} else {
							if (endRow >= size - 1) {
								canAppendLetters = false;
							} else {
								endRow = attemptToPlaceTile(endRow, endColumn, DOWN, size, newTile, word);
							}
						}
					}
				}

				if(newTile.getColumn() != -1 && newTile.getRow() != -1) {
					tilesFromRack.remove(0);
					currentMoveTiles.add(newTile);
				} else {
					continue;
				}

				if(!game.getTree().existsPath(word.toString()) && !canPrependLetters) {
					break;
				}

				if(game.getTree().searchWord(word.toString())){
					Move newMove = new Move(new ArrayList<Tile>(currentMoveTiles));

					int points = game.checkMove(newMove);

					if(points > maxPoints) {
						maxPoints = points;
						minSensiblePoints = (int)(((float)getDifficulty()/(float)DataManager.MAX_DIFFICULTY) * (float)maxPoints);
						allMoves.add(newMove);
					} else if(points > minSensiblePoints) {
						allMoves.add(newMove);
					} else if (points == Game.INVALID_OPTIONAL_WORD_ERROR) {
						break;
					}
				}
			}

			end = System.currentTimeMillis();
			delta = end - start;
			seconds = delta / 1000.0;
		}

		if(allMoves.isEmpty()) {
			return null;
		}

		Move bestMove = getBestMove(allMoves);
		if(bestMove == null) {
			return null;
		}

		bestMove.getTiles().forEach(tile -> getRack().remove(tile.getCopyOf()));

		return bestMove;
	}

	/**
	 * Helper function to choose proper move from list of moves
	 * @param moves list of moves
	 * @return properly selected move
	 */
	private Move getBestMove(List<Move> moves) {

		if(moves == null) {
			return null;
		}

		quickSortInDescendingOrder(moves,0,moves.size()-1);
		removeDuplicates(moves);
		int index = (int)((((float)DataManager.MAX_DIFFICULTY - (float)getDifficulty()) / (float)DataManager.MAX_DIFFICULTY) * moves.size());
		if(index == moves.size()) {
			index--;
		}

		return moves.get(index);
	}

	/**
	 * Removes duplicates from moves
	 * @param moves lit of moves
	 */
	private void removeDuplicates(List<Move> moves) {
		int lastPoints = moves.get(0).getPoints();
		Iterator<Move> i = moves.iterator();
		if(i.hasNext()) i.next();
		while (i.hasNext()) {
			Move currentMove = i.next();
			int currentPoints = currentMove.getPoints();
			if(lastPoints == currentPoints) {
				i.remove();
			} else {
				lastPoints = currentPoints;
			}
		}
	}


	/**
	 * Randomize letter in Tile
	 * @param tile
	 */
	private void rollLetter(Tile tile) {
		Random rand = new Random();
		tile.setLetter((char)((int)'A'+rand.nextInt(26)));
	}

	/**
	 * Helper function for 'getMove()'.<br>
	 * With given position and direction tries to place a Tile.
	 * @param j row
	 * @param i column
	 * @param direction direction to move
	 * @param size size of board
	 * @param newTile Tile to place
	 * @param word word
	 * @return index where Tile was placed
	 */
	private int attemptToPlaceTile(int j, int i, int direction, int size, Tile newTile, StringBuilder word) {

		boolean tilePlaced = false;

		if(direction == LEFT) i--;
		if(direction == RIGHT) i++;
		if(direction == UP) j--;
		if(direction == DOWN) j++;

		while(i >= 0 && i < size && j < size && j >= 0) {
			/* if we already placed a tile before and we encounter free place  then break loop*/
			if(tilePlaced && game.getBoard()[j][i] == null) {
				if(direction == LEFT) return ++i;
				if(direction == RIGHT) return --i;
				if(direction == UP) return ++j;
				if(direction == DOWN) return --j;
			}
			/* found empty place to place tile*/
			if(!tilePlaced && game.getBoard()[j][i] == null) {
				newTile.setRow(j);
				newTile.setColumn(i);
				tilePlaced = true;
				if(direction == LEFT || direction == UP){
					word.insert(0, newTile.getLetter());
				} else {
					word.append(newTile.getLetter());
				}
			} else if (game.getBoard()[j][i] != null) {
				if(direction == LEFT || direction == UP) {
					word.insert(0,game.getBoard()[j][i].getLetter());
				} else {
					word.append(game.getBoard()[j][i].getLetter());
				}

			}
			if(direction == LEFT) i--;
			if(direction == RIGHT) i++;
			if(direction == UP) j--;
			if(direction == DOWN) j++;
		}

		if(direction == LEFT || direction == RIGHT) return i;
		if(direction == UP || direction == DOWN) return j;

		return -1;

	}

	/**
	 * Sort move by points in descending order using quick sort algorithm.
	 * @param moves list of moves
	 * @param low low index
	 * @param high high index
	 */
	public static void quickSortInDescendingOrder (List<Move> moves, int low, int high) {

		if(moves == null || moves.isEmpty()) {
			return;
		}

		int i = low;
		int j = high;
		int middle = moves.get((low+high)/2).getPoints();

		while (i < j) {
			while (moves.get(i).getPoints() > middle) {
				i++;
			}
			while (moves.get(j).getPoints() < middle) {
				j--;
			}
			if (j>=i) {
				Collections.swap(moves,i,j);
				i++;
				j--;
			}
		}

		if (low < j) {
			quickSortInDescendingOrder(moves, low, j);
		}
		if (i < high) {
			quickSortInDescendingOrder(moves, i, high);
		}
	}

	/**
	 * Set game.
	 * @param game game instance
	 */
	public void setGame(Game game) {
		this.game = game;
	}

	/**
	 * Get difficulty
	 * @return difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

}
