package eu.kristina.vsm.test;

import de.dfki.vsm.util.log.LOGDefaultLogger;
import eu.kristina.vsm.rest.Resource;
import eu.kristina.vsm.rest.WebClient;
import eu.kristina.vsm.util.Utilities;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Gregor Mehlmann
 */
public final class TestSuite {

    //
    private static LOGDefaultLogger sLogger
            = LOGDefaultLogger.getInstance();
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

    ////////////////////////////////////////////////////////////////////////////
    public static final void main(final String args[]) {
        testXML(args);
    }

    ////////////////////////////////////////////////////////////////////////////
    private static void testXML(final String args[]) {
        final String events = Utilities.read("res/test/vocapia_event.xml");
        handle(events);

    }

    ////////////////////////////////////////////////////////////////////////////
    public static void handle(final String message) {
        // Print some information
        //mLogger.warning("Receiving SSI event:\n" + message + "");
        try {
            // Parse the received XML string
            final ByteArrayInputStream stream = new ByteArrayInputStream(message.getBytes("UTF-8"));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder builder = factory.newDocumentBuilder();
            final Document document = builder.parse(stream);
            // Get the XML tree root element
            final Element element = document.getDocumentElement();
            // Check if we have SSI events 
            if (element.getTagName().equals("events")) {
                // Get the list of SSI events
                final NodeList eventList = element.getElementsByTagName("event");
                // Check if the list is empty
                if (eventList.getLength() > 0) {
                    // Process the indvidual SSI events
                    for (int i = 0; i < eventList.getLength(); i++) {
                        final Element event = ((Element) eventList.item(i));
                        // Get the event attributes
                        final String mode = event.getAttribute("sender");
                        final String name = event.getAttribute("event");
                        final String type = event.getAttribute("type");
                        final String state = event.getAttribute("state");
                        final Integer glue = Integer.parseInt(event.getAttribute("glue"));
                        final Integer from = Integer.parseInt(event.getAttribute("from"));
                        final Integer dur = Integer.parseInt(event.getAttribute("dur"));
                        final Double prob = Double.parseDouble(event.getAttribute("prob"));
                        // Process the event features
                        if (mode.equalsIgnoreCase("audio")) {
                            if (name.equalsIgnoreCase("vad")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    // User stopped speaking
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // User started speaking
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else if (mode.equalsIgnoreCase("fsender")) {
                            if (name.equalsIgnoreCase("fevent")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("ntuple") || type.equalsIgnoreCase("map")) {
                                        // Get the list of tuples
                                        final NodeList tuples = element.getElementsByTagName("tuple");
                                        for (int j = 0; j < tuples.getLength(); j++) {
                                            // Get the nex tuple element
                                            final Element tuple = ((Element) tuples.item(j));
                                            // Get the tuple's attributes
                                            final String key = tuple.getAttribute("string");
                                            final Double val = Double.parseDouble(tuple.getAttribute("value"));
                                            // Set the variable value
                                            //mLogger.message(key + "=" + String.format(Locale.US, "%.6f", val));
                                            if (key.equalsIgnoreCase("valence")) {
                                            } else if (key.equalsIgnoreCase("arousal")) {
                                            } else {
                                                // Cannot process this  
                                            }
                                        }
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else if (mode.equalsIgnoreCase("vocapia")) {
                            if (name.equalsIgnoreCase("transcript")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent().trim();
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else if (name.equalsIgnoreCase("xml")) {
                                if (state.equalsIgnoreCase("completed")) {
                                    if (type.equalsIgnoreCase("string")) {
                                        // Just get the content
                                        final String text = event.getTextContent().trim();
                                        // User said something
                                        sLogger.success("Vocapia xml is\n" + text + "'");
                                        //
                                        final String json = Utilities.parseVocapia(text);
                                        // User said something
                                        sLogger.success("User Text  is\n" + json + "'");

                                        // Set the variable value         
                                        //set("UserSpeech", text);
                                    } else {
                                        // Cannot process this    
                                    }
                                } else if (state.equalsIgnoreCase("continued")) {
                                    // Cannot process this
                                } else {
                                    // Cannot process this
                                }
                            } else {
                                // Cannot process this
                            }
                        } else {
                            // Cannot process this
                        }
                    }
                }
            }
        } catch (final Exception exc) {
            // Print some information
            sLogger.failure(exc.toString());
        }
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

}
