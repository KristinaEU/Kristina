package owlSpeak.servlet;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import owlSpeak.engine.ServletEngine;

@Path("owlSpeak")
public class OwlServlet {
	private static OwlSpeakServlet instance;
	
	public static void init(ServletEngine engine){
		instance = new OwlSpeakServlet();
		instance.init(engine);
	}
	
	@GET
	public String doGet(@Context UriInfo uriInfo) {
		return instance.doGet(uriInfo);
	}
	
	@POST
	public String doPost(@Context UriInfo uriInfo) {
		return instance.doPost(uriInfo);
	}
}