package de.morihofi.simpleutils;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class Network {
	public static String WhatIsMyIP() {
		String ip = "Unknown";
		BufferedReader in;
		try {
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			ip = in.readLine(); // you get the IP as a String
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return ip;
	}

	public static void OpenBrowser(String url) {

		Desktop desktop = java.awt.Desktop.getDesktop();
		try {
			// specify the protocol along with the URL
			URI oURL = new URI(url);
			desktop.browse(oURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getHostname() {
		String host = System.getenv("COMPUTERNAME");
		if (host != null)
			return host;
		host = System.getenv("HOSTNAME");
		if (host != null)
			return host;

		// undetermined.
		return null;
	}

}
