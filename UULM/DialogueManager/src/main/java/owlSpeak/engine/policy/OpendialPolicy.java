package owlSpeak.engine.policy;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import owlSpeak.Agenda;
import owlSpeak.Variable;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.policy.opendial.OpendialDialog;
import owlSpeak.kristina.DialogueAction;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.emotion.KristinaEmotion;

public class OpendialPolicy extends Policy {
	@Override
	public List<Agenda> policy(Core core, OwlSpeakOntology onto, String user,
			UserAction userAct) {

		System.out
				.println("---------------------------------------OpendialPolicy");

		List<Agenda> ret = new LinkedList<Agenda>();

		int userpos = Settings.getuserpos(user);
		Agenda workspace = onto.workSpace[userpos];
		if (!workspace.getNext().iterator().hasNext()) {
			System.out.println("Workspace: empty");
			return null;
		}

		if (workspace.getNext().size() == 1) {
			ret.addAll(workspace.getNext());
			return ret;
		}

		OpendialDialog opendial = OpendialDialog.getInstance();
		
		//get last userMove
		
		Collection<KristinaMove> kristinaMoves = onto.factory
				.getAllKristinaMoveInstances();
		KristinaMove lastMove = null;
		for (KristinaMove move : kristinaMoves) {
			if (move.getIsLastUserMove() && !move.toString().isEmpty()) {
				lastMove = move;
				break;
			}
		}

		List<KristinaAgenda> allAgendasInWorkspace = workspace.getNext()
				.stream().map(k -> k.asKristinaAgenda())
				.collect(Collectors.toList());
		System.out.println(allAgendasInWorkspace.toString());

		// select the agenda with minimum age and maximum plausibility as
		// possible kiAgenda

		for (KristinaAgenda a : allAgendasInWorkspace) {
			if (a.getLocalName().contains("agenda_KI"))
			System.out.println(a.getAge()+" "+a.getLocalName()+" "+a);
		}

		KristinaAgenda kiVariable = null;
		if(lastMove.getRDFType().contains(DialogueAction.REQUEST_REPLAY)){
			int minAge = 100;
			for (KristinaAgenda a : allAgendasInWorkspace) {
				if (a.getLocalName().contains("agenda_KI") && a.getAge() < minAge && a.isDialogueAction(DialogueAction.SHOW_WEBPAGE)) {
					minAge = a.getAge();
				}
			}
			for (KristinaAgenda a : allAgendasInWorkspace) {
				if (a.getLocalName().contains("agenda_KI") && a.getAge() == minAge&& a.isDialogueAction(DialogueAction.SHOW_WEBPAGE)) {
					kiVariable = a;
					break;
				}
			}
		}else{
	
		List<KristinaAgenda> allAgendasWithMinAge = new LinkedList<KristinaAgenda>();
		List<KristinaAgenda> allAgendasWithMaxPlausibility = new LinkedList<KristinaAgenda>();
		int minAge = 100;
		float maxPlausibility = 0.0f;
		if(lastMove.hasTopic("Other") || lastMove.hasTopic("Else")){
			maxPlausibility = 0.5f;
		}
		for (KristinaAgenda a : allAgendasInWorkspace) {
			if (a.getLocalName().contains("agenda_KI") && a.getAge() < minAge) {
				minAge = a.getAge();
			}
		}
		for (KristinaAgenda a : allAgendasInWorkspace) {
			if (a.getLocalName().contains("agenda_KI") && a.getAge() == minAge) {
				allAgendasWithMinAge.add(a);
			}
		}
		for (KristinaAgenda a : allAgendasWithMinAge) {
			if (a.getPlausibility() > maxPlausibility) {
				maxPlausibility = a.getPlausibility();
			}
		}
		for (KristinaAgenda a : allAgendasWithMinAge) {
			if (a.getPlausibility() == maxPlausibility) {
				allAgendasWithMaxPlausibility.add(a);
			}
		}
		System.out.println("Number of Agendas with maximum Plausibility: "
				+ allAgendasWithMaxPlausibility.size());
		if (allAgendasWithMaxPlausibility.size() == 1) {
			kiVariable = allAgendasWithMaxPlausibility.get(0);
		} else if (allAgendasWithMaxPlausibility.size() == 0) {
			kiVariable = null;
		} else {
			int randomNum = new Random().nextInt(allAgendasWithMaxPlausibility
					.size());
			kiVariable = allAgendasWithMaxPlausibility.get(randomNum);
		}
		}
		String kiName = kiVariable == null ? "" : kiVariable.getLocalName();

		// get variables
		
		String scenario = Core.getVariableString(
				onto.factory.getVariable("var_Scenario"),
				onto.beliefSpace[userpos]);
		String userValence = Core.getVariableString(
				onto.factory.getVariable("var_UserValence"),
				onto.beliefSpace[userpos]);
		float usrVal = Float.parseFloat(userValence);
		String userArousal = Core.getVariableString(
				onto.factory.getVariable("var_UserArousal"),
				onto.beliefSpace[userpos]);
		float usrAr = Float.parseFloat(userArousal);
		// TODO add other variables for culture and emotion
		String culture = Core.getVariableString(
				onto.factory.getVariable("var_UserCulture"),
				onto.beliefSpace[userpos]);
		
		if (lastMove != null) {
			String weather = Core.getVariableString(
					onto.factory.getVariable("var_Weather"),
					onto.beliefSpace[userpos]);
			weather = weather == null ? "" : weather;
			String offer = Core.getVariableString(
					onto.factory.getVariable("var_Offer"),
					onto.beliefSpace[userpos]);
			List<String> dialogueActions = lastMove.getRDFType().stream()
					.map(s -> s.substring(s.indexOf('#') + 1)).distinct()
					.collect(Collectors.toList());
			final KristinaMove sortMove = lastMove;

			System.out.println(dialogueActions);
			if(Math.sqrt(usrVal*usrVal + usrAr*usrAr) > 1.5){
				dialogueActions.add(0, "emotion");
			}
			List<Set<KristinaAgenda>> tmp3 = new LinkedList<Set<KristinaAgenda>>();
			String answerFound = dialogueActions.stream().anyMatch(d -> !d.equals("Statement"))?"yes":""; 
			boolean askTask = dialogueActions.stream().filter(d -> !d.equals("Statement")).count()==1
					&&dialogueActions.stream().filter(d -> d.equals("Acknowledge")).count()>=1
					&&(kiName.isEmpty()||(kiVariable==null?true:kiVariable.getDialogueAction().split("#")[1].toLowerCase().contains("unknown")));
			for (int k = 0; k < dialogueActions.size(); k++) {
				String da = dialogueActions.get(k);
				// send all variables to opendial
				if (k == dialogueActions.size() - 1) {
					opendial.updateVariable("isLast", "true");
				} else {

					opendial.updateVariable("isLast", "false");
				}
				opendial.updateVariable("answerFound", answerFound);
				opendial.updateVariable("nrDAs", askTask?"1":Integer.toString(dialogueActions.size()));
				opendial.updateVariable("kiVariable", kiName);
				opendial.updateVariable("userValence", userValence);
				opendial.updateVariable("userArousal", userArousal);
				opendial.updateVariable("scenario", scenario);
				opendial.updateVariable("weather", weather);
				opendial.updateVariable("offer", offer);
				opendial.updateVariable("userMoveTopic",
						lastMove.getTopics().stream().map(s -> s.split("#")[1])
								.collect(Collectors.toList()).toString());
				opendial.updateVariable("kiMoveTopic",
						kiVariable==null?"":kiVariable.getTopics().stream().map(s -> s.split("#")[1])
								.collect(Collectors.toList()).toString());
				opendial.updateVariable("kiMoveDA",
						kiVariable==null?"":kiVariable.getDialogueAction().split("#")[1]);
				opendial.updateVariable("kiText",
						kiVariable==null?"":(kiVariable.getText()== null?"":kiVariable.getText()));
				opendial.updateVariable("userMoveNegatedTopic", lastMove
						.getNegatedTopics().stream().map(s -> s.split("#")[1])
						.collect(Collectors.toList()).toString());
				opendial.updateVariable("userCulture", culture);

				// send trigger variable to opendial
				opendial.updateVariable("a_u", da);

				// get answer from opendial
				String nextSysMove = opendial.getNextSystemMove(10);

				LinkedList<Set<KristinaAgenda>> tmp = new LinkedList<Set<KristinaAgenda>>();
				if (nextSysMove != null) {
					String[] parts = nextSysMove.split("\\+");
					for (int i = 0; i < parts.length; i++) {
						String[] choices = parts[i].split("\\,");
						Set<KristinaAgenda> tmp2 = new HashSet<KristinaAgenda>();
						for (int j = 0; j < choices.length; j++) {
							for (KristinaAgenda a : allAgendasInWorkspace) {
								if (choices[j] != null
										&& choices[j].equals(a.getLocalName())) {
									tmp2.add(a);
								}
							}
						}
						tmp.add(tmp2);
					}
				} else {
					Set<KristinaAgenda> tmp2 = new HashSet<KristinaAgenda>();
					tmp2.add(onto.factory.getKristinaAgenda("agenda_Empty"));
					tmp.add(tmp2);
				}
				tmp3.addAll(tmp);

			}
			ret = selectSystemMove(tmp3,
					onto.factory.getKristinaAgenda("agenda_Acknowledge"));
			

			if(ret.stream().anyMatch(d -> d.asKristinaAgenda().getDialogueAction().contains("Greet")) && ret.stream().anyMatch(d -> d.asKristinaAgenda().isDialogueAction(DialogueAction.ACKNOWLEDGE))){
				ret.removeIf(d -> d.asKristinaAgenda().isDialogueAction(DialogueAction.ACKNOWLEDGE));
			}
			if(ret.stream().filter(d -> !d.asKristinaAgenda().isDialogueAction(DialogueAction.ACKNOWLEDGE)).count()==0 && dialogueActions.stream().filter(s -> s.contains("Greet")).count() > 0){
				ret.removeIf(d -> d.asKristinaAgenda().isDialogueAction(DialogueAction.ACKNOWLEDGE));
				ret.add(onto.factory.getKristinaAgenda("agenda_SimpleGreet"));
			}
			if(ret.stream().filter(d -> !d.asKristinaAgenda().isDialogueAction(DialogueAction.ACKNOWLEDGE)).count()==0 && dialogueActions.stream().filter(s -> s.contains("Goodbye")).count() > 0){
				ret.removeIf(d -> d.asKristinaAgenda().isDialogueAction(DialogueAction.ACKNOWLEDGE));
				ret.add(onto.factory.getKristinaAgenda("agenda_SimpleSayGoodbye"));
			}
		}

		// update variables in opendial
		//Those are unnecessary in current openDial policy
		/*opendial.updateVariable("lastUserMove", userMove);
		if (lastMove != null) {
			opendial.updateVariable("lastUserTopic",
					lastMove.getTopics().stream().map(s -> s.split("#")[1])
							.collect(Collectors.toList()).toString());
		}*/
		
		
		return ret;
	}

	private static List<Agenda> selectSystemMove(List<Set<KristinaAgenda>> ws,
			KristinaAgenda ifEmpty) {

		LinkedList<Agenda> moves = new LinkedList<Agenda>();
		boolean end = false;
		int i = 0;
		for (Set<KristinaAgenda> set : ws) {
			int strategy = (int) (Math.random() * set.size());
			for (KristinaAgenda move : set) {
				if (strategy == 0) {
					if (move != null
							&& !move.getDialogueAction().equals(
									DialogueAction.EMPTY)
							&& !moves.contains(move)) {
						if (move.isDialogueAction(DialogueAction.ANSWER_THANK)) {
							moves.addFirst(move);
						} else if(i == 0 || !move.isDialogueAction(DialogueAction.ACKNOWLEDGE)){
							moves.add(move);
						}
						// DialogueHistory.add(move, Participant.SYSTEM);
						if (move.getDialogueAction().equals(
								DialogueAction.PERSONAL_GOODBYE)
								|| move.getDialogueAction().equals(
										DialogueAction.SIMPLE_GOODBYE)
								|| move.getDialogueAction().equals(
										DialogueAction.MEET_AGAIN)) {
							end = true;
						}
						break;
					}
				}
				strategy = strategy - 1;
			}
			if (end) {
				break;
			}
			i++;
		}
		if (moves.isEmpty()) {
			moves.add(ifEmpty);
		}
		return moves;
	}
}