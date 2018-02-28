package owlSpeak.kristina.emotion;

import static de.affect.emotion.EmotionsPADRelation.getEmotionPADMapping;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.xmlbeans.XmlException;

import owlSpeak.kristina.DialogueAction;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.kristina.KristinaMove;

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

	public KristinaEmotion getCurrentEmotion() throws Exception{
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
		
	}

	public void processUserEmotion(KristinaEmotion emo) {try{
		fAM.processPADInput(fAM.getCharacterByName(name),
				new Mood(Math.pow(10,emo.getValence()-1), emo.getArousal(), 0), 0.3, "User Emotion "+UUID.randomUUID().toString());
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
	
	public void processSystemAction(KristinaAgenda action) {
		try{
				String dialogueAction = action.getDialogueAction();
				switch(dialogueAction){
				case DialogueAction.SHARE_JOY:
					processSystemAction("ShareJoy");
					break;
				case DialogueAction.CHEER_UP:
					processSystemAction("CheerUp");
					break;
				case DialogueAction.CONSOLE:
					processSystemAction("Console");
					break;
				case DialogueAction.CALM_DOWN:
					processSystemAction("CalmDown");
					break;
				case DialogueAction.PERSONAL_APOLOGISE:
				case DialogueAction.SIMPLE_APOLOGISE:
					processSystemAction("Apologise");
					break;
				case DialogueAction.REPHRASE:
					processSystemAction("RequestRephrase");
					break;
				case DialogueAction.ACCEPT:
					processSystemAction("Accept");
					break;
				case DialogueAction.GREETING:
				case DialogueAction.SIMPLE_GREETING:
				case DialogueAction.PERSONAL_GREETING:
					processSystemAction("Greet");
					break;
				case DialogueAction.SAY_GOODBYE:
				case DialogueAction.PERSONAL_GOODBYE:
				case DialogueAction.SIMPLE_GOODBYE:
				case DialogueAction.MEET_AGAIN:
					processSystemAction("Goodbye");
					break;
				case DialogueAction.ANSWER_THANK:
					processSystemAction("AnswerThank");
					break;
				case DialogueAction.SIMPLE_MOTIVATE:
					processSystemAction("Motivate");
					break;
				case DialogueAction.CANNED:
					if(action.getText().contains("sad")){
					processSystemAction("RequestReasonEmotionSad");
					break;
					}
				}
			}catch(Exception e){
				System.err.println("Something went wrong when updating System Emotion:");
				e.printStackTrace();
			}
	}
	
	public void processUserAction(KristinaMove action) {
		List<String> das = action.getRDFType();
		for(String s: das){
			switch(s){
			case DialogueAction.INCOMPREHENSIBLE:
				processUserAction("Incomprehensible");
				break;
			case DialogueAction.GREETING:
				processUserAction("Greet");
				break;
			case DialogueAction.SAY_GOODBYE:
				processUserAction("Goodbye");
				break;
			case DialogueAction.THANK:
				processUserAction("Thank");
				break;
			case DialogueAction.STATEMENT:
			case DialogueAction.DECLARE:
				if(action.hasTopic("Sad")||action.hasNegatedTopic("Good")){
					processUserAction("StatementSad");
				}else if(action.hasTopic("Fine")||action.hasTopic("Good")){
					processUserAction("StatementFine");
				}
				break;
			}
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
	
	public void stop(){
		fAM.stopAll();
	}

}
