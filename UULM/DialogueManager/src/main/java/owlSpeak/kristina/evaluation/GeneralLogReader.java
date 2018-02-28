package owlSpeak.kristina.evaluation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import javax.json.JsonArray;
import javax.json.JsonException;

public class GeneralLogReader {
 public static void main(String[] args){
	 LinkedList<Dialogue> dialogues = new LinkedList<Dialogue>();
		// read Logs
				
				File[] folder = new File("./log").listFiles();

				String result = "";
				
				for (int i = 0; i < folder.length; i++) {
					File entry = folder[i];
					try {
						JsonReader jsonReader = Json.createReader(new FileReader(entry));
						try{
						JsonObject j = jsonReader.readObject();
						result = result+"Input sentence: "+
						j
						.getJsonObject("data").getJsonObject("vocapia-data")
						.getString("text")+"\n";
						
						
						JsonArray arr = j.getJsonObject("data").getJsonArray("language-generation");
						for(int k = 0; k < arr.size(); k++){
							result = result+ j.getString("date")+" "+arr.getJsonObject(k).getString("text")+"\n";
						}
						
					}catch(JsonException e1){
						jsonReader = Json.createReader(new FileReader(entry));
						JsonArray a = jsonReader.readArray();
						for(int l = 0; l< a.size(); l++){
							JsonObject j = a.getJsonObject(l);
							result = result+"Input sentence: "+
							j
							.getString("userText")+"\n";
							
							
							JsonArray arr = j.getJsonArray("lg");
							if(arr != null){
							for(int k = 0; k < arr.size(); k++){
								result = result+ arr.getJsonObject(k).getString("text")+"\n";
							}
							}

							result = result+"\n";
						}
					}
					}catch(Exception e){
						e.printStackTrace();
					}
					result = result+"\n";
				}
				System.out.println(result);
 }
}
