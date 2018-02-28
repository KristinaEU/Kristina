package owlSpeak.engine.policy;

import java.util.LinkedList;
import java.util.List;

import owlSpeak.Agenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.interfaces.ISummaryBelief;

public class HISPolicy extends Policy {

	@Override
	public List<Agenda> policy(Core core, OwlSpeakOntology onto, String user,
			UserAction userAct) {
		Agenda returnAgenda = null;
		ISummaryBelief sb;
		int userpos = Settings.getuserpos(user);
		if (userAct != null)
			sb = new SummaryBeliefNonpersistent(
					onto.partitionDistributions[userpos], userAct.getMove(), onto);
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
			returnAgenda = summaryAgenda2Agenda(closestSB.getSummaryAgenda(),
					onto, user);
		List<Agenda> result = new LinkedList<Agenda>();
		result.add(returnAgenda);
		return result;
	}

//	/**
//	 * Heuristic for determining the Agenda out of the SummaryAgenda.<br />
//	 * <br />
//	 * For user user, selects the action with highest priority out of workspace
//	 * whose requirements are satisfied based on the most likely partition (only
//	 * field "requires", field "must not" is not regarded) and which belongs to
//	 * SummaryAgenda sumarySystemAction
//	 * 
//	 * @param summaryAgenda
//	 *            the summary agenda
//	 * @param user
//	 *            the user name
//	 * 
//	 * @return the selected agenda
//	 */
//	private Agenda summaryAgenda2Agenda(SummaryAgenda summaryAgenda,
//			OwlSpeakOntology onto, String user) {
//		TreeSet<Agenda> availableAgendas = new TreeSet<Agenda>();
//
//		for (Agenda a : summaryAgenda.getSummarizedAgendas()) {
//			WorkSpace ws = onto.workSpace[Settings.getuserpos(user)];
//			Collection<Agenda> agendasInWS = ws.getNext();
//			Vector<Semantic> availableSemantics = new Vector<Semantic>();
//			for (Belief b : onto.beliefSpace[Settings.getuserpos(user)]
//					.getHasBelief()) {
//				availableSemantics.addAll(b.getSemantic());
//			}
//
//			if (agendasInWS.contains(a)) {
//				/*
//				 * check if agenda is in ws
//				 */
//				if (a.matchRequires(availableSemantics))
//					/*
//					 * check only if "requires" condition is fulfilled and not
//					 * if "mustNot" is fulfilled
//					 */
//					availableAgendas.add(a);
//			}
//		}
//		if (!availableAgendas.isEmpty())
//			return availableAgendas.last();
//
//		return null;
//
//		// SummaryAgendaType type = summarySystemAction.getType();
//		// TreeSet<Agenda> availableAgendas = new TreeSet<Agenda>();
//		//
//		// for (Agenda a : getAgendasFromSummaryAgendaType(type, user)) {
//		// WorkSpace ws =
//		// actualOnto[Settings.getuserpos(user)].workSpace[Settings
//		// .getuserpos(user)];
//		// Collection<Agenda> agendasInWS = ws.getNext();
//		// Vector<Semantic> availableSemantics = new Vector<Semantic>();
//		// for (Belief b :
//		// actualOnto[Settings.getuserpos(user)].beliefSpace[Settings
//		// .getuserpos(user)].getHasBelief()) {
//		// availableSemantics.addAll(b.getSemantic());
//		// }
//		//
//		// if (agendasInWS.contains(a)) {
//		// /*
//		// * check if agenda is in ws
//		// */
//		// if (a.matchRequires(availableSemantics))
//		// /*
//		// * check only if "requires" condition is fulfilled and not
//		// * if "mustNot" is fulfilled
//		// */
//		// availableAgendas.add(a);
//		// }
//		// }
//		// if (availableAgendas.isEmpty())
//		// return null;
//		// return availableAgendas.last();
//	}
}
