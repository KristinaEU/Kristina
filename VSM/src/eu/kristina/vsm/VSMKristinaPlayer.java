package eu.kristina.vsm;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.gti.GTISocketHandler;
import eu.kristina.vsm.owl.OWLSocketHandler;
import eu.kristina.vsm.ssi.SSISocketHandler;
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
    // The player's runtime project 
    private RunTimeProject mProject;
    // The project's specific config
    private PlayerConfig mPlayerConfig;
    // The project's specific name
    private String mPlayerName;
    // The SSI rest service url
    private String mSSIRestServiceURL;
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
    // The OWL socket handler
    private String mOWLSocketLocalHost;
    private Integer mOWLSocketLocalPort;
    private String mOWLSocketRemoteHost;
    private Integer mOWLSocketRemotePort;
    private Boolean mOWLSocketRemoteFlag;
    private OWLSocketHandler mOWLSocket;
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

        // Get the SSI rest service
        mSSIRestServiceURL = mPlayerConfig.getProperty("ssi.rest.service.url");

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

        // Get the OWL configuration    
        mOWLSocketLocalHost = mPlayerConfig.getProperty("owl.socket.local.host");
        mOWLSocketLocalPort = Integer.parseInt(
                mPlayerConfig.getProperty("owl.socket.local.port"));
        mOWLSocketRemoteHost = mPlayerConfig.getProperty("owl.socket.remote.host");
        mOWLSocketRemotePort = Integer.parseInt(
                mPlayerConfig.getProperty("owl.socket.remote.port"));
        mOWLSocketRemoteFlag = Boolean.parseBoolean(
                mPlayerConfig.getProperty("owl.socket.remote.flag"));

        // Print some information
        mLogger.message(""
                + "SSI Rest Service URL : '" + mSSIRestServiceURL + "'" + "\r\n"
                + "ECA Socket Handler Remote Host : '" + mECASocketRemoteHost + "'" + "\r\n"
                + "ECA Socket Handler Remote Port : '" + mECASocketRemotePort + "'" + "\r\n"
                + "SSI Socket Handler Local Host  : '" + mSSISocketLocalHost + "'" + "\r\n"
                + "SSI Socket Handler Local Port  : '" + mSSISocketLocalPort + "'" + "\r\n"
                + "SSI Socket Handler Remote Host : '" + mSSISocketRemoteHost + "'" + "\r\n"
                + "SSI Socket Handler Remote Port : '" + mSSISocketRemotePort + "'" + "\r\n"
                + "SSI Socket Handler Remote Flag : '" + mSSISocketRemoteFlag + "'" + "\r\n"
                + "OWL Socket Handler Local Host  : '" + mOWLSocketLocalHost + "'" + "\r\n"
                + "OWL Socket Handler Local Port  : '" + mOWLSocketLocalPort + "'" + "\r\n"
                + "OWL Socket Handler Remote Host : '" + mOWLSocketRemoteHost + "'" + "\r\n"
                + "OWL Socket Handler Remote Port : '" + mOWLSocketRemotePort + "'" + "\r\n"
                + "OWL Socket Handler Remote Flag : '" + mOWLSocketRemoteFlag + "'" + "\r\n");

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

        // Initialize the OWL socket
        mOWLSocket = new OWLSocketHandler(this,
                mOWLSocketLocalHost, mOWLSocketLocalPort,
                mOWLSocketRemoteHost, mOWLSocketRemotePort,
                mOWLSocketRemoteFlag);
        //mOWLSocket.start();

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
        mOWLSocket.abort();
        mSSISocket.abort();
        // Join with the handlers        
        try {
            mECASocket.join();
            mOWLSocket.join();
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
