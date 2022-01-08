package de.morihofi.simpleutils;

public class Text {
	public static String Base64encode(String input) {

		String encodedString = java.util.Base64.getEncoder().encodeToString(input.getBytes());

		return encodedString;
	}
	
	public static String Base64decode(String input) {

		byte[] decodedBytes = java.util.Base64.getDecoder().decode(input);
		String decodedString = new String(decodedBytes);
		
		return decodedString;
	}
	
	public static String cyclicLeftShift(String s, int n){ //'n' is the number of characters to shift left
        n = n%s.length();
        return s.substring(n) + s.substring(0, n);
    }
      
	public static String cyclicRightShift(String s, int n){ //'n' is the number of characters to shift right
        n = n%s.length();
        return  s.substring(s.length() - n , s.length()) + s.substring(0, s.length() - n);
    }
	
}
