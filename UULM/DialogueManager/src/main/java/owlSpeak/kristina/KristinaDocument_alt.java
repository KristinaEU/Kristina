package owlSpeak.kristina;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Vector;



import java.util.stream.Collectors;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.plugin.Result;
import owlSpeak.servlet.document.OwlDocument;
import owlSpeak.servlet.grammar.IGrammar;

public class KristinaDocument_alt implements OwlDocument{

		private static final long serialVersionUID = 3539566299404044938L;
		private String systemMove = null;
		private List<Agenda> agenda = null;
		private boolean isExitMove = false;
		private KristinaGrammar_alt grammarObject = null;

		public KristinaDocument_alt(IGrammar _grammar) {
			super();
			if(_grammar instanceof KristinaGrammar_alt){
				grammarObject = (KristinaGrammar_alt)_grammar;
			}else{
				System.err.println("Grammtic Type is not compatible with KristinaDocument. Choose KristinaGrammar: 5.");
			}
		}

		public String getSystemMove() {
			return systemMove;
		}

		public String getAgenda() {
			return agenda.stream().map(a -> a.getLocalName()).collect(Collectors.toList()).toString();
		}
		
		public List<Agenda> getAgendaObj() {
			return agenda;
		}

		public boolean isExitMove() {
			return isExitMove;
		}

		@Override
		public OwlDocument fillDocument(Vector<CoreMove> utterance,
				Vector<CoreMove> grammar, List<Agenda> actualAgenda, String whereAmI,
				String user, String noInputCounter) {
			if (!(utterance.isEmpty())) {
				Move m = utterance.firstElement().move;
				systemMove = m.getLocalName();
				isExitMove = m.getIsExitMove();
				agenda = actualAgenda;
			}
			
			
			Element root = new Element("grammar");
			grammarObject.buildGrammar(root, grammar, user);
			Document doc = new Document(root);
			
			try {
				XMLOutputter xmlOutput = new XMLOutputter();
				FileWriter writer = new FileWriter(Settings.homePath+"grammar.xml");
				 
				// display nice nice
				xmlOutput.setFormat(Format.getPrettyFormat());
				xmlOutput.output(doc, writer);
				
				writer.close();
	 
			} catch (IOException e) {
				System.err.println("Dieser Codeabschnitt wurde speziell f�r das Zusammenspiel von SceneMaker und OWLSpeak geschrieben.\nEine falsche Ordnerstruktur oder ein falsch gesetzter HomePath f�hren zu Fehlern.");
				e.printStackTrace();
			}
			
			return this;
		}

		@Override
		public void generateDocument() {
			// TODO Auto-generated method stub
		}

		@Override
		public OwlDocument generateSleep(String whereAmI, String user,
				String noInputCounter) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Element newDialogueForm(String agenda, String user) {
			return null;
		}

		@Override
		public void buildUtterance(Element element, Vector<CoreMove> utterance,
				List<Agenda> actualAgenda, String whereAmI, String user) {
		}

		@Override
		public OwlDocument buildOutput(String whereAmI, String VDocname,
				String output, String user) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OwlDocument buildSolveConflict(String utterance,
				Vector<String[]> grammar, Agenda actualAgenda, String whereAmI,
				String user) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public OwlDocument buildPluginAlternatives(Vector<Result> result,
				Agenda actualAgenda, String whereAmI, String user) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String output() {
			// TODO Auto-generated method stub
			return null;
		}
	}
