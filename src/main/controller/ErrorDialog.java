package main.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Class responsible for showing detailed error dialogs.
 *
 * @author Paweł Okrutny
 * @version 1.0 7.11.2017
 */
public class ErrorDialog {

	// **************************************************
	// Constants
	// **************************************************

	/** Title of dialog */
	private final static String ERROR_WINDOW_TITLE = "Error";

	/** Header text */
	private final static String HEADER_TEXT = "Ups! Looks like your Scrabble game had problem.";

	/** Stack trace label text */
	private final static String STACKTRACE_LABEL_TEXT = "The exception stacktrace was:";

	/** Reason label */
	private final static String REASON_LABEL_TEXT = "Cause: ";


	// **************************************************
	// Methods
	// **************************************************

	/**
	 * Shows error dialog with specified exception and reason.
	 * @param ex thrown exception somewhere in code
	 * @param cause the reason of problem
	 */
	public static void show(Exception ex, String cause) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(ERROR_WINDOW_TITLE);
		alert.setHeaderText(HEADER_TEXT);
		alert.setContentText(REASON_LABEL_TEXT + cause);

		/* Create expandable Exception */
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label(STACKTRACE_LABEL_TEXT);

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		/* Set expandable Exception into the dialog pane */
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
}
