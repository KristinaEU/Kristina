package owlSpeak.engine.his;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.SummaryAgenda;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.Partition.PartitionState;
import owlSpeak.engine.his.interfaces.IPartitionDistribution;
import owlSpeak.engine.his.interfaces.ISummaryBelief;

/**
 * This class implements a point in summary belief space. Each
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
 * This implementation is non-persistent. The data is only stored in this
 * object. If it gets out of scope and the object is destroyed, the data is as
 * well.
 * 
 * This class is used for generating a summary belief point for the current
 * partition distribution in order to find the closest summary belief point to
 * use its action.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */
public class SummaryBeliefNonpersistent implements ISummaryBelief,
		Comparable<ISummaryBelief> {
	private float topBelief;
	private float secondTopBelief;
	private PartitionState pState;
	private HistoryState hState;
	private int pNumFields;
	private Move lastUserAction;
	private Agenda systemAction;
	private OwlSpeakOntology ontology;
	private int interactionQuality;
	

	public SummaryBeliefNonpersistent(
			IPartitionDistribution iPartitionDistribution, Move _lastUserAction, OwlSpeakOntology onto) {
		this(iPartitionDistribution,_lastUserAction,-1,onto);
	}
	
	public SummaryBeliefNonpersistent(
			IPartitionDistribution iPartitionDistribution, Move _lastUserAction, int _interactionQuality, OwlSpeakOntology onto) {
		Partition p = (Partition) iPartitionDistribution.getTopPartition();

		topBelief = p.getBelief();
		Partition secondTopPartition = ((Partition) iPartitionDistribution
				.getSecondTopPartition());
		if (secondTopPartition != null)
			secondTopBelief = secondTopPartition.getBelief();
		else
			secondTopBelief = 0.0f;
		pState = SummaryBelief.computePartitionState(p);
		hState = SummaryBelief.computeHistoryState(p);
//		hState = SummaryBelief.computeHistoryState(iPartitionDistribution
//				.getTopPartitionsHistory());

		// TODO handle user action
		lastUserAction = _lastUserAction;
		pNumFields = p.getFields().size();

		systemAction = null;
		
		interactionQuality = _interactionQuality;
		
		ontology = onto;
	}
	
	public SummaryBeliefNonpersistent(
			IPartitionDistribution iPartitionDistribution, Move _lastUserAction, OwlSpeakOntology onto, Agenda sysAction) {
		this(iPartitionDistribution, _lastUserAction, onto);
		systemAction = sysAction;
	}
	
	public SummaryBeliefNonpersistent(
			IPartitionDistribution iPartitionDistribution, Move _lastUserAction, int _interactionQuality, OwlSpeakOntology onto, Agenda sysAction) {
		this(iPartitionDistribution, _lastUserAction, _interactionQuality, onto);
		systemAction = sysAction;
	}
	
	public SummaryBeliefNonpersistent(float _topBelief, float _secondTopBelief, PartitionState _pState, HistoryState _hState, int _pNumFields, Move _lastUserAction, Agenda _systemAction, OwlSpeakOntology _ontology, int _interactionQuality) {
		topBelief = _topBelief;
		secondTopBelief = _secondTopBelief;
		pState = _pState;
		hState = _hState;
		pNumFields = _pNumFields;
		lastUserAction = _lastUserAction;
		systemAction = _systemAction;
		ontology = _ontology;
		interactionQuality = _interactionQuality;
	}

	/**
	 * This function computes a distance between this summary belief point and
	 * the one provided as parameter. The distance is 0 if both represent the
	 * same point.
	 * 
	 * @param sb
	 *            The summary belief the distance should be computed to.
	 * @return the distance to the summary belief sb
	 */

	@Override
	public float distanceTo(ISummaryBelief sb) {
		return SummaryBelief.distanceBetween(this, sb);
	}

	@Override
	public int compareTo(ISummaryBelief sb) {
		float distance = distanceTo(sb);
		if (distance == 0.0f)
			return 0;
		else if (distance < 0.0f)
			return -1;
		else
			return 1;
	}

	@Override
	public boolean equals(Object sb) {
		if (!(sb instanceof SummaryBeliefNonpersistent))
			return false;
		return distanceTo((SummaryBeliefNonpersistent) sb) == 0.0f ? true
				: false;
	}

	public SummaryAgenda getSummaryAgenda() {
		return systemAction.asSummaryAgenda();
	}
	
	public Agenda getSummaryAgendaAsAgenda() {
		return systemAction.asAgenda();
	}


	public void setSummaryAgenda(Agenda a) {
		systemAction = a;
	}

	@Override
	public float getTopBelief() {
		return topBelief;
	}

	public void setTopBelief(float topBelief) {
		this.topBelief = topBelief;
	}

	@Override
	public float getSecondTopBelief() {
		return secondTopBelief;
	}

	public void setSecondTopBelief(float secondTopBelief) {
		this.secondTopBelief = secondTopBelief;
	}

	@Override
	public PartitionState getPartitionState() {
		return pState;
	}

	public void setpState(PartitionState pState) {
		this.pState = pState;
	}

	@Override
	public HistoryState getHistoryState() {
		return hState;
	}

	public void sethState(HistoryState hState) {
		this.hState = hState;
	}

	@Override
	public int getPartitionNumFields() {
		return pNumFields;
	}

	public void setPartitionNumFields(int pNumFields) {
		this.pNumFields = pNumFields;
	}

	@Override
	public Move getLastUserAction() {
		return lastUserAction;
	}

	public void setLastUserAction(Move lastUserAction) {
		this.lastUserAction = lastUserAction;
	}

	public OwlSpeakOntology getOntology() {
		return ontology;
	}
	
	public void setOntology(OwlSpeakOntology ontology) {
		this.ontology = ontology;		
	}
	
	public void setInteractinQuality(int iq) {
		interactionQuality = iq;
	}
	
	@Override
	public int getInteractionQuality() {
		 return interactionQuality;
	}
	
	@Override
	public String toString() {
		String s = "NP:"+topBelief+":"+secondTopBelief+":"+lastUserAction+":"+pState+":"+hState+":"+systemAction;
		return s;
	}
}
