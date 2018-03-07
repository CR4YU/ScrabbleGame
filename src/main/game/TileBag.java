package main.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class that represent bag of tiles. Has  points values and count for Tiles in every game version
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class TileBag {

	// **************************************************
	// Constants
	// **************************************************

	/** Points for Tiles in Scrabble */
	private final static int[] SCRABBLE_POINTS_PATTERN = {
			/*blank(*)*/0,
			/*A*/1,
			/*B*/3,
			/*C*/3,
			/*D*/2,
			/*E*/1,
			/*F*/4,
			/*G*/2,
			/*H*/4,
			/*I*/1,
			/*J*/8,
			/*K*/5,
			/*L*/1,
			/*M*/3,
			/*N*/1,
			/*O*/1,
			/*P*/3,
			/*Q*/10,
			/*R*/1,
			/*S*/1,
			/*T*/1,
			/*U*/1,
			/*V*/4,
			/*W*/4,
			/*X*/8,
			/*Y*/4,
			/*Z*/10
	};

	/** Points for Tiles in Words With Friends */
	private final static int[] WORDS_WITH_FRIENDS_POINTS_PATTERN = {
			/*blank(*)*/0,
			/*A*/1,
			/*B*/4,
			/*C*/4,
			/*D*/2,
			/*E*/1,
			/*F*/4,
			/*G*/3,
			/*H*/3,
			/*I*/1,
			/*J*/10,
			/*K*/5,
			/*L*/2,
			/*M*/4,
			/*N*/2,
			/*O*/1,
			/*P*/4,
			/*Q*/10,
			/*R*/1,
			/*S*/1,
			/*T*/1,
			/*U*/2,
			/*V*/5,
			/*W*/4,
			/*X*/8,
			/*Y*/3,
			/*Z*/10
	};

	/** Number of each letter inside Scrabble tile bad */
	private final static int[] SCRABBLE_TILE_BAG_PATTERN = {
			/*blank(*)*/2,
			/*A*/9,
			/*B*/2,
			/*C*/2,
			/*D*/4,
			/*E*/12,
			/*F*/2,
			/*G*/3,
			/*H*/2,
			/*I*/9,
			/*J*/1,
			/*K*/1,
			/*L*/4,
			/*M*/2,
			/*N*/6,
			/*O*/8,
			/*P*/2,
			/*Q*/1,
			/*R*/6,
			/*S*/4,
			/*T*/6,
			/*U*/4,
			/*V*/2,
			/*W*/2,
			/*X*/1,
			/*Y*/2,
			/*Z*/1
	};

	/** Number of each letter inside Words With Friends 15x15 tile bad */
	private final static int[] WORDS_WITH_FRIENDS_15x15_TILE_BAG_PATTERN = {
			/*blank(*)*/2,
			/*A*/9,
			/*B*/2,
			/*C*/2,
			/*D*/5,
			/*E*/13,
			/*F*/2,
			/*G*/3,
			/*H*/4,
			/*I*/8,
			/*J*/1,
			/*K*/1,
			/*L*/4,
			/*M*/2,
			/*N*/5,
			/*O*/8,
			/*P*/2,
			/*Q*/1,
			/*R*/6,
			/*S*/5,
			/*T*/7,
			/*U*/4,
			/*V*/2,
			/*W*/2,
			/*X*/1,
			/*Y*/2,
			/*Z*/1
	};

	/** Number of each letter inside Words With Friends 11x11 tile bad */
	private final static int[] WORDS_WITH_FRIENDS_11x11_TILE_BAG_PATTERN = {
			/*blank(*)*/2,
			/*A*/5,
			/*B*/1,
			/*C*/1,
			/*D*/2,
			/*E*/7,
			/*F*/1,
			/*G*/1,
			/*H*/1,
			/*I*/4,
			/*J*/1,
			/*K*/1,
			/*L*/2,
			/*M*/1,
			/*N*/2,
			/*O*/4,
			/*P*/1,
			/*Q*/1,
			/*R*/2,
			/*S*/4,
			/*T*/2,
			/*U*/1,
			/*V*/1,
			/*W*/1,
			/*X*/1,
			/*Y*/1,
			/*Z*/1
	};

	/** Alphabet */
	private final static char[] gameLetters = "*abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();


	// **************************************************
	// Fields
	// **************************************************

	/** Tiles in bag */
	private List<Tile> tiles;

	/** Current pattern */
	private int[] tileBagPattern = SCRABBLE_TILE_BAG_PATTERN;

	/** Current points values */
	private int[] pointsPattern = SCRABBLE_POINTS_PATTERN;


	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor with game version.
	 * @param gameVersion game version
	 * @throws IOException when file reading fails
	 */
	public TileBag(GameVersion gameVersion) throws IOException{
		switch(gameVersion){
			case SCRABBLE_15x15:
				tileBagPattern = SCRABBLE_TILE_BAG_PATTERN;
				pointsPattern = SCRABBLE_POINTS_PATTERN;
				break;
			case WORDS_WITH_FRIENDS_15x15:
				tileBagPattern = WORDS_WITH_FRIENDS_15x15_TILE_BAG_PATTERN;
				pointsPattern = WORDS_WITH_FRIENDS_POINTS_PATTERN;
				break;
			case WORDS_WITH_FRIENDS_11x11:
				tileBagPattern = WORDS_WITH_FRIENDS_11x11_TILE_BAG_PATTERN;
				pointsPattern = WORDS_WITH_FRIENDS_POINTS_PATTERN;
				break;
			case CUSTOM:
				tileBagPattern = DataManager.getPatternFromFile(DataManager.CUSTOM_TILEBAG_PATTERN_FILE);
				pointsPattern = DataManager.getPatternFromFile(DataManager.CUSTOM_POINTS_PATTERN_FILE);
				break;
		}
		initTileBag();
	}


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Initialize tile bag.
	 */
	public void initTileBag() {
		tiles = new ArrayList<Tile>();
		int k = 0;
		for (int i : tileBagPattern) {
			for (int j=0; j<i; j++) {
				tiles.add(new Tile(gameLetters[k], pointsPattern[k]));
			}
			k++;
		}
		Collections.shuffle(tiles);
	}

	/**
	 * Get count of remaining tiles in bag.
	 * @return number of Tiles left
	 */
	public int getRemainingTilesCount() {
		return tiles.size();
	}

	/**
	 * Get tile from bag and remove.
	 * @return Tile
	 */
	public Tile grabTile() {
		if (!tiles.isEmpty()) {
			return tiles.remove(0);
		}
		return null;
	}

	/**
	 * Get all tiles from bag.
	 * @return tiles
	 */
	public List<Tile> getTiles() {
		return tiles;
	}
}
