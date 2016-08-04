package owlSpeak.kristina.util;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Move;
import owlSpeak.kristina.KristinaMove;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class KIConverter {
	
	private static final String onto = "http://kristina-project.eu/ontologies/responses#";

	public static Set<Move> convertToMove(String rdf,OWLOntology dmOnto, OWLDataFactory factory, OWLOntologyManager manager){
		Model model = ModelFactory.createDefaultModel() ;
		model.read(new StringReader(rdf),null, "TURTLE");
		
		ResIterator start = model.listResourcesWithProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),model.getResource(onto+"ResponseContainer"));
		Set<Move> set = new HashSet<Move>();
		while(start.hasNext()){
			Resource r = start.next();
			NodeIterator ns = model.listObjectsOfProperty(r, model.getProperty(onto+"containsResponse"));
		
		while(ns.hasNext()){
			Resource node = ns.next().asResource();
			OWLNamedIndividualImpl indi= new OWLNamedIndividualImpl(IRI.create(node.getURI()));
			set.add(new KristinaMove(indi, dmOnto, factory, manager));
			manager.addAxiom(dmOnto, factory.getOWLDeclarationAxiom(indi));
			if(node.hasProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),model.getResource(onto+"StatementResponse"))){
				if(node.hasProperty(model.getProperty("http://kristina-project.eu/ontologies/responses#responseType"),model.getResource(onto+"free_text"))){
					manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create("http://kristina-project.eu/ontologies/dialogue_actions#ReadNewspaper")), indi));
				}else if(node.hasProperty(model.getProperty("http://kristina-project.eu/ontologies/responses#responseType"),model.getResource("http://kristina-project.eu/ontologies/responses#structured"))){
					manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create("http://kristina-project.eu/ontologies/dialogue_actions#Statement")), indi));
				}
			}
		}
		}
		
		return set;
	}
}
