//dividing places to covered and uncovered

package owlSpeak.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLLiteral;
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
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Reward;





//TODO check folders of files to be created
public class AdaptiveLetsGoGrXML {

	
	static boolean withVars = false;
	
	
	//grammar Strings
	public static String bus = new String ("<gr_BusLine>");
	public static String dest = new String ("<gr_Dest>");
	public static String dep = new String ("<gr_DepPlace>");
	public static String time = new String ("<gr_Time>");	

// Generates the grammars for all the bus stops directly from the files
public static Grammar grBusStops (OSFactory factory){

	String allPlacesCut = AdaptiveLetsGoGrXML.class.getResource("all_places_cut.txt").getPath();

	Grammar grBusStops = factory.createGrammar("gr_BusStops");
	
	File allPlaces_cut = new File(allPlacesCut);
	Scanner fileScanner1;
	try {
		fileScanner1 = new Scanner(allPlaces_cut);

		
		while (fileScanner1.hasNextLine() == true) {
		String line = fileScanner1.nextLine(); // save the line in the string
		line = line.trim();// Remove spaces at the beginning and end of the
								// line

		String name = line.substring(1, line.length() - 1); // Remove the brackets from both sides
			
		name = name.replace("*", ""); // Remove *
		name = name.replace(" ", "_"); // Replace spaces by "_"

		grBusStops.addGrammarString(factory.factory.getOWLLiteral(name,"en"));

		}
	fileScanner1.close();


	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // Create the scanner
	
	return grBusStops;
	
}

public static Grammar grTime(OSFactory factory)
{
	
	Grammar grTime = factory.createGrammar("gr_Time");
	grTime.addGrammarString(factory.factory.getOWLLiteral("<GARBAGE> <gr_TimeValue> [o'clock] [(a m | pm ) [please]","en"));

	return grTime;
}

public static Grammar grTimeValue (OSFactory factory){
	
	Grammar grTimeValue = factory.createGrammar("gr_TimeValue");	
	
	for (int i = 1; i<13; i++){
		for (int j = 0; j<50; j=j+10){

						grTimeValue.addGrammarString(factory.factory.getOWLLiteral(String.valueOf(i) +"_" + String.valueOf(j),"en"));
						if (j==30){	grTimeValue.addGrammarString(factory.factory.getOWLLiteral("half_past_" + String.valueOf(i),"en")); }
						if (j==0){	grTimeValue.addGrammarString(factory.factory.getOWLLiteral(String.valueOf(i),"en")); }
						if (j>0){
							grTimeValue.addGrammarString(factory.factory.getOWLLiteral(String.valueOf(j) + "_past_" + String.valueOf(i),"en"));
						}
		}
	}
	grTimeValue.addGrammarString(factory.factory.getOWLLiteral("now", "en"));
	
	return grTimeValue;
}

// Generates the grammars for all the bus routes directly from the files
public static Grammar grBusRoutes (OSFactory factory){

	String busRoutes = AdaptiveLetsGoGrXML.class.getResource("bus_routes_cut.txt").getPath();
	Grammar grBusRoutes = factory.createGrammar("gr_BusRoutes");
	
	File bus_routes_covered = new File(busRoutes);
	try {
	Scanner fileScanner1 = new Scanner(bus_routes_covered); // Create the scanner
	
		while (fileScanner1.hasNextLine() == true) {
		String line = fileScanner1.nextLine(); // save the line in the string
		line = line.trim();// Remove spaces at the beginning and end of the
								// line
		String name = line.substring(1, line.length() - 1); // Remove the brackets from both sides
			
		name = name.replace("*", ""); // Remove *
		name = name.replace(" ", "_"); // Replace spaces by "_"

		grBusRoutes.addGrammarString(factory.factory.getOWLLiteral(name,"en"));

		}

	fileScanner1.close();

	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} // Create the scanner
	return grBusRoutes;
	
}
	
	//adds a grammar to a move
	public static void addGrammarToMove (Move move, Grammar grammar){
	
		move.setGrammar(null, grammar);

	}

	//creates grammar for bus routes
	public static Grammar grBusLine (OSFactory factory){
		
		Grammar grBusLine = factory.createGrammar("gr_BusLine");
		grBusLine.addGrammarString(factory.factory.getOWLLiteral("<GARBAGE> (bus | route | line | bus line) [number] <gr_BusRoutes>  [please]","en"));

		return grBusLine;
		
	}
	
	//creates grammar for departure places
	public static Grammar grDepPlace (OSFactory factory){
	
		Grammar grDepPlace = factory.createGrammar("gr_DepPlace");
		grDepPlace.addGrammarString(factory.factory.getOWLLiteral("<GARBAGE> (from | am at | departure [place]) <gr_BusStops>  [please]","en"));
		
		return grDepPlace;
	}
	
	//creates grammar for destinations
	public static Grammar grDest (OSFactory factory){

		Grammar grDest = factory.createGrammar("gr_Dest");
		grDest.addGrammarString(factory.factory.getOWLLiteral("<GARBAGE> (to | arrive at | destination | reach) <gr_BusStops>  [please]","en"));

		return grDest;
	}
	
	// create multi-slot grammars with all combinations
	public static Grammar grMulti (String name, OSFactory factory){

		
		Grammar grMulti = factory.createGrammar(name);
		
		if (name == "gr_MultiSlot4"){
			String grammarStringBus1 = bus+dest+dep+time;
			String grammarStringBus2 = bus+dep+dest+time;
			String grammarStringBus3 = bus+time+dest+dep;
			String grammarStringBus4 = bus+time+dep+dest;
			String grammarStringBus5 = bus+dest+time+dep;
			String grammarStringBus6 = bus+dep+time+dest;
	
			String grammarStringDest1 = dest+bus+dep+time;
			String grammarStringDest2 = dest+dep+bus+time;
			String grammarStringDest3 = dest+time+dep+bus;
			String grammarStringDest4 = dest+bus+time+dep;
			String grammarStringDest5 = dest+time+bus+dep;
			String grammarStringDest6 = dest+dep+time+bus;		
			
			String grammarStringDep1 = dep+bus+dest+time;
			String grammarStringDep2 = dep+dest+bus+time;
			String grammarStringDep3 = dep+time+dest+bus;
			String grammarStringDep4 = dep+bus+time+dest;
			String grammarStringDep5 = dep+time+bus+dest;
			String grammarStringDep6 = dep+dest+time+bus;	
			
			String grammarStringTime1 = time+bus+dest+dep;
			String grammarStringTime2 = time+dest+bus+dep;
			String grammarStringTime3 = time+dep+dest+bus;
			String grammarStringTime4 = time+bus+dep+dest;
			String grammarStringTime5 = time+dep+bus+dest;
			String grammarStringTime6 = time+dest+dep+bus;
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus2,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus3,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus4,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus5,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus6,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest2,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest3,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest4,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest5,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest6,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep2,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep3,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep4,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep5,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep6,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime2,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime3,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime4,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime5,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime6,"en"));
			
			
		}

		else if (name == "gr_MultiSlot3BusDestDep")
		{
			String grammarStringBus1 = bus+dest+dep;
			String grammarStringBus2 = bus+dep+dest;
	
			String grammarStringDest1 = dest+bus+dep;
			String grammarStringDest2 = dest+dep+bus;

			String grammarStringDep1 = dep+bus+dest;
			String grammarStringDep2 = dep+dest+bus;

			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus2,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest2,"en"));
		
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep2,"en"));
			
		}
		
		else if (name == "gr_MultiSlot3DepDestTime")
		{
			String grammarStringTime1 = time+dest+dep;
			String grammarStringTime2 = time+dep+dest;
	
			String grammarStringDest1 = dest+time+dep;
			String grammarStringDest2 = dest+dep+time;

			String grammarStringDep1 = dep+time+dest;
			String grammarStringDep2 = dep+dest+time;

			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime2,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest2,"en"));
		
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep2,"en"));
			
		}
		
		else if (name == "gr_MultiSlot3DepBusTime")
		{
			String grammarStringTime1 = time+bus+dep;
			String grammarStringTime2 = time+dep+bus;
	
			String grammarStringBus1 = bus+time+dep;
			String grammarStringBus2 = bus+dep+time;

			String grammarStringDep1 = dep+time+bus;
			String grammarStringDep2 = dep+bus+time;

			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime2,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus2,"en"));
		
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDep2,"en"));
			
		}
		
		else if (name == "gr_MultiSlot3DestBusTime")
		{
			String grammarStringTime1 = time+bus+dest;
			String grammarStringTime2 = time+dest+bus;
	
			String grammarStringBus1 = bus+time+dest;
			String grammarStringBus2 = bus+dest+time;

			String grammarStringDest1 = dest+time+bus;
			String grammarStringDest2 = dest+bus+time;

			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringTime2,"en"));
			
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringBus2,"en"));
		
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarStringDest2,"en"));
			
		}
		
		else if (name == "gr_MultiSlot2DepBus")
		{

			String grammarString1 = dep+bus;
			String grammarString2 = bus+dep;
		
	
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString2,"en"));
			
		}
		
		else if (name == "gr_MultiSlot2DepDest")
		{

			String grammarString1 = dep+dest;
			String grammarString2 = dest+dep;
		
	
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString2,"en"));
			
		}
		else if (name == "gr_MultiSlot2DestTime")
		{

			String grammarString1 = dest+time;
			String grammarString2 = time+dest;
		
	
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString2,"en"));
			
		}
		else if (name == "gr_MultiSlot2DestBus")
		{

			String grammarString1 = dest+bus;
			String grammarString2 = bus+dest;
		
	
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString2,"en"));
			
		}
				
		else if (name == "gr_MultiSlot2DepTime")
		{

			String grammarString1 = dep+time;
			String grammarString2 = time+dep;
		
	
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString2,"en"));
			
		}
		
		else if (name == "gr_MultiSlot2BusTime")
		{

			String grammarString1 = bus+time;
			String grammarString2 = time+bus;
		
	
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString1,"en"));
			grMulti.addGrammarString(factory.factory.getOWLLiteral(grammarString2,"en"));
			
		}
							
			
		
		return grMulti;
		
	}
	

	//adds corresponding utterance-string to system moves
	public static Utterance makeUtterance (OSFactory factory, String name){
		
		Utterance utterance_name = factory.createUtterance(name);
		String utterance_String = "";


		if (name.equals("utterance_Request_Open")){
			utterance_String = "What can I do for you?";
		}
		else if (name.equals("utterance_User_Open")){
			utterance_String = "Any more information?";
		}
		else if (name.equals("utterance_Not_Covered")){
			utterance_String = "Sorry, but I don't have any information on that.";
		}
		else if (name.equals("utterance_Exit_Move2")){
			utterance_String = "Here is your information: You want to leave with bus %Bus_Route_var% from %Departure_place_var% to %Destination_var% at %Time_var%. Thank you. Good bye.";
		}
		else if (name.equals("utterance_Exit_Move")){
			utterance_String = "Here is your information: You want to leave from %Departure_place_var% to %Destination_var% at %Time_var%. Thank you. Good bye.";
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
			utterance_String = "You want to take bus %Bus_route_var%?";
		}
		else if (name.equals("utterance_Confirm_Departure_Place")){
			utterance_String = "Are you leaving from %Departure_place_var%?";
		}
		else if (name.equals("utterance_Confirm_Destination")){
			utterance_String = "Do you want to go to %Destination_var%?";
		}
		else if (name.equals("utterance_Confirm_Time")){
			utterance_String = "Is the time you want to leave %Time_var%?";
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

		String gramString_Deny = "[<GARBAGE>] (No | wrong | false) [<GARBAGE>]";
		
		grammar_Deny.addGrammarString(factory.factory.getOWLLiteral(gramString_Deny,"en"));
		Grammar grammar_Affirm = factory.createGrammar("gr_Affirm");

		String gramString_Affirm = "[<GARBAGE>] (Yes | I am | I do | correct | right) [<GARBAGE>]";
		
		grammar_Affirm.addGrammarString(factory.factory.getOWLLiteral(gramString_Affirm,"en"));

		
		if ((move.toString().equals("user_Affirm_Bus_Route")) || (move.toString().equals("user_Affirm_Destination")) || (move.toString().equals("user_Affirm_Departure_Place")) || (move.toString().equals("user_Affirm_Time"))){
			move.setGrammar(null, grammar_Affirm);
		}
		else if ((move.toString().equals("user_Deny_Bus_Route"))|| (move.toString().equals("user_Deny_Destination")) || (move.toString().equals("user_Deny_Departure_Place")) || (move.toString().equals("user_Deny_Time"))){
			move.setGrammar(null, grammar_Deny);
		}
		else{
			
		}

		
	}
	
	
	public static void main(String[] argv) throws Exception {
		
		//Variable to set the type of dialogue
		//Adaptive 0, explicit 1, implicit 2
		
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
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


			// Dialogue Strategy

			/**
			 * 5 = Dialogue Strategy non-adaptive Mixed_initiative3<br/>
			 * 6 = Dialogue Strategy non-adaptive user-init3<br/>
			 * 7 = Dialogue Strategy non-adaptive System_initiative3<br/>
			 * 8 = Dialogue Strategy adaptive4<br/>
		    */
			int dialogueStrategy = 8;

			// create rewards
			Reward rewDefault = factory.createReward("rew_default");
			rewDefault.setRewardValue(-1);
			rewDefault.setSpecialReward("default_reward");
			Reward rewSuccess = factory.createReward("rew_success");
			rewSuccess.setRewardValue(20);
			rewSuccess.setSpecialReward("success_reward");
			Reward rewFailure = factory.createReward("rew_failure");
			rewFailure.setRewardValue(-100);
			rewFailure.setSpecialReward("abort_reward");

			// Generate SemanticsGroups
			SemanticGroup busSemG = factory.createSemanticGroup("BusLine_sem");
			busSemG.setFieldTotals(1000);

			SemanticGroup depSemG = factory
					.createSemanticGroup("DepPlace_sem");
			depSemG.setFieldTotals(1000);

			SemanticGroup destSemG = factory.createSemanticGroup("Dest_sem");
			destSemG.setFieldTotals(1000);

			SemanticGroup timeSemG = factory.createSemanticGroup("Time_sem");
			timeSemG.setFieldTotals(3600);

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
			depAgenda.addSummaryAgenda(request);
			request.addSummarizedAgenda(depAgenda);

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
			busAgenda.addSummaryAgenda(request);
			request.addSummarizedAgenda(busAgenda);

			// Time Agenda
			Agenda timeAgenda = factory.createAgenda("Time_agenda");
			timeAgenda.addMustnot(conTimeSem);
			timeAgenda.addMustnot(timeSemG);
			timeAgenda.setRole(timeAgenda.getRole(), "collection");
			timeAgenda.addSummaryAgenda(request);
			request.addSummarizedAgenda(timeAgenda);

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

			Agenda conBusAgenda = factory.createAgenda("Confirm_Bus_route_Agenda");
			conBusAgenda.addRequires(busSemG);
			conBusAgenda.addMustnot(conBusSem);
			conBusAgenda.setRole(conBusAgenda.getRole(), "confirmation");
			conBusAgenda.addSummaryAgenda(confirmation);
			confirmation.addSummarizedAgenda(conBusAgenda);

			Agenda conDestAgenda = factory
					.createAgenda("Confirm_Destination_Agenda");
			conDestAgenda.addRequires(destSemG);
			conDestAgenda.addMustnot(conDestSem);
			conDestAgenda.setRole(conDestAgenda.getRole(), "confirmation");
			conDestAgenda.addSummaryAgenda(confirmation);
			confirmation.addSummarizedAgenda(conDestAgenda);

			Agenda conDepAgenda = factory
					.createAgenda("Confirm_Departure_place_Agenda");
			conDepAgenda.addRequires(depSemG);
			conDepAgenda.addMustnot(conDepSem);
			conDepAgenda.setRole(conDepAgenda.getRole(), "confirmation");
			conDepAgenda.addSummaryAgenda(confirmation);
			confirmation.addSummarizedAgenda(conDepAgenda);

			Agenda conTimeAgenda = factory.createAgenda("Confirm_Time_Agenda");
			conTimeAgenda.addRequires(timeSemG);
			conTimeAgenda.addMustnot(conTimeSem);
			conTimeAgenda.setRole(conTimeAgenda.getRole(), "confirmation");
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
			exitAgenda.setRole(exitAgenda.getRole(), "collection");

			Agenda master = factory.createAgenda("masteragenda");
			master.setIsMasterBool(false, true);

		// Dialogue Strategy non-adaptive Mixed_initiative3
			if (dialogueStrategy == 5) {

				master.addNext(openAgenda);
				master.addNext(exitAgenda);

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


			Utterance destinationUtterance = factory
					.createUtterance("destination_Utterance");
			Utterance departure_place_Utterance = factory
					.createUtterance("departure_place_Utterance");
			Utterance timeUtterance = factory.createUtterance("time_Utterance");
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
			

			// Generate system moves
			Move sysmovReqBus = factory.createMove("sys_Request_Bus_Route");
			busAgenda.addHas(sysmovReqBus); // Adding the move to the corresponding Agenda
			getAndAddUtterance(factory,sysmovReqBus);
			Move sysmovConfirmBus = factory.createMove("sys_Confirm_Bus_Route");
			conBusAgenda.addHas(sysmovConfirmBus);
			getAndAddUtterance(factory,sysmovConfirmBus);
			Move sysmovConfirmDepPlace = factory.createMove("sys_Confirm_Departure_Place");
			conDepAgenda.addHas(sysmovConfirmDepPlace);
			getAndAddUtterance(factory,sysmovConfirmDepPlace);
			Move sysmovConfirmDest = factory.createMove("sys_Confirm_Destination");
			conDestAgenda.addHas(sysmovConfirmDest);
			getAndAddUtterance(factory,sysmovConfirmDest);
			Move sysmovConfirmTime = factory.createMove("sys_Confirm_Time");
			conTimeAgenda.addHas(sysmovConfirmTime);
			getAndAddUtterance(factory,sysmovConfirmTime);
			Move sysmovRequestOpen = factory.createMove("sys_Request_Open");
			getAndAddUtterance(factory,sysmovRequestOpen);
			Move sysmovUserOpen = factory.createMove("sys_User_Open");
			getAndAddUtterance(factory,sysmovUserOpen);
			openAgenda.addHas(sysmovRequestOpen);
			openAgenda_afterDepCon.addHas(sysmovUserOpen);
			openAgenda_afterDestCon.addHas(sysmovUserOpen);
			openAgenda_afterTimeCon.addHas(sysmovUserOpen);
			openAgenda_afterDepDestCon.addHas(sysmovUserOpen);
			openAgenda_afterTimeDepCon.addHas(sysmovUserOpen);
			openAgenda_afterTimeDestCon.addHas(sysmovUserOpen);
			openAgenda_afterBusCon.addHas(sysmovUserOpen);
			openAgenda_afterBusDepCon.addHas(sysmovUserOpen);
			openAgenda_afterBusDepDestCon.addHas(sysmovUserOpen);
			openAgenda_afterBusDestCon.addHas(sysmovUserOpen);
			openAgenda_afterBusTimeCon.addHas(sysmovUserOpen);
			openAgenda_afterBusTimeDepCon.addHas(sysmovUserOpen);
			openAgenda_afterBusTimeDestCon.addHas(sysmovUserOpen);
			getAndAddUtterance(factory,sysmovUserOpen);

			Move sysmovReqDepPlace = factory.createMove("sys_Request_Departure_Place");
			depAgenda.addHas(sysmovReqDepPlace);
			depAgendaWithoutDest.addHas(sysmovReqDepPlace);
			depAgendaWithoutTime.addHas(sysmovReqDepPlace);
			depAgendaAll.addHas(sysmovReqDepPlace);
			depAgendaWithoutTimeDest.addHas(sysmovReqDepPlace);
			depAgendaWithoutBus.addHas(sysmovReqDepPlace);
			depAgendaWithoutBusDest.addHas(sysmovReqDepPlace);
			depAgendaWithoutBusTime.addHas(sysmovReqDepPlace);
			depAgendaWithoutBusTimeDest.addHas(sysmovReqDepPlace);
			getAndAddUtterance(factory,sysmovReqDepPlace);
			Move sysmovReqTime = factory.createMove("sys_Request_Time");
			timeAgenda.addHas(sysmovReqTime);
			timeAgendaWithoutDep.addHas(sysmovReqTime);
			timeAgendaWithoutDest.addHas(sysmovReqTime);
			timeAgendaAll.addHas(sysmovReqTime);
			timeAgendaWithoutDepDest.addHas(sysmovReqTime);
			timeAgendaWithoutBus.addHas(sysmovReqTime);
			timeAgendaWithoutBusDep.addHas(sysmovReqTime);
			timeAgendaWithoutBusDepDest.addHas(sysmovReqTime);
			timeAgendaWithoutBusDest.addHas(sysmovReqTime);
			getAndAddUtterance(factory,sysmovReqTime);
			Move sysmovReqDest = factory.createMove("sys_Request_Destination");
			destAgenda.addHas(sysmovReqDest);
			destAgendaWithoutDep.addHas(sysmovReqDest);
			destAgendaWithoutTime.addHas(sysmovReqDest);
			destAgendaAll.addHas(sysmovReqDest);
			destAgendaWithoutTimeDep.addHas(sysmovReqDest);
			destAgendaWithoutBus.addHas(sysmovReqDest);
			destAgendaWithoutBusDep.addHas(sysmovReqDest);
			destAgendaWithoutBusTime.addHas(sysmovReqDest);
			destAgendaWithoutBusTimeDep.addHas(sysmovReqDest);
			getAndAddUtterance(factory,sysmovReqDest);
			Move sysmovNotCovered = factory.createMove("sys_Not_covered");
			getAndAddUtterance(factory,sysmovNotCovered);
			Move exitMove = factory.createMove("sys_Exit_Move");
			exitAgenda.addHas(exitMove);
			exitMove.setIsExitMove(false, true);
			getAndAddUtterance(factory, exitMove);

			// Generate grammars
			
			Grammar busStopGrammar = grBusStops(factory);
			Grammar busRoutesGrammar = grBusRoutes(factory);
			Grammar timeValueGrammar = grTimeValue(factory);
			
			
			
			Move destMove = factory.createMove("external_destination_mv");
			destMove.setVariableOperator("SET(" + destVar
					+ "=:external_destination_mv:)");
			destMove.addSemantic(destSemG);
			destAgenda.addHas(destMove);
			destAgendaWithoutDep.addHas(destMove);
			destAgendaWithoutTime.addHas(destMove);
			destAgendaWithoutBus.addHas(destMove);
			destAgendaWithoutBusDep.addHas(destMove);
			destAgendaWithoutBusTime.addHas(destMove);
			destAgendaWithoutBusTimeDep.addHas(destMove);
			depAgendaWithoutTime.addHas(destMove);
			depAgendaWithoutBus.addHas(destMove);
			depAgendaWithoutBusTime.addHas(destMove);
			timeAgendaWithoutDep.addHas(destMove);
			timeAgendaWithoutBus.addHas(destMove);
			timeAgendaWithoutBusDep.addHas(destMove);
			destAgendaWithoutTimeDep.addHas(destMove);

			depAgendaAll.addHas(destMove);
			destAgendaAll.addHas(destMove);
			timeAgendaAll.addHas(destMove);
			openAgenda.addHas(destMove);
			openAgenda_afterDepCon.addHas(destMove);
			openAgenda_afterTimeCon.addHas(destMove);
			openAgenda_afterTimeDepCon.addHas(destMove);
			openAgenda_afterBusCon.addHas(destMove);
			openAgenda_afterBusDepCon.addHas(destMove);
			openAgenda_afterBusTimeCon.addHas(destMove);
			openAgenda_afterBusTimeDepCon.addHas(destMove);

			Grammar destGrammar = grDest(factory);
			destGrammar.addGeneralGrammar(busStopGrammar);
			addGrammarToMove(destMove, destGrammar);
			destMove.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			// Create the user departure Move, set the variable and connect it
			// to the semantic and the Agendas
			Move depMove = factory.createMove("external_depplace_mv");
			depMove.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:)");
			depMove.addSemantic(depSemG);
			Grammar depGrammar = grDepPlace(factory);
			depGrammar.addGeneralGrammar(busStopGrammar);

			addGrammarToMove(depMove,depGrammar);
			depMove.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			depAgenda.addHas(depMove);
			depAgendaWithoutDest.addHas(depMove);
			depAgendaWithoutTime.addHas(depMove);
			depAgendaWithoutBus.addHas(depMove);
			depAgendaWithoutBusDest.addHas(depMove);
			depAgendaWithoutBusTime.addHas(depMove);
			depAgendaWithoutBusTimeDest.addHas(depMove);
			destAgendaWithoutTime.addHas(depMove);
			destAgendaWithoutBus.addHas(depMove);
			destAgendaWithoutBusTime.addHas(depMove);
			timeAgendaWithoutDest.addHas(depMove);
			timeAgendaWithoutBus.addHas(depMove);
			timeAgendaWithoutBusDest.addHas(depMove);
			depAgendaWithoutTimeDest.addHas(depMove);
			depAgendaAll.addHas(depMove);
			destAgendaAll.addHas(depMove);
			timeAgendaAll.addHas(depMove);
			openAgenda.addHas(depMove);
			openAgenda_afterDestCon.addHas(depMove);
			openAgenda_afterTimeCon.addHas(depMove);
			openAgenda_afterTimeDestCon.addHas(depMove);
			openAgenda_afterBusCon.addHas(depMove);
			openAgenda_afterBusDestCon.addHas(depMove);
			openAgenda_afterBusTimeCon.addHas(depMove);
			openAgenda_afterBusTimeDestCon.addHas(depMove);

			// Create the user bus route Move, set the variable and connect it
			// to the semantic and the Agendas
			Move busMove = factory.createMove("external_busnumber_mv");
			busMove.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:)");
			busMove.addSemantic(busSemG);
			busAgenda.addHas(busMove);
			depAgendaWithoutDest.addHas(busMove);
			depAgendaWithoutTime.addHas(busMove);
			destAgendaWithoutDep.addHas(busMove);
			destAgendaWithoutTime.addHas(busMove);
			timeAgendaWithoutDep.addHas(busMove);
			timeAgendaWithoutDest.addHas(busMove);
			depAgendaWithoutTimeDest.addHas(busMove);
			destAgendaWithoutTimeDep.addHas(busMove);
			timeAgendaWithoutDepDest.addHas(busMove);

			depAgendaAll.addHas(busMove);
			destAgendaAll.addHas(busMove);
			timeAgendaAll.addHas(busMove);

			openAgenda.addHas(busMove);
			openAgenda_afterTimeCon.addHas(busMove);
			openAgenda_afterTimeDestCon.addHas(busMove);
			openAgenda_afterDestCon.addHas(busMove);
			openAgenda_afterDepCon.addHas(busMove);
			openAgenda_afterDepDestCon.addHas(busMove);
			openAgenda_afterTimeDepCon.addHas(busMove);
			Grammar busGrammar = grBusLine(factory);
			busGrammar.addGeneralGrammar(busRoutesGrammar);

			addGrammarToMove(busMove, busGrammar);
			busMove.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovTime = factory.createMove("external_time_mv");
			usermovTime.setVariableOperator("SET(" + timeVar + "=:external_time_mv:)");
			usermovTime.addSemantic(timeSemG);
			timeAgenda.addHas(usermovTime);
			timeAgendaWithoutDep.addHas(usermovTime);
			timeAgendaWithoutDest.addHas(usermovTime);
			timeAgendaWithoutBus.addHas(usermovTime);
			timeAgendaWithoutBusDep.addHas(usermovTime);
			timeAgendaWithoutBusDest.addHas(usermovTime);
			timeAgendaWithoutBusDepDest.addHas(usermovTime);
			depAgendaWithoutDest.addHas(usermovTime);
			depAgendaWithoutBus.addHas(usermovTime);
			depAgendaWithoutBusDest.addHas(usermovTime);
			destAgendaWithoutDep.addHas(usermovTime);
			destAgendaWithoutBus.addHas(usermovTime);
			destAgendaWithoutBusDep.addHas(usermovTime);
			timeAgendaWithoutDepDest.addHas(usermovTime);
			depAgendaAll.addHas(usermovTime);
			destAgendaAll.addHas(usermovTime);
			timeAgendaAll.addHas(usermovTime);
			

			openAgenda.addHas(usermovTime);
			openAgenda_afterDestCon.addHas(usermovTime);
			openAgenda_afterDepCon.addHas(usermovTime);
			openAgenda_afterDepDestCon.addHas(usermovTime);
			openAgenda_afterBusCon.addHas(usermovTime);
			openAgenda_afterBusDepCon.addHas(usermovTime);
			openAgenda_afterBusDepDestCon.addHas(usermovTime);
			openAgenda_afterBusDestCon.addHas(usermovTime);

			Grammar timeGrammar = grTime(factory);
			usermovTime.setGrammar(null, timeGrammar);
			timeGrammar.addGeneralGrammar(timeValueGrammar);
			usermovTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);


			
			// Create multi-slot moves

			Move usermovMulti4 = factory.createMove("multi_slot_4_mv");
			usermovMulti4.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
					+ depVar + "=:external_depplace_mv:;" + destVar
					+ "=:external_destination_mv:;" + timeVar
					+ "=:external_time_mv:)");
			usermovMulti4.addSemantic(timeSemG);
			usermovMulti4.addSemantic(destSemG);
			usermovMulti4.addSemantic(depSemG);
			usermovMulti4.addSemantic(busSemG);
			openAgenda.addHas(usermovMulti4);

			depAgendaAll.addHas(usermovMulti4);
			destAgendaAll.addHas(usermovMulti4);
			timeAgendaAll.addHas(usermovMulti4);
			
			
			Grammar multi4Grammar = grMulti("gr_MultiSlot4", factory);
			multi4Grammar.addGeneralGrammar(busStopGrammar);
			multi4Grammar.addGeneralGrammar(busRoutesGrammar);
			
			usermovMulti4.setGrammar(null, multi4Grammar);
			
			usermovMulti4.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti3BusDestDep = factory.createMove("multi_slot3_BusDestDep_mv");
			usermovMulti3BusDestDep.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
					+ depVar + "=:external_depplace_mv:;" + destVar
					+ "=:external_destination_mv:)");

			usermovMulti3BusDestDep.addSemantic(destSemG);
			usermovMulti3BusDestDep.addSemantic(depSemG);
			usermovMulti3BusDestDep.addSemantic(busSemG);
			depAgendaWithoutTime.addHas(usermovMulti3BusDestDep);
			destAgendaWithoutTime.addHas(usermovMulti3BusDestDep);

			openAgenda.addHas(usermovMulti3BusDestDep);
			openAgenda_afterTimeCon.addHas(usermovMulti3BusDestDep);
			
			Grammar multi3BusDestDepGrammar = grMulti("gr_MultiSlot3BusDestDep", factory);
			multi3BusDestDepGrammar.addGeneralGrammar(busRoutesGrammar);
			multi3BusDestDepGrammar.addGeneralGrammar(busStopGrammar);			
			
			usermovMulti3BusDestDep.setGrammar(null, multi3BusDestDepGrammar);
			usermovMulti3BusDestDep.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti3DepDestTime = factory.createMove("multi_slot3_DepDestTime_mv");
			usermovMulti3DepDestTime.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
					+ destVar + "=:external_destination_mv:;" + timeVar
					+ "=:external_time_mv:)");

			usermovMulti3DepDestTime.addSemantic(destSemG);
			usermovMulti3DepDestTime.addSemantic(depSemG);
			usermovMulti3DepDestTime.addSemantic(timeSemG);
			openAgenda.addHas(usermovMulti3DepDestTime);
			openAgenda_afterBusCon.addHas(usermovMulti3DepDestTime);
			depAgendaAll.addHas(usermovMulti3DepDestTime);
			destAgendaAll.addHas(usermovMulti3DepDestTime);
			timeAgendaAll.addHas(usermovMulti3DepDestTime);
			depAgendaWithoutBus.addHas(usermovMulti3DepDestTime);
			destAgendaWithoutBus.addHas(usermovMulti3DepDestTime);
			timeAgendaWithoutBus.addHas(usermovMulti3DepDestTime);

			Grammar multi3DepDestTimeGrammar = grMulti("gr_MultiSlot3DepDestTime", factory);
			multi3DepDestTimeGrammar.addGeneralGrammar(busRoutesGrammar);
			multi3DepDestTimeGrammar.addGeneralGrammar(busStopGrammar);			
		
			
			usermovMulti3DepDestTime.setGrammar(null, multi3DepDestTimeGrammar);
			usermovMulti3DepDestTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti3DepBusTime = factory.createMove("multi_slot3_DepBusTime_mv");
			usermovMulti3DepBusTime.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
					+ busVar + "=:external_busnumber_mv:;" + timeVar
					+ "=:external_time_mv:)");

			usermovMulti3DepBusTime.addSemantic(busSemG);
			usermovMulti3DepBusTime.addSemantic(depSemG);
			usermovMulti3DepBusTime.addSemantic(timeSemG);
			depAgendaAll.addHas(usermovMulti3DepBusTime);
			destAgendaAll.addHas(usermovMulti3DepBusTime);
			timeAgendaAll.addHas(usermovMulti3DepBusTime);
			timeAgendaWithoutDest.addHas(usermovMulti3DepBusTime);
			depAgendaWithoutDest.addHas(usermovMulti3DepBusTime);
			openAgenda.addHas(usermovMulti3DepBusTime);
			openAgenda_afterDestCon.addHas(usermovMulti3DepBusTime);
			
			Grammar multi3DepBusTimeGrammar = grMulti("gr_MultiSlot3DepBusTime", factory);
			multi3DepBusTimeGrammar.addGeneralGrammar(busRoutesGrammar);
			multi3DepBusTimeGrammar.addGeneralGrammar(busStopGrammar);			
						
			usermovMulti3DepBusTime.setGrammar(null, multi3DepBusTimeGrammar);
			usermovMulti3DepBusTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti3DestBusTime = factory.createMove("multi_slot3_DestBusTime_mv");
			usermovMulti3DestBusTime.setVariableOperator("SET(" + destVar
					+ "=:external_destination_mv:;" + busVar
					+ "=:external_busnumber_mv:;" + timeVar
					+ "=:external_time_mv:)");

			usermovMulti3DestBusTime.addSemantic(busSemG);
			usermovMulti3DestBusTime.addSemantic(destSemG);
			usermovMulti3DestBusTime.addSemantic(timeSemG);
			depAgendaAll.addHas(usermovMulti3DestBusTime);
			destAgendaAll.addHas(usermovMulti3DestBusTime);
			timeAgendaAll.addHas(usermovMulti3DestBusTime);
			destAgendaWithoutDep.addHas(usermovMulti3DestBusTime);
			timeAgendaWithoutDep.addHas(usermovMulti3DestBusTime);
			openAgenda.addHas(usermovMulti3DestBusTime);
			openAgenda_afterDepCon.addHas(usermovMulti3DestBusTime);
			
			Grammar multi3DestBusTimeGrammar = grMulti("gr_MultiSlot3DestBusTime", factory);
			multi3DestBusTimeGrammar.addGeneralGrammar(busRoutesGrammar);
			multi3DestBusTimeGrammar.addGeneralGrammar(busStopGrammar);						
			usermovMulti3DestBusTime.setGrammar(null, multi3DestBusTimeGrammar);
			usermovMulti3DestBusTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);


			
			Move usermovMulti2DepBus = factory.createMove("multi_slot2_DepBus_mv");
			usermovMulti2DepBus.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
					+ depVar + "=:external_depplace_mv:)");

			usermovMulti2DepBus.addSemantic(depSemG);
			usermovMulti2DepBus.addSemantic(busSemG);
			timeAgendaWithoutDest.addHas(usermovMulti2DepBus);
			destAgendaWithoutTime.addHas(usermovMulti2DepBus);
			openAgenda.addHas(usermovMulti2DepBus);
			openAgenda_afterDestCon.addHas(usermovMulti2DepBus);
			openAgenda_afterTimeCon.addHas(usermovMulti2DepBus);
			openAgenda_afterTimeDestCon.addHas(usermovMulti2DepBus);
			depAgendaAll.addHas(usermovMulti2DepBus);
			destAgendaAll.addHas(usermovMulti2DepBus);
			timeAgendaAll.addHas(usermovMulti2DepBus);
			depAgendaWithoutTimeDest.addHas(usermovMulti2DepBus);
			depAgendaWithoutDest.addHas(usermovMulti2DepBus);
			depAgendaWithoutTime.addHas(usermovMulti2DepBus);
			
			Grammar multi2DepBusGrammar = grMulti("gr_MultiSlot2DepBus", factory);
			multi2DepBusGrammar.addGeneralGrammar(busRoutesGrammar);
			multi2DepBusGrammar.addGeneralGrammar(busStopGrammar);						

			usermovMulti2DepBus.setGrammar(null, multi2DepBusGrammar);
			usermovMulti2DepBus.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti2DepDest = factory.createMove("multi_slot2_DepDest_mv");
			usermovMulti2DepDest.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
					+ destVar + "=:external_destination_mv:)");

			usermovMulti2DepDest.addSemantic(destSemG);
			usermovMulti2DepDest.addSemantic(depSemG);
			depAgendaWithoutTime.addHas(usermovMulti2DepDest);
			depAgendaWithoutBus.addHas(usermovMulti2DepDest);
			depAgendaWithoutBusTime.addHas(usermovMulti2DepDest);
			destAgendaWithoutTime.addHas(usermovMulti2DepDest);
			destAgendaWithoutBus.addHas(usermovMulti2DepDest);
			destAgendaWithoutBusTime.addHas(usermovMulti2DepDest);
			timeAgendaWithoutBus.addHas(usermovMulti2DepDest);
			openAgenda.addHas(usermovMulti2DepDest);
			depAgendaAll.addHas(usermovMulti2DepDest);
			destAgendaAll.addHas(usermovMulti2DepDest);
			timeAgendaAll.addHas(usermovMulti2DepDest);

			openAgenda_afterTimeCon.addHas(usermovMulti2DepDest);
			openAgenda_afterBusCon.addHas(usermovMulti2DepDest);
			openAgenda_afterBusTimeCon.addHas(usermovMulti2DepDest);
			
			Grammar multi2DepDestGrammar = grMulti("gr_MultiSlot2DepDest", factory);
			multi2DepDestGrammar.addGeneralGrammar(busRoutesGrammar);
			multi2DepDestGrammar.addGeneralGrammar(busStopGrammar);
			usermovMulti2DepDest.setGrammar(null, multi2DepDestGrammar);
			usermovMulti2DepDest.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti2DestTime = factory.createMove("multi_slot2_DestTime_mv");
			usermovMulti2DestTime.setVariableOperator("SET(" + destVar
					+ "=:external_destination_mv:;" + timeVar
					+ "=:external_time_mv:)");

			usermovMulti2DestTime.addSemantic(destSemG);
			usermovMulti2DestTime.addSemantic(timeSemG);
			destAgendaWithoutDep.addHas(usermovMulti2DestTime);
			destAgendaWithoutBus.addHas(usermovMulti2DestTime);
			destAgendaWithoutBusDep.addHas(usermovMulti2DestTime);
			timeAgendaWithoutDep.addHas(usermovMulti2DestTime);
			timeAgendaWithoutBus.addHas(usermovMulti2DestTime);
			timeAgendaWithoutBusDep.addHas(usermovMulti2DestTime);
			depAgendaWithoutBus.addHas(usermovMulti2DestTime);
			openAgenda.addHas(usermovMulti2DestTime);
			openAgenda_afterDepCon.addHas(usermovMulti2DestTime);
			openAgenda_afterBusCon.addHas(usermovMulti2DestTime);
			openAgenda_afterBusDepCon.addHas(usermovMulti2DestTime);
			depAgendaAll.addHas(usermovMulti2DestTime);
			destAgendaAll.addHas(usermovMulti2DestTime);
			timeAgendaAll.addHas(usermovMulti2DestTime);

			openAgenda_afterTimeDepCon.addHas(usermovMulti2DestTime);
			
			Grammar multi2DestTimeGrammar = grMulti("gr_MultiSlot2DestTime", factory);		
			multi2DestTimeGrammar.addGeneralGrammar(busRoutesGrammar);
			multi2DestTimeGrammar.addGeneralGrammar(busStopGrammar);
			
			usermovMulti2DestTime.setGrammar(null, multi2DestTimeGrammar);
			usermovMulti2DestTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti2DestBus = factory.createMove("multi_slot2_DestBus_mv");
			usermovMulti2DestBus.setVariableOperator("SET(" + destVar
					+ "=:external_destination_mv:;" + busVar
					+ "=:external_busnumber_mv:)");

			usermovMulti2DestBus.addSemantic(destSemG);
			usermovMulti2DestBus.addSemantic(busSemG);
			destAgendaWithoutDep.addHas(usermovMulti2DestBus);
			destAgendaWithoutTime.addHas(usermovMulti2DestBus);
			depAgendaWithoutTime.addHas(usermovMulti2DestBus);
			timeAgendaWithoutDep.addHas(usermovMulti2DestBus);
			openAgenda.addHas(usermovMulti2DestBus);
			openAgenda_afterDepCon.addHas(usermovMulti2DestBus);
			openAgenda_afterTimeCon.addHas(usermovMulti2DestBus);
			openAgenda_afterTimeDepCon.addHas(usermovMulti2DestBus);
			depAgendaAll.addHas(usermovMulti2DestBus);
			destAgendaAll.addHas(usermovMulti2DestBus);
			timeAgendaAll.addHas(usermovMulti2DestBus);
			destAgendaWithoutTimeDep.addHas(usermovMulti2DestBus);
			
			Grammar multi2DestBusGrammar = grMulti("gr_MultiSlot2DestBus", factory);		
			multi2DestBusGrammar.addGeneralGrammar(busRoutesGrammar);
			multi2DestBusGrammar.addGeneralGrammar(busStopGrammar);
					
			usermovMulti2DestBus.setGrammar(null, multi2DestBusGrammar);
			usermovMulti2DestBus.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti2DepTime = factory.createMove("multi_slot2_DepTime_mv");
			usermovMulti2DepTime.setVariableOperator("SET(" + depVar + "=:external_depplace_mv:;"
					+ timeVar + "=:external_time_mv:)");

			usermovMulti2DepTime.addSemantic(depSemG);
			usermovMulti2DepTime.addSemantic(timeSemG);
			depAgendaWithoutDest.addHas(usermovMulti2DepTime);
			depAgendaWithoutBus.addHas(usermovMulti2DepTime);
			depAgendaWithoutBusDest.addHas(usermovMulti2DepTime);
			timeAgendaWithoutDest.addHas(usermovMulti2DepTime);
			timeAgendaWithoutBus.addHas(usermovMulti2DepTime);
			timeAgendaWithoutBusDest.addHas(usermovMulti2DepTime);
			destAgendaWithoutBus.addHas(usermovMulti2DepTime);
			openAgenda.addHas(usermovMulti2DepTime);
			openAgenda_afterDestCon.addHas(usermovMulti2DepTime);
			openAgenda_afterBusCon.addHas(usermovMulti2DepTime);
			openAgenda_afterBusDestCon.addHas(usermovMulti2DepTime);
			depAgendaAll.addHas(usermovMulti2DepTime);
			destAgendaAll.addHas(usermovMulti2DepTime);
			timeAgendaAll.addHas(usermovMulti2DepTime);

			Grammar multi2DepTimeGrammar = grMulti("gr_MultiSlot2DepTime", factory);	
			multi2DepTimeGrammar.addGeneralGrammar(busRoutesGrammar);
			multi2DepTimeGrammar.addGeneralGrammar(busStopGrammar);
			
			usermovMulti2DepTime.setGrammar(null, multi2DepTimeGrammar);
			usermovMulti2DepTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move usermovMulti2BusTime = factory.createMove("multi_slot2_BusTime_mv");
			usermovMulti2BusTime.setVariableOperator("SET(" + busVar + "=:external_busnumber_mv:;"
					+ timeVar + "=:external_time_mv:)");

			usermovMulti2BusTime.addSemantic(busSemG);
			usermovMulti2BusTime.addSemantic(timeSemG);
			timeAgendaWithoutDep.addHas(usermovMulti2BusTime);
			timeAgendaWithoutDest.addHas(usermovMulti2BusTime);
			depAgendaWithoutDest.addHas(usermovMulti2BusTime);
			destAgendaWithoutDep.addHas(usermovMulti2BusTime);
			openAgenda_afterDestCon.addHas(usermovMulti2BusTime);
			openAgenda_afterDepCon.addHas(usermovMulti2BusTime);
			openAgenda_afterDepDestCon.addHas(usermovMulti2BusTime);
			openAgenda.addHas(usermovMulti2BusTime);
			depAgendaAll.addHas(usermovMulti2BusTime);
			destAgendaAll.addHas(usermovMulti2BusTime);
			timeAgendaAll.addHas(usermovMulti2BusTime);
			timeAgendaWithoutDepDest.addHas(usermovMulti2BusTime);
			
			Grammar multi2BusTimeGrammar = grMulti("gr_MultiSlot2BusTime", factory);	
			multi2BusTimeGrammar.addGeneralGrammar(busRoutesGrammar);
			multi2BusTimeGrammar.addGeneralGrammar(busStopGrammar);
			
			usermovMulti2BusTime.setGrammar(null, multi2BusTimeGrammar);
			usermovMulti2BusTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
		
			
			// Generate Confirmation user Moves, connecting them to
			// Semantics and Agendas

			Move moveUserAffirmBusRoute = factory.createMove("user_Affirm_Bus_Route");

			moveUserAffirmBusRoute.addSemantic(conBusSem);
			conBusAgenda.addHas(moveUserAffirmBusRoute);
			getAndAddGrammars(factory,moveUserAffirmBusRoute);
			moveUserAffirmBusRoute.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move moveUserAffirmDest = factory.createMove("user_Affirm_Destination");

			moveUserAffirmDest.addSemantic(conDestSem);
			conDestAgenda.addHas(moveUserAffirmDest);
			getAndAddGrammars(factory,moveUserAffirmDest);
			moveUserAffirmDest.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);


			Move moveUserAffirmDepPlace = factory.createMove("user_Affirm_Departure_Place");
			moveUserAffirmDepPlace.addSemantic(conDepSem);
			conDepAgenda.addHas(moveUserAffirmDepPlace);
			getAndAddGrammars(factory,moveUserAffirmDepPlace);
			moveUserAffirmDepPlace.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move moveUserAffirmTime = factory.createMove("user_Affirm_Time");
			moveUserAffirmTime.addSemantic(conTimeSem);
			conTimeAgenda.addHas(moveUserAffirmTime);
			getAndAddGrammars(factory,moveUserAffirmTime);
			moveUserAffirmTime.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);

			Move moveUserDenyBusRoute = factory.createMove("user_Deny_Bus_Route");
			busAgenda.addHas(moveUserDenyBusRoute);
			moveUserDenyBusRoute.addContrarySemantic(busSemG);
			conBusAgenda.addHas(moveUserDenyBusRoute);
			getAndAddGrammars(factory,moveUserDenyBusRoute);

			Move moveUserDenyDest = factory.createMove("user_Deny_Destination");
			moveUserDenyDest.addContrarySemantic(destSemG);
			conDestAgenda.addHas(moveUserDenyDest);
			getAndAddGrammars(factory,moveUserDenyDest);

			Move moveUserDenyDepPlace = factory.createMove("user_Deny_Departure_Place");
			moveUserDenyDepPlace.addContrarySemantic(depSemG);
			conDepAgenda.addHas(moveUserDenyDepPlace);
			getAndAddGrammars(factory,moveUserDenyDepPlace);

			Move moveUserDenyTime = factory.createMove("user_Deny_Time");
			moveUserDenyTime.addContrarySemantic(timeSemG);
			conTimeAgenda.addHas(moveUserDenyTime);
			getAndAddGrammars(factory,moveUserDenyTime);
			

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
