//dividing places to covered and uncovered

package owlSpeak.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.Utterance;
import owlSpeak.Variable;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;

//TODO check folders of files to be created
public class LetsGousersim {
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

		// Generate Semantics
		SemanticGroup S1 = factory.createSemanticGroup("Bus_route_sem");

		SemanticGroup S2 = factory.createSemanticGroup("Departure_place_sem");

		SemanticGroup S3 = factory.createSemanticGroup("Destination_sem");

		SemanticGroup S4 = factory.createSemanticGroup("Time_sem");

		Semantic S5 = factory.createSemantic("Confirm_Destination_sem");
		S5.setConfirmationInfo(true);

		Semantic S6 = factory.createSemantic("Confirm_Departure_place_sem");
		S6.setConfirmationInfo(true);

		Semantic S7 = factory.createSemantic("Confirm_Time_sem");
		S7.setConfirmationInfo(true);

		Semantic S8 = factory.createSemantic("Confirm_Bus_route_sem");
		S8.setConfirmationInfo(true);

		// Generate Variables
		Variable v1 = factory.createVariable("Bus_route_var");
		S1.addVariable(v1); // connecting the variable to the corresponding
							// Semantic
		v1.addBelongsToSemantic(S1.asSemanticGroup()); // Connecting the Semantic to the
										// corresponding Variable

		Variable v2 = factory.createVariable("Departure_place_var");
		S2.addVariable(v2);
		v2.addBelongsToSemantic(S2.asSemanticGroup());

		Variable v3 = factory.createVariable("Destination_var");
		S3.addVariable(v3);
		v3.addBelongsToSemantic(S3.asSemanticGroup());

		Variable v4 = factory.createVariable("Time_var");
		S4.addVariable(v4);
		v4.addBelongsToSemantic(S4.asSemanticGroup());

		// Generate Agendas
		Agenda A1 = factory.createAgenda("Destination_agenda");
		A1.addMustnot(S5); // Fill the MustNot field in the Agenda
		A1.setRole(A1.getRole(), "collection"); // Setting the Role whether it
												// is confirmation or collection

		Agenda A2 = factory.createAgenda("Departure_place_agenda");
		A2.addMustnot(S6);
		A2.setRole(A2.getRole(), "collection");

		Agenda A3 = factory.createAgenda("Bus_routes_agenda");
		A3.addMustnot(S8);
		A3.setRole(A3.getRole(), "collection");

		Agenda A4 = factory.createAgenda("Time_agenda");
		A4.addMustnot(S7);
		A4.setRole(A4.getRole(), "collection");

		Agenda openAgenda = factory.createAgenda("open_agenda");
		openAgenda.setRole(openAgenda.getRole(), "collection");

		Agenda A6 = factory.createAgenda("Here_your_info_Agenda");
		A6.addRequires(S1);
		A6.addRequires(S2);
		A6.addRequires(S3);
		A6.addRequires(S4);
		A6.addRequires(S5);
		A6.addRequires(S6);
		A6.addRequires(S7);
		A6.addRequires(S8);
		A6.setRole(A6.getRole(), "collection");

		Agenda A7 = factory.createAgenda("not_covered_Agenda");
		A7.addRequires(S5);
		A7.addRequires(S6);
		A7.addRequires(S2);
		A7.addRequires(S3);
		A7.setRole(A7.getRole(), "collection");

		Agenda A8 = factory.createAgenda("Confirm_bus_route_Agenda");
		A8.addRequires(S1);
		A8.addMustnot(S8);
		A8.setRole(A8.getRole(), "confirmation");

		Agenda A9 = factory.createAgenda("Confirm_Destination_Agenda");
		A9.addRequires(S3);
		A9.addMustnot(S5);
		A9.setRole(A9.getRole(), "confirmation");

		Agenda A10 = factory.createAgenda("Confirm_Departure_place_Agenda");
		A10.addRequires(S2);
		A10.addMustnot(S6);
		A10.setRole(A10.getRole(), "confirmation");

		Agenda A11 = factory.createAgenda("Confirm_Time_Agenda");
		A11.addRequires(S4);
		A11.addMustnot(S7);
		A11.setRole(A11.getRole(), "confirmation");
		
		Agenda master = factory.createAgenda("masteragenda");
		master.setIsMasterBool(false, true);
		master.addNext(openAgenda);

		// Generate system moves
		Move m4 = factory.createMove("sysmov_Request_Bus_number");
		A3.addHas(m4); // Adding the move to the corresponding Agenda

		Move m5 = factory.createMove("sysmov_Confirm_Bus_number");
		A8.addHas(m5);

		Move m6 = factory.createMove("sysmov_Confirm_Departure_place");
		A10.addHas(m6);

		Move m7 = factory.createMove("sysmov_Confirm_Arrival_place");
		A9.addHas(m7);

		Move m8 = factory.createMove("sysmov_Confirm_Travel_time");
		A11.addHas(m8);

		Move m9 = factory.createMove("sysmov_Request_Open");
		openAgenda.addHas(m9);

		Move m10 = factory.createMove("sysmov_Inform_Bus_information");
		A6.addHas(m10);

		Move m11 = factory.createMove("sysmov_Request_Departure_place");
		A2.addHas(m11);

		Move m12 = factory.createMove("sysmov_Request_Travel_time");
		A4.addHas(m12);

		Move m13 = factory.createMove("sysmov_Request_Arrival_place");
		A1.addHas(m13);

		Move m14 = factory.createMove("sysmov_Not_covered");
		A7.addHas(m14);
		
		Utterance dummyUtterance = factory.createUtterance("dummyUtterance");
		m4.setUtterance(null, dummyUtterance);
		m5.setUtterance(null, dummyUtterance);
		m6.setUtterance(null, dummyUtterance);
		m7.setUtterance(null, dummyUtterance);
		m8.setUtterance(null, dummyUtterance);
		m9.setUtterance(null, dummyUtterance);
		m10.setUtterance(null, dummyUtterance);
		m11.setUtterance(null, dummyUtterance);
		m12.setUtterance(null, dummyUtterance);
		m13.setUtterance(null, dummyUtterance);
		m14.setUtterance(null, dummyUtterance);



		// Generate user Moves using 4 text files
//		String folder = "D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\";
//		String file_temp2 = folder + "all_places_covered.txt";
//		String file_temp3 = folder + "all_places_not_covered.txt";
//		String file_temp1 = folder + "Bus_routes_covered.txt";
//		String file_temp4 = folder + "Bus_routes_not_covered.txt";
		String allPlacesCoveredFile = LetsGousersim.class.getResource("all_places_covered.txt").getPath();
		String allPlacesNotCoveredFile = LetsGousersim.class.getResource("all_places_not_covered.txt").getPath();
		String busRoutesCoveredFile = LetsGousersim.class.getResource("Bus_routes_covered.txt").getPath();
		String busRoutesNotCoveredFile = LetsGousersim.class.getResource("Bus_routes_not_covered.txt").getPath();

		// 1) Generating Moves for covered_places
		File allPlaces_covered = new File(allPlacesCoveredFile);

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
			Move m1 = factory.createMove("usermov_Dest_" + name);
			m1.setVariableOperator("SET(" + v3 + "=" + name + ")");
			m1.addSemantic(S3);
			A1.addHas(m1);
			openAgenda.addHas(m1);

			// Create the user departure Move, set the variable and connect it
			// to the semantic and the Agendas
			Move m2 = factory.createMove("usermov_Dep_" + name);
			m2.setVariableOperator("SET(" + v2 + "=" + name + ")");
			m2.addSemantic(S2);
			A2.addHas(m2);
			openAgenda.addHas(m2);

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

			name = name.replace("*", "");
			name = name.replace(" ", "_");

			// Create the user destination Move, set the variable and connect it
			// to the semantic and the Agendas
			Move m1 = factory.createMove("usermov_Dest_" + name);
			m1.setVariableOperator("SET(" + v3 + "=" + name + ")");
			m1.addSemantic(S3);
			A1.addHas(m1);
			openAgenda.addHas(m1);

			// Create the user departure Move, set the variable and connect it
			// to the semantic and the Agendas
			Move m2 = factory.createMove("usermov_Dep_" + name);
			m2.setVariableOperator("SET(" + v2 + "=" + name + ")");
			m2.addSemantic(S2);
			A2.addHas(m2);
			openAgenda.addHas(m2);

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
			Move m3 = factory.createMove("usermov_Bus_" + name);
			m3.setVariableOperator("SET(" + v1 + "=" + name + ")");
			m3.addSemantic(S1);
			A3.addHas(m3);
			openAgenda.addHas(m3);

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
			Move m3 = factory.createMove("usermov_Bus_" + name);
			m3.setVariableOperator("SET(" + v1 + "=" + name + ")");
			m3.addSemantic(S1);
			A3.addHas(m3);
			openAgenda.addHas(m3);

		}
		fileScanner4.close();

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
				Move m1 = factory.createMove("usermov_Time_" + time_final);
				m1.setVariableOperator("SET(" + v4 + "=" + time_final + ")");
				m1.addSemantic(S4);
				A4.addHas(m1);
				openAgenda.addHas(m1);

				System.out.println("time_final = " + time_final);
			}
		}

		// Generate Confirmation and Deny user Moves, connecting them to
		// Semantics and Agendas
		Move m15 = factory.createMove("usermov_Affirm_bus_route");
		m15.addSemantic(S8);
		A8.addHas(m15);

		Move m16 = factory.createMove("usermov_Deny_bus_route");

		Move m17 = factory.createMove("usermov_Affirm_destination");
		m17.addSemantic(S5);
		A9.addHas(m17);

		Move m18 = factory.createMove("usermov_Deny_destination");

		Move m19 = factory.createMove("usermov_Affirm_departure_place");
		m19.addSemantic(S6);
		A10.addHas(m19);

		Move m20 = factory.createMove("usermov_Deny_departure_place");

		Move m21 = factory.createMove("usermov_Affirm_time");
		m21.addSemantic(S7);
		A11.addHas(m21);

		Move m22 = factory.createMove("usermov_Deny_time");

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
