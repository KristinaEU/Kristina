package eu.kristina.vsm.test;

import eu.kristina.vsm.util.KristinaUtility;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author Gregor Mehlmann
 */
public final class ParsingTestSuite {

    // Execute the json test
    public static final void main(final String args[]) {
        testParse();
    }

    private static void testParse() {
        final String xml_la = KristinaUtility.read("res/exp/output_language_analysis.txt");

        try {
            final ByteArrayInputStream stream = new ByteArrayInputStream(
                    xml_la.getBytes("UTF-8"));
            // Construct the XML document parser
            final DocumentBuilder parser
                    = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            // Parse the XML document from the stream
            final Document document = parser.parse(stream);
            // Finally close the stream and the URL
            stream.close();
            // Construct the XML document transformer
            final Transformer transformer
                    = TransformerFactory.newInstance().newTransformer();
            //  
            final StreamResult result = new StreamResult(new StringWriter());
            //
            final DOMSource source = new DOMSource(document);
            // Transform the XML document
            transformer.transform(source, result);
            // Get the string representation
            final String trans = result.getWriter().toString();
            System.out.println(trans);
        } catch (final TransformerException | IOException | ParserConfigurationException | SAXException exc) {
            // Print an error message in this case
            System.err.println(exc.toString());
        }

    }
}
