package owlSpeak.engine.his.factories;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import owlSpeak.engine.his.interfaces.INBestList;
import owlSpeak.engine.his.interfaces.IUserAction;

public class NBestListFactory {
	private PyObject nBestListClass;
	private int numActions;
	public static int sNumActions;

	/**
	 * Create a new PythonInterpreter object, then use it to execute some python
	 * code. In this case, we want to import the python module that we will
	 * coerce.
	 * 
	 * Once the module is imported than we obtain a reference to it and assign
	 * the reference to a Java variable
	 * 
	 * TODO only for this system act or in general?
	 * 
	 * @param _numActions
	 *            Total number of possible user actions
	 */
	public NBestListFactory(int _numActions) {
		PythonInterpreter interpreter = new PythonInterpreter();
//		interpreter.exec("from asdt.Utils import NBestList"); TODO change to asdt package
		interpreter.exec("from Utils import NBestList");
		nBestListClass = interpreter.get("NBestList");
		numActions = _numActions;
	}

	public NBestListFactory() {
		PythonInterpreter interpreter = new PythonInterpreter();
//		interpreter.exec("from asdt.Utils import NBestList"); TODO change to asdt package
		interpreter.exec("from Utils import NBestList");		
		nBestListClass = interpreter.get("NBestList");
		numActions = sNumActions;
	}

	/**
	 * The create method is responsible for performing the actual coercion of
	 * the referenced python module into Java bytecode
	 */
	public INBestList create(IUserAction[] userActions, double[] confidences) {
		return create(userActions, confidences, this.numActions);
	}

	public INBestList create(IUserAction[] userActions, double[] confidences,
			int _numActions) {
		PyObject nBestListObject = nBestListClass.__call__();
		INBestList dist = (INBestList) nBestListObject
				.__tojava__(INBestList.class);
		dist.initialize(userActions, confidences, _numActions);
		return dist;
	}
}
