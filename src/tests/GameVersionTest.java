package tests;

import main.game.GameVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameVersionTest {

	@Test
	@DisplayName("Should return proper enum value while passing string")
	void getEnumTest() {
		assertEquals(GameVersion.SCRABBLE_15x15, GameVersion.getEnum("Scrabble 15x15"));
		assertEquals(GameVersion.WORDS_WITH_FRIENDS_15x15, GameVersion.getEnum("Words With Friends 15x15"));
		assertEquals(GameVersion.WORDS_WITH_FRIENDS_11x11, GameVersion.getEnum("Words With Friends 11x11"));
		assertEquals(GameVersion.CUSTOM, GameVersion.getEnum("Custom"));

		//expect default value if string isn't  proper
		assertEquals(GameVersion.SCRABBLE_15x15, GameVersion.getEnum("Some not proper string"));

	}


}