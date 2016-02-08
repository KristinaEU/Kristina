package eu.kristina.vsm;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.VSMRestFulClient.Resource;
import eu.kristina.vsm.ssi.SSISocketHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author Gregor Mehlmann
 */
public final class VSMKristinaPlayer implements RunTimePlayer {

    // The singelton player instance
    public static VSMKristinaPlayer sInstance = null;
    // The singelton logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The VSM runtime environment
    private final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The  rest service urls
    private final HashMap<String, Resource> mResourceMap = new HashMap();
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
    private SSISocketHandler mSSISocket;
    // The rest service client
    private VSMRestFulClient mRestClient;
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
            final Resource resource = new Resource(host, name, path, cons, prod);
            // Print some information
            mLogger.message("Registering RESTful service resource '" + resource + "'" + "\r\n");
            // Add the new service then
            mResourceMap.put(name, resource);
        }
        // Get the SSI configuration
        mSSISocketLocalHost = mPlayerConfig.getProperty("ssi.socket.local.host");
        mSSISocketLocalPort = Integer.parseInt(
                mPlayerConfig.getProperty("ssi.socket.local.port"));
        mSSISocketRemoteHost = mPlayerConfig.getProperty("ssi.socket.remote.host");
        mSSISocketRemotePort = Integer.parseInt(
                mPlayerConfig.getProperty("ssi.socket.remote.port"));
        mSSISocketRemoteFlag = Boolean.parseBoolean(
                mPlayerConfig.getProperty("ssi.socket.remote.flag"));

        // Print some information
        mLogger.message(""
                + "SSI Socket Handler Local Host  : '" + mSSISocketLocalHost + "'" + "\r\n"
                + "SSI Socket Handler Local Port  : '" + mSSISocketLocalPort + "'" + "\r\n"
                + "SSI Socket Handler Remote Host : '" + mSSISocketRemoteHost + "'" + "\r\n"
                + "SSI Socket Handler Remote Port : '" + mSSISocketRemotePort + "'" + "\r\n"
                + "SSI Socket Handler Remote Flag : '" + mSSISocketRemoteFlag + "'" + "\r\n");

        // Initialize the SSI socket
        mSSISocket = new SSISocketHandler(this,
                mSSISocketLocalHost, mSSISocketLocalPort,
                mSSISocketRemoteHost, mSSISocketRemotePort,
                mSSISocketRemoteFlag);
        mSSISocket.start();
        // Initialize the rest client
        mRestClient = new VSMRestFulClient(this);
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

    public final synchronized void blink() {
        // Print some information
        mLogger.message("Executing blink command");
        // Get the resource
        final Resource resource = mResourceMap.get("Character-Engine");
        // Print some information
        mLogger.message("Resource is\n'" + resource + "'");
        // Get the command
        final String command = VSMActionFactory.newBlinkAction();
        // Print some information
        mLogger.message("Command is\n'" + command + "'");
        // Get the query data
        final String query = "?id=hcm";
        // Execute POST request
        mRestClient.post(resource, query, command);
        //
    }

    public final synchronized void face(
            final float valence,
            final float arousal) {
        // Print some information
        mLogger.message("Executing face command");
        // Get the resource
        final Resource resource = mResourceMap.get("Character-Engine");
        // Print some information
        mLogger.message("Resource is\n'" + resource + "'");
        // Get the command
        final String command = VSMActionFactory.newFaceAction(valence, arousal);
        // Print some information
        mLogger.message("Command is\n'" + command + "'");
        // Get the query data
        final String query = "?id=hcm";
        // Execute POST request
        mRestClient.post(resource, query, command);
        //
    }
    
    public final synchronized void voice() {
        // Print some information
        mLogger.message("Executing voice command");
        // Get the resource
        final Resource resource = mResourceMap.get("Character-Engine");
        // Print some information
        mLogger.message("Resource is\n'" + resource + "'");
        // Get the command
        final String command = VSMActionFactory.newVoiceAction();
        // Print some information
        mLogger.message("Command is\n'" + command + "'");
        // Get the query data
        final String query = "?id=hcm";
        // Execute POST request
        mRestClient.post(resource, query, command);
        //
    }

    public final float randFloat() {
        return 2 * (mRandom.nextFloat() - 0.5f);
    }

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

}
