package owlSpeak.engine.his.util;

import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.Partition;

public class PartitionSeedEncapsulation {
	
	Partition[] partitions = null;
	OwlSpeakOntology onto;
	
	public PartitionSeedEncapsulation(Partition p) {
		partitions = new Partition[1];
		partitions[0] = p;
	}
	
	public PartitionSeedEncapsulation(OwlSpeakOntology onto) {
		this.onto = onto;
	}
	
	public Partition[] partitionSeed() {
		if (partitions != null)
			return partitions;
		else
			return Partition.Seed(onto);
	}
}
