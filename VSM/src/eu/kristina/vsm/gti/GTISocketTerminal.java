package eu.kristina.vsm.gti;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Gregor Mehlmann
 */
public final class GTISocketTerminal extends Thread {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The request client
    private final GTISocketHandler mClient;
    // The console reader
    private final BufferedReader mReader;
    // The thread flag
    private volatile boolean mDone = false;

    // Create the terminal thread
    public GTISocketTerminal(final String host, final int port) {
        // Initialize the client
        mClient = new GTISocketHandler(null, host, port);
        // Initialize the reader
        mReader = new BufferedReader(
                new InputStreamReader(System.in));
    }

    // Execute the terminal thread
    @Override
    public void run() {
        // Print some information 
        mLogger.message("Starting ECA socket terminal");
        // Start the socket handler
        mClient.start();
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
                    } else {
                        mClient.send(in);
                    }
                }
            } catch (final IOException exc) {
                // Do nothing here
            }
        }
        // Abort and join socket
        try {
            mClient.abort();
            mClient.join();
        } catch (final InterruptedException exc) {
            mLogger.warning(exc.toString());
        }
        // Print some information 
        mLogger.message("Stopping ECA socket terminal");
    }

    // Execute the terminal thread
    public static void main(final String args[]) {
        // Build the terminal
        final GTISocketTerminal control = new GTISocketTerminal(
                args[0], Integer.parseInt(args[1]));
        // Start the terminal
        control.start();
    }
}
