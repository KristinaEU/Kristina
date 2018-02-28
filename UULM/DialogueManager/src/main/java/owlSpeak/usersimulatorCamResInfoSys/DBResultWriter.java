package owlSpeak.usersimulatorCamResInfoSys;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;
import java.util.Vector;


public class DBResultWriter {
	private static Connection con = null;
	private static TreeMap<String, String> columnTypeMap = null;
	private static final String url = "jdbc:mysql://salmon.e-technik.uni-ulm.de:33306/ultes_lgus";
	private final static String user = "lgus";
	private final static String password = "lgus2014";
	private final String tableName = "cam_eval_2015_07_GPSARSA";
//	private final String tableName = "test";

	{
		columnTypeMap = new TreeMap<String, String>();
		columnTypeMap.put("id", "BIGINT NOT NULL AUTO_INCREMENT");
//		columnTypeMap.put("area_user", "varchar(50)");
//		columnTypeMap.put("area_system", "varchar(50)");
//		columnTypeMap.put("food_user", "varchar(50)");
//		columnTypeMap.put("food_system", "varchar(50)");
//		columnTypeMap.put("pricerange_user", "varchar(50)");
//		columnTypeMap.put("pricerange_system", "varchar(50)");
//		columnTypeMap.put("name_user", "varchar(50)");
//		columnTypeMap.put("name_system", "varchar(50)");
//		columnTypeMap.put("request_area_user", "boolean");
//		columnTypeMap.put("said_area_system", "boolean");
//		columnTypeMap.put("request_food_user", "boolean");
//		columnTypeMap.put("said_food_system", "boolean");
//		columnTypeMap.put("request_pricerange_user", "boolean");
//		columnTypeMap.put("said_pricerange_system", "boolean");
//		columnTypeMap.put("request_address_user", "boolean");
//		columnTypeMap.put("said_address_system", "boolean");
//		columnTypeMap.put("request_phone_user", "boolean");
//		columnTypeMap.put("said_phone_system", "boolean");
//		columnTypeMap.put("request_postcode_user", "boolean");
//		columnTypeMap.put("said_postcode_system", "boolean");
//		columnTypeMap.put("request_signature_user", "boolean");
//		columnTypeMap.put("said_signature_system", "boolean");
//		columnTypeMap.put("request_name_user", "boolean");
//		columnTypeMap.put("said_name_system", "boolean");
		columnTypeMap.put("success", "boolean");
		columnTypeMap.put("exchanges", "int");
		columnTypeMap.put("interaction_quality","int");
		columnTypeMap.put("reward", "double");
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
	
	void insertNewEntry(String strategy, String id, int success, int exchanges, int iq, double reward) {
		
		// insert additionally?
//		String area_user, String area_system,
//		String food_user, String food_system, String pricerange_user, String pricerange_system,
//		String name_user, String name_system, boolean request_area_user, boolean said_area_system,
//		boolean request_food_user, boolean said_food_system, boolean request_pricerange_user,
//		boolean said_pricerange_system, boolean request_address_user, boolean said_address_system,
//		boolean request_phone_user, boolean said_phone_system, boolean request_postcode_user, 
//		boolean said_postcode_system, boolean request_signature_user, boolean said_signature_system,
//		boolean request_name_user, boolean said_name_system, 

		if (connected()) {
			
			String[] headers = getHeader();

			String query = "INSERT INTO " + tableName + "_result (";
			query += "`" + headers[0] + "` ";
			for (int i = 1; i < headers.length-1; i++) {
				query += ", " + "`" + headers[i] + "` ";
			}
			
			query += ") VALUES ('" + id + "','" + success + "','" + exchanges+"'," + iq + "," + reward + ")";
			
			// insert additionally?
//			+ area_user + "','" + area_system + "','"
//			+ food_user + "','" + food_system + "','" + pricerange_user + "','" + pricerange_system + "','" 
//			+ name_user	+ "','" + name_system + "','" + request_area_user + "','" + said_area_system + "','"
//			+ request_food_user	+ "','" + said_food_system + "','" + request_pricerange_user + "','" + said_pricerange_system + "','"
//			+ request_address_user	+ "','" + said_address_system + "','" + request_phone_user + "','" + said_phone_system + "','"
//			+ request_postcode_user	+ "','" + said_postcode_system + "','" + request_signature_user + "','" + said_signature_system + "','"
//			+ request_name_user	+ "','" + said_name_system

			System.out.println(query);
			
			try {
				Statement statement = con.createStatement();
				statement.execute(query);

			} catch (SQLException ex) {
				ex.printStackTrace();
			}

			insertMetaEntry(id, strategy);
		}
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
		headers.add("id");
//		headers.add("area_user");
//		headers.add("area_system");
//		headers.add("food_user");
//		headers.add("food_system");
//		headers.add("pricerange_user");
//		headers.add("pricerange_system");
//		headers.add("name_user");
//		headers.add("name_system");
//		headers.add("request_area_user");
//		headers.add("said_area_system");
//		headers.add("request_food_user");
//		headers.add("said_food_system");
//		headers.add("request_pricerange_user");
//		headers.add("said_pricerange_system");
//		headers.add("request_address_user");
//		headers.add("said_address_system");
//		headers.add("request_phone_user");
//		headers.add("said_phone_system");
//		headers.add("request_postcode_user");
//		headers.add("said_postcode_system");
//		headers.add("request_signature_user");
//		headers.add("said_signature_system");
//		headers.add("request_name_user");
//		headers.add("said_name_system");
		headers.add("success");
		headers.add("exchanges");
		headers.add("interaction_quality");
		headers.add("reward");
		headers.add("");

		String[] stringVector = new String[headers.size()];
		int i = 0;
		for (String d : headers) {
			stringVector[i++] = d;
		}

		return stringVector;
	}

}
