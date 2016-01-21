package owlSpeak.servlet.grammar;

import java.util.Iterator;
import java.util.Vector;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Namespace;
import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;

public class GrammarGSL implements IGrammar {

	@Override
	public void buildGrammar(Element element, Vector<CoreMove> grammar,
			String user) {
		OSFactory factory;
		Element grammar1 = new Element("grammar");
		grammar1.setAttribute("type", "application/x-gsl");
		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
		grammar1.setAttribute("mode", "voice");
		String grammarString = " [";
		Move tempMv;

		while (!grammar.isEmpty()) {
			Grammar tempGr;
			CoreMove tempCoreMv;
			tempCoreMv = grammar.firstElement();
			grammar.remove(0);
			tempMv = tempCoreMv.move;
			factory = tempCoreMv.onto.factory;
			Grammar tempres = tempMv.getGrammar();
			if (tempres == null)
				continue;
			tempGr = factory.getGrammar(tempres.getLocalName());
			String tempString = " ";
			if (tempGr == null)
				continue;
			if (!tempGr.hasGrammarString())
				continue;
			Iterator<OWLLiteral> itGr = tempGr.getGrammarString().iterator();
			if (itGr.hasNext()) {
				itGr.next();
				tempString = "  ";
				tempString += tempCoreMv.parseGrammarVariables(
						tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)],
						user);
				tempString += "  { <move \"";
				tempString += tempCoreMv.onto.Name;
				tempString += "_a1n1d_";
				tempString += tempMv.getLocalName();
				tempString += "\"> }   ";
			} else {
				tempString += "ERROR";
				System.out.print("EVIL!");
			}

			grammarString += tempString;
		}
		grammarString += "]";
		CDATA script = new CDATA(grammarString);
		grammar1.addContent(script);

		element.addContent(grammar1);
	}

	@Override
	public void buildSleepGrammar(Element element) {
		Element dummyGrammar = new Element("grammar");
		dummyGrammar.setAttribute("type", "application/x-gsl");
		dummyGrammar.setAttribute("lang", Settings.asrMode,
				Namespace.XML_NAMESPACE);
		dummyGrammar.setAttribute("mode", "voice");
		String grammarString = " [ (hokuspokus) { <dummy \"hokuspokus\"> } ]";
		CDATA dummyGrammarData = new CDATA(grammarString);
		dummyGrammar.addContent(dummyGrammarData);
		element.addContent(dummyGrammar);
	}

	@Override
	public void buildSolveConflict(Element element, Vector<String[]> grammar) {
		Element grammar1 = new Element("grammar");
		grammar1.setAttribute("type", "application/x-gsl");
		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
		grammar1.setAttribute("mode", "voice");
		String grammarString = " [";
		Iterator<String[]> gramIter = grammar.iterator();
		while (gramIter.hasNext()) {
			String[] tempStr = gramIter.next();
			String tempString = " ";
			tempString = "  ";
			tempString += "[(" + tempStr[2].toLowerCase() + ")]";
			tempString += "  { <move \"";
			tempString += tempStr[0];
			tempString += "_a1n1d_";
			tempString += tempStr[1];
			tempString += "\"> }   ";
			grammarString += tempString;
		}
		grammarString += "]";
		CDATA cdata = new CDATA(grammarString);
		grammar1.addContent(cdata);

		element.addContent(grammar1);
	}

	@Override
	public String[] parse(String string, String[] grammars) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] grammarsOfMove(Move move, String user, OwlSpeakOntology onto) {
		// TODO Auto-generated method stub
		return null;
	}
}
