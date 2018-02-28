package usersimulatorCamResInfoSys.dialogueacts;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import usersimulatorCamResInfoSys.Constraint;
import usersimulatorCamResInfoSys.DialogueAct;
import usersimulatorCamResInfoSys.DialogueType;
import usersimulatorCamResInfoSys.Request;


public class InformDialogueAct extends DialogueAct {

	private Map<String, String> slots;

	public InformDialogueAct(DialogueType t) {
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
		slots.put(c.getSlot(), c.getValue());
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
	public boolean equals(Object o) {
		if (super.equals(o)) {
			InformDialogueAct ida = (InformDialogueAct) o;
			return slots.equals(ida.slots);
		}
		return false;
	}

	@Override
	public Map<Constraint, Integer> createLCMapping(Vector<Constraint> vec) {
		Map<Constraint,Integer> m = new TreeMap<Constraint,Integer>();
		int s = 0;
		Vector<String> slotVec = this.getSlots();
		
		for(Constraint c: vec){
			String slot= c.getSlot();
			String value= c.getValue();
			for(String a: slotVec){
				if(slot.equals(a) && value.equals(this.getValue(a)))
					s=1;
				m.put(c, s);
			}
		}
		return m;
	}

	@Override
	public void mergeActs(DialogueAct d) {
		if(d instanceof InformDialogueAct){
			InformDialogueAct inform = (InformDialogueAct) d;
			Vector<String> v = inform.getSlots();
			for(String s: v)
				this.addSlotValue(s, inform.getValue(s));
			
		}
	}

	
}
