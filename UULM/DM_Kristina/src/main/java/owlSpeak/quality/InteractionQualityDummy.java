package owlSpeak.quality;

import owlSpeak.engine.Core;

public class InteractionQualityDummy extends InteractionQuality {
	
	final int iq = 3;

	public InteractionQualityDummy(Core _core) {
		super(_core);
	}

	@Override
	protected int calculateQuality(QualityParameter p) {
		
		System.out.println("IQ score: " + iq);
		
		return iq;
	}

}
