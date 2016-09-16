package eu.kristina.vsm;

import com.sun.jersey.spi.resource.Singleton;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONObject;

/**
 * @author Gregor Mehlmann
 */
@Singleton
@Path("")
public final class Service {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The restful server
    private final Server mServer;

    // Construct the service
    public Service() {
        // Initialize the server
        mServer = Server.getInstance();
        // Print some information
        mLogger.message("Constructing service '" + this + "'");
    }

    // Handle a POST request
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response post(final String object) {
        // Create the response
        final JSONObject response = new JSONObject();
        // Create the request
        final JSONObject request = new JSONObject(object);
        // Parse the command
        final String cmd = request.getString("cmd");
        // Switch on command
        if (cmd.equalsIgnoreCase("load")) {
            // Parse the argument
            final String arg = request.getString("arg");
            // Switch on argument
            if (mServer.load(arg)) {
                response.put("status", "success");
                response.put("message", "Loaded project '" + arg + "'");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot load project '" + arg + "'");
            }
        } else if (cmd.equalsIgnoreCase("start")) {
            if (mServer.start()) {
                response.put("status", "success");
                response.put("message", "Started project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not start project");
            }
        } else if (cmd.equalsIgnoreCase("stop")) {
            if (mServer.stop()) {
                response.put("status", "success");
                response.put("message", "Stopped project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not stop project");
            }
        } else if (cmd.equalsIgnoreCase("unload")) {
            if (mServer.unload()) {
                response.put("status", "success");
                response.put("message", "Unloaded project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not unload project");
            }
        } else if (cmd.equalsIgnoreCase("show")) {
            if (mServer.show()) {
                response.put("status", "success");
                response.put("message", "Showing project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not show project");
            }
        } else if (cmd.equalsIgnoreCase("hide")) {
            if (mServer.hide()) {
                response.put("status", "success");
                response.put("message", "Hiding project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not hide project");
            }
        } else if (cmd.equalsIgnoreCase("status")) {
            response.put("status", mServer.status());
        } else if (cmd.equalsIgnoreCase("config")) {
            response.put("config", mServer.config());
        } else if (cmd.equalsIgnoreCase("states")) {
            response.put("states", mServer.states());
        } else if (cmd.equalsIgnoreCase("set")) {
            // Parse key value pair
            final JSONObject arg = request.getJSONObject("arg");
            final String var = arg.getString("var");
            final String val = arg.getString("val");
            // Set the variable
            if (mServer.set(var, val)) {
                response.put("status", "success");
                response.put("message", "Setting variable '" + var + "' to value '" + val + "'");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot set variable '" + var + "' to value '" + val + "'");
            }
        } else {
            response.put("status", "failure");
            response.put("message", "Unknown command '" + cmd + "'");
        }
        // Return the response
        return Response
                .status(Response.Status.OK)
                .type(MediaType.APPLICATION_JSON)
                .entity(response.toString(4))
                .build();
    }
}