package eu.kristina.vsm;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.VSMRestFulClient.Service;
import eu.kristina.vsm.gti.GTISocketHandler;
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
    private final HashMap mServiceURLMap
            = new HashMap<String, Service>();
    // The player's runtime project 
    private RunTimeProject mProject;
    // The project's specific config
    private PlayerConfig mPlayerConfig;
    // The project's specific name
    private String mPlayerName;
    // The ECA socket handler
    private String mECASocketRemoteHost;
    private Integer mECASocketRemotePort;
    private GTISocketHandler mECASocket;
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
        final int count = Integer.parseInt(mPlayerConfig.getProperty("restful.service.count"));
        for (int i = 0; i < count; i++) {
            final String host = mPlayerConfig.getProperty("restful.service." + i + ".host");
            final String name = mPlayerConfig.getProperty("restful.service." + i + ".name");
            final String url = mPlayerConfig.getProperty("restful.service." + i + ".url");
            final String in = mPlayerConfig.getProperty("restful.service." + i + ".in");
            final String out = mPlayerConfig.getProperty("restful.service." + i + ".out");
            // Create the service data
            final Service service = new Service(host, name, url, in, out);
            // Add the new service then
            mServiceURLMap.put(url, service);
            // Print some information
            mLogger.message("Registering restful service '" + service + "'" + "\r\n");
        }
        // Get the ECA configuration
        mECASocketRemoteHost = mPlayerConfig.getProperty("eca.socket.remote.host");
        mECASocketRemotePort = Integer.parseInt(
                mPlayerConfig.getProperty("eca.socket.remote.port"));

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
                + "ECA Socket Handler Remote Host : '" + mECASocketRemoteHost + "'" + "\r\n"
                + "ECA Socket Handler Remote Port : '" + mECASocketRemotePort + "'" + "\r\n"
                + "SSI Socket Handler Local Host  : '" + mSSISocketLocalHost + "'" + "\r\n"
                + "SSI Socket Handler Local Port  : '" + mSSISocketLocalPort + "'" + "\r\n"
                + "SSI Socket Handler Remote Host : '" + mSSISocketRemoteHost + "'" + "\r\n"
                + "SSI Socket Handler Remote Port : '" + mSSISocketRemotePort + "'" + "\r\n"
                + "SSI Socket Handler Remote Flag : '" + mSSISocketRemoteFlag + "'" + "\r\n");

        // Initialize the ECA socket
        mECASocket = new GTISocketHandler(
                this, mECASocketRemoteHost, mECASocketRemotePort);
        mECASocket.start();

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
        mECASocket.abort();
        mSSISocket.abort();
        // Join with the handlers        
        try {
            mECASocket.join();
            mSSISocket.join();
        } catch (final InterruptedException exc) {
            mLogger.failure(exc.toString());
        }
        // Print some information
        mLogger.message("Unloading KRISTINA scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    public final void blink(final String clientid) {
        mECASocket.send(clientid + " " + "blink");
    }

    public final void face(
            final String clientid,
            final float activation,
            final float evaluation) {
        mECASocket.send(clientid + " " + "face" + " " + activation + " " + evaluation);
    }

    public final void text(
            final String clientid,
            final String ttstext) {
        mECASocket.send(clientid + " " + "text" + " " + ttstext);
    }

    public final float randFloat() {
        return mRandom.nextFloat();
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
