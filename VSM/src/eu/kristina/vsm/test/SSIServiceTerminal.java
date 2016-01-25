package eu.kristina.vsm.test;

import de.dfki.vsm.util.log.LOGConsoleLogger;
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
    private final LOGConsoleLogger mLogger
            = LOGConsoleLogger.getInstance();
    // The console reader
    private final BufferedReader mReader;
    //
    private final String mSSIServiceURL;
    // The thread flag
    private volatile boolean mDone = false;

    public SSIServiceTerminal(final String url) {
        // Initialize the service
        mSSIServiceURL = url;
        // Initialize the reader
        mReader = new BufferedReader(
                new InputStreamReader(System.in));
    }

    // Run the control terminal
    @Override
    public void run() {
        while (!mDone) {
            // Ask the user for a command
            System.err.println("Enter Command ...");
            try {
                // Read a command from the console
                final String in = mReader.readLine();
                // Check the user's last command
                System.err.println("Your Command Is '" + in + "'");
                if (in != null) {
                    if (in.equals("exit")) {
                        mDone = true;
                    } else if (in.equals("get")) {
                        get(mSSIServiceURL);
                    } else {

                    }
                }
            } catch (final IOException exc) {
                // Do nothing
            }
        }
    }

    public final void get(final String source) {
        try {
            // Create the URL with the service
            final URL url = new URL(source);
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
            mLogger.message("Valence:" + valence + " Arousal:" + arousal + " Error:" + error);
            // Set the according variables ...
        } catch (final IOException exc) {
            mLogger.failure(exc.toString());
        }
    }

    public static void main(final String args[]) {
        // Build terminal
        final SSIServiceTerminal control = new SSIServiceTerminal(args[0]);
        // Start terminal
        control.start();
    }
}
