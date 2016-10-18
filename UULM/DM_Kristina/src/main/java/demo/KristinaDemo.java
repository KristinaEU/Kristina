package demo;

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
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;









import model.KristinaModel;

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
	public synchronized String getWorkspace(){try{
		String[] ws = KristinaPresenter.getWorkspace("user");

		String[] selection = KristinaPresenter.getSystemMove("user");
		
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
	
	@POST
	@Path("sendRequestFluid")
	public synchronized void requestFluid(){
		BufferedReader r;
		
		try {
			r = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/ExampleData_LA/articles/la-article_1.owl")));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();
			
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

			KristinaPresenter.performDM(0f,0f,data, "Iwona", "newspaper", handler);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	@POST
	@Path("sendVA")
	public synchronized void setEmotion(@QueryParam("valence") String valence, @QueryParam("arousal") String arousal){
		KristinaPresenter.setUserEmotion(new KristinaEmotion(Float.parseFloat(valence), Float.parseFloat(arousal)));
	}
	
	@POST
	@Path("sysAct")
	public synchronized void setSystemAction(@QueryParam("action") String action){
		KristinaModel.setSystemAction(action);
	}
	
	@POST
	@Path("usrAct")
	public synchronized void setUserAction(@QueryParam("action") String action){
		KristinaModel.setUserAction(action);
	}
}
