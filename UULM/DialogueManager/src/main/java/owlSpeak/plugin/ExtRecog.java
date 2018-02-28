package owlSpeak.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import owlSpeak.engine.Settings;
import owlSpeak.plugin.Keyword.RecArray;
import owlSpeak.plugin.Keyword.RecWord;
import owlSpeak.servlet.OwlSpeakServlet;


/**
 * Class ExtRecog: Plugin to use a external Speechrecognizer
 * @author Max Grotz
 * @version 1.0
 */
public class ExtRecog {

	/**
	 * starts an external SpeechRecognizer using {@link ExtRecog#recognize} with the specified grammar - each recognized word as its own RecWord
	 * @param grammar Grammar-String
	 * @return Vector<RecWord> with recognized words as {@link RecWord}  
	 */	
	public static Vector<RecWord> outputRecWord(String commandParameter, String grammar){
		Vector<RecWord> vRecWord = new Vector<RecWord>();
		Keyword x = new Keyword();
		List<Element> lResultsChild = recognize(commandParameter, grammar);

		for (int i=0;i<lResultsChild.size();i++){
			
			String[] saRecog = lResultsChild.get(i).getText().toLowerCase().split(" ");
    		//String sConf = lResultsChild.get(i).getAttributeValue("confidence");
    		for (int j = 0; j < saRecog.length; j++) {
    			boolean contains = false;	
    			RecWord temp = x.new RecWord(saRecog[j], "");
	    		if (temp.keyword.equals("...")){
	    			contains = true;
	    		}
    			for (int k = 0; k < vRecWord.size(); k++) {
	    			if (vRecWord.get(k).keyword.equalsIgnoreCase(temp.keyword)) contains = true;
				}

	    		if (contains == false){
	    			vRecWord.add(temp);
	    		}
	 
    		}
    	}
		//Consolen-Ausgabe
		if (lResultsChild.size() != 0){
			System.out.println("***** Extern-Recognizer *****");
			String log = "| ";
			for (int i = 0; i < vRecWord.size(); i++){
				log = log + vRecWord.get(i).keyword + " | "; //"\t -> " + vRecWord.get(i).conf + " | ";
				System.out.println("  " + vRecWord.get(i).keyword); //"\t -> " + vRecWord.get(i).conf);	
			}
			OwlSpeakServlet.logger.logp(Level.INFO, "Extern-Recognizer: ", "", log);
			//System.out.println("*****************************");
		}
				
		return vRecWord;	
	}
	
	/**
	 * starts an external SpeechRecognizer with the spezified grammar
	 * @param commandParameter command parameter for console-programm
	 * @param grammar Grammar-String 
	 * @return Vector&lt;RecKeyword&gt; with all words as {@link RecArray} 
	 */	
	public static Vector<RecArray> outputRecArrays(String commandParameter, String grammar){
		Vector<RecArray> vRecArray = new Vector<RecArray>();
		Keyword x = new Keyword();
		List<Element> lResultsChild = recognize(commandParameter, grammar);
		
		for (int i=0;i<lResultsChild.size();i++){
			String recog = lResultsChild.get(i).getText().toLowerCase();
			String conf = lResultsChild.get(i).getAttributeValue("confidence");
			RecArray temp = x.new RecArray(recog, conf);
			vRecArray.add(temp);
		}
		
		//Consolen-Ausgabe
		if (lResultsChild.size() != 0){
			System.out.println("***** Extern-Recognizer *****");
			String log = "| ";
			for (int i = 0; i < vRecArray.size(); i++){
				for (int j=0; j<(vRecArray.get(i).keywordArr.length); j++){
					System.out.print("  " + vRecArray.get(i).keywordArr[j]);
					log = log + vRecArray.get(i).keywordArr[j] + "  "; //"\t -> " + vRecWord.get(i).conf + " | ";
				}
				System.out.println("\t -> " + vRecArray.get(i).conf);
				log = log + "\t -> " + vRecArray.get(i).conf;
			}
			OwlSpeakServlet.logger.logp(Level.INFO, "Extern-Recognizer: ", "", log);
			//System.out.println("*****************************");
		}
		return vRecArray;
	}


	/**
	 * starts an external SpeechRecognizer and generates a List of XML-{@link Element}s
	 * @param grammar the grammar for the recognizer
	 * @return List&lt;Element&gt; recognized words as a List of XML-{@link Element}
	 */	
	public static List<Element> recognize(String commandParameter, String grammar){
		
		List<Element> lResultsChild = new java.util.ArrayList<Element>();
		String sSpeechRecConPath = Settings.homePath+"Plugins\\SpeechRecCon\\";
		String sOutputFile = sSpeechRecConPath+"output.xml";
		String output = new String();
		Document doc;
		
		String[] command = {"cmd.exe", "/c", sSpeechRecConPath+"SpeechRecCon", "-"+commandParameter, sSpeechRecConPath+"file.wav", sOutputFile, grammar};
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		try {
			process = runtime.exec(command);
			/*for (int i=0; i<50000; i++){
				try {
					Thread.sleep(100);
					process.exitValue();
					break;
				} catch (Exception e) {
					continue;
				}
			}
		    */
			
			BufferedReader brComOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			// *** Read the console-output from external recognizer ***
			String line = null;
			while ((line = brComOut.readLine()) != null) {
				output = output + line; 
			}
	
			// *** Import XML-file with recognition-result ***
			File sourceFile = new File(sOutputFile);
			FileInputStream in = new FileInputStream(sourceFile);
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(in);
			in.close(); 
			Element results = doc.getRootElement();
	        Object result[] = results.getChildren().toArray();
	    	for(int i=0; i<result.length; i++){
	    		lResultsChild.add((Element)result[i]);
	    	}
	    	
		} catch (Exception e) {
			System.out.println(e);
		}    	
		
		// *** Logger-Output ***
		if (lResultsChild.size() == 0) {
			OwlSpeakServlet.logger.logp(Level.INFO, "External-Recognizer: ", output, "");
		}
		
	    return lResultsChild;
	}

}
