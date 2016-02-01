package eu.kristina.vsm;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Gregor Mehlmann
 */
public final class KristinaServicePool {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The runtime instance
    private final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The scene player object
    private final KristinaScenePlayer mPlayer;
    // The http rest server
    private HttpServer mServer;
    // The Executors service
    private ExecutorService mPool;

    // Create the handler thread
    public KristinaServicePool(final KristinaScenePlayer player) {
        // Initialize the player
        mPlayer = player;

    }

    // Start the HTTP server
    public final void start() {
        try {
            // Create the server
            final HttpServer server = HttpServerFactory.create("");
            // Get the executors
            final ExecutorService pool = Executors.newFixedThreadPool(1);
            // Set the executors
            server.setExecutor(pool);
            // Start the server
            server.start();
        } catch (final Exception exc) {
            exc.printStackTrace();
        }

    }

    public void abort() {
        // Kill the executor
        mPool.shutdownNow();
        // Abort the server
        mServer.stop(0);
        // Print information
        mLogger.message("Aborting VSM service server");
    }

}
