package owlSpeak;

import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

import owlSpeak.engine.Settings;
/**
 * The class that represents the DialogueDomains.
 * @author Tobias Heinroth.
 */
public class DialogueDomain extends GenericClass{
	/**
	 * the name of the DialogueDomain Class	
	 */
	public final String name = "DialogueDomain";
	public final String domPropName = "#domainName";
	public final String domPropKeywords = "#domainKeywords";
	/**
	 * creates a DialogueDomain object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public DialogueDomain(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}
	
	/**
	 * returns a collection of RDFSLiterals that are linked in the DomainName field of the DialogueDomain.
	 * @return the collection of RDFSLiterals.
	 */
	public Collection<OWLLiteral> getDomainName(){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropName));
		Iterator<OWLAnnotation> annos = indi.asOWLNamedIndividual().getAnnotations(onto, p).iterator();
		Collection<OWLLiteral> coll = new java.util.LinkedList<OWLLiteral>();
		while(annos.hasNext()){
			OWLLiteral temp = (OWLLiteral)annos.next().getValue();
    		if((temp.getLang()).equals((Settings.asrMode).substring(0, 2))){
    			coll.add(temp);
    		}
		}
		return coll;
	}
    /**
     * returns true if the DomainName field contains at least on item.
     * @return true if DomainName is not empty.
     */
	public boolean hasDomainName(){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropName));
		return (indi.asOWLNamedIndividual().getAnnotations(onto, p).size()>0);
	}
    /**
     * adds a OWLLiteral to the DomainName field of the DialogueDomain.
     * @param newDomainName the OWLLiteral that should be added.
     */
	public void addDomainName(OWLLiteral newDomainName){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropName));
		OWLAnnotation newAnno = factory.getOWLAnnotation(p,factory.getOWLLiteral(newDomainName.getLiteral(), newDomainName.getLang())); 
		manager.applyChange(new AddAxiom(onto, factory.getOWLAnnotationAssertionAxiom(IRI.create(indi.toStringID()), newAnno)));
	}
    /**
     * removes a String from the DomainName field of the DialogueDomain.
     * @param oldDomainName the String that should be removed.
     */
	public void removeDomainName(OWLLiteral oldDomainName){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropName));
		OWLAnnotation oldAnno = factory.getOWLAnnotation(p,factory.getOWLLiteral(oldDomainName.getLiteral(), oldDomainName.getLang())); 
		manager.applyChange(new RemoveAxiom(onto, factory.getOWLAnnotationAssertionAxiom(IRI.create(indi.toStringID()), oldAnno)));
	}
    /**
     * replaces a OWLLiteral in the DomainName field with a new OWLLiteral.
     * @param oldDomainName the old OWLLiteral that should be replaced.
     * @param newDomainName the new OWLLiteral that should replace the current OWLLiteral.
     */
	public void setDomainName(OWLLiteral oldDomainName, OWLLiteral newDomainName){
		removeDomainName(oldDomainName);
		addDomainName(newDomainName);
	}

	/**
	 * returns a collection of RDFSLiterals that are linked in the DomainKeywords field of the DialogueDomain.
	 * @return the collection of RDFSLiterals.
	 */
	public Collection<OWLLiteral> getDomainKeywords(){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropKeywords));
		Iterator<OWLAnnotation> annos = indi.asOWLNamedIndividual().getAnnotations(onto, p).iterator();
		Collection<OWLLiteral> coll = new java.util.LinkedList<OWLLiteral>();
		while(annos.hasNext()){
			OWLLiteral temp = (OWLLiteral)annos.next().getValue();
    		if((temp.getLang()).equals((Settings.asrMode).substring(0, 2))){
    			coll.add(temp);
    		}
		}
		return coll;
	}
    /**
     * returns true if the DomainKeywords field contains at least on item.
     * @return true if DomainKeywords is not empty.
     */
	public boolean hasDomainKeywords(){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropKeywords));
		return (indi.asOWLNamedIndividual().getAnnotations(onto, p).size()>0);
	}
    /**
     * adds a OWLLiteral to the DomainKeywords field of the DialogueDomain.
     * @param newDomainKeywords the OWLLiteral that should be added.
     */
	public void addDomainKeywords(OWLLiteral newDomainKeywords){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropName));
		OWLAnnotation newAnno = factory.getOWLAnnotation(p,factory.getOWLLiteral(newDomainKeywords.getLiteral(), newDomainKeywords.getLang())); 
		manager.applyChange(new AddAxiom(onto, factory.getOWLAnnotationAssertionAxiom(IRI.create(indi.toStringID()), newAnno)));
	}
    /**
     * removes a String from the DomainKeywords field of the DialogueDomain.
     * @param oldDomainKeywords the String that should be removed.
     */
	public void removeDomainKeywords(OWLLiteral oldDomainKeywords){
		OWLAnnotationProperty p = factory.getOWLAnnotationProperty(IRI.create(Settings.uri+domPropName));
		OWLAnnotation oldAnno = factory.getOWLAnnotation(p,factory.getOWLLiteral(oldDomainKeywords.getLiteral(), oldDomainKeywords.getLang())); 
		manager.applyChange(new RemoveAxiom(onto, factory.getOWLAnnotationAssertionAxiom(IRI.create(indi.toStringID()), oldAnno)));
	}
    /**
     * replaces a OWLLiteral in the DomainName field with a new OWLLiteral.
     * @param oldDomainKeywords the old OWLLiteral that should be replaced.
     * @param newDomainKeywords the new OWLLiteral that should replace the current OWLLiteral.
     */
	public void setDomainKeywords(OWLLiteral oldDomainKeywords, OWLLiteral newDomainKeywords){
		removeDomainName(oldDomainKeywords);
		addDomainName(newDomainKeywords);
	}

}
