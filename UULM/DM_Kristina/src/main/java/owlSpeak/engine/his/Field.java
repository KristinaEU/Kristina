package owlSpeak.engine.his;

import java.util.Vector;

/**
 * The Field class represents a field (or slot) which can be addressed during
 * the dialogue, e.g., in a flight booking dialogue it may be "origin" or
 * "destination".
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */
public class Field {
	public enum FieldType {
		EQUALS, EXCLUDES;
	}

	/**
	 * the field type
	 */
	private FieldType type;

	/**
	 * This vector of Strings contains all the values for this field, which are
	 * excluded.
	 */
	private Vector<String> excludes;

	/**
	 * the value for this field. 
	 */
	private String equals;

	/**
	 * the name of this field.
	 */
	private String fieldName;

	/**
	 * if this equals configuration has been confirmed by the user,
	 * confirmed is true.
	 */
	private boolean confirmed;
	
	/**
	 * if this equals configuration has been rejected by the user,
	 * confirmed is true.
	 */
	private boolean rejected;

	/**
	 * Creates an empty field fieldName of type _type.
	 * 
	 * @param fieldName
	 *            the field name
	 * @param _type
	 *            the field type
	 */
	public Field(String fieldName, FieldType _type) {
		setType(_type);
		this.fieldName = fieldName;
		this.equals = null;
		excludes = new Vector<String>();
		confirmed = false;
	}

	/**
	 * Creates a field fieldName of type _type with val either as equals or in
	 * excludes.
	 * 
	 * @param _fieldName
	 *            the field name
	 * @param _type
	 *            the field type
	 * @param val
	 *            the value
	 */
	public Field(String _fieldName, FieldType _type, String val) {
		setType(_type);
		this.fieldName = _fieldName;
		excludes = new Vector<String>();
		if (type == FieldType.EQUALS) {
			equals = val;
		} else if (type == FieldType.EXCLUDES) {
			addExcludeValue(val);
		}
		confirmed = false;
	}

	public void setType(FieldType type) {
		this.type = type;
	}

	public FieldType getType() {
		return type;
	}

	public void setEquals(String equals) {
		this.equals = equals;
	}

	/**
	 * returns the equals value or null if none is set
	 * 
	 * @return equals or null
	 */
	public String getEquals() {
		if (equals != null)
			return equals;
		else
			return null;
	}

	// public void setExcludes(Vector<String> excludes) {
	// this.excludes = excludes;
	// }

	public Vector<String> getExcludes() {
		return excludes;
	}

	@Override
	public Field clone() {
		Field field = new Field(this.fieldName, this.type);
		if (this.equals != null)
			field.equals = new String(this.equals);
		for (String s : this.excludes) {
			field.addExcludeValue(new String(s));
		}
		field.confirmed = this.confirmed;
		return field;
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public String toString() {
		String s = fieldName;
		if (type == FieldType.EQUALS) {
			s += "=" + equals;
		} else if (type == FieldType.EXCLUDES) {
			s += " x(";
			if (excludes.size() > 0) {
				s += excludes.elementAt(0);
				int i = 1;
				for (; i < 2 && i < excludes.size(); i++)
					s += "," + excludes.elementAt(i);
				if (i < excludes.size())
					s += ", ... " + (excludes.size() - i);
			}
			s += ")";
		}
		return s;
	}

	/**
	 * adds val to the excludes vector if it is not already in there.
	 * 
	 * @param val
	 *            the value to be excluded by this field
	 */
	public void addExcludeValue(String val) {
		if (!excludes.contains(val))
			excludes.add(val);
	}

	public boolean removeExcludeValue(String val) {
		return excludes.remove(val);
	}

	public boolean isConfirmed() {
		return confirmed;
	}

	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * @return the rejected
	 */
	public boolean isRejected() {
		return rejected;
	}

	/**
	 * @param rejected the rejected to set
	 */
	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}
}
