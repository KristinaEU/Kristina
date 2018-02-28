package owlSpeak.engine.his;

import owlSpeak.engine.his.interfaces.IHistory;
import owlSpeak.engine.his.interfaces.IPartition;
import owlSpeak.engine.his.interfaces.IUserAction;

public class History implements IHistory {

	@Override
	public boolean __eq__(IHistory otherHistory) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Update(IPartition partition, IUserAction userAction,
			Object sysAction) {
		// TODO Auto-generated method stub

	}

	@Override
	public IHistory Copy() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String __str__() {
		// TODO Auto-generated method stub
		return null;
	}

}
