package main.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.game.DataManager;
import main.game.GameVersion;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * Controller of settings window.<br>
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class SettingsController {

	// **************************************************
	// Constants
	// **************************************************

	/** Menu window .fxml file */
	private final String MENU_FXML= "../view/menu.fxml";

	private final int INFO_LABEL_TIME_MILLIS = 3000;

	// **************************************************
	// FXML view fields
	// **************************************************

	/** Main pane of settings window */
	@FXML
	private BorderPane settingsBorderPane;

	/** ChoiceBox with game versions */
	@FXML
	private ChoiceBox gameVersionChoiceBox;

	/** Dictionary file path Label */
	@FXML
	private Label dictionaryFileLabel;

	/** Bot time in seconds Spinner */
	@FXML
	private Spinner botTimeScaleSpinner;

	/** Info Label */
	@FXML
	private Label savedInfoLabel;

	/** Info Label */
	@FXML
	private Label defaultRestoredInfoLabel;

	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Open menu window.
	 */
	@FXML
	public void backToMenu() {
		openNewWindow(MENU_FXML);
	}

	/**
	 * Init method of controller.
	 * Set values of all controls.
	 */
	public void initialize() {
		gameVersionChoiceBox.setValue(DataManager.getGameVersion().toString());
		dictionaryFileLabel.setText(DataManager.getDictionaryFilePath());
		SpinnerValueFactory<Integer> valueFactory =
				new SpinnerValueFactory.IntegerSpinnerValueFactory(DataManager.MIN_BOT_TIME, DataManager.MAX_BOT_TIME, DataManager.getBotTime());
		botTimeScaleSpinner.setValueFactory(valueFactory);
		botTimeScaleSpinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
	}

	/**
	 * Open window to let user select dictionary file.
	 */
	@FXML
	public void changeDictionaryFile() {
		Stage stage = (Stage) settingsBorderPane.getScene().getWindow();
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open dictionary file");
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("Text Files", "*.txt"),
				new FileChooser.ExtensionFilter("All Files", "*.*"));
		File selectedFile = fileChooser.showOpenDialog(stage);
		if (selectedFile != null) {
			dictionaryFileLabel.setText(selectedFile.getPath());
		}
	}

	/**
	 * Save current settings.
	 */
	@FXML
	public void saveSettings() {
		String gameVersionString = gameVersionChoiceBox.getSelectionModel().getSelectedItem().toString();
		DataManager.setGameVersion(GameVersion.getEnum(gameVersionString));
		DataManager.setBotTime((int)botTimeScaleSpinner.getValue());

		DataManager.setDictionaryFilePath(dictionaryFileLabel.getText());
		try {
			DataManager.saveConfig();
		} catch (FileNotFoundException | XMLStreamException e) {
			ErrorDialog.show(e, "Could not save config");
		}
		showSaveInfoLabel();
	}

	/**
	 * Restore default settings.
	 */
	@FXML
	public void restoreDefaultSettings() {
		gameVersionChoiceBox.setValue(DataManager.DEFAULT_GAME_VERSION.toString());
		dictionaryFileLabel.setText(DataManager.DEFAULT_DICTIONARY_FILE);
		SpinnerValueFactory<Integer> valueFactory =
				new SpinnerValueFactory.IntegerSpinnerValueFactory(DataManager.MIN_BOT_TIME, DataManager.MAX_BOT_TIME, DataManager.DEFAULT_BOT_TIME_SECONDS);
		botTimeScaleSpinner.setValueFactory(valueFactory);
		showDefaultSettingsInfoLabel();
	}


	/**
	 * Go to new window with given .fxml file
	 * @param fxmlFile .fxml file path
	 */
	private void openNewWindow(String fxmlFile) {
		try{
			Parent parent = FXMLLoader.load(getClass().getResource(fxmlFile));
			Scene newScene = new Scene(parent);
			Stage appStage = (Stage) settingsBorderPane.getScene().getWindow();
			appStage.setScene(newScene);
			appStage.show();
		} catch (IOException e) {
			ErrorDialog.show(e, "Could not load window " + fxmlFile);
		}
	}

	/**
	 * Shows info Label for specified time.
	 */
	private void showDefaultSettingsInfoLabel() {
		new Thread(() -> {
			Platform.runLater(()-> {
				defaultRestoredInfoLabel.setVisible(true);
			});
				try {
					Thread.sleep(INFO_LABEL_TIME_MILLIS);
				} catch (InterruptedException e) {
					ErrorDialog.show(e, "Failed to sleep");
				}
			defaultRestoredInfoLabel.setVisible(false);

		}).start();
	}

	/**
	 *  Shows info Label for specified time.
	 */
	private void showSaveInfoLabel() {
		new Thread(() -> {
			Platform.runLater(()-> {
				savedInfoLabel.setVisible(true);
			});
			try {
				Thread.sleep(INFO_LABEL_TIME_MILLIS);
			} catch (InterruptedException e) {
				ErrorDialog.show(e, "Failed to sleep");
			}
			savedInfoLabel.setVisible(false);

		}).start();
	}

}
