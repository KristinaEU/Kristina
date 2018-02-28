package owlSpeak.quality;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
//import java.text.SimpleDateFormat;
//import java.util.Date;

public class QualityParameter {

	private final boolean doPrintout = false;
	private Map<String, Boolean> useFeature;

	private int windowSize = 3;

	// private static final String String = null;
	private ArrayList<String> speech = new ArrayList<String>();
	private ArrayList<Double> timeSt = new ArrayList<Double>();
	private ArrayList<Double> confidences = new ArrayList<Double>();
	private ArrayList<String> systemprompts = new ArrayList<String>();
	private ArrayList<String> myevent = new ArrayList<String>();
	private ArrayList<String> bargeins = new ArrayList<String>();
	private ArrayList<String> modalityList = new ArrayList<String>();
	private ArrayList<String> activityList = new ArrayList<String>();
	private ArrayList<String> myconfirm = new ArrayList<String>();
	private ArrayList<Double> userTurnDurationList = new ArrayList<Double>();

	private double baseTimeStamp;

	private boolean isFirstRequest;

	public QualityParameter() {
		// init();
		initUserSimulator();
	}

	private void init() {
		isFirstRequest = true;
		useFeature = new HashMap<String, Boolean>();

		// exchange level
		useFeature.put("ASRRecognitionStatus", false);
		useFeature.put("ASRConfidence", false);
		useFeature.put("Barged-in?", false);
		useFeature.put("Modality", false);
		useFeature.put("ExMo", false); // always false; not implemented
		useFeature.put("UnExMo?", false); // always false; not implemented
		useFeature.put("GrammarNames", false); // always false; not implemented
		useFeature.put("TriggeredGrammar", false); // always false; not
													// implemented
		useFeature.put("Utterance", false); // always false; not implemented
		useFeature.put("WPUT", false);
		useFeature.put("UTD", true);
		useFeature.put("SemanticParse", false); // always false; not implemented
		useFeature.put("HelpRequest?", false);
		useFeature.put("OperatorRequest", false); // always false; not
													// implemented
		useFeature.put("Activity", false); // always false; not implemented
		useFeature.put("ActivityType", false);
		useFeature.put("Prompt", false); // always false; not implemented
		useFeature.put("WPST", false);
		useFeature.put("RePrompt?", true);
		useFeature.put("Confirmation?", false);
		useFeature.put("TurnNumber", false);
		useFeature.put("DD", true);

		// dialogue level
		useFeature.put("MeanASRConfidence", true);
		useFeature.put("#ASRSuccess", true);
		useFeature.put("%ASRSuccess", true);
		useFeature.put("#ASRRejections", true);
		useFeature.put("%ASRRejections", true);
		useFeature.put("#Time-OutPrompts", true);
		useFeature.put("%Time-OutPrompts", true);
		useFeature.put("#BargeIns", false);
		useFeature.put("%BargeIns", false);
		useFeature.put("#UnExMo", false); // always false; not implemented
		useFeature.put("%UnExmo", false); // always false; not implemented

		// window level
		useFeature.put("{Mean}ASRConfidence", true);
		useFeature.put("{#}ASRSuccess", true);
		useFeature.put("{#}ASRRejections", true);
		useFeature.put("{#}TimeOutPrompts", false);
		useFeature.put("{#}Barge-Ins", false);
		useFeature.put("{#}UnExMo", false); // always false; not implemented
		useFeature.put("{#}HelpRequest", false);
		useFeature.put("{#}OperatorRequests", false); // always false; not
														// implemented
		useFeature.put("{#}RePrompts", true);
		useFeature.put("{#}Confirmation", false);
		useFeature.put("{#}SystemQuestions", true);
	}

	private void initUserSimulator() {
		isFirstRequest = true;
		useFeature = new HashMap<String, Boolean>();

		// exchange level
		useFeature.put("ASRRecognitionStatus", true);
		useFeature.put("ASRConfidence", true);
		useFeature.put("Barged-in?", false);
		useFeature.put("Modality", false);
		useFeature.put("ExMo", false); // always false; not implemented
		useFeature.put("UnExMo?", false); // always false; not implemented
		useFeature.put("GrammarNames", false); // always false; not implemented
		useFeature.put("TriggeredGrammar", false); // always false; not
													// implemented
		useFeature.put("Utterance", false); // always false; not implemented
		useFeature.put("WPUT", false);
		useFeature.put("UTD", false);
		useFeature.put("SemanticParse", false); // always false; not implemented
		useFeature.put("HelpRequest?", false);
		useFeature.put("OperatorRequest", false); // always false; not
													// implemented
		useFeature.put("Activity", false); // always false; not implemented
		useFeature.put("ActivityType", true);
		useFeature.put("Prompt", false); // always false; not implemented
		useFeature.put("WPST", false);
		useFeature.put("RePrompt?", true);
		useFeature.put("Confirmation?", true);
		useFeature.put("TurnNumber", true);
		useFeature.put("DD", false);

		// dialogue level
		useFeature.put("MeanASRConfidence", true);
		useFeature.put("#ASRSuccess", true);
		useFeature.put("%ASRSuccess", true);
		useFeature.put("#ASRRejections", true);
		useFeature.put("%ASRRejections", true);
		useFeature.put("#Time-OutPrompts", false);
		useFeature.put("%Time-OutPrompts", false);
		useFeature.put("#Time-OutRejections", false); // always false; not
														// implemented
		useFeature.put("%Time-OutRejections", false); // always false; not
														// implemented
		useFeature.put("#BargeIns", false);
		useFeature.put("%BargeIns", false);
		useFeature.put("#UnExMo", false); // always false; not implemented
		useFeature.put("%UnExmo", false); // always false; not implemented
		useFeature.put("#RePrompts", true);
		useFeature.put("%RePrompts", true);
		useFeature.put("#SystemQuestions", true);

		// window level
		useFeature.put("{Mean}ASRConfidence", true);
		useFeature.put("{#}ASRSuccess", true);
		useFeature.put("{#}ASRRejections", true);
		useFeature.put("{#}TimeOutPrompts", false);
		useFeature.put("{#}Time-OutRejections", false); // always false; not
														// implemented
		useFeature.put("{#}Barge-Ins", false);
		useFeature.put("{#}UnExMo", false); // always false; not implemented
		useFeature.put("{#}HelpRequest", false);
		useFeature.put("{#}OperatorRequests", false); // always false; not
														// implemented
		useFeature.put("{#}RePrompts", true);
		useFeature.put("{#}Confirmation", false);
		useFeature.put("{#}SystemQuestions", true);
	}

	/**
	 * 
	 * @param speak
	 *            The word sequence returned from ASR
	 * @param event
	 *            The event returned from vxml
	 * @param timeStamp
	 *            The timeStamp of the current turn
	 * @param confidence
	 *            ASR confidence
	 * @param systemutterance
	 *            Word sequence uttered by the system
	 * @param bargein
	 *            Has a barge-in occured?
	 * @param modality
	 *            Modality of input
	 * @param interpretation
	 *            Semantic interpretation of input.
	 * @param activityType
	 *            Type of system action
	 * @param confirmation
	 *            Has the system asked for a confirmation?
	 * @param userTurnDuration
	 *            Duration of the input audio
	 * @throws ParseException
	 */
	public void update(String speak, String event, Double timeStamp,
			Double confidence, String systemutterance, String bargein,
			String modality, String interpretation, String activityType,
			String confirmation, double userTurnDuration) throws ParseException {

		// add parameters and do computing of remaining parameters

		timeSt.add(timeStamp);
		if (speak != null)
			speak = speak.trim();
		speech.add(speak);
		confidences.add(confidence);
		systemprompts.add(systemutterance);
		myevent.add(event);
		bargeins.add(bargein);
		modalityList.add(modality);
		activityList.add(activityType);
		myconfirm.add(confirmation);
		userTurnDurationList.add(userTurnDuration);

		// wpst(systemutterance);
		// wput(speak);
		//
		// asrRecogStatus(event, speak);
		// repromptToDouble(event);
		// helpToDouble(event);
		// dialogDuration();
		turnNumber();
		eventToDouble(event);
		// utteranceTurnDuration(duration);
		meanAsrConfidence_dialog();
		asrSuccess_dialog();
		asrSuccessRatio_dialog();
		asrRejections_dialog();
		asrRejectionsRatio_dialog();
		// timeOutPrompts_dialog();
		// timeOutPromptsRatio_dialog();
		// bargeIns_dialog();
		// bargeInsRatio_dialog();

		// asrSuccess();
		// meanAsrConfidence();
		// rePrompt();

		meanAsrConfidence_window();
		asrSuccess_window();
		asrRejections_window();
		// timeOutPrompts_window();
		// helpRequests_window();
		rePrompt_window();
		// bargeIns_window();
		// systemQuestions_window();

	}

	public double[] getFeatureVector() {

		/*
		 * feature construction for libsvm classification
		 * 
		 * features are:
		 * 
		 * exchange level: 0: ASRRecognitionStatus 1: ASRConfidence 2:
		 * Barged-in? 3: Modality 4: ExMo 5: UnExMo? 6: GrammarNames 7:
		 * TriggeredGrammar 8: Utterance 9: WPUT 10: UTD 11: SemanticParse 12:
		 * HelpRequest? 13: OperatorRequest 14: Activity 15: ActivityType 16:
		 * Prompt 17: WPST 18: RePrompt? 19: Confirmation? 20: TurnNumber 21: DD
		 * 
		 * dialogue level: 22: MeanASRConfidence 23: #ASRSuccess 24: %ASRSuccess
		 * 25: #ASRRejections 26: %ASRRejections 27: #Time-OutPrompts 28:
		 * %Time-OutPrompts 29: #BargeIns 30: %BargeIns 31: #UnExMo 32: %UnExmo
		 * 
		 * window level: 33: {Mean}ASRConfidence 34: {#}ASRSuccess 35:
		 * {#}ASRRejections 36: {#}TimeOutPrompts 37: {#}Barge-Ins 38: {#}UnExMo
		 * 39: {#}HelpRequest 40: {#}OperatorRequests 41: {#}RePrompts 42:
		 * {#}Confirmation 43: {#}SystemQuestions
		 * 
		 * Order of features has to correspond to sql query in perl script for
		 * generating training file.
		 */

		Vector<Double> features = new Vector<Double>();

		// ASRRecognitionStatus
		if (useFeature.get("ASRRecognitionStatus"))
			features.add(eventToDouble(myevent.get(myevent.size() - 1)));

		// ASRConfidence
		if (useFeature.get("ASRConfidence"))
			features.add(confidences.get(confidences.size() - 1));

		// Barged-in?
		if (useFeature.get("Barged-in?"))
			features.add(bargeinToDouble(bargeins.get(bargeins.size() - 1)));

		// Modality
		if (useFeature.get("Modality"))
			features.add(modalityToDouble(modalityList.get(modalityList.size() - 1)));

		// ExMo
		if (useFeature.get("ExMo"))
			features.add(null);

		// UnExMo?
		if (useFeature.get("UnExMo?"))
			features.add(null);

		// GrammarNames
		if (useFeature.get("GrammarNames"))
			features.add(null);

		// TriggeredGrammar
		if (useFeature.get("TriggeredGrammar"))
			features.add(null);

		// Utterance
		if (useFeature.get("Utterance"))
			features.add(null);

		// WPUT
		if (useFeature.get("WPUT"))
			features.add(wput(speech.get(speech.size() - 1)));

		// UTD
		if (useFeature.get("UTD"))
			features.add(userTurnDurationList.get(userTurnDurationList.size() - 1));

		// SemanticParse
		if (useFeature.get("SemanticParse"))
			features.add(null);

		// HelpRequest?
		if (useFeature.get("HelpRequest?"))
			features.add(helpToDouble(myevent.get(myevent.size() - 1)));

		// OperatorRequest
		if (useFeature.get("OperatorRequest"))
			features.add(null);

		// Activity
		if (useFeature.get("Activity"))
			features.add(null);

		// ActivityType
		if (useFeature.get("ActivityType"))
			features.add(activityTypeToDouble(activityList.get(activityList
					.size() - 1)));

		// Prompt
		if (useFeature.get("Prompt"))
			features.add(null);

		// WPST
		if (useFeature.get("WPST"))
			features.add(wpst(systemprompts.get(systemprompts.size() - 1)));

		// RePrompt?
		if (useFeature.get("RePrompt?"))
			features.add(repromptToDouble(myevent.get(myevent.size() - 1)));

		// Confirmation?
		if (useFeature.get("Confirmation?"))
			features.add(confirmationToDouble(myconfirm.get(myconfirm.size() - 1)));

		// TurnNumber
		if (useFeature.get("TurnNumber"))
			features.add(turnNumber());

		// DD
		if (useFeature.get("DD"))
			features.add(dialogDuration());

		// MeanASRConfidence
		if (useFeature.get("MeanASRConfidence"))
			features.add(meanAsrConfidence_dialog());

		// #ASRSuccess
		if (useFeature.get("#ASRSuccess"))
			features.add(asrSuccess_dialog());

		// %ASRSuccess
		if (useFeature.get("%ASRSuccess"))
			features.add(asrSuccessRatio_dialog());

		// #ASRRejections
		if (useFeature.get("#ASRRejections"))
			features.add(asrRejections_dialog());

		// %ASRRejections
		if (useFeature.get("%ASRRejections"))
			features.add(asrRejectionsRatio_dialog());

		// #Time-OutPrompts
		if (useFeature.get("#Time-OutPrompts"))
			features.add(timeOutPrompts_dialog());

		// %Time-OutPrompts
		if (useFeature.get("%Time-OutPrompts"))
			features.add(timeOutPromptsRatio_dialog());

		// #BargeIns
		if (useFeature.get("#BargeIns"))
			features.add(bargeIns_dialog());

		// %BargeIns
		if (useFeature.get("%BargeIns"))
			features.add(bargeInsRatio_dialog());

		// #UnExMo
		if (useFeature.get("#UnExMo"))
			features.add(null);

		// %UnExmo
		if (useFeature.get("%UnExmo"))
			features.add(null);
		
		// #RePrompts
		if (useFeature.get("#RePrompts"))
			features.add(rePrompts_dialog());
			
		// %RePrompts
		if (useFeature.get("%RePrompts"))
			features.add(rePromptsRatio_dialog());
		
		// #SystemQuestion
		if (useFeature.get("#SystemQuestions"))
			features.add(systemQuestions_dialog());

		// {Mean}ASRConfidence
		if (useFeature.get("{Mean}ASRConfidence"))
			features.add(meanAsrConfidence_window());

		// {#}ASRSuccess
		if (useFeature.get("{#}ASRSuccess"))
			features.add(asrSuccess_window());

		// {#}ASRRejections
		if (useFeature.get("{#}ASRRejections"))
			features.add(asrRejections_window());

		// {#}TimeOutPrompts
		if (useFeature.get("{#}TimeOutPrompts"))
			features.add(timeOutPrompts_window());

		// {#}Barge-Ins
		if (useFeature.get("{#}Barge-Ins"))
			features.add(bargeIns_window());

		// {#}UnExMo
		if (useFeature.get("{#}UnExMo"))
			features.add(null);

		// {#}HelpRequest
		if (useFeature.get("{#}HelpRequest"))
			features.add(helpRequests_window());

		// {#}OperatorRequests
		if (useFeature.get("{#}OperatorRequests"))
			features.add(null);

		// {#}RePrompts
		if (useFeature.get("{#}RePrompts"))
			features.add(rePrompt_window());

		// {#}Confirmation
		if (useFeature.get("{#}Confirmation"))
			features.add(confirmation_window());

		// {#}SystemQuestions
		if (useFeature.get("{#}SystemQuestions"))
			features.add(systemQuestions_window());

		double[] featureVector = new double[features.size()];

		int i = 0;
		for (Double d : features) {
			featureVector[i++] = d;
		}

		return featureVector;
	}

	/**
	 * Returns a String array which contains all parameters in a readable form.
	 * 
	 * @return A string array with all parameters as string values.
	 */
	public String[] getStringVector() {
		Vector<String> features = new Vector<String>();

		// ASRRecognitionStatus
		if (useFeature.get("ASRRecognitionStatus"))
			features.add(myevent.get(myevent.size() - 1));

		// ASRConfidence
		if (useFeature.get("ASRConfidence"))
			features.add(Double.toString(confidences.get(confidences.size() - 1)));

		// Barged-in?
		if (useFeature.get("Barged-in?"))
			features.add(bargeins.get(bargeins.size() - 1));

		// Modality
		if (useFeature.get("Modality"))
			features.add(modalityList.get(modalityList.size() - 1));

		// ExMo
		if (useFeature.get("ExMo"))
			features.add(null);

		// UnExMo?
		if (useFeature.get("UnExMo?"))
			features.add(null);

		// GrammarNames
		if (useFeature.get("GrammarNames"))
			features.add(null);

		// TriggeredGrammar
		if (useFeature.get("TriggeredGrammar"))
			features.add(null);

		// Utterance
		if (useFeature.get("Utterance"))
			features.add(speech.get(speech.size() - 1));

		// WPUT
		if (useFeature.get("WPUT"))
			features.add(Double.toString(wput(speech.get(speech.size() - 1))));

		// UTD
		if (useFeature.get("UTD"))
			features.add(Double.toString(userTurnDurationList
					.get(userTurnDurationList.size() - 1)));

		// SemanticParse
		if (useFeature.get("SemanticParse"))
			features.add(null);

		// HelpRequest?
		if (useFeature.get("HelpRequest?"))
			features.add(help(myevent.get(myevent.size() - 1)));

		// OperatorRequest
		if (useFeature.get("OperatorRequest"))
			features.add(null);

		// Activity
		if (useFeature.get("Activity"))
			features.add(null);

		// ActivityType
		if (useFeature.get("ActivityType"))
			features.add(activityList.get(activityList.size() - 1));

		// Prompt
		if (useFeature.get("Prompt"))
			features.add(systemprompts.get(systemprompts.size() - 1));

		// WPST
		if (useFeature.get("WPST"))
			features.add(Double.toString(wpst(systemprompts.get(systemprompts
					.size() - 1))));

		// RePrompt?
		if (useFeature.get("RePrompt?"))
			features.add(reprompt(myevent.get(myevent.size() - 1)));

		// Confirmation?
		if (useFeature.get("Confirmation?"))
			features.add(myconfirm.get(myconfirm.size() - 1));

		// TurnNumber
		if (useFeature.get("TurnNumber"))
			features.add(Double.toString(turnNumber()));

		// DD
		if (useFeature.get("DD"))
			features.add(Double.toString(dialogDuration()));

		// MeanASRConfidence
		if (useFeature.get("MeanASRConfidence"))
			features.add(Double.toString(meanAsrConfidence_dialog()));

		// #ASRSuccess
		if (useFeature.get("#ASRSuccess"))
			features.add(Double.toString(asrSuccess_dialog()));

		// %ASRSuccess
		if (useFeature.get("%ASRSuccess"))
			features.add(Double.toString(asrSuccessRatio_dialog()));

		// #ASRRejections
		if (useFeature.get("#ASRRejections"))
			features.add(Double.toString(asrRejections_dialog()));

		// %ASRRejections
		if (useFeature.get("%ASRRejections"))
			features.add(Double.toString(asrRejectionsRatio_dialog()));

		// #Time-OutPrompts
		if (useFeature.get("#Time-OutPrompts"))
			features.add(Double.toString(timeOutPrompts_dialog()));

		// %Time-OutPrompts
		if (useFeature.get("%Time-OutPrompts"))
			features.add(Double.toString(timeOutPromptsRatio_dialog()));

		// #BargeIns
		if (useFeature.get("#BargeIns"))
			features.add(Double.toString(bargeIns_dialog()));

		// %BargeIns
		if (useFeature.get("%BargeIns"))
			features.add(Double.toString(bargeInsRatio_dialog()));

		// #UnExMo
		if (useFeature.get("#UnExMo"))
			features.add(null);

		// %UnExmo
		if (useFeature.get("%UnExmo"))
			features.add(null);
		
		// #RePrompts
		if (useFeature.get("#RePrompts"))
			features.add(Double.toString(rePrompts_dialog()));
			
		// %RePrompts
		if (useFeature.get("%RePrompts"))
			features.add(Double.toString(rePromptsRatio_dialog()));
		
		// #SystemQuestion
		if (useFeature.get("#SystemQuestion"))
			features.add(Double.toString(systemQuestions_dialog()));

		// {Mean}ASRConfidence
		if (useFeature.get("{Mean}ASRConfidence"))
			features.add(Double.toString(meanAsrConfidence_window()));

		// {#}ASRSuccess
		if (useFeature.get("{#}ASRSuccess"))
			features.add(Double.toString(asrSuccess_window()));

		// {#}ASRRejections
		if (useFeature.get("{#}ASRRejections"))
			features.add(Double.toString(asrRejections_window()));

		// {#}TimeOutPrompts
		if (useFeature.get("{#}TimeOutPrompts"))
			features.add(Double.toString(timeOutPrompts_window()));

		// {#}Barge-Ins
		if (useFeature.get("{#}Barge-Ins"))
			features.add(Double.toString(bargeIns_window()));

		// {#}UnExMo
		if (useFeature.get("{#}UnExMo"))
			features.add(null);

		// {#}HelpRequest
		if (useFeature.get("{#}HelpRequest"))
			features.add(Double.toString(helpRequests_window()));

		// {#}OperatorRequests
		if (useFeature.get("{#}OperatorRequests"))
			features.add(null);

		// {#}RePrompts
		if (useFeature.get("{#}RePrompts"))
			features.add(Double.toString(rePrompt_window()));

		// {#}Confirmation
		if (useFeature.get("{#}Confirmation"))
			features.add(Double.toString(confirmation_window()));

		// {#}SystemQuestions
		if (useFeature.get("{#}SystemQuestions"))
			features.add(Double.toString(systemQuestions_window()));

		String[] stringVector = new String[features.size()];

		int i = 0;
		for (String d : features) {
			stringVector[i++] = d;
		}

		return stringVector;
	}

	/**
	 * Creates a String array containing the parameters for insertion into a
	 * database. All parameter values have the same format as the original Let's
	 * Go.
	 * 
	 * @return array for inserting parameters into data base
	 */
	public String[] getDBStringVector() {
		Vector<String> features = new Vector<String>();

		// ASRRecognitionStatus
		features.add(myevent.get(myevent.size() - 1));

		// ASRConfidence
		features.add(Double.toString(confidences.get(confidences.size() - 1)));

		// Barged-in?
		features.add(Double.toString(bargeinToDouble(bargeins.get(bargeins
				.size() - 1))));

		// Modality
		features.add(modalityList.get(modalityList.size() - 1));

		// ExMo
		// features.add(null);

		// UnExMo?
		// features.add(null);

		// GrammarNames
		// features.add(null);

		// TriggeredGrammar
		// features.add(null);

		// Utterance
		features.add(speech.get(speech.size() - 1));

		// WPUT
		features.add(Double.toString(wput(speech.get(speech.size() - 1))));

		// UTD
		features.add(Double.toString(userTurnDurationList
				.get(userTurnDurationList.size() - 1)));

		// SemanticParse
		// features.add(null);

		// HelpRequest?
		// features.add(help(myevent.get(myevent.size()-1))) ;

		// OperatorRequest
		// features.add(null);

		// Activity
		// features.add(null);

		// ActivityType
		features.add(activityList.get(activityList.size() - 1));

		// Prompt
		features.add(systemprompts.get(systemprompts.size() - 1));

		// WPST
		features.add(Double.toString(wpst(systemprompts.get(systemprompts
				.size() - 1))));

		// RePrompt?
		features.add(Double.toString(repromptToDouble(myevent.get(myevent
				.size() - 1))));

		// Confirmation?
		features.add(Double.toString(confirmationToDouble(myconfirm
				.get(myconfirm.size() - 1))));

		// TurnNumber
		features.add(Double.toString(turnNumber()));

		// DD
		features.add(Double.toString(dialogDuration()));

		// MeanASRConfidence
		features.add(Double.toString(meanAsrConfidence_dialog()));

		// #ASRSuccess
		features.add(Double.toString(asrSuccess_dialog()));

		// %ASRSuccess
		features.add(Double.toString(asrSuccessRatio_dialog()));

		// #ASRRejections
		features.add(Double.toString(asrRejections_dialog()));

		// %ASRRejections
		features.add(Double.toString(asrRejectionsRatio_dialog()));

		// #Time-OutPrompts
		features.add(Double.toString(timeOutPrompts_dialog()));

		// %Time-OutPrompts
		features.add(Double.toString(timeOutPromptsRatio_dialog()));

		// #BargeIns
		features.add(Double.toString(bargeIns_dialog()));

		// %BargeIns
		features.add(Double.toString(bargeInsRatio_dialog()));

		// #UnExMo
		// features.add(null);

		// %UnExmo
		// features.add(null);
		
		

		// {Mean}ASRConfidence
		features.add(Double.toString(meanAsrConfidence_window()));

		// {#}ASRSuccess
		features.add(Double.toString(asrSuccess_window()));

		// {#}ASRRejections
		features.add(Double.toString(asrRejections_window()));

		// {#}TimeOutPrompts
		features.add(Double.toString(timeOutPrompts_window()));

		// {#}Barge-Ins
		features.add(Double.toString(bargeIns_window()));

		// {#}UnExMo
		// features.add(null);

		// {#}HelpRequest
		// features.add(Double.toString(helpRequests_window()));

		// {#}OperatorRequests
		// features.add(null);

		// {#}RePrompts
		features.add(Double.toString(rePrompt_window()));

		// {#}Confirmation
		features.add(Double.toString(confirmation_window()));

		// {#}SystemQuestions
		features.add(Double.toString(systemQuestions_window()));

		String[] stringVector = new String[features.size()];

		int i = 0;
		for (String d : features) {
			stringVector[i++] = d;
		}

		return stringVector;
	}

	public void setBaseTimeStamp(double timeStamp1) {
		baseTimeStamp = timeStamp1;
		if (doPrintout)
			System.out.println("baseTimeStamp is: " + baseTimeStamp);
	}

	/**
	 * @return the isFirstRequest
	 */
	public boolean isFirstRequest() {
		return isFirstRequest;
	}

	/**
	 * @param isFirstRequest
	 *            the isFirstRequest to set
	 */
	public void setFirstRequest(boolean isFirstRequest) {
		this.isFirstRequest = isFirstRequest;
	}

	public boolean appendToFile(String file) {
		boolean success = false;
		BufferedWriter b = null;
		try {
			b = new BufferedWriter(new FileWriter(file, true));
			b.append(this.toString() + "\n");
			success = true;
		} catch (IOException e) {
			System.err.println("Problem with file access.");
			e.printStackTrace();
		} finally {
			if (b != null)
				try {
					b.close();
				} catch (IOException e) {
					System.err.println("Problem with file access.");
					e.printStackTrace();
				}
		}
		return success;
	}

	@Override
	public String toString() {
		String s = new String();

		boolean first = true;
		for (String d : getStringVector()) {
			if (first) {
				s += d;
				first = false;
			} else
				s += ";" + d;
		}
		return s;

	}

	public static String[] getHeader() {
		Vector<String> headers = new Vector<String>();

		// ASRRecognitionStatus
		headers.add("ASRRecognitionStatus");

		// ASRConfidence
		headers.add("ASRConfidence");

		// Barged-in?
		headers.add("Barged-in?");

		// Modality
		headers.add("Modality");

		// ExMo
		// headers.add("ExMo");

		// UnExMo?
		// headers.add("UnExMo?");

		// GrammarNames
		// headers.add("GrammarNames");

		// TriggeredGrammar
		// headers.add("TriggeredGrammar");

		// Utterance
		headers.add("Utterance");

		// WPUT
		headers.add("WPUT");

		// UTD
		headers.add("UTD");

		// SemanticParse
		// headers.add("SemanticParse");

		// HelpRequest?
		// headers.add("HelpRequest?");

		// OperatorRequest
		// headers.add("OperatorRequest");

		// Activity
		// headers.add("Activity");

		// ActivityType
		headers.add("ActivityType");

		// Prompt
		headers.add("Prompt");

		// WPST
		headers.add("WPST");

		// RePrompt?
		headers.add("RePrompt?");

		// Confirmation?
		headers.add("Confirmation?");

		// TurnNumber
		headers.add("TurnNumber");

		// DD
		headers.add("DD");

		// MeanASRConfidence
		headers.add("MeanASRConfidence");

		// #ASRSuccess
		headers.add("#ASRSuccess");

		// %ASRSuccess
		headers.add("%ASRSuccess");

		// #ASRRejections
		headers.add("#ASRRejections");

		// %ASRRejections
		headers.add("%ASRRejections");

		// #Time-OutPrompts
		headers.add("#Time-OutPrompts");

		// %Time-OutPrompts
		headers.add("%Time-OutPrompts");

		// #BargeIns
		headers.add("#BargeIns");

		// %BargeIns
		headers.add("%BargeIns");

		// #UnExMo
		// headers.add("#UnExMo");

		// %UnExmo
		// headers.add("%UnExmo");

		// {Mean}ASRConfidence
		headers.add("{Mean}ASRConfidence");

		// {#}ASRSuccess
		headers.add("{#}ASRSuccess");

		// {#}ASRRejections
		headers.add("{#}ASRRejections");

		// {#}TimeOutPrompts
		headers.add("{#}TimeOutPrompts");

		// {#}Barge-Ins
		headers.add("{#}Barge-Ins");

		// {#}UnExMo
		// headers.add("{#}UnExMo");

		// {#}HelpRequest
		// headers.add("{#}HelpRequest");

		// {#}OperatorRequests
		// headers.add("{#}OperatorRequests");

		// {#}RePrompts
		headers.add("{#}RePrompts");

		// {#}Confirmation
		headers.add("{#}Confirmation");

		// {#}SystemQuestions
		headers.add("{#}SystemQuestions");

		String[] stringVector = new String[headers.size()];

		int i = 0;
		for (String d : headers) {
			stringVector[i++] = d;
		}

		return stringVector;
	}

	public static void main(String args[]) throws ParseException {
		// IF U WANNA TEST IT THEN CREATE AN OBJECT OF THE "Myclass" AND CALL
		// "object.update()" method
		String speak = "ich will nach   hause";
		String event = "";
		double timeStamp1 = 0.4;
		double confidence2 = 0.5;
		String bargein = "";
		String modality = "";
		String activityT = "";
		String confirmation2 = "";
		String systemutterance = "Hello      my dear old    friends";
		String interpretation = "";
		double duration2 = 0.3;
		QualityParameter testObject = new QualityParameter();

		testObject.update(speak, event, timeStamp1, confidence2,
				systemutterance, bargein, modality, interpretation, activityT,
				confirmation2, duration2);
	}

	/**
	 * <ul>
	 * <li>null: 0</li>
	 * <li>no input: 1</li>
	 * <li>no match: 2</li>
	 * <li>success: 3</li>
	 * </ul>
	 * 
	 * @param event
	 * @return
	 */

	private double eventToDouble(String event) {

		double x = 0;

		if ("noinput".equalsIgnoreCase(event)) {
			x = 1;
		}

		else if ("nomatch".equalsIgnoreCase(event)) {
			x = 2;
		}

		else if ("success".equalsIgnoreCase(event)) {
			x = 3;
		}

		if (doPrintout)
			System.out.println("event2double is : " + x);

		return x;
	}

	/**
	 * null => 0 false => 0 true => 1
	 * 
	 * @param bargein
	 * @return
	 */
	private double bargeinToDouble(String bargein) {

		double x = 0;

		if ("true".equalsIgnoreCase(bargein)) {
			x = 1;
		}
		return x;
	}

	/**
	 * voice => 1 dtmf => 2 null => 0
	 * 
	 * @param modality
	 * @return
	 */

	private double modalityToDouble(String modality) {

		double x = 0;
		if ("voice".equalsIgnoreCase(modality))
			x = 1;
		else if ("dtmf".equalsIgnoreCase(modality))
			x = 2;
		else if (modality == null)
			x = 0;

		return x;

	}

	private double wput(String speak) {
		double x = 1;

		if (speak != null) {

			int z;
			for (z = 0; z < speak.length(); z++)
				if (!Character.isWhitespace(speak.charAt(z)))
					break;

			for (int i = z; i < speak.length(); i++) {

				if (Character.isWhitespace(speak.charAt(i)))
					if (!Character.isWhitespace(speak.charAt(i + 1)))
						x = x + 1;

			}
		} else
			x = 0;

		if (doPrintout)
			System.out.println("The number of words in user's speech is: " + x);

		return x;

	}

	private String help(String event) {

		String x = "no_help";

		if ("help".equalsIgnoreCase(event)) {
			x = "help";

		}
		return x;
	}

	/**
	 * help => 1 notHelp => 0
	 * 
	 * @param event
	 * @return
	 */
	private double helpToDouble(String event) {

		double x = 0;

		if ("help".equalsIgnoreCase(event)) {
			x = 1;
			if (doPrintout)
				System.out.println("HELP");
		}
		return x;
	}

	/**
	 * question => 1 announcement => 2 wait_for_user_feedback => 3 confirmation
	 * => 4
	 * 
	 * @param activityT
	 * @return
	 */
	private double activityTypeToDouble(String activityT) {

		double x = 0;

		if ("question".equalsIgnoreCase(activityT))
			x = 1;
		else if ("announcement".equalsIgnoreCase(activityT))
			x = 2;
		else if ("wait".equalsIgnoreCase(activityT))
			x = 3;
		else if ("confirmation".equalsIgnoreCase(activityT))
			x = 4;
		return x;
	}

	private double wpst(String systemutterance) {
		double y = 1;

		if (systemutterance != null) {

			int z;
			for (z = 0; z < systemutterance.length(); z++)
				if (!Character.isWhitespace(systemutterance.charAt(z)))
					break;

			for (int i = z; i < systemutterance.length(); i++) {

				if (Character.isWhitespace(systemutterance.charAt(i)))
					if (!Character.isWhitespace(systemutterance.charAt(i + 1)))
						y = y + 1;

			}

		}

		else
			y = 0;
		if (doPrintout)
			System.out
					.println("The number of words in the utterence of the system is: "
							+ y);
		return y;
	}

	/**
	 * reprompt => 1 notReprompt => 0
	 * 
	 * @param event
	 * @return
	 */
	private double repromptToDouble(String event) {

		double x = 0;

		if ("nomatch".equalsIgnoreCase(event)
				|| "noinput".equalsIgnoreCase(event)
				|| "help".equalsIgnoreCase(event)) {

			if (doPrintout)
				System.out.println("REPROMPT");
			x = 1;
		} else
			x = 0;
		if (doPrintout)
			System.out.println("var:" + x);
		return x;

	}

	private String reprompt(String event) {

		String x = "no_reprompt";

		if ("nomatch".equalsIgnoreCase(event)
				|| "noinput".equalsIgnoreCase(event)
				|| "help".equalsIgnoreCase(event)) {

			if (doPrintout)
				System.out.println("REPROMPT");
			x = "reprompt";
		}
		return x;

	}

	/**
	 * confirmation => 1 notConfirm => 0
	 * 
	 * @param confirmation2
	 * @return
	 */
	private double confirmationToDouble(String confirmation2) {

		double x = 0;

		if ("confirm".equalsIgnoreCase(confirmation2))
			x = 1;

		return x;
	}

	private double dialogDuration() { // throws ParseException

		// SimpleDateFormat dateFormat = new SimpleDateFormat("mm ss S");

		double diff = 0;

		if (timeSt.size() > 0) {

			double a = baseTimeStamp;
			double b = timeSt.get(timeSt.size() - 1);

			diff = (double) b - (double) a;
			;

			if (doPrintout) {
				System.out.println("x: " + a);
				System.out.println("y: " + b);
				System.out.println("Dialogue duration up to this point: "
						+ diff);
			}
		}
		return diff;

	}

	private double turnNumber() {

		double turn;
		turn = systemprompts.size();
		if (doPrintout)
			System.out.println("Turn number of the system is: " + turn);
		return turn;
	}

	private double meanAsrConfidence_window() {
		double result = 0;

		int divisor = Integer.min(windowSize, confidences.size());

		for (int i = 1; i <= divisor; i++) {
			Double t = confidences.get(confidences.size() - i);
			if (t != null)
				result += t;
		}

		result /= (double) divisor;

		if (doPrintout)
			System.out.println("meanAsrConfidence_window is: " + result);

		return result;
	}

	private double asrSuccess_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, myevent.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("success".equalsIgnoreCase(myevent.get(myevent.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("asrSuccesses for the window is: " + counter);

		return counter;

	}

	private double asrRejections_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, myevent.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("nomatch".equalsIgnoreCase(myevent.get(myevent.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("asrRejections for the window is: " + counter);

		return counter;
	}

	private double timeOutPrompts_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, myevent.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("noinput".equalsIgnoreCase(myevent.get(myevent.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("timeOutPrompts for the window is: " + counter);

		return counter;
	}

	private double bargeIns_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, bargeins.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("true".equalsIgnoreCase(bargeins.get(bargeins.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("baregeIns for the window is: " + counter);

		return counter;
	}

	private double helpRequests_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, myevent.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("help".equalsIgnoreCase(myevent.get(myevent.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("helpRequests for the window is: " + counter);

		return counter;
	}

	/**
	 * if the event is different than success and null then it must be
	 * reprompted
	 * 
	 * @return
	 */
	private double rePrompt_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, myevent.size());

		for (int i = 1; i <= currentSize; i++) {
			String evt = myevent.get(myevent.size() - i);
			if ("nomatch".equalsIgnoreCase(evt)
					|| "noinput".equalsIgnoreCase(evt)
					|| "help".equalsIgnoreCase(evt))
				counter++;
		}

		if (doPrintout)
			System.out.println("rePrompts for the window is: " + counter);

		return counter;
	}

	private double confirmation_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, myconfirm.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("confirm".equalsIgnoreCase(myconfirm.get(myconfirm.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("confirmation for the window is: " + counter);

		return counter;
	}

	private double systemQuestions_window() {

		double counter = 0;

		int currentSize = Integer.min(windowSize, activityList.size());

		for (int i = 1; i <= currentSize; i++) {
			if ("question".equalsIgnoreCase(activityList.get(activityList
					.size() - i)))
				counter++;
		}

		if (doPrintout)
			System.out.println("Number of questions  for the window is: "
					+ counter);

		return counter;
	}

	// /////////////////////////////DIALOG LEVEL
	// BELOW!!!!!!!!!!!!!!!!!!!!!!!///////////

	private double meanAsrConfidence_dialog() {

		double result = 0;
		double total = 0;

		for (int i = 0; i < confidences.size(); i++) {

			total = total + confidences.get(i);

		}
		result = total / confidences.size();
		if (doPrintout)
			System.out.println("meanAsrConfidence_dialog is: " + result);
		return result;
	}

	private double asrSuccess_dialog() {

		double total = 0;

		for (int i = 0; i < myevent.size(); i++) {

			if ("success".equalsIgnoreCase(myevent.get(i)))
				total = total + 1;

		}
		if (doPrintout)
			System.out.println("asrSuccess_dialog is: " + total);

		return total;

	}

	private double asrSuccessRatio_dialog() {

		double total = 0;
		double result = 0;
		for (int i = 0; i < myevent.size(); i++) {

			if ("success".equalsIgnoreCase(myevent.get(i)))
				total = total + 1;

		}
		result = total / myevent.size();
		if (doPrintout)
			System.out.println("asrSuccessRatio_dialog is: " + result);

		return result;

	}

	private double asrRejections_dialog() {

		double total = 0;

		for (int i = 0; i < myevent.size(); i++) {

			if ("nomatch".equalsIgnoreCase(myevent.get(i)))
				total = total + 1;

		}
		if (doPrintout)
			System.out.println("asrRejections_dialog is: " + total);
		return total;
	}

	private double asrRejectionsRatio_dialog() {

		double total = 0;
		double result = 0;

		for (int i = 0; i < myevent.size(); i++) {

			if ("nomatch".equalsIgnoreCase(myevent.get(i)))
				total = total + 1;

		}
		result = total / myevent.size();
		if (doPrintout)
			System.out.println("asrRejectionsRatio_dialog is: " + result);

		return result;
	}

	private double timeOutPrompts_dialog() {

		double total = 0;

		for (int i = 0; i < myevent.size(); i++) {

			if ("noinput".equalsIgnoreCase(myevent.get(i)))
				total = total + 1;

		}
		if (doPrintout)
			System.out.println("timeOutPrompts_dialog is: " + total);

		return total;
	}

	private double timeOutPromptsRatio_dialog() {

		double total = 0;
		double result = 0;

		for (int i = 0; i < myevent.size(); i++) {

			if ("noinput".equalsIgnoreCase(myevent.get(i)))
				total = total + 1;

		}
		result = total / myevent.size();
		if (doPrintout)
			System.out.println("timeOutPromptsRatio_dialog is: " + result);

		return result;
	}

	private double bargeIns_dialog() {

		double total = 0;

		for (int i = 0; i < bargeins.size(); i++) {

			if ("true".equalsIgnoreCase(bargeins.get(i)))

				total = total + 1;

		}
		if (doPrintout)
			System.out.println("bargeIns_dialog is: " + total);

		return total;
	}

	private double bargeInsRatio_dialog() {

		double total = 0;
		double result = 0;

		for (int i = 0; i < bargeins.size(); i++) {

			if ("true".equalsIgnoreCase(bargeins.get(i)))
				total = total + 1;

		}
		result = total / bargeins.size();
		if (doPrintout)
			System.out.println("bargeInsRatio_dialog is: " + result);

		return result;
	}

	private double rePrompts_dialog() {

		double total = 0;

		for (int i = 0; i < myevent.size(); i++) {

			String evt = myevent.get(i);
			if ("nomatch".equalsIgnoreCase(evt)
					|| "noinput".equalsIgnoreCase(evt)
					|| "help".equalsIgnoreCase(evt))
				total = total + 1;

		}
		if (doPrintout)
			System.out.println("rePrompts_dialog is: " + total);

		return total;

	}

	private double rePromptsRatio_dialog() {

		double total = 0;
		double result = 0;
		for (int i = 0; i < myevent.size(); i++) {

			String evt = myevent.get(i);
			if ("nomatch".equalsIgnoreCase(evt)
					|| "noinput".equalsIgnoreCase(evt)
					|| "help".equalsIgnoreCase(evt))
				total = total + 1;

		}
		result = total / myevent.size();
		if (doPrintout)
			System.out.println("rePromptsRatio_dialog is: " + result);

		return result;

	}
	
	private double systemQuestions_dialog() {

		double total = 0;

		for (int i = 0; i < myevent.size(); i++) {

			if ("question".equalsIgnoreCase(activityList.get(i)))
				total = total + 1;

		}
		if (doPrintout)
			System.out.println("systemQuestions_dialog is: " + total);

		return total;

	}
}
