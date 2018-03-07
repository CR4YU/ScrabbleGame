package main.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.game.DataManager;
import main.game.GameMode;
import main.game.Player;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller of game menu.<br>
 * Represents window of menu with options to choose by user.<br>
 * Current menu fields:
 *      Single Player,
 *      Multi Player,
 *      Find best move,
 *      Settings.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class MenuController {

	// **************************************************
	// Constants
	// **************************************************

	/** Game window .fxml file */
	private final String GAME_FXML = "../view/game.fxml";

	/** Solver window .fxml file */
	private final String SOLVER_FXML = "../view/solver.fxml";

	/** Settings window .fxml file */
	private final String SETTINGS_FXML = "../view/settings.fxml";

	/** Players setup window .fxml file */
	private final String PLAYER_CONFIGURATION_DIALOG_FXML = "../view/playerConfigurationDialog.fxml";

	/** CSS style for buttons in dialogs */
	private final String DIALOG_BUTTON_STYLE = "-fx-background-radius: 0; -fx-background-color: gray;" +
			" -fx-text-fill: white; -fx-font-family: 'Consolas'; -fx-font-size: 18; ";


	// **************************************************
	// FXML view fields
	// **************************************************

	/** Main menu BorderPane */
	@FXML
	private BorderPane mainMenuBorderPane;

	/** Single player field */
	@FXML
	private VBox singlePlayerFieldVBox;

	/** Multi player field */
	@FXML
	private VBox multiPlayerFieldVBox;

	/** Solver field */
	@FXML
	private VBox cpuMoveFieldVBox;


	// **************************************************
	// Fields
	// **************************************************

	/** List of created players, used by NormalGameController to init NormalGame*/
	public static List<Player> players;


	// **************************************************
	// Methods
	// ************************************************* *

	/**
	 * Init method of controller.
	 */
	public void initialize() {

		try{
			DataManager.loadConfig();
		}
		catch (IOException | XMLStreamException e){
			ErrorDialog.show(e, "Read config error");
		}
	}

	/**
	 * Start new game depending on mode user clicked.
	 * @param event clicked mode
	 */
	@FXML
	public void startNewGame(Event event) {
		GameMode mode = GameMode.SINGLE_PLAYER;
		if(((VBox) event.getSource()).equals(singlePlayerFieldVBox)){
			mode = GameMode.SINGLE_PLAYER;
		} else if(((VBox) event.getSource()).equals(multiPlayerFieldVBox)){
			mode = GameMode.MULTI_PLAYER;
		} else if(((VBox) event.getSource()).equals(cpuMoveFieldVBox)){
			mode = GameMode.SOLVER;
		}

		DataManager.setGameMode(mode);

		if (mode == GameMode.MULTI_PLAYER || mode == GameMode.SINGLE_PLAYER){
			/* Open players setup window ang get players */
			players = getPlayersFromPlayerConfigurationDialog();
			if(players != null) {
				openNewWindow(GAME_FXML);
			}
		} else {
			openNewWindow(SOLVER_FXML);
		}
	}

	/**
	 * Open window to let enter all players information such as nickname or color. <br>
	 * For CPU players difficulty must be provided.
	 * @return list of players
	 */
	private List<Player> getPlayersFromPlayerConfigurationDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		dialog.initOwner(mainMenuBorderPane.getScene().getWindow());
		FXMLLoader fxmlLoader = new FXMLLoader();
		dialog.setTitle("Players Configuration");
		fxmlLoader.setLocation(getClass().getResource(PLAYER_CONFIGURATION_DIALOG_FXML));

		dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
		dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

		try {
			dialog.getDialogPane().setContent(fxmlLoader.load());
		} catch (IOException e) {
			ErrorDialog.show(e, "Couldn't load players configuration dialog");
			return null;
		}

		ButtonBar buttonBar = (ButtonBar)dialog.getDialogPane().lookup(".button-bar");
		buttonBar.getButtons().forEach(b->b.setStyle(DIALOG_BUTTON_STYLE));

		Optional<ButtonType> result = dialog.showAndWait();
		if(result.isPresent() && result.get() == ButtonType.OK) {
			PlayerConfigurationDialogController playerConfigurationDialogController = fxmlLoader.getController();
			/* Return players from controller */
			return playerConfigurationDialogController.getPlayers();
		}
		return null;
	}

	/**
	 * Open settings window.
	 */
	@FXML
	public void openSettingsWindow() {
		openNewWindow(SETTINGS_FXML);
	}

	/**
	 * Go to new window with given .fxml file
	 * @param fxmlFile .fxml file path
	 */
	@FXML
	public void openNewWindow(String fxmlFile) {
		try{
			Parent parent = FXMLLoader.load(getClass().getResource(fxmlFile));
			Scene newScene = new Scene(parent);
			Stage appStage = (Stage) mainMenuBorderPane.getScene().getWindow();
			appStage.setScene(newScene);
			appStage.show();
		} catch (IOException e) {
			ErrorDialog.show(e, "Couldn't open window " + fxmlFile);
		}
	}

}


