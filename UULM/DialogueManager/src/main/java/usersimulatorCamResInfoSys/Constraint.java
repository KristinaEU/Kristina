package usersimulatorCamResInfoSys;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * the Constraint class specifying the required venue, e.g., a centrally
 * located bar serving beer..
 * 
 * @author mkraus
 * 
 */
public class Constraint {
	private String slot;
	private String value;

	public Constraint(String slot, String value) {
		this.slot = slot;
		this.value = value;

	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * generates randomly a vector of Constraints out of a file containing the
	 * slots and the corresponding values.
	 * 
	 * @param filePath
	 * @return
	 */
	public static Vector<Constraint> generateConstraints(String filePath) {
		Vector<Constraint> vec = new Vector<Constraint>();
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONObject informable = (JSONObject) jsonObject.get("informable");
			Object[] slots = informable.keySet().toArray();

			for (int i = slots.length-1; i >= 0; i--) {
				String slot = slots[i].toString();
				Object[] values = informable.values().toArray();

				String v = values[i].toString().substring(1,
						values[i].toString().length() - 1);
				String[] val = v.split(",");
				Random random = new Random();
				String prevalue = val[random.nextInt(val.length)];
				String value = prevalue.replace("\"", "");
				Constraint s = new Constraint(slot, value);
				vec.add(s);
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

		List<Constraint> list = new ArrayList<Constraint>();
		while (!vec.isEmpty()) {
			Random r = new Random();
			Constraint c = vec.get(r.nextInt(vec.size()));
			list.add(c);
			vec.remove(c);
		}

		Random ra = new Random();
		List<Constraint> sList = list.subList(0, ra.nextInt(list.size()) + 1);
		for(Constraint c: sList){
			if(c.slot.equals("name")){
				sList.clear();
				sList.add(c);
				break;
			}
		}
		Vector<Constraint> v = new Vector<Constraint>();
		v.addAll(sList);
		return v;
	}

	public static Vector<Constraint> generateTrainingConstraints(String filePath) {
		Vector<Constraint> vec = new Vector<Constraint>();
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONObject taskInfo = (JSONObject) jsonObject
					.get("task-information");
			JSONObject goal = (JSONObject) taskInfo.get("goal");
			JSONArray constraints = (JSONArray) goal.get("constraints");
			for (int i = 0; i < constraints.size(); i++) {
				JSONArray item = (JSONArray) constraints.get(i);
				String[] arr = item.toString().split(",");
				for (int k = 0; k < arr.length; k++) {
					arr[k] = arr[k].replace("\"", "").replace("[", "")
							.replace("]", "");

				}
				Constraint c = new Constraint(arr[0], arr[1]);
				vec.add(c);
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
		return slot + "=" + value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null && o instanceof Constraint) {
			Constraint c = (Constraint) o;
			return (c.value.equalsIgnoreCase(this.value));
		}
		return false;
	}

}
