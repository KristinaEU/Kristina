package owlSpeak.servlet.grammar;

import java.util.Vector;

import org.jdom.Element;

import owlSpeak.Move;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;

public interface IGrammar {

	public static final int GSL = 1;
	public static final int GRXML = 2;
	public static final int JSGF = 3;
	public static final int JSGFasFile = 4;
	public static final int KristinaGrammar = 5;

	/**
	 * builds the grammar CDATA Element from the given stack
	 * 
	 * @param element
	 *            the Element to which the grammar will be added
	 * @param grammar
	 *            the grammar stack of which the grammar will be build
	 */
	public void buildGrammar(Element element, Vector<CoreMove> grammar,
			String user);

	public void buildSleepGrammar(Element element);

	public void buildSolveConflict(Element element, Vector<String[]> grammar);
	
	public String[] parse(String string, String[] grammars);
	
	public String[] grammarsOfMove(Move move, String user, OwlSpeakOntology onto);
}
