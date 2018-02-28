package owlSpeak.servlet.grammar;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.TreeSet;
import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;

import owlSpeak.Grammar;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
/**
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 *
 */
public class GrammarJSGFasFile extends GrammarJSGF {

	private String fileName = "move.jsgf";

	@Override
	public void buildGrammar(Element element, Vector<CoreMove> grammar,
			String user) {
		TreeSet<Grammar> externalGrammars = new TreeSet<Grammar>();
		String grammarString = getGrammarString(grammar, user, externalGrammars);
		
		Element grammar1 = new Element("grammar");
		if (grammarString != null) {
			grammar1.setAttribute("type", "application/x-jsgf");
			grammar1.setAttribute("lang", Settings.asrMode,
					Namespace.XML_NAMESPACE);
			grammar1.setAttribute("mode", "voice");

			grammar1.setAttribute("src", "/" + fileName);
//			element.addContent(grammar1);

			Writer out = null;
			try {
				out = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(
								Settings.homePath + "/" + fileName, false),
						"UTF-8"));
				out.write(grammarString);
				out.close();
			} catch (IOException e) {
				System.err.println("Grammar file not generated!");
				e.printStackTrace();

			}
		}

		for (Element grammar2 : handleExternalGrammars(grammar, externalGrammars))
			element.addContent(grammar2);
		
		if (grammarString != null)
			element.addContent(grammar1);
	}
}
