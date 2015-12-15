package eu.kristina.eca;

import de.dfki.vsm.util.log.LOGConsoleLogger;

/**
 * @author Gregor Mehlmann
 */
public final class ECABehaviorManager {

    // The singelton logger instance
    private final static LOGConsoleLogger sLogger
            = LOGConsoleLogger.getInstance();

    public static void main(final String args[]) {
        // Build client
        final ECACommandClient client = new ECACommandClient(
                args[0], Integer.parseInt(args[1]));
        // Start client
        client.start();

        // Build terminal
        final ECAControlTerminal control = new ECAControlTerminal(client);
        // Start terminal
        control.start();
    }
}
