package owlSpeak.servlet.document;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Vector;



import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.plugin.Result;
import owlSpeak.servlet.grammar.IGrammar;


/** 
 * used for generating Voice XML documents with grammars written in Nuance GSL and special processing of nomatch-events
 * @see org.jdom.Document
 * @author Dan Denich, Maximilian Grotz
 *
 */
public class EnhancedVoiceDocument extends VoiceDocument implements OwlDocument{
	
	private static final long serialVersionUID = -9116205730578381542L;

	public EnhancedVoiceDocument(IGrammar _grammar) {
		super(_grammar);
		generateDocument();
	};
	
	/**
	 * generates the vxml root element and processing instructions
	 */
	public void generateDocument(){
		ProcessingInstruction access = new org.jdom.ProcessingInstruction("access-control", "allow=\"*\"");
		Element vxml = new Element("vxml");
		vxml.setAttribute("version", "2.1");
		newProp(vxml, "recordutterance", "true");
		this.setRootElement(vxml);
		this.addContent(access);
	}
	
	/**
	 * generates a document that will pause the dialogue.
	 * should get the sleep-state from whereAmI+"?com=getsleep"
	 * @param nextPath the netadress of the servlet aka whereAmI
	 * @param user the name of the user
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return the filled document
	 */
	public EnhancedVoiceDocument generateSleep(String nextPath, String user, String noInputCounter){
		EnhancedVoiceDocument VDoc = new EnhancedVoiceDocument(grammarObject);
		Element mainForm = new Element("form");
		Element field = new Element("field");
		field.setAttribute("name", "dummy");
		newVar(mainForm,"com","'request'");
		newVar(mainForm,"user","'"+user+"'");
		newVar(mainForm,"counter",null);
		newProp(field, "timeout", "3s");
		newProp(field, "maxspeechtimeout", "3s");
		Element dummyGrammar = new Element("grammar");
		dummyGrammar.setAttribute("type", "application/x-gsl");
		dummyGrammar.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
		dummyGrammar.setAttribute("mode", "voice");
		field.addContent(dummyGrammar);
		String grammarString = 	" [ (hokuspokus) { <dummy \"hokuspokus\"> } ]";
		CDATA dummyGrammarData = new CDATA(grammarString);
		dummyGrammar.addContent(dummyGrammarData);
		Element filled = new Element("filled");
		field.addContent(filled);
		Element clear1 = new Element("clear");
		clear1.setAttribute("namelist", "dummy");
		filled.addContent(clear1);		
		newSubmit(filled, nextPath, "com user");
		mainForm.addContent(field);
		Element clear2 = new Element("clear");
		clear2.setAttribute("namelist", "dummy");

		newCatch(field, nextPath, noInputCounter, "noinput nomatch error.badfetch error.unsupported.language error.noresource").addContent(clear2);
		VDoc.getRootElement().addContent(mainForm);
		return VDoc;
	}
	
	/**
	 * generates a new form, adds some var Elements and properties
	 * @param agenda a String that contain the name of the agenda 
	 * @param user the current user 
	 * @return the Form Element
	 */
	public Element newDialogueForm(String agenda, String user){
		Element newForm = new Element("form");
		newForm.setAttribute("id", "mainForm");
		newVar(newForm,"com",null);
		newVar(newForm,"counter",null);
		newVar(newForm,"event",null);
		newVar(newForm,"nomatchRec",null);
		
		if(agenda!=null){
			newVar(newForm,"agenda","'"+agenda+"'");
		}
		else{
			newVar(newForm,"agenda",null);
		}
		newVar(newForm,"speak",null);
		newVar(newForm,"user","'"+user+"'");
		newProp(newForm, "inputmodes", "voice");
		newProp(newForm, "bargein", "false");
		newProp(newForm, "confidencelevel", "0.55");
		newProp(newForm, "maxspeechtimeout", Settings.maxspeechtimeout+"s");
		this.getRootElement().addContent(newForm);
		return newForm;
	}
	
	/**
	 * generates a new form, adds some var Elements and properties
	 * @param agenda a String that contain the name of the agenda 
	 * @param user the current user 
	 * @return the Form Element
	 */
	public Element newForm(String name, String agenda, String user){
		Element newForm = new Element("form");
		newForm.setAttribute("id", name);
		newVar(newForm,"com",null);
		newVar(newForm,"counter",null);
		newVar(newForm,"event",null);
		newVar(newForm,"nomatchRec",null);
		
		if(agenda!=null){
			newVar(newForm,"agenda","'"+agenda+"'");
		}
		else{
			newVar(newForm,"agenda",null);
		}
		newVar(newForm,"speak",null);
		newVar(newForm,"user","'"+user+"'");
		newProp(newForm, "inputmodes", "voice");
		newProp(newForm, "bargein", "false");
		newProp(newForm, "confidencelevel", "0.55");
		newProp(newForm, "maxspeechtimeout", Settings.maxspeechtimeout+"s");
		this.getRootElement().addContent(newForm);
		return newForm;
	}

	/**
	 * builds the footer for the Voice Document
	 * @param field the node to which the footer will be added
	 * @param nextPath the location of the servlet for the next attribute
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops of dialogues
	 */
	public void buildFooter(Element field, String nextPath, String noInputCounter, String user){
		Element catchNomatch = new Element("catch");
		newAssign(catchNomatch, "com", "'request'");
		newNomatch(field, nextPath, noInputCounter, "nomatch");
		newCatch(field, nextPath, noInputCounter, "noinput error.badfetch error.unsupported.language error.noresource");

		Element filled = new Element("filled");
		newAssign(filled, "com", "'work'");
		newAssign(filled, "speak", "move$.utterance");
		field.addContent(filled);
		newSubmit(filled, nextPath, "com move agenda speak user");
	}
	
	/**
	 * builds the footer for the Voice Document that is generated to solve a conflict
	 * @param field the node to which the footer will be added
	 * @param nextPath the location of the servlet for the next attribute
	 * @param question the question the user will be asked
	 */
	public void buildConflictFooter(Element field, String nextPath, String question){
		Element catch1 = new Element("catch");		
		catch1.setAttribute("event", "nomatch noinput error.badfetch error.unsupported.language error.noresource");
		newPrompt(catch1, question);
		field.addContent(catch1);
		
		Element catch2 = new Element("catch");
		catch2.setAttribute("count", "3");
		newAssign(catch2, "com", "'request'");
		field.addContent(catch2);
		catch2.setAttribute("event", "nomatch noinput error.badfetch error.unsupported.language error.noresource");
		newSubmit(catch2, nextPath, "com agenda user");
		
		Element filled = new Element("filled");
		newAssign(filled, "com", "'work'");
		newAssign(filled, "speak", "move$.utterance");

		field.addContent(filled);
		newSubmit(filled, nextPath, "com move agenda speak user");
	}
	
	/**
	 * builds the grammar CDATA Element from the given stack
	 * @param element the Element to which the grammar will be added
	 * @param grammar the grammar stack of which the grammar will be build
	 */
	public void buildGrammar(Element element, Vector <CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user){
		OSFactory factory;
		Element grammar1 = new Element("grammar");
		grammar1.setAttribute("type", "application/x-gsl");
		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
		grammar1.setAttribute("mode", "voice");
		element.addContent(grammar1);
		String grammarString = 	" [";
		Move tempMv;
		
		while (!grammar.isEmpty()){
			Grammar tempGr;
			CoreMove tempCoreMv;
			tempCoreMv=grammar.firstElement();
			grammar.remove(0);
			tempMv=tempCoreMv.move;
			factory=tempCoreMv.onto.factory;
			Grammar tempres = tempMv.getGrammar();
			if(tempres==null) continue;
			tempGr= factory.getGrammar(tempres.getLocalName());
			String tempString = " ";
			if(tempGr == null) continue;
			if(!tempGr.hasGrammarString()) continue;
			Iterator <OWLLiteral> itGr = tempGr.getGrammarString().iterator(); 
			if (itGr.hasNext()){
				itGr.next();
				tempString ="  ";
				tempString += tempCoreMv.parseGrammarVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)], user);
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
		grammarString+="]";
		CDATA script = new CDATA(grammarString);
		grammar1.addContent(script);

	}
	
	/**
	 * builds the utterances Elements from the given stack
	 * @param element the Element to which the utterances will be added
	 * @param utterance the CoreMove Stack of which the utterances will be build
	 */
	public void buildUtterance(Element element, Vector <CoreMove> utterance, Agenda actualAgenda, String whereAmI, String user){
		while (!utterance.isEmpty()){
			CoreMove tempCoreMv;
			tempCoreMv=utterance.firstElement();
			utterance.remove(0);
			String tempString2=tempCoreMv.parseUtteranceVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)], user);
			this.newPrompt(element, tempString2);
		}
	}

	/**
	 * flushes the VoiceDocument to the ServletResponse
	 * @param response the HttpServletResponse to which the Document is written
	 */
	public String output(){
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		try {
			StringWriter output = new StringWriter();
			outputter.output(this, output);
			output.flush();
			return output.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * the main function to fill the document with the given vectors of CoreMoves.
	 * calls functions buildUtterance and buildGrammar
	 * @param utterance the vector that contains the CoreMoves with the Utterances 
	 * @param grammar the vector that contains the CoreMoves with the Grammars
	 * @param actualAgenda the current active Agenda
	 * @param nextPath the location of the servlet for the next attribute
	 * @param user the name of the user
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return returns the filled document
	 */
	public EnhancedVoiceDocument fillDocument(Vector<CoreMove> utterance, Vector<CoreMove> grammar, Agenda actualAgenda, String nextPath, String user, String noInputCounter){
		EnhancedVoiceDocument VDoc = new EnhancedVoiceDocument(grammarObject);
	
		if(   (!utterance.isEmpty())&&(grammar.isEmpty())   ){ //System Turn
    		Element dialogueForm = VDoc.newDialogueForm(actualAgenda.getLocalName(), user);
    		Element block = new Element("block");
    		block.setAttribute("name", "promptBlock");
    		dialogueForm.addContent(block);
    		VDoc.buildUtterance(block, utterance, actualAgenda, nextPath, user);
    		newAssign(block, "com", "'work'");
    		Element submit = new Element("submit");
    		submit.setAttribute("next", nextPath);
    		submit.setAttribute("fetchaudio", "dummy.wav");
    		submit.setAttribute("namelist", "com agenda user");
    		block.addContent(submit);
    		
		} else
		if(   (utterance.isEmpty())&&(!grammar.isEmpty())   ){ // User Turn
    		Element dialogueForm = VDoc.newDialogueForm(actualAgenda.getLocalName(), user);
    		Element field = new Element("field");
    		field.setAttribute("name", "move");
    		newProp(field, "timeout", Settings.loopDuration+"s");
    		dialogueForm.addContent(field);
    		VDoc.buildGrammar(field, grammar, actualAgenda, nextPath, user);
    		VDoc.buildFooter(field, nextPath, noInputCounter, null);

		}else 
		if(   (!utterance.isEmpty())&&(!grammar.isEmpty())   ){  // Exchange
    		Element dialogueForm = VDoc.newDialogueForm(actualAgenda.getLocalName(), user);
    		Element field = new Element("field");
    		field.setAttribute("name", "move");
    		newProp(field, "timeout", Settings.loopDuration+"s");
    		dialogueForm.addContent(field);
    		VDoc.buildGrammar(field, grammar, actualAgenda, nextPath, user);
    		VDoc.buildUtterance(field, utterance, actualAgenda, nextPath, user);
    		VDoc.buildFooter(field, nextPath, noInputCounter, null);
		}		
		return VDoc;
	}
	
	/**
	 * function used to create a message output
	 * @param whereAmI the netadress of the servlet
	 * @param VDocname the name of the document
	 * @param output the messagestring for output
	 * @param user the name of the user
	 * @return returns the filled document with the output
	 */
	public EnhancedVoiceDocument buildOutput(String whereAmI, String VDocname, String output, String user) {
		EnhancedVoiceDocument VDoc = new EnhancedVoiceDocument(grammarObject);
		Element dan1 = VDoc.newDialogueForm(VDocname, user);
		Element block1 = new Element("block");
		dan1.addContent(block1);
		newPrompt(block1, output);
		newAssign(block1, "com", "'request'");
		newSubmit(block1, whereAmI, "com user");
		return VDoc;
	}

	/**
	 * function for the conflict resolution.
	 * since the Utterances and Grammars of this dialogpart are not part of the ontologies, they must be treated special.
	 *    ! The vectors used for this dialog consist of Strings, not CoreMoves !
	 * @param utterance a String containing the Utterance of this Dialog step
	 * @param grammar a vector of String-arrays, which have two strings: 
	 * in the first [0] is the name of the ontology, usually used as prefix and the grammar.
	 * in the second [1] is the name of the move. 
	 * @param actualAgenda the current active Agenda
	 * @param whereAmI the netadress of the servlet
	 * @param user the name of the user
	 * @return returns the filled document
	 */
	public OwlDocument buildSolveConflict(String utterance, Vector<String[]> grammar, Agenda actualAgenda, String whereAmI, String user){
		EnhancedVoiceDocument VDoc = new EnhancedVoiceDocument(grammarObject);
		
    		Element dialogueForm = VDoc.newDialogueForm(actualAgenda.getLocalName(), user);
    		Element field = new Element("field");
    		field.setAttribute("name", "move");
    		dialogueForm.addContent(field);
    		Element grammar1 = new Element("grammar");
    		grammar1.setAttribute("type", "application/x-gsl");
    		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
    		grammar1.setAttribute("mode", "voice");
    		field.addContent(grammar1);
    		String grammarString = 	" [";
    		Iterator<String[]> gramIter = grammar.iterator();
    		while(gramIter.hasNext()){
    			String[] tempStr = gramIter.next();
    			String tempString = " ";
    				tempString ="  ";
    				tempString += "[("+tempStr[2].toLowerCase()+")]";
    				tempString += "  { <move \"";
    				tempString += tempStr[0];
    				tempString += "_a1n1d_";
    				tempString += tempStr[1];
    				tempString += "\"> }   "; 
    				grammarString += tempString;
    		}
    		grammarString+="]";
    		CDATA cdata = new CDATA(grammarString);
    		grammar1.addContent(cdata);
    		this.newPrompt(field, utterance);
    		buildConflictFooter(field, whereAmI, utterance);
    		return VDoc;
	}
	
	/**
	 * generates a output-document for alternativ results
	 * @param result a vector with the Results 
	 * @param actualAgenda the actual agenda
	 * @param whereAmI
	 * @param user
	 */
	public OwlDocument buildPluginAlternatives(Vector<Result> result, Agenda actualAgenda, String whereAmI, String user){
		
		EnhancedVoiceDocument VDoc = new EnhancedVoiceDocument(grammarObject);
			
		// Beginn Schleife um pro Möglichkeit ein <form> zu bauen
		for (int i = 0; i < result.size(); i++) {
			String ontoName = result.elementAt(i).sFoundOntoKeyword;
			String moveName = result.elementAt(i).sFoundMoveKeyword;
			String utterance = result.elementAt(i).sUtterance;
			String yes_gram = result.elementAt(i).sYes;
			String no_gram = result.elementAt(i).sNo;
			String catch1_utt = result.elementAt(i).sCatch1;
			String catch2_utt = result.elementAt(i).sCatch2;
			String agenda = actualAgenda.getLocalName();
			String nextMove = ontoName + "_a1n1d_" + moveName;
			String gotoNext = new String();
			if (i < (result.size() - 1)){
				gotoNext = result.elementAt(i+1).sFoundMoveKeyword;
			} else {
				gotoNext = "NoResult";
			}
			
			// *** create <form> ***
			Element Form = new Element("form");
			Form.setAttribute("id", moveName);
			newVar(Form, "com", null);
			newVar(Form, "counter", null);
			newVar(Form, "event", null);
			newVar(Form, "speak",null);
			newVar(Form, "user", "'" + user + "'");
			newVar(Form, "move", "'" + nextMove + "'");
			if(agenda != null){
				newVar(Form, "agenda", "'" + agenda + "'");
			} else {
				newVar(Form,"agenda",null);
			}
			newProp(Form, "inputmodes", "voice");
			newProp(Form, "bargein", "false");
			newProp(Form, "confidencelevel", "0.55");
			newProp(Form, "maxspeechtimeout", Settings.maxspeechtimeout+"s");
			VDoc.getRootElement().addContent(Form);
		
			// *** create <field> ***
			Element field = new Element("field");
			field.setAttribute("name", "confirm");
			Form.addContent(field);
			
			// *** create <grammar> ***
			Element grammar1 = new Element("grammar");
    		grammar1.setAttribute("type", "application/x-gsl");
    		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
    		grammar1.setAttribute("mode", "voice");
    		field.addContent(grammar1);
    		
    		String grammarString = 	" [  ";
    		grammarString += yes_gram + "  { <confirm \"true\"> }   ";
    		grammarString += no_gram + "  { <confirm \"false\"> }   ";    
    		grammarString+="]";
    		
    		CDATA cdata = new CDATA(grammarString);
    		grammar1.addContent(cdata);
			
			// *** create <prompt> ***
			newPrompt(field, utterance);
			
			// *** create <catches> ***
			Element catch1 = new Element("catch");		
			catch1.setAttribute("event", "nomatch noinput error.badfetch error.unsupported.language error.noresource");
			newPrompt(catch1, catch1_utt);
			field.addContent(catch1);
			
			Element catch2 = new Element("catch");
			catch2.setAttribute("count", "3");
			newPrompt(catch2, catch2_utt);
			newAssign(catch2, "com", "'request'");
			field.addContent(catch2);
			catch2.setAttribute("event", "nomatch noinput error.badfetch error.unsupported.language error.noresource");
			newSubmit(catch2, whereAmI, "com agenda user");
	
			// *** create <filled> ***		
			Element filled = new Element("filled");
			
			Element ifEle = new Element("if");
			ifEle.setAttribute("cond", "confirm == 'true'");
			newAssign(ifEle, "com", "'work'");
			newAssign(ifEle, "speak", "confirm$.utterance");
			newSubmit(ifEle, whereAmI, "com move agenda speak user");
			Element elseEle = new Element("else");
			ifEle.addContent(elseEle);
			if (gotoNext == "NoResult"){
				newAssign(ifEle, "com", "'request'");
				newSubmit(ifEle, whereAmI, "com agenda user");
			} else {
				Element gotoEle = new Element("goto");
				gotoEle.setAttribute("next", "#" + gotoNext);
				ifEle.addContent(gotoEle);
			}
			
			filled.addContent(ifEle);
			field.addContent(filled);

		}
		return VDoc;
	}
	
	/**
	 * adds a new variable Element to the given Element	
	 * @param ele the node, to which the submit will be added
	 * @param name the name of the variable
	 * @param expr the expression the variable stores (can be null)
	 */
	public void newVar(Element ele, String name, String expr){
		Element var = new Element("var");
		var.setAttribute("name", name);
		if(expr!=null){
			var.setAttribute("expr", expr);
		}
		ele.addContent(var);		
	}
	
	/**
	 * adds a new property Element to the given Element	
	 * @param ele the node, to which the property will be added
	 * @param name the name of the property
	 * @param value the value of the property
	 */
	public void newProp(Element ele, String name, String value){
		Element prop = new Element("property");
		prop.setAttribute("name", name);
		prop.setAttribute("value", value);
		ele.addContent(prop);		
	}	
	
	/**
	 * adds a new assign Element to the given Element	
	 * @param ele the node, to which the assign will be added
	 * @param name the name of the variable the assign should work on
	 * @param expr the expression that the variable should store 
	 */
	public void newAssign(Element ele, String name, String expr){
		Element assi = new Element("assign");
		assi.setAttribute("name", name);
		assi.setAttribute("expr", expr);
		ele.addContent(assi);		
	}
	
	/**
	 * adds a new submit Element to the given Element	
	 * @param ele the node, to which the submit will be added
	 * @param next the Uri, the submit should go to
	 * @param namelist the parameters that should be transferred (can be null)
	 */
	public void newSubmit(Element ele, String next, String namelist){
		Element sub = new Element("submit");
		if(namelist!=null){
			sub.setAttribute("namelist", namelist);
			if(namelist.contains("nomatchRec")){
				sub.setAttribute("enctype", "multipart/form-data");
				sub.setAttribute("method", "POST");
				sub.setAttribute("expr", "'"+next+"?com='+com+'&event='+event+'&user='+user");
			} else {
				sub.setAttribute("next", next);
			}
		}
		ele.addContent(sub);	
	}	
	
	/**
	 * adds a new prompt Element to the given Element	
	 * @param ele the node, to which the prompt will be added
	 * @param utterance the utterance that will be added
	 */
	public void newPrompt(Element ele, String utterance){
		Element prompt = new Element("prompt");
		prompt.setAttribute("bargein", "false");
		
		if((Settings.ttsModeID==1)||(Settings.ttsModeID==2)||(Settings.ttsModeID==5)||(Settings.ttsModeID==6)){		
			prompt.setAttribute("lang", Settings.ttsMode, Namespace.XML_NAMESPACE);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate",Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(utterance);

		}
		else if((Settings.ttsModeID==3)||(Settings.ttsModeID==4)){
			Element voice = new Element("voice");
			voice.setAttribute("name", Settings.ttsMode);
			voice.addContent(utterance);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate",Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(voice);
		}
		ele.addContent(prompt);
	}	

	/**
	 * adds a new value Element to the given Element	
	 * @param ele the node, to which the prompt will be added
	 * @param expr the expression that the variable should store 
	 */
	public void newValue(Element ele, String expr){
		Element value = new Element("value");
		value.setAttribute("expr", expr);
		ele.addContent(value);		
	}
	
	/**
	 * adds a new catch Element to the given Element	
	 * @param ele the node, to which the prompt will be added
	 * @param nextPath the location of the servlet for the next attribute
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @param event the event that happens on last servlet call
	 */
	public Element newCatch(Element ele, String nextPath, String noInputCounter, String event){
		Element catchNoinput = new Element("catch");
		catchNoinput.setAttribute("event", event);
		
		int c = 0;
		if(noInputCounter!=null){
			c = (int)Integer.parseInt(noInputCounter);
			c++;
		}
		else{
			c = 1;
		}
		newAssign(catchNoinput, "counter", "'"+String.valueOf(c)+"'");		
		Element ifEle = new Element("if");
		ifEle.setAttribute("cond", "counter > 800");
		if(Settings.platform.equals("voxeo")){
			Element exit = new Element("exit");
			ifEle.addContent(exit);	
		}
		else {
			newAssign(ifEle, "com", "'request'");		
			newSubmit(ifEle, nextPath, "com user");
			newSubmit(ifEle, "event", "_event");
		}
		Element elseEle = new Element("else");
		ifEle.addContent(elseEle);
		newAssign(catchNoinput, "com", "'request'");		
		newSubmit(ifEle, nextPath, "com user counter event");		
		catchNoinput.addContent(ifEle);
		ele.addContent(catchNoinput);
		return catchNoinput;
	}
	
	/**
	 * adds a new nomatch Element to the given Element	
	 * @param ele the node, to which the prompt will be added
	 * @param nextPath the location of the servlet for the next attribute
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @param event the event that happens on last servlet call
	 */
	public Element newNomatch(Element ele, String nextPath, String noInputCounter, String event){
		Element nomatch= new Element("nomatch");
		
		int c = 0;
		if(noInputCounter!=null){
			c = (int)Integer.parseInt(noInputCounter);
			c++;
		}
		else{
			c = 1;
		}
		
		newAssign(nomatch, "nomatchRec", "lastresult$.recording");
		newAssign(nomatch, "counter", "'"+String.valueOf(c)+"'");	
		newAssign(nomatch, "event", "_event");
		newAssign(nomatch, "com", "'request'");	
		
		Element ifEle = new Element("if");
		ifEle.setAttribute("cond", "counter > 800");
		
		if(Settings.platform.equals("voxeo")){
			Element exit = new Element("exit");
			ifEle.addContent(exit);	
		}
		else {
			newAssign(ifEle, "com", "'request'");		
			newSubmit(ifEle, nextPath, "com user");
		}
		Element elseEle = new Element("else");
		ifEle.addContent(elseEle);
		
		newSubmit(ifEle, nextPath, "com user counter event nomatchRec");
		
		nomatch.addContent(ifEle);
		ele.addContent(nomatch);
		return nomatch;
	
	}
}
	