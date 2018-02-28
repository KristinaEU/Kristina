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

public class ErrorModel {
	public static final String filePath = "C:\\Users\\mkraus\\Desktop\\ontology_dstc2.json";
	public ErrorModel() {
		// TODO Auto-generated constructor stub
	}
	
	private Vector<SystemAction> getBestSLUHyp (String filePath){
	
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

					JSONArray sluHyps = (JSONArray) output.get("slu-hyps");
					sluHyps.get(0);
				
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
	

	private static Vector<Constraint> createItemList(){
		Vector<Constraint> vec = new Vector<Constraint>();
		vec.add(new Constraint("constant", null));
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONObject informable = (JSONObject) jsonObject.get("informable");
			Object[] slots = informable.keySet().toArray();

			for (int i = 0; i < slots.length; i++) {
				String slot = slots[i].toString();
				Object[] values = informable.values().toArray();

				String v = values[i].toString().substring(1,
						values[i].toString().length() - 1);
				String[] val = v.split(",");
				for (int j = 0; j < val.length; j++) {
				String prevalue = val[j];
				String value = prevalue.replace("\"", "");
				Constraint s = new Constraint(slot, value);

				vec.add(s);}
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
	 
	public static void main(String[] args){
		Vector<Constraint> vec = createItemList();
		System.out.println(vec.toString());
		
	}
}
