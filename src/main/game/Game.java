package main.game;

import java.io.IOException;
import java.util.*;

/**
 * Game class. Contains board, tree and methods for move checking.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public abstract class Game {

	// **************************************************
	// Constants
	// **************************************************

	/** Empty word error code */
	public static final int EMPTY_WORD_ERROR = -1;

	/** Tiles are not lined up error code */
	public static final int ROW_COLUMN_ERROR = -2;

	/** Invalid word error code */
	public static final int INVALID_WORD_ERROR = -3;

	/** Tiles not connected error code */
	public static final int NOT_CONNECTED_ERROR = -4;

	/** None of tiles placed at begin point error code */
	public static final int BEGIN_ERROR = -5;

	/** Invalid optional words error code */
	public static final int INVALID_OPTIONAL_WORD_ERROR = -6;

	/** Other error code */
	public static final int OTHER_ERROR = -7;


	// **************************************************
	// Fields
	// **************************************************

	/** Game version */
	protected GameVersion gameVersion;

	/** Current board situation */
	protected Tile[][] board;

	/** Board size */
	protected int boardSize;

	/** Board scheme (bonuses) */
	protected BoardBonus[][] boardBonuses;

	/** Tree for searching words */
	protected WordsTree tree;

	/** True if first move was done earlier */
	protected boolean firstMoveDone = false;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Constructor that initiating important objects like tree, board scheme.
	 * @throws IOException when loading board scheme fails
	 */
	public Game() throws IOException {

		tree = new WordsTree();
		tree.loadWords(DataManager.getDictionaryFilePath());

		boardSize = DataManager.getBoardSize();
		gameVersion = DataManager.getGameVersion();
		board = new Tile[boardSize][boardSize];

		boardBonuses = DataManager.getBoardScheme();

	}


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Check move and return points if valid or error code.
	 * @param playerMove move with tiles
	 * @return points or error code
	 */
	public int checkMove(Move playerMove) {

		if (playerMove.getTiles().isEmpty()) {
			return EMPTY_WORD_ERROR;
		}

		/* If first move wasn't done before check some tile is placed on begin point */
		if (!firstMoveDone) {
			boolean success = false;
			for (Tile tile : playerMove.getTiles()) {
				if (boardBonuses[tile.getRow()][tile.getColumn()] == BoardBonus.BEGIN) {
					success = true;
				}
			}
			if(!success) {
				return BEGIN_ERROR;
			}
		}

		/* Check that word is in row or in column*/
		boolean inRow = true;
		boolean inColumn = true;
		boolean connected = false;
		int row = playerMove.getTiles().get(0).getRow();
		int column = playerMove.getTiles().get(0).getColumn();

		for(Tile tile : playerMove.getTiles()) {
			if(tile.getRow() != row) {
				inRow = false;
			}
			if(tile.getColumn() != column) {
				inColumn = false;
			}
			if(hasNeighbour(tile)){
				connected = true;
			}
		}

		if(!connected && firstMoveDone) {
			return NOT_CONNECTED_ERROR;
		}

		if (!inColumn && !inRow) {
			return ROW_COLUMN_ERROR;
		} else if (inColumn && inRow) {
			/* Case that there is only 1 tile in move */
			if(!firstMoveDone) {
				return INVALID_WORD_ERROR;
			}
			/* Check existence of neighbours */
			boolean verticalWord = hasValueCell(row+1,column) || hasValueCell(row-1,column);
			boolean horizontalWord = hasValueCell(row, column+1) || hasValueCell(row, column-1);
			int horizontalWordPoints = 0;
			int verticalWordPoints = 0;
			if (horizontalWord) {
				horizontalWordPoints = checkPossibleWord(playerMove.getTiles().get(0), true);
			}
			if (verticalWord) {
				verticalWordPoints = checkPossibleWord(playerMove.getTiles().get(0), false);
			}
			if( horizontalWordPoints == INVALID_WORD_ERROR || verticalWordPoints == INVALID_WORD_ERROR ) {
				return INVALID_WORD_ERROR;
			} else {
				playerMove.setPoints(horizontalWordPoints + verticalWordPoints);
				return horizontalWordPoints + verticalWordPoints;
			}
		} else {
			/* Case that there are at least 2 tiles */
			/* Sort tiles by columns or rows */
			if(inRow) {
				insertionSortHorizontal(playerMove.getTiles());
			} else {
				insertionSortVertical(playerMove.getTiles());
			}

			/* main word */
			StringBuilder word = new StringBuilder();
			/* Word represented by tiles from move and board together */
			List<Tile> tiles = new ArrayList<Tile>();

			int j; //row counter
			int i; //column counter
			int startIndex = 0;
			int endIndex = 0;

			if(inRow) {
				j = row;
				i = playerMove.getTiles().get(0).getColumn() - 1;
			} else {
				j = playerMove.getTiles().get(0).getRow() - 1;
				i = column;
			}

			/* Go left or up to find start index of word */
			while (i>=0 && j>=0 && (board[j][i] != null)) {
				if(inRow) {
					i--;
				} else {
					j--;
				}
			}

			/* Start index must be + 1, loop above decremented index in last iteration */
			if(inRow) {
				startIndex = i+1;
				i = playerMove.getTiles().get(playerMove.getTiles().size() - 1).getColumn() + 1;
			} else {
				startIndex = j+1;
				j = playerMove.getTiles().get(playerMove.getTiles().size() - 1).getRow() + 1;
			}

			/* Go right or down to find end index of word*/
			while (i<boardSize && j<boardSize && (board[j][i] != null)) {
				if (inRow) {
					i++;
				} else {
					j++;
				}
			}

			/* End index must be -1, loop above incremented index in last iteration */
			if (inRow) {
				endIndex = i - 1;
			} else {
				endIndex = j - 1;
			}

			int l = 0; //counter for tiles from move
			int optionalWordsScore = 0;
			int currentOptionalWordScore = 0;

			if (inRow) {
				i = startIndex;
			} else {
				j = startIndex;
			}

			/* At this point we have start index and end index of potential word */
			/* We iterate through tiles from move and board and building word */
			/* If there's a gap its NOT_CONNECTED_ERROR */
			for (int k = startIndex; k <= endIndex; k++) {
				if(board[j][i] != null) {
					/* Add tile from board to potential word */
					word.append(board[j][i].getLetter());
					tiles.add(board[j][i]);
				} else if(playerMove.getTiles().get(l).getRow() != j || playerMove.getTiles().get(l).getColumn() !=i) {
					/* We have a gap, its error */
					return NOT_CONNECTED_ERROR;
				} else if(playerMove.getTiles().get(l).getRow() == j && playerMove.getTiles().get(l).getColumn() ==i) {
					/* Add tile from move */
					word.append(playerMove.getTiles().get(l).getLetter());
					tiles.add(playerMove.getTiles().get(l));
					if (inRow) {
						currentOptionalWordScore = checkPossibleWord(playerMove.getTiles().get(l),  false);
					} else {
						currentOptionalWordScore = checkPossibleWord(playerMove.getTiles().get(l), true);
					}
					l++;
				}
				if (currentOptionalWordScore == INVALID_WORD_ERROR) {
					return INVALID_OPTIONAL_WORD_ERROR;
				} else {
					optionalWordsScore += currentOptionalWordScore;
				}
				if (inRow) {
					i++;
				} else {
					j++;
				}
			}

			/* Word build complete. Now search whether word is correct */
			if(tree.searchWord(word.toString())) {
				int bingo = 0;
				if(playerMove.getTiles().size() == DataManager.RACK_SIZE){
					switch (gameVersion) {
						case SCRABBLE_15x15:
							bingo = DataManager.SCRABBLE_BINGO;
							break;
						case WORDS_WITH_FRIENDS_15x15:
							bingo = DataManager.WORDS_WITH_FRIENDS_BINGO;
							break;
						case WORDS_WITH_FRIENDS_11x11:
							bingo = DataManager.WORDS_WITH_FRIENDS_BINGO;
							break;
						case CUSTOM:
							bingo = DataManager.SCRABBLE_BINGO;
					}
				}
				playerMove.setPoints(getScore(tiles) + optionalWordsScore + bingo);
				return getScore(tiles) + optionalWordsScore + bingo;
			} else {
				return INVALID_WORD_ERROR;
			}

		}
	}

	/**
	 * Check that given Tile has neighbour on board.
	 * @param tile Tile
	 * @return true if has neighbours
	 */
	public boolean hasNeighbour(Tile tile) {
		return hasValueCell(tile.getRow()+1, tile.getColumn())
				|| hasValueCell(tile.getRow()-1, tile.getColumn())
				|| hasValueCell(tile.getRow(), tile.getColumn()+1)
				|| hasValueCell(tile.getRow(), tile.getColumn()-1);
	}

	/**
	 * Check that given position on board has neighbour.
	 * @param row the row
	 * @param column the column
	 * @return true if has neighbours
	 */
	public boolean hasNeighbour(int row, int column) {
		return hasValueCell(row+1, column)
				|| hasValueCell(row-1, column)
				|| hasValueCell(row, column+1)
				|| hasValueCell(row, column-1);
	}

	/**
	 * Check that given position on board has value
	 * @param row row
	 * @param column column
	 * @return true if has value
	 */
	public boolean hasValueCell(int row, int column) {
		if(row < boardSize && row >=0 && column < boardSize && column >=0 && board[row][column] != null) {
			return true;
		}
		return false;
	}


	/**
	 * With given Tile checks that word able to build horizontally/vertically is correct.
	 * @param tile Tile
	 * @param horizontally check horizontally if true, vertically if false;
	 * @return points if word correct, else error code
	 */
	public int checkPossibleWord(Tile tile, boolean horizontally) {

		List<Tile> tiles = new ArrayList<Tile>();
		tiles.add(tile);
		StringBuilder word = new StringBuilder(String.valueOf(tile.getLetter()));

		int startRow = tile.getRow();
		int startColumn = tile.getColumn();
		int i;
		int j;
		if (horizontally) {
			i = startColumn - 1;
			j = startRow;
		} else {
			i = tile.getColumn();
			j = tile.getRow() - 1;
		}

		/* Go left or up */
		while( i >= 0 && j >= 0 && (board[j][i] != null)) {
			word.insert(0, board[j][i].getLetter());
			tiles.add(0,board[j][i]);
			if (horizontally) {
				i--;
			} else {
				j--;
			}
		}
		if(horizontally) {
			i = startColumn + 1;
		} else {
			j = startRow + 1;
		}

		/* Go right or down */
		while( i < boardSize && j < boardSize && (board[j][i] != null)) {
			word.append(board[j][i].getLetter()) ;
			tiles.add(board[j][i]);
			if (horizontally) {
				i++;
			} else {
				j++;
			}
		}

		if(word.length() == 1) {
			return 0;
		}

		if(tree.searchWord(word.toString())) {
			return getScore(tiles);
		}
		return INVALID_WORD_ERROR;
	}

	/**
	 * Sort list of Tiles by columns using insertion sort
	 * @param input Tiles list
	 */
	public void insertionSortHorizontal(List<Tile> input){
		for (int i=1; i<input.size(); i++) {
			for(int j=i; j>0; j--){
				if(input.get(j).getColumn() < input.get(j-1).getColumn()) {
					Collections.swap(input, j, j-1);
				}
			}
		}
	}

	/**
	 * Sort list of Tiles by rows using insertion sort
	 * @param input Tiles list
	 */
	public void insertionSortVertical(List<Tile> input){
		for (int i=1; i<input.size(); i++) {
			for(int j=i; j>0; j--){
				if(input.get(j).getRow() < input.get(j-1).getRow()) {
					Collections.swap(input, j, j-1);
				}
			}
		}
	}

	/**
	 * Count score for word being beware for board bonuses and board situation.
	 * @param tiles word
	 * @return points count
	 */
	public int getScore(List<Tile> tiles) {
		if(tiles.size() == 1) {
			return 0;
		}

		int score = 0;
		int multi = 1;

		for(Tile tile : tiles) {
			if (board[tile.getRow()][tile.getColumn()] != null) {
				score += tile.getPoints();
			} else {
				switch (boardBonuses[tile.getRow()][tile.getColumn()]) {
					case DOUBLE_LETTER:
						score += tile.getPoints() * 2;
						break;
					case TRIPLE_LETTER:
						score += tile.getPoints() * 3;
						break;
					case DOUBLE_WORD:
						score += tile.getPoints();
						multi *= 2;
						break;
					case TRIPLE_WORD:
						score += tile.getPoints();
						multi *= 3;
						break;
					default:
						score += tile.getPoints();
				}
			}
		}

		return (score * multi);
	}

	/**
	 * Get dictionary tree.
	 * @return tree
	 */
	public WordsTree getTree() {
		return tree;
	}

	/**
	 * Get board scheme.
	 * @return board scheme
	 */
	public BoardBonus[][] getBoardBonuses() {
		return boardBonuses;
	}

	/**
	 * Get board size.
	 * @return board size
	 */
	public int getBoardSize() {
		return boardSize;
	}

	/**
	 * Get board.
	 * @return board
	 */
	public Tile[][] getBoard() {
		return board;
	}

	/**
	 * Check whether first move was done before.
	 * @return true if was
	 */
	public boolean isFirstMoveDone() {
		return firstMoveDone;
	}

}
