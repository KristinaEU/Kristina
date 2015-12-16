package eu.kristina;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGConsoleLogger;
import eu.kristina.eca.ECACommandClient;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

/**
 * @author Gregor Mehlmann
 */
public final class KristinaScenePlayer implements RunTimePlayer {

    // The singelton player instance
    public static KristinaScenePlayer sInstance = null;
    // The runtime environment
    private final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The defaut system logger
    private final LOGConsoleLogger mLogger
            = LOGConsoleLogger.getInstance();
    // The quue of waiting tasks
    private final HashMap<String, Thread> mTaskQueue
            = new HashMap<>();
    // The ECA command client
    private ECACommandClient mClient;
    //
    private final Random mRandom = new Random();
    // The player's runtime project 
    private RunTimeProject mProject;
    // The project specific config
    private PlayerConfig mPlayerConfig;
    // The project specific name
    private String mPlayerName;

    private KristinaScenePlayer() {
    }

    public static synchronized KristinaScenePlayer getInstance() {
        if (sInstance == null) {
            sInstance = new KristinaScenePlayer();
        }
        return sInstance;
    }

    @Override
    public final boolean launch(final RunTimeProject project) {
        // Initialize the project
        mProject = project;
        // Initialize the name
        mPlayerName = project.getPlayerName(this);
        // Initialize the config
        mPlayerConfig = project.getPlayerConfig(mPlayerName);
        // Initialize the client
        mClient = new ECACommandClient("webglstudio.org", 9900);
        //
        mClient.start();
        //
        mClient.init();
        // Print some information
        mLogger.message("Launching scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    @Override
    public final boolean unload() {
        // Print some information
        mLogger.message("Unloading scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        //
        mClient.abort();
        //
        try {
            mClient.join();

        } catch (final InterruptedException exc) {
            mLogger.failure(exc.toString());
        }
        // Return true at success
        return true;
    }

    @Override
    public final void play(final String name, final LinkedList<AbstractValue> args) {

    }

    public final void blink() {
        mClient.send("blink");
    }

    public final void face(
            final String clientid,
            final float activation,
            final float evaluation) {
        mClient.send(clientid + " " + "face" + " " + activation + " " + evaluation);
    }
    
     public final void text(
            final String clientid,
            final String ttstext) {
        mClient.send(clientid + " " + "text" + " " + ttstext);
    }

    public final float rand() {
        return mRandom.nextFloat();
    }
}
