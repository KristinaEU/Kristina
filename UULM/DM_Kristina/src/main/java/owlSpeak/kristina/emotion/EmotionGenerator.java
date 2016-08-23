package owlSpeak.kristina.emotion;

import static de.affect.emotion.EmotionsPADRelation.getEmotionPADMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.xmlbeans.XmlException;

import com.sun.corba.se.spi.legacy.connection.GetEndPointInfoAgainException;

import de.affect.appraisal.AppraisalVariables;
import de.affect.emotion.Emotion;
import de.affect.emotion.EmotionType;
import de.affect.emotion.EmotionVector;
import de.affect.manage.AffectManager;
import de.affect.manage.CharacterManager;
import de.affect.mood.Mood;
import de.affect.xml.AffectComputationDocument.AffectComputation.MoodRelations;
import emotionml.EmotionmlDocument;

public class EmotionGenerator {

	private final static String name = "Kristina";
	private static final boolean sGUIMode = false;
	private AffectManager fAM;

	public EmotionGenerator(InputStream computation, InputStream character) {

		try {
			fAM = new AffectManager(computation, character, sGUIMode);
		} catch (IOException | XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public KristinaEmotion getCurrentEmotion() {
		try {
			EmotionVector emo = fAM.getCharacterByName(name)
					.getCurrentEmotions();
			List<Emotion> emos = emo.getEmotions();
			double pleasure = 0;
			double arousal = 0;
			double intensity = 0;
			int nrEmos = 0;
			for (Emotion e : emos) {

			    if (e.getIntensity() > e.getBaseline()) {
				Mood m = (e.getType().equals(EmotionType.Physical)) ? e.getPADValues() : getEmotionPADMapping(e.getType());
		        
				if (m != null) {
					nrEmos++;
					pleasure += m.getPleasure() * e.getIntensity();
					arousal += m.getArousal() * e.getIntensity();
					intensity += (1 - e.getIntensity());
				}
			    }
			}
			if(nrEmos == 0){
				intensity = 1;
				nrEmos = 1;
			}
			Mood m = fAM.getCharacterByName(name).defaultMood();
			pleasure += m.getPleasure() * intensity;
			arousal += m.getArousal() * intensity;
			pleasure = pleasure / (nrEmos);
			arousal = arousal / (nrEmos);
			return new KristinaEmotion(pleasure, arousal);
			/*
			 * Mood m = fAM.getCharacterByName(name).getCurrentMood(); return
			 * new KristinaEmotion(m.getPleasure(), m.getArousal());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void processUserEmotion(KristinaEmotion emo) {try{
		fAM.processPADInput(fAM.getCharacterByName(name),
				new Mood(emo.getValence(), emo.getArousal(), 0), 0.3, "User Emotion "+UUID.randomUUID().toString());
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	
	public void processSystemEmotion(KristinaEmotion emo) {try{
		fAM.processPADInput(fAM.getCharacterByName(name),
				new Mood(emo.getValence(), emo.getArousal(), 0), 1, "System Emotion "+UUID.randomUUID().toString());
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	
	public void processSystemAction(String action) {try{		
		fAM.processAct(action, 1, fAM.getCharacterByName("Kristina"), new CharacterManager[] {fAM.getCharacterByName("User")},new CharacterManager[0], "Action");
	}catch(Exception e){
		e.printStackTrace();
	}
	}
	
	public void processUserAction(String action) {try{
		fAM.processAct(action, 1, fAM.getCharacterByName("User"), new CharacterManager[] {fAM.getCharacterByName("Kristina")},new CharacterManager[0], "Action");
	}catch(Exception e){
		e.printStackTrace();
	}
	}

	public boolean isActive() {
		return fAM == null ? false : true;
	}

}
