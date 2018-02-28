package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
/**
 * The class that represents the Beliefs.
 * @author Tobias Heinroth.
 */
public class Belief extends GenericClass{
	/**
	 * the name of the Belief Class	
	 */
	public final String name = "Belief";
	/**
	 * creates a Belief object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public Belief(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}
	/**
	 * returns a collection of Semantics that are linked in the Semantic field of the Belief.
	 * @return the collection of Semantics.
	 */
	public Collection<Semantic> getSemantic(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.semProp, onto, factory,manager).iterator(); 
		Collection<Semantic> coll = new LinkedList<Semantic>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asSemantic());
		}
		return coll;
	}
    /**
     * returns true if the Semantic field contains at least on item.
     * @return true if Semantic is not empty.
     */
	public boolean hasSemantic(){
		return GenericProvider.hasObjectProperty(indi, GenericProvider.semProp, onto, factory);
	}
    /**
     * adds a Semantic to the Semantic field of the Belief.
     * @param newSemantic the Semantic that should be added.
     */
	public void addSemantic(Semantic newSemantic){
		GenericProvider.addIndividual(indi, newSemantic.indi, GenericProvider.semProp, onto, factory, manager);
	}
    /**
     * removes a Semantic from the Semantic field of the Belief.
     * @param oldSemantic the Semantic that should be removed.
     */
	public void removeSemantic(Semantic oldSemantic){
		GenericProvider.removeIndividual(indi, oldSemantic.indi, GenericProvider.semProp, onto, factory, manager);
	}
    /**
     * replaces a Semantic in the Semantic field with the new Semantic.
     * @param oldSemantic the current Semantics that should be replaced.
     * @param newSemantic the new Semantic that should replace the current Semantic.
     */
	public void setSemantic(Semantic oldSemantic, Semantic newSemantic){
    	removeSemantic(oldSemantic);
    	addSemantic(newSemantic);
    }
	/**
     * adds a SemanticGroup to the Semantic field of the Belief.
     * @param newSemantic the Semantic that should be added.
     */
	public void addSemantic(SemanticGroup newSemantic){
		GenericProvider.addIndividual(indi, newSemantic.indi, GenericProvider.semProp, onto, factory, manager);
	}
    
	/**
     * returns the VariableValue of the Belief.
     * @return the String contained in the VariableValue.
     */
	public String getVariableValue(){
	   	return GenericProvider.getStringData(indi, GenericProvider.varValProp, onto, factory);
	}
	/**
     * sets the String in the VariableValue field with the given String.
     * @param newVariableOperator the String that should be stored in VariableValue.
     */
	public void addVariableValue(String newVariableOperator){
    	if (hasVariableValue())
    		GenericProvider.removeStringData(indi, getVariableValue(), GenericProvider.varValProp, onto, factory, manager);
    	GenericProvider.addStringData(indi, newVariableOperator, GenericProvider.varValProp, onto, factory, manager);
    }/**
     * returns true if the VariableValue field contains a String with a length &lt;= 1.
     * @return true if VariableValue is not empty.
     */
	public boolean hasVariableValue(){
    	return GenericProvider.hasDataProperty(indi, GenericProvider.varValProp, onto, factory);
    }
    /**
     * replaces the String in the VariableValue field with the given String.
     * @param oldVariableOperator the current String that should be replaced.
     * @param newVariableOperator the String that should be stored in VariableValue.
     */
	public void setVariableValue(String oldVariableOperator, String newVariableOperator){
    	GenericProvider.removeStringData(indi, oldVariableOperator, GenericProvider.varValProp, onto, factory, manager);
    	GenericProvider.addStringData(indi, newVariableOperator, GenericProvider.varValProp, onto, factory, manager);
    }
	/**
	 * returns the Variable in the Variabledefault field of the Belief.
	 * @return the String contained in the Variabledefault.
	 */
	public Variable getVariabledefault(){
		return GenericProvider.getIndividual(indi, GenericProvider.varDefProp, onto, factory, manager).asVariable();
	}
	/**
     * replaces the Variable in the Variabledefault field with the given Variable.
     * @param newVariabledefault the new String that should be stored in Variabledefault.
     */
	public void addVariabledefault(Variable newVariabledefault){
		if (hasVariabledefault())
			GenericProvider.removeIndividual(indi,getVariabledefault().indi, GenericProvider.varDefProp, onto, factory, manager);
    	GenericProvider.addIndividual(indi, newVariabledefault.indi, GenericProvider.varDefProp, onto, factory, manager);
    }
	/**
     * returns true if the Variabledefault field contains a Variable.
     * @return true if Variabledefault is not empty.
     */
	public boolean hasVariabledefault(){
		return GenericProvider.hasObjectProperty(indi, GenericProvider.varDefProp, onto, factory);
	}
    /**
     * replaces the Variable in the Variabledefault field with the given Variable.
     * @param oldVariabledefault the String that should be replaced.
     * @param newVariabledefault the new String that should be stored in Variabledefault.
     */
	public void setVariabledefault(Variable oldVariabledefault, Variable newVariabledefault){
    	GenericProvider.removeIndividual(indi, oldVariabledefault.indi, GenericProvider.varDefProp, onto, factory, manager);
    	GenericProvider.addIndividual(indi, newVariabledefault.indi, GenericProvider.varDefProp, onto, factory, manager);
    }
}
