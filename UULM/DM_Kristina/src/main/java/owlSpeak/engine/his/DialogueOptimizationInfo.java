package owlSpeak.engine.his;

import java.util.Vector;

public class DialogueOptimizationInfo {
	private int returnValue;	
	private Vector<SummaryBeliefNonpersistent> summaryBeliefs;
//	OwlSpeakOntology onto;
		
	public DialogueOptimizationInfo() {
		reset();
	}
	
	/**
	 * Resets all information marking the beginning of a new dialogue.
	 */
	public void reset() {
		returnValue = 0;
		if (summaryBeliefs == null)
			summaryBeliefs = new Vector<SummaryBeliefNonpersistent>();
		summaryBeliefs.clear();
	}

	/**
	 * @return the returnValue
	 */
	public int getReturnValue() {
		return returnValue;
	}

	/**
	 * @param returnValue the returnValue to set
	 */
	public void setReturnValue(int returnValue) {
		this.returnValue = returnValue;
	}

	public void addReward(int rewardFromAgenda) {
		returnValue += rewardFromAgenda;
	}

	/**
	 * @return the summaryBeliefs
	 */
	public Vector<SummaryBeliefNonpersistent> getSummaryBeliefs() {
		return summaryBeliefs;
	}

	/**
	 * @param summaryBeliefs the summaryBeliefs to set
	 */
	public void setSummaryBeliefs(Vector<SummaryBeliefNonpersistent> summaryBeliefs) {
		this.summaryBeliefs = summaryBeliefs;
	}
	
	public void addSummaryBelief(SummaryBeliefNonpersistent bel) {
		this.summaryBeliefs.add(bel);
	}
	
	public int getNumTurns () {
		if (summaryBeliefs != null)
			return summaryBeliefs.size();
		return 0;
	}
}
