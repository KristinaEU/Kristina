package usersimulatorKristina.control;

import java.util.LinkedList;
import java.util.List;

import usersimulatorKristina.dialogueacts.DialogueAct;

/**
 * System Action coming from system containing at least one DialogueAction
 * @author ifeustel
 *
 */

public class SystemAction {

	private List<DialogueAct> acts;
	
	public SystemAction(){
		setActs(new LinkedList<DialogueAct>());
	}
	
	public SystemAction(DialogueAct a){
		setActs(new LinkedList<DialogueAct>());
		acts.add(a);
	}
	
	public SystemAction(List<DialogueAct> acts){
		this.setActs(acts);
	}

	public List<DialogueAct> getActs() {
		return acts;
	}

	public void setActs(List<DialogueAct> acts) {
		this.acts = acts;
	}
	
	@Override
	public String toString() {
		String output = "";
		for(DialogueAct act : acts){
			output += "["+act.getType()+","+act.getTopics()+"],";
		}
		return output;
	}
	
	
}
