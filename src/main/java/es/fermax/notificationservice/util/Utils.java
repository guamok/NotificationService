package es.fermax.notificationservice.util;

public class Utils {
	private Utils(){}	
	
	public static String sanitizeInputString(String input){
		if (input != null) {
			return input.replaceAll("[^a-zA-Z0-9_-]", "");
		}
		else return null;
	}

}
