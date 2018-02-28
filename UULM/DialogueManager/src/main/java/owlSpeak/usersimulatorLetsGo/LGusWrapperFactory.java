package owlSpeak.usersimulatorLetsGo;

import org.python.core.PyObject;
import org.python.util.PythonInterpreter;

public class LGusWrapperFactory {
	private PyObject LGusWrapperClass;

    /**
     * Create a new PythonInterpreter object, then use it to
     * execute some python code. In this case, we want to
     * import the python module that we will coerce.
     *
     * Once the module is imported than we obtain a reference to
     * it and assign the reference to a Java variable
     */

    public LGusWrapperFactory() {
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.exec("from LGus.OwlSpeakWrapper import UsrSim");
        LGusWrapperClass = interpreter.get("UsrSim");
    }

    /**
     * The create method is responsible for performing the actual
     * coercion of the referenced python module into Java bytecode
     */
    public ILGusWrapper create () {
        PyObject buildingObject = LGusWrapperClass.__call__();
        ILGusWrapper lgus = (ILGusWrapper)buildingObject.__tojava__(ILGusWrapper.class);
        return lgus;
    }
    
    public static void main(String[] argv) {
//    	Properties props = new Properties();
//    	String path = props.getProperty("python.path");
//    	props.setProperty("python.path", path + "/home/modules:scripts");
//    	PythonInterpreter.initialize(System.getProperties(), props,
//    	                             new String[] {""});

    	
    	
    	LGusWrapperFactory test = new LGusWrapperFactory();
    	ILGusWrapper test2 = test.create();
    	String jsonCommand1 = "{\"Command\":\"Start over\"}";
    	String jsonCommand2 = "{\"System action\":\"Request(Open)\"}";
    	String jsonCommand3 = "{\"Command\":\"Get user goal\"}";
    	System.out.println(jsonCommand1);
    	System.out.println(test2.get_usr_act(jsonCommand1));
    	System.out.println(jsonCommand2);
    	System.out.println(test2.get_usr_act(jsonCommand2));
    }
}
