package owlSpeak.engine.his;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import owlSpeak.SummaryAgenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;

public class DialogueOptimizer {

	private TreeMap<SummaryBelief, TreeMap<SummaryAgenda, Double>> Q;
	private TreeMap<SummaryBelief, TreeMap<SummaryAgenda, Integer>> N;

	private Set<OwlSpeakOntology> ontologies = new TreeSet<OwlSpeakOntology>();

	private float delta = 0.05f;
	private float gamma = 0.9f;

	private static boolean printToConsole = false;

	public DialogueOptimizer() {
		Q = new TreeMap<SummaryBelief, TreeMap<SummaryAgenda, Double>>();
		N = new TreeMap<SummaryBelief, TreeMap<SummaryAgenda, Integer>>();
	}

	public void optimize(DialogueOptimizationInfo info) {

		int numTurns = info.getNumTurns();
		double reward = info.getReturnValue();

		Vector<SummaryBeliefNonpersistent> v = info.getSummaryBeliefs();
		Vector<SummaryBelief> sbModified = new Vector<SummaryBelief>();

//		if (printToConsole)
			System.out.println("####################################");
//  	if (printToConsole)
			System.out.println("DIALOGUE OPTIMIZER");
//	  	if (printToConsole)
			System.out.println("####################################");

		// iterate over dialogue
		for (int t = numTurns - 1; t >= 0; t--) {

			if (printToConsole)
				System.out.println("####################################");
			if (printToConsole)
				System.out.println("Turn " + t);

			SummaryBeliefNonpersistent sbnpt = v.get(t);
			SummaryAgenda sat = sbnpt.getSummaryAgenda();

			boolean found = false;
			SummaryBelief sbk = null;
			float minDistance = delta;

			// iterate over all summary belief points in the ontology
			// to find a close enough grid point

			for (SummaryBelief sbCandidate : sbnpt.getOntology().summarySpaceBeliefPoints) {

				float distanceToCandidate = sbnpt.distanceTo(sbCandidate);

//				if (printToConsole)
//					System.out.println("distance to "
//							+ sbCandidate.getLocalName() + ": "
//							+ distanceToCandidate);

				if (distanceToCandidate < minDistance) {

					found = true;
					sbk = sbCandidate;
					minDistance = distanceToCandidate;

				}
			}

			if (printToConsole)
				System.out.println("min distance to "
						+ (sbk != null ? sbk.getLocalName()
								: "every point bigger than") + ": "
						+ minDistance);

			// Update of grid point closer than delta distance
			if (found) {

				double rew = 0;
				int times = 0;

				if (Q.containsKey(sbk)) {

					TreeMap<SummaryAgenda, Double> rewards = Q.get(sbk);

					if (rewards.containsKey(sat)) {

						rew = rewards.get(sat);

					}
				} else {

					Q.put(sbk, new TreeMap<SummaryAgenda, Double>());

				}

				if (N.containsKey(sbk)) {

					TreeMap<SummaryAgenda, Integer> nTimes = N.get(sbk);

					if (nTimes.containsKey(sat)) {

						times = nTimes.get(sat);

					}
				} else {

					N.put(sbk, new TreeMap<SummaryAgenda, Integer>());

				}

				double updatedQ = ((rew * times) + reward) / (times + 1);
				int updatedN = times + 1;
				Q.get(sbk).put(sat, updatedQ);
				N.get(sbk).put(sat, updatedN);

				sbModified.addElement(sbk);

			}

			// There is no grid point close enough, we create a new one.

			else {

				SummaryBelief sbt = sbnpt.getOntology().factory
						.createSummaryBelief(sbnpt);

				if (printToConsole)
					System.out
							.println("create new summary belief point in ontology: "
									+ sbt.getLocalName());

				// sbnpt.getOntology().write();
				// sbnpt.getOntology().update();
				ontologies.add(sbnpt.getOntology());

				TreeMap<SummaryAgenda, Double> hmRewards = new TreeMap<SummaryAgenda, Double>();
				hmRewards.put(sat, reward);
				Q.put(sbt, hmRewards);

				TreeMap<SummaryAgenda, Integer> hmTimes = new TreeMap<SummaryAgenda, Integer>();
				hmTimes.put(sat, 1);
				N.put(sbt, hmTimes);

			}

			reward *= gamma;

		}

		// System.out.println("####################################");

		// Policy update
		for (SummaryBelief sb : sbModified) {

			double topQ = Double.NEGATIVE_INFINITY;
			SummaryAgenda agenda = null;

			// System.out.println("Policy update, (sb,agenda,reward): ("+ sb
			// +", "+ Q.get(sb)+ " )" );

			TreeMap<SummaryAgenda, Double> hm = Q.get(sb);
			Iterator<SummaryAgenda> agendaSet = null;

			// TODO what if hm is null?

			if (hm != null)
				agendaSet = hm.keySet().iterator(); // Set of agendas mapped to
													// sb

			// We find the agenda that maximizes Q(sb, agenda)
			if (agendaSet != null) {
				while (agendaSet.hasNext()) {

					SummaryAgenda nextAgenda = agendaSet.next();

					if (hm.get(nextAgenda) > topQ) {

						topQ = hm.get(nextAgenda);
						agenda = nextAgenda;

					}

				}
			}

			// System.out.println("The agenda that maximizes Q is: " + agenda);

			assert agenda != null;

			if (agenda != null) {

				sb.setSummaryAgenda(agenda);

				OwlSpeakOntology.write(sb.manager, sb.onto);
				Core.getCore().updateAllOntolgies();
			}

			// else {
			// System.out.println("The agenda that maximizes Q is: " + agenda);
			// }

		}

		for (OwlSpeakOntology onto : ontologies) {
			onto.write();
			onto.update();
		}

	}

}
