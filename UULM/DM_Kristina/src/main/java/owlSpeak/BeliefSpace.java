package owlSpeak;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
/**
 * The class that represents the BeliefSpaces.
 * @author Tobias Heinroth.
 */
public class BeliefSpace extends GenericClass{
	/**
	 * the name of the BeliefSpace Class	
	 */
	public final String name = "BeliefSpace";
	/**
	 * creates a BeliefSpace object by calling super 
	 * @param indi the OWLIndividual that is contained
	 * @param onto the OWLOntology indi belongs to
	 * @param factory the OWLDataFactory which should be used
	 * @param manager the OWLOntologyManger that mangages onto
	 */ 
	public BeliefSpace(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory, OWLOntologyManager manager){
		super (indi, onto, factory, manager);
	}
	/**
	 * returns a collection of Beliefs that are linked in the HasBelief field of the BeliefSpace.
	 * @return the collection of Beliefs.
	 */
	public Collection<Belief> getHasBelief(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.hasBelProp, onto, factory,manager).iterator(); 
		Collection<Belief> coll = new LinkedList<Belief>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asBelief());
		}
		return coll;
	}
    /**
     * returns true if the HasBelief field contains at least on item.
     * @return true if HasBelief is not empty.
     */
	public boolean hasHasBelief(){
		return GenericProvider.hasObjectProperty(indi, GenericProvider.hasBelProp, onto, factory);
	}
    /**
     * adds a Belief to the HasBelief field of the BeliefSpace.
     * @param newHasBelief the Belief that should be added.
     */
	public void addHasBelief(Belief newHasBelief){
		GenericProvider.addIndividual(indi, newHasBelief.indi, GenericProvider.hasBelProp, onto, factory, manager);
	}
    /**
     * removes a Belief from the HasBelief field of the BeliefSpace.
     * @param oldHasBelief the Belief that should be removed.
     */
	public void removeHasBelief(Belief oldHasBelief){
		GenericProvider.removeIndividual(indi, oldHasBelief.indi, GenericProvider.hasBelProp, onto, factory, manager);
	}
    /**
     * replaces a Belief in the HasBelief field with a new Belief.
     * @param oldHasBelief the current Belief that should be replaced.
     * @param newHasBelief the new Belief that should replace the current Belief.
     */
	public void setHasBelief(Belief oldHasBelief, Belief newHasBelief){
		removeHasBelief(oldHasBelief);
		addHasBelief(newHasBelief);
    }
	/**
	 * returns a collection of Beliefs that are linked in the HasBelief field of the BeliefSpace.
	 * @return the collection of Beliefs.
	 */
	public Collection<Belief> getExcludesBelief(){
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(indi, GenericProvider.excludesBelProp, onto, factory,manager).iterator(); 
		Collection<Belief> coll = new LinkedList<Belief>();
		while(genCollIt.hasNext()){
			coll.add(genCollIt.next().asBelief());
		}
		return coll;
	}
    /**
     * adds a Belief to the ExcludesBelief field of the BeliefSpace.
     * @param newExcludesBelief the Belief that should be added.
     */
	public void addExcludesBelief(Belief newExcludesBelief){
		GenericProvider.addIndividual(indi, newExcludesBelief.indi, GenericProvider.excludesBelProp, onto, factory, manager);
	}
	/**
     * removes a Belief from the HasBelief field of the BeliefSpace.
     * @param oldExcludesBelief the Belief that should be removed.
     */
	public void removeExcludesBelief(Belief oldExcludesBelief){
		GenericProvider.removeIndividual(indi, oldExcludesBelief.indi, GenericProvider.excludesBelProp, onto, factory, manager);
	}
    /**
     * replaces a Belief in the ExcludesBelief field with a new Belief.
     * @param oldExcludesBelief the current Belief that should be replaced.
     * @param newExcludesBelief the new Belief that should replace the current Belief.
     */
	public void setExcludesBelief(Belief oldExcludesBelief, Belief newExcludesBelief){
		removeExcludesBelief(oldExcludesBelief);
		addExcludesBelief(newExcludesBelief);
    }
	
	@Override
	public String toString() {
		String s = super.toString() + ":\t";
		Vector<String> elems = new Vector<String>();
		for (Belief bel : getHasBelief()) {
			if (bel.hasSemantic()) {
				for (Semantic sem : bel.getSemantic()) {
					elems.add(sem.getLocalName());
				}
			}
			if (bel.hasVariabledefault() && bel.hasVariableValue()) {
				elems.add(bel.getVariabledefault().getLocalName() + "=" + bel.getVariableValue());
			}
		}
		if (elems.size() > 0)
			s += elems.get(0);
		for (int i = 1; i < elems.size(); i++) {
			s += "; " + elems.get(i);
		}
		
		
//		String s = "";
//		if (fields.size() > 0) {
//			Vector<String> elems = new Vector<String>();
//			for (String fieldName : fields.keySet()) {
//				if (fields.get(fieldName).getType() == FieldType.EQUALS) {
//					String str = fieldName + "="
//							+ fields.get(fieldName).getEquals();
//					if (fields.get(fieldName).isConfirmed())
//						str = str + "(c)";
//					elems.add(str);
//				} else if (fields.get(fieldName).getExcludes().size() <= 2) {
//					String str = fieldName + " x(";
//					Vector<String> keys = fields.get(fieldName).getExcludes();
//					if (fields.get(fieldName).getExcludes().size() > 0) {
//						str += keys.get(0);
//					}
//					if (fields.get(fieldName).getExcludes().size() > 1) {
//						str += "," + keys.get(1);
//					}
//					str += ")";
//					elems.add(str);
//				} else {
//					elems.add(fieldName + " x(["
//							+ fields.get(fieldName).getExcludes().size()
//							+ " entries])");
//				}
//			}
//			// elems.add("count=" + count);
//			if (elems.size() > 0)
//				s += elems.get(0);
//			for (int i = 1; i < elems.size(); i++) {
//				s += ";" + elems.get(i);
//			}
//		} else
//			s = "(all)";
		return s;
	}
}
