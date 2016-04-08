package owlSpeak.engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.Variable;
import owlSpeak.engine.policy.Policy;
import owlSpeak.engine.systemState.SystemState.SystemStateVariant;

/**
 * owlSpeak Class CoreMove is the basic Move/Agenda/OwlSpeakOntology triplet
 * used for the engine
 * 
 * @author Dan Denich
 * @version 1.0
 */
public class CoreMove {
	/**
	 * the Move (from the Ontology) that should be used in the CoreMove
	 */
	public Move move;
	/**
	 * the Agenda (from the Ontology) in which the Move should be
	 */
	public Agenda agenda;
	/**
	 * the OwlSpeakOntology that contains the Move and the Agenda
	 */
	public OwlSpeakOntology onto;
	/**
	 * the Core that contains the OwlSpeakOntology
	 */
	public Core core;
	/**
	 * a Vector of Strings that cotains conflicts with other Moves, used primary
	 * in Respawns
	 */
	public Vector<String> conflict = new Vector<String>();

	CoreMove(Move initMove, Agenda initAgenda, OwlSpeakOntology initOnto,
			Core initCore) {
		move = initMove;
		agenda = initAgenda;
		onto = initOnto;
		core = initCore;
	}

	CoreMove(CoreMove initSpawn) {
		move = initSpawn.move;
		agenda = initSpawn.agenda;
		onto = initSpawn.onto;
		core = initSpawn.core;
	}

	/**
	 * checks if all the required semantics of the agenda are set in the
	 * ontologies beliefspace
	 * 
	 * @param user
	 *            the username
	 * @param bel
	 *            the BeliefSpace in which the Semantics have to be
	 * @return true if ( onto.getBSSemantics().containsAll(agenda.getRequires())
	 *         ) else false
	 */
	boolean checkRespawn(String user, BeliefSpace bel) {
		boolean check = false;
		if (agenda
				.matchRequires(onto.getBSSemantics(Settings.getuserpos(user)))) {
			if (this.agenda.hasVariableOperator()) {
				String input = this.agenda.getVariableOperator();
				input = parseStringVariables(input, this.onto.factory,
						this.core, bel, user);
				String[] test = owlSpeak.engine.OwlScript.evaluateString(input);
				if ((test[0].equalsIgnoreCase("requires"))
						&& (!test[1].equals("1"))) {
					return false;
				}
			}

			check = true;
		}
		return check;
	}

	/**
	 * checks if the CoreMove has a conflict
	 * 
	 * @return false if ( this.conflict.equals("") ) else true
	 */
	boolean hasConflict() {
		if (conflict.equals(""))
			return false;
		else
			return true;
	}

	/**
	 * combines the CoreMove's Utterances to a String
	 * 
	 * @param bel
	 *            the user's BeliefSpace
	 * @param user
	 *            the user name of the current user
	 * @return the Utterance-String
	 */
	public String parseUtteranceVariables(BeliefSpace bel, String user) {
		Move tempMv = this.move;
		String prompt = "";
		Collection<OWLLiteral> uttColl = tempMv.getUtterance()
				.getUtteranceString();
		Iterator<OWLLiteral> uttIt = uttColl.iterator();
		while (uttIt.hasNext()) {
			prompt = prompt + " " + (uttIt.next()).getLiteral();
		}
		String newPrompt = parseStringVariables(prompt, bel, user);

		return parseStringSemantics(newPrompt, bel, user);
	}

	/**
	 * combines the CoreMove's Grammars to a String
	 * 
	 * @param bel
	 *            the user's BeliefSpace
	 * @param user
	 *            the user String identifier
	 * @return the Grammar-String
	 */
	public String parseGrammarVariables(BeliefSpace bel, String user) {
		Move tempMv = this.move;
		String prompt = "";
		Collection<OWLLiteral> gramColl = tempMv.getGrammar()
				.getGrammarString();
		Iterator<OWLLiteral> gramIt = gramColl.iterator();
		while (gramIt.hasNext()) {
			prompt = prompt + " " + (gramIt.next()).getLiteral();
		}
		String grammarString = parseStringVariables(prompt, bel, user);
		return parseStringSemantics(grammarString, bel, user);
	}

	/**
	 * combines the CoreMove's Grammars to a vector of string
	 * 
	 * @param bel
	 *            the user's BeliefSpace
	 * @param user
	 *            the user String identifier
	 * @return the vector of string containing the grammars
	 */
	public Vector<String> parseGrammarVariablesToVector(BeliefSpace bel,
			String user) {
		Vector<String> grammars = new Vector<String>();
		Move tempMv = this.move;
		Collection<OWLLiteral> gramColl = tempMv.getGrammar()
				.getGrammarString();
		Iterator<OWLLiteral> gramIt = gramColl.iterator();
		while (gramIt.hasNext()) {
			String grammarString = parseStringVariables(gramIt.next()
					.getLiteral(), bel, user);
			grammars.add(parseStringSemantics(grammarString, bel, 
					user));
		}
		return grammars;
	}

	/**
	 * a function that is used to resolve the variables within a string and
	 * replace them with their values calls the function
	 * parseStringVariables(String, OSFactory, Core, BeliefSpace) with the
	 * current context
	 * 
	 * @param Prompt
	 *            the String that should be parsed
	 * @param bel
	 *            the user's BeliefSpace
	 * @param user TODO
	 * @return a string in which the variables are replaced with their values
	 */
	public String parseStringVariables(String Prompt, BeliefSpace bel, String user) {
		return parseStringVariables(Prompt, this.onto.factory, this.core, bel, user);
	}
	
	/**
	 * a function that is used to resolve the semantics within a string and
	 * replace them with their values calls the function
	 * parseStringVariables(String, OSFactory, Core, BeliefSpace) with the
	 * current context
	 * 
	 * @param Prompt
	 *            the String that should be parsed
	 * @param bel
	 *            the user's BeliefSpace
	 * @return a string in which the variables are replaced with their values
	 */
	public String parseStringSemantics(String Prompt, BeliefSpace bel, String user) {
		return CoreMove.parseStringSemantics(Prompt, bel, this.onto, user);
	}

	/**
	 * a function that is used to resolve the variables within a string and
	 * replace them with their values calls the function fetchVariable to get
	 * the values
	 * 
	 * @param Prompt
	 *            the String that should be parsed
	 * @param factory
	 *            the factory that should be used
	 * @param core
	 *            the current Core object
	 * @param bel
	 *            the user's BeliefSpace
	 * @param user TODO
	 * @return a string in which the variables are replaced with their values
	 */
	public static String parseStringVariables(String Prompt, OSFactory factory,
			Core core, BeliefSpace bel, String user) {
		String Prompt2 = "";
		if (Prompt.contains("%")) {
			String[] var = Prompt.split("%", -1);

			// all uneven array entries contain a variable name
			if (!Prompt.startsWith("%")) {
				int size = ((var.length - 1) / 2);
				Prompt2 += var[0];
				for (int i = 1; i < (size + 1); i++) {
					Variable tempVar = fetchVariable(var[(i * 2) - 1], factory,
							core);
					BeliefSpace beliefS = findVarBelSpace(var[(i * 2) - 1],
							factory, core, bel);
					if (!Core.variableIsContainedInBeliefSpace(tempVar,beliefS)) {
						beliefS = factory.getBeliefSpace(user+"BeliefspaceGeneric");
					}
					Prompt2 += Core.getVariableString(tempVar, beliefS);
					Prompt2 += var[(i * 2)];
				}
			}
			// all even array entries contain a variable name
			else {
				int size = ((var.length) / 2);
				for (int i = 0; i < size; i++) {
					Variable tempVar = fetchVariable(var[i * 2], factory, core);
					BeliefSpace beliefS = findVarBelSpace(var[i * 2], factory,
							core, bel);
					if (!Core.variableIsContainedInBeliefSpace(tempVar,beliefS)) {
						beliefS = factory.getBeliefSpace(user+"BeliefspaceGeneric");
					}
					Prompt2 += Core.getVariableString(tempVar, beliefS);
					Prompt2 += var[(i * 2) + 1];
				}
			}
		} else {
			Prompt2 = Prompt;
		}
		return Prompt2;
	}
	
	/**
	 * a function that is used to resolve the semantics within a string and
	 * replace them with their values (semantic strings).
	 * 
	 * Assumes that there is only one semantic string contained in the semantic.
	 * TODO add multilinguality aspects
	 * 
	 * @param prompt
	 *            the String that should be parsed
	 * @param bs
	 *            the belief space that should be used
	 * @param user
	 *            the user name of the current user
	 * @return a string in which the semantics are replaced with their values
	 */
	public static String parseStringSemantics(String prompt, BeliefSpace bs,
			OwlSpeakOntology osOnto, String user) {
		String lang = (Settings.asrMode).substring(0, 2);
		String newPrompt = "";
		if (prompt.contains("#")) {
			String[] var = prompt.split("#", -1);

			// all uneven array entries contain a variable name
			if (!prompt.startsWith("#")) {
				int size = ((var.length - 1) / 2);
				newPrompt += var[0];
				for (int i = 1; i < (size + 1); i++) {
					Semantic sem = osOnto.factory.getSemantic(var[(i * 2) - 1]);

					// semantic is SemanticGroup as there is no semantic string
					//TODO SemGroup remove
					if (!sem.hasSemanticString()) {
						String val = null;
						SemanticGroup sg = sem.asSemanticGroup();
						for (Semantic s : sg.getContainedSemantics()) {
							Vector<Semantic> vecSem = osOnto
									.getBSSemantics(Settings.getuserpos(user));
							if (s.isMemberOf(vecSem)) {
								for (OWLLiteral l : s.getSemanticString()) {
									if (lang == null
											|| (lang != null && l.getLang()
													.equalsIgnoreCase(lang))) {
										val = l.getLiteral();
										break;
									}
								}
							}
							if (val != null)
								break;
						}
						newPrompt += val;
					}

					// semantic is true semantic object
					else {
						newPrompt += ((OWLLiteral) sem.getSemanticString()
								.toArray()[0]).getLiteral();
					}
					newPrompt += var[(i * 2)];
				}
			}
			// all even array entries contain a variable name
			else {
				int size = ((var.length) / 2);
				for (int i = 0; i < size; i++) {
					Semantic sem = osOnto.factory.getSemantic(var[i * 2]);
					newPrompt += ((OWLLiteral) sem.getSemanticString()
							.toArray()[0]).getLiteral();
					newPrompt += var[(i * 2) + 1];
				}
			}
		} else {
			newPrompt = prompt;
		}
		return newPrompt;
	}

	/**
	 * a function that is used to resolve a Variable specified by its name
	 * 
	 * @param var
	 *            the name of the Variable
	 * @param factory
	 *            the factory that should be used
	 * @param core
	 *            the current Core object
	 * @return the Variable
	 */
	public static Variable fetchVariable(String var, OSFactory factory,
			Core core) {
		Variable result = null;
		if (var.contains(":")) {
			String[] parse;
			parse = var.split(":");
			OwlSpeakOntology find = core.findOntology(parse[0]);
			result = find.factory.getVariable(parse[1]);
		} else {
			result = factory.getVariable(var);
		}
		return result;
	}

	/**
	 * a function that is used to resolve a BeliefSpace according to a
	 * Variable-Prefix
	 * 
	 * @param var
	 *            the name of the Variable
	 * @param factory
	 *            the factory that should be used
	 * @param core
	 *            the current Core object
	 * @param bel
	 *            the user's BeliefSpace
	 * @return the BeliefSpace
	 */
	public static BeliefSpace findVarBelSpace(String var, OSFactory factory,
			Core core, BeliefSpace bel) {
		BeliefSpace result = null;
		if (var.contains(":")) {
			String[] parse;
			parse = var.split(":");
			OwlSpeakOntology find = core.findOntology(parse[0]);
			result = find.factory.getBeliefSpace(bel.getLocalName());
		} else {
			result = bel;
		}
		return result;
	}

	/**
	 * a function that is used to resolve a BeliefSpace according to a
	 * Variable-Prefix
	 * 
	 * @param var
	 *            the name of the Variable
	 * @param factory
	 *            the factory that should be used
	 * @param core
	 *            the current Core object
	 * @param bel
	 *            the user's BeliefSpace
	 * @return the BeliefSpace
	 */
	public static BeliefSpace findVarBelSpace(String var, OSFactory factory,
			Core core, BeliefSpace bel, String user) {
		BeliefSpace result = null;
		if (var.contains(":")) {
			String[] parse;
			parse = var.split(":");
			OwlSpeakOntology find = core.findOntology(parse[0]);
			result = find.factory.getBeliefSpace(bel.getLocalName());
		} else {
			if (Policy.sumAgenad2AgendaNextBeliefSpaceCounter == 2)
			{
				OwlSpeakOntology onto = core.actualOnto[Settings.getuserpos(user)];
				if (onto.ontoStateVariant == SystemStateVariant.DISTRIBUTION)
					result = (BeliefSpace) onto.partitionDistributions[Settings.getuserpos(user)].getSecondTopPartition(); 
			}
			else
				result = bel;
		}
		return result;
	}

	/**
	 * a function that is used to set a Variable specified by its name to a
	 * given value the Variable can be within a different ontology
	 * 
	 * @param var
	 *            the name of the Variable
	 * @param value
	 *            the value that should be written to the Variable
	 * @param factory
	 *            the factory that should be used
	 * @param core
	 *            the current Core object
	 * @param bel
	 *            the user's BeliefSpace
	 * @param user
	 *            the name of the current user
	 */
	public static void setVariable(String var, String value, OSFactory factory,
			Core core, BeliefSpace bel, String user) {
		if (var.contains(":")) {
			String[] parse;
			parse = var.split(":");
			OwlSpeakOntology find = core.findOntology(parse[0]);
			BeliefSpace BS = findVarBelSpace(var, factory, core, bel);
			try {
				Variable vara = find.factory.getVariable(parse[1]);
				core.setVariableString(vara, BS, find.factory, value, user);
			} catch (NullPointerException n) {
				// Variable vara=find.factory.createVariable(parse[1]);
				// Core.setVariableString(vara, bel, factory, value);
				// vara.setVariableString(value);
			}
			find.write();

		} else {
			try {
				Variable vara = factory.getVariable(var);
				core.setVariableString(vara, bel, factory, value, user);
			} catch (NullPointerException n) {
				// Variable vara=factory.createVariable(var);
				// vara.setVariableString(value);
				System.out
						.println("There was a problem setting the variable string in belief space "
								+ bel);
			}
		}
	}
}
