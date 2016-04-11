package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.WorkSpace;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.util.InputOutputConverter;
import presenter.KristinaPresenter;

public class KristinaModel {
	
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	public static OWLOntology performUpdate(String usermove) throws OWLOntologyCreationException{
		
		String workspaceRDF = CerthClient.post(usermove);
		
		OWLOntology workspace = parseRDF(workspaceRDF);
		
		return workspace;
	}
	
	private static OWLOntology parseRDF(String rdf) throws OWLOntologyCreationException{
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		//TODO: this is temporary until KI ontology is in place
		manager.setSilentMissingImportsHandling(true);
		OWLOntology onto = manager.loadOntologyFromOntologyDocument(new StringDocumentSource(rdf));
		/*ArrayList<OWLAxiom> axioms = new ArrayList<OWLAxiom>(onto.getAxioms());
		String[] proposal = new String[axioms.size()];
		for (int i = 0; i < axioms.size(); i++){
			proposal[i] = axioms.get(i).toString();
			System.out.println(axioms.get(i).toString());
		}
		return proposal;*/
        return onto;
	}
	
	//Demo functions
	
	public WorkSpace performDemoUpdate(Move userMove, String user, InputOutputConverter ioConv, ServletEngine owlEngine) throws OWLOntologyCreationException{
		removeKIMovesFromWorkspace(owlEngine, user);
		
		String rdfMoves = askDemoKI(userMove);
		Set<Move> systemMoves = ioConv.buildKristinaMove(rdfMoves);
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
					getClass().getResourceAsStream("/Example_Input_Output/KI_feedback.ttl")));
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
}
