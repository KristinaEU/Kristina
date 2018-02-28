package owlSpeak.servlet.document;

import java.util.List;
import java.util.Vector;

import org.jdom.Element;

import owlSpeak.Agenda;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.servlet.grammar.IGrammar;

/**
 * Used for generating Voice XML documents for POMDP dialog modelling with
 * grammars according to IGrammar object.
 * 
 * @see org.jdom.Document
 * @author Savina Koleva
 */
public class VoiceDocumentPOMDP extends VoiceDocument {

	private static final long serialVersionUID = -9116205730578381542L;

	public VoiceDocumentPOMDP(IGrammar _grammar) {
		super(_grammar);
	}

	/**
	 * Generates a new form, adds some var Elements and properties to the POMDP
	 * VoiceXML document. Enable the usage of N-best list.
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
		newVar(newForm, "speak", null);
		newVar(newForm, "user", "'" + user + "'");
		newVar(newForm, "resultArray", null);
		newVar(newForm, "resultLen", null);
		newProp(newForm, "maxnbest", "3");
		newProp(newForm, "inputmodes", "voice");
		newProp(newForm, "bargein", "false");
		newProp(newForm, "maxspeechtimeout", Settings.maxspeechtimeout + "s");
		this.getRootElement().addContent(newForm);
		return newForm;
	}

	/**
	 * Builds the footer for the POMDP Voice Document. Catch the list with
	 * multiple recognized moves said by the user. Get the corresponding user
	 * utterances. Get the corresponding confidence scores.
	 * 
	 * @param field
	 *            the node to which the footer will be added
	 * @param nextPath
	 *            the location of the servlet for the next attribute
	 * @param noInputCounter
	 *            counts the noinput events in order to avoid unlimited loops of
	 *            dialogues
	 */
	public void buildFooter(Element field, String nextPath,
			String noInputCounter, String user) {
	
		int grammarType = Settings.usersetting[Settings.getuserpos(user)].grammarType;

		newVar(field, "myResult1", "");
		newVar(field, "myResult2", "");
		newVar(field, "myResult3", "");
		newVar(field, "Speak1", "");
		newVar(field, "Speak2", "");
		newVar(field, "Speak3", "");
		newVar(field, "Confidence1", "");
		newVar(field, "Confidence2", "");
		newVar(field, "Confidence3", "");

		Element catchNomatch = new Element("catch");
		newAssign(catchNomatch, "com", "'request'");
		newCatch(field, nextPath, noInputCounter,
				"noinput nomatch error.badfetch error.unsupported.language error.noresource");
		Element filled = new Element("filled");
		newAssign(filled, "resultArray", "application.lastresult$");
		newAssign(filled, "resultLen", "resultArray.length");

		Element ifEle = new Element("if");
		ifEle.setAttribute("cond", "resultArray.length >= 3");
		switch (grammarType) {
		case IGrammar.JSGF:
			newAssign(ifEle, "myResult1", "resultArray[0].interpretation");
			newAssign(ifEle, "myResult2", "resultArray[1].interpretation");
			newAssign(ifEle, "myResult3", "resultArray[2].interpretation");
			break;
		default:
			newAssign(ifEle, "myResult1", "resultArray[0].interpretation.move");
			newAssign(ifEle, "myResult2", "resultArray[1].interpretation.move");
			newAssign(ifEle, "myResult3", "resultArray[2].interpretation.move");
		}
		newAssign(ifEle, "Speak1", "resultArray[0].utterance");
		newAssign(ifEle, "Speak2", "resultArray[1].utterance");
		newAssign(ifEle, "Speak3", "resultArray[2].utterance");
		newAssign(ifEle, "Confidence1", "resultArray[0].confidence");
		newAssign(ifEle, "Confidence2", "resultArray[1].confidence");
		newAssign(ifEle, "Confidence3", "resultArray[2].confidence");

		Element elseIfEle1 = new Element("elseif");
		elseIfEle1.setAttribute("cond", "resultArray.length >= 2");
		ifEle.addContent(elseIfEle1);
		switch (grammarType) {
		case IGrammar.JSGF:
			newAssign(ifEle, "myResult1", "resultArray[0].interpretation");
			newAssign(ifEle, "myResult2", "resultArray[1].interpretation");
			break;
		default:
			newAssign(ifEle, "myResult1", "resultArray[0].interpretation.move");
			newAssign(ifEle, "myResult2", "resultArray[1].interpretation.move");
		}
		newAssign(ifEle, "myResult3", "null");
		newAssign(ifEle, "Speak1", "resultArray[0].utterance");
		newAssign(ifEle, "Speak2", "resultArray[1].utterance");
		newAssign(ifEle, "Speak3", "null");
		newAssign(ifEle, "Confidence1", "resultArray[0].confidence");
		newAssign(ifEle, "Confidence2", "resultArray[1].confidence");
		newAssign(ifEle, "Confidence3", "null");

		Element elseIfEle2 = new Element("elseif");
		elseIfEle2.setAttribute("cond", "resultArray.length >= 1");
		ifEle.addContent(elseIfEle2);
		switch (grammarType) {
		case IGrammar.JSGF:
			newAssign(ifEle, "myResult1", "resultArray[0].interpretation");
			break;
		default:
			newAssign(ifEle, "myResult1", "resultArray[0].interpretation.move");
		}

		newAssign(ifEle, "myResult2", "null");
		newAssign(ifEle, "myResult3", "null");
		newAssign(ifEle, "Speak1", "resultArray[0].utterance");
		newAssign(ifEle, "Speak2", "null");
		newAssign(ifEle, "Speak3", "null");
		newAssign(ifEle, "Confidence1", "resultArray[0].confidence");
		newAssign(ifEle, "Confidence2", "null");
		newAssign(ifEle, "Confidence3", "null");

		filled.addContent(ifEle);
		newAssign(filled, "com", "'work'");
		newAssign(filled, "speak", "move$.utterance");
		field.addContent(filled);
		newSubmit(
				filled,
				nextPath,
				"com agenda speak user myResult1 myResult2 myResult3 Speak1 Speak2 Speak3 resultLen Confidence1 Confidence2 Confidence3 ");
	}

	public VoiceDocumentPOMDP fillDocument(Vector<CoreMove> utterance,
			Vector<CoreMove> grammar, List<Agenda> actualAgenda, String nextPath,
			String user, String noInputCounter) {
		return fillDocument(utterance, grammar, actualAgenda, nextPath, user,
				noInputCounter, new VoiceDocumentPOMDP(grammarObject));
	}

	public VoiceDocumentPOMDP fillDocument(Vector<CoreMove> utterance,
			Vector<CoreMove> grammar, List<Agenda> actualAgenda, String nextPath,
			String user, String noInputCounter, VoiceDocumentPOMDP VDoc) {

		if ((!utterance.isEmpty()) && (grammar.isEmpty())) { // System Turn
			boolean disconnect = containsDisconnects(utterance);
			
			Element dialogueForm = VDoc.newDialogueForm(
					actualAgenda.get(0).getLocalName(), user);
			Element block = new Element("block");
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
					actualAgenda.get(0).getLocalName(), user);
			Element field = new Element("field");
			field.setAttribute("name", "move");

			newProp(field, "timeout", Settings.loopDuration + "s");
			dialogueForm.addContent(field);
			grammarObject.buildGrammar(field, grammar, user);
			VDoc.buildFooter(field, nextPath, noInputCounter, user);

		} else if ((!utterance.isEmpty()) && (!grammar.isEmpty())) { // Exchange
			Element dialogueForm = VDoc.newDialogueForm(
					actualAgenda.get(0).getLocalName(), user);
			Element field = new Element("field");
			field.setAttribute("name", "move");
			newProp(field, "timeout", Settings.loopDuration + "s");
			dialogueForm.addContent(field);
			grammarObject.buildGrammar(field, grammar, user);
			VDoc.buildUtterance(field, utterance, actualAgenda, nextPath, user);
			VDoc.buildFooter(field, nextPath, noInputCounter, user);
		}
		return VDoc;
	}

	public VoiceDocumentPOMDP buildOutput(String whereAmI, String VDocname,
			String output, String user) {
		return buildOutput(whereAmI, VDocname, output, user,
				new VoiceDocumentPOMDP(grammarObject));
	}

	public VoiceDocumentPOMDP buildOutput(String whereAmI, String VDocname,
			String output, String user, VoiceDocumentPOMDP VDoc) {
		Element dan1 = VDoc.newDialogueForm(VDocname, user);
		Element block1 = new Element("block");
		dan1.addContent(block1);
		newPrompt(block1, output);
		newAssign(block1, "com", "'request'");
		newSubmit(block1, whereAmI, "com user");
		return VDoc;
	}

}
