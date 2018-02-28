package owlSpeak.engine.policy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.exceptions.MatrixException;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.SummaryAgenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.GPDialogueOptimizer;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.UserAction;

public class TrainingGPPolicy extends Policy {

	@Override
	public List<Agenda> policy(Core core, OwlSpeakOntology onto, String user,
			UserAction userAct) {
		
		// update the ontology
		onto.update();
		
		// get all SummaryBeliefPoints of the ontology
		Collection<SummaryBelief> D = onto.factory.getAllSummaryBeliefInstances(); 

		// get actual SummaryBeliefPoint
		SummaryBeliefNonpersistent b;
		int userpos = Settings.getuserpos(user);
		if (userAct != null)
			b = new SummaryBeliefNonpersistent(
					onto.partitionDistributions[userpos], userAct.getMove(),
					onto);
		else
		{
			// initial belief 
			b = new SummaryBeliefNonpersistent(
					onto.partitionDistributions[userpos], null, onto);
			// select an agenda from the initial set of agendas loaded into the workspace
			Agenda[] next = onto.getWorks(userpos).getNext()
					.toArray(new Agenda[0]);
			Agenda a = next[0];
			List<Agenda> result = new LinkedList<Agenda>();
			result.add(a);
			return result;
		}

		// import the matrices my, C and K to calculate the mean and the covariance of the Q-function
		Matrix my = null;
		Matrix C = null;
		Matrix K = null;
		File myFile = new File("./conf/OwlSpeak/models/GP_PolicyModels/my.csv");
		File CFile = new File("./conf/OwlSpeak/models/GP_PolicyModels/C.csv");
		File KFile = new File("./conf/OwlSpeak/models/GP_PolicyModels/K.csv");
		if (myFile.exists() && CFile.exists() && KFile.exists()) {
			try {
				my = MatrixFactory.importFromFile(FileFormat.CSV,
						"./conf/OwlSpeak/models/GP_PolicyModels/my.csv", "\t");
			} catch (MatrixException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				C = MatrixFactory.importFromFile(FileFormat.CSV,
						"./conf/OwlSpeak/models/GP_PolicyModels/C.csv", "\t");
			} catch (MatrixException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				K = MatrixFactory.importFromFile(FileFormat.CSV,
						"./conf/OwlSpeak/models/GP_PolicyModels/K.csv", "\t");
			} catch (MatrixException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// select an agenda from the initial set of agendas loaded into the workspace
			Agenda[] next = onto.getWorks(userpos).getNext()
					.toArray(new Agenda[0]);
			Agenda a = next[0];
			List<Agenda> result = new LinkedList<Agenda>();
			result.add(a);
			return result;
		}
		
//		String message = "b: " + b + "\n";
		
		// get all SummaryAgendas
//		SummaryAgenda[] allActions = onto.factory.getAllSummaryAgendaInstances().toArray(new SummaryAgenda[0]);

		// get all possible SummaryAgendas
		Collection<Agenda> allAgendas = onto.factory.getAllAgendaInstances();
		Collection<Agenda> allPossibleAgendas = new ArrayList<Agenda>();
		Collection<SummaryAgenda> allPossibleSummaryAgendas = new ArrayList<SummaryAgenda>();
		BeliefSpace bs = (Partition) onto.partitionDistributions[Settings.getuserpos(user)].getTopPartition();
		for (Agenda a : allAgendas) {
//			if (checkAgendaRequirements(a, bs, onto, user, true, true) && !a.equals(onto.factory.getAgenda("Masteragenda")) && !a.equals(onto.factory.getAgenda("agenda_Abort")))
			if (checkAgendaRequirements(a, bs, onto, user, true, true) && !a.equals(onto.factory.getAgenda("Masteragenda")))
				allPossibleAgendas.add(a);
		}
		for (Agenda a : allPossibleAgendas) {
			SummaryAgenda s = a.getSummaryAgenda();
			if (!allPossibleSummaryAgendas.contains(s))
				allPossibleSummaryAgendas.add(s);
		}
		SummaryAgenda[] allActions = allPossibleSummaryAgendas.toArray(new SummaryAgenda[0]);
		
		SummaryAgenda agendaMax = null;
		Agenda returnAgenda = null;
		
		// create a new random variable between 0 and 1
//		double randDouble = new Random().nextDouble();
		
		// define exploration parameter
//		double epsilon = 0.3;
				
//		if (randDouble > epsilon) 
//		{
//			// choose the SummaryAgenda with the highest mean of the Q-value
////			double maxMean = Double.NEGATIVE_INFINITY;
////			for (SummaryAgenda a : allActions) 
////			{
////				double meanQ = GPDialogueOptimizer.k_tilde(b, a, D).transpose().mtimes(my).getAsDouble(0, 0);
////				if (meanQ > maxMean)
////				{
////					maxMean = meanQ;
////					agendaMax = a;
////				}
////			}
//			// get the appropriate Agenda to the SummaryAgenda with the highest mean of the Q-value
////			returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
////			if (returnAgenda == null) 
////			{
////				System.err.println("Agenda with the highest mean is not appropriate.");
////				returnAgenda = onto.factory.getAgenda("agenda_Abort");
////			}
//			
//			message += "randDouble > epsilon -> mean \n";
//			
//			// compute all means and map them to the SummaryAgendas
//			Map<Double, Collection<SummaryAgenda>> allActionsSorted = new HashMap<Double, Collection<SummaryAgenda>>();			
//			for (SummaryAgenda a : allActions) 
//			{
//				double meanQ = GPDialogueOptimizer.k_tilde(b, a, D).transpose().mtimes(my).getAsDouble(0, 0);
//				if (allActionsSorted.containsKey(meanQ))
//				{
//					allActionsSorted.get(meanQ).add(a);
//				}
//				else
//				{
//					Collection<SummaryAgenda> c = new ArrayList<SummaryAgenda>();
//		            c.add(a);
//		            allActionsSorted.put(meanQ, c);
//				}
//			}
//			
//			// choose a SummaryAgenda with the maximum mean
//			double maxKeyInMap=(Collections.max(allActionsSorted.keySet()));
//			Collection<SummaryAgenda> allSummaryAgendasWithMaxKey = allActionsSorted.get(maxKeyInMap);
//			while (returnAgenda == null && !allSummaryAgendasWithMaxKey.isEmpty())
//			{
//				SummaryAgenda[] a = allSummaryAgendasWithMaxKey.toArray(new SummaryAgenda[0]);
//				int index = new Random().nextInt(a.length);
//				agendaMax = a[index];
//				returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
//				if (returnAgenda != null && returnAgenda.equals(onto.factory.getAgenda("agenda_Abort")))
//					returnAgenda = null;
//				allSummaryAgendasWithMaxKey.remove(agendaMax);
//			}
//			if (returnAgenda == null) 
//			{
//				message += "Agendas with the highest means are not appropriate. -> agenda_Abort\n";
//				System.err.println("Agendas with the highest means are not appropriate.");
//				returnAgenda = onto.factory.getAgenda("agenda_Abort");
//			}	
//			
//			// choose an appropriate SummaryAgenda, beginning with the SummaryAgenda with the maximum mean
////			while (returnAgenda == null)
////			{
////				double maxKeyInMap=(Collections.max(allActionsSorted.keySet()));
////				SummaryAgenda[] allSummaryAgendasWithMaxKey = allActionsSorted.get(maxKeyInMap).toArray(new SummaryAgenda[0]);
////				int index = new Random().nextInt(allSummaryAgendasWithMaxKey.length);
////				agendaMax = allSummaryAgendasWithMaxKey[index];
////				returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
////				if (returnAgenda != null && returnAgenda.equals(onto.factory.getAgenda("agenda_Abort")))
////					returnAgenda = null;
////				allActionsSorted.get(maxKeyInMap).remove(agendaMax);
////				if (allActionsSorted.get(maxKeyInMap).isEmpty()) allActionsSorted.remove(maxKeyInMap);
////			}
//		}
//		
//		// if randDobule < epsilon or if there's no appropriate Agenda to the SummaryAgenda with the highest mean of the Q-value
////		if (returnAgenda == null) 
//		
//		// if randDobule < epsilon
//		else
//		{
//			message += "randDouble <= epsilon -> rand \n";
//			
//			// choose random an appropriate Agenda
//			while (returnAgenda == null)
//			{
//				int index = new Random().nextInt(allActions.length);
//				agendaMax = allActions[index];
//				returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
//				if (returnAgenda != null && returnAgenda.equals(onto.factory.getAgenda("agenda_Abort")))
//					returnAgenda = null;
//			}
//			
//			// compute all covariances and map them to the SummaryAgendas
////			Map<Double, Collection<SummaryAgenda>> allActionsSorted = new HashMap<Double, Collection<SummaryAgenda>>();			
////			for (SummaryAgenda a : allActions) 
////			{
////				Matrix k_tilde = GPDialogueOptimizer.k_tilde(b, a, D);
////				double covQ = k_tilde.transpose().mtimes(C).mtimes(k_tilde).getAsDouble(0, 0);
////				if (allActionsSorted.containsKey(covQ))
////				{
////					allActionsSorted.get(covQ).add(a);
////				}
////				else
////				{
////					Collection<SummaryAgenda> c = new ArrayList<SummaryAgenda>();
////		            c.add(a);
////		            allActionsSorted.put(covQ, c);
////				}
////			}
//			
//			// choose a SummaryAgenda with the maximum covariance
////			double maxKeyInMap=(Collections.max(allActionsSorted.keySet()));
////			Collection<SummaryAgenda> allSummaryAgendasWithMaxKey = allActionsSorted.get(maxKeyInMap);
////			while (returnAgenda == null && !allSummaryAgendasWithMaxKey.isEmpty())
////			{
////				SummaryAgenda[] a = allSummaryAgendasWithMaxKey.toArray(new SummaryAgenda[0]);
////				int index = new Random().nextInt(a.length);
////				agendaMax = a[index];
////				returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
////				if (returnAgenda != null && returnAgenda.equals(onto.factory.getAgenda("agenda_Abort")))
////					returnAgenda = null;
////				allSummaryAgendasWithMaxKey.remove(agendaMax);
////			}
////			if (returnAgenda == null) 
////			{
////				System.err.println("Agendas with the highest covariance are not appropriate.");
////				returnAgenda = onto.factory.getAgenda("agenda_Abort");
////			}
//			
//			// choose an appropriate SummaryAgenda, beginning with the SummaryAgenda with the maximum covariance
////			while (returnAgenda == null)
////			{
////				double maxKeyInMap=(Collections.max(allActionsSorted.keySet()));
////				SummaryAgenda[] allSummaryAgendasWithMaxKey = allActionsSorted.get(maxKeyInMap).toArray(new SummaryAgenda[0]);
////				int index = new Random().nextInt(allSummaryAgendasWithMaxKey.length);
////				agendaMax = allSummaryAgendasWithMaxKey[index];
////				returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
////				if (returnAgenda != null && returnAgenda.equals(onto.factory.getAgenda("agenda_Abort")))
////					returnAgenda = null;
////				allActionsSorted.get(maxKeyInMap).remove(agendaMax);
////				if (allActionsSorted.get(maxKeyInMap).isEmpty()) allActionsSorted.remove(maxKeyInMap);
////			}
//		}


		// compute all means and covariances
		double[] meanQ = new double[allActions.length];
		double[] covQ = new double[allActions.length];
		int eta = 3;
		int i = 0;
		for (SummaryAgenda a : allActions) {
			Matrix k_tilde = GPDialogueOptimizer.k_tilde(b, a, D);
			meanQ[i] = k_tilde.transpose().mtimes(my).getAsDouble(0, 0);
			covQ[i] = eta * (GPDialogueOptimizer.k(b, a, b, a) - k_tilde.transpose().mtimes(C).mtimes(k_tilde).getAsDouble(0, 0));
			if (covQ[i] < 0)
				covQ[i] =  eta * (k_tilde.transpose().mtimes(K.minus(C)).mtimes(k_tilde).getAsDouble(0, 0));
//			message = message + "a: " + a + ", meanQ: " + meanQ[i] + ", covQ: " + covQ[i] + "\n";
			i++;
		}
		
		// defines how often the sampling is repeated if no appropriate agenda is found
		int numSamples = 1;
		
		// counts how often the sampling is repeated if no appropriate agenda is found
		int sampleCount = 1;
		
		double[] sample = new double[allActions.length];
		double maxSample = Double.NEGATIVE_INFINITY;
		Random random = new Random();
		int j = 0;
		
		while (returnAgenda == null && sampleCount <= numSamples) {
			System.out.println("Sampling " + sampleCount);
			maxSample = Double.NEGATIVE_INFINITY;
			agendaMax = null;
			returnAgenda = null;
			sampleCount ++;
			j = 0;
			
			// get the SummaryAgenda with the highest sampled Q-value
			for (SummaryAgenda a : allActions) {
				sample[j] = meanQ[j] + random.nextGaussian() * covQ[j];
				if (sample[j] > maxSample) {
					maxSample = sample[j];
					agendaMax = a;
				}
				j++;
			}

			// get the appropriate Agenda to the SummaryAgenda with the highest sampled Q-value
			returnAgenda = summaryAgenda2Agenda(agendaMax, onto, user, false);
			
			// it is not allowed to choose agenda_Abort
//			if (returnAgenda != null && returnAgenda.equals(onto.factory.getAgenda("agenda_Abort")))
//				returnAgenda = null;
		}
		
		// if there's no appropriate agenda found
		if (returnAgenda == null) {
			System.err.println("No appropriate Agenda found while sampling.");
//			returnAgenda = onto.factory.getAgenda("agenda_Abort");
		}
		
//		message = message + "Selected: " + returnAgenda + "\n";
//		
//		File output = new File("C:\\Users\\jmiehle\\Desktop\\output.txt");
//		try {
//			FileWriter writer = new FileWriter(output, true);
//			writer.write(message);
//			writer.flush();
//			writer.close();
//		} catch (IOException e) {
//		      e.printStackTrace();
//	    }
		
		List<Agenda> result = new LinkedList<Agenda>();
		result.add(returnAgenda);
		return result;
	}

}
