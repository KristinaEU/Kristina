package owlSpeak.engine;


import java.util.Vector;
import owlSpeak.Semantic;

/**
 * The class that maintains all the probabilities required for updating the belief states of a Slot.
 * @author Savina Koleva.
 */
public class ProbModel{
	
	/**
	 * A double array representing the user goal probabilities.
	 */
	double [][] user_goal_prob;
	
	/**
	 * A double array representing the user action probabilities.
	 */
	double [][] user_action_prob;
	
	/**
	 * An array representing the observation probabilities.
	 */
	double []observation_prob;
	
	/**
	 * An array representing the initial belief states.
	 */
	double [] init_belief;
	 
	/**
	 * A function for calculating the user goal probabilities. 
	 * They indicate how the user goal changes in the course of the dialogue.
	 * @param user_goal a vector of Semantics representing the current user goal values
	 * @param user_goal_new a vector of Semantics representing the next user goal values
	 * @return a double array of user goal probabilities
	 */
	  public double [][] calcUserGoalProb(Vector <Semantic> user_goal,Vector <Semantic> user_goal_new){
		  user_goal_prob = new double[user_goal_new.size()][user_goal.size()];
		  	
		  for(int i=0; i < user_goal_new.size(); i++)	{
		  	for(int j=0; j < user_goal.size(); j++)	{
		  			if (user_goal_new.get(i).equals(user_goal.get(j))){
		  				user_goal_prob [i][j] = 1.0;
		  			}
		  			else{
		  				user_goal_prob [i][j] = 0.0;
		  			}
				}
		  	} 
		  	return user_goal_prob;
	  }
	  
		/**
		 * A function for calculating the user action probabilities.
		 * They signify which actions the user is likely to take.
		 * @param user_goal a vector of Semantics representing the current user goal values
		 * @param user_actions a vector of Semantics representing the current user action values
		 * @return a double array of user action probabilities
		 */
	  public double [][] calcUserActionProb(Vector <Semantic> user_goal, Vector <Semantic> user_actions){
		  user_action_prob = new double[user_goal.size()][user_actions.size()];
		  
		  for(int i=0; i < user_actions.size(); i++){
		  	for(int j=0; j < user_goal.size(); j++){
		  			if 	(user_actions.get(i) == null){
		  				user_action_prob [j][i] = 0.1;
		  			}
		  			else if (user_actions.get(i).equals(user_goal.get(j))){
		  				user_action_prob [j][i] = 0.9;
		  			}
		  			else{
		  				user_action_prob [j][i] = 0;
		  			}
				}
		  	} 
		  	return user_action_prob;
	  }

	  
	/**
	 * A function for calculating the observation probabilities
	 * They rely on the stored as N-best list multiple recognition results and the corresponding confidence scores.
	 * @param observation a vector of Semantics recognized as a probable user inputs 
	 * @param user_action a vector of Semantics representing the current user action values
	 * @param Confidences a String array containing the confidence scores delivered by the recognizer
	 * @return an array of observation probabilities
	 */	  
	  public double [] calcObservationProb(Vector <Semantic> observation, Vector <Semantic> user_action, String[] Confidences){
		  
		  observation_prob = new double[user_action.size()];

		  Semantic obs = null;
		  Semantic ua = null;
		  
		  int counter = 0;
		  for(int t = 0; t < Confidences.length; t++){
			  if(Confidences[t] != null) counter++;
		  }		  
		  
		  double dConf[] = new double[counter];
		  for(int t = 0; t < dConf.length; t++){
			  dConf[t] = Double.parseDouble(Confidences[t]);
		  }
		  
		  boolean unchanged[] = new boolean[user_action.size()];
		  for(int t = 0; t < observation_prob.length; t++){
			  unchanged[t] = false;
		  }
		  
		  boolean found = false;
		  for(int i=0; i < user_action.size(); i++){
			  if(user_action.get(i) == null){
	  				observation_prob [i] = 1.0/(user_action.size()-1);
	  				continue;
	  			}
			  	found = false;
		  		for(int j=0; j < observation.size(); j++){
		  			obs = observation.get(j);
		  			ua = user_action.get(i);		  			
		  			if (ua.indi.equals(obs.indi)){
		  				observation_prob[i] = dConf[j];
		  				found = true;
		  			}
		  		}
		  		if(!found){
		  			observation_prob[i] = 0;
		  		}
		  }
		  return observation_prob;
	  }
	  
	/**
	 * A function for calculating the initial belief states
	 * Initially the belief states are defined as 1/|the length of the user goal values|.
	 * @param user_goal a vector of Semantics representing the current user goal values
	 * @return an array of the initial belief states
	 */
	  public double [] calcBelief(Vector <Semantic> user_goal){
		  init_belief = new double[user_goal.size()];
		  for(int i=0; i <user_goal.size(); i++)	{
			  init_belief[i]  = 1.0/user_goal.size();
		  }
	   return init_belief;
	  }		
}	  
	
	  


	  
	  
	  
	  


		  
	  
	
	

