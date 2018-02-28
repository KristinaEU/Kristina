package owlSpeak.kristina;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

//import model.DialogueAction;
//import model.KristinaModel;


import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.engine.Core;
import owlSpeak.engine.Settings;
//import owlSpeak.kristina.KristinaMove;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class LAConverter {

	public static KristinaMove convertToKristinaMove(String rdf, String user, String act, String dialogue,
			OWLOntology dmOnto, OWLDataFactory factory, OWLOntologyManager manager) {

		Collection<KristinaMove> all = new OSFactory(dmOnto, factory, manager).getAllKristinaMoveInstances();
		for (KristinaMove m : all) {
			if (m.getLocalName().contains("userMove") && m.getIsLastUserMove())
				m.setIsLastUserMove(false);
		}
		
		KristinaMove userMove = new OSFactory(dmOnto, factory, manager)
				.createKristinaMove("userMove_" + UUID.randomUUID().toString());
		userMove.setIsNonPersistent(true);
		userMove.setIsLastUserMove(true);

		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8)), null, "RDF/XML");

		ResIterator tmp = model.listResourcesWithProperty(RDF.type, model.getResource(dialogue + "UserAction"));

		Resource res = tmp.next().asResource();
		res = ResourceUtils.renameResource(res, userMove.getFullName());
		SemanticsOntology.add(res, model, user);
		
		return userMove;
	}

	public static String convertFromKristinaAgendas(List<KristinaAgenda> sysmovs, double valence, double arousal,
			String dialogue, float verbosity, float directness, boolean isFormal, boolean isAdvice, boolean isBelief) {
		Model model = ModelFactory.createDefaultModel();
		String response = "http://kristina-project.eu/tmp#" + UUID.randomUUID().toString();

		model.createResource(response, model.getResource(dialogue + "SystemAction"));
		model.addLiteral(model.getResource(response), model.getProperty(dialogue + "hasValence"), valence);
		model.addLiteral(model.getResource(response), model.getProperty(dialogue + "hasArousal"), arousal);
		String tmp = "";

		for (KristinaAgenda sysmov : sysmovs) {
			String moveName = "http://kristina-project.eu/tmp#" + UUID.randomUUID().toString();
			model.add(model.getResource(response), model.getProperty(dialogue + "containsSystemAct"),
					model.getResource(moveName));
			if (sysmov.equals(sysmovs.get(0))) {
				model.add(model.getResource(response), model.getProperty(dialogue + "startWith"),
						model.getResource(moveName));
			} else {
				model.add(model.getResource(tmp), model.getProperty(dialogue + "followedBy"),
						model.getResource(moveName));
			}
			tmp = moveName;

			model.addLiteral(model.getResource(moveName), model.getProperty(dialogue + "verbosity"), verbosity);
			model.addLiteral(model.getResource(moveName), model.getProperty(dialogue + "directness"), directness);
			model.addLiteral(model.getResource(moveName), model.getProperty(dialogue + "isFormal"), isFormal);
			model.addLiteral(model.getResource(moveName), model.getProperty(dialogue + "isAdvice"), isAdvice);
			model.addLiteral(model.getResource(moveName), model.getProperty(dialogue + "isBelief"), isBelief);

			switch (sysmov.getDialogueAction()) {
			case DialogueAction.DECLARE:
			case DialogueAction.STATEMENT:
			case DialogueAction.ADDITIONAL_INFORMATION:
				model.createResource(moveName, model.getResource(dialogue + "Declare"));

				List<ReifiedStatement> stmtList = sysmov.getStatements();
				for (ReifiedStatement stmt : stmtList) {
					model.createReifiedStatement(stmt.getURI(), stmt.getStatement());
					model.add(model.getResource(moveName), model.getProperty(dialogue + "containsSemantics"),
							model.getResource(stmt.getURI()));
				}
				break;
			case DialogueAction.READ_NEWSPAPER:
				model.createResource(moveName, model.getResource(dialogue + "ReadNewspaper"));

				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.SHOW_WEBPAGE:
				model.createResource(moveName, model.getResource(dialogue + "ShowWebpage"));
				
				model.add(model.getResource(moveName), model.getProperty(dialogue + "url"), sysmov.getURL());
				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.IR_RESPONSE:
				model.createResource(moveName, model.getResource(dialogue + "IRResponse"));

				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				model.add(model.getResource(moveName), model.getProperty(dialogue + "source"), sysmov.getSource());
				break;
			case DialogueAction.SHOW_WEATHER:
				model.createResource(moveName, model.getResource(dialogue + "ShowWeather"));
				model.add(model.getResource(moveName), model.getProperty(dialogue + "weatherClassification"), sysmov.getWeather());
				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.CANNED:
				model.createResource(moveName, model.getResource(dialogue + "Canned"));
				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.PROACTIVE_CANNED:
				model.createResource(moveName, model.getResource(dialogue + "ProactiveCanned"));
				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.PROACTIVE_LIST:
				model.createResource(moveName, model.getResource(dialogue + "ProactiveList"));
				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.REQUEST_CLARIFICATION:
				model.createResource(moveName, model.getResource(dialogue + "Clarification"));
				model.add(model.getResource(moveName), model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			default:
				model.createResource(moveName, model.getResource(sysmov.getDialogueAction()));
				break;
			}
			Set<String> topics = sysmov.getKITopic();
			for(String s: topics){
				model.add(model.getResource(moveName), model.getProperty(dialogue + "topic"), model.getResource(s));
			}
		}
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		model.write(result, "RDF/XML");
		model.close();
		try {
			return result.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			return result.toString();
		}
	}

	// public static String convertFromEmotion(double valence, double arousal,
	// String dialogue)
	// throws OWLOntologyCreationException, OWLOntologyStorageException {
	//
	// Model model = ModelFactory.createDefaultModel();
	// String response = "http://kristina-project.eu/tmp#"
	// + UUID.randomUUID().toString();
	//
	// model.createResource(response, model.getResource(dialogue +
	// "SystemAction"));
	// model.addLiteral(model.getResource(response),
	// model.getProperty(dialogue + "hasValence"), valence);
	// model.addLiteral(model.getResource(response),
	// model.getProperty(dialogue + "hasArousal"), arousal);
	//
	// String moveName = "http://kristina-project.eu/tmp#"
	// + UUID.randomUUID().toString();
	// model.add(model.getResource(response),
	// model.getProperty(dialogue + "containsSystemAct"), moveName);
	// model.createResource(moveName, model.getResource(dialogue + "Empty"));
	//
	// ByteArrayOutputStream result = new ByteArrayOutputStream();
	// model.write(result, "RDF/XML");
	// /*try {
	// return result.toString("UTF-8");
	// } catch (UnsupportedEncodingException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }*/
	// return result.toString();
	// }

}