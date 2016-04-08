package owlSpeak.engine.his.interfaces;

import owlSpeak.Move;
import owlSpeak.engine.his.Partition.PartitionState;

/**
 * This interface represents a point in summary belief space (part of CSBPVI). Each
 * point in belief space can be transformed to one point in the summary belief
 * space. The transform function is surjective.
 * 
 * The point in summary belief space contains the belief of the partitions with
 * the highest and the second highest belief. Further, it contains variables
 * defining the state of the partition with the highest belief and further a
 * variable defining the state of the history of the top partition. Further, it
 * contains a variable representing the last system action.
 * 
 * To each summary belief point, a summary agenda is related which has to be
 * learned during policy optimization.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * 
 */
public interface ISummaryBelief {
	
	public enum HistoryState {
		INIT, SUPPORTED, OFFERED, CONFIRMED, REJECTED, UNKNOWN
	}

	float getTopBelief();
	
	float getSecondTopBelief();

	HistoryState getHistoryState();

	PartitionState getPartitionState();
	
	Move getLastUserAction();
	
	int getPartitionNumFields();
	
	int getInteractionQuality();

	float distanceTo(ISummaryBelief sb);

}
