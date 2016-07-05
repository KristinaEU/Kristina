package owlSpeak.engine;

import java.util.Iterator;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLOntology;

import owlSpeak.Agenda;
import owlSpeak.Move;
/**
 * owlSpeak Class RespawnManager allows access to multiple Respawn and encapsulates reading / writing 
 * 
 * @author Dan Denich
 * @version 1.0
 * @see CoreMove
 */
public class RespawnManager {
	/**
	 * a Vector of CoreMoves that contains the respawns
	 */
	Vector <CoreMove> respawns;
	
	RespawnManager(){
		respawns = new Vector<CoreMove>();
	}
	
	/**
	 * Adds all respawns of the given factory to the respawns-Vector
	 * @param onto the OwlSpeakOntology from which the respawns should be taken
	 * @param systemCore the Core object
	 */
	void addRespawns(OwlSpeakOntology onto, Core systemCore){
		Agenda tempAgenda;
		Iterator <Move> MoveIt = null;
		Iterator <Agenda> AgIt = onto.factory.getAllAgendaInstances().iterator();
		while (AgIt.hasNext()) {
			tempAgenda = AgIt.next();
			if (tempAgenda.getRespawn()){
				MoveIt=tempAgenda.getHas().iterator();
				while(MoveIt.hasNext()){
				  	respawns.add(new CoreMove(MoveIt.next(), tempAgenda, onto, systemCore));
				}
			}
		}
	}
	
	/**
	 * Clears the respawns-Vector
	 */
	void clear(){
		this.respawns=new Vector<CoreMove>();
	}
	
	/**
	 * Clears the respawns-Vector and adds all respawns of the ontologies of the core 
	 * @param core the ontology core that contains all ontologies
	 * @param user the user's name
	 */
	void updateRespawns(Core core, String user){
		respawns.clear();
		Iterator<OwlSpeakOntology> ontos = core.ontologies.iterator();
		while(ontos.hasNext()){
			OwlSpeakOntology onto = ontos.next();
			if (onto.isenabled(Settings.getuserpos(user)))	addRespawns(onto, core);
		}
	}
	
	/**
	 * returns a Vector with all current active Respawns
	 * @param user the user's name
	 * @return  a Vector with all current active Respawns  
	 */
    public Vector <CoreMove> getRespawns(Boolean work, String user){
	   Vector <CoreMove> Ausgabe = new Vector<CoreMove>();
	   Iterator <CoreMove> respawnIt = respawns.iterator();
	   CoreMove tempRes;
	   while (respawnIt.hasNext()){
		   tempRes=respawnIt.next();
		   tempRes.conflict = new Vector<String>();
		   if(tempRes.checkRespawn(user, tempRes.onto.getBeliefSpace(Settings.getuserpos(user)))) Ausgabe.add(tempRes);
	   }
	   if(work){
		   Vector <String> conflict= new Vector<String>();
		   for (int i=0; i<Ausgabe.size(); i++){
			   tempRes=Ausgabe.get(i);
			   for (int y=0; y<Ausgabe.size(); y++){
				   if(i==y) continue; 
				   if(tempRes.core.settings.getusersetting(user).docType==3){
					   //TODO
					   //conflict = owlSpeak.util.StringTest.checkGRXML(new Vector<DefaultRDFSLiteral>(tempRes.move.getGrammar().getGrammarString()), new Vector<DefaultRDFSLiteral>(Ausgabe.get(y).move.getGrammar().getGrammarString()));
				   }
				   else{
					   if(!tempRes.move.getFullName().equals(Ausgabe.get(y).move.getFullName())){					
						   conflict = owlSpeak.util.StringTest.checkGrammar(tempRes.move.getGrammar().getGrammarString().iterator(), Ausgabe.get(y).move.getGrammar().getGrammarString().iterator());
					   }
				   }
				   tempRes.conflict.addAll(conflict);
			   }
		   }
	   }
	   return Ausgabe;
    }
    
    /**
     * searches the respawns Vector for a move that has the given name  and is in the given ontology
     * @param move the Move that probably is a respawn
     * @param onto the Ontology that probably provides the respawn move
     * @return the Respawn that has a move of which the full name is equal to the requested moves's full name
     */
    CoreMove matchRespawn(Move move, OWLOntology onto){
    	CoreMove ausgabe = null;
    	CoreMove tempRes;
    	Iterator <CoreMove> respawnIt = respawns.iterator();
    	while (respawnIt.hasNext()){
    		tempRes=respawnIt.next();
    		if(tempRes.move.getFullName().equals(move.getFullName())  && tempRes.onto.factory.onto.getOntologyID().getOntologyIRI().equals(onto.getOntologyID().getOntologyIRI())   ){
    			return tempRes;
    		}
    	}
	    return ausgabe;
    }
	
    /**
     * Returns all Respawns of a specified ontology
     * @param onto the name of the {@link OWLOntology}
     * @param user the user's name 
     * @return a vector of CoreMoves containing the respawns
     */
    public Vector<CoreMove> getOntoRespawns(Boolean work, String onto, String user){
    	Vector<CoreMove> ausgabe = new Vector<CoreMove>(); 
    	Vector<CoreMove> temp = getRespawns(work,user);
    	for (int i = 0; i<temp.size(); i++){
    		if(temp.get(i).onto.Name.equals(onto)) ausgabe.add(temp.get(i));
    	}
    	return ausgabe;
    }
    
    
    /**
     * calculates the conflicts of the current respawns
     * @param conflict the potential conflict that should be checked
     * @return a vector of CoreMoves that have the same conflict-potential
     */
    public Vector <CoreMove> getConflicts(String conflict){
    Vector <CoreMove> Ausgabe = new Vector <CoreMove>();
    for (int i=0; i<respawns.size(); i++){
    	if(respawns.get(i).conflict.contains(conflict)) Ausgabe.add(respawns.get(i));
    }
    return Ausgabe;
    }
}
