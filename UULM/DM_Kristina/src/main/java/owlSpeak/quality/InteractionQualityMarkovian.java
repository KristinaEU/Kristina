package owlSpeak.quality;

import owlSpeak.engine.Core;
import owlSpeak.quality.SVMClassifier.SVMResult;

public class InteractionQualityMarkovian extends InteractionQualitySVM {
	/*
	 * transitionMatrix[from][to]
	 */
//	private final double transitionMatrix[][] = { { 0.5, 0.5, 0.0, 0.0, 0.0 },
//			{ 0.33, 0.33, 0.33, 0.0, 0.0 }, { 0.0, 0.33, 0.33, 0.33, 0.0 },
//			{ 0.0, 0.0, 0.33, 0.33, 0.33 }, { 0.0, 0.0, 0.0, 0.5, 0.5 } };
	
	private final double transitionMatrix[][] = { { 0.7, 0.3, 0.0, 0.0, 0.0 },
			{ 0.25, 0.5, 0.25, 0.0, 0.0 }, { 0.0, 0.25, 0.5, 0.25, 0.0 },
			{ 0.0, 0.0, 0.25, 0.5, 0.25 }, { 0.0, 0.0, 0.0, 0.3, 0.7 } };

	private double stateProp[] = { 0.0, 0.0, 0.0, 0.0, 1.0 };

	public InteractionQualityMarkovian(Core _core) {
		super(_core);
	}

	@Override
	protected int calculateQuality(QualityParameter p) {

		SVMResult result;

		try {
			result = getSVM().evaluate(p);
		} catch (Exception e) {
			result = null;
		}

		int iResult = -1;
		if (result != null) {
			double[] probs = result.getProbabilities();
			double[] newProbs = new double[probs.length];
			double norm = 0.0;
			String observationProbOut = "";
			for (int i=0; i < probs.length; i++) {
				double sum = 0.0;
				for (int j=0; j < stateProp.length; j++) {
					sum += stateProp[j] * transitionMatrix[j][i];
				}
				observationProbOut +=  i + " " + probs[i] + "; ";
				newProbs[i] = sum * probs[i];
				norm += newProbs[i];
			}
			
			int maxI = 0;
			double max = 0.0;
			String probOut = "";
			for (int i=0; i < probs.length; i++) {
				stateProp[i] = newProbs[i]/norm;
				probOut += i + " " + stateProp[i] + "; ";
				if (stateProp[i] > max) {
					max = stateProp[i];
					maxI = i;
				}
				
			}
			System.out.println(observationProbOut);
			System.out.println(probOut);
			iResult = maxI+1;
		}

		System.out.println("IQ score: " + iResult);

		return iResult;
	}

}
