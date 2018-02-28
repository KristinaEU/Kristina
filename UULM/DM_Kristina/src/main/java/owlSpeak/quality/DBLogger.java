package owlSpeak.quality;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

public class DBLogger {
	private static TreeMap<String, String> columnTypeMap = null;

	private Connection con = null;

	private final String url = "jdbc:mysql://salmon.e-technik.uni-ulm.de:33306/ultes_lgus";
	private final String user = "lgus";
	private final String password = "lgus2014";
	//private final String tableName = "his_test";
	//private final String tableName = "cam_eval_2015_07_GPSARSA";
	private final String tableName = "eval_2015_07_GPSARSA";

	public DBLogger() {
		columnTypeMap = DBEntry.getColumnTypeMap();
		if (!connect())
			System.err.println("Problem with initilizing DBLogging.");
	}

	@Override
	public void finalize() {
		disconnect();
	}

	public boolean createDBStructure(String[] headers) {
		if (connected()) {
			String query = "CREATE TABLE IF NOT EXISTS " + tableName + " (";
			query += "`" + headers[0] + "` " + columnTypeMap.get(headers[0]);
			if (!columnTypeMap.containsKey(headers[0]))
				System.out.println(headers[0]);
			for (int i = 1; i < headers.length; i++) {
				if (!columnTypeMap.containsKey(headers[i]))
					System.out.println(headers[i]);
				query += ", " + "`" + headers[i] + "` "
						+ columnTypeMap.get(headers[i]);
			}
			query += ")";

			System.out.println(query);

			try {
				Statement statement = con.createStatement();
				statement.execute(query);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean insertNewEntry(DBEntry e) {
		if (connected()) {
			String[] headers = DBEntry.getColumns();
			TreeMap<String, String> values = e.getValues();

			String query = "INSERT INTO " + tableName + " (";
			query += "`" + headers[0] + "` ";
			for (int i = 1; i < headers.length; i++) {
				query += ", " + "`" + headers[i] + "` ";
			}
			query += ") VALUES (";
			boolean isFirst = true;
			for (String header : headers) {
				boolean useQuotes = (values.get(header) != null);

				if (!isFirst)
					query += ", ";

				if (useQuotes)
					query += "\"";

				query += values.get(header);

				if (useQuotes)
					query += "\"";

				isFirst = false;
			}
			query += ")";

			System.out.println(query);
			try {
				Statement statement = con.createStatement();
				statement.execute(query);
				return true;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	public String getNextCallID(String head) {
		String callID = null;
		if (connected()) {
			String query = "SELECT CallID FROM "+tableName+" WHERE CallID LIKE '"
					+ head + "%' ORDER BY CallID DESC;";
			try {
				Statement statement = con.createStatement();
				ResultSet result = statement.executeQuery(query);
				int tail = -1; // empty result set results in tail of 0 due to increment below
				if (result.next())
					tail = Integer.parseInt(result.getString(1).substring(7));
				String sTail = String.valueOf(++tail);
				if (tail < 10)
					sTail = "000" + sTail;
				else if (tail < 100)
					sTail = "00" + sTail;
				else if (tail < 1000)
					sTail = "0" + sTail;
				
//				if (tail < 10)
//					sTail = "00" + sTail;
//				else if (tail < 100)
//					sTail = "0" + sTail;
				

				callID = head + sTail;
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
		return callID;
	}

	public boolean connect() {
		try {
			con = DriverManager.getConnection(url, user, password);
			return true;
			// Statement st = con.createStatement();
			// ResultSet rs = st.executeQuery("SELECT VERSION()");

		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public boolean disconnect() {
		try {
			if (con != null) {
				con.close();
			}
			return true;
		} catch (SQLException ex) {
			return false;
		}
	}

	public boolean connected() {
		try {
			return (con != null && !con.isClosed());
		} catch (SQLException e) {
			return false;
		}
	}

	public static void main(String[] args) {
		DBEntry.setFeatureHeaders(QualityParameter.getHeader());
		String[] metaInfo = { "CallID", "Prediction(IQreduced)" };
		DBEntry.setMetaInfo(metaInfo);

		DBLogger ldb = new DBLogger();
		ldb.createDBStructure(DBEntry.getColumns());

		DBEntry e = new DBEntry();
		ldb.insertNewEntry(e);
	}

}
