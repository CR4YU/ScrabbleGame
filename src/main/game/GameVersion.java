package main.game;

/**
 * Enum class. Contains constants
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public enum GameVersion {

	// **************************************************
	// Constants
	// **************************************************

	SCRABBLE_15x15("Scrabble 15x15"),
	WORDS_WITH_FRIENDS_15x15("Words With Friends 15x15"),
	WORDS_WITH_FRIENDS_11x11("Words With Friends 11x11"),
	CUSTOM("Custom");

	// **************************************************
	// Fields
	// **************************************************

	/** Game version in String */
	private final String versionString;

	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor with String.
	 * @param versionString String version of enum
	 */
	GameVersion(String versionString) {
		this.versionString = versionString;
	}

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * With given string get proper enum.
	 * @param versionString String as enum
	 * @return enum
	 */
	public static GameVersion getEnum(String versionString) {
		switch(versionString){
			case "Scrabble 15x15":
				return SCRABBLE_15x15;
			case "Words With Friends 11x11":
				return WORDS_WITH_FRIENDS_11x11;
			case "Words With Friends 15x15":
				return WORDS_WITH_FRIENDS_15x15;
			case "Custom":
				return CUSTOM;
		}
		return SCRABBLE_15x15;
	}

	/**
	 * Return enum as String.
	 * @return enum as string
	 */
	@Override
	public String toString() {
		return versionString;
	}
}
