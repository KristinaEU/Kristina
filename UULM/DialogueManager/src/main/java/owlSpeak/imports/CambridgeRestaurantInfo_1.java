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
import owlSpeak.Move.ExtractFieldMode;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Reward;


public class CambridgeRestaurantInfo_1 {
	
		//creates the grammars for the user moves to inform about the area
		public static Grammar importGrammarArea (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Area_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Area>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to inform about the food
		public static Grammar importGrammarFood (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Food_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Food>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to inform about the name
		public static Grammar importGrammarName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Name_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Name>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to inform about the pricerange
		public static Grammar importGrammarPricerange (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Pricerange_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Pricerange>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to ask for the address if the name is known
		public static Grammar importGrammarRequestAddressName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Address_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Address_Name>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to ask for the phone if the name is known
		public static Grammar importGrammarRequestPhoneName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Phone_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Phone_Name>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to ask for the postcode if the name is known
		public static Grammar importGrammarRequestPostcodeName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Postcode_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Postcode_Name>] "+name,"en"));
			return g;
		}
				
				
		//creates the grammars for the user moves to ask for the signature if the name is known
		public static Grammar importGrammarRequestSignatureName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Signature_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Signature_Name>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to ask for the area if the name is known
		public static Grammar importGrammarRequestAreaName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Area_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Area_Name>] "+name,"en"));
			return g;
		}
				
				
		//creates the grammars for the user moves to ask for the food if the name is known
		public static Grammar importGrammarRequestFoodName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Food_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Food_Name>] "+name,"en"));
			return g;
		}
				
				
		//creates the grammars for the user moves to ask for the pricerange if the name is known
		public static Grammar importGrammarRequestPricerangeName (OSFactory factory, String name) {
			String grammarName = name;
			grammarName = name.replace(" ", "_"); 
			Grammar g = factory.createGrammar("gr_Request_Pricerange_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("[<gr_Request_Pricerange_Name>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to correct the area
		public static Grammar importGrammarCorrectArea (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Correct_Area_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Deny> [<gr_Area>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to correct the food
		public static Grammar importGrammarCorrectFood (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Correct_Food_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Deny> [<gr_Food>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to correct the name
		public static Grammar importGrammarCorrectName (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Correct_Name_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Deny> [<gr_Name>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to correct the pricerange
		public static Grammar importGrammarCorrectPricerange (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Correct_Pricerange_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Deny> [<gr_Pricerange>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to affirm something and give the pricerange
		public static Grammar importGrammarAffirmAndGivePricerange (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Affirm_And_Give_Pricerange_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Affirm> [<gr_Pricerange>] "+name,"en"));
			return g;
		}
		
		
		//creates the grammars for the user moves to affirm something and give the area
		public static Grammar importGrammarAffirmAndGiveArea (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Affirm_And_Give_Area_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Affirm> [<gr_Area>] "+name,"en"));
			return g;
		}
				
				
		//creates the grammars for the user moves to affirm something and give the food
		public static Grammar importGrammarAffirmAndGiveFood (OSFactory factory, String name) {		
			String grammarName = name;
			grammarName = name.replace(" ", "_");
			Grammar g = factory.createGrammar("gr_Affirm_And_Give_Food_" + grammarName);
			g.addGrammarString(factory.factory.getOWLLiteral("<gr_Affirm> [<gr_Food>] "+name,"en"));
			return g;
		}
		
		
		//adds a grammar to a move
		public static void addGrammarToMove (Move move, Grammar grammar){
			move.setGrammar(null, grammar);
		}

		
		//creates prefix-grammar for area
		public static Grammar prefixGrammarArea (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Area");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] [[(I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna) [eat] [something]] in the]","en"));
			return g;
		}
		
		
		//creates prefix-grammar for food
		public static Grammar prefixGrammarFood (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Food");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] [((I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna) [eat] [something] [some]) | ((It | The food) (should | needs to | has to) be)]","en"));
			return g;
		}
		
		
		//creates prefix grammar for name
		public static Grammar prefixGrammarName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] [([(I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna)] [eat] [something] in the restaurant) | (The name [of the restaurant is]) | (Its name is)]","en"));
			return g;
		}
		
		
		//creates prefix grammar for pricerange
		public static Grammar prefixGrammarPricerange (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Pricerange");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] [((I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna) [eat] [something]) | ((It | The restaurant) (should | needs to | hast to) be)]","en"));
			return g;
		}
		
				
		//creates prefix grammar for asking for the address if you know the name
		public static Grammar prefixGrammarRequestAddressName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Address_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] (I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna | What is | What's) [have] the address of the restaurant","en"));
			return g;
		}
		
		
		//creates prefix grammar for asking for the phone if you know the name
		public static Grammar prefixGrammarRequestPhoneName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Phone_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] (I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna | What is | What's) [have] the phone number of the restaurant","en"));
			return g;
		}
		
		
		//creates prefix grammar for asking for the postcode if you know the name 
		public static Grammar prefixGrammarRequestPostcodeName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Postcode_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] (I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna | What is | What's) [have] the postcode of the restaurant","en"));
			return g;
		}
		
		
		//creates prefix grammar for for asking for the signature if you know the name
		public static Grammar prefixGrammarRequestSignatureName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Signature_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] [and] (I want [to] | I would like [to] | I'd like [to] | I need [to] | I wanna | What is | What's) [have] the signature of the restaurant","en"));
			return g;
		}
		
	
		//creates prefix grammar for asking for the area if you know the name
		public static Grammar prefixGrammarRequestAreaName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Area_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] In which area is the restaurant","en"));
			return g;
		}
		
		
		//creates prefix grammar for asking for the food if you know the name
		public static Grammar prefixGrammarRequestFoodName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Food_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] Which kind of food do I get in the restaurant","en"));
			return g;
		}
		
				
		//creates prefix grammar for asking for the pricerange if you know the name 
		public static Grammar prefixGrammarRequestPricerangeName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Pricerange_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("[Hello | Hi] (What's | What is) the pricerange of the restaurant","en"));
			return g;
		}
		
		
		//creates grammar for denying
		public static Grammar grammarDeny (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Deny");
			g.addGrammarString(factory.factory.getOWLLiteral("([No] [(that's | that is)] (wrong | No | False | not right | none)) | I don't | I do not | Nowhere | From Nowhere | Never | No (thanks | thank you)","en"));
			return g;
		}
		
		
		//creates grammar for affirming
		public static Grammar grammarAffirm (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Affirm");
			g.addGrammarString(factory.factory.getOWLLiteral("([Yes] [(That's | that is)] (correct| right)) | Yes | [Yes] I do | [Yes] please | [Yes] it is","en"));
			return g;
		}
		
		
		//creates grammar for saying Bye
		public static Grammar grammarBye (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Bye");
			g.addGrammarString(factory.factory.getOWLLiteral("[(Thanks | Thank you)] [Good] Bye","en"));
			return g;
		}
		
		
		//creates grammar for acknowledging
		public static Grammar grammarAck (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Ack");
			g.addGrammarString(factory.factory.getOWLLiteral("Okay","en"));
			return g;
		}
		
		
		//creates grammar for thanking
		public static Grammar grammarThankyou (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Thankyou");
			g.addGrammarString(factory.factory.getOWLLiteral("Thanks | Thank you","en"));
			return g;
		}
		
		
		//creates grammar for requesting an alternative
		public static Grammar grammarRequestAlternative (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Alternative");
			g.addGrammarString(factory.factory.getOWLLiteral("[Is there an] alternative","en")); 
			return g;
		}
		
		//creates grammar for asking for the area
			public static Grammar GrammarRequestArea (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Area");
			g.addGrammarString(factory.factory.getOWLLiteral("In which area is it","en"));
			return g;
		}
			
			
		//creates grammar for asking for the food
			public static Grammar GrammarRequestFood (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Food");
			g.addGrammarString(factory.factory.getOWLLiteral("Which kind of food do I get there","en"));
			return g;
		}
			
		
		//creates grammar for asking for the name
			public static Grammar GrammarRequestName (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Name");
			g.addGrammarString(factory.factory.getOWLLiteral("(What's | What is) the name [of this restaurant]","en"));
			return g;
		}
			
		
		//creates grammar for asking for the pricerange
			public static Grammar GrammarRequestPricerange (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Pricerange");
			g.addGrammarString(factory.factory.getOWLLiteral("(What's | What is) the pricerange [of this restaurant]","en"));
			return g;
		}
			
			
		//creates grammar for asking for the address
			public static Grammar GrammarRequestAddress (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Address");
			g.addGrammarString(factory.factory.getOWLLiteral("(What's | What is) the address [of this restaurant]","en"));
			return g;
		}
			
			
		//creates grammar for asking for the phone
			public static Grammar GrammarRequestPhone (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Phone");
			g.addGrammarString(factory.factory.getOWLLiteral("(What's | What is) the phone number [of this restaurant]","en"));
			return g;
		}
			
			
		//creates grammar for asking for the postcode
			public static Grammar GrammarRequestPostcode (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Postcode");
			g.addGrammarString(factory.factory.getOWLLiteral("(What's | What is) the postcode of this restaurant","en"));
			return g;
		}
			
			
		//creates grammar for asking for the signature
			public static Grammar GrammarRequestSignature (OSFactory factory){
			Grammar g = factory.createGrammar("gr_Request_Signature");
			g.addGrammarString(factory.factory.getOWLLiteral("(What's | What is) the signature of this restaurant","en"));
			return g;
		}
		
		
		//adds corresponding utterance-string to system moves
		public static Utterance makeUtterance (OSFactory factory, String name){
			Utterance utterance_name = factory.createUtterance(name);
			String utterance_String = "";
			if (name.equals("utterance_Open")){
				utterance_String = "Welcome to the Cambridge Restaurant Information System. What can I do for you?";
			}
			else if (name.equals("utterance_Bye")){
				utterance_String = "Thank you. Good bye.";
			}
			else if (name.equals("utterance_Abort")){
				utterance_String = "Sorry.";
			}
			else if (name.equals("utterance_Request_Area")){
				utterance_String = "In which area do you want to eat?";
			}
			else if (name.equals("utterance_Request_Food")){
				utterance_String = "What kind of food do you want?";
			}
			else if (name.equals("utterance_Request_Pricerange")){
				utterance_String = "What's your pricerange?";
			}
			else if (name.equals("utterance_Request_Name")){
				utterance_String = "What's the name of the restaurant?";
			}
			else if (name.equals("utterance_Request_More")){
				utterance_String = "Do you want more information?";
			}
			else if (name.equals("utterance_Confirm_Area")){
				utterance_String = "You want to eat something in the %var_Area%?";
			}
			else if (name.equals("utterance_Confirm_Food")){
				utterance_String = "You want to eat %var_Food%?";
			}
			else if (name.equals("utterance_Confirm_Name")){
				utterance_String = "The name of the restaurant is %var_Name%?";
			}
			else if (name.equals("utterance_Confirm_Pricerange")){
				utterance_String = "Your pricerange is %var_Pricerange%?";
			}
			else if (name.equals("utterance_Confirm_Pricerange_And_Request_Area")){
				utterance_String = "In which area should the %var_Pricerange% restaurant be?";
			}
			else if (name.equals("utterance_Confirm_Pricerange_And_Request_Food")){
				utterance_String = "What kind of food do you want in your %var_Pricerange% restaurant?";
			}
			else if (name.equals("utterance_Confirm_Area_And_Request_Food")){
				utterance_String = "What kind of food do you want in your restaurant in the %var_Area%?";
			}
			else if (name.equals("utterance_Confirm_Area_And_Request_Pricerange")){
				utterance_String = "What's your pricerange of the restaurant in the %var_Area%?";
			}
			else if (name.equals("utterance_Confirm_Food_And_Request_Pricerange")){
				utterance_String = "What's your pricerange of the %var_Food% restaurant?";
			}
			else if (name.equals("utterance_Confirm_Food_And_Request_Area")){
				utterance_String = "In which area should the %var_Food% restaurant be?";
			}
			else if (name.equals("utterance_Offer_Name")){
				utterance_String = "The restaurant %var_Name% is a suitable restaurant."; //dummy
			}
			else if (name.equals("utterance_Tell_Area")){
				utterance_String = "The restaurant %var_Name% is in the centre."; //dummy
			}
			else if (name.equals("utterance_Tell_Food")){
				utterance_String = "You get Italian food in the restaurant %var_Name%."; //dummy
			}
			else if (name.equals("utterance_Tell_Name")){
				utterance_String = "The name of the restaurant is %var_Name%."; //dummy
			}
			else if (name.equals("utterance_Tell_Pricerange")){
				utterance_String = "You get cheap food in the restaurant %var_Name%."; //dummy
			}
			else if (name.equals("utterance_Tell_Address")){
				utterance_String = "The address of the restaurant %var_Name% is 27 Baker Street."; //dummy
			}
			else if (name.equals("utterance_Tell_Phone")){
				utterance_String = "The phone number of the restaurant %var_Name% is 00441223458."; //dummy
			}
			else if (name.equals("utterance_Tell_Postcode")){
				utterance_String = "The postcode of the restaurant %var_Name% is CB5."; //dummy
			}
			else if (name.equals("utterance_Tell_Signature")){
				utterance_String = "The signature of the restaurant %var_Name% is creativity."; //dummy
			}
			else if (name.equals("utterance_Tell_Alternative")){
				utterance_String = "The restaurant Anatolia would be an alternative."; //dummy
			}
//			else if (name.equals("utterance_Tell_Area_Food")){
//				utterance_String = "The restaurant %var_Name% is in the centre and has italian food."; //dummy
//			}
//			else if (name.equals("utterance_Tell_Area_Pricerange")){
//				utterance_String = "The restaurant %var_Name% is in the centre and has cheap food."; //dummy
//			}
//			else if (name.equals("utterance_Tell_Food_Pricerange")){
//				utterance_String = "The restaurant %var_Name% has cheap italian food."; //dummy
//			}
//			else if (name.equals("utterance_Tell_Area_Food_Pricerange")){
//				utterance_String = "The restaurant %var_Name% is in the center and has cheap italian food."; //dummy
//			}
			else{
				utterance_String = "unknown utterance name";
			}
			utterance_name.addUtteranceString(factory.factory.getOWLLiteral(utterance_String,"en"));
			return utterance_name;
		}
	
		
		//changes the name-string to get corresponding utterance-name
		public static void getAndAddUtterance (OSFactory factory, Move move){
			String name = move.toString();
			name = name.replace("move_sys", "utterance");
			move.setUtterance(null, makeUtterance(factory, name));			
		}
		
				
		public static void main(String[] argv) throws Exception {
	        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
        @SuppressWarnings("unused")
		ServletEngine engine = new ServletEngine();
        String uriSave = Settings.uri;
        Settings.uri = "http://localhost:8083/OwlSpeakOnto.owl";
        String filename = "camRestInfoJuliana.owl";
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
        
        
		//create default reward
		Reward rewDefault = factory.createReward("rew_default");
		rewDefault.setRewardValue(-1);
		rewDefault.setSpecialReward("default_reward");
		
        
        //generate semantic groups with semantics and variables
		SemanticGroup semGroupArea = factory.createSemanticGroup("semGroup_Area");
		semGroupArea.setFieldTotals(5);
		Variable varArea = factory.createVariable("var_Area");
		semGroupArea.addVariable(varArea);
		varArea.addBelongsToSemantic(semGroupArea);
		Semantic semConfirmArea = factory.createSemantic("sem_ConfirmArea");
		semConfirmArea.setConfirmationInfo(true);
		semConfirmArea.addSemanticGroup(semGroupArea);
		semGroupArea.addContainedSemantic(semConfirmArea);
		Semantic semRejectArea = factory.createSemantic("sem_RejectArea");
		semRejectArea.setConfirmationInfo(false);
		semRejectArea.addSemanticGroup(semGroupArea);
		semGroupArea.addContainedSemantic(semRejectArea);
		
		SemanticGroup semGroupFood = factory.createSemanticGroup("semGroup_Food");
		semGroupFood.setFieldTotals(91);
		Variable varFood = factory.createVariable("var_Food");
		semGroupFood.addVariable(varFood);
		varFood.addBelongsToSemantic(semGroupFood);
		Semantic semConfirmFood = factory.createSemantic("sem_ConfirmFood");
		semConfirmFood.setConfirmationInfo(true);
		semConfirmFood.addSemanticGroup(semGroupFood);
		semGroupFood.addContainedSemantic(semConfirmFood);
		Semantic semRejectFood = factory.createSemantic("sem_RejectFood");
		semRejectFood.setConfirmationInfo(false);
		semRejectFood.addSemanticGroup(semGroupFood);
		semGroupFood.addContainedSemantic(semRejectFood);
		
		SemanticGroup semGroupName = factory.createSemanticGroup("semGroup_Name");
		semGroupName.setFieldTotals(113);
		Variable varName = factory.createVariable("var_Name");
		semGroupName.addVariable(varName);
		varName.addBelongsToSemantic(semGroupName);
		Semantic semConfirmName = factory.createSemantic("sem_ConfirmName");
		semConfirmName.setConfirmationInfo(true);
		semConfirmName.addSemanticGroup(semGroupName);
		semGroupName.addContainedSemantic(semConfirmName);
		Semantic semRejectName = factory.createSemantic("sem_RejectName");
		semRejectName.setConfirmationInfo(false);
		semRejectName.addSemanticGroup(semGroupName);
		semGroupName.addContainedSemantic(semRejectName);
		
		SemanticGroup semGroupPricerange = factory.createSemanticGroup("semGroup_Pricerange");
		semGroupPricerange.setFieldTotals(3);
		Variable varPricerange = factory.createVariable("var_Pricerange");
		semGroupPricerange.addVariable(varPricerange);
		varPricerange.addBelongsToSemantic(semGroupPricerange);
		Semantic semConfirmPricerange = factory.createSemantic("sem_ConfirmPricerange");
		semConfirmPricerange.setConfirmationInfo(true);
		semConfirmPricerange.addSemanticGroup(semGroupPricerange);
		semGroupPricerange.addContainedSemantic(semConfirmPricerange);
		Semantic semRejectPricerange = factory.createSemantic("sem_RejectPricerange");
		semRejectPricerange.setConfirmationInfo(false);
		semRejectPricerange.addSemanticGroup(semGroupPricerange);
		semGroupPricerange.addContainedSemantic(semRejectPricerange);
		
		SemanticGroup semGroupNeedArea = factory.createSemanticGroup("semGroup_NeedArea");
		semGroupNeedArea.setFieldTotals(1);
		Variable varNeedArea = factory.createVariable("var_NeedArea");
		semGroupNeedArea.addVariable(varNeedArea);
		varNeedArea.addBelongsToSemantic(semGroupNeedArea);
		
		SemanticGroup semGroupNeedFood = factory.createSemanticGroup("semGroup_NeedFood");
		semGroupNeedFood.setFieldTotals(1);
		Variable varNeedFood = factory.createVariable("var_NeedFood");
		semGroupNeedFood.addVariable(varNeedFood);
		varNeedFood.addBelongsToSemantic(semGroupNeedFood);
		
		SemanticGroup semGroupNeedName = factory.createSemanticGroup("semGroup_NeedName");
		semGroupNeedName.setFieldTotals(1);
		Variable varNeedName = factory.createVariable("var_NeedName");
		semGroupNeedName.addVariable(varNeedName);
		varNeedName.addBelongsToSemantic(semGroupNeedName);
		
		SemanticGroup semGroupNeedPricerange = factory.createSemanticGroup("semGroup_NeedPricerange");
		semGroupNeedPricerange.setFieldTotals(1);
		Variable varNeedPricerange = factory.createVariable("var_NeedPricerange");
		semGroupNeedPricerange.addVariable(varNeedPricerange);
		varNeedPricerange.addBelongsToSemantic(semGroupNeedPricerange);
		
		SemanticGroup semGroupNeedAddress = factory.createSemanticGroup("semGroup_NeedAddress");
		semGroupNeedAddress.setFieldTotals(1);
		Variable varNeedAddress = factory.createVariable("var_NeedAddress");
		semGroupNeedAddress.addVariable(varNeedAddress);
		varNeedAddress.addBelongsToSemantic(semGroupNeedAddress);
		
		SemanticGroup semGroupNeedPhone = factory.createSemanticGroup("semGroup_NeedPhone");
		semGroupNeedPhone.setFieldTotals(1);
		Variable varNeedPhone = factory.createVariable("var_NeedPhone");
		semGroupNeedPhone.addVariable(varNeedPhone);
		varNeedPhone.addBelongsToSemantic(semGroupNeedPhone);
		
		SemanticGroup semGroupNeedPostcode = factory.createSemanticGroup("semGroup_NeedPostcode");
		semGroupNeedPostcode.setFieldTotals(1);
		Variable varNeedPostcode = factory.createVariable("var_NeedPostcode");
		semGroupNeedPostcode.addVariable(varNeedPostcode);
		varNeedPostcode.addBelongsToSemantic(semGroupNeedPostcode);
		
		SemanticGroup semGroupNeedSignature = factory.createSemanticGroup("semGroup_NeedSignature");
		semGroupNeedSignature.setFieldTotals(1);
		Variable varNeedSignature = factory.createVariable("var_NeedSignature");
		semGroupNeedSignature.addVariable(varNeedSignature);
		varNeedSignature.addBelongsToSemantic(semGroupNeedSignature);
		
		SemanticGroup semGroupNeedAlternative = factory.createSemanticGroup("semGroup_NeedAlternative");
		semGroupNeedAlternative.setFieldTotals(1);
		Variable varNeedAlternative = factory.createVariable("var_NeedAlternative");
		semGroupNeedAlternative.addVariable(varNeedAlternative);
		varNeedAlternative.addBelongsToSemantic(semGroupNeedAlternative);
		
		SemanticGroup semGroupSaidArea = factory.createSemanticGroup("semGroup_SaidArea");
		semGroupSaidArea.setFieldTotals(1);
		Variable varSaidArea = factory.createVariable("var_SaidArea");
		semGroupSaidArea.addVariable(varSaidArea);
		varSaidArea.addBelongsToSemantic(semGroupSaidArea);
		
		SemanticGroup semGroupSaidFood = factory.createSemanticGroup("semGroup_SaidFood");
		semGroupSaidFood.setFieldTotals(1);
		Variable varSaidFood = factory.createVariable("var_SaidFood");
		semGroupSaidFood.addVariable(varSaidFood);
		varSaidFood.addBelongsToSemantic(semGroupSaidFood);
		
		SemanticGroup semGroupSaidName = factory.createSemanticGroup("semGroup_SaidName");
		semGroupSaidName.setFieldTotals(1);
		Variable varSaidName = factory.createVariable("var_SaidName");
		semGroupSaidName.addVariable(varSaidName);
		varSaidName.addBelongsToSemantic(semGroupSaidName);
		
		SemanticGroup semGroupSaidPricerange = factory.createSemanticGroup("semGroup_SaidPricerange");
		semGroupSaidPricerange.setFieldTotals(1);
		Variable varSaidPricerange = factory.createVariable("var_SaidPricerange");
		semGroupSaidPricerange.addVariable(varSaidPricerange);
		varSaidPricerange.addBelongsToSemantic(semGroupSaidPricerange);
		
		SemanticGroup semGroupSaidAddress = factory.createSemanticGroup("semGroup_SaidAddress");
		semGroupSaidAddress.setFieldTotals(1);
		Variable varSaidAddress = factory.createVariable("var_SaidAddress");
		semGroupSaidAddress.addVariable(varSaidAddress);
		varSaidAddress.addBelongsToSemantic(semGroupSaidAddress);
		
		SemanticGroup semGroupSaidPhone = factory.createSemanticGroup("semGroup_SaidPhone");
		semGroupSaidPhone.setFieldTotals(1);
		Variable varSaidPhone = factory.createVariable("var_SaidPhone");
		semGroupSaidPhone.addVariable(varSaidPhone);
		varSaidPhone.addBelongsToSemantic(semGroupSaidPhone);
		
		SemanticGroup semGroupSaidPostcode = factory.createSemanticGroup("semGroup_SaidPostcode");
		semGroupSaidPostcode.setFieldTotals(1);
		Variable varSaidPostcode = factory.createVariable("var_SaidPostcode");
		semGroupSaidPostcode.addVariable(varSaidPostcode);
		varSaidPostcode.addBelongsToSemantic(semGroupSaidPostcode);
		
		SemanticGroup semGroupSaidSignature = factory.createSemanticGroup("semGroup_SaidSignature");
		semGroupSaidSignature.setFieldTotals(1);
		Variable varSaidSignature = factory.createVariable("var_SaidSignature");
		semGroupSaidSignature.addVariable(varSaidSignature);
		varSaidSignature.addBelongsToSemantic(semGroupSaidSignature);
		
		SemanticGroup semGroupSaidAlternative = factory.createSemanticGroup("semGroup_SaidAlternative");
		semGroupSaidAlternative.setFieldTotals(1);
		Variable varSaidAlternative = factory.createVariable("var_SaidAlternative");
		semGroupSaidAlternative.addVariable(varSaidAlternative);
		varSaidAlternative.addBelongsToSemantic(semGroupSaidAlternative);
		
		SemanticGroup semGroupBye = factory.createSemanticGroup("semGroup_Bye");
		semGroupBye.setFieldTotals(1);
		Variable varSaidBye = factory.createVariable("var_SaidBye");
		semGroupBye.addVariable(varSaidBye);
		varSaidBye.addBelongsToSemantic(semGroupBye);
						
				
		//generate agendas
		Agenda agendaMaster = factory.createAgenda("Masteragenda");
		agendaMaster.setIsMasterBool(false, true);

		Agenda agendaOpen = factory.createAgenda("agenda_Open");
		agendaOpen.addMustnot(semConfirmArea);
		agendaOpen.addMustnot(semConfirmFood);
		agendaOpen.addMustnot(semConfirmName);
		agendaOpen.addMustnot(semConfirmPricerange);
		agendaOpen.addMustnot(semGroupArea);
		agendaOpen.addMustnot(semGroupFood);
		agendaOpen.addMustnot(semGroupName);
		agendaOpen.addMustnot(semGroupPricerange);
		agendaOpen.setRole(agendaOpen.getRole(), "collection"); //set the Role whether it is confirmation or collection
		agendaOpen.setPriority(0, 100);
		
		Agenda agendaRequestArea = factory.createAgenda("agenda_Request_Area");
		agendaRequestArea.addMustnot(semConfirmArea);
		agendaRequestArea.addMustnot(semConfirmName);
		agendaRequestArea.addMustnot(semGroupArea);
		agendaRequestArea.addMustnot(semGroupName);
		agendaRequestArea.setRole(agendaRequestArea.getRole(), "collection"); 
		agendaRequestArea.setPriority(0, 50); 
		
		Agenda agendaRequestFood = factory.createAgenda("agenda_Request_Food");
		agendaRequestFood.addMustnot(semConfirmFood); 
		agendaRequestFood.addMustnot(semConfirmName);
		agendaRequestFood.addMustnot(semGroupFood);
		agendaRequestFood.addMustnot(semGroupName);
		agendaRequestFood.setRole(agendaRequestFood.getRole(), "collection");
		agendaRequestFood.setPriority(0, 70);
		
		Agenda agendaRequestPricerange = factory.createAgenda("agenda_Request_Pricerange");
		agendaRequestPricerange.addMustnot(semConfirmPricerange); 
		agendaRequestPricerange.addMustnot(semConfirmName);
		agendaRequestPricerange.addMustnot(semGroupPricerange);
		agendaRequestPricerange.addMustnot(semGroupName);
		agendaRequestPricerange.setRole(agendaRequestPricerange.getRole(), "collection");
		agendaRequestPricerange.setPriority(0, 60);
		
		Agenda agendaRequestName = factory.createAgenda("agenda_Request_Name");
		agendaRequestName.addMustnot(semConfirmName);
		agendaRequestName.addMustnot(semConfirmArea);
		agendaRequestName.addMustnot(semConfirmFood);
		agendaRequestName.addMustnot(semConfirmPricerange);
		agendaRequestName.addMustnot(semGroupName);
		agendaRequestName.addMustnot(semGroupArea);
		agendaRequestName.addMustnot(semGroupFood);
		agendaRequestName.addMustnot(semGroupPricerange);
		agendaRequestName.setRole(agendaRequestName.getRole(), "collection");
		agendaRequestName.setPriority(0, 80);
		
		Agenda agendaRequestMore = factory.createAgenda("agenda_Request_More");
		agendaRequestMore.addRequires(semGroupName);
//		agendaRequestMore.addRequires(semConfirmName);
//		agendaRequestMore.setVariableOperator("", "REQUIRES(((((((((%var_NeedArea%==%var_SaidArea%) && "
//				+ "(%var_NeedFood%==%var_SaidFood%)) && (%var_NeedPricerange%==%var_SaidPricerange%)) && "
//				+ "(%var_NeedName%==%var_SaidName%)) && (%var_NeedAddress%==%var_SaidAddress%)) && "
//				+ "(%var_NeedPhone%==%var_SaidPhone%)) && (%var_NeedPostcode%==%var_SaidPostcode%)) && "
//				+ "(%var_NeedSignature%==%var_SaidSignature%)) && (%var_NeedAlternative%==%var_SaidAlternative%))");
		agendaRequestMore.setRole(agendaRequestMore.getRole(), "collection");
		agendaRequestMore.setPriority(0, 20);
				
		Agenda agendaConfirmArea = factory.createAgenda("agenda_Confirm_Area");
		agendaConfirmArea.addRequires(semGroupArea);
		agendaConfirmArea.addMustnot(semConfirmArea);
		agendaConfirmArea.setRole(agendaConfirmArea.getRole(), "confirmation");
		agendaConfirmArea.setPriority(0, 110);
		
		Agenda agendaConfirmFood = factory.createAgenda("agenda_Confirm_Food");
		agendaConfirmFood.addRequires(semGroupFood);
		agendaConfirmFood.addMustnot(semConfirmFood);
		agendaConfirmFood.setRole(agendaConfirmFood.getRole(), "confirmation");
		agendaConfirmFood.setPriority(0, 110);
		
		Agenda agendaConfirmName = factory.createAgenda("agenda_Confirm_Name");
		agendaConfirmName.addRequires(semGroupName);
		agendaConfirmName.addMustnot(semConfirmName);
		agendaConfirmName.setRole(agendaConfirmName.getRole(), "confirmation");
		agendaConfirmName.setPriority(0, 120);
		
		Agenda agendaConfirmPricerange = factory.createAgenda("agenda_Confirm_Pricerange");
		agendaConfirmPricerange.addRequires(semGroupPricerange);
		agendaConfirmPricerange.addMustnot(semConfirmPricerange);
		agendaConfirmPricerange.setRole(agendaConfirmPricerange.getRole(), "confirmation");
		agendaConfirmPricerange.setPriority(0, 110);
		
		Agenda agendaConfirmPricerangeAndRequestArea = factory.createAgenda("agenda_Confirm_Pricerange_And_Request_Area");
		agendaConfirmPricerangeAndRequestArea.addRequires(semGroupPricerange);
		agendaConfirmPricerangeAndRequestArea.addMustnot(semConfirmPricerange);
		agendaConfirmPricerangeAndRequestArea.addMustnot(semGroupArea);
		agendaConfirmPricerangeAndRequestArea.addMustnot(semConfirmArea); 
		agendaConfirmPricerangeAndRequestArea.setPriority(0, 120); 
		
		Agenda agendaConfirmPricerangeAndRequestFood = factory.createAgenda("agenda_Confirm_Pricerange_And_Request_Food");
		agendaConfirmPricerangeAndRequestFood.addRequires(semGroupPricerange);
		agendaConfirmPricerangeAndRequestFood.addMustnot(semConfirmPricerange);
		agendaConfirmPricerangeAndRequestFood.addMustnot(semGroupFood);
		agendaConfirmPricerangeAndRequestFood.addMustnot(semConfirmFood); 
		agendaConfirmPricerangeAndRequestFood.setPriority(0, 140); 
		
		Agenda agendaConfirmAreaAndRequestFood = factory.createAgenda("agenda_Confirm_Area_And_Request_Food");
		agendaConfirmAreaAndRequestFood.addRequires(semGroupArea);
		agendaConfirmAreaAndRequestFood.addMustnot(semConfirmArea);
		agendaConfirmAreaAndRequestFood.addMustnot(semGroupFood);
		agendaConfirmAreaAndRequestFood.addMustnot(semConfirmFood); 
		agendaConfirmAreaAndRequestFood.setPriority(0, 140);  
		
		Agenda agendaConfirmAreaAndRequestPricerange = factory.createAgenda("agenda_Confirm_Area_And_Request_Pricerange");
		agendaConfirmAreaAndRequestPricerange.addRequires(semGroupArea);
		agendaConfirmAreaAndRequestPricerange.addMustnot(semConfirmArea);
		agendaConfirmAreaAndRequestPricerange.addMustnot(semGroupPricerange);
		agendaConfirmAreaAndRequestPricerange.addMustnot(semConfirmPricerange); 
		agendaConfirmAreaAndRequestPricerange.setPriority(0, 130); 
		
		Agenda agendaConfirmFoodAndRequestPricerange = factory.createAgenda("agenda_Confirm_Food_And_Request_Pricerange");
		agendaConfirmFoodAndRequestPricerange.addRequires(semGroupFood);
		agendaConfirmFoodAndRequestPricerange.addMustnot(semConfirmFood);
		agendaConfirmFoodAndRequestPricerange.addMustnot(semGroupPricerange);
		agendaConfirmFoodAndRequestPricerange.addMustnot(semConfirmPricerange); 
		agendaConfirmFoodAndRequestPricerange.setPriority(0, 130); 
		
		Agenda agendaConfirmFoodAndRequestArea = factory.createAgenda("agenda_Confirm_Food_And_Request_Area");
		agendaConfirmFoodAndRequestArea.addRequires(semGroupFood);
		agendaConfirmFoodAndRequestArea.addMustnot(semConfirmFood);
		agendaConfirmFoodAndRequestArea.addMustnot(semGroupArea);
		agendaConfirmFoodAndRequestArea.addMustnot(semConfirmArea); 
		agendaConfirmFoodAndRequestArea.setPriority(0, 120); 
		
		Agenda agendaOfferName = factory.createAgenda("agenda_Offer_Name");
		agendaOfferName.addRequires(semGroupArea);
		agendaOfferName.addRequires(semGroupFood);
		agendaOfferName.addRequires(semGroupPricerange);
//		agendaOfferName.addRequires(semConfirmArea);
//		agendaOfferName.addRequires(semConfirmFood);
//		agendaOfferName.addRequires(semConfirmPricerange);
		agendaOfferName.setPriority(0, 170);
		
		Agenda agendaTellArea = factory.createAgenda("agenda_Tell_Area");
		agendaTellArea.addRequires(semGroupName);
//		agendaTellArea.setVariableOperator("", "REQUIRES(%var_NeedArea%==yes");
		agendaTellArea.setVariableOperator("", "REQUIRES((%var_NeedArea%==yes) && (%var_SaidArea%!=yes))");
		agendaTellArea.setPriority(0, 150);
		
		Agenda agendaTellFood = factory.createAgenda("agenda_Tell_Food");
		agendaTellFood.addRequires(semGroupName);
//		agendaTellFood.setVariableOperator("", "REQUIRES(%var_NeedFood%==yes)");
		agendaTellFood.setVariableOperator("", "REQUIRES((%var_NeedFood%==yes) && (%var_SaidFood%!=yes))");
		agendaTellFood.setPriority(0, 150);
		
//		Agenda agendaTellName = factory.createAgenda("agenda_Tell_Name");
//		agendaTellName.setVariableOperator("", "REQUIRES(%var_NeedName%==yes)");
//		agendaTellName.setVariableOperator("", "REQUIRES((%var_NeedName%==yes) && (%var_SaidName%!=yes))");
//		agendaTellName.setPriority(0, 150);
			
		Agenda agendaTellPricerange = factory.createAgenda("agenda_Tell_Pricerange");
		agendaTellPricerange.addRequires(semGroupName);
//		agendaTellPricerange.setVariableOperator("", "REQUIRES(%var_NeedPricerange%==yes)");
		agendaTellPricerange.setVariableOperator("", "REQUIRES((%var_NeedPricerange%==yes) && (%var_SaidPricerange%!=yes))");
		agendaTellPricerange.setPriority(0, 150);
		
		Agenda agendaTellAddress = factory.createAgenda("agenda_Tell_Address");
		agendaTellAddress.addRequires(semGroupName);
//		agendaTellAddress.setVariableOperator("", "REQUIRES(%var_NeedAddress%==yes)");
		agendaTellAddress.setVariableOperator("", "REQUIRES((%var_NeedAddress%==yes) && (%var_SaidAddress%!=yes))");
		agendaTellAddress.setPriority(0, 150);
		
		Agenda agendaTellPhone = factory.createAgenda("agenda_Tell_Phone");
		agendaTellPhone.addRequires(semGroupName);
//		agendaTellPhone.setVariableOperator("", "REQUIRES(%var_NeedPhone%==yes)");
		agendaTellPhone.setVariableOperator("", "REQUIRES((%var_NeedPhone%==yes) && (%var_SaidPhone%!=yes))");
		agendaTellPhone.setPriority(0, 150);
		
		Agenda agendaTellPostcode = factory.createAgenda("agenda_Tell_Postcode");
		agendaTellPostcode.addRequires(semGroupName);
//		agendaTellPostcode.setVariableOperator("", "REQUIRES(%var_NeedPostcode%==yes)");
		agendaTellPostcode.setVariableOperator("", "REQUIRES((%var_NeedPostcode%==yes) && (%var_SaidPostcode%!=yes))");
		agendaTellPostcode.setPriority(0, 150);
		
		Agenda agendaTellSignature = factory.createAgenda("agenda_Tell_Signature");
		agendaTellSignature.addRequires(semGroupName);
//		agendaTellSignature.setVariableOperator("", "REQUIRES(%var_NeedSignature%==yes)");
		agendaTellSignature.setVariableOperator("", "REQUIRES((%var_NeedSignature%==yes) && (%var_SaidSignature%!=yes))");
		agendaTellSignature.setPriority(0, 150);
		
		Agenda agendaTellAlternative = factory.createAgenda("agenda_Tell_Alternative");
//		agendaTellAlternative.setVariableOperator("", "REQUIRES(%var_NeedAlternative%==yes)"); 
		agendaTellAlternative.setVariableOperator("", "REQUIRES((%var_NeedAlternative%==yes) && (%var_SaidAlternative%!=yes))"); 
		agendaTellAlternative.setPriority(0, 150);
		
		Agenda agendaBye = factory.createAgenda("agenda_Bye");
		agendaBye.setVariableOperator("", "REQUIRES(%var_SaidBye%==yes)");
		agendaBye.setPriority(0, 200);
		
		Agenda agendaAbort = factory.createAgenda("agenda_Abort");
		agendaAbort.setPriority(0, 0);

		agendaMaster.addNext(agendaOpen);
						
		agendaOpen.addNext(agendaConfirmArea);
		agendaOpen.addNext(agendaConfirmAreaAndRequestFood);
		agendaOpen.addNext(agendaConfirmAreaAndRequestPricerange);
		agendaOpen.addNext(agendaConfirmFood);
		agendaOpen.addNext(agendaConfirmFoodAndRequestArea);
		agendaOpen.addNext(agendaConfirmFoodAndRequestPricerange);
		agendaOpen.addNext(agendaConfirmName);
		agendaOpen.addNext(agendaConfirmPricerange);
		agendaOpen.addNext(agendaConfirmPricerangeAndRequestArea);
		agendaOpen.addNext(agendaConfirmPricerangeAndRequestFood);
		agendaOpen.addNext(agendaOpen);
		agendaOpen.addNext(agendaRequestArea);
		agendaOpen.addNext(agendaRequestFood);
		agendaOpen.addNext(agendaRequestPricerange);
		agendaOpen.addNext(agendaRequestName);
		agendaOpen.addNext(agendaBye);  //if user says bye
		
		agendaRequestArea.addNext(agendaConfirmArea);
		agendaRequestArea.addNext(agendaConfirmAreaAndRequestFood);
		agendaRequestArea.addNext(agendaConfirmAreaAndRequestPricerange);
		agendaRequestArea.addNext(agendaRequestArea);

		agendaRequestFood.addNext(agendaConfirmFood);
		agendaRequestFood.addNext(agendaConfirmFoodAndRequestArea);
		agendaRequestFood.addNext(agendaConfirmFoodAndRequestPricerange);
		agendaRequestFood.addNext(agendaRequestFood);
						
		agendaRequestPricerange.addNext(agendaConfirmPricerange);
		agendaRequestPricerange.addNext(agendaConfirmPricerangeAndRequestArea);
		agendaRequestPricerange.addNext(agendaConfirmPricerangeAndRequestFood);
		agendaRequestPricerange.addNext(agendaRequestPricerange);
		
		agendaRequestName.addNext(agendaConfirmName);
		agendaRequestName.addNext(agendaRequestName);
		
		agendaRequestMore.addNext(agendaTellArea);
		agendaRequestMore.addNext(agendaTellFood);
//		agendaRequestMore.addNext(agendaTellName);
		agendaRequestMore.addNext(agendaTellPricerange);
		agendaRequestMore.addNext(agendaTellAddress);
		agendaRequestMore.addNext(agendaTellPhone);
		agendaRequestMore.addNext(agendaTellPostcode);
		agendaRequestMore.addNext(agendaTellSignature);
		agendaRequestMore.addNext(agendaOfferName);
						
		agendaConfirmArea.addNext(agendaRequestArea); //if it was denied
		agendaConfirmArea.addNext(agendaConfirmArea); //if it was corrected
		agendaConfirmArea.addNext(agendaConfirmPricerange); //if it was affirmed and the pricerange is given
		agendaConfirmArea.addNext(agendaConfirmFood); //if it was affirmed and the food is given
		agendaConfirmArea.addNext(agendaOfferName); //if it was affirmed and the pricerange and food are already confirmed, too

		agendaConfirmFood.addNext(agendaRequestFood); //if it was denied
		agendaConfirmFood.addNext(agendaConfirmFood); //if it was corrected
		agendaConfirmFood.addNext(agendaConfirmPricerange); //if it was affirmed and the pricerange is given
		agendaConfirmFood.addNext(agendaConfirmArea); //if it was affirmed and the area is given
		agendaConfirmFood.addNext(agendaOfferName); //if it was affirmed and the pricerange and area are already confirmed, too
		
		agendaConfirmName.addNext(agendaOpen); //if it was denied
		agendaConfirmName.addNext(agendaConfirmName); //if it was corrected
		agendaConfirmName.addNext(agendaTellAddress); //if it was affirmed and the user wants the address
		agendaConfirmName.addNext(agendaTellArea); //if it was affirmed and the user wants the area
		agendaConfirmName.addNext(agendaTellFood); //if it was affirmed and the user wants the food
		agendaConfirmName.addNext(agendaTellPhone); //if it was affirmed and the user wants the phone
		agendaConfirmName.addNext(agendaTellPostcode); //if it was affirmed and the user wants the postcode
		agendaConfirmName.addNext(agendaTellPricerange); //if it was affirmed and the user wants the pricerange
		agendaConfirmName.addNext(agendaTellSignature); //if it was affirmed and the user wants the signature
		agendaConfirmName.addNext(agendaRequestMore); //if it was affirmed and the user does not ask for some specific information 
		
		agendaConfirmPricerange.addNext(agendaRequestPricerange); //if it was denied
		agendaConfirmPricerange.addNext(agendaConfirmPricerange); //if it was corrected
		agendaConfirmPricerange.addNext(agendaConfirmFood); //if it was affirmed and the food is given
		agendaConfirmPricerange.addNext(agendaConfirmArea); //if it was affirmed and the area is given
		agendaConfirmPricerange.addNext(agendaOfferName); //if it was affirmed and the area and food are already confirmed, too
		
		agendaConfirmAreaAndRequestFood.addNext(agendaConfirmFood); //if the food was told and the pricerange is already confirmed
		agendaConfirmAreaAndRequestFood.addNext(agendaConfirmFoodAndRequestPricerange); //if the food was told an the pricerange is not known
		agendaConfirmAreaAndRequestFood.addNext(agendaRequestArea); //if it was denied
		
		agendaConfirmAreaAndRequestPricerange.addNext(agendaConfirmPricerange); //if the pricerange was told and the food is already confirmed
		agendaConfirmAreaAndRequestPricerange.addNext(agendaConfirmPricerangeAndRequestFood); //if the pricerange was told an the food is not known
		agendaConfirmAreaAndRequestPricerange.addNext(agendaRequestArea); //if it was denied
		
		agendaConfirmFoodAndRequestPricerange.addNext(agendaConfirmPricerange); //if the pricerange was told and the area is already confirmed
		agendaConfirmFoodAndRequestPricerange.addNext(agendaConfirmPricerangeAndRequestArea); //if the pricerange was told an the area is not known
		agendaConfirmFoodAndRequestPricerange.addNext(agendaRequestFood); //if it was denied
		
		agendaConfirmFoodAndRequestArea.addNext(agendaConfirmArea); //if the area was told and the pricerange is already confirmed
		agendaConfirmFoodAndRequestArea.addNext(agendaConfirmAreaAndRequestPricerange); //if the area was told an the pricerange is not known
		agendaConfirmFoodAndRequestArea.addNext(agendaRequestFood); //if it was denied
		
		agendaConfirmPricerangeAndRequestArea.addNext(agendaConfirmArea); //if the area was told and the food is already confirmed
		agendaConfirmPricerangeAndRequestArea.addNext(agendaConfirmAreaAndRequestFood); //if the area was told an the food is not known
		agendaConfirmPricerangeAndRequestArea.addNext(agendaRequestPricerange); //if it was denied
		
		agendaConfirmPricerangeAndRequestFood.addNext(agendaConfirmFood); //if the food was told and the area is already confirmed
		agendaConfirmPricerangeAndRequestFood.addNext(agendaConfirmFoodAndRequestArea); //if the food was told an the area is not known
		agendaConfirmPricerangeAndRequestFood.addNext(agendaRequestPricerange); //if it was denied
		
		agendaTellArea.addNext(agendaRequestMore);
		
		agendaTellFood.addNext(agendaRequestMore);
		
//		agendaTellName.addNext(agendaRequestMore);
		
		agendaTellPricerange.addNext(agendaRequestMore);
		
		agendaTellAddress.addNext(agendaRequestMore);
		
		agendaTellPhone.addNext(agendaRequestMore);
		
		agendaTellPostcode.addNext(agendaRequestMore);
		
		agendaTellSignature.addNext(agendaRequestMore);
		
		agendaTellAlternative.addNext(agendaRequestMore);
	
		agendaOfferName.addNext(agendaTellAlternative); //if user requests an alternative
		agendaOfferName.addNext(agendaTellArea); //if user requests the area
		agendaOfferName.addNext(agendaTellFood); //if user requests the food
//		agendaOfferName.addNext(agendaTellName); //if user requests the name
		agendaOfferName.addNext(agendaTellPricerange); //if user requests the pricerange
		agendaOfferName.addNext(agendaTellAddress); //if user requests the address
		agendaOfferName.addNext(agendaTellPhone); //if user requests the phone number
		agendaOfferName.addNext(agendaTellPostcode); //if user requests the postcode
		agendaOfferName.addNext(agendaTellSignature); //if user requests the signature
		agendaOfferName.addNext(agendaRequestMore); //if user acknowledges the name but does not ask for some specific information 
		agendaOfferName.addNext(agendaConfirmName); //if user corrects the name 

		
		//create summary agendas
		SummaryAgenda sumAgendaOpen = factory.createSummaryAgenda("sumAgenda_Open");
		sumAgendaOpen.setType(sumAgendaOpen.getTypeString(), "request");
		sumAgendaOpen.setRole(sumAgendaOpen.getRole(), "collection");
		
		SummaryAgenda sumAgendaRequest = factory.createSummaryAgenda("sumAgenda_Request");
		sumAgendaRequest.setType(sumAgendaRequest.getTypeString(), "request");
		sumAgendaRequest.setRole(sumAgendaRequest.getRole(), "collection");
		
		SummaryAgenda sumAgendaRequestMore = factory.createSummaryAgenda("sumAgenda_Request_More");
		sumAgendaRequestMore.setType(sumAgendaRequest.getTypeString(), "request");
		sumAgendaRequestMore.setRole(sumAgendaRequest.getRole(), "collection");
		
		SummaryAgenda sumAgendaConfirm = factory.createSummaryAgenda("sumAgenda_Confirm");
		sumAgendaConfirm.setType(sumAgendaConfirm.getTypeString(), "confirmation");
		sumAgendaConfirm.setRole(sumAgendaConfirm.getRole(), "confirmation");
		
		SummaryAgenda sumAgendaConfirmAndRequest = factory.createSummaryAgenda("sumAgenda_Confirm_And_Request");
		sumAgendaConfirmAndRequest.setType(sumAgendaConfirmAndRequest.getTypeString(), "implicit");
		sumAgendaConfirmAndRequest.setRole(sumAgendaConfirmAndRequest.getRole(), "confirmation");
		
		SummaryAgenda sumAgendaTell = factory.createSummaryAgenda("sumAgenda_Tell");
		sumAgendaTell.setType(sumAgendaTell.getTypeString(), "announcement");
		sumAgendaTell.setRole(sumAgendaTell.getRole(), "collection");
		
		SummaryAgenda sumAgendaOffer = factory.createSummaryAgenda("sumAgenda_Offer");
		sumAgendaOffer.setType(sumAgendaTell.getTypeString(), "announcement");
		sumAgendaOffer.setRole(sumAgendaTell.getRole(), "collection");
		
		SummaryAgenda sumAgendaEnd = factory.createSummaryAgenda("sumAgenda_End");
		sumAgendaEnd.setType(sumAgendaEnd.getTypeString(), "announcement");
		sumAgendaEnd.setRole(sumAgendaEnd.getRole(), "collection");
		
		agendaOpen.addSummaryAgenda(sumAgendaOpen);
		sumAgendaOpen.addSummarizedAgenda(agendaOpen);
		
		agendaRequestArea.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaRequestArea);
		agendaRequestFood.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaRequestFood);
		agendaRequestPricerange.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaRequestPricerange);
		agendaRequestName.addSummaryAgenda(sumAgendaRequest);
		sumAgendaRequest.addSummarizedAgenda(agendaRequestName);
//		agendaRequestMore.addSummaryAgenda(sumAgendaRequest);
//		sumAgendaRequest.addSummarizedAgenda(agendaRequestMore);
		
		agendaRequestMore.addSummaryAgenda(sumAgendaRequestMore);
		sumAgendaRequestMore.addSummarizedAgenda(agendaRequestMore);
			
		agendaConfirmArea.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmArea);
		agendaConfirmFood.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmFood);
		agendaConfirmName.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmName);
		agendaConfirmPricerange.addSummaryAgenda(sumAgendaConfirm);
		sumAgendaConfirm.addSummarizedAgenda(agendaConfirmPricerange);
		
		agendaConfirmAreaAndRequestFood.addSummaryAgenda(sumAgendaConfirmAndRequest);
		sumAgendaConfirmAndRequest.addSummarizedAgenda(agendaConfirmAreaAndRequestFood);
		agendaConfirmAreaAndRequestPricerange.addSummaryAgenda(sumAgendaConfirmAndRequest);
		sumAgendaConfirmAndRequest.addSummarizedAgenda(agendaConfirmAreaAndRequestPricerange);
		agendaConfirmPricerangeAndRequestFood.addSummaryAgenda(sumAgendaConfirmAndRequest);
		sumAgendaConfirmAndRequest.addSummarizedAgenda(agendaConfirmPricerangeAndRequestFood);
		agendaConfirmPricerangeAndRequestArea.addSummaryAgenda(sumAgendaConfirmAndRequest);
		sumAgendaConfirmAndRequest.addSummarizedAgenda(agendaConfirmPricerangeAndRequestArea);
		agendaConfirmFoodAndRequestArea.addSummaryAgenda(sumAgendaConfirmAndRequest);
		sumAgendaConfirmAndRequest.addSummarizedAgenda(agendaConfirmFoodAndRequestArea);
		agendaConfirmFoodAndRequestPricerange.addSummaryAgenda(sumAgendaConfirmAndRequest);
		sumAgendaConfirmAndRequest.addSummarizedAgenda(agendaConfirmFoodAndRequestPricerange);
		
		agendaTellArea.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellArea);
		agendaTellFood.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellFood);
//		agendaTellName.addSummaryAgenda(sumAgendaTell);
//		sumAgendaTell.addSummarizedAgenda(agendaTellName);
		agendaTellPricerange.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellPricerange);
		agendaTellAddress.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellAddress);
		agendaTellPhone.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellPhone);
		agendaTellPostcode.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellPostcode);
		agendaTellSignature.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellSignature);
		agendaTellAlternative.addSummaryAgenda(sumAgendaTell);
		sumAgendaTell.addSummarizedAgenda(agendaTellAlternative);
//		agendaOfferName.addSummaryAgenda(sumAgendaTell);
//		sumAgendaTell.addSummarizedAgenda(agendaOfferName);
		
		agendaOfferName.addSummaryAgenda(sumAgendaOffer);
		sumAgendaOffer.addSummarizedAgenda(agendaOfferName);
						
		agendaBye.addSummaryAgenda(sumAgendaEnd);
		sumAgendaEnd.addSummarizedAgenda(agendaBye);
		agendaAbort.addSummaryAgenda(sumAgendaEnd);
		sumAgendaEnd.addSummarizedAgenda(agendaAbort);

        
		//generate system moves
		Move moveSysOpen = factory.createMove("move_sys_Open");
		agendaOpen.addHas(moveSysOpen);
		getAndAddUtterance(factory,moveSysOpen);		
		
		Move moveSysReqArea = factory.createMove("move_sys_Request_Area");
		agendaRequestArea.addHas(moveSysReqArea);
		getAndAddUtterance(factory,moveSysReqArea);
		
		Move moveSysReqFood = factory.createMove("move_sys_Request_Food");
		agendaRequestFood.addHas(moveSysReqFood);
		getAndAddUtterance(factory,moveSysReqFood);
		
		Move moveSysReqPricerange = factory.createMove("move_sys_Request_Pricerange");
		agendaRequestPricerange.addHas(moveSysReqPricerange);
		getAndAddUtterance(factory,moveSysReqPricerange);
		
		Move moveSysReqName = factory.createMove("move_sys_Request_Name");
		agendaRequestName.addHas(moveSysReqName);
		getAndAddUtterance(factory,moveSysReqName);
		
		Move moveSysReqMore = factory.createMove("move_sys_Request_More");
		agendaRequestMore.addHas(moveSysReqMore);
		getAndAddUtterance(factory,moveSysReqMore);
		
		Move moveSysConfirmArea = factory.createMove("move_sys_Confirm_Area");
		agendaConfirmArea.addHas(moveSysConfirmArea);
		getAndAddUtterance(factory, moveSysConfirmArea);
		
		Move moveSysConfirmFood = factory.createMove("move_sys_Confirm_Food");
		agendaConfirmFood.addHas(moveSysConfirmFood);
		getAndAddUtterance(factory, moveSysConfirmFood);
		
		Move moveSysConfirmName = factory.createMove("move_sys_Confirm_Name");
		agendaConfirmName.addHas(moveSysConfirmName);
		getAndAddUtterance(factory, moveSysConfirmName);
		
		Move moveSysConfirmPricerange = factory.createMove("move_sys_Confirm_Pricerange");
		agendaConfirmPricerange.addHas(moveSysConfirmPricerange);
		getAndAddUtterance(factory, moveSysConfirmPricerange);
		
		Move moveSysConfirmPricerangeAndRequestArea = factory.createMove("move_sys_Confirm_Pricerange_And_Request_Area");
		agendaConfirmPricerangeAndRequestArea.addHas(moveSysConfirmPricerangeAndRequestArea);
		getAndAddUtterance(factory, moveSysConfirmPricerangeAndRequestArea);
		
		Move moveSysConfirmPricerangeAndRequestFood = factory.createMove("move_sys_Confirm_Pricerange_And_Request_Food");
		agendaConfirmPricerangeAndRequestFood.addHas(moveSysConfirmPricerangeAndRequestFood);
		getAndAddUtterance(factory, moveSysConfirmPricerangeAndRequestFood);
		
		Move moveSysConfirmAreaAndRequestFood = factory.createMove("move_sys_Confirm_Area_And_Request_Food");
		agendaConfirmAreaAndRequestFood.addHas(moveSysConfirmAreaAndRequestFood);
		getAndAddUtterance(factory, moveSysConfirmAreaAndRequestFood);
		
		Move moveSysConfirmAreaAndRequestPricerange = factory.createMove("move_sys_Confirm_Area_And_Request_Pricerange");
		agendaConfirmAreaAndRequestPricerange.addHas(moveSysConfirmAreaAndRequestPricerange);
		getAndAddUtterance(factory, moveSysConfirmAreaAndRequestPricerange);
		
		Move moveSysConfirmFoodAndRequestPricerange = factory.createMove("move_sys_Confirm_Food_And_Request_Pricerange");
		agendaConfirmFoodAndRequestPricerange.addHas(moveSysConfirmFoodAndRequestPricerange);
		getAndAddUtterance(factory, moveSysConfirmFoodAndRequestPricerange);
		
		Move moveSysConfirmFoodAndRequestArea = factory.createMove("move_sys_Confirm_Food_And_Request_Area");
		agendaConfirmFoodAndRequestArea.addHas(moveSysConfirmFoodAndRequestArea);
		getAndAddUtterance(factory, moveSysConfirmFoodAndRequestArea);
				
		Move moveSysOfferName = factory.createMove("move_sys_Offer_Name");
		moveSysOfferName.setVariableOperator("SET(" + varSaidName + "=yes)");
		agendaOfferName.addHas(moveSysOfferName);
		getAndAddUtterance(factory, moveSysOfferName);
		
		Move moveSysTellArea = factory.createMove("move_sys_Tell_Area");
		moveSysTellArea.setVariableOperator("SET(" + varSaidArea + "=yes)");
		agendaTellArea.addHas(moveSysTellArea);
		getAndAddUtterance(factory, moveSysTellArea);
		
		Move moveSysTellFood = factory.createMove("move_sys_Tell_Food");
		moveSysTellFood.setVariableOperator("SET(" + varSaidFood + "=yes)");
		agendaTellFood.addHas(moveSysTellFood);
		getAndAddUtterance(factory, moveSysTellFood);
		
//		Move moveSysTellName = factory.createMove("move_sys_Tell_Name");
//		moveSysTellName.setVariableOperator("SET(" + varSaidName + "=yes)");
//		agendaTellName.addHas(moveSysTellName);
//		getAndAddUtterance(factory, moveSysTellName);
		
		Move moveSysTellPricerange = factory.createMove("move_sys_Tell_Pricerange");
		moveSysTellPricerange.setVariableOperator("SET(" + varSaidPricerange + "=yes)");
		agendaTellPricerange.addHas(moveSysTellPricerange);
		getAndAddUtterance(factory, moveSysTellPricerange);
		
		Move moveSysTellAddress = factory.createMove("move_sys_Tell_Address");
		moveSysTellAddress.setVariableOperator("SET(" + varSaidAddress + "=yes)");
		agendaTellAddress.addHas(moveSysTellAddress);
		getAndAddUtterance(factory, moveSysTellAddress);
		
		Move moveSysTellPhone = factory.createMove("move_sys_Tell_Phone");
		moveSysTellPhone.setVariableOperator("SET(" + varSaidPhone + "=yes)");
		agendaTellPhone.addHas(moveSysTellPhone);
		getAndAddUtterance(factory, moveSysTellPhone);
				
		Move moveSysTellPostcode = factory.createMove("move_sys_Tell_Postcode");
		moveSysTellPostcode.setVariableOperator("SET(" + varSaidPostcode + "=yes)");
		agendaTellPostcode.addHas(moveSysTellPostcode);
		getAndAddUtterance(factory, moveSysTellPostcode);
		
		Move moveSysTellSignature = factory.createMove("move_sys_Tell_Signature");
		moveSysTellSignature.setVariableOperator("SET(" + varSaidSignature + "=yes)");
		agendaTellSignature.addHas(moveSysTellSignature);
		getAndAddUtterance(factory, moveSysTellSignature);
		
		Move moveSysTellAlternative = factory.createMove("move_sys_Tell_Alternative");
		moveSysTellSignature.setVariableOperator("SET(" + varSaidAlternative + "=yes)");
		agendaTellAlternative.addHas(moveSysTellAlternative);
		getAndAddUtterance(factory, moveSysTellAlternative);
		
		Move moveSysBye = factory.createMove("move_sys_Bye");
		agendaBye.addHas(moveSysBye);
		getAndAddUtterance(factory, moveSysBye);
		moveSysBye.setIsExitMove(false, true);	
		
		Move moveSysAbort = factory.createMove("move_sys_Abort");
		agendaAbort.addHas(moveSysAbort);
		getAndAddUtterance(factory, moveSysAbort);
		moveSysAbort.setIsExitMove(false, true);	
		
		
		//create the general grammars
		Grammar grDeny = grammarDeny(factory);
		Grammar grAffirm = grammarAffirm(factory);
		
		
//		boolean onlyInformMovesWithOneSlot = false;
//		
//		if (onlyInformMovesWithOneSlot)
//		{
//			//create the general grammars
//			Grammar grArea = prefixGrammarArea(factory);
//			Grammar grFood = prefixGrammarFood(factory);
//			Grammar grName = prefixGrammarName(factory);
//			Grammar grPricerange = prefixGrammarPricerange(factory);
//			Grammar grRequestAddressName = prefixGrammarRequestAddressName(factory);
//			Grammar grRequestPhoneName = prefixGrammarRequestPhoneName(factory);
//			Grammar grRequestPostcodeName = prefixGrammarRequestPostcodeName(factory);
//			Grammar grRequestSignatureName = prefixGrammarRequestSignatureName(factory);
//			Grammar grRequestAreaName = prefixGrammarRequestAreaName(factory);
//			Grammar grRequestFoodName = prefixGrammarRequestFoodName(factory);
//			Grammar grRequestPricerangeName = prefixGrammarRequestPricerangeName(factory);
//					
//			
//			//create the grammars and moves for the areas
//			String areasFile = AdaptiveLetsGo.class.getResource("areas.txt").getPath();
//			File areas = new File(areasFile);
//			Scanner fileScannerAreas = new Scanner(areas); //create the scanner
//			while (fileScannerAreas.hasNextLine() == true) {
//				String line = fileScannerAreas.nextLine(); //save the line in the string
//				line = line.trim(); //remove spaces at the beginning and end of the line
//				String name = line.substring(1, line.length() - 2); //remove " and , at the beginning and the end
//				Grammar g1 = importGrammarArea(factory, name);
//				g1.addGeneralGrammar(grArea);
//				Grammar g2 = importGrammarCorrectArea(factory, name);
//				g2.addGeneralGrammar(grArea);
//				g2.addGeneralGrammar(grDeny);
//				Grammar g3 = importGrammarAffirmAndGiveArea(factory, name);
//				g3.addGeneralGrammar(grArea);
//				g3.addGeneralGrammar(grAffirm);
//				name = name.replace(" ", "_"); //replace spaces by _
//				Move moveUserArea = factory.createMove("move_user_Area_" + name);
//				moveUserArea.setVariableOperator("SET(" + varArea + "=" + name + ")");
//				moveUserArea.addSemantic(semGroupArea);
//				addGrammarToMove(moveUserArea, g1);
//				agendaRequestArea.addHas(moveUserArea);
//				agendaOpen.addHas(moveUserArea);
//				Move moveUserImplicitlyAffirmFoodAndGiveArea = factory.createMove("move_user_Implicitly_Affirm_Food_And_Give_Area_" + name);
//				moveUserImplicitlyAffirmFoodAndGiveArea.setVariableOperator("SET(" + varArea + "=" + name + ")");
//				moveUserImplicitlyAffirmFoodAndGiveArea.addSemantic(semGroupArea);
//				moveUserImplicitlyAffirmFoodAndGiveArea.addSemantic(semConfirmFood);
//				addGrammarToMove(moveUserImplicitlyAffirmFoodAndGiveArea, g1);
//				agendaConfirmFoodAndRequestArea.addHas(moveUserImplicitlyAffirmFoodAndGiveArea);
//				Move moveUserImplicitlyAffirmPricerangeAndGiveArea = factory.createMove("move_user_Implicitly_Affirm_Pricerange_And_Give_Area_" + name);
//				moveUserImplicitlyAffirmPricerangeAndGiveArea.setVariableOperator("SET(" + varArea + "=" + name + ")");
//				moveUserImplicitlyAffirmPricerangeAndGiveArea.addSemantic(semGroupArea);
//				moveUserImplicitlyAffirmPricerangeAndGiveArea.addSemantic(semConfirmPricerange);
//				addGrammarToMove(moveUserImplicitlyAffirmPricerangeAndGiveArea, g1);
//				agendaConfirmPricerangeAndRequestArea.addHas(moveUserImplicitlyAffirmPricerangeAndGiveArea);
//				Move moveUserCorrectArea = factory.createMove("move_user_Correct_Area_" + name);
//				moveUserCorrectArea.setVariableOperator("SET(" + varArea + "=" + name + ")");
//				addGrammarToMove(moveUserCorrectArea, g2);
//				agendaConfirmArea.addHas(moveUserCorrectArea);
//				Move moveUserAffirmPricerangeAndGiveArea = factory.createMove("move_user_Affirm_Pricerange_And_Give_Area_" + name);
//				moveUserAffirmPricerangeAndGiveArea.setVariableOperator("SET(" + varArea + "=" + name + ")");
//				moveUserAffirmPricerangeAndGiveArea.addSemantic(semConfirmPricerange);
//				moveUserAffirmPricerangeAndGiveArea.addSemantic(semGroupArea);
//				addGrammarToMove(moveUserAffirmPricerangeAndGiveArea, g3);
//				agendaConfirmPricerange.addHas(moveUserAffirmPricerangeAndGiveArea);
//				Move moveUserAffirmFoodAndGiveArea = factory.createMove("move_user_Affirm_Food_And_Give_Area_" + name);
//				moveUserAffirmFoodAndGiveArea.setVariableOperator("SET(" + varArea + "=" + name + ")");
//				moveUserAffirmFoodAndGiveArea.addSemantic(semConfirmFood);
//				moveUserAffirmFoodAndGiveArea.addSemantic(semGroupArea);
//				addGrammarToMove(moveUserAffirmFoodAndGiveArea, g3);
//				agendaConfirmFood.addHas(moveUserAffirmFoodAndGiveArea);
//			}
//			fileScannerAreas.close();
//			
//			
//			//create the grammars and moves for the foods
//			String foodsFile = AdaptiveLetsGo.class.getResource("foods.txt").getPath();
//			File foods = new File(foodsFile);
//			Scanner fileScannerFoods = new Scanner(foods); //create the scanner
//			while (fileScannerFoods.hasNextLine() == true) {
//				String line = fileScannerFoods.nextLine(); //save the line in the string
//				line = line.trim(); //remove spaces at the beginning and end of the line
//				String name = line.substring(1, line.length() - 2); //remove " and , at the beginning and the end
//				Grammar g1 = importGrammarFood(factory, name);
//				g1.addGeneralGrammar(grFood);
//				Grammar g2 = importGrammarCorrectFood(factory, name);
//				g2.addGeneralGrammar(grFood);
//				g2.addGeneralGrammar(grDeny);
//				Grammar g3 = importGrammarAffirmAndGiveFood(factory, name);
//				g3.addGeneralGrammar(grFood);
//				g3.addGeneralGrammar(grAffirm);
//				name = name.replace(" ", "_"); //replace spaces by _
//				Move moveUserFood = factory.createMove("move_user_Food_" + name);
//				moveUserFood.setVariableOperator("SET(" + varFood + "=" + name + ")");
//				moveUserFood.addSemantic(semGroupFood);
//				addGrammarToMove(moveUserFood, g1);
//				agendaRequestFood.addHas(moveUserFood);
//				agendaOpen.addHas(moveUserFood);
//				Move moveUserImplicitlyAffirmAreaAndGiveFood = factory.createMove("move_user_Implicitly_Affirm_Area_And_Give_Food_" + name);
//				moveUserImplicitlyAffirmAreaAndGiveFood.setVariableOperator("SET(" + varFood + "=" + name + ")");
//				moveUserImplicitlyAffirmAreaAndGiveFood.addSemantic(semGroupFood);
//				moveUserImplicitlyAffirmAreaAndGiveFood.addSemantic(semConfirmArea);
//				addGrammarToMove(moveUserImplicitlyAffirmAreaAndGiveFood, g1);
//				agendaConfirmAreaAndRequestFood.addHas(moveUserImplicitlyAffirmAreaAndGiveFood);
//				Move moveUserImplicitlyAffirmPricerangeAndGiveFood = factory.createMove("move_user_Implicitly_Affirm_Pricerange_And_Give_Food_" + name);
//				moveUserImplicitlyAffirmPricerangeAndGiveFood.setVariableOperator("SET(" + varFood + "=" + name + ")");
//				moveUserImplicitlyAffirmPricerangeAndGiveFood.addSemantic(semGroupFood);
//				moveUserImplicitlyAffirmPricerangeAndGiveFood.addSemantic(semConfirmPricerange);
//				addGrammarToMove(moveUserImplicitlyAffirmPricerangeAndGiveFood, g1);
//				agendaConfirmPricerangeAndRequestFood.addHas(moveUserImplicitlyAffirmPricerangeAndGiveFood);	
//				Move moveUserCorrectFood = factory.createMove("move_user_Correct_Food_" + name);
//				moveUserCorrectFood.setVariableOperator("SET(" + varFood + "=" + name + ")");
//				addGrammarToMove(moveUserCorrectFood, g2);
//				agendaConfirmFood.addHas(moveUserCorrectFood);
//				Move moveUserAffirmPricerangeAndGiveFood = factory.createMove("move_user_Affirm_Pricerange_And_Give_Food_" + name);
//				moveUserAffirmPricerangeAndGiveFood.setVariableOperator("SET(" + varFood + "=" + name + ")");
//				moveUserAffirmPricerangeAndGiveFood.addSemantic(semConfirmPricerange);
//				moveUserAffirmPricerangeAndGiveFood.addSemantic(semGroupFood);
//				addGrammarToMove(moveUserAffirmPricerangeAndGiveFood, g3);
//				agendaConfirmPricerange.addHas(moveUserAffirmPricerangeAndGiveFood);
//				Move moveUserAffirmAreaAndGiveFood = factory.createMove("move_user_Affirm_Area_And_Give_Food_" + name);
//				moveUserAffirmAreaAndGiveFood.setVariableOperator("SET(" + varFood + "=" + name + ")");
//				moveUserAffirmAreaAndGiveFood.addSemantic(semConfirmArea);
//				moveUserAffirmAreaAndGiveFood.addSemantic(semGroupFood);
//				addGrammarToMove(moveUserAffirmAreaAndGiveFood, g3);
//				agendaConfirmArea.addHas(moveUserAffirmAreaAndGiveFood);
//			}
//			fileScannerFoods.close();
//			
//			
//			//create the grammars and moves for the names
//			String namesFile = AdaptiveLetsGo.class.getResource("names.txt").getPath();
//			File names = new File(namesFile);
//			Scanner fileScannerNames = new Scanner(names); //create the scanner
//			while (fileScannerNames.hasNextLine() == true) {
//				String line = fileScannerNames.nextLine(); //save the line in the string
//				line = line.trim(); //remove spaces at the beginning and end of the line
//				String name = line.substring(1, line.length() - 2); //remove " and , at the beginning and the end
//				Grammar g1 = importGrammarName(factory, name);
//				g1.addGeneralGrammar(grName);
//				Grammar g2 = importGrammarCorrectName(factory, name);
//				g2.addGeneralGrammar(grName);
//				g2.addGeneralGrammar(grDeny);
//				Grammar g3 = importGrammarRequestAddressName(factory, name);
//				g3.addGeneralGrammar(grRequestAddressName);
//				Grammar g4 = importGrammarRequestPhoneName(factory, name);
//				g4.addGeneralGrammar(grRequestPhoneName);
//				Grammar g5 = importGrammarRequestPostcodeName(factory, name);
//				g5.addGeneralGrammar(grRequestPostcodeName);
//				Grammar g6 = importGrammarRequestSignatureName(factory, name);
//				g6.addGeneralGrammar(grRequestSignatureName);
//				Grammar g7 = importGrammarRequestAreaName(factory, name);
//				g7.addGeneralGrammar(grRequestAreaName);
//				Grammar g8 = importGrammarRequestFoodName(factory, name);
//				g8.addGeneralGrammar(grRequestFoodName);
//				Grammar g9 = importGrammarRequestPricerangeName(factory, name);
//				g9.addGeneralGrammar(grRequestPricerangeName);
//				name = name.replace(" ", "_"); //replace spaces by _
//				Move moveUserName = factory.createMove("move_user_Name_" + name);
//				moveUserName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserName, g1);
//				agendaRequestName.addHas(moveUserName);
//				agendaOpen.addHas(moveUserName);
//				Move moveUserCorrectName = factory.createMove("move_user_Correct_Name_" + name);
//				moveUserCorrectName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				addGrammarToMove(moveUserCorrectName, g2);
//				agendaConfirmName.addHas(moveUserCorrectName);
//				Move moveUserRequestAddressName = factory.createMove("move_user_Request_Address_" + name);
//				moveUserRequestAddressName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestAddressName.addSemantic(semNeedAddress);
//				moveUserRequestAddressName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestAddressName, g3);
//				agendaOpen.addHas(moveUserRequestAddressName);
//				agendaTellAlternative.addHas(moveUserRequestAddressName);
//				Move moveUserRequestPhoneName = factory.createMove("move_user_Request_Phone_" + name);
//				moveUserRequestPhoneName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestPhoneName.addSemantic(semNeedPhone);
//				moveUserRequestPhoneName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestPhoneName, g4);
//				agendaOpen.addHas(moveUserRequestPhoneName);
//				agendaTellAlternative.addHas(moveUserRequestPhoneName);
//				Move moveUserRequestPostcodeName = factory.createMove("move_user_Request_Postcode_" + name);
//				moveUserRequestPostcodeName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestPostcodeName.addSemantic(semNeedPostcode);
//				moveUserRequestPostcodeName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestPostcodeName, g5);
//				agendaOpen.addHas(moveUserRequestPostcodeName);
//				agendaTellAlternative.addHas(moveUserRequestPostcodeName);
//				Move moveUserRequestSignatureName = factory.createMove("move_user_Request_Signature_" + name);
//				moveUserRequestSignatureName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestSignatureName.addSemantic(semNeedSignature);
//				moveUserRequestSignatureName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestSignatureName, g6);
//				agendaOpen.addHas(moveUserRequestSignatureName);
//				agendaTellAlternative.addHas(moveUserRequestSignatureName);
//				Move moveUserRequestAreaName = factory.createMove("move_user_Request_Area_" + name);
//				moveUserRequestAreaName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestAreaName.addSemantic(semNeedArea);
//				moveUserRequestAreaName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestAreaName, g7);
//				agendaOpen.addHas(moveUserRequestAreaName);
//				agendaTellAlternative.addHas(moveUserRequestAreaName);
//				Move moveUserRequestFoodName = factory.createMove("move_user_Request_Food_" + name);
//				moveUserRequestFoodName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestFoodName.addSemantic(semNeedFood);
//				moveUserRequestFoodName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestFoodName, g8);
//				agendaOpen.addHas(moveUserRequestFoodName);
//				agendaTellAlternative.addHas(moveUserRequestFoodName);
//				Move moveUserRequestPricerangeName = factory.createMove("move_user_Request_Pricerange_" + name);
//				moveUserRequestPricerangeName.setVariableOperator("SET(" + varName + "=" + name + ")");
//				moveUserRequestPricerangeName.addSemantic(semNeedPricerange);
//				moveUserRequestPricerangeName.addSemantic(semGroupName);
//				addGrammarToMove(moveUserRequestPricerangeName, g9);
//				agendaOpen.addHas(moveUserRequestPricerangeName);
//				agendaTellAlternative.addHas(moveUserRequestPricerangeName);
//			}
//			fileScannerNames.close();
//			
//			
//			//create the grammars and moves for the priceranges
//			String pricerangesFile = AdaptiveLetsGo.class.getResource("priceranges.txt").getPath();
//			File priceranges = new File(pricerangesFile);
//			Scanner fileScannerPriceranges = new Scanner(priceranges); //create the scanner
//			while (fileScannerPriceranges.hasNextLine() == true) {
//				String line = fileScannerPriceranges.nextLine(); //save the line in the string
//				line = line.trim(); //remove spaces at the beginning and end of the line
//				String name = line.substring(1, line.length() - 2); //remove " and , at the beginning and the end
//				Grammar g1 = importGrammarPricerange(factory, name);
//				g1.addGeneralGrammar(grPricerange);
//				Grammar g2 = importGrammarCorrectPricerange(factory, name);
//				g2.addGeneralGrammar(grPricerange);
//				g2.addGeneralGrammar(grDeny);
//				Grammar g3 = importGrammarAffirmAndGivePricerange(factory, name);
//				g3.addGeneralGrammar(grPricerange);
//				g3.addGeneralGrammar(grAffirm);
//				name = name.replace(" ", "_"); //replace spaces by _
//				Move moveUserPricerange = factory.createMove("move_user_Pricerange_" + name);
//				moveUserPricerange.setVariableOperator("SET(" + varPricerange + "=" + name + ")");
//				moveUserPricerange.addSemantic(semGroupPricerange);
//				addGrammarToMove(moveUserPricerange, g1);
//				agendaRequestPricerange.addHas(moveUserPricerange);
//				agendaOpen.addHas(moveUserPricerange);
//				Move moveUserImplicitlyAffirmAreaAndGivePricerange = factory.createMove("move_user_Implicitly_Affirm_Area_And_Give_Pricerange_" + name);
//				moveUserImplicitlyAffirmAreaAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=" + name + ")");
//				moveUserImplicitlyAffirmAreaAndGivePricerange.addSemantic(semGroupPricerange);
//				moveUserImplicitlyAffirmAreaAndGivePricerange.addSemantic(semConfirmArea);
//				moveUserImplicitlyAffirmAreaAndGivePricerange.addSemantic(semNeedName);
//				addGrammarToMove(moveUserImplicitlyAffirmAreaAndGivePricerange, g1);
//				agendaConfirmAreaAndRequestPricerange.addHas(moveUserImplicitlyAffirmAreaAndGivePricerange);
//				Move moveUserImplicitlyAffirmFoodAndGivePricerange = factory.createMove("move_user_Implicitly_Affirm_Food_And_Give_Pricerange_" + name);
//				moveUserImplicitlyAffirmFoodAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=" + name + ")");
//				moveUserImplicitlyAffirmFoodAndGivePricerange.addSemantic(semGroupPricerange);
//				moveUserImplicitlyAffirmFoodAndGivePricerange.addSemantic(semConfirmFood);
//				moveUserImplicitlyAffirmFoodAndGivePricerange.addSemantic(semNeedName);
//				addGrammarToMove(moveUserImplicitlyAffirmFoodAndGivePricerange, g1);
//				agendaConfirmFoodAndRequestPricerange.addHas(moveUserImplicitlyAffirmFoodAndGivePricerange);
//				Move moveUserCorrectPricerange = factory.createMove("move_user_Correct_Pricerange_" + name);
//				moveUserCorrectPricerange.setVariableOperator("SET(" + varPricerange + "=" + name + ")");
//				addGrammarToMove(moveUserCorrectPricerange, g2);
//				agendaConfirmPricerange.addHas(moveUserCorrectPricerange);
//				Move moveUserAffirmAreaAndGivePricerange = factory.createMove("move_user_Affirm_Area_And_Give_Pricerange_" + name);
//				moveUserAffirmAreaAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=" + name + ")");
//				moveUserAffirmAreaAndGivePricerange.addSemantic(semConfirmArea);
//				moveUserAffirmAreaAndGivePricerange.addSemantic(semNeedName);
//				moveUserAffirmAreaAndGivePricerange.addSemantic(semGroupPricerange);
//				addGrammarToMove(moveUserAffirmAreaAndGivePricerange, g3);
//				agendaConfirmArea.addHas(moveUserAffirmAreaAndGivePricerange);
//				Move moveUserAffirmFoodAndGivePricerange = factory.createMove("move_user_Affirm_Food_And_Give_Pricerange_" + name);
//				moveUserAffirmFoodAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=" + name + ")");
//				moveUserAffirmFoodAndGivePricerange.addSemantic(semConfirmFood);
//				moveUserAffirmFoodAndGivePricerange.addSemantic(semNeedName);
//				moveUserAffirmFoodAndGivePricerange.addSemantic(semGroupPricerange);
//				addGrammarToMove(moveUserAffirmFoodAndGivePricerange, g3);
//				agendaConfirmFood.addHas(moveUserAffirmFoodAndGivePricerange);
//			}
//			fileScannerPriceranges.close();
//		}
//		else
//		{
			//create a dummygrammar
			Grammar g = factory.createGrammar("dummygrammar");
			
			
			//create the moves for the area, set the variable and connect it to the semantic and the agendas
			Move moveUserArea = factory.createMove("move_user_Area");
			moveUserArea.setVariableOperator("SET(" + varArea + "=:move_user_Area:)");
			moveUserArea.addSemantic(semGroupArea);
			moveUserArea.setGrammar(null, g);
			moveUserArea.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserArea);
			agendaRequestArea.addHas(moveUserArea);
			
			Move moveUserAffirmFoodAndGiveArea = factory.createMove("move_user_Affirm_Food_And_Give_Area");
			moveUserAffirmFoodAndGiveArea.setVariableOperator("SET(" + varArea + "=:move_user_Affirm_Food_And_Give_Area:)");
			moveUserAffirmFoodAndGiveArea.addSemantic(semGroupArea);
			moveUserAffirmFoodAndGiveArea.addSemantic(semConfirmFood);
			moveUserAffirmFoodAndGiveArea.addContrarySemantic(semRejectFood);
			moveUserAffirmFoodAndGiveArea.setGrammar(null, g);
			moveUserAffirmFoodAndGiveArea.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmFoodAndRequestArea.addHas(moveUserAffirmFoodAndGiveArea); //implicitly affirm by giving the asked information
			agendaConfirmFood.addHas(moveUserAffirmFoodAndGiveArea); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmPricerangeAndGiveArea = factory.createMove("move_user_Affirm_Pricerange_And_Give_Area");
			moveUserAffirmPricerangeAndGiveArea.setVariableOperator("SET(" + varArea + "=:move_user_Affirm_Pricerange_And_Give_Area:)");
			moveUserAffirmPricerangeAndGiveArea.addSemantic(semGroupArea);
			moveUserAffirmPricerangeAndGiveArea.addSemantic(semConfirmPricerange);
			moveUserAffirmPricerangeAndGiveArea.addContrarySemantic(semRejectPricerange);
			moveUserAffirmPricerangeAndGiveArea.setGrammar(null, g);
			moveUserAffirmPricerangeAndGiveArea.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmPricerangeAndRequestArea.addHas(moveUserAffirmPricerangeAndGiveArea); //implicitly affirm by giving the asked information
			agendaConfirmPricerange.addHas(moveUserAffirmPricerangeAndGiveArea); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmNameAndGiveArea = factory.createMove("move_user_Affirm_Name_And_Give_Area");
			moveUserAffirmNameAndGiveArea.setVariableOperator("SET(" + varArea + "=:move_user_Affirm_Name_And_Give_Area:)");
			moveUserAffirmNameAndGiveArea.addSemantic(semGroupArea);
			moveUserAffirmNameAndGiveArea.addSemantic(semConfirmName);
			moveUserAffirmNameAndGiveArea.addContrarySemantic(semRejectName);
			moveUserAffirmNameAndGiveArea.setGrammar(null, g);
			moveUserAffirmNameAndGiveArea.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmName.addHas(moveUserAffirmNameAndGiveArea); //explicitly affirm and give more information in one step
			
			Move moveUserCorrectArea = factory.createMove("move_user_Correct_Area");
			moveUserCorrectArea.setVariableOperator("SET(" + varArea + "=:move_user_Correct_Area:)");
			moveUserCorrectArea.addSemantic(semGroupArea);
			moveUserCorrectArea.setGrammar(null, g);
			moveUserCorrectArea.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmArea.addHas(moveUserCorrectArea);
			agendaConfirmAreaAndRequestFood.addHas(moveUserCorrectArea);
			agendaConfirmAreaAndRequestPricerange.addHas(moveUserCorrectArea);

			
			//create the moves for the food, set the variable and connect it to the semantic and the agendas
			Move moveUserFood = factory.createMove("move_user_Food");
			moveUserFood.setVariableOperator("SET(" + varFood + "=:move_user_Food:)");
			moveUserFood.addSemantic(semGroupFood);
			moveUserFood.setGrammar(null, g);
			moveUserFood.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserFood);
			agendaRequestFood.addHas(moveUserFood);
			
			Move moveUserAffirmAreaAndGiveFood = factory.createMove("move_user_Affirm_Area_And_Give_Food");
			moveUserAffirmAreaAndGiveFood.setVariableOperator("SET(" + varFood + "=:move_user_Affirm_Area_And_Give_Food:)");
			moveUserAffirmAreaAndGiveFood.addSemantic(semGroupFood);
			moveUserAffirmAreaAndGiveFood.addSemantic(semConfirmArea);
			moveUserAffirmAreaAndGiveFood.addContrarySemantic(semRejectArea);
			moveUserAffirmAreaAndGiveFood.setGrammar(null, g);
			moveUserAffirmAreaAndGiveFood.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmAreaAndRequestFood.addHas(moveUserAffirmAreaAndGiveFood); //implicitly affirm by giving the asked information
			agendaConfirmArea.addHas(moveUserAffirmAreaAndGiveFood); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmPricerangeAndGiveFood = factory.createMove("move_user_Affirm_Pricerange_And_Give_Food");
			moveUserAffirmPricerangeAndGiveFood.setVariableOperator("SET(" + varFood + "=:move_user_Affirm_Pricerange_And_Give_Food:)");
			moveUserAffirmPricerangeAndGiveFood.addSemantic(semGroupFood);
			moveUserAffirmPricerangeAndGiveFood.addSemantic(semConfirmPricerange);
			moveUserAffirmPricerangeAndGiveFood.addContrarySemantic(semRejectPricerange);
			moveUserAffirmPricerangeAndGiveFood.setGrammar(null, g);
			moveUserAffirmPricerangeAndGiveFood.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmPricerangeAndRequestFood.addHas(moveUserAffirmPricerangeAndGiveFood); //implicitly affirm by giving the asked information
			agendaConfirmPricerange.addHas(moveUserAffirmPricerangeAndGiveFood); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmNameAndGiveFood = factory.createMove("move_user_Affirm_Name_And_Give_Food");
			moveUserAffirmNameAndGiveFood.setVariableOperator("SET(" + varFood + "=:move_user_Affirm_Name_And_Give_Food:)");
			moveUserAffirmNameAndGiveFood.addSemantic(semGroupFood);
			moveUserAffirmNameAndGiveFood.addSemantic(semConfirmName);
			moveUserAffirmNameAndGiveFood.addContrarySemantic(semRejectName);
			moveUserAffirmNameAndGiveFood.setGrammar(null, g);
			moveUserAffirmNameAndGiveFood.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmName.addHas(moveUserAffirmNameAndGiveFood); //explicitly affirm and give more information in one step
			
			Move moveUserCorrectFood = factory.createMove("move_user_Correct_Food");
			moveUserCorrectFood.setVariableOperator("SET(" + varFood + "=:move_user_Correct_Food:)");
			moveUserCorrectFood.addSemantic(semGroupFood);
			moveUserCorrectFood.setGrammar(null, g);
			moveUserCorrectFood.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmFood.addHas(moveUserCorrectFood);
			agendaConfirmFoodAndRequestArea.addHas(moveUserCorrectFood);
			agendaConfirmFoodAndRequestPricerange.addHas(moveUserCorrectFood);
			
			
			//create the moves for the pricerange, set the variable and connect it to the semantic and the agendas
			Move moveUserPricerange = factory.createMove("move_user_Pricerange");
			moveUserPricerange.setVariableOperator("SET(" + varPricerange + "=:move_user_Pricerange:)");
			moveUserPricerange.addSemantic(semGroupPricerange);
			moveUserPricerange.setGrammar(null, g);
			moveUserPricerange.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserPricerange);
			agendaRequestPricerange.addHas(moveUserPricerange);
			
			Move moveUserAffirmAreaAndGivePricerange = factory.createMove("move_user_Affirm_Area_And_Give_Pricerange");
			moveUserAffirmAreaAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=:move_user_Affirm_Area_And_Give_Pricerange:)");
			moveUserAffirmAreaAndGivePricerange.addSemantic(semGroupPricerange);
			moveUserAffirmAreaAndGivePricerange.addSemantic(semConfirmArea);
			moveUserAffirmAreaAndGivePricerange.addContrarySemantic(semRejectArea);
			moveUserAffirmAreaAndGivePricerange.setGrammar(null, g);
			moveUserAffirmAreaAndGivePricerange.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmAreaAndRequestPricerange.addHas(moveUserAffirmAreaAndGivePricerange); //implicitly affirm by giving the asked information
			agendaConfirmArea.addHas(moveUserAffirmAreaAndGivePricerange); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmFoodAndGivePricerange = factory.createMove("move_user_Affirm_Food_And_Give_Pricerange");
			moveUserAffirmFoodAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=:move_user_Affirm_Food_And_Give_Pricerange:)");
			moveUserAffirmFoodAndGivePricerange.addSemantic(semGroupPricerange);
			moveUserAffirmFoodAndGivePricerange.addSemantic(semConfirmFood);
			moveUserAffirmFoodAndGivePricerange.addContrarySemantic(semRejectFood);
			moveUserAffirmFoodAndGivePricerange.setGrammar(null, g);
			moveUserAffirmFoodAndGivePricerange.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmFoodAndRequestPricerange.addHas(moveUserAffirmFoodAndGivePricerange); //implicitly affirm by giving the asked information
			agendaConfirmFood.addHas(moveUserAffirmFoodAndGivePricerange); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmNameAndGivePricerange = factory.createMove("move_user_Affirm_Name_And_Give_Pricerange");
			moveUserAffirmNameAndGivePricerange.setVariableOperator("SET(" + varPricerange + "=:move_user_Affirm_Food_And_Give_Pricerange:)");
			moveUserAffirmNameAndGivePricerange.addSemantic(semGroupPricerange);
			moveUserAffirmNameAndGivePricerange.addSemantic(semConfirmName);
			moveUserAffirmNameAndGivePricerange.addContrarySemantic(semRejectName);
			moveUserAffirmNameAndGivePricerange.setGrammar(null, g);
			moveUserAffirmNameAndGivePricerange.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmName.addHas(moveUserAffirmNameAndGivePricerange); //explicitly affirm and give more information in one step
			
			Move moveUserCorrectPricerange = factory.createMove("move_user_Correct_Pricerange");
			moveUserCorrectPricerange.setVariableOperator("SET(" + varPricerange + "=:move_user_Correct_Pricerange:)");
			moveUserCorrectPricerange.addSemantic(semGroupPricerange);
			moveUserCorrectPricerange.setGrammar(null, g);
			moveUserCorrectPricerange.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmPricerange.addHas(moveUserCorrectPricerange);
			agendaConfirmPricerangeAndRequestArea.addHas(moveUserCorrectPricerange);
			agendaConfirmPricerangeAndRequestFood.addHas(moveUserCorrectPricerange);
			
			
			//create the moves for the name, set the variable and connect it to the semantic and the agendas
			Move moveUserName = factory.createMove("move_user_Name");
			moveUserName.setVariableOperator("SET(" + varName + "=:move_user_Name:)" );
			moveUserName.addSemantic(semGroupName);
			moveUserName.setGrammar(null, g);
			moveUserName.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserName);
			agendaRequestName.addHas(moveUserName);
			
			Move moveUserAffirmAreaAndGiveName = factory.createMove("move_user_Affirm_Area_And_Give_Name");
			moveUserAffirmAreaAndGiveName.setVariableOperator("SET(" + varName + "=:move_user_Affirm_Area_And_Give_Name:)");
			moveUserAffirmAreaAndGiveName.addSemantic(semGroupName);
			moveUserAffirmAreaAndGiveName.addSemantic(semConfirmArea);
			moveUserAffirmAreaAndGiveName.addContrarySemantic(semRejectArea);
			moveUserAffirmAreaAndGiveName.setGrammar(null, g);
			moveUserAffirmAreaAndGiveName.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmArea.addHas(moveUserAffirmAreaAndGiveName); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmFoodAndGiveName = factory.createMove("move_user_Affirm_Food_And_Give_Name");
			moveUserAffirmFoodAndGiveName.setVariableOperator("SET(" + varName + "=:move_user_Affirm_Food_And_Give_Name:)");
			moveUserAffirmFoodAndGiveName.addSemantic(semGroupName);
			moveUserAffirmFoodAndGiveName.addSemantic(semConfirmFood);
			moveUserAffirmFoodAndGiveName.addContrarySemantic(semRejectFood);
			moveUserAffirmFoodAndGiveName.setGrammar(null, g);
			moveUserAffirmFoodAndGiveName.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmFood.addHas(moveUserAffirmFoodAndGiveName); //explicitly affirm and give more information in one step
			
			Move moveUserAffirmPricerangeAndGiveName = factory.createMove("move_user_Affirm_Pricerange_And_Give_Name");
			moveUserAffirmPricerangeAndGiveName.setVariableOperator("SET(" + varName + "=:move_user_Affirm_Pricerange_And_Give_Name:)");
			moveUserAffirmPricerangeAndGiveName.addSemantic(semGroupName);
			moveUserAffirmPricerangeAndGiveName.addSemantic(semConfirmPricerange);
			moveUserAffirmPricerangeAndGiveName.addContrarySemantic(semRejectPricerange);
			moveUserAffirmPricerangeAndGiveName.setGrammar(null, g);
			moveUserAffirmPricerangeAndGiveName.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmPricerange.addHas(moveUserAffirmPricerangeAndGiveName); //explicitly affirm and give more information in one step
			
			Move moveUserCorrectName = factory.createMove("move_user_Correct_Name");
			moveUserCorrectName.setVariableOperator("SET(" + varName + "=:move_user_Correct_Name:)");
			moveUserCorrectName.addSemantic(semGroupName);
			moveUserCorrectName.setGrammar(null, g);
			moveUserCorrectName.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaConfirmName.addHas(moveUserCorrectName);
			agendaOfferName.addHas(moveUserCorrectName);
			
			
			//create multislot moves
			Move moveUserMultislot4 = factory.createMove("move_user_multislot_4");
			moveUserMultislot4.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varFood + "=:move_user_Food:;" 
					+ varPricerange + "=:move_user_Pricerange:;" + varName + "=:move_user_Name:)");
			moveUserMultislot4.addSemantic(semGroupArea);
			moveUserMultislot4.addSemantic(semGroupFood);
			moveUserMultislot4.addSemantic(semGroupPricerange);
			moveUserMultislot4.addSemantic(semGroupName);
			moveUserMultislot4.setGrammar(null, g);
			moveUserMultislot4.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot4);
			agendaRequestArea.addHas(moveUserMultislot4);
			agendaRequestFood.addHas(moveUserMultislot4);
			agendaRequestPricerange.addHas(moveUserMultislot4);

			Move moveUserMultislot3afp = factory.createMove("move_user_multislot_3afp");
			moveUserMultislot3afp.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varFood + "=:move_user_Food:;" 
					+ varPricerange + "=:move_user_Pricerange:)");
			moveUserMultislot3afp.addSemantic(semGroupArea);
			moveUserMultislot3afp.addSemantic(semGroupFood);
			moveUserMultislot3afp.addSemantic(semGroupPricerange);
			moveUserMultislot3afp.setGrammar(null, g);
			moveUserMultislot3afp.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot3afp);
			agendaRequestArea.addHas(moveUserMultislot3afp);
			agendaRequestFood.addHas(moveUserMultislot3afp);
			agendaRequestPricerange.addHas(moveUserMultislot3afp);
			
			Move moveUserMultislot3afn = factory.createMove("move_user_multislot_3afn");
			moveUserMultislot3afn.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varFood + "=:move_user_Food:;" 
					+ varName + "=:move_user_Name:)");
			moveUserMultislot3afn.addSemantic(semGroupArea);
			moveUserMultislot3afn.addSemantic(semGroupFood);
			moveUserMultislot3afn.addSemantic(semGroupName);
			moveUserMultislot3afn.setGrammar(null, g);
			moveUserMultislot3afn.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot3afn);
			agendaRequestArea.addHas(moveUserMultislot3afn);
			agendaRequestFood.addHas(moveUserMultislot3afn);
			
			Move moveUserMultislot3anp = factory.createMove("move_user_multislot_3anp");
			moveUserMultislot3anp.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varPricerange + "=:move_user_Pricerange:;" 
					+ varName + "=:move_user_Name:)");
			moveUserMultislot3anp.addSemantic(semGroupArea);
			moveUserMultislot3anp.addSemantic(semGroupPricerange);
			moveUserMultislot3anp.addSemantic(semGroupName);
			moveUserMultislot3anp.setGrammar(null, g);
			moveUserMultislot3anp.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot3anp);
			agendaRequestArea.addHas(moveUserMultislot3anp);
			agendaRequestPricerange.addHas(moveUserMultislot3anp);
			
			Move moveUserMultislot3fnp = factory.createMove("move_user_multislot_3fnp");
			moveUserMultislot3fnp.setVariableOperator("SET(" + varFood + "=:move_user_Food:;" + varPricerange + "=:move_user_Pricerange:;" 
					+ varName + "=:move_user_Name:)");
			moveUserMultislot3fnp.addSemantic(semGroupFood);
			moveUserMultislot3fnp.addSemantic(semGroupPricerange);
			moveUserMultislot3fnp.addSemantic(semGroupName);
			moveUserMultislot3fnp.setGrammar(null, g);
			moveUserMultislot3fnp.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot3fnp);
			agendaRequestFood.addHas(moveUserMultislot3fnp);
			agendaRequestPricerange.addHas(moveUserMultislot3fnp);
			
			Move moveUserMultislot2af = factory.createMove("move_user_multislot_2af");
			moveUserMultislot2af.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varFood + "=:move_user_Food:)");
			moveUserMultislot2af.addSemantic(semGroupArea);
			moveUserMultislot2af.addSemantic(semGroupFood);
			moveUserMultislot2af.setGrammar(null, g);
			moveUserMultislot2af.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot2af);
			agendaRequestArea.addHas(moveUserMultislot2af);
			agendaRequestFood.addHas(moveUserMultislot2af);
			
			Move moveUserMultislot2ap = factory.createMove("move_user_multislot_2ap");
			moveUserMultislot2ap.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varPricerange + "=:move_user_Pricerange:)");
			moveUserMultislot2ap.addSemantic(semGroupArea);
			moveUserMultislot2ap.addSemantic(semGroupPricerange);
			moveUserMultislot2ap.setGrammar(null, g);
			moveUserMultislot2ap.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot2ap);
			agendaRequestArea.addHas(moveUserMultislot2ap);
			agendaRequestPricerange.addHas(moveUserMultislot2ap);
			
			Move moveUserMultislot2an = factory.createMove("move_user_multislot_2an");
			moveUserMultislot2an.setVariableOperator("SET(" + varArea + "=:move_user_Area:;" + varName + "=:move_user_Name:)");
			moveUserMultislot2an.addSemantic(semGroupArea);
			moveUserMultislot2an.addSemantic(semGroupName);
			moveUserMultislot2an.setGrammar(null, g);
			moveUserMultislot2an.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot2an);
			agendaRequestArea.addHas(moveUserMultislot2an);
			
			Move moveUserMultislot2fp = factory.createMove("move_user_multislot_2fp");
			moveUserMultislot2fp.setVariableOperator("SET(" + varFood + "=:move_user_Food:;" + varPricerange + "=:move_user_Pricerange:)");
			moveUserMultislot2fp.addSemantic(semGroupFood);
			moveUserMultislot2fp.addSemantic(semGroupPricerange);
			moveUserMultislot2fp.setGrammar(null, g);
			moveUserMultislot2fp.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot2fp);
			agendaRequestFood.addHas(moveUserMultislot2fp);
			agendaRequestPricerange.addHas(moveUserMultislot2fp);
			
			Move moveUserMultislot2fn = factory.createMove("move_user_multislot_2fn");
			moveUserMultislot2fn.setVariableOperator("SET(" + varFood + "=:move_user_Food:;" + varName + "=:move_user_Name:)");
			moveUserMultislot2fn.addSemantic(semGroupFood);
			moveUserMultislot2fn.addSemantic(semGroupName);
			moveUserMultislot2fn.setGrammar(null, g);
			moveUserMultislot2fn.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot2fn);
			agendaRequestFood.addHas(moveUserMultislot2fn);
			
			Move moveUserMultislot2np = factory.createMove("move_user_multislot_2np");
			moveUserMultislot2np.setVariableOperator("SET(" + varPricerange + "=:move_user_Pricerange:;" + varName + "=:move_user_Name:)");
			moveUserMultislot2np.addSemantic(semGroupPricerange);
			moveUserMultislot2np.addSemantic(semGroupName);
			moveUserMultislot2np.setGrammar(null, g);
			moveUserMultislot2np.setExtractFieldValues(null, ExtractFieldMode.PSEUDO);
			agendaOpen.addHas(moveUserMultislot2np);
			agendaRequestPricerange.addHas(moveUserMultislot2np);
//		}
						
				
		//generate the remaining user moves
		Move moveUserAffirmArea = factory.createMove("move_user_Affirm_Area");
		moveUserAffirmArea.addSemantic(semConfirmArea);
		moveUserAffirmArea.addContrarySemantic(semRejectArea);
		agendaConfirmArea.addHas(moveUserAffirmArea);
		agendaConfirmAreaAndRequestFood.addHas(moveUserAffirmArea);
		agendaConfirmAreaAndRequestPricerange.addHas(moveUserAffirmArea);
		addGrammarToMove(moveUserAffirmArea, grAffirm);
		
		Move moveUserAffirmFood = factory.createMove("move_user_Affirm_Food");
		moveUserAffirmFood.addSemantic(semConfirmFood);
		moveUserAffirmFood.addContrarySemantic(semRejectFood);
		agendaConfirmFood.addHas(moveUserAffirmFood);
		agendaConfirmFoodAndRequestArea.addHas(moveUserAffirmFood);
		agendaConfirmFoodAndRequestPricerange.addHas(moveUserAffirmFood);
		addGrammarToMove(moveUserAffirmFood, grAffirm);
		
		Move moveUserAffirmName = factory.createMove("move_user_Affirm_Name");
		moveUserAffirmName.addSemantic(semConfirmName);
		moveUserAffirmName.addContrarySemantic(semRejectName);
		agendaConfirmName.addHas(moveUserAffirmName);
		addGrammarToMove(moveUserAffirmName, grAffirm);
		
		Move moveUserAffirmPricerange = factory.createMove("move_user_Affirm_Pricerange");
		moveUserAffirmPricerange.addSemantic(semConfirmPricerange);
		moveUserAffirmPricerange.addContrarySemantic(semRejectPricerange);
		agendaConfirmPricerange.addHas(moveUserAffirmPricerange);
		agendaConfirmPricerangeAndRequestArea.addHas(moveUserAffirmPricerange);
		agendaConfirmPricerangeAndRequestFood.addHas(moveUserAffirmPricerange);
		addGrammarToMove(moveUserAffirmPricerange, grAffirm);
		
		Move moveUserDenyArea = factory.createMove("move_user_Deny_Area");
		moveUserDenyArea.addContrarySemantic(semGroupArea);
		moveUserDenyArea.addContrarySemantic(semConfirmArea);
		moveUserDenyArea.addSemantic(semRejectArea);
		agendaConfirmArea.addHas(moveUserDenyArea);
		agendaConfirmAreaAndRequestFood.addHas(moveUserDenyArea);
		agendaConfirmAreaAndRequestPricerange.addHas(moveUserDenyArea);
		addGrammarToMove(moveUserDenyArea, grDeny);
		
		Move moveUserDenyFood = factory.createMove("move_user_Deny_Food");
		moveUserDenyFood.addContrarySemantic(semGroupFood);
		moveUserDenyFood.addContrarySemantic(semConfirmFood);
		moveUserDenyFood.addSemantic(semRejectFood);
		agendaConfirmFood.addHas(moveUserDenyFood);
		agendaConfirmFoodAndRequestArea.addHas(moveUserDenyFood);
		agendaConfirmFoodAndRequestPricerange.addHas(moveUserDenyFood);
		addGrammarToMove(moveUserDenyFood, grDeny);

		Move moveUserDenyName = factory.createMove("move_user_Deny_Name");
		moveUserDenyName.addContrarySemantic(semGroupName);
		moveUserDenyName.addContrarySemantic(semConfirmName);
		moveUserDenyName.addSemantic(semRejectName);
		agendaConfirmName.addHas(moveUserDenyName);
		addGrammarToMove(moveUserDenyName, grDeny);
		
		Move moveUserDenyPricerange = factory.createMove("move_user_Deny_Pricerange");
		moveUserDenyPricerange.addContrarySemantic(semGroupPricerange);
		moveUserDenyPricerange.addContrarySemantic(semConfirmPricerange);
		moveUserDenyPricerange.addSemantic(semRejectPricerange);
		agendaConfirmPricerange.addHas(moveUserDenyPricerange);
		agendaConfirmPricerangeAndRequestArea.addHas(moveUserDenyPricerange);
		agendaConfirmPricerangeAndRequestFood.addHas(moveUserDenyPricerange);
		addGrammarToMove(moveUserDenyPricerange, grDeny);
		
		Move moveUserRequestAlternative = factory.createMove("move_user_Request_Alternative");
		moveUserRequestAlternative.setVariableOperator("SET(" + varNeedAlternative + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestAlternative);
		Grammar grRequestAlternative = grammarRequestAlternative(factory);
		addGrammarToMove(moveUserRequestAlternative, grRequestAlternative);
				
		Move moveUserRequestArea = factory.createMove("move_user_Request_Area");
		moveUserRequestArea.setVariableOperator("SET(" + varNeedArea + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestArea);
		agendaRequestMore.addHas(moveUserRequestArea);
		agendaTellArea.addHas(moveUserRequestArea);
		agendaTellFood.addHas(moveUserRequestArea);
//		agendaTellName.addHas(moveUserRequestArea);
		agendaTellPricerange.addHas(moveUserRequestArea);
		agendaTellAddress.addHas(moveUserRequestArea);
		agendaTellPhone.addHas(moveUserRequestArea);
		agendaTellPostcode.addHas(moveUserRequestArea);
		agendaTellSignature.addHas(moveUserRequestArea);
		Grammar grRequestArea = GrammarRequestArea(factory);
		addGrammarToMove(moveUserRequestArea, grRequestArea);
		
		Move moveUserRequestFood = factory.createMove("move_user_Request_Food");
		moveUserRequestFood.setVariableOperator("SET(" + varNeedFood + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestFood);
		agendaRequestMore.addHas(moveUserRequestFood);
		agendaTellArea.addHas(moveUserRequestFood);
		agendaTellFood.addHas(moveUserRequestFood);
//		agendaTellName.addHas(moveUserRequestFood);
		agendaTellPricerange.addHas(moveUserRequestFood);
		agendaTellAddress.addHas(moveUserRequestFood);
		agendaTellPhone.addHas(moveUserRequestFood);
		agendaTellPostcode.addHas(moveUserRequestFood);
		agendaTellSignature.addHas(moveUserRequestFood);
		Grammar grRequestFood = GrammarRequestFood(factory);
		addGrammarToMove(moveUserRequestFood, grRequestFood);
		
		Move moveUserRequestName = factory.createMove("move_user_Request_Name");
		moveUserRequestName.setVariableOperator("SET(" + varNeedName + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestName);
		agendaRequestMore.addHas(moveUserRequestName);
		agendaTellArea.addHas(moveUserRequestName);
		agendaTellFood.addHas(moveUserRequestName);
//		agendaTellName.addHas(moveUserRequestName);
		agendaTellPricerange.addHas(moveUserRequestName);
		agendaTellAddress.addHas(moveUserRequestName);
		agendaTellPhone.addHas(moveUserRequestName);
		agendaTellPostcode.addHas(moveUserRequestName);
		agendaTellSignature.addHas(moveUserRequestName);
		Grammar grRequestName = GrammarRequestName(factory);
		addGrammarToMove(moveUserRequestName, grRequestName);
		
		Move moveUserRequestPricerange = factory.createMove("move_user_Request_Pricerange");
		moveUserRequestPricerange.setVariableOperator("SET(" + varNeedPricerange + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestPricerange);
		agendaRequestMore.addHas(moveUserRequestPricerange);
		agendaTellArea.addHas(moveUserRequestPricerange);
		agendaTellFood.addHas(moveUserRequestPricerange);
//		agendaTellName.addHas(moveUserRequestPricerange);
		agendaTellPricerange.addHas(moveUserRequestPricerange);
		agendaTellAddress.addHas(moveUserRequestPricerange);
		agendaTellPhone.addHas(moveUserRequestPricerange);
		agendaTellPostcode.addHas(moveUserRequestPricerange);
		agendaTellSignature.addHas(moveUserRequestPricerange);
		Grammar grRequestPricerange = GrammarRequestPricerange(factory);
		addGrammarToMove(moveUserRequestPricerange, grRequestPricerange);
		
		Move moveUserRequestAddress = factory.createMove("move_user_Request_Address");
		moveUserRequestAddress.setVariableOperator("SET(" + varNeedAddress + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestAddress);
		agendaRequestMore.addHas(moveUserRequestAddress);
		agendaTellArea.addHas(moveUserRequestAddress);
		agendaTellFood.addHas(moveUserRequestAddress);
//		agendaTellName.addHas(moveUserRequestAddress);
		agendaTellPricerange.addHas(moveUserRequestAddress);
		agendaTellAddress.addHas(moveUserRequestAddress);
		agendaTellPhone.addHas(moveUserRequestAddress);
		agendaTellPostcode.addHas(moveUserRequestAddress);
		agendaTellSignature.addHas(moveUserRequestAddress);
		Grammar grRequestAddress = GrammarRequestAddress(factory);
		addGrammarToMove(moveUserRequestAddress, grRequestAddress);
		
		Move moveUserRequestPhone = factory.createMove("move_user_Request_Phone");
		moveUserRequestPhone.setVariableOperator("SET(" + varNeedPhone + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestPhone);
		agendaRequestMore.addHas(moveUserRequestPhone);
		agendaTellArea.addHas(moveUserRequestPhone);
		agendaTellFood.addHas(moveUserRequestPhone);
//		agendaTellName.addHas(moveUserRequestPhone);
		agendaTellPricerange.addHas(moveUserRequestPhone);
		agendaTellAddress.addHas(moveUserRequestPhone);
		agendaTellPhone.addHas(moveUserRequestPhone);
		agendaTellPostcode.addHas(moveUserRequestPhone);
		agendaTellSignature.addHas(moveUserRequestPhone);
		Grammar grRequestPhone = GrammarRequestPhone(factory);
		addGrammarToMove(moveUserRequestPhone, grRequestPhone);
		
		Move moveUserRequestPostcode = factory.createMove("move_user_Request_Postcode");
		moveUserRequestPostcode.setVariableOperator("SET(" + varNeedPostcode + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestPostcode);
		agendaRequestMore.addHas(moveUserRequestPostcode);
		agendaTellArea.addHas(moveUserRequestPostcode);
		agendaTellFood.addHas(moveUserRequestPostcode);
//		agendaTellName.addHas(moveUserRequestPostcode);
		agendaTellPricerange.addHas(moveUserRequestPostcode);
		agendaTellAddress.addHas(moveUserRequestPostcode);
		agendaTellPhone.addHas(moveUserRequestPostcode);
		agendaTellPostcode.addHas(moveUserRequestPostcode);
		agendaTellSignature.addHas(moveUserRequestPostcode);
		Grammar grRequestPostcode = GrammarRequestPostcode(factory);
		addGrammarToMove(moveUserRequestPostcode, grRequestPostcode);
		
		Move moveUserRequestSignature = factory.createMove("move_user_Request_Signature");
		moveUserRequestSignature.setVariableOperator("SET(" + varNeedSignature + "=yes)"); 
		agendaOfferName.addHas(moveUserRequestSignature);
		agendaRequestMore.addHas(moveUserRequestSignature);
		agendaTellArea.addHas(moveUserRequestSignature);
		agendaTellFood.addHas(moveUserRequestSignature);
//		agendaTellName.addHas(moveUserRequestSignature);
		agendaTellPricerange.addHas(moveUserRequestSignature);
		agendaTellAddress.addHas(moveUserRequestSignature);
		agendaTellPhone.addHas(moveUserRequestSignature);
		agendaTellPostcode.addHas(moveUserRequestSignature);
		agendaTellSignature.addHas(moveUserRequestSignature);
		Grammar grRequestSignature = GrammarRequestSignature(factory);
		addGrammarToMove(moveUserRequestSignature, grRequestSignature);
		
		Move moveUserBye = factory.createMove("move_user_Bye");
		moveUserBye.setVariableOperator("SET(" + varSaidBye + "=yes)"); 
		agendaOfferName.addHas(moveUserBye);
		agendaRequestMore.addHas(moveUserBye);
		agendaTellArea.addHas(moveUserBye);
		agendaTellFood.addHas(moveUserBye);
//		agendaTellName.addHas(moveUserBye);
		agendaTellPricerange.addHas(moveUserBye);
		agendaTellAddress.addHas(moveUserBye);
		agendaTellPhone.addHas(moveUserBye);
		agendaTellPostcode.addHas(moveUserBye);
		agendaTellSignature.addHas(moveUserBye);
		agendaTellAlternative.addHas(moveUserBye);
		agendaOpen.addHas(moveUserBye);
		agendaRequestArea.addHas(moveUserBye);
		agendaRequestFood.addHas(moveUserBye);
		agendaRequestName.addHas(moveUserBye);
		agendaRequestPricerange.addHas(moveUserBye);
		Grammar grBye = grammarBye(factory);
		addGrammarToMove(moveUserBye, grBye);
		
		Move moveUserThankyou = factory.createMove("move_user_Thankyou");
		agendaOfferName.addHas(moveUserThankyou);
		agendaTellArea.addHas(moveUserThankyou);
		agendaTellFood.addHas(moveUserThankyou);
//		agendaTellName.addHas(moveUserThankyou);
		agendaTellPricerange.addHas(moveUserThankyou);
		agendaTellAddress.addHas(moveUserThankyou);
		agendaTellPhone.addHas(moveUserThankyou);
		agendaTellPostcode.addHas(moveUserThankyou);
		agendaTellSignature.addHas(moveUserThankyou);
		agendaTellAlternative.addHas(moveUserThankyou);
		Grammar grThankyou = grammarThankyou(factory);
		addGrammarToMove(moveUserThankyou, grThankyou);
				

		System.out.println("everything created!");
		
        try {
            factory.manager.saveOntology(factory.onto,
                    factory.manager.getOntologyDocumentIRI(factory.onto));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        Settings.uri = uriSave;
    }
}
