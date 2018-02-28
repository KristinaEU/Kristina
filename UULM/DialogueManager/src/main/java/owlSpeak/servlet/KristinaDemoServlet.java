package owlSpeak.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringEscapeUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import owlSpeak.kristina.emotion.KristinaEmotion;


@Path("")
public class KristinaDemoServlet{
	
	@Context
	UriInfo uri;
	
	@GET
	@Path("{fileName:.*}")
	public synchronized File get(@PathParam("fileName") String file){
		try {
			return new File("./web-demo/"+file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	@GET
	@Path("emotion")
	@Produces("application/json")
	public synchronized String getEmotion(){
		KristinaEmotion m = KristinaServlet.getCurrentEmotion();
		return "{\"valence\":"+m.getValence()+",\"arousal\":"+m.getArousal()+"}";
	}
	
	@GET
	@Path("workspace")
	@Produces("application/json")
	public synchronized String getWorkspace(){try{
		String[] ws = KristinaServlet.getWorkspace("KristinaUser");

		String[] selection = KristinaServlet.getSystemMove("KristinaUser");
		
		String result = "{\"selection\":[";
		if(selection.length>0){
			result = result+"\""+selection[0]+"\"";
			for(int i = 1; i<selection.length; i++){
				result = result+",\""+selection[i]+"\"";
			}
		}
		result = result +"],\"workspace\":[";
		if(ws.length>0){
			result = result+"\""+ws[0]+"\"";
			for(int i = 1; i<ws.length; i++){
				result = result+",\""+ws[i]+"\"";
			}
		}
		result = result+"]}";
		return result;
	}catch(Exception e){
		e.printStackTrace();
	}
	return null;
	}
	
	@GET
	@Path("verbosity")
	@Produces("application/json")
	public synchronized String getVerbosity(){try{
		String[] ws = KristinaServlet.getVerbosity("KristinaUser");

		String[] selection = KristinaServlet.getSystemMove("KristinaUser");
		
		String result = "{\"selection\":[";
		if(selection.length>0){
			result = result+"\""+selection[0]+"\"";
			for(int i = 1; i<selection.length; i++){
				result = result+",\""+selection[i]+"\"";
			}
		}
		result = result +"],\"verbosity\":[";
		if(ws.length>0){
			result = result+"\""+ws[0]+"\"";
			for(int i = 1; i<ws.length; i++){
				result = result+",\""+ws[i]+"\"";
			}
		}
		result = result+"]}";
		return result;
	}catch(Exception e){
		e.printStackTrace();
	}
	return null;
	}

	
	@POST
	@Path("sendRequestFluid")
	public synchronized void requestFluid(@QueryParam("scenario") String scenario){
		BufferedReader r;

		Client client = ClientBuilder.newClient();
		try {
			r = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/ExampleData_LA/Dropbox/sleep/la-output_11.owl")));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();
			
			String v = "0";
			String a = "0.25";
			/*if(entry.getName().equals("la-output_3.owl")){
				v = "-0.5";
				a = "-0.25";
			}*/
		
			data = "{\"data\":{\"fusion\":{\"valence\":\""+v+"\",\"arousal\":\""+a+"\"},\"language-analysis\":\""
		+StringEscapeUtils.escapeEcmaScript(data)+"\"},\"meta\":{\"user\":\"hans\",\"scenario\":\""+scenario+"\",\"language\":\"de\"}}";

			WebTarget webTarget = client.target(uri.getBaseUri());
			Invocation.Builder ib = webTarget.request("application/rdf+xml");

			long start = System.currentTimeMillis();
			Response response = ib.post(Entity.entity(data, MediaType.APPLICATION_JSON));
			long end = System.currentTimeMillis();
			//System.out.println(end-start);
			if (response.getStatus() != 200) {
				String s = response.getStatusInfo().toString();
				response.close();
				throw new IOException(s);
			}
			String result = StringEscapeUtils.unescapeEcmaScript(response.readEntity(String.class));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	@POST
	@Path("sendVA")
	public synchronized void setEmotion(@QueryParam("valence") String valence, @QueryParam("arousal") String arousal){
		KristinaServlet.setUserEmotion(new KristinaEmotion(Float.parseFloat(valence), Float.parseFloat(arousal)));
	}
	
	@POST
	@Path("sysAct")
	public synchronized void setSystemAction(@QueryParam("action") String action){
		KristinaServlet.setSystemAction(action);
	}
	
	@POST
	@Path("usrAct")
	public synchronized void setUserAction(@QueryParam("action") String action){
		KristinaServlet.setUserAction(action);
	}
}
