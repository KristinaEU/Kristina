package owlSpeak.engine.his.interfaces;

/**
 * Interface for Python implementation of PartitionDistribution
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 *
 */
public interface IPartitionDistribution {
	public void Init();
	public void initialize(Object partitionSeedObject, boolean loading, Object historySeedObject);
	public void initialize(Object partitionSeedObject, boolean loading);
	public void initialize(Object partitionSeedObject);
	public void Compact(int maxPartitions);
	public void Update(INBestList asrResult, Object sysAction);
	public void Update(INBestList asrResult);
	public IPartition getTopPartition();
	public IPartition getSecondTopPartition();
	public IHistory getTopPartitionsHistory();
	public String __str__();
	public String toString();
}
