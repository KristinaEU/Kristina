package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * This class encapsulates a SemanticGroup OWL class
 * 
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @author Karim
 */
public class SemanticGroup extends Semantic {

	public final String name = "SemanticGroup";

	public SemanticGroup(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}

	public Collection<Semantic> getContainedSemantics() {
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(
				indi, GenericProvider.containsSemProp, onto, factory, manager)
				.iterator();
		Collection<Semantic> coll = new LinkedList<Semantic>();
		while (genCollIt.hasNext()) {
			coll.add(genCollIt.next().asSemantic());
		}
		return coll;
	}

	/**
	 * adds a Semantic to the ContainsSemantic field of the SemanticGroup.
	 * 
	 * @param newSemantic
	 *            the Semantic that should be added.
	 */
	public void addContainedSemantic(Semantic newSemantic) {
		GenericProvider.addIndividual(indi, newSemantic.indi,
				GenericProvider.containsSemProp, onto, factory, manager);

	}

	public static boolean isSemanticGroup(GenericClass o, OSFactory osFactory) {
		// if (osFactory.get)
		return false;
	}

	// newly added
	public void addSemanticGroupString(OWLLiteral newSemanticString) {
		GenericProvider.addOWLLiteralProperty(indi, newSemanticString,
				GenericProvider.semGroupProp, onto, factory, manager);
	}

	/**
	 * returns the Variable which is associated to this SemanticGroup.
	 * 
	 * @return Variable object.
	 */
	public Variable getVariable() {
		if (!hasVariable())
			return null;
		GenericClass gen = GenericProvider.getIndividual(indi,
				GenericProvider.hasVariableProp, onto, factory, manager);
		return gen.asVariable();
	}

	/**
	 * returns true if the hasVariable field contains one item.
	 * 
	 * @return true if hasVariable is not empty.
	 */
	public boolean hasVariable() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.hasVariableProp, onto, factory);
	}

	/**
	 * adds a variable to the hasVariable field of the SemanticGroup.
	 * 
	 * @param newVar
	 *            the variable that should be added.
	 */
	public void addVariable(Variable newVar) {
		GenericProvider.addIndividual(indi, newVar.indi,
				GenericProvider.hasVariableProp, onto, factory, manager);
	}

	public int getFieldTotals() {
		return GenericProvider.getIntProperty(indi,
				GenericProvider.fieldTotalsProp, onto, factory);
	}

	public boolean hastFieldTotals() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.fieldTotalsProp, onto, factory);
	}

	/**
	 * removes the entry of fieldTotals. Automatically takes the current value
	 * to be removed as this property is functional.
	 */
	public void removeFieldTotals() {
		GenericProvider.removeIntData(indi, getFieldTotals(),
				GenericProvider.fieldTotalsProp, onto, factory, manager);
	}

	/**
	 * sets the value of fieldTotals to value. Pre-existing values are first removed.
	 * @param value
	 */
	public void setFieldTotals(int value) {
		if (hastFieldTotals())
			removeFieldTotals();
		GenericProvider.addIntData(indi, value,
				GenericProvider.fieldTotalsProp, onto, factory, manager);
	}
}
