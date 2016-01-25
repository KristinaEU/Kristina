package owlSpeak;

import java.util.Collection;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class SummaryAgenda extends Agenda {

	public enum SummaryAgendaType {
		REQUEST, CONFIRMATION, ANNOUNCEMENT, IMPLICIT, UNDEFINED 
	}

	public SummaryAgenda(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
		// TODO Auto-generated constructor stub
	}


	public void setType(String oldType, String newType){
    	GenericProvider.removeStringData(indi, oldType, GenericProvider.summaryAgendaTypeProp, onto, factory, manager);
    	GenericProvider.addStringData(indi, newType, GenericProvider.summaryAgendaTypeProp, onto, factory, manager);
	}
	
	public String getTypeString() {

		if(GenericProvider.hasDataProperty(indi,
				GenericProvider.summaryAgendaTypeProp, onto, factory) == true)
		{
			return GenericProvider.getStringData(indi,
					GenericProvider.summaryAgendaTypeProp, onto, factory);
		}
		
		else return "undefined";
	}
	
	/**
	 * derives the property from the ontology and returns its equivalent
	 * SummaryAgendaType object. If no specification is given in the ontology,
	 * SummaryAgendaType.UNDEFINED is returned.
	 * 
	 * @return SummaryAgendaType derived from ontology or SummaryAgendaType.UNDEFINED
	 */
	public SummaryAgendaType getType() {
		String s = GenericProvider.getStringData(indi,
				GenericProvider.summaryAgendaTypeProp, onto, factory);
		if (s.equalsIgnoreCase("confirmation"))
			return SummaryAgendaType.CONFIRMATION;
		else if (s.equalsIgnoreCase("request"))
			return SummaryAgendaType.REQUEST;
		else if (s.equalsIgnoreCase("announcement"))
			return SummaryAgendaType.ANNOUNCEMENT;
		else if (s.equalsIgnoreCase("implicit"))
			return SummaryAgendaType.IMPLICIT;
		else
			return SummaryAgendaType.UNDEFINED;
	}
	
	/**
	 * sets the role of the agenda
	 */
    public void setRole(String oldRole, String newRole){
    	super.setRole(oldRole, newRole);
    }
	
	
	public void addSummarizedAgenda (Agenda agenda){
		GenericProvider.addIndividual(indi, agenda.indi, GenericProvider.summarizesAgendasProp, onto, factory, manager);
	}

	public Collection<Agenda> getSummarizedAgendas() {
		Collection<GenericClass> col = GenericProvider.getIndividualColl(indi, GenericProvider.summarizesAgendasProp, onto, factory, manager);
		Vector<Agenda> agendas = new Vector<Agenda>();
		if (col != null) {
			for (GenericClass c : col) {
				// TODO convert to Agenda and return
				agendas.add(c.asAgenda());
			}
		}
		return agendas;
	}
}
