package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Reward;
/**
 * The class that represents the Agendas.
 * @author Tobias Heinroth.
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 */
public class Agenda extends GenericClass {
	/**
	 * the name of the Agenda Class	
	 */
	public final String name = "Agenda";
	/**
	 * creates an Agenda object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public Agenda(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}
	/**
	 * returns a collection of Moves that are linked in the Has field of the Agenda.
	 * @return the collection of Moves.
	 */
	public Collection<Move> getHas(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.hasProp, onto, factory,manager).iterator(); 
		Collection<Move> coll = new LinkedList<Move>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asMove());
		}
		return coll;
	}	
    /**
     * returns true if the Has field contains at least on item.
     * @return true if Has is not empty.
     */
	public boolean hasHas(){
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.hasProp, onto, factory);
    }   
    /**
     * adds a Move to the Has field of the Agenda.
     * @param newHas the Move that should be added.
     */
	public void addHas(Move newHas){
    	GenericProvider.addIndividual(indi, newHas.indi, GenericProvider.hasProp, onto, factory, manager);
    }
    /**
     * removes a Move from the Has field of the Agenda.
     * @param oldHas the Move that should be removed.
     */
	public void removeHas(Move oldHas){
    	GenericProvider.removeIndividual(indi, oldHas.indi, GenericProvider.hasProp, onto, factory, manager);
    }
    /**
     * replaces the specified Move in the Has field with the new Move.
     * @param oldHas the current Move.
     * @param newHas the Move that should replace the current Move.
     */
	public void setHas(Move oldHas, Move newHas){
    	removeHas(oldHas);
    	addHas(newHas);
    }
    /**
     * returns the value in the isMasterBool field.
     * This is used to specify the Agenda for initialization.
     * @return true if the Agenda is used as master.
     */
	public boolean getIsMasterBool(){
    	return GenericProvider.getBooleanProperty(indi, GenericProvider.masterProp, onto, factory);
    }
    /**
     * returns true if the IsMasterBool field contains any value (true or false).
     * @return true if IsMasterBool is not empty.
     */
    public boolean hasIsMasterBool(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.masterProp, onto, factory);
    }
    /**
     * removes the IsMasterBool field.
     * @param oldISMasterBool the Boolean value for IsMasterBool to be removed.
     */
    public void removeIsMasterBool(boolean oldISMasterBool){
    	GenericProvider.removeBooleanData(indi, oldISMasterBool, GenericProvider.masterProp, onto, factory, manager);
    }
    /**
     * sets the IsMasterBool field to the specified boolean.
     * @param oldISMasterBool the old Boolean value for IsMasterBool.
     * @param newIsMasterBool the Boolean containing the new value for IsMasterBool.
     */
    public void setIsMasterBool(boolean oldISMasterBool, boolean newIsMasterBool){
    	removeIsMasterBool(oldISMasterBool);
    	GenericProvider.addBooleanData(indi, newIsMasterBool, GenericProvider.masterProp, onto, factory, manager);
    }

    /**
     * returns a Collection of Agendas that are linked in the Next field of the Agenda.
     * @return the Collection of Agendas.
     */
    public Collection<Agenda> getNext(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.nextProp, onto, factory,manager).iterator(); 
		Collection<Agenda> coll = new LinkedList<Agenda>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asAgenda());
		}
		return coll;
    }
    /**
     * returns true if the Next field contains any items. 
     * @return true if Next is not empty.
     */
    public boolean hasNext(){
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.nextProp, onto, factory);
    }
    /**
     * adds an Agenda to the Next field of the Agenda.
     * @param newNext the Agenda that should be added.
     */
    public void addNext(Agenda newNext){
    	GenericProvider.addIndividual(indi, newNext.indi, GenericProvider.nextProp, onto, factory, manager);
    }
    /**
     * removes an Agenda from the Next field of the Agenda.
     * @param oldNext the Agenda that should be removed.
     */
    public void removeNext(Agenda oldNext){
    	GenericProvider.removeIndividual(indi, oldNext.indi, GenericProvider.nextProp, onto, factory, manager);
    }
    /**
     * replaces an Agenda in the Next field with the given Agenda.
     * @param oldNext the current Agenda.
     * @param newNext the new Agenda that should replace the current Agenda.
     */
    public void setNext(Agenda oldNext, Agenda newNext){
    	removeNext(oldNext);
    	addNext(newNext);
    }
    /**
     * returns the Priority of the Agenda.
     * @return the Priority of the Agenda.
     */
    public int getPriority(){
    	return GenericProvider.getIntProperty(indi, GenericProvider.prioProp, onto, factory);
    }
    /**
     * returns true if the Priority field contains a number. 
     * @return true if Priority is not empty.
     */
    public boolean hasPriority(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.prioProp, onto, factory);
    }
    /**
     * removes the Priority of the Agenda.
     * @param oldPriority the old Priority of the Agenda.
     */
    public void removePriority(int oldPriority){
    	GenericProvider.removeIntData(indi, oldPriority, GenericProvider.prioProp, onto, factory, manager);	
    }
    /**
     * sets the Priority of the Agenda to the specified value.
     * @param oldPriority the current Priority.
     * @param newPriority the new Priority of the Agenda.
     */
    public void setPriority(int oldPriority, int newPriority){
    	removePriority(oldPriority);
    	GenericProvider.addIntData(indi, newPriority, GenericProvider.prioProp, onto, factory, manager);
    }
    /**
     * returns the role of the Agenda out of "confirmation" and "collection"
     * @return the role of the Agenda.
     */
    public String getRole(){
    	if (hasRole())
    		return GenericProvider.getStringData(indi, GenericProvider.roleProp, onto, factory);
    	return "collection";
    }
    /**
     * returns true if the role field contains a string. 
     * @return true if role is not empty.
     */
    public boolean hasRole(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.roleProp, onto, factory);
    }
    /**
     * removes the role of the Agenda.
     * @param oldRole the old role of the Agenda.
     */
    public void removeRole(String oldRole){
    	GenericProvider.removeStringData(indi, oldRole, GenericProvider.roleProp, onto, factory, manager);	
    }
    /**
     * sets the role of the Agenda to the specified value.
     * @param oldRole the current role.
     * @param newRole the new role of the Agenda.
     */
    public void setRole(String oldRole, String newRole){
    	removeRole(oldRole);
    	GenericProvider.addStringData(indi, newRole, GenericProvider.roleProp, onto, factory, manager);
    }

    /**
     * returns a Collection of Semantics that are linked in the Requires field of the Agenda.
     * @return the Collection of Semantics.
     */
    public Collection<Semantic> getRequires(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.reqProp, onto, factory,manager).iterator(); 
		Collection<Semantic> coll = new LinkedList<Semantic>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asSemantic());
		}
		return coll;
    }
    /**
     * returns true if the Requires field contains at least on item.
     * @return true if Requires is not empty.
     */
    public boolean hasRequires(){
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.reqProp, onto, factory);
    }
    /**
     * adds a Semantic to the Requires field of the Agenda.
     * @param newRequires the Semantic that should be added.
     */
    public void addRequires(Semantic newRequires){
    	GenericProvider.addIndividual(indi, newRequires.indi, GenericProvider.reqProp, onto, factory, manager);
    }
    /**
     * removes a Semantic from the Requires field of the Agenda.
     * @param oldRequires the Semantic that should be removed.
     */
    public void removeRequires(Semantic oldRequires){
    	GenericProvider.removeIndividual(indi, oldRequires.indi, GenericProvider.reqProp, onto, factory, manager);
    }
    /**
     * replaces a Semantic in the Requires field with a new Semantic.
     * @param oldRequires the Semantic that should be replaced.
     * @param newRequires the new Semantic.
     */
    public void setRequires(Semantic oldRequires, Semantic newRequires){
    	removeRequires(oldRequires);
    	addRequires(newRequires);
    }    
    /**
     * returns the value in the Respawn field.
     * This is used to tag an Agenda as a Respawn.
     * @return the value in Respawn.
     */
    public boolean getRespawn(){
    	return GenericProvider.getBooleanProperty(indi, GenericProvider.respawnProp, onto, factory);
    }
    /**
     * returns true if the Respawn field contains any value (true or false).
     * @return true if Respawn is not empty.
     */
    public boolean hasRespawn(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.respawnProp, onto, factory);
    }
    /**
     * removes the Respawn field.
     * @param oldRespawn the Boolean value for Respawn to be removed.
     */
    public void removeRespawn(boolean oldRespawn){
    	GenericProvider.removeBooleanData(indi, oldRespawn, GenericProvider.respawnProp, onto, factory, manager);
    }
    /**
     * sets the Respawn field to the specified boolean.
     * @param oldRespawn the current Boolean.
     * @param newRespawn the Boolean containing the new value for Respawn.
     */
    public void setRespawn(boolean oldRespawn, boolean newRespawn){
    	removeIsMasterBool(oldRespawn);
    	GenericProvider.addBooleanData(indi, newRespawn, GenericProvider.respawnProp, onto, factory, manager);
    }
    /**
     * returns the String contained in the VariableOperator field.
     * This typically represents an OwlScript expression that can be evaluated.
     * @return the Variable-Operator String
     */
    public String getVariableOperator(){
    	return GenericProvider.getStringData(indi, GenericProvider.varProp, onto, factory);
    }
    /**
     * returns true if the VariableOperator field contains a String with a length of at least 1.
     * @return true if VariableOperator is not empty
     */
    public boolean hasVariableOperator(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.varProp, onto, factory);
    }
    /**
     * sets the VariableOperator field to the specified String.
     * @param oldVariableOperator the current String.
     * @param newVariableOperator the new String for VariableOperator.
     */
    public void setVariableOperator(String oldVariableOperator, String newVariableOperator){
    	GenericProvider.removeStringData(indi, oldVariableOperator, GenericProvider.varProp, onto, factory, manager);
    	GenericProvider.addStringData(indi, newVariableOperator, GenericProvider.varProp, onto, factory, manager);
    }
    /**
     * returns a Collection of Semantics that are linked in the MustNot field of the Agenda.
     * @return the Collection of Semantics.
     */
    public Collection<Semantic> getMustnot(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.mustNotProp, onto, factory,manager).iterator(); 
		Collection<Semantic> coll = new LinkedList<Semantic>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asSemantic());
		}
		return coll;
    }

    /**
     * returns true if the MustNot field contains at least on item.
     * @return true if MustNot is not empty.
     */
    public boolean hasMustnot(){
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.mustNotProp, onto, factory);
    }
    /**
     * adds a Semantic to the MustNot field of the Agenda.
     * @param newMustnot the Semantic that should be added.
     */
    public void addMustnot(Semantic newMustnot){
    	GenericProvider.addIndividual(indi, newMustnot.indi, GenericProvider.mustNotProp, onto, factory, manager);
    }
    /**
     * removes a Semantic from the Requires field of the Agenda.
     * @param oldMustnot the Semantic that should be removed.
     */
    public void removeMustnot(Semantic oldMustnot){
    	GenericProvider.removeIndividual(indi, oldMustnot.indi, GenericProvider.mustNotProp, onto, factory, manager);
    }
    /**
     * replaces the current Semantic in the Requires field with a new Semantic.
     * @param oldMustnot the current Semantic that should be replaced.
     * @param newMustnot the new Semantic.
     */
    public void setMustnot(Semantic oldMustnot, Semantic newMustnot){
    	removeMustnot(oldMustnot);
    	addMustnot(newMustnot);
    }
    public boolean hasReward() {
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.rewardProp, onto, factory);
    }
    public void setReward(Reward r) {
    	if (hasReward())
    		removeReward(r);
		addReward(r);
	}
    private void addReward(Reward r) {
    	GenericProvider.addIndividual(indi, r.indi, GenericProvider.rewardProp, onto, factory, manager);
		
	}
	private void removeReward(Reward r) {
		GenericProvider.removeIndividual(indi, r.indi, GenericProvider.rewardProp, onto, factory, manager);
	}
	public Reward getReward() {
		if (hasReward())
			return GenericProvider.getIndividual(indi, GenericProvider.rewardProp, onto, factory, manager).asReward();
		return null;
	}
	
	/**
     * verifies if the Semantic requirements of the Agenda are provided by the Semantic Vector.
     * @param setBeliefsCol a Vector of Semantics.
     * @return true the requirments are fulfilled.
     */
    public boolean matchRequires(Vector <Semantic> setBeliefsCol){    
        Semantic toMatch;
        Iterator <Semantic> reqIt = this.getRequires().iterator();
        while(reqIt.hasNext()){
        	if(setBeliefsCol.size()<=0) {
    			return false;
    		}
        	toMatch = reqIt.next();
        	for(int i = 0; i < setBeliefsCol.size(); i++){
        		if (setBeliefsCol.get(i).getFullName().equals(toMatch.getFullName() )) break;
        		if (i==setBeliefsCol.size()-1) {
        			return false;
        		}
        	}  	
        } 
    	return true;
    }

	/**
     * verifies if the Semantic mustNot requirements of the Agenda are not provided by the Semantic Vector.
     * @param setBeliefsCol a Vector of Semantics.
     * @return true the requirements are fulfilled.
     */
    public boolean matchMustNot(Vector <Semantic> setBeliefsCol){    
    	Iterator<Semantic> mustnot = null;
		try {
			mustnot = this.getMustnot().iterator();

		} catch (NullPointerException e) {
		}
		;
		boolean mustfound = false;
		try {
			while (mustnot.hasNext()) {
				if (mustnot.next().isMemberOf(setBeliefsCol))
					mustfound = true;
			}
		} catch (NullPointerException e) {
		}
		;
		return mustfound;
    }
    
    public boolean matchRequiresVariableOP(OwlSpeakOntology onto, String user){    
	    if (this.hasVariableOperator()) {
			String input = this.getVariableOperator();
			input = CoreMove.parseStringVariables(input, onto.factory , Core.getCore(),
					onto.getBeliefSpace(Settings.getuserpos(user)), user);
			String[] test = owlSpeak.engine.OwlScript
					.evaluateString(input);
			if ((test[0].equalsIgnoreCase("REQUIRES"))
					&& (!test[1].equals("1"))) {
				return false;
			}
		}
	    return true;
    }
    
    public void addSummaryAgenda (SummaryAgenda sumAgenda){
    	GenericProvider.addIndividual(indi, sumAgenda.indi, GenericProvider.summaryAgendaProp, onto, factory, manager);
    }
    
    public SummaryAgenda getSummaryAgenda() {
    	GenericClass obj = GenericProvider.getIndividual(indi, GenericProvider.summaryAgendaProp, onto, factory, manager);
    	if (obj == null)
    		return null;
		return obj.asSummaryAgenda();
	}
    
    public boolean hasSummaryAgenda() {
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.summaryAgendaProp, onto, factory);
	}
    
    @Override
    public int compareTo(GenericClass c) {
    	if (c instanceof Agenda) {
    		int cmp = this.getPriority() - ((Agenda)c).getPriority();
    		if (cmp == 0)
    			return super.compareTo(c);
    		else
    			return cmp;
    	}
    	else 
    		return super.compareTo(c);
    }
}
