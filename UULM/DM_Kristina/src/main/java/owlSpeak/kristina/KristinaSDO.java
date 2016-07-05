package owlSpeak.kristina;

import java.io.FileNotFoundException;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.SummaryAgenda;
import owlSpeak.Utterance;
import owlSpeak.Variable;
import owlSpeak.Move.ExtractFieldMode;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Reward;

/**
 * @author Juliana Miehle
 */
public class KristinaSDO {
	
	public static void main(String[] argv) throws Exception {
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
        @SuppressWarnings("unused")
		ServletEngine engine = new ServletEngine();
        String uriSave = Settings.uri;
        Settings.uri = "http://localhost:8080/OwlSpeakOnto.owl";
        String filename = "KristinaSDO.owl";
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
        
        // Masteragenda
        Agenda agendaMaster = factory.createAgenda("agenda_Master");
		agendaMaster.setIsMasterBool(false, true);

		// 1) Apologise
		Agenda agendaApologise = factory.createAgenda("agenda_Apologise");
		Move moveSysApologise = factory.createMove("move_sys_Apologise");
		agendaApologise.addHas(moveSysApologise);
		agendaMaster.addNext(agendaApologise);
		agendaApologise.addNext(agendaApologise);

		Agenda agendaApologisePersonal = factory.createAgenda("agenda_ApologisePersonal");
		Move moveSysApologisePersonal = factory.createMove("move_sys_ApologisePersonal");
		agendaApologisePersonal.addHas(moveSysApologisePersonal);
		agendaMaster.addNext(agendaApologisePersonal);
		agendaApologisePersonal.addNext(agendaApologisePersonal);
		
		// 2) Clarify
		Agenda agendaRequestRepeat = factory.createAgenda("agenda_RequestRepeat");
		Move moveSysRequestRepeat = factory.createMove("move_sys_RequestRepeat");
		agendaRequestRepeat.addHas(moveSysRequestRepeat);
		agendaMaster.addNext(agendaRequestRepeat);
		agendaRequestRepeat.addNext(agendaRequestRepeat);

		Agenda agendaRequestRephrase = factory.createAgenda("agenda_RequestRephrase");
		Move moveSysRequestRephrase = factory.createMove("move_sys_RequestRephrase");
		agendaRequestRephrase.addHas(moveSysRequestRephrase);
		agendaMaster.addNext(agendaRequestRephrase);
		agendaRequestRephrase.addNext(agendaRequestRephrase);
		
		Agenda agendaStateMissingComprehension = factory.createAgenda("agenda_StateMissingComprehension");
		Move moveSysStateMissingComprehension = factory.createMove("move_sys_StateMissingComprehension");
		agendaStateMissingComprehension.addHas(moveSysStateMissingComprehension);
		agendaMaster.addNext(agendaStateMissingComprehension);
		agendaStateMissingComprehension.addNext(agendaStateMissingComprehension);
		
		// 3) StartConversation
		Agenda agendaAskMood = factory.createAgenda("agenda_AskMood");
		Move moveSysAskMood = factory.createMove("move_sys_AskMood");
		agendaAskMood.addHas(moveSysAskMood);
		agendaMaster.addNext(agendaAskMood);
		agendaAskMood.addNext(agendaAskMood);
		
		Agenda agendaAskPlans = factory.createAgenda("agenda_AskPlans");
		Move moveSysAskPlans = factory.createMove("move_sys_AskPlans");
		agendaAskPlans.addHas(moveSysAskPlans);
		agendaMaster.addNext(agendaAskPlans);
		agendaAskPlans.addNext(agendaAskPlans);
		
		Agenda agendaAskTask = factory.createAgenda("agenda_AskTask");
		Move moveSysAskTask = factory.createMove("move_sys_AskTask");
		agendaAskTask.addHas(moveSysAskTask);
		agendaMaster.addNext(agendaAskTask);
		agendaAskTask.addNext(agendaAskTask);
		
		Agenda agendaAskTaskSuccess = factory.createAgenda("agenda_AskTaskSuccess");
		Move moveSysAskTaskSuccess = factory.createMove("move_sys_AskTaskSuccess");
		agendaAskTaskSuccess.addHas(moveSysAskTaskSuccess);
		agendaMaster.addNext(agendaAskTaskSuccess);
		agendaAskTaskSuccess.addNext(agendaAskTaskSuccess);
		
		Agenda agendaAskWellBeing = factory.createAgenda("agenda_AskWellBeing");
		Move moveSysAskWellBeing = factory.createMove("move_sys_AskWellBeing");
		agendaAskWellBeing.addHas(moveSysAskWellBeing);
		agendaMaster.addNext(agendaAskWellBeing);
		agendaAskWellBeing.addNext(agendaAskWellBeing);
		
		// 4) EmotionalSystemActions
		Agenda agendaCalmDown = factory.createAgenda("agenda_CalmDown");
		Move moveSysCalmDown = factory.createMove("move_sys_CalmDown");
		agendaCalmDown.addHas(moveSysCalmDown);
		agendaMaster.addNext(agendaCalmDown);
		agendaCalmDown.addNext(agendaCalmDown);
		
		Agenda agendaCheerUp = factory.createAgenda("agenda_CheerUp");
		Move moveSysCheerUp = factory.createMove("move_sys_CheerUp");
		agendaCheerUp.addHas(moveSysCheerUp);
		agendaMaster.addNext(agendaCheerUp);
		agendaCheerUp.addNext(agendaCheerUp);
		
		Agenda agendaConsole = factory.createAgenda("agenda_Console");
		Move moveSysConsole = factory.createMove("move_sys_Console");
		agendaConsole.addHas(moveSysConsole);
		agendaMaster.addNext(agendaConsole);
		agendaConsole.addNext(agendaConsole);
		
		Agenda agendaShareJoy = factory.createAgenda("agenda_ShareJoy");
		Move moveSysShareJoy = factory.createMove("move_sys_ShareJoy");
		agendaShareJoy.addHas(moveSysShareJoy);
		agendaMaster.addNext(agendaShareJoy);
		agendaShareJoy.addNext(agendaShareJoy);
		
		// 5) Greet
		Agenda agendaGreet = factory.createAgenda("agenda_Greet");
		Move moveSysGreet = factory.createMove("move_sys_Greet");
		agendaGreet.addHas(moveSysGreet);
		agendaMaster.addNext(agendaGreet);
		agendaGreet.addNext(agendaGreet);
		
		Agenda agendaGreetPersonal = factory.createAgenda("agenda_GreetPersonal");
		Move moveSysGreetPersonal = factory.createMove("move_sys_GreetPersonal");
		agendaGreetPersonal.addHas(moveSysGreetPersonal);
		agendaMaster.addNext(agendaGreetPersonal);
		agendaGreetPersonal.addNext(agendaGreetPersonal);
		
		Agenda agendaGreetMorning = factory.createAgenda("agenda_GreetMorning");
		Move moveSysGreetMorning = factory.createMove("move_sys_GreetMorning");
		agendaGreetMorning.addHas(moveSysGreetMorning);
		agendaMaster.addNext(agendaGreetMorning);
		agendaGreetMorning.addNext(agendaGreetMorning);
		
		Agenda agendaGreetAfternoon = factory.createAgenda("agenda_GreetAfternoon");
		Move moveSysGreetAfternoon = factory.createMove("move_sys_GreetAfternoon");
		agendaGreetAfternoon.addHas(moveSysGreetAfternoon);
		agendaMaster.addNext(agendaGreetAfternoon);
		agendaGreetAfternoon.addNext(agendaGreetAfternoon);
		
		Agenda agendaGreetEvening = factory.createAgenda("agenda_GreetEvening");
		Move moveSysGreetEvening = factory.createMove("move_sys_GreetEvening");
		agendaGreetEvening.addHas(moveSysGreetEvening);
		agendaMaster.addNext(agendaGreetEvening);
		agendaGreetEvening.addNext(agendaGreetEvening);
		
		// 6) SayGoodbye
		Agenda agendaSayGoodbye = factory.createAgenda("agenda_SayGoodbye");
		Move moveSysSayGoodbye = factory.createMove("move_sys_SayGoodbye");
		agendaSayGoodbye.addHas(moveSysSayGoodbye);
		agendaMaster.addNext(agendaSayGoodbye);
		agendaSayGoodbye.addNext(agendaSayGoodbye);
		
		Agenda agendaSayGoodbyePersonal = factory.createAgenda("agenda_SayGoodbyePersonal");
		Move moveSysSayGoodbyePersonal = factory.createMove("move_sys_SayGoodbyePersonal");
		agendaSayGoodbyePersonal.addHas(moveSysSayGoodbyePersonal);
		agendaMaster.addNext(agendaSayGoodbyePersonal);
		agendaSayGoodbyePersonal.addNext(agendaSayGoodbyePersonal);
		
		Agenda agendaSayGoodbyeMorning = factory.createAgenda("agenda_SayGoodbyeMorning");
		Move moveSysSayGoodbyeMorning = factory.createMove("move_sys_SayGoodbyeMorning");
		agendaSayGoodbyeMorning.addHas(moveSysSayGoodbyeMorning);
		agendaMaster.addNext(agendaSayGoodbyeMorning);
		agendaSayGoodbyeMorning.addNext(agendaSayGoodbyeMorning);
		
		Agenda agendaSayGoodbyeAfternoon = factory.createAgenda("agenda_SayGoodbyeAfternoon");
		Move moveSysSayGoodbyeAfternoon = factory.createMove("move_sys_SayGoodbyeAfternoon");
		agendaSayGoodbyeAfternoon.addHas(moveSysSayGoodbyeAfternoon);
		agendaMaster.addNext(agendaSayGoodbyeAfternoon);
		agendaSayGoodbyeAfternoon.addNext(agendaSayGoodbyeAfternoon);
		
		Agenda agendaSayGoodbyeEvening = factory.createAgenda("agenda_SayGoodbyeEvening");
		Move moveSysSayGoodbyeEvening = factory.createMove("move_sys_SayGoodbyeEvening");
		agendaSayGoodbyeEvening.addHas(moveSysSayGoodbyeEvening);
		agendaMaster.addNext(agendaSayGoodbyeEvening);
		agendaSayGoodbyeEvening.addNext(agendaSayGoodbyeEvening);
		
		Agenda agendaSayGoodbyeWeekend = factory.createAgenda("agenda_SayGoodbyeWeekend");
		Move moveSysSayGoodbyeWeekend = factory.createMove("move_sys_SayGoodbyeWeekend");
		agendaSayGoodbyeWeekend.addHas(moveSysSayGoodbyeWeekend);
		agendaMaster.addNext(agendaSayGoodbyeWeekend);
		agendaSayGoodbyeWeekend.addNext(agendaSayGoodbyeWeekend);
		
		Agenda agendaSayGoodbyeMeetAgain = factory.createAgenda("agenda_SayGoodbyeMeetAgain");
		Move moveSysSayGoodbyeMeetAgain = factory.createMove("move_sys_SayGoodbyeMeetAgain");
		agendaSayGoodbyeMeetAgain.addHas(moveSysSayGoodbyeMeetAgain);
		agendaMaster.addNext(agendaSayGoodbyeMeetAgain);
		agendaSayGoodbyeMeetAgain.addNext(agendaSayGoodbyeMeetAgain);
		
		// 7) Motivate
		Agenda agendaMotivate = factory.createAgenda("agenda_Motivate");
		Move moveSysMotivate = factory.createMove("move_sys_Motivate");
		agendaMotivate.addHas(moveSysMotivate);
		agendaMaster.addNext(agendaMotivate);
		agendaMotivate.addNext(agendaMotivate);
		
		Agenda agendaMotivatePersonal = factory.createAgenda("agenda_MotivatePersonal");
		Move moveSysMotivatePersonal = factory.createMove("move_sys_MotivatePersonal");
		agendaMotivatePersonal.addHas(moveSysMotivatePersonal);
		agendaMaster.addNext(agendaMotivatePersonal);
		agendaMotivatePersonal.addNext(agendaMotivatePersonal);
		
		Agenda agendaMotivateGroupOriented = factory.createAgenda("agenda_MotivateGroupOriented");
		Move moveSysMotivateGroupOriented = factory.createMove("move_sys_MotivateGroupOriented");
		agendaMotivateGroupOriented.addHas(moveSysMotivateGroupOriented);
		agendaMaster.addNext(agendaMotivateGroupOriented);
		agendaMotivateGroupOriented.addNext(agendaMotivateGroupOriented);
		
		Agenda agendaMotivateIndividuisticallyOriented = factory.createAgenda("agenda_MotivateIndividuisticallyOriented");
		Move moveSysMotivateIndividuisticallyOriented = factory.createMove("move_sys_MotivateIndividuisticallyOriented");
		agendaMotivateIndividuisticallyOriented.addHas(moveSysMotivateIndividuisticallyOriented);
		agendaMaster.addNext(agendaMotivateIndividuisticallyOriented);
		agendaMotivateIndividuisticallyOriented.addNext(agendaMotivateIndividuisticallyOriented);
		
		// 8) Thank
		Agenda agendaThank = factory.createAgenda("agenda_Thank");
		Move moveSysThank = factory.createMove("move_sys_Thank");
		agendaThank.addHas(moveSysThank);
		agendaMaster.addNext(agendaThank);
		agendaThank.addNext(agendaThank);
		
		Agenda agendaThankPersonal = factory.createAgenda("agenda_ThankPersonal");
		Move moveSysThankPersonal = factory.createMove("move_sys_ThankPersonal");
		agendaThankPersonal.addHas(moveSysThankPersonal);
		agendaMaster.addNext(agendaThankPersonal);
		agendaThankPersonal.addNext(agendaThankPersonal);
		
		Agenda agendaAnswerThank = factory.createAgenda("agenda_AnswerThank");
		Move moveSysAnswerThank = factory.createMove("move_sys_AnswerThank");
		agendaAnswerThank.addHas(moveSysAnswerThank);
		agendaMaster.addNext(agendaAnswerThank);
		agendaAnswerThank.addNext(agendaAnswerThank);
		
		Agenda agendaAnswerThankPersonal = factory.createAgenda("agenda_AnswerThankPersonal");
		Move moveSysAnswerThankPersonal = factory.createMove("move_sys_AnswerThankPersonal");
		agendaAnswerThankPersonal.addHas(moveSysAnswerThankPersonal);
		agendaMaster.addNext(agendaAnswerThankPersonal);
		agendaAnswerThankPersonal.addNext(agendaAnswerThankPersonal);
        
        System.out.println("Domain independent system actions created!");
		
        try {
            factory.manager.saveOntology(factory.onto,
                    factory.manager.getOntologyDocumentIRI(factory.onto));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        Settings.uri = uriSave;
	}
}
