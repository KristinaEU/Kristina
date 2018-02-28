package usersimulatorCamResInfoSys;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

public class PushActionCombination extends Vector<DialogueAct> {
	private static final long serialVersionUID = 8124722619357503917L;
	
	protected double prob = 1.0;
	public Map<SummaryConditionType, SummaryPushActType> mapping;

	

	public PushActionCombination(Vector<DialogueAct> vec) {
		this.addAll(vec);
		this.mapping = new TreeMap<SummaryConditionType, SummaryPushActType>();
		
	}

	public PushActionCombination() {
		// TODO Auto-generated constructor stub
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}
	
	
	public double calculateProb(ArrayList<PushAction> list) {
		for (PushAction p : list) {
			prob *= p.getProbability();
		}
		return prob;
	}
	
	public void setMapping(ArrayList<PushAction> list){
		for(PushAction p: list)
			this.mapping.putAll(p.mapping);
		
	}
	
	 @Override  
	  public int hashCode() 
	  {  
	    return this.toString().hashCode();  
	  }  
	 @Override
	 public boolean equals(Object other) 
	 {
	   if(other instanceof PushActionCombination){
		  
		return true;
	   }
	   else
	     return false;
	 }
}
