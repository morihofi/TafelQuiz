package de.morihofi.tafelquiz;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.json.JSONObject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import net.glxn.qrgen.javase.QRCode;

public class GameStartScreen implements Initializable{
	String uri;
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		try {
			
			URL aURL = new URL(Main.tq_masterserver.replace("ws://", "http://").replace("wss://", "https://"));
			
			String protocol = "http";
			String host = aURL.getHost();
			int port = aURL.getPort();
			
			
			uri = protocol + "://" + host + ":" + port + "/?sid=" + MainController.quizsession;
			
			Image qrcode = ConvertBufferedImageToImage(generateQRCodeImage(uri));
			
			imgqrcode.setImage(qrcode);
			
			lnksite.setText(uri);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static BufferedImage generateQRCodeImage(String barcodeText) throws Exception {
	    ByteArrayOutputStream stream = QRCode
	      .from(barcodeText)
	      .withSize(200, 200)
	      .stream();
	    ByteArrayInputStream bis = new ByteArrayInputStream(stream.toByteArray());

	    return ImageIO.read(bis);
	}
	
	public static Image ConvertBufferedImageToImage(BufferedImage bimage) {
		Image returnimage = null;
		BufferedImage bufferedImage = bimage;
		ImageIcon imageIcon = new ImageIcon(bufferedImage);

		java.awt.Image img1 = imageIcon.getImage();
		returnimage = javafx.embed.swing.SwingFXUtils.toFXImage(bufferedImage, null);
		return returnimage;
	}
	
    @FXML
    private ImageView imgqrcode;

    @FXML
    private Button btnquizstart;
    
    @FXML
    private Hyperlink lnksite;

    @FXML
    void lnksiteclick(ActionEvent event) {
    	de.morihofi.simpleutils.Network.OpenBrowser(uri);
    }
    
    @FXML
    void btnquizstartclick(ActionEvent event) {
    	
    	if(MainController.mWebSocketClient.isOpen()) {
    		
    		JSONObject obj = new JSONObject();
    		obj.put("session", MainController.quizsession);
    		obj.put("msgtype", "quizstart");
    		MainController.mWebSocketClient.send(obj.toString());
    		
    	}
    	Stage stage = (Stage) btnquizstart.getScene().getWindow();
		// do what you have to do
		stage.close();
    }
}
