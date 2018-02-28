package owlSpeak.kristina.evaluation;

import java.util.LinkedList;

public class Dialogue {
	private String user;
	private String scenario;
	private LinkedList<Turn> turns;
	private String name;
	
	public Dialogue(String name, String user, String scenario, LinkedList<Turn> turns) {
		this.user = user;
		this.scenario = scenario;
		this.turns = turns;
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public String getScenario() {
		return scenario;
	}

	public LinkedList<Turn> getTurns() {
		return turns;
	}
	
	
	public String toString(){
		return name;
	}
}
