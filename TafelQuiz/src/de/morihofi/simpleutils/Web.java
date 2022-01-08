package de.morihofi.simpleutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Web {
	public static String getTextFromURL(String address) {

		String response = null;

		try {
			URL url = new URL(address);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");

			con.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:90.0) Gecko/20100101 Firefox/90.0");

			con.connect();

			int status = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder content = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}

			response = content.toString();
			con.disconnect();

		} catch (IOException e) {
			// TODO Auto-generated catch block

			// e.printStackTrace();
		}

		return response;

	}
}
