package owlSpeak.engine;




import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import owlSpeak.Semantic;
/**
 * The class that represents a POMDP Slot.
 * The user goal is divided into slots, whereby each slot represents a single POMDP.
 * Each slot belongs to a distinct dialogue part such as destination city or travel duration
 * and may take different values related to this part.
 * It is characterized by user goal values, user action values, user goal probabilities, user action probabilities and belief states. 
 * @author Savina Koleva.
 */
public class Slot {
	/**
	 * A vector of Semantics representing the user goal values.
	 */
	public Vector <Semantic> user_goal_values = new Vector<Semantic>();
	
	/**
	 * A vector of Semantics representing the user action values.
	 */
	public Vector <Semantic>  user_actions_values = new Vector<Semantic>() ;

	/**
	 * A double array representing the user goal probabilities.
	 */
	double [][] user_goal_prob;
	
	/**
	 * A double array representing the user action probabilities.
	 */
	double [][] user_action_prob;
	
	/**
	 * An array representing the belief states.
	 */
	double [] belief;
	
	/**
	 * Creates a Slot object by calling super.
	 * It contains parameters of a Slot: user goal values, user action values, user goal probability, user action probability and the belief states.
	 * @param insCM the vector of CoreMoves
	 */
	public Slot(Vector<CoreMove> insCM){
						
		Iterator<CoreMove> itCM = insCM.iterator();
		Iterator<Semantic> itS = null;
		
		CoreMove act = null;
		
		Collection<Semantic> actS = null;
		Semantic actSc = null;
		
		
		while(itCM.hasNext()){
			act = itCM.next();
			
			actS = act.move.getSemantic();
			
			itS = actS.iterator();
			
			while(itS.hasNext()){
				actSc = itS.next();
				user_goal_values.addElement(actSc);
				user_actions_values.addElement(actSc);
				
			}
			
		}
		
		user_actions_values.addElement(null);
		
		ProbModel newSlot = new ProbModel();
		user_goal_prob = newSlot.calcUserGoalProb(user_goal_values, user_goal_values) ;
		user_action_prob = newSlot.calcUserActionProb(user_goal_values, user_actions_values);
		belief = newSlot.calcBelief(user_goal_values);
	}
	
	 /**
     * Returns true if each object of the Semantic list linked to the CoreMove vector is equal to to an object in the Semantic list stored as user goal values of a Slot.
     * @param gram the vector of CoreMoves
     * @return true if Semantic list of a Slot was found.
     */
	public boolean compareToGrammar(Vector<CoreMove> gram){
		
		Iterator<CoreMove> itCM = gram.iterator();
		Vector<Semantic> tempVectS = new Vector<Semantic>();
		
		while(itCM.hasNext()){
			CoreMove act = itCM.next();
			Collection<Semantic> actS = act.move.getSemantic();
			Iterator<Semantic> itS = actS.iterator();
			
			while(itS.hasNext()){
				Semantic actSc = itS.next();
				tempVectS.addElement(actSc);
			}
		}
		
		Iterator<Semantic> thisIt = user_goal_values.iterator();
		
		boolean found = true;
		while(thisIt.hasNext()){
			Semantic tempS = thisIt.next();
			if(!tempS.isMemberOf(tempVectS)){
				found = false;
				break;
			}
		}
		return found;
	}
	
	
	/**
	 * The main function for belief update of a Slot. 
	 * The new belief states are calculated from the old belief states, the user action probabilities, 
	 * the user goal probabilities, the observation function and a normalization factor.
	 * @param all the Transition object containing all required for the belief update probabilities
	 * @return a double array of updated belief states values
	 */
public double[] beliefUpdateSlot(ProbModel all){
		
		double[] ret = new double[this.user_goal_values.size()];
		double[] zwsch = new double[this.user_actions_values.size()*this.user_goal_values.size()];
		double norm;
		double norm_var = 0;
	
		 int count = 0;
		 for(int c1 = 0; c1 < this.user_goal_values.size(); c1++){
			 for(int c2 = 0; c2 < this.user_actions_values.size(); c2++){
				 for(int c3 = 0; c3 < this.user_goal_values.size(); c3++){
					 double obs1 = all.observation_prob[c2];
					 double act1 = this.user_action_prob[c1][c2];
					 double tra1 = this.user_goal_prob[c1][c3];
					 double bel1 =  this.belief[c3];
					 zwsch[count] = obs1 * act1 * tra1 * bel1;
					 count++;
				 } 
			 }
			 count = 0;
			 ret[c1] = 0;
			 for(int i = 0; i < zwsch.length; i++){
				 ret[c1] += zwsch[i];
			 }
		 }
		 for (int i=0; i < ret.length; i++){
			 norm_var = ret[i]+norm_var; 
		 }
		 norm = 1/norm_var;
		 
		 for (int i=0; i < ret.length; i++){
			 
			 ret[i]= norm * ret[i]; 
		 }
		return ret; 
	}
	  
	
}
	  
	
	  


	  
	  
	  
	  


		  
	  
	
	

