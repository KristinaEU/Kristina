package eu.kristina.vsm;

import com.sun.jersey.api.container.httpserver.HttpServerFactory;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;
import com.sun.net.httpserver.HttpServer;
import de.dfki.vsm.editor.EditorInstance;
import de.dfki.vsm.editor.project.EditorProject;
import de.dfki.vsm.editor.project.ProjectEditor;
import de.dfki.vsm.runtime.RunTimeInstance;
import de.dfki.vsm.runtime.symbol.SymbolEntry;
import de.dfki.vsm.util.ios.IOSIndentWriter;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import de.dfki.vsm.util.tpl.TPLTriple;
import de.dfki.vsm.util.xml.XMLWriteError;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.core.MultivaluedMap;

/**
 * @author Gregor Mehlmann
 */
public final class Server extends DefaultResourceConfig {

    // The CORS filter class
    private final class CORSFilter implements ContainerResponseFilter {

        @Override
        public ContainerResponse filter(
                final ContainerRequest request,
                final ContainerResponse response) {
            final MultivaluedMap<String, Object> headers = response.getHttpHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Headers",
                    "origin, content-type, accept, authorization");
            headers.add("Access-Control-Allow-Credentials", "true");
            headers.add("Access-Control-Allow-Methods",
                    "GET, POST, PUT, DELETE, OPTIONS, HEAD");
            return response;
        }
    }
    // The singelton server
    private static Server sInstance = null;

    // Create the singelton
    public static synchronized Server getInstance() {
        if (sInstance == null) {
            sInstance = new Server();
        }
        return sInstance;
    }

    // The restful resource
    private Service mResource = null;

    private Server() {
        // Print some information
        mLogger.message("Constructing server '" + this + "'");
        // Initialize the resource
        mResource = new Service(this);
        // Register the CORS filter
        getContainerResponseFilters().add(new CORSFilter());
    }

    // Get the singelton services
    @Override
    public synchronized Set<Object> getSingletons() {
        final Set<Object> objects = new HashSet<>();
        objects.add(mResource);
        return objects;
    }

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The runtime instance
    private final RunTimeInstance mRunTime
            = RunTimeInstance.getInstance();
    // The editor instance
    private final EditorInstance mWindow
            = EditorInstance.getVisualizer();
    // The project instance
    private EditorProject mProject = null;
    private ProjectEditor mEditor = null;
    //
    private String mLastPath = null;
    // The termination flag    
    private boolean mDone = false;
    // The http server
    private HttpServer mServer = null;
    // The executor pool
    private ExecutorService mPool = null;
    // The inpout reader
    private BufferedReader mReader = null;

    // The status types
    private enum Status {

        NULLED,
        LOADED,
        ACTIVE
    }

    // Run the restful server
    public final void run(final String args[]) {
        // Print information
        mLogger.message("Starting '" + this + "' on '" + args[0] + "'");
        // Start the server
        try {
            // Create the server
            mServer = HttpServerFactory.create(args[0]/*, this*/);
            // Get the executors
            mPool = Executors.newFixedThreadPool(1);
            // Set the executors
            mServer.setExecutor(mPool);
            // Start the server
            mServer.start();
            // Create input reader
            mReader = new BufferedReader(
                    new InputStreamReader(System.in));
            // Accept commands
            while (!mDone) {
                // Read a command from the console
                final String in = mReader.readLine();
                // Print information
                mLogger.message("Command '" + in + "'");
                // Check the user's last command
                if (in != null) {
                    if (in.equals("exit")) {
                        // Exit the server
                        exit();
                    } else if (in.startsWith("load")) {
                        // Get project path
                        final String path = in.substring(4).trim();
                        // Load the project
                        load(path);
                    } else if (in.startsWith("unload")) {
                        // Unload the project
                        unload();
                    } else if (in.startsWith("reset")) {
                        // Reset the project
                        reset();
                    } else if (in.equals("start")) {
                        // Start the project
                        start();
                    } else if (in.equals("stop")) {
                        // Stop the project
                        stop();
                    } else if (in.equals("show")) {
                        // Show the editor
                        show();
                    } else if (in.equals("hide")) {
                        // Hide the editor
                        hide();
                    } else if (in.equals("config")) {
                        // Get the config
                        mLogger.success(config());
                    } else if (in.equals("states")) {
                        // Get the states
                        mLogger.success(states());
                    } else if (in.equals("status")) {
                        // Get the status
                        mLogger.success(status());
                    } else {
                        // Print warning
                        mLogger.warning("Unknown command '" + in + "'");
                    }
                }
            }
            // Kill the executors
            mPool.shutdownNow();
            // Abort the mServer
            mServer.stop(0);
            // Print information
            mLogger.message("Stopping '" + this + "' on '" + args[0] + "'");
        } catch (final IOException | IllegalArgumentException exc) {
            mLogger.failure(exc.toString());
        }
        // ... and exit then
        System.exit(0);
    }

    // Load the VSM project
    public synchronized boolean load(final String path) {
        try {
            // Load the new project
            if (mProject == null) {
                // Create the project file
                final File file = new File(path);
                // Print information
                mLogger.message("Trying to load file '" + file.getAbsolutePath() + "'");
                // Check if the file exists
                if (file.exists() && file.isDirectory()) {
                    // Create new project
                    mProject = new EditorProject();
                    // Parse the project
                    if (mProject.parse(file.getAbsolutePath())) {
                        // Load the runtime project
                        if (mRunTime.load(mProject)) {
                            // Print some information
                            mLogger.success("Successfully loaded project '" + mProject.getProjectPath() + "'");
                            // Remeber the last path
                            mLastPath = path;
                            // Return true at success
                            return true;
                        } else {
                            // Print some information
                            mLogger.failure("Cannot load file '" + file.getAbsolutePath() + "'");
                        }
                    } else {
                        // Print some information
                        mLogger.failure("Cannot open file '" + file.getAbsolutePath() + "'");
                    }
                } else {
                    // Print some information
                    mLogger.failure("Cannot find file '" + file.getAbsolutePath() + "'");
                }
            } else {
                mLogger.failure("Cannot load project because a project is already loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Unload the VSM project
    public synchronized boolean unload() {
        try {
            // Check if project is loaded        
            if (mProject != null) {
                if (!mRunTime.isRunning(mProject)) {
                    // Print some information
                    mLogger.success("Successfully unloaded project '" + mProject.getProjectPath() + "'");
                    // Set the project null
                    mProject = null;
                    // Return true at success
                    return true;
                } else {
                    // Print some information
                    mLogger.failure("Cannot unload project because the project is still running!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot unload project because a project is not yet loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Start the VSM project
    public synchronized boolean start() {
        try {
            // Check if project is loaded        
            if (mProject != null) {
                // Check if project is running
                if (!mRunTime.isRunning(mProject)) {
                    // Then launch the project now
                    if (mRunTime.launch(mProject)) {
                        // And then start the project
                        if (mRunTime.start(mProject)) {
                            // Print some information
                            mLogger.success("Successfully started project '" + mProject.getProjectName() + "'");
                            // Return true at success
                            return true;
                        } else {
                            // Print some information
                            mLogger.failure("Cannot start project '" + mProject.getProjectName() + "'");
                        }
                    } else {
                        // Print some information
                        mLogger.failure("Cannot launch project '" + mProject.getProjectName() + "'");
                    }
                } else {
                    // Print some information
                    mLogger.failure("Cannot start project because the project is already running!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot start project because the project is not yet loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Stop the VSM project   
    public synchronized boolean stop() {
        try {
            // Check if project is loaded
            if (mProject != null) {
                // Check if project is running
                if (mRunTime.isRunning(mProject)) {
                    // Then abort the running project
                    if (mRunTime.abort(mProject)) {
                        // And unload the project then
                        if (mRunTime.unload(mProject)) {
                            // Print some information
                            mLogger.success("Successfully stopped project '" + mProject.getProjectPath() + "'");
                            // Return true at success
                            return true;
                        } else {
                            // Print some information
                            mLogger.failure("Cannot unload project '" + mProject.getProjectPath() + "'");
                        }
                    } else {
                        // Print some information
                        mLogger.failure("Cannot abort project '" + mProject.getProjectPath() + "'");
                    }
                } else {
                    // Print some information
                    mLogger.failure("Cannot stop project because the project is not yet running!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot stop project because the project is not yet loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Show the VSM editor
    public synchronized boolean show() {
        try {
            // Check if the project is null
            if (mProject != null) {
                if (mEditor == null) {
                    // Show the project editor 
                    mEditor = mWindow.showProject(mProject);
                    // Print some information
                    mLogger.message("Successfully showed project '" + mProject.getProjectPath() + "'");
                    // return true at success
                    return true;
                } else {
                    // Print some information
                    mLogger.failure("Cannot show editor because the editor is already shown!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot show editor because the project is not yet loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Hide the VSM editor
    public synchronized boolean hide() {
        try {
            // Check if the project is null
            if (mProject != null) {
                // Check if the editor is null
                if (mEditor != null) {
                    // Hide the current editor 
                    mWindow.hideProject(mEditor);
                    // And set the editor null
                    mEditor = null;
                    // Print some information
                    mLogger.message("Successfully hided project '" + mProject.getProjectPath() + "'");
                    // Return true at success
                    return true;
                } else {
                    // Print some information
                    mLogger.failure("Cannot hide editor because the editor is not yet shown!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot hide editor because the project is not yet loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }   // Return false at failure
        return false;
    }

    // Reset the VSM project
    public synchronized boolean reset() {
        try {
            if (hide()) {
                // Print some information
                mLogger.message("Successfully hiding editor for reset");
            } else {
                // Print some information
                mLogger.failure("Cannot hide project for a hard reset");
            }

            if (stop()) {
                // Print some information
                mLogger.message("Successfully stopping project for reset");
            } else {
                // Print some information
                mLogger.failure("Cannot stop project for a hard reset");
            }

            if (unload()) {// Print some information
                mLogger.message("Successfully unloading project for reset");
            } else {
                // Print some information
                mLogger.failure("Cannot unload project for a hard reset");
            }

            if (mLastPath != null) {
                // Print some information
                mLogger.success("Remembering project '" + mLastPath + "'for reset");
                if (load(mLastPath)) {
                    if (start()) {
                        // Print some information
                        mLogger.message("Successfully restarted project for a reset");
                        if (show()) {
                            // Print some information
                            mLogger.message("Successfully showing editor for a reset");
                            // Return true at success
                            return true;
                        } else {
                            // Print some information
                            mLogger.failure("Cannot start project for a reset");
                        }
                    } else {
                        // Print some information
                        mLogger.failure("Cannot start project for a reset");
                    }
                } else {
                    // Print some information
                    mLogger.failure("Cannot load project for a reset");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot remember a project for a reset");
            }

        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Exit the VSM project
    public synchronized boolean exit() {
        try {
            if (mProject == null && mEditor == null) {
                // Print some information
                mLogger.message("Requesting exiting!");
                // Abort the mServer
                mDone = true;
                // Interrupt the main thread
                mReader.close();
                // Print some information
                mLogger.message("Closing input reader");
                // Return true at success
                return true;
            } else {
                // Print some information
                mLogger.failure("Cannot exit service!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Construct status message
    public synchronized boolean set(final String var, final String val) {
        try {
            if (mProject != null) {
                // Check if project is running
                if (mRunTime.isRunning(mProject)) {
                    if (mRunTime.setVariable(mProject, var, val)) {
                        // Print some information
                        mLogger.success("Setting variable '" + var + "' to value '" + val + "'");
                        // Return true at success
                        return true;
                    } else {
                        // Print some information
                        mLogger.failure("Cannot set variable '" + var + "' to value '" + val + "' because the project is not running!");
                    }
                } else {
                    // Print some information
                    mLogger.failure("Cannot set variable '" + var + "' to value '" + val + "' because the project is not yet loaded!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot set data because the project is not yet loaded!");
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return false at failure
        return false;
    }

    // Construct status message
    public synchronized String status() {
        try {
            if (mProject != null) {
                if (mRunTime.isRunning(mProject)) {
                    return Status.ACTIVE.name();
                } else {
                    // Project already loaded
                    return Status.LOADED.name();
                }
            } else {
                // Project is not yet loaded
                return Status.NULLED.name();
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return null at failure
        return null;
    }

    // Construct config message
    public synchronized String config() {
        try {
            // Create a byte array output stream
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Open the stream with an indent writer
            final IOSIndentWriter writer = new IOSIndentWriter(stream);
            // Check the project state 
            if (mProject != null) {
                // Open the response element 
                writer.println("<Config>").push();
                // Check the project state 
                if (mProject != null) {
                    // Write scene player config
                    mProject.getDefaultScenePlayerConfig().writeXML(writer);
                }
                // Close the response element 
                writer.pop().print("</Config>");
                // Flush and close the writer and the stream
                writer.flush();
                writer.close();
            } else {
                // Print some information
                mLogger.failure("Cannot build config because the project is not yet loaded!");
            }
            // Return xml if writing was successfull
            return stream.toString("UTF-8");
        } catch (final XMLWriteError exc) {
            // Print some information
            mLogger.failure(exc.toString());
        } catch (final UnsupportedEncodingException exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return null at failure
        return null;
    }

    // Construct states
    public synchronized String states() {
        try {
            // Create a byte array output stream
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Open the stream with an indent writer
            final IOSIndentWriter writer = new IOSIndentWriter(stream);
            // Check the project state 
            if (mProject != null) {
                // Get the configuration
                if (mRunTime.isRunning(mProject)) {
                    // Open the states element 
                    writer.println("<States>").push();
                    // Get the active state list
                    final ArrayList states = mRunTime.listActiveStates(mProject);
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
                            final Map.Entry entry = (Map.Entry) object;
                            //
                            final String name = entry.getKey().toString();
                            //
                            final SymbolEntry symbol = (SymbolEntry) entry.getValue();
                            // Write the value element 
                            writer.println("<Variable name=\"" + name + "\">").push();
                            writer.println(symbol.getValue().getConcreteSyntax());
                            writer.pop().println("</Variable>");

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
                } else {
                    // Print some information
                    mLogger.failure("Cannot build states because the project is not running!");
                }
            } else {
                // Print some information
                mLogger.failure("Cannot build states because the project is not yet loaded!");
            }
            // Return xml if writing was successfull
            return stream.toString("UTF-8");
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
        }
        // Return null at failure
        return null;
    }

    // Run the restful mServer
    public static void main(final String args[]) {
        // Create the singelton server
        final Server server = Server.getInstance();
        // Run the singelton server now
        server.run(args);
    }
}
