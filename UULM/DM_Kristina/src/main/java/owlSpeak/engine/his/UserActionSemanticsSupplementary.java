package owlSpeak.engine.his;

import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import owlSpeak.Belief;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.Variable;
import owlSpeak.engine.his.FieldValue.ConfirmationInfo;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;

public class UserActionSemanticsSupplementary implements
		IUserActionSupplementary {

	private OSFactory factory;
	private static UserActionSemanticsSupplementary singleton = null;
	
	public static UserActionSemanticsSupplementary createUserActionSemanticsSupplementary(OSFactory _factory) {
		if (singleton == null) {
			singleton = new UserActionSemanticsSupplementary(_factory);
		}
		else
			singleton.setFactory(_factory);
		return singleton;
	}

	public UserActionSemanticsSupplementary(OSFactory _factory) {
		this.factory = _factory;
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
		Random rand = new Random();
		Belief newBeliefEquals = factory.createBelief("Belief_"
				+ rand.nextInt());
		Semantic sem = factory.getSemantic(val.getValue());
		newBeliefEquals.addSemantic(sem);

		if (sem.hasSemanticGroup()) {
			Belief newBeliefSemGroup = factory.createBelief("Belief_"
					+ rand.nextInt());
			partition.addHasBelief(newBeliefEquals);
			partition.addHasBelief(newBeliefSemGroup);
			newBeliefSemGroup.addSemantic(sem.getSemanticGroup());
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

		Semantic sem = factory.getSemantic(val.getValue());
		newBeliefExcludes.addSemantic(sem);
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
	 */
	@Override
	public Set<String> extractFieldNameFromMove(Move m) {
		Set<String> fields = new TreeSet<String>();
		for (Semantic sem : m.getSemantic()) {
			fields.add(getFieldNameFromSemantic(sem));
		}

		if (!fields.isEmpty())
			return fields;
		
		return null;
	}

	/**
	 * Extracts the field name out of the semantic sem. Expects a semantic
	 * object which is related to exactly one semantic group or a Semantic Group
	 * itself.
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

	@Override
	public Map<String, Integer> getFieldTotals() throws NoFactorySetException {
		if (factory == null)
			throw new NoFactorySetException();
		Map<String, Integer> fieldTotals = new TreeMap<String, Integer>();
		for (Semantic sem : factory.getAllSemanticInstances()) {
			if (sem.isRealSemantic() && sem.hasSemanticGroup()) {
				String fieldName = getFieldNameFromSemantic(sem);
				Integer count = fieldTotals.get(fieldName);
				if (count == null)
					fieldTotals.put(fieldName, 1);
				else
					fieldTotals.put(fieldName, count + 1);
			}
		}
		return fieldTotals;
	}

	/**
	 * Extracts the value (semantic local name) out of the semantic which is
	 * contained in the belief object bel. Expects a belief object containing
	 * one semantic object.
	 * 
	 * @param b
	 *            the belief object
	 * @return the value or null if something went wrong
	 */
	@Override
	public FieldValue getFieldValueFromBelief(Belief b) {
		Semantic sem = ((Semantic) (b.getSemantic().toArray()[0]));
		ConfirmationInfo c = ConfirmationInfo.UNDEFINED;
		if (sem.hasConfirmationInfo())
			c = sem.getConfirmationInfo() ? ConfirmationInfo.CONFIRM
					: ConfirmationInfo.REJECT;
		return new FieldValue(sem.getLocalName(), c);
	}

	/**
	 * Extracts the field name out of the semantic which is contained in the
	 * belief object bel. Expects a belief object containing one semantic object
	 * which in turn contains exactly one semantic string.
	 * 
	 * @param b
	 *            the belief object
	 * @return the field name or null if something went wrong
	 */
	@Override
	public String getFieldNameFromBelief(Belief b) {
		if (!b.hasSemantic())
			return null;
		Semantic sem = (Semantic) b.getSemantic().toArray()[0];
		return getFieldNameFromSemantic(sem);
	}

	@Override
	public String getFieldNameFromVariable(Variable var) {
		if (!var.hasBelongsToSemantic())
			return null;
		return var.getBelongsToSemantic().getLocalName();
	}

	@Override
	public boolean beliefRepresentsField(Belief b) {
		if (b.hasSemantic()) {
			Semantic s = ((Semantic) (b.getSemantic().toArray()[0]));
			if (!s.hasSemanticGroup())
				return true;
		}
		return false;
	}

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
	public String replaceVariableValues(String opString, String var,
			String speak) {
		// TODO meaningful way of using this method reasonable?
		return opString;
	}
}
