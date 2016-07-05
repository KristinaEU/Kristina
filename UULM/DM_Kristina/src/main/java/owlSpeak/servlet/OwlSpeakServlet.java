package owlSpeak.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Random;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom.Element;

import owlSpeak.Agenda;
import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.ServletEngine.ConflictException;
import owlSpeak.engine.ServletEngine.DocumentBuildException;
import owlSpeak.engine.Settings;
import owlSpeak.engine.Settings.Usersetting;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.UserAction.UserActionType;
import owlSpeak.engine.his.interfaces.IUserAction;
import owlSpeak.quality.DBEntry;
import owlSpeak.quality.DBLogger;
import owlSpeak.quality.InteractionQuality;
import owlSpeak.quality.InteractionQualitySVM;
import owlSpeak.quality.QualityParameter;

/**
 * Class OwlSpeakServlet the main servlet that grants access to the ServetEngine
 * via doGet()
 * 
 * @author Dan Denich, Max Grotz
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 1.2
 * @see ServletEngine
 * 
 */

public class OwlSpeakServlet {

	private static final long serialVersionUID = 1L;
	protected ServletEngine owlEngine;
	public static Logger logger = Logger.getLogger("OwlSpeakLog");
	
	private static InteractionQuality interactionQ;
	private static InteractionQuality interactionQregular;
	// private static String parameterFileTimeStamp;
	private static String[] metaInfo = { "CallID", "Prediction(IQreduced)",
			"Prediction(IQ)" };
	private static DBLogger dbLogger;
	private static String callID;

	static int debugCounter = 0;

	/**
	 * the init() method. Instantiates a new ServletEngine Object that serves as
	 * interface for accessing the ontologies and passes the context of the
	 * servlet.
	 */
	public void init() {
		// System.out.println("OwlSpeakServlet.init()");
		
		try {
			owlEngine = new ServletEngine();
			System.out.println("Init");

		} catch (NullPointerException ex) {
			System.out
					.println("Critical Error, could not initialize Servlet. Probably some mess with the OwlSpeakOnto.");
			return;
		}
		

		if (Settings.deriveQuality)
			owlEngine.systemCore.resetAllQualityParameters();

		if (Settings.deriveQuality) {
			interactionQ = InteractionQualitySVM
					.getInteractionQuality(owlEngine.systemCore);
			interactionQregular = InteractionQualitySVM.getInteractionQuality(
					owlEngine.systemCore, 0);
			// parameterFileTimeStamp = new
			// SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
			// .format(new Date());
		}

		
	}

	

	

	

	private OwlOutput doReset(String whereAmI, String user) {
		// parameterFileTimeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
		// .format(new Date());
		owlEngine.systemCore.resetAllQualityParameters();

		

		return owlEngine.buildReset(whereAmI, user);

	}

	public static void extractAndUpdateIQ(String confidence, String speak,
			UserActionType userActionType, String user, ServletEngine owlEngine) {
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.actualOnto[Settings
				.getuserpos(user)];

		QualityParameter parameterCollector = null;
		parameterCollector = owlEngine.systemCore.qualityParameter[Settings
				.getuserpos(user)];

		// deriving the last agenda
		Agenda lastAgenda = null;
		if (ontoOfLastAgenda != null)
			lastAgenda = ontoOfLastAgenda.actualAgenda[Settings
					.getuserpos(user)];

		String role = null;
		String activityT = null;
		if (lastAgenda != null) {
			role = lastAgenda.getRole();
			activityT = lastAgenda.getSummaryAgenda().getType().toString();
			
		}

		// TODO update parameterCollector mit
		// parameterCollector.update(speak, event, timeStamp,
		// confidence, systemutterance, bargein, modality,
		// interpretation, activityType, confirmation,
		// userTurnDuration);

		Double confidence2 = 0.0;
		if (confidence == null)
			confidence = "0";

		confidence2 = Double.parseDouble(confidence);

		String confirmation2 = null;
		if (role != null) {

			if (role.contains("confirmation"))
				confirmation2 = "confirm";

			else
				confirmation2 = "no_confirm";

			System.out.println("There exists  : " + confirmation2);
		}

		String event = null;

		if (userActionType == UserActionType.IG)
			event = "success";

		else if (userActionType == UserActionType.OOG)
			event = "nomatch";

		if (!parameterCollector.isFirstRequest()) {
			try {
				parameterCollector.update(speak, event, 0.0, confidence2, lastAgenda.getLocalName(),
						null, null, null, activityT, confirmation2, 0.0);
			} catch (ParseException e) {
				System.err
						.println("Problem with updating the parameters for Iq estimation.");
				e.printStackTrace();
			}
		}

		parameterCollector.setFirstRequest(false);

		// Assumption: onto is only null if OwlSpeak is first
		// called; as no system-user-exchange has taken place yet
		// it is ok to skip it here
		if (ontoOfLastAgenda != null) {
			int quality = interactionQ.updateQuality(parameterCollector,
					ontoOfLastAgenda.factory,
					ontoOfLastAgenda.beliefSpace[Settings.getuserpos(user)],
					user);
			int qualityDummy = interactionQregular
					.updateQualityDummy(parameterCollector);

			if (dbLogger != null) {
				DBEntry dbe = new DBEntry();
				String[] metaValues = { callID, Integer.toString(quality),
						Integer.toString(qualityDummy) };
				dbe.setValues(metaInfo, metaValues);
				dbe.setValues(DBEntry.getFeatureHeaders(),
						parameterCollector.getDBStringVector());

				dbLogger.insertNewEntry(dbe);
			}
		}
	}

	// Sends requests(from user simulator) to Engine and returns document

	public static OwlOutput process(String whereAmI, String agenda,
			String userMove, String confidence, String speak, String user,
			UserActionType userActionType, String noInputCounter,
			ServletEngine owlEngine) throws SecurityException, IOException {

		String error = "error";
		OwlOutput output = null;
		try {
			error = Settings.settingsFile;
		} catch (NullPointerException n) {
			System.out
					.println("<a>Critical "
							+ error
							+ ", could not initialize Servlet. Probably some mess with the OwlSpeakOnto.</a>");
			return null;
		}
		if (owlEngine != null) {
			if (whereAmI != null && user != null) {

				OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.actualOnto[Settings
						.getuserpos(user)];

				if (Settings.deriveQuality) {
					extractAndUpdateIQ(confidence, speak, userActionType, user,
							owlEngine);
				}

				// If there is a move
				if (agenda != null && userMove != null && speak != null) {
			
					String[] moves = new String[1];
					String[] confidences = new String[1];
					String[] speaks = new String[1];
//					String[] moves = new String[2];
//					String[] confidences = new String[2];
//					String[] speaks = new String[2];
//					moves[1] = userMove;
//					confidences[1] = confidence;
//					speaks[1] = speak;
//					
					moves[0] = userMove;
					confidences[0] = confidence;
					speaks[0] = speak;
					
					System.out.println("input: " + userMove + " (" + confidence + "): " + speak);
					
					output = owlEngine.buildWork(whereAmI, agenda, user, moves,
							"1", confidences, speaks);
					// check user response type??
				} else if (noInputCounter != null) {
					UserAction userAction = null;
					if (userActionType == UserActionType.OOG) {
						userAction = UserAction.creatNewUserAction(
								ontoOfLastAgenda, UserActionType.OOG, user);
					}
					output = owlEngine.buildRequest(whereAmI, user,
							noInputCounter, userAction, true);
				} else {
					output = owlEngine.buildError(whereAmI, user);
				}
			}
		}
		return output;
	}

	public static OwlOutput processRequest(String whereAmI,
			UserActionType userActionType, String user, String noInputCounter,
			ServletEngine owlEngine) throws SecurityException, IOException {
		return process(whereAmI, null, null, null, null, user, userActionType,
				noInputCounter, owlEngine);
	}

	public static OwlOutput processWork(String whereAmI, String agenda,
			String userMove, String confidence, String speak, String user,
			ServletEngine owlEngine) throws SecurityException, IOException {
		return process(whereAmI, agenda,
				owlEngine.systemCore.actualOnto[Settings.getuserpos(user)].Name
						+ "_a1n1d_" + userMove, confidence, speak, user,
				UserActionType.IG, null, owlEngine);

	}

	public static OwlOutput processRequestTraining(String whereAmI,
			UserAction userAct, UserActionType userActionType, String user,
			String noInputCounter, ServletEngine owlEngine)
			throws SecurityException, IOException {
//		return process(whereAmI, null, null, null, null, user, userActionType,
//				noInputCounter, owlEngine);

		String error = "error";
		OwlOutput output = null;
		try {
			error = Settings.settingsFile;
		} catch (NullPointerException n) {
			System.out
					.println("<a>Critical "
							+ error
							+ ", could not initialize Servlet. Probably some mess with the OwlSpeakOnto.</a>");
			return null;
		}
		if (owlEngine != null) {
			if (whereAmI != null && user != null) {

				OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.actualOnto[Settings
						.getuserpos(user)];

				// If there is a move
				if (userAct != null) {

					output = owlEngine.buildRequest(whereAmI, user,
							noInputCounter, userAct, false);
					// check user response type??
				} else {
					if (Settings.deriveQuality) {
						extractAndUpdateIQ(null, null, userActionType, user,
								owlEngine);
					}

					if (noInputCounter != null) {
						UserAction userAction = null;
						if (userActionType == UserActionType.OOG) {
							userAction = UserAction.creatNewUserAction(
									ontoOfLastAgenda, UserActionType.OOG, user);
						}
						output = owlEngine.buildRequest(whereAmI, user,
								noInputCounter, userAction, true);
					} else {
						output = owlEngine.buildError(whereAmI, user);
					}
				}
			}
		}
		return output;
	}

	public static IUserAction processWorkTraining(String whereAmI,
			String agenda, String _userMove, String confidence, String speak,
			String user, ServletEngine owlEngine) throws SecurityException,
			IOException, DocumentBuildException, ConflictException {
		// return process(whereAmI, agenda,
		// owlEngine.systemCore.actualOnto[Settings.getuserpos(user)].Name
		// + "_a1n1d_" + _userMove, confidence, speak, user,
		// UserActionType.IG, null, owlEngine);
		
		/*
		 * UserAction a = (UserAction)processWorkTraining(..);
		 * processRequestTraining(.., a, ..);
		 * 
		 */

		String userMove = owlEngine.systemCore.actualOnto[Settings
				.getuserpos(user)].Name + "_a1n1d_" + _userMove;
		UserActionType userActionType = UserActionType.IG;

		String error = "error";
		IUserAction output = null;
		try {
			error = Settings.settingsFile;
		} catch (NullPointerException n) {
			System.out
					.println("<a>Critical "
							+ error
							+ ", could not initialize Servlet. Probably some mess with the OwlSpeakOnto.</a>");
			return null;
		}
		if (owlEngine != null) {
			if (whereAmI != null && user != null) {
				if (Settings.deriveQuality) {
					extractAndUpdateIQ(confidence, speak, userActionType, user,
							owlEngine);
				}

				// If there is a move
				if (agenda != null && userMove != null && speak != null) {

					String[] moves = new String[1];
					String[] confidences = new String[1];
					String[] speaks = new String[1];
					moves[0] = userMove;
					confidences[0] = confidence;
					speaks[0] = speak;
					output = owlEngine.buildWorkAux(whereAmI, agenda, user,
							moves, "1", confidences, speaks);
				}
			}
		}
		return output;
	}

	public static String reset(ServletEngine owlEngine, String whereAmI,
			String user) {
		// parameterFileTimeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
		// .format(new Date());
		owlEngine.systemCore.resetAllQualityParameters();
		interactionQ = InteractionQualitySVM
				.getInteractionQuality(owlEngine.systemCore);
		interactionQregular = InteractionQualitySVM.getInteractionQuality(
				owlEngine.systemCore, 0);

		

		owlEngine.buildReset(whereAmI, user);

		return callID;
	}

	public static void resetSBP(ServletEngine owlEngine) {
		for (int i = 0; i < owlEngine.systemCore.ontologies.size(); i++) {
			owlEngine.systemCore.ontologies.get(i).update();
			OSFactory factory = owlEngine.systemCore.ontologies.get(i).factory;
			Iterator<SummaryBelief> it = factory.getAllSummaryBeliefInstances()
					.iterator();
			while (it.hasNext()) {
				SummaryBelief bel = it.next();
				bel.delete();
			}
			// owlEngine.systemCore.ontologies.get(i).summarySpaceBeliefPoints.clear();
			//
			owlEngine.systemCore.ontologies.get(i).write();
			owlEngine.systemCore.ontologies.get(i).update();
		}
	}

	public static InteractionQuality getQualityObject() {
		return interactionQ;
	}
	
	public static InteractionQuality getQualityObjectRegular() {
		return interactionQregular;
	}
	
	public static void processEmotion(String semantic, String user,ServletEngine owlEngine){
		OwlSpeakOntology actualOnto = null;
		Belief tempBel = null;
		Random randomizer = new Random();
		
		try {
			actualOnto = owlEngine.systemCore.actualOnto[Settings.getuserpos(user)];
			// actualOnto.update();
		} catch (NullPointerException n) {
		}
		BeliefSpace beliefSpace = actualOnto.beliefSpace[Settings.getuserpos(user)];
		Iterator<Belief> beliefsIt = beliefSpace.getHasBelief().iterator();
		
		Vector<Semantic> semantics = new Vector<Semantic>();
		while (beliefsIt.hasNext()) {
			semantics.addAll(beliefsIt.next().getSemantic());
		}
		
		Semantic sem = actualOnto.factory.getSemantic(semantic);
		SemanticGroup semGroup = sem.getSemanticGroup();
		
		if (!semGroup.asSemantic().isMemberOf(semantics)){
			tempBel = actualOnto.factory.createBelief("Belief_"
					+ randomizer.nextInt());
			beliefSpace.addHasBelief(tempBel);
			tempBel.addSemantic(semGroup);
			semantics.add(semGroup.asSemantic());
				
			tempBel = actualOnto.factory.createBelief("Belief_"
					+ randomizer.nextInt());
			beliefSpace.addHasBelief(tempBel);
			tempBel.addSemantic(sem);
			semantics.add(sem);
		}else if (!sem.isMemberOf(semantics)){
			
			Iterator<Semantic> allsemIt = semGroup.getContainedSemantics().iterator();
			while(allsemIt.hasNext()){
				Semantic tmpSem = allsemIt.next();
				if(tmpSem.isMemberOf(semantics)){
					beliefsIt = beliefSpace.getHasBelief().iterator();
					while (beliefsIt.hasNext()) {
						tempBel = beliefsIt.next();
						Vector<Semantic> v = new Vector<Semantic>(
								tempBel.getSemantic());

						if (tmpSem.isMemberOf(v)) {
							tempBel.delete();
						}

					}
				}
			}
			
			tempBel = actualOnto.factory.createBelief("Belief_"
					+ randomizer.nextInt());
			beliefSpace.addHasBelief(tempBel);
			tempBel.addSemantic(sem);
			semantics.add(sem);
		}
	}
}