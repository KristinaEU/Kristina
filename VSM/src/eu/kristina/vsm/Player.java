package eu.kristina.vsm;

import eu.kristina.vsm.rest.WebClient;
import eu.kristina.vsm.bml.ActionFactory;
import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.rest.Resource;
import eu.kristina.vsm.ssi.SSIEventFactory;
import eu.kristina.vsm.ssi.SSIEventListener;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import eu.kristina.vsm.ssi.SSIEventHandler;
import eu.kristina.vsm.ssi.SSIEventNotifier;
import eu.kristina.vsm.util.Utilities;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * @author Gregor Mehlmann
 */
public final class Player implements RunTimePlayer, SSIEventHandler {

    // The singelton player instance
    public static Player sInstance = null;
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
            // Create the service data
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
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String blink(
            final float duration) {
        // Get the resource
        final Resource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final JSONObject object = new JSONObject();
        // Produce the initial object
        object.put("uuid", id());
        object.put("type", "idle");
        object.put("meta", new JSONObject("{\"avatar\":\"KRISTINA\"}"));
        object.put("data", new JSONObject(ActionFactory.blink(duration)));
        // Execute POST request
        return mRestClient.post(resource, "", object.toString(2));
    }

    // Get a gaze command
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
        object.put("meta", new JSONObject("{\"avatar\":\"KRISTINA\"}"));
        object.put("data", new JSONObject(ActionFactory.gaze(speed, angle, direction)));
        // Execute POST request
        return mRestClient.post(resource, "", object.toString(2));
    }

    ////////////////////////////////////////////////////////////////////////////
    // Produce an inital envelope
    public final String create(final String type, final String meta) {
        final JSONObject object = new JSONObject();
        // Produce the initial object
        object.put("uuid", id());
        object.put("type", type);
        object.put("meta", new JSONObject(meta));
        object.put("data", new JSONObject());
        // Return the initial object
        return object.toString(2);
    }

    // Put a value at some path in the JSON string
    public final String put(
            final String obj,
            final String key,
            final String val,
            final String typ) {
        return Utilities.put(obj, key, val, typ);
    }

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
                                        mLogger.success("Vocapia data is\n" + text + "'");
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
                        } else if (mode.equalsIgnoreCase("vocapia")) {
                            if (name.equalsIgnoreCase("transcript")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent().trim();
                                        // User said something
                                        mLogger.success("User utterance is\n" + text + "'");
                                        // Set the variable value         
                                        set("UserText", text);
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
