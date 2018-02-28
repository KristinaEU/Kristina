package usersimulatorCamResInfoSys;

import java.io.FileNotFoundException;
import java.io.FileReader;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TrainingNumberOfItems {
	private static Map<String, Double> actCount = new TreeMap<String, Double>();
	private static Map<Integer, Double> itemCount = new TreeMap<Integer, Double>();

	public TrainingNumberOfItems() {
		// TODO Auto-generated constructor stub
	}

	public static Map<Integer, Double> getNCount(String filePath, String actType) {

		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray turns = (JSONArray) jsonObject.get("turns");

			for (int i = 0; i < turns.size(); i++) {

				JSONObject turn = (JSONObject) turns.get(i);
				JSONObject output = (JSONObject) turn.get("semantics");
				JSONArray dialogueActs = (JSONArray) output.get("json");
				String cam = (String) output.get("cam");
				int n = 0;
				String actUpperCase = null;
				if (!dialogueActs.isEmpty()) {
					JSONObject item = (JSONObject) dialogueActs.get(0);
					String act = (String) item.get("act");

					n = dialogueActs.size();

					actUpperCase = Character.toUpperCase(act.charAt(0))
							+ act.substring(1);
				} else if (cam.equals("null()")) {
					actUpperCase = "Null";
					n = 1;
				}
				if (actType.equals(actUpperCase)) {
					if (!itemCount.containsKey(n)) {
						double times = 1.0;
						itemCount.put(n, times);
					} else {
						double currentn = itemCount.get(n);
						double nextn = currentn + 1;
						itemCount.replace(n, nextn);
					}
				}

			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return itemCount;
	}

	public static Map<String, Double> getNofActs(String filePath) {

		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray turns = (JSONArray) jsonObject.get("turns");

			for (int i = 0; i < turns.size(); i++) {

				JSONObject turn = (JSONObject) turns.get(i);
				JSONObject output = (JSONObject) turn.get("semantics");
				JSONArray dialogueActs = (JSONArray) output.get("json");
				String cam = (String) output.get("cam");

				String actUpperCase = null;
				if (!dialogueActs.isEmpty()) {
					JSONObject item = (JSONObject) dialogueActs.get(0);
					String act = (String) item.get("act");
					actUpperCase = Character.toUpperCase(act.charAt(0))
							+ act.substring(1);
				} else if (cam.equals("null()"))
					actUpperCase = "Null";
				if ((!cam.startsWith("inform(type=restaurant"))
						&& (!dialogueActs.isEmpty())) {
					if (!actCount.containsKey(actUpperCase)) {
						double n = 1.0;
						actCount.put(actUpperCase, n);
					} else {
						double currentn = actCount.get(actUpperCase);
						double nextn = currentn + 1;
						actCount.replace(actUpperCase, nextn);
					}
				}

			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return actCount;
	}

//	public static void main(String[] args) throws IOException {
//		List<String> list = UserSimulator.listDir(f, " ");
//		List<String> list2 = UserSimulator.listDir(file2, " ");
//		List<String> list3 = UserSimulator.listDir(file3, " ");
//		List<String> list4 = UserSimulator.listDir(file4, " ");
//		List<String> labelList = UserSimulator.getJSONLabel(list);
//		List<String> labelList2 = UserSimulator.getJSONLabel(list2);
//		List<String> labelList3 = UserSimulator.getJSONLabel(list3);
//		List<String> labelList4 = UserSimulator.getJSONLabel(list4);
//		labelList.addAll(labelList2);
//		labelList.addAll(labelList3);
//		labelList.addAll(labelList4);
//		Map<String, Double> map1 = null;
//
//		Vector<Map<Integer, Double>> mapvec = new Vector<Map<Integer, Double>>();
//		for (int i = 0; i < labelList.size(); i++) {
//
//			map1 = getNofActs(labelList.get(i));
//		}
//		Vector<String> acts = new Vector<String>(map1.keySet());
//		for (String a : acts) {
//			Map<Integer, Double> map2 = null;
//			itemCount = new TreeMap<Integer, Double>();
//			for (int i = 0; i < labelList.size(); i++) {
//				map2 = getNCount(labelList.get(i), a);
//			}
//			mapvec.add(map2);
//		}
//		System.out.println(map1.toString());
//		System.out.println();
//		System.out.println("-----------------------");
//		for (Map<Integer, Double> a : mapvec) {
//			System.out.println(a.toString());
//		}
//
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//
//		Map<String, Map<String, Double>> calculatedMap = new TreeMap<String, Map<String, Double>>();
//		int n = 0;
//		for (String s : acts) {
//
//			Map<String, Double> helpMap = new TreeMap<String, Double>();
//			Map<Integer, Double> ma = mapvec.get(n);
//			for (int i = 0; i < ma.size(); i++) {
//				if (ma.containsKey(i + 1))
//					helpMap.put("P(" + String.valueOf(i + 1) + "|" + s + ")",
//							ma.get(i + 1) / map1.get(s));
//				else if ((!ma.containsKey(i + 1)) && ma.containsKey(i + 2))
//					helpMap.put("P(" + String.valueOf(i + 2) + "|" + s + ")",
//							ma.get(i + 2) / map1.get(s));
//				else if ((!ma.containsKey(i + 1)) && (!ma.containsKey(i + 2))
//						&& ma.containsKey(i + 3))
//					helpMap.put("P(" + String.valueOf(i + 3) + "|" + s + ")",
//							ma.get(i + 3) / map1.get(s));
//			}
//			calculatedMap.put("P(n|" + s + ")", helpMap);
//			n++;
//		}
//		Map<String, Double> nullhelpMap = new TreeMap<String, Double>();
//		nullhelpMap.put("P(1|Null)", 1.0);
//		calculatedMap.put("P(n|Null)", nullhelpMap);
//		String s = gson.toJson(calculatedMap);
//		File file = new File("C:\\Users\\mkraus\\Desktop\\TrainingNfull.json");
//		if (!file.exists()) {
//			file.createNewFile();
//		}
//
//		FileOutputStream fOut = new FileOutputStream(file);
//		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//
//		try {
//
//			myOutWriter.append(s);
//			myOutWriter.close();
//			fOut.close();
//
//		} catch (IOException e) {
//			e.printStackTrace();
//
//		}
//	}
}
