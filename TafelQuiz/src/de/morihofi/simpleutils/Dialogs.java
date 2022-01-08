package de.morihofi.simpleutils;

import java.io.File;

import javax.swing.JFileChooser;

import javafx.stage.FileChooser;

public class Dialogs {

	public static String openFileDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		int result = fileChooser.showOpenDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			//File selectedFile = fileChooser.getSelectedFile();
			// System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			return fileChooser.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}
	
	public static String saveFileDialog() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	
		int result = fileChooser.showSaveDialog(null);
		if (result == JFileChooser.APPROVE_OPTION) {
			//File selectedFile = fileChooser.getSelectedFile();
			// System.out.println("Selected file: " + selectedFile.getAbsolutePath());
			return fileChooser.getSelectedFile().getAbsolutePath();
		} else {
			return null;
		}
	}
	


}
