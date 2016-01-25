package eu.kristina.vsm.test;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.net.httpserver.HttpServer;
import de.dfki.vsm.editor.EditorInstance;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.util.log.LOGConsoleLogger;
import java.io.File;
import javax.swing.SwingUtilities;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Gregor Mehlmann
 */
@Path("vsm")
public class VSMControlService {

    // The logger instance
    private static final LOGConsoleLogger sLogger
            = LOGConsoleLogger.getInstance();
    // The runtime instance
    private static final RunTimeInstance sRunTime
            = RunTimeInstance.getInstance();
    // The project instance
    private static final RunTimeProject sProject
            = new RunTimeProject(new File("res/prj/vsm"));

    @GET
    @Path("{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized String vsm(@PathParam("key") final String key) {
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
    public synchronized String get(
            @PathParam("key") final String key) {
        //
        return sRunTime.getValueOf(sProject, key).getConcreteSyntax();

    }

    @GET
    @Path("set/{key}/{val}")
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized String set(
            @PathParam("key") final String key,
            @PathParam("val") final String val) {
        //
        return String.valueOf(sRunTime.setVariable(sProject, key, val));

    }

    private String play() {
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

    private String stop() {
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
        // The termination flag
        boolean done = false;
        // Start the HTTP server
        try {
            // Create the server
            final HttpServer server = HttpServerFactory.create("http://localhost:8080/");
            // Start the server
            server.start();
            //
            while (!done) {

            }
            // Abort the server
            server.stop(0);
        } catch (final Exception exc) {
            System.err.println(exc.toString());
        }
    }
}
