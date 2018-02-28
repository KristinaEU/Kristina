package owlSpeak.usersimulatorLetsGo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import java.util.Vector;

public class DBResultWriter {
	private static Connection con = null;
	private static TreeMap<String, String> columnTypeMap = null;

	private static final String url = "jdbc:mysql://mysql-nt.e-technik.uni-ulm.de/ultes_lgus";
	private final static String user = "lgus";
	private final static String password = "zeixa3Ue";
	private final String tableName = "eval_2016_07_GPSARSA_IQREWARD";

	{
		columnTypeMap = new TreeMap<String, String>();
		columnTypeMap.put("id", "BIGINT NOT NULL AUTO_INCREMENT");
		columnTypeMap.put("Bus_number_user", "varchar(40)");
		columnTypeMap.put("Bus_number_system", "varchar(40)");
		columnTypeMap.put("Arrival_place_user", "varchar(50)");
		columnTypeMap.put("Arrival_place_system", "varchar(50)");
		columnTypeMap.put("Departure_place_user", "varchar(50)");
		columnTypeMap.put("Departure_place_system", "varchar(50)");
		columnTypeMap.put("Time_user", "varchar(4)");
		columnTypeMap.put("Time_system", "varchar(4)");
		columnTypeMap.put("Hang_up", "boolean");
		columnTypeMap.put("Success", "boolean");
		columnTypeMap.put("Exchanges", "int");
		columnTypeMap.put("Interaction_Quality","int");
		columnTypeMap.put("Reward", "double");
		columnTypeMap.put("","PRIMARY KEY(id)");
	}

	static void connect()  {
		try {
			con = DriverManager.getConnection(url, user, password);

			// Statement st = con.createStatement();
			// ResultSet rs = st.executeQuery("SELECT VERSION()");

		} catch (SQLException ex) {
			ex.printStackTrace();

		}
	}

	boolean connected() {
		try {
			return (con != null && !con.isClosed());
		} catch (SQLException e) {
			return false;
		}
	}
	
	int insertNewEntry(String callID, String strategy, String destination_user, String destination_sys,
			String busnumber_user, String busnumber_sys, String depplace_user,
			String depplace_sys, String time_user, String time_sys, int hangUp, int exchanges, int iq, double reward) {
		
		int success = (destination_user.equalsIgnoreCase(destination_sys) && depplace_user.equalsIgnoreCase(depplace_sys) && time_user.equalsIgnoreCase(time_sys)) ? 1 : 0;

		if (connected()) {
			String[] headers = getHeader();

			String query = "INSERT INTO " + tableName + "_result (";
			query += "`" + headers[0] + "` ";
			for (int i = 1; i < headers.length-1; i++) {
				query += ", " + "`" + headers[i] + "` ";
			}
			query += ") VALUES ('"+ callID + "','" + busnumber_user + "','" + busnumber_sys + "','"
					+ destination_user + "','" + destination_sys + "','"
					+ depplace_user + "','" + depplace_sys + "','" + time_user
					+ "','" + time_sys+"','"+hangUp+"','"+success+"','"+exchanges+"',"+iq+","+reward+")";

			System.out.println(query);
			try {
				Statement statement = con.createStatement();
				statement.execute(query);

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

			insertMetaEntry(callID, strategy);
		}
		return success;
	}
	
	void insertMetaEntry(String callID, String strategy) {
		if (connected()) {
			String query = "INSERT INTO " + tableName + "_meta (`ID`,`strategy`) VALUES ";
			query += "('" + callID + "','" + strategy + "');";
			System.out.println(query);
			try {
				Statement statement = con.createStatement();
				statement.execute(query);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	void createMetaTable() {
		if (connected()) {
			String query = "CREATE TABLE IF NOT EXISTS " + tableName + "_meta ";
			query += "(ID bigint(20), strategy varchar(100))";
			try {
				Statement statement = con.createStatement();
				statement.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	void createTable() {
		String[] headers = getHeader();
		if (connected()) {
			String query = "CREATE TABLE IF NOT EXISTS " + tableName + "_result (";
			query += "`" + headers[0] + "` " + columnTypeMap.get(headers[0]);
			if (!columnTypeMap.containsKey(headers[0]))
				System.out.println(headers[0]);
			for (int i = 1; i < headers.length; i++) {
				if (!columnTypeMap.containsKey(headers[i]))
					System.out.println(headers[i]);
				if(headers[i] == "")
					query += 
							","+ columnTypeMap.get(headers[i]);
				else	query += ", " + "`" + headers[i] + "` "
						+ columnTypeMap.get(headers[i]);
			}
			query += ")";
			System.out.println(query);
			try {
				Statement statement = con.createStatement();
				statement.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	static void disconnect() {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException ex) {

		}
	}

	public static void main(String[] args) {
		connect();
		DBResultWriter ldb = new DBResultWriter();
		ldb.createTable();
//		ldb.insertNewEntry("sada", "sad", "aa", "sai", "sao", "sap", "sae", "san", 1,23);
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
		
		// interaction quality
		headers.add("Interaction_Quality");
				
		// reward
		headers.add("Reward");

		// prikey
		headers.add("");

		String[] stringVector = new String[headers.size()];

		int i = 0;
		for (String d : headers) {
			stringVector[i++] = d;
		}

		return stringVector;
	}
}