package owlSpeak.engine.his.sysAction;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;

/**
 * An implicit system action combines confirmation and request hence
 * representing a separate kind of system action.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */
public class ImplicitSystemAction extends GenericSystemAction {

	Map<String, String> fields;

	public ImplicitSystemAction() {
		super(SummaryAgendaType.IMPLICIT);
	}

	public ImplicitSystemAction(Agenda agenda, Core core) {
		super(SummaryAgendaType.IMPLICIT, agenda, core);
	}

	@Override
	public void processAgenda(Agenda agenda, Core core) {
		if (fields == null)
			fields = new HashMap<String, String>();

		for (Move m : agenda.getHas()) {
			OwlSpeakOntology onto = core.findMatchingOntology(agenda);
			IUserActionSupplementary supp = UserAction.getUserActionSupp(
					onto.factory, onto.ontoHISvariant);
			Set<String> moveFields;
			try {
				moveFields = supp.extractFieldNameFromMove(m);
			} catch (NoFactorySetException e) {
				System.err
						.println("Field name cannot be extracted from move as internally, the OSfactory has not been set.");
				continue;
			}
			if (fields != null && moveFields != null) {
				for (String field : moveFields) {
					if (!fields.containsKey(field))
						fields.put(field, null);
				}
			}
		}
	}

	public String getValue(String field) {
		return fields.get(field);
	}

	@Override
	public void setValue(String f, String s) {
		fields.put(f, s);
	}

	@Override
	public void setField(String s) {
		if (fields.size() == 1) {
			String val = null;
			for (String k : fields.keySet()) {
				val = fields.get(k);
				fields.remove(k);
			}
			fields.put(s, val);

		} else if (fields.size() == 0) {
			fields.put(s, null);
		}
	}

	@Override
	public String getField() {
		if (fields.size() == 1) {
			for (String k : fields.keySet()) {
				return k;
			}
		}
		return null;
	}

	@Override
	public Set<String> getFields() {
		return fields.keySet();
	}

	public boolean hasField() {
		return !fields.isEmpty();
	}

	@Override
	public String toString() {
		String s = this.getType() + " [";
		boolean first = true;
		for (String field : fields.keySet()) {
			if (!first)
				s += ":";
			s += field + "=" + fields.get(field);
			first = false;
		}
		s += "]";
		return s;
	}
}
