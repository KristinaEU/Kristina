package org.libsvm;

public class svm_problem implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8510687680361136369L;
	public int l;
	public double[] y;
	public svm_node[][] x;
}
