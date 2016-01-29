package owlSpeak.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;


import org.jdom.CDATA;
import org.jdom.Document;
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


/** 
 * used for generating Voice XML documents with grammars written in Nuance GSL 
 * @see org.jdom.Document
 * @author Dan Denich
 *
 */
public class JVoiceDocument extends Document implements OwlDocument{
	
	private static final long serialVersionUID = -9116205730578381542L;

	public JVoiceDocument(){
		super();
		generateDocument();
	};
	
	/**
	 * generates the vxml root element and processing instructions
	 * application/x-gsl
	 */
	public void generateDocument(){
		ProcessingInstruction access = new org.jdom.ProcessingInstruction("access-control", "allow=\"*\"");
		Element vxml = new Element("vxml");
		vxml.setAttribute("version", "2.1");
		this.setRootElement(vxml);
		this.addContent(access);
	}
	
	public JVoiceDocument generateSleep(String nextPath, String user, String noInputCounter){
		JVoiceDocument VDoc = new JVoiceDocument();
		Element mainForm = new Element("form");
		Element field = new Element("field");
		field.setAttribute("name", "dummy");
		newVar(mainForm,"com","'request'");
		newVar(mainForm,"user","'"+user+"'");
		newVar(mainForm,"counter",null);
		newProp(field, "timeout", "3s");
		newProp(field, "maxspeechtimeout", "3s");
		Element dummyGrammar = new Element("grammar");
		dummyGrammar.setAttribute("type", "application/x-nuance-gsl");
		dummyGrammar.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
		dummyGrammar.setAttribute("mode", "voice");
		field.addContent(dummyGrammar);
		String grammarString = 	";GSL2.0 [ (hokuspokus) { <dummy \"hokuspokus\"> } ]";
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
	public void buildFooter(Element field, String nextPath, String noInputCounter){
		Element catchNomatch = new Element("catch");
		newAssign(catchNomatch, "com", "'request'");
		newCatch(field, nextPath, noInputCounter, "noinput nomatch error.badfetch error.unsupported.language error.noresource");
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
		grammar1.setAttribute("type", "application/x-nuance-gsl");
		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
		grammar1.setAttribute("mode", "voice");
		element.addContent(grammar1);
		String grammarString = 	";GSL2.0 [";
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
			//String tempString2=tempCoreMv.parseUtteranceVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)]);
			//this.newPrompt(element, tempString2);
			this.newPromptFromCoreMove(element, tempCoreMv, user);
		}
	}
	
	public JVoiceDocument fillDocument(Vector<CoreMove> utterance, Vector<CoreMove> grammar, Agenda actualAgenda, String nextPath, String user, String noInputCounter){
		JVoiceDocument VDoc = new JVoiceDocument();
	
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
    		VDoc.buildFooter(field, nextPath, noInputCounter);

		}else 
		if(   (!utterance.isEmpty())&&(!grammar.isEmpty())   ){  // Exchange
    		Element dialogueForm = VDoc.newDialogueForm(actualAgenda.getLocalName(), user);
    		Element field = new Element("field");
    		field.setAttribute("name", "move");
    		newProp(field, "timeout", Settings.loopDuration+"s");
    		dialogueForm.addContent(field);
    		VDoc.buildGrammar(field, grammar, actualAgenda, nextPath, user);
    		VDoc.buildUtterance(field, utterance, actualAgenda, nextPath, user);
    		VDoc.buildFooter(field, nextPath, noInputCounter);
		}		
		return VDoc;
	}
	
	public JVoiceDocument buildOutput(String whereAmI, String VDocname, String output, String user) {
		JVoiceDocument VDoc = new JVoiceDocument();
		Element dan1 = VDoc.newDialogueForm(VDocname, user);
		Element block1 = new Element("block");
		dan1.addContent(block1);
		newPrompt(block1, output);
		newAssign(block1, "com", "'request'");
		newSubmit(block1, whereAmI, "com user");
		
//		Element catchForm = new Element("form");
//		catchForm.setAttribute("id", "catchForm");
//		newPrompt(catchForm, "transfer");
//		Element transfer = new Element("transfer");
//		transfer.setAttribute("type", "blind");
//		transfer.setAttribute("dest", Settings.application);
//		catchForm.addContent(transfer);
//		
//		Element catchTrans = new Element("catch");
//		catchTrans.setAttribute("event", "error.connection.noroute");
//		newSubmit(catchTrans, whereAmI+"?com=request&user="+user, null);
//		catchForm.addContent(catchTrans);
//		
//		VDoc.getRootElement().addContent(catchForm);
		
		return VDoc;
	}

	public OwlDocument buildSolveConflict(String utterance, Vector<String[]> grammar, Agenda actualAgenda, String whereAmI, String user){
		JVoiceDocument VDoc = new JVoiceDocument();
		
    		Element dialogueForm = VDoc.newDialogueForm(actualAgenda.getLocalName(), user);
    		Element field = new Element("field");
    		field.setAttribute("name", "move");
    		dialogueForm.addContent(field);
    		Element grammar1 = new Element("grammar");
    		grammar1.setAttribute("type", "application/x-nuance-gsl");
    		grammar1.setAttribute("lang", Settings.asrMode, Namespace.XML_NAMESPACE);
    		grammar1.setAttribute("mode", "voice");
    		field.addContent(grammar1);
    		String grammarString = 	" [";
    		Iterator<String[]> gramIter = grammar.iterator();
    		while(gramIter.hasNext()){
    			String[] tempStr = gramIter.next();
    			String tempString = " ";
    				tempString ="  ";
    				tempString += ";GSL2.0 [("+tempStr[2].toLowerCase()+")]";
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
		sub.setAttribute("next", next);
		if(namelist!=null){
			sub.setAttribute("namelist", namelist);
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
	 * adds a new prompt Element to the given Element	
	 * @param ele the node, to which the prompt will be added
	 * @param utteranceMove the utterance that will be added
	 * @param user the current user 
	 */
	public void newPromptFromCoreMove(Element ele, CoreMove utteranceMove, String user){
		String utterance = utteranceMove.parseUtteranceVariables(utteranceMove.onto.beliefSpace[Settings.getuserpos(user)], user);
		Element prompt = new Element("prompt");
		prompt.setAttribute("bargein", "false");
		if((Settings.ttsModeID==1)||(Settings.ttsModeID==2)){		
			if(utteranceMove.move.getUtterance().hasSpeakerGender()){
				if(utteranceMove.move.getUtterance().getSpeakerGender().equals("male")){
					prompt.setAttribute("lang", "en-gb-daniel", Namespace.XML_NAMESPACE);
				}
				else if(utteranceMove.move.getUtterance().getSpeakerGender().equals("female")){
					prompt.setAttribute("lang", "en-gb-serena", Namespace.XML_NAMESPACE);
				}
			}
			else{
				prompt.setAttribute("lang", Settings.ttsMode, Namespace.XML_NAMESPACE);
			}
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate",Settings.speakRate);
			if(utteranceMove.move.getUtterance().hasVolume()){	
				prosody.setAttribute("volume",utteranceMove.move.getUtterance().getVolume());
			}
			prompt.addContent(prosody);
			prosody.addContent(utterance);
		}
		if((Settings.ttsModeID==5)||(Settings.ttsModeID==6)){
			if(utteranceMove.move.getUtterance().hasSpeakerGender()){
				if(utteranceMove.move.getUtterance().getSpeakerGender().equals("male")){
					prompt.setAttribute("lang", "de-de-anna", Namespace.XML_NAMESPACE);
				}
				else if(utteranceMove.move.getUtterance().getSpeakerGender().equals("female")){
					prompt.setAttribute("lang", "de-de-steffi", Namespace.XML_NAMESPACE);
				}
			}
			else{
				prompt.setAttribute("lang", Settings.ttsMode, Namespace.XML_NAMESPACE);
			}
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate",Settings.speakRate);
			if(utteranceMove.move.getUtterance().hasVolume()){	
				prosody.setAttribute("volume",utteranceMove.move.getUtterance().getVolume());
			}
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
		newAssign(catchNoinput, "event", "_event");
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
		newAssign(catchNoinput, "com", "'request'");		
		newSubmit(ifEle, nextPath, "com user counter event");		
		catchNoinput.addContent(ifEle);
		ele.addContent(catchNoinput);
		return catchNoinput;
	}
	
	//Dummy
	public OwlDocument buildPluginAlternatives(Vector<Result> result, Agenda actualAgenda, String whereAmI, String user){
		return null;
	}
}
