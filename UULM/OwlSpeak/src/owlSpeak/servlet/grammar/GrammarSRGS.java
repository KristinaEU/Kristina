package owlSpeak.servlet.grammar;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;
import java.util.Vector;
import java.lang.String;

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


public class GrammarSRGS implements IGrammar {
	
	String[] grammarNames = new String[15];
	int countGrammarNames = 0;

	@Override
	public void buildGrammar(Element element, Vector<CoreMove> grammar,
			String user) {

		TreeSet<Grammar> externalGrammars = new TreeSet<Grammar>();
		Vector<Element> grammarString = getGrammarString(grammar, user, externalGrammars);
		Element grammar1 = new Element("grammar");

		// setting the grammar attributes
		if (grammarString != null) {
			grammar1.setAttribute("type", "application/srgs+xml");

			grammar1.setAttribute("lang", Settings.asrMode,
					Namespace.XML_NAMESPACE);
			grammar1.setAttribute("version", "1.0");
			grammar1.setAttribute("mode", "voice");
			grammar1.setAttribute("root", "start");
			element.addContent(grammar1);

			grammar1.addContent(grammarString);

		}

		for (Element grammar2 : handleExternalGrammars(grammar,
				externalGrammars))
			element.addContent(grammar2);
	
		if (grammarString == null)
			System.out.println("buildGrammar: grammarString == null!!");
	}

	
	@Override
	public void buildSleepGrammar(Element element) {
		
	}

	@Override
	public void buildSolveConflict(Element element, Vector<String[]> grammar) {

	}


	// not needed
	@Override
	public String[] parse(String parseString, String[] grammars) {

	return null;
	}


	//not needed
	public String[] grammarsOfMove(Move move, String user, OwlSpeakOntology onto) {

		
	return null;
	}
	
	protected Vector<Element> getGrammarString(Vector<CoreMove> grammar, String user,
			TreeSet<Grammar> externalGrammars) {
		
		Vector<Element> outputVector = new Vector<Element>();

		//some often used Strings used for the tags
		String rule = "rule";
		String ruleref = "ruleref";
		String of = "one-of";
		String item = "item";
		String uri = "uri";
		String id = "id";
		String repeat = "repeat";
		
		
		// build the root rule of the grammar
		Element rootRuleXML = new Element(rule);
		rootRuleXML.setAttribute(id, "start");
		Element rootOneOf = new Element (of);
		rootRuleXML.addContent(rootOneOf);
		outputVector.add(rootRuleXML);

		TreeSet<Grammar> generalGrammars = new TreeSet<Grammar>();

		for (CoreMove tempCoreMv : grammar) {
			Move tempMv = tempCoreMv.move;
			OSFactory factory = tempCoreMv.onto.factory;
			String ontoName = tempCoreMv.onto.Name;
			Grammar tempres = tempMv.getGrammar();
			

			if (tempres != null) {
				Grammar tempGr = factory.getGrammar(tempres.getLocalName());
				String tempGrName=tempres.getLocalName();

					//build the rule, which is about to be handled
					Element tempRule = new Element(rule);
					tempRule.setAttribute(id, tempGrName);
					
					// add all allowed rules to the start rule
					Element tempRefRoot = new Element(ruleref);
					tempRefRoot.setAttribute("uri", "#" + tempGrName);
					Element refItem = new Element(item);
					refItem.addContent(tempRefRoot);
					rootOneOf.addContent(refItem);
				
					
					// get the grammar String
					String tempString = getGrammarStringSingleGrammar(tempGr,
							tempCoreMv.onto, tempMv, generalGrammars, user);

					// start the parsing of the JSGF String, if there is one
					if (tempString != null) {
						
						// char Index in tempString that is currently compared
						int i = 0;
						
						// String to which the parsed elements are added
						String grammarString = "";
						
						
						Element[] parentElements = new Element[50];
						int depth = 0;
						

						Element actParent = tempRule;
						parentElements[depth] = tempRule;

						//used to determine, if the '&' in the String is the start 
						//or the end a grammar with alternatives
						boolean inAlternative = false;

						//used to detect multi-slot grammars, needed to 
						//add the correct move name
						boolean isMultiSlot =false;

						// used to detect confirmation grammars
						int tagCounter = 0;
						
						// replace JSGF elements from the ontology with the corresponding 
						//SRGS elements
	
						//make sure, the grammar starts with '('
						if(tempString.startsWith("(")!= true){
							String prefix = "(";
							tempString.concat(prefix);
						}
						//changing the rule reference tag, so it would not be confused 
						//with tags during parsing
						tempString = tempString.replace("<", "§");
						tempString = tempString.replace(">", "§");
	

						// parsing the grammar String and building the XML structure
						while(i < tempString.length())
						{
							switch(tempString.charAt(i)){
								//starting group input
								case '(':{
									if(grammarString != ""){
										Element newItem = new Element(item);
										actParent.addContent(newItem);
										newItem.addContent(grammarString);
										grammarString = "";
									}
									Element itemAroundOneOf = new Element (item);
									actParent.addContent(itemAroundOneOf);
									Element newOneOf = new Element (of);
									itemAroundOneOf.addContent(newOneOf);
									depth++;
									parentElements[depth] = newOneOf;
									actParent = newOneOf;
									break;												
								}
								//ending group input
								case ')':{
									if(grammarString != ""){
										Element newItem = new Element(item);
										actParent.addContent(newItem);
										newItem.addContent(grammarString);
										grammarString = "";
									}
									depth--;
									actParent = parentElements[depth];
									break;
								}
								// handling the start of optional input
								case '[':{
									if(grammarString != ""){
										Element newItem = new Element(item);
										actParent.addContent(newItem);
										newItem.addContent(grammarString);
										grammarString = "";
									}
									Element optional = new Element(item);
									optional.setAttribute(repeat, "0-1");
									actParent.addContent(optional);
									depth++;
									parentElements[depth] = optional;
									actParent = optional;
									break;
								}
								// handling the end of optional input
								case ']':{
									if(grammarString != ""){
										Element newItem = new Element(item);
										actParent.addContent(newItem);
										newItem.addContent(grammarString);
										grammarString = "";
									}
									depth--;
									actParent = parentElements[depth];
									break;
								}
								// handling referenced rules in String
								case '§':{
									if(grammarString != ""){
										Element newItem = new Element(item);
										actParent.addContent(newItem);
										newItem.addContent(grammarString);
										grammarString = "";
									}
									i++;
									// parsing rule names directly here
									while(tempString.charAt(i) != '§'){
										grammarString = grammarString + tempString.charAt(i);
										i++;
									}
									//build needed tags
									Element refTagItem = new Element(item);
									Element rulerefVar = new Element(ruleref);
									refTagItem.addContent(rulerefVar);
									String garb = "GARBAGE";
									String nll = "NULL";
									String vid = "VOID";
									// check for special rules and if detected, handle accordingly
									if(grammarString.compareTo(garb) == 0 || grammarString.compareTo(nll) == 0  || grammarString.compareTo(vid) == 0 )
									{
										rulerefVar.setAttribute("special", grammarString);
									}
									// handling all other references
									else{
										rulerefVar.setAttribute(uri, "#"+ grammarString);
										
										Element tagRef = new Element("tag");
										String actSem = tempMv.getSemantic().toString();
										
										// handling one-slot grammars
										if(actSem.split("(, )+").length==1)
										{
											actSem = actSem.replace('[', ' ');
											actSem = actSem.replace(']', ' ');
											actSem = actSem.trim();
											tagRef.addContent("out.moveIntern = \"" + ontoName + "_a1n1d_" + tempMv.getLocalName() + "\"; out.varSubmitIntern = ' " + actSem + " ' + " + grammarString + ".varSubmitIntern;");
										}
										//handling multi-slot grammars
										else{
											
											tagRef.addContent("out.varSubmitIntern += rules.latest( ) + '; ';");
											isMultiSlot = true;
										}
										refTagItem.addContent(tagRef);
										tagCounter++;
									}
									actParent.addContent(refTagItem);
									grammarString = "";
									break;
								}
								// cutting out the move name from the String
								case '{':{
									if(grammarString != ""){
										Element newItem = new Element(item);
										actParent.addContent(newItem);
										newItem.addContent(grammarString);
										grammarString = "";
									}
									i++;
									while(tempString.charAt(i) != '}'){
										grammarString = grammarString + tempString.charAt(i);
										i++;
									}
									grammarString = "";
									break;
								}
								// handling alternatives inside a grammar String
								case '|':{
									Element newItem = new Element(item);
									actParent.addContent(newItem);
									newItem.addContent(grammarString);
									grammarString = "";
									break;
								}
								//starting and ending the alternative grammar input
								case '&':{
									if(inAlternative==false){
										Element alterOneOf = new Element(of);
										actParent.addContent(alterOneOf);
										depth++;
										parentElements[depth] = alterOneOf;
										actParent = alterOneOf;
										Element alternative = new Element(item);
										actParent.addContent(alternative);
										depth++;
										parentElements[depth] = alternative;
										actParent = alternative;
										inAlternative=true;
									}
									else{
										depth--;
										actParent = parentElements[depth];
										depth--;
										actParent = parentElements[depth];
										inAlternative=false;
									}
									break;
								}
								// splitting the alternative grammars into separate items
								case '*':{
									depth--;
									actParent = parentElements[depth];
									Element newItem = new Element(item);
									actParent.addContent(newItem);
									depth++;
									parentElements[depth] = newItem;
									actParent = newItem;

									
									break;
								}
								// adding chars to the grammarString until a special char appears and specific action is taken
								default:{
									grammarString = grammarString+tempString.charAt(i);
									break;
								}
							}
							i++;
						}
						// adding the last item, if there is one
						if(grammarString != ""){
							Element newItem = new Element(item);
							actParent.addContent(newItem);
							newItem.addContent(grammarString);
							grammarString = "";
						}
						
						// adding the move tag to multi-slot grammars
						if(isMultiSlot == true){
							Element moveMultiSlot = new Element("tag");
							moveMultiSlot.addContent("out.moveIntern = \"" + ontoName + "_a1n1d_" + tempMv.getLocalName() + "\";");
							tempRule.addContent(moveMultiSlot);
						}
						
						// handling confirmation grammars; affirm grammars have to be declared as PSEUDO, deny grammars do not
						if(tagCounter == 0){
							Element endTagRef = new Element("tag");
						
							String actSemEnd = tempMv.getSemantic().toString();
							actSemEnd = actSemEnd.replace('[', ' ');
							actSemEnd = actSemEnd.replace(']', ' ');
							actSemEnd = actSemEnd.trim();
							if(actSemEnd==" ")
							{
								endTagRef.addContent("out.moveIntern = \"" + ontoName + "_a1n1d_" + tempMv.getLocalName() + "\";");
							}
							else
							{
								endTagRef.addContent("out.moveIntern = \"" + ontoName + "_a1n1d_" + tempMv.getLocalName() + "\"; out.varSubmitIntern = \""+ actSemEnd +" CONFIRM\";");
	
							}
	
								tempRule.addContent(endTagRef);
						}
					}
					
	
					outputVector.add(tempRule);	
			}		
		}
		
		
		outputVector.addAll(handleGeneralGrammars(generalGrammars, externalGrammars));

		return outputVector;
	

}

	
	protected String getGrammarStringSingleGrammar(Grammar tempGr,
			OwlSpeakOntology onto, Move tempMv,
			TreeSet<Grammar> generalGrammars, String user) {
		String grammarString = "";
		if (!tempGr.hasGrammarFile()) {
			if (tempGr != null && tempGr.hasGrammarString()) {
				Iterator<OWLLiteral> itGr = tempGr.getGrammarString()
						.iterator();
				if (itGr.hasNext()) {
					itGr.next();
					grammarString = grammarString + parseGrammarVariables(
							onto.beliefSpace[Settings.getuserpos(user)],
							tempMv, onto, Core.getCore(), user);
					// handling grammars with alternative strings by adding '&' for parsing purposes
					if(grammarString.contains("*")){
						grammarString = "&" + grammarString + "&";
					}
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

		return grammarString;
	}

	
	// building the rules of the general grammars with the return tags, used for the variables
	protected Vector<Element> handleGeneralGrammars(TreeSet<Grammar> generalGrammars,
			TreeSet<Grammar> externalGrammars) {


		Vector<Element> output = new Vector<Element>();

		for (Grammar g : generalGrammars) {
			
			if (g.hasGrammarString()) {

					Element ruleXML = new Element("rule");
					ruleXML.setAttribute("id", g.getLocalName());
					Element oneOf = new Element("one-of");
					ruleXML.addContent(oneOf);
										
					for (OWLLiteral l : g.getGrammarString()) {
						if (l.getLang().equalsIgnoreCase(
								Settings.asrMode.substring(0, 2))) {
							Element actItem = new Element("item");
							actItem.addContent(l.getLiteral());
							Element actTag = new Element("tag");
							actTag.addContent("out.varSubmitIntern = \"" +l.getLiteral() + "\";");
							actItem.addContent(actTag);
							oneOf.addContent(actItem);
							
						}
						
					}
					
					output.add(ruleXML);

			} else if (g.hasGrammarFile()) {
				Element importsXML = new Element("g.getLocalName()");
				
				output.add(importsXML);
				
				externalGrammars.add(g);
			}

		}



		
		return output;

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
				grammar.setAttribute("type", "application/srgs+xml");
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

	private String parseGrammarVariables(BeliefSpace bel, Move move,
			OwlSpeakOntology onto, Core core, String user) {
		String grammar = "";// "(";
		Collection<OWLLiteral> gramColl = move.getGrammar().getGrammarString();
		Iterator<OWLLiteral> gramIt = gramColl.iterator();
		int alternativesCounter = 0;
		while (gramIt.hasNext()) {
			// tagging the alternative grammars with '*' for parsing purposes
			if (alternativesCounter > 0)
				grammar = grammar + "*" + (gramIt.next()).getLiteral();
	
			else
				grammar =  grammar + " " + (gramIt.next()).getLiteral();
			alternativesCounter++;
		}
		grammar += "";
		grammar = CoreMove.parseStringVariables(grammar, onto.factory, core,
				bel, user);
		return CoreMove.parseStringSemantics(grammar, bel, onto, user);
	}
	

//	public static void main(String [ ] args){
//
//	}
}

