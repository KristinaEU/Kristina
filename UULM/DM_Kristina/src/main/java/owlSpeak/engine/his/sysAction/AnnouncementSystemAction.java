package owlSpeak.engine.his.sysAction;

import owlSpeak.Agenda;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.Core;

public class AnnouncementSystemAction extends GenericSystemAction {

	public AnnouncementSystemAction() {
		super(SummaryAgendaType.ANNOUNCEMENT);
	}

	public AnnouncementSystemAction(Agenda agenda, Core core) {
		super(SummaryAgendaType.ANNOUNCEMENT, agenda, core);
	}

	@Override
	public void processAgenda(Agenda agenda, Core core) {
		// nothing to do here
	}

}
