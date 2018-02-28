//dividing places to covered and uncovered

package owlSpeak.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;

//TODO check folders of files to be created
public class AdaptiveLetsGo {
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

	
	//creates the grammars for the bus route user moves
	public static Grammar importGrammarBus (OSFactory factory, String name) {
					
		name = name.replace("*", ""); // Remove *
		String grammarName = name;
		grammarName = name.replace(" ", "_"); // Replace spaces by "_"
		Grammar g1 = factory.createGrammar("gr_Bus_" + grammarName);
		g1.addGrammarString(factory.factory.getOWLLiteral("[<pbus>] "+name,"en"));
		
		return g1;
	}
	
	
	//creates the grammars for the departure place user moves
	public static Grammar importGrammarDep (OSFactory factory, String name) {
			
		name = name.replace("*", ""); // Remove *
		String grammarName = name;
		grammarName = name.replace(" ", "_"); // Replace spaces by "_"
		Grammar g2 = factory.createGrammar("gr_Dep_Place_" + grammarName);
		g2.addGrammarString(factory.factory.getOWLLiteral("<pdep> "+name,"en"));

		
		return g2;
	}
	
	
	//creates the grammars for the destination user moves
	public static Grammar importGrammarDest (OSFactory factory, String name) {
		
		name = name.replace("*", ""); // Remove *
		String grammarName = name;
		grammarName = name.replace(" ", "_"); // Replace spaces by "_"

		Grammar g3 = factory.createGrammar("gr_Dest_" + grammarName);
		g3.addGrammarString(factory.factory.getOWLLiteral("<pdest> "+name,"en"));
	
		return g3;
	}
	
	//creates the grammars for the departure to destination user moves
	public static Grammar importGrammarDepDest (OSFactory factory, String departure, String destination) {
		
		departure = departure.replaceAll("\\*([a-zA-Z0-9]*)", "[$1]");
		destination = destination.replaceAll("\\*([a-zA-Z0-9]*)", "[$1]");
		String grammarName = (departure + "_" + destination).replace(" ", "_").replace("*", ""); // Replace spaces by "_"

		Grammar g3 = factory.createGrammar("gr_Orig_Dest_" + grammarName);
		g3.addGrammarString(factory.factory.getOWLLiteral("<pdep> "+departure+" <pdest2> "+destination,"en"));
	
		return g3;
	}
	
	
	//adds a grammar to a move
	public static void addGrammarToMove (Move move, Grammar grammar){
	
		move.setGrammar(null, grammar);

	}

	//creates prefix-grammar for bus routes
	public static Grammar prefixGrammarBusRoute (OSFactory factory){
		
		Grammar g4 = factory.createGrammar("pbus");
		g4.addGrammarString(factory.factory.getOWLLiteral("[(I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna) [(take | ride with)] [(a | the)] (bus | route | line | bus line)]","en"));

		return g4;
		
	}
	
	//creates prefix-grammar for departure places
	public static Grammar prefixGrammarDepPlace (OSFactory factory){
		

		Grammar g5 = factory.createGrammar("pdep");
		g5.addGrammarString(factory.factory.getOWLLiteral("[(I want [to] | I would like [to] | I'd like [to] | I'm | I am | I need [to] | I wanna) [(be | [a] bus)] (leave | depart | go | leaving | departing)] from","en"));

		return g5;
	}
	
	//creates the prefix grammar for destinations
	public static Grammar prefixGrammarDest (OSFactory factory){

		Grammar g6 = factory.createGrammar("pdest");
		g6.addGrammarString(factory.factory.getOWLLiteral("((I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna) [([a] bus [to])] (arrive at | reach | get to | be at | go to | to)) | to","en"));

		return g6;
	}
	
	//creates the prefix grammar for destinations
	public static Grammar prefixGrammarDest2 (OSFactory factory){

		Grammar g6 = factory.createGrammar("pdest2");
		g6.addGrammarString(factory.factory.getOWLLiteral("([and] ([(I want to | I would like to | I'd like to | I need to | I wanna)] (arrive at | reach | get to | be at | go to))) | to","en"));

		return g6;
	}
	

	//adds corresponding utterance-string to system moves
	public static Utterance makeUtterance (OSFactory factory, String name){
		
		Utterance utterance_name = factory.createUtterance(name);
		String utterance_String = "";


		if (name.equals("utterance_Request_Open")){
			utterance_String = "What can I do for you?";
		}
		else if (name.equals("utterance_Not_Covered")){
			utterance_String = "Sorry, but I don't have any information on that.";
		}
		else if (name.equals("utterance_Here_Is_Your_Info")){
			utterance_String = "Here is your information: You want to leave with bus %Bus_Route_var% from %Departure_Place_var% to %Destination_var% at %Time_var%. Thank you. Good bye.";
		}
		else if (name.equals("utterance_Here_Is_Your_Info_2")){
			utterance_String = "Here is your information: You want to leave from %Departure_Place_var% to %Destination_var% at %Time_var%. Thank you. Good bye.";
		}
		else if (name.equals("utterance_Help_General")){
			utterance_String = "Please state from where you are leaving or where you want to go to. Say for example: I want to leave from century square. Or: I'd like to go to lincoln avenue. ";
		}
		else if (name.equals("utterance_Help_Bus_Route")){
			utterance_String = "Please state the bus line you want to take. Say for example: I need bus line 1a";
		}
		else if (name.equals("utterance_Help_Departure_Place")){
			utterance_String = "Please state your departure place. Say for example: I want to leave from century square or just from century square. If the previous information was understood incorrectly, say no or wrong, else say yes.";
		}
		else if (name.equals("utterance_Help_Destination")){
			utterance_String = "Please state your destination. Say for example: I'd like to go to lincoln avenue or just to lincoln avenue. If the previous information was understood incorrectly, say no or wrong, else say yes.";
		}
		else if (name.equals("utterance_Help_Time")){
			utterance_String = "Please state the time of you departure. Do not use any prepositions, just say for example 2 p. m. If the previous information was understood incorrectly, say no or wrong, else say yes.";
		}
		//explicit confirmation
		else if (name.equals("utterance_Request_Bus_Route")){
			utterance_String = "What bus route do you want to take?";
		}
		else if (name.equals("utterance_Request_Departure_Place")){
			utterance_String = "Where are you leaving from?";
		}
		else if (name.equals("utterance_Request_Time")){
			utterance_String = "At what time do you want to leave?";
		}
		else if (name.equals("utterance_Request_Destination")){
			utterance_String = "Where do you want to go to?";
		}
		else if (name.equals("utterance_Confirm_Bus_Route")){
			utterance_String = "You want to take bus %Bus_Route_var%?";
		}
		else if (name.equals("utterance_Confirm_Departure_Place")){
			utterance_String = "Are you leaving from %Departure_Place_var%?";
		}
		else if (name.equals("utterance_Confirm_Destination")){
			utterance_String = "Do you want to go to %Destination_var%?";
		}
		else if (name.equals("utterance_Confirm_Time")){
			utterance_String = "Do you want to leave at %Time_var%?";
		}

		//implicit confirmation
		else if (name.equals("utterance_Departure_Place_After_Bus_Route")){
			utterance_String = "From where do you want to take bus %Bus_Route_var%?";
		}
		else if (name.equals("utterance_Destination_After_Departure_Place")){
			utterance_String = "Where do you want to go from %Departure_Place_var%?";
		}
		else if (name.equals("utterance_Time_After_Destination")){
			utterance_String = "When do you want to go to %Destination_var%?";
		}
		else if (name.equals("utterance_Departure_Place_After_Destination")){
			utterance_String = "From where do you want to go to %Destination_var%?";
		}
		else if (name.equals("utterance_Time_After_Departure_Place")){
			utterance_String = "When do you want to leave from %Departure_Place_var%?";
		}
		else if (name.equals("utterance_End_Task_Time")){
			utterance_String = "%Time_var%. Please wait. Your request is being processed.";
		}


		else{
			utterance_String = "unknown utterance name";
		}
		
		utterance_name.addUtteranceString(factory.factory.getOWLLiteral(utterance_String,"en"));
		return utterance_name;
			}
	
	//changes name-string to get corresponding utterance-name
	public static void getAndAddUtterance (OSFactory factory, Move move){
		
		String name = move.toString();
		name = name.replace("sys", "utterance");
		move.setUtterance(null, makeUtterance(factory, name));
		
		
	}
	
	
	//method that creates the deny and affirm grammars and adds them to the corresponding moves
	public static void getAndAddGrammars (OSFactory factory, Move move){
		
		Grammar grammar_Deny = factory.createGrammar("gr_Deny");
		String gramString_Deny = "([No] [(that's | that is)] (wrong | No | False | not right | none)) | I don't | I do not | Nowhere | From Nowhere | Never";
		grammar_Deny.addGrammarString(factory.factory.getOWLLiteral(gramString_Deny,"en"));
		Grammar grammar_Affirm = factory.createGrammar("gr_Affirm");
		String gramString_Affirm = "([Yes] [(That's | that is)] (correct| right)) | Yes | [Yes] I do";
		grammar_Affirm.addGrammarString(factory.factory.getOWLLiteral(gramString_Affirm,"en"));
		Grammar grammar_Help = factory.createGrammar("gr_Help");
		String gramString_Help = "([please] Help [me] [please])";
		grammar_Help.addGrammarString(factory.factory.getOWLLiteral(gramString_Help,"en"));


		
		if ((move.toString().equals("user_Affirm_Bus_Route")) || (move.toString().equals("user_Affirm_Destination")) || (move.toString().equals("user_Affirm_Departure_Place")) || (move.toString().equals("user_Affirm_Time"))){
			move.setGrammar(null, grammar_Affirm);
		}
		else if ((move.toString().equals("user_Deny_Bus_Route"))|| (move.toString().equals("user_Deny_Destination")) || (move.toString().equals("user_Deny_Departure_Place")) || (move.toString().equals("user_Deny_Time"))){
			move.setGrammar(null, grammar_Deny);
		}
		else if ((move.toString().equals("user_Help_General")) || (move.toString().equals("user_Help_Bus_Route")) || (move.toString().equals("user_Help_Departure_Place")) || (move.toString().equals("user_Help_Destination")) || (move.toString().equals("user_Help_Time"))){
			move.setGrammar(null, grammar_Help);
		}
		else{
			
		}

		
	}
	
	
	public static void main(String[] argv) throws Exception {
		
		//Variable to set the type of dialogue
		//Adaptive 0, explicit 1, implicit 2
		int dialogueType = 0;
		
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
		ServletEngine engine = new ServletEngine();
		String uriSave = Settings.uri;
		Settings.uri = "http://localhost:8080/OwlSpeakOnto.owl";
		String filename = "adLetsGo.owl";
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

		
		// generate semantic groups and semantics
		SemanticGroup semGroupBusRoute = factory.createSemanticGroup("Bus_Route_sem");

		SemanticGroup semGroupDepPlace = factory.createSemanticGroup("Departure_Place_sem");

		SemanticGroup semGroupDest = factory.createSemanticGroup("Destination_sem");

		SemanticGroup semGroupTime = factory.createSemanticGroup("Time_sem");

		Semantic semConfirmDest = factory.createSemantic("Confirm_Destination_sem");
		semConfirmDest.setConfirmationInfo(true);

		Semantic semConfirmDepPlace = factory.createSemantic("Confirm_Departure_Place_sem");
		semConfirmDepPlace.setConfirmationInfo(true);

		Semantic semConfirmTime = factory.createSemantic("Confirm_Time_sem");
		semConfirmTime.setConfirmationInfo(true);

		Semantic semConfirmBusRoute = factory.createSemantic("Confirm_Bus_Route_sem");
		semConfirmBusRoute.setConfirmationInfo(true);
		
		Semantic semHelpGeneral = factory.createSemantic("Help_General_sem");
		Semantic semHelpBusRoute = factory.createSemantic("Help_Bus_Route_sem");
		Semantic semHelpDepPlace = factory.createSemantic("Help_Departure_Place_sem");
		Semantic semHelpDest = factory.createSemantic("Help_Destination_sem");
		Semantic semHelpTime = factory.createSemantic("Help_Time_sem");

	
		//adding semantics to groups
		semConfirmDest.addSemanticGroup(semGroupDest);
		semGroupDest.addContainedSemantic(semConfirmDest);
		semConfirmDepPlace.addSemanticGroup(semGroupDepPlace);
		semGroupDepPlace.addContainedSemantic(semConfirmDepPlace);
		semConfirmTime.addSemanticGroup(semGroupTime);
		semGroupTime.addContainedSemantic(semConfirmTime);
		semConfirmBusRoute.addSemanticGroup(semGroupBusRoute);
		semGroupBusRoute.addContainedSemantic(semConfirmBusRoute);
		
		// Generate Variables
		Variable varBusRoute = factory.createVariable("Bus_Route_var");
		semGroupBusRoute.addVariable(varBusRoute); // connecting the variable to the corresponding
		varBusRoute.addBelongsToSemantic(semGroupBusRoute); // Connecting the Semantic to the
										// corresponding Variable

		Variable varDepPlace = factory.createVariable("Departure_Place_var");
		semGroupDepPlace.addVariable(varDepPlace);
		varDepPlace.addBelongsToSemantic(semGroupDepPlace);

		Variable varDest = factory.createVariable("Destination_var");
		semGroupDest.addVariable(varDest);
		varDest.addBelongsToSemantic(semGroupDest);

		Variable varTime = factory.createVariable("Time_var");
		semGroupTime.addVariable(varTime);
		varTime.addBelongsToSemantic(semGroupTime);
		
		// Generate Agendas
		
		//general agendas
		Agenda agendaMaster = factory.createAgenda("Masteragenda");
		agendaMaster.setIsMasterBool(false, true);
		
		Agenda agendaOpen = factory.createAgenda("Open_Agenda");
		agendaOpen.setRole(agendaOpen.getRole(), "collection");
		agendaMaster.addNext(agendaOpen);

		Agenda agendaInfo = factory.createAgenda("Here_Your_Info_Agenda");
		agendaInfo.addRequires(semGroupBusRoute);
		agendaInfo.addRequires(semGroupDepPlace);
		agendaInfo.addRequires(semGroupDest);
		agendaInfo.addRequires(semGroupTime);
		agendaInfo.addRequires(semConfirmDest);
		agendaInfo.addRequires(semConfirmDepPlace);
		agendaInfo.addRequires(semConfirmTime);
		agendaInfo.addRequires(semConfirmBusRoute);
		
		Agenda agendaInfo2 = factory.createAgenda("Here_Your_Info_Agenda_2");
		agendaInfo2.addRequires(semGroupDepPlace);
		agendaInfo2.addRequires(semGroupDest);
		agendaInfo2.addRequires(semGroupTime);
		agendaInfo2.addRequires(semConfirmDest);
		agendaInfo2.addRequires(semConfirmDepPlace);
		agendaInfo2.addRequires(semConfirmTime);
		agendaInfo2.setRole(agendaInfo2.getRole(), "collection");

		Agenda agendaNotCovered = factory.createAgenda("Not_Covered_Agenda");
		agendaNotCovered.addRequires(semConfirmDest);
		agendaNotCovered.addRequires(semConfirmDepPlace);
		agendaNotCovered.addRequires(semGroupDepPlace);
		agendaNotCovered.addRequires(semGroupDest);
		agendaNotCovered.setRole(agendaNotCovered.getRole(), "collection");
		
		Agenda agendaHelpGeneral = factory.createAgenda("Help_General_Agenda");
		agendaHelpGeneral.setRole(agendaHelpGeneral.getRole(), "collection");

		Agenda agendaHelpBusRoute = factory.createAgenda("Help_Bus_Route_Agenda");
		agendaHelpBusRoute.setRole(agendaHelpBusRoute.getRole(), "collection");
		
		Agenda agendaHelpDepPlace = factory.createAgenda("Help_Departure_Place_Agenda");
		agendaHelpGeneral.setRole(agendaHelpDepPlace.getRole(), "collection");
		
		Agenda agendaHelpDest = factory.createAgenda("Help_Destination_Agenda");
		agendaHelpDest.setRole(agendaHelpDest.getRole(), "collection");
		
		Agenda agendaHelpTime = factory.createAgenda("Help_Time_Agenda");
		agendaHelpTime.setRole(agendaHelpTime.getRole(), "collection");
	
		//for explicit confirmation
		Agenda agendaDest = factory.createAgenda("Destination_Agenda");
		agendaDest.addMustnot(semConfirmDest); // Fill the MustNot field in the Agenda
		agendaDest.addMustnot(semGroupDest);
		agendaDest.setRole(agendaDest.getRole(), "collection"); // Setting the Role whether it is confirmation or collection
		agendaDest.setPriority(0, 80);

		Agenda agendaDepPlace = factory.createAgenda("Departure_Place_Agenda");
		agendaDepPlace.addMustnot(semConfirmDepPlace);
		agendaDepPlace.addMustnot(semGroupDepPlace);
		agendaDepPlace.setRole(agendaDepPlace.getRole(), "collection");
		agendaDepPlace.setPriority(0, 90);

		Agenda agendaBusRoute = factory.createAgenda("Bus_Routes_Agenda");
		agendaBusRoute.addMustnot(semConfirmBusRoute);
		agendaBusRoute.addMustnot(semGroupBusRoute);
		agendaBusRoute.setRole(agendaBusRoute.getRole(), "collection");
		agendaBusRoute.setPriority(0, 100);

		Agenda agendaTime = factory.createAgenda("Time_Agenda");
		agendaTime.addMustnot(semConfirmTime);
		agendaTime.addMustnot(semGroupTime);
		agendaTime.addRequires(semConfirmDepPlace);
		agendaTime.addRequires(semConfirmDest);
		agendaTime.setRole(agendaTime.getRole(), "collection");
		agendaTime.setPriority(0, 70);

		
		Agenda agendaConfirmBusRoute = factory.createAgenda("Confirm_Bus_Route_Agenda");
		agendaConfirmBusRoute.addRequires(semGroupBusRoute);
		agendaConfirmBusRoute.addMustnot(semConfirmBusRoute);
		agendaConfirmBusRoute.setRole(agendaConfirmBusRoute.getRole(), "confirmation");
		agendaConfirmBusRoute.setPriority(0, 110);

		Agenda agendaConfirmDest = factory.createAgenda("Confirm_Destination_Agenda");
		agendaConfirmDest.addRequires(semGroupDest);
		agendaConfirmDest.addMustnot(semConfirmDest);
		agendaConfirmDest.setRole(agendaConfirmDest.getRole(), "confirmation");
		agendaConfirmDest.setPriority(0, 110);

		Agenda agendaConfirmDepPlace = factory.createAgenda("Confirm_Departure_Place_Agenda");
		agendaConfirmDepPlace.addRequires(semGroupDepPlace);
		agendaConfirmDepPlace.addMustnot(semConfirmDepPlace);
		agendaConfirmDepPlace.setRole(agendaConfirmDepPlace.getRole(), "confirmation");
		agendaConfirmDepPlace.setPriority(0, 110);

		
		Agenda agendaConfirmTime = factory.createAgenda("Confirm_Time_Agenda");
		agendaConfirmTime.addRequires(semGroupTime);
		agendaConfirmTime.addMustnot(semConfirmTime);
		agendaConfirmTime.setRole(agendaConfirmTime.getRole(), "confirmation");
		agendaConfirmTime.setPriority(0, 110);
			

		//for implicit confirmation
		Agenda agendaImplicitDepPlaceAfterBusRoute = factory.createAgenda("Implicit_DepPlace_After_BusRoute_Agenda");
		agendaImplicitDepPlaceAfterBusRoute.addRequires(semGroupBusRoute);
		agendaImplicitDepPlaceAfterBusRoute.addMustnot(semGroupDepPlace);
		agendaImplicitDepPlaceAfterBusRoute.setPriority(0, 100);
						
		Agenda agendaImplicitDestAfterDepPlace = factory.createAgenda("Implicit_Dest_After_DepPlace_Agenda");
		agendaImplicitDestAfterDepPlace.addRequires(semGroupDepPlace);
		agendaImplicitDestAfterDepPlace.addMustnot(semGroupDest);
		agendaImplicitDestAfterDepPlace.setPriority(0, 90);
		
		Agenda agendaImplicitDepPlaceAfterDest = factory.createAgenda("Implicit_DepPlace_After_Dest_Agenda");
		agendaImplicitDepPlaceAfterDest.addRequires(semGroupDest);
		agendaImplicitDepPlaceAfterDest.addMustnot(semGroupDepPlace);
		agendaImplicitDepPlaceAfterDest.setPriority(0, 80);
				
		Agenda agendaImplicitTimeAfterDest = factory.createAgenda("Implicit_Time_After_Dest_Agenda");
		agendaImplicitTimeAfterDest.addRequires(semGroupDest);
		agendaImplicitTimeAfterDest.addMustnot(semGroupTime);
		agendaImplicitTimeAfterDest.setPriority(0, 70);
		
		Agenda agendaImplicitTimeAfterDepPlace = factory.createAgenda("Implicit_Time_After_DepPlace_Agenda");
		agendaImplicitTimeAfterDepPlace.addRequires(semGroupDepPlace);
		agendaImplicitTimeAfterDepPlace.addMustnot(semGroupTime);
		agendaImplicitTimeAfterDepPlace.setPriority(0, 60);
	
		Agenda agendaImplicitEndTaskTime = factory.createAgenda("Implicit_End_Task_Time_Agenda");
		agendaImplicitEndTaskTime.addRequires(semGroupTime);
		agendaImplicitEndTaskTime.addMustnot(semConfirmTime);
		agendaImplicitEndTaskTime.setPriority(0, 50);
	

		//setting the requirements for the different dialogue types, depending on the set variable dialogueType
		//adaptive dialogue
		if (dialogueType==0)
		{
			agendaDest.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaBusRoute.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaTime.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaConfirmBusRoute.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaConfirmDest.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaConfirmDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			agendaConfirmTime.setVariableOperator("", "REQUIRES(%InteractionQuality%<<2)");
			
			agendaImplicitDepPlaceAfterBusRoute.setVariableOperator("", "REQUIRES(%InteractionQuality%>>1)");
			agendaImplicitDestAfterDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%>>1)");
			agendaImplicitDepPlaceAfterDest.setVariableOperator("", "REQUIRES(%InteractionQuality%>>1)");
			agendaImplicitTimeAfterDest.setVariableOperator("", "REQUIRES(%InteractionQuality%>>1)");
			agendaImplicitTimeAfterDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%>>1)");
			agendaImplicitEndTaskTime.setVariableOperator("", "REQUIRES(%InteractionQuality%>>1)");
			
		}
		//explicit dialogue
		else if(dialogueType==1)
		{
			agendaImplicitDepPlaceAfterBusRoute.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaImplicitDestAfterDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaImplicitDepPlaceAfterDest.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaImplicitTimeAfterDest.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaImplicitTimeAfterDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaImplicitEndTaskTime.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
		}
		//implicit dialogue
		else if(dialogueType==2)
		{
			agendaDest.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaBusRoute.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaTime.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaConfirmBusRoute.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaConfirmDest.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaConfirmDepPlace.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			agendaConfirmTime.setVariableOperator("", "REQUIRES(%InteractionQuality%>>3)");
			
		}
		else{}
		
		//creating the summary agendas, adding summarized agendas and properties
		SummaryAgenda sumAgendaStart = factory.createSummaryAgenda("Start_SummaryAgenda");
		sumAgendaStart.setType(sumAgendaStart.getTypeString(), "request");
		sumAgendaStart.setRole(sumAgendaStart.getRole(), "collection");
		
		agendaMaster.addSummaryAgenda(sumAgendaStart);
		sumAgendaStart.addSummarizedAgenda(agendaMaster);
		agendaOpen.addSummaryAgenda(sumAgendaStart);
		sumAgendaStart.addSummarizedAgenda(agendaOpen);
		
		sumAgendaStart.addNext(agendaConfirmBusRoute);
		sumAgendaStart.addNext(agendaConfirmDepPlace);
		sumAgendaStart.addNext(agendaConfirmDest);
		sumAgendaStart.addNext(agendaImplicitDepPlaceAfterDest);
		sumAgendaStart.addNext(agendaImplicitDestAfterDepPlace);
		sumAgendaStart.addNext(agendaImplicitDepPlaceAfterBusRoute);
		
		SummaryAgenda sumAgendaConfirm = factory.createSummaryAgenda("Confirm_SummaryAgenda");
		sumAgendaConfirm.setType(sumAgendaConfirm.getTypeString(), "confirmation");
		sumAgendaConfirm.setRole(sumAgendaConfirm.getRole(), "confirmation");

		agendaConfirmTime.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmTime);
		agendaConfirmDepPlace.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmDepPlace);
		agendaConfirmDest.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmDest);
		agendaConfirmBusRoute.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmBusRoute);
		
		SummaryAgenda sumAgendaImplicit = factory.createSummaryAgenda("Implicit_SummaryAgenda");
		sumAgendaImplicit.setType(sumAgendaConfirm.getTypeString(), "implicit");
		sumAgendaImplicit.setRole(sumAgendaConfirm.getRole(), "confirmation");
		
		agendaImplicitDepPlaceAfterBusRoute.addSummaryAgenda(sumAgendaImplicit);
		sumAgendaImplicit.addSummarizedAgenda(agendaImplicitDepPlaceAfterBusRoute);
		agendaImplicitDestAfterDepPlace.addSummaryAgenda(sumAgendaImplicit);
		sumAgendaImplicit.addSummarizedAgenda(agendaImplicitDestAfterDepPlace);
		agendaImplicitDepPlaceAfterDest.addSummaryAgenda(sumAgendaImplicit);
		sumAgendaImplicit.addSummarizedAgenda(agendaImplicitDepPlaceAfterDest);
		agendaImplicitTimeAfterDepPlace.addSummaryAgenda(sumAgendaImplicit);
		sumAgendaImplicit.addSummarizedAgenda(agendaImplicitTimeAfterDepPlace);
		agendaImplicitTimeAfterDest.addSummaryAgenda(sumAgendaImplicit);
		sumAgendaImplicit.addSummarizedAgenda(agendaImplicitEndTaskTime);
		agendaImplicitEndTaskTime.addSummaryAgenda(sumAgendaImplicit);


		
		SummaryAgenda sumAgendaRequest = factory.createSummaryAgenda("Request_SummaryAgenda");
		sumAgendaRequest.setType(sumAgendaRequest.getTypeString(), "request");
		sumAgendaRequest.setRole(sumAgendaRequest.getRole(), "collection");
		
		agendaDest.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaDest);
		agendaDepPlace.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaDepPlace);
		agendaBusRoute.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaBusRoute);
		agendaTime.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaTime);
		
		SummaryAgenda sumAgendaInfo = factory.createSummaryAgenda("Info_SummaryAgenda");
		sumAgendaInfo.setType(sumAgendaInfo.getTypeString(), "announcement");
		sumAgendaInfo.setRole(sumAgendaInfo.getRole(), "collection");
		
		agendaInfo.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaInfo);
		agendaInfo2.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaInfo2);
		agendaNotCovered.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaNotCovered);
		agendaHelpGeneral.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaHelpGeneral);
		agendaHelpBusRoute.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaHelpBusRoute);
		agendaHelpDepPlace.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaHelpDepPlace);
		agendaHelpDest.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaHelpDest);
		agendaHelpTime.addSummaryAgenda(sumAgendaInfo);
		sumAgendaInfo.addSummarizedAgenda(agendaHelpTime);
		
		
		//adding properties to agendas
		//general agendas
		agendaOpen.addNext(agendaOpen);
		agendaOpen.addNext(agendaConfirmBusRoute);
		agendaOpen.addNext(agendaConfirmDest);
		agendaOpen.addNext(agendaConfirmDepPlace);
		agendaOpen.addNext(agendaImplicitDepPlaceAfterBusRoute);
		agendaOpen.addNext(agendaImplicitDepPlaceAfterDest);
		agendaOpen.addNext(agendaImplicitDestAfterDepPlace);
		agendaOpen.addMustnot(semGroupBusRoute);
		agendaOpen.addMustnot(semGroupDepPlace);
		agendaOpen.addMustnot(semGroupDest);
		agendaOpen.addMustnot(semGroupTime);
		agendaOpen.addMustnot(semConfirmBusRoute);
		agendaOpen.addMustnot(semConfirmDepPlace);
		agendaOpen.addMustnot(semConfirmDest);
		agendaOpen.addMustnot(semConfirmTime);
		agendaOpen.addMustnot(semHelpGeneral);
		agendaOpen.setPriority(0, 227);
		
		agendaOpen.addNext(agendaHelpGeneral);
		agendaHelpGeneral.addNext(agendaOpen);

		agendaHelpGeneral.setPriority(0,230);
		agendaHelpGeneral.addRequires(semHelpGeneral);
		agendaHelpBusRoute.setPriority(0,235);
		agendaHelpBusRoute.addRequires(semHelpBusRoute);
		agendaHelpDepPlace.setPriority(0,240);
		agendaHelpDepPlace.addRequires(semHelpDepPlace);
		agendaHelpDest.setPriority(0,245);
		agendaHelpDest.addRequires(semHelpDest);
		agendaHelpTime.setPriority(0,250);
		agendaHelpTime.addRequires(semHelpTime);
								
		agendaInfo.setPriority(0, 225);
		agendaInfo2.setPriority(0, 215);
		
		//Agendas for explicit confirmation
		agendaDest.addNext(agendaConfirmDest);

		agendaDepPlace.addNext(agendaConfirmDepPlace);


		agendaBusRoute.addNext(agendaConfirmBusRoute);

		agendaTime.addNext(agendaConfirmTime);
		agendaTime.addNext(agendaInfo);		
		agendaTime.addNext(agendaInfo2);

		agendaConfirmBusRoute.addNext(agendaOpen);		//Bus route denied
		agendaConfirmBusRoute.addNext(agendaDepPlace);		//DepPlace next

		agendaConfirmDest.addNext(agendaDest); 	//Destination denied
		agendaConfirmDest.addNext(agendaTime);		//Time missing
		agendaConfirmDest.addNext(agendaDepPlace);		//DepPlace missing
		agendaConfirmDest.addNext(agendaOpen);			//denied and no variables

		agendaConfirmDepPlace.addNext(agendaDepPlace);	//DepPlace denied
		agendaConfirmDepPlace.addNext(agendaDest);	//Destination missing
		agendaConfirmDepPlace.addNext(agendaOpen);	//denied and no variables
		agendaConfirmDepPlace.addNext(agendaTime);	//destination given

		agendaConfirmTime.addNext(agendaTime);	//Time denied
		agendaConfirmTime.addNext(agendaInfo);	//All Info there
		agendaConfirmTime.addNext(agendaInfo2);	//All Info except bus route


		agendaDest.addNext(agendaHelpDest);
		agendaDepPlace.addNext(agendaHelpDepPlace);
		agendaBusRoute.addNext(agendaHelpBusRoute);
		agendaTime.addNext(agendaHelpTime);
		agendaConfirmBusRoute.addNext(agendaHelpBusRoute);
		agendaConfirmDest.addNext(agendaHelpDest);
		agendaConfirmDepPlace.addNext(agendaHelpDepPlace);
		agendaConfirmTime.addNext(agendaHelpTime);
		
		agendaHelpDest.addNext(agendaDest);
		agendaHelpDepPlace.addNext(agendaDepPlace);
		agendaHelpBusRoute.addNext(agendaBusRoute);
		agendaHelpTime.addNext(agendaTime);
		agendaHelpBusRoute.addNext(agendaConfirmBusRoute);
		agendaHelpDest.addNext(agendaConfirmDest);
		agendaHelpDepPlace.addNext(agendaConfirmDepPlace);
		agendaHelpTime.addNext(agendaConfirmTime);
		
		agendaDepPlace.addMustnot(semHelpGeneral);
		agendaDepPlace.addMustnot(semHelpBusRoute);
		agendaDepPlace.addMustnot(semHelpDest);
		agendaDepPlace.addMustnot(semHelpDepPlace);
		agendaDepPlace.addMustnot(semHelpTime);
		
		agendaDest.addMustnot(semHelpGeneral);
		agendaDest.addMustnot(semHelpBusRoute);
		agendaDest.addMustnot(semHelpDest);
		agendaDest.addMustnot(semHelpDepPlace);
		agendaDest.addMustnot(semHelpTime);
		
		agendaBusRoute.addMustnot(semHelpGeneral);
		agendaBusRoute.addMustnot(semHelpBusRoute);
		agendaBusRoute.addMustnot(semHelpDest);
		agendaBusRoute.addMustnot(semHelpDepPlace);
		agendaBusRoute.addMustnot(semHelpTime);
		
		agendaTime.addMustnot(semHelpGeneral);
		agendaTime.addMustnot(semHelpBusRoute);
		agendaTime.addMustnot(semHelpDest);
		agendaTime.addMustnot(semHelpDepPlace);
		agendaTime.addMustnot(semHelpTime);
		

		agendaConfirmDepPlace.addMustnot(semHelpGeneral);
		agendaConfirmDepPlace.addMustnot(semHelpBusRoute);
		agendaConfirmDepPlace.addMustnot(semHelpDest);
		agendaConfirmDepPlace.addMustnot(semHelpDepPlace);
		agendaConfirmDepPlace.addMustnot(semHelpTime);
		
		agendaConfirmDest.addMustnot(semHelpGeneral);
		agendaConfirmDest.addMustnot(semHelpBusRoute);
		agendaConfirmDest.addMustnot(semHelpDest);
		agendaConfirmDest.addMustnot(semHelpDepPlace);
		agendaConfirmDest.addMustnot(semHelpTime);
		
		agendaConfirmBusRoute.addMustnot(semHelpGeneral);
		agendaConfirmBusRoute.addMustnot(semHelpBusRoute);
		agendaConfirmBusRoute.addMustnot(semHelpDest);
		agendaConfirmBusRoute.addMustnot(semHelpDepPlace);
		agendaConfirmBusRoute.addMustnot(semHelpTime);
		
		agendaConfirmTime.addMustnot(semHelpGeneral);
		agendaConfirmTime.addMustnot(semHelpBusRoute);
		agendaConfirmTime.addMustnot(semHelpDest);
		agendaConfirmTime.addMustnot(semHelpDepPlace);
		agendaConfirmTime.addMustnot(semHelpTime);


		
		//Agendas for implicit confirmation
		agendaImplicitDepPlaceAfterBusRoute.addNext(agendaImplicitDestAfterDepPlace);
		agendaImplicitDepPlaceAfterBusRoute.addNext(agendaOpen);
		agendaImplicitDepPlaceAfterBusRoute.addNext(agendaImplicitTimeAfterDepPlace);
		agendaImplicitDestAfterDepPlace.addNext(agendaImplicitTimeAfterDest);
		agendaImplicitDestAfterDepPlace.addNext(agendaImplicitDepPlaceAfterBusRoute);
		agendaImplicitDestAfterDepPlace.addNext(agendaOpen);
		agendaImplicitTimeAfterDest.addNext(agendaImplicitEndTaskTime);
		agendaImplicitTimeAfterDest.addNext(agendaInfo2);
		agendaImplicitTimeAfterDest.addNext(agendaInfo);
		agendaImplicitTimeAfterDest.addNext(agendaImplicitDestAfterDepPlace);
		agendaImplicitTimeAfterDepPlace.addNext(agendaImplicitEndTaskTime);
		agendaImplicitTimeAfterDepPlace.addNext(agendaInfo2);
		agendaImplicitTimeAfterDepPlace.addNext(agendaInfo);
		agendaImplicitTimeAfterDepPlace.addNext(agendaImplicitDepPlaceAfterDest);
		agendaImplicitDepPlaceAfterDest.addNext(agendaOpen);
		agendaImplicitDepPlaceAfterDest.addNext(agendaImplicitTimeAfterDepPlace);
		agendaImplicitEndTaskTime.addNext(agendaImplicitTimeAfterDepPlace);
		agendaImplicitEndTaskTime.addNext(agendaInfo2);
		agendaImplicitEndTaskTime.addNext(agendaInfo);
		
		agendaImplicitDepPlaceAfterBusRoute.addNext(agendaHelpDepPlace);
		agendaImplicitDestAfterDepPlace.addNext(agendaHelpDest);
		agendaImplicitTimeAfterDest.addNext(agendaHelpTime);
		agendaImplicitTimeAfterDepPlace.addNext(agendaHelpTime);
		agendaImplicitDepPlaceAfterDest.addNext(agendaHelpDepPlace);
		agendaImplicitEndTaskTime.addNext(agendaHelpTime);
		
		agendaHelpDepPlace.addNext(agendaImplicitDepPlaceAfterBusRoute);
		agendaHelpDest.addNext(agendaImplicitDestAfterDepPlace);
		agendaHelpTime.addNext(agendaImplicitTimeAfterDest);
		agendaHelpTime.addNext(agendaImplicitTimeAfterDepPlace);
		agendaHelpDepPlace.addNext(agendaImplicitDepPlaceAfterDest);
		agendaHelpTime.addNext(agendaImplicitEndTaskTime);
		
		
		agendaImplicitDepPlaceAfterBusRoute.addMustnot(semHelpGeneral);
		agendaImplicitDepPlaceAfterBusRoute.addMustnot(semHelpBusRoute);
		agendaImplicitDepPlaceAfterBusRoute.addMustnot(semHelpDest);
		agendaImplicitDepPlaceAfterBusRoute.addMustnot(semHelpDepPlace);
		agendaImplicitDepPlaceAfterBusRoute.addMustnot(semHelpTime);
		
		agendaImplicitDestAfterDepPlace.addMustnot(semHelpGeneral);
		agendaImplicitDestAfterDepPlace.addMustnot(semHelpBusRoute);
		agendaImplicitDestAfterDepPlace.addMustnot(semHelpDest);
		agendaImplicitDestAfterDepPlace.addMustnot(semHelpDepPlace);
		agendaImplicitDestAfterDepPlace.addMustnot(semHelpTime);
		
		agendaImplicitTimeAfterDest.addMustnot(semHelpGeneral);
		agendaImplicitTimeAfterDest.addMustnot(semHelpBusRoute);
		agendaImplicitTimeAfterDest.addMustnot(semHelpDest);
		agendaImplicitTimeAfterDest.addMustnot(semHelpDepPlace);
		agendaImplicitTimeAfterDest.addMustnot(semHelpTime);
		
		agendaImplicitTimeAfterDepPlace.addMustnot(semHelpGeneral);
		agendaImplicitTimeAfterDepPlace.addMustnot(semHelpBusRoute);
		agendaImplicitTimeAfterDepPlace.addMustnot(semHelpDest);
		agendaImplicitTimeAfterDepPlace.addMustnot(semHelpDepPlace);
		agendaImplicitTimeAfterDepPlace.addMustnot(semHelpTime);
		
		agendaImplicitDepPlaceAfterDest.addMustnot(semHelpGeneral);
		agendaImplicitDepPlaceAfterDest.addMustnot(semHelpBusRoute);
		agendaImplicitDepPlaceAfterDest.addMustnot(semHelpDest);
		agendaImplicitDepPlaceAfterDest.addMustnot(semHelpDepPlace);
		agendaImplicitDepPlaceAfterDest.addMustnot(semHelpTime);
		
		agendaImplicitEndTaskTime.addMustnot(semHelpGeneral);
		agendaImplicitEndTaskTime.addMustnot(semHelpBusRoute);
		agendaImplicitEndTaskTime.addMustnot(semHelpDest);
		agendaImplicitEndTaskTime.addMustnot(semHelpDepPlace);
		agendaImplicitEndTaskTime.addMustnot(semHelpTime);
		
		
		
		//Changing between confirmation styles
		agendaDest.addNext(agendaImplicitTimeAfterDest);
		agendaDest.addNext(agendaImplicitDepPlaceAfterDest);
		agendaDepPlace.addNext(agendaImplicitTimeAfterDepPlace);
		agendaDepPlace.addNext(agendaImplicitDestAfterDepPlace);
		agendaBusRoute.addNext(agendaImplicitDepPlaceAfterBusRoute);
		agendaTime.addNext(agendaImplicitEndTaskTime);

		agendaImplicitDepPlaceAfterBusRoute.addNext(agendaBusRoute);
		agendaImplicitDepPlaceAfterDest.addNext(agendaConfirmDest);
		agendaImplicitDepPlaceAfterDest.addNext(agendaConfirmDepPlace);
		agendaImplicitDestAfterDepPlace.addNext(agendaConfirmDest);
		agendaImplicitDestAfterDepPlace.addNext(agendaConfirmDepPlace);
		agendaImplicitEndTaskTime.addNext(agendaConfirmTime);
		agendaImplicitTimeAfterDepPlace.addNext(agendaConfirmTime);
		agendaImplicitTimeAfterDepPlace.addNext(agendaConfirmDepPlace);
		agendaImplicitTimeAfterDest.addNext(agendaConfirmTime);
		agendaImplicitTimeAfterDest.addNext(agendaConfirmDest);



		// Generate system moves
		
		//general system moves
		Move sysmovRequestOpen = factory.createMove("sys_Request_Open");
		agendaOpen.addHas(sysmovRequestOpen);
		getAndAddUtterance(factory,sysmovRequestOpen);
		
		Move sysmovNotCovered = factory.createMove("sys_Not_Covered");
		agendaNotCovered.addHas(sysmovNotCovered);
		getAndAddUtterance(factory,sysmovNotCovered);
		
		Move sysmovInfo = factory.createMove("sys_Here_Is_Your_Info");
		agendaInfo.addHas(sysmovInfo);
		getAndAddUtterance(factory,sysmovInfo);
		sysmovInfo.setIsExitMove(false, true);
		
		Move sysmovInfo2 = factory.createMove("sys_Here_Is_Your_Info_2");
		agendaInfo2.addHas(sysmovInfo2);
		getAndAddUtterance(factory,sysmovInfo2);
		sysmovInfo2.setIsExitMove(false, true);
		
		Move sysmovHelpGeneral = factory.createMove("sys_Help_General");
		agendaHelpGeneral.addHas(sysmovHelpGeneral);
		getAndAddUtterance(factory,sysmovHelpGeneral);
		sysmovHelpGeneral.addContrarySemantic(semHelpGeneral);
		
		Move sysmovHelpBusRoute = factory.createMove("sys_Help_Bus_Route");
		agendaHelpBusRoute.addHas(sysmovHelpBusRoute);
		getAndAddUtterance(factory,sysmovHelpBusRoute);
		sysmovHelpBusRoute.addContrarySemantic(semHelpBusRoute);
		
		Move sysmovHelpDepPlace = factory.createMove("sys_Help_Departure_Place");
		agendaHelpDepPlace.addHas(sysmovHelpDepPlace);
		getAndAddUtterance(factory,sysmovHelpDepPlace);
		sysmovHelpDepPlace.addContrarySemantic(semHelpDepPlace);
		
		Move sysmovHelpDest = factory.createMove("sys_Help_Destination");
		agendaHelpDest.addHas(sysmovHelpDest);
		getAndAddUtterance(factory,sysmovHelpDest);
		sysmovHelpDest.addContrarySemantic(semHelpDest);
		
		Move sysmovHelpTime = factory.createMove("sys_Help_Time");
		agendaHelpTime.addHas(sysmovHelpTime);
		getAndAddUtterance(factory,sysmovHelpTime);
		sysmovHelpTime.addContrarySemantic(semHelpTime);

		
		
		// System moves for explicit confirmation
		Move sysmovReqBus = factory.createMove("sys_Request_Bus_Route");
		agendaBusRoute.addHas(sysmovReqBus); // Adding the move to the corresponding Agenda
		getAndAddUtterance(factory,sysmovReqBus); //Create and add the corresponding utterance
		
		Move sysmovReqDepPlace = factory.createMove("sys_Request_Departure_Place");
		agendaDepPlace.addHas(sysmovReqDepPlace);
		getAndAddUtterance(factory,sysmovReqDepPlace);

		Move sysmovReqTime = factory.createMove("sys_Request_Time");
		agendaTime.addHas(sysmovReqTime);
		getAndAddUtterance(factory,sysmovReqTime);

		Move sysmovReqDest = factory.createMove("sys_Request_Destination");
		agendaDest.addHas(sysmovReqDest);
		getAndAddUtterance(factory,sysmovReqDest);

		Move sysmovConfirmBus = factory.createMove("sys_Confirm_Bus_Route");
		agendaConfirmBusRoute.addHas(sysmovConfirmBus);
		getAndAddUtterance(factory,sysmovConfirmBus);

		Move sysmovConfirmDepPlace = factory.createMove("sys_Confirm_Departure_Place");
		agendaConfirmDepPlace.addHas(sysmovConfirmDepPlace);
		getAndAddUtterance(factory,sysmovConfirmDepPlace);

		Move sysmovConfirmDest = factory.createMove("sys_Confirm_Destination");
		agendaConfirmDest.addHas(sysmovConfirmDest);
		getAndAddUtterance(factory,sysmovConfirmDest);

		Move sysmovConfirmTime = factory.createMove("sys_Confirm_Time");
		agendaConfirmTime.addHas(sysmovConfirmTime);
		getAndAddUtterance(factory,sysmovConfirmTime);




		//for implicit confirmation
		Move sysmovImplicitDepPlaceAfterBusRoute = factory.createMove("sys_Departure_Place_After_Bus_Route");
		agendaImplicitDepPlaceAfterBusRoute.addHas(sysmovImplicitDepPlaceAfterBusRoute);
		getAndAddUtterance(factory,sysmovImplicitDepPlaceAfterBusRoute);
		
		Move sysmovImplicitDepPlaceAfterDest = factory.createMove("sys_Departure_Place_After_Destination");
		agendaImplicitDepPlaceAfterDest.addHas(sysmovImplicitDepPlaceAfterDest);
		getAndAddUtterance(factory,sysmovImplicitDepPlaceAfterDest);
		
		Move sysmovImplicitDestAfterDepPlace = factory.createMove("sys_Destination_After_Departure_Place");
		agendaImplicitDestAfterDepPlace.addHas(sysmovImplicitDestAfterDepPlace);
		getAndAddUtterance(factory,sysmovImplicitDestAfterDepPlace);

		Move sysmovImplicitTimeAfterDest = factory.createMove("sys_Time_After_Destination");
		agendaImplicitTimeAfterDest.addHas(sysmovImplicitTimeAfterDest);
		getAndAddUtterance(factory,sysmovImplicitTimeAfterDest);
		
		Move sysmovImplicitTimeAfterDepPlace = factory.createMove("sys_Time_After_Departure_Place");
		agendaImplicitTimeAfterDepPlace.addHas(sysmovImplicitTimeAfterDepPlace);
		getAndAddUtterance(factory,sysmovImplicitTimeAfterDepPlace);
		
		Move sysmovEndTaskTime = factory.createMove("sys_End_Task_Time");
		agendaImplicitEndTaskTime.addHas(sysmovEndTaskTime);
		getAndAddUtterance(factory,sysmovEndTaskTime);
		
		Move procCondDepPlaceAfterBusRoute = factory.createMove("procCond_Departure_Place_After_Bus_Route");
		agendaImplicitDepPlaceAfterBusRoute.addHas(procCondDepPlaceAfterBusRoute);
		procCondDepPlaceAfterBusRoute.addSemantic(semConfirmBusRoute);
		
		Move procCondDepPlaceAfterDestination = factory.createMove("procCond_Departure_Place_After_Destination");
		agendaImplicitDepPlaceAfterDest.addHas(procCondDepPlaceAfterDestination);
		procCondDepPlaceAfterDestination.addSemantic(semConfirmDest);
		
		Move procCondDestAfterDepPlace = factory.createMove("procCond_Destination_After_Departure_Place");
		agendaImplicitDestAfterDepPlace.addHas(procCondDestAfterDepPlace);
		procCondDestAfterDepPlace.addSemantic(semConfirmDepPlace);
		
		Move procCondTimeAfterDest = factory.createMove("procCond_Time_After_Destination");
		agendaImplicitTimeAfterDest.addHas(procCondTimeAfterDest);
		procCondTimeAfterDest.addSemantic(semConfirmDest);
		
		Move procCondTimeAfterDepPlace = factory.createMove("procCond_Time_After_Departure_Place");
		agendaImplicitTimeAfterDepPlace.addHas(procCondTimeAfterDepPlace);
		procCondTimeAfterDepPlace.addSemantic(semConfirmDepPlace);
		
		Move procCondEndTaskTime = factory.createMove("req_mov_End_Task_Time");
		agendaImplicitEndTaskTime.addHas(procCondEndTaskTime);
		procCondEndTaskTime.addSemantic(semConfirmTime);
		procCondEndTaskTime.setIsRequestMove(false, true);

		

		//user move time, working with an external grammar file
		Move usermovTime = factory.createMove("user_Time");
		usermovTime.setVariableOperator("SET(Time_var=:external_time_mv:1440:)");
		Grammar time = factory.createGrammar("gr_Time");
		time.addGrammarFile("/time.jsgf");
		agendaTime.addHas(usermovTime);
		usermovTime.setGrammar(null, time);
		usermovTime.addSemantic(semGroupTime);
		agendaImplicitTimeAfterDepPlace.addHas(usermovTime);
		agendaImplicitTimeAfterDest.addHas(usermovTime);
		procCondTimeAfterDepPlace.addProcessConditions(usermovTime);
		procCondTimeAfterDest.addProcessConditions(usermovTime);
		
		//user moves for Help_agendas
		Move moveHelpGeneral = factory.createMove("user_Help_General");
		getAndAddGrammars(factory,moveHelpGeneral);
		moveHelpGeneral.addSemantic(semHelpGeneral);
		agendaOpen.addHas(moveHelpGeneral);
		
		Move moveHelpBusRoute = factory.createMove("user_Help_Bus_Route");
		getAndAddGrammars(factory,moveHelpBusRoute);
		moveHelpBusRoute.addSemantic(semHelpBusRoute);
		agendaBusRoute.addHas(moveHelpBusRoute);
		agendaConfirmBusRoute.addHas(moveHelpBusRoute);
		
		
		Move moveHelpDepPlace = factory.createMove("user_Help_Departure_Place");
		getAndAddGrammars(factory,moveHelpDepPlace);
		moveHelpDepPlace.addSemantic(semHelpDepPlace);
		agendaConfirmDepPlace.addHas(moveHelpDepPlace);
		agendaDepPlace.addHas(moveHelpDepPlace);
		
		Move moveHelpDest = factory.createMove("user_Help_Destination");
		getAndAddGrammars(factory,moveHelpDest);
		moveHelpDest.addSemantic(semHelpDest);
		agendaConfirmDest.addHas(moveHelpDest);
		agendaDest.addHas(moveHelpDest);
		
		Move moveHelpTime = factory.createMove("user_Help_Time");
		getAndAddGrammars(factory,moveHelpTime);
		moveHelpTime.addSemantic(semHelpTime);
		agendaTime.addHas(moveHelpTime);
		agendaConfirmTime.addHas(moveHelpTime);
		
		
		agendaImplicitDepPlaceAfterBusRoute.addHas(moveHelpDepPlace);
		agendaImplicitDepPlaceAfterDest.addHas(moveHelpDepPlace);
		agendaImplicitDestAfterDepPlace.addHas(moveHelpDest);
		agendaImplicitEndTaskTime.addHas(moveHelpTime);
		agendaImplicitTimeAfterDepPlace.addHas(moveHelpTime);
		agendaImplicitTimeAfterDest.addHas(moveHelpTime);
		
		Move reqmovHelp = factory.createMove("req_mov_Help");
		reqmovHelp.addContrarySemantic(semHelpGeneral);
		reqmovHelp.addContrarySemantic(semHelpBusRoute);
		reqmovHelp.addContrarySemantic(semHelpDepPlace);
		reqmovHelp.addContrarySemantic(semHelpDest);
		reqmovHelp.addContrarySemantic(semHelpTime);
		reqmovHelp.setIsRequestMove(false, true);
		agendaHelpGeneral.addHas(reqmovHelp);
		agendaHelpBusRoute.addHas(reqmovHelp);
		agendaHelpDepPlace.addHas(reqmovHelp);
		agendaHelpDest.addHas(reqmovHelp);
		agendaHelpTime.addHas(reqmovHelp);
		
		
		

		
		
		
		// Generate Confirmation and Deny user Moves, connecting them to
		// Semantics and Agendas
		Move moveUserAffirmBusRoute = factory.createMove("user_Affirm_Bus_Route");
		moveUserAffirmBusRoute.addSemantic(semConfirmBusRoute);
		agendaConfirmBusRoute.addHas(moveUserAffirmBusRoute);
		getAndAddGrammars(factory,moveUserAffirmBusRoute);

		Move moveUserDenyBusRoute = factory.createMove("user_Deny_Bus_Route");
		moveUserDenyBusRoute.addContrarySemantic(semConfirmBusRoute.getSemanticGroup());
		moveUserDenyBusRoute.addContrarySemantic(semConfirmBusRoute);
		agendaConfirmBusRoute.addHas(moveUserDenyBusRoute);
		getAndAddGrammars(factory,moveUserDenyBusRoute);
		agendaImplicitDepPlaceAfterBusRoute.addHas(moveUserDenyBusRoute);

		Move moveUserAffirmDest = factory.createMove("user_Affirm_Destination");
		moveUserAffirmDest.addSemantic(semConfirmDest);
		agendaConfirmDest.addHas(moveUserAffirmDest);
		getAndAddGrammars(factory,moveUserAffirmDest);

		Move moveUserDenyDest = factory.createMove("user_Deny_Destination");
		moveUserDenyDest.addContrarySemantic(semConfirmDest.getSemanticGroup());
		moveUserDenyDest.addContrarySemantic(semConfirmDest);
		agendaConfirmDest.addHas(moveUserDenyDest);
		getAndAddGrammars(factory,moveUserDenyDest);
		agendaImplicitTimeAfterDest.addHas(moveUserDenyDest);
		agendaImplicitDepPlaceAfterDest.addHas(moveUserDenyDest);
		
		Move moveUserAffirmDepPlace = factory.createMove("user_Affirm_Departure_Place");
		moveUserAffirmDepPlace.addSemantic(semConfirmDepPlace);
		agendaConfirmDepPlace.addHas(moveUserAffirmDepPlace);
		getAndAddGrammars(factory,moveUserAffirmDepPlace);

		Move moveUserDenyDepPlace = factory.createMove("user_Deny_Departure_Place");
		moveUserDenyDepPlace.addContrarySemantic(semConfirmDepPlace.getSemanticGroup());
		moveUserDenyDepPlace.addContrarySemantic(semConfirmDepPlace);
		agendaConfirmDepPlace.addHas(moveUserDenyDepPlace);
		getAndAddGrammars(factory,moveUserDenyDepPlace);
		agendaImplicitDestAfterDepPlace.addHas(moveUserDenyDepPlace);
		agendaImplicitTimeAfterDepPlace.addHas(moveUserDenyDepPlace);
	
		
		Move moveUserAffirmTime = factory.createMove("user_Affirm_Time");
		moveUserAffirmTime.addSemantic(semConfirmTime);
		agendaConfirmTime.addHas(moveUserAffirmTime);
		getAndAddGrammars(factory,moveUserAffirmTime);

		Move moveUserDenyTime = factory.createMove("user_Deny_Time");
		moveUserDenyTime.addContrarySemantic(semConfirmTime.getSemanticGroup());
		moveUserDenyTime.addContrarySemantic(semConfirmTime);
		getAndAddGrammars(factory,moveUserDenyTime);
		agendaConfirmTime.addHas(moveUserDenyTime);
		agendaImplicitEndTaskTime.addHas(moveUserDenyTime);


		// Generate user Moves using 4 text files
//		String folder = "D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\";
//		String file_temp2 = folder + "all_places_covered.txt";
//		String file_temp3 = folder + "all_places_not_covered.txt";
//		String file_temp1 = folder + "Bus_routes_covered.txt";
//		String file_temp4 = folder + "Bus_routes_not_covered.txt";
		String allPlacesCoveredFile = AdaptiveLetsGo.class.getResource("all_places_covered.txt").getPath();
		String allPlacesNotCoveredFile = AdaptiveLetsGo.class.getResource("all_places_not_covered.txt").getPath();
		String busRoutesCoveredFile = AdaptiveLetsGo.class.getResource("Bus_routes_covered.txt").getPath();
		String busRoutesNotCoveredFile = AdaptiveLetsGo.class.getResource("Bus_routes_not_covered.txt").getPath();

		
	
		//Creating the prefix grammars
		Grammar preDest = prefixGrammarDest(factory);
		Grammar preDest2 = prefixGrammarDest2(factory);
		Grammar preDepPlace = prefixGrammarDepPlace(factory);
		Grammar preBus = prefixGrammarBusRoute(factory);

		File allPlaces_covered = new File(allPlacesCoveredFile);

		
//		// *********************** stefan destination+origin
//		Scanner fileScanner = new Scanner(allPlaces_covered); // Create the
//		// scanner
//		
//		
//		while (fileScanner.hasNextLine() == true) {
//			
//			String lineDestination = fileScanner.nextLine(); // save the line in the string
//			lineDestination = lineDestination.trim();// Remove spaces at the beginning and end of the
//								// line
//			System.out.println(lineDestination);
//			String destination = lineDestination.substring(1, lineDestination.length() - 1); // Remove the
//																// brackets from
//																// both sides
//			
//			
//			Scanner fileScannerCopy = new Scanner(allPlaces_covered_copy); // Create the
//			// scanner
//			
//			while (fileScannerCopy.hasNextLine() == true) {
//				String lineDeparture = fileScannerCopy.nextLine(); // save the line in the string
//				lineDeparture = lineDeparture.trim();// Remove spaces at the beginning and end of the
//									// line
//				//System.out.println(lineDestination);
//				System.out.print("*");
//				String departure = lineDeparture.substring(1, lineDeparture.length() - 1); // Remove the
//																	// brackets from
//																	// both sides
//				
//				//creating the grammars and adding general grammars i.e. the prefixes
//				Grammar g1 = importGrammarDepDest(factory, departure, destination);
//				g1.addGeneralGrammar(preDepPlace);
//				g1.addGeneralGrammar(preDest2);
//				
//				String name = departure + "_" + destination;
//				
//				name = name.replace("*", ""); // Remove *
//				name = name.replace(" ", "_"); // Replace spaces by "_"
//	
//				// Create the user destination Move, set the variable and connect it
//				// to the semantic and the Agendas
//				Move depDestMove = factory.createMove("user_Dep_Dest_" + name);
//				depDestMove.setVariableOperator("SET(" + varDepPlace + "=" + departure + ";" + varDest + "=" + destination + ")");
//				depDestMove.addSemantic(semGroupDest);
//				depDestMove.addSemantic(semGroupDepPlace);
//				agendaOpen.addHas(depDestMove);
//				//adding the grammar to the move
//				addGrammarToMove(depDestMove, g1);
//				//agendaImplicitDestAfterDepPlace.addHas(destMove);
//				//procCondDestAfterDepPlace.addProcessConditions(destMove);
//	
//	
////				// Create the user departure Move, set the variable and connect it
////				// to the semantic and the Agendas
////				Move depMove = factory.createMove("user_Dep_" + departure);
////				depMove.setVariableOperator("SET(" + varDepPlace + "=" + departure + ")");
////				depMove.addSemantic(semGroupDepPlace);
////				agendaDepPlace.addHas(depMove);
////				agendaOpen.addHas(depMove);
////				//adding the grammar to the move
////				addGrammarToMove(depMove,g2);
////				agendaImplicitDepPlaceAfterBusRoute.addHas(depMove);
////				agendaImplicitDepPlaceAfterDest.addHas(depMove);
////				procCondDepPlaceAfterBusRoute.addProcessConditions(depMove);
////				procCondDepPlaceAfterDestination.addProcessConditions(depMove);
//			}
//			fileScannerCopy.close();
//		}
//		
//		fileScanner.close();
		
		// END *********************** stefan destination+origin
		
		// 1) Generating Moves for covered_places
		Scanner fileScanner = new Scanner(allPlaces_covered); // Create the
																// scanner
		
		while (fileScanner.hasNextLine() == true) {
			String line = fileScanner.nextLine(); // save the line in the string
			line = line.trim();// Remove spaces at the beginning and end of the
								// line
			System.out.println(line);
			String name = line.substring(1, line.length() - 1); // Remove the
																// brackets from
																// both sides
			
			//creating the grammars and adding general grammars i.e. the prefixes
			Grammar g1 = importGrammarDest(factory, name);
			g1.addGeneralGrammar(preDest);
			Grammar g2 = importGrammarDep(factory, name);
			g2.addGeneralGrammar(preDepPlace);

			
			// This part of the code was used to Generate all the possible
			// combinations due to the presence of the "*", not used in this
			// program

			/*
			 * int a=name.indexOf("*"); //int i = 0; int i_final=0;
			 * 
			 * System.out.println(a); //String list[]=new String [5]; String
			 * name2=name; if(a>=0){ // Check if we have an asterisk in the
			 * current line String[] list; list=name2.split(" ");//Divide the
			 * sentence into words i_final=list.length-1;
			 * 
			 * int z1,z2; z1=0; z2=0;
			 * 
			 * // count number of optional words for (int z=0;z<=i_final;z++){
			 * if(list[z].indexOf("*")>=0){ z1=z1+1; } else{ z2=z2+1; } }
			 * System.out.println("z1= " + z1 + "		z2 = " + z2 ); String
			 * final_list[]=new String[(int) Math.pow(2, z1)]; for (int
			 * cnt=0;cnt!=(int) ((Math.pow(2,z1)));cnt++){ final_list[cnt]=""; }
			 * // Generate all the possible combinations of the sentence
			 * final_list=Generate_combinations(list,z1); for (int z3=0;
			 * z3!=(int) ((Math.pow(2,z1)));z3++){
			 * 
			 * name=final_list[z3]; name=name.trim();
			 */
			
			name = name.replace("*", ""); // Remove *
			name = name.replace(" ", "_"); // Replace spaces by "_"

			// Create the user destination Move, set the variable and connect it
			// to the semantic and the Agendas
			Move destMove = factory.createMove("user_Dest_" + name);
			destMove.setVariableOperator("SET(" + varDest + "=" + name + ")");
			destMove.addSemantic(semGroupDest);
			agendaDest.addHas(destMove);
			agendaOpen.addHas(destMove);
			//adding the grammar to the move
			addGrammarToMove(destMove, g1);
			agendaImplicitDestAfterDepPlace.addHas(destMove);
			procCondDestAfterDepPlace.addProcessConditions(destMove);


			// Create the user departure Move, set the variable and connect it
			// to the semantic and the Agendas
			Move depMove = factory.createMove("user_Dep_" + name);
			depMove.setVariableOperator("SET(" + varDepPlace + "=" + name + ")");
			depMove.addSemantic(semGroupDepPlace);
			agendaDepPlace.addHas(depMove);
			agendaOpen.addHas(depMove);
			//adding the grammar to the move
			addGrammarToMove(depMove,g2);
			agendaImplicitDepPlaceAfterBusRoute.addHas(depMove);
			agendaImplicitDepPlaceAfterDest.addHas(depMove);
			procCondDepPlaceAfterBusRoute.addProcessConditions(depMove);
			procCondDepPlaceAfterDestination.addProcessConditions(depMove);

		}

		fileScanner.close();

		// 2) Generating Moves for uncovered_places
		File allPlaces_not_covered = new File(allPlacesNotCoveredFile);

		Scanner fileScanner3 = new Scanner(allPlaces_not_covered);

		while (fileScanner3.hasNextLine() == true) {
			String line = fileScanner3.nextLine();
			line = line.trim();// Remove spaces at the beginning and end of the
								// line
			System.out.println(line);
			String name = line.substring(1, line.length() - 1);// Remove the
																// brackets from
																// both sides
			
			//creating the grammars and adding general grammars i.e. the prefixes
			Grammar g3 = importGrammarDest(factory, name);
			g3.addGeneralGrammar(preDest);
			Grammar g4 = importGrammarDep(factory, name);
			g4.addGeneralGrammar(preDepPlace);

			
			name = name.replace("*", "");
			name = name.replace(" ", "_");

			// Create the user destination Move, set the variable and connect it
			// to the semantic and the Agendas
			Move destMove = factory.createMove("user_Dest_" + name);
			destMove.setVariableOperator("SET(" + varDest + "=" + name + ")");
			destMove.addSemantic(semGroupDest);
			agendaDest.addHas(destMove);
			agendaOpen.addHas(destMove);
			//adding the grammar to the move
			addGrammarToMove(destMove, g3);
			agendaImplicitDestAfterDepPlace.addHas(destMove);
			procCondDestAfterDepPlace.addProcessConditions(destMove);


			// Create the user departure Move, set the variable and connect it
			// to the semantic and the Agendas
			Move depMove = factory.createMove("user_Dep_" + name);
			depMove.setVariableOperator("SET(" + varDepPlace + "=" + name + ")");
			depMove.addSemantic(semGroupDepPlace);
			agendaDepPlace.addHas(depMove);
			agendaOpen.addHas(depMove);
			//adding the grammar to the move
			addGrammarToMove(depMove,g4);

			agendaImplicitDepPlaceAfterBusRoute.addHas(depMove);
			agendaImplicitDepPlaceAfterDest.addHas(depMove);
			procCondDepPlaceAfterBusRoute.addProcessConditions(depMove);
			procCondDepPlaceAfterDestination.addProcessConditions(depMove);

		}

		fileScanner3.close();

		// 3) Generating Moves for covered_bus_routes
		File bus_routes_covered = new File(busRoutesNotCoveredFile);
		Scanner fileScanner1 = new Scanner(bus_routes_covered);

		while (fileScanner1.hasNextLine() == true) {
			String line = fileScanner1.nextLine();
			line = line.trim();
			System.out.println(line);
			String name = line.substring(1, line.length() - 1);

			// Create the user bus route Move, set the variable and connect it
			// to the semantic and the Agendas
			Move m3 = factory.createMove("user_Bus_" + name);
			m3.setVariableOperator("SET(" + varBusRoute + "=" + name + ")");
			m3.addSemantic(semGroupBusRoute);
			agendaBusRoute.addHas(m3);
			agendaOpen.addHas(m3);

			//creating the grammars and adding general grammars i.e. the prefixes
			Grammar g1 = importGrammarBus(factory, name);
			addGrammarToMove(m3, g1);
			g1.addGeneralGrammar(preBus);
			
	
				

		}
		fileScanner1.close();

		// 4) Generating Moves for uncovered_bus_routes
		File bus_routes_not_covered = new File(busRoutesCoveredFile);
		Scanner fileScanner4 = new Scanner(bus_routes_not_covered);

		while (fileScanner4.hasNextLine() == true) {
			String line = fileScanner4.nextLine();
			line = line.trim();
			System.out.println(line);
			String name = line.substring(1, line.length() - 1);

			// Create the user bus route Move, set the variable and connect it
			// to the semantic and the Agendas
			Move m3 = factory.createMove("user_Bus_" + name);
			m3.setVariableOperator("SET(" + varBusRoute + "=" + name + ")");
			m3.addSemantic(semGroupBusRoute);
			agendaBusRoute.addHas(m3);
			agendaOpen.addHas(m3);

			//creating the grammars and adding general grammars i.e. the prefixes
			Grammar g2 = importGrammarBus(factory, name);
			addGrammarToMove(m3, g2);
			g2.addGeneralGrammar(preBus);
	


		}
		
		
		
		fileScanner4.close();
/*
		// Generate UserMove for the Time, Time is expressed as a 4 digit number
		int t1, t2;
		String time, time2, time_final;
		// The loop covers all possible values for the hours
		for (t1 = 0; t1 != 24; t1++) {
			time = Integer.toString(t1); // Convert integer to string

			// add a "0", if hours are expressed by one digit only
			if (time.length() == 1) {
				time = "0" + time;
			}

			// The loop covers all possible values for the minutes
			for (t2 = 0; t2 != 60; t2++) {
				time2 = Integer.toString(t2);
				if (time2.length() == 1) {
					time2 = "0" + time2;
				}

				// combine hours with minutes
				time_final = time + time2;

				// Create the user Time Move, set the variable and connect it to
				// the semantic and the Agendas
				Move m1 = factory.createMove("user_Time_" + time_final);
				m1.setVariableOperator("SET(" + varTime + "=" + time_final + ")");
				m1.addSemantic(semGroupTime);
				agendaTime.addHas(m1);
//				openAgenda.addHas(m1);

				System.out.println("time_final = " + time_final);
			}
		}
*/		

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
