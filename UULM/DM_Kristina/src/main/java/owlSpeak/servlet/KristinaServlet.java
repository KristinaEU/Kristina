package owlSpeak.servlet;

import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import model.CerthClient;

import org.apache.commons.lang3.StringEscapeUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import presenter.KristinaPresenter;

//entspricht OwlSpeakServlet

@Path("")
public class KristinaServlet {
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	public synchronized String post(final String json) {

		System.out.println("received POST");
		try{
			
		long start = System.currentTimeMillis();
		
		//set up logger for evaluation and debugging
		Handler handler = null;
		try {
			LocalTime t = LocalTime.now();
			handler = new FileHandler("log/" + LocalDate.now()
					+ "_"+t.getHour()+"_"+t.getMinute()+"_"+t.getSecond()+"_"+t.getNano()+".log");
			handler.setEncoding("utf-8");

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//read json input
		Logger log1 = Logger.getLogger("VSM2DM");

		log1.setUseParentHandlers(false);
		log1.addHandler(handler);
		log1.info(json);
		JsonReader jsonReader = Json.createReader(new StringReader(json));
		JsonObject j = jsonReader.readObject();
		String valence = "0";
		try{
			valence = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getString("valence"));
		}catch(ClassCastException e){
			valence = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getJsonNumber("valence").toString());
		}
		String arousal = "0";
		try{
			arousal = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getString("arousal"));
		}catch(ClassCastException e){
			arousal = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getJsonNumber("arousal").toString());
		}

		String content = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getString("language-analysis"));
		
		String user = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("user"));
		String scenario = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("scenario"));
		/*try{
		CerthClient.setPath(j.getString("path"));
		}catch(Exception e){
			
		}*/
		
		
		String result = KristinaPresenter.performDM(Float.parseFloat(valence), Float.parseFloat(arousal), content, user, scenario, handler);
			

		System.out.println("DM done");
		long end = System.currentTimeMillis();
		
		Logger log2 = Logger.getLogger("DM2VSM");
		log2.setUseParentHandlers(false);
		log2.addHandler(handler);

		Logger log3 = Logger.getLogger("runtime");
		log3.setUseParentHandlers(false);
		log3.addHandler(handler);
		
		log2.info(StringEscapeUtils.escapeEcmaScript(result));
		log3.info(Long.toString(end-start));
		Handler[] arr = log3.getHandlers();
		for(int i = 0; i < arr.length; i++){
			arr[i].close();
		}
		return StringEscapeUtils.escapeEcmaScript(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}


