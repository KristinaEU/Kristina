package owlSpeak.engine.his;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.Partition.HISvariant;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IUserAction;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;
import owlSpeak.engine.his.sysAction.GenericSystemAction;
import owlSpeak.engine.his.sysAction.ImplicitSystemAction;

/**
 * The UserAction class encapsulates a user action which consists of a Move
 * object and a Map defining fields with their addressed Semantics.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.2
 * 
 */
public abstract class UserAction implements IUserAction {
	private static Core core = null;
	protected IUserActionSupplementary supp = null;

	public static UserAction creatNewUserAction(OwlSpeakOntology onto, UserActionType type, String user) {
		return creatNewUserAction(null,null,onto,type, user);
	}
	
	public static UserAction creatNewUserAction(Move value, String speak,
			OwlSpeakOntology onto, UserActionType type, String user) {
		switch (onto.ontoHISvariant) {
		case SEM:
			return new UserActionSemantics(value, speak, onto, type, user);
		case VAR:
			return new UserActionVariables(value, speak, onto, type, user);
		}
		return null;
	}

	public static IUserActionSupplementary getUserActionSupp(
			OwlSpeakOntology onto) {
			switch (onto.ontoHISvariant) {
			case SEM:
				return UserActionSemanticsSupplementary
						.createUserActionSemanticsSupplementary(onto.factory);
			case VAR:
				return UserActionVariablesSupplementary
						.createUserActionVariablesSupplementary(onto.factory);
			}
			return null;
		}

	public static UserAction creatNewUserAction(Move value, Core _core,
			UserActionType type, String user) {

		core = _core;
		OwlSpeakOntology onto = core.findMatchingOntology(value);
		switch (onto.ontoHISvariant) {
		case SEM:
			return new UserActionSemantics(value, null, onto, type, user);
		case VAR:
			return new UserActionVariables(value, null, onto, type, user);
		}
		return null;
	}
	
	public static UserAction creatNewUserAction(OwlSpeakOntology onto, Core _core,
			UserActionType type, String user) {

		core = _core;
		switch (onto.ontoHISvariant) {
		case SEM:
			return new UserActionSemantics(null, null, onto, type, user);
		case VAR:
			return new UserActionVariables(null, null, onto, type, user);
		}
		return null;
	}

	public static IUserActionSupplementary getUserActionSupp(OSFactory factory,
			HISvariant variant) {
		switch (variant) {
		case SEM:
			return UserActionSemanticsSupplementary
					.createUserActionSemanticsSupplementary(factory);
		case VAR:
			return UserActionVariablesSupplementary
					.createUserActionVariablesSupplementary(factory);
		}
		return null;
	}

	public IUserActionSupplementary getSupp() {
		return supp;
	}

	/**
	 * 
	 * in accordance to ASDT
	 * 
	 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
	 * 
	 */
	public enum UserActionType {
		SILENT, IG, OOG, IMPLICIT;
	}

	private UserActionType type;
	protected Map<String, FieldValue> fields;
	private Move move; // TODO change to vector of moves?
	private GenericSystemAction systemAction;
	private String speak;


	protected UserAction(Move value, String speak,
			OwlSpeakOntology onto, IUserActionSupplementary _supp, UserActionType _type, String user) {
		fields = new TreeMap<String, FieldValue>();
		move = value;
		supp = _supp;
		type = _type;
		this.speak = speak;

		if (value != null) {
			parseMove(value, speak, onto, user);
		}
	}

	public GenericSystemAction getAgendaType() {
		return systemAction;
	}

	public void setSystemAct(GenericSystemAction _sysAct) {
		this.systemAction = _sysAct;
	}

	public UserActionType getType() {
		return type;
	}

	@Override
	public Vector<String> getFieldVector() {
		if (fields == null)
			return null;
		return new Vector<String>(fields.keySet());
	}

	@Override
	public FieldValue getFieldValue(String field) {
		return fields.get(field);
	}

	@Override
	public String __str__() {
		String outputString = type + " [";
		boolean first = true;
		for (String key : fields.keySet()) {
			if (first) {
				outputString += key + ":" + fields.get(key);
				first = false;
			} else {
				outputString += ";" + key + ":" + fields.get(key);
			}
		}
		outputString += "]";
		if (speak != null && speak != "")
			outputString += " \""+speak+"\"";
		return outputString;
	}

	@Override
	public String toString() {
		return __str__();
	}

	public static void setSystemAction(UserAction[] userActions,
			GenericSystemAction sysAct) {
		if (userActions == null)
			return;
		for (UserAction u : userActions) {
			if (u.getMove().onto.equals(sysAct.getAgenda().onto))
					u.setSystemAct(sysAct);
		}
	}

	public SummaryAgendaType getSysActType() {
		if (systemAction != null)
			return systemAction.getType();
		return SummaryAgendaType.UNDEFINED;
	}

	public String getConfirmationValue(String field) {
		if (getSysActType() == SummaryAgendaType.CONFIRMATION)
			return systemAction.getValue(field);
		else if (getSysActType() == SummaryAgendaType.IMPLICIT)
			return ((ImplicitSystemAction)systemAction).getValue(field);
		return null;
	}

	public Move getMove() {
		return move;
	}

	/**
	 * assumption: move only addresses one field
	 * 
	 * @return the field name addressed by the move
	 */
	public Set<String> getMoveFields() {
		try {
			return supp.extractFieldNameFromMove(move);
		} catch (NoFactorySetException e) {
			System.err
					.println("Field name cannot be extracted from move as internally, the OSfactory has not been set.");
			return null;
		}
	}

	public void addAdditionalMove(Move value, OwlSpeakOntology onto, String user) {
		if (value != null)
			parseMove(value, null, onto, user);
	}

	public abstract void parseMove(Move value, String speak, OwlSpeakOntology onto, String user);

	/**
	 * @return the speak
	 */
	public String getSpeak() {
		return speak;
	}

	/**
	 * @param speak the speak to set
	 */
	public void setSpeak(String speak) {
		this.speak = speak;
	}
}
