package eu.kristina.vsm.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.dfki.vsm.util.log.LOGDefaultLogger;

/**
 * @author Gregor Mehlmann
 */
public final class RESTFulWebClient {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    // The RESTful client
    private final Client mClient;

    // TODO: Decide for a policy how many clients to maintain:
    // Singelton client vs. sereral clients vs. client per call
    public RESTFulWebClient() {
        // Create the rest service client
        mClient = Client.create(new DefaultClientConfig());
    }

    public final String get(
            final RESTFulResource resource,
            final String queryargs) {
        try {
            // Create the final URL
            final String url = resource.getPath() + queryargs;
            // Print some information
            mLogger.message("Executing GET request to URL '" + url + "'");
            // Execute the get request
            final ClientResponse response = mClient
                    .resource(url)
                    .accept(resource.getProd())
                    .type(resource.getCons())
                    .get(ClientResponse.class);
            // Check the response status
            if (response.getStatus() == Status.OK.getStatusCode()) {
                // Get the mime type name
                final String type = response.getType().getType()
                        + "/" + response.getType().getSubtype();
                // Get the entity string
                final String entity = response.getEntity(String.class);
                // Print some information
                mLogger.success("Success: The POST request '" + url
                        + "' to RESTful service resource '" + resource
                        + "' returned a response of type '" + type
                        + "' and content entity:\n" + entity);
                // Return the entity then
                return entity;
            } else {
                // Print some information
                mLogger.failure("Failure: Response from '" + resource
                        + "' has status '" + response.getStatus() + "'");
                // Return null at failure
                return Integer.toString(response.getStatus());
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Return null at failure
            return exc.getMessage();
        }
    }

    public final String post(
            final RESTFulResource resource,
            final String queryargs,
            final String content) {
        try {
            // Create the final URL
            final String url = resource.getPath() + queryargs;
            // Print some information
            mLogger.message("Executing POST request to URL '" + url + "' with content:\n" + content);
            // Execute the post request
            final ClientResponse response = mClient
                    .resource(url)
                    .accept(resource.getProd())
                    .type(resource.getCons())
                    .post(ClientResponse.class, content);
            // Check the response status
            if (response.getStatus() == Status.OK.getStatusCode()) {
                // Get the mime type name
                final String type = response.getType().getType()
                        + "/" + response.getType().getSubtype();
                // Get the entity string
                final String entity = response.getEntity(String.class);
                // Print some information
                mLogger.success("Success: The POST request '" + url
                        + "' to RESTful service resource '" + resource
                        + "' returned a response of type '" + type
                        + "' and content entity:\n" + entity);
                // Return the entity then
                return entity;
            } else {
                // Print some information
                mLogger.failure("Failure: Response from '" + resource
                        + "' has status '" + response.getStatus() + "'");
                // Return null at failure
                return Integer.toString(response.getStatus());
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Return null at failure
            return exc.getMessage();
        }
    }
}
