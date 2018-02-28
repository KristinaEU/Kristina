package test;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import owlSpeak.Agenda;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.servlet.OwlOutput;
import owlSpeak.servlet.OwlSpeakServlet;

public class OwlTest {
public static void main(String[] args){
	System.setProperty("owlSpeak.settings.file", "./src/main/resources/OwlSpeak/settings.xml");
	ServletEngine owlEngine = new ServletEngine();
	String whereAmI = "http://beaver.e-technik.uni-ulm.de:8080/owlSpeak";
	final String user = "user";
	//OwlSpeakServlet.reset(owlEngine, whereAmI, user);
	
	/*try {
		OwlSpeakServlet.processRequest(whereAmI, null, user, "0", owlEngine);
	} catch (SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
	
	OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
	Iterator<Agenda> ws = ontoOfLastAgenda.getWorks(Settings.getuserpos(user)).getNext().iterator();
	while(ws.hasNext()){
		System.out.println(ws.next());
	}
	

}
}
