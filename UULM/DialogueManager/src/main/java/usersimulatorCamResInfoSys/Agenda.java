package usersimulatorCamResInfoSys;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.json.simple.JSONObject;

import usersimulatorCamResInfoSys.dialogueacts.ByeDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.NothingDialogueAct;

/**
 * User Agenda class containing the pending user dialogue acts that are needed
 * to elicit the information specified in the goal.
 * 
 * @author mkraus
 * 
 */
public class Agenda {
	protected LinkedList<DialogueAct> agenda;
	protected double probability;
	protected double transprob;

	public Agenda() {
		agenda = new LinkedList<DialogueAct>();
	}

	public Agenda(Vector<DialogueAct> acts, double d) {
		agenda = new LinkedList<DialogueAct>();
		agenda.addAll(acts);
		this.transprob = d;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public double getTransprob() {
		return transprob;
	}

	public void setTransprob(double transprob) {
		this.transprob = transprob;
	}

	/**
	 * Method used to update the Agendas. All Dialogue Acts below the bye() act
	 * are removed from each Agenda. All Duplicate Acts are removed, too.
	 * 
	 * @param vec
	 * @return updated Agenda Vector
	 */
	/*
	 * public static Vector<Agenda> cleanUp(Vector<Agenda> vec) { for (int i =
	 * 0; i < vec.size(); i++) { Agenda a = vec.elementAt(i); double d =
	 * a.transprob; int n = a.getByeAct(); List<DialogueAct> list =
	 * a.agenda.subList(0, n + 1); Vector<DialogueAct> v = new
	 * Vector<DialogueAct>();
	 * 
	 * for (int j = 0; j < list.size(); j++) { if (!v.contains(list.get(j)))
	 * v.add(list.get(j)); }
	 * 
	 * vec.set(i, new Agenda(v, d));
	 * 
	 * }
	 * 
	 * return vec; }
	 * 
	 * /** Method to find the bye() act in an Agenda.
	 * 
	 * @return the position of the bye() act in the Agenda
	 */
	/*
	 * private int getByeAct() { int t = 0; for (int i = 0; i < agenda.size();
	 * i++) { if (agenda.get(i) != null &&
	 * agenda.get(i).getType().equals(DialogueType.Bye)) { t = i; break; } }
	 * return t; }
	 */
	/**
	 * Merging of equal Agendas in the state vector during the ongoing dialogue
	 * including the probability of the Agendas.
	 * 
	 * @param vec
	 * @return pruned state vector
	 */
	public static Vector<Agenda> mergeAgendasWithProb(Vector<Agenda> vec) {
		Vector<Agenda> v = new Vector<Agenda>();
		for (int i = 0; i < vec.size(); i++) {
			if (v.contains(vec.elementAt(i))) {
				int index = v.indexOf(vec.elementAt(i));
				Agenda a = v.get(index);
				a.probability += vec.elementAt(i).probability;
			}

			if (!v.contains(vec.elementAt(i)))
				v.add(vec.elementAt(i));

		}

		return v;
	}

	/**
	 * Merging of the permuted Agendas after initialization.
	 * 
	 * @param vec
	 * @return pruned state Vector
	 */
	public static Vector<Agenda> mergeAgendas(Vector<Agenda> vec) {
		Vector<Agenda> v = new Vector<Agenda>();
		for (int i = 0; i < vec.size(); i++) {
			if (!v.contains(vec.elementAt(i)))
				v.add(vec.elementAt(i));

		}

		return v;
	}

	/**
	 * calculates the uniform probability of the Agendas at the beginning of the
	 * Dialogue
	 * 
	 * @param v
	 */
	public static void calcProb(Vector<Agenda> v) {
		for (int i = 0; i < v.size(); i++) {
			v.elementAt(i).setProbability(1.0 / v.size());

		}
	}

	/**
	 * Method to Push Dialogue Acts onto the Agendas and calculating the
	 * transition probability to the transformed Agenda
	 * 
	 * @param vec
	 * @return Vector containing the new Agendas with the pushed acts.
	 */
	public Vector<StateAgenda> pushActs(LinkedHashSet<PushActionCombination> vec) {
		Vector<StateAgenda> v = new Vector<StateAgenda>();
		
		for (PushActionCombination list : vec) {
			boolean noNothing = true;
			

			StateAgenda a = new StateAgenda(this);
			a.probability = this.probability;
			a.transprob = list.prob;
			if (list.size() == 1)
				noNothing = false;

			for (int i = list.size() - 1; i >= 0; i--) {

				//PushAction p = list.get(i);
				//DialogueAct d = p.getdA();
				DialogueAct d = list.get(i);
				a.mapping = list.mapping;

				if (!(d instanceof NothingDialogueAct)) {

					if (d instanceof ByeDialogueAct) {
						a.agenda.clear();
						noNothing = false;

					}

					if (a.agenda.contains(d))
						a.agenda.remove(d);

					a.agenda.push(d);

				} else {
					noNothing = false;
				}

			}

			 
			/*
			  if (!v.add(a)) { for (Agenda ag : v) { if (ag.equals(a)) {
			  ag.transprob += a.transprob; break; } } }
			 */
			int indexOfA = -1;
			if (noNothing)

				v.add(a);

			else {
				indexOfA = v.indexOf(a);
				if (indexOfA == -1)
					v.add(a);

				else {
					StateAgenda ag = v.get(indexOfA);
					if (ag.equals(a))
						ag.transprob += a.transprob;
				}

			}
			a.probability *= a.transprob;
		}
		
		return v;

	}

	/**
	 * Method to get the probability P(n|T(A(N)))
	 * 
	 * @param n
	 * @param a
	 * @return P(n|T(A(N)))
	 */
	public static double calculateNProb(int n, Agenda a, JSONObject jsonObject) {
		double d = 0.0;
		DialogueAct dA = a.agenda.peek();
		DialogueType t = dA.getType();

		@SuppressWarnings("unchecked")
		Map<String, Double> keys = (Map<String, Double>) jsonObject.get("P(n|"
				+ t + ")");
		if (keys.containsKey("P(" + n + "|" + t + ")"))
			d = keys.get("P(" + n + "|" + t + ")");

		return d;

	}

	/**
	 * calculates the argmax of n, while summing P(n|T(A(N))) over all Agendas
	 * 
	 * @param vec
	 * @return n
	 */
	public static int determineNforUserAct(Agenda a,
			JSONObject jsonObject) {
		Map<Double, Integer> m = new TreeMap<Double,Integer>();
		double  d = 0.0;
		int i =1;
		while(d < 1.0){
		
		d += calculateNProb(i, a, jsonObject);
		m.put(d, i);
		i++;
		}
		int value = ((TreeMap<Double, Integer>) m).ceilingEntry(Math.random()).getValue();
		return value;
	}

	/**
	 * Computes the index of the max element in an array of doubles.
	 * 
	 * @param x
	 *            An array of doubles. x = {x_1, x_2, ... x_n}.
	 * @return argmax_i(x)
	 */
	public static int argmax(double[] x) {
		double max;
		int argmax;
		if (x.length == 0) {
			argmax = -1;
		} else {
			max = x[0];
			argmax = 0;
			for (int i = 1; i < x.length; i++) {
				if (max < x[i]) {
					max = x[i];
					argmax = i;
				}
			}
		}
		return argmax;
	}

	/**
	 * Method to pop the User Action off the Agendas where it is possible.
	 * 
	 * @param vec
	 * @param u
	 * @return updated state Vector
	 */
	public static Vector<Agenda> performPopActs(Vector<Agenda> vec,
			UserAction u, JSONObject jsonObject) {

		List<DialogueAct> dList = new LinkedList<DialogueAct>();
		List<DialogueAct> list = u.getDialActList();
		Vector<Agenda> v = new Vector<Agenda>();

		for (Agenda a : vec) {
			if (a.agenda.size() > 1 && list.size() <= a.agenda.size())
				dList = a.agenda.subList(0, list.size());
			else
				dList = a.agenda;
			if (dList.containsAll(list)) {
				a.probability *= calculateNProb(list.size(), a, jsonObject);
				for (int i = 0; i < list.size(); i++) {
					a.agenda.pop();
				}

			} else {
				a.probability = 0.0;
			}

		}

		for (Agenda a : vec) {
			if (a.probability != 0.0)
				v.add(a);
		}

		return v;
	}

	@Override
	public String toString() {
		return agenda.toString();
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Agenda) {
			Agenda a = (Agenda) o;
			return a.agenda.equals(this.agenda);
		}
		return false;
	}
}
