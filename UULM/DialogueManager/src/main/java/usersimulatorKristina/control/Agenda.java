package usersimulatorKristina.control;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import usersimulatorKristina.dialogueacts.AcceptAct;
import usersimulatorKristina.dialogueacts.AcknowledgeAct;
import usersimulatorKristina.dialogueacts.DialogueAct;
import usersimulatorKristina.dialogueacts.DialogueType;
import usersimulatorKristina.dialogueacts.GreetAct;
import usersimulatorKristina.dialogueacts.RejectAct;
import usersimulatorKristina.dialogueacts.SayGoodbyeAct;
import usersimulatorKristina.goal.Constraint;
import usersimulatorKristina.goal.Request;
import usersimulatorKristina.goal.UserGoal;

public class Agenda {

	private LinkedList<DialogueAct> dialogueActs;
	private double transprobability;
	
	DialogueType[] greetTypes = {DialogueType.AfternoonGreet, DialogueType.SimpleGreet, DialogueType.PersonalGreet, DialogueType.MorningGreet, DialogueType.EveningGreet};
	DialogueType[] byeTypes = {DialogueType.MorningSayGoodbye, DialogueType.MeetAgainSayGoodbye, DialogueType.AfternoonSayGoodbye, DialogueType.EveningSayGoodbye, DialogueType.WeekendSayGoodbye};

	public Agenda() {
		setDialogueActs(new LinkedList<DialogueAct>());
	}

	// initialize Agenda with User Goal
	public Agenda(UserGoal g) {
		this();
		fillAgendaWithGoal(g);

		//Generate random numbers for picking random greet/bye act
		int randGreet = (int) (Math.random()*(greetTypes.length-1));
		int randBye = (int) (Math.random()*(byeTypes.length-1));
		
		// ByeAct as last item for stack
		setByeAct(DialogueAct.createDialogueAct(byeTypes[randBye]));
		// GreetAct as first item on stack
		setGreetAct(DialogueAct.createDialogueAct(greetTypes[randGreet]));
	}

	//initialize Agenda with an agenda-list
	public Agenda(List<DialogueAct> acts) {
		this();
		getDialogueActs().addAll(acts);
	}

	/*
	 * Fill the Agenda with User Goal constraints and requests:
	 * Constraint -> Declare
	 * Requests -> Request 
	 * 
	 */
	private void fillAgendaWithGoal(UserGoal g) {
		List<Constraint> constraints = g.getConstraints();
		List<Request> requests = g.getRequests();

		for (Constraint c : constraints) {
			DialogueAct temp = DialogueAct.createDeclareAct(c);
			//System.out.println(temp.getType() + ": " + temp.getTopics());
			getDialogueActs().add(temp);
		}
		for (Request r : requests) {
			DialogueAct temp = DialogueAct.createRequestAct(r);
			//System.out.println(temp.getType() + ": " + temp.getTopics());
			r.putCount();
			getDialogueActs().add(temp);
		}

	}

	/*
	 * Add a byeAct on the bottom of the stack
	 */
	private void setByeAct(DialogueAct byeAct) {
		if (!(byeAct instanceof SayGoodbyeAct)) {
			System.err.println(byeAct + " is no instance of SayGoodbyeAct");
		} else if (getDialogueActs().getFirst() instanceof SayGoodbyeAct) {
			System.err.println("ByeAct already on agenda");
		} else {
			getDialogueActs().addFirst(byeAct);
		}
	}

	/*
	 * Pops the byeAct of the agenda
	 */
	private DialogueAct popByeAct() {
		if (getDialogueActs().getFirst() instanceof SayGoodbyeAct) {
			DialogueAct bye = getDialogueActs().removeFirst();
			return bye;
		}
		return null;

	}
	
	/*
	 * Pops the greetAct of the Agenda
	 */
	private DialogueAct popGreetAct(){
		if(getDialogueActs().getLast() instanceof GreetAct){
			return getDialogueActs().removeLast();
		}
		return null;
	}

	/*
	 * Add a greetAct on top of the stack
	 */
	private void setGreetAct(DialogueAct greetAct) {
		if (!(greetAct instanceof GreetAct)) {
			System.err.println(greetAct + " is no instance of GreetAct");
		} else if (getDialogueActs().getLast() instanceof GreetAct) {
			System.err.println("GreetAct already on agenda");
		} else{
			getDialogueActs().add(greetAct);
		}
	}

	//refresh Agenda in removing duplicates
	public void updateAgenda() {
		clearDuplicates();
	}

	//Push one Act on top of the agenda
	public void pushAct(DialogueAct act) {
		getDialogueActs().add(act);
		updateAgenda();
	}

	//push several acts on top of the agenda
	public void pushActs(List<DialogueAct> acts) {
		getDialogueActs().addAll(acts);
		updateAgenda();
	}

	// pop one act
	public DialogueAct popAct() {
		return getDialogueActs().removeLast();
	}

	// pop n acts
	public LinkedList<DialogueAct> popActs(int n) {
		LinkedList<DialogueAct> acts = new LinkedList<DialogueAct>();
		for (int i = 0; i < n; i++) {
			acts.add(getDialogueActs().removeLast());
		}
		return acts;
	}

	/**
	 * Looks for the specific DialogueAct with DialogueActType type on the agenda
	 * (From top to bottem (stack view)) 
	 * @param type
	 * @return
	 */
	public DialogueAct popNextSpecAct(DialogueType type){
		for(int i = getDialogueActs().size()-1; i >= 0; i--){
			DialogueAct currItem = getDialogueActs().get(i);
			if(currItem.getType().equals(type)){
				getDialogueActs().remove(currItem);
				return currItem;
			}
		}
		return null;
	}
	
	/*
	 * returns all possible permutations of agenda a
	 */
	public static List<Agenda> getPermutatedAgendas(Agenda a) {
		List<Agenda> agendas = new ArrayList<Agenda>();

		//pop bye/greet act because they have fix positions
		DialogueAct byeAct = a.popByeAct();
		DialogueAct greetAct = a.popGreetAct();

		permuteAgenda(a.getDialogueActs(), 0, agendas);

		//Push Bye/GreetAct again on Agenda
		a.setByeAct(byeAct);
		a.setGreetAct(greetAct);
		for (Agenda agenda : agendas) {
			agenda.setByeAct(byeAct);
			agenda.setGreetAct(greetAct);
		}

		return agendas;

	}

	/*
	 * Create all combinations of possible sequences
	 * 
	 */
	private static void permuteAgenda(List<DialogueAct> arr, int pos, List<Agenda> agendas) {
		if (arr.size() - pos == 1) {
			List<DialogueAct> age = new LinkedList<DialogueAct>(arr);
			agendas.add(new Agenda(age));
		} else
			for (int i = pos; i < arr.size(); i++) {
				java.util.Collections.swap(arr, pos, i);
				permuteAgenda(arr, pos + 1, agendas);
				java.util.Collections.swap(arr, pos, i);
			}
	}
	
	/*
	 *  set probability of agenda
	 *  currently: 1/size 
	 */
	public static void setProbability(List<Agenda> agendas){
		for(Agenda a : agendas){
			a.setTransprobability(1/(double) agendas.size());
		}
	}

	//Clear Duplicated Dialogue Acts
	private void clearDuplicates() {
		LinkedList<DialogueAct> noDuplicates = new LinkedList<DialogueAct>();
		for (DialogueAct d : getDialogueActs()) {
			boolean isDeepConfirmAct = (d instanceof AcceptAct || d instanceof AcknowledgeAct || d instanceof RejectAct) && (getDialogueActs().indexOf(d) < getDialogueActs().size()-2);
			if (!noDuplicates.contains(d) && !isDeepConfirmAct) {
				noDuplicates.add(d);
			}
			
		}
		this.setDialogueActs(noDuplicates);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Agenda) {
			Agenda a = (Agenda) obj;
			if (this.getDialogueActs().equals(a.getDialogueActs())) {
				return true;
			}
		}
		return false;

	}

	// Look for duplicate agendas and merge them
	public List<Agenda> mergeAgendas(List<Agenda> agendas) {
		List<Agenda> merged = new LinkedList<Agenda>();
		for (Agenda a : agendas) {
			if (!merged.contains(a)) {
				merged.add(a);
			}
		}
		return merged;
	}

	public double getTransprobability() {
		return transprobability;
	}

	public void setTransprobability(double transprobability) {
		this.transprobability = transprobability;
	}

	public LinkedList<DialogueAct> getDialogueActs() {
		return dialogueActs;
	}

	public void setDialogueActs(LinkedList<DialogueAct> dialogueActs) {
		this.dialogueActs = dialogueActs;
	}

	public boolean isEmpty(){
		return dialogueActs.isEmpty();
	}
}
