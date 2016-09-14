package eu.kristina.vsm.util;

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
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Gregor Mehlmann
 */
public final class KristinaUtility {

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
//     public final static JSONObject create() {
//         final JSONObject object = new JSONObject();
//         object.
//     }
     
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

}
