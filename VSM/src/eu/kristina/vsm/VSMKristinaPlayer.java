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
    // The  rest service urls
    private final HashMap<String, RESTFulResource> mResourceMap = new HashMap();
    // The player's runtime project 
    private RunTimeProject mProject;
    // The project's specific config
    private PlayerConfig mPlayerConfig;
    // The project's specific name
    private String mPlayerName;
    // The SSI socket handler
    private String mSSISocketLocalHost;
    private Integer mSSISocketLocalPort;
    private String mSSISocketRemoteHost;
    private Integer mSSISocketRemotePort;
    private Boolean mSSISocketRemoteFlag;
    private SSIEventListener mSSISocket;
    // The rest service client
    private RESTFulWebClient mRestClient;
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
            mLogger.message("Registering RESTful service resource '" + resource + "'" + "\r\n");
            // Add the new service then
            mResourceMap.put(name, resource);
        }
        // Get the SSI configuration
        mSSISocketLocalHost = mPlayerConfig.getProperty("ssi.socket.local.host");
        mSSISocketLocalPort = Integer.parseInt(mPlayerConfig.getProperty("ssi.socket.local.port"));
        mSSISocketRemoteHost = mPlayerConfig.getProperty("ssi.socket.remote.host");
        mSSISocketRemotePort = Integer.parseInt(mPlayerConfig.getProperty("ssi.socket.remote.port"));
        mSSISocketRemoteFlag = Boolean.parseBoolean(mPlayerConfig.getProperty("ssi.socket.remote.flag"));

        // Print some information
        mLogger.message(""
                + "SSI Socket Handler Local Host  : '" + mSSISocketLocalHost + "'" + "\r\n"
                + "SSI Socket Handler Local Port  : '" + mSSISocketLocalPort + "'" + "\r\n"
                + "SSI Socket Handler Remote Host : '" + mSSISocketRemoteHost + "'" + "\r\n"
                + "SSI Socket Handler Remote Port : '" + mSSISocketRemotePort + "'" + "\r\n"
                + "SSI Socket Handler Remote Flag : '" + mSSISocketRemoteFlag + "'" + "\r\n");

        // Initialize the SSI socket
        mSSISocket = new SSIEventListener(this,
                mSSISocketLocalHost, mSSISocketLocalPort,
                mSSISocketRemoteHost, mSSISocketRemotePort,
                mSSISocketRemoteFlag);
        mSSISocket.start();
        // Initialize the rest client
        mRestClient = new RESTFulWebClient();
        // Print some information
        mLogger.message("Launching KRISTINA scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    // Unload the VSM project
    @Override
    public final boolean unload() {
        // Abort running handlers
        mSSISocket.abort();
        // Join with the handlers        
        try {
            mSSISocket.join();
        } catch (final InterruptedException exc) {
            mLogger.failure(exc.toString());
        }
        // Print some information
        mLogger.message("Unloading KRISTINA scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    public final /*synchronized*/ String blink(final String id) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Character-Engine");
        // Get the command
        final String command = GTIActionFactory.blink();
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    public final /*synchronized*/ String face(
                    final String id,
                    final float valence,
                    final float arousal) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Character-Engine");
        // Get the command
        final String command = GTIActionFactory.face(valence, arousal);
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);
    }

    public final /*synchronized*/ String speech(final String id) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Character-Engine");
        // Get the command
        final String command = GTIActionFactory.__speech();
        // Execute POST request
        return mRestClient.post(resource, "?id=" + id, command);

    }

    public final String dm(
            final float valence,
            final float arousal,
            final String content) {
        // Get the resource
        final RESTFulResource resource = mResourceMap.get("Dialog-Management");
        // Print some information
        mLogger.message("Resource is\n'" + resource + "'");
        // Get the query data
        final String query = "?valence=" + valence + "&arousal=" + arousal;
        // Execute POST request
        return mRestClient.post(resource, query, content);
    }

    // Get a random float from [-1, 1[
    public final float randFloat() {
        return 2 * (mRandom.nextFloat() - 0.5f);
    }

    // Get a random int from [0, bound[
    public final int randInt(final int bound) {
        return mRandom.nextInt(bound);
    }

    // Set a string variable via runtime
    public final void set(final String name, final String value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a string variable via runtime
    public final void set(final String name, final Float value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a boolean variable via runtime
    public final void set(final String name, final Integer value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a boolean variable via runtime
    public final void set(final String name, final Boolean value) {
        if (mRunTime.hasVariable(mProject, name)) {
            mRunTime.setVariable(mProject, name, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final String value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final Float value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final Integer value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    // Set a record variable via runtime
    public final void set(final String name, final String member, final Boolean value) {
        if (mRunTime.hasVariable(mProject, name, member)) {
            mRunTime.setVariable(mProject, name, member, value);
        } else {
            mLogger.failure("Error: Variable '" + name + "." + member + "' does not exist");
        }
    }

    @Override
    public final void play(final String name, final LinkedList<AbstractValue> args) {
        // Do nothing here ...
    }

    // Handle an SSI event 
    @Override
    public final void handle(final String message) {
        // Print some information
        //mLogger.message("Parsing message " + xml + "");
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
                            if (name.equalsIgnoreCase("voice") || name.equalsIgnoreCase("vad")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    // User stopped speaking
                                    mLogger.message("User stopped speaking");
                                    // Set the variable value
                                    set("UserRoleActivity", "Speaking", false);
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // User started speaking
                                    mLogger.message("User started speaking");
                                    // Set the variable value
                                    set("UserRoleActivity", "Speaking", true);
                                } else {
                                    // Cannot process this
                                }
                            } else if (name.equalsIgnoreCase("speech")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent();
                                        // User said something
                                        mLogger.message("User just said '" + text + "'");
                                        // Set the variable value
                                        set("UserDialogMove", text);
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
                        } else if (mode.equalsIgnoreCase("fsender") || mode.equalsIgnoreCase("fusion")) {
                            if (name.equalsIgnoreCase("fevent") || name.equalsIgnoreCase("fusion")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("ntuple")) {
                                        // Get the list of tuples
                                        final NodeList tuples = element.getElementsByTagName("tuple");
                                        for (int j = 0; j < tuples.getLength(); j++) {
                                            // Get the nex tuple element
                                            final Element tuple = ((Element) tuples.item(j));
                                            // Get the tuple's attributes
                                            final String key = tuple.getAttribute("string");
                                            final Double val = Double.parseDouble(tuple.getAttribute("value"));
                                            // String.format(Locale.US, "%.6f", val)                                            
                                            // Set the variable value
                                            set("UserAffectState", key, val.floatValue());
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
                                        // Print some information
                                        mLogger.message("Language analysis result is\n" + text + "");
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
        }

    }
}
