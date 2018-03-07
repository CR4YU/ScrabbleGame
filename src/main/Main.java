package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.game.DataManager;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;


/**
 * This is the main class of JavaFx application.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class Main extends Application {

	// ******************************
	// Constants
	// ******************************

	/** Title of application */
	public final String TITLE = "Scrabble";

	/** Minimum width of application window */
	public final int APP_MIN_WIDTH = 1200;

	/** Minimum height of application window */
	public final int APP_MIN_HEIGHT = 900;

	/** Intro .fxml file */
	private final String INTRO_FXML = "view/intro.fxml";



	/**
	 * Start method of JavaFx application.
	 *
	 * @param primaryStage main stage
	 * @throws IOException thrown when failed to load .fxml file
	 */
	@Override
	public void start(Stage primaryStage) throws IOException {
		/* Load fxml file */
		Parent root = FXMLLoader.load(getClass().getResource(INTRO_FXML));
		primaryStage.setTitle(TITLE);
		Scene introScene = new Scene(root);
		primaryStage.setScene(introScene);
		/* Set size of window */
		primaryStage.setMinWidth(APP_MIN_WIDTH);
		primaryStage.setMinHeight(APP_MIN_HEIGHT);
		//primaryStage.setResizable(false);
		primaryStage.show();
		introScene.getRoot().requestFocus();
	}

	/**
	 * Main method of application
	 * @param args optional arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * On application exit try to save config file.
	 * @throws XMLStreamException thrown when failed to save config
	 */
	@Override
	public void stop() throws XMLStreamException {
		try {
			DataManager.saveConfig();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
}
