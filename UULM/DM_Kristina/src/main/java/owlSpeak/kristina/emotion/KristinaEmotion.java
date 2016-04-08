package owlSpeak.kristina.emotion;

public class KristinaEmotion {
	private double valence;
	private double arousal;
	
	public KristinaEmotion(double v, double a){
		valence = v;
		arousal = a;
	}

	public double getValence() {
		return valence;
	}

	public double getArousal() {
		return arousal;
	}

}
