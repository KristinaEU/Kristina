package owlSpeak.quality;

import owlSpeak.BeliefSpace;
import owlSpeak.OSFactory;
import owlSpeak.Variable;
import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.engine.systemState.SystemState.SystemStateVariant;

public abstract class InteractionQuality {
	
	private static int defaultMode = 3;
	private Core core;
	
	private int currentIQ = 5;

	private final static String VARNAME = "InteractionQuality";
	
//	enum InteractionQualityMode {
//		SVM(0),
//		HMM(1),
//		Random(3);
//		
//		private int value;    
//
//		private InteractionQualityMode(int value) {
//			this.value = value;
//		}
//
//		public int getValue() {
//			return value;
//		}
//	}
	
	public InteractionQuality(Core _core) {
		core = _core;
	}

	public static InteractionQuality getInteractionQuality(Core _core) {
		return getInteractionQuality(_core, defaultMode);
	}
	
	public static InteractionQuality getInteractionQuality(Core _core, int mode) {
		
		switch (mode) {
		case 0:
			return new InteractionQualitySVM(_core);
		case 1:
			return new InteractionQualityMarkovian(_core);
		case 3:
			return new InteractionQualityRandom(_core);
		case 4:
			return new InteractionQualityDummy(_core);
		}
		return null;
	}
	
	public int updateQuality(QualityParameter p, OSFactory factory,
			BeliefSpace bel, String user) {
		currentIQ = calculateQuality(p);
		setVariable(currentIQ, factory, bel, user);
		return currentIQ;
	}
	
	public int updateQualityDummy(QualityParameter p) {
		currentIQ = calculateQuality(p);
		return currentIQ;
	}
	
	protected void setVariable(int iResult, OSFactory factory, BeliefSpace bel, String user) {
		Variable v = null;

		if (!factory.existsVariable(VARNAME)) {
			v = factory.createVariable("InteractionQuality");
		} else {
			v = factory.getVariable(VARNAME);
		}
		
		if (core.actualOnto[Settings.getuserpos(user)].ontoStateVariant == SystemStateVariant.DISTRIBUTION) {
			if (!factory.existsBeliefSpace(user+"BeliefspaceGeneric"))
				bel = factory.createBeliefSpace(user+"BeliefspaceGeneric");
			else
				bel = factory.getBeliefSpace(user+"BeliefspaceGeneric");
		}
					
		CoreMove.setVariable(v.getLocalName(), Integer.toString(iResult),
				factory, core, bel, user);
	}
	
	protected abstract int calculateQuality(QualityParameter p);

	public int getCurrentIQ() {
		return currentIQ;
	}

//	public void setCurrentIQ(int currentIQ) {
//		this.currentIQ = currentIQ;
//	}
}
