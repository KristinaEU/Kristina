package owlSpeak.engine.his.interfaces;



public interface IPartition {
	/**
	 * Returns the likelihood of the userAction, given the current partition
	 * (self), the dialog history, and the sysAction. PartitionDistribution
	 * REQUIRES that this method exists.
	 * 
	 * @param iUserAction UserAction object containing userAction
	 * @param iHistory History object
	 * @param iSysAction latest system action
	 * @return user action likelihood
	 */
	public double UserActionLikelihood(IUserAction iUserAction, IHistory iHistory,
			ISystemAction iSysAction);

	/**
	 * Splits the current partition (self) into one or more partitions given the
	 * userAction. If the split is successful, this partition is updated, and a
	 * Vector of split-off partitions is returned. If the split is not successful,
	 * returns an empty Vector. PartitionDistribution REQUIRES that this method
	 * exists.
	 * 
	 * @param iUserAction UserAction object containing userAction
	 * @return Vector of split-off partitions or empty Vector
	 */
	public IPartition[] Split(IUserAction iUserAction);

	/**
	 * Attempts to recombine child (partition) with the current partition
	 * (self). If recombination is successful, returns True; else False. It is
	 * guaranteed that child will have been split off from self in the past.
	 * PartitionDistribution REQUIRES that this method exists.
	 * 
	 * @param iChild child partition which should be merged with this partition.
	 * @return true if merge is successful, else false
	 */
	public boolean Recombine(IPartition iChild);
	
	public void setBelief(float _belief);
	
	public float getBelief();
	
	public void addChild(IPartition _iChild);
	
	public void removeChild(IPartition _iChild);
	
	public IPartition[] getChildren();
	    
	public void setParent(IPartition _iParent);
	
	public String __str__();
}
