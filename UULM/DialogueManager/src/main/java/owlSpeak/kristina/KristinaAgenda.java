package owlSpeak.kristina;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.util.ResourceUtils;
import org.apache.jena.vocabulary.RDF;
import org.semanticweb.owlapi.model.HasDirectImports;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Agenda;
import owlSpeak.GenericClass;
import owlSpeak.GenericProvider;
import owlSpeak.OSFactory;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class KristinaAgenda extends Agenda{
	public static final String name = "KristinaAgenda";
	
	private static final String textProperty = "text";	
	private static final String weatherProperty = "weather#weatherClassification";
	private static final String urlProperty = "url";
	private static final String sourceProperty = "source";
	private static final String plausibilityProperty = "plausibility";
	
	public KristinaAgenda(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}
	
	public boolean getIsNonPersistent(){
    	return GenericProvider.getBooleanProperty(indi, GenericProvider.nonPersistentProp, onto, factory);
    }
	
	public void setIsNonPersistent(boolean val){
    	GenericProvider.addBooleanData(indi, val, GenericProvider.nonPersistentProp, onto, factory, manager);
    }
	
	public int getAge(){
		int val = GenericProvider.getIntProperty(indi, GenericProvider.ageProp, onto, factory);
		return val == 420 ? 0 : val;
	}
	
	public void setAge(int age){
		GenericProvider.removeIntData(indi, getAge(), GenericProvider.ageProp, onto, factory, manager);
		GenericProvider.addIntData(indi, age, GenericProvider.ageProp, onto, factory, manager);
	}
	
	public void incrementAge(){
		setAge(getAge() + 1);
	}
	
	public List<ReifiedStatement> getStatements() {
		return SemanticsOntology.getStatements(indi.asOWLNamedIndividual()
				.getIRI());
	}

	public String getText() {
		String text=null;
		if(isDialogueAction(DialogueAction.CANNED)){
			text = GenericProvider.getStringData(indi, GenericProvider.cannedTextProp, onto, factory);
		}if(text==null || text.isEmpty()){
			text = SemanticsOntology.getProperty(indi.asOWLNamedIndividual()
				.getIRI(), textProperty);
			if(text == null || text.isEmpty()){
				text = GenericProvider.getStringData(indi, GenericProvider.cannedTextProp, onto, factory);
			}
		}
		return text == null ? "" : text;
	}
	
	public String getSource() {
		String text = SemanticsOntology.getProperty(indi.asOWLNamedIndividual()
				.getIRI(), sourceProperty);
		return text == null ? "" : text;
	}
	
	public String getURL() {
		String text = SemanticsOntology.getProperty(indi.asOWLNamedIndividual()
				.getIRI(), urlProperty);
		return text == null ? "" : text;
	}
	
	public String getWeather() {
		String text = SemanticsOntology.getProperty(indi.asOWLNamedIndividual()
				.getIRI(), weatherProperty);
		return text == null ? "" : text;
	}
	
	public float getPlausibility(){
		 String text = SemanticsOntology.getProperty(indi.asOWLNamedIndividual()
				.getIRI(), plausibilityProperty);
		return text == null || text.isEmpty() ? 0 : Float.parseFloat(text);
	}

	public boolean hasTopic(String topic) {
			return SemanticsOntology.hasTopic(indi.asOWLNamedIndividual()
					.getIRI(), topic);
	}

	public String getDialogueAction() {
		GenericClass tmp = GenericProvider.getIndividual(indi, GenericProvider.dialogueActionProp, onto, factory, manager);
		return tmp!=null?tmp.getFullName():DialogueAction.UNKNOWN; //TODO: Should it really be UNKNOWN?
	}

	public boolean isDialogueAction(String a) {
		return this.getDialogueAction().equals(a);
	}

	public Model getModel() {
		return SemanticsOntology.getModel(this.indi.asOWLNamedIndividual()
				.getIRI());
	}
	
	public Set<String> getKITopic(){
		return SemanticsOntology.getKITopic(indi.asOWLNamedIndividual().getIRI());
	}

	public Set<String> getTopics() {
		//TODO find out why this is necessary
//		if(!isDialogueAction(DialogueAction.REQUEST_SPECIFYING_INFORMATION)){
			return SemanticsOntology.getTopics(indi.asOWLNamedIndividual().getIRI());
//		}
//		return new HashSet<String>();
	}

	@Override
	public String toString() {
		String action = getDialogueAction();
		String result = action.substring(action.indexOf('#')+1);
		if (getTopics() != null && !getTopics().isEmpty()) {
			Iterator<String> topics = getTopics().iterator();
			if(topics != null && topics.hasNext()){
				String tmp = topics.next();
				tmp = tmp.substring(tmp.indexOf('#')+1);
				result = result+": "+tmp;
				while(topics.hasNext()){
					tmp = topics.next();
					tmp = tmp.substring(tmp.indexOf('#')+1);
					result = result+"/"+tmp;
				}
			}
		}else if(getWeather() != null && !getWeather().isEmpty()){
			result = result+": "+getWeather();
		}else if(getText() != null && !getText().isEmpty()){
			result = result+": "+getText();
		}
		if(getURL() != null && !getURL().isEmpty()){
			result = result+" "+getURL();
		}
		return result;
	}
	
	public List<KristinaAgenda> getAdditionalInformation(){
		return SemanticsOntology.getAdditionalInformation(indi.asOWLNamedIndividual().getIRI()).parallelStream().map(res -> new KristinaAgenda(factory.getOWLNamedIndividual(IRI.create(res)), onto, factory, manager)).collect(Collectors.toList());
	}
	
	public List<KristinaAgenda> getMandatoryInformation(){
		return SemanticsOntology.getMandatoryInformation(indi.asOWLNamedIndividual().getIRI()).parallelStream().map(res -> new KristinaAgenda(factory.getOWLNamedIndividual(IRI.create(res)), onto, factory, manager)).collect(Collectors.toList());
	}
	
	public void addDialogueAction(IRI iri){
		GenericProvider.addIndividual(indi, new OWLNamedIndividualImpl(iri), GenericProvider.dialogueActionProp, onto, factory, manager);
	}
	
	public void addCannedText(String text){
		GenericProvider.addStringData(indi, text, GenericProvider.cannedTextProp, onto, factory, manager);
	}
	
	public void copy(KristinaAgenda a, String user){
		SemanticsOntology.copy(a.indi.asOWLNamedIndividual().getIRI(), indi.asOWLNamedIndividual().getIRI(), user);
		
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof KristinaAgenda){
			KristinaAgenda m = (KristinaAgenda) o;
			if(m.getDialogueAction().equals(getDialogueAction())){
				if(m.getText().equals(getText())){
					if(m.getWeather().equals(getWeather())){
					if(m.getURL().equals(getURL())){
					List<ReifiedStatement> stmts1 = this.getStatements();
					List<ReifiedStatement> stmts2 = m.getStatements();
					boolean equal = true;
					for(ReifiedStatement stmt1: stmts1){
						Statement s1 = stmt1.getStatement();
						boolean foundMatch = false;
						for(ReifiedStatement stmt2: stmts2){
							Statement s2 = stmt2.getStatement();
							if(s1.getSubject().equals(s2.getSubject()) && s1.getObject().equals(s2.getObject()) && s1.getPredicate().equals(s2.getPredicate())){
								foundMatch = true;
								break;
							}
						}
						if(!foundMatch){
							equal =false;
							break;
						}
					}
					if(equal){
						return true;
					}
					}
					}
				}
			}
		}
		return false;
	}
	
	
	@Override
    public int hashCode() {
		List<Statement> stmts = this.getStatements().stream().map(rs -> rs.getStatement()).collect(Collectors.toList());
        return Objects.hash(getDialogueAction(), getText(), getWeather(),getURL(),stmts.stream().map(stmt -> stmt.getSubject()).collect(Collectors.toSet()), stmts.stream().map(stmt -> stmt.getObject()).collect(Collectors.toSet()),stmts.stream().map(stmt -> stmt.getPredicate()).collect(Collectors.toSet()));
    }
	
}