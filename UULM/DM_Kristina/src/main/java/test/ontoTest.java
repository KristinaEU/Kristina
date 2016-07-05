package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

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

import owlSpeak.kristina.util.InputOutputConverter;


public class ontoTest {
	
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		
	public static void main(String[] args){
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(
					"./src/main/java/test/inform_VA.owl"));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();

			//TODO: temporary solution until ontology is in place
			manager.addIRIMapper(new SimpleIRIMapper(IRI.create("http://kristina-project.eu/ontologies/framesituation/core"), IRI.create(new File("./src/main/java/test/frame_situation_Stam.ttl"))));
			OWLOntology onto = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(data));
			Set<OWLAxiom> set = onto.getAxioms();
			for(OWLAxiom ax: set){
				System.out.println(ax);
			}
			
			OWLReasoner reasoner = new StructuralReasonerFactory().createReasoner(onto);
			
			OWLDataFactory factory = manager.getOWLDataFactory();

	        float valence = 0.5f;
	        float arousal = 0.5f;
			OWLAnnotation val = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create("http://kristina-project.eu/ontologies/framesituation/core#hasValence")), factory.getOWLLiteral(valence));
			OWLAnnotation ar = factory.getOWLAnnotation(factory.getOWLAnnotationProperty(IRI.create("http://kristina-project.eu/ontologies/framesituation/core#hasArousal")), factory.getOWLLiteral(arousal));
			
			OWLClass speechact = factory.getOWLClass(IRI.create("http://kristina-project.eu/ontologies/framesituation/core#SpeechAct"));
			Iterator<Node<OWLNamedIndividual>> an = reasoner.getInstances(speechact,false).iterator();
			//Iterator<OWLAnnotationAssertionAxiom> an = onto.getAxioms(AxiomType.ANNOTATION_ASSERTION).iterator();
			
			while(an.hasNext()){
				OWLNamedIndividual ind = an.next().getRepresentativeElement();
				OWLAxiom axiomV = factory.getOWLAnnotationAssertionAxiom(ind.getIRI(),val);
				OWLAxiom axiomA = factory.getOWLAnnotationAssertionAxiom(ind.getIRI(),ar);
				manager.applyChange(new AddAxiom(onto, axiomV));
				manager.applyChange(new AddAxiom(onto, axiomA));
			}
			
			StringDocumentTarget t = new StringDocumentTarget();

            // perform agenda selection
            
            manager.saveOntology(onto, t);
            
            //System.out.println(t);
			
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
