package owlSpeak.engine.policy;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Vector;

import owlSpeak.Agenda;
import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.WorkSpace;
import owlSpeak.engine.AgendaPrio;
import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.UserAction;

public class HeuristicPolicyAllAgendas extends Policy {

	@Override
	public Agenda policy(Core core, OwlSpeakOntology onto, String user,
			UserAction userAct) {
		int userpos = Settings.getuserpos(user);	
		BeliefSpace BS = onto.beliefSpace[userpos];
		OSFactory factory = onto.factory;
		WorkSpace neu = onto.workSpace[userpos]; // neede for agenda priority computation
		
		Collection<Agenda> agendas = factory.getAllAgendaInstances();
		
//		Agenda returnAgenda = null;

		TreeMap<Integer, Collection<Agenda>> tree = new TreeMap<Integer, Collection<Agenda>>();
		Vector<Semantic> semantics = new Vector<Semantic>();
//		Belief tempBel;
		
//		System.out.println("*********Policy*************");
//		System.out.println("BeliefState: " + BS);
		
	
//		String nextAgendas = "";
//		for (Agenda a : master.getNext()) {
//			nextAgendas += a.toString() + "; ";
//		}
//		System.out.println("Workspace: " + nextAgendas);

		for (Belief b : BS.getHasBelief()) {
			semantics.addAll(b.getSemantic());
		}
		
		for (Agenda temp1 : agendas) {
			if (temp1.getIsMasterBool())
				continue;
			
			//System.out.println("\tchecking Agenda " + temp1);
			boolean matchRequirements = temp1.matchRequires(semantics);
			//System.out.println("\t  matching requirements: " + matchRequirements);
			if (matchRequirements) {
				Iterator<Semantic> mustnot = null;
				try {
					mustnot = temp1.getMustnot().iterator();

				} catch (NullPointerException e) {
				}
				;
				boolean mustfound = false;
				try {
					while (mustnot.hasNext()) {
						if (mustnot.next().isMemberOf(semantics))
							mustfound = true;
					}
				} catch (NullPointerException e) {
				}
				
//				System.out.println("\t  fulfilling mustnot requirements: " + !mustfound);
				
				if (mustfound) {
					continue;
				}
				if (temp1.hasVariableOperator()) {
					String input = temp1.getVariableOperator();
					input = CoreMove.parseStringVariables(input, factory, core,
							BS, user);
					String[] test = owlSpeak.engine.OwlScript
							.evaluateString(input);
					if ((test[0].equalsIgnoreCase("REQUIRES"))
							&& (!test[1].equals("1"))) {
						continue;
					}
				}
				
//				System.out.println("\t  fulfilling variable requirements: true");
				
				if (tree.containsKey(AgendaPrio.calculateAgendaPrio(temp1, neu,
						factory, core, core.actualtime))) {
					(tree.get(AgendaPrio.calculateAgendaPrio(temp1, neu,
							factory, core, core.actualtime))).add(temp1);
				} else {
					Collection<Agenda> treeColl = new java.util.LinkedList<Agenda>();
					treeColl.add(temp1);
					tree.put(AgendaPrio.calculateAgendaPrio(temp1, neu,
							factory, core, core.actualtime), treeColl);
					;
				}
			}
		}
		if (tree.isEmpty()) {
			return null;
		}
		return tree.get(tree.lastKey()).toArray(new Agenda[0])[0];

//		Collection<Agenda> coll = tree.get(tree.lastKey());
//		Iterator<Agenda> mvmAusgabe = coll.iterator();
//		while (mvmAusgabe.hasNext()) {
//			returnAgenda = mvmAusgabe.next();
//			if (returnAgenda.hasHas()) {
//				return returnAgenda;
//			}
//		}
//		// System.err.print("Schrecklich, Agendas müssen verarbeitet werden");
//		// TODO platz für optimierungen
//		Iterator<Agenda> temp2 = returnAgenda.getNext().iterator();
//		while (temp2.hasNext()) {
//			Agenda a = (Agenda) temp2.next();
//			neu.addNext(a);
//			AgendaPrio.addAgendaPrio(temp1, neu, factory, core.actualtime);
//			// Ausgabe.removeNext(temp1);
//		}
//		neu.removeNext(returnAgenda);
//		AgendaPrio.setAgendaProctime(temp1, neu, factory, core.actualtime);
//		returnAgenda = policy(core, onto, user, userAct);
//		return returnAgenda;
	}

}
