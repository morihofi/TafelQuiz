package de.morihofi.simpleutils;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class Clipboard {

	public void SetoClipboardString(String texttocopy) {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();
		StringSelection strSel = new StringSelection(texttocopy);
		clipboard.setContents(strSel, null);
	}
	
	public String GetClipboardString() {
		
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();
		String result;
		try {
			result = (String) clipboard.getData(DataFlavor.stringFlavor);
			return result;
		} catch (UnsupportedFlavorException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}
		
		
		
	}
	
}
