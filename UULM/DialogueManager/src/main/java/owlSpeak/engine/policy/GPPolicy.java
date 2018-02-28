package owlSpeak.engine.policy;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;

import owlSpeak.Agenda;
import owlSpeak.SummaryAgenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.GPDialogueOptimizer;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.UserAction;


public class GPPolicy extends Policy {

	@Override
	public List<Agenda> policy(Core core, OwlSpeakOntology onto, String user, UserAction userAct) {
		
		// import the matrix my to calculate the mean of the Q-function
		Matrix my = null;
		try {
			my = MatrixFactory.importFromFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/my.csv", "\t");
		} catch (MatrixException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// update the ontology
		onto.update();
				
		// get all SummaryBeliefPoints of the ontology
		Collection<SummaryBelief> D = onto.factory.getAllSummaryBeliefInstances(); 
		
		// get actual SummaryBeliefPoint
		SummaryBeliefNonpersistent b;
		int userpos = Settings.getuserpos(user);
		if (userAct != null)
			b = new SummaryBeliefNonpersistent(
					onto.partitionDistributions[userpos], userAct.getMove(),
					onto);
		else
		{
			// initial belief 
			b = new SummaryBeliefNonpersistent(
					onto.partitionDistributions[userpos], null, onto);
			// select an agenda from the initial set of agendas loaded into the workspace
			Agenda[] next = onto.getWorks(userpos).getNext()
					.toArray(new Agenda[0]);
			List<Agenda> a = new LinkedList<Agenda>();
			a.add(next[0]);
			return a;
		}
		
		// get all SummaryAgendas
		SummaryAgenda[] allActions = b.getOntology().factory.getAllSummaryAgendaInstances().toArray(new SummaryAgenda[0]);
		
		SummaryAgenda agendaMax = null;
		Agenda returnAgenda = null;
		double maxMean = Double.NEGATIVE_INFINITY;
		
		// get the SummaryAgenda with the highest mean of the Q-value
		for (SummaryAgenda a : allActions) 
		{
			double meanQ = GPDialogueOptimizer.k_tilde(b, a, D).transpose().mtimes(my).getAsDouble(0, 0);
			if (meanQ > maxMean)
			{
				maxMean = meanQ;
				agendaMax = a;
			}
		}
		
		// get the appropriate Agenda to the SummaryAgenda with the highest mean of the Q-value
		returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
		
		if (returnAgenda == null) 
		{
			System.err.println("SummaryAgenda with the highest mean is not appropriate.");
			returnAgenda = onto.factory.getAgenda("agenda_Abort");
		}
		List<Agenda> result = new LinkedList<Agenda>();
		result.add(returnAgenda);
		return result;
	}
}
