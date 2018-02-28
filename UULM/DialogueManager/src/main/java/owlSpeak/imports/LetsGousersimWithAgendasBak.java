//dividing places to covered and uncovered

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
import owlSpeak.engine.MasterSlot;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Reward;

//TODO check folders of files to be created
public class LetsGousersimWithAgendasBak {
	// Generate all sentence combinations due to "*"
	// input is a list of the words separated by " " and number of asterisks in
	// the sentence

	public static String[] Generate_combinations(String[] list, int z1)
			throws Exception {
		int i_final = list.length - 1;
		String final_list[] = new String[(int) Math.pow(2, z1)];
		for (int z3 = 0; z3 != (int) ((Math.pow(2, z1))); z3++) {
			String binaryString = Integer.toBinaryString(z3);
			while (binaryString.length() < z1) {
				binaryString = "0" + binaryString;
			}
			System.out.println("binaryString= " + binaryString);
			char[] charArray = binaryString.toCharArray();
			String name3 = "";

			int z6;
			z6 = 0;
			for (int z7 = 0; z7 <= i_final; z7++) {
				if (list[z7].contains("*")) {
					if (charArray[z6] == '1') {
						name3 = name3 + " " + list[z7];
					}
					z6 = z6 + 1;
				} else {
					name3 = name3 + " " + list[z7];
				}
			}
			System.out.println("name3 = " + name3);
			final_list[z3] = name3;
			System.out.println("final_list[ " + z3 + " ] = " + final_list[z3]);

		}

		return final_list;
	}

	public static void main(String[] argv) throws Exception {
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
		@SuppressWarnings("unused")
		ServletEngine engine = new ServletEngine();
		String uriSave = Settings.uri;
		Settings.uri = "http://localhost:8080/OwlSpeakOnto.owl";
		String filename = "letsGoUs.owl";
		String path = Settings.homePath;

		OSFactory factory = null;
		try {
			factory = OwlSpeakOntology.createOSFactoryEmptyOnto(filename, path);
			// factory = createOSFactory("test.owl", "c:/OwlSpeak/",
			// "OwlSpeakOnto.owl");
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

		// Dialogue Strategy

		/**
		 * 0 = Dialogue Strategy non-adaptive Version User_initiative<br/>
		 * 1 = Dialogue Strategy non-adaptive System_initiative<br/>
		 * 2 = Dialogue Strategy adaptive<br/>
		 * 3 = Dialogue Strategy non-adaptive Mixed_initiative<br/>
		 * 4 = Dialogue Strategy adaptive3<br/>
		 * 5 = Dialogue Strategy non-adaptive Mixed_initiative3<br/>
		 * 6 = Dialogue Strategy non-adaptive user-init3<br/>
		 * 7 = Dialogue Strategy non-adaptive System_initiative3<br/>
		 * 8 = Dialogue Strategy adaptive4<br/>
		 */
		int dialogueStrategy = 7;

		// create rewards
		Reward rewDefault = factory.createReward("rew_default");
		rewDefault.setRewardValue(-1);
		rewDefault.setSpecialReward("default_reward");
		Reward rewSuccess = factory.createReward("rew_success");
		rewSuccess.setRewardValue(100);
		rewSuccess.setSpecialReward("success_reward");
		Reward rewFailure = factory.createReward("rew_failure");
		rewFailure.setRewardValue(-20);
		rewFailure.setSpecialReward("abort_reward");

		// Generate SemanticsGroups

		SemanticGroup busSemG = factory.createSemanticGroup("Bus_route_sem");
		busSemG.setFieldTotals(1000);

		SemanticGroup depSemG = factory
				.createSemanticGroup("Departure_place_sem");
		depSemG.setFieldTotals(1000);

		SemanticGroup destSemG = factory.createSemanticGroup("Destination_sem");
		destSemG.setFieldTotals(1000);

		SemanticGroup timeSemG = factory.createSemanticGroup("Time_sem");
		timeSemG.setFieldTotals(3600);
		
		SemanticGroup hangupSemGroup = factory.createSemanticGroup("hangUp_sem_grp");
		hangupSemGroup.setFieldTotals(1);

		// Generate Semantics
		// Semantic S1 = factory.createSemantic("Bus_route_sem");
		// S1.addSemanticGroup(SG1);
		// Semantic S2 = factory.createSemantic("Departure_place_sem");
		// S2.addSemanticGroup(SG2);
		// Semantic S3 = factory.createSemantic("Destination_sem");
		// S3.addSemanticGroup(SG3);
		// Semantic S4 = factory.createSemantic("Time_sem");
		// S4.addSemanticGroup(SG4);

		Semantic conDestSem = factory.createSemantic("Confirm_Destination_sem");
		conDestSem.setConfirmationInfo(true);
		conDestSem.addSemanticGroup(destSemG);
		Semantic conDepSem = factory
				.createSemantic("Confirm_Departure_place_sem");
		conDepSem.setConfirmationInfo(true);
		conDepSem.addSemanticGroup(depSemG);
		Semantic conTimeSem = factory.createSemantic("Confirm_Time_sem");
		conTimeSem.setConfirmationInfo(true);
		conTimeSem.addSemanticGroup(timeSemG);
		Semantic conBusSem = factory.createSemantic("Confirm_Bus_route_sem");
		conBusSem.setConfirmationInfo(true);
		conBusSem.addSemanticGroup(busSemG);

		Semantic hangUpSem = factory.createSemantic("hangUp_sem");
		hangUpSem.addSemanticGroup(hangupSemGroup);
		hangupSemGroup.addContainedSemantic(hangUpSem);

		// Generate Variables
		Variable busVar = factory.createVariable("Bus_route_var");
		busSemG.addVariable(busVar); // connecting the variable to the
										// corresponding
		busVar.addBelongsToSemantic(busSemG); // Connecting the Semantic to the
		// corresponding Variable

		Variable depVar = factory.createVariable("Departure_place_var");
		depSemG.addVariable(depVar);
		depVar.addBelongsToSemantic(depSemG);

		Variable destVar = factory.createVariable("Destination_var");
		destSemG.addVariable(destVar);
		destVar.addBelongsToSemantic(destSemG);

		Variable timeVar = factory.createVariable("Time_var");
		timeSemG.addVariable(timeVar);
		timeVar.addBelongsToSemantic(timeSemG);

		Variable InteractionQuality = factory
				.createVariable("InteractionQuality");
		InteractionQuality.setDefaultValue("", "5");

		// Generate SummaryAgendas

		SummaryAgenda request = factory
				.createSummaryAgenda("Request_SumAgenda");
		request.setRole(request.getRole(), "collection");
		request.setType(request.getTypeString(), "request");

		SummaryAgenda confirmation = factory
				.createSummaryAgenda("Confirm_SumAgenda");
		confirmation.setRole(confirmation.getRole(), "confirmation");
		confirmation.setType(confirmation.getTypeString(), "confirmation");

		SummaryAgenda start = factory.createSummaryAgenda("Start_SumAgenda");
		start.setRole(start.getRole(), "collection");
		start.setType(start.getTypeString(), "announcement");

		SummaryAgenda exit = factory.createSummaryAgenda("Exit_SumAgenda");
		exit.setRole(exit.getRole(), "collection");
		exit.setType(exit.getTypeString(), "announcement");

		// Generate Agendas
		Agenda reactToHangupAgenda = factory
				.createAgenda("reactToHangup_agenda");
		reactToHangupAgenda.addRequires(hangUpSem);
		reactToHangupAgenda.addSummaryAgenda(exit);
		reactToHangupAgenda.setPriority(reactToHangupAgenda.getPriority(),
				10000);
		reactToHangupAgenda.setReward(rewFailure);
		rewFailure.addRewardingAgenda(reactToHangupAgenda);
		

		Agenda destAgenda = factory.createAgenda("Destination_agenda");
		destAgenda.addMustnot(conDestSem); // Fill the MustNot field in the
											// Agenda
		destAgenda.addMustnot(destSemG);
		destAgenda.setRole(destAgenda.getRole(), "collection"); // Setting the
																// Role whether
																// it
		// destAgenda.setPriority(destAgenda.getPriority(), 800); // is
		// confirmation
		// or collection
		destAgenda.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgenda);
		// destAgenda.setVariableOperator("",
		// "REQUIRES(%InteractionQuality%<<2)");

		// DestAgenda without departure place
		Agenda destAgendaWithoutDep = factory
				.createAgenda("Destination_withoutDep_agenda");
		destAgendaWithoutDep.addMustnot(conDestSem);
		destAgendaWithoutDep.addMustnot(conTimeSem);
		destAgendaWithoutDep.addMustnot(conBusSem);
		destAgendaWithoutDep.addRequires(conDepSem);

		destAgendaWithoutDep.setRole(destAgendaWithoutDep.getRole(),
				"collection");
		destAgendaWithoutDep.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutDep);

		// DestAgenda without Time
		Agenda destAgendaWithoutTime = factory
				.createAgenda("Destination_withoutTime_agenda");
		destAgendaWithoutTime.addMustnot(conDestSem);
		destAgendaWithoutTime.addMustnot(conDepSem);
		destAgendaWithoutTime.addMustnot(conBusSem);
		destAgendaWithoutTime.addRequires(conTimeSem);

		destAgendaWithoutTime.setRole(destAgendaWithoutTime.getRole(),
				"collection");
		destAgendaWithoutTime.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutTime);

		// DestAgenda without Time, Departure

		Agenda destAgendaWithoutTimeDep = factory
				.createAgenda("Destination_withoutTimeDep_agenda");
		destAgendaWithoutTimeDep.addMustnot(conDestSem);
		destAgendaWithoutTimeDep.addMustnot(conBusSem);
		destAgendaWithoutTimeDep.addRequires(conTimeSem);
		destAgendaWithoutTimeDep.addRequires(conDepSem);

		destAgendaWithoutTimeDep.setRole(destAgendaWithoutTimeDep.getRole(),
				"collection");
		destAgendaWithoutTimeDep.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutTimeDep);

		// DestAgenda without Busnumber

		Agenda destAgendaWithoutBus = factory
				.createAgenda("Destination_withoutBus_agenda");
		destAgendaWithoutBus.addMustnot(conDestSem);
		destAgendaWithoutBus.addMustnot(conDepSem);
		destAgendaWithoutBus.addMustnot(conTimeSem);

		destAgendaWithoutBus.addRequires(conBusSem);

		destAgendaWithoutBus.setRole(destAgendaWithoutBus.getRole(),
				"collection");
		destAgendaWithoutBus.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutBus);

		// DestAgenda without Busnumber, Departure

		Agenda destAgendaWithoutBusDep = factory
				.createAgenda("Destination_withoutBusDep_agenda");
		destAgendaWithoutBusDep.addMustnot(conDestSem);
		destAgendaWithoutBusDep.addMustnot(conTimeSem);

		destAgendaWithoutBusDep.addRequires(conBusSem);
		destAgendaWithoutBusDep.addRequires(conDepSem);

		destAgendaWithoutBusDep.setRole(destAgendaWithoutBusDep.getRole(),
				"collection");
		destAgendaWithoutBusDep.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutBusDep);

		// DestAgenda without Busnumber, Time

		Agenda destAgendaWithoutBusTime = factory
				.createAgenda("Destination_withoutBusTime_agenda");
		destAgendaWithoutBusTime.addMustnot(conDestSem);
		destAgendaWithoutBusTime.addMustnot(conDepSem);

		destAgendaWithoutBusTime.addRequires(conBusSem);
		destAgendaWithoutBusTime.addRequires(conTimeSem);

		destAgendaWithoutBusTime.setRole(destAgendaWithoutBusTime.getRole(),
				"collection");
		destAgendaWithoutBusTime.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutBusTime);

		// DestAgenda without Busnumber,Time,Departure

		Agenda destAgendaWithoutBusTimeDep = factory
				.createAgenda("Destination_withoutBusTimeDep_agenda");
		destAgendaWithoutBusTimeDep.addMustnot(conDestSem);

		destAgendaWithoutBusTimeDep.addRequires(conDepSem);
		destAgendaWithoutBusTimeDep.addRequires(conBusSem);
		destAgendaWithoutBusTimeDep.addRequires(conTimeSem);

		destAgendaWithoutBusTimeDep.setRole(
				destAgendaWithoutBusTimeDep.getRole(), "collection");
		destAgendaWithoutBusTimeDep.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaWithoutBusTimeDep);

		// DestAgenda allAllowed
		Agenda destAgendaAll = factory.createAgenda("DestinationAll_agenda");
		destAgendaAll.setRole(destAgendaAll.getRole(), "collection");
		destAgendaAll.addSummaryAgenda(request);
		request.addSummarizedAgenda(destAgendaAll);
		destAgendaAll.addMustnot(conTimeSem);
		destAgendaAll.addMustnot(conDepSem);
		destAgendaAll.addMustnot(conDestSem);
		destAgendaAll.addMustnot(conBusSem);

		// DepAgenda
		Agenda depAgenda = factory.createAgenda("Departure_place_agenda");
		depAgenda.addMustnot(conDepSem);
		depAgenda.addMustnot(depSemG);
		depAgenda.setRole(depAgenda.getRole(), "collection");
		// depAgenda.setPriority(depAgenda.getPriority(), 1000);
		depAgenda.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgenda);
		// depAgenda.setVariableOperator("",
		// "REQUIRES(%InteractionQuality%<<2)");

		// DepAgenda without Destination
		Agenda depAgendaWithoutDest = factory
				.createAgenda("Depplace_withoutDest_agenda");
		depAgendaWithoutDest.addMustnot(conDepSem);
		depAgendaWithoutDest.addMustnot(conTimeSem);
		depAgendaWithoutDest.addMustnot(conBusSem);
		depAgendaWithoutDest.addRequires(conDestSem);

		depAgendaWithoutDest.setRole(depAgendaWithoutDest.getRole(),
				"collection");
		depAgendaWithoutDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutDest);

		// DepAgenda without Time
		Agenda depAgendaWithoutTime = factory
				.createAgenda("Depplace_withoutTime_agenda");
		depAgendaWithoutTime.addMustnot(conDepSem);
		depAgendaWithoutTime.addMustnot(conDestSem);
		depAgendaWithoutTime.addMustnot(conBusSem);
		depAgendaWithoutTime.addRequires(conTimeSem);

		depAgendaWithoutTime.setRole(depAgendaWithoutTime.getRole(),
				"collection");
		depAgendaWithoutTime.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutTime);

		// DepAgenda without Time, Destination

		Agenda depAgendaWithoutTimeDest = factory
				.createAgenda("Depplace_withoutTimeDest_agenda");
		depAgendaWithoutTimeDest.addMustnot(conDepSem);
		depAgendaWithoutTimeDest.addMustnot(conBusSem);
		depAgendaWithoutTimeDest.addRequires(conTimeSem);
		depAgendaWithoutTimeDest.addRequires(conDestSem);

		depAgendaWithoutTimeDest.setRole(depAgendaWithoutTimeDest.getRole(),
				"collection");
		depAgendaWithoutTimeDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutTimeDest);

		// DepAgenda without Busnumber

		Agenda depAgendaWithoutBus = factory
				.createAgenda("Depplace_withoutBus_agenda");
		depAgendaWithoutBus.addMustnot(conDestSem);
		depAgendaWithoutBus.addMustnot(conDepSem);
		depAgendaWithoutBus.addMustnot(conTimeSem);

		depAgendaWithoutBus.addRequires(conBusSem);

		depAgendaWithoutBus
				.setRole(depAgendaWithoutBus.getRole(), "collection");
		depAgendaWithoutBus.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutBus);

		// DepAgenda without Busnumber, Destination

		Agenda depAgendaWithoutBusDest = factory
				.createAgenda("Depplace_withoutBusDest_agenda");

		depAgendaWithoutBusDest.addMustnot(conDepSem);
		depAgendaWithoutBusDest.addMustnot(conTimeSem);
		depAgendaWithoutBusDest.addRequires(conDestSem);
		depAgendaWithoutBusDest.addRequires(conBusSem);

		depAgendaWithoutBusDest.setRole(depAgendaWithoutBusDest.getRole(),
				"collection");
		depAgendaWithoutBusDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutBusDest);

		// DepAgenda without Busnumber, Time

		Agenda depAgendaWithoutBusTime = factory
				.createAgenda("Depplace_withoutBusTime_agenda");

		depAgendaWithoutBusTime.addMustnot(conDepSem);
		depAgendaWithoutBusTime.addMustnot(conDestSem);
		depAgendaWithoutBusTime.addRequires(conTimeSem);
		depAgendaWithoutBusTime.addRequires(conBusSem);

		depAgendaWithoutBusTime.setRole(depAgendaWithoutBusTime.getRole(),
				"collection");
		depAgendaWithoutBusTime.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutBusTime);

		// DepAgenda without Busnumber, Time, Dest

		Agenda depAgendaWithoutBusTimeDest = factory
				.createAgenda("Depplace_withoutBusTimeDest_agenda");

		depAgendaWithoutBusTimeDest.addMustnot(conDepSem);
		depAgendaWithoutBusTimeDest.addRequires(conDestSem);
		depAgendaWithoutBusTimeDest.addRequires(conTimeSem);
		depAgendaWithoutBusTimeDest.addRequires(conBusSem);

		depAgendaWithoutBusTimeDest.setRole(
				depAgendaWithoutBusTimeDest.getRole(), "collection");
		depAgendaWithoutBusTimeDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaWithoutBusTimeDest);

		// DepAgenda allAllowed
		Agenda depAgendaAll = factory.createAgenda("DepplaceAll_agenda");
		depAgendaAll.addMustnot(conTimeSem);
		depAgendaAll.addMustnot(conDepSem);
		depAgendaAll.addMustnot(conDestSem);
		depAgendaAll.addMustnot(conBusSem);
		depAgendaAll.setRole(depAgendaAll.getRole(), "collection");
		depAgendaAll.addSummaryAgenda(request);
		request.addSummarizedAgenda(depAgendaAll);

		// Bus Agenda
		Agenda busAgenda = factory.createAgenda("Bus_routes_agenda");
		busAgenda.addMustnot(conBusSem);
		busAgenda.addMustnot(busSemG);
		busAgenda.setRole(busAgenda.getRole(), "collection");
		// busAgenda.setPriority(busAgenda.getPriority(), 400);
		busAgenda.addSummaryAgenda(request);
		request.addSummarizedAgenda(busAgenda);

		// Time Agenda
		Agenda timeAgenda = factory.createAgenda("Time_agenda");
		timeAgenda.addMustnot(conTimeSem);
		timeAgenda.addMustnot(timeSemG);
		timeAgenda.setRole(timeAgenda.getRole(), "collection");
		// timeAgenda.setPriority(timeAgenda.getPriority(), 600);
		timeAgenda.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgenda);
		// timeAgenda.setVariableOperator("",
		// "REQUIRES(%InteractionQuality%<<2)");

		// TimeAgenda without Destination
		Agenda timeAgendaWithoutDest = factory
				.createAgenda("Time_withoutDest_agenda");
		timeAgendaWithoutDest.addMustnot(conTimeSem);
		timeAgendaWithoutDest.addMustnot(conDepSem);
		timeAgendaWithoutDest.addMustnot(conBusSem);
		timeAgendaWithoutDest.addRequires(conDestSem);

		timeAgendaWithoutDest.setRole(timeAgendaWithoutDest.getRole(),
				"collection");
		timeAgendaWithoutDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutDest);

		// Time Agenda without Departure place
		Agenda timeAgendaWithoutDep = factory
				.createAgenda("Time_withoutDep_agenda");
		timeAgendaWithoutDep.addMustnot(conTimeSem);
		timeAgendaWithoutDep.addMustnot(conDestSem);
		timeAgendaWithoutDep.addMustnot(conBusSem);
		timeAgendaWithoutDep.addRequires(conDepSem);

		timeAgendaWithoutDep.setRole(timeAgendaWithoutDep.getRole(),
				"collection");
		timeAgendaWithoutDep.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutDep);

		// Time Agenda without Departureplace, Destination

		Agenda timeAgendaWithoutDepDest = factory
				.createAgenda("Time_withoutDepDest_agenda");
		timeAgendaWithoutDepDest.addMustnot(conTimeSem);
		timeAgendaWithoutDepDest.addMustnot(conBusSem);
		timeAgendaWithoutDepDest.addRequires(conDepSem);
		timeAgendaWithoutDepDest.addRequires(conDestSem);

		timeAgendaWithoutDepDest.setRole(timeAgendaWithoutDepDest.getRole(),
				"collection");
		timeAgendaWithoutDepDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutDepDest);

		// Time Agenda without Busnumber

		Agenda timeAgendaWithoutBus = factory
				.createAgenda("Time_withoutBus_agenda");
		timeAgendaWithoutBus.addMustnot(conDestSem);
		timeAgendaWithoutBus.addMustnot(conDepSem);
		timeAgendaWithoutBus.addMustnot(conTimeSem);

		timeAgendaWithoutBus.addRequires(conBusSem);

		timeAgendaWithoutBus.setRole(timeAgendaWithoutBus.getRole(),
				"collection");
		timeAgendaWithoutBus.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutBus);

		// Time Agenda wihout Busnumber, Destination

		Agenda timeAgendaWithoutBusDest = factory
				.createAgenda("Time_withoutBusDest_agenda");

		timeAgendaWithoutBusDest.addMustnot(conDepSem);
		timeAgendaWithoutBusDest.addMustnot(conTimeSem);
		timeAgendaWithoutBusDest.addRequires(conDestSem);
		timeAgendaWithoutBusDest.addRequires(conBusSem);

		timeAgendaWithoutBusDest.setRole(timeAgendaWithoutBusDest.getRole(),
				"collection");
		timeAgendaWithoutBusDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutBusDest);

		// Time Agenda without Busnumber, Departure

		Agenda timeAgendaWithoutBusDep = factory
				.createAgenda("Time_withoutBusDep_agenda");

		timeAgendaWithoutBusDep.addMustnot(conDestSem);
		timeAgendaWithoutBusDep.addMustnot(conTimeSem);
		timeAgendaWithoutBusDep.addRequires(conDepSem);
		timeAgendaWithoutBusDep.addRequires(conBusSem);

		timeAgendaWithoutBusDep.setRole(timeAgendaWithoutBusDep.getRole(),
				"collection");
		timeAgendaWithoutBusDep.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutBusDep);

		// Time Agenda without Busnumber,Destination, Departure

		Agenda timeAgendaWithoutBusDepDest = factory
				.createAgenda("Time_withoutBusDepDest_agenda");

		timeAgendaWithoutBusDepDest.addMustnot(conTimeSem);
		timeAgendaWithoutBusDepDest.addRequires(conDepSem);
		timeAgendaWithoutBusDepDest.addRequires(conBusSem);
		timeAgendaWithoutBusDepDest.addRequires(conDestSem);

		timeAgendaWithoutBusDepDest.setRole(
				timeAgendaWithoutBusDepDest.getRole(), "collection");
		timeAgendaWithoutBusDepDest.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaWithoutBusDepDest);

		// TimeAgenda allAllowed
		Agenda timeAgendaAll = factory.createAgenda("TimeAll_agenda");
		timeAgendaAll.addMustnot(conTimeSem);
		timeAgendaAll.addMustnot(conDepSem);
		timeAgendaAll.addMustnot(conDestSem);
		timeAgendaAll.addMustnot(conBusSem);

		timeAgendaAll.setRole(timeAgendaAll.getRole(), "collection");
		timeAgendaAll.addSummaryAgenda(request);
		request.addSummarizedAgenda(timeAgendaAll);

		// Open Agenda
		Agenda openAgenda = factory.createAgenda("open_agenda");

		openAgenda.addMustnot(conDestSem);
		openAgenda.addMustnot(conBusSem);
		openAgenda.addMustnot(conTimeSem);
		openAgenda.addMustnot(conDepSem);
		openAgenda.setRole(openAgenda.getRole(), "collection");
		openAgenda.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda);
		// openAgenda.setPriority(openAgenda.getPriority(), 100);
		// openAgenda.setVariableOperator("","REQUIRES(%InteractionQuality%>>1)");

		Agenda openAgenda_afterDepCon = factory
				.createAgenda("open_afterDepCon_agenda");
		openAgenda_afterDepCon.setRole(openAgenda_afterDepCon.getRole(),
				"collection");
		openAgenda_afterDepCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterDepCon);
		openAgenda_afterDepCon.addRequires(conDepSem);
		openAgenda_afterDepCon.addMustnot(conBusSem);
		openAgenda_afterDepCon.addMustnot(conTimeSem);
		openAgenda_afterDepCon.addMustnot(conDestSem);

		Agenda openAgenda_afterDestCon = factory
				.createAgenda("open_afterDestCon_agenda");
		openAgenda_afterDestCon.setRole(openAgenda_afterDestCon.getRole(),
				"collection");
		openAgenda_afterDestCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterDestCon);
		openAgenda_afterDestCon.addRequires(conDestSem);
		openAgenda_afterDestCon.addMustnot(conBusSem);
		openAgenda_afterDestCon.addMustnot(conTimeSem);
		openAgenda_afterDestCon.addMustnot(conDepSem);

		Agenda openAgenda_afterTimeCon = factory
				.createAgenda("open_afterTimeCon_agenda");
		openAgenda_afterTimeCon.setRole(openAgenda_afterTimeCon.getRole(),
				"collection");
		openAgenda_afterTimeCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterTimeCon);
		openAgenda_afterTimeCon.addRequires(conTimeSem);
		openAgenda_afterTimeCon.addMustnot(conBusSem);
		openAgenda_afterTimeCon.addMustnot(conDestSem);
		openAgenda_afterTimeCon.addMustnot(conDepSem);

		Agenda openAgenda_afterBusCon = factory
				.createAgenda("open_afterBusCon_agenda");
		openAgenda_afterBusCon.setRole(openAgenda_afterBusCon.getRole(),
				"collection");
		openAgenda_afterBusCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusCon);
		openAgenda_afterBusCon.addRequires(conBusSem);
		openAgenda_afterBusCon.addMustnot(conTimeSem);
		openAgenda_afterBusCon.addMustnot(conDestSem);
		openAgenda_afterBusCon.addMustnot(conDepSem);

		Agenda openAgenda_afterBusDepCon = factory
				.createAgenda("open_afterBusDepCon_agenda");
		openAgenda_afterBusDepCon.setRole(openAgenda_afterBusDepCon.getRole(),
				"collection");
		openAgenda_afterBusDepCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusDepCon);
		openAgenda_afterBusDepCon.addRequires(conBusSem);
		openAgenda_afterBusDepCon.addRequires(conDepSem);
		openAgenda_afterBusDepCon.addMustnot(conTimeSem);
		openAgenda_afterBusDepCon.addMustnot(conDestSem);

		Agenda openAgenda_afterBusDestCon = factory
				.createAgenda("open_afterBusDestCon_agenda");
		openAgenda_afterBusDestCon.setRole(
				openAgenda_afterBusDestCon.getRole(), "collection");
		openAgenda_afterBusDestCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusDestCon);
		openAgenda_afterBusDestCon.addRequires(conBusSem);
		openAgenda_afterBusDestCon.addRequires(conDestSem);
		openAgenda_afterBusDestCon.addMustnot(conDepSem);
		openAgenda_afterBusDestCon.addMustnot(conTimeSem);

		Agenda openAgenda_afterBusTimeCon = factory
				.createAgenda("open_afterBusTimeCon_agenda");
		openAgenda_afterBusTimeCon.setRole(
				openAgenda_afterBusTimeCon.getRole(), "collection");
		openAgenda_afterBusTimeCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusTimeCon);
		openAgenda_afterBusTimeCon.addRequires(conBusSem);
		openAgenda_afterBusTimeCon.addRequires(conTimeSem);
		openAgenda_afterBusTimeCon.addMustnot(conDestSem);
		openAgenda_afterBusTimeCon.addMustnot(conDepSem);

		Agenda openAgenda_afterTimeDepCon = factory
				.createAgenda("open_afterTimeDepCon_agenda");
		openAgenda_afterTimeDepCon.setRole(
				openAgenda_afterTimeDepCon.getRole(), "collection");
		openAgenda_afterTimeDepCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterTimeDepCon);
		openAgenda_afterTimeDepCon.addRequires(conTimeSem);
		openAgenda_afterTimeDepCon.addRequires(conDepSem);
		openAgenda_afterTimeDepCon.addMustnot(conBusSem);
		openAgenda_afterTimeDepCon.addMustnot(conDestSem);

		Agenda openAgenda_afterTimeDestCon = factory
				.createAgenda("open_afterTimeDestCon_agenda");
		openAgenda_afterTimeDestCon.setRole(openAgenda_afterTimeCon.getRole(),
				"collection");
		openAgenda_afterTimeDestCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterTimeDestCon);
		openAgenda_afterTimeDestCon.addRequires(conTimeSem);
		openAgenda_afterTimeDestCon.addRequires(conDestSem);
		openAgenda_afterTimeDestCon.addMustnot(conDepSem);
		openAgenda_afterTimeDestCon.addMustnot(conBusSem);

		Agenda openAgenda_afterDepDestCon = factory
				.createAgenda("open_afterDepDestCon_agenda");
		openAgenda_afterDepDestCon.setRole(openAgenda_afterTimeCon.getRole(),
				"collection");
		openAgenda_afterDepDestCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterDepDestCon);
		openAgenda_afterDepDestCon.addRequires(conDepSem);
		openAgenda_afterDepDestCon.addRequires(conDestSem);
		openAgenda_afterDepDestCon.addMustnot(conTimeSem);
		openAgenda_afterDepDestCon.addMustnot(conBusSem);

		Agenda openAgenda_afterBusTimeDepCon = factory
				.createAgenda("open_afterBusTimeDepCon_agenda");
		openAgenda_afterBusTimeDepCon.setRole(
				openAgenda_afterBusTimeDepCon.getRole(), "collection");
		openAgenda_afterBusTimeDepCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusTimeDepCon);
		openAgenda_afterBusTimeDepCon.addRequires(conBusSem);
		openAgenda_afterBusTimeDepCon.addRequires(conTimeSem);
		openAgenda_afterBusTimeDepCon.addRequires(conDepSem);
		openAgenda_afterBusTimeDepCon.addMustnot(conDestSem);

		Agenda openAgenda_afterBusTimeDestCon = factory
				.createAgenda("open_afterBusTimeDestCon_agenda");
		openAgenda_afterBusTimeDestCon.setRole(
				openAgenda_afterBusTimeDestCon.getRole(), "collection");
		openAgenda_afterBusTimeDestCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusTimeDestCon);
		openAgenda_afterBusTimeDestCon.addRequires(conBusSem);
		openAgenda_afterBusTimeDestCon.addRequires(conTimeSem);
		openAgenda_afterBusTimeDestCon.addRequires(conDestSem);
		openAgenda_afterBusTimeDestCon.addMustnot(conDepSem);

		Agenda openAgenda_afterBusDepDestCon = factory
				.createAgenda("open_afterBusDepDestCon_agenda");
		openAgenda_afterBusDepDestCon.setRole(
				openAgenda_afterBusDepDestCon.getRole(), "collection");
		openAgenda_afterBusDepDestCon.addSummaryAgenda(request);
		request.addSummarizedAgenda(openAgenda_afterBusDepDestCon);
		openAgenda_afterBusDepDestCon.addRequires(conBusSem);
		openAgenda_afterBusDepDestCon.addRequires(conDepSem);
		openAgenda_afterBusDepDestCon.addRequires(conDestSem);
		openAgenda_afterBusDepDestCon.addMustnot(conTimeSem);

		Agenda infoAgenda = factory.createAgenda("Here_your_info_Agenda");
		// A6.addRequires(SG1);
		infoAgenda.addRequires(depSemG);
		infoAgenda.addRequires(destSemG);
		// A6.addRequires(SG4);
		// A6.addRequires(S5);
		// A6.addRequires(S6);
		// A6.addRequires(S7);
		// A6.addRequires(S8);
		infoAgenda.setRole(infoAgenda.getRole(), "collection");

		Agenda notCovAgenda = factory.createAgenda("not_covered_Agenda");
		notCovAgenda.addRequires(conDestSem);
		notCovAgenda.addRequires(conDepSem);
		notCovAgenda.addRequires(depSemG);
		notCovAgenda.addRequires(destSemG);
		notCovAgenda.setRole(notCovAgenda.getRole(), "collection");

		Agenda conBusAgenda = factory.createAgenda("Confirm_Bus_route_Agenda");
		conBusAgenda.addRequires(busSemG);

		conBusAgenda.addMustnot(conBusSem);
		conBusAgenda.setRole(conBusAgenda.getRole(), "confirmation");
		// conBusAgenda.setPriority(conBusAgenda.getPriority(), 6000);
		conBusAgenda.addSummaryAgenda(confirmation);
		confirmation.addSummarizedAgenda(conBusAgenda);

		Agenda conDestAgenda = factory
				.createAgenda("Confirm_Destination_Agenda");
		conDestAgenda.addRequires(destSemG);
		// conDestAgenda.addRequires(conDepSem);
		conDestAgenda.addMustnot(conDestSem);
		conDestAgenda.setRole(conDestAgenda.getRole(), "confirmation");
		// conDestAgenda.setPriority(conDestAgenda.getPriority(), 8000);
		conDestAgenda.addSummaryAgenda(confirmation);
		confirmation.addSummarizedAgenda(conDestAgenda);

		Agenda conDepAgenda = factory
				.createAgenda("Confirm_Departure_place_Agenda");
		conDepAgenda.addRequires(depSemG);
		// conDepAgenda.addRequires(timeSemG);
		// conDepAgenda.addRequires(destSemG);
		conDepAgenda.addMustnot(conDepSem);
		conDepAgenda.setRole(conDepAgenda.getRole(), "confirmation");
		// conDepAgenda.setPriority(conDepAgenda.getPriority(), 10000);
		conDepAgenda.addSummaryAgenda(confirmation);
		confirmation.addSummarizedAgenda(conDepAgenda);

		Agenda conTimeAgenda = factory.createAgenda("Confirm_Time_Agenda");
		conTimeAgenda.addRequires(timeSemG);
		// conTimeAgenda.addRequires(conDestSem);
		conTimeAgenda.addMustnot(conTimeSem);
		conTimeAgenda.setRole(conTimeAgenda.getRole(), "confirmation");
		// conTimeAgenda.setPriority(conTimeAgenda.getPriority(), 4000);
		conTimeAgenda.addSummaryAgenda(confirmation);
		confirmation.addSummarizedAgenda(conTimeAgenda);

		Agenda exitAgenda = factory.createAgenda("Exit_Agenda");
		exitAgenda.addRequires(conDepSem);
		exitAgenda.addRequires(conTimeSem);
		exitAgenda.addRequires(conDestSem);
		exitAgenda.setReward(rewSuccess);
		rewSuccess.addRewardingAgenda(exitAgenda);

		exitAgenda.addSummaryAgenda(exit);
		exit.addSummarizedAgenda(exitAgenda);
		// exitAgenda.setPriority(exitAgenda.getPriority(), 200);
		exitAgenda.setRole(exitAgenda.getRole(), "collection");

		Agenda master = factory.createAgenda("masteragenda");
		master.setIsMasterBool(false, true);
		master.addSummaryAgenda(start);
		start.addSummarizedAgenda(master);
		master.addNext(reactToHangupAgenda);

		// Dialogue Strategy non-adaptive Version User_initiative

		if (dialogueStrategy == 0) {

			master.addNext(openAgenda);
			openAgenda.addNext(openAgenda);
			openAgenda.addNext(conTimeAgenda);
			openAgenda.addNext(conDepAgenda);
			openAgenda.addNext(conDestAgenda);

			conTimeAgenda.addNext(openAgenda);
			conTimeAgenda.addNext(openAgenda_afterTimeCon);
			openAgenda_afterTimeCon.addNext(conDestAgenda);
			openAgenda_afterTimeCon.addNext(conDepAgenda);

			openAgenda_afterTimeCon.addNext(openAgenda_afterTimeCon);
			openAgenda_afterTimeCon.addNext(openAgenda_afterTimeDestCon);
			openAgenda_afterTimeCon.addNext(openAgenda_afterTimeDepCon);

			conDepAgenda.addNext(openAgenda);
			conDepAgenda.addNext(openAgenda_afterDepCon);
			openAgenda_afterDepCon.addNext(conDestAgenda);
			openAgenda_afterDepCon.addNext(conTimeAgenda);
			openAgenda_afterDepCon.addNext(openAgenda_afterDepCon);
			openAgenda_afterDepCon.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterDepCon.addNext(openAgenda_afterTimeDepCon);

			conDestAgenda.addNext(openAgenda);
			conDestAgenda.addNext(openAgenda_afterDestCon);
			openAgenda_afterDestCon.addNext(conTimeAgenda);
			openAgenda_afterDestCon.addNext(conDepAgenda);

			openAgenda_afterDestCon.addNext(openAgenda_afterDestCon);
			openAgenda_afterDestCon.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterDestCon.addNext(openAgenda_afterTimeDestCon);

			master.addNext(exitAgenda);
			conDepAgenda.setPriority(0, 10000);
			conTimeAgenda.setPriority(0, 6000);
			conDestAgenda.setPriority(0, 8000);
			openAgenda.setPriority(0, 100);
			openAgenda_afterDepCon.setPriority(0, 1000);
			openAgenda_afterDestCon.setPriority(0, 800);
			openAgenda_afterTimeCon.setPriority(0, 600);
			openAgenda_afterDepDestCon.setPriority(0, 1000);
			openAgenda_afterTimeDepCon.setPriority(0, 800);
			openAgenda_afterTimeDestCon.setPriority(0, 600);
			exitAgenda.setPriority(0, 1500);
		}

		// Dialogue Strategy non-adaptive System_initiative
		if (dialogueStrategy == 1) {

			master.addNext(depAgenda);
			master.addNext(destAgenda);
			master.addNext(timeAgenda);
			master.addNext(conTimeAgenda);
			master.addNext(conDepAgenda);
			master.addNext(conDestAgenda);
			master.addNext(exitAgenda);

			depAgenda.addNext(conDepAgenda);
			conDepAgenda.addNext(depAgenda);

			destAgenda.addNext(conDestAgenda);
			conDestAgenda.addNext(destAgenda);

			timeAgenda.addNext(conTimeAgenda);
			conTimeAgenda.addNext(timeAgenda);

			depAgenda.setPriority(0, 1000);
			destAgenda.setPriority(0, 800);
			timeAgenda.setPriority(0, 600);
			conDepAgenda.setPriority(0, 4000);
			conDestAgenda.setPriority(0, 3000);
			conTimeAgenda.setPriority(0, 2000);
			exitAgenda.setPriority(0, 200);

		}

		// Dialogue Strategy adaptive

		if (dialogueStrategy == 2) {

			master.addNext(openAgenda);
			master.addNext(exitAgenda);
			master.addNext(depAgendaAll);
			master.addNext(depAgenda);
			master.addNext(timeAgendaAll);
			master.addNext(timeAgenda);
			master.addNext(destAgenda);
			master.addNext(destAgendaAll);
			master.addNext(conTimeAgenda);
			master.addNext(conDestAgenda);
			master.addNext(conDepAgenda);

			depAgenda.addNext(conDepAgenda);
			conDepAgenda.addNext(depAgenda);

			depAgendaWithoutTimeDest.addNext(conDepAgenda);
			conDepAgenda.addNext(depAgendaWithoutTimeDest);
			conDepAgenda.addNext(destAgendaWithoutTimeDep);
			conDepAgenda.addNext(depAgendaWithoutTime);
			conDepAgenda.addNext(depAgendaWithoutDest);
			conDepAgenda.addNext(depAgendaAll);
			conDepAgenda.addNext(destAgendaAll);
			conDepAgenda.addNext(timeAgendaAll);
			conDepAgenda.addNext(timeAgendaWithoutDepDest);
			depAgendaWithoutTimeDest.addNext(depAgendaWithoutTimeDest);

			destAgenda.addNext(conDestAgenda);
			conDestAgenda.addNext(destAgenda);
			conDestAgenda.addNext(destAgendaWithoutTimeDep);
			conDestAgenda.addNext(depAgendaWithoutTimeDest);
			conDestAgenda.addNext(timeAgendaWithoutDepDest);
			conDestAgenda.addNext(destAgendaWithoutTime);
			conDestAgenda.addNext(destAgendaWithoutDep);
			conDestAgenda.addNext(depAgendaAll);
			conDestAgenda.addNext(destAgendaAll);
			conDestAgenda.addNext(timeAgendaAll);

			destAgendaWithoutTimeDep.addNext(conDestAgenda);
			destAgendaWithoutTimeDep.addNext(destAgendaWithoutTimeDep);

			timeAgenda.addNext(conTimeAgenda);
			conTimeAgenda.addNext(timeAgenda);

			timeAgendaWithoutDepDest.addNext(conTimeAgenda);
			conTimeAgenda.addNext(timeAgendaWithoutDepDest);
			conTimeAgenda.addNext(depAgendaWithoutTimeDest);
			conTimeAgenda.addNext(destAgendaWithoutTimeDep);
			conTimeAgenda.addNext(timeAgendaWithoutDep);
			conTimeAgenda.addNext(timeAgendaWithoutDest);
			conTimeAgenda.addNext(depAgendaAll);
			conTimeAgenda.addNext(destAgendaAll);
			conTimeAgenda.addNext(timeAgendaAll);

			timeAgendaWithoutDepDest.addNext(timeAgendaWithoutDepDest);

			depAgendaAll.addNext(depAgendaAll);
			depAgendaAll.addNext(conTimeAgenda);
			depAgendaAll.addNext(conDepAgenda);
			depAgendaAll.addNext(conDestAgenda);

			destAgendaAll.addNext(destAgendaAll);
			destAgendaAll.addNext(conTimeAgenda);
			destAgendaAll.addNext(conDepAgenda);
			destAgendaAll.addNext(conDestAgenda);

			timeAgendaAll.addNext(timeAgendaAll);
			timeAgendaAll.addNext(conTimeAgenda);
			timeAgendaAll.addNext(conDepAgenda);
			timeAgendaAll.addNext(conDestAgenda);

			conTimeAgenda.addNext(timeAgendaAll);
			conTimeAgenda.addNext(depAgendaAll);
			conTimeAgenda.addNext(destAgendaAll);

			conDestAgenda.addNext(destAgendaAll);
			conDestAgenda.addNext(depAgendaAll);
			conDestAgenda.addNext(timeAgendaAll);

			conDepAgenda.addNext(depAgendaAll);
			conDepAgenda.addNext(destAgendaAll);
			conDepAgenda.addNext(timeAgendaAll);

			destAgendaWithoutTime.addNext(destAgendaWithoutTime);
			conTimeAgenda.addNext(destAgendaWithoutTime);
			destAgendaWithoutTime.addNext(conDestAgenda);
			conTimeAgenda.addNext(depAgendaWithoutTime);
			depAgendaWithoutTime.addNext(conDepAgenda);
			depAgendaWithoutTime.addNext(depAgendaWithoutTime);

			destAgendaWithoutDep.addNext(destAgendaWithoutDep);
			conDepAgenda.addNext(destAgendaWithoutDep);
			destAgendaWithoutDep.addNext(conDestAgenda);
			conDepAgenda.addNext(timeAgendaWithoutDep);
			timeAgendaWithoutDep.addNext(conTimeAgenda);
			timeAgendaWithoutDep.addNext(timeAgendaWithoutDep);

			depAgendaWithoutDest.addNext(depAgendaWithoutDest);
			conDestAgenda.addNext(depAgendaWithoutDest);
			depAgendaWithoutDest.addNext(conDepAgenda);
			conDestAgenda.addNext(timeAgendaWithoutDest);
			timeAgendaWithoutDest.addNext(conTimeAgenda);
			timeAgendaWithoutDest.addNext(timeAgendaWithoutDest);

			destAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");
			depAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");
			timeAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");

			destAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");

			destAgendaWithoutDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutTimeDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDepDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutTimeDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");

			openAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");

			conDepAgenda.setPriority(0, 4000);
			conDestAgenda.setPriority(0, 3000);
			conTimeAgenda.setPriority(0, 2000);

			openAgenda.setPriority(0, 1500);
			depAgenda.setPriority(0, 1000);
			destAgenda.setPriority(0, 800);
			timeAgenda.setPriority(0, 600);

			depAgendaAll.setPriority(0, 1000);
			destAgendaAll.setPriority(0, 800);
			timeAgendaAll.setPriority(0, 600);

			depAgendaWithoutDest.setPriority(0, 1000);
			depAgendaWithoutTime.setPriority(0, 1000);

			destAgendaWithoutDep.setPriority(0, 800);
			destAgendaWithoutTime.setPriority(0, 800);

			timeAgendaWithoutDep.setPriority(0, 600);
			timeAgendaWithoutDest.setPriority(0, 600);

			destAgendaWithoutTimeDep.setPriority(0, 800);
			depAgendaWithoutTimeDest.setPriority(0, 1000);
			timeAgendaWithoutDepDest.setPriority(0, 600);

			exitAgenda.setPriority(0, 3000);

		}

		// Dialogue Strategy non-adaptive Mixed_initiative

		if (dialogueStrategy == 3) {

			master.addNext(openAgenda);
			master.addNext(exitAgenda);
			openAgenda.addNext(openAgenda);

			openAgenda.addNext(conTimeAgenda);
			conTimeAgenda.addNext(openAgenda);
			openAgenda.addNext(conDestAgenda);
			conDestAgenda.addNext(openAgenda);
			openAgenda.addNext(conDepAgenda);
			conDepAgenda.addNext(openAgenda);

			depAgendaWithoutTimeDest.addNext(conDepAgenda);
			conDepAgenda.addNext(depAgendaWithoutTimeDest);
			conDepAgenda.addNext(destAgendaWithoutTimeDep);
			conDepAgenda.addNext(depAgendaWithoutTime);
			conDepAgenda.addNext(depAgendaWithoutDest);
			depAgendaWithoutTimeDest.addNext(depAgendaWithoutTimeDest);

			conDestAgenda.addNext(destAgendaWithoutTimeDep);
			conDestAgenda.addNext(depAgendaWithoutTimeDest);
			conDestAgenda.addNext(timeAgendaWithoutDepDest);
			conDestAgenda.addNext(destAgendaWithoutTime);
			conDestAgenda.addNext(destAgendaWithoutDep);
			destAgendaWithoutTimeDep.addNext(conDestAgenda);
			destAgendaWithoutTimeDep.addNext(destAgendaWithoutTimeDep);

			timeAgendaWithoutDepDest.addNext(conTimeAgenda);
			conTimeAgenda.addNext(timeAgendaWithoutDepDest);
			conTimeAgenda.addNext(depAgendaWithoutTimeDest);
			conTimeAgenda.addNext(destAgendaWithoutTimeDep);
			conTimeAgenda.addNext(timeAgendaWithoutDep);
			conTimeAgenda.addNext(timeAgendaWithoutDest);
			timeAgendaWithoutDepDest.addNext(timeAgendaWithoutDepDest);

			destAgendaWithoutTime.addNext(destAgendaWithoutTime);
			conTimeAgenda.addNext(destAgendaWithoutTime);
			destAgendaWithoutTime.addNext(conDestAgenda);
			conTimeAgenda.addNext(depAgendaWithoutTime);
			depAgendaWithoutTime.addNext(conDepAgenda);
			depAgendaWithoutTime.addNext(depAgendaWithoutTime);

			destAgendaWithoutDep.addNext(destAgendaWithoutDep);
			conDepAgenda.addNext(destAgendaWithoutDep);
			conDepAgenda.addNext(timeAgendaWithoutDepDest);
			destAgendaWithoutDep.addNext(conDestAgenda);
			conDepAgenda.addNext(timeAgendaWithoutDep);
			timeAgendaWithoutDep.addNext(conTimeAgenda);
			timeAgendaWithoutDep.addNext(timeAgendaWithoutDep);

			depAgendaWithoutDest.addNext(depAgendaWithoutDest);
			conDestAgenda.addNext(depAgendaWithoutDest);
			depAgendaWithoutDest.addNext(conDepAgenda);
			conDestAgenda.addNext(timeAgendaWithoutDest);
			timeAgendaWithoutDest.addNext(conTimeAgenda);
			timeAgendaWithoutDest.addNext(timeAgendaWithoutDest);

			conDepAgenda.setPriority(0, 4000);
			conDestAgenda.setPriority(0, 3000);
			conTimeAgenda.setPriority(0, 2000);

			openAgenda.setPriority(0, 2000);

			depAgendaWithoutDest.setPriority(0, 1000);
			depAgendaWithoutTime.setPriority(0, 1000);

			destAgendaWithoutDep.setPriority(0, 800);
			destAgendaWithoutTime.setPriority(0, 800);

			timeAgendaWithoutDep.setPriority(0, 600);
			timeAgendaWithoutDest.setPriority(0, 600);

			destAgendaWithoutTimeDep.setPriority(0, 800);
			depAgendaWithoutTimeDest.setPriority(0, 1000);
			timeAgendaWithoutDepDest.setPriority(0, 600);

			exitAgenda.setPriority(0, 3000);

		}

		// Dialogue Strategy adaptive3

		if (dialogueStrategy == 4) {

			master.addNext(openAgenda);
			master.addNext(exitAgenda);
			master.addNext(depAgendaAll);
			master.addNext(depAgenda);
			master.addNext(timeAgendaAll);
			master.addNext(timeAgenda);
			master.addNext(destAgenda);
			master.addNext(destAgendaAll);
			master.addNext(conTimeAgenda);
			master.addNext(conDestAgenda);
			master.addNext(conDepAgenda);
			master.addNext(conBusAgenda);
			conBusAgenda.addNext(conBusAgenda);

			conTimeAgenda.addNext(timeAgendaWithoutBusDepDest);
			conTimeAgenda.addNext(timeAgendaWithoutBusDep);
			conTimeAgenda.addNext(timeAgendaWithoutBusDest);
			conTimeAgenda.addNext(timeAgendaWithoutBus);
			conTimeAgenda.addNext(timeAgendaWithoutDepDest);
			conTimeAgenda.addNext(timeAgendaWithoutDep);
			conTimeAgenda.addNext(timeAgendaWithoutDest);
			conTimeAgenda.addNext(timeAgenda);
			conTimeAgenda.addNext(depAgendaWithoutBusTimeDest);
			conTimeAgenda.addNext(depAgendaWithoutBusTime);
			conTimeAgenda.addNext(depAgendaWithoutTimeDest);
			conTimeAgenda.addNext(depAgendaWithoutTime);
			conTimeAgenda.addNext(destAgendaWithoutBusTimeDep);
			conTimeAgenda.addNext(destAgendaWithoutBusTime);
			conTimeAgenda.addNext(destAgendaWithoutTimeDep);
			conTimeAgenda.addNext(destAgendaWithoutTime);
			timeAgendaWithoutBusDepDest.addNext(conTimeAgenda);
			timeAgendaWithoutBusDep.addNext(conTimeAgenda);
			timeAgendaWithoutBusDest.addNext(conTimeAgenda);
			timeAgendaWithoutBus.addNext(conTimeAgenda);
			timeAgendaWithoutDepDest.addNext(conTimeAgenda);
			timeAgendaWithoutDep.addNext(conTimeAgenda);
			timeAgendaWithoutDest.addNext(conTimeAgenda);
			timeAgenda.addNext(conTimeAgenda);

			conDestAgenda.addNext(destAgendaWithoutBusTimeDep);
			conDestAgenda.addNext(destAgendaWithoutBusTime);
			conDestAgenda.addNext(destAgendaWithoutBusDep);
			conDestAgenda.addNext(destAgendaWithoutBus);
			conDestAgenda.addNext(destAgendaWithoutTimeDep);
			conDestAgenda.addNext(destAgendaWithoutTime);
			conDestAgenda.addNext(destAgendaWithoutDep);
			conDestAgenda.addNext(destAgenda);
			conDestAgenda.addNext(depAgendaWithoutBusTimeDest);
			conDestAgenda.addNext(depAgendaWithoutBusDest);
			conDestAgenda.addNext(depAgendaWithoutTimeDest);
			conDestAgenda.addNext(depAgendaWithoutDest);
			conDestAgenda.addNext(timeAgendaWithoutBusDepDest);
			conDestAgenda.addNext(timeAgendaWithoutBusDest);
			conDestAgenda.addNext(timeAgendaWithoutDepDest);
			conDestAgenda.addNext(timeAgendaWithoutDest);
			destAgendaWithoutBusTimeDep.addNext(conDestAgenda);
			destAgendaWithoutBusTime.addNext(conDestAgenda);
			destAgendaWithoutBusDep.addNext(conDestAgenda);
			destAgendaWithoutBus.addNext(conDestAgenda);
			destAgendaWithoutTimeDep.addNext(conDestAgenda);
			destAgendaWithoutTime.addNext(conDestAgenda);
			destAgendaWithoutDep.addNext(conDestAgenda);
			destAgenda.addNext(conDestAgenda);

			conDepAgenda.addNext(depAgendaWithoutBusTimeDest);
			conDepAgenda.addNext(depAgendaWithoutBusTime);
			conDepAgenda.addNext(depAgendaWithoutBusDest);
			conDepAgenda.addNext(depAgendaWithoutBus);
			conDepAgenda.addNext(depAgendaWithoutTimeDest);
			conDepAgenda.addNext(depAgendaWithoutTime);
			conDepAgenda.addNext(depAgendaWithoutDest);
			conDepAgenda.addNext(depAgenda);
			conDepAgenda.addNext(timeAgendaWithoutBusDepDest);
			conDepAgenda.addNext(timeAgendaWithoutBusDep);
			conDepAgenda.addNext(timeAgendaWithoutDepDest);
			conDepAgenda.addNext(timeAgendaWithoutDep);
			conDepAgenda.addNext(destAgendaWithoutBusTimeDep);
			conDepAgenda.addNext(destAgendaWithoutBusDep);
			conDepAgenda.addNext(destAgendaWithoutTimeDep);
			conDepAgenda.addNext(destAgendaWithoutDep);
			depAgendaWithoutBusTimeDest.addNext(conDepAgenda);
			depAgendaWithoutBusTime.addNext(conDepAgenda);
			depAgendaWithoutBusDest.addNext(conDepAgenda);
			depAgendaWithoutBus.addNext(conDepAgenda);
			depAgendaWithoutTimeDest.addNext(conDepAgenda);
			depAgendaWithoutTime.addNext(conDepAgenda);
			depAgendaWithoutDest.addNext(conDepAgenda);
			depAgenda.addNext(conDepAgenda);

			conBusAgenda.addNext(depAgendaWithoutBusTimeDest);
			conBusAgenda.addNext(depAgendaWithoutBusTime);
			conBusAgenda.addNext(depAgendaWithoutBusDest);
			conBusAgenda.addNext(depAgendaWithoutBus);
			conBusAgenda.addNext(destAgendaWithoutBusTimeDep);
			conBusAgenda.addNext(depAgendaWithoutBusTime);
			conBusAgenda.addNext(destAgendaWithoutBusDep);
			conBusAgenda.addNext(destAgendaWithoutBus);
			conBusAgenda.addNext(timeAgendaWithoutBusDepDest);
			conBusAgenda.addNext(timeAgendaWithoutBusDep);
			conBusAgenda.addNext(timeAgendaWithoutBusDest);
			conBusAgenda.addNext(timeAgendaWithoutBus);

			depAgendaAll.addNext(depAgendaAll);
			depAgendaAll.addNext(conTimeAgenda);
			depAgendaAll.addNext(conDepAgenda);
			depAgendaAll.addNext(conDestAgenda);

			destAgendaAll.addNext(destAgendaAll);
			destAgendaAll.addNext(conTimeAgenda);
			destAgendaAll.addNext(conDepAgenda);
			destAgendaAll.addNext(conDestAgenda);

			timeAgendaAll.addNext(timeAgendaAll);
			timeAgendaAll.addNext(conTimeAgenda);
			timeAgendaAll.addNext(conDepAgenda);
			timeAgendaAll.addNext(conDestAgenda);

			conTimeAgenda.addNext(timeAgendaAll);
			conTimeAgenda.addNext(depAgendaAll);
			conTimeAgenda.addNext(destAgendaAll);

			conDestAgenda.addNext(destAgendaAll);
			conDestAgenda.addNext(depAgendaAll);
			conDestAgenda.addNext(timeAgendaAll);

			conDepAgenda.addNext(depAgendaAll);
			conDepAgenda.addNext(destAgendaAll);
			conDepAgenda.addNext(timeAgendaAll);

			destAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");
			depAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");
			timeAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");

			destAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");

			destAgendaWithoutDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutTimeDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDepDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutTimeDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBus.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBusDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBusTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBusTimeDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBus.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBusDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBusTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBusTimeDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBus.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBusDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBusDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBusDepDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");

			openAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");

			conDepAgenda.setPriority(0, 4000);
			conDestAgenda.setPriority(0, 3000);
			conTimeAgenda.setPriority(0, 2000);
			conBusAgenda.setPriority(0, 1800);

			openAgenda.setPriority(0, 1500);
			depAgenda.setPriority(0, 1000);
			destAgenda.setPriority(0, 800);
			timeAgenda.setPriority(0, 600);

			depAgendaAll.setPriority(0, 1000);
			destAgendaAll.setPriority(0, 800);
			timeAgendaAll.setPriority(0, 600);

			depAgendaWithoutDest.setPriority(0, 1000);
			depAgendaWithoutTime.setPriority(0, 1000);
			depAgendaWithoutBus.setPriority(0, 1000);

			destAgendaWithoutDep.setPriority(0, 800);
			destAgendaWithoutTime.setPriority(0, 800);
			destAgendaWithoutBus.setPriority(0, 800);

			timeAgendaWithoutDep.setPriority(0, 600);
			timeAgendaWithoutDest.setPriority(0, 600);
			timeAgendaWithoutBus.setPriority(0, 600);

			destAgendaWithoutTimeDep.setPriority(0, 800);
			destAgendaWithoutBusDep.setPriority(0, 800);
			destAgendaWithoutBusTime.setPriority(0, 800);
			depAgendaWithoutTimeDest.setPriority(0, 1000);
			depAgendaWithoutBusDest.setPriority(0, 1000);
			depAgendaWithoutBusTime.setPriority(0, 1000);
			timeAgendaWithoutDepDest.setPriority(0, 600);
			timeAgendaWithoutBusDest.setPriority(0, 600);
			timeAgendaWithoutBusDep.setPriority(0, 600);

			exitAgenda.setPriority(0, 3000);

		}

		// Dialogue Strategy non-adaptive Mixed_initiative3

		if (dialogueStrategy == 5) {

			master.addNext(openAgenda);
			master.addNext(exitAgenda);
			master.addNext(reactToHangupAgenda);

			openAgenda.addNext(conTimeAgenda);
			conTimeAgenda.addNext(openAgenda);
			openAgenda.addNext(conDestAgenda);
			conDestAgenda.addNext(openAgenda);
			openAgenda.addNext(conDepAgenda);
			conDepAgenda.addNext(openAgenda);
			openAgenda.addNext(conBusAgenda);
			conBusAgenda.addNext(openAgenda);
			conBusAgenda.addNext(conBusAgenda);

			conTimeAgenda.addNext(timeAgendaWithoutBusDepDest);
			conTimeAgenda.addNext(timeAgendaWithoutBusDep);
			conTimeAgenda.addNext(timeAgendaWithoutBusDest);
			conTimeAgenda.addNext(timeAgendaWithoutBus);
			conTimeAgenda.addNext(timeAgendaWithoutDepDest);
			conTimeAgenda.addNext(timeAgendaWithoutDep);
			conTimeAgenda.addNext(timeAgendaWithoutDest);
			conTimeAgenda.addNext(timeAgenda);
			conTimeAgenda.addNext(depAgendaWithoutBusTimeDest);
			conTimeAgenda.addNext(depAgendaWithoutBusTime);
			conTimeAgenda.addNext(depAgendaWithoutTimeDest);
			conTimeAgenda.addNext(depAgendaWithoutTime);
			conTimeAgenda.addNext(destAgendaWithoutBusTimeDep);
			conTimeAgenda.addNext(destAgendaWithoutBusTime);
			conTimeAgenda.addNext(destAgendaWithoutTimeDep);
			conTimeAgenda.addNext(destAgendaWithoutTime);
			timeAgendaWithoutBusDepDest.addNext(conTimeAgenda);
			timeAgendaWithoutBusDep.addNext(conTimeAgenda);
			timeAgendaWithoutBusDest.addNext(conTimeAgenda);
			timeAgendaWithoutBus.addNext(conTimeAgenda);
			timeAgendaWithoutDepDest.addNext(conTimeAgenda);
			timeAgendaWithoutDep.addNext(conTimeAgenda);
			timeAgendaWithoutDest.addNext(conTimeAgenda);
			timeAgenda.addNext(conTimeAgenda);

			conDestAgenda.addNext(destAgendaWithoutBusTimeDep);
			conDestAgenda.addNext(destAgendaWithoutBusTime);
			conDestAgenda.addNext(destAgendaWithoutBusDep);
			conDestAgenda.addNext(destAgendaWithoutBus);
			conDestAgenda.addNext(destAgendaWithoutTimeDep);
			conDestAgenda.addNext(destAgendaWithoutTime);
			conDestAgenda.addNext(destAgendaWithoutDep);
			conDestAgenda.addNext(destAgenda);
			conDestAgenda.addNext(depAgendaWithoutBusTimeDest);
			conDestAgenda.addNext(depAgendaWithoutBusDest);
			conDestAgenda.addNext(depAgendaWithoutTimeDest);
			conDestAgenda.addNext(depAgendaWithoutDest);
			conDestAgenda.addNext(timeAgendaWithoutBusDepDest);
			conDestAgenda.addNext(timeAgendaWithoutBusDest);
			conDestAgenda.addNext(timeAgendaWithoutDepDest);
			conDestAgenda.addNext(timeAgendaWithoutDest);
			destAgendaWithoutBusTimeDep.addNext(conDestAgenda);
			destAgendaWithoutBusTime.addNext(conDestAgenda);
			destAgendaWithoutBusDep.addNext(conDestAgenda);
			destAgendaWithoutBus.addNext(conDestAgenda);
			destAgendaWithoutTimeDep.addNext(conDestAgenda);
			destAgendaWithoutTime.addNext(conDestAgenda);
			destAgendaWithoutDep.addNext(conDestAgenda);
			destAgenda.addNext(conDestAgenda);

			conDepAgenda.addNext(depAgendaWithoutBusTimeDest);
			conDepAgenda.addNext(depAgendaWithoutBusTime);
			conDepAgenda.addNext(depAgendaWithoutBusDest);
			conDepAgenda.addNext(depAgendaWithoutBus);
			conDepAgenda.addNext(depAgendaWithoutTimeDest);
			conDepAgenda.addNext(depAgendaWithoutTime);
			conDepAgenda.addNext(depAgendaWithoutDest);
			conDepAgenda.addNext(depAgenda);
			conDepAgenda.addNext(timeAgendaWithoutBusDepDest);
			conDepAgenda.addNext(timeAgendaWithoutBusDep);
			conDepAgenda.addNext(timeAgendaWithoutDepDest);
			conDepAgenda.addNext(timeAgendaWithoutDep);
			conDepAgenda.addNext(destAgendaWithoutBusTimeDep);
			conDepAgenda.addNext(destAgendaWithoutBusDep);
			conDepAgenda.addNext(destAgendaWithoutTimeDep);
			conDepAgenda.addNext(destAgendaWithoutDep);
			depAgendaWithoutBusTimeDest.addNext(conDepAgenda);
			depAgendaWithoutBusTime.addNext(conDepAgenda);
			depAgendaWithoutBusDest.addNext(conDepAgenda);
			depAgendaWithoutBus.addNext(conDepAgenda);
			depAgendaWithoutTimeDest.addNext(conDepAgenda);
			depAgendaWithoutTime.addNext(conDepAgenda);
			depAgendaWithoutDest.addNext(conDepAgenda);
			depAgenda.addNext(conDepAgenda);

			conBusAgenda.addNext(depAgendaWithoutBusTimeDest);
			conBusAgenda.addNext(depAgendaWithoutBusTime);
			conBusAgenda.addNext(depAgendaWithoutBusDest);
			conBusAgenda.addNext(depAgendaWithoutBus);
			conBusAgenda.addNext(destAgendaWithoutBusTimeDep);
			conBusAgenda.addNext(depAgendaWithoutBusTime);
			conBusAgenda.addNext(destAgendaWithoutBusDep);
			conBusAgenda.addNext(destAgendaWithoutBus);
			conBusAgenda.addNext(timeAgendaWithoutBusDepDest);
			conBusAgenda.addNext(timeAgendaWithoutBusDep);
			conBusAgenda.addNext(timeAgendaWithoutBusDest);
			conBusAgenda.addNext(timeAgendaWithoutBus);

			depAgendaAll.addNext(depAgendaAll);
			depAgendaAll.addNext(conTimeAgenda);
			depAgendaAll.addNext(conDepAgenda);
			depAgendaAll.addNext(conDestAgenda);

			destAgendaAll.addNext(destAgendaAll);
			destAgendaAll.addNext(conTimeAgenda);
			destAgendaAll.addNext(conDepAgenda);
			destAgendaAll.addNext(conDestAgenda);

			timeAgendaAll.addNext(timeAgendaAll);
			timeAgendaAll.addNext(conTimeAgenda);
			timeAgendaAll.addNext(conDepAgenda);
			timeAgendaAll.addNext(conDestAgenda);

			conTimeAgenda.addNext(timeAgendaAll);
			conTimeAgenda.addNext(depAgendaAll);
			conTimeAgenda.addNext(destAgendaAll);

			conDestAgenda.addNext(destAgendaAll);
			conDestAgenda.addNext(depAgendaAll);
			conDestAgenda.addNext(timeAgendaAll);

			conDepAgenda.addNext(depAgendaAll);
			conDepAgenda.addNext(destAgendaAll);
			conDepAgenda.addNext(timeAgendaAll);

			conDepAgenda.setPriority(0, 4000);
			conDestAgenda.setPriority(0, 3000);
			conTimeAgenda.setPriority(0, 2000);
			conBusAgenda.setPriority(0, 1800);

			openAgenda.setPriority(0, 1500);
			depAgenda.setPriority(0, 1000);
			destAgenda.setPriority(0, 800);
			timeAgenda.setPriority(0, 600);

			depAgendaAll.setPriority(0, 1000);
			destAgendaAll.setPriority(0, 800);
			timeAgendaAll.setPriority(0, 600);

			depAgendaWithoutDest.setPriority(0, 1000);
			depAgendaWithoutTime.setPriority(0, 1000);
			depAgendaWithoutBus.setPriority(0, 1000);

			destAgendaWithoutDep.setPriority(0, 800);
			destAgendaWithoutTime.setPriority(0, 800);
			destAgendaWithoutBus.setPriority(0, 800);

			timeAgendaWithoutDep.setPriority(0, 600);
			timeAgendaWithoutDest.setPriority(0, 600);
			timeAgendaWithoutBus.setPriority(0, 600);

			destAgendaWithoutTimeDep.setPriority(0, 800);
			destAgendaWithoutBusDep.setPriority(0, 800);
			destAgendaWithoutBusTime.setPriority(0, 800);
			depAgendaWithoutTimeDest.setPriority(0, 1000);
			depAgendaWithoutBusDest.setPriority(0, 1000);
			depAgendaWithoutBusTime.setPriority(0, 1000);
			timeAgendaWithoutDepDest.setPriority(0, 600);
			timeAgendaWithoutBusDest.setPriority(0, 600);
			timeAgendaWithoutBusDep.setPriority(0, 600);

			exitAgenda.setPriority(0, 3000);

		}

		// Dialogue Strategy non-adaptive user-init3

		if (dialogueStrategy == 6) {

			master.addNext(openAgenda);
			master.addNext(exitAgenda);
			master.addNext(conTimeAgenda);
			master.addNext(conDestAgenda);
			master.addNext(conDepAgenda);
			master.addNext(conBusAgenda);
			conBusAgenda.addNext(openAgenda);
			conTimeAgenda.addNext(openAgenda);
			conDestAgenda.addNext(openAgenda);
			conDepAgenda.addNext(openAgenda);
			conBusAgenda.addNext(conBusAgenda);
			conTimeAgenda.addNext(conTimeAgenda);
			conDepAgenda.addNext(conDepAgenda);
			conDestAgenda.addNext(conDestAgenda);

			conTimeAgenda.addNext(openAgenda_afterBusDepDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusDepCon);
			conTimeAgenda.addNext(openAgenda_afterBusDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusCon);
			conTimeAgenda.addNext(openAgenda_afterDepDestCon);
			conTimeAgenda.addNext(openAgenda_afterDepCon);
			conTimeAgenda.addNext(openAgenda_afterDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusTimeCon);
			conTimeAgenda.addNext(openAgenda_afterTimeDestCon);
			conTimeAgenda.addNext(openAgenda_afterTimeCon);
			conTimeAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conTimeAgenda.addNext(openAgenda_afterTimeDepCon);
			openAgenda_afterBusDepDestCon.addNext(conTimeAgenda);
			openAgenda_afterBusDepCon.addNext(conTimeAgenda);
			openAgenda_afterBusDestCon.addNext(conTimeAgenda);
			openAgenda_afterBusCon.addNext(conTimeAgenda);
			openAgenda_afterDepDestCon.addNext(conTimeAgenda);
			openAgenda_afterDepCon.addNext(conTimeAgenda);
			openAgenda_afterDestCon.addNext(conTimeAgenda);

			conDestAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conDestAgenda.addNext(openAgenda_afterBusTimeCon);
			conDestAgenda.addNext(openAgenda_afterBusDepCon);
			conDestAgenda.addNext(openAgenda_afterBusCon);
			conDestAgenda.addNext(openAgenda_afterTimeDepCon);
			conDestAgenda.addNext(openAgenda_afterTimeCon);
			conDestAgenda.addNext(openAgenda_afterDepCon);
			conDestAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conDestAgenda.addNext(openAgenda_afterBusDestCon);
			conDestAgenda.addNext(openAgenda_afterTimeDestCon);
			conDestAgenda.addNext(openAgenda_afterDestCon);
			conDestAgenda.addNext(openAgenda_afterBusDepDestCon);
			conDestAgenda.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterBusTimeDepCon.addNext(conDestAgenda);
			openAgenda_afterBusTimeCon.addNext(conDestAgenda);
			openAgenda_afterBusDepCon.addNext(conDestAgenda);
			openAgenda_afterBusCon.addNext(conDestAgenda);
			openAgenda_afterTimeDepCon.addNext(conDestAgenda);
			openAgenda_afterTimeCon.addNext(conDestAgenda);
			openAgenda_afterDepCon.addNext(conDestAgenda);

			conDepAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conDepAgenda.addNext(openAgenda_afterBusTimeCon);
			conDepAgenda.addNext(openAgenda_afterBusDestCon);
			conDepAgenda.addNext(openAgenda_afterBusCon);
			conDepAgenda.addNext(openAgenda_afterTimeDestCon);
			conDepAgenda.addNext(openAgenda_afterTimeCon);
			conDepAgenda.addNext(openAgenda_afterDestCon);
			conDepAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conDepAgenda.addNext(openAgenda_afterBusDepCon);
			conDepAgenda.addNext(openAgenda_afterTimeDepCon);
			conDepAgenda.addNext(openAgenda_afterDepCon);
			conDepAgenda.addNext(openAgenda_afterBusDepDestCon);
			conDepAgenda.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterBusTimeDestCon.addNext(conDepAgenda);
			openAgenda_afterBusTimeCon.addNext(conDepAgenda);
			openAgenda_afterBusDestCon.addNext(conDepAgenda);
			openAgenda_afterBusCon.addNext(conDepAgenda);
			openAgenda_afterTimeDestCon.addNext(conDepAgenda);
			openAgenda_afterTimeCon.addNext(conDepAgenda);
			openAgenda_afterDestCon.addNext(conDepAgenda);

			conBusAgenda.addNext(openAgenda_afterTimeCon);
			conBusAgenda.addNext(openAgenda_afterDestCon);
			conBusAgenda.addNext(openAgenda_afterDepCon);
			conBusAgenda.addNext(openAgenda_afterTimeDepCon);
			conBusAgenda.addNext(openAgenda_afterDepCon);
			conBusAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conBusAgenda.addNext(openAgenda_afterBusDestCon);
			conBusAgenda.addNext(openAgenda_afterTimeDestCon);
			conBusAgenda.addNext(openAgenda_afterBusDepDestCon);
			conBusAgenda.addNext(openAgenda_afterBusTimeCon);
			conBusAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conBusAgenda.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterTimeCon.addNext(conBusAgenda);
			openAgenda_afterDestCon.addNext(conBusAgenda);
			openAgenda_afterDepCon.addNext(conBusAgenda);
			openAgenda_afterDepDestCon.addNext(conBusAgenda);
			openAgenda_afterTimeDestCon.addNext(conBusAgenda);
			openAgenda_afterTimeDepCon.addNext(conBusAgenda);

			conDepAgenda.setPriority(0, 10000);
			conTimeAgenda.setPriority(0, 6000);
			conDestAgenda.setPriority(0, 8000);
			conBusAgenda.setPriority(0, 4000);
			openAgenda.setPriority(0, 100);
			exitAgenda.setPriority(0, 1000);

		}

		// Dialogue Strategy non-adaptive System_initiative3
		if (dialogueStrategy == 7) {

			master.addNext(openAgenda);
			master.addNext(conBusAgenda);
			openAgenda.addNext(depAgenda);
			openAgenda.addNext(destAgenda);
			openAgenda.addNext(timeAgenda);
			openAgenda.addNext(conTimeAgenda);
			openAgenda.addNext(conDepAgenda);
			openAgenda.addNext(conDestAgenda);
			master.addNext(exitAgenda);
			conBusAgenda.addNext(conBusAgenda);

			depAgenda.addNext(conDepAgenda);
			conDepAgenda.addNext(depAgenda);

			destAgenda.addNext(conDestAgenda);
			conDestAgenda.addNext(destAgenda);

			timeAgenda.addNext(conTimeAgenda);
			conTimeAgenda.addNext(timeAgenda);

			openAgenda.setPriority(0, 2000);
			depAgenda.setPriority(0, 1000);
			destAgenda.setPriority(0, 800);
			timeAgenda.setPriority(0, 600);
			conDepAgenda.setPriority(0, 6000);
			conDestAgenda.setPriority(0, 5000);
			conTimeAgenda.setPriority(0, 4000);
			conBusAgenda.setPriority(0, 3000);
			exitAgenda.setPriority(0, 200);

		}

		// Dialogue strategy adaptive4

		if (dialogueStrategy == 8) {

			master.addNext(openAgenda);
			master.addNext(exitAgenda);
			master.addNext(depAgendaAll);
			master.addNext(depAgenda);
			master.addNext(timeAgendaAll);
			master.addNext(timeAgenda);
			master.addNext(destAgenda);
			master.addNext(destAgendaAll);
			master.addNext(conTimeAgenda);
			master.addNext(conDestAgenda);
			master.addNext(conDepAgenda);
			master.addNext(conBusAgenda);
			conBusAgenda.addNext(openAgenda);
			conTimeAgenda.addNext(openAgenda);
			conDestAgenda.addNext(openAgenda);
			conDepAgenda.addNext(openAgenda);
			conBusAgenda.addNext(conBusAgenda);
			conTimeAgenda.addNext(conTimeAgenda);
			conDepAgenda.addNext(conDepAgenda);
			conDestAgenda.addNext(conDestAgenda);

			conTimeAgenda.addNext(openAgenda_afterBusDepDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusDepCon);
			conTimeAgenda.addNext(openAgenda_afterBusDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusCon);
			conTimeAgenda.addNext(openAgenda_afterDepDestCon);
			conTimeAgenda.addNext(openAgenda_afterDepCon);
			conTimeAgenda.addNext(openAgenda_afterDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conTimeAgenda.addNext(openAgenda_afterBusTimeCon);
			conTimeAgenda.addNext(openAgenda_afterTimeDestCon);
			conTimeAgenda.addNext(openAgenda_afterTimeCon);
			conTimeAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conTimeAgenda.addNext(openAgenda_afterTimeDepCon);
			openAgenda_afterBusDepDestCon.addNext(conTimeAgenda);
			openAgenda_afterBusDepCon.addNext(conTimeAgenda);
			openAgenda_afterBusDestCon.addNext(conTimeAgenda);
			openAgenda_afterBusCon.addNext(conTimeAgenda);
			openAgenda_afterDepDestCon.addNext(conTimeAgenda);
			openAgenda_afterDepCon.addNext(conTimeAgenda);
			openAgenda_afterDestCon.addNext(conTimeAgenda);

			conDestAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conDestAgenda.addNext(openAgenda_afterBusTimeCon);
			conDestAgenda.addNext(openAgenda_afterBusDepCon);
			conDestAgenda.addNext(openAgenda_afterBusCon);
			conDestAgenda.addNext(openAgenda_afterTimeDepCon);
			conDestAgenda.addNext(openAgenda_afterTimeCon);
			conDestAgenda.addNext(openAgenda_afterDepCon);
			conDestAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conDestAgenda.addNext(openAgenda_afterBusDestCon);
			conDestAgenda.addNext(openAgenda_afterTimeDestCon);
			conDestAgenda.addNext(openAgenda_afterDestCon);
			conDestAgenda.addNext(openAgenda_afterBusDepDestCon);
			conDestAgenda.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterBusTimeDepCon.addNext(conDestAgenda);
			openAgenda_afterBusTimeCon.addNext(conDestAgenda);
			openAgenda_afterBusDepCon.addNext(conDestAgenda);
			openAgenda_afterBusCon.addNext(conDestAgenda);
			openAgenda_afterTimeDepCon.addNext(conDestAgenda);
			openAgenda_afterTimeCon.addNext(conDestAgenda);
			openAgenda_afterDepCon.addNext(conDestAgenda);

			conDepAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conDepAgenda.addNext(openAgenda_afterBusTimeCon);
			conDepAgenda.addNext(openAgenda_afterBusDestCon);
			conDepAgenda.addNext(openAgenda_afterBusCon);
			conDepAgenda.addNext(openAgenda_afterTimeDestCon);
			conDepAgenda.addNext(openAgenda_afterTimeCon);
			conDepAgenda.addNext(openAgenda_afterDestCon);
			conDepAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conDepAgenda.addNext(openAgenda_afterBusDepCon);
			conDepAgenda.addNext(openAgenda_afterTimeDepCon);
			conDepAgenda.addNext(openAgenda_afterDepCon);
			conDepAgenda.addNext(openAgenda_afterBusDepDestCon);
			conDepAgenda.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterBusTimeDestCon.addNext(conDepAgenda);
			openAgenda_afterBusTimeCon.addNext(conDepAgenda);
			openAgenda_afterBusDestCon.addNext(conDepAgenda);
			openAgenda_afterBusCon.addNext(conDepAgenda);
			openAgenda_afterTimeDestCon.addNext(conDepAgenda);
			openAgenda_afterTimeCon.addNext(conDepAgenda);
			openAgenda_afterDestCon.addNext(conDepAgenda);

			conBusAgenda.addNext(openAgenda_afterTimeCon);
			conBusAgenda.addNext(openAgenda_afterDestCon);
			conBusAgenda.addNext(openAgenda_afterDepCon);
			conBusAgenda.addNext(openAgenda_afterTimeDepCon);
			conBusAgenda.addNext(openAgenda_afterDepCon);
			conBusAgenda.addNext(openAgenda_afterBusTimeDestCon);
			conBusAgenda.addNext(openAgenda_afterBusDestCon);
			conBusAgenda.addNext(openAgenda_afterTimeDestCon);
			conBusAgenda.addNext(openAgenda_afterBusDepDestCon);
			conBusAgenda.addNext(openAgenda_afterBusTimeCon);
			conBusAgenda.addNext(openAgenda_afterBusTimeDepCon);
			conBusAgenda.addNext(openAgenda_afterDepDestCon);
			openAgenda_afterTimeCon.addNext(conBusAgenda);
			openAgenda_afterDestCon.addNext(conBusAgenda);
			openAgenda_afterDepCon.addNext(conBusAgenda);
			openAgenda_afterDepDestCon.addNext(conBusAgenda);
			openAgenda_afterTimeDestCon.addNext(conBusAgenda);
			openAgenda_afterTimeDepCon.addNext(conBusAgenda);

			conTimeAgenda.addNext(timeAgendaWithoutBusDepDest);
			conTimeAgenda.addNext(timeAgendaWithoutBusDep);
			conTimeAgenda.addNext(timeAgendaWithoutBusDest);
			conTimeAgenda.addNext(timeAgendaWithoutBus);
			conTimeAgenda.addNext(timeAgendaWithoutDepDest);
			conTimeAgenda.addNext(timeAgendaWithoutDep);
			conTimeAgenda.addNext(timeAgendaWithoutDest);
			conTimeAgenda.addNext(timeAgenda);
			conTimeAgenda.addNext(depAgendaWithoutBusTimeDest);
			conTimeAgenda.addNext(depAgendaWithoutBusTime);
			conTimeAgenda.addNext(depAgendaWithoutTimeDest);
			conTimeAgenda.addNext(depAgendaWithoutTime);
			conTimeAgenda.addNext(destAgendaWithoutBusTimeDep);
			conTimeAgenda.addNext(destAgendaWithoutBusTime);
			conTimeAgenda.addNext(destAgendaWithoutTimeDep);
			conTimeAgenda.addNext(destAgendaWithoutTime);
			timeAgendaWithoutBusDepDest.addNext(conTimeAgenda);
			timeAgendaWithoutBusDep.addNext(conTimeAgenda);
			timeAgendaWithoutBusDest.addNext(conTimeAgenda);
			timeAgendaWithoutBus.addNext(conTimeAgenda);
			timeAgendaWithoutDepDest.addNext(conTimeAgenda);
			timeAgendaWithoutDep.addNext(conTimeAgenda);
			timeAgendaWithoutDest.addNext(conTimeAgenda);
			timeAgenda.addNext(conTimeAgenda);

			conDestAgenda.addNext(destAgendaWithoutBusTimeDep);
			conDestAgenda.addNext(destAgendaWithoutBusTime);
			conDestAgenda.addNext(destAgendaWithoutBusDep);
			conDestAgenda.addNext(destAgendaWithoutBus);
			conDestAgenda.addNext(destAgendaWithoutTimeDep);
			conDestAgenda.addNext(destAgendaWithoutTime);
			conDestAgenda.addNext(destAgendaWithoutDep);
			conDestAgenda.addNext(destAgenda);
			conDestAgenda.addNext(depAgendaWithoutBusTimeDest);
			conDestAgenda.addNext(depAgendaWithoutBusDest);
			conDestAgenda.addNext(depAgendaWithoutTimeDest);
			conDestAgenda.addNext(depAgendaWithoutDest);
			conDestAgenda.addNext(timeAgendaWithoutBusDepDest);
			conDestAgenda.addNext(timeAgendaWithoutBusDest);
			conDestAgenda.addNext(timeAgendaWithoutDepDest);
			conDestAgenda.addNext(timeAgendaWithoutDest);
			destAgendaWithoutBusTimeDep.addNext(conDestAgenda);
			destAgendaWithoutBusTime.addNext(conDestAgenda);
			destAgendaWithoutBusDep.addNext(conDestAgenda);
			destAgendaWithoutBus.addNext(conDestAgenda);
			destAgendaWithoutTimeDep.addNext(conDestAgenda);
			destAgendaWithoutTime.addNext(conDestAgenda);
			destAgendaWithoutDep.addNext(conDestAgenda);
			destAgenda.addNext(conDestAgenda);

			conDepAgenda.addNext(depAgendaWithoutBusTimeDest);
			conDepAgenda.addNext(depAgendaWithoutBusTime);
			conDepAgenda.addNext(depAgendaWithoutBusDest);
			conDepAgenda.addNext(depAgendaWithoutBus);
			conDepAgenda.addNext(depAgendaWithoutTimeDest);
			conDepAgenda.addNext(depAgendaWithoutTime);
			conDepAgenda.addNext(depAgendaWithoutDest);
			conDepAgenda.addNext(depAgenda);
			conDepAgenda.addNext(timeAgendaWithoutBusDepDest);
			conDepAgenda.addNext(timeAgendaWithoutBusDep);
			conDepAgenda.addNext(timeAgendaWithoutDepDest);
			conDepAgenda.addNext(timeAgendaWithoutDep);
			conDepAgenda.addNext(destAgendaWithoutBusTimeDep);
			conDepAgenda.addNext(destAgendaWithoutBusDep);
			conDepAgenda.addNext(destAgendaWithoutTimeDep);
			conDepAgenda.addNext(destAgendaWithoutDep);
			depAgendaWithoutBusTimeDest.addNext(conDepAgenda);
			depAgendaWithoutBusTime.addNext(conDepAgenda);
			depAgendaWithoutBusDest.addNext(conDepAgenda);
			depAgendaWithoutBus.addNext(conDepAgenda);
			depAgendaWithoutTimeDest.addNext(conDepAgenda);
			depAgendaWithoutTime.addNext(conDepAgenda);
			depAgendaWithoutDest.addNext(conDepAgenda);
			depAgenda.addNext(conDepAgenda);

			conBusAgenda.addNext(depAgendaWithoutBusTimeDest);
			conBusAgenda.addNext(depAgendaWithoutBusTime);
			conBusAgenda.addNext(depAgendaWithoutBusDest);
			conBusAgenda.addNext(depAgendaWithoutBus);
			conBusAgenda.addNext(destAgendaWithoutBusTimeDep);
			conBusAgenda.addNext(depAgendaWithoutBusTime);
			conBusAgenda.addNext(destAgendaWithoutBusDep);
			conBusAgenda.addNext(destAgendaWithoutBus);
			conBusAgenda.addNext(timeAgendaWithoutBusDepDest);
			conBusAgenda.addNext(timeAgendaWithoutBusDep);
			conBusAgenda.addNext(timeAgendaWithoutBusDest);
			conBusAgenda.addNext(timeAgendaWithoutBus);

			depAgendaAll.addNext(depAgendaAll);
			depAgendaAll.addNext(conTimeAgenda);
			depAgendaAll.addNext(conDepAgenda);
			depAgendaAll.addNext(conDestAgenda);

			destAgendaAll.addNext(destAgendaAll);
			destAgendaAll.addNext(conTimeAgenda);
			destAgendaAll.addNext(conDepAgenda);
			destAgendaAll.addNext(conDestAgenda);

			timeAgendaAll.addNext(timeAgendaAll);
			timeAgendaAll.addNext(conTimeAgenda);
			timeAgendaAll.addNext(conDepAgenda);
			timeAgendaAll.addNext(conDestAgenda);

			conTimeAgenda.addNext(timeAgendaAll);
			conTimeAgenda.addNext(depAgendaAll);
			conTimeAgenda.addNext(destAgendaAll);

			conDestAgenda.addNext(destAgendaAll);
			conDestAgenda.addNext(depAgendaAll);
			conDestAgenda.addNext(timeAgendaAll);

			conDepAgenda.addNext(depAgendaAll);
			conDepAgenda.addNext(destAgendaAll);
			conDepAgenda.addNext(timeAgendaAll);

			destAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");
			depAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");
			timeAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%<<3)");

			destAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaAll.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");

			destAgendaWithoutDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutTimeDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutDepDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutTimeDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBus.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBusDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBusTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			destAgendaWithoutBusTimeDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBus.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBusDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBusTime.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			depAgendaWithoutBusTimeDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBus.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBusDep.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBusDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");
			timeAgendaWithoutBusDepDest.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>2)");

			openAgenda.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterDepCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterDestCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterTimeCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterTimeDepCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterTimeDestCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusTimeCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterDepDestCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusDestCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusDepCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusDepDestCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusTimeDepCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");
			openAgenda_afterBusTimeDestCon.setVariableOperator("",
					"REQUIRES(%InteractionQuality%>>4)");

			conDepAgenda.setPriority(0, 4000);
			conDestAgenda.setPriority(0, 3000);
			conTimeAgenda.setPriority(0, 2000);
			conBusAgenda.setPriority(0, 1800);

			openAgenda.setPriority(0, 1500);
			depAgenda.setPriority(0, 1000);
			destAgenda.setPriority(0, 800);
			timeAgenda.setPriority(0, 600);

			depAgendaAll.setPriority(0, 1000);
			destAgendaAll.setPriority(0, 800);
			timeAgendaAll.setPriority(0, 600);

			depAgendaWithoutDest.setPriority(0, 1000);
			depAgendaWithoutTime.setPriority(0, 1000);
			depAgendaWithoutBus.setPriority(0, 1000);

			destAgendaWithoutDep.setPriority(0, 800);
			destAgendaWithoutTime.setPriority(0, 800);
			destAgendaWithoutBus.setPriority(0, 800);

			timeAgendaWithoutDep.setPriority(0, 600);
			timeAgendaWithoutDest.setPriority(0, 600);
			timeAgendaWithoutBus.setPriority(0, 600);

			destAgendaWithoutTimeDep.setPriority(0, 800);
			destAgendaWithoutBusDep.setPriority(0, 800);
			destAgendaWithoutBusTime.setPriority(0, 800);
			depAgendaWithoutTimeDest.setPriority(0, 1000);
			depAgendaWithoutBusDest.setPriority(0, 1000);
			depAgendaWithoutBusTime.setPriority(0, 1000);
			timeAgendaWithoutDepDest.setPriority(0, 600);
			timeAgendaWithoutBusDest.setPriority(0, 600);
			timeAgendaWithoutBusDep.setPriority(0, 600);

			exitAgenda.setPriority(0, 3000);

		}

		Utterance dummyUtterance = factory.createUtterance("dummyUtterance");
		Utterance destinationUtterance = factory
				.createUtterance("destinationUtterance");
		Utterance departure_place_Utterance = factory
				.createUtterance("departure_place_Utterance");
		Utterance timeUtterance = factory.createUtterance("timeUtterance");
		Utterance bus_number_Utterance = factory
				.createUtterance("bus_number_Utterance");
		Utterance infoUtterance = factory.createUtterance("infoUtterance");

		OWLLiteral l = factory.factory.getOWLLiteral("%" + destVar + "%", "en");
		OWLLiteral m = factory.factory.getOWLLiteral("%" + depVar + "%", "en");
		OWLLiteral n = factory.factory.getOWLLiteral("%" + timeVar + "%", "en");
		OWLLiteral o = factory.factory.getOWLLiteral("%" + busVar + "%", "en");

		destinationUtterance.addUtteranceString(l);
		departure_place_Utterance.addUtteranceString(m);
		timeUtterance.addUtteranceString(n);
		bus_number_Utterance.addUtteranceString(o);
		infoUtterance.addUtteranceString(l);
		infoUtterance.addUtteranceString(m);
		infoUtterance.addUtteranceString(n);
		infoUtterance.addUtteranceString(o);

		// Generate Grammar

		Grammar g1 = factory.createGrammar("dummygrammar");

		// Generate system moves
		Move m1 = factory.createMove("sysmov_Request_Bus_number");
		busAgenda.addHas(m1); // Adding the move to the corresponding Agenda
		m1.setUtterance(null, dummyUtterance);
		Move m2 = factory.createMove("sysmov_Confirm_Bus_number");
		conBusAgenda.addHas(m2);
		m2.setUtterance(null, bus_number_Utterance);
		Move m3 = factory.createMove("sysmov_Confirm_Departure_place");
		conDepAgenda.addHas(m3);
		m3.setUtterance(null, departure_place_Utterance);
		Move m4 = factory.createMove("sysmov_Confirm_Arrival_place");
		conDestAgenda.addHas(m4);
		m4.setUtterance(null, destinationUtterance);
		Move m5 = factory.createMove("sysmov_Confirm_Travel_time");
		conTimeAgenda.addHas(m5);
		m5.setUtterance(null, timeUtterance);
		Move m6 = factory.createMove("sysmov_Request_Open");
		openAgenda.addHas(m6);
		openAgenda_afterDepCon.addHas(m6);
		openAgenda_afterDestCon.addHas(m6);
		openAgenda_afterTimeCon.addHas(m6);
		openAgenda_afterDepDestCon.addHas(m6);
		openAgenda_afterTimeDepCon.addHas(m6);
		openAgenda_afterTimeDestCon.addHas(m6);
		openAgenda_afterBusCon.addHas(m6);
		openAgenda_afterBusDepCon.addHas(m6);
		openAgenda_afterBusDepDestCon.addHas(m6);
		openAgenda_afterBusDestCon.addHas(m6);
		openAgenda_afterBusTimeCon.addHas(m6);
		openAgenda_afterBusTimeDepCon.addHas(m6);
		openAgenda_afterBusTimeDestCon.addHas(m6);
		m6.setUtterance(null, dummyUtterance);
		Move m7 = factory.createMove("sysmov_Inform_Bus_information");
		infoAgenda.addHas(m7);

		Move m8 = factory.createMove("sysmov_Request_Departure_place");
		depAgenda.addHas(m8);
		depAgendaWithoutDest.addHas(m8);
		depAgendaWithoutTime.addHas(m8);
		depAgendaAll.addHas(m8);
		depAgendaWithoutTimeDest.addHas(m8);
		depAgendaWithoutBus.addHas(m8);
		depAgendaWithoutBusDest.addHas(m8);
		depAgendaWithoutBusTime.addHas(m8);
		depAgendaWithoutBusTimeDest.addHas(m8);
		m8.setUtterance(null, dummyUtterance);
		Move m9 = factory.createMove("sysmov_Request_Travel_time");
		timeAgenda.addHas(m9);
		timeAgendaWithoutDep.addHas(m9);
		timeAgendaWithoutDest.addHas(m9);
		timeAgendaAll.addHas(m9);
		timeAgendaWithoutDepDest.addHas(m9);
		timeAgendaWithoutBus.addHas(m9);
		timeAgendaWithoutBusDep.addHas(m9);
		timeAgendaWithoutBusDepDest.addHas(m9);
		timeAgendaWithoutBusDest.addHas(m9);
		m9.setUtterance(null, dummyUtterance);
		Move m10 = factory.createMove("sysmov_Request_Arrival_place");
		destAgenda.addHas(m10);
		destAgendaWithoutDep.addHas(m10);
		destAgendaWithoutTime.addHas(m10);
		destAgendaAll.addHas(m10);
		destAgendaWithoutTimeDep.addHas(m10);
		destAgendaWithoutBus.addHas(m10);
		destAgendaWithoutBusDep.addHas(m10);
		destAgendaWithoutBusTime.addHas(m10);
		destAgendaWithoutBusTimeDep.addHas(m10);
		m10.setUtterance(null, dummyUtterance);
		Move m11 = factory.createMove("sysmov_Not_covered");
		notCovAgenda.addHas(m11);
		m11.setUtterance(null, dummyUtterance);
		Move m12 = factory.createMove("sysmov_Exit_Move");
		exitAgenda.addHas(m12);
		reactToHangupAgenda.addHas(m12);
		m12.setUtterance(null, infoUtterance);
		Move m13 = factory.createMove("external_destination_mv");
		m13.setVariableOperator("SET(" + destVar
				+ "=:external_destination_mv:)");
		m13.addSemantic(destSemG);
		destAgenda.addHas(m13);
		destAgendaWithoutDep.addHas(m13);
		destAgendaWithoutTime.addHas(m13);
		destAgendaWithoutBus.addHas(m13);
		destAgendaWithoutBusDep.addHas(m13);
		destAgendaWithoutBusTime.addHas(m13);
		destAgendaWithoutBusTimeDep.addHas(m13);
		depAgendaWithoutTime.addHas(m13);
		depAgendaWithoutBus.addHas(m13);
		depAgendaWithoutBusTime.addHas(m13);
		timeAgendaWithoutDep.addHas(m13);
		timeAgendaWithoutBus.addHas(m13);
		timeAgendaWithoutBusDep.addHas(m13);
		destAgendaWithoutTimeDep.addHas(m13);

		depAgendaAll.addHas(m13);
		destAgendaAll.addHas(m13);
		timeAgendaAll.addHas(m13);
		openAgenda.addHas(m13);
		openAgenda_afterDepCon.addHas(m13);
		openAgenda_afterTimeCon.addHas(m13);
		openAgenda_afterTimeDepCon.addHas(m13);
		openAgenda_afterBusCon.addHas(m13);
		openAgenda_afterBusDepCon.addHas(m13);
		openAgenda_afterBusTimeCon.addHas(m13);
		openAgenda_afterBusTimeDepCon.addHas(m13);

		m13.setGrammar(null, g1);
		m13.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		// Create the user departure Move, set the variable and connect it
		// to the semantic and the Agendas
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
		depAgendaWithoutBusTimeDest.addHas(m14);
		destAgendaWithoutTime.addHas(m14);
		destAgendaWithoutBus.addHas(m14);
		destAgendaWithoutBusTime.addHas(m14);
		timeAgendaWithoutDest.addHas(m14);
		timeAgendaWithoutBus.addHas(m14);
		timeAgendaWithoutBusDest.addHas(m14);
		depAgendaWithoutTimeDest.addHas(m14);
		depAgendaAll.addHas(m14);
		destAgendaAll.addHas(m14);
		timeAgendaAll.addHas(m14);
		openAgenda.addHas(m14);
		openAgenda_afterDestCon.addHas(m14);
		openAgenda_afterTimeCon.addHas(m14);
		openAgenda_afterTimeDestCon.addHas(m14);
		openAgenda_afterBusCon.addHas(m14);
		openAgenda_afterBusDestCon.addHas(m14);
		openAgenda_afterBusTimeCon.addHas(m14);
		openAgenda_afterBusTimeDestCon.addHas(m14);

		// Create the user bus route Move, set the variable and connect it
		// to the semantic and the Agendas
		Move m15 = factory.createMove("external_busnumber_mv");
		m15.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:)");
		m15.addSemantic(busSemG);
		busAgenda.addHas(m15);
		depAgendaWithoutDest.addHas(m15);
		depAgendaWithoutTime.addHas(m15);
		destAgendaWithoutDep.addHas(m15);
		destAgendaWithoutTime.addHas(m15);
		timeAgendaWithoutDep.addHas(m15);
		timeAgendaWithoutDest.addHas(m15);
		depAgendaWithoutTimeDest.addHas(m15);
		destAgendaWithoutTimeDep.addHas(m15);
		timeAgendaWithoutDepDest.addHas(m15);

		depAgendaAll.addHas(m15);
		destAgendaAll.addHas(m15);
		timeAgendaAll.addHas(m15);

		openAgenda.addHas(m15);
		openAgenda_afterTimeCon.addHas(m15);
		openAgenda_afterTimeDestCon.addHas(m15);
		openAgenda_afterDestCon.addHas(m15);
		openAgenda_afterDepCon.addHas(m15);
		openAgenda_afterDepDestCon.addHas(m15);
		openAgenda_afterTimeDepCon.addHas(m15);
		m15.setGrammar(null, g1);
		m15.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m16 = factory.createMove("external_time_mv");
		m16.setVariableOperator("SET(" + timeVar + "=:external_time_mv:)");
		m16.addSemantic(timeSemG);
		timeAgenda.addHas(m16);
		timeAgendaWithoutDep.addHas(m16);
		timeAgendaWithoutDest.addHas(m16);
		timeAgendaWithoutBus.addHas(m16);
		timeAgendaWithoutBusDep.addHas(m16);
		timeAgendaWithoutBusDest.addHas(m16);
		timeAgendaWithoutBusDepDest.addHas(m16);
		depAgendaWithoutDest.addHas(m16);
		depAgendaWithoutBus.addHas(m16);
		depAgendaWithoutBusDest.addHas(m16);
		destAgendaWithoutDep.addHas(m16);
		destAgendaWithoutBus.addHas(m16);
		destAgendaWithoutBusDep.addHas(m16);
		timeAgendaWithoutDepDest.addHas(m16);
		depAgendaAll.addHas(m16);
		destAgendaAll.addHas(m16);
		timeAgendaAll.addHas(m16);

		openAgenda.addHas(m16);
		openAgenda_afterDestCon.addHas(m16);
		openAgenda_afterDepCon.addHas(m16);
		openAgenda_afterDepDestCon.addHas(m16);
		openAgenda_afterBusCon.addHas(m16);
		openAgenda_afterBusDepCon.addHas(m16);
		openAgenda_afterBusDepDestCon.addHas(m16);
		openAgenda_afterBusDestCon.addHas(m16);

		m16.setGrammar(null, g1);
		m16.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		// Create multislot move

		Move m17 = factory.createMove("multi_slot4_mv");
		m17.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ depVar + "=:external_depplace_mv:;" + destVar
				+ "=:external_destination_mv:;" + timeVar
				+ "=:external_time_mv:)");
		m17.addSemantic(timeSemG);
		m17.addSemantic(destSemG);
		m17.addSemantic(depSemG);
		m17.addSemantic(busSemG);
		openAgenda.addHas(m17);

		depAgendaAll.addHas(m17);
		destAgendaAll.addHas(m17);
		timeAgendaAll.addHas(m17);
		m17.setGrammar(null, g1);
		m17.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m18 = factory.createMove("multi_slot3a_mv");
		m18.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ depVar + "=:external_depplace_mv:;" + destVar
				+ "=:external_destination_mv:)");

		m18.addSemantic(destSemG);
		m18.addSemantic(depSemG);
		m18.addSemantic(busSemG);
		depAgendaWithoutTime.addHas(m18);
		destAgendaWithoutTime.addHas(m18);

		openAgenda.addHas(m18);
		openAgenda_afterTimeCon.addHas(m18);
		m18.setGrammar(null, g1);
		m18.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m19 = factory.createMove("multi_slot3b_mv");
		m19.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ destVar + "=:external_destination_mv:;" + timeVar
				+ "=:external_time_mv:)");

		m19.addSemantic(destSemG);
		m19.addSemantic(depSemG);
		m19.addSemantic(timeSemG);
		openAgenda.addHas(m19);
		openAgenda_afterBusCon.addHas(m19);
		depAgendaAll.addHas(m19);
		destAgendaAll.addHas(m19);
		timeAgendaAll.addHas(m19);
		depAgendaWithoutBus.addHas(m19);
		destAgendaWithoutBus.addHas(m19);
		timeAgendaWithoutBus.addHas(m19);

		m19.setGrammar(null, g1);
		m19.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m20 = factory.createMove("multi_slot3c_mv");
		m20.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ busVar + "=:external_busnumber_mv:;" + timeVar
				+ "=:external_time_mv:)");

		m20.addSemantic(busSemG);
		m20.addSemantic(depSemG);
		m20.addSemantic(timeSemG);
		depAgendaAll.addHas(m20);
		destAgendaAll.addHas(m20);
		timeAgendaAll.addHas(m20);
		timeAgendaWithoutDest.addHas(m20);
		depAgendaWithoutDest.addHas(m20);
		openAgenda.addHas(m20);
		openAgenda_afterDestCon.addHas(m20);
		m20.setGrammar(null, g1);
		m20.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m21 = factory.createMove("multi_slot3d_mv");
		m21.setVariableOperator("SET(" + destVar
				+ "=:external_destination_mv:;" + busVar
				+ "=:external_busnumber_mv:;" + timeVar
				+ "=:external_time_mv:)");

		m21.addSemantic(busSemG);
		m21.addSemantic(destSemG);
		m21.addSemantic(timeSemG);
		depAgendaAll.addHas(m21);
		destAgendaAll.addHas(m21);
		timeAgendaAll.addHas(m21);
		destAgendaWithoutDep.addHas(m21);
		timeAgendaWithoutDep.addHas(m21);
		openAgenda.addHas(m21);
		openAgenda_afterDepCon.addHas(m21);
		m21.setGrammar(null, g1);
		m21.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m22 = factory.createMove("multi_slot2a_mv");
		m22.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ depVar + "=:external_depplace_mv:)");

		m22.addSemantic(depSemG);
		m22.addSemantic(busSemG);
		timeAgendaWithoutDest.addHas(m22);
		destAgendaWithoutTime.addHas(m22);
		openAgenda.addHas(m22);
		openAgenda_afterDestCon.addHas(m22);
		openAgenda_afterTimeCon.addHas(m22);
		openAgenda_afterTimeDestCon.addHas(m22);
		depAgendaAll.addHas(m22);
		destAgendaAll.addHas(m22);
		timeAgendaAll.addHas(m22);
		depAgendaWithoutTimeDest.addHas(m22);
		depAgendaWithoutDest.addHas(m22);
		depAgendaWithoutTime.addHas(m22);
		m22.setGrammar(null, g1);
		m22.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m23 = factory.createMove("multi_slot2b_mv");
		m23.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ destVar + "=:external_destination_mv:)");

		m23.addSemantic(destSemG);
		m23.addSemantic(depSemG);
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

		openAgenda_afterTimeCon.addHas(m23);
		openAgenda_afterBusCon.addHas(m23);
		openAgenda_afterBusTimeCon.addHas(m23);

		m23.setGrammar(null, g1);
		m23.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m24 = factory.createMove("multi_slot2c_mv");
		m24.setVariableOperator("SET(" + destVar
				+ "=:external_destination_mv:;" + timeVar
				+ "=:external_time_mv:)");

		m24.addSemantic(destSemG);
		m24.addSemantic(timeSemG);
		destAgendaWithoutDep.addHas(m24);
		destAgendaWithoutBus.addHas(m24);
		destAgendaWithoutBusDep.addHas(m24);
		timeAgendaWithoutDep.addHas(m24);
		timeAgendaWithoutBus.addHas(m24);
		timeAgendaWithoutBusDep.addHas(m24);
		depAgendaWithoutBus.addHas(m24);
		openAgenda.addHas(m24);
		openAgenda_afterDepCon.addHas(m24);
		openAgenda_afterBusCon.addHas(m24);
		openAgenda_afterBusDepCon.addHas(m24);
		depAgendaAll.addHas(m24);
		destAgendaAll.addHas(m24);
		timeAgendaAll.addHas(m24);

		openAgenda_afterTimeDepCon.addHas(m24);
		m24.setGrammar(null, g1);
		m24.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m25 = factory.createMove("multi_slot2d_mv");
		m25.setVariableOperator("SET(" + destVar
				+ "=:external_destination_mv:;" + busVar
				+ "=:external_busnumber_mv:)");

		m25.addSemantic(destSemG);
		m25.addSemantic(busSemG);
		destAgendaWithoutDep.addHas(m25);
		destAgendaWithoutTime.addHas(m25);
		depAgendaWithoutTime.addHas(m25);
		timeAgendaWithoutDep.addHas(m25);
		openAgenda.addHas(m25);
		openAgenda_afterDepCon.addHas(m25);
		openAgenda_afterTimeCon.addHas(m25);
		openAgenda_afterTimeDepCon.addHas(m25);
		depAgendaAll.addHas(m25);
		destAgendaAll.addHas(m25);
		timeAgendaAll.addHas(m25);
		destAgendaWithoutTimeDep.addHas(m25);
		m25.setGrammar(null, g1);
		m25.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m26 = factory.createMove("multi_slot2e_mv");
		m26.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
				+ timeVar + "=:external_time_mv:)");

		m26.addSemantic(depSemG);
		m26.addSemantic(timeSemG);
		depAgendaWithoutDest.addHas(m26);
		depAgendaWithoutBus.addHas(m26);
		depAgendaWithoutBusDest.addHas(m26);
		timeAgendaWithoutDest.addHas(m26);
		timeAgendaWithoutBus.addHas(m26);
		timeAgendaWithoutBusDest.addHas(m26);
		destAgendaWithoutBus.addHas(m26);
		openAgenda.addHas(m26);
		openAgenda_afterDestCon.addHas(m26);
		openAgenda_afterBusCon.addHas(m26);
		openAgenda_afterBusDestCon.addHas(m26);
		depAgendaAll.addHas(m26);
		destAgendaAll.addHas(m26);
		timeAgendaAll.addHas(m26);
		m26.setGrammar(null, g1);
		m26.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		Move m27 = factory.createMove("multi_slot2f_mv");
		m27.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
				+ timeVar + "=:external_time_mv:)");

		m27.addSemantic(busSemG);
		m27.addSemantic(timeSemG);
		timeAgendaWithoutDep.addHas(m27);
		timeAgendaWithoutDest.addHas(m27);
		depAgendaWithoutDest.addHas(m27);
		destAgendaWithoutDep.addHas(m27);
		openAgenda_afterDestCon.addHas(m27);
		openAgenda_afterDepCon.addHas(m27);
		openAgenda_afterDepDestCon.addHas(m27);
		openAgenda.addHas(m27);
		depAgendaAll.addHas(m27);
		destAgendaAll.addHas(m27);
		timeAgendaAll.addHas(m27);
		timeAgendaWithoutDepDest.addHas(m27);
		m27.setGrammar(null, g1);
		m27.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

		// Generate Confirmation user Moves, connecting them to
		// Semantics and Agendas

		Move m28 = factory.createMove("usermov_affirm_bus_number");

		m28.addSemantic(conBusSem);
		conBusAgenda.addHas(m28);
		m28.setGrammar(null, g1);

		Move m29 = factory.createMove("usermov_affirm_arrival_place");

		m29.addSemantic(conDestSem);
		conDestAgenda.addHas(m29);
		m29.setGrammar(null, g1);

		Move m30 = factory.createMove("usermov_affirm_departure_place");
		m30.addSemantic(conDepSem);
		conDepAgenda.addHas(m30);
		m30.setGrammar(null, g1);

		Move m31 = factory.createMove("usermov_affirm_travel_time");

		m31.addSemantic(conTimeSem);
		conTimeAgenda.addHas(m31);
		m31.setGrammar(null, g1);

		Move m32 = factory.createMove("usermov_deny_bus_number");
		busAgenda.addHas(m32);
		m32.addContrarySemantic(busSemG);
		conBusAgenda.addHas(m32);
		m32.setGrammar(null, g1);

		Move m33 = factory.createMove("usermov_deny_arrival_place");

		m33.addContrarySemantic(destSemG);
		conDestAgenda.addHas(m33);
		m33.setGrammar(null, g1);

		Move m34 = factory.createMove("usermov_deny_departure_place");

		m34.addContrarySemantic(depSemG);
		conDepAgenda.addHas(m34);
		m34.setGrammar(null, g1);

		Move m35 = factory.createMove("usermov_deny_travel_time");

		m35.addContrarySemantic(timeSemG);
		conTimeAgenda.addHas(m35);
		m35.setGrammar(null, g1);

		Move hangUpMove = factory.createMove("usermov_hang_up");
		hangUpMove.addSemantic(hangUpSem);
		hangUpMove.setGrammar(null,g1);

		for (Agenda a : factory.getAllAgendaInstances()) {
			if (!(a.hasIsMasterBool() && a.getIsMasterBool()))
				a.addHas(hangUpMove);
		}

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
