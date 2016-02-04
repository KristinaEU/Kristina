package eu.kristina.vsm;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.spi.resource.Singleton;
import com.sun.net.httpserver.HttpServer;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.project.RunTimeProject;
import de.dfki.vsm.runtime.symbol.SymbolEntry;
import de.dfki.vsm.util.ios.IOSIndentWriter;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import de.dfki.vsm.util.tpl.TPLTriple;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    // The player instance
    //private static RunTimePlayer sPlayer = null;

    /**
     * Load the VSM runtime project instance for KRISTINA
     *
     * @return A boolean flag indicating success or failure
     */
    private static boolean load(final String filename) {
        // Stop current project first
        if (sProject != null) {
            // Print some information
            sLogger.warning("Warning: Trying to stop VSM runtime project '" + sProject.getProjectName() + "' first");
            // Stop the project now
            if (stop()) {
                // Print some information
                sLogger.success("Success: Stopped running VSM runtime project '" + sProject.getProjectName() + "' first");
            } else {
                // Print some information
                sLogger.warning("Warning: There was no need to stop VSM runtime project '" + sProject.getProjectName() + "'");
            }
        }
        // Create the project file
        final File file = new File(filename);
        // Check if the file exists
        if (file.exists() && file.isDirectory()) {
            // Get the runtime project
            sProject = new RunTimeProject(new File(filename));
            // Load the runtime project
            if (sProject.load()) {
                // Print some information
                sLogger.success("Success: Loaded VSM runtime project '" + sProject.getProjectName() + "'");
                // Return true at success
                return true;
            } else {
                // Print some information
                sLogger.failure("Failure: Cannot load VSM runtime project '" + sProject.getProjectName() + "'");
                // Return false at failure
                return false;
            }
        } else {
            // Print some information
            sLogger.failure("Failure: Cannot find any VSM runtime project base directory '" + file + "'");
            // Return false at failure
            return false;
        }
    }

    /**
     * Unload the VSM runtime project instance for KRISTINA
     */
    private static boolean unload() {
        // Check if project is loaded        
        if (sProject != null) {
            // Stop the project now
            if (stop()) {
                // Print some information
                sLogger.message("Stopped running VSM runtime project '" + sProject.getProjectName() + "' first");
            } else {
                // Print some information
                sLogger.message("There was no need to stop VSM runtime project '" + sProject.getProjectName() + "'");
            }
            // Print some information
            sLogger.success("Success: Unloaded VSM runtime project '" + sProject.getProjectName() + "'");
            // Set the project null
            sProject = null;
            // Return true at success
            return true;
        } else {
            // Print some information
            sLogger.failure("Failure: There is no VSM runtime project loaded yet");
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
        // Check if project is loaded        
        if (sProject != null) {
            // Check if project is running
            if (!sRunTime.isRunning(sProject)) {
                // Then launch the project now
                if (sRunTime.launch(sProject)) {
                    // And then start the project
                    if (sRunTime.start(sProject)) {
                        // Print some information
                        sLogger.success("Success: Starting VSM runtime project '" + sProject.getProjectName() + "'");
                        // Return true at success
                        return true;
                    } else {
                        // Print some information
                        sLogger.failure("Failure: Cannot start VSM runtime project '" + sProject.getProjectName() + "'");
                        // Return false at failure
                        return false;
                    }
                } else {
                    // Print some information
                    sLogger.failure("Failure: Cannot launch VSM runtime project '" + sProject.getProjectName() + "'");
                    // Return false at failure
                    return false;
                }
            } else {
                // Print some information
                sLogger.failure("Failure: VSM runtime project '" + sProject.getProjectName() + "' is already running");
                // Return false at failure
                return false;
            }
        } else {
            // Print some information
            sLogger.failure("Failure: There is no VSM runtime project loaded yet");
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
        // Check if project is loaded
        if (sProject != null) {
            // Check if project is running
            if (sRunTime.isRunning(sProject)) {
                // Then abort the running project
                if (sRunTime.abort(sProject)) {
                    // And unload the project then
                    if (sRunTime.unload(sProject)) {
                        // Print some information
                        sLogger.success("Success: Stopping VSM runtime project '" + sProject.getProjectName() + "'");
                        // Return true at success
                        return true;
                    } else {
                        // Print some information
                        sLogger.failure("Failure: Cannot unload VSM runtime project '" + sProject.getProjectName() + "'");
                        // Return false at failure
                        return false;
                    }
                } else {
                    // Print some information
                    sLogger.failure("Failure: Cannot stop VSM runtime project '" + sProject.getProjectName() + "'");
                    // Return false at failure
                    return false;
                }
            } else {
                // Print some information
                sLogger.failure("Failure: VSM runtime project '" + sProject.getProjectName() + "' is not running");
                // Return false at failure
                return false;
            }
        } else {
            // Print some information
            sLogger.failure("Failure: There is no VSM runtime project loaded yet");
            // Return false at failure
            return false;
        }
    }

    private static String response(final String result, final String status) {
        // Create a byte array output stream
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Open the stream with an indent writer
        final IOSIndentWriter writer = new IOSIndentWriter(stream);
        // Print the status event to xml format
        try {
            // Write the XML header line
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            // Open the response element 
            writer.println("<Response result=\"" + result + "\" status=\"" + status + "\">").push();
            // Check the project state 
            if (sProject != null) {
                // Write scene player config
                sProject.getDefaultScenePlayerConfig().writeXML(writer);
                // Get the configuration
                if (sRunTime.isRunning(sProject)) {
                    // Open the states element 
                    writer.println("<States>").push();
                    // Get the active state list
                    final ArrayList states = sRunTime.listActiveStates(sProject);
                    // Write the state list here
                    for (final Object state : states) {
                        final TPLTriple triple = (TPLTriple) state;
                        final String threadid = (String) triple.getFirst();
                        final String nodename = (String) triple.getSecond();
                        final HashMap table = (HashMap) triple.getThird();
                        // Open the states element 
                        writer.println("<State threadid=\"" + threadid + "\" nodename=\"" + nodename + "\">").push();
                        // Open the values element 
                        writer.println("<Variables>").push();
                        // List all variable values
                        for (final Object object : table.entrySet()) {
                            final Entry entry = (Entry) object;
                            //
                            final String name = entry.getKey().toString();
                            //
                            final SymbolEntry symbol = (SymbolEntry) entry.getValue();
                            // Write the value element 
                            writer.println("<Variable name=\"" + name + "\" value=\"" + symbol.getValue().getConcreteSyntax() + "\">");
                        }
                        // Close the values element 
                        writer.pop().println("</Variables>");
                        // Close the state element 
                        writer.pop().println("</State>");
                    }
                    // Close the states element 
                    writer.pop().println("</States>");
                }
            }
            // Close the response element 
            writer.pop().println("</Response>");
            // Flush and close the writer and the stream
            writer.flush();
            writer.close();
            // Return xml if writing was successfull
            return stream.toString("UTF-8");
        } catch (final Exception exc) {
            // Print some error message in this case
            sLogger.failure(exc.toString());
            // Return false if writing to XML failed
            return null;
        }
    }

    /**
     * Construct the RESTful VSM server object of KRISTINA
     */
    public VSMRestFulServer() {
        // Print some information
        sLogger.message("Constructing Kristina's VSM restful root resource class");
    }

    /**
     * Execute a GET request on the restful server
     *
     * @param cmd The command of the query
     * @param arg The argument of the query
     * @return The result of the query execution
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public synchronized String get(
            @DefaultValue("") @QueryParam("cmd") final String cmd,
            @DefaultValue("") @QueryParam("arg") final String arg) {
        // Check the cmd parameter
        if (!cmd.isEmpty()) {
            // Print some information
            sLogger.message("Detecting GET request query command '" + cmd + "'");
            // Check the query command
            if (cmd.equalsIgnoreCase("load")) {
                // Check the arg parameter
                if (!arg.isEmpty()) {
                    // Print some information
                    sLogger.message("Detecting GET request query argument '" + arg + "'");
                    // Execute the load command
                    return response((load(arg) ? "SUCCESS" : "FAILURE"), "UNKNOWN");
                }
            } else if (cmd.equalsIgnoreCase("unload")) {
                // Execute the unload command
                return response((unload() ? "SUCCESS" : "FAILURE"), "UNKNOWN");
            } else if (cmd.equalsIgnoreCase("start")) {
                // Execute the unload command
                return response((start() ? "SUCCESS" : "FAILURE"), "UNKNOWN");
            } else if (cmd.equalsIgnoreCase("stop")) {
                // Execute the unload command
                return response((stop() ? "SUCCESS" : "FAILURE"), "UNKNOWN");
            }
        }
        // Return something here
        return response("UNKNOWN", "UNKNOWN");

    }

    public static void main(final String args[]) {
        // Print information
        sLogger.message("Initializing VSM KRISTINA restful server");
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
                            unload();
                            // Abort the server
                            done = true;
                        } else if (in.startsWith("load")) {
                            // Play the project
                            load(in.substring(4));
                        } else if (in.equals("start")) {
                            // Play the project
                            start();
                        } else if (in.equals("stop")) {
                            // Stop the project
                            stop();
                        } else {
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
        sLogger.message("Terminating VSM KRISTINA restful server");
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
    /**
     * Produce the HTTP homepage of the server
     *
     * @return The HTTP main navigation menu of the server
     */
    /*
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
     }*/
}
