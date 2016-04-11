/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlSpeak.kristina.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import owlSpeak.Move;
import owlSpeak.kristina.KristinaMove;
import presenter.KristinaPresenter;

/**
 *
 * @author lpragst
 */
public class InputOutputConverter {

	private final OWLOntologyManager manager;
	private final OWLDataFactory factory;

	//Data of main KRISTINA ontology definition
	private final String coreName = "http://kristina-project.eu/ontologies/framesituation/core";
	private final PrefixManager corePrefix = new DefaultPrefixManager(
			coreName + "#");
	private final IRI coreIRI = IRI.create(coreName);
	
	//Data of ontology for output
	private final OWLImportsDeclaration importDeclaraton;
	private final IRI ioOntoIRI = IRI
			.create("http://kristina-project.eu/systemMoves");
	
	//Ontology for DM
	private final OWLOntology dmOnto;
	
	public InputOutputConverter(OWLOntology o, OWLDataFactory f, OWLOntologyManager m) throws URISyntaxException, OWLOntologyCreationException{
		manager = m;
		factory = f;
		dmOnto = o;
		
		importDeclaraton = factory
				.getOWLImportsDeclaration(coreIRI);
		
		// TODO: temporary solution until ontology is in place
		manager.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://kristina-project.eu/ontologies/framesituation/core"),
				IRI.create(getClass().getResource("/Example_Input_Output/frame_situation_Stam.ttl"))));
				
		OWLOntology onto = manager.createOntology(ioOntoIRI);
		
		//TODO: very ugly, but safes time for the first 'real' input
		BufferedReader r;
		
		try {
			r = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/Example_Input_Output/inform.ttl")));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();
			OWLOntology tmp2 = buildOWLOntology(data);
			manager.removeOntology(tmp2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	// Input functions
	public Set<Move> buildKristinaMove(String rdf)
			throws OWLOntologyCreationException {
		
		OWLOntology onto = buildOWLOntology(rdf);

		OWLReasoner reasoner = new StructuralReasonerFactory()
				.createReasoner(onto);
		OWLClass speechact = factory.getOWLClass(":SpeechAct", corePrefix);
		NodeSet<OWLNamedIndividual> ns = reasoner
				.getInstances(speechact, false);
		

		Set<Move> set = new HashSet<Move>();
		for(Node<OWLNamedIndividual> node: ns){
			set.add(new KristinaMove(node.getRepresentativeElement(), dmOnto, factory, manager));
		}
		
		return set;

	}

	// Output functions
	/**
	 * Builds an rdf representation of the system move and the emotion
	 * 
	 * @param sysmov
	 * @param valence
	 * @param arousal
	 * @return rdf String
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws URISyntaxException 
	 */
	public String buildOutput(KristinaMove sysmov, float valence,
			float arousal) throws OWLOntologyCreationException,
			OWLOntologyStorageException {
		// create empty ontology
		OWLOntology onto = createOntology();

		// add semantic content of system move to ontology
		Set<OWLAxiom> set = sysmov.getAxioms();
		manager.addAxioms(onto, set);

		// find speech act individual
		OWLReasoner reasoner = new StructuralReasonerFactory()
				.createReasoner(onto);
		OWLClass speechact = factory.getOWLClass(":SpeechAct", corePrefix);
		NodeSet<OWLNamedIndividual> ns = reasoner
				.getInstances(speechact, false);
		assert ns.getNodes().size() == 1 : "KristinaMove Object does not contain one corresponding Speech Act.";
		OWLNamedIndividual ind = ns.iterator().next()
				.getRepresentativeElement();

		// add emotion to speech act individual
		Set<OWLAxiom> setVA = addVA(valence, arousal, ind);
		manager.addAxioms(onto, setVA);

		String result = buildRDFString(onto);
		return result;
	}

	/**
	 * Builds an rdf representation of an empty system move with an attached
	 * emotion
	 * 
	 * @param valence
	 * @param arousal
	 * @return rdf String
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 * @throws URISyntaxException 
	 */
	public String buildEmotionOutput(float valence, float arousal)
			throws OWLOntologyCreationException, OWLOntologyStorageException{

		// create empty speech act
		OWLClass speechact = factory.getOWLClass(":SpeechAct", corePrefix);
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI
				.create("http://kristina-project.eu/systemMoves#move1"));
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(speechact, ind);

		// add emotion to speech act individual
		Set<OWLAxiom> set = addVA(valence, arousal, ind);

		// add everything to empty ontology
		set.add(axiom);
		OWLOntology onto = createOntology();
		manager.addAxioms(onto, set);

		String result = buildRDFString(onto);
		return result;
	}

	// Helper functions

	/**
	 * Converts an rdf Object to a corresponding String
	 * 
	 * @param onto
	 * @return
	 * @throws OWLOntologyStorageException
	 */
	private String buildRDFString(OWLOntology onto)
			throws OWLOntologyStorageException {

		StringDocumentTarget t = new StringDocumentTarget();
		manager.saveOntology(onto, t);

		return t.toString();

	}

	private OWLOntology buildOWLOntology(String rdf)
			throws OWLOntologyCreationException {
		OWLOntology onto = null;
		try{
			onto = manager
		.loadOntologyFromOntologyDocument(new StringDocumentSource(rdf));
		}catch(OWLOntologyAlreadyExistsException e){
			manager.removeOntology(e.getOntologyID());
			try{
			onto = manager
					.loadOntologyFromOntologyDocument(new StringDocumentSource(rdf));
			}catch(Exception e1){
				e1.printStackTrace();
			}
		}
		
		return onto;
	}

	/**
	 * Attaches an emotion to an individual
	 * 
	 * @param valence
	 * @param arousal
	 * @param ind
	 * @return The axioms connecting the emotion to the individual
	 */
	private Set<OWLAxiom> addVA(float valence, float arousal,
			OWLNamedIndividual ind) {

		OWLAnnotation val = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(":hasValence", corePrefix),
				factory.getOWLLiteral(valence));
		OWLAnnotation ar = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(":hasArousal", corePrefix),
				factory.getOWLLiteral(arousal));

		OWLAxiom axiomV = factory.getOWLAnnotationAssertionAxiom(ind.getIRI(),
				val);
		OWLAxiom axiomA = factory.getOWLAnnotationAssertionAxiom(ind.getIRI(),
				ar);

		Set<OWLAxiom> set = new HashSet<>();
		set.add(axiomA);
		set.add(axiomV);
		return set;
	}

	/**
	 * creates an ontology with the necessary imports for KRISTINA, but without
	 * any axioms
	 * 
	 * @return
	 * @throws OWLOntologyCreationException
	 * @throws URISyntaxException 
	 */
	private OWLOntology createOntology()
			throws OWLOntologyCreationException {

		// create empty ontology
		manager.removeOntology(manager.getOntology(ioOntoIRI));
		OWLOntology onto = manager.createOntology(ioOntoIRI);

		// add and load imports
		manager.applyChange(new AddImport(onto, importDeclaraton));
		
		manager.loadOntology(coreIRI);

		return onto;
	}

}
