package usersimulatorKristina.dialogueacts;

/**
 * 
 * Definition of all possible DialogueTypes
 * 
 * @author iFeustel
 *
 */

public enum DialogueType {
	//Apologise
	SimpleApologise, PersonalApologise,
	//Clarify
	RequestRepeat, RequestRephrase, StateMissingComprehension,
	//Emotional
	AdressEmotion, RequestPresenceEmotion, RequestReasonForEmotion, CalmDown, CheerUp, Console, ShareJoy,
	//Greet
	MorningGreet, AfternoonGreet, EveningGreet, PersonalGreet, SimpleGreet,
	//Incomprehension
	Incomprehensible,
	//Motivate
	GroupOrientedMotivate, IndividualisticallyOrientedMotivate, PersonalMotivate, SimpleMotivate,
	//OnHold
	OnHold,
	//SayGoodbye
	MorningSayGoodbye, AfternoonSayGoodbye, EveningSayGoodbye, WeekendSayGoodbye, MeetAgainSayGoodbye, PersonalSayGoodbye, SimpleSayGoodbye,
	//StartConversation
	AskMood, AskPlans, AskTask, AskTaskSuccess, AskWellBeing,
	//Thank
	PersonalThank, SimpleThank, PersonalAnswerThank, AnswerThank,
	//Request
	Request, RequestAdditionalInformation,RequestMissingInformation,ExplicitlyConfirmRecognisedInput, ImplicitlyConfirmRecognisedInput, RepeatPreviousUtterance, 
	RephrasePreviousUtterance, RequestNewspaper, RequestWeather,
	//Statement
	Accept, Reject, Acknowledge, Advise, Declare, Obligate, Order,
	//Show
	ReadNewspaper, ShowVideo, ShowWeather, ShowWebpage;
		
}
