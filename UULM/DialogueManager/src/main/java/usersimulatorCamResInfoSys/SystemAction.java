package usersimulatorCamResInfoSys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class representing the System Actions
 * 
 * @author mkraus
 * 
 */
public class SystemAction {

	private List<DialogueAct> dialActList;

	public List<DialogueAct> getDialActList() {
		return dialActList;
	}

	public void setDialActList(List<DialogueAct> dialActList) {
		this.dialActList = dialActList;
	}

	public SystemAction() {
		dialActList = new ArrayList<DialogueAct>();
	}

	public SystemAction(Vector<DialogueAct> vec) {
		dialActList = new ArrayList<DialogueAct>();
		dialActList.addAll(vec);
	}

	public SystemAction(DialogueAct d) {
		dialActList = new ArrayList<DialogueAct>();
		dialActList.add(d);
	}

	public static Vector<SystemAction> getTrainingSysAct(String filePath) {
		Vector<SystemAction> vec = new Vector<SystemAction>();
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray turns = (JSONArray) jsonObject.get("turns");

			for (int i = 0; i < turns.size(); i++) {
				SystemAction sysAct = new SystemAction();
				List<DialogueAct> dialActList = new ArrayList<DialogueAct>();
				JSONObject turn = (JSONObject) turns.get(i);
				JSONObject output = (JSONObject) turn.get("output");

				JSONArray dialogueActs = (JSONArray) output.get("dialog-acts");
				Map<String, List<String[]>> map = new TreeMap<String, List<String[]>>();
				for (int j = 0; j < dialogueActs.size(); j++) {

					JSONObject item = (JSONObject) dialogueActs.get(j);
					JSONArray slots = (JSONArray) item.get("slots");
					String[] arr = slots.toString().split(",");
					for (int k = 0; k < arr.length; k++) {
						arr[k] = arr[k].replace("\"", "").replace("[", "")
								.replace("]", "");
					}
					String act = (String) item.get("act");

					String actUpperCase = Character.toUpperCase(act.charAt(0))
							+ act.substring(1);
					if (actUpperCase.equals("Confirm-domain")) {
						actUpperCase = actUpperCase.replace("-", "_");
					}
					if (actUpperCase.equals("Expl-conf")
							|| actUpperCase.equals("Impl-conf")) {
						actUpperCase = "Confirm";
					}

					if (!map.containsKey(actUpperCase)) {
						List<String[]> l = new ArrayList<String[]>();
						l.add(arr);
						map.put(actUpperCase, l);

					} else {
						List<String[]> l = map.get(actUpperCase);
						l.add(arr);
					}

				}
				Vector<String> acts = new Vector<String>(map.keySet());
				for (String act : acts) {
					List<String[]> list = map.get(act);

					DialogueAct dA = DialogueAct.formDialogueAct(list, act);
					dialActList.add(dA);
				}
				sysAct.setDialActList(dialActList);
				vec.add(sysAct);
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
		return vec;
	}

	@Override
	public String toString() {
		return dialActList.toString();
	}

}