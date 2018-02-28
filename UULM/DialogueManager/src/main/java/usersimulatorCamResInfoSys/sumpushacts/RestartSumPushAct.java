package usersimulatorCamResInfoSys.sumpushacts;

import usersimulatorCamResInfoSys.SummaryCondition;
import usersimulatorCamResInfoSys.SummaryPushActType;
import usersimulatorCamResInfoSys.SummaryPushAction;
import usersimulatorCamResInfoSys.UserGoal;

public class RestartSumPushAct extends SummaryPushAction {

	public RestartSumPushAct(double prob, SummaryPushActType t) {
		super(prob, t);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void extractInformation(SummaryCondition sc, UserGoal g) {
		//this.setSource(sc.getScType().toString());
		this.mapping.put(sc.getScType(), this.spType);

	}

	@Override
	public String toString() {
		return super.toString()+"()";
	}
}
