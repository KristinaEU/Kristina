package owlSpeak.servlet.document;

import java.util.Vector;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;

public class HtmlSaltDocument extends HtmlVoiceDocument{

	private boolean change;
	
	public HtmlSaltDocument(){
		this.change=false;
	};

	public HtmlSaltDocument(boolean changeDom){
		this.change = changeDom;
	};

	/**
	 * 
	 */
	private static final long serialVersionUID = 1619510428873866034L;

	public void generateDocument(){
		ProcessingInstruction access = new org.jdom.ProcessingInstruction("access-control", "allow=\"*\"");
		ProcessingInstruction importsalt = new org.jdom.ProcessingInstruction("import", "namespace=\"salt\" implementation=\"#speech-add-in\"");
		Element html = new Element("HTML");
		Namespace salt = Namespace.getNamespace("salt", "http://www.saltforum.org/2002/SALT");
		html.addNamespaceDeclaration(salt);
		Element saltobject = new Element("object");
		saltobject.setAttribute("id", "speech-add-in");
		saltobject.setAttribute("CLASSID", "clsid:33cbfc53-a7de-491a-90f3-0e782a7e347a");
		Element garg = new Element("garg");
		saltobject.addContent(garg);
		
		html.addContent(saltobject);
		html.addContent(importsalt);
		//html.setNamespace(salt);
		//html.setAttribute("xmlns", "salt", salt);
		
		//html.setAttribute("xmlns:salt", "http://www.saltforum.org/2002/SALT");
		Element meta = new Element("META");
		meta.setAttribute("HTTP-EQUIV", "PRAGMA");
		meta.setAttribute("CONTENT", "NO-CACHE");
		html.addContent(meta);
		Element head = new Element("head");
		html.addContent(head);
		//html.setAttribute("version", "2.1");		
		this.setRootElement(html);
		this.addContent(access);
		
	}
	
	public void buildUtterance(Element element, Vector<CoreMove> utterance, Agenda actualAgenda, String whereAmI, String user) {
		Element add = new Element("div");
		element.addContent(add);
		Element bold = new Element ("b");
		
		bold.addContent("The system says: ");
		add.addContent(bold);
		Element br = new Element("br");
		add.addContent(br);
		Element prompt = new Element("prompt");
		Namespace salt = Namespace.getNamespace("salt", "http://www.saltforum.org/2002/SALT");
	
		prompt.setNamespace(salt);
		prompt.setAttribute("id", "first");
		element.addContent(prompt);
		if(change){
			this.addPrompt(add, "The context has changed.");
			prompt.addContent("The context has changed.");
		}
		while (!utterance.isEmpty()){
			CoreMove tempCoreMv;
			tempCoreMv=utterance.firstElement();
			utterance.remove(0);
			String Prompt2=tempCoreMv.parseUtteranceVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)], user);
			this.addPrompt(add, Prompt2);
			prompt.addContent(Prompt2);
		}
		Element br2 = new Element("br");
		add.addContent(br2);
	}
	
	public void buildGrammar(Element element, Vector<CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user) {
		Element add = new Element("div");
		element.addContent(add);
		Element bold = new Element("b");
		bold.addContent("The system understands: ");
		add.addContent(bold);
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
			this.addGrammar(add, tempCoreMv.parseGrammarVariables(tempCoreMv.onto.beliefSpace[Settings.getuserpos(user)], user), actualAgenda.getLocalName(), tempCoreMv.onto.Name+":"+tempMv.getLocalName(), user);
			this.addBr(add);
		}
	}
	
	public void addPrompt(Element e, String utterance){
		Element pre = new Element("div");
		pre.addContent(utterance);
		e.addContent(pre);
	}
	
	public void buildFooter(Element field1, String nextPath, String user) {
		Element next = new Element("a");
		next.setAttribute("href", nextPath);
		next.addContent("Next");
		field1.addContent(next);
	}
	
	public OwlDocument fillDocument(Vector<CoreMove> utterance,
			Vector<CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user) {
		HtmlSaltDocument VDoc = new HtmlSaltDocument();
		Element dan = VDoc.newDialogueForm("body", user);
		dan.setAttribute("onload", "first.Start()");
		Element img = new Element("img");
		img.setAttribute("src", "http://owlspeakonto.dyndns.info/owl2.jpg");
		img.setAttribute("style", "width:150px;height:110px");
		img.setAttribute("align", "middle");
		Element owlSpeak = new Element("h2");
		//owlSpeak.setAttribute("align", "middle");
		owlSpeak.addContent(img);
		owlSpeak.addContent("OwlSpeak HTML Interface");
		dan.addContent(owlSpeak);
		
		
		if(!utterance.isEmpty()) this.buildUtterance(dan, utterance, actualAgenda, whereAmI, user);
		if(!grammar.isEmpty()) this.buildGrammar(dan, grammar, actualAgenda, whereAmI, user); else this.buildFooter(dan, whereAmI+"?com=work&agenda="+actualAgenda.getLocalName()+"&user="+user, user);
		return VDoc;
	}

	public void addGrammar(Element e, String grammar, String agenda, String move, String user) {
//		Element pre = new Element("div");
		Element gram = new Element("a");
	//	pre.addContent("grammar: ");
		gram.setAttribute("href", "./owlSpeak?com=work&agenda="+agenda+"&move="+move+"&user="+user);
		gram.addContent(grammar);
		//e.addContent(pre);
		e.addContent(gram);
	}

}
