package owlSpeak.kristina;



import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.Vector;

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
import owlSpeak.servlet.grammar.IGrammar;

public class KristinaGrammar implements IGrammar {
	//Externe Grammatiken und Variablen werden noch nicht unterstützt
	
	
	
	@Override
	public void buildGrammar(Element element, Vector<CoreMove> grammar,
			String user) {
		// TODO Auto-generated method stub
//		System.out.println("buildGrammar SRGS: Anfang");
		TreeSet<Grammar> externalGrammars = new TreeSet<Grammar>();
		Collection<Element> rules = getGrammarString(grammar, user, externalGrammars);
		
//		System.out.println("buildGrammar SRGS: Element" + element + "Vector: " + grammar);
		
		if (rules != null) {
			element.setName("grammar");
			element.setAttribute("root", "rootRule");
			element.setAttribute("lang", Settings.asrMode,
					Namespace.XML_NAMESPACE);
			element.setAttribute("mode", "voice");
			element.setAttribute("tag-format","semantics/1.0");
			element.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
			element.setAttribute("version", "1.0");
			
			element.addContent(rules);
		}

		for (Element grammar2 : handleExternalGrammars(grammar,
				externalGrammars))
			element.addContent(grammar2);
		
	}

	
	// leicht umändern!!! Grammatik Teil anpassen, sonst wie JSGF
	@Override
	public void buildSleepGrammar(Element element) {
		// TODO Auto-generated method stub

	}

	// nachher, erstmal nicht wichtig
	@Override
	public void buildSolveConflict(Element element, Vector<String[]> grammar) {

	}


	// NICHT wichtig, wenn das mit dem Extrahieren klappt!!
	@Override
	public String[] parse(String parseString, String[] grammars) {
		
/*		String grammarRuleName = "move";
		
		String[] rules = null;
	
		RuleGrammar ruleGrammar = null;


		try {
			StringReader reader = new StringReader(grammars[0]);
			
			ruleGrammar = RuleGrammar.parse(parseString, grammars[0]);
			
			
			for (int i = 1; i < grammars.length; i++) {
				FileReader fReader = null;
				try {
					fReader = new FileReader(grammars[i]);
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
	
		} catch (GrammarException e1) {
			return null;
		}
*/		
		return null;
	}

	@Override
	public String[] grammarsOfMove(Move move, String user, OwlSpeakOntology onto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Collection<Element> getGrammarString(Vector<CoreMove> grammar, String user,
			TreeSet<Grammar> externalGrammars) {
		
		LinkedList<Element> result = new LinkedList<Element>();
		
		Element rootRule = new Element("rule");
		rootRule.setAttribute("scope", "public");
		rootRule.setAttribute("id", "rootRule");
		rootRule.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
		Element rootChoice = new Element("one-of");
		rootChoice.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
		rootRule.addContent(rootChoice);
		Element rootTag = new Element("tag");
		rootTag.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
		rootTag.addContent("out.TAGutterance = \"'\" + meta.current().text + \"'\";");
		rootRule.addContent(rootTag);
		
		result.add(rootRule);

		for (CoreMove tempCoreMv : grammar) {
			Move tempMv = tempCoreMv.move;
			OSFactory factory = tempCoreMv.onto.factory;
			Grammar tempres = tempMv.getGrammar();
		
			if (tempres != null) {
				Grammar tempGr = factory.getGrammar(tempres.getLocalName());
				String tempGrName=tempres.getLocalName();
				
				Element rootElem = new Element("item");
				rootElem.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				Element elemRef = new Element("ruleref");
				elemRef.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				elemRef.setAttribute("uri", "#"+tempGrName);
				Element elemTag = new Element("tag");
				elemTag.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				elemTag.addContent("out = rules."+tempGrName+";");
				rootElem.addContent(elemRef);
				rootElem.addContent(elemTag);
				rootChoice.addContent(rootElem);
				
				Element newRule = new Element("rule");
				newRule.setAttribute("id", tempGrName);
				newRule.setAttribute("scope", "private");
				newRule.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				Element ruleTag = new Element("tag");
				ruleTag.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				ruleTag.addContent("out = new Object();\nout.TAGfunction = \""+tempMv+"\";");
				Element ruleChoice = new Element("one-of");
				ruleChoice.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				newRule.addContent(ruleChoice);
				newRule.addContent(ruleTag);
				
				result.add(newRule);
				
				for( OWLLiteral literal : tempGr.getGrammarString()){
					ruleChoice.addContent(parseLiteral(literal.getLiteral()));
				}
			}
		}
		return result;
	}
	
	private Element parseLiteral(String l){
		Element result = new Element("item");
		result.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
		String s = "";
		
		for(int i = 0; i < l.length(); i++){
			if(l.charAt(i) == '('){
				result.addContent(s);
				s = "";
				String tmp = "";
				i++;
				int counter = 1;
				while(counter != 0){
					tmp += l.charAt(i);
					i++;
					if(l.charAt(i)=='('){
						counter += 1;
					}else if(l.charAt(i) == ')'){
						counter -=1;
					}
				}
				Element choice = new Element("one-of");
				choice.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				String[] choices = tmp.split("\\|");
				for(int j = 0; j < choices.length; j++){
					choice.addContent(parseLiteral(choices[j]));
				}
				
				result.addContent(choice);
			}
			else if(l.charAt(i)=='['){
				result.addContent(s);
				s = "";
				String tmp = "";
				i++;
				int counter = 1;
				while(counter != 0){
					tmp += l.charAt(i);
					i++;
					if(l.charAt(i)=='['){
						counter += 1;
					}else if(l.charAt(i) == ']'){
						counter -=1;
					}
				}
				
				Element el = new Element("item");
				el.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				el.setAttribute("repeat","0-1");
				
				el.addContent(parseLiteral(tmp));
				result.addContent(el);
			}
			else if(l.charAt(i)=='|'){
				Element choice = new Element("one-of");
				choice.setNamespace(Namespace.getNamespace("http://www.w3.org/2001/06/grammar"));
				result.addContent(s);
				s = "";
				choice.addContent(result);
				choice.addContent(parseLiteral(l.substring(i+1)));
				result = choice;
				break;
			}
			else{
				s += l.charAt(i);
			}
		}
		
		result.addContent(s);
		
		return result;
	}
	
	protected String getGrammarStringSingleGrammar(Grammar tempGr,
			OwlSpeakOntology onto, Move tempMv,
			TreeSet<Grammar> generalGrammars, String user) {
		System.out.println("SingleGrammar");
		
		String grammarString = "";
		if (!tempGr.hasGrammarFile()) {
			if (tempGr != null && tempGr.hasGrammarString()) {
				Iterator<OWLLiteral> itGr = tempGr.getGrammarString()
						.iterator();
				int innerAlternativesCount = 0;
				if (itGr.hasNext()) {
					itGr.next();
//					grammarString += " ( ";
					grammarString += parseGrammarVariables(
							onto.beliefSpace[Settings.getuserpos(user)],
							tempMv, onto, Core.getCore(), user);
					grammarString += "{";
					grammarString += onto.Name;
					grammarString += "_a1n1d_";
					grammarString += tempMv.getLocalName();
					grammarString += "}   ";
					if (innerAlternativesCount > 0)
						grammarString += "</item> <item>";
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
		{
			return null;
		}
//		System.out.println("getGrammarSingleGrammar:" + grammarString);
		
		return grammarString;
	}

	protected String handleGeneralGrammars(TreeSet<Grammar> generalGrammars,
			String grammarString, TreeSet<Grammar> externalGrammars) {
		System.out.println("handleGeneralGrammar");
		
//		String imports = "";

		for (Grammar g : generalGrammars) {
			if (g.hasGrammarString()) {
				grammarString += "<rule id =\"" + g.getLocalName() + "\"><one-of>";
				for (OWLLiteral l : g.getGrammarString()) {
					if (l.getLang().equalsIgnoreCase(
							Settings.asrMode.substring(0, 2))) {
						grammarString += "<item>" + l.getLiteral() + "</item>";
					}
				}
				grammarString = grammarString.replace("<addVar>", "<tag>out."+g.getLocalName()+"=\"");
				grammarString = grammarString.replace("</addVar>", "\"</tag>");
				
				grammarString += "</one-of></rule>";
			} else if (g.hasGrammarFile()) {
//				imports += "import <" + g.getLocalName() + ".*>;\n";
				externalGrammars.add(g);
			}
		}
		return grammarString;
//		return imports + grammarString;
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
		System.out.println("handleExternalGrammar");
		
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
				grammar.setAttribute("type", "application/srgs");
				grammar.setAttribute("lang", Settings.asrMode,
						Namespace.XML_NAMESPACE);
				externalGrammars.add(grammar);
			}
		}

		return externalGrammars;
	}

	protected Vector<String> loadExternalGrammars(Grammar grammar,
			TreeSet<Grammar> grammars, OwlSpeakOntology onto) {
		System.out.println("loadExternalGrammar");
		
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
		System.out.println("Variables");
		String grammar = "";// "(";
		Collection<OWLLiteral> gramColl = move.getGrammar().getGrammarString();
		Iterator<OWLLiteral> gramIt = gramColl.iterator();
		int alternativesCounter = 0;
		while (gramIt.hasNext()) {
			if (alternativesCounter > 0)
				grammar = grammar + " </item> <item> " + (gramIt.next()).getLiteral();
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
