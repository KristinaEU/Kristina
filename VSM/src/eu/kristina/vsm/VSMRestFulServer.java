package eu.kristina.vsm;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.net.httpserver.HttpServer;
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
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Gregor Mehlmann
 */
@Singleton
@Path("")
public final class VSMRestFulServer {

    // The logger instance
    private static final LOGDefaultLogger sLogger
            = LOGDefaultLogger.getInstance();
    // The runtime instance
    private static final RunTimeInstance sRunTime
            = RunTimeInstance.getInstance();
    // The project instance
    private static RunTimeProject sProject = null;

    /**
     * Load the VSM runtime project instance for KRISTINA
     *
     * @return A boolean flag indicating success or failure
     */
    private static boolean load(final String filename) {
        // Create the project file
        final File file = new File(filename);
        // Check if the file exists
        if (file.exists() && file.isDirectory()) {
            // Get the runtime project
            sProject = new RunTimeProject(new File(filename));
            // Load the runtime project
            if (sProject.load()) {
                // Print some information
                sLogger.message("Success: Loading VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                // Return true at success
                return true;
            } else {
                // Print some information
                sLogger.message("Failure: Cannot load VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                // Return false at failure
                return false;
            }
        } else {
            // Print some information
            sLogger.message("Failure: Cannot fine VSM runtime project base directory '" + file + "'");
            // Return false at failure
            return false;
        }
    }

    /**
     * Start the VSM runtime project instance for KRISTINA
     *
     * @return A boolean flag indicating success or failure
     */
    private static boolean start() {
        if (sRunTime != null) {
            if (sRunTime.launch(sProject)) {
                if (sRunTime.start(sProject)) {
                    // Print some information
                    sLogger.message("Success: Starting VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                    // Return true at success
                    return true;
                } else {
                    // Print some information
                    sLogger.message("Failure: Cannot start VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                    // Return false at failure
                    return false;
                }
            } else {
                // Print some information
                sLogger.message("Failure: Cannot launch VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                // Return false at failure
                return false;
            }
        } else {
            // Print some information
            sLogger.message("Failure: There is no VSM runtime project for KRISTINA loaded yet");
            // Return false at failure
            return false;
        }
    }

    /**
     * Stop the VSM runtime project instance for KRISTINA
     *
     * @return A boolean flag indicating success or failure
     */
    private static boolean stop() {
        if (sRunTime != null) {
            if (sRunTime.abort(sProject)) {
                if (sRunTime.unload(sProject)) {
                    // Print some information
                    sLogger.message("Success: Stopping VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                    // Return true at success
                    return true;
                } else {
                    // Print some information
                    sLogger.message("Failure: Cannot unload VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                    // Return false at failure
                    return false;
                }
            } else {
                // Print some information
                sLogger.message("Failure: Cannot stop VSM runtime project '" + sProject.getProjectName() + "' for KRISTINA");
                // Return false at failure
                return false;
            }
        } else {
            // Print some information
            sLogger.message("Failure: There is no VSM runtime project for KRISTINA loaded yet");
            // Return false at failure
            return false;
        }
    }

    /**
     * Produce the HTTP homepage of the server
     *
     * @return The HTTP main navigation menu of the server
     */
    private static String http() {
        try {
            // Get the CSS style definitions
            final String style = new String(Files.readAllBytes(Paths.get("res/css/styles.css")));
            // 
            return "<!DOCTYPE html>\n"
                    + "<head>\n"
                    + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n"
                    + "<title>Kristina Project Demonstrator - Visual Scene Maker Service</title>\n"
                    + "<style>\n"
                    + style
                    + "</style>"
                    + "</head>\n"
                    + "<html>\n"
                    + "<body>\n"
                    + "<div id=\"container\">"
                    + "<header>\n"
                    + "  <div class=\"width\">\n"
                    + "    <div>\n"
                    + "      <h1>\n"
                    + "        <a href=\"/\"><span>KRISTINA</span> Project Demonstrator</a>\n"
                    + "      </h1>\n"
                    + "      <h1>\n"
                    + "        <a href=\"/\">Visual <span>SceneMaker</span> Control Service</a>\n"
                    + "      </h1>\n"
                    + "    </div>\n"
                    + "  </div>\n"
                    + "</header>"
                    + "<nav>\n"
                    + "  <div  class=\"width\">\n"
                    + "    <ul>\n"
                    + "                <li class=\"start selected\"><a href=\"index.html\">Home</a></li>\n"
                    + "                <li class=\"\"><a href=\"getstarted.html\">Get Started!</a></li>\n"
                    + "                <li class=\"\"><a href=\"tutorial.html\">Tutorials</a></li>\n"
                    + "                <li class=\"\"><a href=\"faq.html\">FAQ</a></li>\n"
                    + "                <li class=\"\"><a href=\"downloads.html\">Download</a></li>\n"
                    + "                <li class=\"\"><a href=\"projects.html\">Projects</a></li>\n"
                    + "                <li class=\"\"><a href=\"contact.html\">Contact</a></li>\n"
                    + "            </ul>\n"
                    + "        </div>\n"
                    + "    </nav>  "
                    + "</div>"
                    + "</body>\n"
                    + "</html>";
        } catch (final IOException exc) {
            // Print some information
            sLogger.failure(exc.toString());
            // Return null at failure
            return null;
        }
    }

    public static void main(final String args[]) {
        // Print information
        System.out.println("Initializing VSM KRISTINA restful server");
        // Initialize the reader
        final BufferedReader reader = new BufferedReader(
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
                            //service.stop();
                            // Abort the server
                            done = true;
                        } else if (in.equals("load")) {
                            // Play the project
                            //service.load();
                        } else if (in.equals("start")) {
                            // Play the project
                            //service.start();
                        } else if (in.equals("stop")) {
                            // Stop the project
                            //service.stop();
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
        } catch (final Exception exc) {
            System.err.println(exc.toString());
        }
        // Print information
        System.out.println("Terminating VSM KRISTINA restful server");
    }

    /**
     * Construct the RESTful VSM server object of KRISTINA
     */
    public VSMRestFulServer() {
        // Print some information
        sLogger.message("Constructing Kristina's VSM restful root resource class");
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public synchronized String get() {
        // Print some information
        sLogger.message("Get");
        // Return something here
        return http();
//        
//        if (sRunTime.isRunning(sProject)) {
//            return sProject.getProjectName();
//        } else {
//            return main();
//        }
    }

//    @GET
//    @Path("prj")
//    @Produces(MediaType.TEXT_PLAIN)
//    public synchronized String prj() {
//        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        final IOSIndentWriter indentWriter = new IOSIndentWriter(stream);
//        // Write the visicon config
//        XMLUtilities.writeToXMLWriter(sProject.getVisicon(), indentWriter);
//        // Write the acticon config
//        XMLUtilities.writeToXMLWriter(sProject.getActicon(), indentWriter);
//        // Write the gesticon config
//        XMLUtilities.writeToXMLWriter(sProject.getGesticon(), indentWriter);
//        // Write the scenflow config
//        XMLUtilities.writeToXMLWriter(sProject.getSceneFlow(), indentWriter);
//        // Write the scenescript config
//        XMLUtilities.writeToXMLWriter(sProject.getSceneScript(), indentWriter);
//        // Retur the final string
//        return stream.toString();
//    }
//    @GET
//    @Path("run/{key}")
//    @Produces(MediaType.TEXT_HTML)
//    public synchronized String run(@PathParam("key") final String key) {
//        if (key.equals("play")) {
//            if (start()) {
//                return "success";
//            } else {
//                return "failure";
//            }
//        } else if (key.equals("stop")) {
//            if (stop()) {
//                return "success";
//            } else {
//                return "failure";
//            }
//        } else {
//            return "unknown";
//        }
//    }
//    @GET
//    @Path("get/{key}")
//    @Produces(MediaType.TEXT_PLAIN)
//    public synchronized String get(
//            @PathParam("key") final String key) {
//        //
//        return sRunTime.getValueOf(sProject, key).getConcreteSyntax();
//
//    }
//    @GET
//    @Path("set/{key}/{val}")
//    @Produces(MediaType.TEXT_PLAIN)
//    public synchronized String set(
//            @PathParam("key") final String key,
//            @PathParam("val") final String val) {
//        //
//        return String.valueOf(sRunTime.setVariable(sProject, key, val));
//
//    }
//    @GET
//    @Path("log")
//    @Produces(MediaType.TEXT_PLAIN)
//    public synchronized String log() throws IOException {
//        return new String(Files.readAllBytes(Paths.get(Preferences.sLOGFILE_FILE_NAME)));
//    }
}
