package owlSpeak.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Vector;

import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import owlSpeak.Agenda;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.plugin.Result;
//import java.text.SimpleDateFormat;
//import java.util.Date;
import owlSpeak.servlet.grammar.IGrammar;

/**
 * used for generating Voice XML documents with grammars
 * 
 * @see org.jdom.Document
 * @author Dan Denich
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 */
public class VoiceDocument extends Document implements OwlDocument {

	private static final long serialVersionUID = -9116205730578381542L;
	public static String callID = "generic";

	protected IGrammar grammarObject = null;

	public static Namespace nsVoxeo = Namespace.getNamespace("voxeo",
			"http://community.voxeo.com/xmlns/vxml");

	// public VoiceDocument(){
	// super();
	// generateDocument();
	// };

	public VoiceDocument(IGrammar _grammar) {
		super();
		grammarObject = _grammar;
		generateDocument();
	};

	/**
	 * generates the vxml root element and processing instructions
	 * 
	 */
	public void generateDocument() {
		ProcessingInstruction access = new org.jdom.ProcessingInstruction(
				"access-control", "allow=\"*\"");
		Element vxml = new Element("vxml");
		vxml.setAttribute("version", "2.1");
		vxml.addNamespaceDeclaration(nsVoxeo);
		newProp(vxml, "recordutterance", "true");

		this.setRootElement(vxml);
		this.addContent(access);

	}

	public VoiceDocument generateSleep(String nextPath, String user,
			String noInputCounter) {
		return generateSleep(nextPath, user, noInputCounter, new VoiceDocument(
				grammarObject));
	}

	public VoiceDocument generateSleep(String nextPath, String user,
			String noInputCounter, VoiceDocument VDoc) {
		Element mainForm = new Element("form");

		Element field = new Element("field");
		field.setAttribute("name", "dummy");
		newVar(mainForm, "com", "'request'");
		newVar(mainForm, "user", "'" + user + "'");
		newVar(mainForm, "counter", null);
		newProp(field, "timeout", "10s");
		newProp(field, "maxspeechtimeout", "10s");
		grammarObject.buildSleepGrammar(field);
		Element filled = new Element("filled");
		field.addContent(filled);
		Element clear1 = new Element("clear");
		clear1.setAttribute("namelist", "dummy");
		filled.addContent(clear1);
		newSubmit(filled, nextPath, "com user");
		mainForm.addContent(field);
		Element clear2 = new Element("clear");
		clear2.setAttribute("namelist", "dummy");
		newCatch(
				field,
				nextPath,
				noInputCounter,
				"help noinput nomatch error.badfetch error.unsupported.language error.noresource")
				.addContent(clear2);
		VDoc.getRootElement().addContent(mainForm);
		return VDoc;
	}

	/**
	 * generates a new form, adds some var Elements and properties
	 * 
	 * @param agenda
	 *            a String that contain the name of the agenda
	 * @param user
	 *            the current user
	 * @return the Form Element
	 */
	public Element newDialogueForm(String agenda, String user) {
		Element newForm = new Element("form");
		newForm.setAttribute("id", "mainForm");
		newVar(newForm, "com", null);
		newVar(newForm, "counter", null);
		newVar(newForm, "event", null);
		if (agenda != null) {
			newVar(newForm, "agenda", "'" + agenda + "'");
		} else {
			newVar(newForm, "agenda", null);
		}

		newVar(newForm, "interpretation", null);
		newVar(newForm, "confidence", null);
		newVar(newForm, "speak", null);
		
		for (int i = 0; i < Settings.nbNBestListEntries; i++) {
			newVar(newForm, "myResult" + i, " ");
			newVar(newForm, "Speak" + i, " ");
			newVar(newForm, "Confidence" + i, " ");
		}
		

		newVar(newForm, "user", "'" + user + "'");
		newVar(newForm, "duration", null);
		newVar(newForm, "bargein", null);
		newVar(newForm, "recording", null);
		newVar(newForm, "inputmode", null);

		newVar(newForm, "resultArray", null);
		newVar(newForm, "resultLen", null);
		newProp(newForm, "maxnbest", "" + Settings.nbNBestListEntries);

		newProp(newForm, "inputmodes", "voice");
		newProp(newForm, "bargein", "false");
		newProp(newForm, "maxspeechtimeout", Settings.maxspeechtimeout + "s");
		// newProp(newForm, "universals", "help");

		this.getRootElement().addContent(newForm);
		return newForm;
	}

	/**
	 * builds the footer for the Voice Document
	 * 
	 * @param field
	 *            the node to which the footer will be added
	 * @param nextPath
	 *            the location of the servlet for the next attribute
	 * @param noInputCounter
	 *            counts the noinput events in order to avoid unlimited loops of
	 *            dialogues
	 * @param user
	 *            user name
	 */
	public void buildFooter(Element field, String nextPath,
			String noInputCounter, String user) {

		int grammarType = Settings.usersetting[Settings.getuserpos(user)].grammarType;
		

		// handing errors and events except filled
		Element catchNomatch = new Element("catch");
		newAssign(catchNomatch, "com", "'request'");
		newNomatch(field, nextPath, noInputCounter, "nomatch");
		newCatch(field, nextPath, noInputCounter,
				"help error.badfetch noinput  error.badfetch error.unsupported.language error.noresource");

		
		// processing input if filled
		Element filled = new Element("filled");
		newAssign(filled, "resultArray", "application.lastresult$");
		newAssign(filled, "resultLen", "resultArray.length");

		for (int i = 0; i < Settings.nbNBestListEntries; i++) {


			Element ifEle = new Element("if");
			ifEle.setAttribute("cond", "resultArray.length > " + i);
			switch (grammarType) {
			case IGrammar.JSGFasFile:
			case IGrammar.JSGF:
				newAssign(ifEle, "myResult" + i, "resultArray[" + i
						+ "].interpretation");
				newAssign(filled, "speak", "move$.utterance");
				newAssign(ifEle, "myResult" + i, "resultArray[" + i
						+ "].interpretation.move");
				break;
			case IGrammar.GRXML:
				newAssign(ifEle, "myResult" + i, "resultArray[" + i
						+ "].interpretation");
				
				newAssign(filled, "speak", "lastresult$.interpretation.varSubmitIntern");
				newAssign(filled, "move", "lastresult$.interpretation.moveIntern");

				break;
			default:

			}
			newAssign(ifEle, "Speak" + i, "resultArray[" + i + "].utterance");
			newAssign(ifEle, "Confidence" + i, "resultArray[" + i
					+ "].confidence");

			Element elseEle = new Element("else");
			ifEle.addContent(elseEle);
			newAssign(ifEle, "myResult" + i, "null");
			newAssign(ifEle, "Speak" + i, "null");
			newAssign(ifEle, "Confidence" + i, "null");
			filled.addContent(ifEle);
		}

		newAssign(filled, "com", "'work'");
		
		newAssign(filled, "interpretation",
				"application.lastresult$.interpretation");
		newAssign(filled, "event", "'success'");
		newAssign(filled, "confidence", "application.lastresult$[0].confidence");
		newAssign(filled, "inputmode", "move$.inputmode");
		newAssign(filled, "recording", "application.lastresult$.recording");
		newAssign(filled, "bargein", "application.lastresult$.bargein");
		newAssign(filled, "duration",
				"application.lastresult$.recordingduration");
		field.addContent(filled);
		
		String submitFieldList = "com user move agenda event speak user interpretation confidence duration inputmode bargein resultLen";
		//		String submitFieldList = "com user move";

		for (int i = 0; i < Settings.nbNBestListEntries; i++) {
			submitFieldList += " myResult" + i + " Speak" + i + " Confidence"
					+ i;
		}
		newSubmit(filled, nextPath, submitFieldList);
		// newSubmit(filled, nextPath,
		// "com user move agenda event speak recording user interpretation confidence duration   inputmode bargein");

	}

	/**
	 * builds the footer for the Voice Document that is generated to solve a
	 * conflict
	 * 
	 * @param field
	 *            the node to which the footer will be added
	 * @param nextPath
	 *            the location of the servlet for the next attribute
	 * @param question
	 *            the question the user will be asked
	 */
	public void buildConflictFooter(Element field, String nextPath,
			String question) {
		Element catch1 = new Element("catch");
		catch1.setAttribute(
				"event",
				"help nomatch noinput error.badfetch error.unsupported.language error.noresource");
		newPrompt(catch1, question);
		field.addContent(catch1);

		Element catch2 = new Element("catch");
		catch2.setAttribute("count", "3");
		newAssign(catch2, "com", "'request'");
		field.addContent(catch2);
		catch2.setAttribute(
				"event",
				"help nomatch noinput error.badfetch error.unsupported.language error.noresource");
		newSubmit(catch2, nextPath, "com agenda user");

		Element filled = new Element("filled");
		newAssign(filled, "com", "'work'");

		
		newAssign(filled, "interpretation",
				"application.lastresult$.interpretation");
		newAssign(filled, "inputmode", "move$.inputmode");
		newAssign(filled, "duration",
				"application.lastresult$.recordingduration");
		newAssign(filled, "recording", "application.lastresult$.recording");
		field.addContent(filled);
		newSubmit(
				filled,
				nextPath,
				"com move agenda speak user interpretation duration triggeredgrammar  inputmode");
	}

	/**
	 * builds the utterances Elements from the given stack.
	 * 
	 * @param element
	 *            the Element to which the utterances will be added
	 * @param utterance
	 *            the CoreMove Stack of which the utterances will be build
	 */
	public void buildUtterance(Element element, Vector<CoreMove> utterance,
			Agenda actualAgenda, String whereAmI, String user) {
		while (!utterance.isEmpty()) {
			CoreMove tempCoreMv;
			tempCoreMv = utterance.firstElement();
			utterance.remove(0);
			// String
			// tempString2=tempCoreMv.parseUtteranceVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)]);
			// this.newPrompt(element, tempString2);
			this.newPromptFromCoreMove(element, tempCoreMv, user);
		}
	}

	/**
	 * flushes the VoiceDocument to the ServletResponse
	 * 
	 * @param response
	 *            the HttpServletResponse to which the Document is written
	 */
	public void output(HttpServletResponse response) {
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		OutputStream output;
		try {
			output = response.getOutputStream();
			// response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
			response.setHeader("Cache-Control",
					"no-store, no-cache, must-revalidate");
			response.addHeader("Cache-Control", "post-check=0, pre-check=0");
			response.setHeader("Pragma", "no-cache");
			outputter.output(this, output);
			output.flush();
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public VoiceDocument fillDocument(Vector<CoreMove> utterance,
			Vector<CoreMove> grammar, Agenda actualAgenda, String nextPath,
			String user, String noInputCounter) {

		return fillDocument(utterance, grammar, actualAgenda, nextPath, user,
				noInputCounter, new VoiceDocument(grammarObject));
	}

	public VoiceDocument fillDocument(Vector<CoreMove> utterance,
			Vector<CoreMove> grammar, Agenda actualAgenda, String nextPath,
			String user, String noInputCounter, VoiceDocument VDoc) {


		boolean recordingActivated = Settings.callRecording;

		if ((!utterance.isEmpty()) && (grammar.isEmpty())) { // System Turn
			boolean disconnect = containsDisconnects(utterance);

			Element dialogueForm = VDoc.newDialogueForm(
					actualAgenda.getLocalName(), user);

			Element block = new Element("block");
			if (recordingActivated)
				block.addContent(getRecordCall());
			block.setAttribute("name", "promptBlock");
			VDoc.buildUtterance(block, utterance, actualAgenda, nextPath, user);
			newAssign(block, "com", "'work'");
			dialogueForm.addContent(block);

			if (!disconnect) {
				Element submit = new Element("submit");
				submit.setAttribute("next", nextPath);
				submit.setAttribute("fetchaudio", "dummy.wav");
				submit.setAttribute("namelist", "com agenda user");
				block.addContent(submit);
			} else {
				VDoc.newDisconnect(block);
				newCatchDisconnect(dialogueForm);
			}
		} else if ((utterance.isEmpty()) && (!grammar.isEmpty())) { // User Turn
		
			Element dialogueForm = VDoc.newDialogueForm(
					actualAgenda.getLocalName(), user);

			if (recordingActivated) {
				Element block = new Element("block");
				block.addContent(getRecordCall());
				dialogueForm.addContent(block);

			}

			Element field = new Element("field");
			field.setAttribute("name", "move");
			newProp(field, "timeout", Settings.loopDuration + "s");
			
			grammarObject.buildGrammar(field, grammar, user);

			VDoc.buildFooter(field, nextPath, noInputCounter, user);
			dialogueForm.addContent(field);
		} else if ((!utterance.isEmpty()) && (!grammar.isEmpty())) { // Exchange
	

			Element dialogueForm = VDoc.newDialogueForm(
					actualAgenda.getLocalName(), user);

			if (recordingActivated) {
				Element block = new Element("block");
				block.addContent(getRecordCall());
				dialogueForm.addContent(block);
	
			}

			Element field = new Element("field");
			field.setAttribute("name", "move");
			newProp(field, "timeout", Settings.loopDuration + "s");
		
			grammarObject.buildGrammar(field, grammar, user);

			VDoc.buildUtterance(field, utterance, actualAgenda, nextPath, user);
			VDoc.buildFooter(field, nextPath, noInputCounter, user);
			dialogueForm.addContent(field);
		}
		return VDoc;
	}

	protected boolean containsDisconnects(Vector<CoreMove> utterance) {
		boolean disconnect = false;
		for (CoreMove u : utterance) {
			disconnect |= u.move.getIsExitMove();
		}
		return disconnect;
	}

	public VoiceDocument buildOutput(String whereAmI, String VDocname,
			String output, String user) {
		return buildOutput(whereAmI, VDocname, output, user, new VoiceDocument(
				grammarObject));
	}

	public VoiceDocument buildOutput(String whereAmI, String VDocname,
			String output, String user, VoiceDocument VDoc) {
		Element dan1 = VDoc.newDialogueForm(VDocname, user);
		Element block1 = new Element("block");
		dan1.addContent(block1);
		newPrompt(block1, output);
		newAssign(block1, "com", "'request'");
		newSubmit(block1, whereAmI, "com user");
		return VDoc;
	}

	public OwlDocument buildSolveConflict(String utterance,
			Vector<String[]> grammar, Agenda actualAgenda, String whereAmI,
			String user) {
		return buildSolveConflict(utterance, grammar, actualAgenda, whereAmI,
				user, new VoiceDocument(grammarObject));
	}

	public OwlDocument buildSolveConflict(String utterance,
			Vector<String[]> grammar, Agenda actualAgenda, String whereAmI,
			String user, VoiceDocument VDoc) {

		Element dialogueForm = VDoc.newDialogueForm(
				actualAgenda.getLocalName(), user);
		Element field = new Element("field");
		field.setAttribute("name", "move");
		dialogueForm.addContent(field);
		grammarObject.buildSolveConflict(field, grammar);
		this.newPrompt(field, utterance);
		buildConflictFooter(field, whereAmI, utterance);
		return VDoc;
	}

	/**
	 * adds a new variable Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the submit will be added
	 * @param name
	 *            the name of the variable
	 * @param expr
	 *            the expression the variable stores (can be null)
	 */
	public void newVar(Element ele, String name, String expr) {
		Element var = new Element("var");
		var.setAttribute("name", name);
		if (expr != null) {
			var.setAttribute("expr", expr);
		}
		ele.addContent(var);
	}

	/**
	 * adds a new property Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the property will be added
	 * @param name
	 *            the name of the property
	 * @param value
	 *            the value of the property
	 */
	public void newProp(Element ele, String name, String value) {
		Element prop = new Element("property");
		prop.setAttribute("name", name);
		prop.setAttribute("value", value);
		ele.addContent(prop);
	}

	/**
	 * adds a new assign Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the assign will be added
	 * @param name
	 *            the name of the variable the assign should work on
	 * @param expr
	 *            the expression that the variable should store
	 */
	public void newAssign(Element ele, String name, String expr) {
		Element assi = new Element("assign");
		assi.setAttribute("name", name);
		assi.setAttribute("expr", expr);
		ele.addContent(assi);
	}

	/**
	 * adds a new submit Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the submit will be added
	 * @param next
	 *            the Uri, the submit should go to
	 * @param namelist
	 *            the parameters that should be transferred (can be null)
	 */
	/*
	 * public void newSubmit(Element ele, String next, String namelist){ Element
	 * sub = new Element("submit"); sub.setAttribute("next", next);
	 * if(namelist!=null){ sub.setAttribute("namelist", namelist);
	 * 
	 * } ele.addContent(sub); }
	 */

	public void newSubmit(Element ele, String next, String namelist) {
		Element sub = new Element("submit");
		sub.setAttribute("next", next);
		sub.setAttribute("method", "post");
		if (namelist != null) {
			sub.setAttribute("namelist", namelist);

		}
		ele.addContent(sub);
	}

	/**
	 * adds a new prompt Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the prompt will be added
	 * @param utterance
	 *            the utterance that will be added
	 */
	public void newPrompt(Element ele, String utterance) {
		Element prompt = new Element("prompt");
		prompt.setAttribute("bargein", "false");
		if ((Settings.ttsModeID == 1) || (Settings.ttsModeID == 2)
				|| (Settings.ttsModeID == 5) || (Settings.ttsModeID == 6)) {
			prompt.setAttribute("lang", Settings.ttsMode,
					Namespace.XML_NAMESPACE);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(utterance);
		} else if ((Settings.ttsModeID == 3) || (Settings.ttsModeID == 4)) {
			Element voice = new Element("voice");
			voice.setAttribute("name", Settings.ttsMode);
			voice.addContent(utterance);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(voice);
		}
		ele.addContent(prompt);
	}

	/**
	 * adds a new prompt Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the prompt will be added
	 * @param utterance
	 *            the utterance that will be added
	 */
	public void newPromptTest(Element ele, String utterance) {
		Element prompt = new Element("prompt");
		prompt.setAttribute("bargein", "false");
		Element value = new Element("value");
		Element value2 = new Element("value");
		value.setAttribute("expr", "event");
		value2.setAttribute("expr", "_message");
		if ((Settings.ttsModeID == 1) || (Settings.ttsModeID == 2)
				|| (Settings.ttsModeID == 5) || (Settings.ttsModeID == 6)) {
			prompt.setAttribute("lang", Settings.ttsMode,
					Namespace.XML_NAMESPACE);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(utterance);
			prosody.addContent(value);
			prosody.addContent(value2);
		} else if ((Settings.ttsModeID == 3) || (Settings.ttsModeID == 4)) {
			Element voice = new Element("voice");
			voice.setAttribute("name", Settings.ttsMode);
			voice.addContent(utterance);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(voice);
		}
		ele.addContent(prompt);
	}

	/**
	 * adds a new prompt Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the prompt will be added
	 * @param utteranceMove
	 *            the utterance that will be added
	 * @param user
	 *            the current user
	 */

	public static String systemutter;

	public void newPromptFromCoreMove(Element ele, CoreMove utteranceMove,
			String user) {
		String utterance = utteranceMove
				.parseUtteranceVariables(
						utteranceMove.onto.beliefSpace[Settings
								.getuserpos(user)], user);
		systemutter = utterance;
		Element prompt = new Element("prompt");
		prompt.setAttribute("bargein", "false");
		if ((Settings.ttsModeID == 1) || (Settings.ttsModeID == 2)) {
			if (utteranceMove.move.getUtterance().hasSpeakerGender()) {
				if (utteranceMove.move.getUtterance().getSpeakerGender()
						.equals("male")) {
					prompt.setAttribute("lang", "en-gb-daniel",
							Namespace.XML_NAMESPACE);
				} else if (utteranceMove.move.getUtterance().getSpeakerGender()
						.equals("female")) {
					prompt.setAttribute("lang", "en-gb-serena",
							Namespace.XML_NAMESPACE);
				}
			} else {
				prompt.setAttribute("lang", Settings.ttsMode,
						Namespace.XML_NAMESPACE);
			}
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			if (utteranceMove.move.getUtterance().hasVolume()) {
				prosody.setAttribute("volume", utteranceMove.move
						.getUtterance().getVolume());
			}
			prompt.addContent(prosody);
			prosody.addContent(utterance);
		}
		if ((Settings.ttsModeID == 5) || (Settings.ttsModeID == 6)) {
			if (utteranceMove.move.getUtterance().hasSpeakerGender()) {
				if (utteranceMove.move.getUtterance().getSpeakerGender()
						.equals("male")) {
					prompt.setAttribute("lang", "de-de-anna",
							Namespace.XML_NAMESPACE);
				} else if (utteranceMove.move.getUtterance().getSpeakerGender()
						.equals("female")) {
					prompt.setAttribute("lang", "de-de-steffi",
							Namespace.XML_NAMESPACE);
				}
			} else {
				prompt.setAttribute("lang", Settings.ttsMode,
						Namespace.XML_NAMESPACE);
			}
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			if (utteranceMove.move.getUtterance().hasVolume()) {
				prosody.setAttribute("volume", utteranceMove.move
						.getUtterance().getVolume());
			}
			prompt.addContent(prosody);
			prosody.addContent(utterance);
		} else if ((Settings.ttsModeID == 3) || (Settings.ttsModeID == 4)) {
			Element voice = new Element("voice");
			voice.setAttribute("name", Settings.ttsMode);
			voice.addContent(utterance);
			Element prosody = new Element("prosody");
			prosody.setAttribute("rate", Settings.speakRate);
			prompt.addContent(prosody);
			prosody.addContent(voice);
		}
		ele.addContent(prompt);
	}

	public String getName() {

		return systemutter;
	}

	public Element newCatch(Element ele, String nextPath,
			String noInputCounter, String event) {
		Element catchNoinput = new Element("catch");
		catchNoinput.setAttribute("event", event);
		int c = 0;
		if (noInputCounter != null) {
			c = (int) Integer.parseInt(noInputCounter);
			c++;
		} else {
			c = 1;
		}
		newAssign(catchNoinput, "counter", "'" + String.valueOf(c) + "'");
		newAssign(catchNoinput, "event", "_event");
		Element ifEle = new Element("if");
		ifEle.setAttribute("cond", "counter > 800");
		if (Settings.platform.equals("voxeo")) {
			Element exit = new Element("exit");
			ifEle.addContent(exit);
		} else {
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

	// Dummy
	public OwlDocument buildPluginAlternatives(Vector<Result> result,
			Agenda actualAgenda, String whereAmI, String user) {
		return null;
	}

	/**
	 * adds a new nomatch Element to the given Element
	 * 
	 * @param ele
	 *            the node, to which the prompt will be added
	 * @param nextPath
	 *            the location of the servlet for the next attribute
	 * @param noInputCounter
	 *            counts the noinput events in order to avoid unlimited loops.
	 * @param event
	 *            the event that happens on last servlet call
	 */
	public Element newNomatch(Element ele, String nextPath,
			String noInputCounter, String event) {
		Element nomatch = new Element("nomatch");
		int c = 0;
		if (noInputCounter != null) {
			c = (int) Integer.parseInt(noInputCounter);
			c++;
		} else {
			c = 1;
		}

		newAssign(nomatch, "recording", "lastresult$.recording");
		// newAssign(nomatch, "madeup", "null");
//		newPrompt(nomatch, "recording = <value expr=\"recording\"/>");
		
		
		newAssign(nomatch, "duration", "lastresult$.recordingduration");
		newAssign(nomatch, "counter", "'" + String.valueOf(c) + "'");
		newAssign(nomatch, "event", "_event");
		newAssign(nomatch, "com", "'request'");

		Element ifEle = new Element("if");
		ifEle.setAttribute("cond", "counter > 800");

		if (Settings.platform.equals("voxeo")) {
			Element exit = new Element("exit");
			ifEle.addContent(exit);
		} else {
//
			
			newAssign(ifEle, "com", "'request'");
			newSubmit(ifEle, nextPath, "com user");
		}
		Element elseEle = new Element("else");
		ifEle.addContent(elseEle);

		// newSubmit(ifEle, nextPath,
		// "com user counter event madeup recording duration");
		newSubmit(ifEle, nextPath, "com user counter event duration");

		nomatch.addContent(ifEle);
		ele.addContent(nomatch);
		return nomatch;

	}

	/**
	 * adds a new Disconnect to the given Element
	 * 
	 * @param ele
	 *            the node, to which the disconnect will be added
	 */
	public void newDisconnect(Element ele) {
		Element disconnect = new Element("disconnect");
		ele.addContent(disconnect);
	}

	/**
	 * adds a new catch Disconnect to the given Element
	 * 
	 * @param ele
	 *            the node, to which the catch disconnect will be added
	 */
	public Element newCatchDisconnect(Element ele) {
		Element catchDisconnect = new Element("catch");
		catchDisconnect.setAttribute("event", "connection.disconnect");
		newExit(catchDisconnect);
		ele.addContent(catchDisconnect);
		return catchDisconnect;
	}

	/**
	 * adds a new exit to the given Element
	 * 
	 * @param ele
	 *            the node, to which the exit will be added
	 */
	public Element newExit(Element ele) {
		return newExit(ele, null);
	}

	/**
	 * adds a new exit to the given Element
	 * 
	 * @param ele
	 *            the node, to which the exit will be added
	 * @param namelist
	 *            the list of parameters returned to browser
	 */
	public Element newExit(Element ele, String namelist) {
		Element exit = new Element("exit");
		if (namelist != null)
			exit.setAttribute("namelist", namelist);
		ele.addContent(exit);
		return exit;
	}

	/**
	 * Creates an element recordcall in namespace voxeo with recording
	 * probability 100
	 * 
	 * @return the voxeo:recordcall element
	 */
	private Element getRecordCall() {
		return getRecordCall(100);
	}

	/**
	 * Creates an element recordcall in namespace voxeo with provided recording
	 * probability
	 * 
	 * @param recordingProbability
	 *            the probability that the call will be recorded
	 * @return the voxeo:recordcall element
	 */
	private Element getRecordCall(int recordingProbability) {
		Element recordCall = new Element("recordcall");

		recordCall.setNamespace(nsVoxeo);
		recordCall
				.setAttribute("value", Integer.toString(recordingProbability));
		if (callID == null)
			callID = "generic";
		recordCall.setAttribute("info", callID);

		return recordCall;
	}
}
