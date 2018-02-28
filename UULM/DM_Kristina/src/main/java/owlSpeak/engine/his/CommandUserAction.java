package owlSpeak.engine.his;

import java.util.Vector;

import owlSpeak.engine.his.interfaces.IUserAction;

public class CommandUserAction implements IUserAction {
	
	private String sysCommand;

	public CommandUserAction (String systemCommand) {
		this.setSysCommand(systemCommand);
	}

	@Override
	public String __str__() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector<String> getFieldVector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldValue getFieldValue(String field) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the sysCommand
	 */
	public String getSysCommand() {
		return sysCommand;
	}

	/**
	 * @param sysCommand the sysCommand to set
	 */
	public void setSysCommand(String sysCommand) {
		this.sysCommand = sysCommand;
	}

}
