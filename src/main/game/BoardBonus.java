package main.game;

/**
 * Enum class with possible board bonuses.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public enum BoardBonus {

	// **************************************************
	// Constants
	// **************************************************

	BEGIN("\u2605"),
	STANDARD(""),
	DOUBLE_WORD("DW"),
	TRIPLE_WORD("TW"),
	DOUBLE_LETTER("DL"),
	TRIPLE_LETTER("TL");

	// **************************************************
	// Fields
	// **************************************************

	/** Board bonus as string */
	private String shortcut;

	// **************************************************
	// Constructors
	// **************************************************

	/**
	 * Parameterized constructor.
	 * @param shortcut string shortcut
	 */
	BoardBonus(String shortcut) {
		this.shortcut = shortcut;
	}

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Return shortcut as String
	 * @return shortcut
	 */
	@Override
	public String toString() {
		return shortcut;
	}
}
