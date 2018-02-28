package owlSpeak.kristina;

import java.io.IOException;

import owlSpeak.engine.ServletEngine;
import owlSpeak.servlet.OwlSpeakServlet;

public class KristinaTest_alt {
	
	public static void main(String[] args) throws IOException{
		System.setProperty("owlSpeak.settings.file", "C:/OwlSpeak/settings.xml");
		ServletEngine owlEngine = new ServletEngine();
		String whereAmI = "http://beaver.e-technik.uni-ulm.de:8080/owlSpeak";
		final String user = "user";
		OwlSpeakServlet.reset(owlEngine, whereAmI, user);
		
		
		KristinaDocument_alt systemMove = (KristinaDocument_alt) (OwlSpeakServlet.processRequest(whereAmI, null, user, "0", owlEngine));
		String move = systemMove.getSystemMove();
		String agenda = systemMove.getAgenda();
		
		boolean printToConsole = true;
		String confidence = "0";    
		String speak = "";
		
		if (printToConsole) System.out.println("System move:");
		if (printToConsole) System.out.println("\tmove: " + move);
		if (printToConsole) System.out.println("\tagenda: " + agenda);
		if (printToConsole) System.out.println("\tExitMove: " + systemMove.isExitMove());
		
		//OwlSpeakServlet.processEmotion("sem_emoNegative", user, owlEngine);
		//OwlSpeakServlet.processEmotion("sem_emoPositive", user, owlEngine);

		long start = System.currentTimeMillis();
		systemMove = (KristinaDocument_alt) (OwlSpeakServlet.processWork(whereAmI, agenda, "usermov_Greet", confidence, "Hallo", user, owlEngine)); 
		long end = System.currentTimeMillis();
		System.out.println(end-start);
		
		move = systemMove.getSystemMove();
		agenda = systemMove.getAgenda();
		if (printToConsole) System.out.println("System move:");
		if (printToConsole) System.out.println("\tmove: " + move);
		if (printToConsole) System.out.println("\tagenda: " + agenda);
		if (printToConsole) System.out.println("\tExitMove: " + systemMove.isExitMove());
		
		
		
		
	}

}
