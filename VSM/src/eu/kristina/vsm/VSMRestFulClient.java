package eu.kristina.vsm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * @author Gregor Mehlmann
 */
public final class VSMRestFulClient {

    // The rest service client
    private final Client mClient;
    // The clien configuration
    private final ClientConfig mConfig;
    // The scene player object
    private final VSMKristinaPlayer mPlayer;

    public VSMRestFulClient(final VSMKristinaPlayer player) {
        // Initialize the player instance
        mPlayer = player;
        // Create the client configuration 
        mConfig = new DefaultClientConfig();
        // Configure the rest service client
        mConfig.getProperties().put(ClientConfig.PROPERTY_FOLLOW_REDIRECTS, true);
        // Create the rest service client
        mClient = Client.create(mConfig);
    }

    public final void get(final String uri) {
        // Create the web resource
        final WebResource resource = mClient.resource(uri);
        // Execute the get request
        final String response = resource.get(String.class);

        System.out.println(response);

    }

    // Execute the rest client
    public static void main(final String args[]) {
        // Build the rest client
        final VSMRestFulClient client = new VSMRestFulClient(null);
        // Start a get request
        client.get(args[0]);
    }

}
