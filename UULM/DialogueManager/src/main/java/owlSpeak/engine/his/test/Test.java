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
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("starting...");
		
		System.setProperty("owlSpeak.settings.file","C:/OwlSpeak/settings.xml");
		
		
		final ServletEngine owlEngine = new ServletEngine();
    	
		String whereAmI = "http://penguin.e-technik.uni-ulm.de:8080/owlSpeak";
    	final String user = "user";
		
		owlEngine.buildReset(whereAmI, user);
		owlEngine.buildReset(whereAmI, user);
		owlEngine.buildRequest("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "user", null, null, false);
		
		
		String[] resList0 = {null,null,null};
		String[] spkList0 = {null,null,null};
		String[] conList0 = {null,null,null};
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "greet_agenda", "user", resList0, null , conList0, spkList0);
		String[] resList1 = {"travel_a1n1d_dest_london_mv",null,null};
		String[] spkList1 = {"london",null,null};
		String[] conList1 = {"0.87",null,null};
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList1, "1", conList1, spkList1);
		String[] resList2 = {"travel_a1n1d_conf_dest_mv","travel_a1n1d_reject_dest_mv",null};
		String[] spkList2 = {"yes","no",null};
		String[] conList2 = {"0.95","0.05",null};
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "user", resList2, "2", conList2, spkList2);
		String[] resList3 = {"travel_a1n1d_iss_work_mv","travel_a1n1d_iss_party_mv",null};
		String[] spkList3 = {"work","party",null};
		String[] conList3 = {"0.59","0.01",null};
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_iss_agenda", "user", resList3, "2", conList3, spkList3);
		String[] resList4 = {"travel_a1n1d_reject_iss_mv",null,null};
		String[] spkList4 = {"no",null,null};
		String[] conList4 = {"0.94",null,null};
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dur_impl_iss_agenda", "user", resList4, "1", conList4, spkList4);

				
		long stop = System.currentTimeMillis() - start;
		System.out.println("finished after " + stop + " ms");
	}
}
