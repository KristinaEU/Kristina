package owlSpeak.kristina;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.OWL2;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.model.IRI;


public class SemanticsOntology {

	private static final HashMap<String, LinkedList<IRI>> userMap = new HashMap<String, LinkedList<IRI>>();
	private static final HashMap<IRI, Model> semantics = new HashMap<IRI, Model>();

	public static void restart(String user) {
		LinkedList<IRI> rmList = userMap.get(user);
		if (rmList != null) {
			for (IRI entry : rmList) {
				Model m = semantics.remove(entry);
				m.close();
			}
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
	
	public static void copy(IRI id, IRI idNew, String user){
		Model onto = semantics.get(id);
		Model model = ModelFactory.createDefaultModel();
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		onto.write(result, "RDF/XML");
		try {
			model.read(new StringReader(result.toString("UTF-8")), null, "RDF/XML");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Resource node = model.getResource(id.toString());
		
		node = ResourceUtils.renameResource(node, idNew.toString());
		
		SemanticsOntology.add(node, model, user);
	}

	public static List<ReifiedStatement> getStatements(IRI id) {
		Model onto = semantics.get(id);

		LinkedList<ReifiedStatement> list = new LinkedList<ReifiedStatement>();
		if (onto != null) {
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
		if (onto == null) {
			return null;
		}
		Statement p = null;
		if(property.contains("#")){
			p = onto.getProperty(onto.getResource(id.toString()),
					onto.getProperty(OntologyPrefix.onto.substring(0, OntologyPrefix.onto.lastIndexOf('/')+1) + property));
		}else{
			p = onto.getProperty(onto.getResource(id.toString()),
				onto.getProperty(OntologyPrefix.onto + property));
		}
		if (p == null) {
			return "";
		} else {
			if(p.getObject().isLiteral()){
				return p.getObject().asLiteral().getValue().toString();
			}else{
				return p.getObject().toString();
			}
		}
	}

	public static boolean hasTopic(IRI id, String topic) {
		Model onto = semantics.get(id);
		if (onto == null) {
			return false;
		}
		ResIterator it = onto.listResourcesWithProperty(RDF.type,
				onto.getResource(OntologyPrefix.ontoLA + topic));

		return it.hasNext();
	}

	public static boolean hasNegatedTopic(IRI id, String topic) {
		Model onto = semantics.get(id);
		if (onto == null) {
			return false;
		}
		ResIterator it = onto.listResourcesWithProperty(RDF.type,
				onto.getResource(topic));
		while (it.hasNext()) {
			Resource r = it.next();
			ResIterator it2 = onto.listResourcesWithProperty(
					onto.getProperty(OntologyPrefix.context + "includes"), r);
			while (it2.hasNext()) {
				Resource r2 = it2.next();
				if (onto.contains(onto.createLiteralStatement(
						r2,
						onto.getProperty(OntologyPrefix.context
								+ "hasTruthValue"), false))) {
					return true;
				}
			}
			it2 = onto.listResourcesWithProperty(
					onto.getProperty(OntologyPrefix.context + "includesEvent"), r);
			while (it2.hasNext()) {
				Resource r2 = it2.next();
				if (onto.contains(onto.createLiteralStatement(
						r2,
						onto.getProperty(OntologyPrefix.context
								+ "hasTruthValue"), false))) {
					return true;
				}
			}
		}
		return false;

	}
	
	public static Set<String> getNegatedTopics(IRI id) {

		Set<String> topics = getTopics(id).stream().filter(t -> hasNegatedTopic(id,t)).collect(Collectors.toSet());
		
		return topics;

	}

	
	public static Set<String> getTopics(IRI id) {
		Model onto = semantics.get(id);

		HashSet<String> list = new HashSet<String>();
		if (onto == null) {
			return list;
		}

		if (onto.contains(onto.getResource(id.toString()),
				onto.getProperty(OntologyPrefix.onto + "responseType"),
				onto.getResource(OntologyPrefix.onto + "structured"))
				|| onto.contains(onto.getResource(id.toString()),
						onto.getProperty(OntologyPrefix.onto + "responseType"),
						onto.getResource(OntologyPrefix.onto + "event"))) {
			
			//This is a structured KI response
			NodeIterator it = onto.listObjectsOfProperty(
					onto.getResource(id.toString()),
					onto.getProperty(OntologyPrefix.onto + "rdf"));
			while (it.hasNext()) {
				Statement s = it.next().as(ReifiedStatement.class)
						.getStatement();
				if(s.getPredicate().equals(RDF.type)){
					RDFNode obj = s.getObject();
					if(obj.asResource().getNameSpace().equals(OntologyPrefix.ontoLA)){
						list.add(obj.asResource().toString());
					}
				}
			}
		}else if(!onto.listResourcesWithProperty(RDF.type, onto.getResource(OntologyPrefix.onto+"ResponseContainer")).hasNext()){
			//This is LA input

			NodeIterator it = onto.listObjectsOfProperty(RDF.type);
			while(it.hasNext()){
				RDFNode obj = it.next();
				if(obj.asResource().getNameSpace().equals(OntologyPrefix.ontoLA)){
					list.add(obj.asResource().toString());
				}
			}
		}
		return list;
	}

	public static void specifyType(IRI id, String type) {
		Model onto = semantics.get(id);
		if (onto != null) {
			Resource root = onto.getResource(id.toString());
			NodeIterator it = onto.listObjectsOfProperty(root,
					onto.getProperty(OntologyPrefix.act + "contains"));
			while (it.hasNext()) {
				Resource res = it.next().asResource();
				List<Statement> it2 = onto.listStatements(res, RDF.type,(RDFNode)null).toList();
				for(Statement s: it2){
					
					if(s.getResource().toString().equals(DialogueAction.STATEMENT) || s.getResource().toString().equals(DialogueAction.REQUEST)|| s.getResource().toString().equals(DialogueAction.BOOL_REQUEST)|| s.getResource().toString().equals(DialogueAction.ACCEPT)){
						onto.remove(s);
						onto.add(s.getSubject(), RDF.type,
								onto.getResource(OntologyPrefix.dialogue + type));
					}
					if(type.equals("ThankVideo") && s.getResource().toString().equals(DialogueAction.THANK) ){
						onto.remove(s);
						onto.add(s.getSubject(), RDF.type,
								onto.getResource(OntologyPrefix.dialogue + type));
					}
					if(type.equals("Accept") && s.getResource().toString().equals(DialogueAction.ACKNOWLEDGE) ){
						onto.remove(s);
						onto.add(s.getSubject(), RDF.type,
								onto.getResource(OntologyPrefix.dialogue + type));
					}
				}
			}
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
	
	public static Set<String> getKITopic(IRI id) {
		Model onto = semantics.get(id);	
		Set<String> result = new HashSet<String>();
		
		if (onto == null) {
			return new HashSet<String>();
		}
		Resource root = onto.getResource(id.toString());
		return onto.listObjectsOfProperty(root, onto.getProperty(OntologyPrefix.onto+"topic")).mapWith(r -> r.asResource().toString()).toSet();
	}
	
	public static String getSpeak(IRI id) {
		Model onto = semantics.get(id);		
		
		Statement s = onto
				.getProperty(
						onto.listResourcesWithProperty(
								RDF.type,
								onto.getResource(OntologyPrefix.dialogue
										+ "UserAction")).next(),
						onto.getProperty(OntologyPrefix.act
								+ "textualContent"));
		if (s == null) {
			return "";
		} else {
			return s.getString();
		}
	}

	public static List<String> getType(IRI id) {
		Model onto = semantics.get(id);
		LinkedList<String> result = new LinkedList<String>();
		if (onto == null) {
			return result;
		}
		
		Resource root = onto.getResource(id.toString());
		List<RDFNode> it = onto.listObjectsOfProperty(root,
				onto.getProperty(OntologyPrefix.act + "contains")).toList();
		List<Resource> followed = onto.listSubjectsWithProperty(onto.getProperty(OntologyPrefix.dialogue + "followedBy")).toList();
		RDFNode last = it.stream().filter(node -> !followed.contains(node.asResource())).collect(Collectors.toList()).get(0);
		while(!it.isEmpty()){
			Resource res = last.asResource();
			
			NodeIterator it2 = onto.listObjectsOfProperty(
					res, RDF.type);
			while (it2.hasNext()) {
				String n = it2.next().asResource().getURI();
				if (n.startsWith(OntologyPrefix.dialogue)) {
					result.addFirst(n);
				}
			}
			
			it.remove(it.indexOf(last));
			ResIterator it3 = onto.listResourcesWithProperty(onto.getProperty(OntologyPrefix.dialogue + "followedBy"), last);
			if(it3.hasNext()){
				last = it3.next();
			}else{
				break;
			}
		}
		
		
		return result;
	}
	
	public static String getKIType(IRI id){
		Model onto = semantics.get(id);
		String result = "";
		if (onto == null) {
			return result;
		}
		Resource root = onto.getResource(id.toString());
		return onto.listObjectsOfProperty(root, RDF.type).next().asResource().toString();
		
	}
	
	public static Set<String> getAdditionalInformation(IRI id){
		Model onto = semantics.get(id);
		if (onto == null) {
			return new HashSet<String>();
		}
		Set<Resource> it = onto.listSubjectsWithProperty(RDF.type, onto.getResource(OntologyPrefix.onto+"AdditionalResponse")).filterDrop(r -> r.hasLiteral(onto.getProperty(OntologyPrefix.onto+"isMandatory"),true)).toSet();
		it.addAll(onto.listSubjectsWithProperty(RDF.type, onto.getResource(OntologyPrefix.onto+"ProactiveResponse")).toSet());
//		it.addAll(onto.listSubjectsWithProperty(RDF.type, onto.getResource(OntologyPrefix.onto+"SpecifyingInformationResponse")).toSet());
		
		return it.parallelStream().map(res -> res.getURI()).collect(Collectors.toSet());
	}
	
	public static Set<String> getMandatoryInformation(IRI id){
		Model onto = semantics.get(id);
		if (onto == null) {
			return new HashSet<String>();
		}
		Set<Resource> it = onto.listSubjectsWithProperty(RDF.type, onto.getResource(OntologyPrefix.onto+"AdditionalResponse")).filterDrop(r -> r.hasLiteral(onto.getProperty(OntologyPrefix.onto+"isMandatory"),false)).toSet();
		
		return it.parallelStream().map(res -> res.getURI()).collect(Collectors.toSet());
	}

}
