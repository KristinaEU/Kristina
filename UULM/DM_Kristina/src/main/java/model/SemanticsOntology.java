package model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.semanticweb.owlapi.model.IRI;

import com.sun.corba.se.impl.orbutil.graph.Node;

import presenter.LAConverter;

public class SemanticsOntology {

	private static final HashMap<String, LinkedList<IRI>> userMap = new HashMap<String, LinkedList<IRI>>();
	private static final HashMap<IRI, Model> semantics = new HashMap<IRI, Model>();

	public static void restart(String user) {
		LinkedList<IRI> rmList = userMap.get(user);
		for (IRI entry : rmList) {
			semantics.remove(entry);
		}
		userMap.remove(user);
	}

	public static void add(Resource r, Model m, String user) {
		IRI id = IRI.create(r.getURI());
		if (userMap.containsKey(user)) {
			userMap.get(user).add(id);
		} else {
			LinkedList<IRI> tmp = new LinkedList<IRI>();
			tmp.add(id);
			userMap.put(user, tmp);
		}
		semantics.put(id, m);
	}

	public static Model getModel(IRI id) {
		return semantics.get(id);
	}

	public static List<ReifiedStatement> getStatements(IRI id) {
		Model onto = semantics.get(id);
		
		LinkedList<ReifiedStatement> list = new LinkedList<ReifiedStatement>();
		if(onto != null){
		NodeIterator it = onto.listObjectsOfProperty(
				onto.getResource(id.toString()),
				onto.getProperty(OntologyPrefix.onto + "rdf"));
		while (it.hasNext()) {
			ReifiedStatement s = it.next().as(ReifiedStatement.class);
			list.add(s);
		}
		}
		return list;
	}

	public static String getProperty(IRI id, String property) {
		Model onto = semantics.get(id);
		if(onto==null){
			return null;
		}
		Statement p = onto.getProperty(onto.getResource(id.toString()),
				onto.getProperty(OntologyPrefix.onto + property));
		if (p == null) {
			return "";
		} else {
			return p.getObject().toString();
		}
	}

	public static boolean hasTopic(IRI id, String topic) {
		Model onto = semantics.get(id);
		if(onto==null){
			return false;
		}
		ResIterator it = onto.listResourcesWithProperty(RDF.type,
				onto.getResource(OntologyPrefix.ontoLA + topic));

		return it.hasNext();
	}

	public static Set<String> getTopics(IRI id) {
		Model onto = semantics.get(id);
		if(onto==null){
			return null;
		}
		NodeIterator it = onto.listObjectsOfProperty(
				onto.getResource(id.toString()),
				onto.getProperty(OntologyPrefix.onto + "rdf"));
		HashSet<String> list = new HashSet<String>();
		while (it.hasNext()) {
			Statement s = it.next().as(ReifiedStatement.class).getStatement();
			NodeIterator it2 = onto.listObjectsOfProperty(s.getSubject(),
					RDF.type);
			while (it2.hasNext()) {
				list.add(it2.next().toString());
			}
			RDFNode obj = s.getObject();
			if (obj.isResource()) {
				it2 = onto.listObjectsOfProperty(obj.asResource(), RDF.type);
				while (it2.hasNext()) {
					list.add(it2.next().toString());
				}
			}
		}
		return list;
	}

	public static void specifyType(IRI id, String type) {
		Model onto = semantics.get(id);
		if(onto!=null){
			onto.add(onto.getResource(id.toString()), RDF.type,
					onto.getResource(OntologyPrefix.dialogue + type));
		}
	}

	public static float getConfidence(IRI id) {
		Model onto = semantics.get(id);
		
		Statement s = onto
				.getProperty(
						onto.listResourcesWithProperty(
								RDF.type,
								onto.getResource(OntologyPrefix.dialogue
										+ "UserAction")).next(),
						onto.getProperty(OntologyPrefix.act
								+ "sentenceASRconfidence"));
		if (s == null) {
			return 1f;
		} else {
			return s.getFloat();
		}
	}

	public static String getType(IRI id) {
		Model onto = semantics.get(id);
		if(onto==null){
			return null;
		}
		NodeIterator it = onto.listObjectsOfProperty(
				onto.getResource(id.toString()), RDF.type);
		while (it.hasNext()) {
			String n = it.next().asResource().getURI();
			if (n.startsWith(OntologyPrefix.dialogue)) {
				return n;
			}
		}
		assert false : "This is not a dialogue actions.";
		return null;
	}

}
