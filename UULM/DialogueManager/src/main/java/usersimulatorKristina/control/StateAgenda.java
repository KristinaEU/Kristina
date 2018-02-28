package usersimulatorKristina.control;

import java.util.List;
import java.util.Vector;

/**
 * 
 * Agenda representing a state
 * Based on a tree structure with parents and children
 * 
 * @author iFeustel
 *
 */
public class StateAgenda {
	private Vector<StateAgenda> parents, children;
	private Agenda agenda;
	
	public StateAgenda() {
		setCurrAgenda(new Agenda());
		setParents(new Vector<StateAgenda>());
		setChildren(new Vector<StateAgenda>());
	}
	
	public StateAgenda(Agenda a){
		this();
		this.setCurrAgenda(a);
	}

	public Agenda getAgenda() {
		return agenda;
	}
	
	public void addChild(StateAgenda child){
		children.add(child);
	}
	
	public void addParent(StateAgenda par){
		parents.add(par);
	}

	public void setCurrAgenda(Agenda currAgenda) {
		currAgenda.updateAgenda();
		this.agenda = currAgenda;
		
	}

	public Vector<StateAgenda> getChildren() {
		return children;
	}

	public void setChildren(Vector<StateAgenda> children) {
		this.children = children;
	}
	
	public void removeChild(StateAgenda child){
		children.remove(child);
	}

	public Vector<StateAgenda> getParents() {
		return parents;
	}
	
	public void removeParent(StateAgenda parent){
		parents.remove(parent);
	}

	public void setParents(Vector<StateAgenda> parents) {
		this.parents = parents;
	}
	
	/**
	 * look for duplicates and delete them
	 * repair parents/children connection
	 * @param agendas
	 * @return
	 */
	public static List<StateAgenda> mergeStateAgendas(List<StateAgenda> agendas) {
		Vector<StateAgenda> merged = new Vector<StateAgenda>();
		for (StateAgenda a : agendas) {
			if (!merged.contains(a)) {
				merged.add(a);
			} else{
				StateAgenda sa = merged.get(merged.indexOf(a));
				for(StateAgenda par : a.getParents()){
					par.removeChild(a);
					if(!(sa.getParents().contains(par))){
						sa.addParent(par);
						par.addChild(sa);
					}
				}
				for(StateAgenda child : a.getChildren()){
					child.removeParent(a);
					if(!(sa.getChildren().contains(child))){
						child.addParent(sa);
						sa.addChild(child);
					}
				}
				
				
				
			}
		}
	
		return merged;
	}
	
	/*
	 * StateAgenda are equal when their Agendas are the same
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StateAgenda) {
			StateAgenda sa = (StateAgenda) obj;
			Agenda a = sa.getAgenda();
			if (this.agenda.equals(a)){
				return true;
			}
		}
		return false;
	}

}
