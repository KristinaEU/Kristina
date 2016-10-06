package owlSpeak.servlet;

import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ListIterator;

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
		try{

		System.out.println("received POST");

		JsonReader jsonReader = Json.createReader(new StringReader(json));
		JsonObject j = jsonReader.readObject();
		
		String valence = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getString("valence"));
		String arousal = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getString("arousal"));

		String content = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("data").getString("language-analysis"));
		
		String user = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("user"));
		String scenario = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("scenario"));
		
		//CerthClient.setPath(j.getString("path"));
		
		String result;
		try {
			result = KristinaPresenter.performDM(Float.parseFloat(valence), Float.parseFloat(arousal), content, user, scenario);
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WebApplicationException();
		}

		System.out.println("DM done");

		return StringEscapeUtils.escapeEcmaScript(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}

}


