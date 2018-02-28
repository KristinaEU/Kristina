package usersimulatorCamResInfoSys.dialogueacts;

import java.util.Map;
import java.util.Vector;

import usersimulatorCamResInfoSys.Constraint;
import usersimulatorCamResInfoSys.DialogueAct;
import usersimulatorCamResInfoSys.DialogueType;
import usersimulatorCamResInfoSys.Request;


public class RequestDialogueAct extends DialogueAct {
	
	String slot;

	public RequestDialogueAct(DialogueType t){
		super(t);
		
	}
	public String getSlot() {
		return slot;
	}
	public void setSlot(String slot) {
		this.slot = slot;
	}
	@Override
	public void extractInformation(Constraint c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void extractInformation(Request r) {
		slot = r.getSlot();
		
	}
	
	@Override
	public String toString() {
		return super.toString() + "(" + slot + ")"; 
	}
	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			RequestDialogueAct rda = (RequestDialogueAct) o;
			if(slot != null)
			return slot.equals(rda.slot);
		}
		return false;	
	}
	@Override
	public Map<Constraint, Integer> createLCMapping(Vector<Constraint> vec) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void mergeActs(DialogueAct d) {
		
			
		
		
	}
	

}
