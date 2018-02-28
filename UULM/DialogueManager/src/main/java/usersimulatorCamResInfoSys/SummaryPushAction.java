package usersimulatorCamResInfoSys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import usersimulatorCamResInfoSys.sumpushacts.AckSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.AffirmAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.AffirmSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.ByeSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.ConfirmAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.DenyAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.HelloSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.InformAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.InformBYSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.NegateAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.NothingSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.NullSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.RepeatSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.ReqaltsAXSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.RequestASumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.RestartSumPushAct;
import usersimulatorCamResInfoSys.sumpushacts.ThankyouSumPushAct;

/**
 * SummaryPushAction class generated from the SummaryConditions with the
 * Probability P(spA|sC)
 * 
 * @author mkraus
 * 
 */
public abstract class SummaryPushAction {
	protected SummaryPushActType spType;
	private double probability;
	public Map<SummaryConditionType, SummaryPushActType> mapping;

	public SummaryPushAction(double prob, SummaryPushActType t) {
		this.probability = prob;
		this.spType = t;
		mapping = new TreeMap<SummaryConditionType, SummaryPushActType>();
	}

	public abstract void extractInformation(SummaryCondition sc, UserGoal g);

	/**
	 * Generates a list of SummaryPush Actions depending on the
	 * SummaryCondition. If the probability P(spA|sC) = 0 the SummaryPushAction
	 * is discarded
	 * 
	 * @param sc
	 * @param g
	 * @return new list of SummaryPushActions
	 */
	public static List<SummaryPushAction> createSumPushList(
			SummaryCondition sc, UserGoal g, JSONObject jsonObject) {
		List<SummaryPushAction> list = new ArrayList<SummaryPushAction>();

		for (int i = 0; i < SummaryPushActType.values().length; i++) {
			SummaryPushActType t = SummaryPushActType.values()[i];
			double d = getPushTransitionProb(t, sc, jsonObject);
			if (d > 0) {
				InformAXSumPushAct iA = null;
				switch (t) {
				case PushHello:
					HelloSumPushAct h = new HelloSumPushAct(d, t);
					h.extractInformation(sc, g);
					list.add(h);
					break;
				case PushBye:
					ByeSumPushAct b = new ByeSumPushAct(d, t);
					b.extractInformation(sc, g);
					list.add(b);
					break;
				case PushInformAX:
					iA = new InformAXSumPushAct(d, t);
					iA.extractInformation(sc, g);
					list.add(iA);
					break;
				case PushInformBY:
					InformBYSumPushAct iB = new InformBYSumPushAct(d, t);
					SummaryPushAction sA = list.get(2);
					if(sA instanceof InformAXSumPushAct){
					iA = (InformAXSumPushAct) sA;
					iB.extractInformation(sc, g, iA);}
					else
						iB.extractInformation(sc, g);
					list.add(iB);
					break;
				case PushRequestA:
					RequestASumPushAct r = new RequestASumPushAct(d, t);
					r.extractInformation(sc, g);
					list.add(r);
					break;
				case PushReqaltsAX:
					ReqaltsAXSumPushAct rq = new ReqaltsAXSumPushAct(d, t);
					rq.extractInformation(sc, g);
					list.add(rq);
					break;
				case PushConfirmAX:
					ConfirmAXSumPushAct c = new ConfirmAXSumPushAct(d, t);
					c.extractInformation(sc, g);
					list.add(c);
					break;
				case PushAffirm:
					AffirmSumPushAct a = new AffirmSumPushAct(d, t);
					a.extractInformation(sc, g);
					list.add(a);
					break;
				case PushAffirmAX:
					AffirmAXSumPushAct aff = new AffirmAXSumPushAct(d, t);
					aff.extractInformation(sc, g);
					list.add(aff);
					break;
				case PushNegateAX:
					NegateAXSumPushAct n = new NegateAXSumPushAct(d, t);
					n.extractInformation(sc, g);
					list.add(n);
					break;
				case PushDenyAX:
					DenyAXSumPushAct den = new DenyAXSumPushAct(d, t);
					den.extractInformation(sc, g);
					list.add(den);
					break;
				case PushNothing:
					NothingSumPushAct not = new NothingSumPushAct(d, t);
					not.extractInformation(sc, g);
					list.add(not);
					break;
				case PushThankyou:
					ThankyouSumPushAct thx = new ThankyouSumPushAct(d, t);
					thx.extractInformation(sc, g);
					list.add(thx);
					break;
				case PushNull:
					NullSumPushAct nsp = new NullSumPushAct(d, t);
					nsp.extractInformation(sc, g);
					list.add(nsp);
					break;
				case PushAck:
					AckSumPushAct asp = new AckSumPushAct(d, t);
					asp.extractInformation(sc, g);
					list.add(asp);
					break;
				case PushRepeat:
					RepeatSumPushAct rsp = new RepeatSumPushAct(d, t);
					rsp.extractInformation(sc, g);
					list.add(rsp);
					break;
				case PushRestart:
					RestartSumPushAct rstart = new RestartSumPushAct(d, t);
					rstart.extractInformation(sc, g);
					list.add(rstart);
					break;
				default:
					break;
				}
			}
		}

		return list;
	}

	/**
	 * computes the probability P(spA|sC)
	 * 
	 * @param sc
	 * @param sp
	 * @return P(spA|sC)
	 * @throws IOException
	 */
	private static void calculateInitialProb() throws IOException {
		double n;

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		Map<SummaryConditionType, Map<String, Double>> fMap = new TreeMap<SummaryConditionType, Map<String, Double>>();

		SummaryConditionType[] condArr = SummaryConditionType.values();
		SummaryPushActType[] pushArr = SummaryPushActType.values();
		for (SummaryConditionType sct : condArr) {
			Map<String, Double> calculatedMap = new TreeMap<String, Double>();
			for (SummaryPushActType spt : pushArr) {

				/*
				 * if (sct.equals(SummaryConditionType.ReceiveOffer) &&
				 * (spt.equals(SummaryPushActType.PushAck) ||
				 * spt.equals(SummaryPushActType.PushReqaltsAX) || spt
				 * .equals(SummaryPushActType.PushThankyou)|| spt
				 * .equals(SummaryPushActType.PushBye)|| spt
				 * .equals(SummaryPushActType.PushNothing) || spt
				 * .equals(SummaryPushActType.PushNull)|| spt
				 * .equals(SummaryPushActType.PushRepeat) || spt
				 * .equals(SummaryPushActType.PushRequestA))) n = 1.0 / 8; else
				 * if (!(sct.equals(SummaryConditionType.ReceiveOffer))) n =
				 * 1.0/ 17; else n = 0.0;
				 */
				n = 1.0 / 17;
				calculatedMap.put("P(" + spt + "|" + sct + ")", n);
			}
			fMap.put(sct, calculatedMap);
		}
		String s = gson.toJson(fMap);
		File file = new File(
				"C:\\Users\\mkraus\\Desktop\\pushTransitionModel2.json");
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fOut = new FileOutputStream(file);
		OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);

		try {

			myOutWriter.append(s);
			myOutWriter.close();
			fOut.close();

		} catch (IOException e) {
			e.printStackTrace();

		}

	}

	private static double getPushTransitionProb(SummaryPushActType t,
			SummaryCondition sc, JSONObject jsonObject) {
		SummaryConditionType sct = sc.scType;
		double d = 0.0;
		@SuppressWarnings("unchecked")
		Map<String, Double> helpMap = (Map<String, Double>) jsonObject.get(sct
				.toString());
		d = (Double) helpMap.get("P(" + t + "|" + sct + ")");

		return d;
	}

	public SummaryPushActType getType() {
		return spType;
	}

	public void setType(SummaryPushActType type) {
		this.spType = type;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		return "" + spType + "[" + probability + "]";
	}

	public static void main(String[] args) throws IOException {
		calculateInitialProb();
	}
}
