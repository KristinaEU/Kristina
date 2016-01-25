package owlSpeak.engine.policy;

import java.util.Collection;
import java.util.Random;

import owlSpeak.Agenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.interfaces.ISummaryBelief;

public class TrainingPolicy extends Policy {

	static final double EPSILON = 0.3;
	
	private boolean printout = true;

	@Override
	public Agenda policy(Core core, OwlSpeakOntology onto, String user,
			UserAction userAct) {

		Random rnd1 = new Random();
		double rndDouble = rnd1.nextDouble();
		Agenda returnAgenda = null;
		ISummaryBelief sb;

		if (rndDouble > EPSILON) {
			int userpos = Settings.getuserpos(user);
			if (userAct != null)
				sb = new SummaryBeliefNonpersistent(
						onto.partitionDistributions[userpos],
						userAct.getMove(), onto);
			else
				sb = new SummaryBeliefNonpersistent(
						onto.partitionDistributions[userpos], null, onto);
			SummaryBelief closestSB = null;

			// find closest beliefPoint in summarySpace; Complexity O(n)
			for (SummaryBelief beliefPoint : onto.summarySpaceBeliefPoints) {
				if (sb.distanceTo(beliefPoint) < sb.distanceTo(closestSB)) {
					closestSB = beliefPoint;
				}
			}

			if (closestSB != null)
				returnAgenda = summaryAgenda2Agenda(
						closestSB.getSummaryAgenda(), onto, user);
		}

		if (returnAgenda == null) {

			// TODO Replace with SummaryAgendas?

			// Collection<SummaryAgenda> agendas =
			// onto.factory.getAllSummaryAgendaInstances();
			Collection<Agenda> agendas = onto.factory.getAllAgendaInstances();
			
			agendas = getValidAgendas(agendas,onto,user);
			
			if (agendas.isEmpty()) {
				System.err.println("No agendas defined in ontology.");
			} else {
				boolean agendaFound = false;
				do {
					int size = agendas.size();
					Random rnd2 = new Random();
					int rndPos = rnd2.nextInt(size);

					//SummaryAgenda[] arrayAgenda = new SummaryAgenda[0];
					Agenda[] arrayAgenda = new Agenda[0];
					arrayAgenda = agendas.toArray(arrayAgenda);
					//returnAgenda = summaryAgenda2Agenda(arrayAgenda[rndPos],
					//onto, user);
					
					returnAgenda = arrayAgenda[rndPos];
					
					if (!returnAgenda.getIsMasterBool())
						agendaFound = true;
				} while (agendaFound == false);
				if (printout) {
					if (rndDouble <= EPSILON)
						System.out.println("rndDouble = " + rndDouble + " < " + EPSILON
							+ " --------> Random agenda selected: " + returnAgenda);
					else 
						System.out.println("---------------------------> Random agenda selected: " + returnAgenda);
				}
			}
		}
		if (printout) System.out.println(returnAgenda.toString());
		return returnAgenda;
	}
}
