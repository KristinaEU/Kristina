package usersimulatorCamResInfoSys.sumpushacts;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;




import usersimulatorCamResInfoSys.Request;
import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryPushActType;
import usersimulatorCamResInfoSys.SummaryPushAction;
import usersimulatorCamResInfoSys.UserGoal;
import usersimulatorCamResInfoSys.sumconditions.OfferSumCond;


public class ReqaltsAXSumPushAct extends SummaryPushAction {
	private Map<String, String> slots;

	public ReqaltsAXSumPushAct(double prob, SummaryPushActType t) {
		super(prob, t);
		slots = new TreeMap<String, String>();
	}

	@Override
	public void extractInformation(SummaryCondition sc,  UserGoal g) {
		//this.setSource(sc.getScType().toString());
		this.mapping.put(sc.getScType(), this.spType);
		Vector<Request> req = g.getRequests();
		if (sc instanceof OfferSumCond) {

			for (Request r : req) {
				if (r.getValue() != null)
					slots.put(r.getSlot(), r.getValue());

			}
		} else {

			for (Request r : req) {

				slots.put(r.getSlot(), r.getValue());
			}
		}
			
		
	}
	public int getNumSlots() {
		if (slots == null)
			return 0;
		return slots.size();
	}

	public Vector<String> getSlots() {
		if (slots == null)
			return null;
		return new Vector<String>(slots.keySet());
	}

	public String getValue(String slot) {
		if (slots == null)
			return null;
		return slots.get(slot);
	}

	public Map<String, String> getAllSlots() {
		return slots;
	}

	public void addSlotValue(String slot, String value) {
		slots.put(slot, value);
	}

	public void addSlotValues(Vector<String> slots, Vector<String> values) {
		if (slots.size() != values.size())
			return; // TODO add exception
		for (int i = 0; i < slots.size(); i++) {
			addSlotValue(slots.get(i), values.get(i));
		}
	}

	@Override
	public String toString() {
		return super.toString();
	}

}
