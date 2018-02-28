package owlSpeak.kristina.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import model.SemanticsOntology;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
	
	private final static String act = "http://kristina-project.eu/ontologies/dialogue_actions#";
	//private final static String act = "http://kristina-project.eu/ontologies/la/action#";
	
	public static IRI convertToMove(String rdf){
		Model model = ModelFactory.createDefaultModel() ;
		model.read(new StringReader(rdf),null, "TURTLE");
		
		ResIterator start = model.listResourcesWithProperty(model.getProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),model.getResource(act+"UserAction"));
		
		Resource res = start.next();
		
		StmtIterator it = res.listProperties();
		Resource id = model.createResource(res.getNameSpace()+UUID.randomUUID().toString(), model.getResource(act+"UserAction"));

		IRI usrMove = IRI.create(id.getURI());
		
		while(it.hasNext()){
			Statement s = it.next();
			model.add(model.createStatement(id, s.getPredicate(), s.getObject()));
		}
		model.remove(res.listProperties());
		
		SemanticsOntology.add(usrMove, model);
		
		return usrMove;
	}

	public static String convertFromMove(List<KristinaMove> sysmovs, float valence,
			float arousal) {
		Model model = ModelFactory.createDefaultModel() ;
		String response = "http://kristina-project.eu/tmp#"+UUID.randomUUID().toString();
		
		model.createResource(response, model.getResource(act+"SystemAction"));
		model.addLiteral(model.getResource(response), model.getProperty(act+"hasValence"),valence);
		model.addLiteral(model.getResource(response), model.getProperty(act+"hasArousal"),arousal);
		
		for(KristinaMove sysmov: sysmovs){
			String moveName = "http://kristina-project.eu/tmp#"+UUID.randomUUID().toString();
			model.add(model.getResource(response), model.getProperty(act+"containsSystemAct"),moveName);
			if(sysmov.equals(sysmovs.get(0))){
				model.add(model.getResource(response), model.getProperty(act+"startWith"),moveName);
			}
		switch(sysmov.getDialogueAction()){
			
			case DECLARE:
				model.createResource(moveName, model.getResource(act+"Declare"));
				model.addLiteral(model.getResource(moveName), model.getProperty(act+"verbosity"),1);
				model.addLiteral(model.getResource(moveName), model.getProperty(act+"directness"),1);
				model.addLiteral(model.getResource(moveName), model.getProperty(act+"isFormal"),true);
				model.addLiteral(model.getResource(moveName), model.getProperty(act+"isAdvice"),true);
				model.addLiteral(model.getResource(moveName), model.getProperty(act+"isBelief"),true);
		
				List<ReifiedStatement> stmtList = sysmov.getStatements();
				for(ReifiedStatement stmt: stmtList){
					model.createReifiedStatement(stmt.getURI(), stmt.getStatement());
					model.add(model.getResource(moveName), model.getProperty(act+"containsSemantics"),model.getResource(stmt.getURI()));
				}
				break;
			case READ_NEWSPAPER:
				model.createResource(moveName, model.getResource(act+"ReadNewspaper"));
				
				model.add(model.getResource(moveName), model.getProperty(act+"text"), sysmov.getText());
				break;
			default:
				break;
		}
		}
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		model.write(result,"TURTLE");
		return result.toString();
	}
	
	public static String convertFromEmotion(float valence, float arousal)
			throws OWLOntologyCreationException, OWLOntologyStorageException{
		
		Model model = ModelFactory.createDefaultModel() ;
		String response = "http://kristina-project.eu/tmp#"+UUID.randomUUID().toString();
		
		model.createResource(response, model.getResource(act+"SystemAction"));
		model.addLiteral(model.getResource(response), model.getProperty(act+"hasValence"),valence);
		model.addLiteral(model.getResource(response), model.getProperty(act+"hasArousal"),arousal);
		

		String moveName = "http://kristina-project.eu/tmp#"+UUID.randomUUID().toString();
		model.add(model.getResource(response), model.getProperty(act+"containsSystemAct"),moveName);
		model.createResource(moveName, model.getResource(act+"Empty"));
		
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		model.write(result,"TURTLE");
		return result.toString();
	}
}
