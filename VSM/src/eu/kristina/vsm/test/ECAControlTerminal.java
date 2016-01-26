package eu.kristina.vsm.test;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.eca.ECASocketHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Gregor Mehlmann
 */
public final class ECAControlTerminal extends Thread {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The request client
    private final ECASocketHandler mClient;
    // The console reader
    private final BufferedReader mReader;
    // The thread flag
    private volatile boolean mDone = false;

    public ECAControlTerminal(final String host, final int port) {
        // Initialize the client
        mClient = new ECASocketHandler(host, port);
        // Initialize the reader
        mReader = new BufferedReader(
                new InputStreamReader(System.in));
    }

    // Run the control terminal
    @Override
    public void run() {

        // Start client
        mClient.start();
        mClient.init();
        //
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
                    } else {
                        mClient.send(in);
                    }
                }
            } catch (final IOException exc) {
                // Do nothing
            }
        }
        // 
        System.err.println("Stopping control terminal");
        //
        try {
            mClient.abort();
            mClient.join();
        } catch (final InterruptedException exc) {
            mLogger.warning(exc.toString());
        }
    }

    public static void main(final String args[]) {
        // Build terminal
        final ECAControlTerminal control = new ECAControlTerminal(
                args[0], Integer.parseInt(args[1]));
        // Start terminal
        control.start();
    }
}
