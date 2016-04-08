package owlSpeak.quality;

import owlSpeak.engine.Core;
import owlSpeak.quality.SVMClassifier.SVMResult;

public class InteractionQualitySVM extends InteractionQuality {
	
	private SVMClassifier svm = null;
	

	public InteractionQualitySVM(Core _core) {
		super(_core);
		svm = new SVMClassifier("iq_5_1_lgus.model","iq_5_1_lgus.scale");
	}
	
	public InteractionQualitySVM(Core _core, String modelName) {
		super(_core);
		svm = new SVMClassifier(modelName+".model",modelName+".scale");
	}

	protected int calculateQuality(QualityParameter p) {
		SVMResult result;

		boolean useSVM = true;
		int iResult;

		if (useSVM) {
			try {
				result = getSVM().evaluate(p);
				iResult = (int) result.getC();
			} catch (Exception e) {
				iResult = -1;
			}
		} else {
			System.out.println("else is active. iq score always 5");
			iResult = 5;
		}

		//System.out.println("IQ score: " + iResult);

		return iResult;
	}
	
	protected SVMClassifier getSVM() {
		return svm;
	}
}
