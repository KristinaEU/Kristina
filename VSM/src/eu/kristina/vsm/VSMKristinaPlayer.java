package eu.kristina.vsm;

import eu.kristina.vsm.rest.RESTFulWebClient;
import eu.kristina.vsm.gti.GTIActionFactory;
import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.rest.RESTFulResource;
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
import java.util.Locale;

/**
 * @author Gregor Mehlmann
 */
public final class VSMKristinaPlayer implements RunTimePlayer, SSIEventHandler {

    // The singelton player instance
    public static VSMKristinaPlayer sInstance = null;
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
    private RESTFulWebClient mRestClient;
    // The kristina avatar id
    private String mAvatarID = "KRISTINA";
    // The language parameter 
    private String mLanguage = "de";
    // The  rest service urls
    private final HashMap<String, RESTFulResource> mResourceMap = new HashMap();
    // A random number generator
    private final Random mRandom = new Random();

    // Get the singelton player
    public static synchronized VSMKristinaPlayer getInstance() {
        if (sInstance == null) {
            sInstance = new VSMKristinaPlayer();
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
            final RESTFulResource resource = new RESTFulResource(host, name, path, cons, prod);
            // Print some information
            //mLogger.message("Registering RESTful service resource '" + resource + "'" + "\r\n");
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
        // Initialize the avatar identifier
        mAvatarID = mPlayerConfig.getProperty("kristina.gti.avatar.id");
        // Initialize the language parameter
        mLanguage = mPlayerConfig.getProperty("kristina.conf.lang.out");
        // Initialize the SSI notifier
        mSSINotifier = new SSIEventNotifier(this,
                ssinlhost, Integer.parseInt(ssinlport),
                ssinrhost, Integer.parseInt(ssinrport),
                Boolean.parseBoolean(ssinrflag));
        mSSINotifier.start();
        // Initialize the rest client
        mRestClient = new RESTFulWebClient();
        // Initialize the SSI listener
        mSSIListener = new SSIEventListener(this,
                ssillhost, Integer.parseInt(ssillport),
                ssilrhost, Integer.parseInt(ssilrport),
                Boolean.parseBoolean(ssilrflag));
        mSSIListener.start();
        // Print some information
        //mLogger.message("Launching KRISTINA scene player '" + this + "' with configuration:\n" + mPlayerConfig);
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
    public final void ssi(final String content) {
        // Create the SSI event
        final String event = SSIEventFactory.createEvent(content);
        // Send the SSI event
        mSSINotifier.sendString(event);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String blink() {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.blink(1.0f);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String blink(
            final float duration) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.blink(duration);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String blink(
            final String id) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.blink(1.0f);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    public final String blink(
            final String id,
            final float duration) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.blink(duration);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String face() {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.face(0.0f, 0.0f, 1.0f);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String face(
            final float valence,
            final float arousal) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.face(valence, arousal, 1.0f);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String face(
            final float valence,
            final float arousal,
            final float duration) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.face(valence, arousal, duration);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String face(
            final String id) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.face(0.0f, 0.0f, 1.0f);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    public final String face(
            final String id,
            final float valence,
            final float arousal) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.face(valence, arousal, 1.0f);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    public final String face(
            final String id,
            final float valence,
            final float arousal,
            final float duration) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.face(valence, arousal, duration);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String eyes(
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.eyes(speed, angle, direction);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String eyes(
            final String id,
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.eyes(speed, angle, direction);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String gaze(
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.gaze(speed, angle, direction);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String gaze(
            final String id,
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.gaze(speed, angle, direction);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String head(
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.head(speed, angle, direction);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String head(
            final String id,
            final float speed,
            final float angle,
            final String direction) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.head(speed, angle, direction);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String nod(
            final float amount,
            final int repetition) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.nod(amount, repetition);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String nod(
            final String id,
            final float amount,
            final int repetition) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.nod(amount, repetition);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String shake(
            final float amount,
            final int repetition) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.shake(amount, repetition);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String shake(
            final String id,
            final float amount,
            final int repetition) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.shake(amount, repetition);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String tilt(
            final float amount,
            final int repetition) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.tilt(amount, repetition);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, command);
    }

    public final String tilt(
            final String id,
            final float amount,
            final int repetition) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.tilt(amount, repetition);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String anim(
            final String id,
            final String name,
            final String mode,
            final float speed) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Idle");
        // Get the command
        final String command = GTIActionFactory.anim(name, mode, speed);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String speech(final String id) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Verbal");
        // Get the command
        final String command = GTIActionFactory.speech();
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);

    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public final String dm(
            final float valence,
            final float arousal,
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Dialog-Management");
        // Print some information
        //mLogger.message("Resource is\n'" + resource + "'");
        // Get the query data
        final String query = "?valence=" + valence + "&arousal=" + arousal;
        // Execute POST request
        return mRestClient.post(resource, query, content);
    }

    public final String mv(
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Mode-Selection");
        // Print some information
        //mLogger.message("Resource is\n'" + resource + "'");
        // Execute POST request
        return mRestClient.post(resource, "?mode=verbal", content);
    }

    public final String mn(
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Mode-Selection");
        // Print some information
        //mLogger.message("Resource is\n'" + resource + "'");
        // Execute POST request
        return mRestClient.post(resource, "?mode=non_verbal", content);
    }

    public final String lg(
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Language-Generation");
        // Print some information
        //mLogger.message("Resource is\n'" + resource + "'");
        // Get the query data
        final String query = "";        
        // The new query with language parameter?
        //final String query = "?lang=" + mAvatarID;
        // Execute POST request
        return mRestClient.post(resource, query, content);
    }

    public final String av(
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Verbal");
        // Print some information
        //mLogger.message("Resource is\n'" + resource + "'");
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, content);
    }

    public final String an(
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Avatar-Nonverbal");
        // Print some information
        //mLogger.message("Resource is\n'" + resource + "'");
        // Execute POST request
        return mRestClient.post(resource, "?id=" + mAvatarID, content);
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
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
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    // Set a string variable via runtime
    public final void set(final String name, final String value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a string variable via runtime
    public final void set(final String name, final Float value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a boolean variable via runtime
    public final void set(final String name, final Integer value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a boolean variable via runtime
    public final void set(final String name, final Boolean value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final String value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final Float value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final Integer value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final Boolean value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            //mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    @Override
    public final void play(final String name, final LinkedList<AbstractValue> args) {
        // Do nothing here ...
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    // Handle an SSI event 
    @Override
    public final void handle(final String message) {
        // Print some information
        //mLogger.message("Receiving SSI event:\n" + message + "");
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
                                    set("US", false);
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // User started speaking
                                    mLogger.success("User started speaking");
                                    set("US", true);
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
                                                set("UV", val.floatValue());
                                            } else if (key.equalsIgnoreCase("arousal")) {
                                                set("UA", val.floatValue());
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
                        } else if (mode.equalsIgnoreCase("upf")) {
                            if (name.equalsIgnoreCase("la")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent();
                                        // User said something
                                        mLogger.success("User speech act is\n" + text + "");
                                        // Set the variable value
                                        set("LA", text);
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
                                        final String text = event.getTextContent();
                                        // User said something
                                        mLogger.success("User utterance is\n" + text + "'");
                                        // Set the variable value
                                        set("TS", text);
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
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
            exc.printStackTrace();
        }
    }
}
