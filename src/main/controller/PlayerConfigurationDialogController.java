package main.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import main.game.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller of players setup window.<br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class PlayerConfigurationDialogController {

	// **************************************************
	// FXML view fields
	// **************************************************

	/** Main pane of dialog */
	@FXML
	private DialogPane dialogPane;

	/** Grid pane with players */
	@FXML
	private GridPane mainGridPane;

	/** Checkbox of bot option in 1st row */
	@FXML
	private CheckBox bot1CheckBox;

	/** Checkbox of bot option in 2nd row */
	@FXML
	private CheckBox bot2CheckBox;

	/** Colors of players */
	@FXML
	private ColorPicker player1ColorPicker, player2ColorPicker, player3ColorPicker, player4ColorPicker;

	/** Difficulty slider of bot in 2nd row */
	@FXML
	private Slider bot2DifficultySlider;

	/** Add player button in 3rd row */
	@FXML
	private Button addPlayer3;

	// **************************************************
	// Fields
	// **************************************************

	/** Count of currently active players */
	private int playersCount = 2;

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Init method of controller.
	 */
	public void initialize() {
		/* If single player mode chosen, disable possibility of adding more players and changing players type */
		if(DataManager.getGameMode() == GameMode.SINGLE_PLAYER) {
			bot1CheckBox.setDisable(true);
			bot2CheckBox.setDisable(true);
			bot2CheckBox.setSelected(true);
			addPlayer3.setVisible(false);
			bot2DifficultySlider.setVisible(true);
		}
	}

	/**
	 * When user clicks PLUS button, and new row (make visible actually) with next player.
	 */
	@FXML
	private void addPlayer() {
		playersCount++;
		Scene scene = dialogPane.getScene();
		Button addButton = (Button) scene.lookup("#addPlayer"+toStr(playersCount));
		addButton.setVisible(false);
		if(playersCount < 4) {
			addButton = (Button) scene.lookup("#addPlayer"+toStr(playersCount+1));
			addButton.setVisible(true);
		}
		HBox playerHBox = (HBox) scene.lookup("#player"+toStr(playersCount)+"HBox");
		playerHBox.setVisible(true);
	}

	/**
	 * Helper function to convert int -> String.
	 * @param i integer
	 * @return as string
	 */
	private String toStr(int i){
		return Integer.toString(i);
	}

	/**
	 * Show difficulty slider when user clicked botCheckBox
	 * @param event clicked checkbox
	 */
	@FXML
	public void setDifficultySlider(Event event) {
		CheckBox checkBox = (CheckBox)event.getSource();
		HBox hBox= (HBox) checkBox.getParent();
		Slider slider = (Slider) hBox.getChildren().get(4);
		if(checkBox.isSelected()) {
			slider.setVisible(true);
		} else {
			slider.setVisible(false);
		}
	}

	/**
	 * Return list of entered players.
	 * @return players
	 */
	public List<Player> getPlayers() {
		List<Player> players = new ArrayList<Player>();

		List<HBox> hBoxList = mainGridPane.getChildren()
				.stream()
				.filter(p -> p instanceof HBox)
				.map(p -> (HBox) p)
				.collect(Collectors.toList());

		for (int i = 0; i < playersCount; i++) {
			String nick = ((TextField) (((hBoxList.get(i)).getChildren().get(1)))).getText();
			nick = (nick.equals(""))? "Player "+(i+1) : nick;
			Color color = ((ColorPicker) (((hBoxList.get(i)).getChildren().get(2)))).getValue();
			boolean isBot = ((CheckBox) (((hBoxList.get(i)).getChildren().get(3)))).isSelected();
			if(isBot) {
				int difficulty = (int)((Slider) (((hBoxList.get(i)).getChildren().get(4)))).getValue();
				players.add(new PlayerBot("(CPU)"+nick, color, difficulty));
			} else {
				players.add(new PlayerHuman(nick, color));
			}
		}
		return players;
	}
}
