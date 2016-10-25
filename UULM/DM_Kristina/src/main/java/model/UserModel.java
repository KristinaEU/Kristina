package model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
	
	private static Map<String, String> cultureMap = new HashMap<String, String>();
	
	public static void init(){
		cultureMap.put("elisabeth", Culture.GERMAN);
		cultureMap.put("hans", Culture.GERMAN);
		cultureMap.put("iwona", Culture.POLISH);
		cultureMap.put("maria", Culture.SPANISH);
		cultureMap.put("juan", Culture.SPANISH);
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
