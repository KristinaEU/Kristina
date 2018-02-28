package usersimulatorKristina.dialogueacts;

import java.util.HashSet;
import java.util.Set;

public class DeclareAct extends DialogueAct {

	private Set<String> topics;

	public DeclareAct(DialogueType t) {
		super(t);
		topics = new HashSet<String>();
		// TODO Auto-generated constructor stub
	}

	public void setTopics(Set<String> top) {
		topics = top;
	}

	public void addTopics(Set<String> top) {
		topics.addAll(top);
	}
	
	public Set<String> getTopics() {
		return topics;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Declare: " + topics.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DialogueAct) {
			DialogueAct act = (DialogueAct) obj;
			if (this.getType().equals(act.getType()) && this.topics.equals(act.getTopics())) {
				return true;
			}
		}
		return false;
	}
}
