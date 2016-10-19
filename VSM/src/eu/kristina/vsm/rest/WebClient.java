package eu.kristina.vsm.rest;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.core.Response.Status;

/**
 * @author Gregor Mehlmann
 */
public final class WebClient {

    // The logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
    private final TrustManager[] mTrust;
    // The RESTful client
    private final Client mClient;

    // TODO: Decide for a policy how many clients to maintain:
    // Singelton client vs. sereral clients vs. client per call
    public WebClient() {

        // Create a trust manager that does 
        // not validate certificate chains
        mTrust = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public final X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public final void checkClientTrusted(
                        final X509Certificate[] certs,
                        final String authentication) {
                }

                @Override
                public final void checkServerTrusted(
                        final X509Certificate[] certs,
                        final String authentication) {
                }
            }};
        // Create the rest service client
        mClient = Client.create(new DefaultClientConfig());
        mClient.setConnectTimeout(60000);
        mClient.setReadTimeout(60000);
        // Print some information
        mLogger.message("Creating RESTful client '" + mClient + "'");
        // Install the trust manager
        try {

            final SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, mTrust, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            // Print some information
            mLogger.message("Installing  trust manager '" + mTrust + "'");

        } catch (final Exception exc) {
            mLogger.warning(exc.toString());
        }

    }

    public final String post(
            final Resource resource,
            final String queryargs,
            final String content) {
        // Create the final URL
        final String url = resource.getPath() + queryargs;

        try {

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
                mLogger.failure("Failure: The POST request '" + url
                        + "' to RESTful service resource '" + resource
                        + "' failed with status '" + response.getStatus() + "'");
                // Return null at failure
                return Integer.toString(response.getStatus());
            }
        } catch (final Exception exc) {
            // Print some information
            mLogger.failure(exc.toString());
            // Print some information
            mLogger.failure("Failure: The POST request '" + url
                    + "' to RESTful service resource '" + resource
                    + "' failed with an exception");
            //exc.printStackTrace();
            // Return null at failure
            return exc.getMessage();
        }
    }

    public final String get(
            final Resource resource,
            final String queryargs) {
        try {
            // Create the final URL
            final String url = resource.getPath() + queryargs;
            // Print some information
            //mLogger.message("Executing GET request to URL '" + url + "'");
            // Execute the get request
            final ClientResponse response = mClient
                    .resource(url)
                    .accept(resource.getProd())
                    .type(resource.getCons())
                    .get(ClientResponse.class);
            // Print some information
            //mLogger.message("Receiving GET response from URL '" + url + "' as object:\n" + response);
            // Check the response status
            if (response.getStatus() == Status.OK.getStatusCode()) {
                // Get the mime type name
                final String type = response.getType().getType()
                        + "/" + response.getType().getSubtype();
                // Get the entity string
                final String entity = response.getEntity(String.class);
                // Print some information
                //mLogger.success("Success: The GET request '" + url
                //        + "' to RESTful service resource '" + resource
                //        + "' returned a response of type '" + type
                //        + "' and content entity:\n" + entity);
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
