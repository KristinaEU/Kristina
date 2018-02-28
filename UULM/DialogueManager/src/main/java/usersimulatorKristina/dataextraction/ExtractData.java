package usersimulatorKristina.dataextraction;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class ExtractData {

	final String hostname = "mysql-nt.e-technik.uni-ulm.de";
	final String port = "3306";
	final String dbname = "kristina";
	final String user = "kristina";
	final String password = "aiFi7ueQ";
	Connection conn = null;

	String[] sysActions = { "SimpleApologise", "PersonalApologise", "RequestRepeat", "RequestRephrase",
			"StateMissingComprehension", "AdressEmotion", "RequestPresenceEmotion", "RequestReasonForEmotion",
			"CalmDown", "CheerUp", "Console", "ShareJoy", "MorningGreet", "AfternoonGreet", "EveningGreet",
			"PersonalGreet", "SimpleGreet", "Incomprehensible", "GroupOrientedMotivate",
			"IndividualisticallyOrientedMotivate", "PersonalMotivate", "SimpleMotivate", "OnHold", "MorningSayGoodbye",
			"AfternoonSayGoodbye", "EveningSayGoodbye", "WeekendSayGoodbye", "MeetAgainSayGoodbye",
			"PersonalSayGoodbye", "AskMood", "AskPlans", "AskTask", "AskTaskSuccess", "AskWellBeing", "PersonalThank",
			"SimpleThank", "PersonalAnswerThank", "AnswerThank", "Request", "RequestAdditionalInformation",
			"RequestMissingInformation", "ExplicitlyConfirmRecognisedInput", "ImplicitlyConfirmRecognisedInput",
			"RepeatPreviousUtterance", "RephrasePreviousUtterance", "RequestNewspaper", "RequestWeather", "Accept",
			"Reject", "Acknowledge", "Advise", "Declare", "Obligate", "Order", "ReadNewspaper", "ShowVideo",
			"ShowWeather", "ShowWebpage" };
	List<List<UserAction>> allActionsList;

	public ExtractData() {

		allActionsList = new ArrayList<List<UserAction>>();
		openConnection();

		if (conn != null) {
			setActionsLists();
			writeToJSON();
			closeConnection();
		}

	}

	// Open Connection to Database Kristina
	public void openConnection() {
		try {
			System.out.println("* Treiber laden");
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception e) {
			System.err.println("Unable to load driver.");
			e.printStackTrace();
		}
		try {
			System.out.println("* Verbindung aufbauen");
			String url = "jdbc:mysql://" + hostname + "/" + dbname;
			conn = DriverManager.getConnection(url, user, password);

		} catch (SQLException sqle) {
			System.out.println("SQLException: " + sqle.getMessage());
			System.out.println("SQLState: " + sqle.getSQLState());
			System.out.println("VendorError: " + sqle.getErrorCode());
			sqle.printStackTrace();
		}
	}

	// Get Lists from database and calculate Probability
	public void setActionsLists() {
		List<UserAction> tempList = null;
		for (String sysact : sysActions) {
			tempList = lookUpSystemAction(sysact);
			calculateProbability(tempList);
			allActionsList.add(tempList);
		}
	}

	// Write List to JSON
	@SuppressWarnings("unchecked")
	public void writeToJSON() {
		JSONObject obj = new JSONObject();
		for (List<UserAction> list : allActionsList) {
			JSONObject userActsPerSysAct = new JSONObject();
			if (list.size() > 0) {
				String sysAct = list.get(0).prevSysDA;
				for (UserAction u : list) {
					userActsPerSysAct.put(u.userDA, u.probability);
				}
				obj.put(sysAct, userActsPerSysAct);
			}
		}

		// try-with-resources statement based on post comment below :)
		try (FileWriter file = new FileWriter("models/data.json")) {
			file.write(obj.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("\nJSON Object: " + obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<UserAction> calculateProbability(List<UserAction> userAction) {
		double sumActs = 0;
		// Sum up the amount of acts
		for (UserAction u : userAction) {
			sumActs += u.amount;
		}

		// calculate probability for each user dialogueAction
		for (UserAction u : userAction) {
			// System.out.println(u.amount / sumActs);
			u.probability = u.amount / sumActs;
		}
		return userAction;

	}

	public void closeConnection() {
		try {
			System.out.println("* Datenbank-Verbindung beenden");
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	// SQL-Statement
	public List<UserAction> lookUpSystemAction(String systemAction) {
		List<UserAction> actions = new ArrayList<UserAction>();

		try {
			PreparedStatement preparedStatement = conn.prepareStatement("Select a2.dialogueaction, count(*) as amount "
					+ "From Annotation as a1 join Annotation as a2 on a1.dialogueID = a2.dialogueID "
					+ "Where a1.DialogueAction = ? and a1.participant = 'System' and a2.participant = 'User' and a2.dialogueActionNr = a1.DialogueActionNr +1 "
					+ "GROUP BY a2.dialogueaction");
			preparedStatement.setString(1, systemAction);

			ResultSet resultSet = preparedStatement.executeQuery();

			String tempUsrDa;
			int tmpAmount;
			while (resultSet.next()) {
				tempUsrDa = resultSet.getString("DialogueAction");
				tmpAmount = resultSet.getInt("amount");
				actions.add(new UserAction(tempUsrDa, systemAction, tmpAmount));
			}

			return actions;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public static void main(String[] args) {
		@SuppressWarnings("unused")
		ExtractData data = new ExtractData();
		
		/*
		 * UserAction a = new UserAction("AnswerThank", "SimpleThank", 3);
		 * UserAction b = new UserAction("PersonalAnswerThank", "SimpleThank",
		 * 2); UserAction c = new UserAction("Acknowledge", "SimpleThank", 1);
		 * 
		 * List<UserAction> list = new ArrayList<>(); list.add(a); list.add(b);
		 * list.add(c);
		 * 
		 * data.allActionsList.add(list); data.calculateProbability(list);
		 * data.writeToJSON();
		 */

	}
}
