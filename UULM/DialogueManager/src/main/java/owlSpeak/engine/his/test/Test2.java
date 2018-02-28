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
public class Test2 {

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
		
		
		
//		OwlSpeakOntology o = owlEngine.systemCore.actualOnto[Settings.getuserpos(user)];
//		IPartitionDistribution pd = o.partitionDistributions[Settings.getuserpos(user)];
//		SummaryBelief sumBel = o.factory.createSummaryBelief(pd, null, user);
//		SummaryAgenda sumA = null;
//		for (SummaryAgenda a : owlEngine.systemCore.getSummaryAgendasFromSummaryAgendaType(SummaryAgendaType.REQUEST, user)) {
//			sumA = a;
//			break;
//		}
//		sumBel.setSummaryAgenda(sumA);
//		o.summarySpaceBeliefPoints.add(sumBel);
		
		String[] resList0 = {null,null,null};
		String[] spkList0 = {null,null,null};
		String[] conList0 = {null,null,null};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "greet_agenda", "null", "user", resList0, null , conList0, spkList0);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "greet_agenda", "user", resList0, null , conList0, spkList0);
		String[] resList1 = {"travel_a1n1d_dest_miami_mv",null,null};
		String[] spkList1 = {"miami",null,null};
		String[] conList1 = {"0.89",null,null};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "miami", "user", resList1, "1", conList1, spkList1);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList1, "1", conList1, spkList1);
		String[] resList2 = {"travel_a1n1d_reject_dest_mv",null,null};
		String[] spkList2 = {"nein",null,null};
		String[] conList2 = {"0.85",null,null}; //{"0.97",null,null}; // TODO {"0.85",null,null}; check if this works for sem but not for var
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "nein", "user", resList2, "1", conList2, spkList2);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "user", resList2, "1", conList2, spkList2);
		String[] resList3 = {"travel_a1n1d_dest_paris_mv","travel_a1n1d_dest_paris_mv",null};
		String[] spkList3 = {"ich möchte nach paris","ich will nach paris",null};
		String[] conList3 = {"0.74","0.12",null};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "ich möchte nach paris", "user", resList3, "2", conList3, spkList3);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "user", resList3, "2", conList3, spkList3);
		String[] resList4 = {"travel_a1n1d_conf_dest_mv","travel_a1n1d_conf_dest_mv",null};
		String[] spkList4 = {"ja","genau",null};
		String[] conList4 = {"0.91","0",null};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "ja", "user", resList4, "2", conList4, spkList4);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dest_agenda", "user", resList4, "2", conList4, spkList4);
		String[] resList5 = {"travel_a1n1d_iss_work_mv",null,null};
		String[] spkList5 = {"arbeit",null,null};
		String[] conList5 = {"0.89",null,null};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_iss_agenda", "arbeit", "user", resList5, "1", conList5, spkList5);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_iss_agenda", "user", resList5, "1", conList5, spkList5);
		String[] resList6 = {"travel_a1n1d_conf_iss_mv","travel_a1n1d_conf_iss_mv","travel_a1n1d_reject_iss_mv"};
		String[] spkList6 = {"genau","ja","nein"};
		String[] conList6 = {"0.78","0","0"};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_iss_agenda", "genau", "user", resList6, "3", conList6, spkList6);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_iss_agenda", "user", resList6, "3", conList6, spkList6);
		String[] resList7 = {"travel_a1n1d_dur_four_weeks_mv",null,null};
		String[] spkList7 = {"vier wochen",null,null};
		String[] conList7 = {"0.91",null,null};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dur_agenda", "vier wochen", "user", resList7, "1", conList7, spkList7);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dur_agenda", "user", resList7, "1", conList7, spkList7);
		String[] resList8 = {"travel_a1n1d_conf_dur_mv","travel_a1n1d_reject_dur_mv","travel_a1n1d_conf_dur_mv"};
		String[] spkList8 = {"stimmt","stimmt nicht","stimmt genau"};
		String[] conList8 = {"0.9","0.1","0.1"};
		//owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dur_agenda", "stimmt", "user", resList8, "3", conList8, spkList8);
		owlEngine.buildWork("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "conf_dur_agenda", "user", resList8, "3", conList8, spkList8);

//		String[] resList9 = {null,null,null};
//		String[] spkList9 = {null,null,null};
//		String[] conList9 = {null,null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "end_agenda", "null", "user", resList9, null , conList9, spkList9);
		
//		String[] resList0 = {null,null,null};
//		String[] spkList0 = {null,null,null};
//		String[] conList0 = {null,null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "greet_agenda", null, "user", resList0, null , conList0, spkList0);
//		String[] resList1 = {"travel_a1n1d_dest_london_mv",null,null};
//		String[] spkList1 = {"london",null,null};
//		String[] conList1 = {"0.88",null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dest_agenda", "london", "user", resList1, "1", conList1, spkList1);
//		String[] resList2 = {null,null,null};
//		String[] spkList2 = {null,null,null};
//		String[] conList2 = {null,null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "submit_london_agenda", null, "user", resList2, null , conList2, spkList2);
//		String[] resList3 = {"travel_a1n1d_iss_work_mv",null,null};
//		String[] spkList3 = {"arbeit",null,null};
//		String[] conList3 = {"0.91",null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_iss_agenda", "arbeit", "user", resList3, "1", conList3, spkList3);
//		String[] resList4 = {null,null,null};
//		String[] spkList4 = {null,null,null};
//		String[] conList4 = {null,null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "submit_work_agenda", null, "user", resList4, null , conList4, spkList4);
//		String[] resList5 = {"travel_a1n1d_dur_two_weeks_mv",null,null};
//		String[] spkList5 = {"zwei wochen",null,null};
//		String[] conList5 = {"0.84",null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "ask_dur_agenda", "zwei wochen", "user", resList5, "1", conList5, spkList5);
//		String[] resList6 = {null,null,null};
//		String[] spkList6 = {null,null,null};
//		String[] conList6 = {null,null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "submit_two_weeks_agenda", null, "user", resList6, null , conList6, spkList6);
//		String[] resList7 = {null,null,null};
//		String[] spkList7 = {null,null,null};
//		String[] conList7 = {null,null,null};
//		owlEngine.buildWorkHIS("http://penguin.e-technik.uni-ulm.de:8080/owlSpeak", "end_agenda", null, "user", resList7, null , conList7, spkList7);
//
//		
		

////		owlEngine.buildRequestHIS(whereAmI, user, "0");
//		
//    	String agenda = "greet_agenda";
//    	String speak = "paris";
//		
//		String destResultLists[] = new String[3];
//    	
//		// N-best list results, limited to 3 
//    	destResultLists[0] = "travel_a1n1d_dest_paris_mv";
//    	destResultLists[1] = "travel_a1n1d_dest_miami_mv";
//    	destResultLists[2] = "travel_a1n1d_dest_london_mv";
//    	
//    	String destSpeakLists[] = new String[3];
//    	
//    	destSpeakLists[0] = "paris";
//    	destSpeakLists[1] = "miami";
//    	destSpeakLists[2] = "london";
//
//    	String destResultLen = "1";
//    	
//    	String destConfidences[] = new String[3];
//    	destConfidences[0] = "0.95";
//    	destConfidences[1] = "0.2";
//    	destConfidences[2] = "0.05";
//    	
//    	
//		owlEngine.buildWorkHIS(whereAmI, agenda, speak, user, destResultLists, "0", destConfidences, destSpeakLists);
//		
//		agenda = "ask_dest_agenda";
//		
//		owlEngine.buildWorkHIS(whereAmI, agenda, speak, user, destResultLists, destResultLen, destConfidences, destSpeakLists);
//		
//		
//		// issue
//    	agenda = "ask_iss_agenda";
//    	speak = "arbeit";
//		
//		String issResultLists[] = new String[3];
//    	
//		// N-best list results, limited to 3 
//		issResultLists[0] = "travel_a1n1d_iss_work_mv";
//		issResultLists[1] = "travel_a1n1d_iss_shopping_mv";
//		issResultLists[2] = "travel_a1n1d_iss_party_mv";
//    	
//    	String issSpeakLists[] = new String[3];
//    	
//    	issSpeakLists[0] = "work";
//    	issSpeakLists[1] = "shopping";
//    	issSpeakLists[2] = "party";
//
//    	String issResultLen = "1";
//    	
//    	String issConfidences[] = new String[3];
//    	issConfidences[0] = "0.95";
//    	issConfidences[1] = "0.2";
//    	issConfidences[2] = "0.05";
//		
//    	owlEngine.buildWorkHIS(whereAmI, agenda, speak, user, issResultLists, issResultLen, issConfidences, issSpeakLists);
//		
////		owlEngine.buildWorkHIS(whereAmI, "greet_agenda", speak, user, resultLists, resultLen, Confidences, speakLists);
////		owlEngine.buildWorkHIS(whereAmI, agenda, speak, user, resultLists, resultLen, Confidences, speakLists);
////		System.out.println(response);
//		owlEngine.buildReset(whereAmI, user);
//		owlEngine.buildReset(whereAmI, user);
				
		long stop = System.currentTimeMillis() - start;
		System.out.println("finished after " + stop + " ms");
	}
}
