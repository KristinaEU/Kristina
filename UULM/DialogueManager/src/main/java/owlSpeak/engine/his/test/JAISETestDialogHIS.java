package owlSpeak.engine.his.test;

import owlSpeak.engine.ServletEngine;

/**
 * OwlSpeak is tested with 3-best-list input as it would have been performed
 * talking to OwlSpeak.
 * 
 * This test class uses the modified travel ontology originally created by Savina Koleva.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.2
 * 
 */
public class JAISETestDialogHIS {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		System.out.println("starting...");
		
		System.setProperty("owlSpeak.settings.file","C:/OwlSpeak/settings.xml");
		
		
		final ServletEngine owlEngine = new ServletEngine();
    	
		String whereAmI = "http://leopard.e-technik.uni-ulm.de:8080/owlSpeak";
    	final String user = "user";
		
		owlEngine.buildReset(whereAmI, user);
		owlEngine.buildReset(whereAmI, user);
		owlEngine.buildRequest("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "user", null, null, false);

		
		String[] resList0 = {null,null,null};
		String[] spkList0 = {null,null,null};
		String[] conList0 = {null,null,null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "greet_agenda", "user", resList0, null , conList0, spkList0);
		String[] resList1 = {"travel_a1n1d_dest_paris_mv","travel_a1n1d_dest_london_mv",null};
		String[] spkList1 = {"paris","london",null};
		String[] conList1 = {"0.79","0.1",null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList1, "2", conList1, spkList1);
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList1, "2", conList1, spkList1);
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList1, "2", conList1, spkList1);
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList1, "2", conList1, spkList1);
		String[] resList2 = {"travel_a1n1d_reject_dest_mv",null,null};
		String[] spkList2 = {"nein",null,null};
		String[] conList2 = {"0.85",null,null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "user", resList2, "1", conList2, spkList2);

		
//		String[] resList30 = {"heating_a1n1d_user_heating_on_mv",null,null};
//		String[] spkList30 = {"heating on",null,null};
//		String[] conList30 = {"0.80",null,null};
//		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "user", resList30, "1", conList30, spkList30);
//		String[] resList31 = {"heating_a1n1d_user_heating_temp_20_mv",null,null};
//		String[] spkList31 = {"20 degrees",null,null};
//		String[] conList31 = {"0.89",null,null};
//		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "heating_which_temp_agenda", "user", resList31, "1", conList31, spkList31);
//		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "heating_on_and_temp_agenda", "user", resList0, null , conList0, spkList0);
		
//		String[] resList3 = {"travel_a1n1d_dest_london_mv","travel_a1n1d_dest_london_mv",null};
//		String[] spkList3 = {"ich möchte nach london","ich will nach london",null};
//		String[] conList3 = {"0.74","0.12",null};
//		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList3, "2", conList3, spkList3);
		String[] resList4 = {"travel_a1n1d_conf_dest_mv","travel_a1n1d_conf_dest_mv",null};
		String[] spkList4 = {"ja","genau",null};
		String[] conList4 = {"0.91","0",null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "user", resList4, "2", conList4, spkList4);
		String[] resList5 = {"travel_a1n1d_iss_work_mv","travel_a1n1d_iss_shopping_mv",null};
		String[] spkList5 = {"work","shopping",null};
		String[] conList5 = {"0.79","0.2",null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_iss_agenda", "user", resList5, "2", conList5, spkList5);
		String[] resList6 = {"travel_a1n1d_reject_iss_mv",null,null};
		String[] spkList6 = {"nein",null,null};
		String[] conList6 = {"0.78",null,null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dur_impl_iss_agenda", "user", resList6, "1", conList6, spkList6);
//		String[] resList7 = {"travel_a1n1d_iss_shopping_mv",null,null};
//		String[] spkList7 = {"shopping",null,null};
//		String[] conList7 = {"0.63",null,null};
//		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_iss_agenda", "user", resList7, "1", conList7, spkList7);
		String[] resList8 = {"travel_a1n1d_dur_two_weeks_mv",null,null};
		String[] spkList8 = {"zwei wochen",null,null};
		String[] conList8 = {"0.91",null,null};
		owlEngine.buildWork("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dur_impl_iss_agenda", "user", resList8, "1", conList8, spkList8);
		owlEngine.buildRequest("http://leopard.e-technik.uni-ulm.de:8080/owlSpeak", "user", null, null, true);
				
		long stop = System.currentTimeMillis() - start;
		System.out.println("finished after " + stop + " ms");
	}
}
