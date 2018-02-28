package usersimulatorCamResInfoSys.sumpushacts;


import java.util.Vector;

import usersimulatorCamResInfoSys.Request;
import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryPushActType;
import usersimulatorCamResInfoSys.SummaryPushAction;
import usersimulatorCamResInfoSys.UserGoal;

import usersimulatorCamResInfoSys.sumconditions.OfferSumCond;

public class RequestASumPushAct extends SummaryPushAction {
	Vector<String> slot;

	public RequestASumPushAct(double prob, SummaryPushActType t) {
		super(prob, t);
		slot = new Vector<String>();
	}

	@Override
	public void extractInformation(SummaryCondition sc, UserGoal g) {
		//this.setSource(sc.getScType().toString());
		this.mapping.put(sc.getScType(), this.spType);
		Vector<Request> req = g.getRequests();
		if (sc instanceof OfferSumCond) {

			for (Request r : req) {
				if (r.getValue() == null)
					slot.add(r.getSlot());

			}
		} else {

			for (Request r : req) {

				slot.add(r.getSlot());
			}
		}
	}

	public Vector<String> getSlot() {
		return slot;
	}

	public void setSlot(Vector<String> slot) {
		this.slot = slot;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + slot + ")";
	}

}
