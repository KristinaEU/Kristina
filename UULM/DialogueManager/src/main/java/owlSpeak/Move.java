package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.engine.Settings;

/**
 * The class that represents the Moves.
 * 
 * @author Tobias Heinroth.
 */
public class Move extends GenericClass {
	/**
	 * the name of the Move Class
	 */
	public final String name = "Move";

	/**
	 * creates a Move object by calling super
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
	public Move(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory,
			OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}

	/**
	 * returns a collection of Semantics that are linked in the Semantic field
	 * of the Move.
	 * 
	 * @return the collection of Semantics.
	 */
	public Collection<Semantic> getSemantic() {
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(
				indi, GenericProvider.semProp, onto, factory, manager)
				.iterator();
		Collection<Semantic> coll = new LinkedList<Semantic>();
		while (genCollIt.hasNext()) {
			coll.add(genCollIt.next().asSemantic());
		}
		return coll;
	}

	/**
	 * returns true if the Semantic field contains at least on item.
	 * 
	 * @return true if Semantic is not empty.
	 */
	public boolean hasSemantic() {
		return GenericProvider.hasObjectProperty(indi, GenericProvider.semProp,
				onto, factory);
	}

	/**
	 * adds a Semantic to the Semantic field of the Move.
	 * 
	 * @param newSemantic
	 *            the Semantic that should be added.
	 */
	public void addSemantic(Semantic newSemantic) {
		GenericProvider.addIndividual(indi, newSemantic.indi,
				GenericProvider.semProp, onto, factory, manager);
	}

	/**
	 * removes a Semantic from the Semantic field of the Move.
	 * 
	 * @param oldSemantic
	 *            the Semantic that should be removed.
	 */
	public void removeSemantic(Semantic oldSemantic) {
		GenericProvider.removeIndividual(indi, oldSemantic.indi,
				GenericProvider.semProp, onto, factory, manager);
	}

	/**
	 * replaces a Semantic in the Semantic field with a new Semantics.
	 * 
	 * @param oldSemantic
	 *            the Semantics that should be replaced.
	 * @param newSemantic
	 *            the Semantics that should replace the current Semantic.
	 */
	public void setSemantic(Semantic oldSemantic, Semantic newSemantic) {
		removeSemantic(oldSemantic);
		addSemantic(newSemantic);
	}

	/**
	 * returns a collection of Semantics that are linked in the ContrarySemantic
	 * field of the Move.
	 * 
	 * @return the collection of Semantics.
	 */
	public Collection<Semantic> getContrarySemantic() {
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(
				indi, GenericProvider.contProp, onto, factory, manager)
				.iterator();
		Collection<Semantic> coll = new LinkedList<Semantic>();
		while (genCollIt.hasNext()) {
			coll.add(genCollIt.next().asSemantic());
		}
		return coll;
	}

	/**
	 * returns true if the ContrarySemantic field contains at least on item.
	 * 
	 * @return true if ContrarySemantic is not empty.
	 */
	public boolean hasContrarySemantic() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.contProp, onto, factory);
	}

	/**
	 * adds a Semantic to the ContrarySemantic field of the Move.
	 * 
	 * @param newContrarySemantic
	 *            the Semantic that should be added.
	 */
	public void addContrarySemantic(Semantic newContrarySemantic) {
		GenericProvider.addIndividual(indi, newContrarySemantic.indi,
				GenericProvider.contProp, onto, factory, manager);
	}

	/**
	 * removes a Semantic from the ContrarySemantic field of the Move.
	 * 
	 * @param oldContrarySemantic
	 *            the Semantic that should be removed.
	 */
	public void removeContrarySemantic(Semantic oldContrarySemantic) {
		GenericProvider.removeIndividual(indi, oldContrarySemantic.indi,
				GenericProvider.contProp, onto, factory, manager);
	}

	/**
	 * replaces a Semantic in the ContrarySemantic field with a new Semantic.
	 * 
	 * @param oldContrarySemantic
	 *            the Semantic that should be removed.
	 * @param newContrarySemantic
	 *            the Semantics that should replace the current Semantic.
	 */
	public void setContrarySemantic(Semantic oldContrarySemantic,
			Semantic newContrarySemantic) {
		removeContrarySemantic(oldContrarySemantic);
		addContrarySemantic(newContrarySemantic);
	}

	/**
	 * returns the Grammar that is linked in the Grammar field of the Move.
	 * 
	 * @return the collection of Semantics.
	 */
	public Grammar getGrammar() {
		if (!hasGrammar())
			return null;
		return GenericProvider.getIndividual(indi, GenericProvider.gramProp,
				onto, factory, manager).asGrammar();
	}

	/**
	 * returns true if the Grammar field contains a Grammar.
	 * 
	 * @return true if Grammar is not empty.
	 */
	public boolean hasGrammar() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.gramProp, onto, factory);
	}

	/**
	 * replaces a Grammar in the Grammar field with a new Grammar.
	 * 
	 * @param oldGrammar
	 *            the Grammar that should be replaced by the new Grammar.
	 * @param newGrammar
	 *            the Grammar that should replace the current Grammar.
	 */
	public void setGrammar(Grammar oldGrammar, Grammar newGrammar) {
		if (oldGrammar != null)
			GenericProvider.removeIndividual(indi, oldGrammar.indi,
					GenericProvider.gramProp, onto, factory, manager);
		GenericProvider.addIndividual(indi, newGrammar.indi,
				GenericProvider.gramProp, onto, factory, manager);
	}

	/**
	 * returns the Integer contained in the Priority field of the Move.
	 * 
	 * @return the Priority.
	 */
	public int getPriority() {
		return GenericProvider.getIntProperty(indi, GenericProvider.prioProp,
				onto, factory);
	}

	/**
	 * returns true if the Priority field contains a value.
	 * 
	 * @return true if Priority is not empty.
	 */
	public boolean hasPriority() {
		return GenericProvider.hasDataProperty(indi, GenericProvider.prioProp,
				onto, factory);
	}

	/**
	 * removes the Priority of the Move.
	 * 
	 * @param oldPriority
	 *            the new Priority of the Move.
	 */
	public void removePriority(int oldPriority) {
		GenericProvider.removeIntData(indi, oldPriority,
				GenericProvider.prioProp, onto, factory, manager);
	}

	/**
	 * sets the Priority to the given Integer.
	 * 
	 * @param oldPriority
	 *            the old Priority.
	 * @param newPriority
	 *            the new Priority.
	 */
	public void setPriority(int oldPriority, int newPriority) {
		removePriority(oldPriority);
		GenericProvider.addIntData(indi, newPriority, GenericProvider.prioProp,
				onto, factory, manager);
	}

	/**
	 * returns the Utterance that is linked in the Utterance field of the Move
	 * object.
	 * 
	 * @return the Utterance of the Move.
	 */
	public Utterance getUtterance() {
		return GenericProvider.getIndividual(indi, GenericProvider.uttProp,
				onto, factory, manager).asUtterance();
	}

	/**
	 * returns true if the Utterance field contains an Utterance.
	 * 
	 * @return true if Utterance is not empty.
	 */
	public boolean hasUtterance() {
		return GenericProvider.hasObjectProperty(indi, GenericProvider.uttProp,
				onto, factory);
	}

	/**
	 * sets the Utterance to the given Utterance.
	 * 
	 * @param oldUtterance
	 *            the old Utterance.
	 * @param newUtterance
	 *            the new Utterance.
	 */
	public void setUtterance(Utterance oldUtterance, Utterance newUtterance) {
		if (oldUtterance != null)
			GenericProvider.removeIndividual(indi, oldUtterance.indi,
					GenericProvider.uttProp, onto, factory, manager);
		GenericProvider.addIndividual(indi, newUtterance.indi,
				GenericProvider.uttProp, onto, factory, manager);
	}

	/**
	 * returns the String contained in the VariableOperator field. This
	 * typically represents an OwlScript expression that sets a variable.
	 * 
	 * @return the Variable-Operator String
	 */
	public String getVariableOperator() {
		if (hasVariableOperator())
			return GenericProvider.getStringData(indi, GenericProvider.varProp,
					onto, factory);
		else
			return null;
	}

	/**
	 * returns true if the VariableOperator field contains a String with a
	 * length of at least 1.
	 * 
	 * @return true if VariableOperator is not empty
	 */
	public boolean hasVariableOperator() {
		return GenericProvider.hasDataProperty(indi, GenericProvider.varProp,
				onto, factory);
	}

	/**
	 * sets the VariableOperator field to the specified String.
	 * 
	 * @param oldVariableOperator
	 *            the old String for VariableOperator.
	 * @param newVariableOperator
	 *            the new String for VariableOperator.
	 */
	public void setVariableOperator(String oldVariableOperator,
			String newVariableOperator) {
		if (oldVariableOperator != null)
			GenericProvider.removeStringData(indi, oldVariableOperator,
					GenericProvider.varProp, onto, factory, manager);
		GenericProvider.addStringData(indi, newVariableOperator,
				GenericProvider.varProp, onto, factory, manager);
	}

	/**
	 * sets the VariableOperator field to the specified String.
	 * 
	 * @param newVariableOperator
	 *            the new String for VariableOperator.
	 */
	public void setVariableOperator(String newVariableOperator) {
		setVariableOperator(null, newVariableOperator);
	}

	/**
	 * returns a collection of Keywords that are linked in the Keyword field of
	 * the Move.
	 * 
	 * @return the collection of Keywords.
	 */
	public Collection<OWLLiteral> getKeywordsString() {
		Iterator<OWLLiteral> all = GenericProvider.getOWLLiteralProperties(
				indi, GenericProvider.keyStringProp, onto, factory).iterator();
		Collection<OWLLiteral> coll = new java.util.LinkedList<OWLLiteral>();
		while (all.hasNext()) {
			OWLLiteral o = all.next();
			if ((o.getLang()).equals((Settings.asrMode).substring(0, 2))) {
				coll.add(o);
			}
		}
		return coll;
	}

	/**
	 * returns true if the KeywordsString field contains at least on item.
	 * 
	 * @return true if KeywordsString is not empty.
	 */
	public boolean hasKeywordsString() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.keyStringProp, onto, factory);
	}

	/**
	 * adds an OWLLiteral to the KeywordsString field of the Grammar.
	 * 
	 * @param newKeywordsString
	 *            the OWLLiteral that should be added.
	 */
	public void addKeywordsString(OWLLiteral newKeywordsString) {
		GenericProvider.addOWLLiteralProperty(indi, newKeywordsString,
				GenericProvider.keyStringProp, onto, factory, manager);
	}

	/**
	 * removes an OWLLiteral from the KeywordsString field of the Grammar.
	 * 
	 * @param oldKeywordsString
	 *            the OWLLiteral that should be removed.
	 */
	public void removeKeywordsString(OWLLiteral oldKeywordsString) {
		GenericProvider.removeOWLLiteralProperty(indi, oldKeywordsString,
				GenericProvider.keyStringProp, onto, factory, manager);
	}

	/**
	 * replaces the specified OWLLiteral with a new OWLLiteral.
	 * 
	 * @param oldKeywordsString
	 *            the old OWLLiteral that should be replaced.
	 * @param newKeywordsString
	 *            the new OWLLiteral that should replace the current OWLLiteral.
	 */
	public void setGrammarString(OWLLiteral oldKeywordsString,
			OWLLiteral newKeywordsString) {
		GenericProvider.removeOWLLiteralProperty(indi, oldKeywordsString,
				GenericProvider.keyStringProp, onto, factory, manager);
		GenericProvider.addOWLLiteralProperty(indi, newKeywordsString,
				GenericProvider.keyStringProp, onto, factory, manager);
	}

	/**
	 * returns the value in the isExitMove field. This is used to specify if the
	 * Move ends the conversation.
	 * 
	 * @return true if the Move ends the conversation.
	 */
	public boolean getIsExitMove() {
		return GenericProvider.getBooleanProperty(indi,
				GenericProvider.isExitMoveProp, onto, factory);
	}

	/**
	 * returns true if the isExitMove field contains any value (true or false).
	 * 
	 * @return true if isExitMove is not empty.
	 */
	public boolean hasIsExitMove() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.isExitMoveProp, onto, factory);
	}

	/**
	 * removes the isExitMove field.
	 * 
	 * @param oldIsExitMove
	 *            the Boolean value for isExitMove to be removed.
	 */
	public void removeIsExitMove(boolean oldIsExitMove) {
		GenericProvider.removeBooleanData(indi, oldIsExitMove,
				GenericProvider.isExitMoveProp, onto, factory, manager);
	}

	/**
	 * sets the isExitMove field to the specified boolean.
	 * 
	 * @param oldIsExitMove
	 *            the old Boolean value for isExitMove.
	 * @param newIsExitMove
	 *            the Boolean containing the new value for isExitMove.
	 */
	public void setIsExitMove(boolean oldIsExitMove, boolean newIsExitMove) {
		removeIsExitMove(oldIsExitMove);
		GenericProvider.addBooleanData(indi, newIsExitMove,
				GenericProvider.isExitMoveProp, onto, factory, manager);
	}

	/**
	 * returns a collection of Moves that are linked in the
	 * LateExecutionConditions field of the Move.
	 * 
	 * @return the collection of Moves.
	 */
	public Collection<Move> getProcessConditions() {
		Iterator<GenericClass> genCollIt = GenericProvider
				.getIndividualColl(indi, GenericProvider.processConditions,
						onto, factory, manager).iterator();
		Collection<Move> coll = new LinkedList<Move>();
		while (genCollIt.hasNext()) {
			coll.add(genCollIt.next().asMove());
		}
		return coll;
	}

	/**
	 * returns true if the LateExecutionConditions field contains at least on
	 * item.
	 * 
	 * @return true if Semantic is not empty.
	 */
	public boolean hasProcessConditions() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.processConditions, onto, factory);
	}

	/**
	 * adds a Move to the LateExecutionConditions field of the Move.
	 * 
	 * @param newMove
	 *            the Move that should be added.
	 */
	public void addProcessConditions(Move newMove) {
		GenericProvider.addIndividual(indi, newMove.indi,
				GenericProvider.processConditions, onto, factory, manager);
	}

	/**
	 * removes a Move from the LateExecutionConditions field of the Move.
	 * 
	 * @param oldMove
	 *            the Move that should be removed.
	 */
	public void removeProcessConditions(Move oldMove) {
		GenericProvider.removeIndividual(indi, oldMove.indi,
				GenericProvider.processConditions, onto, factory, manager);
	}

	@Override
	public String toString() {
		return getLocalName();
	}

	public boolean getConfirmationInfo() throws NoConfirmationInfoException {
		boolean ret = false;
		boolean confInfoFound = false;
		for (Semantic s : getSemantic()) {
			confInfoFound |= s.hasConfirmationInfo();
			ret |= (s.hasConfirmationInfo() ? s.getConfirmationInfo() : false);
		}
		if (!confInfoFound)
			throw new NoConfirmationInfoException();
		return ret;
	}
	
	public boolean hasConfirmationInfo() {
		boolean confInfoFound = false;
		for (Semantic s : getSemantic()) {
			confInfoFound |= s.hasConfirmationInfo();
		}
		return confInfoFound;
	}

	public class NoConfirmationInfoException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3727814392124546402L;

	}

	/**
	 * returns the value in the isExitMove field. This is used to specify if the
	 * Move ends the conversation.
	 * 
	 * @return true if the Move ends the conversation.
	 */
	public boolean getIsRequestMove() {
		return GenericProvider.getBooleanProperty(indi,
				GenericProvider.isRequestMoveProp, onto, factory);
	}

	/**
	 * returns true if the isRequestMove field contains any value (true or
	 * false).
	 * 
	 * @return true if isRequestMove is not empty.
	 */
	public boolean hasIsRequestMove() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.isRequestMoveProp, onto, factory);
	}

	/**
	 * removes the isRequestMove field.
	 * 
	 * @param oldIsRequestMove
	 *            the Boolean value for isRequestMove to be removed.
	 */
	public void removeIsRequestMove(boolean oldIsRequestMove) {
		GenericProvider.removeBooleanData(indi, oldIsRequestMove,
				GenericProvider.isRequestMoveProp, onto, factory, manager);
	}

	/**
	 * sets the isRequestMove field to the specified boolean.
	 * 
	 * @param oldIsRequestMove
	 *            the old Boolean value for isRequestMove.
	 * @param newIsRequestMove
	 *            the Boolean containing the new value for isRequestMove.
	 */
	public void setIsRequestMove(boolean oldIsRequestMove,
			boolean newIsRequestMove) {
		removeIsRequestMove(oldIsRequestMove);
		GenericProvider.addBooleanData(indi, newIsRequestMove,
				GenericProvider.isRequestMoveProp, onto, factory, manager);
	}

	/**
	 * returns the value in the extractFieldValues field. This is used to
	 * specify if the user input string should be parsed in order to extract the
	 * field values out of the parse tree.
	 * 
	 * @return true if the Move ends the conversation.
	 */
	public ExtractFieldMode getExtractFieldValues() {
		try {
			String mode = GenericProvider.getStringData(indi,
				GenericProvider.extractFieldValuesProp, onto, factory);
			return ExtractFieldMode.valueOf(mode);
		} catch (IllegalArgumentException e) {
			return ExtractFieldMode.NO;
		} catch (NoSuchElementException e) {
			return ExtractFieldMode.NO;
		} catch (NullPointerException e) {
			return ExtractFieldMode.NO;
		}
		
	}

	/**
	 * returns true if the extractFieldValues field contains any value
	 * 
	 * @return true if extractFieldValues is not empty.
	 */
	public boolean hasExtractFieldValues() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.extractFieldValuesProp, onto, factory);
	}

	/**
	 * Removes mode
	 */
	public void removeExtractFieldValues(ExtractFieldMode mode) {
		if (hasExtractFieldValues())
			GenericProvider.removeStringData(indi, mode.toString(),
					GenericProvider.extractFieldValuesProp, onto, factory,
					manager);
	}

	/**
	 * Sets the extractFieldValues field to newMode.
	 */
	public void setExtractFieldValues(ExtractFieldMode oldMode, ExtractFieldMode newMode) {
		removeExtractFieldValues(oldMode);
		GenericProvider.addStringData(indi, newMode.toString(),
				GenericProvider.extractFieldValuesProp, onto, factory, manager);
	}

	// /**
	// * returns the String contained in the VariableOperator field. This
	// * typically represents an OwlScript expression that sets a variable.
	// *
	// * @return the Variable-Operator String
	// */
	// public String getBuildInGrammar() {
	// if (GenericProvider.hasDataProperty(indi,
	// GenericProvider.useBuildInGrammarProp, onto, factory))
	// return GenericProvider.getStringData(indi,
	// GenericProvider.useBuildInGrammarProp, onto, factory);
	// else
	// return null;
	// }
	//
	// /**
	// * returns true if the VariableOperator field contains a String with a
	// * length of at least 1.
	// *
	// * @return true if VariableOperator is not empty
	// */
	// public boolean hasBuildInGrammar() {
	// String s = getBuildInGrammar();
	// return (s != null && !s.equalsIgnoreCase("none"));
	// }
	//
	// /**
	// * sets the VariableOperator field to the specified String.
	// *
	// * @param oldVariableOperator
	// * the old String for VariableOperator.
	// * @param newVariableOperator
	// * the new String for VariableOperator.
	// */
	// public void setBuildInGrammar(String newVariableOperator) {
	// String oldVariableOperator = getBuildInGrammar();
	// if (oldVariableOperator != null)
	// GenericProvider.removeStringData(indi, oldVariableOperator,
	// GenericProvider.useBuildInGrammarProp, onto, factory,
	// manager);
	// GenericProvider.addStringData(indi, newVariableOperator,
	// GenericProvider.useBuildInGrammarProp, onto, factory, manager);
	// }
	
	public enum ExtractFieldMode {
		NO, GRAMMAR, PSEUDO;
	}
}
