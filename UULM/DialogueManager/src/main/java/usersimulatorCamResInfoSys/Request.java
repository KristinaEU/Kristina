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
 * The Request Class specifying the desired pieces of information, e.g., the
 * name, address and phone number of the venue.
 * 
 * @author mkraus
 * 
 */
public class Request {

	private String slot;
	private String value;

	public Request(String slot, String value) {
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
	 * generated Requests out of the file containing the requestable slots
	 * 
	 * @param filePath
	 * @param con
	 * @return
	 */
	public static Vector<Request> generateRequests(String filePath,
			Vector<Constraint> con) {
		Vector<Request> vec = new Vector<Request>();
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONArray requestable = (JSONArray) jsonObject.get("requestable");
			String slot = (String) requestable.toString();
			String slotSub = slot.substring(1, slot.length() - 1);
			String[] request = slotSub.split(",");
			for (int i = 0; i < request.length; i++) {
				String prevalue = request[i];
				String value = prevalue.replace("\"", "");
				boolean b = false;
				int n = 0;
				while (n < con.size() && !(con.isEmpty())) {
					if (value.contains(con.elementAt(n).getSlot()))
						b = true;
					n++;
				}
				if (b != true) {
					Request r = new Request(value, null);
					if (!vec.contains(r))
						vec.add(r);
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

		List<Request> list = new ArrayList<Request>();
		while (!vec.isEmpty()) {
			Random r = new Random();
			Request c = vec.get(r.nextInt(vec.size()));
			list.add(c);
			vec.remove(c);
		}

		Random ra = new Random();
		List<Request> sList = new ArrayList<Request>();
		
		for(Request r: list){
			if(r.slot.equals("name")){
				list.clear();
				sList.add(r);
				break;
			}
			
				 
		}
		if(!list.isEmpty())
		sList = list.subList(0, ra.nextInt(list.size()) + 1);
		
		Vector<Request> v = new Vector<Request>();
		v.addAll(sList);
		return v;
	}

	public static Vector<Request> generateTrainingRequests(String filePath) {
		Vector<Request> vec = new Vector<Request>();
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONObject taskInfo = (JSONObject) jsonObject
					.get("task-information");
			JSONObject goal = (JSONObject) taskInfo.get("goal");
			JSONArray constraints = (JSONArray) goal.get("request-slots");

			String[] arr = constraints.toString().split(",");
			for (int k = 0; k < arr.length; k++) {
				arr[k] = arr[k].replace("\"", "").replace("[", "")
						.replace("]", "");
				Request r = new Request(arr[k], null);
				vec.add(r);
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
		if(value != null) return slot+"="+value;
		else return slot;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof Request) {
			Request r = (Request) o;
			if (slot.equals(r.toString()))

				return true;
		}
		return false;
	}

}
