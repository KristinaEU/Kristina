package eu.kristina.vsm.ssi;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Gregor Mehlmann
 */
public final class SSIHandlerTerminal extends Thread implements SSIEventHandler {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The console reader
    private final BufferedReader mReader;
    // The socket handler
    private final SSIEventListener mListener;
    // The thread flag
    private volatile boolean mDone = false;

    // Create the terminal thread
    public SSIHandlerTerminal(
            final String lHost, final Integer lPort,
            final String rHost, final Integer rPort,
            final Boolean rFlag) {
        // Initialize the client
        mListener = new SSIEventListener(this, lHost, lPort, rHost, rPort, rFlag);
        // Initialize the reader
        mReader = new BufferedReader(new InputStreamReader(System.in));
        // Print some information 
        mLogger.message("Creating SSI handler terminal");
    }

    // Execute the terminal thread
    @Override
    public void run() {
        // Print some information 
        mLogger.message("Starting SSI handler terminal");
        // Start the socket handler
        mListener.start();
        // Process the user commands
        while (!mDone) {
            // Ask the user for command
            System.err.println("");
            try {
                // Read a command from user
                final String in = mReader.readLine();
                // Check the user's command
                if (in != null && in.equals("exit")) {
                    // Abort terminal thread 
                    mDone = true;
                    // Print some information 
                    mLogger.message("Aborting SSI handler terminal");
                }
            } catch (final IOException exc) {
                // Print some information 
                mLogger.warning(exc.toString());
            }
        }
        // Abort and join listener
        try {
            mListener.abort();
            mListener.join();
        } catch (final InterruptedException exc) {
            mLogger.warning(exc.toString());
        }
        // Print some information 
        mLogger.message("Stopping SSI handler terminal");
    }

    @Override
    public void handle(final String event) {
        // Print some information 
        mLogger.message(event);
    }

    // Execute the terminal thread
    public static void main(final String args[]) {
        // Build the terminal
        final SSIHandlerTerminal terminal = new SSIHandlerTerminal(
                args[0], Integer.parseInt(args[1]),
                args[2], Integer.parseInt(args[3]),
                Boolean.parseBoolean(args[4]));
        // Start the terminal
        terminal.start();
    }
}
