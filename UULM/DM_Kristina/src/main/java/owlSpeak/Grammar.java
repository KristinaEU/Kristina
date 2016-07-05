package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.engine.Settings;

/**
 * The class that represents the Grammars.
 * 
 * @author Tobias Heinroth
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @author Karim
 */
public class Grammar extends GenericClass {
	/**
	 * the name of the Grammar Class
	 */
	public final String name = "Grammar";

	/**
	 * creates a Grammar object by calling super
	 * 
	 * @param indi
	 *            the OWLIndividual that is contained
	 * @param onto
	 *            the OWLOntology indi belongs to
	 * @param factory
	 *            the OWLDataFactory which should be used
	 * @param manager
	 *            the OWLOntologyManger that mangages onto
	 */
	public Grammar(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}

	/**
	 * returns a collection of OWLLiteral that are linked in the GrammarString
	 * field of the Grammar.
	 * 
	 * @return the collection of OWLLiteral.
	 */
	public Collection<OWLLiteral> getGrammarString() {
		Iterator<OWLLiteral> all = GenericProvider.getOWLLiteralProperties(
				indi, GenericProvider.gramStringProp, onto, factory).iterator();
		Collection<OWLLiteral> coll = new java.util.LinkedList<OWLLiteral>();
		while (all.hasNext()) {
			OWLLiteral o = all.next();
			if ((o.getLang()).equals((Settings.asrMode).substring(0, 2))) {
				coll.add(o);
			}
		}
		return coll;
	}

	/**
	 * returns true if the GrammarString field contains at least on item.
	 * 
	 * @return true if GrammarString is not empty.
	 */
	public boolean hasGrammarString() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.gramStringProp, onto, factory);
	}

	/**
	 * adds an OWLLiteral to the GrammarString field of the Grammar.
	 * 
	 * @param newGrammarString
	 *            the OWLLiteral that should be added.
	 */
	public void addGrammarString(OWLLiteral newGrammarString) {
		GenericProvider.addOWLLiteralProperty(indi, newGrammarString,
				GenericProvider.gramStringProp, onto, factory, manager);
	}

	/**
	 * removes an OWLLiteral from the GrammarString field of the Grammar.
	 * 
	 * @param oldGrammarString
	 *            the OWLLiteral that should be removed.
	 */
	public void removeGrammarString(OWLLiteral oldGrammarString) {
		GenericProvider.removeOWLLiteralProperty(indi, oldGrammarString,
				GenericProvider.gramStringProp, onto, factory, manager);
	}

	/**
	 * adds a Grammar object to the GeneralGrammar field of the Grammar.
	 * 
	 * @param g
	 *            the Grammar object that should be added.
	 */

	// new
	public void addGeneralGrammar(Grammar g) {
		GenericProvider.addIndividual(indi, g.indi,
				GenericProvider.generalGramProp, onto, factory, manager);
	}

	/**
	 * replaces the specified OWLLiteral with a new OWLLiteral.
	 * 
	 * @param oldGrammarString
	 *            the old OWLLiteral that should be replaced.
	 * @param newGrammarString
	 *            the new OWLLiteral that should replace the current OWLLiteral.
	 */
	public void setGrammarString(OWLLiteral oldGrammarString,
			OWLLiteral newGrammarString) {
		GenericProvider.removeOWLLiteralProperty(indi, oldGrammarString,
				GenericProvider.gramStringProp, onto, factory, manager);
		GenericProvider.addOWLLiteralProperty(indi, newGrammarString,
				GenericProvider.gramStringProp, onto, factory, manager);
	}

	/**
	 * returns a collection of Grammar object which are contained in the field
	 * general grammars of the grammar owl individual represented by this object
	 * or null if no general grammars exist
	 * 
	 * @return a collection of grammars or null
	 */
	public Collection<Grammar> getGeneralGrammars() {
		Collection<GenericClass> col = GenericProvider.getIndividualColl(indi,
				GenericProvider.generalGramProp, onto, factory, manager);
		if (col != null && !col.isEmpty()) {
			LinkedList<Grammar> generalGrammars = new LinkedList<Grammar>();
			for (GenericClass c : col) {
				generalGrammars.add(c.asGrammar());
			}
			return generalGrammars;
		}
		return null;
	}

	/**
	 * returns a collection of OWLLiteral that are linked in the GrammarString
	 * field of the Grammar.
	 * 
	 * TODO comments of grammar file methods
	 * 
	 * @return the collection of OWLLiteral.
	 */
	public String getGrammarFile() {
		if (hasGrammarFile())
			return GenericProvider.getStringData(indi,
					GenericProvider.gramFileProp, onto, factory);
		return null;
	}

	/**
	 * returns true if the GrammarString field contains at least on item.
	 * 
	 * @return true if GrammarString is not empty.
	 */
	public boolean hasGrammarFile() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.gramFileProp, onto, factory);
	}

	/**
	 * adds an OWLLiteral to the GrammarString field of the Grammar.
	 * 
	 * @param newGrammarFile
	 *            the OWLLiteral that should be added.
	 */
	public void addGrammarFile(String newGrammarFile) {
		GenericProvider.addStringData(indi, newGrammarFile,
				GenericProvider.gramFileProp, onto, factory, manager);
	}

	/**
	 * removes the GrammarFile string from the GrammarFile field of the Grammar.
	 * 
	 */
	public void removeGrammarFile() {
		if (hasGrammarFile()) {
			GenericProvider.removeStringData(indi, getGrammarFile(),
					GenericProvider.gramFileProp, onto, factory, manager);
		}
	}
}