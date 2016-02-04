package owlSpeak.kristina;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class KIParser {
	
	static String content = "";
	static boolean started = false;

	public static void parse(InputStream file){
	    try {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		DefaultHandler handler = new DefaultHandler() {

		boolean bfname = false;
		boolean blname = false;
		boolean bnname = false;
		boolean bsalary = false;

		public void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
			if (qName.equalsIgnoreCase("dto:BiographicalAspect")) {
				started = true;
			}

		}

		public void endElement(String uri, String localName,
			String qName) throws SAXException {
			if (qName.equalsIgnoreCase("dto:BiographicalAspect")) {
			System.out.println(content);
			content = "";
			started = false;
			}

		}

		public void characters(char ch[], int start, int length) throws SAXException {

			if(started){
				content = content.concat(ch.toString());
			}

		}

	     };

	       saxParser.parse(file, handler);
	 
	     } catch (Exception e) {
	       e.printStackTrace();
	     }
	  
	   }
	
}
