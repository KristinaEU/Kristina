package owlSpeak.engine.his.sysAction;

import java.util.Set;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;

public class QuestionSystemAction extends GenericSystemAction {

	public QuestionSystemAction() {
		super(SummaryAgendaType.REQUEST);
	}

	public QuestionSystemAction(Agenda agenda, Core core) {
		super(SummaryAgendaType.REQUEST, agenda, core);
	}

	@Override
	public void processAgenda(Agenda agenda, Core core) {
		for (Move m : agenda.getHas()) {
			Set<String> moveFields;
			try {
				OwlSpeakOntology onto = core.findMatchingOntology(agenda);
				IUserActionSupplementary supp = UserAction.getUserActionSupp(
						onto.factory, onto.ontoHISvariant);
				moveFields = supp.extractFieldNameFromMove(m);
			} catch (NoFactorySetException e) {
				System.err
						.println("Field name cannot be extracted from move as internally, the OSfactory has not been set.");
				continue;
			}
			if (moveFields != null) {
				Set<String> fields = getFields();
				for (String field : moveFields) {
					if (!fields.contains(field))
						setField(field);
				}
			}
		}
	}
}
