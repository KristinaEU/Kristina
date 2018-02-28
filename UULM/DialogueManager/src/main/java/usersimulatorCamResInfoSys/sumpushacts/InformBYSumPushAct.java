package usersimulatorCamResInfoSys.sumpushacts;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import usersimulatorCamResInfoSys.Constraint;
import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryPushActType;
import usersimulatorCamResInfoSys.SummaryPushAction;
import usersimulatorCamResInfoSys.UserGoal;
import usersimulatorCamResInfoSys.sumconditions.RequestSumCond;

public class InformBYSumPushAct extends SummaryPushAction {
	private Map<String, String> slots;

	public InformBYSumPushAct(double prob, SummaryPushActType t) {
		super(prob, t);
		slots = new TreeMap<String, String>();
	}

	public void extractInformation(SummaryCondition sc, UserGoal g,
			InformAXSumPushAct inf) {
		//this.setSource(sc.getScType().toString());
		this.mapping.put(sc.getScType(), this.spType);
		Vector<Constraint> vec = g.getConstraints();
		if (sc instanceof RequestSumCond) {

			RequestSumCond aSC = (RequestSumCond) sc;
			String con = aSC.getReq();

			for (Constraint c : vec) {
				if (!(c.getSlot().equals(con)))
					slots.put(c.getSlot(), c.getValue());
			}

		} else {
			Vector<String> svec = inf.getSlots();
			for (Constraint c : vec) {
				for (String s : svec) {
					if (!(c.getSlot().equals(s)))
						slots.put(c.getSlot(), c.getValue());
				}
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
		return super.toString() + slots.toString();
	}

	@Override
	public void extractInformation(SummaryCondition sc, UserGoal g) {
		// TODO Auto-generated method stub

	}
}
