package main.game;

import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Data Manager. Contains fields and methods used by game.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class DataManager {

	// **************************************************
	// Constants
	// **************************************************

	/** Default game mode */
	public static final GameMode DEFAULT_GAME_MODE = GameMode.SINGLE_PLAYER;

	/** Default game version */
	public static final GameVersion DEFAULT_GAME_VERSION = GameVersion.SCRABBLE_15x15;

	/** Default dictionary filepath */
	public static final String DEFAULT_DICTIONARY_FILE = "data_files/words.txt";

	/** Scrabble 15x15 board scheme file path */
	public static final String SCRABBLE_15x15_BOARD_FILE = "data_files/scrabble_15_board.txt";

	/** Words With Friends 15x15 board scheme file path */
	public static final String WORDS_WITH_FRIENDS_15x15_BOARD_FILE = "data_files/wwf_15_board.txt";

	/** Words With Friends 11x11 board scheme file path */
	public static final String WORDS_WITH_FRIENDS_11x11_BOARD_FILE = "data_files/wwf_11_board.txt";

	/** Custom board scheme file path */
	public static final String CUSTOM_BOARD_FILE = "data_files/custom_board.txt";

	/** Custom points pattern file path */
	public static final String CUSTOM_POINTS_PATTERN_FILE = "data_files/custom_points.txt";

	/** Custom tile bag pattern file path */
	public static final String CUSTOM_TILEBAG_PATTERN_FILE = "data_files/custom_tilebag.txt";

	/** Default config file path */
	private static final String DEFAULT_CONFIG_FILE_NAME = "data_files/config.xml";

	/** Default board size */
	public static final int DEFAULT_BOARD_SIZE = 15;

	/** Min difficulty */
	public static final int MIN_DIFFICULTY = 1;

	/** Max difficulty */
	public static final int MAX_DIFFICULTY = 10;

	/** Max tiles in rack */
	public static final int RACK_SIZE = 7;

	/** Bingo in Scrabble */
	public static final int SCRABBLE_BINGO = 50;

	/** Bingo in Words With Friends */
	public static final int WORDS_WITH_FRIENDS_BINGO = 35;

	/** Default bot time in seconds */
	public static final int DEFAULT_BOT_TIME_SECONDS = 1;

	/** Min bot time in seconds */
	public static final int MIN_BOT_TIME = 1;

	/** Max bot time in seconds */
	public static final int MAX_BOT_TIME = 10;

	/** Max passes count */
	public static final int MAX_PASSES = 2;

	/** Config field in xml */
	private static final String CONFIG = "config";

	/** Game version field in xml */
	private static final String GAME_VERSION = "game_version";

	/** Bot time field in xml */
	private static final String BOT_TIME = "bot_time";

	/** Dictionary field in xml */
	private static final String DICTIONARY = "dictionary";


	// **************************************************
	// Fields
	// **************************************************

	/** Game mode */
	private static GameMode gameMode = DEFAULT_GAME_MODE;

	/** Game version */
	private static GameVersion gameVersion = DEFAULT_GAME_VERSION;

	/** Board size */
	private static int boardSize = DEFAULT_BOARD_SIZE;

	/** Dictionary file path */
	private static String dictionaryFilePath = DEFAULT_DICTIONARY_FILE;

	/** Default bot time in seconds */
	private static int botTime = DEFAULT_BOT_TIME_SECONDS;


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Get game mode.
	 * @return game mode
	 */
	public static GameMode getGameMode() {
		return gameMode;
	}

	/**
	 * Set game mode
	 * @param mode game mode
	 */
	public static void setGameMode(GameMode mode) {
		DataManager.gameMode = mode;
	}

	/**
	 * Get game version.
	 * @return game version
	 */
	public static GameVersion getGameVersion() {
		return gameVersion;
	}

	/**
	 * Set game version and proper board size
	 * @param gameVersion game version
	 */
	public static void setGameVersion(GameVersion gameVersion) {
		switch(gameVersion) {
			case SCRABBLE_15x15:
				setBoardSize(15);
				break;
			case WORDS_WITH_FRIENDS_11x11:
				setBoardSize(11);
				break;
			case WORDS_WITH_FRIENDS_15x15:
				setBoardSize(15);
				break;
		}
		DataManager.gameVersion = gameVersion;
	}

	/**
	 * Get bot time in seconds.
	 * @return bot time
	 */
	public static int getBotTime() {
		return botTime;
	}

	/**
	 * Set bot time.
	 * @param botTime time in seconds
	 */
	public static void setBotTime(int botTime) {
		if (botTime >= MIN_BOT_TIME && botTime<= MAX_BOT_TIME) {
			DataManager.botTime = botTime;
		}
	}

	/**
	 * Get dictionary file path.
	 * @return dictionary file path
	 */
	public static String getDictionaryFilePath() {
		return dictionaryFilePath;
	}

	/**
	 * Set dictionary file path.
	 * @param dictionaryFilePath file path of dictionary
	 */
	public static void setDictionaryFilePath(String dictionaryFilePath) {
		DataManager.dictionaryFilePath = dictionaryFilePath;
	}

	/**
	 * Get board size.
	 * @return board size
	 */
	public static int getBoardSize() {
		return boardSize;
	}

	/**
	 * Set board size.
	 * @param boardSize board size
	 */
	public static void setBoardSize(int boardSize) {
		if(!(boardSize < 1)) {
			DataManager.boardSize = boardSize;
		}
	}

	/**
	 * Load config with default filename.
	 * @throws IOException can be thrown during file loading
	 * @throws XMLStreamException can be thrown during xml parsing
	 */
	public static void loadConfig() throws IOException, XMLStreamException {
		loadConfig(DEFAULT_CONFIG_FILE_NAME);
	}


	/**
	 * Load config with given filename.
	 * @param path config file path
	 * @throws IOException can be thrown during file loading
	 * @throws XMLStreamException can be thrown during xml parsing
	 */
	public static void loadConfig(String path) throws IOException, XMLStreamException  {
		if (path == null) {
			path = DEFAULT_CONFIG_FILE_NAME;
		}

		// Create a new XMLInputFactory
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		// Create eventReader
		InputStream in = new FileInputStream(path);
		XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();

			if (event.isStartElement()) {

				if (event.asStartElement().getName().getLocalPart().equals(GAME_VERSION)) {
					event = eventReader.nextEvent();
					setGameVersion(GameVersion.getEnum(event.asCharacters().getData()));
					continue;
				}

				if (event.asStartElement().getName().getLocalPart().equals(BOT_TIME)) {
					event = eventReader.nextEvent();
					setBotTime(Integer.parseInt(event.asCharacters().getData()));
					continue;
				}

				if (event.asStartElement().getName().getLocalPart().equals(DICTIONARY)) {
					event = eventReader.nextEvent();
					setDictionaryFilePath(event.asCharacters().getData());
				}

			}
		}
	}

	/**
	 * Save config with default file name
	 * @throws FileNotFoundException thrown when file not found
	 * @throws XMLStreamException thrown if xml writer fails
	 */
	public static void saveConfig() throws FileNotFoundException, XMLStreamException {
		saveConfig(DEFAULT_CONFIG_FILE_NAME);
	}

	/**
	 * Save config with give file path
	 * @param path file to save
	 * @throws FileNotFoundException thrown when file not found
	 * @throws XMLStreamException thrown if xml writer fails
	 */
	public static void saveConfig(String path) throws FileNotFoundException, XMLStreamException {
		if (path == null) {
			path = DEFAULT_CONFIG_FILE_NAME;
		}

		// Create new XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// create XMLEventWriter
		XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(path));
		// create an EventFactory
		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		// create and write Start Tag
		StartDocument startDocument = eventFactory.createStartDocument();
		eventWriter.add(startDocument);
		eventWriter.add(end);

		StartElement configsStartElement = eventFactory.createStartElement("", "", "configs");
		eventWriter.add(configsStartElement);
		eventWriter.add(end);

		// create config open tag
		StartElement configStartElement = eventFactory.createStartElement("", "", CONFIG);
		eventWriter.add(configStartElement);
		eventWriter.add(end);
		// Write the different nodes

		createNode(eventWriter, GAME_VERSION, gameVersion.toString());
		createNode(eventWriter, DICTIONARY, dictionaryFilePath);
		createNode(eventWriter, BOT_TIME, Integer.toString(botTime));

		eventWriter.add(eventFactory.createEndElement("", "", CONFIG));
		eventWriter.add(end);

		eventWriter.add(eventFactory.createEndElement("", "", "configs"));
		eventWriter.add(end);
		eventWriter.add(eventFactory.createEndDocument());
		eventWriter.close();

	}

	/**
	 * Helper function for saveConfig()
	 * @param eventWriter event writer
	 * @param name name of xml field
	 * @param value value of xml field
	 * @throws XMLStreamException
	 */
	private static void createNode(XMLEventWriter eventWriter, String name, String value) throws XMLStreamException {

		XMLEventFactory eventFactory = XMLEventFactory.newInstance();
		XMLEvent end = eventFactory.createDTD("\n");
		XMLEvent tab = eventFactory.createDTD("\t");
		// create Start node
		StartElement sElement = eventFactory.createStartElement("", "", name);
		eventWriter.add(tab);
		eventWriter.add(sElement);
		// create Content
		Characters characters = eventFactory.createCharacters(value);
		eventWriter.add(characters);
		// create End node
		EndElement eElement = eventFactory.createEndElement("", "", name);
		eventWriter.add(eElement);
		eventWriter.add(end);
	}

	/**
	 * Get board scheme from file
	 * @return board scheme
	 * @throws IOException thrown when file reading fails
	 */
	public static BoardBonus[][] getBoardScheme() throws IOException {
		String boardFile = SCRABBLE_15x15_BOARD_FILE;
		switch (gameVersion) {
			case SCRABBLE_15x15:
				boardFile = SCRABBLE_15x15_BOARD_FILE;
				break;
			case WORDS_WITH_FRIENDS_11x11:
				boardFile = WORDS_WITH_FRIENDS_11x11_BOARD_FILE;
				break;
			case WORDS_WITH_FRIENDS_15x15:
				boardFile = WORDS_WITH_FRIENDS_15x15_BOARD_FILE;
				break;
			case CUSTOM:
				boardFile = CUSTOM_BOARD_FILE;
				break;
		}

		Path path = Paths.get(boardFile);
		BufferedReader br = Files.newBufferedReader(path);
		String line;
		int size = Integer.parseInt(br.readLine());
		if(gameVersion.equals(GameVersion.CUSTOM)) {
			setBoardSize(size);
		}
		BoardBonus[][] board = new BoardBonus[size][size];

		int row = 0;
		while ((line = br.readLine()) != null) {

			String[] tokens = line.split(" ");
			for (int i=0; i<size; i++) {
				String k = tokens[i];
				board[row][i] = (k.equals("S"))? BoardBonus.STANDARD :
						(k.equals("B"))? BoardBonus.BEGIN :
								(k.equals("TW"))? BoardBonus.TRIPLE_WORD :
										(k.equals("DW"))? BoardBonus.DOUBLE_WORD :
												(k.equals("DL"))? BoardBonus.DOUBLE_LETTER :
														BoardBonus.TRIPLE_LETTER;
			}
			row++;
		}

		br.close();

		return board;
	}

	/**
	 * Get int array from file
	 * @param file file path
	 * @return int array with values
	 * @throws IOException thrown when file reading fails
	 */
	public static int[] getPatternFromFile(String file) throws IOException {

		Path path = Paths.get(file);
		BufferedReader br = Files.newBufferedReader(path);

		String[] tokens = br.readLine().split(",");
		int[] array = Arrays.asList(tokens).stream().mapToInt(Integer::parseInt).toArray();

		return array;
	}
}
