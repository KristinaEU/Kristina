package owlSpeak.engine.systemState;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLLiteral;


import owlSpeak.Agenda;
import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.Variable;
import owlSpeak.Move.ExtractFieldMode;
import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.sysAction.GenericSystemAction;

public abstract class SystemState {
	
	public enum SystemStateVariant {
		SINGLE, DISTRIBUTION
	}

	protected Core core;

	public SystemState(Core _core) {
		core = _core;
	}

	public static SystemState getSystemState(Core core, OwlSpeakOntology onto, String user) {
		switch (onto.ontoStateVariant) {
		case DISTRIBUTION:
			return new SystemStateDistribution(core);
		case SINGLE:
		default:
			return new SystemStateSingle(core);
		}
	}

	public abstract void updateState(UserAction[] userActions,
			double[] confidenceScores, GenericSystemAction act,
			OwlSpeakOntology actualOnto, String user);

	public abstract void processAgendaExceptGrammar(GenericSystemAction sysAct,
			OwlSpeakOntology onto, UserAction userAction, String user);

	public abstract void updateStateSingleAction(UserAction userAction,
			double confidenceScore, GenericSystemAction sysAct,
			OwlSpeakOntology actualOnto, String user);

	public abstract void processRequestMoves(Agenda toDoAgenda,
			OwlSpeakOntology onto, String user);
	
	/**
	 * processes the given move and writes its semantics to a new Belief in the
	 * beliefSpace
	 * @param beliefSpace
	 *            the beliefSpace to which the beliefs will be added
	 * @param factory
	 *            the OSFactory of the move and the beliefSpace
	 * @param user
	 *            the name of the current user
	 * @param core TODO
	 * @param toDoMove
	 *            the move with the semantics
	 * @param speak
	 *            TODO
	 */
	public static void processMove(UserAction userAction, BeliefSpace beliefSpace,
			OSFactory factory, String prefix, String user, Core core) {
		core.actualtime++;
		// System.out.println(toDoMove.getLocalName());
		Iterator<Belief> beliefsIt = null;
		Semantic toDoSem = null;
		Belief tempBel = null;
		Semantic remSem = null;
		Move toDoMove = userAction.getMove();

		Iterator<Semantic> toDoSemanticIt = toDoMove.getSemantic().iterator();
		Iterator<Semantic> remSemIt = toDoMove.getContrarySemantic().iterator();

		beliefsIt = beliefSpace.getHasBelief().iterator();
		Random randomizer = new Random();

		Vector<Semantic> semantics = new Vector<Semantic>();
		while (beliefsIt.hasNext()) {
			semantics.addAll(beliefsIt.next().getSemantic());
		}
		Vector<Variable> variables = new Vector<Variable>();
		for (Belief b : beliefSpace.getHasBelief()) {
			if (b.hasVariabledefault())
				variables.add(b.getVariabledefault());
		}

		String temp = "";
		while (toDoSemanticIt.hasNext()) {
			toDoSem = toDoSemanticIt.next();
			toDoSem = factory.getSemantic(toDoSem.getLocalName());
			if (toDoSem.getSemanticString() != null) {
				Iterator<OWLLiteral> semStringIter = toDoSem
						.getSemanticString().iterator();
				while (semStringIter.hasNext()) {
					temp = semStringIter.next().getLiteral() + ";" + temp;
				}
			}
			if (!toDoSem.isMemberOf(semantics)) {
				tempBel = factory
						.createBelief("Belief_" + randomizer.nextInt());
				beliefSpace.addHasBelief(tempBel);
				tempBel.addSemantic(toDoSem);
				semantics.add(toDoSem);
				if (toDoSem.hasSemanticGroup()) {
					SemanticGroup group = toDoSem.getSemanticGroup();
					Semantic groupSem = group.asSemantic();
					if (!groupSem.isMemberOf(semantics)) {
						tempBel = factory.createBelief("Belief_"
								+ randomizer.nextInt());
						beliefSpace.addHasBelief(tempBel);
						tempBel.addSemantic(group);
						semantics.add(groupSem);
					}
				}
			}
			
		}

		while (remSemIt.hasNext()) {
			remSem = factory.getSemantic(remSemIt.next().getLocalName());
			SemanticGroup remSemGroup = remSem.getSemanticGroup();

			// remSem is not a semantic group
			if (remSemGroup != null) {
				// remove semantic
				if (remSem.isMemberOf(semantics)) {
					beliefsIt = beliefSpace.getHasBelief().iterator();
					while (beliefsIt.hasNext()) {
						tempBel = beliefsIt.next();
						Vector<Semantic> v = new Vector<Semantic>(
								tempBel.getSemantic());

						if (remSem.isMemberOf(v)) {
							tempBel.delete();
						}

					}
				}

				/*
				 * remove semantic group iff no other semantics or variable of
				 * group exist in belief space
				 */
				Semantic remSemGroupSem = remSemGroup.asSemantic();
				if (remSemGroupSem.isMemberOf(semantics)) {
					beliefsIt = beliefSpace.getHasBelief().iterator();
					while (beliefsIt.hasNext()) {
						tempBel = beliefsIt.next();
						Vector<Semantic> v = new Vector<Semantic>(
								tempBel.getSemantic());

						if (remSemGroupSem.isMemberOf(v)) {
							boolean semFound = false;
							for (Semantic s : remSemGroup
									.getContainedSemantics()) {
								if (s.isMemberOf(v)) {
									semFound = true;
									break;
								}
							}
							if (remSemGroup.hasVariable()) {
								Variable var = remSemGroup.getVariable();
								if (var.isMemberOf(variables))
									semFound = true;
								if (!semFound)
									tempBel.delete();
							}
						}
					}
				}
			}
			// remSem is a semantic group
			else {
				// remove semantic group and all its semantics and variables
				// from belief space
				remSemGroup = remSem.asSemanticGroup();
				if (remSem.isMemberOf(semantics)) {
					// remove semantic group itself
					beliefsIt = beliefSpace.getHasBelief().iterator();
					while (beliefsIt.hasNext()) {
						tempBel = beliefsIt.next();
						Vector<Semantic> v = new Vector<Semantic>(
								tempBel.getSemantic());
						if (remSem.isMemberOf(v)) {
							tempBel.delete();
						}
					}
					// remove all semantics of semantic group containted in
					// belief space
					for (Semantic s : remSemGroup.getContainedSemantics()) {
						if (s == null)
							break;
						beliefsIt = beliefSpace.getHasBelief().iterator();
						while (beliefsIt.hasNext()) {
							tempBel = beliefsIt.next();
							Vector<Semantic> v = new Vector<Semantic>(
									tempBel.getSemantic());
							if (s.isMemberOf(v)) {
								tempBel.delete();
							}
						}
					}
					Variable v = remSemGroup.getVariable();
					beliefsIt = beliefSpace.getHasBelief().iterator();
					while (beliefsIt.hasNext()) {
						tempBel = beliefsIt.next();
						if (tempBel.hasVariabledefault()
								&& tempBel.getVariabledefault().equals(v)) {
							tempBel.delete();
						}
					}
				}
			}
		}

		if (toDoMove.hasVariableOperator()) {
			String opString = toDoMove.getVariableOperator();

			if (toDoMove.getExtractFieldValues() != ExtractFieldMode.NO) {
				for (String field : userAction.getMoveFields()) {
					SemanticGroup sg = factory.getSemanticGroup(field);
					if (sg.hasVariable()) {
						opString = userAction.getSupp().replaceVariableValues(
								opString, sg.getVariable().getLocalName(),
								userAction.getFieldValue(field).getValue());
					}
				}
			}

			opString = CoreMove.parseStringVariables(opString, factory, core,
					beliefSpace, user);
			String[] owlScript = owlSpeak.engine.OwlScript
					.evaluateString(opString);
			if (owlScript[0].equalsIgnoreCase("SET")) {
				Vector<String> sets = owlSpeak.engine.OwlScript
						.getSets(owlScript[1]);
				for (int i = 0; i < sets.size(); i++) {
					String[] set = owlSpeak.engine.OwlScript.parseSet(sets
							.get(i));
					CoreMove.setVariable(set[0], set[1], factory, core,
							beliefSpace, user);
				}
			} else if (owlScript[0].equalsIgnoreCase("IF")) {
				if (owlScript[1].equals("1")) {
					Vector<String> sets = owlSpeak.engine.OwlScript
							.getSets(owlScript[2]);
					for (int i = 0; i < sets.size(); i++) {
						String[] set = owlSpeak.engine.OwlScript.parseSet(sets
								.get(i));
						CoreMove.setVariable(set[0], set[1], factory, core,
								beliefSpace, user);
					}
				}
			}
		}
	}
}
