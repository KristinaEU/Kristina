package usersimulatorCamResInfoSys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Class representing the UserActions
 * 
 * @author mkraus
 * 
 */
public class UserAction {
	private List<DialogueAct> dialActList;

	public List<DialogueAct> getDialActList() {
		return dialActList;
	}

	public void setDialActList(List<DialogueAct> dialActList) {
		this.dialActList = dialActList;
	}

	public UserAction() {
		dialActList = new ArrayList<DialogueAct>();
	}

	public UserAction(List<DialogueAct> list) {
		this.dialActList = list;

	}

	public UserAction(DialogueAct d) {
		dialActList = new ArrayList<DialogueAct>();
		dialActList.add(d);
	}

	/**
	 * extracts the UserAction from the Agendas
	 * 
	 * @param vec
	 * @param n
	 * @return new UserAction
	 */
	public static UserAction extractUserAction(Vector<Agenda> vec,
			JSONObject object) {
		if (vec == null || vec.isEmpty() == true)
			return null;
		
		double max = vec.elementAt(0).getProbability();
		int argmax = 0;
		for (int i = 0; i < vec.size(); i++) {
			if (max < vec.elementAt(i).getProbability()) {
				max = vec.elementAt(i).getProbability();
				argmax = i;
			}
		}
		Agenda a = vec.elementAt(argmax);
		int n = Agenda.determineNforUserAct(a, object);
		List<DialogueAct> list = new ArrayList<DialogueAct>();
		Vector<DialogueType> v = new Vector<DialogueType>();
		for (int i = 0; i < n; i++) {
			if (a.agenda.size() > i){
				
				DialogueAct d = a.agenda.get(i);
				if(!v.contains(d.type)){
				list.add(d);
				v.add(d.type);}
				else{
					int index = v.indexOf(d.type);
					DialogueAct firstAppearence = list.get(index);
					firstAppearence.createMerge(d);
				}
			}
				
		}
		return new UserAction(list);
	}

	public static Vector<UserAction> getTrainingUserAct(String filePath) {

		Vector<UserAction> vec = new Vector<UserAction>();
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray turns = (JSONArray) jsonObject.get("turns");

			for (int i = 0; i < turns.size(); i++) {
				UserAction userAct = new UserAction();
				List<DialogueAct> dialActList = new ArrayList<DialogueAct>();
				String actUpperCase = "";
				JSONObject turn = (JSONObject) turns.get(i);
				JSONObject output = (JSONObject) turn.get("semantics");
				String cam = (String) output.get("cam");
				JSONArray dialogueActs = (JSONArray) output.get("json");
				if (dialogueActs.size() != 0) {
					for (int j = 0; j < dialogueActs.size(); j++) {

						JSONObject item = (JSONObject) dialogueActs.get(j);
						JSONArray slots = (JSONArray) item.get("slots");
						String[] arr = slots.toString().split(",");
						for (int k = 0; k < arr.length; k++) {
							arr[k] = arr[k].replace("\"", "").replace("[", "")
									.replace("]", "");
						}
						String act = (String) item.get("act");
						actUpperCase = Character.toUpperCase(act.charAt(0))
								+ act.substring(1);
						List<String[]> l = new ArrayList<String[]>();
						if (!arr[0].isEmpty())
							l.add(arr);

						DialogueAct dA = DialogueAct.formDialogueAct(l,
								actUpperCase);
						dialActList.add(dA);
					}
				} else {
					if (cam.equals("null()")
							|| cam.startsWith("inform(type=restaurant")
							|| cam.startsWith("inform(task=find"))
						actUpperCase = "Null";
					DialogueAct dA = DialogueAct.formDialogueAct(null,
							actUpperCase);
					dialActList.add(dA);
				}
				userAct.setDialActList(dialActList);
				vec.add(userAct);
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
