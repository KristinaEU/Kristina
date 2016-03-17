package owlSpeak.servlet;

import java.util.Vector;
import org.jdom.Element;
import owlSpeak.Agenda;
import owlSpeak.engine.CoreMove;


public class KristinaDocument implements OwlOutput{

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
    public KristinaDocument fillDocument(Vector<CoreMove> utterance, Vector<CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user, String noInputCounter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KristinaDocument buildOutput(String whereAmI, String VDocname, String output, String user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public KristinaDocument buildSolveConflict(String utterance, Vector<String[]> grammar, Agenda actualAgenda, String whereAmI, String user) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static KristinaDocument buildEmotionOutput(float valence, float arousal) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
