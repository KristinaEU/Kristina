package presenter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.rdf.model.Resource;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import model.DialogueAction;
import model.DialogueHistory;
import model.KristinaModel;
import model.DialogueHistory.Participant;
import model.KristinaModel.Scenario;
import model.OntologyPrefix;
import model.UserModel;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyDocumentAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.WorkSpace;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.emotion.EmotionGenerator;
import owlSpeak.kristina.emotion.KristinaEmotion;
import owlSpeak.servlet.OwlSpeakServlet;

public class KristinaPresenter {

	private final static Logger logger = Logger.getLogger("LoggerDM");
	private static Handler handler;

	private static ServletEngine owlEngine;

	private static String user = "user";
	private static Scenario currentScenario = Scenario.UNDEFINED;

	// only needed for demo
	private static List<KristinaMove> systemMove;
	private static List<Set<KristinaMove>> ws;

	public static void init() throws URISyntaxException,
			OWLOntologyCreationException {

		System.setProperty("owlSpeak.settings.file",
				"./conf/OwlSpeak/settings.xml");
		owlEngine = new ServletEngine();
		OwlSpeakServlet.reset(owlEngine, "", "user");

		KristinaModel.init();

		try {
			handler = new FileHandler("log/user" + LocalDate.now()
					+ LocalTime.now().toString().replace(':', '-'));
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.addHandler(handler);

	}

	public static String performDM(float valence, float arousal,
			String content) throws OWLOntologyCreationException,
			OWLOntologyStorageException {

		logger.info("\n----------------------------\nInput LA\n----------------------------\n"
				+ content);
		

		KristinaModel.setUserEmotion(new KristinaEmotion(valence, arousal));
		
		if (content.equals("timeout")) {
			KristinaModel.setSystemEmotion(new KristinaEmotion(valence,0.25));
			LinkedList<KristinaMove> list = new LinkedList<KristinaMove>();
			list.add(KristinaModel.getCannedTextMove("Are you still there?",
					user, owlEngine));
			KristinaModel.setSystemEmotion(new KristinaEmotion(KristinaModel.getCurrentEmotion().getValence(), 0.25));
			KristinaEmotion emo = KristinaModel.getCurrentEmotion();
			return LAConverter.convertFromMove(list, emo.getValence(), emo.getArousal(),
					OntologyPrefix.dialogue);
		} else {
			String output = "";
			List<Resource> userMove = null;
			try {
				userMove = LAConverter.convertToMove(content, user,
						OntologyPrefix.act, OntologyPrefix.dialogue);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// TODO: tmp solution
			if (!content.isEmpty()) {
				// perform dialogue state update

				try {
					ws = KristinaModel.performUpdate(
							userMove, user, currentScenario, valence,
							arousal, owlEngine);

					systemMove = selectSystemMove(ws);
					updateSystemEmotion(systemMove);
					systemMove = realiseCommunicationStyle(systemMove, user);

					KristinaEmotion emo = KristinaModel.getCurrentEmotion();
					output = LAConverter.convertFromMove(systemMove,
							emo.getValence(), emo.getArousal(), OntologyPrefix.dialogue);
				} catch (OWLOntologyDocumentAlreadyExistsException e1) {
					System.err.println(e1.getOntologyDocumentIRI());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {

				KristinaEmotion emo = KristinaModel.getCurrentEmotion();
				output = LAConverter.convertFromEmotion(emo.getValence(), emo.getArousal(),
						OntologyPrefix.dialogue);

			}

			createEvent("Sending System Move");

			return output;
		}
	}

	public static void restart(String u, String scenario) {
		user = u;
		switch (scenario) {
		case "baby":
			currentScenario = Scenario.BABY;
			break;
		case "sleep":
			currentScenario = Scenario.SLEEP;
			break;
		case "pain":
			currentScenario = Scenario.PAIN;
			break;
		case "weather":
			currentScenario = Scenario.WEATHER;
			break;
		case "newspaper":
			currentScenario = Scenario.NEWSPAPER;
			break;
		default:
			currentScenario = Scenario.UNDEFINED;
			break;
		}

		logger.removeHandler(handler);
		try {
			handler = new FileHandler("log/" + user + LocalDate.now()
					+ LocalTime.now().toString().replace(':', '-'));
			logger.addHandler(handler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		OwlSpeakServlet.reset(owlEngine, "", user);
		KristinaModel.restart(user);
	}

	public static boolean hasExtremeEmotion() {
		return KristinaModel.hasExtremeEmotion();
	}
	
	private static List<KristinaMove> realiseCommunicationStyle(List<KristinaMove> l, String user){
		LinkedList<KristinaMove> result = new LinkedList<KristinaMove>();
		for(KristinaMove move: l){
			if(move.getDialogueAction().equals(DialogueAction.ASK_TASK)&&UserModel.isIndirect(user)){
				result.add(KristinaModel.getCannedTextMove("Perhaps I can help you with something", user, owlEngine));
			}else if (move.hasTopic("StartTime")&&move.hasTopic("Sleep")&&(UserModel.isIndirect(user)||UserModel.isVerbose(user))){
				if(UserModel.isIndirect(user)&&UserModel.isVerbose(user)){
					if(Math.random()<0.5){
						result.add(KristinaModel.getCannedTextMove("Eugene ususally goes to bed at midnight. He watches TV before that.", user, owlEngine));
					}else{
						result.add(KristinaModel.getCannedTextMove("He watches TV until midnight.", user, owlEngine));
					}
				}else if(UserModel.isIndirect(user)){
					result.add(KristinaModel.getCannedTextMove("He watches TV until midnight.", user, owlEngine));
				}else{
					result.add(KristinaModel.getCannedTextMove("Eugene ususally goes to bed at midnight. He watches TV before that.", user, owlEngine));
				}
			}else if(move.hasTopic("TV")&&move.hasTopic("EndTime")&&(UserModel.isIndirect(user)||UserModel.isVerbose(user))){
				if(UserModel.isIndirect(user)&&UserModel.isVerbose(user)){
					if(Math.random()<0.5){
						result.add(KristinaModel.getCannedTextMove("He usually watches TV until 9:45. That is when his favourite show ends.", user, owlEngine));
					}else{
						result.add(KristinaModel.getCannedTextMove("His favourite show is broadcasted in the evening.", user, owlEngine));
					}
				}else if(UserModel.isIndirect(user)){
					result.add(KristinaModel.getCannedTextMove("His favourite show is broadcasted in the evening.", user, owlEngine));
				}else{
					result.add(KristinaModel.getCannedTextMove("He usually watches TV until 9:45. That is when his favourite show ends.", user, owlEngine));
				}
			}else if(move.hasTopic("TV")&&UserModel.isVerbose(user)){
				result.add(KristinaModel.getCannedTextMove("He often watches TV. His favourite show is broadcasted very late.", user, owlEngine));
			}else if(move.hasTopic("TV")&&UserModel.isConcise(user)){
				result.add(KristinaModel.getCannedTextMove("Watching TV.", user, owlEngine));
			}else if(move.getDialogueAction().equals(DialogueAction.REJECT)&&currentScenario.equals(Scenario.SLEEP)&&(UserModel.isIndirect(user)||UserModel.isVerbose(user))){
				if(UserModel.isIndirect(user)&&UserModel.isVerbose(user)){
					if(Math.random()<0.5){
						result.add(KristinaModel.getCannedTextMove("No, he sleeps well without medication.", user, owlEngine));
					}else{
						result.add(KristinaModel.getCannedTextMove("He has no trouble falling asleep.", user, owlEngine));
					}
				}else if(UserModel.isIndirect(user)){
					result.add(KristinaModel.getCannedTextMove("He has no trouble falling asleep.", user, owlEngine));
				}else{
					result.add(KristinaModel.getCannedTextMove("No, he sleeps well without medication.", user, owlEngine));
				}
			}else if(move.hasTopic("WaterBottle")&&UserModel.isVerbose(user)){
				result.add(KristinaModel.getCannedTextMove("He likes a lot to use a hot-water bottle; also to drink one glass of milk.", user, owlEngine));
			}else if(move.hasTopic("Sleep")&&move.hasTopic("Duration")&&UserModel.isVerbose(user)){
				result.add(KristinaModel.getCannedTextMove("Eugene usually sleeps 6 hours. That is normal for his age group.", user, owlEngine));
			}else if(move.hasTopic("WakeUp")&&move.hasTopic("Frequency")&&UserModel.isConcise(user)){
				result.add(KristinaModel.getCannedTextMove("Between 1 and 4 times.", user, owlEngine));
			}
			//TODO: This condition should be verified
			else if(move.getDialogueAction().equals(DialogueAction.REQUEST)&&move.hasTopic("Assistance")&&UserModel.isIndirect(user)){
				result.add(KristinaModel.getCannedTextMove("I am not sure what kind of assistance you mean.", user, owlEngine));
			}else if(move.hasTopic("Assistance")&&UserModel.isDirect(user)){
				if(Math.random()<0.5){
					result.add(KristinaModel.getCannedTextMove("You should help him up.", user, owlEngine));
				}else{
					result.add(KristinaModel.getCannedTextMove("Help him up when he is finished.", user, owlEngine));
				}
			}else if(move.hasTopic("Hair")&&UserModel.isDirect(user)){
				if(Math.random()<0.5){
					result.add(KristinaModel.getCannedTextMove("You could comb his hair.", user, owlEngine));
				}else{
					result.add(KristinaModel.getCannedTextMove("You should comb his hair.", user, owlEngine));
				}
			}else if(move.hasTopic("BoardGame")&&UserModel.isVerbose(user)){
				result.add(KristinaModel.getCannedTextMove("He often plays Lude and Morris. It is played by two people and the goal is to...", user, owlEngine));
			}else if(move.hasTopic("Toilet")&&move.hasTopic("Frequency")&&UserModel.isConcise(user)){
				result.add(KristinaModel.getCannedTextMove("Just once.", user, owlEngine));
			}else{
				result.add(move);
			}
		}
		return result;
	}
	
	private static void updateSystemEmotion(List<KristinaMove> systemActions){
		for(KristinaMove action: systemActions){
			String dialogueAction = action.getDialogueAction();
			switch(dialogueAction){
			case DialogueAction.SHARE_JOY:
				KristinaModel.setSystemAction("ShareJoy");
				break;
			case DialogueAction.CHEER_UP:
				KristinaModel.setSystemAction("CheerUp");
				break;
			case DialogueAction.CONSOLE:
				KristinaModel.setSystemAction("Console");
				break;
			case DialogueAction.CALM_DOWN:
				KristinaModel.setSystemAction("CalmDown");
				break;
			case DialogueAction.PERSONAL_APOLOGISE:
			case DialogueAction.SIMPLE_APOLOGISE:
				KristinaModel.setSystemAction("Apologise");
				break;
			case DialogueAction.REPHRASE:
				KristinaModel.setSystemAction("RequestRephrase");
				break;
			case DialogueAction.ACCEPT:
				KristinaModel.setSystemAction("Accept");
				break;
			case DialogueAction.GREETING:
			case DialogueAction.SIMPLE_GREETING:
			case DialogueAction.PERSONAL_GREETING:
				KristinaModel.setSystemAction("Greet");
				break;
			case DialogueAction.SAY_GOODBYE:
			case DialogueAction.PERSONAL_GOODBYE:
			case DialogueAction.SIMPLE_GOODBYE:
			case DialogueAction.MEET_AGAIN:
				KristinaModel.setSystemAction("Goodbye");
				break;
			case DialogueAction.ANSWER_THANK:
				KristinaModel.setSystemAction("AnswerThank");
				break;
			case DialogueAction.SIMPLE_MOTIVATE:
				KristinaModel.setSystemAction("Motivate");
				break;
			case DialogueAction.CANNED:
				if(action.getText().contains("sad")){
				KristinaModel.setSystemAction("RequestReasonEmotionSad");
				break;
				}
			}
		}
	}

	/* Functions needed for the demonstration */

	public static KristinaEmotion getCurrentEmotion() {
		return KristinaModel.getCurrentEmotion();
	}

	public static int getGenerationStatus() {
		return KristinaModel.getGenerationStatus();
	}

	public static int getManagerStatus() {
		if (owlEngine != null) {
			return 0;
		} else {
			return 1;
		}
	}

	public static String[] getWorkspace(String user) {
		
		LinkedList<String> result = new LinkedList<String>();
		if(ws!=null){
		for (Set<KristinaMove> a : ws) {
			if(a != null){
			for(KristinaMove move: a){
				result.add(move.toString());
			}
			}
		}
		}
		return result.toArray(new String[0]);
	}

	public static String[] getSystemMove(String user) {
		// owlEngine.systemCore.actualOnto[Settings.getuserpos(user)]

		LinkedList<String> result = new LinkedList<String>();
		if(systemMove != null){
			for(KristinaMove m: systemMove){
				result.add(m.toString());
			}
		}
		return result.toArray(new String[0]);
	}

	public static void setUserEmotion(KristinaEmotion emo) {
		KristinaModel.setUserEmotion(emo);
	}

	private static List<KristinaMove> selectSystemMove(
			List<Set<KristinaMove>> ws) {
		LinkedList<KristinaMove> moves = new LinkedList<KristinaMove>();
		for (Set<KristinaMove> set : ws) {
			int strategy = (int) (Math.random() * set.size());
			for (KristinaMove move : set) {
				if (strategy == 0) {
					if (!move.getDialogueAction().equals(DialogueAction.EMPTY)) {
						moves.add(move);
						DialogueHistory.add(move, Participant.SYSTEM);
					}
				}
				strategy = strategy - 1;
			}
		}
		if (moves.isEmpty()) {
			moves.add(KristinaModel.getEmptyMove(owlEngine));
		}else{
			KristinaModel.setSystemEmotion(new KristinaEmotion(KristinaModel.getCurrentEmotion().getArousal(),0.25));
		}

		return moves;
	}

	private static void createEvent(String value) {
		String event = "\"<?xml version=\\\"1.0\\\" ?>"
				+ "<events ssi-v=\\\"V2\\\">"
				+ "<event sender=\\\"DM\\\" event=\\\"STATUS\\\" from=\\\"0\\\" dur=\\\"0\\\" prob=\\\"1.000000\\\" type=\\\"STRING\\\" state=\\\"COMPLETED\\\" glue=\\\"0\\\">"
				+ value + "<\\/event>" + "<\\/events>\"";
		try {
			DatagramSocket clientSocket = new DatagramSocket();
			InetAddress IPAddress = InetAddress.getByName("137.250.171.232");
			// InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = event.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, 1337);
			clientSocket.send(sendPacket);
			clientSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
