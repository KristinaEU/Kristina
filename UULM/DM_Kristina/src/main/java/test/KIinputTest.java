package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RSIterator;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.coode.owlapi.rdfxml.parser.AnonymousNodeChecker;
import org.coode.owlapi.rdfxml.parser.OWLRDFConsumer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import uk.ac.manchester.cs.owl.owlapi.turtle.parser.TurtleParser;

public class KIinputTest {
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	private static final OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
	
	public static void main(String[] args){
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(
					"./src/main/java/test/sleeping_habits_response_1.ttl"));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();

			//TODO: temporary solution until ontology is in place
			//manager.addIRIMapper(new SimpleIRIMapper(IRI.create("http://kristina-project.eu/ontologies/responses/v1"), IRI.create(new File("./src/main/java/test/response.ttl"))));
			
			OWLOntology onto = manager.loadOntologyFromOntologyDocument(new File("./src/main/java/test/sleeping_habits_response_1.ttl"));
			
			
			/*OWLDataFactory factory = manager.getOWLDataFactory();
			OWLAxiom axiomA = factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(IRI.create("http://www.w3.org/1999/02/22-rdf-syntax-ns#subject")),IRI.create("http://kristina-project.eu/ontologies/testdata#Statement_7"), IRI.create("http://kristina-project.eu/ontologies/sleeping_habits_pattern#TemporalEntity_1"));
			manager.applyChange(new AddAxiom(onto, axiomA));*/
			
			/*Set<OWLAxiom> axioms = onto.getAxioms();
			for(OWLAxiom ax: axioms){
				System.out.println(ax);
			}*/
			
			StringDocumentTarget t = new StringDocumentTarget();

            // perform agenda selection
            
            manager.saveOntology(onto, t);
            //System.out.println(t);
            
            /*manager.saveOntology(onto, new FileOutputStream(new File("./src/main/java/test/sleeping_habits_response_2.ttl")));
            
            OWLOntology onto2 = manager2.loadOntologyFromOntologyDocument(new File("./src/main/java/test/sleeping_habits_response_2.ttl"));
            
            t = new StringDocumentTarget();

            // perform agenda selection
            
            manager2.saveOntology(onto2, t);
            System.out.println(t);*/   
            
            Model model = ModelFactory.createDefaultModel() ;
            model.read(new FileInputStream("./src/main/java/test/sleeping_habits_response_1.ttl"), "something","TURTLE") ;
            
            OntModel owlOntology = ModelFactory.createOntologyModel( OntModelSpec.RDFS_MEM, model );
            for ( OntClass klass : owlOntology.listClasses().toList() ) {
                System.out.println( klass );
            }
            
            
            //model.write(System.out, "TURTLE");
            
            
            
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
