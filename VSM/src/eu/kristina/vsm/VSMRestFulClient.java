package eu.kristina.vsm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import de.dfki.vsm.util.log.LOGDefaultLogger;
import java.math.BigDecimal;
import javax.json.Json;
import javax.json.JsonObject;

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

    public final void get(final Service service, final String query) {
        // Create the final URL
        final String url = service.getServiceURL() + query;
        // Print some information
        mLogger.message("Executing GET request to URL '" + url + "'");
        // Execute the get request
        final ClientResponse response = mClient
                .resource(url)
                .accept(service.mOutMimeType)
                .type(service.mInMimeType)
                .get(ClientResponse.class);
        // Print some information
        mLogger.message("Receiving GET response from URL '" + url + "' with content :\n" + response.toString());
        // Check the response status
        if (response.getStatus() == Status.OK.getStatusCode()) {
            // Print some information
            mLogger.success("Success: Reading response content of type '"
                    + response.getType().getType() + "/" + response.getType().getSubtype()
                    + "' from '" + service + "':\n" + response.getEntity(String.class));
        } else {
            // Print some information
            mLogger.failure("Failure: Cannot read response from '" + service + "':\n" + response.getStatus());
        }

    }

    public final void post(final Service service, final String query, final String object) {
        // Create the final URL
        final String url = service.getServiceURL() + query;
        // Print some information
        mLogger.message("Executing POST request to URL '" + url + "' with content:\n" + object.toString());
        // Execute the post request
        final ClientResponse response = mClient
                .resource(url)
                .accept(service.mOutMimeType)
                .type(service.mInMimeType)
                .post(ClientResponse.class, object);
        // Print some information
        mLogger.message("Receiving POST response from URL '" + url + "' with content :\n" + response.toString());
        // Check the response status
        if (response.getStatus() == Status.OK.getStatusCode()) {
            // Print some information
            mLogger.success("Success: Reading response content of type '"
                    + response.getType().getType() + "/" + response.getType().getSubtype()
                    + "' from '" + service + "':\n" + response.getEntity(String.class));
        } else {
            // Print some information
            mLogger.failure("Failure: Cannot read response from '" + service + "':\n" + response.getStatus());
        }

    }

    // Execute the rest client
    public static final void main(final String args[]) {
        // Build the restful client
        final VSMRestFulClient client = new VSMRestFulClient(null);
        //
        //testVSM(client);
        //testSSI(client);
        //testOWL(client);
        //testUPF(client);
        testGTI(client);

    }

    private static void testSSI(final VSMRestFulClient client) {
        // Build the SSI service config
        final Service ssi = new Service(
                "UAU", "SSI - Social Cue Pipeline",
                "http://137.250.171.230:11193",
                "*/*", "text/html");
        // Try executing a GET or a POST 
        client.get(ssi, "?text=hello");
        client.post(ssi, "", "Hello World!");
    }

    private static void testVSM(final VSMRestFulClient client) {
        // Build the VSM service config
        final Service vsm = new Service(
                "UAU", "VSM - Turn Management",
                "http://137.250.171.231:11223",
                "*/*", "application/xml");
        // Try executing a GET or a POST 
        client.get(vsm, "?cmd=status");
        client.post(vsm, "", "Hello World!");
    }

    private static void testOWL(final VSMRestFulClient client) {
        // Build the OWL service config
        final Service owl = new Service(
                "UULM", "OWL - Dialog Management",
                "http://137.250.171.232:11153",
                "*/*", "*/*");
        // Try executing a GET or a POST 
        client.get(owl, "?text=hello");
        client.post(owl, "", "Hello World!");
    }

    private static void testGTI(final VSMRestFulClient client) {
        // Construct some JSON request
        final String json = ""
                + "{" + "\n"
                + " \"audioURL\": \"http://www.webglstudio.org/gerard/visemes/es003_2.wav\"" + "," + "\n"
                + " \"sequence\":"
                + " \"[[0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5],"
                + "  [1.0, 0.5, 0.0, 0.5, 1.0, 0.5, 1.0],"
                + "  [1.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]\"" + "," + "\n"
                + " \"face\": \"[-0.5, 0.5]\"" + "," + "\n"
                + " \"blink\": \"true\"" + "," + "\n"
                + " \"text\": \"Hello world!\"" + "\n"
                + "}";

        // Build the GTI service config
        final Service gti = new Service(
                "UPF", "GTI - Character Engine",
                "http://webglstudio.org:8080/newData",
                "application/json", "*/*");
        // Try executing a GET or a POST 
        client.post(gti, "?id=hcm", json);
    }

    private static void testUPF(final VSMRestFulClient client) {
        // Build the UPF service configs
        final Service la = new Service(
                "UPF", "TALN - Language Analysis",
                "http://kristina.taln.upf.edu/services/language_analysis",
                "application/x-www-form-urlencoded", "application/xml");
        final Service lg = new Service(
                "UPF", "TALN - Language Generation",
                "http://kristina.taln.upf.edu/services/language_generation",
                "application/x-www-form-urlencoded", "application/json");
        final Service ms = new Service(
                "UPF", "TALN - Mode Selection",
                "http://kristina.taln.upf.edu/services/mode_selection",
                "application/x-www-form-urlencoded", "application/xml");
        // Try executing a GET or a POST 
        client.get(la, "?text=hello");
        client.get(lg, "?text=hello");
        client.get(ms, "?text=hello");
        client.post(la, "", "Hello World!");
        client.post(lg, "", "Hello World!");
        client.post(ms, "", "Hello World!");
    }

    /**
     *
     */
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
                    + " host=\"" + mServiceHost + "\""
                    + " name=\"" + mServiceName + "\""
                    + " url=\"" + mServiceURL + "\""
                    + " in=\"" + mInMimeType + "\""
                    + " out=\"" + mOutMimeType + "\""
                    + "/>";
        }
    }
}
