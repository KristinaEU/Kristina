package owlSpeak.engine.his.sysAction;

import java.util.Set;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.Semantic;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;

public class ConfirmationSystemAction extends GenericSystemAction {

	private String confirmationField;

	public ConfirmationSystemAction() {
		super(SummaryAgendaType.CONFIRMATION);
	}

	public ConfirmationSystemAction(Agenda agenda, Core core) {
		super(SummaryAgendaType.CONFIRMATION, agenda, core);
	}

	@Override
	public void processAgenda(Agenda agenda, Core core) {
		for (Move m : agenda.getHas()) {
			if (!m.hasConfirmationInfo())
				continue;
			OwlSpeakOntology onto = core.findMatchingOntology(agenda);
			IUserActionSupplementary supp = UserAction.getUserActionSupp(
					onto.factory, onto.ontoHISvariant);
			Set<String> moveFields;
			try {
				// create new method which only extracts fields to be confirmed (check semantics for this)
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

			for (Semantic s : m.getSemantic()) {
				boolean testForConfirmation = s.hasConfirmationInfo();
				testForConfirmation &= (testForConfirmation ? s.getConfirmationInfo() : false);
				if (testForConfirmation) {
					String confField = s.getSemanticGroup().getLocalName();
					if (confirmationField == null
							|| confirmationField.equalsIgnoreCase(confField))
						confirmationField = confField;
					else if (!confirmationField.equalsIgnoreCase(confField))
						System.err
								.println("Agenda allows confirmation of more than one slot.");

				}
			}
			
			if (confirmationField != null && !confirmationField.isEmpty() && !this.getFields().contains(confirmationField))
				this.setField(confirmationField);
		}

		return;
	}

	public String getConfirmationField() {
		return confirmationField;
	}
}
