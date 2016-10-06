package presenter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import model.DialogueAction;
import model.KristinaModel;
import model.SemanticsOntology;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
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
import owlSpeak.kristina.KristinaMove;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class LAConverter {

	

	public static List<Resource> convertToMove(String rdf, String user, String act, String dialogue) {
		Model model = ModelFactory.createDefaultModel();
		model.read(new ByteArrayInputStream(rdf.getBytes(StandardCharsets.UTF_8)), null, "RDF/XML");
		
		ResIterator tmp = model.listResourcesWithProperty(RDF.type,
				model.getResource(dialogue + "UserAction"));

		Resource start = tmp.next();
		NodeIterator it = model.listObjectsOfProperty(start,
				model.getProperty(act + "contains"));
		LinkedList<Resource> usrmove = new LinkedList<Resource>();
		while (it.hasNext()) {
			Resource res = it.next().asResource();
			res = ResourceUtils.renameResource(res, res.getNameSpace()
					+ UUID.randomUUID().toString());
			
			SemanticsOntology.add(res, model, user);
			usrmove.add(res);
		}

		return usrmove;
	}

	public static String convertFromMove(List<KristinaMove> sysmovs,
			double valence, double arousal, String dialogue) {
		Model model = ModelFactory.createDefaultModel();
		String response = "http://kristina-project.eu/tmp#"
				+ UUID.randomUUID().toString();

		model.createResource(response, model.getResource(dialogue + "SystemAction"));
		model.addLiteral(model.getResource(response),
				model.getProperty(dialogue + "hasValence"), valence);
		model.addLiteral(model.getResource(response),
				model.getProperty(dialogue + "hasArousal"), arousal);
		String tmp = "";
		
		for (KristinaMove sysmov : sysmovs) {
			String moveName = "http://kristina-project.eu/tmp#"
					+ UUID.randomUUID().toString();
			model.add(model.getResource(response),
					model.getProperty(dialogue + "containsSystemAct"), model.getResource(moveName));
			if (sysmov.equals(sysmovs.get(0))) {
				model.add(model.getResource(response),
						model.getProperty(dialogue + "startWith"), model.getResource(moveName));
			}else{
				model.add(model.getResource(tmp), model.getProperty(dialogue+"followedBy"), model.getResource(moveName));
			}
			tmp = moveName;
			switch (sysmov.getDialogueAction()) {

			case DialogueAction.DECLARE:
				model.createResource(moveName,
						model.getResource(dialogue + "Declare"));
				model.addLiteral(model.getResource(moveName),
						model.getProperty(dialogue + "verbosity"), 0);
				model.addLiteral(model.getResource(moveName),
						model.getProperty(dialogue + "directness"), 1);
				model.addLiteral(model.getResource(moveName),
						model.getProperty(dialogue + "isFormal"), false);
				model.addLiteral(model.getResource(moveName),
						model.getProperty(dialogue + "isAdvice"), false);
				model.addLiteral(model.getResource(moveName),
						model.getProperty(dialogue + "isBelief"), false);

				List<ReifiedStatement> stmtList = sysmov.getStatements();
				for (ReifiedStatement stmt : stmtList) {
					model.createReifiedStatement(stmt.getURI(),
							stmt.getStatement());
					model.add(model.getResource(moveName),
							model.getProperty(dialogue + "containsSemantics"),
							model.getResource(stmt.getURI()));
				}
				break;
			case DialogueAction.READ_NEWSPAPER:
				model.createResource(moveName,
						model.getResource(dialogue + "ReadNewspaper"));

				model.add(model.getResource(moveName),
						model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.SHOW_WEATHER:
				model.createResource(moveName,
						model.getResource(dialogue + "ShowWeather"));

				model.add(model.getResource(moveName),
						model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			case DialogueAction.CANNED:
				model.createResource(moveName,
						model.getResource(dialogue + "Canned"));
				model.add(model.getResource(moveName),
						model.getProperty(dialogue + "text"), sysmov.getText());
				break;
			default:
				model.createResource(moveName,
						model.getResource(sysmov.getDialogueAction()));
				break;
			}
		}
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		model.write(result, "RDF/XML");
		/*try {
			return result.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return result.toString();
	}

	public static String convertFromEmotion(double valence, double arousal, String dialogue)
			throws OWLOntologyCreationException, OWLOntologyStorageException {

		Model model = ModelFactory.createDefaultModel();
		String response = "http://kristina-project.eu/tmp#"
				+ UUID.randomUUID().toString();

		model.createResource(response, model.getResource(dialogue + "SystemAction"));
		model.addLiteral(model.getResource(response),
				model.getProperty(dialogue + "hasValence"), valence);
		model.addLiteral(model.getResource(response),
				model.getProperty(dialogue + "hasArousal"), arousal);

		String moveName = "http://kristina-project.eu/tmp#"
				+ UUID.randomUUID().toString();
		model.add(model.getResource(response),
				model.getProperty(dialogue + "containsSystemAct"), moveName);
		model.createResource(moveName, model.getResource(dialogue + "Empty"));

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		model.write(result, "RDF/XML");
		/*try {
			return result.toString("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return result.toString();
	}
	

}
