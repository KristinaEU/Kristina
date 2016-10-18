package test;

import java.io.StringReader;
import java.util.ListIterator;

import javax.json.*;

public class JsonTest {
	public static void main(String[] args) {

		JsonReader jsonReader = Json
				.createReader(new StringReader(
						"{\"data\":{\"fusion\":{\"valence\":\"1\",\"arousal\":\"0.5\"},\"language-analysis\":\"...\",\"avatar-turn\":\"...\",\"mode-selection\":{\"verbal\":\"...\",\"nonverbal\":\"...\"},\"language-generation\":\"...\",\"dialog-management\":\"...\"},\"meta\":{\"scenario\":\"...\",\"language\":\"...\"},\"type\":\"turn\",\"uuid\":\"...\"}"));

		JsonObject j = jsonReader.readObject();
		String valence = j.getJsonObject("data").getJsonObject("fusion").getString("arousal");
		System.out.println(valence);
		System.out.println(j);
		JsonValue v = j.get("data");
		System.out.println(v);
	}
}
