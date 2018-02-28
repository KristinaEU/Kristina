package usersimulatorCamResInfoSys.sumconditions;

import usersimulatorCamResInfoSys.DialogueAct;
import usersimulatorCamResInfoSys.DialogueType;
import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryConditionType;
import usersimulatorCamResInfoSys.dialogueacts.ConfreqDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RequestDialogueAct;

public class RequestSumCond extends SummaryCondition {
	String req;

	public RequestSumCond(SummaryConditionType t) {
		super(t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void extractInformation(DialogueAct a) {
		if(a.getType() == DialogueType.Confreq){
			ConfreqDialogueAct rDA = (ConfreqDialogueAct) a;
		this.req = rDA.getReq();}
		else {RequestDialogueAct rDA = (RequestDialogueAct) a;
		this.req = rDA.getSlot();}

	}

	public String getReq() {
		return req;
	}

	public void setReq(String req) {
		this.req = req;
	}

	@Override
	public String toString() {
		return super.toString() + "(" + req + ")";
	}
}
