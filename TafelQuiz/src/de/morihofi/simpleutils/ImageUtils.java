package de.morihofi.simpleutils;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import sun.misc.BASE64Encoder;

public class ImageUtils {

	public static Image takeScreenshotOfAllScreens() throws Exception {
		// Okay so all we have to do here is find the screen with the lowest x,
        // the screen with the lowest y, the screen with the higtest value of X+
        // width and the screen with the highest value of Y+height
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] screens = ge.getScreenDevices();

        Rectangle allScreenBounds = new Rectangle();
        int farx = 0;
        int fary = 0;
        for (GraphicsDevice screen : screens) {
            Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
            //finding the one corner
            if (allScreenBounds.x > screenBounds.x) {
                allScreenBounds.x = screenBounds.x;
            }
            if (allScreenBounds.y > screenBounds.y) {
                allScreenBounds.y = screenBounds.y;
            }
            //finding the other corner
            if (farx < (screenBounds.x + screenBounds.width)) {
                farx = screenBounds.x + screenBounds.width;
            }
            if (fary < (screenBounds.y + screenBounds.height)) {
                fary = screenBounds.y + screenBounds.height;
            }
            allScreenBounds.width = farx - allScreenBounds.x;
            allScreenBounds.height = fary - allScreenBounds.y;
        }
        Robot robot = new Robot();
        Image screenShot = robot.createScreenCapture(allScreenBounds);
       // ImageIO.write((RenderedImage) screenShot, "jpg", new File("C:\\tmp\\test.jpg"));
        
        return screenShot;
	}
	
	public static String encodeImageToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            BASE64Encoder encoder = new BASE64Encoder();
            imageString = encoder.encode(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }
	

	
	
}
