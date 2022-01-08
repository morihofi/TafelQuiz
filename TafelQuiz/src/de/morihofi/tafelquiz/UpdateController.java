package de.morihofi.tafelquiz;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;

public class UpdateController extends Application implements Initializable {

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {

		Task dltask = new Task<Void>() {
			@Override
			public Void call() {

				Platform.runLater(() -> lblstatus.setText("Verbindung zu Update Server wird hergestellt..."));

				if (netIsAvailable()) {

					Platform.runLater(() -> lblstatus.setText("Downloading update ..."));

					try {
						String zipfilepath = Main.startupdir() + File.separator + "package.zip";
						URL url = new URL("https://data.morihofi.de/tafelquiz/update/update.zip");
						HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
						httpConnection.addRequestProperty("User-Agent",
								"Mozilla/5.0 TafelQuizUpdate/" + Main.AppVer + Main.AppVerRelease);
						long completeFileSize = httpConnection.getContentLength();

						java.io.BufferedInputStream in = new java.io.BufferedInputStream(
								httpConnection.getInputStream());
						java.io.FileOutputStream fos = new java.io.FileOutputStream(zipfilepath);
						java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
						byte[] data = new byte[1024];
						long downloadedFileSize = 0;
						int x = 0;
						while ((x = in.read(data, 0, 1024)) >= 0) {
							downloadedFileSize += x;

							// calculate progress
							final Double currentProgress = (Double) ((((double) downloadedFileSize)
									/ ((double) completeFileSize)) * 100000d) / 1000;

							// final int currentProgress = (int) ((downloadedFileSize / completeFileSize) *
							// 100);

							Platform.runLater(() -> lblstatus.setText(
									"TafelQuiz wird jetzt aktualisiert. Bitte warten Sie einen Augenblick! (Schritt 1/3)"));

							Double pbprogressperc = (double) (currentProgress / 100);

							Platform.runLater(() -> pbprogress.setProgress(pbprogressperc));

							bout.write(data, 0, x);
						}
						bout.close();
						in.close();

						// Update complete
						Platform.runLater(() -> {
							lblstatus.setText(
									"TafelQuiz wird jetzt aktualisiert. Bitte warten Sie einen Augenblick! (Schritt 2/3)");
							pbprogress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
						});

						String fileZip = zipfilepath;
						File destDir = new File(Main.startupdir() + File.separator + "dti");
						byte[] buffer = new byte[1024];
						ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
						ZipEntry zipEntry = zis.getNextEntry();

						while (zipEntry != null) {
							File newFile = newFile(destDir, zipEntry);
							if (zipEntry.isDirectory()) {
								if (!newFile.isDirectory() && !newFile.mkdirs()) {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Verzeichnis konnte nicht erstellt werden");
									alert.setHeaderText("Verzeichnis konnte nicht erstellt werden");
									alert.setContentText("Verzeichnis konnte nicht erstellt werden: " + newFile);
									alert.showAndWait();
								}
							} else {
								// fix for Windows-created archives
								File parent = newFile.getParentFile();
								if (!parent.isDirectory() && !parent.mkdirs()) {
									Alert alert = new Alert(AlertType.ERROR);
									alert.setTitle("Verzeichnis konnte nicht erstellt werden");
									alert.setHeaderText("Verzeichnis konnte nicht erstellt werden");
									alert.setContentText("Verzeichnis konnte nicht erstellt werden: " + parent);
									alert.showAndWait();

								}

								// write file content
								FileOutputStream fileos = new FileOutputStream(newFile);
								int len;
								while ((len = zis.read(buffer)) > 0) {
									fileos.write(buffer, 0, len);
								}
								fileos.close();
							}
							zipEntry = zis.getNextEntry();
						}
						zis.closeEntry();
						zis.close();

						// Update complete

						Platform.runLater(() -> {
							lblstatus.setText(
									"TafelQuiz wird jetzt aktualisiert. Bitte warten Sie einen Augenblick! (Schritt 3/3)");
							pbprogress.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
						});
						File deletefile = new File(zipfilepath);
						deletefile.delete();
						// Clean up complete

						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("TafelQuiz Update");
						alert.setHeaderText("TafelQuiz wurde erfolreich aktualisiert");
						alert.setContentText(
								"TafelQuiz wurde erfolreich aktualisiert. Bitte starten Sie TafelQuiz erneut, um das Update abzuschließen.");
						alert.showAndWait();

						canexit = true;
						exitapp();

					} catch (Exception e) {
						Platform.runLater(() -> {
							Alert alert = new Alert(AlertType.ERROR);
							alert.setTitle("Ein Fehler ist aufgetreten");
							alert.setHeaderText("Ein Fehler ist aufgetreten");
							alert.setContentText(e.getMessage());
							alert.showAndWait();

							canexit = true;
							exitapp();
						});
					}

				} else {
					Platform.runLater(() -> {
						Alert alert = new Alert(AlertType.ERROR);
						alert.setTitle("Keine Verbindung");
						alert.setHeaderText("Keine Verbindung");
						alert.setContentText("Es konnte keine Verbindung zum Update-Server hergestellt werden.");
						alert.showAndWait();

						canexit = true;
						exitapp();

					});
				}

				return null;

			};

		};

		Thread dlthread = new Thread(dltask);
		dlthread.start();

	}

	public static void startup(String[] args) {
		launch(args);
	}

	public Scene scene = null;
	public Boolean canexit = false;
	JMetro jMetro = null;

	public void exitapp() {
		System.out.println("TafelQuiz wird beendet...");
		Platform.exit();
		System.exit(0);
	}

	private static boolean netIsAvailable() {
		try {
			String resp = getHostURL("https://data.morihofi.de/tafelquiz");
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	static String getHostURL(String uri) {
		URL url;
		try {
			url = new URL(uri);
			String protocol = url.getProtocol();
			String authority = url.getAuthority();
			return String.format("%s://%s", protocol, authority);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/de/morihofi/tafelquiz/Update.fxml"));
		Parent root = loader.load();
		scene = new Scene(root);

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent e) {
				if (canexit) {
					exitapp();
				} else {
					e.consume();
				}
			}
		});

		jMetro = new JMetro(Main.jMetroStyle);

		jMetro.setScene(scene);
		stage.getIcons().add(new Image("/de/morihofi/tafelquiz/images/AppTafelQuizUpdate.png"));
		stage.setTitle("TafelQuiz Update");
		stage.setResizable(true);
		stage.setScene(scene);
		stage.show();

	}

	@FXML
	private ProgressBar pbprogress;

	@FXML
	private Label lblstatus;

	@FXML
	private Label lblprogress;

}
