package owlSpeak;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
/**
 * The class that represents the Histories.
 * @author Tobias Heinroth.
 */
public class History extends GenericClass{
	/**
	 * the name of the History Class	
	 */
	public final String name = "History";
	/**
	 * creates a History object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public History(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}
	/**
	 * returns the Integer contained in the AddTime field of the History.
	 * @return the AddTime.
	 */
	public int getAddTime(){
		return GenericProvider.getIntProperty(indi, GenericProvider.addProp, onto, factory);
	}
    /**
     * returns true if the AddTime field contains a value.
     * @return true if AddTime is not empty.
     */
	public boolean hasAddTime(){
		return GenericProvider.hasDataProperty(indi, GenericProvider.addProp, onto, factory);
	}
    /**
     * removes the AddTime of the History.
     * @param oldAddTime the old time of the History.
     */
    public void removeAddTime(int oldAddTime){
    	GenericProvider.removeIntData(indi, oldAddTime, GenericProvider.addProp, onto, factory, manager);	
    }
    /**
     * sets the AddTime to the given Integer.
     * @param oldAddTime the old AddTime.
     * @param newAddTime the new AddTime.
     */
	public void setAddTime(int oldAddTime, int newAddTime){
		removeAddTime(oldAddTime);
    	GenericProvider.addIntData(indi, newAddTime, GenericProvider.addProp, onto, factory, manager);
    }

    /**
     * returns the Integer contained in the AgendaPriority field of the History.
     * @return the AgendaPriority.
     */
	public int getAgendaPriority(){
		return GenericProvider.getIntProperty(indi, GenericProvider.agPrioProp, onto, factory);
	}
    /**
     * returns true if the AgendaPriority field contains a value.
     * @return true if AgendaPriority is not empty.
     */
	public boolean hasAgendaPriority(){
		return GenericProvider.hasDataProperty(indi, GenericProvider.agPrioProp, onto, factory);
	}
    /**
     * removes the AgendaPriority of the History.
     * @param oldAgendaPriority the old priority of the History.
     */
    public void removeAgendaPriority(int oldAgendaPriority){
    	GenericProvider.removeIntData(indi, oldAgendaPriority, GenericProvider.agPrioProp, onto, factory, manager);	
    }
    /**
     * sets the AgendaPriority to the given Integer.
     * @param oldAgendaPriority the old AddTime.
     * @param newAgendaPriority the new AddTime.
     */
	public void setAgendaPriority(int oldAgendaPriority, int newAgendaPriority){
		removeAgendaPriority(oldAgendaPriority);
    	GenericProvider.addIntData(indi, newAgendaPriority, GenericProvider.agPrioProp, onto, factory, manager);
    }

    /**
     * returns the Agenda that belongs to the History object. 
     * @return the Agenda of  the History. 
     */
	public Agenda getForAgenda(){
		return GenericProvider.getIndividual(indi, GenericProvider.forAgProp, onto, factory, manager).asAgenda();
	}
    /**
     * returns true if the forAgenda field contains an Agenda.
     * @return true if forAgenda is not empty.
     */
	public boolean hasForAgenda(){
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.forAgProp, onto, factory);
    }
    /**
     * sets the forAgenda to the given Agenda.
     * @param oldForAgenda the old Agenda.
     * @param newForAgenda the new Agenda.
     */
	public void setForAgenda(Agenda oldForAgenda, Agenda newForAgenda){
    	try{
    		GenericProvider.removeIndividual(indi, oldForAgenda.indi, GenericProvider.forAgProp, onto, factory, manager);
    	}catch(NullPointerException e){} 
    	GenericProvider.addIndividual(indi, newForAgenda.indi, GenericProvider.forAgProp, onto, factory, manager);
    }

    /**
     * returns the WorkSpace that belongs to the History object. 
     * @return the WorkSpace of the History. 
     */
	public WorkSpace getInWorkspace(){
		return GenericProvider.getIndividual(indi, GenericProvider.inWoProp, onto, factory, manager).asWorkSpace();
	}
    /**
     * returns true if the inWorkSpace field contains a WorkSpace.
     * @return true if inWorkSpace is not empty.
     */
	public boolean hasInWorkspace(){
    	return GenericProvider.hasObjectProperty(indi, GenericProvider.inWoProp, onto, factory);
    }
    /**
     * sets the inWorkSpace to the given WorkSpace.
     * @param oldInWorkspace the old WorkSpace.
     * @param newInWorkspace the new WorkSpace.
     */
	public void setInWorkspace(WorkSpace oldInWorkspace, WorkSpace newInWorkspace){
    	try{
    		GenericProvider.removeIndividual(indi, oldInWorkspace.indi, GenericProvider.inWoProp, onto, factory, manager);
    	}catch(NullPointerException e){} 	
    	GenericProvider.addIndividual(indi, newInWorkspace.indi, GenericProvider.inWoProp, onto, factory, manager);
	}

	/**
	 * returns the Integer contained in the ProcTime field of the History.
	 * @return the ProcTime.
	 */
	public int getProcTime(){
		return GenericProvider.getIntProperty(indi, GenericProvider.procProp, onto, factory);
	}
    /**
     * returns true if the ProcTime field contains a value.
     * @return true if ProcTime is not empty.
     */
	public boolean hasProcTime(){
		return GenericProvider.hasDataProperty(indi, GenericProvider.procProp, onto, factory);
	}
    /**
     * removes the ProcTime of the History.
     * @param oldProcTime the old processed time of the History.
     */
    public void removeProcTime(int oldProcTime){
    	GenericProvider.removeIntData(indi, oldProcTime, GenericProvider.procProp, onto, factory, manager);	
    }
    /**
     * sets the ProcTime to the given Integer.
     * @param oldProcTime the old ProcTime.
     * @param newProcTime the new ProcTime.
     */
	public void setProcTime(int oldProcTime, int newProcTime){
		removeProcTime(oldProcTime);
    	GenericProvider.addIntData(indi, newProcTime, GenericProvider.procProp, onto, factory, manager);
    }
}
