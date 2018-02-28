package owlSpeak;

import java.util.Collections;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLEntityRemover;

import owlSpeak.engine.his.Reward;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.kristina.KristinaMove;

/**
 * The superclass of all owl types.
 * 
 * @author Tobias Heinroth.
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 */
public class GenericClass implements Comparable<GenericClass> {
	/**
	 * the OWLIndividual that is wrapped by the respective class.
	 */
	public OWLIndividual indi;
	/**
	 * the OWLOntology indi beloongs to.
	 */
	public OWLOntology onto;
	/**
	 * the OWLDataFactory the indi belongs to.
	 */
	public OWLDataFactory factory;
	/**
	 * the OWLOntologyManager that is used for the onto.
	 */
	public OWLOntologyManager manager;

	/**
	 * creates a GenericClass object
	 * 
	 * @param indi
	 *            the OWLIndividual that is contained
	 * @param onto
	 *            the OWLOntology indi belongs to
	 * @param factory
	 *            the OWLDataFactory which should be used
	 * @param manager
	 *            the OWLOntologyManger that mangages onto
	 */
	public GenericClass(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		this.indi = indi;
		this.onto = onto;
		this.factory = factory;
		this.manager = manager;
	}

	/**
	 * returns the local name of the indi.
	 * 
	 * @return a String with the local name of the indi.
	 */
	public String getLocalName() {
		String full = indi.toStringID();
		String[] a = full.split("\\#");
		return a[a.length - 1];
	}

	/**
	 * returns the full name of the indi.
	 * 
	 * @return a String with the full name (URI) of the indi.
	 */
	public String getFullName() {
		return indi.toStringID();
	}

	/**
	 * deletes the indi permanently from the onto.
	 */
	public void delete() {
		OWLEntityRemover remover = new OWLEntityRemover(manager,
				Collections.singleton(onto));
		indi.asOWLNamedIndividual().accept(remover);
		manager.applyChanges(remover.getChanges());
	}

	/**
	 * returns the GenericClass object as new Agenda Object.
	 * 
	 * @return a new Object of type Agenda.
	 */
	public Agenda asAgenda() {
		return new Agenda(indi, onto, factory, manager);
	}
	
	/**
	 * returns the GenericClass object as new SummaryAgenda Object.
	 * 
	 * @return a new Object of type SummaryAgenda.
	 */
	public SummaryAgenda asSummaryAgenda() {
		return new SummaryAgenda(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new Move Object.
	 * 
	 * @return a new Object of type Move.
	 */
	public Move asMove() {
		return new Move(indi, onto, factory, manager);
	}
	
	/**
	 * returns the GenericClass object as new KristinaMove Object.
	 * 
	 * @return a new Object of type KristinaMove.
	 */
	public KristinaMove asKristinaMove() {
		return new KristinaMove(indi, onto, factory, manager);
	}
	
	/**
	 * returns the GenericClass object as new KristinaAgenda Object.
	 * 
	 * @return a new Object of type KristinaAgenda.
	 */
	public KristinaAgenda asKristinaAgenda() {
		return new KristinaAgenda(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new Belief Object.
	 * 
	 * @return a new Object of type Belief.
	 */
	public Belief asBelief() {
		return new Belief(indi, onto, factory, manager);
	}
	
	/**
	 * returns the GenericClass object as new SummaryBelief Object.
	 * 
	 * @return a new Object of type SummaryBelief.
	 */
	public SummaryBelief asSummaryBelief() {
		return new SummaryBelief(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new Semantic Object.
	 * 
	 * @return a new Object of type Semantic.
	 */
	public Semantic asSemantic() {
		return new Semantic(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new Grammar Object.
	 * 
	 * @return a new Object of type Grammar.
	 */
	public Grammar asGrammar() {
		return new Grammar(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new Utterance Object.
	 * 
	 * @return a new Object of type Utterance.
	 */
	public Utterance asUtterance() {
		return new Utterance(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new WorkSpace Object.
	 * 
	 * @return a new Object of type WorkSpace.
	 */
	public WorkSpace asWorkSpace() {
		return new WorkSpace(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new Variable Object.
	 * 
	 * @return a new Object of type Variable.
	 */
	public Variable asVariable() {
		return new Variable(indi, onto, factory, manager);
	}

	/**
	 * returns the GenericClass object as new SemanticGroup Object.
	 * 
	 * @return a new Object of type Variable.
	 */
	public SemanticGroup asSemanticGroup() {
		return new SemanticGroup(indi, onto, factory, manager);
	}
	
	/**
	 * returns the GenericClass object as new SemanticGroup Object.
	 * 
	 * @return a new Object of type Variable.
	 */
	public Reward asReward() {
		return new Reward(indi, onto, factory, manager);
	}

	/**
	 * Returns the local name as standard string representation for all
	 * derivatices.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return getLocalName();
	}

	/**
	 * Returns true if the IRI of c is equal to the iri of this object.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object c) {
		if (c instanceof GenericClass) {
			return ((GenericClass) c).getFullName().equalsIgnoreCase(
					this.getFullName());
		}
		return false;
	}

	/**
	 * return 0 if equals returns true, else compares the iri of c with the iri
	 * of this object and returns its value.
	 */
	@Override
	public int compareTo(GenericClass c) {
		if (this.equals(c))
			return 0;

		return this.getFullName().compareTo(((GenericClass) c).getFullName());
	}
}
