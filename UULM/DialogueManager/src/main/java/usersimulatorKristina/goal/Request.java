package usersimulatorKristina.goal;
/**
 * 
 * Request Object
 * 
 * @author: Isabel Feustel
 * 
 */

public class Request {

	private String identifier;
	private String val;

	// Has the Request been answered?
	private boolean answered;
	// Count how often the user requested this
	private int count;

	public Request(String id) {
		setAnswered(false);
		identifier = id;
		count = 0;
	}

	public Request(String id, String val) {
		setAnswered(false);
		identifier = id;
		this.val = val;
		count = 0;
	}

	public String getIdentifier() {
		return identifier;
	}

	public boolean checkId(String id) {
		if (this.identifier.equals(id)) {
			return true;
		} else {
			return false;
		}
	}

	public String getVal() {
		return val;
	}

	public boolean isAnswered() {
		return answered;
	}

	public void setAnswered(boolean answered) {
		this.answered = answered;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void putCount() {
		this.count += 1;
	}

	
	
}
