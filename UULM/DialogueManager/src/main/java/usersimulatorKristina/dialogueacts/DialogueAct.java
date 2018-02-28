package usersimulatorKristina.dialogueacts;

import java.util.HashSet;
import java.util.Set;

import usersimulatorKristina.goal.Constraint;
import usersimulatorKristina.goal.Request;

public abstract class DialogueAct {

	private DialogueType type;
	private double probability;

	public DialogueAct(DialogueType t) {
		this.type = t;
	}

	public DialogueType getType() {
		return type;
	}

	public abstract void setTopics(Set<String> top);

	public abstract Set<String> getTopics();

	/*
	 * Creates specific dialogueAct based on the incoming DialogueType 
	 * 
	 */
	public static DialogueAct createDialogueAct(DialogueType t) {
		switch (t) {
		case Request:
		case RequestMissingInformation:
		case RequestAdditionalInformation:
		case RequestNewspaper:
		case RequestWeather:
		case RequestPresenceEmotion:
		case RequestReasonForEmotion:
			return new RequestAct(t);
		case ExplicitlyConfirmRecognisedInput:
		case ImplicitlyConfirmRecognisedInput:
		case RepeatPreviousUtterance:
			return new ConfirmAct(t);
		case SimpleApologise:
		case PersonalApologise:
			return new ApologiseAct(t);
		case RequestRepeat:
		case RequestRephrase:
		case StateMissingComprehension:
			return new ClarifyAct(t);
		case AdressEmotion:
		case CalmDown:
		case CheerUp:
		case Console:
		case ShareJoy:
			return new EmotionalAct(t);
		case MorningGreet:
		case AfternoonGreet:
		case EveningGreet:
		case PersonalGreet:
		case SimpleGreet:
			return new GreetAct(t);
		case GroupOrientedMotivate:
		case IndividualisticallyOrientedMotivate:
		case PersonalMotivate:
		case SimpleMotivate:
			return new MotivationAct(t);
		case MorningSayGoodbye:
		case AfternoonSayGoodbye:
		case EveningSayGoodbye:
		case WeekendSayGoodbye:
		case MeetAgainSayGoodbye:
		case PersonalSayGoodbye:
			return new SayGoodbyeAct(t);
		case AskMood:
		case AskPlans:
		case AskTask:
		case AskTaskSuccess:
		case AskWellBeing:
			return new StartConversationAct(t);
		case PersonalThank:
		case SimpleThank:
		case PersonalAnswerThank:
		case AnswerThank:
			return new ThankAct(t);
		case Accept:
			return new AcceptAct(t);
		case Reject:
			return new RejectAct(t);
		case Acknowledge:
			return new AcknowledgeAct(t);
		case Advise:
		case Obligate:
		case Order:
			return new StatementAct(t);
		case Declare:
			return new DeclareAct(t);
		case ReadNewspaper:
		case ShowVideo:
		case ShowWeather:
		case ShowWebpage:
			return new ShowAct(t);

		// TODO: Einordnung??
		case OnHold:
		case Incomprehensible:
		default:
			return new UndefinedAct(t);
		}

	}

	/*
	 * Creates DeclareAct with Topics based on a constraint c
	 */
	public static DialogueAct createDeclareAct(Constraint c) {
		DeclareAct declare = new DeclareAct(DialogueType.Declare);
		Set<String> topics = new HashSet<String>();
		topics.add(c.getIdentifier());
		topics.add(c.getValue());
		declare.setTopics(topics);
		return declare;
	}

	/*
	 * Creates RequestAct with Topics based on a request r
	 */
	public static DialogueAct createRequestAct(Request r) {
		RequestAct request;
		//Check if it is a request or specific Request (e.g. RequestNewspaper)
		if (r.getIdentifier().equals(DialogueType.Request.toString())) {
			request = new RequestAct(DialogueType.Request);
			Set<String> topics = new HashSet<String>();
			topics.add(r.getVal());
			request.setTopics(topics);
		} else {
			request = new RequestAct(DialogueType.valueOf(r.getIdentifier()));
		}

		return request;
	}

	@Override
	public String toString() {
		return type.toString();
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DialogueAct) {
			DialogueAct act = (DialogueAct) obj;
			if (this.type == act.type) {
				return true;
			}
		}
		return false;

	}

}
