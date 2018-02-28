package owlSpeak.imports;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;

import owlSpeak.OSFactory;
import owlSpeak.Variable;
import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;

/**
 * Class for the database connection of the IDACO spoken language operation assistant
 * 
 * @author Nadine Gerstenlauer
 */
public class IdacoDB {

	public static Connection con = null;
	private static final String url = "jdbc:sqlserver://pheasant.e-technik.uni-ulm.de;instanceName=sqlexpress";
	private final static String user = "idaco";
	private final static String password = "ocadi!";
	
	public IdacoDB () {
		if (!connect())
			System.err.println("Problem with initializing IdacoDB");
	}
	
	public static boolean connect () {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(url, user, password);
			System.out.println("Connected to IdacoDB");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean connected () {
		try {
			return (con != null && !con.isClosed());
		} catch (SQLException e) {
			return false;
		}
	}
	
	public boolean disconnect () {
		try {
			if (con != null) {
				con.close();
			}
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public void finalize () {
		disconnect();
	}
		
	
	// Reading variables from database at the beginning of each move
	public static void getIdacoVariables () {
		if (!connected()){
			connect();
		}
		OwlSpeakOntology onto = Core.getCore().findOntology("idaco");
		Collection<Variable> varCol = IDACO.getVars(onto);
		int sessionID = getCurrentSession();
		
		for (Variable v : varCol) {
			String name = v.getLocalName();
			String val;
			if (name.equals("var_StaffComplete")) {
				val = sessionTeamComplete(sessionID);
				CoreMove.setVariable(name, val, onto.factory, Core.getCore(), onto.beliefSpace[Settings.getuserpos("user")], "user");
			}
		}
	}
	
	
	// Updating variables in database after each move
	public static void updateIdacoVariables () {
		if (!connected()){
			connect();
		}
		OwlSpeakOntology onto = Core.getCore().findOntology("idaco");
		Collection<Variable> varCol = IDACO.getVars(onto);
		int sessionID = getCurrentSession();
		
		for (Variable v : varCol) {
			String name = v.getLocalName();
			if (name.contains("varDB")) {
				String[] varNameArr = name.split("_");
				String varVal = Core.getVariableString(v, onto.beliefSpace[Settings.getuserpos("user")]);
				float varValFloat = Float.parseFloat(varVal);
				if (varNameArr[1].equals("dev")) {
					updateCurrentDevParamValue (sessionID, varNameArr[2], varNameArr[3], varValFloat);
				} else if (varNameArr[1].equals("setup")) {
					updateCurrentPresetParamValue (sessionID, varNameArr[2], varNameArr[3], varValFloat);
				}
			}
		}
	}
	
	
	public static int getCurrentSession () {
		try {
			Statement statement = con.createStatement();
			String queryString = "exec sp_Get_Current_Session";
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return Integer.parseInt(rs.getString("Session_id").trim());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static int getCurrentRecordID (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	record_id "
					+ "FROM		IDACO_session_record "
					+ "WHERE	session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return Integer.parseInt(rs.getString("record_id").trim());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	
	public static String sessionTeamComplete (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	* "
					+ "FROM		IDACO_session_team "
					+ "WHERE	staff_is_present = 0 "
					+ "			AND session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return "false";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "true";
	}
	
	
	public static int getPrimarySurgeonID (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT primary_surgeon_id "
					+ "FROM IDACO_session "
					+ "WHERE session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return Integer.parseInt(rs.getString("primary_surgeon_id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	
	public static String getPrimarySurgeonName (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	staff_surname "
					+ "FROM		IDACO_staff AS staff "
					+ "JOIN		IDACO_session AS sess "
					+ "ON		staff.staff_id = sess.primary_surgeon_id "
					+ "WHERE	sess.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return "Doktor " + rs.getString("staff_surname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getSecondarySurgeonName (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	staff_surname "
					+ "FROM		IDACO_staff AS staff "
					+ "JOIN		IDACO_session AS sess "
					+ "ON		staff.staff_id = sess.secondary_surgeon_id "
					+ "WHERE	sess.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return "Doktor " + rs.getString("staff_surname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getAnesthetistName (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	staff_surname "
					+ "FROM		IDACO_staff AS staff "
					+ "JOIN		IDACO_session_team AS team "
					+ "ON		staff.staff_id = team.staff_id "
					+ "WHERE	staff.staff_role_id = 6 "
					+ "			AND	team.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return "Doktor " + rs.getString("staff_surname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getNurseName (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	staff_surname "
					+ "FROM		IDACO_staff AS staff "
					+ "JOIN		IDACO_session_team AS team "
					+ "ON		staff.staff_id = team.staff_id "
					+ "WHERE	staff.staff_role_id = 4 "
					+ "			AND	team.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return "Schwester " + rs.getString("staff_surname");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getSessionType (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	session_type_name "
					+ "FROM		IDACO_session_type "
					+ "AS		sesType "
					+ "JOIN		IDACO_session AS ses "
					+ "ON		sesType.session_type_id = ses.session_type_id "
					+ "WHERE	ses.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					return rs.getString("session_type_name");
			}
			return "unbekannt";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getFullPatientName (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	patient_name, patient_surname "
					+ "FROM		IDACO_patient AS pat "
					+ "JOIN		IDACO_session_record AS rec "
					+ "ON		pat.patient_id = rec.patient_id "
					+ "WHERE	rec.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
				String name = rs.getString("patient_name");
				name += " " + rs.getString("patient_surname");
				return name;
			}
			return "unbekannt";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getPatientGenderGerman (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	patient_gender_value "
					+ "FROM		IDACO_gender_value AS genVal "
					+ "JOIN		IDACO_patient AS pat "
					+ "ON		genVal.patient_gender = pat.patient_gender "
					+ "JOIN		IDACO_session_record AS rec "
					+ "ON		pat.patient_id = rec.patient_id "
					+ "WHERE	rec.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
				String gender = rs.getString("patient_gender_value");
				if (gender.trim().equals("male")) {
					return "männlich";
				} else {
					return "weiblich";
				}
			}
			return "Geschlecht unbekannt";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static int getPatientAge (int sessionID) {
		Date date = new Date();
		
		String currDate = date.toInstant().toString().substring(0, 10);
		String currYear = currDate.substring(0, 4);
		String currMonth = currDate.substring(5, 7);
		String currDay = currDate.substring(8, 10);
		
		int y = Integer.parseInt(currYear);
		if (currMonth.charAt(0) == '0') {
			currMonth = currMonth.substring(1);
		}
		int m = Integer.parseInt(currMonth);
		if (currDay.charAt(0) == '0') {
			currDay = currDay.substring(1);
		}
		int d = Integer.parseInt(currDay);
		
		int age = -1;
		
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	patient_birthdate "
					+ "FROM		IDACO_patient AS pat "
					+ "JOIN		IDACO_session_record AS rec "
					+ "ON		pat.patient_id = rec.patient_id "
					+ "WHERE	rec.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			
			while(rs.next()) {
				String birthdate = rs.getString("patient_birthdate");
				String birthYear = birthdate.substring(0, 4);
				String birthMonth = birthdate.substring(5, 7);
				String birthDay = birthdate.substring(8, 10);
				
				int by = Integer.parseInt(birthYear);
				if (birthMonth.charAt(0) == '0') {
					birthMonth = birthMonth.substring(1);
				}
				int bm = Integer.parseInt(birthMonth);
				if (birthDay.charAt(0) == '0') {
					birthDay = birthDay.substring(1);
				}
				int bd = Integer.parseInt(birthDay);
				age = y - by;
				if (m - bm > 0) { // already had birthday
					return age;
				} else if (m - bm < 0) { // didn't have birthday yet
					return age - 1;
				} else { // month of birthday => day?
					if (d - bd >= 0) { // already had birthday or has today
						return age;
					} else { // didn't have birthday yet
						return age - 1;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return age;
	}
	
	
	public static String getPatientPreDiseases (int sessionID) {
		String prediseases = "Bekannte Vorerkrankungen: ";
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	pre_disease_name "
					+ "FROM		IDACO_pre_disease AS preDis "
					+ "JOIN		IDACO_patient_pre_disease_list AS patPreDis "
					+ "ON		preDis.pre_disease_id = patPreDis.pre_disease_id "
					+ "JOIN		IDACO_session_record AS rec "
					+ "ON		patPreDis.record_id = rec.record_id "
					+ "WHERE	rec.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			
			if(rs.next()) {
				prediseases += rs.getString("pre_disease_name") + ", ";
				while (rs.next()) {
					prediseases += rs.getString("pre_disease_name") + ", ";
				}
				int i = prediseases.length();
				prediseases = prediseases.substring(0, i-2);
				return prediseases;
			}
			
			return "Es sind keine Allergien oder andere Besonderheiten bekannt.";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getSIUnitNameByID (int unitID) {
		String unitShortcut, unitName;
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	SI_unit_type_shortcut "
					+ "FROM		IDACO_SI_unit "
					+ "WHERE	SI_unit_id = " + unitID;
			ResultSet rs = statement.executeQuery(queryString);
			while(rs.next()) {
					unitShortcut = rs.getString("SI_unit_type_shortcut").trim();
					
					switch (unitShortcut) {
						case "sec":		unitName = "Sekunden";
										break;
						case "m":		unitName = "Meter";
										break;
						case "l":		unitName = "Liter";
										break;
						case "ml":		unitName = "Milliliter";
										break;
						case "g/ml":	unitName = "Gramm pro Milliliter";
										break;
						case "mmol/l":	unitName = "Millimol pro Liter";
										break;
						case "mg/dl":	unitName = "Milligramm pro Deciliter";
										break;
						case "U/l":		unitName = "Einheiten pro Liter";
										break;
						case "g/dl":	unitName = "Gramm pro Deciliter";
										break;
						case "%":		unitName = "Prozent";
										break;
						case "cm":		unitName = "Centimeter";
										break;
						case "°":		unitName = "Grad";
										break;
						case "kg":		unitName = "Kilogramm";
										break;
						case "mm":		unitName = "Millimeter";
										break;
						case "mg/l":	unitName = "Milligramm pro Liter";
										break;
						case "mg":		unitName = "Milligramm";
										break;
						default:		unitName = "";
										break;
					}
					
					return unitName;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static String getPatientPreMedication (int sessionID) {
		int recordID = getCurrentRecordID(sessionID);
		String premedication = "Bekannte medikamentöse Vorbehandlung: Täglich ";
		String medName, unitName;
		int dose, unitID;
		
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	p.pre_medication_dosis_per_day, m.SI_unit_id, m.medication_name "
					+ "FROM		IDACO_medications AS m "
					+ "JOIN		IDACO_patient_pre_medication_list AS p "
					+ "ON		m.medication_id = p.medication_id "
					+ "WHERE	record_id = " + recordID;
			ResultSet rs = statement.executeQuery(queryString);
			
			if (rs.next()) {
				dose = Math.round(Float.parseFloat(rs.getString("pre_medication_dosis_per_day").trim()));
				medName = rs.getString("medication_name").trim();
				unitID = Integer.parseInt(rs.getString("SI_unit_id").trim());
				unitName = getSIUnitNameByID(unitID);
				premedication += dose + " " + unitName + " " + medName + ", ";
						
				while (rs.next()) {
					dose = Math.round(Float.parseFloat(rs.getString("pre_medication_dosis_per_day").trim()));
					medName = rs.getString("medication_name").trim();
					unitID = Integer.parseInt(rs.getString("SI_unit_id").trim());
					unitName = getSIUnitNameByID(unitID);
					premedication += dose + " " + unitName + " " + medName + ", ";
				}
				
				int i = premedication.length();
				premedication = premedication.substring(0, i-2);
				return premedication;
			}
			
			return "Es ist keine medikamentöse Vorbehandlung bekannt.";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	public static String getPatientLaboratoryData (int sessionID) {
		int recordID = getCurrentRecordID(sessionID);
		String labdata = "Bekannte Labordaten: ";
		String dataDescr, value, unitName;
		String[] dataName;
		int unitID;
		
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	p.laboratory_data_value, l.SI_unit_id, l.laboratory_data_description "
					+ "FROM		IDACO_laboratory_data AS l "
					+ "JOIN		IDACO_laboratory_data_list AS p "
					+ "ON		l.laboratory_data_id = p.laboratory_data_id "
					+ "WHERE	record_id = " + recordID;
			ResultSet rs = statement.executeQuery(queryString);
			
			if (rs.next()) {
				dataDescr = rs.getString("laboratory_data_description").trim();
				dataName = dataDescr.split("\\(");
				unitID = Integer.parseInt(rs.getString("SI_unit_id").trim());
				unitName = getSIUnitNameByID(unitID);
				if (unitName.equals("Einheiten pro Liter")) {
					value = String.format("%.0f", Float.parseFloat(rs.getString("laboratory_data_value").trim()));
				} else {
					value = String.format("%.2f", Float.parseFloat(rs.getString("laboratory_data_value").trim()));
				}
				labdata += dataName[0].trim() + " " + value + " " + unitName + ", ";
						
				while (rs.next()) {
					dataDescr = rs.getString("laboratory_data_description").trim();
					dataName = dataDescr.split("\\(");
					unitID = Integer.parseInt(rs.getString("SI_unit_id").trim());
					unitName = getSIUnitNameByID(unitID);
					if (unitName.equals("Einheiten pro Liter")) {
						value = String.format("%.0f", Float.parseFloat(rs.getString("laboratory_data_value").trim()));
					} else {
						value = String.format("%.2f", Float.parseFloat(rs.getString("laboratory_data_value").trim()));
					}
					labdata += dataName[0].trim() + " " + value + " " + unitName + ", ";
				}
				
				int i = labdata.length();
				labdata = labdata.substring(0, i-2);
				return labdata;
			}
			
			return "Es sind keine Labordaten bekannt.";
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "";
	}	
	
	
	public static String getCurrentSetupID (int sessionID) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	stSetup.setup_id "
					+ "FROM		IDACO_staff_setup AS stSetup "
					+ "JOIN		IDACO_setup AS setup "
					+ "ON		stSetup.setup_id = setup.setup_id "
					+ "JOIN		IDACO_session AS sess "
					+ "ON		setup.session_type_id = sess.session_type_id "
					+ "WHERE	stSetup.staff_id = sess.primary_surgeon_id "
					+ "			AND stSetup.setup_active = 1 "
					+ "			AND sess.session_id = " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				return rs.getString("setup_id");
			}
			return "unbekannt";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	

	public static String getCurrentDevID (int sessionID, String devTypeName) {
		String setupID = getCurrentSetupID(sessionID);
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	device_id "
					+ "FROM		IDACO_setup_device AS setupDev "
					+ "JOIN		IDACO_device AS dev "
					+ "ON		setupDev.device_id = dev.device_list_id "
					+ "JOIN		IDACO_device_type AS devType "
					+ "ON		dev.device_type_id = devType.device_type_id "
					+ "WHERE	devType.device_type_name LIKE '%" + devTypeName + "%' "
					+ "			AND setupDev.setup_id = " + setupID;
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				return rs.getString("device_id");
			}
			return "unbekannt";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getParamID (String devID, String devParamName) {
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	device_parameter_list_id "
					+ "FROM		IDACO_device_parameter_list "
					+ "WHERE	device_parameter_name LIKE '%" + devParamName + "%' "
					+ "			AND device_list_id = " + devID;
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				return rs.getString("device_parameter_list_id");
			}
			return "unbekannt";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	public static String getCurrentDevParamValue (int sessionID, String devTypeName, String devParamName) {
		String devID = getCurrentDevID(sessionID, devTypeName);
		String paramKey = getParamID(devID, devParamName);
		try {
			Statement statement = con.createStatement();
			String queryString = "exec sp_Get_IDACO_Device_Status " + devID + ", " + paramKey + ", " + sessionID;
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				return rs.getString("device_status_param_value");
			}
			return "unbekannt";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}
	
	
	private static void updateCurrentDevParamValue (int sessionID, String devTypeName, String devParamName, float value) {
		String devID = getCurrentDevID(sessionID, devTypeName);
		String paramKey = getParamID(devID, devParamName);
		try {
			Statement statement = con.createStatement();
			String queryString = "exec sp_Update_IDACO_Device_Status " + devID + ", " + paramKey + ", " + value + ", " + sessionID;
			statement.executeUpdate(queryString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	private static void updateCurrentPresetParamValue (int sessionID, String devTypeName, String devParamName, float value) {
		String devID = getCurrentDevID(sessionID, devTypeName);
		String paramKey = getParamID(devID, devParamName);
		String setupID = getCurrentSetupID(sessionID);
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "UPDATE	IDACO_device_status_setup "
					+ "SET		device_status_param_value = " + value + " "
					+ "WHERE	setup_id = " + setupID + " "
					+ "			AND device_list_id = " + devID + " "
					+ "			AND device_status_param_key = " + paramKey;
			statement.executeUpdate(queryString);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static String getCurrentSetupParamValue (int sessionID, String devTypeName, String devParamName) {
		String devID = getCurrentDevID(sessionID, devTypeName);
		String setupID = getCurrentSetupID(sessionID);
		try {
			Statement statement = con.createStatement();
			String queryString = ""
					+ "SELECT	devStatSetup.device_status_param_value "
					+ "FROM		IDACO_device_status_setup AS devStatSetup "
					+ "JOIN		IDACO_device AS dev "
					+ "ON		devStatSetup.device_list_id = dev.device_list_id "
					+ "JOIN		IDACO_device_parameter_list AS devParamList "
					+ "ON		devStatSetup.device_status_param_key = devParamList.device_parameter_list_id "
					+ "WHERE	devStatSetup.setup_id = " + setupID + " "
					+ "			AND dev.device_list_id = " + devID + " "
					+ "			AND devParamList.device_parameter_name LIKE '%" + devParamName + "%'";
			ResultSet rs = statement.executeQuery(queryString);
			while (rs.next()) {
				return rs.getString("device_status_param_value");
			}
			return "unbekannt";
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "null";
	}

	
	public static void main (String[] args) {
		IdacoDB db = new IdacoDB();
	}

}