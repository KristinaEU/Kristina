package owlSpeak.usersimulatorLetsGo;

import java.io.IOException;

import owlSpeak.engine.ServletEngine;
import owlSpeak.servlet.OwlSpeakServlet;

public class Test {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws SecurityException, IOException {
		
		String agenda = "open_agenda";
		String userMove = "multi_slot3c_mv";
		String confidence = "0.18";
		String speak = "Bus_route_sem G2 Departure_place_sem BOULEVARD_OF_ALLIES_CENTER Time_sem now";
		
		String user = "user";
		String whereAmI = "http://penguin.e-technik.uni-ulm.de:8080/owlSpeak";
		
		System.setProperty("owlSpeak.settings.file", "C:/OwlSpeak/settings.xml");
		ServletEngine owlEngine = new ServletEngine();
		
		owlEngine.buildReset(whereAmI, user);
		owlEngine.buildReset(whereAmI, user);
		
		OwlSpeakServlet
		.processRequest(whereAmI, null, user, "0", owlEngine);
		
		OwlSpeakServlet.processWork(
				whereAmI, agenda, userMove, confidence, speak,
				user, owlEngine);

	}

}
