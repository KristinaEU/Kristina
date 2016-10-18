package test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import model.OntologyPrefix;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import owlSpeak.kristina.KristinaMove;


public class ontoTest {
	
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
	public static void main(String[] args){
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(
					"./src/main/resources/results/KI2DM/ki-output__16.ttl"));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();

			Model model = ModelFactory.createDefaultModel();
			model.read(new StringReader(data), null, "TURTLE");
			String onto = OntologyPrefix.onto;
			
			ResIterator start = model.listResourcesWithProperty(RDF.type,model.getResource(onto+"ResponseContainer"));
			Set<KristinaMove> set = new HashSet<KristinaMove>();
			while(start.hasNext()){
				Resource res = start.next();
				NodeIterator ns = model.listObjectsOfProperty(res, model.getProperty(onto+"containsResponse"));
				List<RDFNode> context = model.listObjectsOfProperty(res, model.getProperty(onto+"conversationalContext")).toList();
				
				System.out.println(!Collections.disjoint(context,model.listResourcesWithProperty(RDF.type,model.getResource(OntologyPrefix.ontoContext+"ReadNewspaperContext")).toList()));	
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
