package view;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import presenter.KristinaPresenter;

@Path("")
public class KristinaServer {
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public synchronized String post(@QueryParam("valence") String valence, @QueryParam("arousal") String arousal, final String content) {

		System.out.println("received POST: ");
		System.out.println("VALENCE: "+valence);
		System.out.println("AROUSAL: "+arousal);
		System.out.println("CONTENT:\n"+content);
		String result = KristinaPresenter.performDM(valence, arousal, content);

		System.out.println("DM done");

		return result;
	}


}


