package model;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RSIterator;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.python.apache.commons.compress.utils.IOUtils;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import com.github.andrewoma.dexx.collection.HashMap;

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
	
	private static Model inputKI;
	
	public static OWLOntology performUpdate(String usermove) throws OWLOntologyCreationException{
		
		String workspaceRDF = new KristinaModel().askDemoKI(null);
		
		OWLOntology workspace = parseRDF(workspaceRDF);
		inputKI = extractSemantics(workspaceRDF);
		
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
	
	private static Model extractSemantics(String rdf){
		Model model = ModelFactory.createDefaultModel() ;
        model.read(new StringReader(rdf),null, "TURTLE") ;
        
        return model;
	}
	
	//Demo functions
	
	public WorkSpace performDemoUpdate(Move userMove, String user, InputOutputConverter ioConv, ServletEngine owlEngine) throws OWLOntologyCreationException{
		removeKIMovesFromWorkspace(owlEngine, user);
		
		String rdfMoves = askDemoKI(userMove);
		Set<Move> systemMoves = ioConv.buildKristinaKIMove(rdfMoves);
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
					getClass().getResourceAsStream("/Example_Input_Output/test.ttl")));
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
		StmtIterator it = inputKI.getResource(response.toString()).listProperties(inputKI.getProperty("http://kristina-project.eu/ontologies/responses/v1#rdf"));
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
}
