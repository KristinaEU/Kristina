package eu.kristina;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGConsoleLogger;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Gregor Mehlmann
 */
public final class KristinaScenePlayer implements RunTimePlayer {

    // The singelton player instance
    public static KristinaScenePlayer sInstance = null;
    // The runtime environment
    protected final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The defaut system logger
    protected final LOGConsoleLogger mLogger
            = LOGConsoleLogger.getInstance();
    // The quue of waiting tasks
    protected final HashMap<String, Thread> mTaskQueue
            = new HashMap<>();
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
        // Print some information
        mLogger.message("Launching scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    @Override
    public final boolean unload() {
        // Print some information
        mLogger.message("Unloading scene player '" + this + "' with configuration:\n" + mPlayerConfig);
        // Return true at success
        return true;
    }

    @Override
    public final void play(final String name, final LinkedList<AbstractValue> args) {

    }
}
