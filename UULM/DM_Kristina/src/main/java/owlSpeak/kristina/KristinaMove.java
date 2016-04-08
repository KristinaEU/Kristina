package owlSpeak.kristina;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import owlSpeak.Move;

/**
 *
 * @author lpragst
 */
public class KristinaMove extends Move{
	
	
	
	public KristinaMove(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory,
			OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
		
	}
	
	public Set<OWLAxiom> getAxioms(){
		OWLOntology srcOnto = findSrcOntology();
		assert srcOnto != null: "Found no declaration for individual "+this;

		Set<OWLAxiom> axioms = ((OWLEntity) indi).getReferencingAxioms(srcOnto);
		return axioms;
	}
	
	public Set<OWLClass> getTopics(){
		OWLOntology srcOnto = findSrcOntology();
		assert srcOnto != null: "Found no declaration for individual "+this;
		
		HashSet<OWLClass> topics = new HashSet<OWLClass>();
		
		return topics;
	}
	
	public OWLClass getDialogueAct(){
		OWLOntology srcOnto = findSrcOntology();
		assert srcOnto != null: "Found no declaration for individual "+this;
		
		Set<OWLClassExpression> classes = indi.getTypes(srcOnto);
		OWLClassExpression dialogueAct = classes.iterator().next();
		return (OWLClass)dialogueAct;
	}
	
	/*@Override
	public String toString(){
		String result = getDialogueAct().toString();
		Set<OWLClass> topics = getTopics();
		if (topics.size()> 0){
			result = result + ": ";
			for(OWLClass cl: topics){
				result = result + cl.toString();
			}
		}
		return result;
	}*/
	
	private OWLOntology findSrcOntology(){
		Set<OWLOntology> allOntos = manager.getOntologies();
		OWLAxiom decl = factory.getOWLDeclarationAxiom((OWLNamedIndividual) indi);
		for(OWLOntology srcOnto: allOntos){
			if(srcOnto.containsAxiom(decl)){
				return srcOnto;
			}
		}
		return null;
	}
}