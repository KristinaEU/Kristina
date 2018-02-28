package usersimulatorCamResInfoSys;

import java.util.List;
import java.util.Vector;

import usersimulatorCamResInfoSys.dialogueacts.InformDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.OfferDialogueAct;
import usersimulatorCamResInfoSys.dialogueacts.RequestDialogueAct;

/**
 * UserGoal class compromising Constraints and Requests.
 * 
 * @author mkraus
 * 
 */
public class UserGoal {
	public static final String filePath = "c:\\OwlSpeak\\models\\ontology_dstc2.json";

	private Vector<Request> requests;
	private Vector<Constraint> constraints;

	public UserGoal() {
		constraints = Constraint.generateConstraints(filePath);
		requests = Request.generateRequests(filePath, constraints);
	}

	public UserGoal(String trainingPath) {
		constraints = Constraint.generateTrainingConstraints(trainingPath);
		requests = Request.generateTrainingRequests(trainingPath);
	}

	public UserGoal(Vector<Constraint> cVec, Vector<Request> rVec) {
		constraints = cVec;
		requests = rVec;
	}

	public Vector<Request> getRequests() {
		return requests;
	}

	public void setRequests(Vector<Request> requests) {
		this.requests = requests;
	}

	public Vector<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(Vector<Constraint> constraints) {
		this.constraints = constraints;
	}

	public void addConstraints(Vector<Constraint> c) {
		constraints.addAll(c);
	}

	public void updateRequests(SystemAction sysAct) {
		List<DialogueAct> actList = sysAct.getDialActList();
		Vector<Request> req = this.getRequests();
		for (DialogueAct act : actList) {
			DialogueType t = act.getType();

			switch (t) {
			case Inform:
				InformDialogueAct infAct = (InformDialogueAct) act;
				for (Request r : req) {
					if (r.getValue() == null) {
						for (String s : infAct.getSlots()) {
							if (r.getSlot().equals(s))
								r.setValue(infAct.getValue(s));
						}
					}

				}
				break;
			case Offer:
				OfferDialogueAct offAct = (OfferDialogueAct) act;
				for (Request r : req) {
					if (r.getValue() == null) {
						for (String s : offAct.getSlots()) {
							if (r.getSlot().equals(s))
								r.setValue(offAct.getValue(s));
						}
					}

				}
				break;
			default:
				break;
			}

		}
	}

	/**
	 * Transforms Requests and Constraints to Agendas
	 * 
	 * @param g
	 * @return new AgendaVec
	 */
	public Vector<Agenda> createAgendaVec() {
		Vector<DialogueAct> req = new Vector<DialogueAct>();
		Vector<DialogueAct> con = new Vector<DialogueAct>();
		Vector<Vector<DialogueAct>> a1 = null;
		Vector<Vector<DialogueAct>> a2 = null;
		Vector<Agenda> agenda_vec = new Vector<Agenda>();

		if (!constraints.isEmpty()) {
			for (Constraint c : constraints) {
				con.add(DialogueAct.createDialogueAct(c));
			}
			a1 = DialogueAct.permuteAgendas(con);
		}
		if (!requests.isEmpty()) {
			for (Request r : requests) {
				req.add(DialogueAct.createDialogueAct(r));
			}
			a2 = DialogueAct.permuteAgendas(req);
		}

		for (Vector<DialogueAct> vec : a1) {
			for (Vector<DialogueAct> vec1 : a2) {
				vec.addAll(vec1);
				Agenda a = new Agenda(vec, 0.0);
				a.agenda.add(DialogueAct.createDialogueAct(DialogueType.Bye));
				agenda_vec.add(a);
				vec.removeAll(vec1);
			}
		}

		return agenda_vec;
	}

	public void updateConstraints(SystemAction sysAct) {
		List<DialogueAct> actList = sysAct.getDialActList();
		Vector<Constraint> con = this.getConstraints();

		for (DialogueAct act : actList) {
			boolean newCon = true;
			if (act.getType().equals(DialogueType.Request)) {
				RequestDialogueAct reqAct = (RequestDialogueAct) act;
				for (Constraint c : con) {
					if (reqAct.getSlot().equals(c.getSlot()))
						newCon = false;
				}
				if (newCon == true)
					con.add(new Constraint(reqAct.getSlot(), "dontcare"));
			}

		}

	}

	@Override
	public String toString() {
		return "Constraints: " + constraints.toString() + " Requests: "
				+ requests;
	}
}
