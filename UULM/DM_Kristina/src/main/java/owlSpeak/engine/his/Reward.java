package owlSpeak.engine.his;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Agenda;
import owlSpeak.GenericClass;
import owlSpeak.GenericProvider;

/**
 * This class implements the java class representation of the owl concept of a
 * reward. It is used along with automatic policy optimization techniques to
 * determine the reward value for the executed agenda of the dialogue manager OwlSpeak.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */

public class Reward extends GenericClass {
	/**
	 * creates an Agenda object by calling super
	 * 
	 * @param indi
	 *            the OWLIndividual that is contained
	 * @param onto
	 *            the OWLOntology indi belongs to
	 * @param factory
	 *            the OWLDataFactory which should be used
	 * @param manager
	 *            the OWLOntologyManger that mangages onto
	 */
	public Reward(OWLIndividual indi, OWLOntology onto, OWLDataFactory factory,
			OWLOntologyManager manager) {
		super(indi, onto, factory, manager);
	}

	/**
	 * returns a collection of Agendas that are linked in the getRewardingAgendas field of the
	 * Reward.
	 * 
	 * @return the collection of Agendas.
	 */
	public Collection<Agenda> getRewardingAgendas() {
		Iterator<GenericClass> genCollIt = GenericProvider.getIndividualColl(
				indi, GenericProvider.rewardingAgendasProp, onto, factory,
				manager).iterator();
		Collection<Agenda> coll = new LinkedList<Agenda>();
		while (genCollIt.hasNext()) {
			coll.add(genCollIt.next().asAgenda());
		}
		return coll;
	}

	/**
	 * returns true if the getRewardingAgendas field contains at least on item.
	 * 
	 * @return true if getRewardingAgendas is not empty.
	 */
	public boolean hasRewardingAgendas() {
		return GenericProvider.hasObjectProperty(indi,
				GenericProvider.rewardingAgendasProp, onto, factory);
	}

	/**
	 * adds an Agenda to the getRewardingAgendas field of the Reward.
	 * 
	 * @param newRewardingAgenda
	 *            the Move that should be added.
	 */
	public void addRewardingAgenda(Agenda newRewardingAgenda) {
		GenericProvider.addIndividual(indi, newRewardingAgenda.indi,
				GenericProvider.rewardingAgendasProp, onto, factory, manager);
	}

	/**
	 * removes a Agenda from the getRewardingAgendas field of the Reward.
	 * 
	 * @param rewardingAgenda
	 *            the Move that should be removed.
	 */
	public void removeRewardingAgenda(Agenda rewardingAgenda) {
		GenericProvider.removeIndividual(indi, rewardingAgenda.indi,
				GenericProvider.rewardingAgendasProp, onto, factory, manager);
	}

	/**
	 * replaces the specified Agenda in the getRewardingAgendas field with the new Agenda.
	 * 
	 * @param oldRewardingAgenda
	 *            the current Agenda.
	 * @param newRewardingAgenda
	 *            the Agenda that should replace the current Agenda.
	 */
	public void replaceRewardingAgenda(Agenda oldRewardingAgenda,
			Agenda newRewardingAgenda) {
		removeRewardingAgenda(oldRewardingAgenda);
		addRewardingAgenda(newRewardingAgenda);
	}

	/**
	 * returns the rewardValue of the Reward.
	 * 
	 * @return the rewardValue of the Reward.
	 */
	public int getRewardValue() {
		return GenericProvider.getIntProperty(indi,
				GenericProvider.rewardValueProp, onto, factory);
	}

	/**
	 * returns true if the rewardValue field contains a number.
	 * 
	 * @return true if rewardValue is not empty.
	 */
	public boolean hasRewardValue() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.rewardValueProp, onto, factory);
	}

	/**
	 * removes the rewardValue of the Reward.
	 */
	public void removeRewardValue() {
		GenericProvider.removeIntData(indi, getRewardValue(),
				GenericProvider.rewardValueProp, onto, factory, manager);
	}

	/**
	 * sets the rewardValue of the Reward to the specified value.
	 * 
	 * @param newRewardValue
	 *            the new rewardValue of the Reward.
	 */
	public void setRewardValue(int newRewardValue) {
		if (hasRewardValue())
			removeRewardValue();
		GenericProvider.addIntData(indi, newRewardValue,
				GenericProvider.rewardValueProp, onto, factory, manager);
	}

	/**
	 * returns the special reward of the Reward out of "no" and "abort_reward"
	 * 
	 * @return the special reward of the Reward.
	 */
	public String getSpecialReward() {
		if (hasSpecialReward())
			return GenericProvider.getStringData(indi,
					GenericProvider.specialRewardProp, onto, factory);
		return "no";
	}

	/**
	 * returns true if the special reward field contains a string.
	 * 
	 * @return true if special reward is not empty.
	 */
	public boolean hasSpecialReward() {
		return GenericProvider.hasDataProperty(indi,
				GenericProvider.specialRewardProp, onto, factory);
	}

	/**
	 * removes the special reward property of the Reward.
	 */
	public void removeSpecialReward() {
		GenericProvider.removeStringData(indi, getSpecialReward(),
				GenericProvider.specialRewardProp, onto, factory, manager);
	}

	/**
	 * sets the special reward of the Reward to the specified value.
	 * 
	 * @param newSpecialReward
	 *            the new special reward of the Reward.
	 */
	public void setSpecialReward(String newSpecialReward) {
		if (hasSpecialReward())
			removeSpecialReward();
		GenericProvider.addStringData(indi, newSpecialReward,
				GenericProvider.specialRewardProp, onto, factory, manager);
	}
}
