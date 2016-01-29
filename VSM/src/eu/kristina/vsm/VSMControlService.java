package eu.kristina.vsm;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import de.dfki.vsm.Preferences;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Gregor Mehlmann
 */
@Path("vsm")
public final class VSMControlService {

    // The logger instance
    private static final LOGDefaultLogger sLogger
            = LOGDefaultLogger.getInstance();
    // The runtime instance
    private static final RunTimeInstance sRunTime
            = RunTimeInstance.getInstance();
    // The project instance
    private static final RunTimeProject sProject
            = new RunTimeProject(new File("res/prj/vsm"));
    
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public static synchronized String vsm() {
        return String.valueOf(sRunTime.isRunning(sProject));
    }
    
    @GET
    @Path("{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public static synchronized String run(@PathParam("key") final String key) {
        if (key.equals("play")) {
            return play();
        } else if (key.equals("stop")) {
            return stop();
        } else {
            return null;
        }
    }
    
    @GET
    @Path("get/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public static synchronized String get(
            @PathParam("key") final String key) {
        //
        return sRunTime.getValueOf(sProject, key).getConcreteSyntax();
        
    }
    
    @GET
    @Path("set/{key}/{val}")
    @Produces(MediaType.TEXT_PLAIN)
    public static synchronized String set(
            @PathParam("key") final String key,
            @PathParam("val") final String val) {
        //
        return String.valueOf(sRunTime.setVariable(sProject, key, val));
        
    }
    
    @GET
    @Path("log")
    @Produces(MediaType.TEXT_PLAIN)
    public static synchronized String log() throws IOException {
        return new String(Files.readAllBytes(Paths.get(Preferences.sLOGFILE_FILE_NAME)));
    }
    
    private static String play() {
        if (sRunTime.launch(sProject)) {
            if (sRunTime.start(sProject)) {
                return "Starting project '" + sProject + "'";
            } else {
                return "Cannot start project";
            }
        } else {
            return "Cannot launch project";
        }
    }
    
    private static String stop() {
        if (sRunTime.abort(sProject)) {
            if (sRunTime.unload(sProject)) {
                return "Stopping project '" + sProject + "'";
            } else {
                return "Cannot unload project";
            }
        } else {
            return "Cannot abort project";
        }
    }
    
    public static void main(final String args[]) {
        // Initialize the reader
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
        // The termination flag
        boolean done = false;
        // Start the HTTP server
        try {
            // Create the server
            final HttpServer server = HttpServerFactory.create(args[0]);
            // Get the executors
            final ExecutorService pool = Executors.newFixedThreadPool(1);
            // Set the executors
            server.setExecutor(pool);
            // Start the server
            server.start();
            // Accept commands
            while (!done) {
                try {
                    // Read a command from the console
                    final String in = reader.readLine();
                    // Check the user's last command
                    if (in != null) {
                        if (in.equals("exit")) {
                            // Stop the project
                            stop();
                            // Abort the server
                            done = true;
                        } else if (in.equals("play")) {
                            // Play the project
                            play();
                        } else if (in.equals("stop")) {
                            // Stop the project
                            stop();
                        }
                    }
                } catch (final IOException exc) {
                    // Do nothing here
                }
            }
            // Kill the executor
            pool.shutdownNow();
            // Abort the server
            server.stop(0);
            // Print information
            sLogger.message("Aborting VSM control server");
        } catch (final Exception exc) {
            System.err.println(exc.toString());
        }
    }
}
