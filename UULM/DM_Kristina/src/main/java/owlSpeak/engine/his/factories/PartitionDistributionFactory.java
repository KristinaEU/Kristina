package owlSpeak.engine.his.factories;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

import owlSpeak.engine.his.interfaces.IPartitionDistribution;


public class PartitionDistributionFactory {
	private PyObject partitionDistributionClass;

    /**
     * Create a new PythonInterpreter object, then use it to
     * execute some python code. In this case, we want to
     * import the python module that we will coerce.
     *
     * Once the module is imported than we obtain a reference to
     * it and assign the reference to a Java variable
     */

    public PartitionDistributionFactory() {
        PythonInterpreter interpreter = new PythonInterpreter();
//        interpreter.exec("from asdt.PartitionDistribution import PartitionDistribution"); TODO change to asdt package
        interpreter.exec("from PartitionDistribution import PartitionDistribution");
        partitionDistributionClass = interpreter.get("PartitionDistribution");
    }

    /**
     * The create method is responsible for performing the actual
     * coercion of the referenced python module into Java bytecode
     */
    public IPartitionDistribution create (Object partitionSeedObject, Object historySeedObject, boolean loading) {
        PyObject buildingObject = partitionDistributionClass.__call__();
        IPartitionDistribution dist = (IPartitionDistribution)buildingObject.__tojava__(IPartitionDistribution.class);
        if (historySeedObject != null)
        	dist.initialize(partitionSeedObject, loading, historySeedObject);
        else
        	dist.initialize(partitionSeedObject, loading);
        return dist;
    }
    
    /**
     * The create method is responsible for performing the actual
     * coercion of the referenced python module into Java bytecode
     */
    public IPartitionDistribution create (Object partitionSeedObject, Object historySeedObject) {
        return create(partitionSeedObject, historySeedObject, false);
    }
}
