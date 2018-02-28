package owlSpeak;

import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.engine.Settings;
/**
 * The class that represents the Utterances.
 * @author Tobias Heinroth.
 * @author Karim.
 */
public class Utterance extends GenericClass{
	/**
	 * the name of the Utterance Class	
	 */
	public final String name = "Utterance";
	/**
	 * creates a Utterance object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public Utterance(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}
	/**
	 * returns a collection of OWLLiteral that are linked in the UtteranceString field of the Utterance.
	 * @return the collection of OWLLiteral.
	 */
	public Collection<OWLLiteral> getUtteranceString(){
    	Iterator<OWLLiteral> all = GenericProvider.getOWLLiteralProperties(indi, GenericProvider.uttStringProp, onto, factory).iterator();
    	Collection<OWLLiteral> coll = new java.util.LinkedList<OWLLiteral>();
    	while(all.hasNext()){
    		OWLLiteral o = all.next();
    		if((o.getLang()).equals((Settings.asrMode).substring(0, 2))){
    			coll.add(o);
    		}
    	} 	
    	return coll;
    }
    /**
     * returns true if the UtteranceString field contains at least on item.
     * @return true if UtteranceString is not empty.
     */
	public boolean hasUtteranceString(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.uttStringProp, onto, factory);
    }
    /**
     * adds a OWLLiteral to the UtteranceString field of the Utterance.
     * @param newUtteranceString the OWLLiteral that should be added.
     */
	public void addUtteranceString(OWLLiteral newUtteranceString){
    	GenericProvider.addOWLLiteralProperty(indi, newUtteranceString, GenericProvider.uttStringProp, onto, factory, manager);
    }
	
	
	
	//new
	/**
     * adds an includesSemantic to the Utterance object.
     * @param s the SemanticGroup that should be added.
     */
	@Deprecated
	public void addincludesSemantic(SemanticGroup s  )
	{
		GenericProvider.addIndividual(indi,s.indi,GenericProvider.includesSemanticProp, onto, factory, manager );
	}
	//new
	/**
     * adds an includesSemantic to the Utterance object.
     * @param s the Semantic that should be added.
     */
	public void addincludesSemantic(Semantic s  )
	{
		GenericProvider.addIndividual(indi,s.indi,GenericProvider.includesSemanticProp, onto, factory, manager );
	}
	
	
    /**
     * removes a OWLLiteral from the UtteranceString field of the Utterance.
     * @param oldUtteranceString the OWLLiteral that should be removed.
     */
	public void removeUtteranceString(OWLLiteral oldUtteranceString){
    	GenericProvider.removeOWLLiteralProperty(indi, oldUtteranceString, GenericProvider.uttStringProp, onto, factory, manager);
    }
    /**
     * replaces the OWLLiteral in the UtteranceString field with the new OWLLiteral.
     * @param oldUtteranceString the old OWLLiteral that should be replaced by the new OWLLiteral.
     * @param newUtteranceString the new OWLLiteral.
     */
	public void setUtteranceString(OWLLiteral oldUtteranceString, OWLLiteral newUtteranceString){
		removeUtteranceString(oldUtteranceString);
		addUtteranceString(newUtteranceString);    	
    }
	/**
     * returns the volume of the Utterance.
     * @return the volume of the Utterance.
     */
    public String getVolume(){
    	return GenericProvider.getStringData(indi, GenericProvider.uttVolumeProp, onto, factory);
    }
    /**
     * returns true if the Volume field contains a number. 
     * @return true if volume is not empty.
     */
    public boolean hasVolume(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.uttVolumeProp, onto, factory);
    }
    /**
     * removes the Volume of the Utterance.
     * @param oldVolume the new Volume of the Utterance.
     */
    public void removeVolume(String oldVolume){
    	GenericProvider.removeStringData(indi, oldVolume, GenericProvider.uttVolumeProp, onto, factory, manager);	
    }
    /**
     * sets the Volume of the Utterance to the specified value.
     * @param oldVolume the current Volume.
     * @param newVolume the new Volume of the Utterance.
     */
    public void setVolume(String oldVolume, String newVolume){
    	removeVolume(oldVolume);
    	GenericProvider.addStringData(indi, newVolume, GenericProvider.uttVolumeProp, onto, factory, manager);
    }
}