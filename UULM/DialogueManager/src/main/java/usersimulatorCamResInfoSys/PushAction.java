package usersimulatorCamResInfoSys;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import usersimulatorCamResInfoSys.dialogueacts.AffirmDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ConfirmDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.DenyDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.InformDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.NegateDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ReqaltsDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RequestDialogueAct;
import usersimulatorCamResInfoSys.sumpushacts.AffirmAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.ConfirmAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.DenyAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.InformAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.InformBYSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.NegateAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.ReqaltsAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.RequestASumPushAct;

/**
 * PushAction class representing the Acts which will be pushed on the Agenda
 * 
 * @author mkraus
 * 
 */
public class PushAction {
	private DialogueAct dA;
	private double probability;
	//private String pushTransModParam;
	public Map<SummaryConditionType,SummaryPushActType> mapping;

	public PushAction(DialogueAct dA, double prob,  Map<SummaryConditionType,SummaryPushActType> map) {
		this.dA = dA;
		this.probability = prob;
		//this.pushTransModParam = push;
		this.mapping = map;
	}

	/**
	 * creates Push Actions depending on the SummaryPushAction
	 * 
	 * @param sPA
	 * @return new Push Action
	 */
	public static Vector<PushAction> createPushAct(SummaryPushAction sPA) {
		SummaryPushActType t = sPA.getType();
		Vector<PushAction> vec = new Vector<PushAction>();
		Vector<String> v;
		DialogueAct d;
		switch (t) {
		case PushAffirmAX:
			AffirmAXSumPushAct aSP = (AffirmAXSumPushAct) sPA;
			v = aSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				AffirmDialogueAct ad = (AffirmDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Affirm);
				ad.addSlotValue(v.elementAt(i), aSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(ad, sPA.getProbability() / v.size(), aSP.mapping));
			}
			return vec;

		case PushInformAX:
			InformAXSumPushAct iSP = (InformAXSumPushAct) sPA;
			v = iSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				InformDialogueAct id = (InformDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Inform);
				id.addSlotValue(v.elementAt(i), iSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(id, sPA.getProbability() / v.size(),  iSP.mapping));
			}
			return vec;
		case PushHello:
			d = DialogueAct.createDialogueAct(DialogueType.Hello);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		case PushBye:
			d = DialogueAct.createDialogueAct(DialogueType.Bye);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		case PushInformBY:

			InformBYSumPushAct iBSP = (InformBYSumPushAct) sPA;
			v = iBSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				InformDialogueAct idA = (InformDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Inform);
				idA.addSlotValue(v.elementAt(i), iBSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(idA, sPA.getProbability() / v.size(),  iBSP.mapping));
			}
			return vec;
		case PushRequestA:

			RequestASumPushAct rSP = (RequestASumPushAct) sPA;
			v = rSP.getSlot();
			for (int i = 0; i < v.size(); i++) {
				RequestDialogueAct r = (RequestDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Request);
				r.setSlot(v.elementAt(i));
				vec.add(new PushAction(r, sPA.getProbability() / v.size(),  rSP.mapping));
			}
			return vec;
		case PushReqaltsAX:

			ReqaltsAXSumPushAct rqSP = (ReqaltsAXSumPushAct) sPA;
			v = rqSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				ReqaltsDialogueAct rq = (ReqaltsDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Reqalts);
				rq.addSlotValue(v.elementAt(i), rqSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(rq, sPA.getProbability() / v.size(),  rqSP.mapping));
			}
			return vec;
		case PushConfirmAX:

			ConfirmAXSumPushAct cSP = (ConfirmAXSumPushAct) sPA;
			v = cSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				ConfirmDialogueAct c = (ConfirmDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Confirm);
				c.addSlotValue(v.elementAt(i), cSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(c, sPA.getProbability() / v.size(),  cSP.mapping));
			}
			return vec;
		case PushAffirm:
			d = DialogueAct.createDialogueAct(DialogueType.Affirm);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		case PushNegateAX:

			NegateAXSumPushAct nSP = (NegateAXSumPushAct) sPA;
			v = nSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				NegateDialogueAct n = (NegateDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Negate);
				n.addSlotValue(v.elementAt(i), nSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(n, sPA.getProbability() / v.size(), nSP.mapping));
			}
			return vec;
		case PushDenyAX:

			DenyAXSumPushAct dSP = (DenyAXSumPushAct) sPA;
			v = dSP.getSlots();
			for (int i = 0; i < v.size(); i++) {
				DenyDialogueAct dd = (DenyDialogueAct) DialogueAct
						.createDialogueAct(DialogueType.Deny);
				dd.addSlotValue(v.elementAt(i), dSP.getValue(v.elementAt(i)));
				vec.add(new PushAction(dd, sPA.getProbability() / v.size(),  dSP.mapping));
			}
			return vec;
		case PushNothing:
			d = DialogueAct.createDialogueAct(DialogueType.Nothing);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		case PushThankyou:
			d = DialogueAct.createDialogueAct(DialogueType.Thankyou);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		case PushNull:
			d = DialogueAct.createDialogueAct(DialogueType.Null);
			vec.add(new PushAction(d, sPA.getProbability(),sPA.mapping));
			return vec;
		case PushAck:
			d = DialogueAct.createDialogueAct(DialogueType.Ack);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		case PushRepeat:
			d = DialogueAct.createDialogueAct(DialogueType.Repeat);
			vec.add(new PushAction(d, sPA.getProbability(), sPA.mapping));
			return vec;
		case PushRestart:
			d = DialogueAct.createDialogueAct(DialogueType.Restart);
			vec.add(new PushAction(d, sPA.getProbability(),  sPA.mapping));
			return vec;
		default:
			break;
		}
		return null;
	}

	/**
	 * generates a list of PushActs out of a list of SummaryPushActs
	 * 
	 * @param listSPA
	 * @return
	 */
	public static List<PushAction> createPushActionList(
			List<SummaryPushAction> listSPA) {
		List<PushAction> p = new ArrayList<PushAction>();
		for (SummaryPushAction sPA : listSPA) {
			Vector<PushAction> a = PushAction.createPushAct(sPA);
			p.addAll(a);
		}
		return p;
	}

	/**
	 * Method to generate Combinations of PushActions of a different Set of
	 * SummaryPushActions
	 * 
	 * @param v
	 * @return
	 */
/*	public static LinkedHashSet<PushActionCombination> generateCombination(
			Vector<List<PushAction>> v) {
		LinkedHashSet<PushActionCombination> result = new LinkedHashSet<PushActionCombination>();
		PushActionCombination list = new PushActionCombination();

		combination(v, 0, list, result);

		return result;
	}

	/**
	 * Recursive Method invoked by the generateCombination() method in order to
	 * generate combinations
	 * 
	 * @param a
	 * @param pos
	 * @param list
	 * @param result
	 */
	/*
	private static void combination(Vector<List<PushAction>> a, int pos,
			PushActionCombination list,
			LinkedHashSet<PushActionCombination> result) {

		if (a.size() == pos) {
			// shrink list
			// add list only if not already present in result

			double prob = list.calculateProb();
			//ArrayList<PushAction> helplist = new ArrayList<PushAction>(list);
			LinkedHashSet<PushAction> set = new LinkedHashSet<PushAction>(list);
			list = new PushActionCombination(new ArrayList<PushAction>(set));
			list.setProb(prob);
			
			if (!result.add(list)) {
				for (PushActionCombination p : result) {
					if (p.equals(list))
						p.prob += list.prob;
					break;
				}
			}

			// if list already present, add their probabilities
			return;

		} else {
			List<PushAction> currentResult = a.get(pos);

			for (PushAction p : currentResult) {
				PushActionCombination copy = (PushActionCombination) list
						.clone();

				copy.add(p);

				combination(a, pos + 1, copy, result);
			} 

		}

	}
*/
	public static LinkedHashSet<PushActionCombination> generateCombination(
			Vector<List<PushAction>> v) {
		LinkedHashSet<PushActionCombination> result = new LinkedHashSet<PushActionCombination>();
		ArrayList<PushAction> list = new ArrayList<PushAction>();

		combination(v, 0, list, result);

		return result;
	}

	/**
	 * Recursive Method invoked by the generateCombination() method in order to
	 * generate combinations
	 * 
	 * @param a
	 * @param pos
	 * @param list
	 * @param result
	 */
	
	private static void combination(Vector<List<PushAction>> a, int pos,
			ArrayList<PushAction> list,
			LinkedHashSet<PushActionCombination> result) {

		if (a.size() == pos) {
			// shrink list
			// add list only if not already present in result
			Vector<DialogueAct> vec = new Vector<DialogueAct>();
			for(PushAction p: list){
				if(!vec.contains(p.dA))
					vec.add(p.dA);
			}
			PushActionCombination p = new PushActionCombination(vec);
			double prob = p.calculateProb(list);
			p.setMapping(list);
			//ArrayList<PushAction> helplist = new ArrayList<PushAction>(list);
			
			p.setProb(prob);
			
			if (!result.add(p)) {
				for (PushActionCombination pA : result) {
					if (pA.equals(list))
						pA.prob += p.prob;
					break;
				}
			}

			// if list already present, add their probabilities
			return;

		} else {
			List<PushAction> currentResult = a.get(pos);

			for (PushAction p : currentResult) {
				@SuppressWarnings("unchecked")
				ArrayList<PushAction> copy = (ArrayList<PushAction>) list
						.clone();

				copy.add(p);

				combination(a, pos + 1, copy, result);
			} 

		}

	}
	public DialogueAct getdA() {
		return dA;
	}

	public void setdA(DialogueAct dA) {
		this.dA = dA;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

/*	public String getPushTransModParam() {
		return pushTransModParam;
	}

	public void setPushTransModParam(String pushTransModParam) {
		this.pushTransModParam = pushTransModParam;
	}
*/
	@Override
	public String toString() {
		return "" + dA + "[" + probability + "]";
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof PushAction) {

			return true;
		} else
			return false;
	}
}
