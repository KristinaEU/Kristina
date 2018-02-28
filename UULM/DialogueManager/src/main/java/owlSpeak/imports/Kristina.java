package owlSpeak.imports;

import java.io.FileNotFoundException;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.OSFactory;
import owlSpeak.SemanticGroup;
import owlSpeak.SummaryAgenda;
import owlSpeak.Variable;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.DialogueAction;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.kristina.KristinaMove;

/**
 * @author Juliana Miehle
 */
public class Kristina {
	
	public static void main(String[] argv) throws Exception {
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
        @SuppressWarnings("unused")
		ServletEngine engine = new ServletEngine();
        String uriSave = Settings.uri;
        Settings.uri = "http://localhost:8083/OwlSpeakOnto.owl";
        String filename = "kristina.owl";
        String path = Settings.homePath;
        OSFactory factory = null;
        try {
            factory = OwlSpeakOntology.createOSFactoryEmptyOnto(filename, path);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        
        // create SemanticGroups and Variables
        SemanticGroup semGroupWeather = factory.createSemanticGroup("semGroup_Weather");
		Variable varWeather = factory.createVariable("var_Weather");
		semGroupWeather.addVariable(varWeather);
		varWeather.addBelongsToSemantic(semGroupWeather);
        
        SemanticGroup semGroupOffer = factory.createSemanticGroup("semGroup_Offer");
		Variable varOffer = factory.createVariable("var_Offer");
		semGroupOffer.addVariable(varOffer);
		varOffer.addBelongsToSemantic(semGroupOffer);
		
		SemanticGroup semGroupScenario = factory.createSemanticGroup("semGroup_Scenario");
		Variable varScenario = factory.createVariable("var_Scenario");
		semGroupScenario.addVariable(varScenario);
		varScenario.addBelongsToSemantic(semGroupScenario);
		
		SemanticGroup semGroupUserCulture = factory.createSemanticGroup("semGroup_UserCulture");
		Variable varUserCulture = factory.createVariable("var_UserCulture");
		semGroupUserCulture.addVariable(varUserCulture);
		varUserCulture.addBelongsToSemantic(semGroupUserCulture);
        
		SemanticGroup semGroupUserEmotion = factory.createSemanticGroup("semGroup_UserEmotion");
		Variable varUserValence = factory.createVariable("var_UserValence");
		varUserValence.setDefaultValue("", "0.0");
		semGroupUserEmotion.addVariable(varUserValence);
		varUserValence.addBelongsToSemantic(semGroupUserEmotion);
		Variable varUserArousal = factory.createVariable("var_UserArousal");
		varUserArousal.setDefaultValue("", "0.0");
		semGroupUserEmotion.addVariable(varUserArousal);
		varUserArousal.addBelongsToSemantic(semGroupUserEmotion);
		
		SemanticGroup semGroupSystemEmotion = factory.createSemanticGroup("semGroup_SystemEmotion");
		Variable varSystemValence = factory.createVariable("var_SystemValence");
		varSystemValence.setDefaultValue("", "0.0");
		semGroupSystemEmotion.addVariable(varSystemValence);
		varSystemValence.addBelongsToSemantic(semGroupSystemEmotion);
		Variable varSystemArousal = factory.createVariable("var_SystemArousal");
		varSystemArousal.setDefaultValue("", "0.0");
		semGroupSystemEmotion.addVariable(varSystemArousal);
		varSystemArousal.addBelongsToSemantic(semGroupSystemEmotion);
		
		SemanticGroup semGroupSystemCommunicationStyle = factory.createSemanticGroup("semGroup_SystemCommunicationStyle");
		Variable varSystemDirectness = factory.createVariable("var_SystemDirectness");
		varSystemDirectness.setDefaultValue("", "3");
		semGroupSystemCommunicationStyle.addVariable(varSystemDirectness);
		varSystemDirectness.addBelongsToSemantic(semGroupSystemCommunicationStyle);
		Variable varSystemVerbosity = factory.createVariable("var_SystemVerbosity");
		varSystemVerbosity.setDefaultValue("", "3");
		semGroupSystemCommunicationStyle.addVariable(varSystemVerbosity);
		varSystemVerbosity.addBelongsToSemantic(semGroupSystemCommunicationStyle);
		Variable varSystemIsBelief = factory.createVariable("var_SystemIsBelief");
		varSystemIsBelief.setDefaultValue("", "0");
		semGroupSystemCommunicationStyle.addVariable(varSystemIsBelief);
		varSystemIsBelief.addBelongsToSemantic(semGroupSystemCommunicationStyle);
		Variable varSystemIsAdvice = factory.createVariable("var_SystemIsAdvice");
		varSystemIsAdvice.setDefaultValue("", "0");
		semGroupSystemCommunicationStyle.addVariable(varSystemIsAdvice);
		varSystemIsAdvice.addBelongsToSemantic(semGroupSystemCommunicationStyle);
		Variable varSystemIsFormal = factory.createVariable("var_SystemIsFormal");
		varSystemIsFormal.setDefaultValue("", "0");
		semGroupSystemCommunicationStyle.addVariable(varSystemIsFormal);
		varSystemIsFormal.addBelongsToSemantic(semGroupSystemCommunicationStyle);
        
        // create SummaryAgenda (for OwlSpeak reasons)
      	SummaryAgenda sumAgenda = factory.createSummaryAgenda("sumAgenda");
      	sumAgenda.setType(sumAgenda.getTypeString(), "announcement");
      	sumAgenda.setRole(sumAgenda.getRole(), "collection");
        
        // create dummyUserMove (with dummyGrammar for OwlSpeak reasons) -> each Agenda containing it will wait for some user input
        KristinaMove dummyUserMove = factory.createKristinaMove("dummyUserMove");
        Grammar dummyGrammar = factory.createGrammar("dummyGrammar");
        dummyUserMove.setGrammar(null, dummyGrammar);

        // Masteragenda
        Agenda agendaMaster = factory.createAgenda("agenda_Master");
		agendaMaster.setIsMasterBool(false, true);
		
		// Startagenda -> selected by the system waiting for the user to start the conversation
		KristinaAgenda agendaStart = factory.createKristinaAgenda("agenda_Start");
        agendaStart.addHas(dummyUserMove);
        agendaMaster.addNext(agendaStart);
        agendaStart.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaStart);
      	
      	// Additional Agenda which can be selected if nothing should be uttered
      	KristinaAgenda agendaEmpty = factory.createKristinaAgenda("agenda_Empty");
		agendaStart.addNext(agendaEmpty);
		agendaEmpty.addNext(agendaEmpty);
		agendaEmpty.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaEmpty);
      	agendaEmpty.addDialogueAction(IRI.create(DialogueAction.EMPTY));

		// A) Independent Actions
		// 1) Apologise
      	KristinaAgenda agendaSimpleApologise = factory.createKristinaAgenda("agenda_SimpleApologise");
		agendaSimpleApologise.addHas(dummyUserMove);
		agendaStart.addNext(agendaSimpleApologise);
		agendaSimpleApologise.addNext(agendaSimpleApologise);
		agendaSimpleApologise.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaSimpleApologise);
      	agendaSimpleApologise.addDialogueAction(IRI.create(DialogueAction.SIMPLE_APOLOGISE));

      	KristinaAgenda agendaPersonalApologise = factory.createKristinaAgenda("agenda_PersonalApologise");
		agendaPersonalApologise.addHas(dummyUserMove);
		agendaStart.addNext(agendaPersonalApologise);
		agendaPersonalApologise.addNext(agendaPersonalApologise);
		agendaPersonalApologise.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaPersonalApologise);
      	agendaPersonalApologise.addDialogueAction(IRI.create(DialogueAction.PERSONAL_APOLOGISE));
		
		// 2) Clarify
		KristinaAgenda agendaRequestRepeat = factory.createKristinaAgenda("agenda_RequestRepeat");
		agendaRequestRepeat.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestRepeat);
		agendaRequestRepeat.addNext(agendaRequestRepeat);
		agendaRequestRepeat.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaRequestRepeat);
      	agendaRequestRepeat.addDialogueAction(IRI.create(DialogueAction.REPEAT)); 

		KristinaAgenda agendaRequestRephrase = factory.createKristinaAgenda("agenda_RequestRephrase");
		agendaRequestRephrase.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestRephrase);
		agendaRequestRephrase.addNext(agendaRequestRephrase);
		agendaRequestRephrase.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaRequestRephrase);
      	agendaRequestRephrase.addDialogueAction(IRI.create(DialogueAction.REPHRASE)); 
		
		KristinaAgenda agendaStateMissingComprehension = factory.createKristinaAgenda("agenda_StateMissingComprehension");
		agendaStateMissingComprehension.addHas(dummyUserMove);
		agendaStart.addNext(agendaStateMissingComprehension);
		agendaStateMissingComprehension.addNext(agendaStateMissingComprehension);
		agendaStateMissingComprehension.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaStateMissingComprehension);
      	agendaStateMissingComprehension.addDialogueAction(IRI.create(DialogueAction.MISSING_COMPREHENSION));
		
		// 3) StartConversation
		KristinaAgenda agendaAskMood = factory.createKristinaAgenda("agenda_AskMood");
		agendaAskMood.addHas(dummyUserMove);
		agendaStart.addNext(agendaAskMood);
		agendaAskMood.addNext(agendaAskMood);
		agendaAskMood.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAskMood);
      	agendaAskMood.addDialogueAction(IRI.create(DialogueAction.ASK_MOOD));
		
//		KristinaAgenda agendaAskPlans = factory.createKristinaAgenda("agenda_AskPlans");
//		agendaAskPlans.addHas(dummyUserMove);
//		agendaStart.addNext(agendaAskPlans);
//		agendaAskPlans.addNext(agendaAskPlans);
//		agendaAskPlans.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaAskPlans);
		
		KristinaAgenda agendaAskTask = factory.createKristinaAgenda("agenda_AskTask");
		agendaAskTask.addHas(dummyUserMove);
		agendaStart.addNext(agendaAskTask);
		agendaAskTask.addNext(agendaAskTask);
		agendaAskTask.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAskTask);
      	agendaAskTask.addDialogueAction(IRI.create(DialogueAction.ASK_TASK));
      	
      	KristinaAgenda agendaAskTaskFollowUp = factory.createKristinaAgenda("agenda_AskTaskFollowUp");
      	agendaAskTaskFollowUp.addHas(dummyUserMove);
		agendaStart.addNext(agendaAskTaskFollowUp);
		agendaAskTaskFollowUp.addNext(agendaAskTaskFollowUp);
		agendaAskTaskFollowUp.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAskTaskFollowUp);
      	agendaAskTaskFollowUp.addDialogueAction(IRI.create(DialogueAction.ASK_FURTHER_TASK));
		
//		KristinaAgenda agendaAskTaskSuccess = factory.createKristinaAgenda("agenda_AskTaskSuccess");
//		agendaAskTaskSuccess.addHas(dummyUserMove);
//		agendaStart.addNext(agendaAskTaskSuccess);
//		agendaAskTaskSuccess.addNext(agendaAskTaskSuccess);
//		agendaAskTaskSuccess.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaAskTaskSuccess);
		
//		KristinaAgenda agendaAskWellBeing = factory.createKristinaAgenda("agenda_AskWellBeing");
//		agendaAskWellBeing.addHas(dummyUserMove);
//		agendaStart.addNext(agendaAskWellBeing);
//		agendaAskWellBeing.addNext(agendaAskWellBeing);
//		agendaAskWellBeing.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaAskWellBeing);
		
		// 4) EmotionalSystemActions
//		KristinaAgenda agendaAdressEmotion = factory.createKristinaAgenda("agenda_AdressEmotion");
//		agendaAdressEmotion.addHas(dummyUserMove);
//		agendaStart.addNext(agendaAdressEmotion);
//		agendaAdressEmotion.addNext(agendaAdressEmotion);
//		agendaAdressEmotion.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaAdressEmotion);
		
//		KristinaAgenda agendaRequestPresenceEmotion = factory.createKristinaAgenda("agenda_RequestPresenceEmotion");
//		agendaRequestPresenceEmotion.addHas(dummyUserMove);
//		agendaStart.addNext(agendaRequestPresenceEmotion);
//		agendaRequestPresenceEmotion.addNext(agendaRequestPresenceEmotion);
//		agendaRequestPresenceEmotion.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaRequestPresenceEmotion);
		
//		KristinaAgenda agendaRequestReasonForEmotion = factory.createKristinaAgenda("agenda_RequestReasonForEmotion");
//		agendaRequestReasonForEmotion.addHas(dummyUserMove);
//		agendaStart.addNext(agendaRequestReasonForEmotion);
//		agendaRequestReasonForEmotion.addNext(agendaRequestReasonForEmotion);
//		agendaRequestReasonForEmotion.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaRequestReasonForEmotion);
		
		KristinaAgenda agendaCalmDown = factory.createKristinaAgenda("agenda_CalmDown");
		agendaCalmDown.addHas(dummyUserMove);
		agendaStart.addNext(agendaCalmDown);
		agendaCalmDown.addNext(agendaCalmDown);
		agendaCalmDown.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaCalmDown);
      	agendaCalmDown.addDialogueAction(IRI.create(DialogueAction.CALM_DOWN));
		
		KristinaAgenda agendaCheerUp = factory.createKristinaAgenda("agenda_CheerUp");
		agendaCheerUp.addHas(dummyUserMove);
		agendaStart.addNext(agendaCheerUp);
		agendaCheerUp.addNext(agendaCheerUp);
		agendaCheerUp.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaCheerUp);
      	agendaCheerUp.addDialogueAction(IRI.create(DialogueAction.CHEER_UP));
		
		KristinaAgenda agendaConsole = factory.createKristinaAgenda("agenda_Console");
		agendaConsole.addHas(dummyUserMove);
		agendaStart.addNext(agendaConsole);
		agendaConsole.addNext(agendaConsole);
		agendaConsole.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaConsole);
      	agendaConsole.addDialogueAction(IRI.create(DialogueAction.CONSOLE));
		
		KristinaAgenda agendaShareJoy = factory.createKristinaAgenda("agenda_ShareJoy");
		agendaShareJoy.addHas(dummyUserMove);
		agendaStart.addNext(agendaShareJoy);
		agendaShareJoy.addNext(agendaShareJoy);
		agendaShareJoy.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaShareJoy);
      	agendaShareJoy.addDialogueAction(IRI.create(DialogueAction.SHARE_JOY));
		
		// 5) Greet
		KristinaAgenda agendaSimpleGreet = factory.createKristinaAgenda("agenda_SimpleGreet");
		agendaSimpleGreet.addHas(dummyUserMove);
		agendaStart.addNext(agendaSimpleGreet);
		agendaSimpleGreet.addNext(agendaSimpleGreet);
		agendaSimpleGreet.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaSimpleGreet);
      	agendaSimpleGreet.addDialogueAction(IRI.create(DialogueAction.SIMPLE_GREETING));
		
		KristinaAgenda agendaPersonalGreet = factory.createKristinaAgenda("agenda_PersonalGreet");
		agendaPersonalGreet.addHas(dummyUserMove);
		agendaStart.addNext(agendaPersonalGreet);
		agendaPersonalGreet.addNext(agendaPersonalGreet);
		agendaPersonalGreet.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaPersonalGreet);
      	agendaPersonalGreet.addDialogueAction(IRI.create(DialogueAction.PERSONAL_GREETING));
		
		KristinaAgenda agendaMorningGreet = factory.createKristinaAgenda("agenda_MorningGreet");
		agendaMorningGreet.addHas(dummyUserMove);
		agendaStart.addNext(agendaMorningGreet);
		agendaMorningGreet.addNext(agendaMorningGreet);
		agendaMorningGreet.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaMorningGreet);
      	agendaMorningGreet.addDialogueAction(IRI.create(DialogueAction.MORNING_GREETING));
		
		KristinaAgenda agendaAfternoonGreet = factory.createKristinaAgenda("agenda_AfternoonGreet");
		agendaAfternoonGreet.addHas(dummyUserMove);
		agendaStart.addNext(agendaAfternoonGreet);
		agendaAfternoonGreet.addNext(agendaAfternoonGreet);
		agendaAfternoonGreet.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAfternoonGreet);
      	agendaAfternoonGreet.addDialogueAction(IRI.create(DialogueAction.AFTERNOON_GREETING));
		
		KristinaAgenda agendaEveningGreet = factory.createKristinaAgenda("agenda_EveningGreet");
		agendaEveningGreet.addHas(dummyUserMove);
		agendaStart.addNext(agendaEveningGreet);
		agendaEveningGreet.addNext(agendaEveningGreet);
		agendaEveningGreet.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaEveningGreet);
      	agendaEveningGreet.addDialogueAction(IRI.create(DialogueAction.EVENING_GREETING));
		
		// 6) SayGoodbye
		KristinaAgenda agendaSimpleSayGoodbye = factory.createKristinaAgenda("agenda_SimpleSayGoodbye");
		agendaSimpleSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaSimpleSayGoodbye);
		agendaSimpleSayGoodbye.addNext(agendaSimpleSayGoodbye);
		agendaSimpleSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaSimpleSayGoodbye);
      	agendaSimpleSayGoodbye.addDialogueAction(IRI.create(DialogueAction.SIMPLE_GOODBYE));
		
		KristinaAgenda agendaPersonalSayGoodbye = factory.createKristinaAgenda("agenda_PersonalSayGoodbye");
		agendaPersonalSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaPersonalSayGoodbye);
		agendaPersonalSayGoodbye.addNext(agendaPersonalSayGoodbye);
		agendaPersonalSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaPersonalSayGoodbye);
      	agendaPersonalSayGoodbye.addDialogueAction(IRI.create(DialogueAction.PERSONAL_GOODBYE));
		
		KristinaAgenda agendaMorningSayGoodbye = factory.createKristinaAgenda("agenda_MorningSayGoodbye");
		agendaMorningSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaMorningSayGoodbye);
		agendaMorningSayGoodbye.addNext(agendaMorningSayGoodbye);
		agendaMorningSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaMorningSayGoodbye);
      	agendaMorningSayGoodbye.addDialogueAction(IRI.create(DialogueAction.MORNING_GOODBYE));
		
		KristinaAgenda agendaAfternoonSayGoodbye = factory.createKristinaAgenda("agenda_AfternoonSayGoodbye");
		agendaAfternoonSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaAfternoonSayGoodbye);
		agendaAfternoonSayGoodbye.addNext(agendaAfternoonSayGoodbye);
		agendaAfternoonSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAfternoonSayGoodbye);
      	agendaAfternoonSayGoodbye.addDialogueAction(IRI.create(DialogueAction.AFTERNOON_GOODBYE));
		
		KristinaAgenda agendaEveningSayGoodbye = factory.createKristinaAgenda("agenda_EveningSayGoodbye");
		agendaEveningSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaEveningSayGoodbye);
		agendaEveningSayGoodbye.addNext(agendaEveningSayGoodbye);
		agendaEveningSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaEveningSayGoodbye);
      	agendaEveningSayGoodbye.addDialogueAction(IRI.create(DialogueAction.EVENING_GOODBYE));
		
		KristinaAgenda agendaWeekendSayGoodbye = factory.createKristinaAgenda("agenda_WeekendSayGoodbye");
		agendaWeekendSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaWeekendSayGoodbye);
		agendaWeekendSayGoodbye.addNext(agendaWeekendSayGoodbye);
		agendaWeekendSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaWeekendSayGoodbye);
      	agendaWeekendSayGoodbye.addDialogueAction(IRI.create(DialogueAction.WEEKEND_GOODBYE));
		
		KristinaAgenda agendaMeetAgainSayGoodbye = factory.createKristinaAgenda("agenda_MeetAgainSayGoodbye");
		agendaMeetAgainSayGoodbye.addHas(dummyUserMove);
		agendaStart.addNext(agendaMeetAgainSayGoodbye);
		agendaMeetAgainSayGoodbye.addNext(agendaMeetAgainSayGoodbye);
		agendaMeetAgainSayGoodbye.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaMeetAgainSayGoodbye);
      	agendaMeetAgainSayGoodbye.addDialogueAction(IRI.create(DialogueAction.MEET_AGAIN));
		
		// 7) Motivate
		KristinaAgenda agendaSimpleMotivate = factory.createKristinaAgenda("agenda_SimpleMotivate");
		agendaSimpleMotivate.addHas(dummyUserMove);
		agendaStart.addNext(agendaSimpleMotivate);
		agendaSimpleMotivate.addNext(agendaSimpleMotivate);
		agendaSimpleMotivate.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaSimpleMotivate);
      	agendaSimpleMotivate.addDialogueAction(IRI.create(DialogueAction.SIMPLE_MOTIVATE));
		
		KristinaAgenda agendaPersonalMotivate = factory.createKristinaAgenda("agenda_PersonalMotivate");
		agendaPersonalMotivate.addHas(dummyUserMove);
		agendaStart.addNext(agendaPersonalMotivate);
		agendaPersonalMotivate.addNext(agendaPersonalMotivate);
		agendaPersonalMotivate.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaPersonalMotivate);
      	agendaPersonalMotivate.addDialogueAction(IRI.create(DialogueAction.PERSONAL_MOTIVATE));
		
//		KristinaAgenda agendaGroupOrientedMotivate = factory.createKristinaAgenda("agenda_GroupOrientedMotivate");
//		agendaGroupOrientedMotivate.addHas(dummyUserMove);
//		agendaStart.addNext(agendaGroupOrientedMotivate);
//		agendaGroupOrientedMotivate.addNext(agendaGroupOrientedMotivate);
//		agendaGroupOrientedMotivate.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaGroupOrientedMotivate);
		
//		KristinaAgenda agendaIndividuisticallyOrientedMotivate = factory.createKristinaAgenda("agenda_IndividuisticallyOrientedMotivate");
//		agendaIndividuisticallyOrientedMotivate.addHas(dummyUserMove);
//		agendaStart.addNext(agendaIndividuisticallyOrientedMotivate);
//		agendaIndividuisticallyOrientedMotivate.addNext(agendaIndividuisticallyOrientedMotivate);
//		agendaIndividuisticallyOrientedMotivate.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaIndividuisticallyOrientedMotivate);
		
		// 8) Thank
		KristinaAgenda agendaSimpleThank = factory.createKristinaAgenda("agenda_SimpleThank");
		agendaSimpleThank.addHas(dummyUserMove);
		agendaStart.addNext(agendaSimpleThank);
		agendaSimpleThank.addNext(agendaSimpleThank);
		agendaSimpleThank.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaSimpleThank);
      	agendaSimpleThank.addDialogueAction(IRI.create(DialogueAction.SIMPLE_THANK));
		
		KristinaAgenda agendaPersonalThank = factory.createKristinaAgenda("agenda_PersonalThank");
		agendaPersonalThank.addHas(dummyUserMove);
		agendaStart.addNext(agendaPersonalThank);
		agendaPersonalThank.addNext(agendaPersonalThank);
		agendaPersonalThank.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaPersonalThank);
      	agendaPersonalThank.addDialogueAction(IRI.create(DialogueAction.PERSONAL_THANK));
		
		KristinaAgenda agendaAnswerThank = factory.createKristinaAgenda("agenda_AnswerThank");
		agendaAnswerThank.addHas(dummyUserMove);
		agendaStart.addNext(agendaAnswerThank);
		agendaAnswerThank.addNext(agendaAnswerThank);
		agendaAnswerThank.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAnswerThank);
      	agendaAnswerThank.addDialogueAction(IRI.create(DialogueAction.ANSWER_THANK));
		
		KristinaAgenda agendaPersonalAnswerThank = factory.createKristinaAgenda("agenda_PersonalAnswerThank");
		agendaPersonalAnswerThank.addHas(dummyUserMove);
		agendaStart.addNext(agendaPersonalAnswerThank);
		agendaPersonalAnswerThank.addNext(agendaPersonalAnswerThank);
		agendaPersonalAnswerThank.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaPersonalAnswerThank);
      	agendaPersonalAnswerThank.addDialogueAction(IRI.create(DialogueAction.PERSONAL_ANSWER_THANK));
		
		// B) Dependent Actions
		// 1) Request
//		KristinaAgenda agendaRequestAdditionalInformation = factory.createKristinaAgenda("agenda_RequestAdditionalInformation");
//		agendaRequestAdditionalInformation.addHas(dummyUserMove);
//		agendaStart.addNext(agendaRequestAdditionalInformation);
//		agendaRequestAdditionalInformation.addNext(agendaRequestAdditionalInformation);
//		agendaRequestAdditionalInformation.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaRequestAdditionalInformation);
		
//		KristinaAgenda agendaRequestMissingInformation = factory.createKristinaAgenda("agenda_RequestMissingInformation");
//		agendaRequestMissingInformation.addHas(dummyUserMove);
//		agendaStart.addNext(agendaRequestMissingInformation);
//		agendaRequestMissingInformation.addNext(agendaRequestMissingInformation);
//		agendaRequestMissingInformation.addSummaryAgenda(sumAgenda);
//		sumAgenda.addSummarizedAgenda(agendaRequestMissingInformation);
		
		// 2) Clarify
//		KristinaAgenda agendaExplicitlyConfirmRecognisedUserinput = factory.createKristinaAgenda("agenda_ExplicitlyConfirmRecognisedUserinput");
//		agendaExplicitlyConfirmRecognisedUserinput.addHas(dummyUserMove);
//		agendaStart.addNext(agendaExplicitlyConfirmRecognisedUserinput);
//		agendaExplicitlyConfirmRecognisedUserinput.addNext(agendaExplicitlyConfirmRecognisedUserinput);
//		agendaExplicitlyConfirmRecognisedUserinput.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaExplicitlyConfirmRecognisedUserinput);
		
//		KristinaAgenda agendaImplicitlyConfirmRecognisedUserinput = factory.createKristinaAgenda("agenda_ImplicitlyConfirmRecognisedUserinput");
//		agendaImplicitlyConfirmRecognisedUserinput.addHas(dummyUserMove);
//		agendaStart.addNext(agendaImplicitlyConfirmRecognisedUserinput);
//		agendaImplicitlyConfirmRecognisedUserinput.addNext(agendaImplicitlyConfirmRecognisedUserinput);
//		agendaImplicitlyConfirmRecognisedUserinput.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaImplicitlyConfirmRecognisedUserinput);
		
//		KristinaAgenda agendaRepeatPreviousSystemutterance = factory.createKristinaAgenda("agenda_RepeatPreviousSystemutterance");
//		agendaRepeatPreviousSystemutterance.addHas(dummyUserMove);
//		agendaStart.addNext(agendaRepeatPreviousSystemutterance);
//		agendaRepeatPreviousSystemutterance.addNext(agendaRepeatPreviousSystemutterance);
//		agendaRepeatPreviousSystemutterance.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaRepeatPreviousSystemutterance);
		
//		KristinaAgenda agendaRephrasePreviousSystemutterance = factory.createKristinaAgenda("agenda_RephrasePreviousSystemutterance");
//		agendaRephrasePreviousSystemutterance.addHas(dummyUserMove);
//		agendaStart.addNext(agendaRephrasePreviousSystemutterance);
//		agendaRephrasePreviousSystemutterance.addNext(agendaRephrasePreviousSystemutterance);
//		agendaRephrasePreviousSystemutterance.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaRephrasePreviousSystemutterance);
		
		// 3) Statement
      	
      	KristinaAgenda agendaAffirm = factory.createKristinaAgenda("agenda_Affirm");
      	agendaAffirm.addHas(dummyUserMove);
		agendaStart.addNext(agendaAffirm);
		agendaAffirm.addNext(agendaAffirm);
		agendaAffirm.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAffirm);
      	agendaAffirm.addDialogueAction(IRI.create(DialogueAction.AFFIRM));
      	
		KristinaAgenda agendaAccept = factory.createKristinaAgenda("agenda_Accept");
		agendaAccept.addHas(dummyUserMove);
		agendaStart.addNext(agendaAccept);
		agendaAccept.addNext(agendaAccept);
		agendaAccept.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAccept);
      	agendaAccept.addDialogueAction(IRI.create(DialogueAction.ACCEPT));
		
		KristinaAgenda agendaUnknownStatement = factory.createKristinaAgenda("agenda_UnknownStatement");
		agendaUnknownStatement.addHas(dummyUserMove);
		agendaStart.addNext(agendaUnknownStatement);
		agendaUnknownStatement.addNext(agendaUnknownStatement);
		agendaUnknownStatement.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaUnknownStatement);
      	agendaUnknownStatement.addDialogueAction(IRI.create(DialogueAction.UNKNOWN_STATEMENT));
      	
      	KristinaAgenda agendaNotFound = factory.createKristinaAgenda("agenda_NotFound");
      	agendaNotFound.addHas(dummyUserMove);
		agendaStart.addNext(agendaNotFound);
		agendaNotFound.addNext(agendaNotFound);
		agendaNotFound.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaNotFound);
      	agendaNotFound.addDialogueAction(IRI.create(DialogueAction.NOT_FOUND));
      	
      	KristinaAgenda agendaUnknownRequest = factory.createKristinaAgenda("agenda_UnknownRequest");
		agendaUnknownRequest.addHas(dummyUserMove);
		agendaStart.addNext(agendaUnknownRequest);
		agendaUnknownRequest.addNext(agendaUnknownRequest);
		agendaUnknownRequest.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaUnknownRequest);
      	agendaUnknownRequest.addDialogueAction(IRI.create(DialogueAction.UNKNOWN_REQUEST));
      	
      	KristinaAgenda agendaReject = factory.createKristinaAgenda("agenda_Reject");
		agendaReject.addHas(dummyUserMove);
		agendaStart.addNext(agendaReject);
		agendaReject.addNext(agendaReject);
		agendaReject.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaReject);
      	agendaReject.addDialogueAction(IRI.create(DialogueAction.REJECT));
		
		KristinaAgenda agendaAcknowledge = factory.createKristinaAgenda("agenda_Acknowledge");
		agendaAcknowledge.addHas(dummyUserMove);
		agendaStart.addNext(agendaAcknowledge);
		agendaAcknowledge.addNext(agendaAcknowledge);
		agendaAcknowledge.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaAcknowledge);
      	agendaAcknowledge.addDialogueAction(IRI.create(DialogueAction.ACKNOWLEDGE));
      	
      	KristinaAgenda agendaIntroduce = factory.createKristinaAgenda("agenda_Introduce");
      	agendaIntroduce.addHas(dummyUserMove);
		agendaStart.addNext(agendaIntroduce);
		agendaIntroduce.addNext(agendaIntroduce);
		agendaIntroduce.addSummaryAgenda(sumAgenda);
      	sumAgenda.addSummarizedAgenda(agendaIntroduce);
      	agendaIntroduce.addDialogueAction(IRI.create(DialogueAction.INTRODUCE));
		
//		KristinaAgenda agendaAdvise = factory.createKristinaAgenda("agenda_Advise");
//		agendaAdvise.addHas(dummyUserMove);
//		agendaStart.addNext(agendaAdvise);
//		agendaAdvise.addNext(agendaAdvise);
//		agendaAdvise.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaAdvise);
		
//		KristinaAgenda agendaDeclare = factory.createKristinaAgenda("agenda_Declare");
//		agendaDeclare.addHas(dummyUserMove);
//		agendaStart.addNext(agendaDeclare);
//		agendaDeclare.addNext(agendaDeclare);
//		agendaDeclare.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaDeclare);
		
//		KristinaAgenda agendaObligate = factory.createKristinaAgenda("agenda_Obligate");
//		agendaObligate.addHas(dummyUserMove);
//		agendaStart.addNext(agendaObligate);
//		agendaObligate.addNext(agendaObligate);
//		agendaObligate.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaObligate);
		
//		KristinaAgenda agendaOrder = factory.createKristinaAgenda("agenda_Order");
//		agendaOrder.addHas(dummyUserMove);
//		agendaStart.addNext(agendaOrder);
//		agendaOrder.addNext(agendaOrder);
//		agendaOrder.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaOrder);
		
		// 4) Show
//		KristinaAgenda agendaReadNewspaper = factory.createKristinaAgenda("agenda_ReadNewspaper");
//		agendaReadNewspaper.addHas(dummyUserMove);
//		agendaStart.addNext(agendaReadNewspaper);
//		agendaReadNewspaper.addNext(agendaReadNewspaper);
//		agendaReadNewspaper.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaReadNewspaper);
		
//		KristinaAgenda agendaShowVideo = factory.createKristinaAgenda("agenda_ShowVideo");
//		agendaShowVideo.addHas(dummyUserMove);
//		agendaStart.addNext(agendaShowVideo);
//		agendaShowVideo.addNext(agendaShowVideo);
//		agendaShowVideo.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaShowVideo);
		
//		KristinaAgenda agendaShowWheather = factory.createKristinaAgenda("agenda_ShowWheather");
//		agendaShowWheather.addHas(dummyUserMove);
//		agendaStart.addNext(agendaShowWheather);
//		agendaShowWheather.addNext(agendaShowWheather);
//		agendaShowWheather.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaShowWheather);
		
//		KristinaAgenda agendaShowWebpage = factory.createKristinaAgenda("agenda_ShowWebpage");
//		agendaShowWebpage.addHas(dummyUserMove);
//		agendaStart.addNext(agendaShowWebpage);
//		agendaShowWebpage.addNext(agendaShowWebpage);
//		agendaShowWebpage.addSummaryAgenda(sumAgenda);
//      	sumAgenda.addSummarizedAgenda(agendaShowWebpage);
      	
      	// C) Canned Text Actions
		KristinaAgenda agendaRequestReasonSad = factory.createKristinaAgenda("agenda_RequestReasonSad");
		agendaRequestReasonSad.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestReasonSad);
		agendaRequestReasonSad.addNext(agendaRequestReasonSad);
		agendaRequestReasonSad.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestReasonSad);
	   	agendaRequestReasonSad.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaRequestReasonSad.addCannedText("Why are you sad?");
	   	
	   	KristinaAgenda agendaRequestHeadlineArticle = factory.createKristinaAgenda("agenda_RequestHeadlineArticle");
	   	agendaRequestHeadlineArticle.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestHeadlineArticle);
		agendaRequestHeadlineArticle.addNext(agendaRequestHeadlineArticle);
		agendaRequestHeadlineArticle.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestHeadlineArticle);
	   	agendaRequestHeadlineArticle.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaRequestHeadlineArticle.addCannedText("Tell me the headline of the article.");
	   	
	   	KristinaAgenda agendaRequestOtherArticle = factory.createKristinaAgenda("agenda_RequestOtherArticle");
	   	agendaRequestOtherArticle.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestOtherArticle);
		agendaRequestOtherArticle.addNext(agendaRequestOtherArticle);
		agendaRequestOtherArticle.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestOtherArticle);
	   	agendaRequestOtherArticle.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestOtherArticle.addCannedText("Is there another article you would like me to read out loud?");
	   	
	   	KristinaAgenda agendaEventAnotherTime = factory.createKristinaAgenda("agenda_EventAnotherTime");
	   	agendaEventAnotherTime.addHas(dummyUserMove);
		agendaStart.addNext(agendaEventAnotherTime);
		agendaEventAnotherTime.addNext(agendaEventAnotherTime);
		agendaEventAnotherTime.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaEventAnotherTime);
	   	agendaEventAnotherTime.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaEventAnotherTime.addCannedText("Let me know if I can check for events another time.");
	   	
	   	KristinaAgenda agendaSocialAnotherTime = factory.createKristinaAgenda("agenda_SocialMediaAnotherTime");
	   	agendaSocialAnotherTime.addHas(dummyUserMove);
		agendaStart.addNext(agendaSocialAnotherTime);
		agendaSocialAnotherTime.addNext(agendaSocialAnotherTime);
		agendaSocialAnotherTime.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaSocialAnotherTime);
	   	agendaSocialAnotherTime.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaSocialAnotherTime.addCannedText("Let me know if I can check for social media news another time again.");
	   
	   	KristinaAgenda agendaNewspaperAnotherTime = factory.createKristinaAgenda("agenda_NewspaperAnotherTime");
	   	agendaNewspaperAnotherTime.addHas(dummyUserMove);
		agendaStart.addNext(agendaNewspaperAnotherTime);
		agendaNewspaperAnotherTime.addNext(agendaNewspaperAnotherTime);
		agendaNewspaperAnotherTime.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaNewspaperAnotherTime);
	   	agendaNewspaperAnotherTime.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaNewspaperAnotherTime.addCannedText("just let me know if I can read out aloud the newspaper another time again.");
	   
	   	
	   	KristinaAgenda agendaSuggestWalk = factory.createKristinaAgenda("agenda_SuggestWalk");
	   	agendaSuggestWalk.addHas(dummyUserMove);
		agendaStart.addNext(agendaSuggestWalk);
		agendaSuggestWalk.addNext(agendaSuggestWalk);
		agendaSuggestWalk.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaSuggestWalk);
	   	agendaSuggestWalk.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaSuggestWalk.addCannedText("You can go for a walk.");

	   	KristinaAgenda agendaRequestWishClosestHealthCenter = factory.createKristinaAgenda("agenda_RequestWishClosestHealthCenter");
	   	agendaRequestWishClosestHealthCenter.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishClosestHealthCenter);
		agendaRequestWishClosestHealthCenter.addNext(agendaRequestWishClosestHealthCenter);
		agendaRequestWishClosestHealthCenter.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishClosestHealthCenter);
	   	agendaRequestWishClosestHealthCenter.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishClosestHealthCenter.addCannedText("Do you want me to show you the nearest health centre?");
	   	
	   	KristinaAgenda agendaRequestWishInfoProtectionBaby = factory.createKristinaAgenda("agenda_RequestWishInfoProtectionBaby");
	   	agendaRequestWishInfoProtectionBaby.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishInfoProtectionBaby);
		agendaRequestWishInfoProtectionBaby.addNext(agendaRequestWishInfoProtectionBaby);
		agendaRequestWishInfoProtectionBaby.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishInfoProtectionBaby);
	   	agendaRequestWishInfoProtectionBaby.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishInfoProtectionBaby.addCannedText("Do you want me to give you some advices for protecting your baby during hot weather?");
	   	
	   	KristinaAgenda agendaRequestWishActivitiesBaby = factory.createKristinaAgenda("agenda_RequestWishActivitiesBaby");
	   	agendaRequestWishActivitiesBaby.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishActivitiesBaby);
		agendaRequestWishActivitiesBaby.addNext(agendaRequestWishActivitiesBaby);
		agendaRequestWishActivitiesBaby.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishActivitiesBaby);
	   	agendaRequestWishActivitiesBaby.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishActivitiesBaby.addCannedText("Do you want me to tell you activities in the city where you can go with your baby?");
	   	
	   	KristinaAgenda agendaRequestWishClosestParks = factory.createKristinaAgenda("agenda_RequestWishClosestParks");
	   	agendaRequestWishClosestParks.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishClosestParks);
		agendaRequestWishClosestParks.addNext(agendaRequestWishClosestParks);
		agendaRequestWishClosestParks.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishClosestParks);
	   	agendaRequestWishClosestParks.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishClosestParks.addCannedText("Do you want me to tell you where the nearest park is?");
	   	
	   	KristinaAgenda agendaRequestWishInfoDiabetes = factory.createKristinaAgenda("agenda_RequestWishInfoDiabetes");
	   	agendaRequestWishInfoDiabetes.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishInfoDiabetes);
		agendaRequestWishInfoDiabetes.addNext(agendaRequestWishInfoDiabetes);
		agendaRequestWishInfoDiabetes.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishInfoDiabetes);
	   	agendaRequestWishInfoDiabetes.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishInfoDiabetes.addCannedText("Would you like me to show you some information about diabetes?");
	   	
	   	KristinaAgenda agendaRequestWishInfoDementia = factory.createKristinaAgenda("agenda_RequestWishInfoDementia");
	   	agendaRequestWishInfoDementia.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishInfoDementia);
		agendaRequestWishInfoDementia.addNext(agendaRequestWishInfoDementia);
		agendaRequestWishInfoDementia.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishInfoDementia);
	   	agendaRequestWishInfoDementia.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishInfoDementia.addCannedText("Would you like me to show you some information about dementia?");
	   	
	   	KristinaAgenda agendaRequestWishInfoSleep = factory.createKristinaAgenda("agenda_RequestWishInfoSleep");
	   	agendaRequestWishInfoSleep.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishInfoSleep);
		agendaRequestWishInfoSleep.addNext(agendaRequestWishInfoSleep);
		agendaRequestWishInfoSleep.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishInfoSleep);
	   	agendaRequestWishInfoSleep.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishInfoSleep.addCannedText("Would you like me to give you some information on to sleep better?");
	   	
	   	KristinaAgenda agendaRequestWishInfoSleepHygiene = factory.createKristinaAgenda("agenda_RequestWishInfoSleepHygiene");
	   	agendaRequestWishInfoSleepHygiene.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishInfoSleepHygiene);
		agendaRequestWishInfoSleepHygiene.addNext(agendaRequestWishInfoSleepHygiene);
		agendaRequestWishInfoSleepHygiene.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishInfoSleepHygiene);
	   	agendaRequestWishInfoSleepHygiene.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishInfoSleepHygiene.addCannedText("Would you like me to give you some information on sleep hygiene?");
	   	
	   	KristinaAgenda agendaRequestWishRecipe = factory.createKristinaAgenda("agenda_RequestWishRecipe");
	   	agendaRequestWishRecipe.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishRecipe);
		agendaRequestWishRecipe.addNext(agendaRequestWishRecipe);
		agendaRequestWishRecipe.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishRecipe);
	   	agendaRequestWishRecipe.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishRecipe.addCannedText("Would you like me to show you any recipes?");
	   	
	   	KristinaAgenda agendaRequestWishSocialMedia = factory.createKristinaAgenda("agenda_RequestWishSocialMedia");
	   	agendaRequestWishSocialMedia.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishSocialMedia);
		//This should probably only be done once per dialogue
//		agendaRequestWishSocialMedia.addNext(agendaRequestWishSocialMedia);
		agendaRequestWishSocialMedia.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishSocialMedia);
	   	agendaRequestWishSocialMedia.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishSocialMedia.addCannedText("Would you like to see what is discussed in social media?");

	   	KristinaAgenda agendaRequestWishEvent = factory.createKristinaAgenda("agenda_RequestWishEvent");
	   	agendaRequestWishEvent.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishEvent);
		//This should probably only be done once per dialogue
//		agendaRequestWishEvent.addNext(agendaRequestWishEvent);
		agendaRequestWishEvent.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishEvent);
	   	agendaRequestWishEvent.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishEvent.addCannedText("Would you like me to check for events in the city?");
	   	
		KristinaAgenda agendaRequestWishWeather = factory.createKristinaAgenda("agenda_RequestWishWeather");
		agendaRequestWishWeather.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishWeather);
		//This should probably only be done once per dialogue
//		agendaRequestWishWeather.addNext(agendaRequestWishWeather);
		agendaRequestWishWeather.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishWeather);
	   	agendaRequestWishWeather.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishWeather.addCannedText("Would you like me to give you the weather today?");
	   	
	   	KristinaAgenda agendaRequestWishNewspaper = factory.createKristinaAgenda("agenda_RequestWishNewspaper");
	   	agendaRequestWishNewspaper.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishNewspaper);
		//This should probably only be done once per dialogue
//		agendaRequestWishNewspaper.addNext(agendaRequestWishNewspaper);
		agendaRequestWishNewspaper.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishNewspaper);
	   	agendaRequestWishNewspaper.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishNewspaper.addCannedText("Would you like me to read the newspaper for you?");
	   	
	   	KristinaAgenda agendaTellArticleNotFound = factory.createKristinaAgenda("agenda_TellArticleNotFound");
	   	agendaTellArticleNotFound.addHas(dummyUserMove);
		agendaStart.addNext(agendaTellArticleNotFound);
		agendaTellArticleNotFound.addNext(agendaTellArticleNotFound);
		agendaTellArticleNotFound.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaTellArticleNotFound);
	   	agendaTellArticleNotFound.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaTellArticleNotFound.addCannedText("I can't find that article.");
	   	
	   	KristinaAgenda agendaRequestArticleLiked = factory.createKristinaAgenda("agenda_RequestArticleLiked");
	   	agendaRequestArticleLiked.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestArticleLiked);
		agendaRequestArticleLiked.addNext(agendaRequestArticleLiked);
		agendaRequestArticleLiked.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestArticleLiked);
	   	agendaRequestArticleLiked.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaRequestArticleLiked.addCannedText("Did you like the article?");
	   	
	   	KristinaAgenda agendaSuggestUmbrella = factory.createKristinaAgenda("agenda_SuggestUmbrella");
	   	agendaSuggestUmbrella.addHas(dummyUserMove);
		agendaStart.addNext(agendaSuggestUmbrella);
		agendaSuggestUmbrella.addNext(agendaSuggestUmbrella);
		agendaSuggestUmbrella.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaSuggestUmbrella);
	   	agendaSuggestUmbrella.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaSuggestUmbrella.addCannedText("Remember to take your umbrella.");
	   	
	   	KristinaAgenda agendaSuggestSunglassesAndHat = factory.createKristinaAgenda("agenda_SuggestSunglassesAndHat");
	   	agendaSuggestSunglassesAndHat.addHas(dummyUserMove);
		agendaStart.addNext(agendaSuggestSunglassesAndHat);
		agendaSuggestSunglassesAndHat.addNext(agendaSuggestSunglassesAndHat);
		agendaSuggestSunglassesAndHat.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaSuggestSunglassesAndHat);
	   	agendaSuggestSunglassesAndHat.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaSuggestSunglassesAndHat.addCannedText("Don't forget your sunglasses and your hat.");
	   	
	   	KristinaAgenda agendaRequestWellbeing = factory.createKristinaAgenda("agenda_RequestWellbeing");
	   	agendaRequestWellbeing.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWellbeing);
		agendaRequestWellbeing.addNext(agendaRequestWellbeing);
		agendaRequestWellbeing.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWellbeing);
	   	agendaRequestWellbeing.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWellbeing.addCannedText("You sound upset, is everything alright?");
	   	
	   	KristinaAgenda agendaExpressEmpathy = factory.createKristinaAgenda("agenda_ExpressEmpathy");
	   	agendaExpressEmpathy.addHas(dummyUserMove);
		agendaStart.addNext(agendaExpressEmpathy);
		agendaExpressEmpathy.addNext(agendaExpressEmpathy);
		agendaExpressEmpathy.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaExpressEmpathy);
	   	agendaExpressEmpathy.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaExpressEmpathy.addCannedText("I'm sorry to hear that.");
	   	
	   	KristinaAgenda agendaConfirmAssistance = factory.createKristinaAgenda("agenda_ConfirmAssistance");
	   	agendaConfirmAssistance.addHas(dummyUserMove);
		agendaStart.addNext(agendaConfirmAssistance);
		agendaConfirmAssistance.addNext(agendaConfirmAssistance);
		agendaConfirmAssistance.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaConfirmAssistance);
	   	agendaConfirmAssistance.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaConfirmAssistance.addCannedText("No worries. I'm with you.");
	   	
	   	KristinaAgenda agendaAdviseDoctor = factory.createKristinaAgenda("agenda_AdviseDoctor");
	   	agendaAdviseDoctor.addHas(dummyUserMove);
		agendaStart.addNext(agendaAdviseDoctor);
		agendaAdviseDoctor.addNext(agendaAdviseDoctor);
		agendaAdviseDoctor.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaAdviseDoctor);
	   	agendaAdviseDoctor.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaAdviseDoctor.addCannedText("Please don't forget to consult a doctor if Mr. Müller hurts anywhere. And maybe inform his relatives about this incident.");
        
	   	KristinaAgenda agendaRequestMatter = factory.createKristinaAgenda("agenda_RequestMatter");
	   	agendaRequestMatter.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestMatter);
		agendaRequestMatter.addNext(agendaRequestMatter);
		agendaRequestMatter.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestMatter);
	   	agendaRequestMatter.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaRequestMatter.addCannedText("What's the matter?");
	   	
	   	KristinaAgenda agendaAdviseDoctorSelf = factory.createKristinaAgenda("agenda_AdviseDoctorSelf");
	   	agendaAdviseDoctorSelf.addHas(dummyUserMove);
		agendaStart.addNext(agendaAdviseDoctorSelf);
		agendaAdviseDoctorSelf.addNext(agendaAdviseDoctorSelf);
		agendaAdviseDoctorSelf.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaAdviseDoctorSelf);
	   	agendaAdviseDoctorSelf.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaAdviseDoctorSelf.addCannedText("Please, get an appointment with your doctor and tell him all your symptoms.");
        
	   	KristinaAgenda agendaRequestWishAppointment = factory.createKristinaAgenda("agenda_RequestWishAppointment");
	   	agendaRequestWishAppointment.addHas(dummyUserMove);
		agendaStart.addNext(agendaRequestWishAppointment);
		agendaRequestWishAppointment.addNext(agendaRequestWishAppointment);
		agendaRequestWishAppointment.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaRequestWishAppointment);
	   	agendaRequestWishAppointment.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaRequestWishAppointment.addCannedText("Would you like me to show some information on how to get a doctor appointment?");
        
	   	KristinaAgenda agendaAskSpecificTask = factory.createKristinaAgenda("agenda_AskSpecificTask");
	   	agendaAskSpecificTask.addHas(dummyUserMove);
		agendaStart.addNext(agendaAskSpecificTask);
		agendaAskSpecificTask.addNext(agendaAskSpecificTask);
		agendaAskSpecificTask.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaAskSpecificTask);
	   	agendaAskSpecificTask.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaAskSpecificTask.addCannedText("How can I assist you?");
	   	
	   	KristinaAgenda agendaCongrats = factory.createKristinaAgenda("agenda_Congrats");
	   	agendaCongrats.addHas(dummyUserMove);
		agendaStart.addNext(agendaCongrats);
		agendaCongrats.addNext(agendaCongrats);
		agendaCongrats.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaCongrats);
	   	agendaCongrats.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaCongrats.addCannedText("Congratulations!");
	   	
		KristinaAgenda agendaPartnerAware = factory.createKristinaAgenda("agenda_PartnerAware");
		agendaPartnerAware.addHas(dummyUserMove);
		agendaStart.addNext(agendaPartnerAware);
		agendaPartnerAware.addNext(agendaPartnerAware);
		agendaPartnerAware.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaPartnerAware);
	   	agendaPartnerAware.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaPartnerAware.addCannedText("Is your partner aware of that?");
        
	   	KristinaAgenda agendaAskDoctor = factory.createKristinaAgenda("agenda_AskDoctor");
	   	agendaAskDoctor.addHas(dummyUserMove);
		agendaStart.addNext(agendaAskDoctor);
		agendaAskDoctor.addNext(agendaAskDoctor);
		agendaAskDoctor.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaAskDoctor);
	   	agendaAskDoctor.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaAskDoctor.addCannedText("Always ask for advices talking to your Family Doctor.");
	   	
	   	KristinaAgenda agendaSources = factory.createKristinaAgenda("agenda_Sources");
	   	agendaSources.addHas(dummyUserMove);
		agendaStart.addNext(agendaSources);
		agendaSources.addNext(agendaSources);
		agendaSources.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaSources);
	   	agendaSources.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaSources.addCannedText("Nowadays it's difficult to find reliable sources, be careful with the information you find on the web");
	   	
	   	KristinaAgenda agendaKnowHow = factory.createKristinaAgenda("agenda_KnowHow");
	   	agendaKnowHow.addHas(dummyUserMove);
		agendaStart.addNext(agendaKnowHow);
		agendaKnowHow.addNext(agendaKnowHow);
		agendaKnowHow.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaKnowHow);
	   	agendaKnowHow.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaKnowHow.addCannedText("Do you know how to do it?");
	   	
	   	KristinaAgenda agendaHelpful = factory.createKristinaAgenda("agenda_Helpful");
	   	agendaHelpful.addHas(dummyUserMove);
		agendaStart.addNext(agendaHelpful);
		agendaHelpful.addNext(agendaHelpful);
		agendaHelpful.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaHelpful);
	   	agendaHelpful.addDialogueAction(IRI.create(DialogueAction.PROACTIVE_CANNED));
	   	agendaHelpful.addCannedText("Was it helpful?");
	   	
	   	KristinaAgenda agendaVideoAgain = factory.createKristinaAgenda("agenda_VideoAgain");
	   	agendaVideoAgain.addHas(dummyUserMove);
		agendaStart.addNext(agendaVideoAgain);
		agendaVideoAgain.addNext(agendaVideoAgain);
		agendaVideoAgain.addSummaryAgenda(sumAgenda);
	   	sumAgenda.addSummarizedAgenda(agendaVideoAgain);
	   	agendaVideoAgain.addDialogueAction(IRI.create(DialogueAction.CANNED));
	   	agendaVideoAgain.addCannedText("If you want to see the video again, just let me know.");
	   	
	   	
        System.out.println("kristina.owl created");
		
        try {
            factory.manager.saveOntology(factory.onto,
                    factory.manager.getOntologyDocumentIRI(factory.onto));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        Settings.uri = uriSave;
	}
}