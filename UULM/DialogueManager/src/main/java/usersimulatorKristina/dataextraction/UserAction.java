package usersimulatorKristina.dataextraction;

/**
 * 
 * Simple Object for saving user model depending data
 * 
 * @author Ifeustel
 *
 */
public class UserAction {

	protected String userDA;
	protected String prevSysDA;
	protected int amount;
	protected double probability;
	
	
	public UserAction(String userDA, String prevSysDA, int amount) {
		this.userDA = userDA;
		this.prevSysDA = prevSysDA;
		this.amount = amount;
	}

}
