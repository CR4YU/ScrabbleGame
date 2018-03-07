package main.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import main.game.Player;

import java.util.Collections;
import java.util.List;

/**
 * Controller to handle end of game.<br>
 * Shows small window with players list and theirs scores.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class GameEndController {

	// **************************************************
	// Constants
	// **************************************************

	/**  CSS class for Label with player color */
	private final String COLOR_LABEL_STYLE_CLASS = "colorLabel";

	/**  CSS class for Label with player's nick */
	private final String NICK_LABEL_STYLE_CLASS = "nickLabel";

	/** CSS class for Label with player's points */
	private final String POINTS_LABEL_STYLE_CLASS = "pointsLabel";

	/** CSS class for HBox with all labels from above */
	private final String PLAYER_HBOX_STYLE_CLASS = "playerHBox";

	// **************************************************
	// FXML view fields
	// **************************************************

	/** Represents vertical box/list with players */
	@FXML
	private VBox pointsTable;

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Create and show table with players and their scores
	 * @param players players to be shown
	 */
	public void showConclusionTable(List<Player> players) {
		sortPlayersByPoints(players);

		for(Player player : players) {
			HBox hbox = new HBox();
			hbox.getStyleClass().add(PLAYER_HBOX_STYLE_CLASS);
			Label nickLabel = new Label(player.getNick());
			nickLabel.getStyleClass().add(NICK_LABEL_STYLE_CLASS);
			Label pointsLabel = new Label(player.getPoints()+"");
			Label colorLabel = new Label();
			colorLabel.setBackground(new Background(new BackgroundFill(player.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
			colorLabel.getStyleClass().add(COLOR_LABEL_STYLE_CLASS);
			pointsLabel.getStyleClass().add(POINTS_LABEL_STYLE_CLASS);
			hbox.getChildren().add(colorLabel);
			hbox.getChildren().add(nickLabel);
			hbox.getChildren().add(pointsLabel);
			HBox.setMargin(colorLabel, new Insets(0,10,0,0));
			HBox.setMargin(nickLabel, new Insets(0,10,0,0));
			hbox.setAlignment(Pos.CENTER);
			pointsTable.getChildren().add(hbox);
		}

	}

	/**
	 * Sort players by their scores using insertion sort algorithm.
	 * @param players players to be sorted.
	 */
	private void sortPlayersByPoints(List<Player> players) {
		for (int i=1; i<players.size(); i++) {
			for(int j=i; j>0; j--){
				if(players.get(j).getPoints() > players.get(j-1).getPoints()) {
					Collections.swap(players, j, j-1);
				}
			}
		}

	}
}
