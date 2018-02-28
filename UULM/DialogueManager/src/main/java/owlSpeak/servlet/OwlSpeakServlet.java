package owlSpeak.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

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
import owlSpeak.imports.IdacoDB;
import owlSpeak.plugin.GerNet;
import owlSpeak.plugin.SameFileRenamePolicy;
import owlSpeak.quality.DBEntry;
import owlSpeak.quality.DBLogger;
import owlSpeak.quality.InteractionQuality;
import owlSpeak.quality.InteractionQualitySVM;
import owlSpeak.quality.QualityParameter;
import owlSpeak.servlet.document.HtmlVoiceDocument;
import owlSpeak.servlet.document.OwlDocument;
import owlSpeak.servlet.document.VoiceDocument;
import owlSpeak.util.OwlSpeakYoupi;
import fr.limsi.upnp.common.MutableDevice;

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
	public GerNet gNet;

	private VoiceDocument voiceObject;
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
	public void init(ServletEngine engine) {
		
		System.out.println("OwlSpeakServlet.init()");
		
		owlEngine = engine;
		
		if (Settings.youpi == 1)
			startUpnp(owlEngine);

		if (Settings.plugin == 100) {
			gNet = new GerNet();
		}

		if (Settings.deriveQuality)
			owlEngine.systemCore.resetAllQualityParameters();

		voiceObject = new VoiceDocument(null);

		if (Settings.deriveQuality) {
			interactionQ = InteractionQuality
					.getInteractionQuality(owlEngine.systemCore);
			interactionQregular = InteractionQuality.getInteractionQuality(
					owlEngine.systemCore, 0);
			// parameterFileTimeStamp = new
			// SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
			// .format(new Date());
		}

		if (Settings.dbLogging) {
			dbLogger = new DBLogger();
			initDBLogging();
		}
	}

	/**
	 * the destroy() method. Stops UPnP server before calling super method.
	 */
	public void destroy() {
		if (Settings.youpi == 1)
			stopUpnp();
	}

	/**
	 * Starts up the UPnP wrapper of the SDM.
	 * 
	 * @param owlEngine
	 *            the ServletEngine that provides the control methods of the
	 *            dialogue manager.
	 */
	private void startUpnp(ServletEngine owlEngine) {
		OwlSpeakYoupi.create(owlEngine);
		OwlSpeakYoupi.dev.generateUUIDs(Long.valueOf(Settings.youpiID));
		((MutableDevice) OwlSpeakYoupi.dev.getRootDevice())
				.setFriendlyName(Settings.youpiFriendlyName);
		OwlSpeakYoupi.dev.start();
	}

	private void stopUpnp() {
		OwlSpeakYoupi.dev.stop();
	}

	public String doGet( UriInfo uriInfo) {
		try{
		MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

		String s = "";
		

		Set<String> names = queryParams.keySet();

		for(String name: names){
			s += name + "=" + queryParams.getFirst(name) + "; ";
		}
		System.out.println("Query-String: " + s);

		// for (Object name : request.getParameterMap().keySet()) {
		// s += name + "=" + request.getParameter(name) + "; ";
		// }


		String logPath = Settings.homePath + "Logs\\";

		String user = "user";
		String whereAmI = uriInfo.getBaseUri().toString();
		whereAmI = whereAmI.substring(0,whereAmI.length()-1);
		String path = uriInfo.getPath().toString();
		if (!path.isEmpty()){
			whereAmI = whereAmI + "/" + path;
		}
		System.out.println("whereAmI: " + whereAmI);
//		String whereAmI = request.getRequestURL().toString();  -> this worked for jetty
//		String whereAmI = "http://pheasant.e-technik.uni-ulm.de:8080/owlSpeak";

		String command = queryParams.getFirst("com");
		try {
			if (command == null)
				command = "ohneparams";
		} catch (NullPointerException ex) {
		}

		if (command.equalsIgnoreCase("start")) {
			doReset(whereAmI, user);
			command = "request";
		}

		String error = "error";

		String event = queryParams.getFirst("event");

		System.out.println("Event: " + event);

		String ontoname = queryParams.getFirst("onto");
		String noInputCounter = queryParams.getFirst("counter");

		String move = queryParams.getFirst("move");
		String speak = queryParams.getFirst("speak");

		String timeStamp2 = new SimpleDateFormat("HH mm ss S")
				.format(new Date());
		FileHandler fh = null;
		try {
			fh = new FileHandler(logPath + timeStamp2 + "__%g.log",
					200000, 100, true);
			/*
			 * A Logger object is used to log messages for a specific system or
			 * application component The handler receives the log message from the
			 * logger and exports it to a certain target.A handler can be turn off
			 * with the setLevel(Level.OFF) method and turned on with setLevel()
			 * method.
			 * 
			 * The log levels define the severity of a message. The Level class is
			 * used to define which messages should be written to the log.
			 */
			logger.addHandler(fh);
			logger.setLevel(Level.ALL);
		} catch (SecurityException | IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		

		String loggerInfo = "";

		QualityParameter parameterCollector = null;

		SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm ss S");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

		double timeStamp1 = 0;

		try {
			timeStamp1 = ((double) dateFormat.parse(timeStamp2).getTime()) / 1000;
		} catch (ParseException e1) {
			System.err
					.println("timestamp1 could not be generated as timestamp2 could not be parsed. timestamp2: "
							+ timeStamp2);
		}
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.actualOnto[Settings
				.getuserpos(user)];

		if (Settings.deriveQuality) {
			parameterCollector = owlEngine.systemCore.qualityParameter[Settings
					.getuserpos(user)];

			// deriving the last agenda
			Agenda lastAgenda = null;
			if (ontoOfLastAgenda != null)
				lastAgenda = ontoOfLastAgenda.actualAgenda.get(Settings
						.getuserpos(user)).get(0);

			String role = null;

			if (lastAgenda != null)
				role = lastAgenda.getRole();

			String duration = queryParams.getFirst("duration");
			double duration2 = 0.0;
			if (duration == null)
				duration = "0";
			duration2 = (Double.parseDouble(duration)) / 1000;
			// String recording = request.getParameter("recording");

			String modality = queryParams.getFirst("inputmode");

			String bargein = queryParams.getFirst("bargein");

			String confidence = queryParams.getFirst("confidence");
			Double confidence2 = 0.0;
			if (confidence == null)
				confidence = "0";

			confidence2 = Double.parseDouble(confidence);

			String systemutterance = voiceObject.getName();

			String interpretation = queryParams.getFirst("interpretation");

			String activityT = null;
			if (lastAgenda != null) {

				if (lastAgenda.getLocalName().contains("question"))
					activityT = "question";

				else if (lastAgenda.getLocalName().contains("announcement"))
					activityT = "announcement";

				else if (lastAgenda.getLocalName().contains("wait"))
					activityT = "wait";

				else if (lastAgenda.getLocalName().contains("exp_confirm"))
					activityT = "confirmation";

			}

			String confirmation2 = null;
			if (role != null) {

				if (role.contains("confirmation"))
					confirmation2 = "confirm";

				else
					confirmation2 = "no_confirm";

				// System.out.println("There exists  : " + confirmation2);

			}

			if (event != null || "work".equalsIgnoreCase(command)) {

				if (!parameterCollector.isFirstRequest()) {
					try {
						parameterCollector.update(speak, event, timeStamp1,
								confidence2, systemutterance, bargein,
								modality, interpretation, activityT,
								confirmation2, duration2);
					} catch (ParseException e) {
						System.err
								.println("Problem with updating the parameters for Iq estimation.");
						e.printStackTrace();
					}
					// String loggerFile = "C:\\OwlSpeak\\temp\\iq_parameters_"
					// + parameterFileTimeStamp + ".log";
					// parameterCollector.appendToFile(loggerFile);

					int quality2 = interactionQ.updateQuality(
							parameterCollector, ontoOfLastAgenda.factory,
							ontoOfLastAgenda.beliefSpace[Settings
									.getuserpos(user)], user);

					if (dbLogger != null) {
						int quality5 = interactionQregular
								.updateQualityDummy(parameterCollector);

						DBEntry dbe = new DBEntry();
						String[] metaValues = { callID,
								Integer.toString(quality2),
								Integer.toString(quality5) };
						dbe.setValues(metaInfo, metaValues);
						dbe.setValues(DBEntry.getFeatureHeaders(),
								parameterCollector.getDBStringVector());

						dbLogger.insertNewEntry(dbe);
					}
				}
			}
		}

		try {
			error = Settings.settingsFile;
		} catch (NullPointerException n) {
			String out = "";
			out+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			out+="<html><title>owlSpeak Website</title><body>\n";
			out+="<a>Critical "
					+ error
					+ ", could not initialize Servlet. Probably some mess with the OwlSpeakOnto.</a>";
			return out;
		}

		if (!command.equals("ohneparams")
				&& ((user == null) || (Settings.getuserpos(user) == -1))) {
			user = "user";
			OwlDocument VDoc = owlEngine.buildError(whereAmI,
					"unknown User, using default user " + user, user);
			return VDoc.output();
		}

		if (command.equalsIgnoreCase("ohneparams")) {
			String out = "";
			out+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
			out+="<html><title>owlSpeak Website</title><body>\n";
			out+="<a> TEST! </a>";
			return out;
		} else {
			if (Settings.getuserpos(user) == -1) {
				System.out
						.println("No corresponding user found in Settings file.");
			} else {

				if (command.equalsIgnoreCase("request")) {

					if (Settings.deriveQuality
							&& parameterCollector.isFirstRequest()) {

						parameterCollector.setBaseTimeStamp(timeStamp1);
						parameterCollector.setFirstRequest(false);
					}
					
					
					UserAction userAct = null;

					boolean multipart = false;
					if (event != null) {
						loggerInfo = event;
						if (event.equalsIgnoreCase("nomatch")) {
							if (owlEngine.settings.getusersetting(user).docType == Usersetting.ENHANCED_VXM_DOC) {
								multipart = true;
								requestMultipart(uriInfo);
								loggerInfo += " | wav-File written ";
							} else {
								userAct = UserAction.creatNewUserAction(
										ontoOfLastAgenda, UserActionType.OOG,
										user);
							}
						} else if (event.equalsIgnoreCase("noinput")) {
							userAct = UserAction.creatNewUserAction(
									ontoOfLastAgenda, UserActionType.SILENT,
									user);
						}
					}

					if (multipart == false) {
						OwlDocument VDoc = owlEngine.buildRequest(whereAmI,
								user, noInputCounter, userAct, true);
						return VDoc.output();
					}

				} else

				if (command.equalsIgnoreCase("work")) {
					
					if (Settings.idaco) {
						IdacoDB.getIdacoVariables(); // Reading variables from database at the beginning of each move
					}
					
					String agenda = queryParams.getFirst("agenda");
					// String speak = request.getParameter("speak");

					// NEXT LINES HAVE BEEN ADDED
					System.out.println("agenda: " + agenda); // i added

					loggerInfo = "Agenda: " + agenda + " | Move: " + move
							+ " | Speak: " + speak;
					// Activate multiple recognition results in case of docType
					// = 7

					String resultLists[] = new String[Settings.nbNBestListEntries];
					String speakLists[] = new String[Settings.nbNBestListEntries];
					String confidences[] = new String[Settings.nbNBestListEntries];
					String resultLen = null;
					
					
					if (Settings.nbNBestListEntries == 1) {
						resultLists[0] = move;
						speakLists[0] = speak;
						confidences[0] = "1.0";
						resultLen = "1";
					}
					else {
					for (int i = 0; i < Settings.nbNBestListEntries; i++) {
						resultLists[i] = queryParams.getFirst("myResult" + (i));
						speakLists[i] = queryParams.getFirst("Speak" + (i));
						confidences[i] = queryParams.getFirst("Confidence" + (i));
//						System.out.println("myResult" + (i) + " = " + resultLists[i]);
//						System.out.println("Speak" + (i) + " = " + speakLists[i]);
//						System.out.println("Confidence" + (i) + " = " + confidences[i]);
					}
					resultLen = queryParams.getFirst("resultLen");
					}

					
					OwlDocument VDoc = owlEngine.buildWork(whereAmI, agenda,
							user, resultLists, resultLen, confidences,
							speakLists);
					
					if (Settings.idaco) {
						IdacoDB.updateIdacoVariables(); // Updating variables in database after each move
					}
					
					return VDoc.output();

				} else

				if (command.equalsIgnoreCase("reset")) {
					OwlDocument VDoc = doReset(whereAmI, user);
					owlEngine.systemCore.optimizationInfo.reset();
					return VDoc.output();

				} else

				if (command.equalsIgnoreCase("resetone")) {
					OwlDocument VDoc = owlEngine.buildReset(whereAmI, user,
							ontoname);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("error")) {
					OwlDocument VDoc = owlEngine.buildError(whereAmI, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("reloadSettings")) {
					OwlDocument VDoc = owlEngine.buildReloadSettings(whereAmI,
							user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("setSleepEnabled")) {
					OwlDocument VDoc = owlEngine.buildSetSleepEnabled(whereAmI,
							user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("setSleepDisabled")) {
					OwlDocument VDoc = owlEngine.buildSetSleepDisabled(
							whereAmI, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("getSleep")) {
					HtmlVoiceDocument VDoc = new HtmlVoiceDocument();
					Element test = VDoc.newDialogueForm("body", user);
					Element sleepStatus = new Element("sleepStatus");
					if (owlEngine.settings.getusersetting(user).sleep == 1) {
						sleepStatus.addContent("1");
					} else {
						sleepStatus.addContent("0");
					}
					test.addContent(sleepStatus);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("enable")) {
					OwlDocument VDoc = owlEngine.buildEnable(whereAmI,
							ontoname, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("disable")) {
					OwlDocument VDoc = owlEngine.buildDisable(whereAmI,
							ontoname, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("list")) {
					OwlDocument VDoc = owlEngine.buildListOntos(whereAmI, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("createBelief")) {
					String semantic = queryParams.getFirst("semantic");
					OwlDocument VDoc = owlEngine.buildCreateBelief(whereAmI,
							ontoname, semantic, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("setVariable")) {
					String variable = queryParams.getFirst("variable");
					String value = queryParams.getFirst("value");
					OwlDocument VDoc = owlEngine.buildSetVariable(whereAmI,
							ontoname, variable, value, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("getVariable")) {
					String variable = queryParams.getFirst("variable");
					OwlDocument VDoc = owlEngine.buildGetVariable(whereAmI,
							ontoname, variable, user);
					return VDoc.output();
				} else

				if (command.equalsIgnoreCase("writeSets")) {
					owlEngine.settings.write();
					OwlDocument VDoc = owlEngine.buildOutput(whereAmI,
							"writeList", "Sets written", user);
					return VDoc.output();
				}

				else {
					String out = "";
					out+="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
					out+="<html><title>owlSpeak Website</title><body>\n";
					out+="<a> Wrong command or wrong filename </a><br>COM: \n";
					out+=command;
					return out;
				}
				if (!(command.equals("getVariable"))) {
					logger.logp(Level.INFO, "Command: " + command, "",
							loggerInfo);
				}
				fh.close();
				logger.removeHandler(fh);
			}
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}

	private OwlDocument doReset(String whereAmI, String user) {
		// parameterFileTimeStamp = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss")
		// .format(new Date());
		owlEngine.systemCore.resetAllQualityParameters();

		if (Settings.dbLogging)
			initDBLogging();

		return owlEngine.buildReset(whereAmI, user);

	}

	private static void initDBLogging() {
		String callIdHead = new SimpleDateFormat("yyyyMMdd").format(new Date());
		callIdHead = callIdHead.substring(0, 1) + callIdHead.substring(2);

		try {
			DBEntry.setFeatureHeaders(QualityParameter.getHeader());
			DBEntry.setMetaInfo(metaInfo);
			dbLogger.createDBStructure(DBEntry.getColumns());
		} catch (NullPointerException e) {
			System.err.println("Error when initializing DBLogging.");
			e.printStackTrace();
		}

		callID = dbLogger.getNextCallID(callIdHead);
		VoiceDocument.callID = callID; // FIXME dirty! has to be changed!
	}


	public String doPost( UriInfo uriInfo) {
		// requestMultipart(request, response);
		return doGet(uriInfo);

	}

	void requestMultipart(UriInfo uriInfo) {

		// *** Logging ***
		String logPath = Settings.homePath + "Logs\\";
		String timeStamp = new SimpleDateFormat("yyyy_MM_dd")
				.format(new Date());
		FileHandler fh=null;
		try {
			fh = new FileHandler(logPath + timeStamp + "__%g.log",
					200000, 100, true);
		} catch (SecurityException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("timeStamp: " + timeStamp);

		logger.addHandler(fh);
		logger.setLevel(Level.ALL);
		//String whereAmI = request.getRequestURL().toString();
		String user = "user";

		// *** wav-File wird mittels MultipartRequest auf Server gespeichert ***
		SameFileRenamePolicy fg = new SameFileRenamePolicy();
		String sSpeechRecConPath = Settings.homePath
				+ "Plugins\\SpeechRecCon\\";

		try {
			// process the request object, save the audio part, parse the posted
			// variables
			//@SuppressWarnings("unused")
			//com.oreilly.servlet.MultipartRequest mr = new com.oreilly.servlet.MultipartRequest(
			//		request, sSpeechRecConPath, 10 * 1024 * 1024, fg);
		} catch (Exception e) {
			System.out.println("Problem @ MultipartRequest");
			e.printStackTrace();
		}

		// *** Plugins - Aufruf ***
		/*int pluginsActivated = Settings.plugin;
		String sNoInputCounter = request.getParameter("counter");

		System.out.println("Plugin activated: " + pluginsActivated);

		OwlDocument VDoc = owlEngine.buildRequest(whereAmI, user,
				sNoInputCounter);
		if (pluginsActivated == 001 || pluginsActivated == 010
				|| pluginsActivated == 100) {
			System.out.println("wav storaged");
			String timeStamp2 = new SimpleDateFormat("yyyy_MM_dd__HH_mm_ss")
					.format(new Date());

			File source = new File(sSpeechRecConPath + "file.wav");
			File destination = new File(sSpeechRecConPath + "wav\\"
					+ timeStamp2 + ".wav");
			FileCopy fc = new FileCopy();
			fc.copy(source, destination);
		}

		if (pluginsActivated == 001) { // Keywordspotting with Grammar
			VDoc = Spotting.performSpottingGrammar(owlEngine, user, whereAmI,
					sNoInputCounter);
		} else if (pluginsActivated == 010) { // Levensthein
			VDoc = Levenshtein.performLevenshtein(owlEngine, user, whereAmI,
					sNoInputCounter);
		} else if (pluginsActivated == 100) { // GermaNet
			VDoc = gNet.performGermaNet(owlEngine, user, whereAmI,
					sNoInputCounter);
		}*/

		fh.close();
		logger.removeHandler(fh);
		System.out.println("******** Next Request ********"); 
		

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
			lastAgenda = ontoOfLastAgenda.actualAgenda.get(Settings
					.getuserpos(user)).get(0);

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

	public static OwlDocument process(String whereAmI, String agenda,
			String userMove, String confidence, String speak, String user,
			UserActionType userActionType, String noInputCounter,
			ServletEngine owlEngine) throws SecurityException, IOException {

		String error = "error";
		OwlDocument output = null;
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

	public static OwlDocument processRequest(String whereAmI,
			UserActionType userActionType, String user, String noInputCounter,
			ServletEngine owlEngine) throws SecurityException, IOException {
		return process(whereAmI, null, null, null, null, user, userActionType,
				noInputCounter, owlEngine);
	}

	public static OwlDocument processWork(String whereAmI, String agenda,
			String userMove, String confidence, String speak, String user,
			ServletEngine owlEngine) throws SecurityException, IOException {
		return process(whereAmI, agenda,
				owlEngine.systemCore.actualOnto[Settings.getuserpos(user)].Name
						+ "_a1n1d_" + userMove, confidence, speak, user,
				UserActionType.IG, null, owlEngine);

	}

	public static OwlDocument processRequestTraining(String whereAmI,
			UserAction userAct, UserActionType userActionType, String user,
			String noInputCounter, ServletEngine owlEngine)
			throws SecurityException, IOException {
//		return process(whereAmI, null, null, null, null, user, userActionType,
//				noInputCounter, owlEngine);

		String error = "error";
		OwlDocument output = null;
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
		interactionQ = InteractionQuality
				.getInteractionQuality(owlEngine.systemCore);
		interactionQregular = InteractionQuality.getInteractionQuality(
				owlEngine.systemCore, 0);

		if (Settings.dbLogging) {
			dbLogger = new DBLogger();

			initDBLogging();
		}

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