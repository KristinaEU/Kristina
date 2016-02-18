package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class KristinaModel {
	
	private static final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
	
	public static OWLOntology performUpdate(String usermove) throws OWLOntologyCreationException{
		
		String workspaceRDF = CerthClient.post(usermove);
        System.out.println(workspaceRDF);
		
		OWLOntology workspace = parseRDF(workspaceRDF);
		
		return workspace;
	}
	
	private static OWLOntology parseRDF(String rdf) throws OWLOntologyCreationException{
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
}
