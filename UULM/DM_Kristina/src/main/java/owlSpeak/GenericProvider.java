package owlSpeak;

import java.util.Collection;
import java.util.Iterator;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.RemoveAxiom;

import owlSpeak.engine.Settings;
/**
 * This class encapsulates the static funtions needed for manipulating the ontologies.
 * @author Tobias Heinroth
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @author Karim 
 */
public class GenericProvider {
	/**
	 * the xsd type	string
	 */
	public static final IRI xsdString = IRI.create("http://www.w3.org/2001/XMLSchema#string");
	/**
	 * the xsd type	int
	 */
	public static final IRI xsdInt = IRI.create("http://www.w3.org/2001/XMLSchema#int");
	/**
	 * the xsd type	float
	 */
	public static final IRI xsdFloat = IRI.create("http://www.w3.org/2001/XMLSchema#float");
	/**
	 * the xsd type	boolean
	 */
	public static final IRI xsdBoolean = IRI.create("http://www.w3.org/2001/XMLSchema#boolean");
	/**
	 * the object property has 
	 */
	public static final String hasProp = "#has";
	/**
	 * the object property next 
	 */
	public static final String nextProp = "#next";
	/**
	 * the data property isMasterBool 
	 */
	public static final String masterProp = "#isMasterBool";
	/**
	 * the data property confirmationInfo 
	 */
	public static final String confirmationInfo = "#confirmationInfo";
	
	/**
	 * the data property priority 
	 */
	public static final String prioProp = "#priority";
	/**
	 * the object property requires 
	 */
	public static final String reqProp = "#requires";
	/**
	 * the data property respawn 
	 */
	public static final String respawnProp = "#respawn";
	/**
	 * the data property variableOperator 
	 */
	public static final String varProp ="#variableOperator";
	/**
	 * the object property mustNot 
	 */
	public static final String mustNotProp ="#mustnot";
	/**
	 * the object property semantic 
	 */
	public static final String semProp ="#semantic";
	/**
	 * the object property semantic group
	 */
	public static final String semGroupProp ="#semanticGroup";
	/**
	 * the object property semantic group
	 */
	public static final String containsSemProp ="#containsSemantics";
	/**
	 * the object property subSemantic
	 */
	public static final String subSemProp = "#subSemantic";
	/**
	 * the object property masterSemantic
	 */
	public static final String masterSemProp = "#masterSemantic";
	/**
	 * the object property contrarySemantic 
	 */
	public static final String contProp ="#contrarySemantic";
	/**
	 * the object property grammar 
	 */
	public static final String gramProp ="#grammar";
	/**
	 * the object property general grammar 
	 */
	public static final String generalGramProp ="#generalGrammar";
	/**
	 * the object property utterance 
	 */
	public static final String uttProp ="#utterance";
	/**
	 * the object property isExitMove 
	 */
	public static final String isExitMoveProp = "#isExitMove";
	/**
	 * the object property isRequestMove 
	 */
	public static final String isRequestMoveProp = "#isRequestMove";
	/**
	 * the object property processConditions 
	 */
	public static final String processConditions = "#processConditions";
	
	public static final String useBuildInGrammarProp = "#useBuildInGrammar";
	//new_shrief
	/**
	 * the object property hasVariable 
	 */
	public static final String hasVariableProp ="#hasVariable";
	
	/**
	 * the object property belongsToSemantic
	 */
	public static final String belongsToSemantic ="#belongsToSemantic";
	
	//new
	/**
	 * the object property includesSemantic 
	 */
	public static final String includesSemanticProp ="#includesSemantic";
	
	/**
	 * the data property variableValue 
	 */
	public static final String varValProp ="#variableValue";
	/**
	 * the object property variabledefault 
	 */
	public static final String varDefProp ="#variabledefault";
	/**
	 * the object property hasBelief 
	 */
	public static final String hasBelProp = "#hasBelief";
	/**
	 * the object property hasBelief 
	 */
	public static final String excludesBelProp = "#excludesBelief";
	/**
	 * the data property addTime 
	 */
	public static final String addProp = "#addTime";
	/**
	 * the data property agendaPriority 
	 */
	public static final String agPrioProp = "#agendaPriority";
	/**
	 * the object property forAgenda 
	 */
	public static final String forAgProp = "#forAgenda";
	/**
	 * the data property summary agenda for agendas
	 */
	public static final String summaryAgendaProp = "#summaryAgenda";
	/**
	 * the data property summary agenda for agendas
	 */
	public static final String summaryAgendaTypeProp = "#summaryAgendaType";
	/**
	 * the data property summarizesAgendas for agendas
	 */
	public static final String summarizesAgendasProp = "#summarizesAgendas";
	/**
	 * the object property inWorkspace 
	 */
	public static final String inWoProp = "#inWorkspace";
	/**
	 * the data property procTime 
	 */
	public static final String procProp = "#procTime";
	/**
	 * the data property semanticString 
	 */
	public static final String semStringProp = "#semanticString";
	/**
	 * the data property utteranceString 
	 */
	public static final String uttStringProp = "#utteranceString";
	/**
	 * the data property speakerGender (male|female) 
	 */
	public static final String speakerGenderProp = "#speakerGender";
	/**
	 * the data property utteranceVolume (String) 
	 */
	public static final String uttVolumeProp = "#utteranceVolume";
	/**
	 * the data property grammarString 
	 */
	public static final String gramStringProp = "#grammarString";
	/**
	 * the data property keywordString for moves
	 */
	public static final String keyStringProp = "#keywords";
	
	/**
	 * the data property parent for belief spaces
	 */
	public static final String parentProp = "#parent";
	/**
	 * the data property children for belief spaces
	 */
	public static final String childrenProp = "#children";
	/**
	 * the data property belief value of belief spaces
	 */
	public static final String beliefProp = "#beliefValue";
	/**
	 * the data property prior of belief spaces
	 */
	public static final String priorProp = "#prior";

	/**
	 * the data property role of agenda
	 */
	public static final String roleProp = "#role";
	/**
	 * the data property defaultValue 
	 */
	public static final String varDefValProp = "#defaultValue";
	
	public static final String summaryBeliefAgendaProp = "#summaryBeliefAgenda";
	public static final String lastGrammarMoveProp = "#lastGrammarMove";
	public static final String topBeliefValueProp = "#topBeliefValue";
	public static final String secondTopBeliefValueProp = "#secondTopBeliefValue";
	public static final String historyStateProp = "#historyState";
	public static final String partitionStateProp = "#partitionState";
	public static final String partitionNumFieldsProp = "#partitionNumFields";
	public static final String interactionQualityProp = "#lastInteractionQuality";
	
	/**
	 * constants dealing with reward
	 */
	public static final String rewardingAgendasProp = "#rewardingAgendas";
	public static final String rewardValueProp = "#rewardValue";
	public static final String specialRewardProp = "#specialReward";
	public static final String gramFileProp = "#grammarFile";
	public static final String extractFieldValuesProp = "#extractFieldValuesFromInput";
	public static final String fieldTotalsProp = "#fieldTotals";
	public static final String rewardProp = "#reward";
	
	/**
	 * KRISTINA specific data properties
	 */
	public static final String directnessProp = "#directness";
	public static final String verbosityProp = "#verbosity";
	public static final String isFormalProp = "#isFormal";
	public static final String isAdviceProp = "#isAdvice";
	public static final String isBeliefProp = "#isBelief";
	
	/**
	 * returns a collection of GenericClass objects that are linked from a specific individual via a specific property.
	 * @param owlIndi the individual the GenericClass objects are linked with.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that is used.
	 * @param manager the manager that manages the ontology.
	 * @return the collection of GenericClass.
	 */
	public static Collection<GenericClass> getIndividualColl(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(Settings.uri+property));
		Iterator<OWLIndividual> coll = owlIndi.getObjectPropertyValues(p, onto).iterator();
		Collection<GenericClass> indiColl = new java.util.LinkedList<GenericClass>();
		while(coll.hasNext()){
			indiColl.add(new GenericClass(coll.next(),onto,factory, manager));
		}
		return indiColl;
	}
	/**
	 * returns a GenericClass object that is linked from a specific individual via a specific property.
	 * @param owlIndi the individual the GenericClass object is linked with.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that is used.
	 * @param manager the manager that manages the ontology.
	 * @return the GenericClass object.
	 */
	public static GenericClass getIndividual(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(Settings.uri+property));
		Iterator<OWLIndividual> coll = owlIndi.getObjectPropertyValues(p, onto).iterator();
		if (coll.hasNext())
			return new GenericClass(coll.next(),onto,factory, manager);
		return null;
	}
    /**
     * returns true if the specific object property field contains at least one item.
	 * @param owlIndi the individual the object(s) is/are linked with.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that is used.
     * @return true if the object property field is not empty.
     */
	public static boolean hasObjectProperty(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(Settings.uri+property));
		boolean hasNext = owlIndi.getObjectPropertyValues(p, onto).iterator().hasNext();
		return hasNext;
	}
    /**
     * returns true if the specific data property field contains at least one item.
	 * @param owlIndi the individual the object(s) is/are linked with.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that is used.
     * @return true if the data property field is not empty.
     */
	public static boolean hasDataProperty(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		return (owlIndi.getDataPropertyValues(p, onto).size() > 0);
	}
    /**
     * adds an individual to the specific property field of the current individual.
	 * @param owlIndi the current individual.
	 * @param indiToAdd the new individual that should be added to the owlindi's property field.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param manager the manager that manages the ontology.
	 * @param factory the factory that is used.
     */
	public static void addIndividual(OWLIndividual owlIndi, OWLIndividual indiToAdd, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(Settings.uri+property));
	    manager.applyChange(new AddAxiom(onto, factory.getOWLObjectPropertyAssertionAxiom(p, owlIndi, indiToAdd)));
	}
    /**
     * removes an individual from the specific property field of the current individual.
	 * @param owlIndi the current individual.
	 * @param indiToRemove the old individual that should be removed from the owlindi's property field.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param manager the manager that manages the ontology.
	 * @param factory the factory that is used.
     */
	public static void removeIndividual(OWLIndividual owlIndi, OWLIndividual indiToRemove, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLObjectProperty p = factory.getOWLObjectProperty(IRI.create(Settings.uri+property));
	    manager.applyChange(new RemoveAxiom(onto, factory.getOWLObjectPropertyAssertionAxiom(p, owlIndi, indiToRemove)));
	}
    /**
     * returns the boolean value in the specific property field.
	 * @param owlIndi the current individual.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
     * @return true if the property field is defined as true. In any other cases it returns false.
     */
	public static boolean getBooleanProperty(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		boolean b = false;
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		try {
			OWLLiteral o = owlIndi.getDataPropertyValues(p, onto).iterator().next();
			b = Boolean.parseBoolean(o.getLiteral());
		} catch (Exception e) {}
		return b;
	}
    /**
     * adds a new boolean data to the specified boolean property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the new boolean value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void addBooleanData(OWLIndividual owlIndi, boolean value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(Boolean.toString(value), factory.getOWLDatatype(xsdBoolean));
	    manager.applyChange(new AddAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * removes an old boolean data from the specified boolean property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the old boolean value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void removeBooleanData(OWLIndividual owlIndi, boolean value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(Boolean.toString(value), factory.getOWLDatatype(xsdBoolean));
	    manager.applyChange(new RemoveAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * returns the int value in the specific property field.
	 * @param owlIndi the current individual.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
     * @return the int value of the property or 42 in all other cases (e.g., undefined).
     */
	public static int getIntProperty(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		int i = -5;
		OWLLiteral o = null;
		try{
			OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
			o = owlIndi.getDataPropertyValues(p, onto).iterator().next();
			i = Integer.parseInt(o.getLiteral());
		}catch(Exception e){}	
		if (i!=-5){
			return i;
		}else{
			return 42;
		}
	}
    /**
     * adds a new int data to the specified int property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the new int value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void addIntData(OWLIndividual owlIndi, int value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(Integer.toString(value), factory.getOWLDatatype(xsdInt));
	    manager.applyChange(new AddAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * removes an old int data from the specified int property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the old int value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void removeIntData(OWLIndividual owlIndi, int value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(Integer.toString(value), factory.getOWLDatatype(xsdInt));
	    manager.applyChange(new RemoveAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * returns the float value in the specific property field.
	 * @param owlIndi the current individual.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
     * @return the float value of the property or 0.0 in all other cases (e.g., undefined).
     */
	public static float getFloatProperty(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		float f = -5;
		OWLLiteral o = null;
		try{
			OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
			o = owlIndi.getDataPropertyValues(p, onto).iterator().next();
			f = Float.parseFloat(o.getLiteral());
		}catch(Exception e){}
		if (f!=-5){
			return f;
		}else{
			return 0.0f;
		}
	}
	/**
     * adds a new float data to the specified float property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the new float value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void addFloatData(OWLIndividual owlIndi, float value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(Float.toString(value), factory.getOWLDatatype(xsdFloat));
	    manager.applyChange(new AddAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
	/**
     * removes an old float data from the specified float property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the old float value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void removeFloatData(OWLIndividual owlIndi, float value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(Float.toString(value), factory.getOWLDatatype(xsdFloat));
	    manager.applyChange(new RemoveAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
	/**
     * adds a new string data to the specified string property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the new int value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void addStringData(OWLIndividual owlIndi, String value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(value, factory.getOWLDatatype(xsdString));
	    manager.applyChange(new AddAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * removes an old string data from the specified string property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the old string value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void removeStringData(OWLIndividual owlIndi, String value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(value, factory.getOWLDatatype(xsdString));
	    manager.applyChange(new RemoveAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * returns the string value in the specific property field.
	 * @param owlIndi the current individual.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
     * @return the string value of the property.
     */
	public static String getStringData(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		return owlIndi.getDataPropertyValues(p, onto).iterator().next().getLiteral();
	}
    /**
     * returns a collection of the OWLLiteral values in the specific property field.
	 * @param owlIndi the current individual.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
     * @return the collection of OWLLiterals.
     */
	public static Collection<OWLLiteral> getOWLLiteralProperties(OWLIndividual owlIndi, String property, OWLOntology onto, OWLDataFactory factory){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		Collection<OWLLiteral> o = owlIndi.getDataPropertyValues(p, onto);
		return o;
	}
    /**
     * adds a new OWLLiteral data to the specified OWLLiteral property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the new OWLLiteral value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void addOWLLiteralProperty(OWLIndividual owlIndi, OWLLiteral value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(value.getLiteral(), value.getLang());
	    manager.applyChange(new AddAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
    /**
     * removes an old OWLLiteral data from the specified OWLLiteral property of the individual.
	 * @param owlIndi the current individual.
	 * @param value the old OWLLiteral value.
	 * @param property the property as defined in the vocabulary.
	 * @param onto the ontology the individuals are part of.
	 * @param factory the factory that should be used.
	 * @param manager the manager that manages the ontology.
     */
	public static void removeOWLLiteralProperty(OWLIndividual owlIndi, OWLLiteral value, String property, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager  manager){
		OWLDataProperty p = factory.getOWLDataProperty(IRI.create(Settings.uri+property));
		OWLLiteral owlc = factory.getOWLLiteral(value.getLiteral(), value.getLang());
	    manager.applyChange(new RemoveAxiom(onto, factory.getOWLDataPropertyAssertionAxiom(p, owlIndi, owlc)));
	}
}
