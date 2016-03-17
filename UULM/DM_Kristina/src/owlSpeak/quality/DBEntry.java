package owlSpeak.quality;

import java.util.TreeMap;

public class DBEntry {
	private TreeMap<String,String> values = null;
	
	private static TreeMap<String, String> columnTypeMap = null;
	private static String[] featureHeaders;
	private static String[] metaInfo;
	
	public DBEntry () {
		values = new TreeMap<String, String>();
	}
	
	public TreeMap<String, String> getValues() {
		return values;
	}

	public void setValues(String[] headers, String[] values) {
		if (headers.length == values.length) {
			for (int i=0; i < headers.length; i++) {
				this.values.put(headers[i],values[i]);
			}
		}
	}

	public static TreeMap<String, String> getColumnTypeMap() {
		if (columnTypeMap == null)
			setUpColumnTypeMapping();
		return columnTypeMap;
	}

	public static String[] getFeatureHeaders() {
		return featureHeaders;
	}

	public static void setFeatureHeaders(String[] featureHeaders) {
		DBEntry.featureHeaders = featureHeaders;
	}

	public static String[] getMetaInfo() {
		return metaInfo;
	}

	public static void setMetaInfo(String[] metaInfo) {
		DBEntry.metaInfo = metaInfo;
	}
	
	public static String[] getColumns() {
		String[] columns = new String[featureHeaders.length + metaInfo.length];
		
		int i=0;
		for (String s : metaInfo)
			columns[i++] = s;
		for (String s : featureHeaders)
			columns[i++] = s;
		
		return columns;
	}

	public static void setUpColumnTypeMapping() {
		if (columnTypeMap == null) {
			columnTypeMap = new TreeMap<String, String>();

			columnTypeMap.put("CallID", "bigint");
			columnTypeMap.put("Prompt", "varchar(800)");
			columnTypeMap.put("Utterance", "varchar(200)");
			columnTypeMap.put("ASRRecognitionStatus", "varchar(60)");
			columnTypeMap.put("#ASRSuccess", "int(11)");
			columnTypeMap.put("(#)ASRSuccess", "int(11)");
			columnTypeMap.put("%ASRSuccess", "double");
			columnTypeMap.put("#TimeOutPrompts", "int(11)");
			columnTypeMap.put("(#)TimeOutPrompts", "int(11)");
			columnTypeMap.put("%TimeOutPrompts", "double");
			columnTypeMap.put("#ASRRejections", "int(11)");
			columnTypeMap.put("(#)ASRRejections", "int(11)");
			columnTypeMap.put("%ASRRejections", "double");
			columnTypeMap.put("#TimeOuts_ASRRej", "int(11)");
			columnTypeMap.put("(#)TimeOuts_ASRRej", "int(11)");
			columnTypeMap.put("%TimeOuts_ASRRej", "double");
			columnTypeMap.put("Barged-In?", "tinyint(4)");
			columnTypeMap.put("#Barge-Ins", "int(11)");
			columnTypeMap.put("(#)Barge-Ins", "int(11)");
			columnTypeMap.put("%Barge-Ins", "double");
			columnTypeMap.put("ASRConfidence", "double");
			columnTypeMap.put("MeanASRConfidence", "double");
			columnTypeMap.put("(Mean)ASRConfidence", "double");
			columnTypeMap.put("UTD", "float");
			columnTypeMap.put("ExMo", "varchar(6)");
			columnTypeMap.put("Modality", "varchar(10)");
			columnTypeMap.put("UnExMo?", "int(11)");
			columnTypeMap.put("#UnExMo", "int(11)");
			columnTypeMap.put("(#)UnExMo", "int(11)");
			columnTypeMap.put("%UnExMo", "double");
			columnTypeMap.put("WPUT", "int(11)");
			columnTypeMap.put("WPST", "int(11)");
			columnTypeMap.put("SemanticParse", "varchar(500)");
			columnTypeMap.put("HelpRequest?", "int(11)");
			columnTypeMap.put("#HelpRequests", "int(11)");
			columnTypeMap.put("(#)HelpRequest", "int(11)");
			columnTypeMap.put("%HelpRequest", "double");
			columnTypeMap.put("Activity", "varchar(100)");
			columnTypeMap.put("ActivityType", "varchar(50)");
			columnTypeMap.put("DD", "double");
			columnTypeMap.put("RoleIndex", "tinyint(4)");
			columnTypeMap.put("RoleName", "varchar(20)");
			columnTypeMap.put("RePrompt?", "int(11)");
			columnTypeMap.put("#RePrompts", "int(11)");
			columnTypeMap.put("(#)RePrompts", "int(11)");
			columnTypeMap.put("%RePrompts", "double");
			columnTypeMap.put("LoopName", "varchar(10)");
			columnTypeMap.put("#Exchanges", "smallint(6)");
			columnTypeMap.put("#SystemTurns", "int(11)");
			columnTypeMap.put("#UserTurns", "int(11)");
			columnTypeMap.put("#SystemQuestions", "int(11)");
			columnTypeMap.put("(#)SystemQuestions", "int(11)");
			columnTypeMap.put("SystemDialogueAct", "varchar(50)");
			columnTypeMap.put("UserDialogueAct", "varchar(50)");
			columnTypeMap.put("AudioFile", "varchar(60)");
			columnTypeMap.put("EmotionalState", "longtext");
			columnTypeMap.put("IQRater1", "int(11)");
			columnTypeMap.put("IQRater2", "int(11)");
			columnTypeMap.put("IQRater3", "int(11)");
			columnTypeMap.put("IQMedian", "int(11)");
			columnTypeMap.put("Prediction(IQreduced)", "int(11)");
			columnTypeMap.put("Prediction(IQ)", "int(11)");
			columnTypeMap.put("Barged-in?","int(11)");
			columnTypeMap.put("Confirmation?","int(11)");
			columnTypeMap.put("TurnNumber","int(11)");
			columnTypeMap.put("#Time-OutPrompts","int(11)");
			columnTypeMap.put("%Time-OutPrompts","double");
			columnTypeMap.put("#BargeIns","int(11)");
			columnTypeMap.put("%BargeIns","double");
			columnTypeMap.put("{Mean}ASRConfidence","double");
			columnTypeMap.put("{#}ASRSuccess","int(11)");
			columnTypeMap.put("{#}ASRRejections","int(11)");
			columnTypeMap.put("{#}TimeOutPrompts","int(11)");
			columnTypeMap.put("{#}Barge-Ins","int(11)");
			columnTypeMap.put("{#}RePrompts","int(11)");
			columnTypeMap.put("{#}Confirmation","int(11)");
			columnTypeMap.put("{#}SystemQuestions","int(11)");
		}
	}
}
