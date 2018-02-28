package model;

import java.util.HashMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.semanticweb.owlapi.model.IRI;

public class SemanticsOntology {

	private static final HashMap<IRI,Model> semantics = new HashMap<IRI, Model> ();
	
	public void reset(String user){
		
	}
	
	public static void add(IRI id, Model m){
		semantics.put(id, m);
	}
	
	public static Model getModel(IRI id){
		return semantics.get(id);
	}
}
