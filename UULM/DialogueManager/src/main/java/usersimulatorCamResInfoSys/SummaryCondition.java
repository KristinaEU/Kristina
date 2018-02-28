package usersimulatorCamResInfoSys;


import java.util.List;
import java.util.Vector;

import usersimulatorCamResInfoSys.sumconditions.AffirmSumCond;
import usersimulatorCamResInfoSys.sumconditions.ByeSumCond;
import usersimulatorCamResInfoSys.sumconditions.CantHelpSumCond;
import usersimulatorCamResInfoSys.sumconditions.ConfirmNotOKSumCond;
import usersimulatorCamResInfoSys.sumconditions.ConfirmOKSumCond;
import usersimulatorCamResInfoSys.sumconditions.HelloSumCond;
import usersimulatorCamResInfoSys.sumconditions.InformSumCond;
import usersimulatorCamResInfoSys.sumconditions.OfferSumCond;
import usersimulatorCamResInfoSys.sumconditions.ReqMoreSumCond;
import usersimulatorCamResInfoSys.sumconditions.RequestSumCond;
import usersimulatorCamResInfoSys.sumconditions.SelectSumCond;

/**
 * SummaryCondition class mapping the large group of possible machine acts to a
 * smaller group of Summary Conditions
 * 
 * @author mkraus
 * 
 */
public abstract class SummaryCondition {

	protected SummaryConditionType scType;

	public SummaryCondition(SummaryConditionType t) {
		this.scType = t;
	}

	public abstract void extractInformation(DialogueAct a);

	/**
	 * generates new SummaryConditions depending on the type of the Condition
	 * 
	 * @param t
	 * @return new SummaryCondition
	 */
	public static SummaryCondition createSumCond(SummaryConditionType t) {

		switch (t) {
		case ReceiveHello:
			return new HelloSumCond(t);
		case ReceiveAffirmAX:
			return new AffirmSumCond(t);
		case ReceiveBye:
			return new ByeSumCond(t);
		case ReceiveConfirmAXnotOk:
			return new ConfirmNotOKSumCond(t);
		case ReceiveConfirmAXok:
			return new ConfirmOKSumCond(t);
		case ReceiveInformAX:
			return new InformSumCond(t);
		case ReceiveRequestA:
			return new RequestSumCond(t);
		case ReceiveSelect:
			return new SelectSumCond(t);
		case ReceiveReqMore:
			return new ReqMoreSumCond(t);
		case ReceiveOffer:
			return new OfferSumCond(t);
		case ReceiveCantHelp:
			return new CantHelpSumCond(t);
		default:
			break;
		}
		return null;
	}

	/**
	 * Creates a vector of SummaryConditions out of the current System Action
	 * and the UserGoal
	 * 
	 * @param sa
	 * @param g
	 * @return vector of SummaryConditions
	 */
	public static Vector<SummaryCondition> createSumCondVec(SystemAction sa,
			UserGoal g) {
		Vector<SummaryCondition> vec = new Vector<SummaryCondition>();
		SummaryCondition sc = null;
		List<DialogueAct> list = sa.getDialActList();
		
		if (!list.isEmpty()) {
		
			for (DialogueAct a: list) {
					for (SummaryConditionType t : a.getSumCondTypeFromDA(g)) {
					sc = createSumCond(t);
					sc.extractInformation(a);
					vec.add(sc);
				}
			}
		}

		return vec;
	}

	

	public SummaryConditionType getScType() {
		return scType;
	}

	public void setScType(SummaryConditionType scType) {
		this.scType = scType;
	}

	@Override
	public String toString() {
		return "" + scType;
	}
}
