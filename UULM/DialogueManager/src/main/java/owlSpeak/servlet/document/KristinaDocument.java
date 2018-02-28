package owlSpeak.servlet.document;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


import java.util.stream.Collectors;

import org.jdom.Document;
import org.jdom.Element;
import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.plugin.Result;


public class KristinaDocument extends Document implements OwlDocument{
	
	private static final long serialVersionUID = 1L;
	private List<KristinaAgenda> agendas = new LinkedList<KristinaAgenda>();
	
	public List<KristinaAgenda> getAgenda() {
		return agendas;
	}

	@Override
    public void generateDocument() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KristinaDocument generateSleep(String whereAmI, String user, String noInputCounter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Element newDialogueForm(String agenda, String user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KristinaDocument fillDocument(Vector<CoreMove> utterance, Vector<CoreMove> grammar, List<Agenda> actualAgenda, String whereAmI, String user, String noInputCounter) {
    	agendas = actualAgenda.stream().map(a -> a.asKristinaAgenda()).collect(Collectors.toList());
		return this;
	}

    @Override
    public KristinaDocument buildOutput(String whereAmI, String VDocname, String output, String user) {
		return null;
    }

    @Override
    public KristinaDocument buildSolveConflict(String utterance, Vector<String[]> grammar, Agenda actualAgenda, String whereAmI, String user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static KristinaDocument buildEmotionOutput(float valence, float arousal) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public void buildUtterance(Element element, Vector<CoreMove> utterance,
			List<Agenda> actualAgenda, String whereAmI, String user) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.		
	}

	@Override
	public String output() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public OwlDocument buildPluginAlternatives(Vector<Result> result,
			Agenda actualAgenda, String whereAmI, String user) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}