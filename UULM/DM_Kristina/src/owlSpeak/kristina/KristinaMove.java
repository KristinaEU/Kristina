package owlSpeak.kristina;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Move;

/**
 *
 * @author lpragst
 */
public class KristinaMove extends Move{
	//TODO choose taxonomy
	public static enum DialogueAct {REQUEST,INFORM};
	
	private DialogueAct dialogueAct;
	
	private Set topics;
	
	public KristinaMove(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory,
			OWLOntologyManager manager, DialogueAct d, Set t) {
		super(indi, onto, factory, manager);
		dialogueAct = d;
		topics = t;
	}
}