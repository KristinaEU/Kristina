package eu.kristina.vsm.util;

import com.sun.jersey.json.impl.JSONHelper;

import de.dfki.vsm.util.log.LOGDefaultLogger;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Gregor Mehlmann
 */
public final class Utilities {

    private static LOGDefaultLogger sLogger = LOGDefaultLogger.getInstance();

    ////////////////////////////////////////////////////////////////////////////
    public final static String merge(
            final String lstr, final String rstr, final int indent) {
        final JSONObject lobj = new JSONObject(lstr);
        final JSONObject robj = new JSONObject(rstr);
        final JSONObject mobj = merge(lobj, robj);
        return mobj.toString(indent);
    }

    public final static JSONObject merge(
            final JSONObject lobj, final JSONObject robj) {
        final Map<String, Object> lmap = lobj.toMap();
        final Map<String, Object> rmap = robj.toMap();
        return new JSONObject(merge(lmap, rmap));
    }

    public final static Map<String, Object> merge(
            final Map<String, Object> lmap,
            final Map<String, Object> rmap) {
        final Map<String, Object> mmap = new HashMap();
        for (final Entry<String, Object> entry : lmap.entrySet()) {
            mmap.put(entry.getKey(), entry.getValue());
        }
        for (final Entry<String, Object> entry : rmap.entrySet()) {
            final String key = entry.getKey();
            final Object val = entry.getValue();
            if (mmap.containsKey(key)) {
                final Object obj = mmap.get(key);
                try {
                    mmap.put(key, merge(
                            (Map<String, Object>) obj,
                            (Map<String, Object>) val));
                } catch (final ClassCastException exc) {
                    sLogger.warning(exc.toString());
                }
            } else {
                mmap.put(key, val);
            }
        }
        return mmap;
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String put(
            final String obj,
            final String key,
            final String val,
            final String typ) {
        // Create the final object
        final JSONObject object = new JSONObject(obj);
        // Initialize temporaries
        int index;
        String path = key;
        JSONObject member = object;
        // Recursively insert now
        while ((index = path.indexOf(".")) != -1) {
            // Eventually insert
            if (!member.has(path.substring(0, index))) {
                member.put(path.substring(0, index), new JSONObject());
            }
            // Update the member
            member = member.getJSONObject(path.substring(0, index));
            // Update the path
            path = path.substring(index + 1);

        }
        // Insert key value pair
        if (typ.equals("OBJECT")) {
            try {
                // Insert parsed value
                member.put(path, new JSONObject(val));
            } catch (final JSONException exc) {
                // Print some information
                sLogger.failure(exc.toString() + " from input '"+val+"'");
                // Insert default value
                member.put(path, new JSONObject());
            }
        } else if (typ.equals("ARRAY")) {
            try {
                // Insert parsed value
                member.put(path, new JSONArray(val));
            } catch (final JSONException exc) {
                // Print some information
                sLogger.failure(exc.toString() + " from input '"+val+"'");
                // Insert default value
                member.put(path, new JSONArray());
            }
        } else if (typ.equals("DOUBLE")) {
            try {
                // Insert parsed value
                member.put(path, Double.parseDouble(val));
            } catch (final JSONException | NumberFormatException exc) {
                // Print some information
                sLogger.failure(exc.toString() + " from input '"+val+"'");
                // Insert default value
                member.put(path, 0.0);
            }
        } else if (typ.equals("STRING")) {
            try {
                // Insert parsed value
                member.put(path, val);
            } catch (final JSONException exc) {
                // Print some information
                sLogger.failure(exc.toString() + " from input '"+val+"'");
                // Insert default value
                member.put(path, "");
            }
        } else {

        }
        // Return the final object        
        return object.toString();
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String read(final String filename) {
        final StringBuffer buffer = new StringBuffer();
        try {
            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filename), "UTF8"));
            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char) ch);
            }
            in.close();
            return buffer.toString();
        } catch (final IOException exc) {
            sLogger.failure(exc.toString());
        }
        return null;
    }

    public final static void write(final String output, final String filename) {
        try {
            final FileOutputStream fos = new FileOutputStream(filename);
            final Writer out = new OutputStreamWriter(fos, "UTF8");
            out.write(output);
            out.close();
        } catch (final IOException exc) {
            sLogger.failure(exc.toString());
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String normalize(final String input) {
        try {
            // Construct the XML input stream
            final ByteArrayInputStream stream = new ByteArrayInputStream(
                    input.getBytes("UTF-8"));
            // Construct the document builder
            final DocumentBuilder parser
                    = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Parse the document from stream
            final Document document = parser.parse(stream);
            // Finally close the stream
            stream.close();

            // Construct the XML transformer
            final Transformer transformer
                    = TransformerFactory.newInstance().newTransformer();
            // Construct the output writer
            final StreamResult result = new StreamResult(new StringWriter());
            // Construct the output source
            final DOMSource source = new DOMSource(document);
            // Transform the XML document
            transformer.transform(source, result);
            // Get the string representation
            final String output = result.getWriter().toString();
            // Return the normalized representation
            return output;
        } catch (final TransformerException | IOException |
                ParserConfigurationException | SAXException exc) {
            // Print an error message in this case
            sLogger.failure(exc.toString());
            // Return NULL at failure
            return null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String encodeXML(final String input) {
        // Encode the output XML string
        final String xmlencoded = StringEscapeUtils.escapeXml10(input);
        // Encode the output XML string
        final String urlencoded
                = xmlencoded;//.replaceAll(" ", "%20").replaceAll("\r\n", "%0A");
        // Return the encoded representation
        return urlencoded;
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String decodeXML(final String input) {
        // Decode the output XML string
        final String urldecoded
                = input;//.replaceAll("%20", " ").replaceAll("%0A", "\r\n");
        // Decode the output XML string
        final String xmldecoded = StringEscapeUtils.unescapeXml(urldecoded);
        // Return the decoded representation
        return xmldecoded;
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String encodeJSON(final String input) {
        // Encode the output XML string
        final String xmlencoded = StringEscapeUtils.escapeEcmaScript(input);
        // Encode the output XML string
        final String urlencoded
                = xmlencoded;//.replaceAll(" ", "%20").replaceAll("\r\n", "%0A");
        // Return the encoded representation
        return urlencoded;
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String decodeJSON(final String input) {
        // Decode the output XML string
        final String urldecoded
                = input;//.replaceAll("%20", " ").replaceAll("%0A", "\r\n");
        // Decode the output XML string
        final String xmldecoded = StringEscapeUtils.unescapeEcmaScript(urldecoded);
        // Return the decoded representation
        return xmldecoded;
    }

    ////////////////////////////////////////////////////////////////////////////
    public final static String parseVocapia(final String xml) {
        final JSONObject object = new JSONObject();
        // Insert the XML data
        object.put("xml", xml);
        try {
            // Parse the received XML string
            final ByteArrayInputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder parser = factory.newDocumentBuilder();
            final Document document = parser.parse(stream);
            // Get the XML tree root element
            final Element element = document.getDocumentElement();
            if (element.getTagName().equals("AudioDoc")) {
                // Infer the file confidence
                final NodeList channelLists = element.getElementsByTagName("ChannelList");
                if (channelLists.getLength() == 1) {
                    final Element channelList = ((Element) channelLists.item(0));
                    final NodeList channels = channelList.getElementsByTagName("Channel");
                    if (channels.getLength() == 1) {
                        final Element channel = ((Element) channels.item(0));
                        final String tconf = channel.getAttribute("tconf");
                        //System.err.println(tconf);
                        object.put("confidence", Double.valueOf(tconf));
                    }
                }
                // Construct the spoken text
                final NodeList segmentLists = element.getElementsByTagName("SegmentList");
                if (segmentLists.getLength() == 1) {
                    for (int i = 0; i < segmentLists.getLength(); i++) {
                        final StringBuilder builder = new StringBuilder();
                        final Element segmentList = ((Element) segmentLists.item(i));
                        final NodeList speechSegments = segmentList.getElementsByTagName("SpeechSegment");
                        if (speechSegments.getLength() > 0) {
                            for (int j = 0; j < speechSegments.getLength(); j++) {
                                final Element speechSegment = ((Element) segmentLists.item(j));
                                final NodeList words = speechSegment.getElementsByTagName("Word");
                                if (words.getLength() > 0) {
                                    for (int k = 0; k < words.getLength(); k++) {
                                        final Element word = ((Element) words.item(k));
                                        // Get the word attributes
                                        final String stime = word.getAttribute("stime");
                                        final String dur = word.getAttribute("dur");
                                        final String conf = word.getAttribute("conf");
                                        final String text = word.getTextContent().trim();
                                        //
                                        builder.append(text).append(" ");
                                    }
                                }
                            }
                        }
                        // Insert the text data
                        object.put("text", builder.toString());
                    }
                }
            }
        } catch (final DOMException | IOException | SAXException | NumberFormatException | ParserConfigurationException exc) {
            // Print some information
            exc.printStackTrace();
        }
        // Return the final string
        return object.toString(2);
    }
}
