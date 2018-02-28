package owlSpeak.servlet;

import java.io.File;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("")
public class OwlDocumentServlet {
	
	@GET
	@Path("{fileName:.*}")
	public synchronized File get(@PathParam("fileName") String file){
		try {
			return new File("./conf/OwlSpeak/"+file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
