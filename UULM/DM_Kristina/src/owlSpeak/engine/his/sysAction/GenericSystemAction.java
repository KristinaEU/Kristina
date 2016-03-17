package owlSpeak.engine.his.sysAction;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import owlSpeak.Agenda;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.Core;
import owlSpeak.engine.his.interfaces.ISystemAction;

/**
 * TODO What does it do?
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * 
 */
public abstract class GenericSystemAction implements ISystemAction {

	private SummaryAgendaType type;
	private Agenda agenda;
	private Map<String, String> values = new TreeMap<String, String>();
	private TreeSet<String> fields = new TreeSet<String>();

	public GenericSystemAction(SummaryAgendaType _type) {
		type = _type;
	}

	public GenericSystemAction(SummaryAgendaType _type, Agenda _agenda,
			Core _core) {
		type = _type;
		this.agenda = _agenda;
		processAgenda(_agenda, _core);
	}

	@Override
	public Agenda getAgenda() {
		return agenda;
	}

	/**
	 * returns the type of this action
	 * 
	 * @return the action type
	 */
	public SummaryAgendaType getType() {
		return type;
	}

	/**
	 * returns a system action of correct type according to type of the summary
	 * agenda to which the given agenda belongs to.
	 * 
	 * @param agenda
	 *            the owl agenda
	 * @param core
	 *            TODO
	 * @return system action object which extends GenericSystemAction
	 */
	public static GenericSystemAction createSystemAction(Agenda agenda,
			Core core) {

		if (agenda != null && core != null) {
			SummaryAgendaType _type = agenda.getSummaryAgenda().getType();

			switch (_type) {
			case ANNOUNCEMENT:
				return new AnnouncementSystemAction(agenda, core);
			case REQUEST:
				return new QuestionSystemAction(agenda, core);
			case CONFIRMATION:
				return new ConfirmationSystemAction(agenda, core);
			case IMPLICIT:
				return new ImplicitSystemAction(agenda, core);
			default:
				return createSystemAction();
			}
		} else {
			return createSystemAction();
		}
	}

	/**
	 * creates a dummy system action not belonging to any type
	 * 
	 * @return new GenericSystemAction not belonging to any type
	 */
	public static GenericSystemAction createSystemAction() {
		return null;
	}

	/**
	 * this method is called by the GenericSysAction constructor with agenda
	 * parameter
	 * 
	 * @param agenda
	 * @param core
	 *            TODO
	 */
	public abstract void processAgenda(Agenda agenda, Core core);
	
	// TODO check
	public String getValue(String field) {
		return values.get(field);
	}

	public void setValue(String field, String v) {
		for (String f : fields) {
			if (f.equals(field))
				values.put(f, v);
		}
	}

	public void setField(String s) {
		fields.add(s);
	}

	// TODO remove
	public String getField() {
		if (fields.size() == 1)
			return fields.first();
		else if (values.size() == 1) {
			String fn = null;
			for (String key : values.keySet()) {
				fn = key;
			}
			return fn;
		}
		return null;
	}

	public boolean hasField() {
		return (fields != null && !fields.isEmpty());
	}

	public Set<String> getFields() {
		return fields;
	}

	@Override
	public String toString() {
		String s = "";
		for (String f : fields) {
			s += f + "=" + values.get(f) + ";";
		}
		return type + " [" + s + "]";
	}
}
