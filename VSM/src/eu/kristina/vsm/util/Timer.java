package eu.kristina.vsm.util;

import de.dfki.vsm.util.log.LOGDefaultLogger;

/**
 * @author Gregor Mehlmann
 */
public final class Timer extends Thread {

    // The singelton system togger
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The thread termination flag
    private volatile boolean mDone = false;
    // The timer wait interval
    private final long mTimerInterval;
    // The launch startup time
    private volatile long mStartupTime;
    // The current player time
    private volatile long mCurrentTime;

    // Construct the system timer
    public Timer(final long interval) {
        super("Timer");
        // Initialize the interval
        mTimerInterval = interval;
        // Print some Information
        mLogger.message("Creating System Timer");
    }

    // Reset the system timer
    public final void reset() {
        // Print some Information
        mLogger.message("Resetting System Timer");
        // Set the start time
        mStartupTime = System.currentTimeMillis();
    }

    // Abort the system timer
    public final void abort() {
        // Print some Information
        mLogger.message("Aborting System Timer");
        // Set termination flag
        mDone = true;
        // Interrupt thread state
        interrupt();
    }

    public final long time() {
        // Reurn the JPL time
        return mCurrentTime;
    }

    // Execute the system timer
    @Override
    public final void run() {
        // Print some information
        mLogger.message("Starting System Timer");
        // Set the start time
        reset();
        // Then update the time
        while (!mDone) {
            // Sleep for some very short time
            try {
                // Eventually change interval 
                Thread.sleep(mTimerInterval);
            } catch (final InterruptedException exc) {
                // Print some information
                mLogger.warning(exc.toString());
                // Exit on an interrupt
                mDone = true;
            }
            // Update the player time
            mCurrentTime
                    = System.currentTimeMillis() - mStartupTime;
        }
        // Print some information
        mLogger.message("Stopping System Timer");
    }
}
