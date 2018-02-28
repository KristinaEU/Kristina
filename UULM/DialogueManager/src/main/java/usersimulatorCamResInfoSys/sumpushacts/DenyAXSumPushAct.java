package usersimulatorCamResInfoSys.sumpushacts;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import usersimulatorCamResInfoSys.Constraint;
import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryPushActType;
import usersimulatorCamResInfoSys.SummaryPushAction;
import usersimulatorCamResInfoSys.UserGoal;
import usersimulatorCamResInfoSys.sumconditions.ConfirmNotOKSumCond;

public class DenyAXSumPushAct extends SummaryPushAction {
	private Map<String, String> slots;

	public DenyAXSumPushAct(double prob, SummaryPushActType t) {
		super(prob, t);
		slots = new TreeMap<String, String>();
	}

	@Override
	public void extractInformation(SummaryCondition sc, UserGoal g) {
	//	this.setSource(sc.getScType().toString());
		this.mapping.put(sc.getScType(), this.spType);
		if (sc instanceof ConfirmNotOKSumCond) {
			ConfirmNotOKSumCond aSC = (ConfirmNotOKSumCond) sc;
			Vector<String> vec = aSC.getSlots();
			for (int i = 0; i < vec.size(); i++) {
				addSlotValue(vec.elementAt(i), aSC.getValue(vec.elementAt(i)));
			}
		}
		else{
			Vector<Constraint> con = g.getConstraints();
			for(Constraint c: con){
				slots.put(c.getSlot(), c.getValue());
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
		return super.toString()+slots.toString();
	}


}
