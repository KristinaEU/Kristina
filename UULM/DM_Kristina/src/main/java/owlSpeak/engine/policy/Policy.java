package owlSpeak.engine.policy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import owlSpeak.Agenda;
import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.Semantic;
import owlSpeak.SummaryAgenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.UserAction;

public abstract class Policy {

	public static int sumAgenad2AgendaNextBeliefSpaceCounter = 0;

	public enum PolicyVariant {
		HEURISTIC, HIS, TRAINING, GP, TRAINING_GP, HEURISTIC_WOWS
	}

	public static Policy getPolicy(String user, OwlSpeakOntology onto) {
		switch (onto.ontoPolicyVariant) {
		case HIS:
			return new HISPolicy();
		case TRAINING:
			return new TrainingPolicy();
		/*case TRAINING_GP:
			return new TrainingGPPolicy();
		case GP:
			return new GPPolicy();   //not used as TRAINING_GP is also used for evaluation*/
		case HEURISTIC_WOWS:
			return new HeuristicPolicyAllAgendas();
		case HEURISTIC:
		default:
			return new HeuristicPolicy();

		}
	}

	/**
	 * Heuristic for determining the Agenda out of the SummaryAgenda.<br />
	 * <br />
	 * For user user, selects the action with highest priority out of workspace
	 * whose requirements are satisfied based on the most likely partition (only
	 * field "requires", field "must not" is not regarded) and which belongs to
	 * SummaryAgenda sumarySystemAction
	 * 
	 * @param summaryAgenda
	 *            the summary agenda
	 * @param user
	 *            the user name
	 * 
	 * @return the selected agenda
	 */
	protected Agenda summaryAgenda2Agenda(SummaryAgenda summaryAgenda,
			OwlSpeakOntology onto, String user, boolean descentIntoPartitionTree) {
		TreeSet<Agenda> availableAgendas = new TreeSet<Agenda>();
		
		if (summaryAgenda == null)
			return null;

		// Collection<Agenda> wsAgendas =
		// onto.workSpace[Settings.getuserpos(user)].getNext();

		sumAgenad2AgendaNextBeliefSpaceCounter = 0;

		BeliefSpace bs = null;

		do {
			bs = getNextBeliefSpace(onto, user,
					sumAgenad2AgendaNextBeliefSpaceCounter);
			if (bs != null
					&& (sumAgenad2AgendaNextBeliefSpaceCounter < 1 || descentIntoPartitionTree)) {

				for (Agenda a : summaryAgenda.getSummarizedAgendas()) {
					// -------------- modified by Stefan

					if (checkAgendaRequirements(a, bs, onto, user, true, true)) {
						// if (wsAgendas.contains(a))
						availableAgendas.add(a);
					}
				}
			}
			sumAgenad2AgendaNextBeliefSpaceCounter++;

		} while (availableAgendas.isEmpty() && descentIntoPartitionTree);

		System.out.println("sumagenda2agenda sumAgenda:" + summaryAgenda + " bs:"
				+ bs + " counter:" + sumAgenad2AgendaNextBeliefSpaceCounter
				+ " numAvailableAgendas:" + availableAgendas.size());

		if (!availableAgendas.isEmpty()){
			
			// it is not allowed to choose agenda_Abort
			if (availableAgendas.contains(onto.factory.getAgenda("agenda_Abort")))
				availableAgendas.remove(onto.factory.getAgenda("agenda_Abort"));
			
			Agenda[] available = availableAgendas.toArray(new Agenda[0]);
			int index = new Random().nextInt(available.length);
			return available[index];
//			return availableAgendas.last();
		}
			

		sumAgenad2AgendaNextBeliefSpaceCounter = 0;
		return null;
	}

	protected Agenda summaryAgenda2Agenda(SummaryAgenda summaryAgenda,
			OwlSpeakOntology onto, String user) {
		return summaryAgenda2Agenda(summaryAgenda, onto, user, true);
	}

	protected Agenda summaryAgenda2AgendaSoft(SummaryAgenda summaryAgenda,
			OwlSpeakOntology onto, String user) {
		TreeSet<Agenda> availableAgendas = new TreeSet<Agenda>();

		if (summaryAgenda == null)
			return null;

		sumAgenad2AgendaNextBeliefSpaceCounter = 0;

		// Collection<Agenda> wsAgendas =
		// onto.workSpace[Settings.getuserpos(user)].getNext();

		BeliefSpace bs = null;

		while (availableAgendas.isEmpty()
				&& (bs = getNextBeliefSpace(onto, user,
						sumAgenad2AgendaNextBeliefSpaceCounter)) != null) {
			for (Agenda a : summaryAgenda.getSummarizedAgendas()) {
				// -------------- modified by Stefan

				if (checkAgendaRequirements(a, bs, onto, user, false, false)) {
					// if (wsAgendas.contains(a))
					availableAgendas.add(a);
				}
			}
			sumAgenad2AgendaNextBeliefSpaceCounter++;
		}

		if (!availableAgendas.isEmpty())
			return availableAgendas.last();

		sumAgenad2AgendaNextBeliefSpaceCounter = 0;
		return null;
	}

	/**
	 * Selects only agendas whose preconditions are fulfilled.
	 * 
	 * @param colA
	 *            Collection of agendas to select from
	 * @param onto
	 *            ontology agendas belong to
	 * @param user
	 *            user name
	 * @return Collection of Agendas whose preconditions are fulfilled. May be
	 *         empty.
	 */
	protected Collection<Agenda> getValidAgendas(Collection<Agenda> colA,
			OwlSpeakOntology onto, String user) {
		ArrayList<Agenda> returnA = new ArrayList<Agenda>();

		sumAgenad2AgendaNextBeliefSpaceCounter = 0;

		BeliefSpace bs = null;
		while (returnA.isEmpty()

				&& (bs = getNextBeliefSpace(onto, user,
						sumAgenad2AgendaNextBeliefSpaceCounter)) != null) {
			for (Agenda a : colA) {
				if (checkAgendaRequirements(a, bs, onto, user, true, false))
					returnA.add(a);
			}
			sumAgenad2AgendaNextBeliefSpaceCounter++;
		}

		if (returnA.isEmpty())
			sumAgenad2AgendaNextBeliefSpaceCounter = 0;
		return returnA;
	}

	private BeliefSpace getNextBeliefSpace(OwlSpeakOntology onto,
			String username, int count) {
		BeliefSpace bs = null;

		if (onto.hasPartitionDistribution()) {
			if (count == 0)
				bs = (Partition) onto.partitionDistributions[Settings
						.getuserpos(username)].getTopPartition();
			else if (count == 1)
				bs = (Partition) onto.partitionDistributions[Settings
						.getuserpos(username)].getSecondTopPartition();
		} else if (count == 0)
			bs = onto.beliefSpace[Settings.getuserpos(username)];

		return bs;
	}

	/**
	 * Checks whether all preconditions of agenda a are fulfilled.
	 * 
	 * @param a
	 *            the agenda to be tested
	 * @param onto
	 *            the ontology the agenda belongs to
	 * @param user
	 *            the user name string
	 * @param mustNot
	 *            set to true if mustnot conditions should be checked
	 * @param checkVariableOp
	 *            set to true if conditions in the variable operator should be
	 *            checked
	 * @return ture if all conditions of agenda a are fulfilled, else false
	 */
	protected boolean checkAgendaRequirements(Agenda a, BeliefSpace bs,
			OwlSpeakOntology onto, String user, boolean mustNot,
			boolean checkVariableOp) {
		Vector<Semantic> availableSemantics = new Vector<Semantic>();

		for (Belief b : bs.getHasBelief()) {
			availableSemantics.addAll(b.getSemantic());
		}

		boolean requirementsFulfilled = true;
		requirementsFulfilled &= a.matchRequires(availableSemantics);
		if (requirementsFulfilled && mustNot)
			requirementsFulfilled &= !a.matchMustNot(availableSemantics);
		if (requirementsFulfilled && checkVariableOp)
			requirementsFulfilled &= a.matchRequiresVariableOP(onto, user);

		return requirementsFulfilled;
	}

	public abstract Agenda policy(Core core, OwlSpeakOntology onto,
			String user, UserAction userAct);
}
