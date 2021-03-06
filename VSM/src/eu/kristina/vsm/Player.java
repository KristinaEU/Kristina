package eu.kristina.vsm;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.bml.ActionFactory;
import eu.kristina.vsm.rest.Resource;
import eu.kristina.vsm.rest.WebClient;
import eu.kristina.vsm.ssi.SSIEventFactory;
import eu.kristina.vsm.ssi.SSIEventHandler;
import eu.kristina.vsm.ssi.SSIEventListener;
import eu.kristina.vsm.ssi.SSIEventNotifier;
import eu.kristina.vsm.util.Timer;
import eu.kristina.vsm.util.Utilities;

/**
 * @author Gregor Mehlmann
 */
public final class Player implements RunTimePlayer, SSIEventHandler {

    // The singelton player instance
    public static Player sInstance = null;
    // The format to print dates
    private final SimpleDateFormat mEnvFormat
            = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss:SSS");
    // The format to log dates
    private final SimpleDateFormat mLogFormat
            = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS");
    // The singelton logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The VSM runtime environment
    private final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The player's runtime project 
    private RunTimeProject mProject;
    // The project's specific config
    private PlayerConfig mPlayerConfig;
    // The project's specific name
    private String mPlayerName;
    // The SSI socket handlers
    private SSIEventListener mSSIListener;
    private SSIEventNotifier mSSINotifier;
    // The rest service client
    private WebClient mRestClient;
    // The rest service urls
    private final HashMap<String, Resource> mResourceMap = new HashMap();
    // A random number generator
    private final Random mRandom = new Random();
    // A system time timer
    private Timer mTimer = null;
    
    private class SemanticEvent {
    	public String xml;
    	public long timestamp;
    }
    private final ArrayList<SemanticEvent> semanticEvents = new ArrayList<SemanticEvent>(1000);
    private long semanticEventsStartTime = 0;

    // Get the singelton player
    public static synchronized Player getInstance() {
        if (sInstance == null) {
            sInstance = new Player();
        }
        return sInstance;
    }

    // Launch the VSM project
    @Override
    public final boolean launch(final RunTimeProject project) {
        // Initialize the project
        mProject = project;
        // Initialize the name
        mPlayerName = project.getPlayerName(this);
        // Initialize the config
        mPlayerConfig = project.getPlayerConfig(mPlayerName);
        // Get the rest services
        final int count = Integer.parseInt(mPlayerConfig.getProperty("restful.resource.count"));
        for (int i = 0; i < count; i++) {
            final String host = mPlayerConfig.getProperty("restful.resource." + i + ".host");
            final String name = mPlayerConfig.getProperty("restful.resource." + i + ".name");
            final String path = mPlayerConfig.getProperty("restful.resource." + i + ".path");
            final String cons = mPlayerConfig.getProperty("restful.resource." + i + ".cons");
            final String prod = mPlayerConfig.getProperty("restful.resource." + i + ".prod");
            // Create the service date
            final Resource resource = new Resource(host, name, path, cons, prod);
            // Print some information
            mLogger.message("Registering RESTful service resource '" + resource + "'" + "\r\n");
            // Add the new service then
            mResourceMap.put(name, resource);
        }
        // Get SSI listener config
        final String ssillhost = mPlayerConfig.getProperty("ssi.listener.local.host");
        final String ssillport = mPlayerConfig.getProperty("ssi.listener.local.port");
        final String ssilrhost = mPlayerConfig.getProperty("ssi.listener.remote.host");
        final String ssilrport = mPlayerConfig.getProperty("ssi.listener.remote.port");
        final String ssilrflag = mPlayerConfig.getProperty("ssi.listener.remote.flag");
        // Print some information
        mLogger.message(""
                + "SSI Listener Local Host  : '" + ssillhost + "'" + "\r\n"
                + "SSI Listener Local Port  : '" + ssillport + "'" + "\r\n"
                + "SSI Listener Remote Host : '" + ssilrhost + "'" + "\r\n"
                + "SSI Listener Remote Port : '" + ssilrport + "'" + "\r\n"
                + "SSI Listener Remote Flag : '" + ssilrflag + "'" + "\r\n");
        // Get SSI notifier config
        final String ssinlhost = mPlayerConfig.getProperty("ssi.notifier.local.host");
        final String ssinlport = mPlayerConfig.getProperty("ssi.notifier.local.port");
        final String ssinrhost = mPlayerConfig.getProperty("ssi.notifier.remote.host");
        final String ssinrport = mPlayerConfig.getProperty("ssi.notifier.remote.port");
        final String ssinrflag = mPlayerConfig.getProperty("ssi.notifier.remote.flag");
        // Print some information
        mLogger.message(""
                + "SSI Notifier Local Host  : '" + ssinlhost + "'" + "\r\n"
                + "SSI Notifier Local Port  : '" + ssinlport + "'" + "\r\n"
                + "SSI Notifier Remote Host : '" + ssinrhost + "'" + "\r\n"
                + "SSI Notifier Remote Port : '" + ssinrport + "'" + "\r\n"
                + "SSI Notifier Remote Flag : '" + ssinrflag + "'" + "\r\n");
        // Initialize the rest client
        mRestClient = new WebClient();
        // Initialize the SSI notifier
        mSSINotifier = new SSIEventNotifier(this,
                ssinlhost, Integer.parseInt(ssinlport),
                ssinrhost, Integer.parseInt(ssinrport),
                Boolean.parseBoolean(ssinrflag));
        mSSINotifier.start();
        // Initialize the SSI listener
        mSSIListener = new SSIEventListener(this,
                ssillhost, Integer.parseInt(ssillport),
                ssilrhost, Integer.parseInt(ssilrport),
                Boolean.parseBoolean(ssilrflag));
        mSSIListener.start();
        // Start the time
        mTimer = new Timer();
        // Print some information
        mLogger.message("Launching KRISTINA scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    // Unload the VSM project
    @Override
    public final boolean unload() {
        // Abort running handlers
        mSSIListener.abort();
        mSSINotifier.abort();
        //
        // Join with the handlers        
        try {
            mSSIListener.join();
            mSSINotifier.join();
        } catch (final InterruptedException exc) {
            mLogger.failure(exc.toString());
        }
        // Print some information
        mLogger.message("Unloading KRISTINA scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Produce a blink command
    public final String blink(
            final float duration) {
        // Get the resource
        final Resource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final JSONObject object = new JSONObject();
        // Produce the initial object
        object.put("uuid", id());
        object.put("type", "idle");
        object.put("date", date());
        object.put("meta", new JSONObject("{\"avatar\":\"KRISTINA\"}"));
        object.put("data", new JSONObject(ActionFactory.blink(duration)));
        // Execute POST request
        return mRestClient.post(resource, "", object.toString(2));
    }

    // Produce a gaze command
    public final String gaze(
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final Resource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final JSONObject object = new JSONObject();
        // Produce the initial object
        object.put("uuid", id());
        object.put("type", "idle");
        object.put("date", date());
        object.put("meta", new JSONObject("{\"avatar\":\"KRISTINA\"}"));
        object.put("data", new JSONObject(ActionFactory.gaze(speed, angle, direction)));
        // Execute POST request
        return mRestClient.post(resource, "", object.toString(2));
    }

    // Produce a turn envelope
    public final String turn(final String meta) {
        final JSONObject object = new JSONObject();
        // Produce the initial object
        object.put("uuid", id());
        object.put("type", "turn");
        object.put("date", date());
        object.put("meta", new JSONObject(meta));
        object.put("data", new JSONObject());
        // Return the initial object
        return object.toString(2);
    }

    // Produce a control envelope
    public final String state(final String meta, final String state) {
        final JSONObject data = new JSONObject();
        data.put("state", state);
        final JSONObject object = new JSONObject();
        // Produce the initial object
        object.put("uuid", id());
        object.put("type", "control");
        object.put("date", date());
        object.put("meta", new JSONObject(meta));
        object.put("data", data);
        // Return the initial object
        return object.toString(2);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Put a value at some path in the JSON string
    public final String put(
            final String obj,
            final String key,
            final String val,
            final String typ) {
        return Utilities.put(obj, key, val, typ);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Post some request to a specific service
    public final String post(
            final String service,
            final String content) {
        // Get the resource
        final Resource resource = mResourceMap.get(service);
        // Create a JSON object 
        final JSONObject object = new JSONObject(content);
        // Create the payload
        final String payload = object.toString(4);
        // Execute POST request
        final String response = mRestClient.post(resource, "", payload);
        // Return the result
        return response;
    }
    
    public final String mapGesture(final Double label, final boolean hand){
    	/*
0: unknown
1: right upperbody
2: left upperbody
3: right lowerbody
4: left lowerbody
5: right upperarm
6: right lowerarm
7: left upperarm
8: right lowerarm
9: face
    	 */
        if (hand){
        	final String[] labels = { "unknown","rightUpperBody","leftUpperBody","rightLowerBody","leftLowerBody","rightUpperArm","rightLowerArm","leftUpperArm","rightUpperArm","head" };
    		return labels[label.intValue()];
    	} else {
    		final String[] labels = { "leftHandWave","rightHandWave","headShake","headNod" };
    		String res = "";
    		Integer val = label.intValue();
    		res += ((val & 1) == 1)?labels[0]+"|":"";
    		res += ((val & 2) == 2)?labels[1]+"|":"";
    		res += ((val & 4) == 4)?labels[2]+"|":"";
    		res += ((val & 8) == 8)?labels[3]+"|":"";
            return res;
        }
    }
    
    public final String mergeSemanticEvents(){
    	String response = "";
    	/*
      	    <?xml version="1.0" ?>
			<events ssi-v="V2">
				<event sender="gesture" event="info" from="245" dur="0" prob="1.000000" type="MAP" state="COMPLETED" glue="0">
					<tuple string="arousal" value="0.045947" />
					<tuple string="codeLH" value="0.000000" />
					<tuple string="codeRH" value="0.000000" />
					<tuple string="codeGesture" value="0.000000" />
					<tuple string="leftHandMissing" value="0.000000" />
					<tuple string="rightHandMissing" value="0.000000" />
				</event>
			</events>
    	 */
    	final ConcurrentSkipListMap<Long,Double> leftHand = new ConcurrentSkipListMap<Long,Double>();
    	final ConcurrentSkipListMap<Long,Double> rightHand = new ConcurrentSkipListMap<Long,Double>();
    	final ConcurrentSkipListMap<Long,Double> gesture = new ConcurrentSkipListMap<Long,Double>();
    	
    	synchronized(semanticEvents){
    		//loop through all events, filter out near similar, put larger steps aside (per field);
    		double prev_lh = 0.0;
    		double prev_rh = 0.0;
    		double prev_gs = 0.0;
    		for (SemanticEvent event : semanticEvents){
    			//Poor mans parser, grep the correct fields:
                        //mLogger.success("checking:"+event.xml);
    			final String txt[] = event.xml.split("\n");
    			for (String line : txt){
                                //mLogger.success("substring:"+line);
    				if (line.indexOf("codeLH")>0){
    					final double lh = Double.parseDouble(line.substring(line.indexOf("codeLH")+15,line.indexOf("codeLH")+23));
    					if (lh != prev_lh){
        					leftHand.put(event.timestamp,lh);
        					prev_lh = lh;
        				}
        			}
    				if (line.indexOf("codeRH")>0){
    					final double rh = Double.parseDouble(line.substring(line.indexOf("codeRH")+15,line.indexOf("codeRH")+23));	
    					if (rh != prev_rh){
        					rightHand.put(event.timestamp,rh);
        					prev_rh = rh;
        				}
        			}
    				if (line.indexOf("codeGesture")>0){
    					final double gs = Double.parseDouble(line.substring(line.indexOf("codeGesture")+20,line.indexOf("codeGesture")+28));
    					if (gs != prev_gs){
    						gesture.put(event.timestamp,gs);
    						prev_gs = gs;
    					}}
    			}
    		}
    		//Put list of states into a string form. Json object with separate arrays
    		final JSONObject object = new JSONObject();
            final JSONArray leftHandJson = new JSONArray();
            final JSONArray rightHandJson = new JSONArray();
            final JSONArray gestureJson = new JSONArray();
            
            //Add list of states (and translate double back to labels)
            Entry<Long,Double> prev = leftHand.pollFirstEntry();
            //Add unknown state before prev.
            if (prev != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", 0);
            	item.put("end", prev.getKey()-semanticEventsStartTime);
            	item.put("label", mapGesture(0.0,true));
            	leftHandJson.put(item);
            }
            Entry<Long,Double> next = leftHand.pollFirstEntry();
            while (next != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", prev.getKey()-semanticEventsStartTime);
            	item.put("end", next.getKey()-semanticEventsStartTime);
            	item.put("label", mapGesture(prev.getValue(),true));
            	leftHandJson.put(item);
            	prev = next;
            	next = leftHand.pollFirstEntry();
            }
            //Add last state:
            if (prev != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", prev.getKey()-semanticEventsStartTime);
            	item.put("end", System.currentTimeMillis()-semanticEventsStartTime);
            	item.put("label", mapGesture(prev.getValue(),true));
            	leftHandJson.put(item);
            }
            object.put("leftHandLocation", leftHandJson);
            
            //Add list of states (and translate double back to labels)
            prev = rightHand.pollFirstEntry();
            //Add unknown state before prev.
            if (prev != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", 0);
            	item.put("end", prev.getKey()-semanticEventsStartTime);
            	item.put("label", mapGesture(0.0,true));
            	rightHandJson.put(item);
            }
            next = rightHand.pollFirstEntry();
            while (next != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", prev.getKey()-semanticEventsStartTime);
            	item.put("end", next.getKey()-semanticEventsStartTime);
            	item.put("label", mapGesture(prev.getValue(),true));
            	rightHandJson.put(item);
            	prev = next;
            	next = rightHand.pollFirstEntry();
            }
            //Add last state:
            if (prev != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", prev.getKey()-semanticEventsStartTime);
            	item.put("end", System.currentTimeMillis()-semanticEventsStartTime);
            	item.put("label", mapGesture(prev.getValue(),true));
            	rightHandJson.put(item);
            }
            object.put("rightHandLocation", rightHandJson);

            //Add list of states (and translate double back to labels)
            prev = gesture.pollFirstEntry();
            //Add unknown state before prev.
            if (prev != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", 0);
            	item.put("end", prev.getKey()-semanticEventsStartTime);
            	item.put("label", mapGesture(0.0,false));
            	gestureJson.put(item);
            }
            next = gesture.pollFirstEntry();
            while (next != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", prev.getKey()-semanticEventsStartTime);
            	item.put("end", next.getKey()-semanticEventsStartTime);
            	item.put("label", mapGesture(prev.getValue(),false));
            	gestureJson.put(item);
            	prev = next;
            	next = gesture.pollFirstEntry();
            }
            //Add last state:
            if (prev != null){
            	final JSONObject item = new JSONObject();
            	item.put("start", prev.getKey()-semanticEventsStartTime);
            	item.put("end", System.currentTimeMillis()-semanticEventsStartTime);
            	item.put("label", mapGesture(prev.getValue(),false));
            	gestureJson.put(item);
            }
            object.put("gestures", gestureJson);

            response = object.toString(4);
            semanticEventsStartTime = 0;
            semanticEvents.clear();
    	}
    	return response;
    }
    
    // Post some request to a specific service
    public final void abort() {
        mRestClient.abort();
    }

    ////////////////////////////////////////////////////////////////////////////
    @Override
    public final void handle(final String message) {
        // Print some information
        //mLogger.warning("Receiving SSI event:\n" + message + "");
        try {
            // Parse the received XML string
            final ByteArrayInputStream stream = new ByteArrayInputStream(message.getBytes("UTF-8"));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(stream);

            // Get the XML tree root element
            final Element element = document.getDocumentElement();
            // Check if we have SSI events 
            if (element.getTagName().equals("events")) {
                // Get the list of SSI events
                final NodeList eventList = element.getElementsByTagName("event");
                // Check if the list is empty
                if (eventList.getLength() > 0) {
                    // Process the indvidual SSI events
                    for (int i = 0; i < eventList.getLength(); i++) {
                        final Element event = ((Element) eventList.item(i));
                        // Get the event attributes
                        final String mode = event.getAttribute("sender");
                        final String name = event.getAttribute("event");
                        final String type = event.getAttribute("type");
                        final String state = event.getAttribute("state");
                        final Integer glue = Integer.parseInt(event.getAttribute("glue"));
                        final Integer from = Integer.parseInt(event.getAttribute("from"));
                        final Integer dur = Integer.parseInt(event.getAttribute("dur"));
                        final Double prob = Double.parseDouble(event.getAttribute("prob"));
                        // Process the event features 
                        if (mode.equalsIgnoreCase("audio")) {
                            if (name.equalsIgnoreCase("vad")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    // User stopped speaking
                                    mLogger.success("User stopped speaking");
                                    set("UserTalking", false);
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // User started speaking
                                    mLogger.success("User started speaking");
                                    set("UserTalking", true);
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else if (mode.equalsIgnoreCase("fsender")) {
                            if (name.equalsIgnoreCase("fevent")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("ntuple") || type.equalsIgnoreCase("map")) {
                                        // Get the list of tuples
                                        final NodeList tuples = element.getElementsByTagName("tuple");
                                        for (int j = 0; j < tuples.getLength(); j++) {
                                            // Get the nex tuple element
                                            final Element tuple = ((Element) tuples.item(j));
                                            // Get the tuple's attributes
                                            final String key = tuple.getAttribute("string");
                                            final Double val = Double.parseDouble(tuple.getAttribute("value"));
                                            // Set the variable value
                                            //mLogger.message(key + "=" + String.format(Locale.US, "%.6f", val));
                                            if (key.equalsIgnoreCase("valence")) {
                                                set("UserValence", val.floatValue());
                                            } else if (key.equalsIgnoreCase("arousal")) {
                                                set("UserArousal", val.floatValue());
                                            } else {
                                                // Cannot process this  
                                            }
                                        }
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else if (mode.equalsIgnoreCase("face")) {
                            if (name.equalsIgnoreCase("discreteemotion")) {
                               // mLogger.success("Received face semantic event:'" + message + "'\n");
                            }
                        } else if (mode.equalsIgnoreCase("gesture")) {
                            if (name.equalsIgnoreCase("info")) {
                                mLogger.success("Received gesture event:'" + message + "'\n");
                            	/*
                            	 *  <?xml version="1.0" ?>
									<events ssi-v="V2">
										<event sender="gesture" event="info" from="245" dur="0" prob="1.000000" type="MAP" state="COMPLETED" glue="0">
											<tuple string="arousal" value="0.045947" />
											<tuple string="codeLH" value="0.000000" />
											<tuple string="codeRH" value="0.000000" />
											<tuple string="codeGesture" value="0.000000" />
											<tuple string="leftHandMissing" value="0.000000" />
											<tuple string="rightHandMissing" value="0.000000" />
										</event>
									</events>
                            	 * 
                            	 * Receive events, add them to a queue/array. On forwarding turn to DM, merge inputs to state timeslots. Empty queue again, restart collection.
                            	 */
								final SemanticEvent se = new SemanticEvent();
								se.timestamp = System.currentTimeMillis();
								se.xml = message;
								synchronized (semanticEvents) {
									if (semanticEventsStartTime == 0){
										semanticEventsStartTime = se.timestamp;
									}
									if (semanticEvents.size() < 10000) { // Some sanity limit (keeping memory down)
										semanticEvents.add(se);
									}
								}
							} else {
								// Cannot process this
							}
						} else if (mode.equalsIgnoreCase("vocapia")) {
                            if (name.equalsIgnoreCase("transcript")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent().trim();
                                        // User said something
                                        mLogger.success("User utterance is\n" + text + "'");
                                        // Set the variable value         
                                        //set("UserText", text);
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else if (name.equalsIgnoreCase("xml")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent().trim();
                                        // User said something
                                        mLogger.success("Vocapia xml is\n" + text + "'");
                                        // Get the JSON text
                                        final String data = Utilities.parseVocapia(text);
                                        // User said something
                                        mLogger.success("Vocapia data is\n" + data + "'");
                                        // Set the variable value         
                                        set("UserData", data);
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else {
                            // Cannot process this
                        }
                    }
                }
            }
        } catch (final DOMException | IOException | SAXException | NumberFormatException | ParserConfigurationException exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public final void set(final String name, final String value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Variable '" + name + "' does not exist");
        }
    }

    public final void set(final String name, final Float value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Variable '" + name + "' does not exist");
        }
    }

    public final void set(final String name, final Boolean value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Variable '" + name + "' does not exist");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public final void ssi(final String content) {
        // Create the SSI event
        final String event = SSIEventFactory.createEvent(content);
        // Send the SSI event
        mSSINotifier.sendString(event);
    }

    ////////////////////////////////////////////////////////////////////////////
    // Get a random float 
    public final float randFloat(final float scale, final float shift) {
        return scale * (mRandom.nextFloat() - shift);
    }

    // Get a random int from [0, bound[
    public final int randInt(final int bound) {
        return mRandom.nextInt(bound);
    }

    ////////////////////////////////////////////////////////////////////////////
    public final void reset() {
        mTimer.reset();
    }

    public final String time() {
        return Long.toString(mTimer.time());
    }

    public final String date() {
        return mEnvFormat.format(new Date());
    }

    ////////////////////////////////////////////////////////////////////////////
    public final void log(final String string) {
        final JSONObject object = new JSONObject(string);
        try {
            final File file = new File("C:/Users/Administrator/Desktop/KristinaLogFiles/"
                    + mLogFormat.format(new Date()) + ".json");
            final FileOutputStream stream = new FileOutputStream(file);
            final Writer out = new OutputStreamWriter(stream, "UTF8");
            out.write(object.toString(4));
            out.flush();
            out.close();
            stream.close();
        } catch (final IOException exc) {
            mLogger.failure(exc.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public final int count(final String file) {
        try {
            return (int) Files.lines(Paths.get(file)).count();
        } catch (final IOException exc) {
            return 0;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public final String read(final String file, final int line) {
        try {
            return Files.readAllLines(Paths.get(file)).get(line);
        } catch (final IOException exc) {
            return "{}";
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    private volatile long mId = 0;

    // Get a new command id
    public synchronized long id() {
        return ++mId;
    }

    ////////////////////////////////////////////////////////////////////////////
    @Override
    public final void play(final String name, final LinkedList<AbstractValue> args) {
        // Do nothing here ...
    }
}
