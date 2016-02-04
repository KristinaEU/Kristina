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
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
    // The status variables
    //private static Status sStatus = Status.NULLED;

    // The status enumeration
    private enum Status {

        NULLED,
        LOADED,
        ACTIVE
    }

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

    /**
     * Return a response as result object to a GET request
     *
     * @param A flag indicating success or failure of the query
     * @return The xml string reprsentation of the response
     */
    private static String result(final String text) {
        // Create a byte array output stream
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Open the stream with an indent writer
        final IOSIndentWriter writer = new IOSIndentWriter(stream);
        // Print the status event to xml format
        try {
            // Write the XML header line
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            //
            if (text.isEmpty()) {
                // Open the response element 
                writer.print("<Result/>");
            } else {
                // Open the response element 
                writer.println("<Result>");
                // Write the text content
                writer.println(text);
                // Open the response element 
                writer.print("</Result>");
            }
            // Flush and close writer 
            writer.flush();
            writer.close();
            // Return xml if writing was successfull
            return stream.toString("UTF-8");
        } catch (final UnsupportedEncodingException exc) {
            // Print some error message in this case
            sLogger.failure(exc.toString());
            // Return false if writing to XML failed
            return null;
        }
    }

    /**
     * Construct a xml string representation of the VSM status
     *
     * @return The xml string representation of the VSM status
     */
    private static String status() {
        if (sProject != null) {
            if (sRunTime.isRunning(sProject)) {
                return Status.ACTIVE.name();
            } else {
                return Status.LOADED.name();
            }
        } else {
            return Status.NULLED.name();
        }
    }

    /**
     * Construct a xml string representation of the VSM config
     *
     * @return The xml string representation of the VSM config
     */
    private static String config() {
        // Create a byte array output stream
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Open the stream with an indent writer
        final IOSIndentWriter writer = new IOSIndentWriter(stream);
        // Print the status event to xml format
        try {
            // Check the project state 
            if (sProject != null) {
                // Open the response element 
                writer.println("<Config>").push();
                // Check the project state 
                if (sProject != null) {
                    // Write scene player config
                    sProject.getDefaultScenePlayerConfig().writeXML(writer);
                }
                // Close the response element 
                writer.pop().print("</Config>");
                // Flush and close the writer and the stream
                writer.flush();
                writer.close();
            }
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
     * Construct a xml string representation of the VSM states
     *
     * @return The xml string representation of the VSM states
     */
    private static String states() {
        // Create a byte array output stream
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Open the stream with an indent writer
        final IOSIndentWriter writer = new IOSIndentWriter(stream);
        // Print the status event to xml format
        try {
            // Check the project state 
            if (sProject != null) {
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
                    writer.pop().print("</States>");
                    // Flush and close the writer and the stream
                    writer.flush();
                    writer.close();
                }
            }
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
     * Construct the restful VSM server
     */
    public VSMRestFulServer() {
        // Print some information
        sLogger.message("Constructing Kristina's VSM restful root resource class");
    }

    /**
     * Execute a GET request on the restful VSM server
     *
     * @param cmd The command of the query
     * @param arg The argument of the query
     * @return The result of the query execution
     */
    @GET
    @Produces(MediaType.APPLICATION_XML)
    public synchronized String get(
            @DefaultValue("") @QueryParam("cmd") final String cmd,
            @DefaultValue("") @QueryParam("arg") final String arg) {
        if (cmd.equalsIgnoreCase("load")) {
            return result((load(arg) ? "SUCCESS" : "FAILURE"));
        } else if (cmd.equalsIgnoreCase("start")) {
            return result((start() ? "SUCCESS" : "FAILURE"));
        } else if (cmd.equalsIgnoreCase("stop")) {
            return result((stop() ? "SUCCESS" : "FAILURE"));
        } else if (cmd.equalsIgnoreCase("unload")) {
            return result((unload() ? "SUCCESS" : "FAILURE"));
        } else if (cmd.equalsIgnoreCase("status")) {
            return result(status());
        } else if (cmd.equalsIgnoreCase("config")) {
            return result(config());
        } else if (cmd.equalsIgnoreCase("states")) {
            return result(states());
        } else {
            return result("UNKNOWN");
        }
    }

    /**
     * Execute a POST request on the restful VSM server
     *
     * @param object The argument of the query
     * @return The result of the query execution
     */
    @POST
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_XML)
    public synchronized Response post(final String object) {
        // Return the object
        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_XML)
                .entity(result(object)).build();
    }

    //
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
                        //
                    }
                }
            }
            // Kill the executor
            pool.shutdownNow();
            // Abort the server
            server.stop(0);
        } catch (final Exception exc) {
            sLogger.failure(exc.toString());
        }
        // Print information
        sLogger.message("Terminating VSM KRISTINA restful server");
    }
}
