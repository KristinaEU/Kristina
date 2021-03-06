package usersimulatorCamResInfoSys.dialogueacts;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import usersimulatorCamResInfoSys.Constraint;
import usersimulatorCamResInfoSys.DialogueAct;
import usersimulatorCamResInfoSys.DialogueType;
import usersimulatorCamResInfoSys.Request;


public class DenyDialogueAct extends DialogueAct {
	private Map<String, String> slots;

	public DenyDialogueAct(DialogueType t) {
		super(t);
		slots = new TreeMap<String, String>();
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
	public void extractInformation(Constraint c) {
		// TODO Auto-generated method stub

	}

	@Override
	public void extractInformation(Request r) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public String toString() {
		return super.toString() + slots.toString();
	}

	@Override
	public Map<Constraint, Integer> createLCMapping(Vector<Constraint> vec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mergeActs(DialogueAct d) {
		if(d instanceof DenyDialogueAct){
			DenyDialogueAct inform = (DenyDialogueAct) d;
			Vector<String> v = inform.getSlots();
			for(String s: v)
				this.addSlotValue(s, inform.getValue(s));
			
		}
		
	}
}
