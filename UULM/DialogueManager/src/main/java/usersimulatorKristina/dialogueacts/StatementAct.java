package usersimulatorKristina.dialogueacts;

import java.util.Set;

public class StatementAct extends DialogueAct{
	
	private Set<String> topics;

	public StatementAct(DialogueType t) {
		super(t);
	}

	@Override
	public void setTopics(Set<String> top) {
		topics = top;
		
	}

	@Override
	public Set<String> getTopics() {
		// TODO Auto-generated method stub
		return topics;
	}

	
	
}
