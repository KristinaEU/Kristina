package owlSpeak.engine.his.test;

import owlSpeak.engine.ServletEngine;

/**
 * OwlSpeak is tested with 3-best-list input as it would have been performed
 * talking to OwlSpeak.
 * 
 * This test class uses the travel ontology as created by Savina Koleva.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */
public class TestShowPartitionDistribution {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("starting...");
		
		System.setProperty("owlSpeak.settings.file","C:/OwlSpeak/settings.xml");
		
		
		final ServletEngine owlEngine = new ServletEngine();
    	
		
		owlEngine.buildRequest("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "user", null, null, false);
	}
}
