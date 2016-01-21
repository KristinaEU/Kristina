package owlSpeak.engine.his;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Agenda;
import owlSpeak.GenericClass;
import owlSpeak.GenericProvider;
import owlSpeak.Move;
import owlSpeak.SummaryAgenda;
import owlSpeak.engine.his.Partition.PartitionState;
import owlSpeak.engine.his.interfaces.IHistory;
import owlSpeak.engine.his.interfaces.IPartition;
import owlSpeak.engine.his.interfaces.ISummaryBelief;

/**
 * This class implements a point in summary belief space. Each point in belief
 * space can be transformed to one point in the summary belief space. The
 * transform function is surjective.
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
 * This implementation is persistent. The data is stored in an owl ontology and
 * each access to the class members is passed to the ontology.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */
public class SummaryBelief extends GenericClass implements ISummaryBelief {
	private static int summaryBeliefNumber = 0;
	
//	weights = {TopBelief, 2nd TopBelief, historyState, partitionState, last UserAction};
	
//	private static float[] weights = {0.2f, 0.2f, 0.2f, 0.2f, 0.2f};	
//	private static float[] weights = {0.5f, 0.5f, 0.2f, 0.2f, 0.2f};
	private static float[] weights = {0.4f, 0.4f, 0.2f, 0.2f, 0.3f};

	public SummaryBelief(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);

	}

	/**
	 * Takes a Partition object and computes its state.
	 * 
	 * @param p
	 *            the Partition object the state is computed of
	 * @return the partition state of partition p
	 */
	public static PartitionState computePartitionState(Partition p) {
		Map<String, Field> fields = p.getFields();
		boolean atLeastOneEquals = false;
		boolean allEqual = true;

		for (Field f : fields.values()) {
			if (f.getEquals() != null)
				atLeastOneEquals = true;
			else
				allEqual = false;
		}

		if (allEqual)
			return PartitionState.UNIQUE;
		else if (atLeastOneEquals)
			return PartitionState.GROUP;
		else
			return PartitionState.GENERIC;
	}

	/**
	 * Takes a IHistory object and computes its state.
	 * 
	 * @param h
	 *            the IHistory object the state is computed of.
	 * @return the history state of history h
	 */
	public static HistoryState computeHistoryState(IHistory h) {
		return HistoryState.INIT;
	}
	
	
	/**
	 * Takes an IPartition object to compute the grounding state
	 *  
	 * @param p an IPartition object (currently, only Partition class is supported)
	 * @return the identified HistoryState
	 */
	public static HistoryState computeHistoryState(IPartition p) {
		HistoryState h = HistoryState.INIT;
		if (p instanceof Partition) {
			for (String f : ((Partition) p).getFields().keySet()) {
				if (((Partition) p).getFields().get(f).isConfirmed()) {
					if (h == HistoryState.INIT)
						h = HistoryState.CONFIRMED;
				}
				else if (((Partition) p).getFields().get(f).isRejected()) {
					h = HistoryState.REJECTED;
				}
				else {
					if (h == HistoryState.CONFIRMED)
						h = HistoryState.SUPPORTED;
				}
			}
		}
		return h;
	}

	public static String historyState2String(HistoryState state) {
		if (state == null)
			return null;
		return state.toString();
	}

	public static HistoryState string2HistoryState(String state) {
		if (state == null)
			return null;

		return HistoryState.valueOf(state);
	}

	/**
	 * This function computes a distance between summary belief point sb1 and
	 * summary belief point sb2. The distance is 0 if both represent the same
	 * point.
	 * 
	 * 
	 * @param sb1
	 *            The first summary belief point
	 * @param sb2
	 *            The second summary belief point
	 * @return the distance between sb1 and sb2
	 */

	public static float distanceBetween(ISummaryBelief sb1, ISummaryBelief sb2) {
		
		if (sb1 == null || sb2 == null)
		return Float.POSITIVE_INFINITY;

		float distance = 0.0f;
		float dist[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
//		float dist[] = {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};

		dist[0] += Math.abs(sb1.getTopBelief() - sb2.getTopBelief());
		dist[1] += Math
				.abs(sb1.getSecondTopBelief() - sb2.getSecondTopBelief());
//		dist[2] += Math.abs(sb1.getInteractionQuality() - sb2.getInteractionQuality());

		if (sb2.getHistoryState() != sb1.getHistoryState())
			dist[2] += 1.0f;
		if (sb2.getPartitionState() != sb1.getPartitionState())
			dist[3] += 1.0f;
		if ((sb1.getLastUserAction() == null && sb2.getLastUserAction() != null) || (sb1.getLastUserAction() != null && sb2.getLastUserAction() == null))
			dist[4] += 1.0f;
		else if (sb1.getLastUserAction() != null && !sb1.getLastUserAction().equals(sb2.getLastUserAction()))
			dist[4] += 1.0f;
		
		for (int i = 0; i < dist.length && dist.length == weights.length; i++)
			distance += dist[i] * weights[i];

		return distance;
		
	}

	/*
	 * non-static methods
	 */

	@Override
	public float getTopBelief() {
		return GenericProvider.getFloatProperty(indi,
				GenericProvider.topBeliefValueProp, onto, factory);
	}

	public void setTopBelief(float f) {
		if (hasTopBelief()) {
			removeTopBelief(getTopBelief());
		}
		GenericProvider.addFloatData(indi, f,
				GenericProvider.topBeliefValueProp, onto, factory, manager);
	}

	public boolean hasTopBelief() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.topBeliefValueProp, onto, factory);
	}

	public void removeTopBelief(float f) {
		GenericProvider.removeFloatData(indi, f,
				GenericProvider.topBeliefValueProp, onto, factory, manager);
	}

	@Override
	public float getSecondTopBelief() {
		return GenericProvider.getFloatProperty(indi,
				GenericProvider.secondTopBeliefValueProp, onto, factory);
	}

	public void setSecondTopBelief(float belief) {
		if (hasSecondTopBelief()) {
			removeSecondTopBelief(getSecondTopBelief());
		}
		GenericProvider.addFloatData(indi, belief,
				GenericProvider.secondTopBeliefValueProp, onto, factory,
				manager);
	}

	public boolean hasSecondTopBelief() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.secondTopBeliefValueProp, onto, factory);
	}

	public void removeSecondTopBelief(float f) {
		GenericProvider.removeFloatData(indi, f,
				GenericProvider.secondTopBeliefValueProp, onto, factory,
				manager);
	}

	@Override
	public PartitionState getPartitionState() {
		if (!hasPartitionState())
			return null;
		return Partition.string2PartitionState(GenericProvider.getStringData(
				indi, GenericProvider.partitionStateProp, onto, factory));
	}

	public void setPartitionState(PartitionState state) {
		if (hasPartitionState()) {
			removePartitionState(getPartitionState());
		}
		GenericProvider.addStringData(indi,
				Partition.partitionState2String(state),
				GenericProvider.partitionStateProp, onto, factory, manager);
	}

	public boolean hasPartitionState() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.partitionStateProp, onto, factory);
	}

	public void removePartitionState(PartitionState state) {
		GenericProvider.removeStringData(indi,
				Partition.partitionState2String(state),
				GenericProvider.partitionStateProp, onto, factory, manager);
	}

	@Override
	public HistoryState getHistoryState() {
		if (!hasHistoryState())
			return null;
		return string2HistoryState(GenericProvider.getStringData(indi,
				GenericProvider.historyStateProp, onto, factory));
	}

	public void setHistoryState(HistoryState state) {
		if (hasHistoryState()) {
			removeHistoryState(getHistoryState());
		}
		GenericProvider.addStringData(indi, historyState2String(state),
				GenericProvider.historyStateProp, onto, factory, manager);
	}

	public boolean hasHistoryState() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.historyStateProp, onto, factory);
	}

	public void removeHistoryState(HistoryState state) {
		GenericProvider.removeStringData(indi, historyState2String(state),
				GenericProvider.historyStateProp, onto, factory, manager);
	}

	@Override
	public int getPartitionNumFields() {
		return GenericProvider.getIntProperty(indi,
				GenericProvider.partitionNumFieldsProp, onto, factory);
	}

	public void setPartitionNumFields(int size) {
		if (hasPartitionNumFields()) {
			removePartitionNumFields(getPartitionNumFields());
		}
		GenericProvider.addIntData(indi, size,
				GenericProvider.partitionNumFieldsProp, onto, factory, manager);
	}

	public boolean hasPartitionNumFields() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.partitionNumFieldsProp, onto, factory);
	}

	public void removePartitionNumFields(int size) {
		GenericProvider.removeIntData(indi, size,
				GenericProvider.partitionNumFieldsProp, onto, factory, manager);
	}

	@Override
	public Move getLastUserAction() {
		if (!GenericProvider.hasObjectProperty(indi,
				GenericProvider.lastGrammarMoveProp, onto, factory))
			return null;
		GenericClass gen = GenericProvider.getIndividual(indi,
				GenericProvider.lastGrammarMoveProp, onto, factory, manager);
		return gen.asMove();
	}

	public void setLastUserAction(Move move) {
		if (move == null)
			return;
		if (hasLastUserAction()) {
			removeLastUserAction(getLastUserAction());
		}
		GenericProvider.addIndividual(indi, move.indi,
				GenericProvider.lastGrammarMoveProp, onto, factory, manager);

	}

	public boolean hasLastUserAction() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.lastGrammarMoveProp, onto, factory);
	}

	public void removeLastUserAction(Move move) {
		if (move == null)
			return;
		GenericProvider.removeIndividual(indi, move.indi,
				GenericProvider.lastGrammarMoveProp, onto, factory, manager);
	}

	public SummaryAgenda getSummaryAgenda() {
		if (!hasSummaryAgenda())
			return null;
		GenericClass gen = GenericProvider
				.getIndividual(indi, GenericProvider.summaryBeliefAgendaProp,
						onto, factory, manager);
		return gen.asSummaryAgenda();
	}
	
	public Agenda getSummaryAgendaAsAgenda() {
		if (!hasSummaryAgenda())
			return null;
		GenericClass gen = GenericProvider
				.getIndividual(indi, GenericProvider.summaryBeliefAgendaProp,
						onto, factory, manager);
		return gen.asAgenda();
	}

//	public void setSummaryAgenda(SummaryAgenda agenda) {
//		if (hasSummaryAgenda()) {
//			removeSummaryAgenda(getSummaryAgenda());
//		}
//		GenericProvider
//				.addIndividual(indi, agenda.indi,
//						GenericProvider.summaryBeliefAgendaProp, onto, factory,
//						manager);
//	}
	
	public void setSummaryAgenda(Agenda agenda) {
		if (hasSummaryAgenda()) {
			removeSummaryAgenda(getSummaryAgendaAsAgenda());
		}
		GenericProvider
				.addIndividual(indi, agenda.indi,
						GenericProvider.summaryBeliefAgendaProp, onto, factory,
						manager);
	}


	public boolean hasSummaryAgenda() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.summaryBeliefAgendaProp, onto, factory);
	}

//	/**
//	 * removes the summary agenda association form the owl individual
//	 * 
//	 * @param agenda
//	 *            the agenda object representing the owl individual of type
//	 *            SummaryAgenda
//	 */
//	public void removeSummaryAgenda(SummaryAgenda agenda) {
//		GenericProvider
//				.removeIndividual(indi, agenda.indi,
//						GenericProvider.summaryBeliefAgendaProp, onto, factory,
//						manager);
//	}
	
	/**
	 * removes the summary agenda association form the owl individual
	 * 
	 * @param agenda
	 *            the agenda object representing the owl individual of type
	 *            SummaryAgenda
	 */
	public void removeSummaryAgenda(Agenda agenda) {
		GenericProvider
				.removeIndividual(indi, agenda.indi,
						GenericProvider.summaryBeliefAgendaProp, onto, factory,
						manager);
	}
	
	@Override
	public int getInteractionQuality() {
		return GenericProvider.getIntProperty(indi,
				GenericProvider.interactionQualityProp, onto, factory);
	}
	public void setInteractionQuality(int i) {
		if (hasInteractionQuality()) {
			removeInteractionQuality(getInteractionQuality());
		}
		GenericProvider.addIntData(indi, i,
				GenericProvider.interactionQualityProp, onto, factory, manager);
	}
	public boolean hasInteractionQuality() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.interactionQualityProp, onto, factory);
	}
	public void removeInteractionQuality(int i) {
		GenericProvider.removeIntData(indi, i,
				GenericProvider.interactionQualityProp, onto, factory, manager);
	}

	/**
	 * This function computes a distance between this summary belief point and
	 * the one provided as parameter. The distance is 0 if both represent the
	 * same point. distance &gt;= 0
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
	public int compareTo(GenericClass gc) {
		if (gc instanceof SummaryBelief) {
			SummaryBelief sb = (SummaryBelief) gc;
			float distance = distanceTo(sb);
			if (distance == 0.0f)
				return 0;
			else if (distance < 0.0f)
				return -1;
			else
				return 1;
		} else
			return super.compareTo(gc);
	}

	@Override
	public boolean equals(Object sb) {
		if (!(sb instanceof SummaryBelief))
			return false;
		return distanceTo((SummaryBelief) sb) == 0.0f ? true : false;
	}
	
	public static int getAndIncreaseSummaryBeliefNumber() {
		return ++summaryBeliefNumber;
	}
	
	public static int setAndIncreaseSummaryBeliefNumber(int newSize) {
		summaryBeliefNumber = newSize;
		return getAndIncreaseSummaryBeliefNumber();
	}
	
	@Override
	public String toString() {
		String s = getLocalName()
				+":"+getTopBelief()
				+":"+getSecondTopBelief()
				+":"+getLastUserAction()
				+":"+getPartitionState()
				+":"+getHistoryState()
				+":"+getSummaryAgenda()
				+":"+getInteractionQuality();
		return s;
	}
}
