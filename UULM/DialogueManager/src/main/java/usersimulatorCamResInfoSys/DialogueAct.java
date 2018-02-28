package usersimulatorCamResInfoSys;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import usersimulatorCamResInfoSys.dialogueacts.AckDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.AffirmDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ByeDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.CantHelpDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ConfirmDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.Confirm_DomainDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ConfreqDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.DenyDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.HelloDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.InformDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.NegateDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.NothingDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.NullDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.OfferDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RepeatDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ReqMoreDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ReqaltsDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RequestDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RestartDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.SelectDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.ThankyouDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.WelcomeMSGDialogueAct;

/**
 * Abstract class Dialogue Act generating all types of Dialogue Acts
 * 
 * @author mkraus
 * 
 */
public abstract class DialogueAct {

	protected DialogueType type;

	public DialogueType getType() {
		return type;
	}

	public DialogueAct(DialogueType t) {
		this.type = t;
	}

	public abstract void extractInformation(Constraint c);

	public abstract void extractInformation(Request r);
	
	public abstract void mergeActs(DialogueAct d);
	
	public abstract Map<Constraint, Integer> createLCMapping(Vector<Constraint> vec);

	/**
	 * generates Dialogue Acts depending on the individual Dialogue Type.
	 * 
	 * @param t
	 * @return new Dialogue Act
	 */
	public static DialogueAct createDialogueAct(DialogueType t) {

		switch (t) {
		case Inform:
			return new InformDialogueAct(t);
		case Request:
			return new RequestDialogueAct(t);

		case Affirm:
			return new AffirmDialogueAct(t);

		case Negate:
			return new NegateDialogueAct(t);

		case Bye:
			return new ByeDialogueAct(t);

		case Confirm:
			return new ConfirmDialogueAct(t);

		case Confreq:
			return new ConfreqDialogueAct(t);

		case Deny:
			return new DenyDialogueAct(t);

		case Hello:
			return new HelloDialogueAct(t);

		case Reqalts:
			return new ReqaltsDialogueAct(t);

		case Select:
			return new SelectDialogueAct(t);

		case Nothing:
			return new NothingDialogueAct(t);

		case Welcomemsg:
			return new WelcomeMSGDialogueAct(t);

		case Offer:
			return new OfferDialogueAct(t);

		case Thankyou:
			return new ThankyouDialogueAct(t);

		case Canthelp:
			return new CantHelpDialogueAct(t);

		case Reqmore:
			return new ReqMoreDialogueAct(t);

		case Confirm_domain:
			return new Confirm_DomainDialogueAct(t);

		case Null:
			return new NullDialogueAct(t);

		case Ack:
			return new AckDialogueAct(t);

		case Repeat:
			return new RepeatDialogueAct(t);

		case Restart:
			return new RestartDialogueAct(t);
		default:
			break;

		}
		return null;

	}

	/**
	 * Method to create Inform Dialogue Acts from Constraints
	 * 
	 * @param c
	 * @return new Information DialogueAct
	 */
	public static DialogueAct createDialogueAct(Constraint c) {
		// extract type from c
		DialogueType t = DialogueType.Inform;

		DialogueAct a = createDialogueAct(t);

		a.extractInformation(c);
		return a;
	}

	/**
	 * Method to create Request Dialogue Acts from Requests
	 * 
	 * @param r
	 * @return new Request DialogueAct
	 */
	public static DialogueAct createDialogueAct(Request r) {
		// extract type from c
		DialogueType t = DialogueType.Request;

		DialogueAct a = createDialogueAct(t);

		a.extractInformation(r);
		return a;
	}

	public static DialogueAct formDialogueAct(List<String[]> list, String act) {

		DialogueType t;
		if(act.equals("Canthelp.exception"))
			act = "Canthelp";
		t = DialogueType.valueOf(act);

		DialogueAct DialAct = createDialogueAct(t);

		switch (t) {
		case Inform:
			InformDialogueAct inform = (InformDialogueAct) DialAct;
			for (String[] slots : list)
				inform.addSlotValue(slots[0], slots[1]);
			return inform;

		case Request:
			RequestDialogueAct req = (RequestDialogueAct) DialAct;
			for (String[] slots : list)
				req.setSlot(slots[1]);
			return req;

		case Welcomemsg:
			WelcomeMSGDialogueAct welcome = (WelcomeMSGDialogueAct) DialAct;
			return welcome;

		case Offer:
			OfferDialogueAct offer = (OfferDialogueAct) DialAct;
			for (String[] slots : list)
				offer.addSlotValue(slots[0], slots[1]);
			return offer;

		case Thankyou:
			return (ThankyouDialogueAct) DialAct;

		case Bye:
			return (ByeDialogueAct) DialAct;

		case Canthelp:
			CantHelpDialogueAct cDA = (CantHelpDialogueAct) DialAct;
			for (String[] slots : list)
				cDA.addSlotValue(slots[0], slots[1]);
			return cDA;

		case Reqmore:
			return (ReqMoreDialogueAct) DialAct;

		case Null:
			return (NullDialogueAct) DialAct;

		case Ack:
			return (AckDialogueAct) DialAct;

		case Repeat:
			return (RepeatDialogueAct) DialAct;

		case Restart:
			return (RestartDialogueAct) DialAct;

		case Reqalts:
			return (ReqaltsDialogueAct) DialAct;

		case Affirm:
			AffirmDialogueAct affirm = (AffirmDialogueAct) DialAct;
			if (!list.isEmpty())
				for (String[] slots : list)
					affirm.addSlotValue(slots[0], slots[1]);

			return affirm;

		case Confirm:
			ConfirmDialogueAct conf = (ConfirmDialogueAct) DialAct;
			for (String[] slots : list)
				conf.addSlotValue(slots[0], slots[1]);
			return conf;

		case Confirm_domain:
			return (Confirm_DomainDialogueAct) DialAct;

		case Confreq:
			ConfreqDialogueAct conReq = (ConfreqDialogueAct) DialAct;
			for (String[] slots : list)
				conReq.addSlotValue(slots[0], slots[1]);
			return conReq;

		case Deny:
			DenyDialogueAct deny = (DenyDialogueAct) DialAct;
			if (!list.isEmpty())
				for (String[] slots : list)
					deny.addSlotValue(slots[0], slots[1]);

			return deny;
			
		case Hello:
			return (HelloDialogueAct) DialAct;
			
		case Negate:
			NegateDialogueAct neg = (NegateDialogueAct) DialAct;
			if (!list.isEmpty())
				for (String[] slots : list)
					neg.addSlotValue(slots[0], slots[1]);

			return neg;
			
		case Select:
			SelectDialogueAct sel = (SelectDialogueAct) DialAct;
			if (!list.isEmpty())
				for (String[] slots : list)
					sel.addSlotValue(slots[0], slots[1]);

			return sel;
			

		default:
			break;
		}
		return null;
	}
	
	
	public void createMerge(DialogueAct dialAct){
		DialogueType t = this.type;
		switch (t) {
		case Inform:
			InformDialogueAct inform = (InformDialogueAct) this;
			inform.mergeActs(dialAct);;
			break;
		case Request:
			RequestDialogueAct req = (RequestDialogueAct) this;
			req.mergeActs(dialAct);
			break;
		case Canthelp:
			CantHelpDialogueAct cDA = (CantHelpDialogueAct) this;
			cDA.mergeActs(dialAct);
			break;
		case Reqmore:
			ReqMoreDialogueAct reqmore = (ReqMoreDialogueAct) this;
			reqmore.mergeActs(dialAct);
			break;
		case Reqalts:
			ReqaltsDialogueAct reqalts = (ReqaltsDialogueAct) this;
			reqalts.mergeActs(dialAct);
			break;
		case Affirm:
			AffirmDialogueAct affirm = (AffirmDialogueAct) this;
			affirm.mergeActs(dialAct);
			break;
		case Confirm:
			ConfirmDialogueAct conf = (ConfirmDialogueAct) this;
			conf.mergeActs(dialAct);
			break;
		case Confreq:
			ConfreqDialogueAct conReq = (ConfreqDialogueAct) this;
			conReq.mergeActs(dialAct);
			break;
		case Deny:
			DenyDialogueAct deny = (DenyDialogueAct) this;
			deny.mergeActs(dialAct);
			break;
		case Negate:
			NegateDialogueAct neg = (NegateDialogueAct) this;
			neg.mergeActs(dialAct);
			break;
		case Select:
			SelectDialogueAct sel = (SelectDialogueAct) this;
			sel.mergeActs(dialAct);
			break;

		default:
			break;
		}
	}
	
	/**
	 * Method to gain the SummaryCondition type depending on the type of the
	 * individual Dialogue Act
	 * 
	 * @param a
	 * @param g
	 * @return
	 */
	public Vector<SummaryConditionType> getSumCondTypeFromDA(UserGoal g) {
		Vector<SummaryConditionType> vType = new Vector<SummaryConditionType>();
		ConfirmDialogueAct cDA;
		Vector<String> v;
		Vector<Constraint> vec1 = new Vector<Constraint>();
		Vector<Constraint> vec2 = new Vector<Constraint>();
		switch (this.getType()) {
		case Inform:
			vType.add(SummaryConditionType.ReceiveInformAX);
			break;
		case Affirm:
			vType.add(SummaryConditionType.ReceiveAffirmAX);
			break;
		case Bye:
			vType.add(SummaryConditionType.ReceiveBye);
			break;
		case Confirm:
			cDA = (ConfirmDialogueAct) this;
			v = cDA.getSlots();
			for(String s: v){
				Constraint c = new Constraint(s, cDA.getValue(s));
				vec1.add(c);
			}
			
			vec2 = g.getConstraints();
			if (!vec2.isEmpty()) {
			
				if (vec2.containsAll(vec1))
					vType.add(SummaryConditionType.ReceiveConfirmAXok);
				else{
				vType.add(SummaryConditionType.ReceiveConfirmAXnotOk);}}
	/*				for (int i = 0; i < vec2.size(); i++) {
						String s = vec2.elementAt(i).getSlot();
						String val = null;
						for (int j = 0; j < v.size(); j++) {
							if (v.elementAt(j).equals(s)) {
								val = cDA.getValue(s);
								if (val.equals(vec2.elementAt(i).getValue()))
									vType.add(SummaryConditionType.ReceiveConfirmAXok);
								else
									vType.add(SummaryConditionType.ReceiveConfirmAXnotOk);
							}
						}
					}
		*/		
			
			
				
				
			
			break;
		case Confreq:
			ConfreqDialogueAct crDA = (ConfreqDialogueAct) this;
			v = crDA.getSlots();
			for(String s: v){
				Constraint c = new Constraint(s, crDA.getValue(s));
				vec1.add(c);
			}
			
			vec2 = g.getConstraints();
			if (!vec2.isEmpty()) {
				if (!vec2.isEmpty()) {
					
					if (vec2.containsAll(vec1))
						vType.add(SummaryConditionType.ReceiveConfirmAXok);
					else{
					vType.add(SummaryConditionType.ReceiveConfirmAXnotOk);}}
				
				}
			vType.add(SummaryConditionType.ReceiveRequestA);
			break;
		case Welcomemsg:
			vType.add(SummaryConditionType.ReceiveHello);
			break;
		case Request:
			vType.add(SummaryConditionType.ReceiveRequestA);
			break;
		case Select:
			vType.add(SummaryConditionType.ReceiveSelect);
			break;
		case Offer:
			vType.add(SummaryConditionType.ReceiveOffer);
			break;
		case Reqmore:
			vType.add(SummaryConditionType.ReceiveReqMore);
			break;
		case Canthelp:
			vType.add(SummaryConditionType.ReceiveCantHelp);
			break;
		default:
			break;
		}
		return vType;
	}

	/**
	 * Invokes the permutation void method in order to permute the initial
	 * Agenda
	 * 
	 * @param vec
	 * @return Vector of the permuted Agendas
	 */
	public static Vector<Vector<DialogueAct>> permuteAgendas(
			Vector<DialogueAct> vec) {

		Vector<Vector<DialogueAct>> result = new Vector<Vector<DialogueAct>>();
		permutation(vec, 0, result);
		return result;
	}

	/**
	 * Void method invoked by the permutateAgenda function. Uses the swap void
	 * method to change the order of the Dialogue Acts.
	 * 
	 * @param a
	 *            (initial Agenda)
	 * @param pos
	 *            (Position of the Dialogue Act which will be swapped)
	 * @param result
	 *            (Vector of the permuted Agendas)
	 */
	private static void permutation(Vector<DialogueAct> a, int pos,
			Vector<Vector<DialogueAct>> result) {
		if (a.size() - pos == 1) {

			Vector<DialogueAct> age = new Vector<DialogueAct>(a);

			result.add(age);
		} else
			for (int i = pos; i < a.size(); i++) {
				swap(a, pos, i);
				permutation(a, pos + 1, result);
				swap(a, pos, i);
			}

	}

	/**
	 * Swaps the elements of the Agenda
	 * 
	 * @param arr
	 * @param pos1
	 * @param pos2
	 */
	private static void swap(Vector<DialogueAct> arr, int pos1, int pos2) {
		DialogueAct h = arr.elementAt(pos1);
		arr.set(pos1, arr.elementAt(pos2));
		arr.set(pos2, h);
	}
	
	
	@Override
	public String toString() {
		return  type.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof DialogueAct) {
			DialogueAct da = (DialogueAct) o;
			return (da.type == this.type);
		}
		return false;
	}
}
