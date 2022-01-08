package de.morihofi.tafelquiz;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Optional;

import org.controlsfx.control.StatusBar;
import org.json.JSONArray;
import org.json.JSONObject;

public class QuizEditor {

	@FXML
	private StatusBar sbbottom;

	@FXML
	private ListView<String> lstquestions;

	@FXML
	private Button btnedit;

	@FXML
	private Button btndelete;

	@FXML
	private TextField txtquestion;

	@FXML
	private TextField txtanswer_a;

	@FXML
	private TextField txtanswer_b;

	@FXML
	private TextField txtanswer_c;

	@FXML
	private TextField txtanswer_d;

	@FXML
	private Button btnaddquestion;

	@FXML
	private ToggleGroup question;

	@FXML
	private RadioButton rbanswer_a;

	@FXML
	private RadioButton rbanswer_b;

	@FXML
	private RadioButton rbanswer_c;

	@FXML
	private RadioButton rbanswer_d;

	@FXML
	private MenuItem menquizload;

	@FXML
	private MenuItem menquizsave;

	@FXML
	private Button btnnew;

	@FXML
	private AnchorPane apquestionedit;

	@FXML
	private Button btnmoveup;

	@FXML
	private Button btnmovedown;

	
	@FXML
	private MenuItem mennewquiz;
	
	void redrawquestionlist() {
		
		btnmoveup.setDisable(true);
		btnmovedown.setDisable(true);
		btnnew.setDisable(true);
		btnedit.setDisable(true);
		btndelete.setDisable(true);
		
		sbbottom.setText("Wird verarbeitet...");
		
		lstquestions.getSelectionModel().clearSelection();
		lstquestions.getItems().clear();
		
		JSONObject questioncache = new JSONObject();
		for (Object questionstr : questionarr) {
			questioncache = new JSONObject(questionstr.toString());

			lstquestions.getItems().add(questioncache.getString("question"));
		}
		sbbottom.setText("Bereit.");
		
	
		btnmoveup.setDisable(false);
		btnmovedown.setDisable(false);
		btnnew.setDisable(false);
		btnedit.setDisable(false);
		btndelete.setDisable(false);
		
	}
	@FXML
	void mennewquizclick(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("TafelQuiz Editor");
		alert.setHeaderText("Haben Sie alles gespeichert?");
		alert.setContentText("Eventuell nicht gespeicherte Daten gehen verloren. Möchten Sie fortfahren?");

		ButtonType buttonTypeYes = new ButtonType("Neues Quiz erstellen");
		ButtonType buttonTypeNo = new ButtonType("Abbrechen");

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {
			apquestionedit.setDisable(true);
			questionarr = new JSONArray();
			clearallfields();
			lstquestions.getItems().clear();
		}
		
	}
	
	@FXML
	void btnmoveupclick(ActionEvent event) {
		
		try {
			int selectedindex = lstquestions.getSelectionModel().getSelectedIndex();
			int newindex = selectedindex - 1;
			
			
			if (selectedindex <= 0) {
				System.out.println("Schon am Anfang");
			}else {

				
				//Umwandeln in Liste
				LinkedList<String> questionll= new LinkedList();
				for(Object questionobj : questionarr) {
					String questionarr = questionobj.toString();
					questionll.add(questionarr);
				}
				
				//Tauschen in Liste
				de.morihofi.simpleutils.ArrayListMove.move(questionll, selectedindex, newindex);
				
				//Zurück in JSONArray
				questionarr = new JSONArray();
				
				for(String questionobj : questionll) {
					questionarr.put(new JSONObject(questionobj));
				}
				
				
				redrawquestionlist();
				
				lstquestions.getSelectionModel().select(newindex);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
	}

	@FXML
	void btnmovedownclick(ActionEvent event) {
		
		try {
			int selectedindex = lstquestions.getSelectionModel().getSelectedIndex();
			int newindex = selectedindex + 1;

			int lenght = lstquestions.getItems().size();
			/*
			System.out.println("Lenght: " + lenght);
			System.out.println("Current Index: " + selectedindex);
			System.out.println("New Index: " + newindex);
			System.out.println("-------------------------------");
			*/
			if (selectedindex >= (lenght - 1)) {
				System.out.println("Schon am Ende");
			}else {
			
				//Umwandeln in Liste
				LinkedList<String> questionll= new LinkedList();
				for(Object questionobj : questionarr) {
					String questionarr = questionobj.toString();
					questionll.add(questionarr);
				}
				
				//Tauschen in Liste
				de.morihofi.simpleutils.ArrayListMove.move(questionll, selectedindex, newindex);
				
				//Zurück in JSONArray
				questionarr = new JSONArray();
				
				for(String questionobj : questionll) {
					questionarr.put(new JSONObject(questionobj));
				}

				redrawquestionlist();
				
				lstquestions.getSelectionModel().select(newindex);
				
				
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
	}

	@FXML
	void btnnewclick(ActionEvent event) {
		clearallfields();
		apquestionedit.setDisable(false);
	}

	@FXML
	void menquizloadclick(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("TafelQuiz Editor");
		alert.setHeaderText("Haben Sie alles gespeichert?");
		alert.setContentText("Eventuell nicht gespeicherte Daten gehen verloren. Möchten Sie fortfahren?");

		ButtonType buttonTypeYes = new ButtonType("Quiz öffnen");
		ButtonType buttonTypeNo = new ButtonType("Abbrechen");

		alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {

			try {
				Stage stage = (Stage) btnnew.getScene().getWindow();

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Quiz laden");
				fileChooser.getExtensionFilters()
						.add(new FileChooser.ExtensionFilter("TafelQuiz Quiz-Datei (*.tquiz)", "*.tquiz"));

				File file = fileChooser.showOpenDialog(stage);

				if (file != null) {
					String quizfilename = file.getAbsolutePath();
					sbbottom.setText("Quiz wird geladen...");
					lstquestions.getItems().clear();

					String quizjson = MainController.readLineByLineJava8(quizfilename);
					questionarr = new JSONArray(quizjson);

					
					
					redrawquestionlist();

					sbbottom.setText("Bereit");

				}

			} catch (Exception ex) {
				Alert alert2 = new Alert(AlertType.ERROR);
				alert2.setTitle("Verarbeitungsfehler");
				alert2.setHeaderText("Verarbeitungsfehler");
				alert2.setContentText(ex.getMessage());

				alert2.showAndWait();
			}

		} else {
			return;
		}

	}

	@FXML
	void menquizsaveclick(ActionEvent event) {

		Stage stage = (Stage) btnnew.getScene().getWindow();

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Quiz speichern");
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("TafelQuiz Quiz-Datei (*.tquiz)", "*.tquiz"));
		fileChooser.setInitialFileName("MeinQuiz.tquiz");
		File file = fileChooser.showSaveDialog(stage);

		// System.out.println("Pfad: " + fileloc);

		if (file != null) {
			String fileloc = file.getAbsolutePath();

			boolean exists = (new File(fileloc)).exists();
			if (exists) {
				// File or directory exists
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Datei speichern");
				alert.setHeaderText("Datei existiert bereits");
				alert.setContentText("Die von Ihnen ausgewählte Datei existiert bereits. Möchten Sie sie überschreiben?");

				ButtonType buttonTypeYes = new ButtonType("Ja");
				ButtonType buttonTypeNo = new ButtonType("Nein");

				alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == buttonTypeYes) {

				}
				if (result.get() == buttonTypeNo) {
					return;
				}

			} else {
				// File or directory does not exist
			}

			// Datei schreiben

			try {
				sbbottom.setText("Wird gespeichert...");
				Files.deleteIfExists((new File(fileloc)).toPath());
				MainController.saveStringToFile(fileloc, questionarr.toString());

				Alert alert2 = new Alert(AlertType.INFORMATION);
				alert2.setTitle("Gespeichert!");
				alert2.setHeaderText("Gespeichert!");
				alert2.setContentText("Ihr Quiz wurde erfolgreich gespeichert!");

				alert2.showAndWait();
				sbbottom.setText("Bereit.");
			} catch (IOException ex) {

				Alert alert2 = new Alert(AlertType.ERROR);
				alert2.setTitle("Ein/Ausgabefehler");
				alert2.setHeaderText("Ein/Ausgabefehler");
				alert2.setContentText(ex.getMessage());

				alert2.showAndWait();
				sbbottom.setText("Bereit.");
			}

		}
	}

	@FXML
	void btnaddquestionclick(ActionEvent event) {

		if (txtquestion.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unausgefülltes Feld");
			alert.setHeaderText("Unausgefülltes Feld");
			alert.setContentText("Die Frage muss ausgefüllt sein");
			alert.showAndWait();
			return;
		}

		if (txtanswer_a.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unausgefülltes Feld");
			alert.setHeaderText("Unausgefülltes Feld");
			alert.setContentText("Die Antwortmöglichkeit A muss ausgefüllt sein");
			alert.showAndWait();
			return;
		}

		if (txtanswer_b.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unausgefülltes Feld");
			alert.setHeaderText("Unausgefülltes Feld");
			alert.setContentText("Die Antwortmöglichkeit B muss ausgefüllt sein");
			alert.showAndWait();
			return;
		}

		if (txtanswer_c.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unausgefülltes Feld");
			alert.setHeaderText("Unausgefülltes Feld");
			alert.setContentText("Die Antwortmöglichkeit C muss ausgefüllt sein");
			alert.showAndWait();
			return;
		}

		if (txtanswer_d.getText().equals("")) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Unausgefülltes Feld");
			alert.setHeaderText("Unausgefülltes Feld");
			alert.setContentText("Die Antwortmöglichkeit D muss ausgefüllt sein");
			alert.showAndWait();
			return;
		}

		String correctanswer = null;

		if (rbanswer_a.isSelected()) {
			correctanswer = "a";
		}

		if (rbanswer_b.isSelected()) {
			correctanswer = "b";
		}

		if (rbanswer_c.isSelected()) {
			correctanswer = "c";
		}

		if (rbanswer_d.isSelected()) {
			correctanswer = "d";
		}

		String question = txtquestion.getText();
		String answer_a = txtanswer_a.getText();
		String answer_b = txtanswer_b.getText();
		String answer_c = txtanswer_c.getText();
		String answer_d = txtanswer_d.getText();

		JSONObject questionobj = new JSONObject();
		questionobj.put("question", question);
		questionobj.put("correctanswer", correctanswer);
		questionobj.put("answer_a", answer_a);
		questionobj.put("answer_b", answer_b);
		questionobj.put("answer_c", answer_c);
		questionobj.put("answer_d", answer_d);

		
		Boolean questionexists = false;
		AddUpdateOperation operation = null;
		
		int orgindex = 0;

		for(Object questionobjo : questionarr) {
			String questionarrstr = questionobjo.toString();
			JSONObject questionobj1 = new JSONObject(questionarrstr);
				
			if(questionobj1.getString("question").startsWith(question)) {
				questionexists = true;
				
				break;
			}
			
			
			orgindex = orgindex + 1;
			
			
			
		}
		
		System.out.println("[DEBUG] questionexists: " + questionexists);
		System.out.println("[DEBUG] Question found on index: " + orgindex);
		

		if(questionexists) {
			
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Frage hinzufügen/aktualisieren");
			alert.setHeaderText("Frage hinzufügen/aktualisieren");
			alert.setContentText("Es existiert bereits eine ähnliche Frage. Möchten Sie eine neue Frage hinzufügen oder die bisherige aktualiseren?");

			ButtonType buttonTypeUpdate = new ButtonType("Aktualisieren");
			ButtonType buttonTypeNew = new ButtonType("Neu hinzufügen");
			ButtonType buttonTypeAbort = new ButtonType("Abbrechen");

			alert.getButtonTypes().setAll(buttonTypeUpdate, buttonTypeNew, buttonTypeAbort);

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == buttonTypeNew) {
				operation = AddUpdateOperation.ADD;
			}
			if (result.get() == buttonTypeUpdate) {
				operation = AddUpdateOperation.UPDATE;
			}
			if (result.get() == buttonTypeAbort) {
				operation = AddUpdateOperation.ABORT;
			}
			
			
			
			
		}else {
			operation = AddUpdateOperation.ADD;
		}
		
		
		
		if(operation == AddUpdateOperation.ADD) {

			questionarr.put(questionobj);
			
		}
		
		if(operation == AddUpdateOperation.UPDATE) {
			
			//Umwandeln in Liste
			LinkedList<String> questionll= new LinkedList();
			for(Object questionobjo : questionarr) {
				String questionarr = questionobjo.toString();
				questionll.add(questionarr);
			}
			
			questionll.remove(orgindex);
			questionll.addLast(questionobj.toString());
			
			int fromindex = questionarr.length() - 1;
			
			System.out.println("[DEBUG] From index: " + fromindex);
			System.out.println("[DEBUG] To index: " + orgindex);
			
			
			//Verschieben in Liste (oldindex -> newindex)
			de.morihofi.simpleutils.ArrayListMove.move(questionll, orgindex, fromindex);
			
						
			//Zurück in JSONArray
			questionarr = new JSONArray();
			
			for(String questionobjo : questionll) {
				questionarr.put(new JSONObject(questionobjo));
				System.out.println("[DEBUG] Adding " + questionobjo);
			}

			
		}
		
		if(operation != AddUpdateOperation.ABORT) {
			// Felder leeren
			clearallfields();
		}
		

		redrawquestionlist();
	}
	
	enum AddUpdateOperation {
		 UPDATE,ADD, ABORT
		}

	private void clearallfields() {
		txtquestion.setText("");
		txtanswer_a.setText("");
		txtanswer_b.setText("");
		txtanswer_c.setText("");
		txtanswer_d.setText("");
	}

	public JSONArray questionarr = new JSONArray();

	@FXML
	void btndeleteclick(ActionEvent event) {
		clearallfields();
		try {
			int selectedindex = lstquestions.getSelectionModel().getSelectedIndex();
			questionarr.remove(selectedindex);
			lstquestions.getItems().remove(selectedindex);
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}

	@FXML
	void btneditclick(ActionEvent event) {

		try {
			int selectedindex = lstquestions.getSelectionModel().getSelectedIndex();
			// System.out.println("Selected Index: " + selectedindex);

			String selectedquestion = questionarr.get(selectedindex).toString();
			// System.out.println("Selected Question: " + selectedquestion);

			JSONObject questionobj = new JSONObject(selectedquestion);
			// System.out.println("Questionobj: " + questionobj);

			String question = questionobj.getString("question");
			String answer_a = questionobj.getString("answer_a");
			String answer_b = questionobj.getString("answer_b");
			String answer_c = questionobj.getString("answer_c");
			String answer_d = questionobj.getString("answer_d");
			String correctanswer = questionobj.getString("correctanswer");
			txtquestion.setText(question);
			txtanswer_a.setText(answer_a);
			txtanswer_b.setText(answer_b);
			txtanswer_c.setText(answer_c);
			txtanswer_d.setText(answer_d);

			if (correctanswer.equals("a")) {
				rbanswer_a.setSelected(true);
				rbanswer_b.setSelected(false);
				rbanswer_c.setSelected(false);
				rbanswer_d.setSelected(false);
			}
			if (correctanswer.equals("b")) {
				rbanswer_a.setSelected(false);
				rbanswer_b.setSelected(true);
				rbanswer_c.setSelected(false);
				rbanswer_d.setSelected(false);
			}
			if (correctanswer.equals("c")) {
				rbanswer_a.setSelected(false);
				rbanswer_b.setSelected(false);
				rbanswer_c.setSelected(true);
				rbanswer_d.setSelected(false);
			}
			if (correctanswer.equals("d")) {
				rbanswer_a.setSelected(false);
				rbanswer_b.setSelected(false);
				rbanswer_c.setSelected(false);
				rbanswer_d.setSelected(true);
			}

			apquestionedit.setDisable(false);
		} catch (Exception ex) {
			// ex.printStackTrace();
		}
	}

}
