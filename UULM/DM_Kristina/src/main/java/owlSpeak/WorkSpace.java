package owlSpeak;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
/**
 * The class that represents the Workspaces.
 * @see Agenda
 * @author Tobias Heinroth.
 */
public class WorkSpace extends Agenda {
	/**
	 * the name of the WorkSpace Class	
	 */
	public final String name = "WorkSpace";
	/**
	 * creates an WorkSpace object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public WorkSpace(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}	
}
