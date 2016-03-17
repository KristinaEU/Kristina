package owlSpeak.engine.his.interfaces;

import org.python.core.PyString;

/**
 * Needed by ASDT jython implementation.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 *
 */
public interface INBestList {
	public void initialize(IUserAction[] userActions, double[] confidences, int numActions);
	public PyString __str__();
	public String toString();
}
