package owlSpeak.engine.his;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import owlSpeak.Belief;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.Variable;
import owlSpeak.engine.OwlScript;
import owlSpeak.engine.his.FieldValue.ConfirmationInfo;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;

public class UserActionVariablesSupplementary implements
		IUserActionSupplementary {

	private OSFactory factory;
	private static UserActionVariablesSupplementary singleton = null;

	public static UserActionVariablesSupplementary createUserActionVariablesSupplementary(
			OSFactory _factory) {
		if (singleton == null) {
			singleton = new UserActionVariablesSupplementary(_factory);
		} else
			singleton.setFactory(_factory);
		return singleton;
	}

	public UserActionVariablesSupplementary(OSFactory _factory) {
		factory = _factory;
	}

	private void setFactory(OSFactory _factory) {
		this.factory = _factory;
	}

	@Override
	public void createNewConfirmationBelief(FieldValue val, String fieldName,
			Partition partition) throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Random rand = new Random();
		Belief newBelief = factory.createBelief("Belief_" + rand.nextInt());
		partition.addHasBelief(newBelief);
		Semantic sem = factory.getSemantic(val.getValue());
		newBelief.addSemantic(sem);
	}

	@Override
	public void createNewEqualsBelief(FieldValue val, String fieldName,
			Partition partition) throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Variable fieldVar = factory.getSemanticGroup(fieldName).getVariable();
		Random rand = new Random();
		Belief newBeliefEquals = factory.createBelief("Belief_"
				+ rand.nextInt());
		newBeliefEquals.addVariabledefault(fieldVar);
		newBeliefEquals.addVariableValue(val.getValue());

		if (fieldVar.hasBelongsToSemantic()) {
			Belief newBeliefSem = factory.createBelief("Belief_"
					+ rand.nextInt());
			partition.addHasBelief(newBeliefEquals);
			partition.addHasBelief(newBeliefSem);
			newBeliefSem.addSemantic(fieldVar.getBelongsToSemantic());
		}
	}

	@Override
	public void createNewExcludesBelief(FieldValue val, String fieldName,
			Partition partition) throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Random rand = new Random();
		Belief newBeliefExcludes = factory.createBelief("Belief_"
				+ rand.nextInt());
		partition.addExcludesBelief(newBeliefExcludes);

		Variable fieldVar = factory.getSemanticGroup(fieldName).getVariable();
		newBeliefExcludes.addVariabledefault(fieldVar);
		newBeliefExcludes.addVariableValue(val.getValue());
	}

	/**
	 * extracts the field name from Move m. It is assumed that each move
	 * addresses exactly one field.
	 * 
	 * TODO extend to handle more fields per move
	 * 
	 * @param m
	 *            the move which contains the fields
	 * @return the field name or null
	 * @throws NoFactorySetException
	 */
	@Override
	public Set<String> extractFieldNameFromMove(Move m) throws NoFactorySetException {
		Vector<Semantic> semVec = new Vector<Semantic>();
		if (m.hasVariableOperator()) {
			String[] varOpParse = OwlScript.evaluateString(m
					.getVariableOperator());
			if ("SET".equalsIgnoreCase(varOpParse[0])) {
				Vector<String> sets = owlSpeak.engine.OwlScript
						.getSets(varOpParse[1]);
				for (String setInstruction : sets) {
					String[] set = owlSpeak.engine.OwlScript
							.parseSet(setInstruction);
					if (factory == null)
						throw new NoFactorySetException();
					for (int i = 0; i < set.length; i += 2) {
						Variable var = factory.getVariable(set[i]);
						SemanticGroup s = var.getBelongsToSemantic();
						// FIXME a little dirty: semanticgroup cast as semantic
						// for easier processing
						semVec.add(s.asSemantic());
					}
				}
			}
		}

		if (m.hasSemantic()) {
			for (Semantic sem : m.getSemantic()) {
				if (sem.hasConfirmationInfo()) {
					semVec.add(sem);
				}
			}
		}
		
		Set<String> fields = new TreeSet<String>();
		for (Semantic sem : semVec) {
			fields.add(getFieldNameFromSemantic(sem));
		}

		if (!fields.isEmpty())
			return fields;
		
		return null;
	}

	/**
	 * Extracts the field name out of the semantic sem. Expects a semantic
	 * object which is related to exactly one semantic group.
	 * 
	 * @param sem
	 *            the semantic object
	 * @return the field name or null if something went wrong
	 */
	@Override
	public String getFieldNameFromSemantic(Semantic sem) {
		if (!sem.hasSemanticGroup())
			// if no semantic group exists it is assumed that the semantic
			// itself represents the field
			return sem.getLocalName();

		String fieldName = sem.getSemanticGroup().getLocalName();
		return fieldName;
	}

	/**
	 * Extracts the field semantic out of the semantic sem. Expects a semantic
	 * object which is related to exactly one master semantic.
	 * 
	 * @param sem
	 *            the semantic object
	 * @return the semantic identifying the field or null if no field is set
	 */
	public SemanticGroup getFieldFromSemantic(Semantic sem) {
		if (!sem.hasSemanticGroup())
			return null;

		return sem.getSemanticGroup();
	}

	/**
	 * {@inheritDoc}<br/>
	 * <br/>
	 * 
	 * Pseudo code:
	 * 
	 * <OL type="1">
	 * <LI>Find all moves which contain non-empty variableOperator. Regard only
	 * variableOperators containing "SET"
	 * <LI>Identify variable name and value.
	 * <OL type="a">
	 * <li>Collect variable object and resolve belongsToSemantic which
	 * represents the field name. Calls {@link #extractFieldNameFromMove(Move)}.
	 * <li>Increase counter for this field
	 * </OL>
	 * <LI>return map
	 * </OL>
	 */
	
	public Map<String, Integer> getFieldTotalsOld() throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Map<String, Integer> fieldTotals = new TreeMap<String, Integer>();
		for (Move mov : factory.getAllMoveInstances()) {
			// assumption that once a move contains a variableOperator SET and
			// the field name is not null, it contains a value
			if (mov.hasVariableOperator()) {
				String[] varOpParse = OwlScript.evaluateString(mov
						.getVariableOperator());
				
				// FIXME computation of field totals if more than one field is addressed by move
				if ("SET".equalsIgnoreCase(varOpParse[0])) {
					for (String fieldName : extractFieldNameFromMove(mov)) {
					if (fieldName != null) {
						int totals = getTotalsFromSetString(varOpParse[1]);
						if (totals != -1)
							fieldTotals.put(fieldName, totals);
						else {
							Integer count = fieldTotals.get(fieldName);
							if (count == null)
								fieldTotals.put(fieldName, 1);
							else
								fieldTotals.put(fieldName, count + 1);
						}
					}
					}
				}
			}
		}
		return fieldTotals;
	}
	
	@Override
	public Map<String, Integer> getFieldTotals() throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Map<String, Integer> fieldTotals = new TreeMap<String, Integer>();
		for (SemanticGroup semgrp : factory.getAllSemanticGroupInstances()) {
			if (semgrp.hastFieldTotals()) {
				fieldTotals.put(semgrp.getLocalName(), semgrp.getFieldTotals());
			}
		}
		return fieldTotals;
	}

	/**
	 * Extracts the field value out of the belief.
	 * 
	 * @param b
	 *            the belief object
	 * @return the field value or null if something went wrong
	 */
	@Override
	public FieldValue getFieldValueFromBelief(Belief b) {
		FieldValue val = null;
		if (b.hasSemantic()) {
			Semantic sem = ((Semantic) (b.getSemantic().toArray()[0]));
			ConfirmationInfo c = ConfirmationInfo.UNDEFINED;
			if (sem.hasConfirmationInfo())
				c = sem.getConfirmationInfo() ? ConfirmationInfo.CONFIRM
						: ConfirmationInfo.REJECT;
			val = new FieldValue(sem.getLocalName(), c);
		} else if (b.hasVariableValue()) {
			val = new FieldValue(b.getVariableValue());
		}
		return val;
	}

	@Override
	public String getFieldNameFromBelief(Belief b) {
		String str = null;
		if (b.hasVariabledefault()
				&& b.getVariabledefault().hasBelongsToSemantic()) {
			str = b.getVariabledefault().getBelongsToSemantic().getLocalName();
		} else if (b.hasSemantic()) {
			Semantic s = (Semantic) (b.getSemantic().toArray()[0]);
			str = getFieldNameFromSemantic(s);
		}
		return str;
	}

	@Override
	public String getFieldNameFromVariable(Variable var) {
		if (!var.hasBelongsToSemantic())
			return null;
		return var.getBelongsToSemantic().getLocalName();
	}

	/**
	 * Assumption: fields are encoded as semantics groups, which do not belong
	 * to another semantic group object, i.e., hasSemanticGroup() returns false.
	 */
	@Override
	public boolean beliefRepresentsField(Belief b) {
		if (b.hasSemantic()) {
			Semantic s = ((Semantic) (b.getSemantic().toArray()[0]));
			if (!s.hasSemanticGroup())
				return true;
		}
		return false;
	}

	/**
	 * Assumption: confirmations are encoded as semantics, whose
	 * confirmationInfo field is not undefined.
	 */
	@Override
	public boolean beliefRepresentsConfirmation(Belief b) {
		if (b.hasSemantic()) {
			Semantic s = ((Semantic) (b.getSemantic().toArray()[0]));
			if (s.hasConfirmationInfo())
				return true;
		}
		return false;
	}

	@Override
	public void copyHasBelief(Belief b, Partition partition)
			throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Random rand = new Random();
		Belief newBelief = factory.createBelief("Belief_" + rand.nextInt());
		partition.addHasBelief(newBelief);
		for (Semantic sem : b.getSemantic())
			newBelief.addSemantic(sem);
		if (b.hasVariabledefault())
			newBelief.addVariabledefault(b.getVariabledefault());
		if (b.hasVariableValue())
			newBelief.addVariableValue(b.getVariableValue());
	}

	@Override
	public void copyExcludesBelief(Belief b, Partition partition)
			throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Random rand = new Random();
		Belief newBelief = factory.createBelief("Belief_" + rand.nextInt());
		partition.addExcludesBelief(newBelief);
		for (Semantic sem : b.getSemantic())
			newBelief.addSemantic(sem);
		if (b.hasVariabledefault())
			newBelief.addVariabledefault(b.getVariabledefault());
		if (b.hasVariableValue())
			newBelief.addVariableValue(b.getVariableValue());
	}

	@Override
	public String replaceVariableValues(String opString, String var, String value) {
//		return opString
//				.replaceAll("(:external_[a-zA-Z0-9]+_mv:[0-9]+:)", speak.replace(" ", "_"));
		String pattern = "(.*"+var+"=)(:[a-zA-Z0-9_]+:)(.*)";
		return opString
				.replaceAll(pattern, "$1"+value+"$3");
	}

	/**
	 * Extracts the number of field totals out of a string. The input string has
	 * to comply to the following regexp: <code>.*:external_[a-zA-Z0-9]+_mv:([0-9]+):.*</code>
	 * 
	 * @param setString
	 * @return the total number of values the field can take
	 */
	public static int getTotalsFromSetString(String setString) {
		Matcher m = Pattern.compile(".*:external_[a-zA-Z0-9]+_mv:([0-9]+):.*")
				.matcher(setString);
		String s = m.find() ? m.group(1) : "";
		int ret;
		try {
			ret = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			ret = -1;
		}
		return ret;
	}

}
