package eu.kristina.vsm;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;
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

    public final void get(final Resource resource, final String query) {
        // Create the final URL
        final String url = resource.getPath() + query;
        // Print some information
        mLogger.message("Executing GET request to URL '" + url + "'");
        // Execute the get request
        final ClientResponse response = mClient
                .resource(url)
                .accept(resource.getProd())
                .type(resource.getCons())
                .get(ClientResponse.class);
        // Print some information
        //mLogger.message("Receiving GET response from URL '" + url + "' with content :\n" + response.toString());
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
        } else {
            // Print some information
            mLogger.failure("Failure: Response from '" + resource
                    + "' has status '" + response.getStatus() + "'");
        }

    }

    public final void post(final Resource resource, final String query, final String content) {
        // Create the final URL
        final String url = resource.getPath() + query;
        // Print some information
        mLogger.message("Executing POST request to URL '" + url + "' with content:\n" + content);
        // Execute the post request
        final ClientResponse response = mClient
                .resource(url)
                .accept(resource.getProd())
                .type(resource.getCons())
                .post(ClientResponse.class, content);
        // Print some information
        //mLogger.message("Receiving POST response from URL '" + url + "' with content :\n" + response.toString());
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
        } else {
            // Print some information
            mLogger.failure("Failure: Response from '" + resource
                    + "' has status '" + response.getStatus() + "'");
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
        final Resource ssiweb = new Resource(
                "UAU-SSI", "Status Website",
                "http://137.250.171.230:11193",
                "*/*", "text/html");
        client.get(ssiweb, "");
        // Build the SSI service config
        final Resource ssiget = new Resource(
                "UAU-SSI", "Fusion Status",
                "http://137.250.171.230:11193",
                "*/*", "application/json");
        client.get(ssiget, "/Fusion/Service.svc/getStatus");
        // Build the SSI service config
        final Resource ssiemo = new Resource(
                "UAU-SSI", "Fusion Emotion",
                "http://137.250.171.230:11193",
                "*/*", "application/json");
        client.get(ssiemo, "/Fusion/Service.svc/getEmotion");
        // Build the SSI service config
        final Resource ssicmd = new Resource(
                "UAU-SSI", "Fusion Control",
                "http://137.250.171.230:11193",
                "application/json", "application/json");
        client.post(ssicmd,
                "/Fusion/Service.svc/executeCommand",
                "{\"Target\":\"SSI\",\"Command\":\"restart\"}");
    }

    private static void testVSM(final VSMRestFulClient client) {
        // Build the VSM service config
        final Resource vsmall = new Resource(
                "UAU-VSM", "Turn Management",
                "http://137.250.171.231:11223",
                "*/*", "application/xml");
        client.get(vsmall, "?cmd=status");
        client.get(vsmall, "?cmd=load&arg=res/prj/vsm");
        client.get(vsmall, "?cmd=config");
        client.post(vsmall, "", "Hello World!");
    }

    private static void testOWL(final VSMRestFulClient client) {
        // Build the OWL service config
        final Resource owlall = new Resource(
                "UULM-OWL", "Dialog Management",
                "http://137.250.171.232:11153",
                "*/*", "*/*");
        // Try executing a GET or a POST 
        client.get(owlall, "?text=hello");
        client.post(owlall, "", "Hello World!");
    }

    private static void testGTI(final VSMRestFulClient client) {
        // Construct some JSON request
        final String json = ""
                + "{" + "\n"
                + " \"cmdId\": 12345," + "\n"
                + " \"audioURL\": \"http://www.webglstudio.org/gerard/visemes/es003_2.wav\"" + "," + "\n"
                + " \"sequence\":"
                + " [[0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5],"
                + "  [1.0, 0.5, 0.0, 0.5, 1.0, 0.5, 1.0],"
                + "  [1.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]" + "," + "\n"
                + " \"face\": [-0.5, 0.5]" + "," + "\n"
                + " \"blink\": true" + "," + "\n"
                + " \"text\": \"Hello world!\"" + "\n"
                + "}";

        // Build the GTI service config
        final Resource gticmd = new Resource(
                "UPF-GTI", "Character Engine",
                "http://webglstudio.org:8080/newData",
                "application/json", "*/*");
        // Try executing a GET or a POST 
        client.post(gticmd, "?id=hcm", json);
    }

    private static void testUPF(final VSMRestFulClient client) {
        // Build the UPF service configs
        final Resource la = new Resource(
                "UPF-TALN", "Language Analysis",
                "http://kristina.taln.upf.edu/services/language_analysis",
                "application/x-www-form-urlencoded", "application/xml");
        final Resource lg = new Resource(
                "UPF-TALN", "Language Generation",
                "http://kristina.taln.upf.edu/services/language_generation",
                "application/x-www-form-urlencoded", "application/json");
        final Resource ms = new Resource(
                "UPF-TALN", "Mode Selection",
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
    public static final class Resource {

        private final String mHost;
        private final String mName;
        private final String mPath;
        private final String mCons;
        private final String mProd;

        public Resource(
                final String host,
                final String name,
                final String path,
                final String inmt,
                final String outmt) {
            mHost = host;
            mName = name;
            mPath = path;
            mCons = inmt;
            mProd = outmt;
        }

        public String getHost() {
            return mHost;
        }

        public String getName() {
            return mName;
        }

        public final String getPath() {
            return mPath;
        }

        public final String getCons() {
            return mCons;
        }

        public final String getProd() {
            return mProd;
        }

        @Override
        public String toString() {
            return "<Service"
                    + " host=\"" + mHost + "\""
                    + " name=\"" + mName + "\""
                    + " path=\"" + mPath + "\""
                    + " cons=\"" + mCons + "\""
                    + " prod=\"" + mProd + "\""
                    + "/>";
        }
    }
}
