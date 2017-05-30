package eu.kristina.vsm.util;

import de.dfki.vsm.util.log.LOGDefaultLogger;

/**
 * @author Gregor Mehlmann
 * @author Ludo Stellingwerff
 */
public final class Timer {

    // The singelton system togger
    private final LOGDefaultLogger mLogger = LOGDefaultLogger.getInstance();
    // The launch startup time
    private volatile long mStartupTime;

    // Construct the system timer
    public Timer() {
        // Set the start time
        reset();
        // Print some Information
        mLogger.message("Creating System Timer");
    }

    // Reset the system timer
    public final synchronized void reset() {
        // Print some Information
        mLogger.message("Resetting System Timer");
        // Set the start time
        mStartupTime = System.currentTimeMillis();
    }

    public final synchronized long time() {
        // Return the sytem time
        return System.currentTimeMillis() - mStartupTime;
    }
}
