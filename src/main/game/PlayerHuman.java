package main.game;

import javafx.scene.paint.Color;

/**
 * Class that represents human player. <br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class PlayerHuman extends Player {

	/**
	 * Parameterized constructor with nick and color
	 * @param nick player's nick
	 * @param color player's color
	 */
	public PlayerHuman(String nick, Color color) {
		super(nick, color);
	}
}
