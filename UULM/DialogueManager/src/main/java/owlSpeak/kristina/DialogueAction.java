package owlSpeak.kristina;

public class DialogueAction {
	public static final String STATEMENT
	= OntologyPrefix.dialogue+"Statement";
	public static final String DECLARE
	= OntologyPrefix.dialogue+"Declare";
	public static final String REQUEST_MISSING
	= OntologyPrefix.dialogue+"RequestMissingInformation";
	public static final String REQUEST_ADDITIONAL
	= OntologyPrefix.dialogue+"AdditionalInformationRequest";
	public static final String REQUEST_CLARIFICATION
	= OntologyPrefix.dialogue+"RequestClarification";
	public static final String CONFIRM_EXPLICITLY
	= OntologyPrefix.dialogue+"ExplicitlyConfirmRecognisedInput";
	public static final String CONFIRM_IMPLICITLY
	= OntologyPrefix.dialogue+"ImplicitlyConfirmRecognisedInput";
	public static final String REPEAT
	= OntologyPrefix.dialogue+"RequestRepeat";
	public static final String REPHRASE
	= OntologyPrefix.dialogue+"RequestRephrase";
	public static final String MISSING_COMPREHENSION
	= OntologyPrefix.dialogue+"StateMissingComprehension";
	public static final String AFFIRM
	= OntologyPrefix.dialogue+"Affirm";
	public static final String ACCEPT
	= OntologyPrefix.dialogue+"Accept";
	public static final String REJECT
	= OntologyPrefix.dialogue+"Reject";
	public static final String ACKNOWLEDGE
	= OntologyPrefix.dialogue+"Acknowledge";
	public static final String ADVISE
	= OntologyPrefix.dialogue+"Advise";
	public static final String OBLIGATE
	= OntologyPrefix.dialogue+"Obligate";
	public static final String ORDER
	= OntologyPrefix.dialogue+"Order";
	public static final String SHOW_WEBPAGE
	= OntologyPrefix.dialogue+"ShowWebpage";
	public static final String SHOW_VIDEO
	= OntologyPrefix.dialogue+"ShowVideo";
	public static final String READ_NEWSPAPER
	= OntologyPrefix.dialogue+"ReadNewspaper";
	public static final String GREETING
	= OntologyPrefix.dialogue+"Greet";
	public static final String PERSONAL_GREETING
	= OntologyPrefix.dialogue+"PersonalGreet";
	public static final String SIMPLE_GREETING
	= OntologyPrefix.dialogue+"SimpleGreet";
	public static final String MORNING_GREETING
	= OntologyPrefix.dialogue+"MorningGreet";
	public static final String AFTERNOON_GREETING
	= OntologyPrefix.dialogue+"AfternoonGreet";
	public static final String EVENING_GREETING
	= OntologyPrefix.dialogue+"EveningGreet";
	public static final String CALM_DOWN
	= OntologyPrefix.dialogue+"CalmDown";
	public static final String CHEER_UP
	= OntologyPrefix.dialogue+"CheerUp";
	public static final String CONSOLE
	= OntologyPrefix.dialogue+"Console";
	public static final String SHARE_JOY
	= OntologyPrefix.dialogue+"ShareJoy";
	public static final String ADRESS_EMOTION
	= OntologyPrefix.dialogue+"AdressEmotion";
	public static final String REQUEST_REASON_EMOTION
	= OntologyPrefix.dialogue+"RequestReasonForEmotion";
	public static final String SAY_GOODBYE
	= OntologyPrefix.dialogue+"SayGoodbye";
	public static final String MEET_AGAIN
	= OntologyPrefix.dialogue+"MeetAgainSayGoodbye";
	public static final String PERSONAL_GOODBYE
	= OntologyPrefix.dialogue+"PersonalSayGoodbye";
	public static final String SIMPLE_GOODBYE
	= OntologyPrefix.dialogue+"SimpleSayGoodbye";
	public static final String MORNING_GOODBYE
	= OntologyPrefix.dialogue+"MorningSayGoodbye";
	public static final String AFTERNOON_GOODBYE
	= OntologyPrefix.dialogue+"AfternoonSayGoodbye";
	public static final String EVENING_GOODBYE
	= OntologyPrefix.dialogue+"EveningSayGoodby";
	public static final String WEEKEND_GOODBYE
	= OntologyPrefix.dialogue+"WeekendSayGoodbye";

	public static final String INTRODUCE= OntologyPrefix.dialogue+"Introduce";
	public static final String REQUESTINTRO= OntologyPrefix.dialogue+"RequestIntro";
	public static final String THANK= OntologyPrefix.dialogue+"Thank";
	public static final String PERSONAL_THANK= OntologyPrefix.dialogue+"PersonalThank";
	public static final String SIMPLE_THANK= OntologyPrefix.dialogue+"SimpleThank";
	public static final String ANSWER_THANK= OntologyPrefix.dialogue+"AnswerThank";
	public static final String PERSONAL_ANSWER_THANK= OntologyPrefix.dialogue+"PersonalAnswerThank";
	public static final String REQUEST=OntologyPrefix.dialogue+"Request";
	public static final String SHOW_WEATHER=OntologyPrefix.dialogue+"ShowWeather";
	public static final String CANNED=OntologyPrefix.dialogue+"Canned";
	public static final String SIMPLE_APOLOGISE=OntologyPrefix.dialogue+"SimpleApologise";
	public static final String PERSONAL_APOLOGISE=OntologyPrefix.dialogue+"PersonalApologise";
	public static final String ASK_MOOD=OntologyPrefix.dialogue+"AskMood";
	public static final String ASK_TASK=OntologyPrefix.dialogue+"AskTask";
	public static final String ASK_FURTHER_TASK=OntologyPrefix.dialogue+"AskTaskFollowUp";
	public static final String SIMPLE_MOTIVATE=OntologyPrefix.dialogue+"SimpleMotivate";
	public static final String PERSONAL_MOTIVATE=OntologyPrefix.dialogue+"PersonalMotivate";
	public static final String EMPTY=OntologyPrefix.dialogue+"Empty";
	public static final String INCOMPREHENSIBLE=OntologyPrefix.dialogue+"Incomprehensible";
	public static final String UNKNOWN=OntologyPrefix.dialogue+"Unknown";
	public static final String FURTHER_INFORMATION=OntologyPrefix.dialogue+"RequestFurtherInformation";
	public static final String BOOL_REQUEST=OntologyPrefix.dialogue+"BooleanRequest";
	public static final String IR_RESPONSE=OntologyPrefix.dialogue+"IRResponse";
	public static final String REQUEST_FEEDBACK=OntologyPrefix.dialogue+"RequestFeedback";
	public static final String UNKNOWN_STATEMENT=OntologyPrefix.dialogue+"UnknownStatement";
	public static final String UNKNOWN_REQUEST=OntologyPrefix.dialogue+"UnknownRequest";
	public static final String ADDITIONAL_INFORMATION=OntologyPrefix.dialogue+"AdditionalInformation";
	public static final String PROACTIVE_LIST=OntologyPrefix.dialogue+"ProactiveList";
	public static final String PROACTIVE_CANNED=OntologyPrefix.dialogue+"ProactiveCanned";
	public static final String NOT_FOUND=OntologyPrefix.dialogue+"NotFound";
	
	//for KI input
	public static final String REQUEST_WEATHER=OntologyPrefix.dialogue+"RequestWeather";
	public static final String REQUEST_NEWSPAPER=OntologyPrefix.dialogue+"RequestNewspaper";
	public static final String REQUEST_ARTICLES=OntologyPrefix.dialogue+"RequestArticles";
	public static final String REQUEST_LOCAL_EVENT=OntologyPrefix.dialogue+"RequestLocalEvent";
	public static final String REQUEST_SOCIAL_MEDIA=OntologyPrefix.dialogue+"RequestSocialMedia";
	public static final String REQUEST_RECIPE=OntologyPrefix.dialogue+"RequestRecipe";
	public static final String REQUEST_INFO_SLEEP=OntologyPrefix.dialogue+"RequestInfoSleep";
	public static final String REQUEST_INFO_SLEEP_HYGIENE=OntologyPrefix.dialogue+"RequestInfoSleepHygiene";
	public static final String REQUEST_INFO_DEMENTIA=OntologyPrefix.dialogue+"RequestInfoDementia";
	public static final String REQUEST_INFO_DIABETES=OntologyPrefix.dialogue+"RequestInfoDiabetes";
	public static final String REQUEST_CLOSEST_PARKS=OntologyPrefix.dialogue+"RequestClosestParks";
	public static final String REQUEST_ACTIVITIES_BABY=OntologyPrefix.dialogue+"RequestActivitiesBaby";
	public static final String REQUEST_INFO_PROTECTION_BABY=OntologyPrefix.dialogue+"RequestInfoProtectionBaby";
	public static final String REQUEST_CLOSEST_HEALTH_CENTER=OntologyPrefix.dialogue+"RequestClosestHealthCenter";
	public static final String REQUEST_APPOINTMENT=OntologyPrefix.dialogue+"Appointment";
	
	public static final String REQUEST_REPLAY=OntologyPrefix.dialogue+"RequestReplay";
	public static final String THANK_VIDEO=OntologyPrefix.dialogue+"ThankVideo";
}