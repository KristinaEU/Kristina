package usersimulatorCamResInfoSys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class StateAgenda extends Agenda {
	private Vector<StateAgenda> parents;
	private Vector<StateAgenda> children;
	protected Map<SummaryConditionType,SummaryPushActType> mapping;
	private double forwardTransProb;
	private double backwardTransProb;

	public StateAgenda() {
		super();
		parents = new Vector<StateAgenda>();
		children = new Vector<StateAgenda>();
		
	}

	@SuppressWarnings("unchecked")
	public StateAgenda(Agenda a) {
		super();

		this.agenda = (LinkedList<DialogueAct>) a.agenda.clone();
		this.transprob = a.transprob;
		
		parents = new Vector<StateAgenda>();
		children = new Vector<StateAgenda>();
	}

	private void calculateForwardProb(Vector<StateAgenda> prevState, int k) {
		

		for (StateAgenda sA : prevState) {
			 
			this.forwardTransProb += sA.getForwardTransProb()* (k - 1) * this.getTransprob();
		}
	}

	private void calculateBackwardProb(Vector<StateAgenda> nextState, int k) {

		for (StateAgenda sA : nextState) {
			
			this.backwardTransProb += sA.getTransprob() * sA.getBackwardTransProb() + (k + 1);
		}

	}

	private double calculateT(double a_end, double a_i, int k) {

		double t_ij = (a_i * this.getTransprob() * this.getBackwardTransProb()* (k + 1)) / a_end;
		return t_ij;
	}

	public void addChildren(UserGoal goal) {
		 Vector<Agenda> vec = goal.createAgendaVec();
		calcProb(vec);

		for (Agenda a : vec) {

			children.add(new StateAgenda(a));
			StateAgenda s = children.lastElement();
			s.parents.add(this);
			s.forwardTransProb = vec.get(0).getProbability();
		}
	}

	public void addChildren(LinkedHashSet<PushActionCombination> comb) {

			
			for (StateAgenda a :this.pushActs(comb)) {
			children.add(a);
			a.parents.add(this);
		}
		//this.agenda.clear();

	}

	public void addChildren(UserAction userAct, JSONObject object) {

		List<DialogueAct> list = userAct.getDialActList();

		List<DialogueAct> dList = new LinkedList<DialogueAct>();

		if (this.agenda.size() > 1 && list.size() <= this.agenda.size())
			dList = this.agenda.subList(0, list.size());
		else
			dList = this.agenda;
		StateAgenda duplicate =  new StateAgenda(this);

		if (dList.containsAll(list)) {

			duplicate.transprob = calculateNProb(list.size(), this, object);

			for (int i = 0; i < list.size(); i++) {
				duplicate.agenda.pop();
			}

		} else {
			duplicate.transprob = 0.0;
			//duplicate.agenda.clear();
		}

		if (duplicate.transprob != 0.0) {
			children.add(duplicate);
			duplicate.parents.add(this);
			
		}
		//this.agenda.clear();
	}

	/**
	 * Merging of equal Agendas in the state vector during the ongoing dialogue
	 * including the probability of the Agendas.
	 * 
	 * @param vec
	 * @return pruned state vector
	 */
	private static Vector<StateAgenda> mergeStateAgendasWithProb(
			Vector<StateAgenda> vec) {
		Vector<StateAgenda> v = new Vector<StateAgenda>();
		for(StateAgenda a: vec){
		int indexOfA = v.indexOf(a);
		if (indexOfA == -1)
			v.add(a);

		else {
			StateAgenda ag = v.get(indexOfA);
			if (ag.equals(a))
				ag.transprob += a.transprob;
			ag.parents.addAll(a.parents);
		}
				
			
		}
		

		return v;
	}

	public double getForwardTransProb() {
		return forwardTransProb;
	}

	public void setForwardTransProb(double forwardTransProb) {
		this.forwardTransProb = forwardTransProb;
	}

	public double getBackwardTransProb() {
		return backwardTransProb;
	}

	public void setBackwardTransProb(double backwardTransProb) {
		this.backwardTransProb = backwardTransProb;
	}

	public static Map<SummaryConditionType, Map<String,Double>> generateTrainingTree(UserGoal goal,
			Vector<SystemAction> sysVec, Vector<UserAction> userVec) {

		String filePath = "C:\\Users\\mkraus\\Desktop\\pushTransitionModel2.json";
		String filePath1 = "C:\\Users\\mkraus\\Desktop\\TrainingNfull.json";
		JSONObject pushModel = null;
		JSONObject popModel = null;
		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			JSONParser jsonParser = new JSONParser();
			pushModel = (JSONObject) jsonParser.parse(reader);

			FileReader reader1 = new FileReader(filePath1);
			JSONParser jsonParser1 = new JSONParser();
			popModel = (JSONObject) jsonParser1.parse(reader1);

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

		StateAgenda emptyAgenda = new StateAgenda();
		emptyAgenda.addChildren(goal);

		Vector<Vector<StateAgenda>> sortedTree = new Vector<Vector<StateAgenda>>();
		int indexStart = 2;

		for (int i = 0; i < sysVec.size(); i++) {
			System.out.print(i+1+" ");
			Vector<StateAgenda> turnStateAfterSysAct = new Vector<StateAgenda>();
			Vector<StateAgenda> turnStateAfterUserAct = new Vector<StateAgenda>();

			if (i == 0) {
				Vector<List<SummaryPushAction>> sumPushVec = new Vector<List<SummaryPushAction>>();

				Vector<SummaryCondition> sumCondVec = SummaryCondition
						.createSumCondVec(sysVec.get(i), goal);

				Vector<List<PushAction>> pushActList = new Vector<List<PushAction>>();

				for (SummaryCondition sc : sumCondVec) {
					 
					sumPushVec.add(SummaryPushAction.createSumPushList(
							sc, goal, pushModel));
				}

				for (List<SummaryPushAction> l : sumPushVec) {
					
					pushActList.add(PushAction.createPushActionList(l));
				}

				LinkedHashSet<PushActionCombination> comb = PushAction
						.generateCombination(pushActList);
				for (StateAgenda sA : emptyAgenda.getChildren()) {
					sA.addChildren(comb);
					turnStateAfterSysAct.addAll(sA.getChildren());
					for (StateAgenda st : sA.getChildren())
						st.calculateForwardProb(emptyAgenda.getChildren(),
								indexStart);

					

				}
				indexStart++;
				
			} else {
				
				Vector<List<SummaryPushAction>> sumPushVec = new Vector<List<SummaryPushAction>>();

				Vector<SummaryCondition> sumCondVec = SummaryCondition
						.createSumCondVec(sysVec.get(i), goal);

				Vector<List<PushAction>> pushActList = new Vector<List<PushAction>>();

				for (SummaryCondition sc : sumCondVec) {
					 
					sumPushVec.add(SummaryPushAction.createSumPushList(
							sc, goal, pushModel));
				}

				for (List<SummaryPushAction> l : sumPushVec) {
					
					pushActList.add(PushAction.createPushActionList(l));
				}

				LinkedHashSet<PushActionCombination> comb = PushAction
						.generateCombination(pushActList);
				for (StateAgenda sA : sortedTree.get(i * 2 - 1)) {
					sA.addChildren(comb);
					
					for(StateAgenda a: sA.getChildren()){
						a.calculateForwardProb(sortedTree.get(i*2 - 1), indexStart);
						
					}
					
					turnStateAfterSysAct.addAll(sA.getChildren());
				}
			/*	for (StateAgenda st : turnStateAfterSysAct)
					st.calculateForwardProb(sortedTree.get(i - 1), indexStart);
*/
				indexStart++;

			}

			goal.updateConstraints(sysVec.get(i));
			goal.updateRequests(sysVec.get(i));
		
			sortedTree.add(mergeStateAgendasWithProb(turnStateAfterSysAct));
			for (StateAgenda stateA : turnStateAfterSysAct) {
				stateA.addChildren(userVec.get(i), popModel);
				
				for(StateAgenda a : stateA.getChildren()){
					a.calculateForwardProb(turnStateAfterSysAct, indexStart);
				}
				
				turnStateAfterUserAct.addAll(stateA.getChildren());

			}

			/*for (StateAgenda st : turnStateAfterUserAct)
				st.calculateForwardProb(turnStateAfterSysAct, indexStart);
*/
			indexStart++;
			Vector<StateAgenda> prunedStateUser = mergeStateAgendasWithProb(turnStateAfterUserAct);

			sortedTree.add(prunedStateUser);
			if (sortedTree.lastElement().isEmpty())
				break;
		}
		StateAgenda last;
		double a_end;
		if (!sortedTree.lastElement().isEmpty()) {
			last = sortedTree.lastElement().get(0);
			last.setBackwardTransProb(1);
			a_end = last.getForwardTransProb();

		} else {

			return null;

		}

		for (int i = sortedTree.size() - 1; i > 0; i--) {

			for (StateAgenda sA : sortedTree.get(i - 1)) {
				sA.calculateBackwardProb(sortedTree.get(i), i);
			}

		}

		Map<SummaryConditionType, Double> conditionSumMap = new TreeMap<SummaryConditionType, Double>();
		Map<String, Double> condPushSumMap = new TreeMap<String, Double>();
		int k = 2;
		for (int i = 0; i < sortedTree.size(); i += 2) {

			double start = emptyAgenda.getChildren().get(0)
					.getForwardTransProb();

			double t;
			if (i == 0) {
				for (StateAgenda sA : sortedTree.get(i)) {
					t = sA.calculateT(a_end, start, k);

					for (SummaryConditionType s : sA.mapping.keySet()) {
						//String[] a = s.split(",");
						SummaryPushActType type = sA.mapping.get(s);
						String combined = "P(" + type + "|" +s + ")";
						if (!conditionSumMap.containsKey(s))
							conditionSumMap.put(s, t);
						else {
							double current = conditionSumMap.get(s);
							double next = current + t;
							conditionSumMap.replace(s, next);
						}
						if (!condPushSumMap.containsKey(combined))
							condPushSumMap.put(combined, t);
						else {
							double current = condPushSumMap.get(combined);
							double next = current + t;
							condPushSumMap.replace(combined, next);
						}
					}
				}
				k += 2;
			}

			else {
				for (StateAgenda sA : sortedTree.get(i)) {
					for (StateAgenda stateA : sortedTree.get(i - 1)) {
						t = sA.calculateT(a_end, stateA.getForwardTransProb(),
								k);

						for (SummaryConditionType s : sA.mapping.keySet()) {
							
							SummaryPushActType type = sA.mapping.get(s);
							String combined = "P(" + type + "|" +s + ")";
							if (!conditionSumMap.containsKey(s))
								conditionSumMap.put(s, t);
							else {
								double current = conditionSumMap.get(s);
								double next = current + t;
								conditionSumMap.replace(s, next);
							}
							if (!condPushSumMap.containsKey(combined))
								condPushSumMap.put(combined, t);
							else {
								double current = condPushSumMap.get(combined);
								double next = current + t;
								condPushSumMap.replace(combined, next);
							}
						}
					}
				}
				k += 2;
			}
		}

		Map<SummaryConditionType, Map<String, Double>> probabilityMap = new Gson().fromJson(
				pushModel.toString(), new TypeToken<Map<SummaryConditionType, Map<String, Double>>>() {
				}.getType());
		Vector<String> keysCondPush = new Vector<String>(
				condPushSumMap.keySet());
		Vector<SummaryConditionType> keysCond = new Vector<SummaryConditionType>(conditionSumMap.keySet());
		for (SummaryConditionType s1 : keysCond) {
			Map<String,Double> fMap = probabilityMap.get(s1);
			double sum = 0;
			double denominator = conditionSumMap.get(s1);
			for (String s2 : keysCondPush) {
				if (s2.contains(s1.toString())) {
					double enumerator = condPushSumMap.get(s2);
					double calculated = enumerator / denominator;
					if(calculated < 0.0001)
						calculated = 0.0;
					fMap.replace(s2, calculated);
				}
			}
			for(String s: fMap.keySet()){
				sum += fMap.get(s);
				
			}
			for(String s: fMap.keySet()){
				double calc = fMap.get(s);
				fMap.replace(s, calc/sum);
			}
			probabilityMap.replace(s1, fMap);
		}
		
		return probabilityMap;
	}

	public Vector<StateAgenda> getChildren() {
		return children;
	}

	public static void main(String[] args) throws IOException {
		int count=0;
		File f = new File("C:\\Users\\mkraus\\Desktop\\Traindev\\data\\Mar13_S0A1");
		List<String> list = UserSimulator.listDir(f, " ");
		List<String> labelList = UserSimulator.getJSONLabel(list);
		List<String> logList = UserSimulator.getJSONLog(list);
		

	Gson gson = new GsonBuilder().setPrettyPrinting().create();
		System.out.println("Number of dialogues to be processed: "
				+ logList.size());
		int i =207;
		try {
			for (; i < logList.size(); i++) {
				//if(i == 78 || i == 90 )
					//i++;
				
			System.out.println("Dialogues process: " + i);
				UserGoal goal = new UserGoal(labelList.get(i));
				
				Vector<SystemAction> sysVec = SystemAction
						.getTrainingSysAct(logList.get(i));
				Vector<UserAction> userVec = UserAction
						.getTrainingUserAct(labelList.get(i));
				if(userVec.size() < 6){
				System.out.println(goal.toString());
				System.out.println(sysVec.toString());
				System.out.println(userVec.toString());
				
			Map<SummaryConditionType, Map<String, Double>> map = generateTrainingTree(goal, sysVec,
					userVec);

				if (map != null) {
				String s = gson.toJson(map);				File file = new File(
						"C:\\Users\\mkraus\\Desktop\\pushTransitionModel2.json");
					if (!file.exists()) {
						file.createNewFile();
				}

				FileOutputStream fOut = new FileOutputStream(file);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(
						fOut);

				try {

					myOutWriter.append(s);
					myOutWriter.close();
					fOut.close();

				} catch (IOException e) {
					e.printStackTrace();

				}

				} else{
					count++;
					System.out.println("---------------changing goal: " + i
							+ "-----------------");}
			}
			}		} catch (NullPointerException e) {
			System.out.println("Number of processed dialogues: " + i);
			System.out.println("Number of goal changes: "+count);
			throw e;
		}
		System.out.println("Number of processed dialogues: " + i);
		System.out.println("Number of goal changes: "+count);
	}
}
