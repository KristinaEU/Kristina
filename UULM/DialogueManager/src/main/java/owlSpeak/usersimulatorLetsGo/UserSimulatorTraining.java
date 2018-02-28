package owlSpeak.usersimulatorLetsGo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.naming.NamingException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import owlSpeak.engine.Core;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.his.DialogueOptimizationInfo;
import owlSpeak.engine.his.DialogueOptimizer;
import owlSpeak.engine.his.UserAction.UserActionType;
import owlSpeak.servlet.OwlSpeakServlet;
import owlSpeak.servlet.document.UsersimulatorDocument;
import jxl.*;
import jxl.write.*;
import jxl.write.Number;


public class UserSimulatorTraining{

	private static boolean printToConsole = false;
	private static int numDialogues = 10;
	private static String strategy = "adaptiveHMM";	

	protected static ServletEngine owlEngine;
	static int debugCounter = 0;

	// private static final String urlToLGus =
	// "http://infinitive.lti.cs.cmu.edu:9090";
	// private static final String urlToLGus = "http://seagull.e-technik.uni-ulm.de:9090";
	private static final String urlToLGus = "http://ibis.e-technik.uni-ulm.de:9090";

	static String loginToUserSimulator(String url)
			throws MalformedURLException, IOException // Login
	// function,
	// takes
	// the
	// login
	// URL
	{
		HttpURLConnection con = (HttpURLConnection) new URL(url)
				.openConnection(); // Create a HttpURLConnection obj
		// System.out.println("Connected!");
		// System.out.println();
		con.setRequestProperty("Connection", "keep-alive"); // set the request
															// property to Keep
															// alive
		String headerName = null;
		String cookie = null;
		// System.out.println(con.getHeaderFieldKey(1));
		for (int i = 1; (headerName = con.getHeaderFieldKey(i)) != null; i++) { //
			if (headerName.equals("Set-Cookie")) // Extract cookie from the
													// header
				cookie = con.getHeaderField(i); //
		}
		try {
			cookie = cookie.substring(0, cookie.indexOf(";"));
		}
		catch (NullPointerException e) {
			System.err.println("LUGS Server seems to be down.");
			e.printStackTrace();
		}
		// String cookieName = cookie.substring(0, cookie.indexOf("="));
		String cookieValue = cookie.substring(cookie.indexOf("=") + 1,
				cookie.length());
		con = (HttpURLConnection) new URL(urlToLGus + "/do_login")
				.openConnection();
		con.setRequestMethod("POST"); // set it to post request
		con.setRequestProperty("Cookie", "session_id=" + cookieValue);
		con.setDoInput(true);
		con.setDoOutput(true);
		PrintWriter writer = new PrintWriter(con.getOutputStream());
		String account = "ntds";
		// String account = "Ulm.Mule";
		String password = "ntds2013"; // Define username and password here
		// String password = "123456"; // Define username and password here
		writer.println("&username=" + account + "&password=" + password
				+ "&action=do_login&http=1");
		writer.close();
		// System.err.println(con.getResponseCode());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				con.getInputStream())); // Reader reads the response
		// String response;
		// while ((response = reader.readLine()) != null) {
		// // System.out.println(response);
		// }
		reader.close();
		return cookieValue; // return the cookie

	}

	// send request commands to user simulator and returns Json response of the
	// User simulator
	@SuppressWarnings("unchecked")
	static String sendRequest(String url, String cookieValue,
			String CommandType, String CommandValue) throws IOException,
			JSONException {
		String Array = null;
		// if(CommandType=="System action" &&
		// CommandValue=="Request(Bus number)")
		// { Array[0]="usermov_Bus"; }
		// if(CommandType=="System action" &&
		// CommandValue=="Request(Departure place)")
		// { Array[0]="usermov_dep"; }
		// if(CommandType=="System action" &&
		// CommandValue=="Request(Arrival place)")
		// { Array[0]="usermov_dest"; }
		// if(CommandType=="System action" &&
		// CommandValue=="Request(Travel time)")
		// { Array[0]="usermov_Time"; }

		JSONObject Obj = new JSONObject(); // create new Json object
		Obj.put(CommandType, CommandValue); // add the commands into it

		// Create a HttpURLConnection object and set the properties as specified
		// in the user simulator manual
		HttpURLConnection con = (HttpURLConnection) new URL(url)
				.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty(
				"User-Agent",
				"User-Agent':'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.1 (KHTML, like Gecko) Chrome/14.0.835.202 Safari/535.1'");
		con.setRequestProperty("Connection", "keep-alive");
		con.setRequestProperty("Content-Type", "application/json");
		con.setRequestProperty("Content-Length", "65530");
		con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Cookie", "session_id=" + cookieValue);

		OutputStreamWriter osw = new OutputStreamWriter(con.getOutputStream());
		osw.write(Obj.toString());
		osw.flush(); // send the request
		osw.close(); // Close connection
		// System.err.println(con.getResponseCode());
		// System.err.println(con.getResponseMessage());
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				con.getInputStream())); // read the response
		String response;

		if (CommandValue != "Start over") {
			if (printToConsole)
				System.out.println("Here is your Response:");
			if (printToConsole)
				System.out.println();
		}

		else if (printToConsole)
			System.out.println();

		String Mainresponse = reader.readLine();
		Array = Mainresponse;
		while ((response = reader.readLine()) != null) {
			if (printToConsole)
				System.out.println(response);
			response = reader.readLine(); // Display and save the response
		}
		// if (printToConsole) System.out.println(Array[0]);
		if (printToConsole)
			System.out.println();
		if (printToConsole)
			System.out.println(Array);
		if (printToConsole)
			System.out.println();
		if (printToConsole)
			System.out.println();
		reader.close();
		return Array; // return Json response of the server
	}

	// Parse the Json data received from the user simulator to parameters of
	// interest
	/**
	 * Parses json input and returns array containing String array containing
	 * Arr[0] = Move; Arr[1] = Confidence; Arr[2] = UserRespType;
	 * 
	 * @param _json
	 * @return String array containing Arr[0] = Move; Arr[1] = Confidence;
	 *         Arr[2] = UserRespType;
	 * @throws IOException
	 */

	public static String[] parseJson(String _json, String _move)
			throws IOException {

		String confidence = null;
		// String Arrival = null;
		// String Departure = null;
		// String Travel = null;
		// String Bus = null;
		String move = ""; // Declaration
		String action = null;
		String whichAction = "";
		// String ModifiedJson = null;
		String userRespType = "";
		String semanticGroupName = "";
		String speak = "";
		String[] actionArr = new String[4];
		String[] str = null;
		String[] str1 = null;
		String newSpeak = "";
		String[] parsedArray1 = null;
		String[] parsedArray2 = null;
		String a = "";
		String b = "";
		String j = "";
		String k = "";
		// String path = "C:\\Users\\mhailese\\Desktop\\output.txt"; // Define a
		// file path
		// the
		// response
		// to be
		// written
		// PrintWriter out = new PrintWriter(new FileWriter(path), true); //
		// Create
		// a
		// file
		// on
		// the
		// path
		// defined
		// System.out.println(Json[1]);
		// out.println(Json); // Write the Json data to the file created
		// System.out.println(); System.out.println();
		// System.out.println(Json[0]);

		try {
			// Object Obj = (JSONObject) new JSONParser().parse(new FileReader(
			// path)); // Create an object out of the file created
			Object Obj = (JSONObject) new JSONParser().parse(new StringReader(
					_json)); // Create an object out of the file created

			// Extract the String with user action field to know the response
			// info(bus number, time travel,.. )
			action = ((JSONObject) Obj).get("User action").toString();
			// Extract the confidence
			confidence = ((JSONObject) Obj).get("Confidence score").toString();

			if (action.contains(",")) {
				str = action.split(",");
				if (printToConsole)
					System.out
							.println("----------------------------------Split: "
									+ str.length);
			} else {
				String[] oneElArr = new String[1];
				oneElArr[0] = action;

				str = oneElArr;
			}

			for (int i = 0; i < str.length; i++) {

				actionArr[i] = str[i];

				if (actionArr[i].contains("Inform")) {
					userRespType += "Inform";
				} else {

					userRespType += actionArr[i].replaceAll(
							"^(\"| |,|\\[)*|(\"| |,|\\])*$", "");
					// if( i == 0){
					// if (str.length == 1)
					// userRespType += actionArr[i].substring(2,
					// actionArr[i].length() - 2);
					// else
					// userRespType += actionArr[i].substring(2,
					// actionArr[i].length() - 1);}
					// else{
					// userRespType += actionArr[i].substring(1,
					// actionArr[i].length() - 2);
					// }
					// UserRespType = UserRespType.substring(2,
					// UserRespType.length() - 2);
				}
			}

			for (int i = 0; i < str.length; i++) {
				// String extraction to get the move string

				// ModifiedJson = Action + "]";

				if (actionArr[i].contains(":")) {
					//
					// String[]
					str1 = actionArr[i].split(":");
					String c = str1[0];
					if (!(c.contains("[")))
						c = "[" + c;
					whichAction = c.substring(9, c.length()); //
					// System.out.println(Action);
				} else {

					whichAction = "";
					if (userRespType.contains("Deny")) {
						int pos = _move.indexOf('_');
						_move = _move.substring(pos + 1);
						pos = _move.indexOf('_');
						_move = _move.substring(pos + 1);
						j = "usermov_deny_" + _move.toLowerCase();
						move += j;
						k = "Deny)";
						speak += k;
					} else if (userRespType.contains("Affirm")) {
						int pos = _move.indexOf('_');
						_move = _move.substring(pos + 1);
						pos = _move.indexOf('_');
						_move = _move.substring(pos + 1);
						j = "usermov_affirm_" + _move.toLowerCase();
						move += j;
						k = "Affirm)";
						speak += k;
					}
					if (userRespType.contains("Hang up")) {
						move += "usermov_hang_up";
						speak += "Hang up)";
					}

					if (userRespType.contains("Non-understanding")) {
						move += "usermov_non_understanding";
						speak += "Non understanding";
					}

				}

				if (whichAction.equals("Bus number")) // If response is bus
														// number
				{
					/*
					 * if (((JSONObject) Obj).get("User action").toString()
					 * .contains(",")) { Bus = ModifiedJson; } else { Bus =
					 * ((JSONObject) Obj).get("User action").toString(); }
					 */
					// String[]
					// str = Bus.split(":");

					speak += str1[1];
					b = "Bus_route_sem";
					semanticGroupName += b;
					a = "external_busnumber_mv";
					move += a;
					// create the move
					// string
					// System.out.println("Move=" + Move);
					// System.out.println("Confidence score="
					// + ((JSONObject) Obj).get("Confidence score"));
				}

				else if (whichAction.equals("Departure place")) // If response
																// is
				// departure place
				{
					/*
					 * if (((JSONObject) Obj).get("User action").toString()
					 * .contains(",")) { Departure = ModifiedJson; } else {
					 * Departure = ((JSONObject) Obj).get("User action")
					 * .toString(); }
					 */
					// String[]
					// str = Departure.split(":");

					speak += str1[1];
					b = "Departure_place_sem";
					semanticGroupName += b;
					a = "external_depplace_mv"; // Extract the string and
												// create the move
												// string
					move += a;
					// System.out.println("Move=" + Move);
					// System.out.println("Confidence score="
					// + ((JSONObject) Obj).get("Confidence score"));
				} else if (whichAction.equals("Arrival place")) // If response
																// is
				// arrival
				// time
				{
					/*
					 * if (((JSONObject) Obj).get("User action").toString()
					 * .contains(",")) { Arrival = ModifiedJson; } else {
					 * Arrival = ((JSONObject)
					 * Obj).get("User action").toString(); }
					 */
					// String[]
					// str = Arrival.split(":");

					speak += str1[1];
					b = "Destination_sem";
					semanticGroupName += b;
					a = "external_destination_mv";
					move += a;
					// create the move
					// string
					// System.out.println("Move=" + Move);
					// System.out.println("Confidence score="
					// + ((JSONObject) Obj).get("Confidence score"));
				} else if (whichAction.equals("Travel time")) // If response is
																// Travel
																// time
				{
					/*
					 * if (((JSONObject) Obj).get("User action").toString()
					 * .contains(",")) { Travel = ModifiedJson; } else { Travel
					 * = ((JSONObject) Obj).get("User action").toString(); }
					 */
					// String[]
					// str = Travel.split(":");

					speak += str1[1];
					b = "Time_sem";
					semanticGroupName += b;
					a = "external_time_mv";
					move += a;
					// create the move
					// string

				}

				int count1 = StringUtils.countMatches(move,
						"external_busnumber_mv");
				int count2 = StringUtils.countMatches(move,
						"external_depplace_mv");
				int count3 = StringUtils.countMatches(move,
						"external_destination_mv");
				int count4 = StringUtils.countMatches(move, "external_time_mv");

				if ((count1 > 1) || (count2 > 1) || (count3 > 1)
						|| (count4 > 1)) {

					speak = speak.substring(0,
							speak.length() - (str1[1].length() + 1));
					semanticGroupName = semanticGroupName.substring(0,
							semanticGroupName.length() - (b.length() + 1));
					move = move.substring(0, move.length() - (a.length() + 1));
				}

				if ((str.length != 1)
						&& ((speak.contains("Affirm)")) || (speak
								.contains("Deny)")))) {
					if (printToConsole)
						System.out
								.println("----------------------------------Problem is here: "
										+ userRespType);
					if (!((userRespType.contains("Affirm")) && (userRespType
							.contains("Deny")))) {
						semanticGroupName = semanticGroupName.substring(0,
								semanticGroupName.length() - 1);
						move = move.substring(0, move.length()
								- (j.length() + 1));
						speak = speak.substring(0, speak.length()
								- (k.length() + 2));
					}
				}

				if ((str.length != 1) && (i != str.length - 1)) {
					userRespType += ",";
					move += ",";
					speak += ",";
					semanticGroupName += ",";
				}

			} // else {

			// }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();

		}
		String[] Arr = new String[6]; // Create an array

		// if (Move != "usermov_non_understanding") {

		// speak = speak.replace(")", " ").replace("", " ")
		// }

		if (move.contains(",")) {
			parsedArray1 = speak.split(",");
			parsedArray2 = semanticGroupName.split(",");
			newSpeak = "";
			speak = "";

			for (int n = 0; n < parsedArray1.length; n++) {

				if (!(parsedArray1[n].equals("Non understanding"))) {
					int pos = parsedArray1[n].indexOf(")");
					parsedArray1[n] = parsedArray1[n].substring(0, pos)
							.replace(" ", "_");
				}

				newSpeak += parsedArray2[n] + " " + parsedArray1[n] + " ";
				speak += parsedArray1[n] + ",";
			}
		} else {

			if (!(speak.equals("Non understanding"))) {
				int pos = speak.indexOf(")");
				speak = speak.substring(0, pos).replace(" ", "_");
			}
			newSpeak = semanticGroupName + " " + speak;
		}

		if ((!userRespType.contains("Hang up"))
				|| (!userRespType.contains("Non understanding"))) {

			if (move.equals("external_busnumber_mv,external_depplace_mv,external_destination_mv,external_time_mv"))
				move = "multi_slot4_mv";
			else if (move.contains("external_busnumber_mv")
					&& move.contains("external_depplace_mv")
					&& move.contains("external_destination_mv"))
				move = "multi_slot3a_mv";
			else if (move.contains("external_depplace_mv")
					&& move.contains("external_destination_mv")
					&& move.contains("external_time_mv"))
				move = "multi_slot3b_mv";
			else if (move.contains("external_depplace_mv")
					&& move.contains("external_time_mv")
					&& move.contains("external_busnumber_mv"))
				move = "multi_slot3c_mv";
			else if (move.contains("external_destination_mv")
					&& move.contains("external_time_mv")
					&& move.contains("external_busnumber_mv"))
				move = "multi_slot3d_mv";
			else if (move.contains("external_busnumber_mv")
					&& move.contains("external_depplace_mv"))
				move = "multi_slot2a_mv";
			else if (move.contains("external_depplace_mv")
					&& move.contains("external_destination_mv"))
				move = "multi_slot2b_mv";
			else if (move.contains("external_destination_mv")
					&& move.contains("external_time_mv"))
				move = "multi_slot2c_mv";
			else if (move.contains("external_destination_mv")
					&& move.contains("external_busnumber_mv"))
				move = "multi_slot2d_mv";
			else if (move.contains("external_depplace_mv")
					&& move.contains("external_time_mv"))
				move = "multi_slot2e_mv";
			else if (move.contains("external_busnumber_mv")
					&& move.contains("external_time_mv"))
				move = "multi_slot2f_mv";

		}

		Arr[0] = move; // Add Move string to the array
		Arr[1] = confidence; // Add Confidence to the array
		Arr[2] = userRespType; // Add Response Type(Inform, deny,
								// non-understanding...) to the array
		Arr[3] = speak;
		Arr[4] = semanticGroupName;
		Arr[5] = newSpeak;
		if (printToConsole)
			System.out.println("User Response Type:" + Arr[2]);
		if (printToConsole)
			System.out.println("Move=" + Arr[0]);
		if (printToConsole)
			System.out.println("Confidence score=" + Arr[1]);
		if (printToConsole)
			System.out.println("speak=" + Arr[3]);

		return Arr; // Return array
		// array[0]=Move; array[1]=Confidence;
		// array[1]=Arrival;array[1]=Departure;array[1]=Travel;array[1]=Bus;
		/*
		 * MockHttpServletRequest request=new MockHttpServletRequest();
		 * MockHttpServletResponse response=new MockHttpServletResponse();
		 * request.addParameter("Confidence", Confidence);
		 * request.addParameter("Arrival", Arrival);
		 * request.addParameter("Departure", Departure);
		 * request.addParameter("Travel", Travel); request.addParameter("Bus",
		 * Bus); request.addParameter("Move", Move); toEngine(request,
		 * response);
		 */

	}

	public static void parseJsonUserGoalDB(String Json,
			Map<String, String> variable, DBResultWriter ldb, int hangUp,
			int exchanges, String callID) throws IOException {

		String Arrival = null;
		String Departure = null;
		String Travel = null;
		String Bus = null;
		int i = 0;
		String[] Arr1 = new String[4];
		try {

			Object Obj = (JSONObject) new JSONParser().parse(new StringReader(
					Json)); // Create an object out of the file created

			Departure = ((JSONObject) Obj).get("Departure place").toString();
			Arrival = ((JSONObject) Obj).get("Arrival place").toString();
			Travel = ((JSONObject) Obj).get("Travel time").toString();
			Bus = ((JSONObject) Obj).get("Bus number").toString();
		}

		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();

		}
		if (hangUp != 1) {
			if (printToConsole)
				System.out.println("Here your Information:");
			for (String k : variable.keySet()) {

				String var = variable.get(k);
				k = k.substring(0, k.length() - 4).replace("_", " ");

				if (var != null) {
					Arr1[i] = var.replace("_", " ");
					if (printToConsole)
						System.out.println(k + ": " + var.replace("_", " "));
				}

				else if (var == null) {
					Arr1[i] = var;
					if (printToConsole)
						System.out.println(k + ": No Information requested");
				}
				i++;
			}
		}
		if (printToConsole)
			System.out.println("Thank you for using LetsGoBus!");

		String[] Arr2 = new String[4];
		Arr2[0] = Arrival.trim();
		Arr2[1] = Bus.trim();
		Arr2[2] = Departure.trim();
		Arr2[3] = Travel.trim();

		// ldb.insertNewEntry(callID, strategy, Arr2[0], Arr1[2], Arr2[1],
		// Arr1[0], Arr2[2],
		// Arr1[1], Arr2[3], Arr1[3], hangUp, exchanges);

	}

	private static String move2command(String move, Map<String, String> variable) {
		String command = null;

		int pos = move.indexOf("_");
		move = move.substring(pos + 1);
		pos = move.indexOf("_");
		command = move.substring(0, pos);
		move = move.substring(pos + 1);
		// if command is Request
		if ("Request".equalsIgnoreCase(command))
			command += "(" + move.replace('_', ' ') + ")";
		// if command is Confirm
		else if ("Confirm".equalsIgnoreCase(command)) {
			for (String k : variable.keySet()) {
				String var = variable.get(k);

				command += "(" + move.replace('_', ' ') + ":"
						+ var.replace('_', ' ') + ")";
			}

		}
		return command;
	}

	public static String[] getHeader() {
		Vector<String> headers = new Vector<String>();

		// id
		headers.add("id");

		// Bus number
		headers.add("Bus_number_user");

		headers.add("Bus_number_system");

		// Arrival place
		headers.add("Arrival_place_user");

		headers.add("Arrival_place_system");

		// Departure place
		headers.add("Departure_place_user");

		headers.add("Departure_place_system");

		// Time
		headers.add("Time_user");

		headers.add("Time_system");

		// hangUp
		headers.add("Hang_up");

		headers.add("Success");

		// exchanges
		headers.add("Exchanges");

		// prikey
		headers.add("");

		String[] stringVector = new String[headers.size()];

		int i = 0;
		for (String d : headers) {
			stringVector[i++] = d;
		}

		return stringVector;
	}

	// communication between usersimulator and OwlSpeak starts here and ends
	// here
	public static void main(String[] args) throws IOException, WriteException, JSONException,
			NamingException, ParseException {
		/*
		 * ------------------------------------------- Attributes and Values to
		 * USER SIMULATOR ------------------------------------------- 'System
		 * action' 'Request(Open)', 'Request(Bus number)', 'Request(Departure
		 * place)', 'Request(Arrival place)', 'Request(Travel time)',
		 * 'Confirm(Bus number:VALUE1)', 'Confirm(Departure place:VALUE)',
		 * 'Confirm(Arrival place:VALUE)', 'Confirm(Travel time:VALUE)'
		 * 'Command' 'Start over', 'Get user goal'
		 * ---------------------------------------------- Attributes and Values
		 * from USER SIMULATOR ----------------------------------------------
		 * 'User action' 'Inform(Bus number:VALUE)', 'Inform(Departure
		 * place:VALUE)', 'Inform(Arrival place:VALUE)', 'Inform(Travel
		 * time:VALUE)', 'Affirm', 'Deny', 'Non-understanding' 'Confidence
		 * score' Numerical value ranging from 0 to 1
		 */

		if (printToConsole)
			System.out.println("starting...");

		System.setProperty("owlSpeak.settings.file", "C:/OwlSpeak/settings.xml");

		ServletEngine owlEngine = new ServletEngine();

		String whereAmI = "http://beaver.e-technik.uni-ulm.de:8080/owlSpeak";
		final String user = "user";

		String callID = OwlSpeakServlet.reset(owlEngine, whereAmI, user);
// Attention!! Reset of SBP		
		OwlSpeakServlet.reset(owlEngine, whereAmI, user);
		OwlSpeakServlet.resetSBP(owlEngine);
		
		int successCount = 0;
		int hangUpCount = 0;
		int errorCount = 0;
		
//		ArrayLists for JExcel		
		ArrayList<Double> optimizerTimes = new ArrayList<Double>();
		ArrayList<Double> dialogueTimes = new ArrayList<Double>();
		ArrayList<Integer> numberExchanges = new ArrayList<Integer>();
		ArrayList<Integer> success = new ArrayList<Integer>();
		
		// URL for login
		String url1 = urlToLGus;
		// URL for request
		String ulr2 = urlToLGus + "/services/rest/usr_sim";

		DBResultWriter.connect();
		DBResultWriter ldb = new DBResultWriter();
		ldb.createTable();
		ldb.createMetaTable();

		// Login request
		String cookieValue = loginToUserSimulator(url1);
		if (printToConsole)
			System.out.println("Logged on to user simulator");
		for (int dialogueNo = 0; dialogueNo < numDialogues; dialogueNo++) {
//			if (printToConsole)
				System.out.println("################### new dialogue ("
						+ (dialogueNo+1) + ") #######################");			
			
			// determine first user move and execution time measurement			
				
			double baseTime = System.nanoTime();
			double dialogueBaseTime = baseTime;
			double owlTime = 0;
			double optimizerTime = 0;
			double dialogueTime  = 0;
			UsersimulatorDocument systemMove = (UsersimulatorDocument) (OwlSpeakServlet
					.processRequest(whereAmI, null, user, "0", owlEngine));			
			owlTime = System.nanoTime() - baseTime;			
			System.out.println("First move time = " + owlTime);
			
			
			String move = systemMove.getSystemMove();
			String agenda = systemMove.getAgenda();
			Map<String, String> m = systemMove.getVariables();
			Vector<String> userMoveList = systemMove.getAllowedUserMovesList();
			
//Attention!! THIS WAS ADDED			
			Core.getCore().optimizationInfo.reset();
//-------------------------			
			Core.getCore().optimizationInfo.addSummaryBelief(systemMove
					.getSbnp());
			int reward = Core.getCore().getRewardFromAgenda(
					systemMove.getAgendaObj().get(0), "user");
			int iq = OwlSpeakServlet.getQualityObject().getCurrentIQ();
			
//Attention!! THIS WAS ADDED			
			if (reward == 20){
				successCount++;
				success.add(1);
			}
			if (reward == -100){
				hangUpCount++;
				success.add(0);
			}
//-------------------------		
			
			Core.getCore().optimizationInfo.addReward(reward);
			System.out.println("IQ/Reward/Return: " + iq + "/" + reward + " /"
					+ Core.getCore().optimizationInfo.getReturnValue());			

			boolean exit = false;
			boolean errorOccurred = false;
			String command = "";
			String jsonResponse = "";

			String userGoal = null;
			String userMove = "";
			String confidence = "";
			String UserRespType = "";
			int hangUp = 0;

			String newSpeak = "";

			int exchanges = 0;
			if (printToConsole)
				System.out.println("System move:\t" + agenda + "!" + move);

			while (exit == false) {

				if (agenda.startsWith("Exit")) {
					command = "Get user goal";
					exit = true;

				} else {
					// map move to JSON command
					command = move2command(move, m);
					if (printToConsole)
						System.out.println(command);
					
					// request to the user simulator and execution time measurement
					baseTime = System.nanoTime();
					double lgusTime = 0;
					jsonResponse = sendRequest(ulr2, cookieValue,
							"System action", command);
					lgusTime = System.nanoTime() - baseTime;			
					System.out.println("LGus time = " + lgusTime);
				}
								
				
				if (jsonResponse == null || jsonResponse.equalsIgnoreCase("null")) {
					errorOccurred = true;
					break;
				}
				
				if (!(agenda.startsWith("Exit"))) {
					try{
						 // parse the Json response of user simulator					
						String[] response = parseJson(jsonResponse, move);
						userMove = response[0];
						confidence = response[1];
						UserRespType = response[2];

						newSpeak = response[5];

					}catch(ArrayIndexOutOfBoundsException e){
						errorOccurred = true;
						break;
					}
					
					// response of OwlSpeak engine
					if (printToConsole)
						System.out.println("User move:\t" + agenda + "!"
								+ userMove);
					
					
					if (userMove.equals("usermov_hang_up")) {

						// systemMove = (UserSimDoc)
						// (OwlSpeakServlet.processWork(
						// whereAmI, agenda, userMove, confidence,
						// newSpeak, user, owlEngine));
						// exchanges++;
						// move = systemMove.getSystemMove();
						// agenda = systemMove.getAgenda();
						// m = systemMove.getVariables();
						// userMoveList = systemMove.getAllowedUserMovesList();
						//
						// if (printToConsole)
						// System.out.println("System move:\t " + agenda + "!"
						// + move);

						command = "Get user goal";
						hangUp = 1;
						
						hangUpCount++;
						success.add(0); //Graphical representation

						Core.getCore().optimizationInfo.addReward(-100);
						System.out.println(" (Reward/Return: -100 /"
								+ Core.getCore().optimizationInfo
										.getReturnValue() + ")");

						exit = true;

					}

					else if ((!(userMoveList.contains(userMove)))
							|| ((UserRespType.contains("Affirm")) && (UserRespType
									.contains("Deny")))) {

						systemMove = (UsersimulatorDocument) (OwlSpeakServlet
								.processRequest(whereAmI, UserActionType.OOG,
										user, "0", owlEngine));

						move = systemMove.getSystemMove();
						agenda = systemMove.getAgenda();
						m = systemMove.getVariables();
						userMoveList = systemMove.getAllowedUserMovesList();
						if (printToConsole)
							System.out.println("System move:\t " + agenda + "!"
									+ move);
						exchanges++;

						Core.getCore().optimizationInfo
								.addSummaryBelief(systemMove.getSbnp());
						reward = Core.getCore().getRewardFromAgenda(
								systemMove.getAgendaObj().get(0), "user");
						iq = OwlSpeakServlet.getQualityObject().getCurrentIQ();
						Core.getCore().optimizationInfo.addReward(reward);
						
						if (reward == 20){
							successCount++;
							success.add(1);
						}
						if (reward == -100){
							hangUpCount++;
							success.add(0);
						}
						
						System.out.println("IQ/Reward/Return: " + iq + "/" + reward + " /"
								+ Core.getCore().optimizationInfo.getReturnValue());
					}

					else {

						systemMove = (UsersimulatorDocument) (OwlSpeakServlet.processWork(
								whereAmI, agenda, userMove, confidence,
								newSpeak, user, owlEngine));
						exchanges++;
						move = systemMove.getSystemMove();
						agenda = systemMove.getAgenda();
						m = systemMove.getVariables();
						userMoveList = systemMove.getAllowedUserMovesList();

						if (printToConsole)
							System.out.println("System move:\t " + agenda + "!"
									+ move);

						Core.getCore().optimizationInfo
								.addSummaryBelief(systemMove.getSbnp());
						reward = Core.getCore().getRewardFromAgenda(
								systemMove.getAgendaObj().get(0), "user");
						iq = OwlSpeakServlet.getQualityObject().getCurrentIQ();
						Core.getCore().optimizationInfo.addReward(reward);
						
						if (reward == 20){
							successCount++;
							success.add(1);
						}
						if (reward == -100){
							hangUpCount++;
							success.add(0);
						}
						
						System.out.println("IQ/Reward/Return: " + iq + "/" + reward + " /"
								+ Core.getCore().optimizationInfo.getReturnValue());
					}
				}
			}
			
			if (errorOccurred) {
				System.err.println("JSON response error. Skipping this dialogue.");	
				errorCount++;
			}
			else {
				// Modifications
				DialogueOptimizationInfo info = owlEngine.systemCore.optimizationInfo;
				DialogueOptimizer opt = new DialogueOptimizer();
				
				//Optimizer call and execution time measurement				
				
				baseTime = System.nanoTime();
				opt.optimize(info);
				optimizerTime = System.nanoTime() - baseTime;
				optimizerTimes.add(optimizerTime);
				System.out.println("Optimizer time = " + optimizerTime);

				if (printToConsole)
					System.out.println("Get user goal");
				userGoal = sendRequest(ulr2, cookieValue, "Command", command);
				parseJsonUserGoalDB(userGoal, m, ldb, hangUp, exchanges, callID);
				
				numberExchanges.add(exchanges+1); //Added
				
				if (printToConsole)
					System.out.println("Exchanges: " + exchanges);				
			}

			if (printToConsole)
				System.out.println();

			jsonResponse = sendRequest(ulr2, cookieValue, "Command",
					"Start over");
			callID = OwlSpeakServlet.reset(owlEngine, whereAmI, user);
			dialogueTime = System.nanoTime() - dialogueBaseTime;
			if (printToConsole)
				System.out.println("Overall Dialogue Time = " + (dialogueTime*0.000000001));
			dialogueTimes.add(dialogueTime*0.000000001);
			
		}
		
		System.out.println();
		System.out.println("Result");
		System.out.println("Success: " + successCount +" times.");
		System.out.println("Hang Up: " + hangUpCount +" times.");
		System.out.println("Skipped: " + errorCount +" times.");
		System.out.println();
		
		try{
			
			String fileName = "C:\\Users\\rlago\\Documents\\Thesis\\file.xls";
			WritableWorkbook workbook = Workbook.createWorkbook(new File(fileName));
			WritableSheet sheet = workbook.createSheet("Sheet1", 0);
			
			Label optimizer = new Label(0, 0, "optimizer times");
			Label exchange = new Label(2, 0, "exchanges");
			Label successDialogue = new Label(4, 0, "Success");
			Label dialogue = new Label(6, 0, "dialogue times");
			
			sheet.addCell(optimizer);
			sheet.addCell(exchange);
			sheet.addCell(successDialogue);
			sheet.addCell(dialogue);
			
			for(int i = 0; i<optimizerTimes.size(); i++){
				Number optiTimes = new Number(1, i, optimizerTimes.get(i));				
				sheet.addCell(optiTimes);				
			}
			for(int i = 0; i<numberExchanges.size(); i++){			
				Number exchangesNum = new Number(3, i, numberExchanges.get(i));
				sheet.addCell(exchangesNum);
			}
			for(int i = 0; i<success.size(); i++){			
				Number dialogueResult = new Number(5, i, success.get(i));
				sheet.addCell(dialogueResult);
			}
			for(int i = 0; i<optimizerTimes.size(); i++){
				Number dialogTimes = new Number(7, i, dialogueTimes.get(i));				
				sheet.addCell(dialogTimes);	
			}				
			workbook.write();
			workbook.close();
		} catch(WriteException e){				
		}

		
		DBResultWriter.disconnect();

		// second request to the user Simulator
		// map move to JSON command
		/*
		 * command = move2command(move, m); System.out.println(command);
		 * jsonResponse = sendRequest(ulr2, cookieValue, "System action",
		 * command);
		 * 
		 * response = parseJson(jsonResponse); // parse the Json response // of
		 * user simulator userMove = response[0];
		 * System.out.println("System move:\t" + agenda + "!" + userMove);
		 */

		// Modifications

		// Collection<Belief> beliefsCollection =
		// OSFactory.getAllBeliefInstances();

		// Vector<SummaryBelief> sumBeliefVector = owlEngine.systemCore.actua
	}
}
