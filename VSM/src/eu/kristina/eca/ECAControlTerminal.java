package eu.kristina.eca;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Gregor Mehlmann
 */
public class ECAControlTerminal extends Thread {

    // The request client
    private final ECACommandClient mClient;
    // The thread flag
    private volatile boolean mDone = false;
    // The console reader
    private final BufferedReader mReader = new BufferedReader(new InputStreamReader(System.in));

    public ECAControlTerminal(final ECACommandClient client) {
        mClient = client;
    }

    // Await some input then
    @Override
    public void run() {
        while (!mDone) {
            // Wait until user aborts
            System.err.println("Enter Command ...");
            try {
                final String in = mReader.readLine();
                // Wait until user aborts
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
        // Print some information
        System.err.println("Stopping control thread");
    }
}
