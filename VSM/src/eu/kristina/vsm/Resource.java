package eu.kristina.vsm;

import com.sun.jersey.spi.resource.Singleton;
import de.dfki.vsm.util.ios.IOSIndentWriter;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
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
public final class Resource {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The restful service
    private final Service mService;

    // Construct the resource
    public Resource() {
        // Initialize the service
        mService = Service.getInstance();
        // Print some information
        mLogger.message("Constructing resource '" + this + "'");
    }

    // Construct the resource
    //public Resource(final Service service) {
    // Initialize the service
    //    mService = service;
    // Print some information
    //    mLogger.message("Constructing resource '" + this + "'");
    //}
    // Execute a GET request 
//    @GET
//    @Consumes(MediaType.WILDCARD)
//    @Produces(MediaType.APPLICATION_XML)
//    public synchronized String get(
//            @DefaultValue("") @QueryParam("cmd") final String cmd,
//            @DefaultValue("") @QueryParam("arg") final String arg) {
//       
//    }
    // Execute a POST request
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public synchronized Response post(final String object) {
        // Create the response
        final JSONObject response = new JSONObject();
        // Create the request
        final JSONObject request = new JSONObject(object);
        // Parse the fields
        final String cmd = request.getString("cmd");
        //
        if (cmd.equalsIgnoreCase("load")) {
            //
            final String arg = request.getString("arg");
            if (mService.load(arg)) {
                response.put("status", "success");
                response.put("message", "Loaded project '" + arg + "'");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot load project '" + arg + "'");
            }
        } else if (cmd.equalsIgnoreCase("start")) {
            if (mService.start()) {
                response.put("status", "success");
                response.put("message", "Started project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not start project");
            }
        } else if (cmd.equalsIgnoreCase("stop")) {
            if (mService.stop()) {
                response.put("status", "success");
                response.put("message", "Stopped project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not stop project");
            }
        } else if (cmd.equalsIgnoreCase("unload")) {
            if (mService.unload()) {
                response.put("status", "success");
                response.put("message", "Unloaded project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not unload project");
            }
        } else if (cmd.equalsIgnoreCase("show")) {
            if (mService.show()) {
                response.put("status", "success");
                response.put("message", "Showing project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not show project");
            }
        } else if (cmd.equalsIgnoreCase("hide")) {
            if (mService.hide()) {
                response.put("status", "success");
                response.put("message", "Hiding project");
            } else {
                response.put("status", "failure");
                response.put("message", "Cannot not hide project");
            }
        } else if (cmd.equalsIgnoreCase("status")) {
            response.put("status", mService.status());
        } else if (cmd.equalsIgnoreCase("config")) {
            response.put("config", mService.config());
        } else if (cmd.equalsIgnoreCase("states")) {
            response.put("states", mService.states());
        } else if (cmd.equalsIgnoreCase("set")) {
            //
            final JSONObject arg = request.getJSONObject("arg");
            final String var = arg.getString("var");
            final String val = arg.getString("val");
            // Set the meta data
            if (mService.set(var, val)) {
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

    // Format a success
    private String success(final String text) {
        return response("success", text);
    }

    // Format a success
    private String failure(final String text) {
        return response("failure", text);
    }

    // Format a response
    private String response(final String type, final String text) {
        try {
            // Create a byte array output stream
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            // Open the stream with an indent writer
            final IOSIndentWriter writer = new IOSIndentWriter(stream);
            // Write the XML header line
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            // Print the status to xml 
            if (text.isEmpty()) {
                // Open the response element 
                writer.print("<" + type + "/>");
            } else {
                // Open the response element 
                writer.println("<" + type + ">");
                // Write the text content
                writer.println(text);
                // Open the response element 
                writer.print("</" + type + ">");
            }
            // Flush and close writer 
            writer.flush();
            writer.close();
            // Return xml if writing was successfull
            return stream.toString("UTF-8");
        } catch (final UnsupportedEncodingException exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Return null at failure
            return null;
        }
    }
}
