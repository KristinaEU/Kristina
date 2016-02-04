package eu.kristina.vsm.ssi;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

/**
 * @author Gregor Mehlmann
 */
public final class SSIServiceTerminal extends Thread {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The console reader
    private final BufferedReader mReader;
    // The SSI rest url
    private final String mSSIServiceURL;
    // The thread flag
    private volatile boolean mDone = false;

    // Create the terminal thread
    public SSIServiceTerminal(final String url) {
        // Initialize the service
        mSSIServiceURL = url;
        // Initialize the reader
        mReader = new BufferedReader(
                new InputStreamReader(System.in));
    }

    // Execute the terminal thread
    @Override
    public void run() {
        // Print some information 
        mLogger.message("Starting SSI service terminal");
        // Process the user commands
        while (!mDone) {
            // Ask the user for a command
            System.err.println("");
            try {
                // Read a command from the console
                final String in = mReader.readLine();
                // Check the user's last command
                if (in != null) {
                    if (in.equals("exit")) {
                        mDone = true;
                    } else if (in.equals("get")) {
                        get(mSSIServiceURL);
                    }
                }
            } catch (final IOException exc) {
                // Do nothing here
            }
        }
        // Print some information 
        mLogger.message("Stopping SSI service terminal");
    }

    // Execute a GET request
    public final void get(final String source) {
        try {
            // Create the URL with the service
            final URL url = new URL(source);
            // Create the URL stream reader 
            final JsonReader reader = Json.createReader(url.openStream());
            // Read a JSON object from URL
            final JsonObject object = reader.readObject();
            // Check the object content
            if (object != null) {
                // Print some information
                mLogger.message(object.toString());
                // Parse the individual fields 
                final JsonNumber jsonArousal = object.getJsonNumber("Arousal");
                final JsonNumber jsonValence = object.getJsonNumber("Valence");
                final JsonString jsonError = object.getJsonString("Error");
                // Check the parsed content
                if (jsonArousal != null && jsonValence != null && jsonError != null) {
                    // Convert the single objects             
                    final float arousal = jsonArousal.bigDecimalValue().floatValue();
                    final float valence = jsonValence.bigDecimalValue().floatValue();
                    final String error = jsonError.getString();
                    // Print some information
                    mLogger.message("Valence:" + valence + " Arousal:" + arousal + " Error:" + error);
                } else {
                    // Print some information
                    mLogger.failure("Error: Cannot parse all features from the SSI service response");
                }
            } else {
                // Print some information
                mLogger.failure("Error: Cannot parse JSON object from the SSI service response");
            }
            // Set the according variables ...
        } catch (final IOException exc) {
            mLogger.failure(exc.toString());
        }
    }

    // Execute the terminal thread
    public static void main(final String args[]) {
        // Build the terminal
        final SSIServiceTerminal control = new SSIServiceTerminal(args[0]);
        // Start the terminal
        control.start();
    }
}
