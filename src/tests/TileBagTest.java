package tests;

import main.game.GameVersion;
import main.game.TileBag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TileBagTest {

	TileBag tileBag;

	@Test
	@DisplayName("Scrabble tile bag should have 100 tiles")
	public void scrabbleTilesCountTest() {
		try {
			tileBag = new TileBag(GameVersion.SCRABBLE_15x15);
			assertEquals(100, tileBag.getRemainingTilesCount());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	@DisplayName("Words With Friends 15x15 tile bag should have 104 tiles")
	public void wordsWithFriends15x15TilesCountTest() {
		try {
			tileBag = new TileBag(GameVersion.WORDS_WITH_FRIENDS_15x15);
			assertEquals(104, tileBag.getRemainingTilesCount());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Test
	@DisplayName("Words With Friends 11x11 tile bag should have 52 tiles")
	public void wordsWithFriends11x11TilesCountTest() {
		try {
			tileBag = new TileBag(GameVersion.WORDS_WITH_FRIENDS_11x11);
			assertEquals(52, tileBag.getRemainingTilesCount());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}