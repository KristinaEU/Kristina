package owlSpeak.servlet;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import presenter.KristinaPresenter;

//entspricht OwlSpeakServlet

@Path("")
public class KristinaServlet {
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public synchronized String post(@QueryParam("valence") String valence, @QueryParam("arousal") String arousal, final String content) {

		System.out.println("received POST");
		String result;
		try {
			result = KristinaPresenter.performDM(Float.parseFloat(valence), Float.parseFloat(arousal), content);
			
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

		return result;
	}


}


