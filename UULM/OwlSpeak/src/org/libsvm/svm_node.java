package org.libsvm;

public class svm_node implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8348232470972195769L;
	public int index;
	public double value;

	@Override
	public String toString() {
		return String.format("(" + index + "|%1$10.6f)", value);
	}
}
