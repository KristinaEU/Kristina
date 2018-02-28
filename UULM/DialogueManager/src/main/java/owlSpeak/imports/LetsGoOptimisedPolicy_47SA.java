package owlSpeak.imports;

import java.io.FileNotFoundException;

import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.Move.ExtractFieldMode;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.SummaryAgenda;
import owlSpeak.Utterance;
import owlSpeak.Variable;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Reward;

public class LetsGoOptimisedPolicy_47SA {

	public static void main(String[] argv) throws Exception {
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
		@SuppressWarnings("unused")
		ServletEngine engine = new ServletEngine();
		String uriSave = Settings.uri;
		Settings.uri = "http://localhost:8080/OwlSpeakOnto.owl";
		String filename = "letsGoUs.owl";
		String path = "C:/OwlSpeak/";

		OSFactory factory = null;
		try {
			factory = OwlSpeakOntology.createOSFactoryEmptyOnto(filename, path);
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		boolean confirmationMandatory = true;


		// Create Rewards
		Reward rewDefault = factory.createReward("rew_default");
		rewDefault.setRewardValue(-6); // bei IQReward -4, sonst -1
		rewDefault.setSpecialReward("default_reward");

		
		// Create SemanticGroups
		SemanticGroup busSemG = factory.createSemanticGroup("Bus_route_sem");
		busSemG.setFieldTotals(100);
		
		SemanticGroup depSemG = factory.createSemanticGroup("Departure_place_sem");
		depSemG.setFieldTotals(100);
		
		SemanticGroup destSemG = factory.createSemanticGroup("Destination_sem");
		destSemG.setFieldTotals(100);
		
		SemanticGroup timeSemG = factory.createSemanticGroup("Time_sem");
		timeSemG.setFieldTotals(100);

		
		// Create Semantics
		Semantic conDestSem = factory.createSemantic("Confirm_Destination_sem");
		conDestSem.setConfirmationInfo(true);
		conDestSem.addSemanticGroup(destSemG);
		destSemG.addContainedSemantic(conDestSem);

		Semantic rejDestSem = factory.createSemantic("Reject_Destination_sem");
		rejDestSem.setConfirmationInfo(false);
		rejDestSem.addSemanticGroup(destSemG);
		destSemG.addContainedSemantic(rejDestSem);
		
		Semantic conDepSem = factory.createSemantic("Confirm_Departure_place_sem");
		conDepSem.setConfirmationInfo(true);
		conDepSem.addSemanticGroup(depSemG);
		depSemG.addContainedSemantic(conDepSem);
		
		Semantic rejDepSem = factory.createSemantic("Reject_Departure_place_sem");
		rejDepSem.setConfirmationInfo(false);
		rejDepSem.addSemanticGroup(depSemG);
		depSemG.addContainedSemantic(rejDepSem);
		
		Semantic conTimeSem = factory.createSemantic("Confirm_Time_sem");
		conTimeSem.setConfirmationInfo(true);
		conTimeSem.addSemanticGroup(timeSemG);
		timeSemG.addContainedSemantic(conTimeSem);
		
		Semantic rejTimeSem = factory.createSemantic("Reject_Time_sem");
		rejTimeSem.setConfirmationInfo(false);
		rejTimeSem.addSemanticGroup(timeSemG);
		timeSemG.addContainedSemantic(rejTimeSem);
		
		Semantic conBusSem = factory.createSemantic("Confirm_Bus_route_sem");
		conBusSem.setConfirmationInfo(true);
		conBusSem.addSemanticGroup(busSemG);
		busSemG.addContainedSemantic(conBusSem);
		
		Semantic rejBusSem = factory.createSemantic("Reject_Bus_route_sem");
		rejBusSem.setConfirmationInfo(false);
		rejBusSem.addSemanticGroup(busSemG);
		busSemG.addContainedSemantic(rejBusSem);

		
		// Create Variables
		Variable busVar = factory.createVariable("Bus_route_var");
		busSemG.addVariable(busVar);
		busVar.addBelongsToSemantic(busSemG); 

		Variable depVar = factory.createVariable("Departure_place_var");
		depSemG.addVariable(depVar);
		depVar.addBelongsToSemantic(depSemG);

		Variable destVar = factory.createVariable("Destination_var");
		destSemG.addVariable(destVar);
		destVar.addBelongsToSemantic(destSemG);

		Variable timeVar = factory.createVariable("Time_var");
		timeSemG.addVariable(timeVar);
		timeVar.addBelongsToSemantic(timeSemG);

//		Variable InteractionQuality = factory.createVariable("InteractionQuality");
//		InteractionQuality.setDefaultValue("", "5");

		
		// Create Master Agenda
		Agenda masterAgenda = factory.createAgenda("Masteragenda");
		masterAgenda.setIsMasterBool(false, true);
		
		
		// Create Start Agenda
		SummaryAgenda openSummaryAgenda = factory.createSummaryAgenda("Open_SummaryAgenda");
		openSummaryAgenda.setRole(openSummaryAgenda.getRole(), "collection");
		openSummaryAgenda.setType(openSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda = factory.createAgenda("Open_agenda");
		openAgenda.addMustnot(busSemG);
		openAgenda.addMustnot(timeSemG);
		openAgenda.addMustnot(destSemG);
		openAgenda.addMustnot(depSemG);
		openAgenda.addMustnot(conBusSem);
		openAgenda.addMustnot(conTimeSem);
		openAgenda.addMustnot(conDestSem);
		openAgenda.addMustnot(conDepSem);
		openAgenda.setRole(openAgenda.getRole(), "collection");
		openAgenda.addSummaryAgenda(openSummaryAgenda);
		openSummaryAgenda.addSummarizedAgenda(openAgenda);
		
		
		// Create Exit Agenda
		SummaryAgenda exitSummaryAgenda = factory.createSummaryAgenda("Exit_SummaryAgenda");
		exitSummaryAgenda.setRole(exitSummaryAgenda.getRole(), "collection");
		exitSummaryAgenda.setType(exitSummaryAgenda.getTypeString(), "announcement");
		Agenda exitAgenda = factory.createAgenda("Exit_Agenda");
		exitAgenda.addRequires(depSemG);
		exitAgenda.addRequires(timeSemG);
		exitAgenda.addRequires(destSemG);
		if (confirmationMandatory) 
		{
			exitAgenda.addRequires(conDepSem);
			exitAgenda.addRequires(conTimeSem);
			exitAgenda.addRequires(conDestSem);
		}
		exitAgenda.setRole(exitAgenda.getRole(), "collection");
		exitAgenda.addSummaryAgenda(exitSummaryAgenda);
		exitSummaryAgenda.addSummarizedAgenda(exitAgenda);

		
		// Create Confirm Agendas
		SummaryAgenda conBusSummaryAgenda = factory.createSummaryAgenda("Confirm_Bus_route_SummaryAgenda");
		conBusSummaryAgenda.setRole(conBusSummaryAgenda.getRole(), "confirmation");
		conBusSummaryAgenda.setType(conBusSummaryAgenda.getTypeString(), "confirmation");
		Agenda conBusAgenda = factory.createAgenda("Confirm_Bus_route_Agenda");
		conBusAgenda.addRequires(busSemG);
		conBusAgenda.addMustnot(conBusSem);
		conBusAgenda.setRole(conBusAgenda.getRole(), "confirmation");
		conBusAgenda.addSummaryAgenda(conBusSummaryAgenda);
		conBusSummaryAgenda.addSummarizedAgenda(conBusAgenda);

		SummaryAgenda conDestSummaryAgenda = factory.createSummaryAgenda("Confirm_Destination_SummaryAgenda");
		conDestSummaryAgenda.setRole(conDestSummaryAgenda.getRole(), "confirmation");
		conDestSummaryAgenda.setType(conDestSummaryAgenda.getTypeString(), "confirmation");
		Agenda conDestAgenda = factory.createAgenda("Confirm_Destination_Agenda");
		conDestAgenda.addRequires(destSemG);
		conDestAgenda.addMustnot(conDestSem);
		conDestAgenda.setRole(conDestAgenda.getRole(), "confirmation");
		conDestAgenda.addSummaryAgenda(conDestSummaryAgenda);
		conDestSummaryAgenda.addSummarizedAgenda(conDestAgenda);

		SummaryAgenda conDepSummaryAgenda = factory.createSummaryAgenda("Confirm_Departure_place_SummaryAgenda");
		conDepSummaryAgenda.setRole(conDepSummaryAgenda.getRole(), "confirmation");
		conDepSummaryAgenda.setType(conDepSummaryAgenda.getTypeString(), "confirmation");
		Agenda conDepAgenda = factory.createAgenda("Confirm_Departure_place_Agenda");
		conDepAgenda.addRequires(depSemG);
		conDepAgenda.addMustnot(conDepSem);
		conDepAgenda.setRole(conDepAgenda.getRole(), "confirmation");
		conDepAgenda.addSummaryAgenda(conDepSummaryAgenda);
		conDepSummaryAgenda.addSummarizedAgenda(conDepAgenda);

		SummaryAgenda conTimeSummaryAgenda = factory.createSummaryAgenda("Confirm_Time_SummaryAgenda");
		conTimeSummaryAgenda.setRole(conTimeSummaryAgenda.getRole(), "confirmation");
		conTimeSummaryAgenda.setType(conTimeSummaryAgenda.getTypeString(), "confirmation");
		Agenda conTimeAgenda = factory.createAgenda("Confirm_Time_Agenda");
		conTimeAgenda.addRequires(timeSemG);
		conTimeAgenda.addMustnot(conTimeSem);
		conTimeAgenda.setRole(conTimeAgenda.getRole(), "confirmation");
		conTimeAgenda.addSummaryAgenda(conTimeSummaryAgenda);
		conTimeSummaryAgenda.addSummarizedAgenda(conTimeAgenda);
		
		
		// Create Agendas for the System Initiative
		SummaryAgenda destSummaryAgenda = factory.createSummaryAgenda("Destination_SumAgenda");
		destSummaryAgenda.setRole(destSummaryAgenda.getRole(), "collection");
		destSummaryAgenda.setType(destSummaryAgenda.getTypeString(), "request");
		Agenda destAgenda = factory.createAgenda("Destination_agenda");
		destAgenda.addMustnot(conDestSem); 
		destAgenda.addMustnot(destSemG);
		destAgenda.setRole(destAgenda.getRole(), "collection"); 
		destAgenda.addSummaryAgenda(destSummaryAgenda);
		destSummaryAgenda.addSummarizedAgenda(destAgenda);
		
		SummaryAgenda depSummaryAgenda = factory.createSummaryAgenda("Departure_place_SumAgenda");
		depSummaryAgenda.setRole(depSummaryAgenda.getRole(), "collection");
		depSummaryAgenda.setType(depSummaryAgenda.getTypeString(), "request");
		Agenda depAgenda = factory.createAgenda("Departure_place_agenda");
		depAgenda.addMustnot(conDepSem);
		depAgenda.addMustnot(depSemG);
		depAgenda.setRole(depAgenda.getRole(), "collection");
		depAgenda.addSummaryAgenda(depSummaryAgenda);
		depSummaryAgenda.addSummarizedAgenda(depAgenda);
		
		SummaryAgenda timeSummaryAgenda = factory.createSummaryAgenda("Time_SumAgenda");
		timeSummaryAgenda.setRole(timeSummaryAgenda.getRole(), "collection");
		timeSummaryAgenda.setType(timeSummaryAgenda.getTypeString(), "request");
		Agenda timeAgenda = factory.createAgenda("Time_agenda");
		timeAgenda.addMustnot(conTimeSem);
		timeAgenda.addMustnot(timeSemG);
		timeAgenda.setRole(timeAgenda.getRole(), "collection");
		timeAgenda.addSummaryAgenda(timeSummaryAgenda);
		timeSummaryAgenda.addSummarizedAgenda(timeAgenda);
		
		SummaryAgenda busSummaryAgenda = factory.createSummaryAgenda("Bus_route_SumAgenda");
		busSummaryAgenda.setRole(busSummaryAgenda.getRole(), "collection");
		busSummaryAgenda.setType(busSummaryAgenda.getTypeString(), "request");
		Agenda busAgenda = factory.createAgenda("Bus_route_agenda");
		busAgenda.addMustnot(conBusSem);
		busAgenda.addMustnot(busSemG);
		busAgenda.setRole(busAgenda.getRole(), "collection");
		busAgenda.addSummaryAgenda(busSummaryAgenda);
		busSummaryAgenda.addSummarizedAgenda(busAgenda);
		
		
		// Create Agendas for the User Initiative
		SummaryAgenda openAfterDepSummaryAgenda = factory.createSummaryAgenda("open_afterDep_SumAgenda");
		openAfterDepSummaryAgenda.setRole(openAfterDepSummaryAgenda.getRole(), "collection");
		openAfterDepSummaryAgenda.setType(openAfterDepSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterDep = factory.createAgenda("open_afterDep_agenda");
		openAgenda_afterDep.addRequires(depSemG);
		openAgenda_afterDep.addMustnot(busSemG);
		openAgenda_afterDep.addMustnot(timeSemG);
		openAgenda_afterDep.addMustnot(destSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterDep.addRequires(conDepSem);
			openAgenda_afterDep.addMustnot(conBusSem);
			openAgenda_afterDep.addMustnot(conTimeSem);
			openAgenda_afterDep.addMustnot(conDestSem);
		}
		openAgenda_afterDep.setRole(openAgenda_afterDep.getRole(), "collection");
		openAgenda_afterDep.addSummaryAgenda(openAfterDepSummaryAgenda);
		openAfterDepSummaryAgenda.addSummarizedAgenda(openAgenda_afterDep);

		SummaryAgenda openAfterDestSummaryAgenda = factory.createSummaryAgenda("open_afterDest_SumAgenda");
		openAfterDestSummaryAgenda.setRole(openAfterDestSummaryAgenda.getRole(), "collection");
		openAfterDestSummaryAgenda.setType(openAfterDestSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterDest = factory.createAgenda("open_afterDest_agenda");
		openAgenda_afterDest.addRequires(destSemG);
		openAgenda_afterDest.addMustnot(busSemG);
		openAgenda_afterDest.addMustnot(timeSemG);
		openAgenda_afterDest.addMustnot(depSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterDest.addRequires(conDestSem);
			openAgenda_afterDest.addMustnot(conBusSem);
			openAgenda_afterDest.addMustnot(conTimeSem);
			openAgenda_afterDest.addMustnot(conDepSem);
		}
		openAgenda_afterDest.setRole(openAgenda_afterDest.getRole(), "collection");
		openAgenda_afterDest.addSummaryAgenda(openAfterDestSummaryAgenda);
		openAfterDestSummaryAgenda.addSummarizedAgenda(openAgenda_afterDest);

		SummaryAgenda openAfterTimeSummaryAgenda = factory.createSummaryAgenda("open_afterTime_SumAgenda");
		openAfterTimeSummaryAgenda.setRole(openAfterTimeSummaryAgenda.getRole(), "collection");
		openAfterTimeSummaryAgenda.setType(openAfterTimeSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterTime = factory.createAgenda("open_afterTime_agenda");
		openAgenda_afterTime.addRequires(timeSemG);
		openAgenda_afterTime.addMustnot(busSemG);
		openAgenda_afterTime.addMustnot(destSemG);
		openAgenda_afterTime.addMustnot(depSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterTime.addRequires(conTimeSem);
			openAgenda_afterTime.addMustnot(conBusSem);
			openAgenda_afterTime.addMustnot(conDestSem);
			openAgenda_afterTime.addMustnot(conDepSem);
		}
		openAgenda_afterTime.setRole(openAgenda_afterTime.getRole(), "collection");
		openAgenda_afterTime.addSummaryAgenda(openAfterTimeSummaryAgenda);
		openAfterTimeSummaryAgenda.addSummarizedAgenda(openAgenda_afterTime);

		SummaryAgenda openAfterBusSummaryAgenda = factory.createSummaryAgenda("open_afterBus_SumAgenda");
		openAfterBusSummaryAgenda.setRole(openAfterBusSummaryAgenda.getRole(), "collection");
		openAfterBusSummaryAgenda.setType(openAfterBusSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBus = factory.createAgenda("open_afterBus_agenda");
		openAgenda_afterBus.addRequires(busSemG);
		openAgenda_afterBus.addMustnot(timeSemG);
		openAgenda_afterBus.addMustnot(destSemG);
		openAgenda_afterBus.addMustnot(depSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBus.addRequires(conBusSem);
			openAgenda_afterBus.addMustnot(conTimeSem);
			openAgenda_afterBus.addMustnot(conDestSem);
			openAgenda_afterBus.addMustnot(conDepSem);
		}
		openAgenda_afterBus.setRole(openAgenda_afterBus.getRole(), "collection");
		openAgenda_afterBus.addSummaryAgenda(openAfterBusSummaryAgenda);
		openAfterBusSummaryAgenda.addSummarizedAgenda(openAgenda_afterBus);

		SummaryAgenda openAfterBusDepSummaryAgenda = factory.createSummaryAgenda("open_afterBusDep_SumAgenda");
		openAfterBusDepSummaryAgenda.setRole(openAfterBusDepSummaryAgenda.getRole(), "collection");
		openAfterBusDepSummaryAgenda.setType(openAfterBusDepSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBusDep = factory.createAgenda("open_afterBusDep_agenda");
		openAgenda_afterBusDep.addRequires(busSemG);
		openAgenda_afterBusDep.addRequires(depSemG);
		openAgenda_afterBusDep.addMustnot(timeSemG);
		openAgenda_afterBusDep.addMustnot(destSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBusDep.addRequires(conBusSem);
			openAgenda_afterBusDep.addRequires(conDepSem);
			openAgenda_afterBusDep.addMustnot(conTimeSem);
			openAgenda_afterBusDep.addMustnot(conDestSem);
		}
		openAgenda_afterBusDep.setRole(openAgenda_afterBusDep.getRole(), "collection");
		openAgenda_afterBusDep.addSummaryAgenda(openAfterBusDepSummaryAgenda);
		openAfterBusDepSummaryAgenda.addSummarizedAgenda(openAgenda_afterBusDep);

		SummaryAgenda openAfterBusDestSummaryAgenda = factory.createSummaryAgenda("open_afterBusDest_SumAgenda");
		openAfterBusDestSummaryAgenda.setRole(openAfterBusDestSummaryAgenda.getRole(), "collection");
		openAfterBusDestSummaryAgenda.setType(openAfterBusDestSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBusDest = factory.createAgenda("open_afterBusDest_agenda");
		openAgenda_afterBusDest.addRequires(busSemG);
		openAgenda_afterBusDest.addRequires(destSemG);
		openAgenda_afterBusDest.addMustnot(depSemG);
		openAgenda_afterBusDest.addMustnot(timeSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBusDest.addRequires(conBusSem);
			openAgenda_afterBusDest.addRequires(conDestSem);
			openAgenda_afterBusDest.addMustnot(conDepSem);
			openAgenda_afterBusDest.addMustnot(conTimeSem);
		}
		openAgenda_afterBusDest.setRole(openAgenda_afterBusDest.getRole(), "collection");
		openAgenda_afterBusDest.addSummaryAgenda(openAfterBusDestSummaryAgenda);
		openAfterBusDestSummaryAgenda.addSummarizedAgenda(openAgenda_afterBusDest);

		SummaryAgenda openAfterBusTimeSummaryAgenda = factory.createSummaryAgenda("open_afterBusTime_SumAgenda");
		openAfterBusTimeSummaryAgenda.setRole(openAfterBusTimeSummaryAgenda.getRole(), "collection");
		openAfterBusTimeSummaryAgenda.setType(openAfterBusTimeSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBusTime = factory.createAgenda("open_afterBusTime_agenda");
		openAgenda_afterBusTime.addRequires(busSemG);
		openAgenda_afterBusTime.addRequires(timeSemG);
		openAgenda_afterBusTime.addMustnot(destSemG);
		openAgenda_afterBusTime.addMustnot(depSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBusTime.addRequires(conBusSem);
			openAgenda_afterBusTime.addRequires(conTimeSem);
			openAgenda_afterBusTime.addMustnot(conDestSem);
			openAgenda_afterBusTime.addMustnot(conDepSem);
		}
		openAgenda_afterBusTime.setRole(openAgenda_afterBusTime.getRole(), "collection");
		openAgenda_afterBusTime.addSummaryAgenda(openAfterBusTimeSummaryAgenda);
		openAfterBusTimeSummaryAgenda.addSummarizedAgenda(openAgenda_afterBusTime);

		SummaryAgenda openAfterTimeDepSummaryAgenda = factory.createSummaryAgenda("open_afterTimeDep_SumAgenda");
		openAfterTimeDepSummaryAgenda.setRole(openAfterTimeDepSummaryAgenda.getRole(), "collection");
		openAfterTimeDepSummaryAgenda.setType(openAfterTimeDepSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterTimeDep = factory.createAgenda("open_afterTimeDep_agenda");
		openAgenda_afterTimeDep.addRequires(timeSemG);
		openAgenda_afterTimeDep.addRequires(depSemG);
		openAgenda_afterTimeDep.addMustnot(busSemG);
		openAgenda_afterTimeDep.addMustnot(destSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterTimeDep.addRequires(conTimeSem);
			openAgenda_afterTimeDep.addRequires(conDepSem);
			openAgenda_afterTimeDep.addMustnot(conBusSem);
			openAgenda_afterTimeDep.addMustnot(conDestSem);
		}
		openAgenda_afterTimeDep.setRole(openAgenda_afterTimeDep.getRole(), "collection");
		openAgenda_afterTimeDep.addSummaryAgenda(openAfterTimeDepSummaryAgenda);
		openAfterTimeDepSummaryAgenda.addSummarizedAgenda(openAgenda_afterTimeDep);

		SummaryAgenda openAfterTimeDestSummaryAgenda = factory.createSummaryAgenda("open_afterTimeDest_SumAgenda");
		openAfterTimeDestSummaryAgenda.setRole(openAfterTimeDestSummaryAgenda.getRole(), "collection");
		openAfterTimeDestSummaryAgenda.setType(openAfterTimeDestSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterTimeDest = factory.createAgenda("open_afterTimeDest_agenda");
		openAgenda_afterTimeDest.addRequires(timeSemG);
		openAgenda_afterTimeDest.addRequires(destSemG);
		openAgenda_afterTimeDest.addMustnot(depSemG);
		openAgenda_afterTimeDest.addMustnot(busSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterTimeDest.addRequires(conTimeSem);
			openAgenda_afterTimeDest.addRequires(conDestSem);
			openAgenda_afterTimeDest.addMustnot(conDepSem);
			openAgenda_afterTimeDest.addMustnot(conBusSem);
		}
		openAgenda_afterTimeDest.setRole(openAgenda_afterTimeDest.getRole(), "collection");
		openAgenda_afterTimeDest.addSummaryAgenda(openAfterTimeDestSummaryAgenda);
		openAfterTimeDestSummaryAgenda.addSummarizedAgenda(openAgenda_afterTimeDest);

		SummaryAgenda openAfterDepDestSummaryAgenda = factory.createSummaryAgenda("open_afterDepDest_SumAgenda");
		openAfterDepDestSummaryAgenda.setRole(openAfterDepDestSummaryAgenda.getRole(), "collection");
		openAfterDepDestSummaryAgenda.setType(openAfterDepDestSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterDepDest = factory.createAgenda("open_afterDepDest_agenda");
		openAgenda_afterDepDest.addRequires(depSemG);
		openAgenda_afterDepDest.addRequires(destSemG);
		openAgenda_afterDepDest.addMustnot(timeSemG);
		openAgenda_afterDepDest.addMustnot(busSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterDepDest.addRequires(conDepSem);
			openAgenda_afterDepDest.addRequires(conDestSem);
			openAgenda_afterDepDest.addMustnot(conTimeSem);
			openAgenda_afterDepDest.addMustnot(conBusSem);
		}
		openAgenda_afterDepDest.setRole(openAgenda_afterDepDest.getRole(), "collection");
		openAgenda_afterDepDest.addSummaryAgenda(openAfterDepDestSummaryAgenda);
		openAfterDepDestSummaryAgenda.addSummarizedAgenda(openAgenda_afterDepDest);

		SummaryAgenda openAfterBusTimeDepSummaryAgenda = factory.createSummaryAgenda("open_afterBusTimeDep_SumAgenda");
		openAfterBusTimeDepSummaryAgenda.setRole(openAfterBusTimeDepSummaryAgenda.getRole(), "collection");
		openAfterBusTimeDepSummaryAgenda.setType(openAfterBusTimeDepSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBusTimeDep = factory.createAgenda("open_afterBusTimeDep_agenda");
		openAgenda_afterBusTimeDep.addRequires(busSemG);
		openAgenda_afterBusTimeDep.addRequires(timeSemG);
		openAgenda_afterBusTimeDep.addRequires(depSemG);
		openAgenda_afterBusTimeDep.addMustnot(destSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBusTimeDep.addRequires(conBusSem);
			openAgenda_afterBusTimeDep.addRequires(conTimeSem);
			openAgenda_afterBusTimeDep.addRequires(conDepSem);
			openAgenda_afterBusTimeDep.addMustnot(conDestSem);
		}
		openAgenda_afterBusTimeDep.setRole(openAgenda_afterBusTimeDep.getRole(), "collection");
		openAgenda_afterBusTimeDep.addSummaryAgenda(openAfterBusTimeDepSummaryAgenda);
		openAfterBusTimeDepSummaryAgenda.addSummarizedAgenda(openAgenda_afterBusTimeDep);

		SummaryAgenda openAfterBusTimeDestSummaryAgenda = factory.createSummaryAgenda("open_afterBusTimeDest_SumAgenda");
		openAfterBusTimeDestSummaryAgenda.setRole(openAfterBusTimeDestSummaryAgenda.getRole(), "collection");
		openAfterBusTimeDestSummaryAgenda.setType(openAfterBusTimeDestSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBusTimeDest = factory.createAgenda("open_afterBusTimeDest_agenda");
		openAgenda_afterBusTimeDest.addRequires(busSemG);
		openAgenda_afterBusTimeDest.addRequires(timeSemG);
		openAgenda_afterBusTimeDest.addRequires(destSemG);
		openAgenda_afterBusTimeDest.addMustnot(depSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBusTimeDest.addRequires(conBusSem);
			openAgenda_afterBusTimeDest.addRequires(conTimeSem);
			openAgenda_afterBusTimeDest.addRequires(conDestSem);
			openAgenda_afterBusTimeDest.addMustnot(conDepSem);
		}
		openAgenda_afterBusTimeDest.setRole(openAgenda_afterBusTimeDest.getRole(), "collection");
		openAgenda_afterBusTimeDest.addSummaryAgenda(openAfterBusTimeDestSummaryAgenda);
		openAfterBusTimeDestSummaryAgenda.addSummarizedAgenda(openAgenda_afterBusTimeDest);

		SummaryAgenda openAfterBusDepDestSummaryAgenda = factory.createSummaryAgenda("open_afterBusDepDest_SumAgenda");
		openAfterBusDepDestSummaryAgenda.setRole(openAfterBusDepDestSummaryAgenda.getRole(), "collection");
		openAfterBusDepDestSummaryAgenda.setType(openAfterBusDepDestSummaryAgenda.getTypeString(), "request");
		Agenda openAgenda_afterBusDepDest = factory.createAgenda("open_afterBusDepDest_agenda");
		openAgenda_afterBusDepDest.addRequires(busSemG);
		openAgenda_afterBusDepDest.addRequires(depSemG);
		openAgenda_afterBusDepDest.addRequires(destSemG);
		openAgenda_afterBusDepDest.addMustnot(timeSemG);
		if (confirmationMandatory) 
		{
			openAgenda_afterBusDepDest.addRequires(conBusSem);
			openAgenda_afterBusDepDest.addRequires(conDepSem);
			openAgenda_afterBusDepDest.addRequires(conDestSem);
			openAgenda_afterBusDepDest.addMustnot(conTimeSem);
		}
		openAgenda_afterBusDepDest.setRole(openAgenda_afterBusDepDest.getRole(), "collection");
		openAgenda_afterBusDepDest.addSummaryAgenda(openAfterBusDepDestSummaryAgenda);
		openAfterBusDepDestSummaryAgenda.addSummarizedAgenda(openAgenda_afterBusDepDest);
	
		
		// Create Agendas for the Mixed Initiative
		SummaryAgenda destinationWithoutDepSummaryAgenda = factory.createSummaryAgenda("Destination_withoutDep_SumAgenda");
		destinationWithoutDepSummaryAgenda.setRole(destinationWithoutDepSummaryAgenda.getRole(), "collection");
		destinationWithoutDepSummaryAgenda.setType(destinationWithoutDepSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutDep = factory.createAgenda("Destination_withoutDep_agenda");
		destAgendaWithoutDep.addMustnot(destSemG);
		destAgendaWithoutDep.addMustnot(timeSemG);
		destAgendaWithoutDep.addMustnot(busSemG);
		destAgendaWithoutDep.addRequires(depSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutDep.addMustnot(conDestSem);
			destAgendaWithoutDep.addMustnot(conTimeSem);
			destAgendaWithoutDep.addMustnot(conBusSem);
			destAgendaWithoutDep.addRequires(conDepSem);
		}
		destAgendaWithoutDep.setRole(destAgendaWithoutDep.getRole(), "collection");
		destAgendaWithoutDep.addSummaryAgenda(destinationWithoutDepSummaryAgenda);
		destinationWithoutDepSummaryAgenda.addSummarizedAgenda(destAgendaWithoutDep);

		SummaryAgenda destinationWithoutTimeSummaryAgenda = factory.createSummaryAgenda("Destination_withoutTime_SumAgenda");
		destinationWithoutTimeSummaryAgenda.setRole(destinationWithoutTimeSummaryAgenda.getRole(), "collection");
		destinationWithoutTimeSummaryAgenda.setType(destinationWithoutTimeSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutTime = factory.createAgenda("Destination_withoutTime_agenda");
		destAgendaWithoutTime.addMustnot(destSemG);
		destAgendaWithoutTime.addMustnot(depSemG);
		destAgendaWithoutTime.addMustnot(busSemG);
		destAgendaWithoutTime.addRequires(timeSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutTime.addMustnot(conDestSem);
			destAgendaWithoutTime.addMustnot(conDepSem);
			destAgendaWithoutTime.addMustnot(conBusSem);
			destAgendaWithoutTime.addRequires(conTimeSem);
		}
		destAgendaWithoutTime.setRole(destAgendaWithoutTime.getRole(), "collection");
		destAgendaWithoutTime.addSummaryAgenda(destinationWithoutTimeSummaryAgenda);
		destinationWithoutTimeSummaryAgenda.addSummarizedAgenda(destAgendaWithoutTime);

		SummaryAgenda destinationWithoutBusSummaryAgenda = factory.createSummaryAgenda("Destination_withoutBus_SumAgenda");
		destinationWithoutBusSummaryAgenda.setRole(destinationWithoutBusSummaryAgenda.getRole(), "collection");
		destinationWithoutBusSummaryAgenda.setType(destinationWithoutBusSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutBus = factory.createAgenda("Destination_withoutBus_agenda");
		destAgendaWithoutBus.addMustnot(destSemG);
		destAgendaWithoutBus.addMustnot(depSemG);
		destAgendaWithoutBus.addMustnot(timeSemG);
		destAgendaWithoutBus.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutBus.addMustnot(conDestSem);
			destAgendaWithoutBus.addMustnot(conDepSem);
			destAgendaWithoutBus.addMustnot(conTimeSem);
			destAgendaWithoutBus.addRequires(conBusSem);
		}
		destAgendaWithoutBus.setRole(destAgendaWithoutBus.getRole(), "collection");
		destAgendaWithoutBus.addSummaryAgenda(destinationWithoutBusSummaryAgenda);
		destinationWithoutBusSummaryAgenda.addSummarizedAgenda(destAgendaWithoutBus);
		
		SummaryAgenda destinationWithoutTimeDepSummaryAgenda = factory.createSummaryAgenda("Destination_withoutTimeDep_SumAgenda");
		destinationWithoutTimeDepSummaryAgenda.setRole(destinationWithoutTimeDepSummaryAgenda.getRole(), "collection");
		destinationWithoutTimeDepSummaryAgenda.setType(destinationWithoutTimeDepSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutTimeDep = factory.createAgenda("Destination_withoutTimeDep_agenda");
		destAgendaWithoutTimeDep.addMustnot(destSemG);
		destAgendaWithoutTimeDep.addMustnot(busSemG);
		destAgendaWithoutTimeDep.addRequires(timeSemG);
		destAgendaWithoutTimeDep.addRequires(depSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutTimeDep.addMustnot(conDestSem);
			destAgendaWithoutTimeDep.addMustnot(conBusSem);
			destAgendaWithoutTimeDep.addRequires(conTimeSem);
			destAgendaWithoutTimeDep.addRequires(conDepSem);
		}
		destAgendaWithoutTimeDep.setRole(destAgendaWithoutTimeDep.getRole(), "collection");
		destAgendaWithoutTimeDep.addSummaryAgenda(destinationWithoutTimeDepSummaryAgenda);
		destinationWithoutTimeDepSummaryAgenda.addSummarizedAgenda(destAgendaWithoutTimeDep);

		SummaryAgenda destinationWithoutBusDepSummaryAgenda = factory.createSummaryAgenda("Destination_withoutBusDep_SumAgenda");
		destinationWithoutBusDepSummaryAgenda.setRole(destinationWithoutBusDepSummaryAgenda.getRole(), "collection");
		destinationWithoutBusDepSummaryAgenda.setType(destinationWithoutBusDepSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutBusDep = factory.createAgenda("Destination_withoutBusDep_agenda");
		destAgendaWithoutBusDep.addMustnot(destSemG);
		destAgendaWithoutBusDep.addMustnot(timeSemG);
		destAgendaWithoutBusDep.addRequires(busSemG);
		destAgendaWithoutBusDep.addRequires(depSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutBusDep.addMustnot(conDestSem);
			destAgendaWithoutBusDep.addMustnot(conTimeSem);
			destAgendaWithoutBusDep.addRequires(conBusSem);
			destAgendaWithoutBusDep.addRequires(conDepSem);
		}
		destAgendaWithoutBusDep.setRole(destAgendaWithoutBusDep.getRole(), "collection");
		destAgendaWithoutBusDep.addSummaryAgenda(destinationWithoutBusDepSummaryAgenda);
		destinationWithoutBusDepSummaryAgenda.addSummarizedAgenda(destAgendaWithoutBusDep);

		SummaryAgenda destinationWithoutBusTimeSummaryAgenda = factory.createSummaryAgenda("Destination_withoutBusTime_SumAgenda");
		destinationWithoutBusTimeSummaryAgenda.setRole(destinationWithoutBusTimeSummaryAgenda.getRole(), "collection");
		destinationWithoutBusTimeSummaryAgenda.setType(destinationWithoutBusTimeSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutBusTime = factory.createAgenda("Destination_withoutBusTime_agenda");
		destAgendaWithoutBusTime.addMustnot(destSemG);
		destAgendaWithoutBusTime.addMustnot(depSemG);
		destAgendaWithoutBusTime.addRequires(busSemG);
		destAgendaWithoutBusTime.addRequires(timeSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutBusTime.addMustnot(conDestSem);
			destAgendaWithoutBusTime.addMustnot(conDepSem);
			destAgendaWithoutBusTime.addRequires(conBusSem);
			destAgendaWithoutBusTime.addRequires(conTimeSem);
		}
		destAgendaWithoutBusTime.setRole(destAgendaWithoutBusTime.getRole(), "collection");
		destAgendaWithoutBusTime.addSummaryAgenda(destinationWithoutBusTimeSummaryAgenda);
		destinationWithoutBusTimeSummaryAgenda.addSummarizedAgenda(destAgendaWithoutBusTime);

		SummaryAgenda destinationWithoutBusTimeDepSummaryAgenda = factory.createSummaryAgenda("Destination_withoutBusTimeDep_SumAgenda");
		destinationWithoutBusTimeDepSummaryAgenda.setRole(destinationWithoutBusTimeDepSummaryAgenda.getRole(), "collection");
		destinationWithoutBusTimeDepSummaryAgenda.setType(destinationWithoutBusTimeDepSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaWithoutBusTimeDep = factory.createAgenda("Destination_withoutBusTimeDep_agenda");
		destAgendaWithoutBusTimeDep.addMustnot(destSemG);
		destAgendaWithoutBusTimeDep.addRequires(depSemG);
		destAgendaWithoutBusTimeDep.addRequires(busSemG);
		destAgendaWithoutBusTimeDep.addRequires(timeSemG);
		if (confirmationMandatory) 
		{
			destAgendaWithoutBusTimeDep.addMustnot(conDestSem);
			destAgendaWithoutBusTimeDep.addRequires(conDepSem);
			destAgendaWithoutBusTimeDep.addRequires(conBusSem);
			destAgendaWithoutBusTimeDep.addRequires(conTimeSem);
		}
		destAgendaWithoutBusTimeDep.setRole(destAgendaWithoutBusTimeDep.getRole(), "collection");
		destAgendaWithoutBusTimeDep.addSummaryAgenda(destinationWithoutBusTimeDepSummaryAgenda);
		destinationWithoutBusTimeDepSummaryAgenda.addSummarizedAgenda(destAgendaWithoutBusTimeDep);

		SummaryAgenda destinationAllSummaryAgenda = factory.createSummaryAgenda("DestinationAll_SumAgenda");
		destinationAllSummaryAgenda.setRole(destinationAllSummaryAgenda.getRole(), "collection");
		destinationAllSummaryAgenda.setType(destinationAllSummaryAgenda.getTypeString(), "request");
		Agenda destAgendaAll = factory.createAgenda("DestinationAll_agenda");
		destAgendaAll.addMustnot(timeSemG);
		destAgendaAll.addMustnot(depSemG);
		destAgendaAll.addMustnot(destSemG);
		destAgendaAll.addMustnot(busSemG);
		if (confirmationMandatory) 
		{
			destAgendaAll.addMustnot(conTimeSem);
			destAgendaAll.addMustnot(conDepSem);
			destAgendaAll.addMustnot(conDestSem);
			destAgendaAll.addMustnot(conBusSem);
		}
		destAgendaAll.setRole(destAgendaAll.getRole(), "collection");
		destAgendaAll.addSummaryAgenda(destinationAllSummaryAgenda);
		destinationAllSummaryAgenda.addSummarizedAgenda(destAgendaAll);
		
		SummaryAgenda depplaceWithoutDestSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutDest_SumAgenda");
		depplaceWithoutDestSummaryAgenda.setRole(depplaceWithoutDestSummaryAgenda.getRole(), "collection");
		depplaceWithoutDestSummaryAgenda.setType(depplaceWithoutDestSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutDest = factory.createAgenda("Depplace_withoutDest_agenda");
		depAgendaWithoutDest.addMustnot(depSemG);
		depAgendaWithoutDest.addMustnot(timeSemG);
		depAgendaWithoutDest.addMustnot(busSemG);
		depAgendaWithoutDest.addRequires(destSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutDest.addMustnot(conDepSem);
			depAgendaWithoutDest.addMustnot(conTimeSem);
			depAgendaWithoutDest.addMustnot(conBusSem);
			depAgendaWithoutDest.addRequires(conDestSem);
		}
		depAgendaWithoutDest.setRole(depAgendaWithoutDest.getRole(), "collection");
		depAgendaWithoutDest.addSummaryAgenda(depplaceWithoutDestSummaryAgenda);
		depplaceWithoutDestSummaryAgenda.addSummarizedAgenda(depAgendaWithoutDest);

		SummaryAgenda depplaceWithoutTimeSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutTime_SumAgenda");
		depplaceWithoutTimeSummaryAgenda.setRole(depplaceWithoutTimeSummaryAgenda.getRole(), "collection");
		depplaceWithoutTimeSummaryAgenda.setType(depplaceWithoutTimeSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutTime = factory.createAgenda("Depplace_withoutTime_agenda");
		depAgendaWithoutTime.addMustnot(depSemG);
		depAgendaWithoutTime.addMustnot(destSemG);
		depAgendaWithoutTime.addMustnot(busSemG);
		depAgendaWithoutTime.addRequires(timeSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutTime.addMustnot(conDepSem);
			depAgendaWithoutTime.addMustnot(conDestSem);
			depAgendaWithoutTime.addMustnot(conBusSem);
			depAgendaWithoutTime.addRequires(conTimeSem);
		}
		depAgendaWithoutTime.setRole(depAgendaWithoutTime.getRole(), "collection");
		depAgendaWithoutTime.addSummaryAgenda(depplaceWithoutTimeSummaryAgenda);
		depplaceWithoutTimeSummaryAgenda.addSummarizedAgenda(depAgendaWithoutTime);

		SummaryAgenda depplaceWithoutBusSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutBus_SumAgenda");
		depplaceWithoutBusSummaryAgenda.setRole(depplaceWithoutBusSummaryAgenda.getRole(), "collection");
		depplaceWithoutBusSummaryAgenda.setType(depplaceWithoutBusSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutBus = factory.createAgenda("Depplace_withoutBus_agenda");
		depAgendaWithoutBus.addMustnot(destSemG);
		depAgendaWithoutBus.addMustnot(depSemG);
		depAgendaWithoutBus.addMustnot(timeSemG);
		depAgendaWithoutBus.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutBus.addMustnot(conDestSem);
			depAgendaWithoutBus.addMustnot(conDepSem);
			depAgendaWithoutBus.addMustnot(conTimeSem);
			depAgendaWithoutBus.addRequires(conBusSem);
		}
		depAgendaWithoutBus.setRole(depAgendaWithoutBus.getRole(), "collection");
		depAgendaWithoutBus.addSummaryAgenda(depplaceWithoutBusSummaryAgenda);
		depplaceWithoutBusSummaryAgenda.addSummarizedAgenda(depAgendaWithoutBus);
		
		SummaryAgenda depplaceWithoutTimeDestSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutTimeDest_SumAgenda");
		depplaceWithoutTimeDestSummaryAgenda.setRole(depplaceWithoutTimeDestSummaryAgenda.getRole(), "collection");
		depplaceWithoutTimeDestSummaryAgenda.setType(depplaceWithoutTimeDestSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutTimeDest = factory.createAgenda("Depplace_withoutTimeDest_agenda");
		depAgendaWithoutTimeDest.addMustnot(depSemG);
		depAgendaWithoutTimeDest.addMustnot(busSemG);
		depAgendaWithoutTimeDest.addRequires(timeSemG);
		depAgendaWithoutTimeDest.addRequires(destSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutTimeDest.addMustnot(conDepSem);
			depAgendaWithoutTimeDest.addMustnot(conBusSem);
			depAgendaWithoutTimeDest.addRequires(conTimeSem);
			depAgendaWithoutTimeDest.addRequires(conDestSem);
		}
		depAgendaWithoutTimeDest.setRole(depAgendaWithoutTimeDest.getRole(), "collection");
		depAgendaWithoutTimeDest.addSummaryAgenda(depplaceWithoutTimeDestSummaryAgenda);
		depplaceWithoutTimeDestSummaryAgenda.addSummarizedAgenda(depAgendaWithoutTimeDest);

		SummaryAgenda depplaceWithoutBusDestSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutBusDest_SumAgenda");
		depplaceWithoutBusDestSummaryAgenda.setRole(depplaceWithoutBusDestSummaryAgenda.getRole(), "collection");
		depplaceWithoutBusDestSummaryAgenda.setType(depplaceWithoutBusDestSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutBusDest = factory.createAgenda("Depplace_withoutBusDest_agenda");
		depAgendaWithoutBusDest.addMustnot(depSemG);
		depAgendaWithoutBusDest.addMustnot(timeSemG);
		depAgendaWithoutBusDest.addRequires(destSemG);
		depAgendaWithoutBusDest.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutBusDest.addMustnot(conDepSem);
			depAgendaWithoutBusDest.addMustnot(conTimeSem);
			depAgendaWithoutBusDest.addRequires(conDestSem);
			depAgendaWithoutBusDest.addRequires(conBusSem);
		}
		depAgendaWithoutBusDest.setRole(depAgendaWithoutBusDest.getRole(), "collection");
		depAgendaWithoutBusDest.addSummaryAgenda(depplaceWithoutBusDestSummaryAgenda);
		depplaceWithoutBusDestSummaryAgenda.addSummarizedAgenda(depAgendaWithoutBusDest);

		SummaryAgenda depplaceWithoutBusTimeSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutBusTime_SumAgenda");
		depplaceWithoutBusTimeSummaryAgenda.setRole(depplaceWithoutBusTimeSummaryAgenda.getRole(), "collection");
		depplaceWithoutBusTimeSummaryAgenda.setType(depplaceWithoutBusTimeSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutBusTime = factory.createAgenda("Depplace_withoutBusTime_agenda");
		depAgendaWithoutBusTime.addMustnot(depSemG);
		depAgendaWithoutBusTime.addMustnot(destSemG);
		depAgendaWithoutBusTime.addRequires(timeSemG);
		depAgendaWithoutBusTime.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutBusTime.addMustnot(conDepSem);
			depAgendaWithoutBusTime.addMustnot(conDestSem);
			depAgendaWithoutBusTime.addRequires(conTimeSem);
			depAgendaWithoutBusTime.addRequires(conBusSem);
		}
		depAgendaWithoutBusTime.setRole(depAgendaWithoutBusTime.getRole(), "collection");
		depAgendaWithoutBusTime.addSummaryAgenda(depplaceWithoutBusTimeSummaryAgenda);
		depplaceWithoutBusTimeSummaryAgenda.addSummarizedAgenda(depAgendaWithoutBusTime);

		SummaryAgenda depplaceWithoutBusTimeDestSummaryAgenda = factory.createSummaryAgenda("Depplace_withoutBusTimeDest_SumAgenda");
		depplaceWithoutBusTimeDestSummaryAgenda.setRole(depplaceWithoutBusTimeDestSummaryAgenda.getRole(), "collection");
		depplaceWithoutBusTimeDestSummaryAgenda.setType(depplaceWithoutBusTimeDestSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaWithoutBusTimeDest = factory.createAgenda("Depplace_withoutBusTimeDest_agenda");
		depAgendaWithoutBusTimeDest.addMustnot(depSemG);
		depAgendaWithoutBusTimeDest.addRequires(destSemG);
		depAgendaWithoutBusTimeDest.addRequires(timeSemG);
		depAgendaWithoutBusTimeDest.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			depAgendaWithoutBusTimeDest.addMustnot(conDepSem);
			depAgendaWithoutBusTimeDest.addRequires(conDestSem);
			depAgendaWithoutBusTimeDest.addRequires(conTimeSem);
			depAgendaWithoutBusTimeDest.addRequires(conBusSem);
		}
		depAgendaWithoutBusTimeDest.setRole(depAgendaWithoutBusTimeDest.getRole(), "collection");
		depAgendaWithoutBusTimeDest.addSummaryAgenda(depplaceWithoutBusTimeDestSummaryAgenda);
		depplaceWithoutBusTimeDestSummaryAgenda.addSummarizedAgenda(depAgendaWithoutBusTimeDest);

		SummaryAgenda depplaceAllSummaryAgenda = factory.createSummaryAgenda("DepplaceAll_SumAgenda");
		depplaceAllSummaryAgenda.setRole(depplaceAllSummaryAgenda.getRole(), "collection");
		depplaceAllSummaryAgenda.setType(depplaceAllSummaryAgenda.getTypeString(), "request");
		Agenda depAgendaAll = factory.createAgenda("DepplaceAll_agenda");
		depAgendaAll.addMustnot(timeSemG);
		depAgendaAll.addMustnot(depSemG);
		depAgendaAll.addMustnot(destSemG);
		depAgendaAll.addMustnot(busSemG);
		if (confirmationMandatory) 
		{
			depAgendaAll.addMustnot(conTimeSem);
			depAgendaAll.addMustnot(conDepSem);
			depAgendaAll.addMustnot(conDestSem);
			depAgendaAll.addMustnot(conBusSem);
		}
		depAgendaAll.setRole(depAgendaAll.getRole(), "collection");
		depAgendaAll.addSummaryAgenda(depplaceAllSummaryAgenda);
		depplaceAllSummaryAgenda.addSummarizedAgenda(depAgendaAll);

		SummaryAgenda timeWithoutDestSummaryAgenda = factory.createSummaryAgenda("Time_withoutDest_SumAgenda");
		timeWithoutDestSummaryAgenda.setRole(timeWithoutDestSummaryAgenda.getRole(), "collection");
		timeWithoutDestSummaryAgenda.setType(timeWithoutDestSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutDest = factory.createAgenda("Time_withoutDest_agenda");
		timeAgendaWithoutDest.addMustnot(timeSemG);
		timeAgendaWithoutDest.addMustnot(depSemG);
		timeAgendaWithoutDest.addMustnot(busSemG);
		timeAgendaWithoutDest.addRequires(destSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutDest.addMustnot(conTimeSem);
			timeAgendaWithoutDest.addMustnot(conDepSem);
			timeAgendaWithoutDest.addMustnot(conBusSem);
			timeAgendaWithoutDest.addRequires(conDestSem);
		}
		timeAgendaWithoutDest.setRole(timeAgendaWithoutDest.getRole(), "collection");
		timeAgendaWithoutDest.addSummaryAgenda(timeWithoutDestSummaryAgenda);
		timeWithoutDestSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutDest);

		SummaryAgenda timeWithoutDepSummaryAgenda = factory.createSummaryAgenda("Time_withoutDep_SumAgenda");
		timeWithoutDepSummaryAgenda.setRole(timeWithoutDepSummaryAgenda.getRole(), "collection");
		timeWithoutDepSummaryAgenda.setType(timeWithoutDepSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutDep = factory.createAgenda("Time_withoutDep_agenda");
		timeAgendaWithoutDep.addMustnot(timeSemG);
		timeAgendaWithoutDep.addMustnot(destSemG);
		timeAgendaWithoutDep.addMustnot(busSemG);
		timeAgendaWithoutDep.addRequires(depSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutDep.addMustnot(conTimeSem);
			timeAgendaWithoutDep.addMustnot(conDestSem);
			timeAgendaWithoutDep.addMustnot(conBusSem);
			timeAgendaWithoutDep.addRequires(conDepSem);
		}
		timeAgendaWithoutDep.setRole(timeAgendaWithoutDep.getRole(), "collection");
		timeAgendaWithoutDep.addSummaryAgenda(timeWithoutDepSummaryAgenda);
		timeWithoutDepSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutDep);
		
		SummaryAgenda timeWithoutBusSummaryAgenda = factory.createSummaryAgenda("Time_withoutBus_SumAgenda");
		timeWithoutBusSummaryAgenda.setRole(timeWithoutBusSummaryAgenda.getRole(), "collection");
		timeWithoutBusSummaryAgenda.setType(timeWithoutBusSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutBus = factory.createAgenda("Time_withoutBus_agenda");
		timeAgendaWithoutBus.addMustnot(destSemG);
		timeAgendaWithoutBus.addMustnot(depSemG);
		timeAgendaWithoutBus.addMustnot(timeSemG);
		timeAgendaWithoutBus.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutBus.addMustnot(conDestSem);
			timeAgendaWithoutBus.addMustnot(conDepSem);
			timeAgendaWithoutBus.addMustnot(conTimeSem);
			timeAgendaWithoutBus.addRequires(conBusSem);
		}
		timeAgendaWithoutBus.setRole(timeAgendaWithoutBus.getRole(), "collection");
		timeAgendaWithoutBus.addSummaryAgenda(timeWithoutBusSummaryAgenda);
		timeWithoutBusSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutBus);

		SummaryAgenda timeWithoutDepDestSummaryAgenda = factory.createSummaryAgenda("Time_withoutDepDest_SumAgenda");
		timeWithoutDepDestSummaryAgenda.setRole(timeWithoutDepDestSummaryAgenda.getRole(), "collection");
		timeWithoutDepDestSummaryAgenda.setType(timeWithoutDepDestSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutDepDest = factory.createAgenda("Time_withoutDepDest_agenda");
		timeAgendaWithoutDepDest.addMustnot(timeSemG);
		timeAgendaWithoutDepDest.addMustnot(busSemG);
		timeAgendaWithoutDepDest.addRequires(depSemG);
		timeAgendaWithoutDepDest.addRequires(destSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutDepDest.addMustnot(conTimeSem);
			timeAgendaWithoutDepDest.addMustnot(conBusSem);
			timeAgendaWithoutDepDest.addRequires(conDepSem);
			timeAgendaWithoutDepDest.addRequires(conDestSem);
		}
		timeAgendaWithoutDepDest.setRole(timeAgendaWithoutDepDest.getRole(), "collection");
		timeAgendaWithoutDepDest.addSummaryAgenda(timeWithoutDepDestSummaryAgenda);
		timeWithoutDepDestSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutDepDest);

		SummaryAgenda timeWithoutBusDestSummaryAgenda = factory.createSummaryAgenda("Time_withoutBusDest_SumAgenda");
		timeWithoutBusDestSummaryAgenda.setRole(timeWithoutBusDestSummaryAgenda.getRole(), "collection");
		timeWithoutBusDestSummaryAgenda.setType(timeWithoutBusDestSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutBusDest = factory.createAgenda("Time_withoutBusDest_agenda");
		timeAgendaWithoutBusDest.addMustnot(depSemG);
		timeAgendaWithoutBusDest.addMustnot(timeSemG);
		timeAgendaWithoutBusDest.addRequires(destSemG);
		timeAgendaWithoutBusDest.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutBusDest.addMustnot(conDepSem);
			timeAgendaWithoutBusDest.addMustnot(conTimeSem);
			timeAgendaWithoutBusDest.addRequires(conDestSem);
			timeAgendaWithoutBusDest.addRequires(conBusSem);
		}
		timeAgendaWithoutBusDest.setRole(timeAgendaWithoutBusDest.getRole(), "collection");
		timeAgendaWithoutBusDest.addSummaryAgenda(timeWithoutBusDestSummaryAgenda);
		timeWithoutBusDestSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutBusDest);

		SummaryAgenda timeWithoutBusDepSummaryAgenda = factory.createSummaryAgenda("Time_withoutBusDep_SumAgenda");
		timeWithoutBusDepSummaryAgenda.setRole(timeWithoutBusDepSummaryAgenda.getRole(), "collection");
		timeWithoutBusDepSummaryAgenda.setType(timeWithoutBusDepSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutBusDep = factory.createAgenda("Time_withoutBusDep_agenda");
		timeAgendaWithoutBusDep.addMustnot(destSemG);
		timeAgendaWithoutBusDep.addMustnot(timeSemG);
		timeAgendaWithoutBusDep.addRequires(depSemG);
		timeAgendaWithoutBusDep.addRequires(busSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutBusDep.addMustnot(conDestSem);
			timeAgendaWithoutBusDep.addMustnot(conTimeSem);
			timeAgendaWithoutBusDep.addRequires(conDepSem);
			timeAgendaWithoutBusDep.addRequires(conBusSem);
		}
		timeAgendaWithoutBusDep.setRole(timeAgendaWithoutBusDep.getRole(), "collection");
		timeAgendaWithoutBusDep.addSummaryAgenda(timeWithoutBusDepSummaryAgenda);
		timeWithoutBusDepSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutBusDep);

		SummaryAgenda timeWithoutBusDepDestSummaryAgenda = factory.createSummaryAgenda("Time_withoutBusDepDest_SumAgenda");
		timeWithoutBusDepDestSummaryAgenda.setRole(timeWithoutBusDepDestSummaryAgenda.getRole(), "collection");
		timeWithoutBusDepDestSummaryAgenda.setType(timeWithoutBusDepDestSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaWithoutBusDepDest = factory.createAgenda("Time_withoutBusDepDest_agenda");
		timeAgendaWithoutBusDepDest.addMustnot(timeSemG);
		timeAgendaWithoutBusDepDest.addRequires(depSemG);
		timeAgendaWithoutBusDepDest.addRequires(busSemG);
		timeAgendaWithoutBusDepDest.addRequires(destSemG);
		if (confirmationMandatory) 
		{
			timeAgendaWithoutBusDepDest.addMustnot(conTimeSem);
			timeAgendaWithoutBusDepDest.addRequires(conDepSem);
			timeAgendaWithoutBusDepDest.addRequires(conBusSem);
			timeAgendaWithoutBusDepDest.addRequires(conDestSem);
		}
		timeAgendaWithoutBusDepDest.setRole(timeAgendaWithoutBusDepDest.getRole(), "collection");
		timeAgendaWithoutBusDepDest.addSummaryAgenda(timeWithoutBusDepDestSummaryAgenda);
		timeWithoutBusDepDestSummaryAgenda.addSummarizedAgenda(timeAgendaWithoutBusDepDest);

		SummaryAgenda timeAllSummaryAgenda = factory.createSummaryAgenda("TimeAll_SumAgenda");
		timeAllSummaryAgenda.setRole(timeAllSummaryAgenda.getRole(), "collection");
		timeAllSummaryAgenda.setType(timeAllSummaryAgenda.getTypeString(), "request");
		Agenda timeAgendaAll = factory.createAgenda("TimeAll_agenda");
		timeAgendaAll.addMustnot(timeSemG);
		timeAgendaAll.addMustnot(depSemG);
		timeAgendaAll.addMustnot(destSemG);
		timeAgendaAll.addMustnot(busSemG);
		if (confirmationMandatory) 
		{
			timeAgendaAll.addMustnot(conTimeSem);
			timeAgendaAll.addMustnot(conDepSem);
			timeAgendaAll.addMustnot(conDestSem);
			timeAgendaAll.addMustnot(conBusSem);
		}
		timeAgendaAll.setRole(timeAgendaAll.getRole(), "collection");
		timeAgendaAll.addSummaryAgenda(timeAllSummaryAgenda);
		timeAllSummaryAgenda.addSummarizedAgenda(timeAgendaAll);

		
		// Set next relations
		masterAgenda.addNext(openAgenda);
		
		openAgenda.addNext(openAgenda);
		openAgenda.addNext(exitAgenda);
		
		openAgenda.addNext(conTimeAgenda);
		openAgenda.addNext(conDestAgenda);
		openAgenda.addNext(conDepAgenda);
		openAgenda.addNext(conBusAgenda);
		
		openAgenda.addNext(depAgenda);
		openAgenda.addNext(timeAgenda);
		openAgenda.addNext(destAgenda);
		openAgenda.addNext(busAgenda);
		
		openAgenda.addNext(openAgenda_afterBusDepDest);
		openAgenda.addNext(openAgenda_afterBusDep);
		openAgenda.addNext(openAgenda_afterBusDest);
		openAgenda.addNext(openAgenda_afterBus);
		openAgenda.addNext(openAgenda_afterDepDest);
		openAgenda.addNext(openAgenda_afterDep);
		openAgenda.addNext(openAgenda_afterDest);
		openAgenda.addNext(openAgenda_afterBusTimeDest);
		openAgenda.addNext(openAgenda_afterBusTime);
		openAgenda.addNext(openAgenda_afterTimeDest);
		openAgenda.addNext(openAgenda_afterTime);
		openAgenda.addNext(openAgenda_afterBusTimeDep);
		openAgenda.addNext(openAgenda_afterTimeDep);
		
		openAgenda.addNext(timeAgendaAll);
		openAgenda.addNext(timeAgendaWithoutBusDepDest);
		openAgenda.addNext(timeAgendaWithoutBusDep);
		openAgenda.addNext(timeAgendaWithoutBusDest);
		openAgenda.addNext(timeAgendaWithoutBus);
		openAgenda.addNext(timeAgendaWithoutDepDest);
		openAgenda.addNext(timeAgendaWithoutDep);
		openAgenda.addNext(timeAgendaWithoutDest);
		openAgenda.addNext(depAgendaAll);
		openAgenda.addNext(depAgendaWithoutBusTimeDest);
		openAgenda.addNext(depAgendaWithoutBusTime);
		openAgenda.addNext(depAgendaWithoutBusDest);
		openAgenda.addNext(depAgendaWithoutBus);
		openAgenda.addNext(depAgendaWithoutTimeDest);
		openAgenda.addNext(depAgendaWithoutTime);
		openAgenda.addNext(depAgendaWithoutDest);
		openAgenda.addNext(destAgendaAll);
		openAgenda.addNext(destAgendaWithoutBusTimeDep);
		openAgenda.addNext(destAgendaWithoutBusTime);
		openAgenda.addNext(destAgendaWithoutBusDep);
		openAgenda.addNext(destAgendaWithoutBus);
		openAgenda.addNext(destAgendaWithoutTimeDep);
		openAgenda.addNext(destAgendaWithoutTime);
		openAgenda.addNext(destAgendaWithoutDep);

//		conTimeAgenda.addNext(openAgenda);
//		conTimeAgenda.addNext(conTimeAgenda);
//		conTimeAgenda.addNext(openAgenda_afterBusDepDest);
//		conTimeAgenda.addNext(openAgenda_afterBusDep);
//		conTimeAgenda.addNext(openAgenda_afterBusDest);
//		conTimeAgenda.addNext(openAgenda_afterBus);
//		conTimeAgenda.addNext(openAgenda_afterDepDest);
//		conTimeAgenda.addNext(openAgenda_afterDep);
//		conTimeAgenda.addNext(openAgenda_afterDest);
//		conTimeAgenda.addNext(openAgenda_afterBusTimeDest);
//		conTimeAgenda.addNext(openAgenda_afterBusTime);
//		conTimeAgenda.addNext(openAgenda_afterTimeDest);
//		conTimeAgenda.addNext(openAgenda_afterTime);
//		conTimeAgenda.addNext(openAgenda_afterBusTimeDep);
//		conTimeAgenda.addNext(openAgenda_afterTimeDep);
//		
//		openAgenda_afterBusDepDest.addNext(conTimeAgenda);
//		openAgenda_afterBusDep.addNext(conTimeAgenda);
//		openAgenda_afterBusDest.addNext(conTimeAgenda);
//		openAgenda_afterBus.addNext(conTimeAgenda);
//		openAgenda_afterDepDest.addNext(conTimeAgenda);
//		openAgenda_afterDep.addNext(conTimeAgenda);
//		openAgenda_afterDest.addNext(conTimeAgenda);
//
//		conDestAgenda.addNext(openAgenda);
//		conDestAgenda.addNext(conDestAgenda);
//		conDestAgenda.addNext(openAgenda_afterBusTimeDep);
//		conDestAgenda.addNext(openAgenda_afterBusTime);
//		conDestAgenda.addNext(openAgenda_afterBusDep);
//		conDestAgenda.addNext(openAgenda_afterBus);
//		conDestAgenda.addNext(openAgenda_afterTimeDep);
//		conDestAgenda.addNext(openAgenda_afterTime);
//		conDestAgenda.addNext(openAgenda_afterDep);
//		conDestAgenda.addNext(openAgenda_afterBusTimeDest);
//		conDestAgenda.addNext(openAgenda_afterBusDest);
//		conDestAgenda.addNext(openAgenda_afterTimeDest);
//		conDestAgenda.addNext(openAgenda_afterDest);
//		conDestAgenda.addNext(openAgenda_afterBusDepDest);
//		conDestAgenda.addNext(openAgenda_afterDepDest);
//		
//		openAgenda_afterBusTimeDep.addNext(conDestAgenda);
//		openAgenda_afterBusTime.addNext(conDestAgenda);
//		openAgenda_afterBusDep.addNext(conDestAgenda);
//		openAgenda_afterBus.addNext(conDestAgenda);
//		openAgenda_afterTimeDep.addNext(conDestAgenda);
//		openAgenda_afterTime.addNext(conDestAgenda);
//		openAgenda_afterDep.addNext(conDestAgenda);
//
//		conDepAgenda.addNext(openAgenda);
//		conDepAgenda.addNext(conDepAgenda);
//		conDepAgenda.addNext(openAgenda_afterBusTimeDest);
//		conDepAgenda.addNext(openAgenda_afterBusTime);
//		conDepAgenda.addNext(openAgenda_afterBusDest);
//		conDepAgenda.addNext(openAgenda_afterBus);
//		conDepAgenda.addNext(openAgenda_afterTimeDest);
//		conDepAgenda.addNext(openAgenda_afterTime);
//		conDepAgenda.addNext(openAgenda_afterDest);
//		conDepAgenda.addNext(openAgenda_afterBusTimeDep);
//		conDepAgenda.addNext(openAgenda_afterBusDep);
//		conDepAgenda.addNext(openAgenda_afterTimeDep);
//		conDepAgenda.addNext(openAgenda_afterDep);
//		conDepAgenda.addNext(openAgenda_afterBusDepDest);
//		conDepAgenda.addNext(openAgenda_afterDepDest);
//		
//		openAgenda_afterBusTimeDest.addNext(conDepAgenda);
//		openAgenda_afterBusTime.addNext(conDepAgenda);
//		openAgenda_afterBusDest.addNext(conDepAgenda);
//		openAgenda_afterBus.addNext(conDepAgenda);
//		openAgenda_afterTimeDest.addNext(conDepAgenda);
//		openAgenda_afterTime.addNext(conDepAgenda);
//		openAgenda_afterDest.addNext(conDepAgenda);
//
//		conBusAgenda.addNext(openAgenda);
//		conBusAgenda.addNext(conBusAgenda);
//		conBusAgenda.addNext(openAgenda_afterTime);
//		conBusAgenda.addNext(openAgenda_afterDest);
//		conBusAgenda.addNext(openAgenda_afterDep);
//		conBusAgenda.addNext(openAgenda_afterTimeDep);
//		conBusAgenda.addNext(openAgenda_afterBus);
//		conBusAgenda.addNext(openAgenda_afterBusDep);
//		conBusAgenda.addNext(openAgenda_afterBusTimeDest);
//		conBusAgenda.addNext(openAgenda_afterBusDest);
//		conBusAgenda.addNext(openAgenda_afterTimeDest);
//		conBusAgenda.addNext(openAgenda_afterBusDepDest);
//		conBusAgenda.addNext(openAgenda_afterBusTime);
//		conBusAgenda.addNext(openAgenda_afterBusTimeDep);
//		conBusAgenda.addNext(openAgenda_afterDepDest);
//		
//		openAgenda_afterTime.addNext(conBusAgenda);
//		openAgenda_afterDest.addNext(conBusAgenda);
//		openAgenda_afterDep.addNext(conBusAgenda);
//		openAgenda_afterDepDest.addNext(conBusAgenda);
//		openAgenda_afterTimeDest.addNext(conBusAgenda);
//		openAgenda_afterTimeDep.addNext(conBusAgenda);
//
//		conTimeAgenda.addNext(timeAgendaWithoutBusDepDest);
//		conTimeAgenda.addNext(timeAgendaWithoutBusDep);
//		conTimeAgenda.addNext(timeAgendaWithoutBusDest);
//		conTimeAgenda.addNext(timeAgendaWithoutBus);
//		conTimeAgenda.addNext(timeAgendaWithoutDepDest);
//		conTimeAgenda.addNext(timeAgendaWithoutDep);
//		conTimeAgenda.addNext(timeAgendaWithoutDest);
//		conTimeAgenda.addNext(timeAgenda);
//		conTimeAgenda.addNext(depAgendaWithoutBusTimeDest);
//		conTimeAgenda.addNext(depAgendaWithoutBusTime);
//		conTimeAgenda.addNext(depAgendaWithoutTimeDest);
//		conTimeAgenda.addNext(depAgendaWithoutTime);
//		conTimeAgenda.addNext(destAgendaWithoutBusTimeDep);
//		conTimeAgenda.addNext(destAgendaWithoutBusTime);
//		conTimeAgenda.addNext(destAgendaWithoutTimeDep);
//		conTimeAgenda.addNext(destAgendaWithoutTime);
//		
//		timeAgendaWithoutBusDepDest.addNext(conTimeAgenda);
//		timeAgendaWithoutBusDep.addNext(conTimeAgenda);
//		timeAgendaWithoutBusDest.addNext(conTimeAgenda);
//		timeAgendaWithoutBus.addNext(conTimeAgenda);
//		timeAgendaWithoutDepDest.addNext(conTimeAgenda);
//		timeAgendaWithoutDep.addNext(conTimeAgenda);
//		timeAgendaWithoutDest.addNext(conTimeAgenda);
//		timeAgenda.addNext(conTimeAgenda);
//
//		conDestAgenda.addNext(destAgendaWithoutBusTimeDep);
//		conDestAgenda.addNext(destAgendaWithoutBusTime);
//		conDestAgenda.addNext(destAgendaWithoutBusDep);
//		conDestAgenda.addNext(destAgendaWithoutBus);
//		conDestAgenda.addNext(destAgendaWithoutTimeDep);
//		conDestAgenda.addNext(destAgendaWithoutTime);
//		conDestAgenda.addNext(destAgendaWithoutDep);
//		conDestAgenda.addNext(destAgenda);
//		conDestAgenda.addNext(depAgendaWithoutBusTimeDest);
//		conDestAgenda.addNext(depAgendaWithoutBusDest);
//		conDestAgenda.addNext(depAgendaWithoutTimeDest);
//		conDestAgenda.addNext(depAgendaWithoutDest);
//		conDestAgenda.addNext(timeAgendaWithoutBusDepDest);
//		conDestAgenda.addNext(timeAgendaWithoutBusDest);
//		conDestAgenda.addNext(timeAgendaWithoutDepDest);
//		conDestAgenda.addNext(timeAgendaWithoutDest);
//		
//		destAgendaWithoutBusTimeDep.addNext(conDestAgenda);
//		destAgendaWithoutBusTime.addNext(conDestAgenda);
//		destAgendaWithoutBusDep.addNext(conDestAgenda);
//		destAgendaWithoutBus.addNext(conDestAgenda);
//		destAgendaWithoutTimeDep.addNext(conDestAgenda);
//		destAgendaWithoutTime.addNext(conDestAgenda);
//		destAgendaWithoutDep.addNext(conDestAgenda);
//		destAgenda.addNext(conDestAgenda);
//
//		conDepAgenda.addNext(depAgendaWithoutBusTimeDest);
//		conDepAgenda.addNext(depAgendaWithoutBusTime);
//		conDepAgenda.addNext(depAgendaWithoutBusDest);
//		conDepAgenda.addNext(depAgendaWithoutBus);
//		conDepAgenda.addNext(depAgendaWithoutTimeDest);
//		conDepAgenda.addNext(depAgendaWithoutTime);
//		conDepAgenda.addNext(depAgendaWithoutDest);
//		conDepAgenda.addNext(depAgenda);
//		conDepAgenda.addNext(timeAgendaWithoutBusDepDest);
//		conDepAgenda.addNext(timeAgendaWithoutBusDep);
//		conDepAgenda.addNext(timeAgendaWithoutDepDest);
//		conDepAgenda.addNext(timeAgendaWithoutDep);
//		conDepAgenda.addNext(destAgendaWithoutBusTimeDep);
//		conDepAgenda.addNext(destAgendaWithoutBusDep);
//		conDepAgenda.addNext(destAgendaWithoutTimeDep);
//		conDepAgenda.addNext(destAgendaWithoutDep);
//		
//		depAgendaWithoutBusTimeDest.addNext(conDepAgenda);
//		depAgendaWithoutBusTime.addNext(conDepAgenda);
//		depAgendaWithoutBusDest.addNext(conDepAgenda);
//		depAgendaWithoutBus.addNext(conDepAgenda);
//		depAgendaWithoutTimeDest.addNext(conDepAgenda);
//		depAgendaWithoutTime.addNext(conDepAgenda);
//		depAgendaWithoutDest.addNext(conDepAgenda);
//		depAgenda.addNext(conDepAgenda);
//
//		conBusAgenda.addNext(depAgendaWithoutBusTimeDest);
//		conBusAgenda.addNext(depAgendaWithoutBusTime);
//		conBusAgenda.addNext(depAgendaWithoutBusDest);
//		conBusAgenda.addNext(depAgendaWithoutBus);
//		conBusAgenda.addNext(depAgendaWithoutBusTime);
//		conBusAgenda.addNext(destAgendaWithoutBusTimeDep);
//		conBusAgenda.addNext(destAgendaWithoutBusDep);
//		conBusAgenda.addNext(destAgendaWithoutBus);
//		conBusAgenda.addNext(timeAgendaWithoutBusDepDest);
//		conBusAgenda.addNext(timeAgendaWithoutBusDep);
//		conBusAgenda.addNext(timeAgendaWithoutBusDest);
//		conBusAgenda.addNext(timeAgendaWithoutBus);
//
//		depAgendaAll.addNext(depAgendaAll);
//		depAgendaAll.addNext(conTimeAgenda);
//		depAgendaAll.addNext(conDepAgenda);
//		depAgendaAll.addNext(conDestAgenda);
//
//		destAgendaAll.addNext(destAgendaAll);
//		destAgendaAll.addNext(conTimeAgenda);
//		destAgendaAll.addNext(conDepAgenda);
//		destAgendaAll.addNext(conDestAgenda);
//
//		timeAgendaAll.addNext(timeAgendaAll);
//		timeAgendaAll.addNext(conTimeAgenda);
//		timeAgendaAll.addNext(conDepAgenda);
//		timeAgendaAll.addNext(conDestAgenda);
//
//		conTimeAgenda.addNext(timeAgendaAll);
//		conTimeAgenda.addNext(depAgendaAll);
//		conTimeAgenda.addNext(destAgendaAll);
//
//		conDestAgenda.addNext(destAgendaAll);
//		conDestAgenda.addNext(depAgendaAll);
//		conDestAgenda.addNext(timeAgendaAll);
//
//		conDepAgenda.addNext(depAgendaAll);
//		conDepAgenda.addNext(destAgendaAll);
//		conDepAgenda.addNext(timeAgendaAll);

//		destAgenda.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%<<3)");
//		depAgenda.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%<<3)");
//		timeAgenda.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%<<3)");
//
//		destAgendaAll.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaAll.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaAll.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//
//		destAgendaWithoutDep.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		destAgendaWithoutTime.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutTime.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutDep.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutTimeDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutDepDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		destAgendaWithoutTimeDep.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		destAgendaWithoutBus.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		destAgendaWithoutBusDep.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		destAgendaWithoutBusTime.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		destAgendaWithoutBusTimeDep.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutBus.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutBusDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutBusTime.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		depAgendaWithoutBusTimeDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutBus.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutBusDep.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutBusDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//		timeAgendaWithoutBusDepDest.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>2)");
//
//		openAgenda.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterDepCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterDestCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterTimeCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterTimeDepCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterTimeDestCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusTimeCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterDepDestCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusDestCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusDepCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusDepDestCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusTimeDepCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//		openAgenda_afterBusTimeDestCon.setVariableOperator("",
//				"REQUIRES(%InteractionQuality%>>4)");
//
//		conDepAgenda.setPriority(0, 5000);
//		conDestAgenda.setPriority(0, 4000);
//		conTimeAgenda.setPriority(0, 3000);
//		conBusAgenda.setPriority(0, 2000);
//
//		openAgenda.setPriority(0, 1500);
//		openAgenda_afterBusTimeDestCon.setPriority(0, 1500);
//		openAgenda_afterBusTimeCon.setPriority(0, 1500);
//		openAgenda_afterBusDestCon.setPriority(0, 1500);
//		openAgenda_afterBusCon.setPriority(0, 1500);
//		openAgenda_afterTimeDestCon.setPriority(0, 1500);
//		openAgenda_afterTimeCon.setPriority(0, 1500);
//		openAgenda_afterDestCon.setPriority(0, 1500);
//		openAgenda_afterBusTimeDepCon.setPriority(0, 1500);
//		openAgenda_afterBusDepCon.setPriority(0, 1500);
//		openAgenda_afterTimeDepCon.setPriority(0, 1500);
//		openAgenda_afterDepCon.setPriority(0, 1500);
//		openAgenda_afterBusDepDestCon.setPriority(0, 1500);
//		openAgenda_afterDepDestCon.setPriority(0, 1500);		
//		
//		depAgenda.setPriority(0, 1000);
//		depAgendaAll.setPriority(0, 1000);
//		depAgendaWithoutDest.setPriority(0, 1000);
//		depAgendaWithoutTime.setPriority(0, 1000);
//		depAgendaWithoutBus.setPriority(0, 1000);
//		depAgendaWithoutTimeDest.setPriority(0, 1000);
//		depAgendaWithoutBusDest.setPriority(0, 1000);
//		depAgendaWithoutBusTime.setPriority(0, 1000);
//		
//		destAgenda.setPriority(0, 800);
//		destAgendaAll.setPriority(0, 800);
//		destAgendaWithoutDep.setPriority(0, 800);
//		destAgendaWithoutTime.setPriority(0, 800);
//		destAgendaWithoutBus.setPriority(0, 800);
//		destAgendaWithoutTimeDep.setPriority(0, 800);
//		destAgendaWithoutBusDep.setPriority(0, 800);
//		destAgendaWithoutBusTime.setPriority(0, 800);
//		
//		timeAgenda.setPriority(0, 600);
//		timeAgendaAll.setPriority(0, 600);
//		timeAgendaWithoutDep.setPriority(0, 600);
//		timeAgendaWithoutDest.setPriority(0, 600);
//		timeAgendaWithoutBus.setPriority(0, 600);
//		timeAgendaWithoutDepDest.setPriority(0, 600);
//		timeAgendaWithoutBusDest.setPriority(0, 600);
//		timeAgendaWithoutBusDep.setPriority(0, 600);
//
//		exitAgenda.setPriority(0, 3000);

		
		// Create Utterances
		Utterance dummyUtterance = factory.createUtterance("dummyUtterance");
		Utterance destinationUtterance = factory.createUtterance("destinationUtterance");
		Utterance departureplaceUtterance = factory.createUtterance("departureplaceUtterance");
		Utterance timeUtterance = factory.createUtterance("timeUtterance");
		Utterance busnumberUtterance = factory.createUtterance("busnumberUtterance");
		Utterance infoUtterance = factory.createUtterance("infoUtterance");

		OWLLiteral l = factory.factory.getOWLLiteral("%" + destVar + "%", "en");
		OWLLiteral m = factory.factory.getOWLLiteral("%" + depVar + "%", "en");
		OWLLiteral n = factory.factory.getOWLLiteral("%" + timeVar + "%", "en");
		OWLLiteral o = factory.factory.getOWLLiteral("%" + busVar + "%", "en");

		destinationUtterance.addUtteranceString(l);
		departureplaceUtterance.addUtteranceString(m);
		timeUtterance.addUtteranceString(n);
		busnumberUtterance.addUtteranceString(o);
		infoUtterance.addUtteranceString(l);
		infoUtterance.addUtteranceString(m);
		infoUtterance.addUtteranceString(n);
		infoUtterance.addUtteranceString(o);
		
		
		// Create Grammar
		Grammar g1 = factory.createGrammar("dummygrammar");

		
		// Create System Moves
		Move m2 = factory.createMove("sysmov_Confirm_Bus_number");
		conBusAgenda.addHas(m2);
		m2.setUtterance(null, busnumberUtterance);
		
		Move m3 = factory.createMove("sysmov_Confirm_Departure_place");
		conDepAgenda.addHas(m3);
		m3.setUtterance(null, departureplaceUtterance);
		
		Move m4 = factory.createMove("sysmov_Confirm_Arrival_place");
		conDestAgenda.addHas(m4);
		m4.setUtterance(null, destinationUtterance);
		
		Move m5 = factory.createMove("sysmov_Confirm_Travel_time");
		conTimeAgenda.addHas(m5);
		m5.setUtterance(null, timeUtterance);
				
		Move m6 = factory.createMove("sysmov_Request_Open");
		openAgenda.addHas(m6);
		openAgenda_afterDep.addHas(m6);
		openAgenda_afterDest.addHas(m6);
		openAgenda_afterTime.addHas(m6);
		openAgenda_afterBus.addHas(m6);
		openAgenda_afterDepDest.addHas(m6);
		openAgenda_afterTimeDep.addHas(m6);
		openAgenda_afterTimeDest.addHas(m6);
		openAgenda_afterBusDep.addHas(m6);
		openAgenda_afterBusDest.addHas(m6);
		openAgenda_afterBusTime.addHas(m6);
		openAgenda_afterBusTimeDep.addHas(m6);
		openAgenda_afterBusTimeDest.addHas(m6);
		openAgenda_afterBusDepDest.addHas(m6);
		m6.setUtterance(null, dummyUtterance);
		
		Move m1 = factory.createMove("sysmov_Request_Bus_number");
		busAgenda.addHas(m1);
		m1.setUtterance(null, dummyUtterance);

		Move m8 = factory.createMove("sysmov_Request_Departure_place");
		depAgenda.addHas(m8);
		depAgendaWithoutDest.addHas(m8);
		depAgendaWithoutTime.addHas(m8);
		depAgendaWithoutBus.addHas(m8);
		depAgendaWithoutTimeDest.addHas(m8);
		depAgendaWithoutBusDest.addHas(m8);
		depAgendaWithoutBusTime.addHas(m8);
		depAgendaWithoutBusTimeDest.addHas(m8);
		depAgendaAll.addHas(m8);
		m8.setUtterance(null, dummyUtterance);
		
		Move m9 = factory.createMove("sysmov_Request_Travel_time");
		timeAgenda.addHas(m9);
		timeAgendaWithoutDep.addHas(m9);
		timeAgendaWithoutDest.addHas(m9);
		timeAgendaWithoutBus.addHas(m9);
		timeAgendaWithoutDepDest.addHas(m9);
		timeAgendaWithoutBusDep.addHas(m9);
		timeAgendaWithoutBusDest.addHas(m9);
		timeAgendaWithoutBusDepDest.addHas(m9);
		timeAgendaAll.addHas(m9);
		m9.setUtterance(null, dummyUtterance);
		
		Move m10 = factory.createMove("sysmov_Request_Arrival_place");
		destAgenda.addHas(m10);
		destAgendaWithoutDep.addHas(m10);
		destAgendaWithoutTime.addHas(m10);
		destAgendaWithoutBus.addHas(m10);
		destAgendaWithoutTimeDep.addHas(m10);
		destAgendaWithoutBusDep.addHas(m10);
		destAgendaWithoutBusTime.addHas(m10);
		destAgendaWithoutBusTimeDep.addHas(m10);
		destAgendaAll.addHas(m10);
		m10.setUtterance(null, dummyUtterance);

		Move m12 = factory.createMove("sysmov_Exit");
		exitAgenda.addHas(m12);
		m12.setUtterance(null, infoUtterance);
		
		
		// Create User Moves with one slot
		Move m13 = factory.createMove("external_destination_mv");
		m13.setVariableOperator("SET(" + destVar + "=:external_destination_mv:)");
		m13.addSemantic(destSemG);
		m13.setGrammar(null, g1);
		m13.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		destAgenda.addHas(m13);
		destAgendaWithoutDep.addHas(m13);
		destAgendaWithoutTime.addHas(m13);
		destAgendaWithoutBus.addHas(m13);
		destAgendaWithoutBusDep.addHas(m13);
		destAgendaWithoutBusTime.addHas(m13);
		destAgendaWithoutTimeDep.addHas(m13);
		destAgendaWithoutBusTimeDep.addHas(m13);
		destAgendaAll.addHas(m13);
		depAgendaWithoutTime.addHas(m13);
		depAgendaWithoutBus.addHas(m13);
		depAgendaWithoutBusTime.addHas(m13);
		depAgendaAll.addHas(m13);
		timeAgendaWithoutDep.addHas(m13);
		timeAgendaWithoutBus.addHas(m13);
		timeAgendaWithoutBusDep.addHas(m13);
		timeAgendaAll.addHas(m13);
		openAgenda.addHas(m13);
		openAgenda_afterDep.addHas(m13);
		openAgenda_afterTime.addHas(m13);
		openAgenda_afterBus.addHas(m13);
		openAgenda_afterTimeDep.addHas(m13);
		openAgenda_afterBusDep.addHas(m13);
		openAgenda_afterBusTime.addHas(m13);
		openAgenda_afterBusTimeDep.addHas(m13);

		Move m14 = factory.createMove("external_depplace_mv");
		m14.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:)");
		m14.addSemantic(depSemG);
		m14.setGrammar(null, g1);
		m14.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		depAgenda.addHas(m14);
		depAgendaWithoutDest.addHas(m14);
		depAgendaWithoutTime.addHas(m14);
		depAgendaWithoutBus.addHas(m14);
		depAgendaWithoutBusDest.addHas(m14);
		depAgendaWithoutBusTime.addHas(m14);
		depAgendaWithoutTimeDest.addHas(m14);
		depAgendaWithoutBusTimeDest.addHas(m14);
		depAgendaAll.addHas(m14);
		destAgendaWithoutTime.addHas(m14);
		destAgendaWithoutBus.addHas(m14);
		destAgendaWithoutBusTime.addHas(m14);
		destAgendaAll.addHas(m14);
		timeAgendaWithoutDest.addHas(m14);
		timeAgendaWithoutBus.addHas(m14);
		timeAgendaWithoutBusDest.addHas(m14);
		timeAgendaAll.addHas(m14);
		openAgenda.addHas(m14);
		openAgenda_afterDest.addHas(m14);
		openAgenda_afterTime.addHas(m14);
		openAgenda_afterBus.addHas(m14);
		openAgenda_afterTimeDest.addHas(m14);
		openAgenda_afterBusDest.addHas(m14);
		openAgenda_afterBusTime.addHas(m14);
		openAgenda_afterBusTimeDest.addHas(m14);

		Move m15 = factory.createMove("external_busnumber_mv");
		m15.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:)");
		m15.addSemantic(busSemG);
		m15.setGrammar(null, g1);
		m15.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		busAgenda.addHas(m15);
		depAgendaWithoutDest.addHas(m15);
		depAgendaWithoutTime.addHas(m15);
		depAgendaWithoutTimeDest.addHas(m15);
		depAgendaAll.addHas(m15);
		destAgendaWithoutDep.addHas(m15);
		destAgendaWithoutTime.addHas(m15);
		destAgendaWithoutTimeDep.addHas(m15);
		destAgendaAll.addHas(m15);
		timeAgendaWithoutDep.addHas(m15);
		timeAgendaWithoutDest.addHas(m15);
		timeAgendaWithoutDepDest.addHas(m15);
		timeAgendaAll.addHas(m15);
		openAgenda.addHas(m15);
		openAgenda_afterTime.addHas(m15);
		openAgenda_afterDest.addHas(m15);
		openAgenda_afterDep.addHas(m15);
		openAgenda_afterTimeDest.addHas(m15);
		openAgenda_afterDepDest.addHas(m15);
		openAgenda_afterTimeDep.addHas(m15);

		Move m16 = factory.createMove("external_time_mv");
		m16.setVariableOperator("SET(" + timeVar + "=:external_time_mv:)");
		m16.addSemantic(timeSemG);
		m16.setGrammar(null, g1);
		m16.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		timeAgenda.addHas(m16);
		timeAgendaWithoutDep.addHas(m16);
		timeAgendaWithoutDest.addHas(m16);
		timeAgendaWithoutBus.addHas(m16);
		timeAgendaWithoutBusDep.addHas(m16);
		timeAgendaWithoutBusDest.addHas(m16);
		timeAgendaWithoutDepDest.addHas(m16);
		timeAgendaWithoutBusDepDest.addHas(m16);
		timeAgendaAll.addHas(m16);
		depAgendaWithoutDest.addHas(m16);
		depAgendaWithoutBus.addHas(m16);
		depAgendaWithoutBusDest.addHas(m16);
		depAgendaAll.addHas(m16);
		destAgendaWithoutDep.addHas(m16);
		destAgendaWithoutBus.addHas(m16);
		destAgendaWithoutBusDep.addHas(m16);
		destAgendaAll.addHas(m16);
		openAgenda.addHas(m16);
		openAgenda_afterDest.addHas(m16);
		openAgenda_afterDep.addHas(m16);
		openAgenda_afterDepDest.addHas(m16);
		openAgenda_afterBus.addHas(m16);
		openAgenda_afterBusDep.addHas(m16);
		openAgenda_afterBusDepDest.addHas(m16);
		openAgenda_afterBusDest.addHas(m16);


		// Create multislot User Moves
		Move m17 = factory.createMove("multi_slot4_mv");
		m17.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ depVar + "=:external_depplace_mv:;" + destVar + "=:external_destination_mv:;" 
				+ timeVar + "=:external_time_mv:)");
		m17.addSemantic(timeSemG);
		m17.addSemantic(destSemG);
		m17.addSemantic(depSemG);
		m17.addSemantic(busSemG);
		m17.setGrammar(null, g1);
		m17.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		openAgenda.addHas(m17);
		depAgendaAll.addHas(m17);
		destAgendaAll.addHas(m17);
		timeAgendaAll.addHas(m17);

		Move m18 = factory.createMove("multi_slot3a_mv");
		m18.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ depVar + "=:external_depplace_mv:;" + destVar + "=:external_destination_mv:)");
		m18.addSemantic(destSemG);
		m18.addSemantic(depSemG);
		m18.addSemantic(busSemG);
		m18.setGrammar(null, g1);
		m18.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		depAgendaWithoutTime.addHas(m18);
		destAgendaWithoutTime.addHas(m18);
		openAgenda.addHas(m18);
		openAgenda_afterTime.addHas(m18);

		Move m19 = factory.createMove("multi_slot3b_mv");
		m19.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ destVar + "=:external_destination_mv:;" + timeVar + "=:external_time_mv:)");
		m19.addSemantic(destSemG);
		m19.addSemantic(depSemG);
		m19.addSemantic(timeSemG);
		m19.setGrammar(null, g1);
		m19.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		openAgenda.addHas(m19);
		openAgenda_afterBus.addHas(m19);
		depAgendaAll.addHas(m19);
		destAgendaAll.addHas(m19);
		timeAgendaAll.addHas(m19);
		depAgendaWithoutBus.addHas(m19);
		destAgendaWithoutBus.addHas(m19);
		timeAgendaWithoutBus.addHas(m19);

		Move m20 = factory.createMove("multi_slot3c_mv");
		m20.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ busVar + "=:external_busnumber_mv:;" + timeVar + "=:external_time_mv:)");
		m20.addSemantic(busSemG);
		m20.addSemantic(depSemG);
		m20.addSemantic(timeSemG);
		m20.setGrammar(null, g1);
		m20.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		depAgendaAll.addHas(m20);
		destAgendaAll.addHas(m20);
		timeAgendaAll.addHas(m20);
		timeAgendaWithoutDest.addHas(m20);
		depAgendaWithoutDest.addHas(m20);
		openAgenda.addHas(m20);
		openAgenda_afterDest.addHas(m20);

		Move m21 = factory.createMove("multi_slot3d_mv");
		m21.setVariableOperator("SET(" + destVar + "=:external_destination_mv:;" 
				+ busVar + "=:external_busnumber_mv:;" + timeVar + "=:external_time_mv:)");
		m21.addSemantic(busSemG);
		m21.addSemantic(destSemG);
		m21.addSemantic(timeSemG);
		m21.setGrammar(null, g1);
		m21.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		depAgendaAll.addHas(m21);
		destAgendaAll.addHas(m21);
		timeAgendaAll.addHas(m21);
		destAgendaWithoutDep.addHas(m21);
		timeAgendaWithoutDep.addHas(m21);
		openAgenda.addHas(m21);
		openAgenda_afterDep.addHas(m21);

		Move m22 = factory.createMove("multi_slot2a_mv");
		m22.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ depVar + "=:external_depplace_mv:)");
		m22.addSemantic(depSemG);
		m22.addSemantic(busSemG);
		m22.setGrammar(null, g1);
		m22.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		timeAgendaWithoutDest.addHas(m22);
		destAgendaWithoutTime.addHas(m22);
		openAgenda.addHas(m22);
		openAgenda_afterDest.addHas(m22);
		openAgenda_afterTime.addHas(m22);
		openAgenda_afterTimeDest.addHas(m22);
		depAgendaAll.addHas(m22);
		destAgendaAll.addHas(m22);
		timeAgendaAll.addHas(m22);
		depAgendaWithoutTimeDest.addHas(m22);
		depAgendaWithoutDest.addHas(m22);
		depAgendaWithoutTime.addHas(m22);

		Move m23 = factory.createMove("multi_slot2b_mv");
		m23.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ destVar + "=:external_destination_mv:)");
		m23.addSemantic(destSemG);
		m23.addSemantic(depSemG);
		m23.setGrammar(null, g1);
		m23.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		depAgendaWithoutTime.addHas(m23);
		depAgendaWithoutBus.addHas(m23);
		depAgendaWithoutBusTime.addHas(m23);
		destAgendaWithoutTime.addHas(m23);
		destAgendaWithoutBus.addHas(m23);
		destAgendaWithoutBusTime.addHas(m23);
		timeAgendaWithoutBus.addHas(m23);
		openAgenda.addHas(m23);
		depAgendaAll.addHas(m23);
		destAgendaAll.addHas(m23);
		timeAgendaAll.addHas(m23);
		openAgenda_afterTime.addHas(m23);
		openAgenda_afterBus.addHas(m23);
		openAgenda_afterBusTime.addHas(m23);

		Move m24 = factory.createMove("multi_slot2c_mv");
		m24.setVariableOperator("SET(" + destVar + "=:external_destination_mv:;" 
				+ timeVar + "=:external_time_mv:)");
		m24.addSemantic(destSemG);
		m24.addSemantic(timeSemG);
		m24.setGrammar(null, g1);
		m24.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		destAgendaWithoutDep.addHas(m24);
		destAgendaWithoutBus.addHas(m24);
		destAgendaWithoutBusDep.addHas(m24);
		timeAgendaWithoutDep.addHas(m24);
		timeAgendaWithoutBus.addHas(m24);
		timeAgendaWithoutBusDep.addHas(m24);
		depAgendaWithoutBus.addHas(m24);
		openAgenda.addHas(m24);
		openAgenda_afterDep.addHas(m24);
		openAgenda_afterBus.addHas(m24);
		openAgenda_afterBusDep.addHas(m24);
		depAgendaAll.addHas(m24);
		destAgendaAll.addHas(m24);
		timeAgendaAll.addHas(m24);
		openAgenda_afterTimeDep.addHas(m24);

		Move m25 = factory.createMove("multi_slot2d_mv");
		m25.setVariableOperator("SET(" + destVar + "=:external_destination_mv:;" 
				+ busVar + "=:external_busnumber_mv:)");
		m25.addSemantic(destSemG);
		m25.addSemantic(busSemG);
		m25.setGrammar(null, g1);
		m25.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		destAgendaWithoutDep.addHas(m25);
		destAgendaWithoutTime.addHas(m25);
		depAgendaWithoutTime.addHas(m25);
		timeAgendaWithoutDep.addHas(m25);
		openAgenda.addHas(m25);
		openAgenda_afterDep.addHas(m25);
		openAgenda_afterTime.addHas(m25);
		openAgenda_afterTimeDep.addHas(m25);
		depAgendaAll.addHas(m25);
		destAgendaAll.addHas(m25);
		timeAgendaAll.addHas(m25);
		destAgendaWithoutTimeDep.addHas(m25);

		Move m26 = factory.createMove("multi_slot2e_mv");
		m26.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ timeVar + "=:external_time_mv:)");
		m26.addSemantic(depSemG);
		m26.addSemantic(timeSemG);
		m26.setGrammar(null, g1);
		m26.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		depAgendaWithoutDest.addHas(m26);
		depAgendaWithoutBus.addHas(m26);
		depAgendaWithoutBusDest.addHas(m26);
		timeAgendaWithoutDest.addHas(m26);
		timeAgendaWithoutBus.addHas(m26);
		timeAgendaWithoutBusDest.addHas(m26);
		destAgendaWithoutBus.addHas(m26);
		openAgenda.addHas(m26);
		openAgenda_afterDest.addHas(m26);
		openAgenda_afterBus.addHas(m26);
		openAgenda_afterBusDest.addHas(m26);
		depAgendaAll.addHas(m26);
		destAgendaAll.addHas(m26);
		timeAgendaAll.addHas(m26);

		Move m27 = factory.createMove("multi_slot2f_mv");
		m27.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ timeVar + "=:external_time_mv:)");
		m27.addSemantic(busSemG);
		m27.addSemantic(timeSemG);
		m27.setGrammar(null, g1);
		m27.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		timeAgendaWithoutDep.addHas(m27);
		timeAgendaWithoutDest.addHas(m27);
		depAgendaWithoutDest.addHas(m27);
		destAgendaWithoutDep.addHas(m27);
		openAgenda_afterDest.addHas(m27);
		openAgenda_afterDep.addHas(m27);
		openAgenda_afterDepDest.addHas(m27);
		openAgenda.addHas(m27);
		depAgendaAll.addHas(m27);
		destAgendaAll.addHas(m27);
		timeAgendaAll.addHas(m27);
		timeAgendaWithoutDepDest.addHas(m27);

		
		// Create Confirmation User Moves
		Move m28 = factory.createMove("usermov_affirm_bus_number");
		m28.addSemantic(conBusSem);
		m28.addContrarySemantic(rejBusSem);
		conBusAgenda.addHas(m28);
		m28.setGrammar(null, g1);

		Move m29 = factory.createMove("usermov_affirm_arrival_place");
		m29.addSemantic(conDestSem);
		m29.addContrarySemantic(rejDestSem);
		conDestAgenda.addHas(m29);
		m29.setGrammar(null, g1);

		Move m30 = factory.createMove("usermov_affirm_departure_place");
		m30.addSemantic(conDepSem);
		m30.addContrarySemantic(rejDepSem);
		conDepAgenda.addHas(m30);
		m30.setGrammar(null, g1);

		Move m31 = factory.createMove("usermov_affirm_travel_time");
		m31.addSemantic(conTimeSem);
		m31.addContrarySemantic(rejTimeSem);
		conTimeAgenda.addHas(m31);
		m31.setGrammar(null, g1);

		Move m32 = factory.createMove("usermov_deny_bus_number");
		m32.addSemantic(rejBusSem);
		m32.addContrarySemantic(busSemG);
		m32.addContrarySemantic(conBusSem);
		conBusAgenda.addHas(m32);
		m32.setGrammar(null, g1);

		Move m33 = factory.createMove("usermov_deny_arrival_place");
		m33.addSemantic(rejDestSem);
		m33.addContrarySemantic(destSemG);
		m33.addContrarySemantic(conDestSem);
		conDestAgenda.addHas(m33);
		m33.setGrammar(null, g1);

		Move m34 = factory.createMove("usermov_deny_departure_place");
		m34.addSemantic(rejDepSem);
		m34.addContrarySemantic(depSemG);
		m34.addContrarySemantic(conDepSem);
		conDepAgenda.addHas(m34);
		m34.setGrammar(null, g1);

		Move m35 = factory.createMove("usermov_deny_travel_time");
		m35.addSemantic(rejTimeSem);
		m35.addContrarySemantic(timeSemG);
		m35.addContrarySemantic(conTimeSem);
		conTimeAgenda.addHas(m35);
		m35.setGrammar(null, g1);
		

		try {
			factory.manager.saveOntology(factory.onto,
					factory.manager.getOntologyDocumentIRI(factory.onto));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Settings.uri = uriSave;
	}
}
