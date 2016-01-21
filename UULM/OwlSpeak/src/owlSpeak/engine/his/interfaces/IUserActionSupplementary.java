package owlSpeak.engine.his.interfaces;

import java.util.Map;
import java.util.Set;

import owlSpeak.Belief;
import owlSpeak.Move;
import owlSpeak.Semantic;
import owlSpeak.Variable;
import owlSpeak.engine.his.FieldValue;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.exception.NoFactorySetException;

public interface IUserActionSupplementary {

	public void createNewConfirmationBelief(FieldValue val, String fieldName, Partition partition) throws NoFactorySetException;

	public void createNewEqualsBelief(FieldValue val, String fieldName, Partition partition) throws NoFactorySetException;

	public void createNewExcludesBelief(FieldValue val, String fieldName, Partition partition) throws NoFactorySetException;

	/**
	 * extracts the field name from Move m. It is assumed that each move
	 * addresses exactly one field.
	 * 
	 * @param m
	 *            the move which contains the fields
	 * @return the field name or null
	 * @throws NoFactorySetException 
	 */
	public Set<String> extractFieldNameFromMove(Move m) throws NoFactorySetException;

	/**
	 * Extracts the field name out of the semantic sem. Expects a semantic
	 * object which is related to exactly one semantic group.
	 * 
	 * @param sem
	 *            the semantic object
	 * @return the field name or null if something went wrong
	 */
	public String getFieldNameFromSemantic(Semantic sem);

	/**
	 * Computes the number of possible elements of all fields (slots).
	 * 
	 * @return A Map object containing the numbmer of elements for all fields.
	 * @throws NoFactorySetException
	 */
	public Map<String, Integer> getFieldTotals() throws NoFactorySetException;

	/**
	 * Extracts the value (semantic local name) out of the semantic which is
	 * contained in the belief object bel. Expects a belief object containing
	 * one semantic object.
	 * 
	 * @param bel
	 *            the belief object
	 * @return the value or null if something went wrong
	 */
	public FieldValue getFieldValueFromBelief(Belief b);

	public String getFieldNameFromBelief(Belief b);

	public String getFieldNameFromVariable(Variable var);

	public boolean beliefRepresentsField(Belief b);

	public boolean beliefRepresentsConfirmation(Belief b);

	public void copyHasBelief(Belief b, Partition partition) throws NoFactorySetException;

	public void copyExcludesBelief(Belief b, Partition partition) throws NoFactorySetException;
	
	public String replaceVariableValues(String opString, String var, String speak);
}
