package owlSpeak.engine.his.interfaces;

import java.util.Vector;

import owlSpeak.engine.his.FieldValue;

/**
 * This method implements Java and Jython to string functionality
 */
public interface IUserAction {
	public String __str__();
	public Vector<String> getFieldVector();
	public FieldValue getFieldValue(String field);
}