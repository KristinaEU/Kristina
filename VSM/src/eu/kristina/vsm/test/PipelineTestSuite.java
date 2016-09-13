package eu.kristina.vsm.test;

import eu.kristina.vsm.rest.RESTFulResource;
import eu.kristina.vsm.rest.RESTFulWebClient;
import eu.kristina.vsm.util.KristinaUtility;

/**
 * @author Gregor Mehlmann
 */
public final class PipelineTestSuite {

    // Execute the rest test
    public static final void main(final String args[]) {
        // Create the client
        final RESTFulWebClient client = new RESTFulWebClient();
        //testVSM();
        //testSSI();
        testDM(client);
        //testUPF(client);
        testAV(client);
        //testPipe(client);
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
        //final RESTFulResource la = new RESTFulResource(
        //        "UPF-TALN", "Language Analysis",
        //        "http://kristina.taln.upf.edu/services/language_analysis",
        //        "application/x-www-form-urlencoded", "application/xml");
        //final String _la = client.post(la, "", "");
        final String _la = "</timeout>";
        // DIALOG MANAGEMENT
        final RESTFulResource dm = new RESTFulResource(
                "UULM-OWL", "Dialog Management",
                "http://172.31.26.245:11150",
                "*/*", "*/*");
        final String _dm = client.post(dm, "?valence=" + 0.5f + "&arousal=" + 0.5f, _la);
        // MODE SELECTION
        final RESTFulResource ms = new RESTFulResource(
                "UPF-TALN", "Mode Selection",
                "http://kristina.taln.upf.edu/services/mode_selection",
                "application/x-www-form-urlencoded", "application/json");
        final String _ms_v = client.post(ms, "?mode=verbal&lang=pl", _dm);
        final String _ms_n = client.post(ms, "?mode=non_verbal&lang=pl", _dm);
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
        final String _ce_v = client.post(ce_v, "?id=KRISTINA", _lg);
        // CHARACTER ENGINE 
        final RESTFulResource ce_n = new RESTFulResource(
                "UPF-GTI", "Character Engine",
                "http://webglstudio.org:8080/non_verbal",
                "application/xml", "*/*");
        final String _ce_n = client.post(ce_n, "?id=KRISTINA", _ms_n);

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

    private static void testDM(final RESTFulWebClient client) {
        // Build the DM service config
        final RESTFulResource dm = new RESTFulResource(
                "UULM-OWL", "Dialog-Management",
                "http://172.31.26.245:11150",
                "text/plain", "text/plain");
        // Try executing a POST request
        final String params = "?valence=" + 0.5f + "&arousal=" + 0.5f;
        final String payload = KristinaUtility.read("res/exp/output_language_analysis.txt");
        final String result = client.post(dm, params, payload);
        System.out.println(result);
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

        final String first = "{\n"
                + "  \"0\": {\n"
                + "    \"verbal\":{\n"
                + "      \"owl\": \"<rdf:RDF\\n    xmlns:rdf=\\\"http:\\/\\/www.w3.org\\/1999\\/02\\/22-rdf-syntax-ns#\\\"\\n    xmlns:j.0=\\\"http:\\/\\/kristina-project.eu\\/ontologies\\/dialogue_actions#\\\">\\n  <j.0:SystemAction rdf:about=\\\"http:\\/\\/kristina-project.eu\\/tmp#1163a855-d4a2-4f7b-b4a8-b9d2ce7aae06\\\">\\n    <j.0:startWith>\\n      <j.0:ShowWeather rdf:about=\\\"http:\\/\\/kristina-project.eu\\/tmp#85ae8fd3-4330-4410-a410-24ea79163eb0\\\">\\n        <j.0:text>@prefix xsd:     &lt;http:\\/\\/www.w3.org\\/2001\\/XMLSchema#&gt; . @prefix rdfs:    &lt;http:\\/\\/www.w3.org\\/2000\\/01\\/rdf-schema#&gt; . @prefix owl:     &lt;http:\\/\\/www.w3.org\\/2002\\/07\\/owl#&gt; . @prefix rdf:     &lt;http:\\/\\/www.w3.org\\/1999\\/02\\/22-rdf-syntax-ns#&gt; .  &lt;http:\\/\\/temp#13p2om0pkeevfc9kf8cq0ik8hh&gt;       a       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#Forecast&gt; ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#humidity&gt;               \\\\\\\"58\\\\\\\"^^xsd:double ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#pressure&gt;               \\\\\\\"725\\\\\\\"^^xsd:double ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#skyCondition&gt;               \\\\\\\"partlyCloudySkyConditionRating\\\\\\\"^^xsd:string ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#temperature&gt;               \\\\\\\"23\\\\\\\"^^xsd:double ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#windDirection&gt;               \\\\\\\"WWindDirectionRating\\\\\\\"^^xsd:string ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#windSpeed&gt;               \\\\\\\"3\\\\\\\"^^xsd:double .  &lt;http:\\/\\/response-data&gt;       a       owl:Ontology ;       owl:imports &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather&gt; . <\\/j.0:text>\\n      <\\/j.0:ShowWeather>\\n    <\\/j.0:startWith>\\n    <j.0:hasValence rdf:datatype=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema#float\\\"\\n    >0.0<\\/j.0:hasValence>\\n    <j.0:hasArousal rdf:datatype=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema#float\\\"\\n    >0.0<\\/j.0:hasArousal>\\n    <j.0:containsSystemAct rdf:resource=\\\"http:\\/\\/kristina-project.eu\\/tmp#85ae8fd3-4330-4410-a410-24ea79163eb0\\\"\\/>\\n  <\\/j.0:SystemAction>\\n<\\/rdf:RDF>\\n\"\n"
                + "    }\n"
                + "  }\n"
                + "}";

        // Try executing a GET or a POST 
        client.post(la, "", "Hello World!");
        client.post(lg, "", "Hello World!");
        client.post(ms, "", "Hello World!");
    }

    private static void testAV(final RESTFulWebClient client) {
        final RESTFulResource av = new RESTFulResource(
                "UPF-GTI", "Avatar-Taln",
                "https://webglstudio.org:8080/taln",
                "application/json", "application/json");

        // Try executing a POST request
        final String params = "?id=" + "KRISTINA";
        
        final String json_lg = KristinaUtility.read("res/exp/output_language_generation.txt");
        final String json_ms = KristinaUtility.read("res/exp/output_mode_selection.txt");
        final String payload = KristinaUtility.merge(json_lg, json_ms, 2);
        
        final String result = client.post(av, params, payload);

    }

}
