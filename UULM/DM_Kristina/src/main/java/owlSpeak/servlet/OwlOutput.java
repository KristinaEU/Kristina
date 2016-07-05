package owlSpeak.servlet;

import java.util.Vector;
import org.jdom.Element;

import owlSpeak.Agenda;
import owlSpeak.engine.CoreMove;

/**
 * Interface OwlOutput used to specify the functions for the Output Documents
 * @author Dan Denich
 * @version 1.0
 * 
 */
public interface OwlOutput {

	/**
	 * used for the construction of the document, usually creates the header 
	 */
	public void generateDocument();
	
	/**
	 * generates a document that will pause the dialogue.
	 * should get the sleep-state from whereAmI+"?com=getsleep"
	 * @param whereAmI the netadress of the servlet
	 * @param user the name of the user
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return the filled document
	 */
	public OwlOutput generateSleep(String whereAmI, String user, String noInputCounter);

	/**
	 * generates a document with the current Agenda (for variable initialization) and the username
	 * @param agenda the name of the current active Agenda
	 * @param user the name of the user
	 * @return the root element of the document 
	 */
	public Element newDialogueForm(String agenda, String user);			
		
	/**
	 * the main function to fill the document with the given vectors of CoreMoves.
	 * calls functions buildUtterance and buildGrammar
	 * @param utterance the vector that contains the CoreMoves with the Utterances 
	 * @param grammar the vector that contains the CoreMoves with the Grammars
	 * @param actualAgenda the current active Agenda
	 * @param whereAmI the netadress of the servlet
	 * @param user the name of the user
	 * @param noInputCounter counts the noinput events in order to avoid unlimited loops.
	 * @return returns the filled document
	 */
	public OwlOutput fillDocument(Vector<CoreMove> utterance, Vector<CoreMove> grammar, Agenda actualAgenda, String whereAmI, String user, String noInputCounter);
	
	/**
	 * function used to create a message output
	 * @param whereAmI the netadress of the servlet
	 * @param VDocname the name of the document
	 * @param output the messagestring for output
	 * @param user the name of the user
	 * @return returns the filled document with the output
	 */
	public OwlOutput buildOutput(String whereAmI, String VDocname, String output, String user);
	
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
	public OwlOutput buildSolveConflict(String utterance, Vector<String[]> grammar, Agenda actualAgenda, String whereAmI, String user);
	
}
