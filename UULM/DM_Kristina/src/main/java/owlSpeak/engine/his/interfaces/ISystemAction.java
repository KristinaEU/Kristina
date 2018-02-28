package owlSpeak.engine.his.interfaces;

import owlSpeak.Agenda;
import owlSpeak.SummaryAgenda.SummaryAgendaType;

public interface ISystemAction {
	
	public SummaryAgendaType getType(); 
	
	public Agenda getAgenda();
}
