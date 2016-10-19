package eu.kristina.vsm.test;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.bml.ActionFactory;
import eu.kristina.vsm.rest.Resource;
import eu.kristina.vsm.rest.WebClient;
import eu.kristina.vsm.util.Utilities;
import java.util.Random;
import org.json.JSONObject;
import static org.ujmp.core.util.StringUtil.duration;

/**
 * @author Gregor Mehlmann
 */
public final class TestSuite {

    // Create the REST client
    private final static WebClient sClient = new WebClient();
    // Dialog Management
    private final static Resource sDM = new Resource(
            "UULM-OWL", "Dialog Management",
            "http://172.31.26.245:11150",
            "application/json", "text/plain");
    private final static Resource sMV = new Resource(
            "UPF-TALN", "Mode-Selection-Verbal",
            "http://kristina.taln.upf.edu/services/mode_selection/verbal",
            "application/json", "text/plain");
    private final static Resource sMN = new Resource(
            "UPF-TALN", "Mode-Selection-Nonverbal",
            "http://kristina.taln.upf.edu/services/mode_selection/nonverbal",
            "application/json", "text/plain");
    private final static Resource sAI = new Resource(
            "UPF-GTI", "Avatar-Idle",
            "https://webglstudio.org:8080/idle",
            "application/json", "application/json");
    private final static Resource sAT = new Resource(
            "UPF-GTI", "Avatar-Turn",
            "https://webglstudio.org:8080/turn",
            "application/json", "application/json");

   


private static LOGDefaultLogger sLogger = LOGDefaultLogger.getInstance();

    ////////////////////////////////////////////////////////////////////////////
    public static final void main(final String args[]) {
        // Get the generator
        final Random random = new Random();
        // Get the command
        final JSONObject object = new JSONObject();
        
        //
        final String action = ActionFactory.gaze(1.0f, 10.0f, "LEFT");

        // Produce the initial object
        object.put("uuid",random.nextInt());
        object.put("type", "idle");
        object.put("meta", new JSONObject("{\"avatar\":\"KRISTINA\"}"));
        object.put("data", new JSONObject(action));
        // Execute POST request
        sClient.post(sAI, "", object.toString(2));
    }

    ////////////////////////////////////////////////////////////////////////////
    private static void testJSON(final String args[]) {
        final String l1 = "{\"0\":\"l\"}";
        final String r1 = "{\"0\":\"r\"}";
        final String m1 = Utilities.merge(l1, r1, 4);
        System.out.println(l1);
        System.out.println(r1);
        System.out.println(m1);

        final String l2 = "{\"0\":{\"1\":\"l\"}}";
        final String r2 = "{\"0\":{\"2\":\"r\"}}";
        final String m2 = Utilities.merge(l2, r2, 4);
        System.out.println(l2);
        System.out.println(r2);
        System.out.println(m2);

        final String json_lg = Utilities.read("res/exp/output_language_generation.txt");
        final String json_ms = Utilities.read("res/exp/output_mode_selection.txt");
        final String merge = Utilities.merge(json_lg, json_ms, 2);
        System.out.println(merge);

        final String l = Utilities.read("src/eu/kristina/vsm/test/envelope.txt");
        final String r = Utilities.read("src/eu/kristina/vsm/test/envelope.txt");
        final String m = Utilities.merge(l, r, 2);
        Utilities.write(m, "src/eu/kristina/vsm/test/second.txt");

        final String input = Utilities.read("res/exp/output_language_analysis.txt");
        final String encoded = Utilities.encodeJSON(input);
        System.out.println(encoded);
        final String decoded = Utilities.decodeJSON(encoded);
        System.out.println(decoded);

        final String x = Utilities.decodeJSON("\"<rdf:RDF\\n    xmlns:rdf=\\\"http:\\/\\/www.w3.org\\/1999\\/02\\/22-rdf-syntax-ns#\\\"\\n    xmlns:j.0=\\\"http:\\/\\/kristina-project.eu\\/ontologies\\/dialogue_actions#\\\">\\n  <j.0:SystemAction rdf:about=\\\"http:\\/\\/kristina-project.eu\\/tmp#1163a855-d4a2-4f7b-b4a8-b9d2ce7aae06\\\">\\n    <j.0:startWith>\\n      <j.0:ShowWeather rdf:about=\\\"http:\\/\\/kristina-project.eu\\/tmp#85ae8fd3-4330-4410-a410-24ea79163eb0\\\">\\n        <j.0:text>@prefix xsd:     &lt;http:\\/\\/www.w3.org\\/2001\\/XMLSchema#&gt; . @prefix rdfs:    &lt;http:\\/\\/www.w3.org\\/2000\\/01\\/rdf-schema#&gt; . @prefix owl:     &lt;http:\\/\\/www.w3.org\\/2002\\/07\\/owl#&gt; . @prefix rdf:     &lt;http:\\/\\/www.w3.org\\/1999\\/02\\/22-rdf-syntax-ns#&gt; .  &lt;http:\\/\\/temp#13p2om0pkeevfc9kf8cq0ik8hh&gt;       a       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#Forecast&gt; ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#humidity&gt;               \\\\\\\"58\\\\\\\"^^xsd:double ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#pressure&gt;               \\\\\\\"725\\\\\\\"^^xsd:double ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#skyCondition&gt;               \\\\\\\"partlyCloudySkyConditionRating\\\\\\\"^^xsd:string ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#temperature&gt;               \\\\\\\"23\\\\\\\"^^xsd:double ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#windDirection&gt;               \\\\\\\"WWindDirectionRating\\\\\\\"^^xsd:string ;       &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather#windSpeed&gt;               \\\\\\\"3\\\\\\\"^^xsd:double .  &lt;http:\\/\\/response-data&gt;       a       owl:Ontology ;       owl:imports &lt;http:\\/\\/kristina-project.eu\\/ontologies\\/weather&gt; . <\\/j.0:text>\\n      <\\/j.0:ShowWeather>\\n    <\\/j.0:startWith>\\n    <j.0:hasValence rdf:datatype=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema#float\\\"\\n    >0.0<\\/j.0:hasValence>\\n    <j.0:hasArousal rdf:datatype=\\\"http:\\/\\/www.w3.org\\/2001\\/XMLSchema#float\\\"\\n    >0.0<\\/j.0:hasArousal>\\n    <j.0:containsSystemAct rdf:resource=\\\"http:\\/\\/kristina-project.eu\\/tmp#85ae8fd3-4330-4410-a410-24ea79163eb0\\\"\\/>\\n  <\\/j.0:SystemAction>\\n<\\/rdf:RDF>\\n\"\n"
                + "");
        System.out.println(x);

        final String a = Utilities.read("res/exp/output_dialog_management.txt");
        final String b = Utilities.encodeJSON(a);
        System.out.println(b);
    }

    ////////////////////////////////////////////////////////////////////////////
    private static void testPipe(final String args[]) {
        String payload, response = null;

        // DM 
        payload = "{\n"
                + "  \"data\":{" + "\n"
                + "    \"fusion\":{" + "\n"
                + "      \"valence\":\"0.5\"" + "," + "\n"
                + "      \"arousal\":\"0.25\"" + "\n"
                + "    }" + "," + "\n"
                + "    \"language-analysis\":\"" + Utilities.encodeJSON(Utilities.read("res/test/output_language_analysis.txt")) + "\"" + "\n"
                + "  }" + "," + "\n"
                + "  \"meta\":{" + "\n"
                + "    \"user\":\"Anna\"" + "," + "\n"
                + "    \"scenario\":{" + "\n"
                + "      \"name\":\"babycare\"" + "\n"
                + "    }" + "\n"
                + "  }" + "\n"
                + "}";
        System.err.println(payload);
        response = sClient.post(sDM, "", payload);
        System.err.println(response);

        // MV
        payload = "{\n"
                + "  \"data\":{" + "\n"
                + "    \"fusion\":{" + "\n"
                + "      \"valence\":\"0.5\"" + "," + "\n"
                + "      \"arousal\":\"0.25\"" + "\n"
                + "    }" + "," + "\n"
                + "    \"language-analysis\":\"" + Utilities.encodeJSON(Utilities.read("res/test/output_language_analysis.txt")) + "\"" + "," + "\n"
                + "    \"dialog-management\":\"" + Utilities.encodeJSON(Utilities.read("res/test/output_dialog_management.txt")) + "\"" + "\n"
                + "  }" + "," + "\n"
                + "  \"meta\":{" + "\n"
                + "    \"user\":\"Anna\"" + "," + "\n"
                + "    \"scenario\":{" + "\n"
                + "      \"name\":\"babycare\"" + "\n"
                + "    }" + "\n"
                + "  }" + "\n"
                + "}";
        System.err.println(payload);
        response = sClient.post(sMV, "", payload);
        System.err.println(response);
        response = sClient.post(sMN, "", payload);
        System.err.println(response);
    }

    ////////////////////////////////////////////////////////////////////////////
    private static void testDM(final String args[]) {

    }

    ////////////////////////////////////////////////////////////////////////////
    //private static void testPipe(final String args[]) {
    //testVSM();
    //testSSI();
    //testDM(client);
    //testUPF(client);
    //testAV(client);
    //testPipe(client);
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
    //final Resource la = new Resource(
    //        "UPF-TALN", "Language Analysis",
    //        "http://kristina.taln.upf.edu/services/language_analysis",
    //        "application/x-www-form-urlencoded", "application/xml");
    //final String _la = client.post(la, "", "");
//        final String _la = "</timeout>";
//        // DIALOG MANAGEMENT
//        final Resource dm = new Resource(
//                "UULM-OWL", "Dialog Management",
//                "http://172.31.26.245:11150",
//                "*/*", "*/*");
//        final String _dm = client.post(dm, "?valence=" + 0.5f + "&arousal=" + 0.5f, _la);
//        // MODE SELECTION
//        final Resource ms = new Resource(
//                "UPF-TALN", "Mode Selection",
//                "http://kristina.taln.upf.edu/services/mode_selection",
//                "application/x-www-form-urlencoded", "application/json");
//        final String _ms_v = client.post(ms, "?mode=verbal&lang=pl", _dm);
//        final String _ms_n = client.post(ms, "?mode=non_verbal&lang=pl", _dm);
//        // LANGUAGE GENERATION
//        final Resource lg = new Resource(
//                "UPF-TALN", "Language Generation",
//                "http://kristina.taln.upf.edu/services/language_generation",
//                "application/x-www-form-urlencoded", "application/json");
//        final String _lg = client.post(lg, "", _ms_v);
//        // CHARACTER ENGINE 
//        final Resource ce_v = new Resource(
//                "UPF-GTI", "Character Engine",
//                "http://webglstudio.org:8080/verbal",
//                "application/json", "*/*");
//        final String _ce_v = client.post(ce_v, "?id=KRISTINA", _lg);
//        // CHARACTER ENGINE 
//        final Resource ce_n = new Resource(
//                "UPF-GTI", "Character Engine",
//                "http://webglstudio.org:8080/non_verbal",
//                "application/xml", "*/*");
//        final String _ce_n = client.post(ce_n, "?id=KRISTINA", _ms_n);
    //}
    private static void testSSI(final WebClient client) {
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

    private static void testVSM(final WebClient client) {
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

    private static void testUPF(final WebClient client) {
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

   
}
