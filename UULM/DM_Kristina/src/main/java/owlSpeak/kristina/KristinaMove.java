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
		
		HashSet<OWLNamedIndividual> indiColl = new HashSet<OWLNamedIndividual>();
		HashSet<OWLNamedIndividual> indis = new HashSet<OWLNamedIndividual>();
		indis.add((OWLNamedIndividual)indi);
		indiColl.add((OWLNamedIndividual)indi);
		do{
			HashSet<OWLNamedIndividual> tmpColl = new HashSet<OWLNamedIndividual>();
			for(OWLIndividual i: indis){
				tmpColl.addAll(findRelatedIndividuals(i,srcOnto));
			}
			indis = tmpColl;
			indis.removeAll(indiColl);
			indiColl.addAll(indis);
		}while(!indis.isEmpty());
		
		Set<OWLClass> topics = getClasses(indiColl, srcOnto);
		
		return topics;
	}
	
	public OWLClass getDialogueAct(){
		OWLOntology srcOnto = findSrcOntology();
		assert srcOnto != null: "Found no declaration for individual "+this;
		
		Set<OWLClassExpression> classes = indi.getTypes(srcOnto);
		OWLClassExpression dialogueAct = classes.iterator().next();
		return (OWLClass)dialogueAct;
	}
	
	@Override
	public String toString(){
		OWLClass dialogueAct = getDialogueAct();

		if(!findSrcOntology().equals(onto)){

		String result = dialogueAct.toStringID().split("\\#")[1];
		Set<OWLClass> topics = getTopics();

		if (topics.size()> 0){
			result = result + ": ";
			String top = "";
			for(OWLClass cl: topics){
				top = top + ", "+cl.toStringID().split("\\#")[1];
			}
			result = result + top.substring(2);
		}

		return result;
		}else{
			return getLocalName();
		}
	}
	
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
	
	private Set<OWLNamedIndividual> findRelatedIndividuals(OWLIndividual i, OWLOntology srcOnto){

		Set<OWLAxiom> axioms = ((OWLEntity) i).getReferencingAxioms(srcOnto);
		HashSet<OWLNamedIndividual> indis = new HashSet<OWLNamedIndividual>();
		for(OWLAxiom a: axioms){
			indis.addAll(a.getIndividualsInSignature());
		}
		return indis;
	}
	
	private Set<OWLClass> getClasses(Set<OWLNamedIndividual> indis, OWLOntology o){
		HashSet<OWLClass> classes = new HashSet<OWLClass>();
		for(OWLNamedIndividual i: indis){
			classes.add((OWLClass)i.getTypes(o).iterator().next());
		}
		return classes;
	}
}