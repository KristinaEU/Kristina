package owlSpeak.engine;
import java.util.Vector;

/**
 * The class that represents all POMDP Slots.
 * Contain all SLot that have been created.
 * It is used to prove if a Slot already exist. 
 * @author Savina Koleva.
 */
public class MasterSlot {
	
	/**
	 * The Vector containing all Slots.
	 */
	Vector <Slot> Slots;

	/**
	 * Creates an empty MasterSlot object by calling super.
	 */ 
	public MasterSlot(){
		Slots = new Vector<Slot>();
	}

	
	/**
	 * Creates an MasterSlot object by calling super which contains all existing Slots.
	 * @param in the Vector of Slot objects
	 */ 
	public MasterSlot(Vector <Slot> in){
		Slots = new Vector<Slot>();
		for(int t = 0; t < in.size(); ++t){
			Slots.add(in.get(t));
		}
	}
    /**
     * Adds a Slot to the Slot Vector.
     * @param in the Slot that should be added.
     */
	public void addSlot(Slot in){
		Slots.add(in);
	}
    /**
     * Return the size of the Slot Vector.
     */
	public int count(){
		return Slots.size();
	}
	
    /**
     * Transforms a Slot Vector to a Slot array.
     */
	public Slot[] getAll(){
		Object[] t = Slots.toArray();
		Slot[] temp = new Slot[t.length];
		for(int i = 0; i < temp.length; i++){
			temp[i] = (Slot)t[i];
		}
		return temp;
	}
	
    /**
     * Return a Slot object from the Slot array.
     * @param index the Slot index in the Slot array
     * @return the Slot by a given index
     */
	public Slot get(int index){
		Object[] t = Slots.toArray();
		Slot temp = (Slot)t[index];
		return temp;
	}
	
    /**
     * Return true if a Slot is part of an array.
     * @param check the Slot that should be compared
     * @return true is a Slot is found in the Slot array
     */
	public boolean isExistent(Slot check){
		boolean retval = true;
		
		for(int index_slots = 0; index_slots < this.count(); index_slots++){
			Slot temp = this.get(index_slots);
			if(check.user_goal_values.size() != temp.user_goal_values.size()){
				continue;
			}
			
			for(int index_ug1 = 0; index_ug1 < check.user_goal_values.size(); index_ug1++){
				boolean found = false;
				for(int index_ug2 = 0; index_ug2 < check.user_goal_values.size(); index_ug2++){
					if(check.user_goal_values.get(index_ug1).equals(temp.user_goal_values.get(index_ug2))){
						found = true;
						break;
					}
				}
				if(!found){
					retval = false;
					break;
				}
			}	
		}
		return retval;	
	}
	
}
	  
	
	  


	  
	  
	  
	  


		  
	  
	
	

