package owlSpeak.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import fr.limsi.upnp.Action;
import fr.limsi.upnp.Argument;
import fr.limsi.upnp.DeviceServer;
import fr.limsi.upnp.Service;
import fr.limsi.upnp.StateVariable;
import fr.limsi.upnp.constants.UPnPStatusCodes;
import fr.limsi.upnp.device.ActionListener;
import fr.limsi.upnp.util.ArgMap;
import fr.limsi.upnp.util.UPnPStatus;

/**
 * The UPnP wrapper that is used to control the Spoken Dialogue Manager ()
 * @author Tobias Heinroth
 * 
 */
public class OwlSpeakYoupi {
	
	public static DeviceServer dev = new DeviceServer();
	
	public static final String user = "user";

	public static HashMap<String,String> dialogueNameMap = new HashMap<String, String>();
	public static HashMap<String,String> taskIDMap = new HashMap<String, String>();
	
	public static final String service = "urn:uulm:serviceId:sdm:1";
	
	/**
	 * Creates a UPnP device that wraps the Spoken Dialogue Manager.
	 * @param owlE the ServletEngine the DeviceServer should control
	 */
	public static DeviceServer create(ServletEngine owlE) {

		try {
			dev = new DeviceServer(Settings.youpiPath);

			Service devServ = dev.getRootDevice().getService(service);
			
			final ServletEngine owlEngfin = owlE;
			
			final StateVariable targetSV = devServ.getStateVariable("targetSV");
			final StateVariable taskIDSV = devServ.getStateVariable("taskIDSV");
			@SuppressWarnings("unused")
			final StateVariable lastUserWill = devServ.getStateVariable("lastUserWill");
			
			// resets all available ontologies
			String arguments0[] = {};
			StateVariable states0[] = {};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments0)), new Vector<StateVariable>(Arrays.asList(states0)),owlEngfin, "reloadSettings");
			
			// resets all available ontologies
			String arguments1[] = {};
			StateVariable states1[] = {};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments1)), new Vector<StateVariable>(Arrays.asList(states1)),owlEngfin, "resetAll");
			
			// resets a specific ontology
			String arguments2[] = {"dialogueName"};
			StateVariable states2[] = {targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments2)), new Vector<StateVariable>(Arrays.asList(states2)),owlEngfin, "reset");
		
			// enabels a specific ontology
			String arguments3[] = {"oioID", "dialogueName", "taskID"};
			StateVariable states3[] = {targetSV, targetSV, taskIDSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments3)), new Vector<StateVariable>(Arrays.asList(states3)),owlEngfin, "enableSDO");

			// disabels a specific ontology
			String arguments4[] = {"oioID", "taskID"};
			StateVariable states4[] = {targetSV, taskIDSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments4)), new Vector<StateVariable>(Arrays.asList(states4)),owlEngfin, "disableSDO");
			
			// sets the SDM's sleep mode on or off 
			String arguments5[] = {"sleepState"};
			StateVariable states5[] = {targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments5)), new Vector<StateVariable>(Arrays.asList(states5)),owlEngfin, "setSleep");
			
			// gets the SDM's sleep status 
			String arguments6[] = {"sleepState"};
			StateVariable states6[] = {targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments6)), new Vector<StateVariable>(Arrays.asList(states6)),owlEngfin, "getSleep");
			
			// sets a specific variable within a specific ontology
			String arguments7[] = {"taskID","varName","value"};
			StateVariable states7[] = {taskIDSV,targetSV,targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments7)), new Vector<StateVariable>(Arrays.asList(states7)),owlEngfin, "setVariable");
			
			// returns a specific variable within a specific ontology
			String arguments8[] = {"taskID","varName","value"};
			StateVariable states8[] = {taskIDSV,targetSV,targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments8)), new Vector<StateVariable>(Arrays.asList(states8)),owlEngfin, "getVariable");
			
			// sets the TTS mode to a specific value (1=en-gb-daniel, 2=en-gb-serena)
			String arguments9[] = {"ttsMode"};
			StateVariable states9[] = {targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments9)), new Vector<StateVariable>(Arrays.asList(states9)),owlEngfin, "setTtsMode");
			
			// sets the SDM's sleep mode on or off 
			String arguments10[] = {"asrMode"};
			StateVariable states10[] = {targetSV};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments10)), new Vector<StateVariable>(Arrays.asList(states10)),owlEngfin, "setAsrMode");
			
			// loads the spoken dialogue ontologies 
			String arguments11[] = {};
			StateVariable states11[] = {};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments11)), new Vector<StateVariable>(Arrays.asList(states11)),owlEngfin, "loadSDOs");

			// stops all available ontologies expect the system ontology
			String arguments12[] = {};
			StateVariable states12[] = {};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments12)), new Vector<StateVariable>(Arrays.asList(states12)),owlEngfin, "stopAllDialogue");
			
			// returns a list of all active task ID
			String arguments13[] = {"taskIDs"};
			StateVariable states13[] = {};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments13)), new Vector<StateVariable>(Arrays.asList(states13)),owlEngfin, "getActiveTaskIds");
			
			// returns a list of all active ontologies
			String arguments14[] = {"ontologies"};
			StateVariable states14[] = {};
			actionMaker(devServ, new Vector<String>(Arrays.asList(arguments14)), new Vector<StateVariable>(Arrays.asList(states14)),owlEngfin, "getActiveOntos");
			
			return dev;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Executes an action that is related with a specific UPnP command.
	 * @param devServ the DeviceServer that should execute the action
	 * @param args a Vector of arguements that are realted to the action
	 * @param sv a Vector of StateVariables that are part of the deviceServer
	 * @param se the ServletEngine that should perform the actions
	 * @param act the name of the action to be performed 
	 * 
	 */
	private static void actionMaker(Service devServ, final Vector<String> args, final Vector<StateVariable> sv, final ServletEngine se, final String act){
		dev.setImplementation(devServ.getAction(act), new ActionListener() {
			public UPnPStatus actionInvoked(Action action, ArgMap actionArgs) {
				Iterator<String> argsIt = args.iterator(); 
				while(argsIt.hasNext()){
					if (actionArgs.get(argsIt.next()) == null){
						return new UPnPStatus(UPnPStatusCodes.INVALID_ARGS, "Missing arg: " + argsIt.next());
					}
				}
				
				if(act.equals("reloadSettings")){					
					se.settings.reload();
				}
				
				if(act.equals("resetAll")){
					se.reset();
				}
				
				if(act.equals("reset")){
					Argument dialogueName=actionArgs.get(args.get(0));
					se.reset(user, dialogueName.getValueAsString().toLowerCase());
					se.systemCore.findOntology(dialogueName.getValueAsString().toLowerCase()).update();
				}
				
				if(act.equals("enableSDO")){
					Argument dialogueName=actionArgs.get(args.get(1));
					Argument taskID=actionArgs.get(args.get(2));
					se.enableOnto(dialogueName.getValueAsString().toLowerCase(), user);
					se.systemCore.findOntology(dialogueName.getValueAsString().toLowerCase()).update();
					if(!dialogueNameMap.containsKey(taskID.getValueAsString())){
						dialogueNameMap.put(taskID.getValueAsString(),dialogueName.getValueAsString().toLowerCase());
					}
					if(!taskIDMap.containsKey(dialogueName.getValueAsString().toLowerCase())){
						taskIDMap.put(dialogueName.getValueAsString().toLowerCase(),taskID.getValueAsString());	
					}
					Iterator<String> iterator = dialogueNameMap.keySet().iterator();  					   
					while (iterator.hasNext()) {  
						String key = iterator.next().toString();  
						String value = dialogueNameMap.get(key).toString();  
					    System.out.println(key + " " + value);  
					}  
				}
				
				if(act.equals("disableSDO")){
					Argument taskID=actionArgs.get(args.get(1));
					if(dialogueNameMap.containsKey(taskID.getValueAsString())){
						String dialogueNameString = dialogueNameMap.get(taskID.getValueAsString());
						se.disableOnto(dialogueNameString.toLowerCase(), user);
						se.systemCore.findOntology(dialogueNameString.toLowerCase()).update();
						dialogueNameMap.remove(taskID.getValueAsString());
						if(taskIDMap.containsKey(dialogueNameString.toLowerCase())){
							taskIDMap.remove(dialogueNameString.toLowerCase());
						}
					}
					else if(se.systemCore.findOntology(taskID.getValueAsString().toLowerCase())!=null){
						se.disableOnto(taskID.getValueAsString().toLowerCase(), user);
						se.systemCore.findOntology(taskID.getValueAsString().toLowerCase()).update();
						if(dialogueNameMap.containsValue(taskID.getValueAsString().toLowerCase())){
							Set<Map.Entry<String,String>> entries = dialogueNameMap.entrySet();
							Iterator<Entry<String, String>> it = entries.iterator();
						    while (it.hasNext()) {
						        Map.Entry<String, String> entry = (Map.Entry<String, String>) it.next();
						        if(entry.getValue().toLowerCase().equals(taskID.getValueAsString().toLowerCase())){
						        	dialogueNameMap.remove(entry.getKey());
						        }
						    }
						}
						if(taskIDMap.containsKey(taskID.getValueAsString().toLowerCase())){
							taskIDMap.remove(taskID.getValueAsString().toLowerCase());
						}
					}
					Iterator<String> iterator = dialogueNameMap.keySet().iterator();  					   
					while (iterator.hasNext()) {  
						String key = iterator.next().toString();  
						String value = dialogueNameMap.get(key).toString();  
					    System.out.println(key + " " + value);  
					} 
				}
				
				if(act.equals("setSleep")){
					Argument sleepState=actionArgs.get(args.get(0));
					if(sleepState.getValueAsBoolean()){
						se.setSleepEnabled(user);
					}
					else{
						se.setSleepDisabled(user);	
					}
					se.settings.reload();
				}	
				
				if(act.equals("getSleep")){
					Argument sleepState=actionArgs.get(args.get(0));
					int s = se.getSleep(user);
					if(s==0){
						sleepState.setValue(false);
					}
					else if (s==1){
						sleepState.setValue(true);
					}
				}	
				
				if(act.equals("setVariable")){
					Argument taskID=actionArgs.get(args.get(0));
					Argument varName=actionArgs.get(args.get(1));
					Argument value=actionArgs.get(args.get(2));
					if(dialogueNameMap.containsKey(taskID.getValueAsString())){
						String ontoName = dialogueNameMap.get(taskID.getValueAsString());	
						if(se.setVariable(ontoName.toLowerCase(), varName.getValueAsString(), value.getValueAsString(), user)){
							se.systemCore.findOntology(ontoName.toLowerCase()).update();
							System.out.println("Set variable '"+varName.getValueAsString()+"' of task '"+taskID.getValueAsString()+"' to value '"+value.getValueAsString()+"'");	
						}						
					}
					else if((se.systemCore.findOntology(taskID.getValueAsString()))!=null){
						String ontoName = se.systemCore.findOntology(taskID.getValueAsString()).Name;						
						if(se.setVariable(ontoName.toLowerCase(), varName.getValueAsString(), value.getValueAsString(), user)){
							se.systemCore.findOntology(ontoName.toLowerCase()).update();
							System.out.println("Set variable '"+varName.getValueAsString()+"' of ontology '"+taskID.getValueAsString()+"' to value '"+value.getValueAsString()+"'");	
						}
					}
					else{
						System.out.println("Did nothing :-(");
					}
				}
				
				if(act.equals("getVariable")){
					Argument taskID=actionArgs.get(args.get(0));
					Argument varName=actionArgs.get(args.get(1));
					Argument value=actionArgs.get(args.get(2));
					if(dialogueNameMap.containsKey(taskID.getValueAsString())){
						String ontoName = dialogueNameMap.get(taskID.getValueAsString());						
						String valueString = se.getVariable(ontoName.toLowerCase(), varName.getValueAsString(), user);
						value.setValue(valueString);
					}	
					else if((se.systemCore.findOntology(taskID.getValueAsString()))!=null){
						String ontoName = se.systemCore.findOntology(taskID.getValueAsString()).Name;						
						String valueString = se.getVariable(ontoName.toLowerCase(), varName.getValueAsString(), user);
						value.setValue(valueString);
					}
				}
				
				if(act.equals("setTtsMode")){
					Argument id=actionArgs.get(args.get(0));
					se.setTtsMode(id.getValueAsInt(), user);
					se.settings.reload();
				}
				
				if(act.equals("setAsrMode")){
					Argument id=actionArgs.get(args.get(0));
					se.setAsrMode(id.getValueAsInt(), user);
					se.settings.reload();
				}
				
				if(act.equals("loadSDOs")){
					se.settings.reload();
					se.systemCore.loadOntologiesFromSettings();
				}
				
				if(act.equals("stopAllDialogue")){
					dialogueNameMap = new HashMap<String, String>();
					taskIDMap = new HashMap<String, String>();
					Iterator<OwlSpeakOntology> ontoIter = se.systemCore.ontologies.iterator();
					while(ontoIter.hasNext()){
						String ontoName = ontoIter.next().Name;
						if(!ontoName.equals("system")){
							se.disableOnto(ontoName,user);
							OwlSpeakOntology onto = se.systemCore.findOntology(ontoName);
							if (onto!=null) {
								se.systemCore.findOntology(ontoName).update();	
							}	
						}						
					}
				}
				
				if(act.equals("getActiveTaskIds")){
					Argument taskIDs =actionArgs.get(args.get(0));
					Iterator<String> taskIter = taskIDMap.values().iterator();
					String tasks = "";
					while(taskIter.hasNext()){
						tasks += taskIter.next()+";";
					}
					taskIDs.setValue(tasks);				
				}
					
				if(act.equals("getActiveOntos")){
					Argument ontologies =actionArgs.get(args.get(0));
					ontologies.setValue(se.listOntos());
				}
				
				Iterator<StateVariable> svIt = sv.iterator(); 
				int i = 0;
				while(svIt.hasNext()){
					DeviceServer.changeStateVariable(svIt.next(), args.get(i)+";"+actionArgs.get(args.get(i)).getValueAsString().toLowerCase());
					i++;
				}

				return new UPnPStatus();
			}
		});
	}
}

