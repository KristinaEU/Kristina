package usersimulatorCamResInfoSys.sumconditions;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import usersimulatorCamResInfoSys.DialogueAct;
import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryConditionType;
import usersimulatorCamResInfoSys.dialogueacts.InformDialogueAct;


public class InformSumCond extends SummaryCondition {
	private Map<String, String> slots;

	public InformSumCond(SummaryConditionType t) {
		super(t);
		slots = new TreeMap<String, String>();
	}

	@Override
	public void extractInformation(DialogueAct a) {
		if (a instanceof InformDialogueAct) {
			InformDialogueAct iDA = (InformDialogueAct) a;
			Vector<String> vec = iDA.getSlots();
			for (int i = 0; i < vec.size(); i++) {
				slots.put(vec.elementAt(i), iDA.getValue(vec.elementAt(i)));
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

}
