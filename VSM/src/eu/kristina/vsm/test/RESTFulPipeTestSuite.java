package eu.kristina.vsm.test;

import eu.kristina.vsm.rest.RESTFulResource;
import eu.kristina.vsm.rest.RESTFulWebClient;

/**
 * @author Gregor Mehlmann
 */
public class RESTFulPipeTestSuite {

    // Execute the rest test
    public static final void main(final String args[]) {
        // Create the client
        final RESTFulWebClient client = new RESTFulWebClient();

        //testVSM();
        //testSSI();
        testOWL(client);
        //testUPF(client);
        //testGTI();
        //testPipe();

    }

    private static void testPipe(final RESTFulWebClient client) {

        /*
        // Create the listener
        final SSIEventListener listener = new SSIEventListener(
                new SSIEventHandler() {
                    @Override
                    public void handle(String event) {
                        
                    }
                },
                "137.250.171.231", 11220,
                "137.250.171.230", 11190,
                false);
        // Start the listener
        listener.start();
         */
        // LANGUAGE ANALYSIS
        final RESTFulResource la = new RESTFulResource(
                "UPF-TALN", "Language Analysis",
                "http://kristina.taln.upf.edu/services/language_analysis",
                "application/x-www-form-urlencoded", "application/xml");
        final String _la = client.post(la, "", "");
        // DIALOG MANAGEMENT
        final RESTFulResource dm = new RESTFulResource(
                "UULM-OWL", "Dialog Management",
                "http://137.250.171.232:11153",
                "*/*", "*/*");
        final String _dm = client.post(dm, "?valence=" + 0.5f + "&arousal=" + 0.5f, _la);
        // MODE SELECTION
        final RESTFulResource ms = new RESTFulResource(
                "UPF-TALN", "Mode Selection",
                "http://kristina.taln.upf.edu/services/mode_selection",
                "application/x-www-form-urlencoded", "application/xml");
        final String _ms_v = client.post(ms, "?mode=verbal", _dm);
        final String _ms_n = client.post(ms, "?mode=non_verbal", _dm);
        // LANGUAGE GENERATION
        final RESTFulResource lg = new RESTFulResource(
                "UPF-TALN", "Language Generation",
                "http://kristina.taln.upf.edu/services/language_generation",
                "application/x-www-form-urlencoded", "application/json");
        final String _lg = client.post(lg, "", _ms_v);
        // CHARACTER ENGINE 
        final RESTFulResource ce_v = new RESTFulResource(
                "UPF-GTI", "Character Engine",
                "http://webglstudio.org:8080/verbal",
                "application/json", "*/*");
        final String _ce_v = client.post(ce_v, "?id=hcm", _lg);
        // CHARACTER ENGINE 
        final RESTFulResource ce_n = new RESTFulResource(
                "UPF-GTI", "Character Engine",
                "http://webglstudio.org:8080/non_verbal",
                "application/xml", "*/*");
        final String _ce_n = client.post(ce_n, "?id=hcm", _ms_n);

    }

    private static void testSSI(final RESTFulWebClient client) {
        // Build the SSI service config
        final RESTFulResource ssiweb = new RESTFulResource(
                "UAU-SSI", "Status Website",
                "http://137.250.171.230:11193",
                "*/*", "text/html");
        client.get(ssiweb, "");
        // Build the SSI service config
        final RESTFulResource ssiget = new RESTFulResource(
                "UAU-SSI", "Fusion Status",
                "http://137.250.171.230:11193",
                "*/*", "application/json");
        client.get(ssiget, "/Fusion/Service.svc/getStatus");
        // Build the SSI service config
        final RESTFulResource ssiemo = new RESTFulResource(
                "UAU-SSI", "Fusion Emotion",
                "http://137.250.171.230:11193",
                "*/*", "application/json");
        client.get(ssiemo, "/Fusion/Service.svc/getEmotion");
        // Build the SSI service config
        final RESTFulResource ssicmd = new RESTFulResource(
                "UAU-SSI", "Fusion Control",
                "http://137.250.171.230:11193",
                "application/json", "application/json");
        client.post(ssicmd,
                "/Fusion/Service.svc/executeCommand",
                "{\"Target\":\"SSI\",\"Command\":\"restart\"}");
    }

    private static void testVSM(final RESTFulWebClient client) {
        // Build the VSM service config
        final RESTFulResource vsmall = new RESTFulResource(
                "UAU-VSM", "Turn Management",
                "http://137.250.171.231:11223",
                "*/*", "application/xml");
        client.get(vsmall, "?cmd=status");
        client.get(vsmall, "?cmd=load&arg=res/prj/vsm");
        client.get(vsmall, "?cmd=config");
        client.post(vsmall, "", "Hello World!");
    }

    private static void testOWL(final RESTFulWebClient client) {
        // Build the OWL service config
        final RESTFulResource owlall = new RESTFulResource(
                "UULM-OWL", "Dialog Management",
                "http://52.29.254.9:11153",
                "*/*", "*/*");
        // Try executing a GET or a POST 
        //client.get(owlall, "?valence=" + 0.5f + "&arousal=" + 0.5f);
        client.post(owlall, "", "Hello World!");
    }

    private static void testUPF(final RESTFulWebClient client) {
        // Build the UPF service configs
        final RESTFulResource la = new RESTFulResource(
                "UPF-TALN", "Language Analysis",
                "http://kristina.taln.upf.edu/services/language_analysis",
                "application/x-www-form-urlencoded", "application/xml");
        final RESTFulResource lg = new RESTFulResource(
                "UPF-TALN", "Language Generation",
                "http://kristina.taln.upf.edu/services/language_generation",
                "application/x-www-form-urlencoded", "application/json");
        final RESTFulResource ms = new RESTFulResource(
                "UPF-TALN", "Mode Selection",
                "http://kristina.taln.upf.edu/services/mode_selection",
                "application/x-www-form-urlencoded", "application/xml");
        // Try executing a GET or a POST 
        //client.get(la, "?text=hello");
        //client.get(lg, "?text=hello");
        //client.get(ms, "?text=hello");
        client.post(la, "", "Hello World!");
        client.post(lg, "", "Hello World!");
        client.post(ms, "", "Hello World!");
    }

    private static void testGTI() {
        // Create the client
        final RESTFulWebClient client = new RESTFulWebClient();
        // Construct some JSON request
        final String json = ""
                + "{" + "\n"
                + " \"audioURL\": \"http://www.webglstudio.org/gerard/visemes/es003_2.wav\"" + "," + "\n"
                + " \"sequence\":"
                + " [[0.0, 0.0, 0.5, 0.0, 0.0, 0.0, 0.5],"
                + "  [1.0, 0.5, 0.0, 0.5, 1.0, 0.5, 1.0],"
                + "  [1.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0]]" + "," + "\n"
                + " \"face\": [-0.5, 0.5]" + "," + "\n"
                + " \"blink\": true" + "," + "\n"
                + " \"text\": \"Hello world!\"" + "\n"
                + "}";

        // Construct some XML request
        //final String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
        //        + "</HellWord> ";
        // Build the GTI service config
        final RESTFulResource ce_v = new RESTFulResource(
                "UPF-GTI", "Avatar-Verbal",
                "http://webglstudio.org:8080/verbal",
                "application/json", "*/*");
        // Try executing a GET or a POST 
        client.post(ce_v, "?id=hcm", json);

        // Build the GTI service config
        //final RESTFulResource ce_n = new RESTFulResource(
        //        "UPF-GTI", "Avatar-Nonverbal",
        //        "http://webglstudio.org:8080/non_verbal",
        //        "application/json", "*/*");
        // Try executing a GET or a POST 
        //client.post(ce_n, "?id=hcm", xml);
    }

}
