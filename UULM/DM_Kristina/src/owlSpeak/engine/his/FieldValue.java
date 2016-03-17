package owlSpeak.engine.his;

public class FieldValue {
	
	public enum ConfirmationInfo {CONFIRM, REJECT, UNDEFINED};
	
	private String value;
	private ConfirmationInfo confirmationInfo;

	public FieldValue () {
		this(new String(),ConfirmationInfo.UNDEFINED);
	}
	
	public FieldValue (String val) {
		this(val,ConfirmationInfo.UNDEFINED);
	}
	
	public FieldValue (ConfirmationInfo confInfo) {
		this(new String(),confInfo);
	}
	public FieldValue(String val, ConfirmationInfo confInfo) {
		value = val;
		confirmationInfo = confInfo;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public ConfirmationInfo getConfirmationInfo() {
		return confirmationInfo;
	}

	public void setConfirmationInfo(ConfirmationInfo confirmationInfo) {
		this.confirmationInfo = confirmationInfo;
	}
	
	@Override
	public String toString() {
		String s = getValue() + ":" + getConfirmationInfo();
		return s;
	}
	
	@Override
	public boolean equals(Object v) {
		if (v instanceof FieldValue) {
			FieldValue fv = (FieldValue)v;
			return (fv.value.equalsIgnoreCase(this.value) && fv.confirmationInfo == this.confirmationInfo);
		}
		return false;
	}
}
