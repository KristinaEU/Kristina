package demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;






import org.python.apache.commons.compress.utils.IOUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import owlSpeak.kristina.emotion.KristinaEmotion;
import presenter.KristinaPresenter;

@Path("")
public class KristinaDemo {
	
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
	@Path("status")
	@Produces("application/json")
	public synchronized String getStatus(){
		return "{\"owlRunning\":"+KristinaPresenter.getManagerStatus()+",\"almaRunning\":"+KristinaPresenter.getGenerationStatus()+"}";
	}
	
	@GET
	@Path("emotion")
	@Produces("application/json")
	public synchronized String getEmotion(){
		KristinaEmotion m = KristinaPresenter.getCurrentEmotion();
		return "{\"valence\":"+m.getValence()+",\"arousal\":"+m.getArousal()+"}";
	}
	
	@GET
	@Path("workspace")
	@Produces("application/json")
	public synchronized String getWorkspace(){
		String[] ws = KristinaPresenter.getWorkspace("user");
		String selection = KristinaPresenter.getSystemMove("user");
		String result = "{\"selection\":\""+selection+"\",\"workspace\":[";
		if(ws.length>0){
			result = result+"\""+ws[0]+"\"";
			for(int i = 1; i<ws.length; i++){
				result = result+",\""+ws[i]+"\"";
			}
		}
		result = result+"]}";
		return result;
	}
	
	@POST
	@Path("sendRequestFluid")
	public synchronized void requestFluid(){
		BufferedReader r;
		
		try {
			r = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/Example_Input_Output/inform.ttl")));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();

			KristinaPresenter.performDemoDM(data,"user");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	}
	
	@POST
	@Path("sendVA")
	public synchronized void setEmotion(@QueryParam("valence") String valence, @QueryParam("arousal") String arousal){
		KristinaPresenter.setUserEmotion(new KristinaEmotion(Float.parseFloat(valence), Float.parseFloat(arousal)));
	}
}
