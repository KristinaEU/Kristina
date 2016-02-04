package eu.kristina.vsm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.dfki.vsm.util.log.LOGDefaultLogger;

/**
 * @author Gregor Mehlmann
 */
public final class VSMRestFulClient {

    // The singelton logger instance
    private final LOGDefaultLogger mLogger
            = LOGDefaultLogger.getInstance();
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

    public final void get(final Service service) {
        // Create the web resource
        final WebResource resource = mClient.resource(service.getServiceURL());
        // Print some information
        mLogger.message("Executing GET request to service '" + service + "':\n" + "");
        // Print some information
        mLogger.success("Receiving GET response from service '" + service + "':\n" + resource.get(String.class));

    }

    public final void post(final Service service, final Object object) {
        // Print some information
        mLogger.message("Executing POST request to service '" + service + "':\n" + object.toString());
        // Execute the pot request
        final ClientResponse response = mClient
                .resource(service.getServiceURL())
                .accept(service.mOutMimeType)
                .type(service.mInMimeType)
                .post(ClientResponse.class, object);
        // Print some information
        mLogger.message("Receiving POST response from service '" + service + "':\n" + response.toString());
        // Check the response status
        if (response.getStatus() == ClientResponse.Status.OK.getStatusCode()) {
            // Print some information
            mLogger.success("Success: Reading response content from '" + service + "':\n" + response.getEntity(String.class));
        } else {
            // Print some information
            mLogger.failure("Failure: Cannot read response content from '" + service + "':\n" + response.getStatus());
        }

    }

    // Execute the rest client
    public static final void main(final String args[]) {
        // Build the restful client
        final VSMRestFulClient client = new VSMRestFulClient(null);
        // Build all the services
        final Service ssi = new Service("UAU", "SSI - Social Cue Pipeline",
                "http://137.250.171.230:11193",
                "*/*", "*/*");
        final Service vsm = new Service("UAU", "VSM - Turn Management",
                "http://137.250.171.231:11223",
                "*/*", "*/*");
        final Service owl = new Service("UULM", "OWL - Dialog Management",
                "http://137.250.171.232:11153",
                "*/*", "*/*");
        final Service la = new Service("UPF", "TALN - Language Analysis",
                "http://kristina.taln.upf.edu/services/language_analysis",
                "application/x-www-form-urlencoded", "application/xml");
        final Service lg = new Service("UPF", "TALN - Language Generation",
                "http://kristina.taln.upf.edu/services/language_generation",
                "application/x-www-form-urlencoded", "application/json");
        final Service ms = new Service("UPF", "TALN - Mode Selection",
                "http://kristina.taln.upf.edu/services/mode_selection",
                "application/x-www-form-urlencoded", "application/xml");
        final Service gti = new Service("UPF", "GTI - Character Engine",
                "http://webglstudio.org:9902", "*/*", "*/*");

        client.get(ssi);
        //client.post(vsm, "Hello World!");
        //client.post(owl, "Hello World!");
        client.post(la, "Hello World!");
        client.post(lg, "Hello World!");
        client.post(ms, "Hello World!");
        client.post(gti, "Hello World!");
    }

    public static final class Service {

        private final String mServiceHost;
        private final String mServiceName;
        private final String mServiceURL;
        private final String mInMimeType;
        private final String mOutMimeType;

        public Service(
                final String serviceHost,
                final String serviceName,
                final String serviceURL,
                final String inMimeType,
                final String outMimeType) {
            mServiceHost = serviceHost;
            mServiceName = serviceName;
            mServiceURL = serviceURL;
            mInMimeType = inMimeType;
            mOutMimeType = outMimeType;
        }

        public String getServiceHost() {
            return mServiceHost;
        }

        public String getServiceName() {
            return mServiceName;
        }

        public final String getServiceURL() {
            return mServiceURL;
        }

        public final String getInMimeType() {
            return mInMimeType;
        }

        public final String getOutMimeType() {
            return mOutMimeType;
        }

        @Override
        public String toString() {
            return "<Service"
                    + " host=\"" + mServiceName + "\""
                    + " name=\"" + mServiceName + "\""
                    + " url=\"" + mServiceURL + "\""
                    + " in=\"" + mInMimeType + "\""
                    + " out=\"" + mOutMimeType + "\""
                    + "/>";
        }
    }
}
