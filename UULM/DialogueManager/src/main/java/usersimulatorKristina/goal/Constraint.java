package usersimulatorKristina.goal;
/**
 * 
 * Constraint Object
 * With identifier (e.g. Location) and the corresponding values (e.g. City)
 * 
 * @author: iFeustel
 * 
 */


public class Constraint {

	private String identifier;
	private String value;
	
	public Constraint(String id, String val){
		identifier = id;
		setValue(val);
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String values) {
		this.value = values;
	}
	
	public boolean checkId(String id){
		if(this.identifier.equals(id)){
			return true;
		} else{
			return false;
		}
	}
	
	
	
}
