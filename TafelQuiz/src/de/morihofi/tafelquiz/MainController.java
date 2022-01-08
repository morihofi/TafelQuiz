package de.morihofi.tafelquiz;

import java.io.BufferedWriter;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Stream;

import org.controlsfx.control.StatusBar;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.application.Platform;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainController extends Application implements Initializable {
	/*
	 * Funtktion zum laden einer Datei in einen String
	 */
	public static String readLineByLineJava8(String filePath) {
		StringBuilder contentBuilder = new StringBuilder();

		try (Stream<String> stream = Files.lines(Paths.get(filePath), StandardCharsets.UTF_8)) {
			stream.forEach(s -> contentBuilder.append(s).append("\n"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return contentBuilder.toString();
	}

	public static void saveStringToFile(String filepath, String content) throws IOException {
		Path file = Paths.get(filepath);
		try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
			writer.write(content);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		apquestion.setVisible(false);
		apsummary.setVisible(false);
		aponlinedir.setVisible(false);
		btnnextquestion.setDisable(true);

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Lizenzhinweis");
		alert.setHeaderText("Lizenzhinweis");
		alert.setContentText("Dieses Programm ist Freie Software: Sie können es unter den Bedingungen\n"
				+ "der GNU General Public License, wie von der Free Software Foundation,\n"
				+ "Version 3 der Lizenz oder (nach Ihrer Wahl) jeder neueren\n"
				+ "veröffentlichten Version, weiter verteilen und/oder modifizieren.\n" + "\n"
				+ "Dieses Programm wird in der Hoffnung bereitgestellt, dass es nützlich sein wird, jedoch\n"
				+ "OHNE JEDE GEWÄHR, sogar ohne die implizite\n"
				+ "Gewähr der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.\n"
				+ "Siehe die GNU General Public License für weitere Einzelheiten.\n" + "\n"
				+ "Sie sollten eine Kopie der GNU General Public License zusammen mit diesem\n"
				+ "Programm erhalten haben. Wenn nicht, siehe <https://www.gnu.org/licenses/>.");

		ButtonType buttonTypeYes = new ButtonType("Verstanden");

		alert.getButtonTypes().setAll(buttonTypeYes);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeYes) {

		}

		Task inittask = new Task<Void>() {
			@Override
			public Void call() {
				String settingsfilepath = de.morihofi.simpleutils.PathUtils.getappdirectory() + File.separator
						+ "config.json";
				File configfile = new File(settingsfilepath);
				if (configfile.exists()) {

					
					Platform.runLater(() -> {
						sbbottom.setText("Konfiguration wird eingelesen...");
					});
					
					try {
						String settingsjson = readLineByLineJava8(settingsfilepath);

						JSONObject obj = new JSONObject(settingsjson);

						Main.tq_masterserver = obj.getString("masterserver");
						Main.tq_onlinedirservice = obj.getString("onlinedirservice");

						Main.tq_machinename = de.morihofi.simpleutils.Network.getHostname();

					} catch (Exception ex) {
						Platform.runLater(() -> {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Initialisierungsfehler");
							alert.setHeaderText("Verarbeitungsfehler");
							alert.setContentText("Fehler beim Verarbeiten der Konfigurationsdatei \"config.json\":\n"
									+ ex.getLocalizedMessage() + " TafelQuiz wird beendet!");

							alert.showAndWait();
							System.exit(1);
						});
					}
					
					Platform.runLater(() -> {
						sbbottom.setText("Bereit. Wählen Sie eine der Optionen um fortzufahren!");
					});
					
					
				} else {

					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Initialisierungsfehler");
						alert.setHeaderText("Datei nicht gefunden");
						alert.setContentText(
								"Die Konfigurationsdatei \"config.json\" wurde nicht gefunden. TafelQuiz wird beendet!");

						alert.showAndWait();
						System.exit(1);
					});
				}

				return null;
			}
		};

		Thread initthread = new Thread(inittask);
		initthread.start();

	}

	public static void startup(String[] args) {
		launch(args);
	}

	public Scene scene = null;

	JMetro jMetro = null;

	public void exitapp() {
		System.out.println("TafelQuiz wird beendet...");
		Platform.exit();
		System.exit(0);
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/morihofi/tafelquiz/Main.fxml"));
		Parent root = loader.load();
		scene = new Scene(root);

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {

				exitapp();
			}
		});

		jMetro = new JMetro(Main.jMetroStyle);

		jMetro.setScene(scene);
		stage.getIcons().add(new Image("/de/morihofi/tafelquiz/images/AppTafelQuiz.png"));
		stage.setTitle("TafelQuiz - Version " + Main.AppVer + " (" + Main.AppVerRelease + ")");
		stage.setResizable(true);
		stage.setScene(scene);
		stage.show();

	}

	@FXML
	private StatusBar sbbottom;

	@FXML
	private Button btnquizrun;

	@FXML
	private AnchorPane apquestion;

	@FXML
	private Label lblcurrentquestion;

	@FXML
	private Label lblcurrentanswera;

	@FXML
	private Label lblcurrentanswerb;

	@FXML
	private Label lblcurrentanswerc;

	@FXML
	private Label lblcurrentanswerd;

	@FXML
	private Button btnnextquestion;

	@FXML
	private Label lblping;

	@FXML
	private Button btnevaluation;

	@FXML
	private BarChart<String, Number> bcsummary;

	@FXML
	private CategoryAxis bcxaxis;

	@FXML
	private NumberAxis bcyaxis;

	@FXML
	private AnchorPane apsummary;

	@FXML
	private Label lblpeoplevoted;

	@FXML
	private MenuItem menfileexit;

	@FXML
	private Button btncreatequiz;

	@FXML
	private Spinner<Integer> spautonextwhenvoted;

	@FXML
	private CheckBox cbautonextwhenvoted;

	@FXML
	private MenuItem menloadquiz;

	@FXML
	private MenuItem mencreatequiz;

	@FXML
	private AnchorPane aponlinedir;

	
	@FXML
	private Button btnonlinedir;

	@FXML
	private ListView<String> lvonlinedir;

	@FXML
	private MenuItem menonlinedirquizload;

	@FXML
	private MenuItem menonlinedirquizsave;

	@FXML
	void menonlinedirquizloadclick(ActionEvent event) {
		ResponseBody body = null;
		try {
			
			String selecteditemstr = lvonlinedir.getSelectionModel().getSelectedItem().toString();
			
			
			OkHttpClient httpClient = new OkHttpClient();
			Request request = new Request.Builder().url(Main.tq_onlinedirservice + "?operation=get&name=" + selecteditemstr)
					.addHeader("User-Agent", "Mozilla/5.0 TafelQuiz/" + Main.AppVer + Main.AppVerRelease).build();
			try (Response response = httpClient.newCall(request).execute()) {

				if (!response.isSuccessful())
					throw new IOException("Unexpected code " + response);

				
				
				quizarr = new JSONArray(response.body().string());
				
			
				
				
				quizinit();
			}

		} catch (IOException ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Kommunikationsfehler");
			alert.setHeaderText("Kommunikationsfehler");
			alert.setContentText(
					"Quiz konnte nicht aus dem Online-Verzeichnis geladen werden: " + ex.getMessage());
			alert.showAndWait();


		} finally {

			if (body != null) {
				body.close();
			}
		}
		
		
		
		
	}

	@FXML
	void menonlinedirquizsaveclick(ActionEvent event) {

		ResponseBody body = null;
		try {
			
			String selecteditemstr = lvonlinedir.getSelectionModel().getSelectedItem().toString();
			
			
			OkHttpClient httpClient = new OkHttpClient();
			Request request = new Request.Builder().url(Main.tq_onlinedirservice + "?operation=get&name=" + selecteditemstr)
					.addHeader("User-Agent", "Mozilla/5.0 TafelQuiz/" + Main.AppVer + Main.AppVerRelease).build();
			try (Response response = httpClient.newCall(request).execute()) {

				if (!response.isSuccessful())
					throw new IOException("Unexpected code " + response);

				
				
				String savetext = response.body().string();
				
			
				Stage stage = (Stage) btnonlinedir.getScene().getWindow();

				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Quiz speichern");
				fileChooser.getExtensionFilters()
						.add(new FileChooser.ExtensionFilter("TafelQuiz Quiz-Datei (*.tquiz)", "*.tquiz"));
				fileChooser.setInitialFileName(selecteditemstr + ".tquiz");
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
						MainController.saveStringToFile(fileloc, savetext);

						Alert alert2 = new Alert(AlertType.INFORMATION);
						alert2.setTitle("Gespeichert!");
						alert2.setHeaderText("Gespeichert!");
						alert2.setContentText("Das Quiz wurde erfolgreich heruntergeladen!");

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

		} catch (IOException ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Kommunikationsfehler");
			alert.setHeaderText("Kommunikationsfehler");
			alert.setContentText(
					"Quiz konnte nicht aus dem Online-Verzeichnis geladen werden: " + ex.getMessage());
			alert.showAndWait();


		} finally {

			if (body != null) {
				body.close();
			}
		}
		
		
		
		
	}

	@FXML
	void btnonlinedirclick(ActionEvent event) {
		sbbottom.setText("Online-Verzeichnis wird heruntergeladen...");
		lvonlinedir.getItems().clear();
		aponlinedir.setVisible(false);
		
		btnonlinedir.setDisable(true);
	
		Task loadtask = new Task<Void>() {
			@Override
			public Void call() {
				
				

				ResponseBody body = null;
				try {
					OkHttpClient httpClient = new OkHttpClient();
					Request request = new Request.Builder().url(Main.tq_onlinedirservice + "?operation=list")
							.addHeader("User-Agent", "Mozilla/5.0 TafelQuiz/" + Main.AppVer + Main.AppVerRelease).build();
					try (Response response = httpClient.newCall(request).execute()) {

						if (!response.isSuccessful())
							throw new IOException("Unexpected code " + response);

						// Get response headers
						/*
						 * Headers responseHeaders = response.headers(); for (int i = 0; i <
						 * responseHeaders.size(); i++) { System.out.println(responseHeaders.name(i) +
						 * ": " + responseHeaders.value(i)); }
						 */
						// Get response body
						// System.out.println(response.body().string());

						JSONArray jsonarr = new JSONArray(response.body().string());
						// System.out.println(jsonarr);
						for (Object jsonobjstr : jsonarr) {

							JSONObject jsonobj = new JSONObject(jsonobjstr.toString());

							// System.out.println(jsonobj);
							Platform.runLater(() -> {
								lvonlinedir.getItems().add(jsonobj.getString("name"));
							});
							
						}

					}
					Platform.runLater(() -> {
						aponlinedir.setVisible(true);
						sbbottom.setText("Online-Verzeichnis wurde erfolgreich heruntergeladen.");
					});
					
				} catch (Exception ex) {
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Kommunikationsfehler");
						alert.setHeaderText("Kommunikationsfehler");
						alert.setContentText(
								"Die Liste aus dem Online-Verzeichnis konnte nicht geladen werden: " + ex.getMessage());
						alert.showAndWait();
						sbbottom.setText("Fehler beim herunterladen des Online-Verzeichnises.");
						aponlinedir.setVisible(false);
					});
					
					

				} 
				
				if (body != null) {
					body.close();
				}
				
				Platform.runLater(() -> {
					btnonlinedir.setDisable(false);
				});
				
				return null;
			}
		};

		Thread loadthread = new Thread(loadtask);
		loadthread.start();
		
		
		

	}

	void openeditor() {

		Stage stage = (Stage) btncreatequiz.getScene().getWindow();
		stage.hide();

		System.out.println("TafelQuiz Editor wird geladen...");

		Platform.runLater(() -> {

			try {
				FXMLLoader fxmlLoader0 = new FXMLLoader(
						getClass().getResource("/de/morihofi/tafelquiz/QuizEditor.fxml"));
				Parent root1 = (Parent) fxmlLoader0.load();
				Stage stage1 = new Stage();
				Scene scene1 = new Scene(root1);

				JMetro jMetro1 = new JMetro(Main.jMetroStyle);
				jMetro1.setScene(scene1);

				stage1.setScene(scene1);
				// stage1.setOnCloseRequest(e -> e.consume());
				stage1.getIcons().add(new Image("/de/morihofi/tafelquiz/images/AppTafelQuizEditor.png"));
				stage1.setTitle("TafelQuiz Editor");
				stage1.initStyle(StageStyle.DECORATED);
				stage1.initModality(Modality.NONE);
				stage1.show();

			} catch (Exception e) {
				e.printStackTrace();

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Fenster konnte nicht erzeugt werden");
				alert.setHeaderText("Fenster konnte nicht erzeugt werden");
				alert.setContentText(e.getMessage() + "\n" + e.getStackTrace().toString());
				alert.showAndWait();
			}

		});
	}

	@FXML
	void btncreatequizclick(ActionEvent event) {
		openeditor();

	}

	@FXML
	void mencreatequizclick(ActionEvent event) {
		btncreatequiz.fire();
	}

	@FXML
	void menloadquizclick(ActionEvent event) {
		btnquizrun.fire();
	}

	@FXML
	void menfileexitclick(ActionEvent event) {
		exitapp();
	}

	@FXML
	void btnevaluationclick(ActionEvent event) {

		if (!mWebSocketClient.isClosed()) {
			btnevaluation.setDisable(true);
			btnnextquestion.setDisable(true);
			apquestion.setDisable(true);

			sbbottom.setText("Punktzahlen werden übermittelt...");

			JSONObject sendobj = new JSONObject();

			apquestion.setVisible(false);
			apsummary.setVisible(true);

			sendobj.put("session", quizsession);
			sendobj.put("msgtype", "quizend");
			sendobj.put("allquestions", currentQuestion + 1);
			mWebSocketClient.send(sendobj.toString());

		}

	}

	@FXML
	void btnquizrunclick(ActionEvent event) {

		try {
			Stage stage = (Stage) btnquizrun.getScene().getWindow();
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Quiz laden");
			fileChooser.getExtensionFilters()
					.add(new FileChooser.ExtensionFilter("TafelQuiz Quiz-Datei (*.tquiz)", "*.tquiz"));

			File file = fileChooser.showOpenDialog(stage);

			if (file != null) {
				String quizfilename = file.getAbsolutePath();
				sbbottom.setText("Quiz wird geladen...");
				System.out.println("Geladenes Quiz: " + quizfilename);

				String quizjson = readLineByLineJava8(quizfilename);

				quizarr = new JSONArray(quizjson);
				
				quizinit();
			}

		} catch (Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Verarbeitungsfehler");
			alert.setHeaderText("Verarbeitungsfehler");
			alert.setContentText(ex.getMessage());

			alert.showAndWait();
		}

	}
	
	void quizinit() {
		btnquizrun.setVisible(false);
		btnonlinedir.setVisible(false);
		btncreatequiz.setVisible(false);
		menloadquiz.setVisible(false);
		mencreatequiz.setVisible(false);

		sbbottom.setText("Sitzung wird vorbereitet...");
		quizsession = de.morihofi.simpleutils.Hash.MD5_HashString(UUID.randomUUID().toString()).substring(0,
				20);

		sbbottom.setText("Verbindung zum Master Server wird hergestellt...");
		connecttowebsocket(Main.tq_masterserver);

		// Weiter bei wenn verbunden
	}

	public int currentQuestion = 0;
	public JSONArray quizarr = null;
	public static String quizsession = "";
	public String quizping_send = "";
	public String quizping_recv = "";
	public Boolean quizping_ready = true;
	public Long quizping_time = Long.valueOf(0);
	public int peoplevoted = 0;

	public void ShowQuestion(int index) {

		btnevaluation.setDisable(false);

		currentQuestion = index;

		peoplevoted = 0;
		Platform.runLater(() -> {
			lblpeoplevoted.setText("Bisher haben " + peoplevoted + " Personen abgestimmt");
		});

		String json = quizarr.get(index).toString();

		JSONObject obj = new JSONObject(json);

		String question = obj.getString("question");
		String answer_a = obj.getString("answer_a");
		String answer_b = obj.getString("answer_b");
		String answer_c = obj.getString("answer_c");
		String answer_d = obj.getString("answer_d");
		String correctanswer = obj.getString("correctanswer");

		if (mWebSocketClient.isClosed() == false) {

			JSONObject sendobj = new JSONObject();

			sendobj.put("session", quizsession);
			sendobj.put("question", question);
			sendobj.put("answer_a", answer_a);
			sendobj.put("answer_b", answer_b);
			sendobj.put("answer_c", answer_c);
			sendobj.put("answer_d", answer_d);
			sendobj.put("correctanswer", correctanswer);
			sendobj.put("msgtype", "question");
			mWebSocketClient.send(sendobj.toString());
		}

		Platform.runLater(() -> {
			lblcurrentquestion.setText(question);
			lblcurrentanswera.setText("Antwort A: " + answer_a);
			lblcurrentanswerb.setText("Antwort B: " + answer_b);
			lblcurrentanswerc.setText("Antwort C: " + answer_c);
			lblcurrentanswerd.setText("Antwort D: " + answer_d);

			if (quizarr.length() == (currentQuestion + 1)) {
				btnnextquestion.setDisable(true);
			}
			if (currentQuestion == 0) {
				btnnextquestion.setDisable(false);
			}
		});

	}

	@FXML
	void btnnextquestionclick(ActionEvent event) {

		ShowQuestion(currentQuestion + 1);
	}

	public void startquiz() {
		
		
		
		apquestion.setVisible(true);
		apsummary.setVisible(false);
		aponlinedir.setVisible(false);
		
		
		btnnextquestion.setDisable(false);

		

		ShowQuestion(0);

		Task conninfotask = new Task<Void>() {
			@Override
			public Void call() throws InterruptedException {

				while (mWebSocketClient.isOpen()) {

					if (quizping_ready) {
						quizping_send = UUID.randomUUID().toString();
						quizping_time = System.currentTimeMillis();
						quizping_ready = false;

						JSONObject pingobj = new JSONObject();

						pingobj.put("msgtype", "ping");
						pingobj.put("ping", quizping_send);
						pingobj.put("session", quizsession);

						mWebSocketClient.send(pingobj.toString());
					}

					Thread.sleep(500);
				}

				return null;
			}
		};

		Thread conninfothread = new Thread(conninfotask);
		conninfothread.start();
	}

	public static WebSocketClient mWebSocketClient;

	public void connecttowebsocket(String furi) {
		URI uri;
		try {
			uri = new URI(furi);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		mWebSocketClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {

				System.out.println("[WEBSOCKET] [CONNECTION] Connected to " + uri);

				Platform.runLater(() -> {
					sbbottom.setText("Verbindung zum Master Server hergestellt!");

					try {
						FXMLLoader fxmlLoader0 = new FXMLLoader(
								getClass().getResource("/de/morihofi/tafelquiz/GameStartScreen.fxml"));
						Parent root1 = (Parent) fxmlLoader0.load();
						Stage stage = new Stage();
						stage.setScene(new Scene(root1));
						stage.setOnCloseRequest(e -> e.consume());
						stage.initStyle(StageStyle.UTILITY);
						stage.initModality(Modality.APPLICATION_MODAL);
						stage.show();

					} catch (Exception e) {
						e.printStackTrace();

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Fenster konnte nicht erzeugt werden");
						alert.setHeaderText("Fenster konnte nicht erzeugt werden");
						alert.setContentText(e.getMessage() + "\n" + e.getStackTrace().toString());
						alert.showAndWait();
					}

				});

			}

			@Override
			public void onMessage(String s) {
				final String message = s;

				System.out.println("[WEBSOCKET] [MESSAGE RECIVED] " + message);

				// JSON Parsen
				String jsonString = message;
				JSONObject obj = new JSONObject(jsonString);

				if (obj.getString("session").equals(quizsession)) {

					// System.out.println(jsonString);

					if (obj.getString("msgtype").equals("ping")) {

						quizping_recv = obj.getString("ping");

						if (quizping_send.equals(quizping_recv)) {

							Long ping = System.currentTimeMillis() - quizping_time;

							Platform.runLater(() -> {
								lblping.setText("Ping: " + ping + "ms; Sitzungs-ID: " + quizsession);
							});

							quizping_ready = true;

						}

					}

					if (obj.getString("msgtype").equals("quizstart")) {

						startquiz();

					}

					if (obj.getString("msgtype").equals("voted")) {
						peoplevoted++;
						Platform.runLater(() -> {
							lblpeoplevoted.setText("Bisher haben " + peoplevoted + " Personen abgestimmt");
						});

						if (cbautonextwhenvoted.isSelected()) {

							if (spautonextwhenvoted.getValue() == peoplevoted) {

								Platform.runLater(() -> {

									if (quizarr.length() == (currentQuestion + 1)) {
										btnevaluation.fire();
									} else {
										btnnextquestion.fire();
									}

								});

							}

						}

					}

					if (obj.getString("msgtype").equals("quizsummary")) {

						int points = obj.getInt("points");
						String username = obj.getString("user");

						Platform.runLater(() -> {

							// Chart
							XYChart.Series series1 = new XYChart.Series();
							series1.setName(username);
							series1.getData().add(new XYChart.Data(username, points));
							bcsummary.getData().addAll(series1);

						});

					}

				}
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				System.out.println("[WEBSOCKET] [CONNECTION] Connection closed");

				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Verbindungsfehler");
					alert.setHeaderText("Verbindungsfehler");
					alert.setContentText("Die Verbindung zum Master-Server wurde getrennt!");

					alert.showAndWait();

				});
			}

			@Override
			public void onError(Exception e) {
				System.out.println("[WEBSOCKET] [Error] " + e.getMessage());

				Platform.runLater(() -> {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Verbindungsfehler");
					alert.setHeaderText("Verbindungsfehler");
					alert.setContentText("Es gab ein Verbindungsproblem: " + e.getMessage());

					alert.showAndWait();

				});

			}

		};
		mWebSocketClient.connect();

	}

}
