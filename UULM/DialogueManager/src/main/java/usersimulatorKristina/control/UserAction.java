package usersimulatorKristina.control;

import java.util.LinkedList;
import java.util.List;

import usersimulatorKristina.dialogueacts.DialogueAct;

/**
 * User Action coming from the UserSimulator containing at least one DialogueAction
 * @author ifeustel
 *
 */

public class UserAction {

private List<DialogueAct> acts;
	
	public UserAction(){
		setActs(new LinkedList<DialogueAct>());
	}
	
	public UserAction(DialogueAct act){
		this();
		acts.add(act);
	}
	
	public UserAction(List<DialogueAct> acts){
		this.setActs(acts);
	}

	public List<DialogueAct> getActs() {
		return acts;
	}

	public void setActs(List<DialogueAct> acts) {
		this.acts = acts;
	}
	
	public void addAct(DialogueAct act){
		acts.add(act);
	}
	
}
