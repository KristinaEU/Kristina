package usersimulatorKristina.goal;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import usersimulatorKristina.dialogueacts.DialogueType;
import usersimulatorKristina.dialogueacts.RequestAct;

/**
 * 
 * User Goal Creation for User Simulator
 * 
 * @author: Isabel Feustel
 * 
 * 
 */

public class UserGoal {

	/**
	 * Pick Scenario with Random Number from 0-9:
	 * 
	 * 0: Reading aloud the newspaper 1: News from social media 2: Weather
	 * forecast 3: Information on local events 4: Sleeping routine 5: Eating
	 * routine 6: Tips on sleep hygiene 7: Information on dementia 8: Tips on
	 * communication with someone suffering from dementia 9: Nutrition
	 * information and recipes
	 * 
	 */
	private int scenarioNum = 0;

	// Amount of scenarios
	private final int maxScenario = 3;

	// Maximal amount of constraints
	private final int maxConstraint = 3;

	private final int maxRepitionOfRequest = 3;

	// Final List of Constraints and Requests for G=(C,R)
	private List<Constraint> constraints;
	private List<Request> requests;

	// Path for JSON file
	private String filePath = "./conf/OwlSpeak/models/US_Kristina/usergoal.json";

	public UserGoal() {
		// choose random scenario
		scenarioNum = (int) (Math.random() * maxScenario);
		constraints = new LinkedList<Constraint>();
		requests = new LinkedList<Request>();
		pickConstraintsFromJSON();
	}

	@SuppressWarnings("unchecked")
	private void pickConstraintsFromJSON() {

		try {
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

			JSONObject scenarioObj = (JSONObject) jsonObject.get("scenario_" + scenarioNum);

			JSONObject constraints = (JSONObject) scenarioObj.get("constraints");
			JSONObject requests = (JSONObject) scenarioObj.get("requests");

			// Generate Random Number for Constraint Amount.
			// Maximum: maxConstraint; Minimum: 1?
			int numConstraints = (int) (Math.random() * (maxConstraint - 1)) + 1;
			// System.out.println(numConstraints);
			String constraint;

			// Get Keys from Constraints
			Object[] keysConstraints = constraints.keySet().toArray();

			/*
			 * Check how many constraints exist:
			 * 
			 * 1. Less than numConstraints -> Pick All Constraints 2. More than
			 * numConstraints -> Choose Random Constraints
			 * 
			 */

			if (keysConstraints.length <= numConstraints) {
				// Iterate through all constraints and add them to List
				for (Object key : keysConstraints) {
					JSONArray values = (JSONArray) constraints.get(key);

					// Random Numbers for picking constraints/values
					List<Integer> randomV = new LinkedList<Integer>();

					int numValues = (int) (Math.random() * (values.size() - 1)) + 1;
					// Generate numValues random numbers and proof they're all
					// different
					for (int j = 0; j < numValues; j++) {
						int rnd = (int) (Math.random() * (values.size() - 1));
						while (randomV.contains(rnd)) {
							rnd = (int) (Math.random() * (values.size() - 1));
						}
						randomV.add(rnd);
					}
					String[] final_Vals = new String[numValues];
					// Choose Random Values
					for (int j = 0; j < randomV.size(); j++) {
						final_Vals[j] = values.get(randomV.get(j)).toString();
					}

					// Create Constraint for each Value
					for (String val : final_Vals) {
						Constraint c = new Constraint(key.toString(), val);
						this.constraints.add(c);
					}

				}
			} else {
				// Random Numbers for picking constraints/values
				List<Integer> randomC = new LinkedList<Integer>();
				List<Integer> randomV = new LinkedList<Integer>();

				// Generate numConstraints random numbers and proof they're all
				// different
				for (int i = 0; i < numConstraints; i++) {
					int rnd = (int) (Math.random() * constraints.size());
					while (randomC.contains(rnd)) {
						rnd = (int) (Math.random() * constraints.size());
					}
					randomC.add(rnd);
				}
				// Add random constraints to List
				for (int i = 0; i < randomC.size(); i++) {
					constraint = keysConstraints[randomC.get(i)].toString();
					JSONArray values = (JSONArray) constraints.get(constraint);

					int numValues = (int) Math.random() * (values.size() - 1) + 1;
					// Generate numValues random numbers and proof they're all
					// different
					for (int j = 0; j < numValues; j++) {
						int rnd = (int) (Math.random() * (values.size() - 1));
						while (randomV.contains(rnd)) {
							rnd = (int) (Math.random() * (values.size() - 1));
						}
						randomV.add(rnd);
					}
					String[] final_Vals = new String[numValues];
					// Choose Random Values
					for (int j = 0; j < randomV.size(); j++) {
						final_Vals[j] = values.get(randomV.get(j)).toString();
					}

					// Create Constraint for each Value
					for (String val : final_Vals) {
						Constraint c = new Constraint(constraint, val);
						this.constraints.add(c);
					}
					randomV.clear();

				}
			}

			// TEST
			/*
			 * Iterator<Constraint> it = this.constraints.iterator(); while
			 * (it.hasNext()) { Constraint temp1 = it.next();
			 * System.out.println("Constraint:" + temp1.getIdentifier() + ": " +
			 * temp1.getValue()); }
			 */

			/*
			 * 
			 * Select Requests
			 * 
			 */

			Object[] keysRequests = requests.keySet().toArray();

			List<String> valuesReq;

			for (Object key : keysRequests) {
				if (!key.toString().equals("Request")) {
					Request r = new Request(key.toString(), null);
					this.requests.add(r);
				} else {

					valuesReq = (List<String>) requests.get(key);

					// Look for Duplicates in Requests and Constraints and
					// remove
					// them
					List<String> toRemove = new ArrayList<String>();
					for (String val : valuesReq) {
						for (Constraint con : this.constraints) {
							if (con.checkId(val)) {
								toRemove.add(val);
							}
						}
					}
					valuesReq.removeAll(toRemove);

					// Create Request for each value
					for (String val : valuesReq) {
						Request r = new Request(key.toString(), val);
						this.requests.add(r);
					}
				}
			}

			// TEST
			/*
			 * Iterator<Request> itR = this.requests.iterator(); while
			 * (itR.hasNext()) { Request temp1 = itR.next();
			 * System.out.println(temp1.getIdentifier() + ": " +
			 * temp1.getVal());
			 * 
			 * }
			 */

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public List<Request> getRequests() {
		return requests;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}

	// returns random constraint
	public Constraint getRandConstraint() {
		int rand = (int) (Math.random() * constraints.size()) - 1;
		return constraints.get(rand);
	}

	public boolean checkOccuranceOfTopic(String topic) {
		for (Constraint c : constraints) {
			if (c.getValue().equals(topic) || c.getIdentifier().equals(topic)) {
				return true;
			}
		}
		return false;
	}

	public void updateCountOfRequest(RequestAct req) {
		for (Request r : requests) {
			if (r.getIdentifier().equals(req.getType()) && r.getVal().equals(req.getTopics())) {
				r.putCount();
			}
		}
	}

	public Request getNotAnsweredRequest(DialogueType type) {
		Vector<Request> tempRequest = new Vector<Request>();
		for (Request r : requests) {
			if (!r.isAnswered() && r.getCount() < maxRepitionOfRequest
					&& r.getIdentifier().equals(String.valueOf(type))) {
				tempRequest.add(r);
			}
		}
		if (tempRequest.isEmpty()) {
			return null;
		}
		Random rand = new Random();
		int choose = rand.nextInt(tempRequest.size());

		return tempRequest.get(choose);
	}

	public boolean hasNotAnsweredRequests() {
		for (Request r : requests) {
			if (!r.isAnswered()) {
				return true;
			}
		}
		return false;
	}

	public Constraint getSuitableConstraint(Set<String> topics) {
		for (Constraint c : constraints) {
			if (topics.contains(c.getValue()) || topics.contains(c.getIdentifier())) {
				return c;
			}
		}
		return null;
	}

	public String getGoalCompletion() {
		String output = "";
		for (Request r : requests) {
			output += r.getIdentifier() + " completed? " + r.isAnswered() + " asked " + r.getCount() + " times.\n";
		}
		return output;
	}

	public boolean isComplete() {
		for (Request r : requests) {
			if (!r.isAnswered()) {
				return false;
			}
		}
		return true;
	}

}
