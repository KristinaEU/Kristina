package owlSpeak.plugin;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.plugin.Keyword.MoveKeyword;
import owlSpeak.plugin.Keyword.OntoKeyword;
import owlSpeak.plugin.Keyword.RecArray;
import owlSpeak.servlet.OwlDocument;
import owlSpeak.servlet.OwlSpeakServlet;

/**
 * Class Levenshtein: Plugin to handle a NOMATCH using Levenshtein
 * @author Max Grotz
 * @version 1.0
 */

public class Levenshtein {

	/**
	 * this function is used to perform a Levenshtein-comparison with an amount of RecWords and Keywords
	 * @param owlEngine the actual owlEngine
	 * @param user the name of the user
	 * @param whereAmI the netadress of the servlet
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return the filled document
	 */
	public static OwlDocument performLevenshtein(ServletEngine owlEngine, String user, String whereAmI, String noInputCounter){

		String sFoundOntoKey = new String();
		String sFoundOntoDN = new String();
		String sFoundMoveKey = new String();
		int iOntoDistance;
		int iMoveDistance;
		Vector<RecArray> vRecArray;
		Vector<OntoKeyword> vOntoKeywords;
		Vector<MoveKeyword> vMoveKeywords = new Vector<MoveKeyword>();
		Vector<Result> vResult = new Vector<Result>();

		vRecArray = ExtRecog.outputRecArrays("l", "");
		if (vRecArray.size() == 0){
			System.out.println("No Result external Recognizer -> new Request!");
			OwlDocument VDoc = owlEngine.buildRequest(whereAmI, user, noInputCounter);
			return VDoc;
		}
		
		vOntoKeywords = KeywordLists.generateOntoKeywordsList(owlEngine, user);
		
		System.out.println("******** OntoDistance ********");
		for (int i=0; i < vRecArray.size(); i++){	
			for (int j=0; j < vOntoKeywords.size(); j++){
				boolean added = false;
				for (int k=0; k < vRecArray.elementAt(i).keywordArr.length; k++){
					iOntoDistance = calculateDistance(vRecArray.elementAt(i).keywordArr[k], vOntoKeywords.elementAt(j).keyword);
					if (iOntoDistance < 2){
						sFoundOntoKey = vOntoKeywords.elementAt(j).ontoName;
						sFoundOntoDN = vOntoKeywords.elementAt(j).ontoDomainName;
						System.out.println("Onto: " + sFoundOntoDN + " found");
						vMoveKeywords = KeywordLists.generateMoveKeywordsList(owlEngine, user, sFoundOntoKey);
						for (int l=0; l < vRecArray.size(); l++){
							for (int m=0; m < vMoveKeywords.size(); m++){
								for (int n=0; n < vRecArray.elementAt(l).keywordArr.length; n++){
									iMoveDistance = calculateDistance(vRecArray.elementAt(l).keywordArr[n], vMoveKeywords.elementAt(m).keyword);
									Result rFound = new Result();
									if (iMoveDistance < 2){
										sFoundMoveKey = vMoveKeywords.elementAt(m).moveName;
										boolean bContains = false;
										for (int o = 0; o < vResult.size(); o++) {
											if ((vResult.elementAt(o).sFoundMoveKeyword.equalsIgnoreCase(sFoundMoveKey))) bContains = true;
										}
										System.out.println("Move: " + sFoundMoveKey + " found");
										if (!bContains){
											rFound.sFoundOntoKeyword = sFoundOntoKey;
											rFound.sFoundMoveKeyword = sFoundMoveKey;
											rFound.sRecordedOntoKeyword = vRecArray.elementAt(i).keywordArr[k];
											rFound.sRecordedMoveKeyword = vRecArray.elementAt(l).keywordArr[n];
											vResult.add(rFound);
											added = true;
										}
									}
								}
							}
							if (added) break;
						}
						if (added) break;
					}
				}
			}			
		}
		
		if (vResult.isEmpty()){
			if(owlEngine.systemCore.actualOnto[owlSpeak.engine.Settings.getuserpos(user)]==null){
				sFoundOntoKey = owlEngine.systemCore.actualOnto[owlSpeak.engine.Settings.getuserpos(user)].Name;
				sFoundOntoDN = owlEngine.systemCore.actualOnto[owlSpeak.engine.Settings.getuserpos(user)].getOntoDomainName();
				vMoveKeywords = KeywordLists.generateMoveKeywordsList(owlEngine, user, sFoundOntoKey);
				for (int l=0; l < vRecArray.size(); l++){
					for (int m=0; m < vMoveKeywords.size(); m++){
						for (int n=0; n < vRecArray.elementAt(l).keywordArr.length; n++){
							iMoveDistance = calculateDistance(vRecArray.elementAt(l).keywordArr[n], vMoveKeywords.elementAt(m).keyword);
							Result rFound = new Result();
							if (iMoveDistance < 2){
								rFound.sFoundOntoKeyword = sFoundOntoKey;
								rFound.sFoundMoveKeyword = vMoveKeywords.elementAt(m).moveName;
								rFound.sRecordedOntoKeyword = sFoundOntoDN;
								rFound.sRecordedMoveKeyword = vRecArray.elementAt(l).keywordArr[n];
								vResult.add(rFound);
							}
						}
					}
				}
			}
		}
		
		// *** Logger - Output ***
		String log = "| ";
		for (int i = 0; i < vResult.size(); i++) {
			log = log + "Onto: " + vResult.elementAt(i).sFoundOntoKeyword + "  Move: " + vResult.elementAt(i).sFoundMoveKeyword + " | ";    
		} 
		OwlSpeakServlet.logger.logp(Level.INFO, "Levenshtein-Plugin: ", vResult.size()+" Matches found", log);
		
		// *** Creating answer-String in case of a Nomatch with the result of the Spotting ***
		if (vResult.isEmpty()){
			String sDraft = new String();
			OwlSpeakOntology onto = owlEngine.systemCore.findOntology("system"); 
			Agenda uttAgenda = onto.factory.getAgenda("Plugin_Nomatch_ag");
			Iterator <Move> moveIt=uttAgenda.getHas().iterator();
			while(moveIt.hasNext()){
				Iterator<OWLLiteral> olIt = moveIt.next().getUtterance().getUtteranceString().iterator();
				while(olIt.hasNext()){
					sDraft = sDraft+" "+(olIt.next().getLiteral());
				}
			}
			OwlDocument VDoc = owlEngine.buildOutput(whereAmI, "NOMATCH", sDraft, user);
			return VDoc;
		}
			
		
		OwlSpeakOntology systemOnto = owlEngine.systemCore.findOntology("system"); 
		// *** yes-no-grammars ***
		String sYes = new String();
		Move tempMove_yes = systemOnto.factory.getMove("yes_mv");
		Iterator<OWLLiteral> olIt_yes = tempMove_yes.getGrammar().getGrammarString().iterator();
		while(olIt_yes.hasNext()){
			sYes = sYes+" "+(olIt_yes.next().getLiteral());
		}
		String sNo = new String();
		Move tempMove_No = systemOnto.factory.getMove("no_mv");
		Iterator<OWLLiteral> olIt_No = tempMove_No.getGrammar().getGrammarString().iterator();
		while(olIt_No.hasNext()){
			sNo = sNo+" "+(olIt_No.next().getLiteral());
		}
		// *** catches-utterances ***
		String sCatch1 = new String();
		Move tempMove_Catch1 = systemOnto.factory.getMove("catch1_mv");
		Iterator<OWLLiteral> olIt_Catch1 = tempMove_Catch1.getUtterance().getUtteranceString().iterator();
		while(olIt_Catch1.hasNext()){
			sCatch1 = sCatch1+" "+(olIt_Catch1.next().getLiteral());
		}
		String sCatch2 = new String();
		Move tempMove_Catch2 = systemOnto.factory.getMove("catch2_mv");
		Iterator<OWLLiteral> olIt_Catch2 = tempMove_Catch2.getUtterance().getUtteranceString().iterator();
		while(olIt_Catch2.hasNext()){
			sCatch2 = sCatch2+" "+(olIt_Catch2.next().getLiteral());
		}
		
		// *** Creating answer-String with the result of the Spotting ***
		String sDraft = new String();
		Agenda uttAgenda = systemOnto.factory.getAgenda("Plugin_Found_Keyword_ag");
		Iterator <Move> moveIt=uttAgenda.getHas().iterator();
		while(moveIt.hasNext()){
			Iterator<OWLLiteral> olIt = moveIt.next().getUtterance().getUtteranceString().iterator();
			while(olIt.hasNext()){
				sDraft = sDraft+" "+(olIt.next().getLiteral());
			}
		}
	
		for (int i = 0; i < vResult.size(); i++) {
			String sUtterance = sDraft.replace("%moveKeyword%", vResult.elementAt(i).sRecordedMoveKeyword).replace("%ontoKeyword%", vResult.elementAt(i).sRecordedOntoKeyword);
			vResult.elementAt(i).sUtterance = sUtterance;
			vResult.elementAt(i).sYes = sYes;
			vResult.elementAt(i).sNo = sNo;
			vResult.elementAt(i).sCatch1 = sCatch1;
			vResult.elementAt(i).sCatch2 = sCatch2;
		}


		OwlDocument VDoc = owlEngine.buildPluginAlternatives(vResult, whereAmI, user);		
	
		return VDoc;
	}

	/**
	* this function calculates the minimum of three integers
	* @param a int
	* @param b int
	* @param c int 
	* @return the minimum as integer 
	*/
	private static int calculateMinimum (int a, int b, int c) {
		int mi;
	    mi = a;
	    if (b < mi) {
	    	mi = b;
	    }
	    if (c < mi) {
	    	mi = c;
	    }
	    return mi;
	}
	
	/**
	* this function calculates the Levenshtein-distance between two strings
	* @param s String 1
	* @param t String 2
	* @return int the minimum 
	*/
	private static int calculateDistance (String s, String t) {
		int iMatrix[][];
		int iLenghtS;
		int iLenghtT;
		char s_i;
		char t_j;
		int iCost;

	    iLenghtS = s.length ();
	    iLenghtT = t.length ();
	    if (iLenghtS == 0) {
	      return iLenghtT;
	    }
	    if (iLenghtT == 0) {
	      return iLenghtS;
	    }
	    iMatrix = new int[iLenghtS+1][iLenghtT+1];

	    for (int i = 0; i <= iLenghtS; i++) {
	      iMatrix[i][0] = i;
	    }
	    for (int j = 0; j <= iLenghtT; j++) {
	      iMatrix[0][j] = j;
	    }

	    for (int i = 1; i <= iLenghtS; i++) {
	    	s_i = s.charAt (i - 1);
	    	
	    	for (int j = 1; j <= iLenghtT; j++) {
	    		t_j = t.charAt (j - 1);

	    		if (s_i == t_j) {
	    			iCost = 0;
	    		}
	    		else {
	    			iCost = 1;
	    		}

	    		iMatrix[i][j] = calculateMinimum (iMatrix[i-1][j]+1, iMatrix[i][j-1]+1, iMatrix[i-1][j-1] + iCost);
	    	}
	    }

	    return iMatrix[iLenghtS][iLenghtT];
	}
}
