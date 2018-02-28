package owlSpeak;

import java.util.Vector;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The class that represents the Variables.
 * 
 * @author Tobias Heinroth.
 */
public class Variable extends GenericClass {
	/**
	 * the name of the Variable Class
	 */
	public final String name = "Variable";

	/**
	 * creates a Variable object by calling super
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
	public Variable(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}

	/**
	 * returns the String in the DefaultValue field of the Variable.
	 * 
	 * @return the String contained in the DefaultValue.
	 */
	public String getDefaultValue() {
		try {
			return GenericProvider.getStringData(indi,
					GenericProvider.varDefValProp, onto, factory);
		} catch (Exception e) {
			return "pla";
		}
	}

	/**
	 * returns true if the DefaultValue field contains a String with a length
	 * &gt;= 1.
	 * 
	 * @return true if DefaultValue is not empty.
	 */
	public boolean hasDefaultValue() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.varDefValProp, onto, factory);
	}

	/**
	 * replaces the String in the UtteranceString field with the new String.
	 * 
	 * @param oldDefaultValue
	 *            the old String that should be replaced by the new String.
	 * @param newDefaultValue
	 *            the new String.
	 */
	public void setDefaultValue(String oldDefaultValue, String newDefaultValue) {
		GenericProvider.removeStringData(indi, oldDefaultValue,
				GenericProvider.varDefValProp, onto, factory, manager);
		GenericProvider.addStringData(indi, newDefaultValue,
				GenericProvider.varDefValProp, onto, factory, manager);
	}
	
	/**
	 * adds a String to the UtteranceString field.
	 * 
	 * @param newDefaultValue
	 *            the new String.
	 */
	public void setFirstDefaultValue(String newDefaultValue) {
		GenericProvider.addStringData(indi, newDefaultValue,
				GenericProvider.varDefValProp, onto, factory, manager);
	}

	/**
	 * returns the Semantic in the belongsToSemantic field of the variable.
	 * 
	 * @return the semantic this variable belongs to
	 */
	public SemanticGroup getBelongsToSemantic() {
		return GenericProvider.getIndividual(indi,
				GenericProvider.belongsToSemantic, onto, factory, manager)
				.asSemanticGroup();
	}

	/**
	 * adds a Semantic to the belongsToSemantic field of the variable.
	 * 
	 * @param newSem
	 *            the semantic that should be added.
	 */
	public void addBelongsToSemantic(SemanticGroup newSem) {
		if (hasBelongsToSemantic())
			removeBelongsToSemantic(getBelongsToSemantic());
		GenericProvider.addIndividual(indi, newSem.indi,
				GenericProvider.belongsToSemantic, onto, factory, manager);
	}

	/**
	 * remove a Semantic from the belongsToSemantic field of the variable.
	 * 
	 * @param oldSem
	 *            the semantic that should be removed.
	 */
	public void removeBelongsToSemantic(SemanticGroup oldSem) {
		GenericProvider.removeIndividual(indi, getBelongsToSemantic().indi,
				GenericProvider.belongsToSemantic, onto, factory, manager);
	}

	/**
	 * returns boolean indicating if the belongsToSemantic field contains
	 * something
	 * 
	 * @return true iff the belongsToSemantic field contains a Variable
	 */
	public boolean hasBelongsToSemantic() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.belongsToSemantic, onto, factory);
	}

	/**
	 * adds a Semantic to the belongsToSemantic field of the variable.
	 * 
	 * @param oldSem
	 *            the old semantic contained in belongsToField
	 * @param newSem
	 *            the semantic that should be added.
	 */
	public void setBelongsToSemantic(SemanticGroup oldSem, SemanticGroup newSem) {
		removeBelongsToSemantic(oldSem);
		addBelongsToSemantic(newSem);
	}

	/**
	 * verifies if the Variable is part of a Variable Vector.
	 * 
	 * @param variables
	 *            the Vector of Variables that should be tested.
	 * @return true the Variable is part of the Vector.
	 */
	public boolean isMemberOf(Vector<Variable> variables) {
		if (variables.isEmpty()) {
			return false;
		}
		String varName = this.getFullName();
		for (Variable var : variables) {
			String varIndiName = var.getFullName();
			if (varName.equals(varIndiName)) {
				return true;
			}
		}
		return false;
	}
}
