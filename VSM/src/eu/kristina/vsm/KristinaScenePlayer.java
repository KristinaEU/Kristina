package eu.kristina.vsm;

import de.dfki.vsm.model.project.PlayerConfig;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.values.AbstractValue;
import de.dfki.vsm.runtime.players.RunTimePlayer;
import de.dfki.vsm.util.log.LOGConsoleLogger;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.eca.ECACommandClient;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

/**
 * @author Gregor Mehlmann
 */
public final class KristinaScenePlayer implements RunTimePlayer {

    // The player instance
    public static KristinaScenePlayer sInstance = null;
    // The system logger
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The runtime environment
    private final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The player's runtime project 
    private RunTimeProject mProject;
    // The project specific config
    private PlayerConfig mPlayerConfig;
    // The project specific name
    private String mPlayerName;
    // The ECA command client
    private ECACommandClient mClient;
    // The SSI service url
    private String mSSIServiceURL;
    // Random number generator
    private final Random mRandom = new Random();
    // The queue of waiting tasks
    private final HashMap<String, Thread> mTaskQueue
            = new HashMap<>();

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
        // Initialize service url
        mSSIServiceURL = mPlayerConfig.getProperty("ssi.service.url");
        // Initialize the client
        mClient = new ECACommandClient("webglstudio.org", 9900);
        // Start the client
        mClient.start();
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
        // Do nothing here ...
    }

    public final void blink(final String clientid) {
        mClient.send(clientid + " " + "blink");
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

    public final float randFloat() {
        return mRandom.nextFloat();
    }

    public final int randInt(final int bound) {
        return mRandom.nextInt(bound);
    }

    public final void get() {
        try {
            // Create the URL with the service
            final URL url = new URL(mSSIServiceURL);
            // Create the URL stream reader 
            final JsonReader reader = Json.createReader(url.openStream());
            // Read a JSON object from URL
            final JsonObject object = reader.readObject();
            // Parse the individual fields 
            final JsonNumber jsonArousal = object.getJsonNumber("Arousal");
            final JsonNumber jsonValence = object.getJsonNumber("Valence");
            final JsonString jsonError = object.getJsonString("Error");
            // Convert the single objects             
            final float arousal = jsonArousal.bigDecimalValue().floatValue();
            final float valence = jsonValence.bigDecimalValue().floatValue();
            final String error = jsonError.getString();
            // Print some information
            //mLogger.message("Valence:" + valence + " Arousal:" + arousal + " Error:" + error);
            // Set according variables
            mRunTime.setVariable(mProject, "Valence", valence);
            mRunTime.setVariable(mProject, "Arousal", arousal);
            mRunTime.setVariable(mProject, "Error", error);

        } catch (final IOException exc) {
            mLogger.failure(exc.toString());
        }
    }

}
