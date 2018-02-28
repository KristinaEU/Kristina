package model;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import model.DialogueHistory.Participant;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.WorkSpace;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.util.KIConverter;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class KristinaModel {
	
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	private static Model inputKI;
	
	public WorkSpace performUpdate(IRI userMoveID,  String user, ServletEngine owlEngine) throws OWLOntologyCreationException{
		
		removeKIMovesFromWorkspace(owlEngine, user);
		
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
		
		//create OWL representation of user move and add it to dialogue history
		OWLNamedIndividualImpl indi= new OWLNamedIndividualImpl(userMoveID);
		KristinaMove userMove = new KristinaMove(indi, ontoOfLastAgenda.factory.onto, ontoOfLastAgenda.factory.factory, ontoOfLastAgenda.factory.manager);
		DialogueHistory.add(userMove, Participant.USER);
		
		//send user move to CERTH service, receive possible system responses
		ByteArrayOutputStream input = new ByteArrayOutputStream();
		userMove.getModel().write(input,"TURTLE");
		System.out.println("LA input afterwards:");
		System.out.println(input.toString());
		String rdfMoves = CerthClient.post(input.toString());
		System.out.println("KI input: \n"+rdfMoves);
		
		Set<Move> systemMoves = KIConverter.convertToMove(rdfMoves, ontoOfLastAgenda.factory.onto, ontoOfLastAgenda.factory.factory, ontoOfLastAgenda.factory.manager);
		
		WorkSpace workspace = addToWorkspace(systemMoves, user,owlEngine);
		inputKI = extractSemantics(rdfMoves);

		return workspace;
	}
	
	private static Model extractSemantics(String rdf){
		Model model = ModelFactory.createDefaultModel() ;
        model.read(new StringReader(rdf),null, "TURTLE") ;
        
        return model;
	}
	
	//Demo functions
	
	public WorkSpace performDemoUpdate(Move userMove, String user, ServletEngine owlEngine) throws OWLOntologyCreationException{
		removeKIMovesFromWorkspace(owlEngine, user);
		

		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
		
		String rdfMoves = askDemoKI(userMove);
		Set<Move> systemMoves = KIConverter.convertToMove(rdfMoves, ontoOfLastAgenda.factory.onto, ontoOfLastAgenda.factory.factory, ontoOfLastAgenda.factory.manager);
		inputKI = extractSemantics(rdfMoves);
		return addToWorkspace(systemMoves, user,owlEngine);
		
	}
	
	private WorkSpace addToWorkspace(Set<Move> moves, String user, ServletEngine owlEngine){
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
		WorkSpace workspace = ontoOfLastAgenda.getWorks(Settings
				.getuserpos(user));
		
		for(Move move: moves){
			OWLIndividual newIndi = ontoOfLastAgenda.factory.factory.getOWLNamedIndividual(IRI.create(ontoOfLastAgenda.factory.onto.getOntologyID().getOntologyIRI() + "#" + UUID.randomUUID()));
			Agenda ag = new Agenda(newIndi, ontoOfLastAgenda.factory.onto, ontoOfLastAgenda.factory.factory,ontoOfLastAgenda.factory.manager);
			ag.addHas(move);

			workspace.addNext(ag);
		}
		
		return workspace;
	}
	
	private String askDemoKI(Move userMove){
		BufferedReader r;
		
		try {
			r = new BufferedReader(new InputStreamReader(
					getClass().getResourceAsStream("/Example_Input_Output/ki-beforeSleep_Response.ttl")));
			String data = "";
			String tmp = r.readLine();
			while(tmp != null){
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();
			return data;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	private void removeKIMovesFromWorkspace(ServletEngine owlEngine, String user){
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
		WorkSpace workspace = ontoOfLastAgenda.getWorks(Settings
				.getuserpos(user));
		Collection<Agenda> agendas = workspace.getNext();
		
		for(Agenda agenda: agendas){
			Collection<Move> moves = agenda.getHas();
			for(Move move: moves){
				OWLAxiom decl = ontoOfLastAgenda.factory.factory.getOWLDeclarationAxiom((OWLNamedIndividual)move.indi);
				if(!ontoOfLastAgenda.factory.onto.containsAxiom(decl)){
					workspace.removeNext(agenda);
					break;
				}
			}
		}
	}
	
	public static List<ReifiedStatement> getStatements(IRI response){
		StmtIterator it = inputKI.getResource(response.toString()).listProperties(inputKI.getProperty("http://kristina-project.eu/ontologies/responses#rdf"));
		LinkedList<ReifiedStatement> result = new LinkedList<ReifiedStatement>();
		Iterator<ReifiedStatement> it2 = inputKI.listReifiedStatements();
		List<String> stmts = new LinkedList<String>();
		
		while(it.hasNext()){
			Statement st = it.next();
			stmts.add(st.getObject().asResource().getURI());
		}
		while(it2.hasNext()){
			ReifiedStatement stmt = it2.next();
			if(stmts.contains(stmt.getURI())){
					result.add(stmt);
			}
		}
		
		return result;
	}
	
	public static String getText(IRI response){
		return inputKI.getProperty(inputKI.getResource(response.toString()), inputKI.getProperty("http://kristina-project.eu/ontologies/responses#text")).getObject().toString();
		
	}
}
