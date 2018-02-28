package owlSpeak.kristina;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Statement;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.GenericProvider;
import owlSpeak.Move;

/**
 *
 * @author lpragst
 */
public class KristinaMove extends Move {

	public static final String name = "KristinaMove";

	private static final String textProperty = "text";
	private static final String plausibilityProperty = "plausibility";

	public KristinaMove(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}
	
	public boolean getIsNonPersistent(){
    	return GenericProvider.getBooleanProperty(indi, GenericProvider.nonPersistentProp, onto, factory);
    }
	
	public void setIsNonPersistent(boolean val){
    	GenericProvider.addBooleanData(indi, val, GenericProvider.nonPersistentProp, onto, factory, manager);
    }
	
	public boolean getIsLastUserMove(){
		return GenericProvider.getBooleanProperty(indi, GenericProvider.isLastUserMoveProp, onto, factory);
	}
	
	public void setIsLastUserMove(boolean value){
		GenericProvider.removeBooleanData(indi, getIsLastUserMove(), GenericProvider.isLastUserMoveProp, onto, factory, manager);
		GenericProvider.addBooleanData(indi, value, GenericProvider.isLastUserMoveProp, onto, factory, manager);
	}

	public boolean hasTopic(String topic) {
		return SemanticsOntology.hasTopic(indi.asOWLNamedIndividual().getIRI(), topic);
	}


	public boolean hasNegatedTopic(String topic) {
		return SemanticsOntology.hasNegatedTopic(indi.asOWLNamedIndividual().getIRI(), topic);
	}

	public void specify(String type) {
		SemanticsOntology.specifyType(indi.asOWLNamedIndividual().getIRI(), type);
	}

	public List<String> getRDFType() {
		return SemanticsOntology.getType(indi.asOWLNamedIndividual().getIRI());
	}

	public float getConfidence() {
		return SemanticsOntology.getConfidence(indi.asOWLNamedIndividual().getIRI());
	}

	public String getSpeak() {
		return SemanticsOntology.getSpeak(indi.asOWLNamedIndividual().getIRI());
	}

	public Model getModel() {
		return SemanticsOntology.getModel(this.indi.asOWLNamedIndividual().getIRI());
	}

	public Set<String> getNegatedTopics() {
		return SemanticsOntology.getNegatedTopics(indi.asOWLNamedIndividual().getIRI());
	}
	
	public Set<String> getTopics() {
		return SemanticsOntology.getTopics(indi.asOWLNamedIndividual().getIRI());
	}
	
	public int getNrWords(){
		return getSpeak().split("\\s|[^(\\p{L})]").length;
	}

	@Override
	public String toString() {
		List<String> action = getRDFType();
		String result = action.stream().map(s -> s.substring(s.indexOf('#') + 1)).collect(Collectors.joining("/"));
		Iterator<String> topics = getTopics().iterator();
		if (topics != null && topics.hasNext()) {
			result = result + ": ";
			String tmp = topics.next();
			if(hasNegatedTopic(tmp)){
				result = result+"!";
			}
			tmp = tmp.substring(tmp.indexOf('#') + 1);
			result = result + tmp;
			while (topics.hasNext()) {
				result = result + "/";
				tmp = topics.next();
				if(hasNegatedTopic(tmp)){
					result = result+"!";
				}
				tmp = tmp.substring(tmp.indexOf('#') + 1);
				result = result + tmp;
			}
		}
		return result;
	}
}