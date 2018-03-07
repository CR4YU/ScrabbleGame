package main.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Controller of intro of the game.<br>
 * Shows window with background image.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class IntroController {

	// **************************************************
	// Constants
	// **************************************************

	/** Menu window .fxml file */
	private final String MENU_FXML= "../view/menu.fxml";

	// **************************************************
	// FXML view fields
	// **************************************************

	/** Main pane of window */
	@FXML
	GridPane gridPane;

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Handles pressed button on keyboard. Go to menu if ENTER pressed.
	 * @param keyEvent pressed key
	 */
	@FXML
	public void handleKeyPressed(KeyEvent keyEvent) {
		if (keyEvent.getCode().equals(KeyCode.ENTER)) {
			openNewWindow(MENU_FXML);
		}
	}

	/**
	 * Go to menu window
	 */
	@FXML
	public void openMenuWindow() {
		openNewWindow(MENU_FXML);
	}

	/**
	 * Go to new window with given .fxml file
	 * @param fxmlFile .fxml file path
	 */
	public void openNewWindow(String fxmlFile) {
		try{
			Parent parent = FXMLLoader.load(getClass().getResource(fxmlFile));
			Scene newScene = new Scene(parent);
			Stage appStage = (Stage) gridPane.getScene().getWindow();
			appStage.setScene(newScene);
			appStage.show();
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not load window " + fxmlFile);
		}
	}


}
