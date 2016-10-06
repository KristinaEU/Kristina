package model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
	
	private static Map<String, String> cultureMap = new HashMap<String, String>();
	
	public static void init(){
		cultureMap.put("Elisabeth", Culture.GERMAN);
		cultureMap.put("Hans", Culture.GERMAN);
		cultureMap.put("Iwona", Culture.POLISH);
		cultureMap.put("Maria", Culture.SPANISH);
		cultureMap.put("Juan", Culture.SPANISH);
	}
	
	public static boolean isVerbose(String user){
		String culture = cultureMap.get(user);
		return culture.equals(Culture.POLISH);
	}
	
	public static boolean isConcise(String user){
		String culture = cultureMap.get(user);
		return culture.equals(Culture.GERMAN);
	}
	
	public static boolean isIndirect(String user){
		String culture = cultureMap.get(user);
		return culture.equals(Culture.POLISH);
	}
	
	public static boolean isDirect(String user){
		String culture = cultureMap.get(user);
		return culture.equals(Culture.GERMAN);
	}

}
