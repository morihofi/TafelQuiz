package de.morihofi.tafelquiz;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class Main{
	public static String AppVer = "0.7";
	public static String AppVerRelease = "beta";
	public static String tq_masterserver = ""; //Aus Konfig in Maincontroller
	public static String tq_onlinedirservice = "";
	public static String tq_machinename = ""; //Automatisch aus Netzwerkhostname
	
	
	public static Style jMetroStyle = Style.LIGHT;
	

	
	public static void main(String[] args) {
		System.out.println(
				  " _____     ___     _ _____     _     \n"
				+ "|_   _|___|  _|___| |     |_ _|_|___ \n"
				+ "  | | | .'|  _| -_| |  |  | | | |- _|\n"
				+ "  |_| |__,|_| |___|_|__  _|___|_|___|\n"
				+ "                       |__|          ");
		System.out.println("Copyright © 2015-2021 Moritz Hofmann");
		System.out.println("Copyright © 2018-2021 morihofi.de");
		System.out.println("TafelQuiz V." + AppVer + " ("+ AppVerRelease +") wird gestartet...");
		
		for(String arg : args) {
			if(arg.startsWith("--gui-dark-mode")) {
				jMetroStyle = Style.DARK;
			}
			if(arg.startsWith("--gui-light-mode")) {
				jMetroStyle = Style.LIGHT;
			}
			
		}
		
		System.out.print("Suche nach Updates...");
		Boolean isupdateavailiable = true;
		
		if(isupdateavailiable) {
			System.out.println(" Update verfügbar");
			UpdateController.startup(args);
			
		}else {
			System.out.println(" kein Update verfügbar");
			MainController.startup(args);
		}
		
		
		
	}
	
	
	public static String startupdir() {

		return System.getProperty("user.dir");

	}
	


	



}
