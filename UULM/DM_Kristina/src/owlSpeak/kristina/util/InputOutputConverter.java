/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package owlSpeak.kristina.util;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import owlSpeak.kristina.KristinaMove;

/**
 *
 * @author lpragst
 */
public class InputOutputConverter {

	private static final OWLOntologyManager manager = OWLManager
			.createOWLOntologyManager();
	private static final OWLDataFactory factory = manager.getOWLDataFactory();
	
	private static final String coreName = "http://kristina-project.eu/ontologies/framesituation/core";
	private static final PrefixManager prefix = new DefaultPrefixManager(
			coreName + "#");
	private static final IRI core = IRI.create(coreName);
	private static final OWLImportsDeclaration importDeclaraton = factory
			.getOWLImportsDeclaration(core);
	private static final IRI ontoIRI = IRI.create("http://kristina-project.eu/systemMoves");

	public static String buildOutput(KristinaMove sysmov, float valence, float arousal) throws OWLOntologyCreationException, OWLOntologyStorageException{
		OWLOntology onto = createOntology();
		
		//TODO fill ontology with system move semantics and VA
		
		return buildRDFString(onto);
	}

	public static String buildEmotionOutput(float valence, float arousal)
			throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		OWLClass speechact = factory.getOWLClass(":SpeechAct", prefix);
		OWLNamedIndividual ind = factory.getOWLNamedIndividual(IRI
				.create("http://kristina-project.eu/systemMoves#move1"));
		OWLAxiom axiom = factory.getOWLClassAssertionAxiom(speechact, ind);
		Set<OWLAxiom> set = addVA(valence, arousal, ind);
		set.add(axiom);
		OWLOntology onto = createOntology();
		manager.addAxioms(onto, set);

		return buildRDFString(onto);
	}

	private static String buildRDFString(OWLOntology onto)
			throws OWLOntologyStorageException {

		StringDocumentTarget t = new StringDocumentTarget();
		manager.saveOntology(onto, t);

		return t.toString();

	}

	private static Set<OWLAxiom> addVA(float valence, float arousal,
			OWLNamedIndividual ind) {
		OWLAnnotation val = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(":hasValence", prefix),
				factory.getOWLLiteral(valence));
		OWLAnnotation ar = factory.getOWLAnnotation(
				factory.getOWLAnnotationProperty(":hasArousal", prefix),
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
	
	private static OWLOntology createOntology() throws OWLOntologyCreationException{
		manager.removeOntology(manager.getOntology(ontoIRI));
		OWLOntology onto = manager.createOntology(ontoIRI);
		
		manager.applyChange(new AddImport(onto, importDeclaraton));
		// TODO: temporary solution until ontology is in place
		manager.addIRIMapper(new SimpleIRIMapper(
				IRI.create("http://kristina-project.eu/ontologies/framesituation/core"),
				IRI.create(new File("./src/test/frame_situation_Stam.ttl"))));
		manager.loadOntology(core);
		
		return onto;
	}

}
