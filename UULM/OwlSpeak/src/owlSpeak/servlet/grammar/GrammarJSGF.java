package owlSpeak.servlet.grammar;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.Vector;

import javax.speech.EngineException;
import javax.speech.EngineStateError;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import javax.speech.recognition.RuleSequence;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Namespace;
import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.BeliefSpace;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammar;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammarFactory;
import edu.cmu.sphinx.jsgf.JSGFRuleGrammarManager;
import edu.cmu.sphinx.jsgf.parser.JSGFParser;

/**
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 *
 */
public class GrammarJSGF implements IGrammar {

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
			element.addContent(grammar1);

			CDATA script = new CDATA(grammarString);
			grammar1.addContent(script);
		}

		for (Element grammar2 : handleExternalGrammars(grammar,
				externalGrammars))
			element.addContent(grammar2);
		
		if (grammarString != null)
			element.addContent(grammar1);

	}

	@Override
	public void buildSleepGrammar(Element element) {
		Element dummyGrammar = new Element("grammar");
		dummyGrammar.setAttribute("type", "application/x-jsgf");
		dummyGrammar.setAttribute("lang", Settings.asrMode,
				Namespace.XML_NAMESPACE);
		dummyGrammar.setAttribute("mode", "voice");
		String grammarString = " [ hokuspokus { dummy \"hokuspokus\" } ]";
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

	/**
	 * make sure that first grammar in grammars is main grammar which contains
	 * the grammar rule move
	 */
	@Override
	public String[] parse(String parseString, String[] grammars) {
	
		String grammarRuleName = "move";
	
		String[] rules = null;
	
		JSGFRuleGrammar jsgfRuleGrammar = null;
		JSGFRuleGrammarManager manager = new JSGFRuleGrammarManager();
		JSGFRuleGrammarFactory ruleGrammarFactory = new JSGFRuleGrammarFactory(
				manager);
		try {
			StringReader reader = new StringReader(grammars[0]);
			jsgfRuleGrammar = JSGFParser.newGrammarFromJSGF(reader,
					ruleGrammarFactory);
			for (int i = 1; i < grammars.length; i++) {
				FileReader fReader = null;
				try {
					fReader = new FileReader(grammars[i]);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JSGFParser.newGrammarFromJSGF(fReader, ruleGrammarFactory);
			}
	
		} catch (JSGFGrammarParseException e1) {
			return null;
		}
	
		BaseRecognizer jsapiRecognizer = new BaseRecognizer(manager);
		try {
			jsapiRecognizer.allocate();
		} catch (EngineException e1) {
			return null;
		} catch (EngineStateError e1) {
			return null;
		}
		RuleGrammar ruleGrammar = new BaseRuleGrammar(jsapiRecognizer,
				jsgfRuleGrammar);
		RuleParse ruleParse = null;
		try {
			ruleParse = ruleGrammar.parse(parseString, grammarRuleName);
		} catch (IllegalArgumentException e) {
			return null;
		} catch (GrammarException e) {
			return null;
		}
		if (ruleParse != null) {
			RuleSequence rule = (RuleSequence) ruleParse.getRule();
			Rule[] r = rule.getRules();
			rules = new String[r.length];
			for (int i = 0; i < r.length; i++) {
				rules[i] = r[i].toString();
			}
		}
	
		try {
			jsapiRecognizer.deallocate();
		} catch (EngineException e) {
			return null;
		} catch (EngineStateError e) {
			return null;
		}
	
		return rules;
	}

	@Override
	public String[] grammarsOfMove(Move move, String user, OwlSpeakOntology onto) {
	
		TreeSet<Grammar> externalGrammars = new TreeSet<Grammar>();
		TreeSet<Grammar> generalGrammars = new TreeSet<Grammar>();
		String[] grammarsOfMove = null;
		
		Grammar grammar = move.getGrammar();
		String preGrammarString = "#JSGF V1.0;\ngrammar move;\n";
		String grammarString = "public <move> = ";
		
		String tGrammarString = getGrammarStringSingleGrammar(grammar, onto,
				move, generalGrammars, user);
		
		if (tGrammarString != null) {
			
			grammarString += tGrammarString;
			grammarString += ";";
			grammarString = handleGeneralGrammars(generalGrammars, grammarString, externalGrammars);
			
			Vector<String> eg = loadExternalGrammars(grammar, externalGrammars,	onto);
			
			grammarsOfMove = new String[eg.size() + 1];
			grammarsOfMove[0] = preGrammarString + grammarString;
			for (int i = 0; i < eg.size(); i++)
				grammarsOfMove[i + 1] = eg.elementAt(i);
		}
		
		return grammarsOfMove;
	}

	protected String getGrammarString(Vector<CoreMove> grammar, String user,
			TreeSet<Grammar> externalGrammars) {
		String preGrammarString = "#JSGF V1.0;\ngrammar move;\n";

		String grammarString = "public <move> = ";
		String tGrammarString = "";
		int alternativesCount = 0;

		TreeSet<Grammar> generalGrammars = new TreeSet<Grammar>();

		for (CoreMove tempCoreMv : grammar) {
			Move tempMv = tempCoreMv.move;
			OSFactory factory = tempCoreMv.onto.factory;
			Grammar tempres = tempMv.getGrammar();
			if (tempres != null) {
				Grammar tempGr = factory.getGrammar(tempres.getLocalName());

				// ------------------------- start here

				String tempString = getGrammarStringSingleGrammar(tempGr,
						tempCoreMv.onto, tempMv, generalGrammars, user);

				if (tempString != null) {
					if (alternativesCount > 0)
						tGrammarString += " | ";

					alternativesCount++;
					tGrammarString += tempString;
				}
				// end here
			}
		}

		// if there are no grammars defined
		if (tGrammarString == "")
			return null;

		grammarString += tGrammarString;
		grammarString += ";";
		
		grammarString = handleGeneralGrammars(generalGrammars, grammarString, externalGrammars);

		return preGrammarString + grammarString;
	}

	protected String getGrammarStringOld(Vector<CoreMove> grammar, String user,
			TreeSet<Grammar> externalGrammars) {

		String preGrammarString = "#JSGF V1.0;\ngrammar move;\n";

		String grammarString = "public <move> = ";
		String tGrammarString = "";
		int alternativesCount = 0;

		TreeSet<Grammar> generalGrammars = new TreeSet<Grammar>();

		for (CoreMove tempCoreMv : grammar) {
			Move tempMv = tempCoreMv.move;
			OSFactory factory = tempCoreMv.onto.factory;
			Grammar tempres = tempMv.getGrammar();
			if (tempres != null) {
				Grammar tempGr = factory.getGrammar(tempres.getLocalName());
				if (!tempGr.hasGrammarFile()) {
					String tempString = " ";// " (";
					if (tempGr != null && tempGr.hasGrammarString()) {
						Iterator<OWLLiteral> itGr = tempGr.getGrammarString()
								.iterator();
						int innerAlternativesCount = 0;
						if (itGr.hasNext()) {
							itGr.next();
							tempString += " ( ";
							tempString += parseGrammarVariables(
									tempCoreMv.onto.beliefSpace[Settings
											.getuserpos(user)],
									tempMv, tempCoreMv.onto, tempCoreMv.core,
									user);
							tempString += " ) {";
							// tempString += " {";
							tempString += tempCoreMv.onto.Name;
							tempString += "_a1n1d_";
							tempString += tempMv.getLocalName();
							tempString += "}   ";
							if (innerAlternativesCount > 0)
								tempString += "|";
							innerAlternativesCount++;
						} else {
							tempString += "ERROR";
							System.out.print("EVIL!");
						}
						tempString += "";// ")";

						if (alternativesCount > 0)
							tGrammarString += " | ";

						alternativesCount++;
						tGrammarString += tempString;
					}

					// beware of loops in grammar definition from ontology
					// ==> grammar forest

					Collection<Grammar> gr = tempGr.getGeneralGrammars();
					Queue<Grammar> grammarQueue = new LinkedList<Grammar>();

					if (gr != null) {
						generalGrammars.addAll(gr);
						grammarQueue.addAll(gr);
						while (!grammarQueue.isEmpty()) {
							Grammar g = grammarQueue.poll();
							gr = g.getGeneralGrammars();
							if (gr != null) {
								grammarQueue.addAll(gr);
								generalGrammars.addAll(gr);
							}
						}
					}
				}
			}
		}

		// if there are no grammars defined
		if (tGrammarString == "")
			return null;

		grammarString += tGrammarString;
		grammarString += ";";

		// boolean generalGrammarsAdded = false;
		// do {
		// generalGrammarsAdded = false;
		// TreeSet<Grammar> tempGeneralGrammars = new TreeSet<Grammar>();
		// for (Grammar g : generalGrammars) {
		// Collection<Grammar> gG = g.getGeneralGrammars();
		// if (gG != null) {
		// tempGeneralGrammars.addAll(gG);
		// }
		// }
		// if (!generalGrammars.containsAll(tempGeneralGrammars)) {
		// generalGrammars.addAll(tempGeneralGrammars);
		// generalGrammarsAdded = true;
		// }
		// } while (generalGrammarsAdded);

		String imports = "";

		for (Grammar g : generalGrammars) {
			if (g.hasGrammarString()) {
				for (OWLLiteral l : g.getGrammarString()) {
					if (l.getLang().equalsIgnoreCase(
							Settings.asrMode.substring(0, 2))) {
						grammarString += "\n\n<" + g.getLocalName() + "> = "
								+ l.getLiteral() + ";";
					}
				}

			} else if (g.hasGrammarFile()) {
				imports += "import <" + g.getLocalName() + ".*>;\n";
				externalGrammars.add(g);
			}
		}
		return preGrammarString + imports + grammarString;
	}

	protected String getGrammarStringSingleGrammar(Grammar tempGr,
			OwlSpeakOntology onto, Move tempMv,
			TreeSet<Grammar> generalGrammars, String user) {
		String grammarString = "";
		if (!tempGr.hasGrammarFile()) {
			if (tempGr != null && tempGr.hasGrammarString()) {
				Iterator<OWLLiteral> itGr = tempGr.getGrammarString()
						.iterator();
				int innerAlternativesCount = 0;
				if (itGr.hasNext()) {
					itGr.next();
					grammarString += " ( ";
					grammarString += parseGrammarVariables(
							onto.beliefSpace[Settings.getuserpos(user)],
							tempMv, onto, Core.getCore(), user);
					grammarString += " ) {";
					grammarString += onto.Name;
					grammarString += "_a1n1d_";
					grammarString += tempMv.getLocalName();
					grammarString += "}   ";
					if (innerAlternativesCount > 0)
						grammarString += "|";
					innerAlternativesCount++;
				} else {
					grammarString += " ERROR";
					System.out.print("EVIL!");
				}
			}

			// beware of loops in grammar definition from ontology
			// ==> grammar forest not allowed

			Collection<Grammar> gr = tempGr.getGeneralGrammars();
			Queue<Grammar> grammarQueue = new LinkedList<Grammar>();

			if (gr != null) {
				generalGrammars.addAll(gr);
				grammarQueue.addAll(gr);
				while (!grammarQueue.isEmpty()) {
					Grammar g = grammarQueue.poll();
					gr = g.getGeneralGrammars();
					if (gr != null) {
						grammarQueue.addAll(gr);
						generalGrammars.addAll(gr);
					}
				}
			}
		}

		if ("".equals(grammarString))
			return null;
		return grammarString;
	}

	protected String handleGeneralGrammars(TreeSet<Grammar> generalGrammars,
			String grammarString, TreeSet<Grammar> externalGrammars) {

		String imports = "";

		for (Grammar g : generalGrammars) {
			if (g.hasGrammarString()) {
				for (OWLLiteral l : g.getGrammarString()) {
					if (l.getLang().equalsIgnoreCase(
							Settings.asrMode.substring(0, 2))) {
						grammarString += "\n\n<" + g.getLocalName() + "> = "
								+ l.getLiteral() + ";";
					}
				}

			} else if (g.hasGrammarFile()) {
				imports += "import <" + g.getLocalName() + ".*>;\n";
				externalGrammars.add(g);
			}
		}
//		return grammarString;
		return imports + grammarString;
	}

	/**
	 * Creates a grammar xml tag for each external grammar file.
	 * 
	 * @param moves
	 * @return
	 */
	protected Vector<Element> handleExternalGrammars(Vector<CoreMove> moves,
			TreeSet<Grammar> grammars) {
		Vector<Element> externalGrammars = new Vector<Element>();

		for (CoreMove cm : moves) {
			Move tempMv = cm.move;
			OSFactory factory = cm.onto.factory;
			Grammar tempres = tempMv.getGrammar();
			if (tempres != null) {
				Grammar tempGr = factory.getGrammar(tempres.getLocalName());
				grammars.add(tempGr);
			}
		}

		for (Grammar g : grammars) {
			if (g.hasGrammarFile()) {
				Element grammar = new Element("grammar");
				grammar.setAttribute("src", g.getGrammarFile());
				grammar.setAttribute("type", "application/x-jsgf");
				grammar.setAttribute("lang", Settings.asrMode,
						Namespace.XML_NAMESPACE);
				externalGrammars.add(grammar);
			}
		}

		return externalGrammars;
	}

	protected Vector<String> loadExternalGrammars(Grammar grammar,
			TreeSet<Grammar> grammars, OwlSpeakOntology onto) {

		Vector<String> externalGrammars = new Vector<String>();

		OSFactory factory = onto.factory;
		if (grammar != null) {
			Grammar tempGr = factory.getGrammar(grammar.getLocalName());
			grammars.add(tempGr);
		}

		for (Grammar g : grammars) {
			if (g.hasGrammarFile()) {
				externalGrammars.add(g.getGrammarFile());
			}
		}

		return externalGrammars;
	}

	/**
	 * combines the CoreMove's Grammars to a String
	 * 
	 * @param bel
	 *            the user's BeliefSpace
	 * @param onto
	 *            TODO
	 * @param core
	 *            TODO
	 * @return the Grammar-String
	 */
	private String parseGrammarVariables(BeliefSpace bel, Move move,
			OwlSpeakOntology onto, Core core, String user) {
		String grammar = "";// "(";
		Collection<OWLLiteral> gramColl = move.getGrammar().getGrammarString();
		Iterator<OWLLiteral> gramIt = gramColl.iterator();
		int alternativesCounter = 0;
		while (gramIt.hasNext()) {
			if (alternativesCounter > 0)
				grammar = grammar + " | " + (gramIt.next()).getLiteral();
			else
				grammar = grammar + " " + (gramIt.next()).getLiteral();
			alternativesCounter++;
		}
		grammar += "";// ")";
		grammar = CoreMove.parseStringVariables(grammar, onto.factory, core,
				bel, user);
		return CoreMove.parseStringSemantics(grammar, bel, onto, user);
	}
}
