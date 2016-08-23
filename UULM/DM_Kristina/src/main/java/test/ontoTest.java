package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Set;

import model.OntologyPrefix;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
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


public class ontoTest {
	
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
	public static void main(String[] args){
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(
					"./src/main/resources/ExampleData_LA/example-imFine_User.ttl"));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();

			Model model = ModelFactory.createDefaultModel();
			model.read(new StringReader(data), null, "TURTLE");
			
			NodeIterator it = model.listObjectsOfProperty(RDF.type);
			System.out.println(model.getResource(OntologyPrefix.ontoLA+"Fine"));
			while(it.hasNext()){
				RDFNode res = it.next();
				System.out.println(res);
			}
			System.out.println(model.listSubjectsWithProperty(RDF.type, model.getResource(OntologyPrefix.ontoLA + "Fine")).hasNext());
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
