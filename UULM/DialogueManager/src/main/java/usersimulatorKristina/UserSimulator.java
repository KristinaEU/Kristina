package usersimulatorKristina;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import usersimulatorKristina.control.Agenda;
import usersimulatorKristina.control.StateAgenda;
import usersimulatorKristina.control.SystemAction;
import usersimulatorKristina.control.UserAction;
import usersimulatorKristina.dialogueacts.AcceptAct;
import usersimulatorKristina.dialogueacts.AcknowledgeAct;
import usersimulatorKristina.dialogueacts.ApologiseAct;
import usersimulatorKristina.dialogueacts.DeclareAct;
import usersimulatorKristina.dialogueacts.DialogueAct;
import usersimulatorKristina.dialogueacts.DialogueType;
import usersimulatorKristina.dialogueacts.GreetAct;
import usersimulatorKristina.dialogueacts.RejectAct;
import usersimulatorKristina.dialogueacts.RequestAct;
import usersimulatorKristina.dialogueacts.SayGoodbyeAct;
import usersimulatorKristina.dialogueacts.ShowAct;
import usersimulatorKristina.dialogueacts.ThankAct;
import usersimulatorKristina.goal.Constraint;
import usersimulatorKristina.goal.Request;
import usersimulatorKristina.goal.UserGoal;

/**
 * 
 * User Simulator
 * 
 * 1) Initialize UserSimulator us = new UserSimulator();
 * 
 * 2) If you want the user to start the conversation: us.startConversation()
 * 
 * 3) For retrieving a User Move: us.getNextUserMove(SystemAction sa)
 * 
 * 4) For reseting User Goal and Agenda: us.resetUserSimulator()
 * 
 * 
 * use UserSimulationKristina.java for testing purpose
 * 
 * @author ifeustel
 *
 */

public class UserSimulator {

	private StateAgenda currState;
	private List<StateAgenda> stateTree;
	private UserGoal goal;

	private String pushTransitionModel = "./conf/OwlSpeak/models/US_Kristina/data.json";
	private JSONObject pushModel;

	// Checks if greeting already done
	private boolean checkGreet;

	public UserSimulator() {
		goal = new UserGoal();
		// Empty State
		currState = new StateAgenda(new Agenda());
		// Create Agenda from User Goal
		Agenda ag = new Agenda(goal);
		initAgendaTree(ag);

		checkGreet = false;
		
		/**
		 * initialize Reader + JSON Object
		 */
		FileReader reader;
		try {
			reader = new FileReader(pushTransitionModel);
			JSONParser jsonParser = new JSONParser();
			pushModel = (JSONObject) jsonParser.parse(reader);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Initialize StateTree with permutations from incoming agenda
	 * 
	 * @param agenda
	 */
	public void initAgendaTree(Agenda agenda) {
		// Empty Agenda as Start Node
		stateTree = new LinkedList<StateAgenda>();
		stateTree.add(currState);

		// permuted agendas generated from user goal
		List<Agenda> permutatedAgendas = Agenda.getPermutatedAgendas(agenda);
		Agenda.setProbability(permutatedAgendas);
		for (Agenda a : permutatedAgendas) {
			StateAgenda temp = new StateAgenda(a);
			temp.addParent(currState);
			currState.addChild(temp);
			stateTree.add(temp);
		}

	}

	/**
	 * Reset UserSimulator: new goal new agenda_tree
	 * 
	 */
	public void resetUserSimulator() {
		goal = new UserGoal();
		currState = new StateAgenda(new Agenda());
		Agenda agenda = new Agenda(goal);

		checkGreet = false;
		initAgendaTree(agenda);
	}

	public void updateAgendaTree() {
		stateTree = StateAgenda.mergeStateAgendas(stateTree);
	}

	/**
	 * Start Conversation US will start conversation
	 * 
	 * Method pops first DialogueAct of the Agenda (GreetAct)
	 * and randomly one more act
	 * 
	 * @return
	 */
	public UserAction startConversation() {
		StateAgenda newState = chooseAgenda();
		if (newState == null) {
			// ErrorHandling?
			System.err.println("No state found");
		}
		currState = newState;
		// pop only GreetAct
		DialogueAct greetAct = currState.getAgenda().popAct();
		if (!(greetAct instanceof GreetAct)) {
			System.err.println("Something went wrong.. No GreetAct on top of agenda.");
			return null;
		}

		UserAction userMove = new UserAction(greetAct);

		// pop 1 more act
		Random rand = new Random();
		if (rand.nextBoolean()) {
			DialogueAct nextAct = currState.getAgenda().popAct();
			userMove.addAct(nextAct);
		}

		checkGreet = true;

		return userMove;
	}

	/**
	 * 
	 * Method for creating next User move corresponding on incoming SystemAction
	 * 
	 * @param sysAct
	 * @return
	 */
	public UserAction getUserMove(SystemAction sysAct) {

		List<DialogueAct> sysDiaActs = sysAct.getActs();

		// Each list contains possible reactions for the depending system act
		// e.g. SysAction = accept, request => possibleUserActsForAllSysActs =
		// [[ack,req], [accept,reject]]
		// having ack, req being reactions on systems accept and accept reject
		// being reactions on systems request
		List<List<DialogueAct>> possibleUserActsForAllSysActs = new LinkedList<List<DialogueAct>>();

		// Checks if systemAct follows the User Goal
		List<Boolean> isValidForUserGoal = new ArrayList<Boolean>();

		
		//create possible answer DialogueActions for each system DialogueAction
		for (DialogueAct sysDiaAct : sysDiaActs) {
			if (sysDiaAct != null) {

				// Check if Topics are goal oriented
				isValidForUserGoal.add(validForUserGoal(sysDiaAct));

				// possible Declare Act
				DialogueAct declare = null;

				/*
				 * Incoming Declare: Check goal requests and set isAnswered
				 * true. Only for Requests! Not RequestWeather etc..
				 */
				if (sysDiaAct instanceof DeclareAct) {
					Set<String> topics = sysDiaAct.getTopics();
					if (topics != null) {
						for (String topic : topics) {
							for (Request r : goal.getRequests()) {
								if (topic.equals(r.getVal())) {
									r.setAnswered(true);
								}
							}
						}
					}
				}
				// Incoming ShowAct: check corresponding requests (e.g.
				// RequestWeather) and set isAnswered true.
				else if (sysDiaAct instanceof ShowAct) {
					switch (sysDiaAct.getType()) {
					case ReadNewspaper:
						for (Request r : goal.getRequests()) {
							if (r.getIdentifier().equals("RequestNewspaper")) {
								r.setAnswered(true);
								break;
							}
						}
						break;
					case ShowWeather:
						for (Request r : goal.getRequests()) {
							if (r.getIdentifier().equals("RequestWeather")) {
								r.setAnswered(true);
								break;
							}
						}
						break;
					case ShowVideo:
						/**
						 * Not implemented yet
						 */
					case ShowWebpage:
						/**
						 * Not implemented yet
						 */
						break;
					default:
						System.err.println("Could not match " + sysDiaAct.getType() + " as RequestAct");
						break;
					}
				}
				// Incoming RequestAct: Look up suitable constraints as answer
				else if (sysDiaAct instanceof RequestAct) {
					switch (sysDiaAct.getType()) {
					case RequestMissingInformation:
					case RequestAdditionalInformation:
						Constraint con = goal.getSuitableConstraint(sysDiaAct.getTopics());
						if (con != null) {
							declare = DialogueAct.createDeclareAct(con);
						}
						break;
					case RequestPresenceEmotion:
						/**
						 * Not implemented yet
						 */
					case RequestReasonForEmotion:
						/**
						 * Not implemented yet
						 */
					default:
						break;
					}
				}
				// Has not yet been implemented since logic handling here is complex
				else if (sysDiaAct instanceof AcceptAct) {
					// TODO: set request answered if previous act was a request (?)
				} else if (sysDiaAct instanceof RejectAct) {
					// TODO: 
				} else if (sysDiaAct instanceof AcknowledgeAct) {
					// TODO
				}

				//if user goal has been achieved then stop pushing more actions and finish conversation
				if (goal.isComplete()) {
					break;
				}

				// Transition Model:
				// P(useract|sysact)
				List<DialogueAct> tempPossActs = extractPossibleActsFromJSON(sysDiaAct);
				DialogueAct empty = DialogueAct.createDialogueAct(DialogueType.Declare);
				if (declare != null) {
					if (tempPossActs.contains(empty)) {
						int index = tempPossActs.indexOf(empty);
						tempPossActs.set(index, declare);
					}
				}
				if (tempPossActs != null) {
					possibleUserActsForAllSysActs.add(tempPossActs);
				}
			}

		}

		// Create Combination of PushActions
		List<List<DialogueAct>> possibleUserPushActions = new LinkedList<List<DialogueAct>>();
		generatePermutations(possibleUserActsForAllSysActs, possibleUserPushActions, 0, new LinkedList<DialogueAct>());

		// System.out.println("my size1: " + stateTree.size());
		checkStatesForUserGoal(possibleUserPushActions, isValidForUserGoal);
		// System.out.println("my size2: " + stateTree.size());
		stateTree = StateAgenda.mergeStateAgendas(stateTree);

		// System.out.println("my size3: " + stateTree.size());
		/**
		 * TODO: Anzahl der Items zum Pushen P(n|act) Erstmal Random (1-3),
		 * sp�ter aus DB auslesen?
		 */
		Random rand = new Random();
		int n = rand.nextInt(3) + 1;

		int size = currState.getAgenda().getDialogueActs().size();
		if (n > size) {
			n = size;

		}

		// if two or three actions are left and one of them is a request, only
		// pop the
		// request (or last 2 acts)
		DialogueAct lastElem = currState.getAgenda().getDialogueActs().getLast();
		if (size > 1) {
			DialogueAct prevElem = currState.getAgenda().getDialogueActs()
					.get(currState.getAgenda().getDialogueActs().size() - 2);
			if (size < 3 && lastElem instanceof RequestAct) {
				n = 1;
			} else if (size == 3 && (lastElem instanceof RequestAct || prevElem instanceof RequestAct)) {
				n = 2;
			}
		}

		
		//Pick a new state
		StateAgenda newState = chooseAgenda();
		if (newState == null) {
			// ErrorHandlin!
			System.err.println("No state found");
		}

		currState = newState;
		LinkedList<DialogueAct> finalUserActs = currState.getAgenda().popActs(n);
		UserAction userMove = new UserAction(finalUserActs);

		//If a request is going to be asked set its counter +1
		for (DialogueAct act : finalUserActs) {
			if (act instanceof RequestAct) {
				goal.updateCountOfRequest((RequestAct) act);
			}
		}

		//only for testing purpose, show the completion of user goal
		if (finalUserActs.getLast() instanceof SayGoodbyeAct) {
			System.out.println(goal.getGoalCompletion());
		}

		return userMove;
	}

	/*
	 * 
	 * Get Possible User Actions from user Model
	 * 
	 */
	private List<DialogueAct> extractPossibleActsFromJSON(DialogueAct sysAct) {
		if (pushModel == null || sysAct == null) {
			return null;
		}
		JSONObject sysActObject = (JSONObject) pushModel.get(sysAct.getType().toString());
		Object[] userActionKeys = sysActObject.keySet().toArray();

		List<DialogueAct> possibleUserActionsForSysAct = new LinkedList<DialogueAct>();

		for (Object key : userActionKeys) {
			DialogueAct userAct = DialogueAct.createDialogueAct(DialogueType.valueOf(key.toString()));
			userAct.setProbability(Double.valueOf(sysActObject.get(key).toString()));
			possibleUserActionsForSysAct.add(userAct);
		}

		return possibleUserActionsForSysAct;

	}

	//Pick agenda with probability if none has been picked, pick the one with maximum Probability
	private StateAgenda chooseAgenda() {
		Random rand = new Random();
		StateAgenda maxProb = null;
		for (StateAgenda a : stateTree) {
			if (a.getParents().contains(currState)) {
				if (rand.nextDouble() <= a.getAgenda().getTransprobability()) {
					return a;
				}
				if (maxProb == null) {
					maxProb = a;
				} else if (a.getAgenda().getTransprobability() > maxProb.getAgenda().getTransprobability()) {
					maxProb = a;
				}
			}
		}

		return maxProb;

	}

	
	/*
	 * 
	 * Checks wether chosen possible User DialogueAction are valid for User Goal
	 * 
	 * 
	 */
	private void checkStatesForUserGoal(List<List<DialogueAct>> possibleUserPushActions,
			List<Boolean> isValidForUserGoal) {
		for (List<DialogueAct> possibleActs : possibleUserPushActions) {
			
			//Create new Agenda for adding to state tree (can be the same as curr State!)
			Agenda tempAgenda = currState.getAgenda();
			double transprob = calculateAgendaProbability(possibleActs);

			boolean isValid = false;
			if (!possibleActs.isEmpty()) {
				isValid = isValidForUserGoal.get(possibleActs.size() - 1);

			}

			Vector<DialogueAct> toRemove = new Vector<DialogueAct>();
			Vector<DialogueAct> toAdd = new Vector<DialogueAct>();
			
			/*
			 * Check specific acts and handle them different
			 * 
			 */
			for (DialogueAct act : possibleActs) {
				switch (act.getType()) {
				case Declare:
					//If it is a declare, try to push a declare from the curr agenda before creating a new one
					if (act.getTopics().isEmpty()) {
						DialogueAct nextDeclare = tempAgenda.popNextSpecAct(DialogueType.Declare);
						if (nextDeclare != null && nextDeclare.getType().equals(DialogueType.Declare)) {
							nextDeclare.setProbability(act.getProbability());
							toAdd.add(nextDeclare);
						}
						toRemove.add(act);
					}
					break;
				case Acknowledge:
					//TODO: Different Handling than accepT?
				case Accept:
					//only pushed if in UG 
					if (!isValid) {
						toRemove.addElement(act);
					}
					break;

				case Reject:
					//only pushed if not in UG
					if (isValid) {
						toRemove.addElement(act);
					}
					break;
				case Request:
				case RequestNewspaper:
				case RequestWeather:
					//For each request check which requests has not been answered yet and push them again on top of the agenda
					DialogueType type = act.getType();
					Request request = goal.getNotAnsweredRequest(type);

					DialogueAct nextRequest = null;
					if (request != null) {
						nextRequest = DialogueAct.createRequestAct(request);
					}
					// System.out.println("req up");
					if (nextRequest == null) {
						nextRequest = tempAgenda.popNextSpecAct(type);
					}
					// get next RequestAct on Agenda and put it to the top
					if (nextRequest != null && nextRequest.getType().equals(type)) {
						nextRequest.setProbability(act.getProbability());
						toAdd.add(nextRequest);
					}
					toRemove.add(act);
				default:
					break;
				}
				//remove any greet acts if the greeting has already been completed
				if (act instanceof GreetAct && checkGreet) {
					toRemove.add(act);
				}
			}

			possibleActs.addAll(toAdd);
			possibleActs.removeAll(toRemove);

			// check for duplicates (e.g. 2 GreetActs/ThankActs)
			possibleActs = checkForDuplicatedActs(possibleActs);

			if (possibleActs.isEmpty()) {
				tempAgenda.setTransprobability(transprob);
			} else {
				tempAgenda.pushActs(possibleActs);
				tempAgenda.setTransprobability(calculateAgendaProbability(possibleActs));
			}

			
			//add agenda to statetree 
			StateAgenda tempState = new StateAgenda(tempAgenda);
			currState.addChild(tempState);
			tempState.addParent(currState);
			stateTree.add(tempState);

		}
	}

	/*
	 * Checks if a sysAct is valid for the current user goal
	 */
	private boolean validForUserGoal(DialogueAct sysAct) {
		if (sysAct.getType().equals(DialogueType.AskTask) && goal.hasNotAnsweredRequests()) {
			return true;
		}
		Set<String> topics = sysAct.getTopics();
		if (topics != null) {
			for (String topic : topics) {
				if (goal.checkOccuranceOfTopic(topic)) {
					return true;
				}
			}
		}
		;
		return false;
	}

	private List<DialogueAct> checkForDuplicatedActs(List<DialogueAct> possibleActs) {
		List<DialogueAct> contains = new ArrayList<DialogueAct>();
		RequestAct request = null;
		DeclareAct declare = null;

		for (DialogueAct act : possibleActs) {
			if (act instanceof GreetAct) {
				if (!checkForSpecificDialogueActClass(contains, "GreetAct")) {
					contains.add(act);
				}
			} else if (act instanceof SayGoodbyeAct) {
				if (!checkForSpecificDialogueActClass(contains, "SayGoodbyeAct")) {
					contains.add(act);
				}
			} else if (act instanceof AcceptAct) {
				if (!checkForSpecificDialogueActClass(contains, "AcceptAct")) {
					contains.add(act);
				}
			} else if (act instanceof AcknowledgeAct) {
				if (!checkForSpecificDialogueActClass(contains, "AcknowledgeAct")) {
					contains.add(act);
				}
			} else if (act instanceof RejectAct) {
				if (!checkForSpecificDialogueActClass(contains, "RejectAct")) {
					contains.add(act);
				}
			} else if (act instanceof ApologiseAct) {
				if (!checkForSpecificDialogueActClass(contains, "ApologiseAct")) {
					contains.add(act);
				}
			} else if (act instanceof ThankAct) {
				if (!checkForSpecificDialogueActClass(contains, "ThankAct")) {
					contains.add(act);
				}
			} else if (act instanceof RequestAct) {
				if (request == null) {
					request = (RequestAct) act;
					contains.add(act);
				} else {
					request.addTopics(act.getTopics());
				}

			} else if (act instanceof DeclareAct) {
				if (declare == null) {
					declare = (DeclareAct) act;
					contains.add(act);
				} else {
					declare.addTopics(act.getTopics());
				}

			} else if (!contains.contains(act)) {
				contains.add(act);
			}
		}
		return contains;
	}

	/**
	 * Checks if a list contains a specific type of DialogueAct (e.g.
	 * GreetAct..)
	 * 
	 * @param acts
	 * @param dclass
	 * @return
	 */
	private boolean checkForSpecificDialogueActClass(List<DialogueAct> acts, String dclass) {
		for (DialogueAct act : acts) {
			switch (dclass) {
			case "GreetAct":
				if (act instanceof GreetAct) {
					return true;
				}
				break;
			case "SayGoodbyeAct":
				if (act instanceof SayGoodbyeAct) {
					return true;
				}
				break;

			case "AcceptAct":
				if (act instanceof AcceptAct) {
					return true;
				}
				break;
			case "AcknowledgeAct":
				if (act instanceof AcknowledgeAct) {
					return true;
				}
				break;
			case "RejectAct":
				if (act instanceof RejectAct) {
					return true;
				}
				break;
			case "ApologiseAct":
				if (act instanceof ApologiseAct) {
					return true;
				}
				break;
			case "ThankAct":
				if (act instanceof ThankAct) {
					System.out.println("duplicated thank");
					return true;

				}
				break;

			default:
				break;
			}

		}
		return false;
	}

	/*
	 * Permutation of Dialogue PushActs
	 * 
	 * X: [A, B, C] Y: [W, X, Y, Z] => [AW, AX, AY, AZ, BW, BX, BY, BZ, CW, CX,
	 * CY, CZ]
	 * 
	 */
	private static void generatePermutations(List<List<DialogueAct>> original, List<List<DialogueAct>> result,
			int depth, List<DialogueAct> current) {
		if (depth == original.size()) {
			result.add(current);
			return;
		}
		List<DialogueAct> currentList = original.get(depth);
		for (DialogueAct d : currentList) {
			List<DialogueAct> copy = new LinkedList<DialogueAct>(current);
			copy.add(d);
			generatePermutations(original, result, depth + 1, copy);
		}

	}

	private double calculateAgendaProbability(List<DialogueAct> acts) {
		double prob = 1.0;
		for (DialogueAct a : acts) {
			prob = prob * a.getProbability();
		}
		return prob;
	}

	public StateAgenda getStateAgenda(Agenda a) {
		for (StateAgenda stateAgenda : stateTree) {
			if (stateAgenda.getAgenda().equals(a)) {
				return stateAgenda;
			}
		}
		return null;
	}

	/**
	 * 
	 * For testing issues
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		List<DialogueAct> temp1 = new LinkedList<DialogueAct>();
		temp1.add(DialogueAct.createDialogueAct(DialogueType.Reject));
		// temp1.add(DialogueAct.createDialogueAct(DialogueType.Request));
		// temp1.add(DialogueAct.createDialogueAct(DialogueType.Declare));

		List<DialogueAct> temp2 = new LinkedList<DialogueAct>();
		temp2.add(DialogueAct.createDialogueAct(DialogueType.SimpleApologise));
		// temp2.add(DialogueAct.createDialogueAct(DialogueType.Acknowledge));
		// temp2.add(DialogueAct.createDialogueAct(DialogueType.Accept));

		List<DialogueAct> temp3 = new LinkedList<DialogueAct>();
		temp3.add(DialogueAct.createDialogueAct(DialogueType.SimpleGreet));

		List<List<DialogueAct>> test = new LinkedList<List<DialogueAct>>();
		test.add(temp1);
		test.add(temp2);

		// List<List<DialogueAct>> test2 = new LinkedList<List<DialogueAct>>();

		// generatePermutations(test, test2, 0, new LinkedList<DialogueAct>());

		UserSimulator us = new UserSimulator();
		us.getUserMove(new SystemAction(DialogueAct.createDialogueAct(DialogueType.Accept)));

		Agenda a = new Agenda(temp1);
		Agenda b = new Agenda(temp1);
		Agenda c = new Agenda(temp2);
		Agenda d = new Agenda(temp3);

		List<Agenda> agendas = new LinkedList<Agenda>();
		agendas.add(a);
		agendas.add(b);
		agendas.add(c);
		agendas.add(d);

		/*
		 * List<Agenda> merged = us.currState.getAgenda().mergeAgendas(agendas);
		 * for (Agenda ag : merged) { System.out.println(ag.getDialogueActs());
		 * }
		 */

		StateAgenda empty = new StateAgenda();
		StateAgenda sa = new StateAgenda(a);
		StateAgenda sb = new StateAgenda(b);
		StateAgenda sc = new StateAgenda(a);
		StateAgenda sd = new StateAgenda(d);

		sa.addParent(empty);
		empty.addChild(sa);
		sb.addParent(empty);
		empty.addChild(sb);

		sc.addParent(sa);
		sa.addChild(sc);
		sd.addParent(sb);
		sb.addChild(sd);

		List<StateAgenda> state = new LinkedList<StateAgenda>();
		state.add(empty);
		state.add(sa);
		state.add(sb);
		state.add(sc);
		state.add(sd);

		System.out.println("Before: " + state);
		state = StateAgenda.mergeStateAgendas(state);
		for (StateAgenda saa : state) {
			System.out.println("im " + saa + " my acts: " + saa.getAgenda().getDialogueActs() + " and my parents are: "
					+ saa.getParents() + " and my childs are: " + saa.getChildren());
		}
		System.out.println("After: " + state);

	}

}
