package usersimulatorCamResInfoSys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import usersimulatorCamResInfoSys.dialogueacts.ByeDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ConfirmDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.InformDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.OfferDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RequestDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.WelcomeMSGDialogueAct;

public class UserSimulator {

	private  UserGoal goal;
	private  Vector<Agenda> state;
	private static List<String> matches = new ArrayList<String>();

	private String filePathTransitionModel = "\\models\\pushTransitionModel.json";
	private String filePathUserActionModel = "\\models\\TrainingNfull.json";
	private JSONObject pushModel;
	private JSONObject popModel;
	
	public UserSimulator (String modelHomePath) {
		filePathTransitionModel = modelHomePath + filePathTransitionModel;
		filePathUserActionModel = modelHomePath + filePathUserActionModel;
		this.goal = new UserGoal();
		this.state = this.goal.createAgendaVec();
		Agenda.calcProb(this.state);
	}
	
	public UserGoal getGoal(){
		return this.goal;
	}
	
	private  Vector<Agenda> performPushAction(SystemAction sysAct, UserGoal goal,
			JSONObject object, Agenda a){
		Vector<List<SummaryPushAction>> sumPushVec = new Vector<List<SummaryPushAction>>();

		Vector<SummaryCondition> sumCondVec = SummaryCondition
				.createSumCondVec(sysAct, goal);

		Vector<List<PushAction>> pushActList = new Vector<List<PushAction>>();

		Vector<Agenda> newState = new Vector<Agenda>();

		for (SummaryCondition sc : sumCondVec) {
			 
			sumPushVec.add(SummaryPushAction.createSumPushList(
					sc, goal, object));
		}

		for (List<SummaryPushAction> l : sumPushVec) {
			
			pushActList.add(PushAction.createPushActionList(l));
		}

		LinkedHashSet<PushActionCombination> comb = PushAction
				.generateCombination(pushActList);

		newState.addAll(a.pushActs(comb));
		
		return newState;
	}
	
	public void resetUS(){
		this.goal = new UserGoal();
		this.state = this.goal.createAgendaVec();
		Agenda.calcProb(this.state);
		
	}

	/**
	 * 
	 * 
	 * @param sysAct
	 * @return
	 */
	public  UserAction fromSDS(SystemAction sysAct) {
		Vector<Agenda> newState = new Vector<Agenda>();
		
		try {
			// read the json file
			FileReader reader = new FileReader(filePathTransitionModel);
			JSONParser jsonParser = new JSONParser();
			pushModel = (JSONObject) jsonParser.parse(reader);

			FileReader reader1 = new FileReader(filePathUserActionModel);
			JSONParser jsonParser1 = new JSONParser();
			popModel = (JSONObject) jsonParser1.parse(reader1);

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Agenda a: state){
			newState.addAll(performPushAction(sysAct, goal, pushModel, a));
		}
		state = newState;
		goal.updateConstraints(sysAct);
		goal.updateRequests(sysAct);
		
		UserAction userAct = UserAction.extractUserAction(state, popModel);
		if (userAct == null) {
			userAct = new UserAction(new ByeDialogueAct(DialogueType.Bye));
		}
		else {
			state = Agenda.performPopActs(state, userAct, popModel);
		}
		System.out.println("System Action: " + sysAct.toString());
		System.out.println("User Action: " + userAct.toString());
		return userAct;
	}

	public void listDir(File dir) {
		listDir(dir, "");
	}

	public static List<String> listDir(File dir, String prefix) {

		File[] files = dir.listFiles();

		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				matches.add(files[i].getAbsolutePath());

				if (files[i].isDirectory()) {

					listDir(files[i], prefix + "  ");
				}

			}
		}
		return matches;
	}

	public  static List<String> getJSONLabel(List<String> list) {
		List<String> labelList = new ArrayList<String>();
		for (String s : list) {
			if (s.endsWith("label.json"))
				labelList.add(s);
		}
		return labelList;
	}

	public static  List<String> getJSONLog(List<String> list) {
		List<String> logList = new ArrayList<String>();
		for (String s : list) {
			if (s.endsWith("log.json"))
				logList.add(s);
		}
		return logList;
	}

	public static void main(String[] args) {
		Vector<Constraint> cVec = new Vector<Constraint>();
		Vector<Request> rVec = new Vector<Request>();
		Constraint c = new Constraint("food", "taiwan");
		Constraint c2 = new Constraint("name", "aloha");
		Constraint c1 = new Constraint("area", "south");
		Request r = new Request("name", null);
		cVec.add(c);
		cVec.add(c2);
		cVec.add(c1);
		rVec.add(r);
		
		UserSimulator usersim = new UserSimulator("c:\\OwlSpeak");
		
		
		
	
	WelcomeMSGDialogueAct welActSys1 = new WelcomeMSGDialogueAct(DialogueType.Welcomemsg);
	SystemAction s1 = new SystemAction(welActSys1);
	
	
	ConfirmDialogueAct reqActSys2 = new ConfirmDialogueAct(DialogueType.Confirm);
	reqActSys2.addSlotValue("area", "south");
	SystemAction s2 = new SystemAction(reqActSys2);
	RequestDialogueAct reqAct = new RequestDialogueAct(DialogueType.Request);
	reqAct.setSlot("bla");
	SystemAction s3 = new SystemAction(reqAct);
	
	
	OfferDialogueAct offerActSys3  =new OfferDialogueAct(DialogueType.Offer);
	offerActSys3.addSlotValue("name", "Wong");
	InformDialogueAct infActSys3 = new InformDialogueAct(DialogueType.Inform);
	infActSys3.addSlotValue("name", "aloha");
	Vector<DialogueAct> list2 = new Vector<DialogueAct>();
	list2.add(infActSys3);
	list2.add(offerActSys3);
	SystemAction s4 = new SystemAction(list2);
	
	System.out.println("Turn1: ");
	usersim.fromSDS(s1);
	System.out.println("Turn2: ");
	usersim.fromSDS(s2);
	System.out.println("Turn3: ");
	usersim.fromSDS(s3);
	System.out.println("Turn4: ");
	usersim.fromSDS(s4);

		
		

	

	}

}
