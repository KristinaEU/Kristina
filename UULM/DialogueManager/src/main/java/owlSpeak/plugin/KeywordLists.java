package owlSpeak.plugin;

import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;

import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.DialogueDomain;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.WorkSpace;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.RespawnManager;
import owlSpeak.engine.ServletEngine;
import owlSpeak.plugin.Keyword.MoveKeyword;
import owlSpeak.plugin.Keyword.OntoKeyword;
import owlSpeak.servlet.OwlSpeakServlet;


/**
 * Class KeywordLists: Plugin to generate Keyword-Lists from actual context
 * @author Max Grotz
 * @version 1.0
 */
public class KeywordLists {

	 /**
	 * this function is used to generate a Keywordslist from Ontologies 
	 * @param owlEngine the actual owlEngine
	 * @param user the actual user
	 * @return Vector&lt;OntoKeyword&gt;
	 */
	public static Vector<OntoKeyword> generateOntoKeywordsList(ServletEngine owlEngine, String user){
		Vector<OntoKeyword> vOntoKeywords = new Vector<OntoKeyword>();
		Keyword x = new Keyword();
		for (int i=0; i<owlEngine.systemCore.ontologies.size(); i++){
			OwlSpeakOntology temp = owlEngine.systemCore.ontologies.elementAt(i);
			if (temp.isenabled(owlSpeak.engine.Settings.getuserpos(user))){
				Iterator <DialogueDomain> itDiDo = temp.factory.getAllDialogueDomainInstances().iterator();
				while (itDiDo.hasNext()){
					Iterator <OWLLiteral> itDoKeys = itDiDo.next().getDomainKeywords().iterator();
					while (itDoKeys.hasNext()){
						OntoKeyword okTemp = x.new OntoKeyword(itDoKeys.next().getLiteral().toLowerCase(), temp.Name, temp.getOntoDomainName());
						vOntoKeywords.add(okTemp);
					}
				}
			}	
		}
		
		// ************** CONSOLEN - AUSGABE: **************
		String log = "";
		System.out.println("******* Onto-Keywords *******");
    	for (int i=0; i < vOntoKeywords.size(); i++){
    		System.out.print(" " + vOntoKeywords.get(i).keyword);	
    		log = log + vOntoKeywords.get(i).keyword + " | "; 	
    	}
    	System.out.println();
    	OwlSpeakServlet.logger.logp(Level.INFO, "OntoKeywords: ", log, "");
		
		return vOntoKeywords;
	}
	
	/**
	 * this function is used to generate a Keywordslist from available moves depending on an ontolgy 
	 * @param owlEngine the actual owlEngine
	 * @param user the actual user
	 * @param ontoName the name of the ontology the moves are taken from
	 * @return Vector&lt;MoveKeyword&gt; with {@link MoveKeyword}s
	 */
	public static Vector<MoveKeyword> generateMoveKeywordsList(ServletEngine owlEngine, String user, String ontoName){
		Vector<MoveKeyword> vMoveKeywords = new Vector<MoveKeyword>();
		Keyword x = new Keyword();
		OwlSpeakOntology actualOnto = null;
		
		Iterator<OwlSpeakOntology> itOSOnto = owlEngine.systemCore.ontologies.iterator();
		while (itOSOnto.hasNext()){
			actualOnto = itOSOnto.next();
			if (actualOnto.Name.equalsIgnoreCase(ontoName)){
				break;
			}
		}
		
		int iUserpos = owlSpeak.engine.Settings.getuserpos(user);
		WorkSpace WS = owlEngine.systemCore.actualOnto[iUserpos].getWorks(iUserpos);
		BeliefSpace BS = owlEngine.systemCore.actualOnto[iUserpos].getBeliefSpace(iUserpos);
		OSFactory factory =  owlEngine.systemCore.actualOnto[iUserpos].factory;
		Agenda master = owlEngine.systemCore.actualOnto[iUserpos].getActualAgenda(iUserpos).get(0);
		
		Iterator <Agenda> itAg = owlEngine.systemCore.findAllPossAgendas(master, WS, BS, factory, user).iterator(); 
		RespawnManager rsm = owlEngine.systemCore.respawnManager[iUserpos];
		while (itAg.hasNext()){
			Agenda inWorkspace = itAg.next();		
			Iterator<Move> itMoves = inWorkspace.getHas().iterator();
			while (itMoves.hasNext()){
				Move temp = itMoves.next();
				Iterator<OWLLiteral> itLit = temp.getKeywordsString().iterator();
				while (itLit.hasNext()){
					MoveKeyword mkTemp = x.new MoveKeyword(itLit.next().getLiteral().toLowerCase(), temp.getLocalName(), ontoName, inWorkspace.getLocalName());
					vMoveKeywords.add(mkTemp);
				}
			}
		}
		Vector<CoreMove> respawns = rsm.getOntoRespawns(false, actualOnto.Name, user);
		for (int i = 0; i< respawns.size(); i++){
			Move temp = respawns.get(i).move;
			Iterator<OWLLiteral> itLit = temp.getKeywordsString().iterator();
			while (itLit.hasNext()){
				MoveKeyword mkTemp = x.new MoveKeyword(itLit.next().getLiteral().toLowerCase(), temp.getLocalName(), ontoName, respawns.get(i).agenda.getLocalName());
				vMoveKeywords.add(mkTemp);
			}
		}
		
		// ************** CONSOLEN - AUSGABE: **************
		String log = "";
		System.out.println("******* Move-Keywords *******");
    	for (int i=0; i < vMoveKeywords.size(); i++){
    		System.out.print(" " + vMoveKeywords.get(i).keyword);	
    		log = log + vMoveKeywords.get(i).keyword + " | "; 	
    	}
    	System.out.println();
    	OwlSpeakServlet.logger.logp(Level.INFO, "MoveKeywords: ", log, "");
    	
		return vMoveKeywords;
	}
	
	/**
	 * this function is used to generate a Keywordslist from available moves
	 * @param owlEngine the actual owlEngine
	 * @param user the actual user
	 * @return Vector&lt;MoveKeyword&gt; with {@link MoveKeyword}s
	 */
	public static Vector<MoveKeyword> generateMoveKeywordsListAllOnto(ServletEngine owlEngine, String user){
		Vector<MoveKeyword> vMoveKeywords = new Vector<MoveKeyword>();
		Keyword x = new Keyword();
		OwlSpeakOntology actualOnto = null;
		boolean bActualAgendaAdded = false;
		Iterator<OwlSpeakOntology> itOSOnto = owlEngine.systemCore.ontologies.iterator();

		int iUserpos = owlSpeak.engine.Settings.getuserpos(user);
		WorkSpace WS = owlEngine.systemCore.actualOnto[iUserpos].getWorks(iUserpos);
		BeliefSpace BS = owlEngine.systemCore.actualOnto[iUserpos].getBeliefSpace(iUserpos);
		OSFactory factory =  owlEngine.systemCore.actualOnto[iUserpos].factory;
		Agenda master = owlEngine.systemCore.actualOnto[iUserpos].getActualAgenda(iUserpos).get(0);
		Iterator <Agenda> itAg = owlEngine.systemCore.findAllPossAgendas(master, WS, BS, factory, user).iterator(); 
		RespawnManager rsm = owlEngine.systemCore.respawnManager[iUserpos];
		
		// *** Adding MoveKeywords from actual Agenda ***
		if (bActualAgendaAdded == false){ 
			Iterator<Move> itMovesMaster = master.getHas().iterator();
			while (itMovesMaster.hasNext()){
				
				Move temp = itMovesMaster.next();
				Iterator<OWLLiteral> itLit = temp.getKeywordsString().iterator();
				while (itLit.hasNext()){
					MoveKeyword mkTemp = x.new MoveKeyword(itLit.next().getLiteral().toLowerCase(), temp.getLocalName(), owlEngine.systemCore.actualOnto[iUserpos].Name, master.getLocalName());
					vMoveKeywords.add(mkTemp);
				}
			}
			bActualAgendaAdded = true;
		}
			
		while (itOSOnto.hasNext()){
				
			actualOnto = itOSOnto.next();		
			// *** Adding MoveKeywords from available Respawns ***
			Vector<CoreMove> respawns = rsm.getOntoRespawns(false, actualOnto.Name, user);
			for (int i = 0; i< respawns.size(); i++){
				Move temp = respawns.get(i).move;
				Iterator<OWLLiteral> itLit = temp.getKeywordsString().iterator();
				while (itLit.hasNext()){
					MoveKeyword mkTemp = x.new MoveKeyword(itLit.next().getLiteral().toLowerCase(), temp.getLocalName(), actualOnto.Name, respawns.get(i).agenda.getLocalName());
					if (!(vMoveKeywords.contains(mkTemp))){
						vMoveKeywords.add(mkTemp);
					}
				}
			}
		}
		
		// *** Adding MoveKeywords from available Agendas ***
		while (itAg.hasNext()){
			Agenda inWorkspace = itAg.next();		
			Iterator<Move> itMovesAvailable = inWorkspace.getHas().iterator();
			while (itMovesAvailable.hasNext()){
				Move temp = itMovesAvailable.next();
				Iterator<OWLLiteral> itLit = temp.getKeywordsString().iterator();
				while (itLit.hasNext()){
					MoveKeyword mkTemp = x.new MoveKeyword(itLit.next().getLiteral().toLowerCase(), temp.getLocalName(), actualOnto.Name, inWorkspace.getLocalName());
					if (!(vMoveKeywords.contains(mkTemp))){
						vMoveKeywords.add(mkTemp);
					}
				}
			}
		}
		
		// ************** CONSOLEN - AUSGABE: **************
		String log = "";
		System.out.println("******* Move-Keywords *******");
    	for (int i=0; i < vMoveKeywords.size(); i++){
    		System.out.print(" " + vMoveKeywords.get(i).keyword);	
    		log = log + vMoveKeywords.get(i).keyword + " | "; 	
    	}
    	System.out.println();

    	return vMoveKeywords;
	}
	
}
