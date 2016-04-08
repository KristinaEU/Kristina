package owlSpeak.kristina.emotion;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.apache.xmlbeans.XmlException;

import de.affect.manage.AffectManager;
import de.affect.mood.Mood;
import emotionml.EmotionmlDocument;

public class EmotionGenerator {
	
	private final static String name = "Kristina";
	private static final boolean sGUIMode = false;
	private AffectManager fAM;

	public EmotionGenerator(InputStream computation,
			InputStream character) {

		try {
			fAM = new AffectManager(computation, character, sGUIMode);
		} catch (IOException | XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public KristinaEmotion getCurrentEmotion() {
		Mood m = fAM.getCharacterByName(name).getCurrentMood();
		return new KristinaEmotion(m.getPleasure(), m.getArousal());
	}

	public void processUserEmotion(KristinaEmotion emo) {
		fAM.processPADInput(fAM.getCharacterByName(name),
				new Mood(emo.getValence(), emo.getArousal(), 0), 0.5, null);
	}
	
	public boolean isActive(){
		return fAM==null?false:true;
	}

}
