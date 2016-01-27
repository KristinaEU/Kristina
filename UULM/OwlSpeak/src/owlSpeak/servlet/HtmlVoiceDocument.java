package owlSpeak.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Vector;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.ProcessingInstruction;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.plugin.Result;

public class HtmlVoiceDocument extends Document implements OwlDocument {

	private static final long serialVersionUID = 1923754516578673778L;

	public HtmlVoiceDocument(){
		super();
		this.setDocType(new DocType("HTML", "-//W3C//DTD HTML 4.01 Transitional//EN",  "http://www.w3.org/TR/html4/loose.dtd"));
		generateDocument();
	};
	
	public Element newDialogueForm(String name, String user){
		Element newForm = new Element(name);

		//newForm.setAttribute("id",id);
		this.getRootElement().addContent(newForm);
		return newForm;
	}
	
	public void generateDocument(){
		ProcessingInstruction access = new org.jdom.ProcessingInstruction("access-control", "allow=\"*\"");
		Element html = new Element("HTML");
		Element meta = new Element("META");
		meta.setAttribute("HTTP-EQUIV", "PRAGMA");
		meta.setAttribute("CONTENT", "NO-CACHE");
		html.addContent(meta);

		//html.setAttribute("version", "2.1");		
		this.setRootElement(html);
		this.addContent(access);
	}
	
	public HtmlVoiceDocument generateSleep(String whereAmI, String user, String noInputCounter){
		HtmlVoiceDocument VDoc = new HtmlVoiceDocument();
		Element dan1 = VDoc.newDialogueForm("body", user);
		Element name = new Element("a");
		Element br = new Element("br");
		name.addContent("Pagetype: "+"sleep");
		dan1.addContent(name);
		dan1.addContent(br);
		//this.addPrompt(dan1, VDocname);
		//Element body = new Element("body");
		//dan1.addContent(body);
		Element outel = new Element("a");
		outel.addContent("System is suspended.");
		dan1.addContent(outel);
		//dan1.addContent(br);
		Element br2 = new Element("br");
		dan1.addContent(br2);
		Element next = new Element("a");
		next.setAttribute("href", "./owlSpeak?com=request&user="+user);
		next.addContent("to request");
		dan1.addContent(next);
		//this.addPrompt(dan1, output);
		return VDoc;
	}
	
	public HtmlVoiceDocument buildOutput(String whereAmI, String VDocname, String output, String user) {
		HtmlVoiceDocument VDoc = new HtmlVoiceDocument();
		Element dan1 = VDoc.newDialogueForm("body", user);
		Element name = new Element("a");
		Element br = new Element("br");
		name.addContent("Pagetype: "+VDocname);
		dan1.addContent(name);
		dan1.addContent(br);
		//this.addPrompt(dan1, VDocname);
		//Element body = new Element("body");
		//dan1.addContent(body);
		Element outel = new Element("a");
		outel.addContent(output);
		dan1.addContent(outel);
		//dan1.addContent(br);
		Element br2 = new Element("br");
		dan1.addContent(br2);
		Element next = new Element("a");
		next.setAttribute("href", "./owlSpeak?com=request&user="+user);
		next.addContent("to request");
		dan1.addContent(next);
		//this.addPrompt(dan1, output);
		return VDoc;
	}

	public void addPrompt(Element e, String utterance){
		Element pre = new Element("a");
		pre.addContent("Utterance: ");
		Element utt = new Element("a");
		utt.addContent(utterance);
		e.addContent(pre);
		pre.addContent(utt);
	}
	
	public void addGrammar(Element e, String grammar, String agenda, String move, String user) {
		Element pre = new Element("a");
		Element gram = new Element("a");
		pre.addContent("grammar: ");
		
		// com, move, user, resultLen, event, inputmode, bargein
		String queryString = "com=work&agenda="+agenda+"&move="+move+"&user="+user+"&resultLen=1&event=success&inputmode=speak&bargein=false";

		// confidence, Confidence0
		float confidence = new Random().nextFloat();
		confidence = confidence * (1.0f - 0.3f) + 0.3f;
		queryString += "&confidence="+confidence+"&Confidence0="+confidence;
		
		// interpretation, myResult0
		queryString += "&interpretation="+move+"&myResult0="+move;
		
		// speak, Speak0
		String speak = grammar.replaceAll("\\[[^\\]]+\\]","").replaceAll("[<,>]","").trim();
		queryString += "&speak="+speak+"&Speak0="+speak;
		
		// missing: duration
		
		gram.setAttribute("href", "./owlSpeak?" + queryString);
		
		gram.addContent(grammar);
		e.addContent(pre);
		pre.addContent(gram);
	}

	public void addBr(Element e){
		Element br = new Element("br");
		e.addContent(br);
	}

	public void buildFooter(Element field1, String nextPath, String user) {
		Element next = new Element("a");
		next.setAttribute("href", nextPath);
		next.addContent(nextPath);
		field1.addContent(next);
	}

	public void buildGrammar(Element element, Vector<CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user) {
		Element add = new Element("a");
		element.addContent(add);
		add.addContent("The system understands: ");
		this.addBr(add);
		Move tempMv;
		while (!grammar.isEmpty()){
			Grammar tempGr;
			CoreMove tempCoreMv;
			tempCoreMv=grammar.firstElement();
			grammar.remove(0);
			tempMv=tempCoreMv.move;
			tempGr=tempMv.getGrammar();
			if(!tempGr.hasGrammarString()) continue;
			this.addGrammar(add, tempCoreMv.parseGrammarVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)], user), actualAgenda.getLocalName(), tempCoreMv.onto.Name+"_a1n1d_"+tempMv.getLocalName(), user);
			this.addBr(add);
		}
	}

	@Override
	public void buildUtterance(Element element, Vector<CoreMove> utterance, Agenda actualAgenda, String whereAmI, String user) {
		Element add = new Element("a");
		element.addContent(add);
		add.addContent("The system says: ");
		Element br = new Element("br");
		add.addContent(br);
		while (!utterance.isEmpty()){
			Element br1 = new Element("br");			
			CoreMove tempCoreMv;
			tempCoreMv=utterance.firstElement();
			utterance.remove(0);
			String Prompt2=tempCoreMv.parseUtteranceVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)], user);
			this.addPrompt(add, Prompt2);
			add.addContent(br1);
		}
	}

	@Override
	public OwlDocument fillDocument(Vector<CoreMove> utterance,Vector<CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user, String noInputCounter) {
		HtmlVoiceDocument VDoc = new HtmlVoiceDocument();
		Element dan = VDoc.newDialogueForm("Body", user);
		if(!utterance.isEmpty()) this.buildUtterance(dan, utterance, actualAgenda, whereAmI, user);
		if(!grammar.isEmpty()) this.buildGrammar(dan, grammar, actualAgenda, whereAmI, user); else this.buildFooter(dan, whereAmI+"?com=work&agenda="+actualAgenda.getLocalName()+"&user="+user, user);
		return VDoc;
	}


	public OwlDocument buildSolveConflict(String utterance, Vector<String[]> grammar, Agenda actualAgenda, String whereAmI, String user){
		HtmlVoiceDocument VDoc = new HtmlVoiceDocument();
		
		Element dan = VDoc.newDialogueForm("Body", user);
		
		Element add = new Element("a");
		dan.addContent(add);
		add.addContent("The system says: ");
		Element br = new Element("br");
		add.addContent(br);
		this.addPrompt(add, utterance);
		Element br2 = new Element("br");
		add.addContent(br2);
		
		Element add2 = new Element("a");
		dan.addContent(add2);
		add2.addContent("The system understands: ");
		this.addBr(add2);
		String[] tempStr;
		while (!grammar.isEmpty()){
			tempStr=grammar.firstElement();
			grammar.remove(0);
			this.addGrammar(add2, tempStr[0], actualAgenda.getLocalName(), tempStr[0]+"_a1n1d_"+tempStr[1], user);
			this.addBr(add2);
		}
	
		//if(!utterance.isEmpty()) this.buildUtterance(dan, utterance, actualAgenda, whereAmI);
//		if(!grammar.isEmpty()) this.buildGrammar(dan, grammar, actualAgenda, whereAmI); else this.buildFooter(dan, whereAmI+"?com=work&agenda="+actualAgenda.getLocalName());
		return VDoc;

	}
	
	//Dummy
	public OwlDocument buildPluginAlternatives(Vector<Result> result, Agenda actualAgenda, String whereAmI, String user){
		return null;
	}
	

	public static void main (String[] argv) {
		String s = "[<muss weg>] soll <bleiben>";
		String regexp = "\\[[^\\]]+\\]";
		String regexp2 = "[<,>]";
		String sNew = s.replaceAll(regexp, "").replaceAll(regexp2, "").trim();
		System.out.println(s);
		System.out.println(regexp);
		System.out.println(sNew);
	}
}
