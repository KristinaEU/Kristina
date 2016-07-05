package owlSpeak.quality;

import java.util.Random;

import owlSpeak.engine.Core;

public class InteractionQualityRandom extends InteractionQuality {

	public InteractionQualityRandom(Core _core) {
		super(_core);
	}
	
	protected int calculateQuality(QualityParameter p) {
		Random rand = new Random();
//		int iResult = (rand.nextInt(3)*2)+1;
		int iResult = rand.nextInt(5)+1;

		return iResult;
	}
}
