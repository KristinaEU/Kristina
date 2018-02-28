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
import owlSpeak.plugin.Keyword.RecWord;
import owlSpeak.servlet.document.OwlDocument;
import owlSpeak.servlet.OwlSpeakServlet;

/**
 * Class Keywordspotting: Plugin to handle a NOMATCH using Keywordspotting with Freetext-Grammar
 * @author Max Grotz
 * @version 1.0
 */
public class Spotting {
	
	/**
	 * this function is used to perform a keywordspotting with a grammar builded regarding the Keywords
	 * @param owlEngine the actual owlEngine
	 * @param user the actual User
	 * @param whereAmI the netadress of the servlet
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return the filled document
	 */
	public static OwlDocument performSpottingGrammar(ServletEngine owlEngine, String user, String whereAmI, String noInputCounter){
		
		String sOntoKeywordTemp = new String();
		String sMoveKeywordTemp = new String();
		String sFoundOntoKey = new String();
		String sFoundMoveKey = new String();
		Vector<RecWord> recVecOnto;
		Vector<RecWord> recVecMove;
		Vector<OntoKeyword> vOntoKeywords;
		Vector<MoveKeyword> vMoveKeywords = new Vector<MoveKeyword>();
		Vector<Result> vResult = new Vector<Result>();
		
		vOntoKeywords = KeywordLists.generateOntoKeywordsList(owlEngine, user);
		for (int i=0; i < vOntoKeywords.size(); i++){
			sOntoKeywordTemp = sOntoKeywordTemp + "#" + vOntoKeywords.get(i).keyword;	
		}
		if (sOntoKeywordTemp.startsWith("#")) sOntoKeywordTemp = sOntoKeywordTemp.substring(1); 
		recVecOnto = ExtRecog.outputRecWord("k", sOntoKeywordTemp); 
		if (recVecOnto.size() == 0){
			OwlDocument VDoc = owlEngine.buildRequest(whereAmI, user, noInputCounter);
			return VDoc;
		}
		
		// *** Looking for Matches in Onto-Keywords and over proper Move-Keywords ***
		
		for (int i=0; i < recVecOnto.size(); i++){
			for (int j=0; j < vOntoKeywords.size(); j++){
				boolean added = false;
				if (recVecOnto.elementAt(i).keyword.toLowerCase().contains(vOntoKeywords.elementAt(j).keyword.toLowerCase())){
					sFoundOntoKey = vOntoKeywords.elementAt(j).ontoName;
					vMoveKeywords = KeywordLists.generateMoveKeywordsList(owlEngine, user, sFoundOntoKey);
					for (int k=0; k < vMoveKeywords.size(); k++){
						sMoveKeywordTemp = sMoveKeywordTemp + "#" + vMoveKeywords.get(k).keyword;	
					}
					if(sMoveKeywordTemp.startsWith("#")) sMoveKeywordTemp = sMoveKeywordTemp.substring(1);
					recVecMove = ExtRecog.outputRecWord("k", sMoveKeywordTemp); 
					for (int k=0; k < recVecMove.size(); k++){
						for (int l=0; l < vMoveKeywords.size(); l++){
							Result rFound = new Result();
							if (recVecMove.elementAt(k).keyword.toLowerCase().contains(vMoveKeywords.elementAt(l).keyword.toLowerCase())){
								sFoundMoveKey = vMoveKeywords.elementAt(l).moveName;
							 	boolean bContains = false;
								for (int m = 0; m < vResult.size(); m++) {
									if ((vResult.elementAt(m).sFoundMoveKeyword.equalsIgnoreCase(sFoundMoveKey))) bContains = true;
								}
								if (!bContains){
									rFound.sFoundOntoKeyword = sFoundOntoKey;
									rFound.sFoundMoveKeyword = sFoundMoveKey;
									rFound.sRecordedOntoKeyword = recVecOnto.elementAt(i).keyword;
									rFound.sRecordedMoveKeyword = recVecMove.elementAt(k).keyword;
									vResult.add(rFound);
									added = true;
								}
							}
							
						}
						if (added) break;
					}
					if (added) break;
				}
			}
		}
		
		// *** Looking over the Move-Keywords of the actual Onto *** 
		if (vResult.isEmpty()){
			if(owlEngine.systemCore.actualOnto[owlSpeak.engine.Settings.getuserpos(user)]==null){
				OwlSpeakOntology actualOnto = owlEngine.systemCore.actualOnto[owlSpeak.engine.Settings.getuserpos(user)];
				vMoveKeywords = KeywordLists.generateMoveKeywordsList(owlEngine, user, actualOnto.Name);
				for (int i=0; i < vMoveKeywords.size(); i++){
					sMoveKeywordTemp = sMoveKeywordTemp + "#" + vMoveKeywords.get(i).keyword;	
				}
				if(sMoveKeywordTemp.startsWith("#")) sMoveKeywordTemp=sMoveKeywordTemp.substring(1);
				//System.out.println("übergebene Grama(allMove):" + sMoveKeywordTemp);
				recVecMove = ExtRecog.outputRecWord("k", sMoveKeywordTemp); 
				for (int i=0; i < recVecMove.size(); i++){
					for (int j=0; j < vMoveKeywords.size(); j++){
						Result rFound = new Result();
						if (recVecMove.elementAt(i).keyword.toLowerCase().contains(vMoveKeywords.elementAt(j).keyword.toLowerCase())){
							rFound.sFoundOntoKeyword = actualOnto.Name;
							rFound.sFoundMoveKeyword = vMoveKeywords.elementAt(j).moveName;
							rFound.sRecordedOntoKeyword = actualOnto.getOntoDomainName();
							rFound.sRecordedMoveKeyword = recVecMove.elementAt(i).keyword;
							vResult.add(rFound);
						}
					}
				}
			}
		}
		
		// *** possibility to add a further search for all moveKeywords in all Ontos
		
		// *** Logger - Output ***
		String log = "| ";
		for (int i = 0; i < vResult.size(); i++) {
			log = log + "Onto: " + vResult.elementAt(i).sFoundOntoKeyword + "  Move: " + vResult.elementAt(i).sFoundMoveKeyword + " | ";    
		} 
		OwlSpeakServlet.logger.logp(Level.INFO, "Keyword-Spotting: ", vResult.size()+" Matches found", log);
		
		// *** Creating answer-utterance in case of a Nomatch with the result of the Spotting ***
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
	
}

