package usersimulatorKristina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import usersimulatorKristina.control.Pair;
import usersimulatorKristina.control.SystemAction;
import usersimulatorKristina.control.UserAction;
import usersimulatorKristina.dialogueacts.DialogueAct;
import usersimulatorKristina.dialogueacts.DialogueType;
import usersimulatorKristina.dialogueacts.SayGoodbyeAct;

/**
 * 
 * Class for connecting with Kristina 
 * (not yet implemented)
 * 
 * Includes testCases
 * @author iFeustel
 *
 */

public class UserSimulationKristina {

	private static UserSimulator us;

	public static void main(String[] args) {
		/*
		 * System.setProperty("owlSpeak.settings.file",
		 * "./conf/OwlSpeak/settings.xml"); ServletEngine owlEngine = new
		 * ServletEngine(); String whereAmI =
		 * "http://peewit.e-technik.uni-ulm.de:8083/"; final String user =
		 * "user"; String id = KristinaServlet.reset(owlEngine, whereAmI, user);
		 * 
		 * 
		 * // get first system move KristinaDocument systemMove =
		 * (KristinaDocument) (KristinaServlet.processRequest(whereAmI, null,
		 * user, "0", owlEngine));
		 * 
		 */
		/*
		 * 
		 * System.out.println(systemMoveToAction("Request",null));
		 * List<DialogueAct> acts = new LinkedList<DialogueAct>(); DialogueAct
		 * act = DialogueAct.createDialogueAct(DialogueType.RequestWeather);
		 * act.setTopics(new
		 * HashSet<String>(Arrays.asList("Weather","Sun","Warm")));
		 * acts.add(act);
		 * acts.add(DialogueAct.createDialogueAct(DialogueType.Request));
		 * acts.add(DialogueAct.createDialogueAct(DialogueType.Request));
		 * UserAction action = new UserAction(acts);
		 * System.out.println(userActionToMove(action));
		 */
		us = new UserSimulator();
		
		UserAction ua = us.startConversation();
		System.out.println("US: "+ua.getActs());
		//testCase1();
		testCase2();

	}

	//Two simulators talk to each other (Not working if user model does not contain system actions!)
	private void testCase1() {
		UserSimulator us2 = new UserSimulator();

		SystemAction sysAct = new SystemAction(DialogueAct.createDialogueAct(DialogueType.SimpleGreet));
		System.out.println("Simple Greet");
		UserAction uact = us.getUserMove(sysAct);
		System.out.println("us1 sagt: " + uact.getActs());
		UserAction u2Act = null;
		int count = 0;
		while (count <= 4) {
			u2Act = us2.getUserMove(new SystemAction(uact.getActs()));
			System.out.println("us2 sagt: " + u2Act.getActs());
			uact = us.getUserMove(new SystemAction(u2Act.getActs()));
			System.out.println("us1 sagt: " + uact.getActs());
			count++;
		}
	}

	
	/**
	 * TestCase for communicating with US
	 * 
	 * Example Input: 
	 * DialogueAction Topic Topic \n
	 * DialogueAction Topic\n
	 * 0
	 * 
	 * would be a System Action with 2 dialogueActions
	 * 
	 */
	private static void testCase2() {
		Scanner scan = new Scanner(System.in);
		boolean end = true;
		while(end){
			
			boolean loop = true;
			List<DialogueAct> sysActions = new ArrayList<>();
			while(loop){
				String input = scan.nextLine();
				if(input.equals("0")){
					loop = false;
					break;
				}
				String[] inputSplit = input.split(" ");
				String action = inputSplit[0];
				
				DialogueAct sysDiaAct = DialogueAct.createDialogueAct(DialogueType.valueOf(action));
				
				if(inputSplit.length >1){
					String[] topicLine = Arrays.copyOfRange(inputSplit, 1, inputSplit.length-1);
					Set<String> topics = new HashSet(Arrays.asList(topicLine));
					sysDiaAct.setTopics(topics);
				}
				
				sysActions.add(sysDiaAct);
			}
			
			SystemAction sysAct = new SystemAction(sysActions);
			UserAction ua = us.getUserMove(sysAct);
			System.out.println("US: "+ua.getActs());
			for(DialogueAct act : ua.getActs()){
				if(act instanceof SayGoodbyeAct){
					scan.close();
					end = false;
				}
			}
		}

	}
	
	//Converting a systemMove from Kristina to a US readable User Action
	private static SystemAction systemMoveToAction(String move, Set<String> topics) {
		try {
			DialogueType t = DialogueType.valueOf(move);
			DialogueAct a = DialogueAct.createDialogueAct(t);
			a.setTopics(topics);
			return new SystemAction(a);
		}
		// No DialogueType match
		catch (IllegalArgumentException e) {
			return null;
		}
	}

	//Converting a userAction from US to a Kristina readable User Move
	private static List<Pair<String, Set<String>>> userActionToMove(UserAction action) {
		List<Pair<String, Set<String>>> userMove = new LinkedList<Pair<String, Set<String>>>();
		List<DialogueAct> acts = action.getActs();
		for (DialogueAct act : acts) {
			userMove.add(new Pair<String, Set<String>>(act.getType().toString(), act.getTopics()));
		}
		return userMove;
	}

}
