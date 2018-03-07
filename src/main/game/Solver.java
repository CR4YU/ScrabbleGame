package main.game;

import java.io.IOException;
import java.util.*;

/**
 * Solver class. <br>
 * Has PlayerBot instance with maximum difficulty and method to return best move
 * with given board situation and rack.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class Solver extends Game {

	// **************************************************
	// Fields
	// **************************************************

	/** Tile bag with all letters, used by Solver Controller */
	private TileBag tileBag;

	/** Bot instance */
	private PlayerBot bot;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Constructor.
	 * @throws IOException when file loading fails
	 */
	public Solver() throws IOException {

		bot = new PlayerBot("Solver Bot", null, DataManager.MAX_DIFFICULTY);
		bot.setGame(this);

		tileBag = new TileBag(DataManager.getGameVersion());

		/* Sort by letters */
		Collections.sort(tileBag.getTiles(), new Comparator<Tile>() {
			@Override
			public int compare(Tile o1, Tile o2) {
				if(o1.getLetter() > o2.getLetter()){
					return 1;
				} else if(o1.getLetter() < o2.getLetter()) {
					return -1;
				}
				return 0;
			}
		});

	}

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Returns best found move
	 * @param board current board situation
	 * @param rack current tiles on rack
	 * @return best move
	 */
	public Move solve(List<Tile> board, List<Tile> rack) {
		firstMoveDone = !board.isEmpty();
		for(int i=0; i<boardSize; i++){
			for(int j=0; j<boardSize; j++) {
				super.board[i][j] = null;
			}
		}
		board.forEach(tile -> {super.board[tile.getRow()][tile.getColumn()] = tile; });
		bot.setRack(rack);
		return bot.getMove();
	}

	/**
	 * Get tile bag
	 * @return tile bag
	 */
	public TileBag getTileBag() {
		return tileBag;
	}

}
