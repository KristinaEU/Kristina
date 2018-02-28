package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.OSFactory;

public class KIConverter {

	public static Set<KristinaAgenda> convertToKristinaAgendas(String rdf, String user, OWLOntology dmOnto,
			OWLDataFactory factory, OWLOntologyManager manager, String onto, String dialogue) {

		Model model = ModelFactory.createDefaultModel();
		model.read(new StringReader(rdf), null, "TURTLE");

		ResIterator start = model.listResourcesWithProperty(RDF.type, model.getResource(onto + "ResponseContainer"));
		Set<KristinaAgenda> set = new HashSet<KristinaAgenda>();
		while (start.hasNext()) {
			Resource r = start.next();
			NodeIterator ns = model.listObjectsOfProperty(r, model.getProperty(onto + "containsResponse"));
			List<RDFNode> context = model.listObjectsOfProperty(r, model.getProperty(onto + "conversationalContext"))
					.toList();
			while (ns.hasNext()) {
				Resource node = ns.next().asResource();
				KristinaAgenda agenda = new OSFactory(dmOnto, factory, manager)
						.createKristinaAgenda("agenda_KI_" + UUID.randomUUID().toString());
				if(!node.hasProperty(RDF.type,model.getResource(onto+"AdditionalResponse"))
						&& !node.hasProperty(RDF.type,model.getResource(onto+"AdditionalInformationRequest"))
						&& !node.hasProperty(RDF.type,model.getResource(onto+"ProactiveResponse"))){
					set.add(agenda);
				}
				node = ResourceUtils.renameResource(node, agenda.getFullName());
				if (node.hasProperty(RDF.type, model.getResource(onto + "StatementResponse"))) {
					if (node.hasProperty(model.getProperty(onto + "responseType"),
							model.getResource(onto + "free_text"))) {
//						if (node.hasProperty(RDF.type,model.getResource(onto+"AdditionalResponse"))){
//							agenda.addDialogueAction(IRI.create(DialogueAction.CANNED));
//						}
						if (node.hasLiteral(model.getProperty(onto+"isIR"),false)){
							agenda.addDialogueAction(IRI.create(DialogueAction.CANNED));
						}
						else if (!Collections
								.disjoint(
										context, model
												.listResourcesWithProperty(RDF.type,
														model.getResource(
																OntologyPrefix.ontoContext + "ReadNewspaperContext"))
												.toList())) {
							agenda.addDialogueAction(IRI.create(DialogueAction.READ_NEWSPAPER));
						} else {
							agenda.addDialogueAction(IRI.create(DialogueAction.IR_RESPONSE));
						}
					} else if (node.hasProperty(model.getProperty(onto + "responseType"),
							model.getResource(onto + "structured"))) {
						agenda.addDialogueAction(IRI.create(DialogueAction.STATEMENT));
					}
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "WeatherResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.SHOW_WEATHER));
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "ProactiveResponse"))) {
					if(node.hasLiteral(model.getProperty(onto+"isCanned"),true)){
						agenda.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
					}else{
						agenda.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_LIST));
					}
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "UnknownResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.UNKNOWN));
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "NotFoundResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.NOT_FOUND));
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "URLResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.SHOW_WEBPAGE));
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "PositiveResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.AFFIRM));
				}else if (node.hasProperty(RDF.type, model.getResource(onto + "NegativeResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.REJECT));
				}else if (node.hasProperty(RDF.type,model.getResource(onto+"SpecifyingInformationResponse"))||node.hasProperty(RDF.type, model.getResource(onto + "ClarificationResponse"))) {
					agenda.addDialogueAction(IRI.create(DialogueAction.REQUEST_CLARIFICATION));
				}else if(node.hasProperty(RDF.type,model.getResource(onto+"AdditionalResponse")) || node.hasProperty(RDF.type,model.getResource(onto+"AdditionalInformationRequest"))){
					agenda.addDialogueAction(IRI.create(DialogueAction.ADDITIONAL_INFORMATION));
				}
				SemanticsOntology.add(node, model, user);
			}
		}
		return set;
	}
	
}