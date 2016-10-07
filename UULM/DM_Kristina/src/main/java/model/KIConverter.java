package model;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Move;
import owlSpeak.kristina.KristinaMove;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class KIConverter {
	
	

	public static Set<KristinaMove> convertToMove(String rdf, String user, OWLOntology dmOnto, OWLDataFactory factory, OWLOntologyManager manager, String onto, String dialogue){
		Model model = ModelFactory.createDefaultModel() ;
		model.read(new StringReader(rdf),null, "TURTLE");
		
		ResIterator start = model.listResourcesWithProperty(RDF.type,model.getResource(onto+"ResponseContainer"));
		Set<KristinaMove> set = new HashSet<KristinaMove>();
		while(start.hasNext()){
			Resource r = start.next();
			NodeIterator ns = model.listObjectsOfProperty(r, model.getProperty(onto+"containsResponse"));
			List<RDFNode> context = model.listObjectsOfProperty(r, model.getProperty(onto+"conversationalContext")).toList();
		
		while(ns.hasNext()){
			Resource node = ns.next().asResource();
			node = ResourceUtils.renameResource(node, node.getNameSpace()+ UUID.randomUUID().toString());
			OWLNamedIndividualImpl indi= new OWLNamedIndividualImpl(IRI.create(node.getURI()));
			set.add(new KristinaMove(indi, dmOnto, factory, manager));
			manager.addAxiom(dmOnto, factory.getOWLDeclarationAxiom(indi));
			if(node.hasProperty(RDF.type,model.getResource(onto+"StatementResponse"))){
				if(node.hasProperty(model.getProperty(onto+"responseType"),model.getResource(onto+"free_text"))){
					if(!Collections.disjoint(context,model.listResourcesWithProperty(RDF.type,model.getResource(OntologyPrefix.ontoContext+"ReadNewspaperContext")).toList())){
						manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(dialogue+"ReadNewspaper")), indi));
					}else{
						//TODO: change this to IRResponse after first prototype, introduce IRResponse to the remaining code
						manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(dialogue+"ReadNewspaper")), indi));
					}
				}else if(node.hasProperty(model.getProperty(onto+"responseType"),model.getResource(onto+"structured"))){
					manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(dialogue+"Declare")), indi));
				}
			}else if (node.hasProperty(RDF.type,model.getResource(onto+"WeatherResponse"))){
				manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(dialogue+"ShowWeather")), indi));
			}else if (node.hasProperty(RDF.type,model.getResource(onto+"AdditionalInformationRequest"))){
				manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(dialogue+"AdditionalInformationRequest")), indi));
			}else if (node.hasProperty(RDF.type,model.getResource(onto+"UnknownResponse"))){
				manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(dialogue+"Unknown")), indi));
			}
			
			SemanticsOntology.add(node, model, user);
		}
		

		}
		
		
		
		return set;
	}
}
