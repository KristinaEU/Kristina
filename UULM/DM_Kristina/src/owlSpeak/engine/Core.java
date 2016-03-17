package owlSpeak.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.GenericClass;
import owlSpeak.History;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.SummaryAgenda;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.Variable;
import owlSpeak.WorkSpace;
import owlSpeak.engine.his.DialogueOptimizationInfo;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.Reward;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.factories.NBestListFactory;
import owlSpeak.engine.his.interfaces.ISummaryBelief;
import owlSpeak.engine.policy.Policy;
import owlSpeak.engine.systemState.SystemState.SystemStateVariant;
import owlSpeak.quality.QualityParameter;

/**
 * contains the ontologies-Vector, the OwlSpeakOntology actualOnto and the
 * RespawnManager respawnManager
 * 
 * @see OwlSpeakOntology
 * @see RespawnManager
 * @author Dan Denich
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 */
public class Core {
	static public Core singleton = null;
	
	public static Core getCore() {
		if (singleton == null)
			singleton = new Core();
		return singleton;
	}
	
	/**
	 * the Vector containing all ontologies
	 */
	public Vector<OwlSpeakOntology> ontologies;
	/**
	 * the Settings object
	 */
	public Settings settings;
	/**
	 * the actual ontology for the request/work
	 */
	public OwlSpeakOntology[] actualOnto;
	/**
	 * the "time" of the Core, increased by any function
	 */
	public int actualtime;
	/**
	 * the respawnManager for the respawns
	 */
	public RespawnManager[] respawnManager;
	/**
	 * the Vector containing all Slots
	 */
	public Vector<Slot> Slots;

	/**
	 * the actual Slot for the POMDP request/work methods
	 */
	public Slot actualSlot = null;

	public NBestListFactory nBestListFactory;

	/**
	 * 
	 */
	public QualityParameter[] qualityParameter;

	/**
	 * Information collected during the dialogue optimization process for
	 * automatic dialogue policy optimization
	 */
	public DialogueOptimizationInfo optimizationInfo;

	/**
	 * creates a Core object and loads the settings
	 */
	public Core() {
		this.settings = new Settings();
		this.respawnManager = new RespawnManager[Settings.usernames.length];
		this.actualtime = 0;
		loadOntologiesFromSettings();
		this.actualOnto = new OwlSpeakOntology[Settings.usernames.length];
		this.Slots = new Vector<Slot>();
		this.qualityParameter = new QualityParameter[Settings.usernames.length];
		resetAllQualityParameters();
		this.optimizationInfo = new DialogueOptimizationInfo();
	
		for (OwlSpeakOntology o : ontologies) {	
			if (o.ontoStateVariant == SystemStateVariant.DISTRIBUTION) {
				nBestListFactory = new NBestListFactory();
				break;
			}
		}
		
//		for (Usersetting s : Settings.usersetting) {	
//			if (s.docType == Usersetting.HIS_VXML_DOC) {
//				nBestListFactory = new NBestListFactory();
//				break;
//			}
//		}
	}

	/**
	 * the main function of catching the actual Slot compare the Semantics
	 * linked to the CoreMove Vector with the user goal values of each Slot if
	 * not found create a new Slot
	 * 
	 * @param gram
	 *            a Vector of CoreMoves
	 * @return a Slot object
	 */
	public Slot CatchActualSlot(Vector<CoreMove> gram) {

		Iterator<Slot> itThisSlots = Slots.iterator();
		Slot tempSlot = null;

		boolean found = false;

		while (itThisSlots.hasNext()) {
			tempSlot = itThisSlots.next();
			if (tempSlot.compareToGrammar(gram)) {
				found = true;
				break;
			}
		}

		if (!found) {
			tempSlot = new Slot(gram);
			Slots.addElement(tempSlot);
		}
		return tempSlot;
	}

	/**
	 * the main matching function of owlSpeak - retrieves one agenda that meets
	 * the requirements and has top priority
	 * 
	 * @param master
	 *            the masteragenda of the current workspace
	 * @param neu
	 *            the current workSpace
	 * @param BS
	 *            the beliefSpace corresponding to the workSpace
	 * @param factory
	 *            the Factory
	 * @param user TODO
	 * @return the Agenda that matches the requirements and has the highest
	 *         priority
	 */
	public Agenda matchAgenda(Agenda master, WorkSpace neu, BeliefSpace BS,
			OSFactory factory, String user) {
		Agenda Ausgabe = null;
		Agenda temp1 = null;
		Iterator<Agenda> masterNext = master.getNext().iterator();
		Iterator<Belief> beliefsIt = BS.getHasBelief().iterator();
		TreeMap<Integer, Collection<Agenda>> tree = new TreeMap<Integer, Collection<Agenda>>();
		Vector<Semantic> semantics = new Vector<Semantic>();
		Belief tempBel;

		if (!masterNext.hasNext()) {
			return null;
		}

		while (beliefsIt.hasNext()) {
			tempBel = beliefsIt.next();
			semantics.addAll(tempBel.getSemantic());
		}

		while (masterNext.hasNext()) {
			temp1 = masterNext.next();
			if (temp1.getRequires().iterator().hasNext()) {
			}
			for (int i = 0; i < semantics.size(); i++) {

				if (semantics.isEmpty())
					break;
			}
			if (temp1.matchRequires(semantics)) {
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
				;
				if (mustfound) {
					continue;
				}
				if (temp1.hasVariableOperator()) {
					String input = temp1.getVariableOperator();
					input = CoreMove.parseStringVariables(input, factory, this,
							BS, user);
					String[] test = owlSpeak.engine.OwlScript
							.evaluateString(input);
					if ((test[0].equalsIgnoreCase("REQUIRES"))
							&& (!test[1].equals("1"))) {
						continue;
					}
				}
				if (tree.containsKey(AgendaPrio.calculateAgendaPrio(temp1, neu,
						factory, this, actualtime))) {
					(tree.get(AgendaPrio.calculateAgendaPrio(temp1, neu,
							factory, this, actualtime))).add(temp1);
				} else {
					Collection<Agenda> treeColl = new java.util.LinkedList<Agenda>();
					treeColl.add(temp1);
					tree.put(AgendaPrio.calculateAgendaPrio(temp1, neu,
							factory, this, actualtime), treeColl);
					;
				}
			}
		}
		if (tree.isEmpty()) {
			return null;
		}

		Collection<Agenda> coll = tree.get(tree.lastKey());
		Iterator<Agenda> mvmAusgabe = coll.iterator();
		while (mvmAusgabe.hasNext()) {
			Ausgabe = mvmAusgabe.next();
			if (Ausgabe.hasHas()) {
				return Ausgabe;
			}
		}
		// System.err.print("Schrecklich, Agendas m�ssen verarbeitet werden");
		// //platz f�r optimierungen
		Iterator<Agenda> temp2 = Ausgabe.getNext().iterator();
		while (temp2.hasNext()) {
			Agenda a = (Agenda) temp2.next();
			neu.addNext(a);
			AgendaPrio.addAgendaPrio(temp1, neu, factory, actualtime);
			// Ausgabe.removeNext(temp1);
		}
		neu.removeNext(Ausgabe);
		AgendaPrio.setAgendaProctime(temp1, neu, factory, this.actualtime);
		Ausgabe = matchAgenda(master, neu, BS, factory, user);
		return Ausgabe;
	}

	/**
	 * processes the given move and writes its semantics to a new Belief in the
	 * beliefSpace
	 * 
	 * @param toDoMove
	 *            the move with the semantics
	 * @param beliefSpace
	 *            the beliefSpace to which the beliefs will be added
	 * @param factory
	 *            the OSFactory of the move and the beliefSpace
	 * @param user
	 *            the name of the current user
	 */
	public void processMove(Move toDoMove, BeliefSpace beliefSpace,
			OSFactory factory, String prefix, String user) {
		actualtime++;
		// System.out.println(toDoMove.getLocalName());
		Iterator<Belief> beliefsIt = null;
		Semantic toDoSem = null;
		Belief tempBel = null;
		Semantic remSem = null;

		Iterator<Semantic> toDoSemanticIt = toDoMove.getSemantic().iterator();
		Iterator<Semantic> remSemIt = toDoMove.getContrarySemantic().iterator();

		beliefsIt = beliefSpace.getHasBelief().iterator();
		Random randomizer = new Random();

		Vector<Semantic> semantics = new Vector<Semantic>();
		while (beliefsIt.hasNext()) {
			semantics.addAll(beliefsIt.next().getSemantic());
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
//			if (Settings.youpi == 1) TODO check if youpi should be activated
//				DeviceServer.changeStateVariable(OwlSpeakYoupi.dev
//						.getRootDevice().getService(OwlSpeakYoupi.service)
//						.getStateVariable("lastUserWill"),
//						(OwlSpeakYoupi.taskIDMap.get(prefix) + ";" + temp)
//								.substring(0,
//										((OwlSpeakYoupi.taskIDMap.get(prefix)
//												+ ";" + temp).length() - 1)));
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
						;
						if (remSem.isMemberOf(v)) {
							tempBel.delete();
						}

					}
				}

				// remove semantic group iff no other semantics of group exist
				// in belief space
				Semantic remSemGroupSem = remSemGroup.asSemantic();
				if (remSemGroupSem.isMemberOf(semantics)) {
					beliefsIt = beliefSpace.getHasBelief().iterator();
					while (beliefsIt.hasNext()) {
						tempBel = beliefsIt.next();
						Vector<Semantic> v = new Vector<Semantic>(
								tempBel.getSemantic());
						;
						if (remSemGroupSem.isMemberOf(v)) {
							boolean semFound = false;
							for (Semantic s : remSemGroup
									.getContainedSemantics()) {
								if (s.isMemberOf(v)) {
									semFound = true;
									break;
								}
							}
							if (!semFound)
								tempBel.delete();
						}
					}
				}
			}
			// remSem is a semantic group
			else {
				// remove semantic group and all its semantics from belief space
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
				}
			}
		}

		if (toDoMove.hasVariableOperator()) {
			String opString = toDoMove.getVariableOperator();
			// opString=CoreMove.checkVariables(opString, factory, this,
			// beliefSpace);
			opString = CoreMove.parseStringVariables(opString, factory, this,
					beliefSpace, user);
			String[] owlScript = owlSpeak.engine.OwlScript
					.evaluateString(opString);
			if (owlScript[0].equalsIgnoreCase("SET")) {
				Vector<String> sets = owlSpeak.engine.OwlScript
						.getSets(owlScript[1]);
				for (int i = 0; i < sets.size(); i++) {
					String[] set = owlSpeak.engine.OwlScript.parseSet(sets
							.get(i));
					CoreMove.setVariable(set[0], set[1], factory, this,
							beliefSpace, user);
				}
			} else if (owlScript[0].equalsIgnoreCase("IF")) {
				if (owlScript[1].equals("1")) {
					Vector<String> sets = owlSpeak.engine.OwlScript
							.getSets(owlScript[2]);
					for (int i = 0; i < sets.size(); i++) {
						String[] set = owlSpeak.engine.OwlScript.parseSet(sets
								.get(i));
						CoreMove.setVariable(set[0], set[1], factory, this,
								beliefSpace, user);
					}
				}
			}
		}
	}

	/**
	 * processes all moves of a given agenda except the moves with grammars
	 * 
	 * @param toDoAgenda
	 *            the agenda
	 * @param beliefSpace
	 *            the beliefspace of the agenda
	 * @param factory
	 *            the factory of the agenda
	 * @param user
	 *            the name of the current user
	 * @param userMove
	 *            the user move object needed for checking processing conditions
	 */
	public void processAgendaExceptGrammar(Agenda toDoAgenda,
			BeliefSpace beliefSpace, OSFactory factory, String prefix,
			String user, Move userMove) {
		Move tempMove;
		
		System.out.println("Core.processAgendaExeptGrammar  for agenda " + toDoAgenda);
		
		Iterator<Move> agendaHas = toDoAgenda.getHas().iterator();
		while (agendaHas.hasNext()) {
			tempMove = agendaHas.next();
			if (!tempMove.hasGrammar() && !tempMove.getIsRequestMove()) {
				// if(!tempMove.hasSemantic()) continue;
				// System.out.println(tempMove.getLocalName());

				if (!tempMove.hasProcessConditions()) {
					this.processMove(tempMove, beliefSpace, factory, prefix,
							user);
				} else {
					if (tempMove.getProcessConditions().contains(userMove))
						this.processMove(tempMove, beliefSpace, factory,
								prefix, user);
				}
			}
		}
	}

	/**
	 * processes all moves of a given agenda within a request
	 * 
	 * @param toDoAgenda
	 *            the agenda
	 * @param beliefSpace
	 *            the beliefspace of the agenda
	 * @param factory
	 *            the factory of the agenda
	 * @param user
	 *            the name of the current user
	 */
	public void processRequestMoves(Agenda toDoAgenda, BeliefSpace beliefSpace,
			OSFactory factory, String prefix, String user) {
		Move tempMove;
		Iterator<Move> agendaHas = toDoAgenda.getHas().iterator();
		while (agendaHas.hasNext()) {
			tempMove = agendaHas.next();
			if (!tempMove.hasGrammar() && tempMove.getIsRequestMove()) {
				System.out
						.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\nprocessing a request move\n"
								+ tempMove.getLocalName()
								+ "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
				this.processMove(tempMove, beliefSpace, factory, prefix, user);
			}
		}
	}

	/**
	 * adds all next-agendas of the given agenda to the workSpace and removes
	 * itself
	 * 
	 * @param agenda
	 *            the agenda of which the next(s) will be added
	 * @param neu
	 *            the target workSpace
	 * @param factory
	 *            the OSFactory of agenda and workSpace
	 * @param keepAgenda
	 *            if agenda should not be removed from workspace
	 */
	public void tidyUpAgenda(Agenda agenda, WorkSpace neu, OSFactory factory,
			boolean keepAgenda) {
		actualtime++;
		// is the next Agenda the same as the current Agenda, if yes the Agenda
		// should NOT be removed.
		boolean equalFound = false;
		Agenda temp1 = null;
		Iterator<Agenda> temp2 = agenda.getNext().iterator();
		if (agenda.hasNext()) {
			while (temp2.hasNext()) {
				temp1 = temp2.next();
				neu.addNext(temp1);
				AgendaPrio.addAgendaPrio(temp1, neu, factory, actualtime);
				if (agenda.getFullName().equals(temp1.getFullName())) {
					equalFound = true;
					AgendaPrio.setAgendaProctime(temp1, neu, factory,
							this.actualtime);
				}
			}
		}

		if (!keepAgenda && !equalFound) {
			temp1 = factory.getAgenda(agenda.getLocalName());
			AgendaPrio.setAgendaProctime(temp1, neu, factory, this.actualtime);
			neu.removeNext(temp1);
		}
	}

	/**
	 * the matchagenda without parameters calls the matchagenda(MA, WS, BS,
	 * F)-function for all ontologies in the ontologies Vector
	 * 
	 * @param user
	 *            the username
	 */
	public void matchAgenda(String user) {
		actualtime++;
		Agenda tempAgenda;
		TreeMap<Integer, Collection<OwlSpeakOntology>> tree = new TreeMap<Integer, Collection<OwlSpeakOntology>>();
		OwlSpeakOntology tempOnto;
		Iterator<OwlSpeakOntology> ontos = ontologies.iterator();
		int userpos = Settings.getuserpos(user);
		while (ontos.hasNext()) {
			tempOnto = ontos.next();
			if (tempOnto.isenabled(userpos)) {
				tempAgenda = matchAgenda(tempOnto.workSpace[userpos],
						tempOnto.workSpace[userpos],
						tempOnto.beliefSpace[userpos], tempOnto.factory, user);
				if (tempAgenda == null)
					continue;
				tempOnto.setActualAgenda(userpos, tempAgenda);

				if (tree.containsKey(AgendaPrio.calculateAgendaPrio(tempAgenda,
						tempOnto.workSpace[userpos], tempOnto.factory, this,
						actualtime))) {
					(tree.get(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime))).add(tempOnto);
				} else {
					Collection<OwlSpeakOntology> treeColl = new java.util.LinkedList<OwlSpeakOntology>();
					treeColl.add(tempOnto);
					tree.put(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime), treeColl);
				}
			}
		}

		try {
			try {
				if ((actualOnto[userpos] != null)
						&& (tree.get(tree.lastKey())
								.contains(actualOnto[userpos])))
					return;
			} catch (NoSuchElementException k) {
				loadOntologiesFromSettings();
				return;
			}
		} catch (NullPointerException n) {
		}
		try {
			actualOnto[userpos] = (OwlSpeakOntology) tree.get(tree.lastKey())
					.iterator().next();
		} catch (NoSuchElementException e) {
			System.err.println("no agenda was found.");
		}
	}

	/**
	 * Built like matchAgenda. It takes each ontology, selects the most probable
	 * partition as beliefSpace and selects the next best action according to
	 * requirements and priorities of the agendas. This is done by setting the
	 * actualAgenda field of the ontology to the selected action.
	 * 
	 * @param user
	 *            the user name
	 * @see owlSpeak.engine.Core#matchAgenda(String)
	 */
	public void policyHISregular(String user) {
		Agenda tempAgenda;
		TreeMap<Integer, Collection<OwlSpeakOntology>> tree = new TreeMap<Integer, Collection<OwlSpeakOntology>>();
		OwlSpeakOntology tempOnto;
		Iterator<OwlSpeakOntology> ontos = ontologies.iterator();
		int userpos = Settings.getuserpos(user);
		while (ontos.hasNext()) {
			tempOnto = ontos.next();
			if (tempOnto.isenabled(userpos)) {
				// set the current belief space to the most probable partition
				tempOnto.beliefSpace[userpos] = (Partition) tempOnto.partitionDistributions[userpos]
						.getTopPartition();

				tempAgenda = matchAgenda(tempOnto.workSpace[userpos],
						tempOnto.workSpace[userpos],
						tempOnto.beliefSpace[userpos], tempOnto.factory, user);
				if (tempAgenda == null)
					continue;
				tempOnto.setActualAgenda(userpos, tempAgenda);

				if (tree.containsKey(AgendaPrio.calculateAgendaPrio(tempAgenda,
						tempOnto.workSpace[userpos], tempOnto.factory, this,
						actualtime))) {
					(tree.get(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime))).add(tempOnto);
				} else {
					Collection<OwlSpeakOntology> treeColl = new java.util.LinkedList<OwlSpeakOntology>();
					treeColl.add(tempOnto);
					tree.put(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime), treeColl);
				}
			}
		}

		try {
			try {
				if ((actualOnto[userpos] != null)
						&& (tree.get(tree.lastKey())
								.contains(actualOnto[userpos])))
					return;
			} catch (NoSuchElementException k) {
				loadOntologiesFromSettings();
				return;
			}
		} catch (NullPointerException n) {
		}
		try {
			actualOnto[userpos] = (OwlSpeakOntology) tree.get(tree.lastKey())
					.iterator().next();
		} catch (NoSuchElementException e) {
			System.err.println("no agenda was found.");
		}
	}

	/**
	 * Apply the specified policy per active ontology. If multiple agendas are returned, the one with the highest priority is selected. 
	 * 
	 * @param user
	 * @param userAct
	 */
	public void policy(String user, UserAction userAct) {
		actualtime++;
		Agenda tempAgenda;
		TreeMap<Integer, Collection<OwlSpeakOntology>> tree = new TreeMap<Integer, Collection<OwlSpeakOntology>>();
		OwlSpeakOntology tempOnto;
		Iterator<OwlSpeakOntology> ontos = ontologies.iterator();
		int userpos = Settings.getuserpos(user);
		while (ontos.hasNext()) {
			tempOnto = ontos.next();
			if (tempOnto.isenabled(userpos)) {
				// set the current belief space to the partition with highest
				// probability
				if (tempOnto.hasPartitionDistribution())
					tempOnto.beliefSpace[userpos] = (Partition) tempOnto.partitionDistributions[userpos]
							.getTopPartition();

				tempAgenda = Policy.getPolicy(user, tempOnto).policy(this,
						tempOnto, user, userAct);
				if (tempAgenda == null)
					continue;
				tempOnto.setActualAgenda(userpos, tempAgenda);

				if (tree.containsKey(AgendaPrio.calculateAgendaPrio(tempAgenda,
						tempOnto.workSpace[userpos], tempOnto.factory, this,
						actualtime))) {
					(tree.get(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime))).add(tempOnto);
				} else {
					Collection<OwlSpeakOntology> treeColl = new java.util.LinkedList<OwlSpeakOntology>();
					treeColl.add(tempOnto);
					tree.put(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime), treeColl);
				}
				
				try {
					if ((actualOnto[userpos] != null)
							&& (tree.get(tree.lastKey())
									.contains(actualOnto[userpos])))
						return;
				} catch (NoSuchElementException k) {
					System.out.println("Oh oh");
				}
			}
		}

		try {
			try {
				if ((actualOnto[userpos] != null)
						&& (tree.get(tree.lastKey())
								.contains(actualOnto[userpos])))
					return;
			} catch (NoSuchElementException k) {
				loadOntologiesFromSettings();
				return;
			}
		} catch (NullPointerException n) {
		}
		try {
			actualOnto[userpos] = (OwlSpeakOntology) tree.get(tree.lastKey())
					.iterator().next();
		} catch (NoSuchElementException e) {
			System.err.println("no agenda was found.");
		}
	}

	/**
	 * Implementation of the policy as described by Young et al. (The hidden
	 * information state model: A practical framework for POMDP-based spoken
	 * dialogue management (2010), Computer Speech & Language). Build after
	 * matchAgenda. For each ontology, a summary belief point is created and the
	 * closest point out of the summary belief point set is determined. This
	 * summary belief point contains a summary agenda which is then mapped to a
	 * real agenda according to the requirements and priorities of the
	 * agendas. Finally, the actualAgenda field of the ontology is set to the
	 * selected agenda.
	 * 
	 * @param user
	 *            the user name
	 * @param userAct
	 *            the userAct object representing the last user act
	 * @see owlSpeak.engine.Core#matchAgenda(Agenda, WorkSpace, BeliefSpace,
	 *      OSFactory, String)
	 */
	public void policyHIS(String user, UserAction userAct) {
		Agenda tempAgenda = null;
		TreeMap<Integer, Collection<OwlSpeakOntology>> tree = new TreeMap<Integer, Collection<OwlSpeakOntology>>();
		OwlSpeakOntology tempOnto;
		Iterator<OwlSpeakOntology> ontos = ontologies.iterator();
		int userpos = Settings.getuserpos(user);
		while (ontos.hasNext()) {
			tempOnto = ontos.next();
			if (tempOnto.isenabled(userpos)) {

				ISummaryBelief sb;
				if (userAct != null)
					sb = new SummaryBeliefNonpersistent(
							tempOnto.partitionDistributions[userpos],
							userAct.getMove(), tempOnto);
				else
					sb = new SummaryBeliefNonpersistent(
							tempOnto.partitionDistributions[userpos], null, tempOnto);
				SummaryBelief closestSB = null;

				// find closest beliefPoint in summarySpace; O(n)
				for (SummaryBelief beliefPoint : tempOnto.summarySpaceBeliefPoints) {
					if (sb.distanceTo(beliefPoint) < sb.distanceTo(closestSB)) {
						closestSB = beliefPoint;
					}
				}

				if (closestSB != null)
					tempAgenda = summaryAgenda2Agenda(
							closestSB.getSummaryAgenda(), user);

				if (tempAgenda == null)
					continue;
				tempOnto.setActualAgenda(userpos, tempAgenda);

				if (tree.containsKey(AgendaPrio.calculateAgendaPrio(tempAgenda,
						tempOnto.workSpace[userpos], tempOnto.factory, this,
						actualtime))) {
					(tree.get(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime))).add(tempOnto);
				} else {
					Collection<OwlSpeakOntology> treeColl = new java.util.LinkedList<OwlSpeakOntology>();
					treeColl.add(tempOnto);
					tree.put(AgendaPrio.calculateAgendaPrio(tempAgenda,
							tempOnto.workSpace[userpos], tempOnto.factory,
							this, actualtime), treeColl);
				}
			}
		}

		try {
			try {
				if ((actualOnto[userpos] != null)
						&& (tree.get(tree.lastKey())
								.contains(actualOnto[userpos])))
					return;
			} catch (NoSuchElementException k) {
				loadOntologiesFromSettings();
				return;
			}
		} catch (NullPointerException n) {
		}
		if (tree.isEmpty())
			return;
		// try {
		// actualOnto[userpos] = (OwlSpeakOntology) tree.get(tree.lastKey())
		// .iterator().next();
		// } catch (NoSuchElementException e) {
		// System.err.println("no agenda was found.");
		// }
	}

	/**
	 * For user user, selects the action with highest priority out of workspace
	 * whose requirements are satisfied (only field "requires", field "must not"
	 * is not regarded) and which belongs to SummaryAgenda sumarySystemAction
	 * 
	 * @param summaryAgenda
	 *            the summary agenda
	 * @param user
	 *            the user name
	 * 
	 * @return the selected agenda
	 */
	private Agenda summaryAgenda2Agenda(SummaryAgenda summaryAgenda, String user) {
		TreeSet<Agenda> availableAgendas = new TreeSet<Agenda>();

		for (Agenda a : summaryAgenda.getSummarizedAgendas()) {
			WorkSpace ws = actualOnto[Settings.getuserpos(user)].workSpace[Settings
					.getuserpos(user)];
			Collection<Agenda> agendasInWS = ws.getNext();
			Vector<Semantic> availableSemantics = new Vector<Semantic>();
			for (Belief b : actualOnto[Settings.getuserpos(user)].beliefSpace[Settings
					.getuserpos(user)].getHasBelief()) {
				availableSemantics.addAll(b.getSemantic());
			}

			if (agendasInWS.contains(a)) {
				/*
				 * check if agenda is in ws
				 */
				if (a.matchRequires(availableSemantics))
					/*
					 * check only if "requires" condition is fulfilled and not
					 * if "mustNot" is fulfilled
					 */
					availableAgendas.add(a);
			}
		}
		if (!availableAgendas.isEmpty())
			return availableAgendas.last();

		return null;

		// SummaryAgendaType type = summarySystemAction.getType();
		// TreeSet<Agenda> availableAgendas = new TreeSet<Agenda>();
		//
		// for (Agenda a : getAgendasFromSummaryAgendaType(type, user)) {
		// WorkSpace ws =
		// actualOnto[Settings.getuserpos(user)].workSpace[Settings
		// .getuserpos(user)];
		// Collection<Agenda> agendasInWS = ws.getNext();
		// Vector<Semantic> availableSemantics = new Vector<Semantic>();
		// for (Belief b :
		// actualOnto[Settings.getuserpos(user)].beliefSpace[Settings
		// .getuserpos(user)].getHasBelief()) {
		// availableSemantics.addAll(b.getSemantic());
		// }
		//
		// if (agendasInWS.contains(a)) {
		// /*
		// * check if agenda is in ws
		// */
		// if (a.matchRequires(availableSemantics))
		// /*
		// * check only if "requires" condition is fulfilled and not
		// * if "mustNot" is fulfilled
		// */
		// availableAgendas.add(a);
		// }
		// }
		// if (availableAgendas.isEmpty())
		// return null;
		// return availableAgendas.last();
	}

	/**
	 * For user use, selects all agendas whose corresponding summary agenda is
	 * of type type.
	 * 
	 * @param type
	 *            the summary agenda type
	 * @param user
	 *            the user name
	 * @return a collection of agendas which may be empty
	 */
	public Collection<Agenda> getAgendasFromSummaryAgendaType(
			SummaryAgendaType type, String user) {
		TreeSet<Agenda> agendas = new TreeSet<Agenda>();
		for (SummaryAgenda sumA : getSummaryAgendasFromSummaryAgendaType(type,
				user)) {
			agendas.addAll(sumA.getSummarizedAgendas());
		}
		return agendas;
	}

	/**
	 * Returns all summary agendas of type type.
	 * 
	 * @param type
	 *            the summary agenda type
	 * @param user
	 *            the user name
	 * @return a collection of summary agendas which may be empty
	 */
	public Collection<SummaryAgenda> getSummaryAgendasFromSummaryAgendaType(
			SummaryAgendaType type, String user) {
		Vector<SummaryAgenda> agendas = new Vector<SummaryAgenda>();
		OSFactory factory = actualOnto[Settings.getuserpos(user)].factory;
		for (SummaryAgenda a : factory.getAllSummaryAgendaInstances()) {
			if (a.getType() == type) {
				agendas.add(a);
			}
		}

		return agendas;
	}

	/**
	 * Returns the reward associated with agenda a (notwithstanding the current
	 * system state). The method identifies the ontology a belongs to and
	 * searches in all reward objects for the given agenda. If no reward has
	 * been defined for agenda a, a default reward is searched for. If said
	 * reward does not exist either, 0 is returned.
	 * 
	 * @param a
	 *            the agenda for which an reward should be provided
	 * @param user
	 *            the user name
	 * @return the reward value or 0 if none has been defined
	 */
	public int getRewardFromAgenda(Agenda a, String user) {
		OwlSpeakOntology onto = null;

		// find OwlSpeakOntology belonging to Agenda a
		for (OwlSpeakOntology o : ontologies) {
			if (o.factory.onto.equals(a.onto)
					&& o.isenabled(Settings.getuserpos(user))) {
				onto = o;
				break;
			}
		}

		OSFactory factory = onto.factory;

		int summaryVal = Integer.MIN_VALUE;
		// see if agenda or summaryAgenda is contained in one of the reward
		// objects		
		for (Reward r : factory.getAllRewardInstances()) {			
			for (Agenda ra : r.getRewardingAgendas()) {				
				if (ra.indi.equals(a.indi))
					// agenda is in found in one of the reward objects
					return r.getRewardValue();
				if (a.getSummaryAgenda() != null && ra.indi.equals(a.getSummaryAgenda().indi))
					summaryVal = r.getRewardValue();
			}
		}

		if (summaryVal != Integer.MIN_VALUE)
			return summaryVal;

		// agenda not contained. find default reward.
		for (Reward r : factory.getAllRewardInstances()) {
			if (r.getSpecialReward().equalsIgnoreCase("default_reward"))
				return r.getRewardValue();
		}

		return 0;
	}

	/**
	 * returns the first OwlSpeakOntology in the ontologies Vector that matches
	 * the given name
	 * 
	 * @param Name
	 *            the name to search for
	 * @return the OwlSpeakOntology
	 */
	public OwlSpeakOntology findOntology(String Name) {
		OwlSpeakOntology Ausgabe = null;
		OwlSpeakOntology tempOnto;
		Iterator<OwlSpeakOntology> ontoIt = ontologies.iterator();
		while (ontoIt.hasNext()) {
			tempOnto = ontoIt.next();
			if (tempOnto.Name.equals(Name)) {
				Ausgabe = tempOnto;
			} else {
				tempOnto.close();
			}
		}
		return Ausgabe;

	}

	/**
	 * Find the ontology to which the object belongs. Works only under the
	 * assumption that two ontologies do not contain an object with the same
	 * name.<br />
	 * <br />
	 * Currently implemented:
	 * <ul>
	 * <li>{@link Move}
	 * <li>{@link Agenda}
	 * </ul>
	 * 
	 * @param genericObject
	 *            the object
	 * @return the ontology object representing the corresponding ontology
	 */
	public OwlSpeakOntology findMatchingOntology(GenericClass genericObject) {

		for (OwlSpeakOntology onto : ontologies) {
			if (genericObject instanceof Move) {
				Move move = genericObject.asMove();
				for (Move m : onto.factory.getAllMoveInstances()) {
					if (m.equals(move)) {
						return onto;
					}
				}
			} else if (genericObject instanceof Agenda) {
				Agenda agenda = genericObject.asAgenda();
				for (Agenda a : onto.factory.getAllAgendaInstances()) {
					if (a.equals(agenda)) {
						return onto;
					}
				}
			} else if (genericObject instanceof SummaryBelief) {
				SummaryBelief sumBel = genericObject.asSummaryBelief();
				for (SummaryBelief sb : onto.factory.getAllSummaryBeliefInstances()) {
					if (sb.equals(sumBel)) {
						return onto;
					}
				}
			}
		}
		return null;
	}

	/**
	 * loads the Ontologies as specified in the Settings object
	 */
	public void loadOntologiesFromSettings() {
		ontologies = new Vector<OwlSpeakOntology>();
		for (int i = 0; i < Settings.usernames.length; i++) {
			respawnManager[i] = new RespawnManager();
		}
		OwlSpeakOntology temp;
		for (int i = 0; i < Settings.ontologies.length; i++) {
			temp = OwlSpeakOntology.createOntology(Settings.ontologies[i].name,
					Settings.ontologies[i].path, Settings.ontologies[i].file,
					Settings.ontologies[i].ontoHISvariant,Settings.ontologies[i].ontoPolicyVariant,Settings.ontologies[i].ontoStateVariant);
			for (int j = 0; j < Settings.usersetting.length; j++) {
				try {
					if (Settings.usersetting[j].userOntoSettings[i].ontostatus
							.equalsIgnoreCase("enabled"))
						temp.enable(j);
					else
						temp.disable(j);
				} catch (NullPointerException n) {
					temp.disable(j);
				}

			}
			ontologies.add(temp);
		}
	}

	/**
	 * creates a Belief with the given semantic in the BeliefSpace using the
	 * specified factory
	 * 
	 * @param toDoSem
	 *            the Semantic to add in the Belief
	 * @param BS
	 *            the BeliefSpace in which the Belief should be linked
	 * @param factory
	 *            the factory that should be used
	 */
	public void createBelief(Semantic toDoSem, BeliefSpace BS, OSFactory factory) {
		Iterator<Belief> beliefsIt = BS.getHasBelief().iterator();
		Belief tempBel;
		Random test = new Random();
		Vector<Semantic> semantics = new Vector<Semantic>();
		while (beliefsIt.hasNext()) {
			semantics.addAll(beliefsIt.next().getSemantic());
		}
		if (!toDoSem.isMemberOf(semantics)) {
			tempBel = factory.createBelief("Belief_" + test.nextInt());
			BS.addHasBelief(tempBel);
			tempBel.addSemantic(toDoSem);
		}

	}

	/**
	 * Returns the variablestring of a given variable in the given BeliefSpace
	 * 
	 * @param var
	 *            the Variable
	 * @param bel
	 *            the BeliefSpace in which the Variable could be changed
	 * @return the variableString
	 */
	public static String getVariableString(Variable var, BeliefSpace bel) {
		String Ausgabe = "";
		Belief tempbel;
		Iterator<Belief> beliefIt = bel.getHasBelief().iterator();
		try {
			Ausgabe = var.getDefaultValue();
			while (beliefIt.hasNext()) {
				tempbel = beliefIt.next();
				if (tempbel.hasVariabledefault()) {
					if (tempbel.getVariabledefault().getFullName()
							.equals(var.getFullName()))
						Ausgabe = tempbel.getVariableValue();
				}
			}
		} catch (NullPointerException n) {
		}
		return Ausgabe;
	}
	
	public static boolean variableIsContainedInBeliefSpace(Variable var, BeliefSpace bel) {
		for (Belief b : bel.getHasBelief()) {
			if (b.hasVariabledefault() && b.getVariabledefault().getFullName().equals(var.getFullName()))
				return true;
		}
		return false;
	}

	/**
	 * Sets the variablestring of a given variable in the given BeliefSpace
	 * 
	 * @param var
	 *            the Variable
	 * @param bel
	 *            the BeliefSpace in which the Variable should be changed
	 * @param factory
	 *            the factory that should be used
	 * @param value
	 *            the value that should be written to the Variable
	 * @param user
	 *            the name of the current user
	 */
	public void setVariableString(Variable var, BeliefSpace bel,
			OSFactory factory, String value, String user) {
		Belief tempbel;
		Iterator<Belief> beliefIt = bel.getHasBelief().iterator();
		while (beliefIt.hasNext()) {
			tempbel = beliefIt.next();
			if (tempbel.hasVariabledefault()) {
				if (tempbel.getVariabledefault().getFullName()
						.equals(var.getFullName())) {
					tempbel.setVariableValue(tempbel.getVariableValue(), value);
					// if (Settings.youpi==1){
					// int userpos=Settings.getuserpos(user);
					// DeviceServer.changeStateVariable(OwlSpeakYoupi.dev.getRootDevice().getService(OwlSpeakYoupi.service).getStateVariable("lastUserWill"),
					// OwlSpeakYoupi.taskIDMap.get(actualOnto[userpos])+";"+var.getLocalName()+"="+value);
					// }
					return; // belief containing variable exists in belief space
				}
			}
		}

		// a new belief has to be created in belief space
		Random test = new Random();
		tempbel = factory.createBelief("Belief_" + test.nextInt());
		tempbel.setVariableValue(value, value);
		tempbel.setVariabledefault(var, var);
		bel.addHasBelief(tempbel);
		// if (Settings.youpi==1)
		// DeviceServer.changeStateVariable(OwlSpeakYoupi.dev.getRootDevice().getService(OwlSpeakYoupi.service).getStateVariable("lastUserWill"),
		// var.getLocalName()+"="+value);
	}

	/**
	 * returns a vector of all Agendas that match the requirements (OwlScript
	 * and requires and mustnot) !!! work in progress, usage not recommended !!!
	 * 
	 * @param master
	 *            the masteragenda of the current workspace
	 * @param neu
	 *            the current workSpace
	 * @param BS
	 *            the beliefSpace corresponding to the workSpace
	 * @param factory
	 *            the Factory
	 * @param user TODO
	 * @return a vector of Agendas that matches the requirements and do not have
	 *         a mustnot field
	 */
	public Vector<Agenda> findAllPossAgendas(Agenda master, WorkSpace neu,
			BeliefSpace BS, OSFactory factory, String user) {
		Vector<Agenda> ausgabe = new Vector<Agenda>();
		Agenda temp1 = null;
		Iterator<Agenda> masterNext = master.getNext().iterator();
		Iterator<Belief> beliefsIt = BS.getHasBelief().iterator();
		Iterator<Semantic> mustIt;
		Semantic tempSem;
		boolean mustnot;
		Vector<Semantic> semantics = new Vector<Semantic>();
		if (!masterNext.hasNext()) {
			ausgabe.add(master);
			return ausgabe;
		}
		while (beliefsIt.hasNext()) {
			semantics.addAll(beliefsIt.next().getSemantic());
		}

		while (masterNext.hasNext()) {
			temp1 = masterNext.next();
			if (temp1.matchRequires(semantics)) {
				mustnot = false;
				mustIt = temp1.getMustnot().iterator();
				while (mustIt.hasNext()) {
					tempSem = mustIt.next();
					if (semantics.contains(tempSem)) {
						mustnot = true;
						break;
					}
				}
				if (mustnot)
					continue;

				if (temp1.hasVariableOperator()) {
					String input = temp1.getVariableOperator();
					input = CoreMove.parseStringVariables(input, factory, this,
							BS, user);
					String[] test = owlSpeak.engine.OwlScript
							.evaluateString(input);
					if ((test[0].equalsIgnoreCase("REQUIRES"))
							&& (!test[1].equals("1"))) {
						continue;
					}
				}
				if (temp1.hasHas()) {
					ausgabe.add(temp1);
				}
			}
		}

		return ausgabe;
	}

	/**
	 * returns the Agenda that has the highest procTime !!! work in progress,
	 * usage not recommended !!!
	 * 
	 * @param WS
	 *            the WorkSpace
	 * @param factory
	 *            the Factory
	 * @return the Agenda that has the highest procTime
	 */
	public Agenda getLastProc(WorkSpace WS, OSFactory factory) {
		Iterator<History> HisIt = factory.getAllHistoryInstances().iterator();
		Vector<History> Histories = new Vector<History>();
		History tempHist;
		while (HisIt.hasNext()) {
			tempHist = HisIt.next();
			if (tempHist.getInWorkspace().getFullName()
					.equals(WS.getFullName())) {
				Histories.add(tempHist);
			}
		}
		Collections.sort(Histories, new Comparator<History>() {
			public int compare(History his2, History his1) {
				return Integer.valueOf(his1.getProcTime()).compareTo(
						Integer.valueOf(his2.getProcTime()));
			}
		});
		tempHist = Histories.lastElement();
		return factory.getAgenda(tempHist.getForAgenda().getLocalName());
	}

	/**
	 * resets the quality parameters by creating new QualityParameter objects
	 * for each user
	 */
	public void resetAllQualityParameters() {
		// create new quality parameters object
		for (int i = 0; i < qualityParameter.length; i++) {
			qualityParameter[i] = new QualityParameter();
		}
	}

	public void writeAndUpdateAllOntolgies() {
		for (OwlSpeakOntology onto : ontologies) {
			onto.write();
			onto.update();
		}
	}
	
	public void updateAllOntolgies() {
		for (OwlSpeakOntology onto : ontologies) {
			onto.update();
		}
	}
}