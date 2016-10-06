package owlSpeak.kristina;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.DialogueAction;
import model.KIConverter;
import model.KristinaModel;
import model.SemanticsOntology;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Statement;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import owlSpeak.Move;

/**
 *
 * @author lpragst
 */
public class KristinaMove extends Move {

	private static final String textProperty = "text";

	public KristinaMove(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);

	}

	public List<ReifiedStatement> getStatements() {
		return SemanticsOntology.getStatements(indi.asOWLNamedIndividual()
				.getIRI());
	}

	public String getText() {
		String text = SemanticsOntology.getProperty(indi.asOWLNamedIndividual()
				.getIRI(), textProperty);
		return text == null ? "" : text;
	}

	public boolean hasTopic(String topic) {
		if (this.isDialogueAction(DialogueAction.ADVISE)
				|| this.isDialogueAction(DialogueAction.DECLARE)
				|| this.isDialogueAction(DialogueAction.ORDER)
				|| this.isDialogueAction(DialogueAction.OBLIGATE)
				|| this.isDialogueAction(DialogueAction.STATEMENT)
				|| this.isDialogueAction(DialogueAction.REQUEST)
				|| this.isDialogueAction(DialogueAction.REQUEST_ADDITIONAL)
				|| this.isDialogueAction(DialogueAction.REQUEST_MISSING)
				|| this.isDialogueAction(DialogueAction.BOOL_REQUEST)
				|| this.isDialogueAction(DialogueAction.ACKNOWLEDGE)) {
			return SemanticsOntology.hasTopic(indi.asOWLNamedIndividual()
					.getIRI(), topic);
		}
		return false;
	}
	
	public boolean hasNegatedTopic(String topic) {
		if (this.isDialogueAction(DialogueAction.ADVISE)
				|| this.isDialogueAction(DialogueAction.DECLARE)
				|| this.isDialogueAction(DialogueAction.ORDER)
				|| this.isDialogueAction(DialogueAction.OBLIGATE)
				|| this.isDialogueAction(DialogueAction.STATEMENT)
				|| this.isDialogueAction(DialogueAction.REQUEST)
				|| this.isDialogueAction(DialogueAction.REQUEST_ADDITIONAL)
				|| this.isDialogueAction(DialogueAction.REQUEST_MISSING)
				|| this.isDialogueAction(DialogueAction.BOOL_REQUEST)) {
			return SemanticsOntology.hasNegatedTopic(indi.asOWLNamedIndividual()
					.getIRI(), topic);
		}
		return false;
	}

	public void specify(String type) {
		SemanticsOntology.specifyType(indi.asOWLNamedIndividual().getIRI(),
				type);
	}

	public String getDialogueAction() {
		return indi.getTypes(onto).iterator().next().asOWLClass().getIRI()
				.toString();
	}

	public String getRDFType() {
		return SemanticsOntology.getType(indi.asOWLNamedIndividual().getIRI());
	}

	public boolean isDialogueAction(String a) {
		return this.getDialogueAction().equals(a);
	}

	public float getConfidence() {
		return SemanticsOntology.getConfidence(indi.asOWLNamedIndividual()
				.getIRI());
	}

	

	public Model getModel() {
		return SemanticsOntology.getModel(this.indi.asOWLNamedIndividual()
				.getIRI());
	}

	

	public Set<String> getTopics() {
		if (this.isDialogueAction(DialogueAction.ADVISE)
				|| this.isDialogueAction(DialogueAction.DECLARE)
				|| this.isDialogueAction(DialogueAction.ORDER)
				|| this.isDialogueAction(DialogueAction.OBLIGATE)
				|| this.isDialogueAction(DialogueAction.STATEMENT)
				|| this.isDialogueAction(DialogueAction.REQUEST)
				|| this.isDialogueAction(DialogueAction.REQUEST_ADDITIONAL)
				|| this.isDialogueAction(DialogueAction.REQUEST_MISSING)
				|| this.isDialogueAction(DialogueAction.BOOL_REQUEST)) {
			return SemanticsOntology.getTopics(indi.asOWLNamedIndividual().getIRI());
		}
		return null;
	}

	@Override
	public String toString() {
		String action = getDialogueAction();
		String result = action.substring(action.indexOf('#')+1);
		if (this.isDialogueAction(DialogueAction.ADVISE)
				|| this.isDialogueAction(DialogueAction.DECLARE)
				|| this.isDialogueAction(DialogueAction.ORDER)
				|| this.isDialogueAction(DialogueAction.OBLIGATE)
				|| this.isDialogueAction(DialogueAction.STATEMENT)
				|| this.isDialogueAction(DialogueAction.REQUEST)
				|| this.isDialogueAction(DialogueAction.REQUEST_ADDITIONAL)
				|| this.isDialogueAction(DialogueAction.REQUEST_MISSING)
				|| this.isDialogueAction(DialogueAction.BOOL_REQUEST)) {
			Iterator<String> topics = getTopics().iterator();
			if(topics != null && topics.hasNext()){
				String tmp = topics.next();
				tmp = tmp.substring(tmp.indexOf('#')+1);
				result = result+": "+tmp;
				while(topics.hasNext()){
					tmp = topics.next();
					tmp = tmp.substring(tmp.indexOf('#')+1);
					result = result+", "+tmp;
				}
			}
		}else if(this.isDialogueAction(DialogueAction.CANNED)){
			result = result+": "+getText();
		}
		return result;
	}
	
	@Override
	//TODO: works only for moves from KI, not LA
	public boolean equals(Object o){
		if(o instanceof KristinaMove){
			KristinaMove m = (KristinaMove) o;
			if(m.getDialogueAction().equals(getDialogueAction())){
				if(m.getText().equals(getText())){
					List<ReifiedStatement> stmts1 = this.getStatements();
					List<ReifiedStatement> stmts2 = m.getStatements();
					boolean equal = true;
					for(ReifiedStatement stmt1: stmts1){
						Statement s1 = stmt1.getStatement();
						boolean foundMatch = false;
						for(ReifiedStatement stmt2: stmts2){
							Statement s2 = stmt2.getStatement();
							if(s1.getSubject().equals(s2.getSubject()) && s1.getObject().equals(s2.getObject()) && s1.getPredicate().equals(s2.getPredicate())){
								foundMatch = true;
								break;
							}
						}
						if(!foundMatch){
							equal =false;
							break;
						}
					}
					if(equal){
						return true;
					}
				}
			}
		}
		return false;
	}
}