package tests;

import main.game.BoardBonus;
import main.game.DataManager;
import main.game.GameMode;
import main.game.GameVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import javax.xml.stream.XMLStreamException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DataManagerTest {

	@Test
	@DisplayName("Should throw Exception")
	public void failedConfigLoadShouldThrowException() {

		assertThrows(Exception.class, () -> {
			DataManager.loadConfig("fileThatNotExists.xml");
		});
	}

	@Test
	@DisplayName("Check values after wrong parameters on setters")
	public void defaultValuesSettersTest() {

		int boardSizeBefore = DataManager.getBoardSize();

		DataManager.setBoardSize(0);
		assertEquals(boardSizeBefore, DataManager.getBoardSize());
		DataManager.setBoardSize(-3);
		assertEquals(boardSizeBefore, DataManager.getBoardSize());

	}

	@Test
	@DisplayName("Check values after setters")
	public void valuesSettersTest() {
		DataManager.setGameMode(GameMode.MULTI_PLAYER);
		assertEquals(GameMode.MULTI_PLAYER, DataManager.getGameMode());
		DataManager.setGameMode(GameMode.SINGLE_PLAYER);
		assertEquals(GameMode.SINGLE_PLAYER, DataManager.getGameMode());
		DataManager.setGameMode(GameMode.SOLVER);
		assertEquals(GameMode.SOLVER, DataManager.getGameMode());

		DataManager.setGameVersion(GameVersion.WORDS_WITH_FRIENDS_15x15);
		assertEquals(GameVersion.WORDS_WITH_FRIENDS_15x15, DataManager.getGameVersion());
		DataManager.setGameVersion(GameVersion.WORDS_WITH_FRIENDS_11x11);
		assertEquals(GameVersion.WORDS_WITH_FRIENDS_11x11, DataManager.getGameVersion());
		DataManager.setGameVersion(GameVersion.SCRABBLE_15x15);
		assertEquals(GameVersion.SCRABBLE_15x15, DataManager.getGameVersion());

		DataManager.setBoardSize(11);
		assertEquals(11, DataManager.getBoardSize());

	}

	@Test
	@DisplayName("Check values after save and load")
	public void saveConfigTest() {
		try {
			DataManager.loadConfig();
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}
		DataManager.setBotTime(3);
		try {
			DataManager.saveConfig();
			DataManager.loadConfig();
		} catch (IOException | XMLStreamException e) {
			e.printStackTrace();
		}
		assertEquals(3, DataManager.getBotTime());
	}

	@Test
	@DisplayName("Should properly load board scheme")
	public void getBoardSchemeTest() {
		DataManager.setGameVersion(GameVersion.WORDS_WITH_FRIENDS_15x15);
		BoardBonus[][] boardBonuses = null;
		try{
			boardBonuses = DataManager.getBoardScheme();
		} catch (IOException e){
			e.printStackTrace();
		}

		assertEquals(BoardBonus.BEGIN.toString(), boardBonuses[7][7].toString());
		assertEquals(BoardBonus.TRIPLE_WORD.toString(), boardBonuses[0][3].toString());


	}
}