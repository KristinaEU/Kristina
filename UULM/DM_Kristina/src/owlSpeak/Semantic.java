package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * The class that represents the Semantics.
 * 
 * @author Tobias Heinroth.
 */
public class Semantic extends GenericClass {
	/**
	 * the name of the Semantic Class
	 */
	public final String name = "Semantic";

	/**
	 * creates a Semantic object by calling super
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
	public Semantic(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}

	/**
	 * returns a collection of OWLLiteral that are linked in the SemanticString
	 * field of the Semantic.
	 * 
	 * @return the collection of OWLLiteral.
	 */
	public Collection<OWLLiteral> getSemanticString() {
		return GenericProvider.getOWLLiteralProperties(indi,
				GenericProvider.semStringProp, onto, factory);
	}

	/**
	 * returns true if the SemanticString field contains at least on item.
	 * 
	 * @return true if SemanticString is not empty.
	 */
	public boolean hasSemanticString() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.semStringProp, onto, factory);
	}

	/**
	 * adds an OWLLiteral to the SemanticString field of the Semantic.
	 * 
	 * @param newSemanticString
	 *            the String that should be added.
	 */
	public void addSemanticString(OWLLiteral newSemanticString) {
		GenericProvider.addOWLLiteralProperty(indi, newSemanticString,
				GenericProvider.semStringProp, onto, factory, manager);
	}

	/**
	 * removes an OWLLiteral from the SemanticString field of the Semantic.
	 * 
	 * @param oldSemanticString
	 *            the String that should be removed.
	 */
	public void removeSemanticString(OWLLiteral oldSemanticString) {
		GenericProvider.removeOWLLiteralProperty(indi, oldSemanticString,
				GenericProvider.semStringProp, onto, factory, manager);
	}

	/**
	 * replaces an OWLLiteral in the SemanticString field with a new OWLLiteral.
	 * 
	 * @param oldSemanticString
	 *            the OWLLiteral that should be replaced by the new OWLLiteral.
	 * @param newSemanticString
	 *            the new OWLLiteral.
	 */
	public void setSemanticString(OWLLiteral oldSemanticString,
			OWLLiteral newSemanticString) {
		removeSemanticString(oldSemanticString);
		addSemanticString(newSemanticString);
	}

	/**
	 * verifies if the Semantic requirement is part of a Semantic Vector.
	 * 
	 * @param semantics
	 *            the Vector of Semantics that should be tested.
	 * @return true the Semantic is part of the Vector.
	 */
	public boolean isMemberOf(Vector<Semantic> semantics) {
		String semName = this.getFullName();
		if (semantics.isEmpty()) {
			return false;
		}
		Iterator<Semantic> semIt = semantics.iterator();
		while (semIt.hasNext()) {
			String semIndiName = semIt.next().getFullName();
			if (semName.equals(semIndiName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns true if the SemanticGroup field contains at least on item.
	 * 
	 * @return true if SemanticGroup is not empty.
	 */
	public boolean hasSemanticGroup() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.semGroupProp, onto, factory);
	}

	/**
	 * returns the SemanticGroup this Semantic belongs to.
	 * 
	 * @return SemanticGroup object.
	 */
	public SemanticGroup getSemanticGroup() {
		if (!hasSemanticGroup())
			return null;
		GenericClass gen = GenericProvider.getIndividual(indi,
				GenericProvider.semGroupProp, onto, factory, manager);
		return gen.asSemanticGroup();
	}
	

	/**
	 * adds a Semantic to the ContainsSemantic field of the SemanticGroup.
	 * 
	 * @param newSemanticGroup
	 *            the SemanticGroup which the Semantic belongs to.
	 */
	public void addSemanticGroup(SemanticGroup newSemanticGroup) {
		GenericProvider.addIndividual(indi, newSemanticGroup.indi,
				GenericProvider.semGroupProp, onto, factory, manager);
	}

//	/**
//	 * returns true if the Semantic belongs to a master semantic
//	 * 
//	 * @return true if masterSemantic is not empty.
//	 */
//	public boolean hasMasterSemantic() {
//		return GenericProvider.hasObjectProperty(indi,
//				GenericProvider.masterSemProp, onto, factory);
//	}
//
//	/**
//	 * returns the master semantic object this Semantic belongs to.
//	 * 
//	 * @return master Semantic object.
//	 */
//	public Semantic getMasterSemantic() {
//		GenericClass gen = GenericProvider.getIndividual(indi,
//				GenericProvider.masterSemProp, onto, factory, manager);
//		return gen.asSemantic();
//	}
//	/**
//	 * returns true if the Semantic belongs to a master semantic
//	 * 
//	 * @return true if masterSemantic is not empty.
//	 */
//	public boolean hasSubSemantic() {
//		return GenericProvider.hasObjectProperty(indi,
//				GenericProvider.subSemProp, onto, factory);
//	}
//
//	/**
//	 * returns the master semantic object this Semantic belongs to.
//	 * 
//	 * @return master Semantic object.
//	 */
//	public Semantic getSubSemantic() {
//		GenericClass gen = GenericProvider.getIndividual(indi,
//				GenericProvider.subSemProp, onto, factory, manager);
//		return gen.asSemantic();
//	}
	
	/**
	 * returns if this Semantic objects represents a real value or only
	 * represents a helper value like a confirmation
	 * 
	 * @return true if Semantic represents a real value, else false
	 */
	public boolean isRealSemantic() {
		// TODO find more elaborated way
		return hasSemanticString();
	}

	/**
	 * returns a boolean representing the confirmation information. Note that if
	 * no confirmation info is set still false is returned.
	 * 
	 * @return true if semantic contains confirmation info "CONFRIM", else
	 *         false.
	 */
	public boolean getConfirmationInfo() {
		return confInfoStr2Boolean(getConfirmationInfoString());
	}

	/**
	 * returns true if Semantic is a contains confirmation information.
	 * 
	 * @return true if Semantic is a contains confirmation information.
	 */
	public boolean hasConfirmationInfo() {
		boolean has = GenericProvider.hasDataProperty(indi,
				GenericProvider.confirmationInfo, onto, factory);
		if (has) {
			has = !getConfirmationInfoString().equalsIgnoreCase("undefined");
		}
		return has;
	}

	/**
	 * removes the ConfirmationInfo of the Semantic.
	 * 
	 */
	public void removeConfirmationInfo() {
		GenericProvider.removeStringData(indi, getConfirmationInfoString(),
				GenericProvider.confirmationInfo, onto, factory, manager);
	}

	/**
	 * sets the Confirmation Info of this Semantic
	 * 
	 * @param c
	 *            the new confirmation info
	 */
	public void setConfirmationInfo(boolean c) {
		if (hasConfirmationInfo())
			removeConfirmationInfo();
		GenericProvider.addStringData(indi, confInfoBoolean2String(c),
				GenericProvider.confirmationInfo, onto, factory, manager);
	}

	/**
	 * returns the confirmation info string of the semantic
	 * 
	 * @return the confirmation info string of the semantic
	 * 
	 */
	private String getConfirmationInfoString() {
		return GenericProvider.getStringData(indi,
				GenericProvider.confirmationInfo, onto, factory);
	}

	/**
	 * converts a confirmation info String to a boolean value
	 * 
	 * @param s
	 *            the confirmation info String
	 * @return the boolean representation of the string
	 */
	private boolean confInfoStr2Boolean(String s) {
		if (s.equalsIgnoreCase("confirmation"))
			return true;
		else
			return false;
	}

	/**
	 * converts a confirmation info from boolean to string
	 * 
	 * @param b
	 *            the boolean value
	 * @return the string which corresponds to the boolean b, either
	 *         "confirmation" or "rejection"
	 */
	private String confInfoBoolean2String(boolean b) {
		if (b)
			return "confirmation";
		else if (!b)
			return "rejection";
		return null;
	}
}
