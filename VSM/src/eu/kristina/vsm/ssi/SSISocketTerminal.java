package eu.kristina.vsm.ssi;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Gregor Mehlmann
 */
public final class SSISocketTerminal extends Thread {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The console reader
    private final BufferedReader mReader;
    // The socket handler
    private final SSISocketHandler mClient;
    // The thread flag
    private volatile boolean mDone = false;

    // Create the terminal thread
    public SSISocketTerminal(
            final String lHost, final Integer lPort,
            final String rHost, final Integer rPort,
            final Boolean rFlag) {
        // Initialize the client
        mClient = new SSISocketHandler(null, lHost, lPort, rHost, rPort, rFlag);
        // Initialize the reader
        mReader = new BufferedReader(
                new InputStreamReader(System.in));
    }

    // Execute the terminal thread
    @Override
    public void run() {
        // Print some information 
        mLogger.message("Starting SSI socket terminal");
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
        mLogger.message("Stopping SSI socket terminal");
    }

    // Execute the terminal thread
    public static void main(final String args[]) {
        // Build the terminal
        final SSISocketTerminal terminal = new SSISocketTerminal(
                args[0], Integer.parseInt(args[1]),
                args[2], Integer.parseInt(args[3]),
                Boolean.parseBoolean(args[4]));
        // Start the terminal
        terminal.start();
    }
}
