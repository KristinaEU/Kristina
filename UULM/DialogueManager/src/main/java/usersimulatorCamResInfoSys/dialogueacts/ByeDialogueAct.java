package usersimulatorCamResInfoSys.dialogueacts;

import java.util.Map;
import java.util.Vector;

import usersimulatorCamResInfoSys.Constraint;
import usersimulatorCamResInfoSys.DialogueAct;
import usersimulatorCamResInfoSys.DialogueType;
import usersimulatorCamResInfoSys.Request;


public class ByeDialogueAct extends DialogueAct {
	private boolean isExit;

	public ByeDialogueAct(DialogueType t) {
		super(t);
		isExit = true;

	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
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
		return super.toString()+"()";
	}

	@Override
	public Map<Constraint, Integer> createLCMapping(Vector<Constraint> vec) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void mergeActs(DialogueAct d) {
		// TODO Auto-generated method stub
		
	}
}
