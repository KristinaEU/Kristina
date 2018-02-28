package owlSpeak.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Statement;
import org.jdom.JDOMException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import owlSpeak.OSFactory;
import owlSpeak.SummaryAgenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.Settings.UserOntoSetting;
import owlSpeak.engine.policy.opendial.OpendialDialog;
import owlSpeak.estimationVerbosityDirectness.SVC;
import owlSpeak.kristina.CerthClient;
import owlSpeak.kristina.DialogueAction;
import owlSpeak.kristina.KIConverter;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.kristina.KristinaCulture;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.LAConverter;
import owlSpeak.kristina.OntologyPrefix;
import owlSpeak.kristina.SemanticsOntology;
import owlSpeak.kristina.emotion.EmotionGenerator;
import owlSpeak.kristina.emotion.KristinaEmotion;
import owlSpeak.kristina.verbosity.VerbosityGenerator;
import owlSpeak.servlet.document.KristinaDocument;

//entspricht OwlSpeakServlet

@Path("")
public class KristinaServlet {

	private static ServletEngine owlEngine;
	private static EmotionGenerator emotionGenerator;
	private static CerthClient certhClient;
	private static String whereAmI;
	private static boolean sentToKI;
	private static Boolean need2Inform;

	// ALMA configuration files
	private final static String sALMACOMP = "/EmotionGeneration/AffectComputation.aml";
	private final static String sALMADEF = "/EmotionGeneration/CharacterDefinition.aml";

	// CERTH configuration file
	private final static String sCERTH = "./conf/CERTH/config.xml";

	// Support Vector Classification data files
	private final static String sVERBOSITY = "./conf/estimationVerbosityDirectness/data_verbosity.json";
	private final static String sDIRECTNESS = "./conf/estimationVerbosityDirectness/data_directness.json";
	
	// Support Vector Classifier
	private static SVC clf_verbosity;
	private static SVC clf_directness;

	// for Demo
	private static KristinaAgenda[] sysMoveDemo;
	private static KristinaAgenda[] workspaceDemo;

	// Verbosity Generation
	private static VerbosityGenerator verbosityGen;
	private static int misunderstoodCnt;
	
	// History
	private static Map<String, Integer> recentTopics;
	private static Vector historyVec;
	private static Map<KristinaAgenda, Integer> recentAgendas; 
	private static Map<KristinaAgenda, Integer> elaboratenesPool; 

	// Variables to decide whether a reset is needed
	private static String _user = "";
	private static String _scenario = "";

	public static void init(ServletEngine engine, URI baseUri)
			throws URISyntaxException, OWLOntologyCreationException, IOException, JDOMException {

		System.out.println("KristinaServlet.init()");

		owlEngine = engine;
		whereAmI = baseUri.toString();

		reset();
		verbosityGen = new VerbosityGenerator();
		certhClient = new CerthClient(sCERTH);
		
		clf_verbosity = new SVC(sVERBOSITY);
		clf_directness = new SVC(sDIRECTNESS);

		training();
		
		recentTopics = new HashMap<String, Integer>();
		historyVec = null;
		recentAgendas = new HashMap<KristinaAgenda, Integer>();
		elaboratenesPool = new HashMap<KristinaAgenda, Integer>();
		misunderstoodCnt = 0;
	}

	public static void reset() {

		System.out.println("KristinaServlet.reset()");

		owlEngine.reset();

		for (String user : Settings.usernames) {
			for (UserOntoSetting i : owlEngine.settings.getusersetting(user).userOntoSettings) {
				if (i.ontostatus.equals("enabled")) {
					System.out.println("");
					System.out.println("user: " + user);
					owlEngine.buildRequest(whereAmI, user, "0", null, true);
					SemanticsOntology.restart(user);
					// DialogueHistory.restart(user); TODO
				}
			}
		}

		emotionGenerator = new EmotionGenerator(KristinaServlet.class.getResourceAsStream(sALMACOMP),
				KristinaServlet.class.getResourceAsStream(sALMADEF));

		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
		OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
		OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
		OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;

		Collection<KristinaAgenda> allKristinaAgendaInstances = new OSFactory(dmOnto, factory, manager)
				.getAllKristinaAgendaInstances();
		for (KristinaAgenda a : allKristinaAgendaInstances) {
			if (a.getIsNonPersistent()) {
				new OSFactory(dmOnto, factory, manager).removeKristinaAgenda(a);
			}
		}

		Collection<KristinaMove> allKristinaMoveInstances = new OSFactory(dmOnto, factory, manager)
				.getAllKristinaMoveInstances();
		for (KristinaMove m : allKristinaMoveInstances) {
			if (m.getIsNonPersistent()) {
				new OSFactory(dmOnto, factory, manager).removeKristinaMove(m);
			}
		}
		
		recentTopics = new HashMap<String, Integer>();
		historyVec = null;
		recentAgendas = new HashMap<KristinaAgenda,Integer>();
		elaboratenesPool = new HashMap<KristinaAgenda,Integer>();
		misunderstoodCnt = 0;
	}

	/**
	 * Performs the training procedure for parameter estimation at start of DM
	 */
	private static void training() {

		// define paths to training files
		String trainingFileGerman = "./conf/OpenDial/German_training.xml";
		String trainingFilePolish = "./conf/OpenDial/Polish_training.xml";
		String trainingFileSpanish = "./conf/OpenDial/Spanish_training.xml";
		String trainingFileTurkish = "./conf/OpenDial/Turkish_training.xml";
System.out.println("in training");
		OpendialDialog od = OpendialDialog.getInstance();
		System.out.println("dialogue instance successfull");

		boolean res1 = false;
		boolean res2 = false;
		boolean res3 = false;
		boolean res4 = false;
		boolean res5 = false;
		try {
			System.out.println("################# start training #################");

			// Repeat for all languages
			// GERMAN
			// call opendial model for opendial reasons
			od.updateVariable("kiVariable", "");
			od.updateVariable("userCulture", "German");
			od.updateVariable("a_u", "Greet");

			// read input file
			res1 = od.wizardTraining(trainingFileGerman);
			// wait until training finishes (opendial reasons)
			Thread.sleep(20000);

			// POLISH
			od.updateVariable("kiVariable", "");
			od.updateVariable("userCulture", "Polish");
			od.updateVariable("a_u", "Greet");

			res2 = od.wizardTraining(trainingFilePolish);
			Thread.sleep(25000);

			// SPANISH
			od.updateVariable("kiVariable", "");
			od.updateVariable("userCulture", "Spanish");
			od.updateVariable("a_u", "Greet");

			res3 = od.wizardTraining(trainingFileSpanish);
			Thread.sleep(7000);

			// TURKISH
			od.updateVariable("kiVariable", "");
			od.updateVariable("userCulture", "Turkish");
			od.updateVariable("a_u", "Greet");

			res4 = od.wizardTraining(trainingFileTurkish);
			Thread.sleep(7000);

			// call opendial model 2 times to reset dialogue manager (opendial
			// reasons)
			for (int i = 0; i < 2; i++) {
				od.updateVariable("a_u", "Greet");
				Thread.sleep(100);
			}

			// end the learning mode
			res5 = od.terminateWizard();
			// export the estimated parameters
			od.parameterExport("./conf/OpenDial/savedParameters.xml");

			if (res1 && res2 && res3 && res4 && res5) {
				System.out.println("Training successfull");
			} else {
				System.out.println("Training not successfull");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("################# end training #################");
	}

	public static void close() {

		System.out.println("KristinaServlet.close()");
		if (emotionGenerator != null)
			emotionGenerator.stop();
		
		
	}

	public static void setUserCulture(String ontoName, String user) {

		switch (user) {
		case "elisabeth":
			owlEngine.setVariable(ontoName, "var_UserCulture", KristinaCulture.GERMAN, user);
			break;
		case "hans":
			owlEngine.setVariable(ontoName, "var_UserCulture", KristinaCulture.GERMAN, user);
			break;
		case "iwona":
			owlEngine.setVariable(ontoName, "var_UserCulture", KristinaCulture.POLISH, user);
			break;
		case "maria":
			owlEngine.setVariable(ontoName, "var_UserCulture", KristinaCulture.SPANISH, user);
			break;
		case "juan":
			owlEngine.setVariable(ontoName, "var_UserCulture", KristinaCulture.SPANISH, user);
			break;
		case "claudia":
			owlEngine.setVariable(ontoName, "var_UserCulture",
					KristinaCulture.GERMAN, user);
			break;
		case "jana":
			owlEngine.setVariable(ontoName, "var_UserCulture",
					KristinaCulture.POLISH, user);
			break;
		case "mustafa":
			owlEngine.setVariable(ontoName, "var_UserCulture",
					KristinaCulture.TURKISH, user);
			break;
		case "carmen":
			owlEngine.setVariable(ontoName, "var_UserCulture",
					KristinaCulture.SPANISH, user);
			break;
		case "carlos":
			owlEngine.setVariable(ontoName, "var_UserCulture",
					KristinaCulture.SPANISH, user);
			break;
		default:
			System.err.println("No Culture defined for userName " + user);
		}
	}
	
	private static float getVerbProb(String culture){
		switch(culture){
		case KristinaCulture.SPANISH:
			return 0.7f;
		case KristinaCulture.ARAB:
			return 0.6f;
		case KristinaCulture.TURKISH:
			return 0.5f;
		case KristinaCulture.POLISH:
			return 0.3f;
		case KristinaCulture.GERMAN:
			return 0.2f;
		default:
			return 0.0f;
		}
	}

	public static KristinaDocument processKristina(
			OwlSpeakOntology ontoOfLastAgenda, OWLOntology dmOnto,
			OWLDataFactory factory, OWLOntologyManager manager, String user,
			KristinaMove userMove, String valenceUser, String arousalUser,
			String scenario, String lang, String json, Handler handler) throws Exception {

		// update scenario-specific variables
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_Scenario", scenario, user);

		// update user-specific variables
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_UserValence", valenceUser, user);
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_UserArousal", arousalUser, user);
		setUserCulture(ontoOfLastAgenda.Name, user);
		
		// update system-specific variables
		KristinaEmotion sysEmo = getCurrentEmotion();
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_SystemValence", String.valueOf(sysEmo.getValence()), user);
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_SystemArousal", String.valueOf(sysEmo.getArousal()), user);

		// estimate current verbosity and directness from user input and update
		// variables
		float currentVerbosity = 0f;
		float currentDirectness = 0f;
		int i = 0;
		for(String s : userMove.getRDFType()){
			i++;
			currentVerbosity+= clf_verbosity.scaleAndPredictVerbosity(s.substring(s.indexOf('#') + 1), userMove.getNrWords());
			currentDirectness+=clf_directness.scaleAndPredictDirectness(s.substring(s.indexOf('#') + 1), currentVerbosity, userMove.getNrWords());
		}
		currentVerbosity = currentVerbosity/i;
		currentDirectness = currentDirectness/i;
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_CurrentVerbosity", String.valueOf(currentVerbosity), user);
		owlEngine.setVariable(ontoOfLastAgenda.Name, "var_CurrentDirectness", String.valueOf(currentDirectness), user);

		// increment the age of all KI-dependent KristinaAgendas in the
		// workSpace

		Collection<KristinaAgenda> all = new OSFactory(dmOnto, factory, manager)
				.getAllKristinaAgendaInstances();
		for (KristinaAgenda a : all) {
			if (ontoOfLastAgenda.workSpace[Settings.getuserpos(user)].getNext().contains(a.asAgenda())
					&& a.getLocalName().contains("agenda_KI")){
				a.incrementAge();
			}
		}


		// get KI-dependent KristinaAgendas and write them into the workSpace
		String offer = Core.getVariableString(ontoOfLastAgenda.factory.getVariable("var_Offer"),
				ontoOfLastAgenda.beliefSpace[Settings.getuserpos(user)]);
		offer = offer != null ? offer : "";
		
		
		if((userMove.getRDFType().contains(DialogueAction.REQUEST)
				|| userMove.getRDFType().contains(DialogueAction.BOOL_REQUEST)
				|| userMove.getRDFType().contains(DialogueAction.STATEMENT))
				&& (userMove.hasTopic("Like")||userMove.hasTopic("Want"))&& userMove.hasTopic("See")){
			userMove.specify("Accept");
		}
		if((offer.equals("helpful")&&userMove.getRDFType().contains(DialogueAction.STATEMENT))
				&& (userMove.hasTopic("Helpful")|| userMove.hasTopic("Informative"))){
			userMove.specify("Accept");
		}
		if((offer != null && !offer.isEmpty() && userMove.getRDFType().contains(DialogueAction.ACKNOWLEDGE))){
			userMove.specify("Accept");
		}
		if(userMove.getRDFType().contains(DialogueAction.THANK)&&userMove.getSpeak().toLowerCase().contains("video")){
			userMove.specify("ThankVideo");
		}
		
		if((userMove.getRDFType().contains(DialogueAction.REQUEST)
				|| userMove.getRDFType().contains(DialogueAction.BOOL_REQUEST))
				&& userMove.hasTopic("Video")&& ((userMove.hasTopic("Once")&&(userMove.hasTopic("Play")||userMove.hasTopic("Show"))) || userMove.hasTopic("Replay"))){
			userMove.specify("RequestReplay");	
		}
		
					

		if (((userMove.getRDFType().contains(DialogueAction.REQUEST)
				|| userMove.getRDFType().contains(DialogueAction.BOOL_REQUEST))&&!(userMove.hasTopic("AnythingElse")||(userMove.hasTopic("Else")&&userMove.hasTopic("Something"))))
				||userMove.getRDFType().contains(DialogueAction.REQUEST_REPLAY)
				|| userMove.getRDFType().contains(DialogueAction.STATEMENT)
				|| userMove.getRDFType().contains(DialogueAction.DECLARE)
				|| userMove.getRDFType().contains(DialogueAction.ACCEPT)
				&& offer != null && !offer.isEmpty()
				|| userMove.getRDFType().contains(
						DialogueAction.REQUEST_ADDITIONAL)
				|| (userMove.getRDFType().contains(DialogueAction.REJECT) && (offer.equals("appointment2") || offer.equals("pain")))) {
			
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("weather")
					|| (userMove.getRDFType().contains(DialogueAction.REQUEST) || userMove
							.getRDFType().contains(DialogueAction.BOOL_REQUEST))
					&& userMove.hasTopic("Weather")) {
				userMove.specify("RequestWeather");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("newspaper")
					|| (userMove.getRDFType().contains(DialogueAction.REQUEST) || userMove
							.getRDFType().contains(DialogueAction.BOOL_REQUEST))
					&& userMove.hasTopic("Newspaper")) {
				userMove.specify("RequestArticles");
			}
			if (((userMove.getRDFType().contains(DialogueAction.REQUEST) || userMove
					.getRDFType().contains(DialogueAction.BOOL_REQUEST))
					&& (offer.equals("article")||(userMove.hasTopic("Article")&&userMove.hasTopic("Read"))))) {
				userMove.specify("RequestNewspaper");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("event")) {
				userMove.specify("RequestLocalEvent");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("appointment")) {
				userMove.specify("RequestAppointment");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("socialMedia")) {
				userMove.specify("RequestSocialMedia");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("recipe")) {
				userMove.specify("RequestRecipe");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("infoSleep")) {
				userMove.specify("RequestInfoSleep");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("infoSleepHygiene")) {
				userMove.specify("RequestInfoSleepHygiene");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("infoDementia")) {
				userMove.specify("RequestInfoDementia");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("infoDiabetes")) {
				userMove.specify("RequestInfoDiabetes");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("park")) {
				userMove.specify("RequestClosestParks");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("activitiesBaby")) {
				userMove.specify("RequestActivitiesBaby");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("infoProtectionBaby")) {
				userMove.specify("RequestInfoProtectionBaby");
			}
			if (userMove.getRDFType().contains(DialogueAction.ACCEPT)
					&& offer.equals("healthCenter")) {
				userMove.specify("RequestClosestHealthCenter");
			}
			if(offer.equals("appointment2")&&
					((userMove.getRDFType().contains(DialogueAction.STATEMENT)
					&&userMove.hasTopic("Show")) || userMove.getRDFType().contains(DialogueAction.REJECT))){
				userMove.specify("RequestAppointment");
			}
			if(offer.equals("ki")){
				owlEngine.setVariable(ontoOfLastAgenda.Name, "var_Offer",
						"", user);
			}
			Set<KristinaAgenda> agendasKI = null;
			if(userMove.getRDFType().contains(DialogueAction.REQUEST_REPLAY)){
				Map<KristinaAgenda, Integer> la = recentAgendas.entrySet().stream().filter(e -> e.getKey().getLocalName().contains("KI") ).collect(Collectors.toMap(e -> e.getKey(),e -> e.getValue()));
				int tmp = Integer.MAX_VALUE;
				for(KristinaAgenda ka: la.keySet()){
					if(la.get(ka)< tmp){
						tmp = la.get(ka);
					}
				}
				final int minimumAge = tmp;
				Set<KristinaAgenda> tmpSet = la.entrySet().stream().filter(a -> a.getValue()==minimumAge).map(e -> e.getKey()).collect(Collectors.toSet());
				agendasKI = new HashSet<KristinaAgenda>();
				for(KristinaAgenda a: tmpSet){
					KristinaAgenda agenda = new OSFactory(dmOnto, factory, manager)
							.createKristinaAgenda("agenda_KI_" + UUID.randomUUID().toString());
					
					agenda.addDialogueAction(IRI.create(a.getDialogueAction()));
					agenda.copy(a, user);
					agendasKI.add(agenda);
				}
			}else{
				agendasKI = askKI(userMove,
					Double.parseDouble(valenceUser),
					Double.parseDouble(arousalUser), user, scenario, lang, json, dmOnto,
					manager, factory, handler);
			}
			
			for (KristinaAgenda b : agendasKI) {
				System.out.println("added: "+b.getLocalName()+" "+b.toString());
				b.setAge(0);
				b.setIsNonPersistent(true);
				ontoOfLastAgenda.workSpace[Settings.getuserpos(user)].addNext(b);
				// add dummyUserMove -> each Agenda containing it will wait for
				// some user input
				b.addHas(ontoOfLastAgenda.factory.getKristinaMove("dummyUserMove"));
				// add SummaryAgenda for OwlSpeak reasons
				SummaryAgenda sumAgenda = ontoOfLastAgenda.factory.getSummaryAgenda("sumAgenda");
				b.addSummaryAgenda(sumAgenda);
				sumAgenda.addSummarizedAgenda(b);
				
				

				// extract useful information from KI input
				if (b.isDialogueAction(DialogueAction.SHOW_WEATHER)) {
					owlEngine.setVariable(ontoOfLastAgenda.Name, "var_Weather", b.getWeather(), user);
				}
				if(b.isDialogueAction(DialogueAction.PROACTIVE_CANNED)||b.isDialogueAction(DialogueAction.PROACTIVE_LIST)||b.isDialogueAction(DialogueAction.REQUEST_CLARIFICATION)){
					need2Inform = true;
				}
			}

		}

		ontoOfLastAgenda.write();
		ontoOfLastAgenda.update();


		String agenda = ontoOfLastAgenda.actualAgenda.get(Settings.getuserpos(user)).get(0).getLocalName();

		int resultLen = 1; // predefined as LA only forwards best result
		String moveList[] = new String[resultLen];
		String speakList[] = new String[resultLen];
		String confidenceList[] = new String[resultLen];

		moveList[0] = ontoOfLastAgenda.Name + "_a1n1d_" + userMove.getLocalName();
		speakList[0] = URLDecoder.decode(userMove.getSpeak(), "utf-8");
		confidenceList[0] = String.valueOf(userMove.getConfidence());
		return (KristinaDocument) owlEngine.buildWork(whereAmI, agenda, user, moveList, String.valueOf(resultLen),
				confidenceList, speakList);

	}

	private static Set<KristinaAgenda> askKI(KristinaMove userMove,
			double valence, double arousal, String user, String scenario, String lang, String json, 
			OWLOntology dmOnto, OWLOntologyManager manager,
			OWLDataFactory factory, Handler handler) throws Exception {

		if (!sentToKI) {
			sentToKI = true;
			ByteArrayOutputStream input = new ByteArrayOutputStream();
			Model model = userMove.getModel();
			List<Statement> stmts = model
					.listStatements(null, model.getProperty(OntologyPrefix.act + "textualContent"), (String) null)
					.toList();
			for (Statement s : stmts) {
				model.remove(s);
				model.add(s.getSubject(), s.getPredicate(), URLEncoder.encode(s.getString(), "utf-8"));
			}

			Logger log1 = Logger.getLogger("DM2KI");
			log1.setUseParentHandlers(false);
			log1.addHandler(handler);

			userMove.getModel().write(input, "TURTLE");
			log1.info(input.toString("UTF-8"));

			String rdfMoves = certhClient.post(input.toString("UTF-8"), valence, arousal, user, scenario, lang, json);
			need2Inform = rdfMoves.toLowerCase().contains("ProactiveResponse") || rdfMoves.contains("ClarificationResponse") || rdfMoves.contains("SpecifyingInformationResponse");
//			String rdfMoves = certhClient.simulate(scenario);
			Logger log2 = Logger.getLogger("KI2DM");
			log2.setUseParentHandlers(false);
			log2.addHandler(handler);
			log2.info(rdfMoves);

			return KIConverter.convertToKristinaAgendas(rdfMoves, user, dmOnto, factory, manager, OntologyPrefix.onto,
					OntologyPrefix.dialogue);
		}
		return new HashSet<KristinaAgenda>();
	}

	public static KristinaEmotion getCurrentEmotion() {
		try {
			return emotionGenerator.getCurrentEmotion();
		} catch (Exception e) {
			e.printStackTrace();
			return new KristinaEmotion(0, 0);
		}
	}

	@POST
	@Produces("application/rdf+xml")
	@Consumes(MediaType.APPLICATION_JSON)
	public synchronized String post(final String json) {
		System.out.println("received POST");

		sentToKI = false;
		need2Inform = false;

		Handler handler = null;

		try {
			long start = System.currentTimeMillis();

			// set up logger for evaluation and debugging
			try {
				LocalTime t = LocalTime.now();
				handler = new FileHandler("log/" + LocalDate.now() + "_" + String.format("%02d", t.getHour()) + "_"
						+ String.format("%02d", t.getMinute()) + "_" + String.format("%02d", t.getSecond()) + "_"
						+ String.format("%09d", t.getNano()) + ".log");
				handler.setEncoding("utf-8");
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// read json input
			Logger log1 = Logger.getLogger("VSM2DM");
			log1.setUseParentHandlers(false);
			log1.addHandler(handler);
			log1.info(json);
			JsonReader jsonReader = Json.createReader(new StringReader(json));
			JsonObject j = jsonReader.readObject();

			String valenceUser = "0";
			try {
				valenceUser = StringEscapeUtils
						.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getString("valence"));
			} catch (ClassCastException e) {
				valenceUser = StringEscapeUtils.unescapeEcmaScript(
						j.getJsonObject("data").getJsonObject("fusion").getJsonNumber("valence").toString());
			}

			String arousalUser = "0";
			try {
				arousalUser = StringEscapeUtils
						.unescapeEcmaScript(j.getJsonObject("data").getJsonObject("fusion").getString("arousal"));
			} catch (ClassCastException e) {
				arousalUser = StringEscapeUtils.unescapeEcmaScript(
						j.getJsonObject("data").getJsonObject("fusion").getJsonNumber("arousal").toString());
			}

			String user = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("user")).toLowerCase();
			String scenario = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("scenario"))
					.toLowerCase();
			String lang = StringEscapeUtils.unescapeEcmaScript(j.getJsonObject("meta").getString("language"))
					.toLowerCase();

			String content = StringEscapeUtils
					.unescapeEcmaScript(j.getJsonObject("data").getString("language-analysis"));
			
			if (!user.equals(_user) || !scenario.equals(_scenario)) {
				reset();
				_user = user;
				_scenario = scenario;
			}

			OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
			OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
			OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
			OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;
			try {
				KristinaMove userMove = LAConverter.convertToKristinaMove(content, user, OntologyPrefix.act,
						OntologyPrefix.dialogue, dmOnto, factory, manager);

				System.out.println("userMove: " + userMove);	

				Logger log = Logger.getLogger("usrmv");
				log.setUseParentHandlers(false);
				log.addHandler(handler);
				log.info(user + ": " + userMove.toString());

				emotionGenerator.processUserAction(userMove);

				//update recent Topics
				recentTopics = recentTopics.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()+1));
				recentTopics = recentTopics.entrySet().stream().filter(e-> (e.getValue()< 20)).collect(Collectors.toMap(e-> e.getKey(), e -> e.getValue(), (t1,t2) -> Math.min(t1,t2)));
				recentTopics.putAll(userMove.getTopics().stream().map(s-> s.split("#")[1].toLowerCase()).collect(Collectors.toMap(t -> t, t -> 0, (t1,t2) -> Math.min(t1,t2))));

				Vector tmpVec = verbosityGen.getVector(userMove, lang);
				if(tmpVec != null){
				if(historyVec==null){
					historyVec = tmpVec;
				}else{
					historyVec = historyVec.add(tmpVec).scale(0.5);
				}
				}


				// DialogueHistory.add(userMove, Participant.USER); TODO
				KristinaDocument Doc = processKristina(ontoOfLastAgenda,
						dmOnto, factory, manager, user, userMove, valenceUser,
						arousalUser, scenario, lang, json, handler);

				List<KristinaAgenda> selectedAgendas = Doc.getAgenda();

				

				//add mandatory KI responses
				
				for(KristinaAgenda a : selectedAgendas){
					if(a.isDialogueAction(DialogueAction.REQUEST_CLARIFICATION)){
						break;
					}
					if(!a.getMandatoryInformation().isEmpty()){
						for(KristinaAgenda a2: a.getMandatoryInformation()){
							if(!selectedAgendas.contains(a2)){
								selectedAgendas.add(a2);
							}
						}
						break;
					}
				}
				
				for (KristinaAgenda a : selectedAgendas){
					if(a.getDialogueAction().equals(DialogueAction.MISSING_COMPREHENSION) || a.getDialogueAction().equals(DialogueAction.UNKNOWN_REQUEST) || a.getDialogueAction().equals(DialogueAction.UNKNOWN_STATEMENT) || a.getDialogueAction().equals(DialogueAction.NOT_FOUND)){
						misunderstoodCnt = misunderstoodCnt+1;
					}else{
						misunderstoodCnt = 0;
					}
					tmpVec = verbosityGen.getVector(a, lang);
					if(historyVec != null && tmpVec != null){
						historyVec = historyVec.add(tmpVec).scale(0.5);
					}else{
						historyVec = tmpVec;
					}
				}

				KristinaEmotion sysEmo = getCurrentEmotion();

				updateOffer(userMove, selectedAgendas, user, ontoOfLastAgenda.Name);

				String offer = Core.getVariableString(
						ontoOfLastAgenda.factory.getVariable("var_Offer"),
						ontoOfLastAgenda.beliefSpace[Settings.getuserpos(user)]);
				offer = offer != null ? offer : "";
				
				elaboratenesPool = elaboratenesPool.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()+1));
				elaboratenesPool = elaboratenesPool.entrySet().stream().filter(e-> (e.getValue()< 20)&&(!e.getKey().isDialogueAction(DialogueAction.PROACTIVE_CANNED)&&!e.getKey().isDialogueAction(DialogueAction.PROACTIVE_LIST))).collect(Collectors.toMap(e-> e.getKey(), e -> e.getValue(), (t1,t2) -> Math.min(t1,t2)));
				elaboratenesPool.putAll(selectedAgendas.stream().flatMap(a -> a.getAdditionalInformation().stream()).collect(Collectors.toMap(t -> t, t -> 0, (t1,t2) -> Math.min(t1,t2))));

				Set<String> das = selectedAgendas.stream().map(a -> a.getDialogueAction()).collect(Collectors.toSet());
				List<KristinaAgenda> addAgendas = new LinkedList<KristinaAgenda>();
				
				
				if(offer.isEmpty() && !(das.contains(DialogueAction.EVENING_GOODBYE)||
						das.contains(DialogueAction.MORNING_GOODBYE)||
						das.contains(DialogueAction.PERSONAL_GOODBYE)||
						das.contains(DialogueAction.SIMPLE_GOODBYE)||
						das.contains(DialogueAction.WEEKEND_GOODBYE)||
						das.contains(DialogueAction.MEET_AGAIN)||das.contains(DialogueAction.AFTERNOON_GOODBYE)
						||das.contains(DialogueAction.REQUEST_CLARIFICATION)
						||das.contains(DialogueAction.ASK_TASK))){
					
					log = Logger.getLogger("verbosity");
					log.setUseParentHandlers(false);
					log.addHandler(handler);
					
					double verb = Double.parseDouble(owlEngine.getVariable(ontoOfLastAgenda.Name, "var_CurrentVerbosity", user))/2.5;
					
					if( misunderstoodCnt > 0){
						if( Math.random()< misunderstoodCnt*0.5*verb){
						
							addAgendas = verbosityGen.recoverMisunderstanding(selectedAgendas,ontoOfLastAgenda.workSpace[Settings
														                                              						.getuserpos(user)].getNext().stream()
														                                            						.map(s -> s.asKristinaAgenda())
														                                              						.filter(s -> laterUsageAllowed(s)||s.isDialogueAction(DialogueAction.ASK_FURTHER_TASK))
													                                            						.collect(Collectors.toList()), ontoOfLastAgenda.workSpace[Settings
													  														                                              						.getuserpos(user)].getNext().stream()
													  														                                            						.map(s -> s.asKristinaAgenda())
													  														                                              						.filter(s -> s.isDialogueAction(DialogueAction.STATEMENT))
													  													                                            						.collect(Collectors.toMap(e -> e, e -> e.getAge(), (t1,t2) -> Math.min(t1,t2))), elaboratenesPool.entrySet().stream().filter(e -> laterUsageAllowed(e.getKey())).collect(Collectors.toMap(e -> e.getKey(),e -> e.getValue(), (t1,t2) -> Math.min(t1,t2))),historyVec, lang,recentAgendas, scenario,log);
					}
					}else if(das.contains(DialogueAction.ASK_FURTHER_TASK)){
						if( Math.random()< verb ){
						
							addAgendas = verbosityGen.continueConversation(selectedAgendas,ontoOfLastAgenda.workSpace[Settings
													                                              						.getuserpos(user)].getNext().stream()
													                                            						.map(s -> s.asKristinaAgenda())
													                                              						.filter(s -> laterUsageAllowed(s)||s.isDialogueAction(DialogueAction.ASK_FURTHER_TASK))
												                                            						.collect(Collectors.toList()), ontoOfLastAgenda.workSpace[Settings
													  														                                              						.getuserpos(user)].getNext().stream()
													  														                                            						.map(s -> s.asKristinaAgenda())
													  														                                              						.filter(s -> s.isDialogueAction(DialogueAction.STATEMENT)||s.isDialogueAction(DialogueAction.SHOW_WEBPAGE))
													  													                                            						.collect(Collectors.toMap(e -> e, e -> e.getAge(), (t1,t2) -> Math.min(t1,t2))),elaboratenesPool,historyVec, lang,recentAgendas, scenario,log);
					}
					}else if ( Math.random()< verb){
						
						addAgendas = verbosityGen
								.addAdditionalInformation(selectedAgendas, ontoOfLastAgenda.workSpace[Settings
								                                              						.getuserpos(user)].getNext().stream()
								                                            						.map(s -> s.asKristinaAgenda())
								                                              						.filter(s -> laterUsageAllowed(s)||s.isDialogueAction(DialogueAction.ASK_FURTHER_TASK))
							                                            						.collect(Collectors.toList()), ontoOfLastAgenda.workSpace[Settings
								  														                                              						.getuserpos(user)].getNext().stream()
								  														                                            						.map(s -> s.asKristinaAgenda())
								  														                                              						.filter(s -> s.isDialogueAction(DialogueAction.STATEMENT)||s.isDialogueAction(DialogueAction.SHOW_WEBPAGE))
								  													                                            						.collect(Collectors.toMap(e -> e, e -> e.getAge(), (e1, e2)-> Math.min(e1,e2))),elaboratenesPool,historyVec, lang,recentAgendas, scenario,log);
					}
				}

				List<KristinaAgenda>tmpAgendas = new LinkedList<KristinaAgenda>(addAgendas);
				tmpAgendas.removeAll(selectedAgendas);
				for(KristinaAgenda a: tmpAgendas){
					tmpVec = verbosityGen.getVector(a, lang);
					if(historyVec != null && tmpVec != null){
						historyVec = historyVec.add(tmpVec).scale(0.5);
					}else{
						historyVec = tmpVec;
					}
				}

				for(KristinaAgenda k2: addAgendas){
					if(!selectedAgendas.contains(k2)){
						selectedAgendas.add(k2);
					}
				}
				if(need2Inform){
					try{
					if(selectedAgendas.stream().map(a -> a.getDialogueAction()).filter(s -> s.equals(DialogueAction.REQUEST_CLARIFICATION)||s.equals(DialogueAction.PROACTIVE_CANNED)||s.equals(DialogueAction.PROACTIVE_LIST)).count()>0){
						certhClient.informCERTH(true);
					}else{
						certhClient.informCERTH(false);
					}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
				for(KristinaAgenda a: recentAgendas.keySet()){
					if(a.isDialogueAction(DialogueAction.SHOW_WEBPAGE))
					System.out.println(a.getLocalName()+" "+a);
				}
				recentAgendas = recentAgendas.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()+1, (t1,t2) -> Math.min(t1,t2)));
				for(KristinaAgenda a: recentAgendas.keySet()){
					if(a.isDialogueAction(DialogueAction.SHOW_WEBPAGE))
					System.out.println(a.getLocalName()+" "+a);
				}
				recentAgendas = recentAgendas.entrySet().stream().filter(e-> (e.getValue()< 20)).collect(Collectors.toMap(e-> e.getKey(), e -> e.getValue(), (t1,t2) -> Math.min(t1,t2)));
				for(KristinaAgenda k: selectedAgendas){
					recentAgendas.put(k,0);
					for(KristinaAgenda ka: k.getAdditionalInformation()){
						elaboratenesPool.put(ka,0);
					}
				}
				

				

				updateOffer(userMove, selectedAgendas, user, ontoOfLastAgenda.Name);

				System.out.println("selectedAgendas: ");
				for (KristinaAgenda a : selectedAgendas)
					System.out.println("\t" + a.toString());
				
				// get sysMoves and workspace for demo
				sysMoveDemo = selectedAgendas.toArray(new KristinaAgenda[0]);
				workspaceDemo = ontoOfLastAgenda.workSpace[Settings.getuserpos(user)].getNext().stream()
						.map(s -> s.asKristinaAgenda()).collect(Collectors.toList()).toArray(new KristinaAgenda[0]);

				String ws = "{\n";

				for (KristinaAgenda m : workspaceDemo) {
					if (m != null)
						ws = ws + m + "\n";
				}
				ws = ws + "}\n";

				log = Logger.getLogger("workspace");
				log.setUseParentHandlers(false);
				log.addHandler(handler);
				log.info(ws);
				
				

				

				// Log output Agenda
				log = Logger.getLogger("sysmv");
				log.setUseParentHandlers(false);
				log.addHandler(handler);
				log.info("System: " + selectedAgendas.toString());
				
				if(selectedAgendas == null || selectedAgendas.isEmpty()){
					throw new Exception("No Agendas chosen. This really should not happen.");
				}

				String result = LAConverter.convertFromKristinaAgendas(selectedAgendas, sysEmo.getValence(),
						sysEmo.getArousal(), OntologyPrefix.dialogue,
						Float.parseFloat(owlEngine.getVariable(ontoOfLastAgenda.Name, "var_CurrentVerbosity", user)),
						Float.parseFloat(owlEngine.getVariable(ontoOfLastAgenda.Name, "var_CurrentDirectness", user)),
						owlEngine.getVariable(ontoOfLastAgenda.Name, "var_SystemIsFormal", user).equals("1"),
						owlEngine.getVariable(ontoOfLastAgenda.Name, "var_SystemIsAdvice", user).equals("1"),
						owlEngine.getVariable(ontoOfLastAgenda.Name, "var_SystemIsBelief", user).equals("1"));

				System.out.println("DM done");
				long end = System.currentTimeMillis();

				Logger log2 = Logger.getLogger("DM2VSM");
				log2.setUseParentHandlers(false);
				log2.addHandler(handler);

				Logger log3 = Logger.getLogger("runtime");
				log3.setUseParentHandlers(false);
				log3.addHandler(handler);

				log2.info(result);
				log3.info(Long.toString(end - start));
				if (result == null || result.isEmpty()) {
					throw new Exception("No result. This really should not happen.");
				}
				return result;
			} catch (Exception e) {
				Logger log2 = Logger.getLogger("exception");
				log2.setUseParentHandlers(false);
				log2.addHandler(handler);
				log2.log(Level.SEVERE, "Something went wrong in the DM", e);

				emotionGenerator.processSystemEmotion(new KristinaEmotion(Float.parseFloat(valenceUser), 0.25));
				List<KristinaAgenda> list = new LinkedList<KristinaAgenda>();
				list.add(new KristinaAgenda(
						factory.getOWLNamedIndividual(IRI
								.create(dmOnto.getOntologyID().getOntologyIRI() + "#agenda_StateMissingComprehension")),
						dmOnto, factory, manager));
				try {
					emotionGenerator.processSystemEmotion(
							new KristinaEmotion(emotionGenerator.getCurrentEmotion().getValence(), 0.25));
				} catch (Exception e1) {
					e1.printStackTrace();
					emotionGenerator.processSystemEmotion(new KristinaEmotion(0, 0.25));
				}

				KristinaEmotion emo;
				try {
					emo = emotionGenerator.getCurrentEmotion();

					return LAConverter.convertFromKristinaAgendas(list, emo.getValence(), emo.getArousal(),
							OntologyPrefix.dialogue, 0, 0, false, false, false);
				} catch (Exception e1) {
					e1.printStackTrace();

					return LAConverter.convertFromKristinaAgendas(list, 0, 0, OntologyPrefix.dialogue, 0, 0, false,
							false, false);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Logger log1 = Logger.getLogger("exception");
			log1.setUseParentHandlers(false);
			log1.addHandler(handler);
			log1.log(Level.SEVERE, "Probably wrong Json input", e);
		} finally {
			if (handler != null) {
				handler.close();
			}
		}
		return null;
	}
	
	private static void updateOffer(KristinaMove userMove, List<KristinaAgenda> selectedAgendas, String user, String ontology){
		// update beliefspace
		if (userMove.getRDFType().contains(DialogueAction.ACCEPT) || userMove
				.getRDFType().contains(DialogueAction.REJECT)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_WEATHER)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_CLOSEST_HEALTH_CENTER)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_CLOSEST_PARKS)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_INFO_DEMENTIA)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_INFO_DIABETES)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_INFO_PROTECTION_BABY)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_INFO_SLEEP)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_LOCAL_EVENT)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_NEWSPAPER)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_RECIPE)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_ACTIVITIES_BABY)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_SOCIAL_MEDIA)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_INFO_SLEEP_HYGIENE)
				|| userMove
				.getRDFType().contains(DialogueAction.REQUEST_APPOINTMENT)) {
			owlEngine.setVariable(ontology, "var_Offer",
					"", user);
		}
		if (!userMove.getRDFType().contains(DialogueAction.ACCEPT) && !userMove
				.getRDFType().contains(DialogueAction.REJECT) && owlEngine.getVariable(ontology,
						"var_Offer", user) != null && owlEngine.getVariable(ontology,
						"var_Offer", user).equals("task")){
			owlEngine.setVariable(ontology, "var_Offer",
					"", user);
		}

		for (KristinaAgenda a : selectedAgendas) {
			emotionGenerator.processSystemAction(a);

			if (
					a.getLocalName().equals(
							"agenda_RequestOtherArticle")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "newspaper", user);
			} else if (a.getLocalName().equals(
					"agenda_RequestWishAppointment")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "appointment", user);
			}else if (a.getLocalName().equals(
					"agenda_RequestWishWeather")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "weather", user);
			}else if (a.getLocalName().equals(
					"agenda_RequestWellbeing")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "wellbeing", user);
			}else if (a.getLocalName().equals(
						"agenda_KnowHow")) {
					owlEngine.setVariable(ontology,
							"var_Offer", "appointment2", user);
			}else if (a.getLocalName().equals(
					"agenda_AskTaskFollowUp") || a.getLocalName().equals(
							"agenda_AskTask")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "task", user);
			} else if (a.getLocalName().equals(
					"agenda_RequestArticleLiked")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "preference", user);
			} else if (a.getLocalName().equals(
					"agenda_Helpful")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "helpful", user);
			}else if (a.getLocalName().equals(
					"agenda_RequestHeadlineArticle")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "article", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("event")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "event", user);
			}else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("social media")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "socialMedia", user);
			}else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("newspaper")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "newspaper", user);
			}else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("health centre")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "healthCenter", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("sleep hygiene")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "infoSleepHygiene", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("sleep better")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "infoSleep", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("recipes")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "recipe", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("dementia")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "infoDementia", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("diabetes")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "infoDiabetes", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("park")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "park", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("activities")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "activitiesBaby", user);
			} else if (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("protecting")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "infoProtectionBaby", user);
			} else if (a.isDialogueAction(DialogueAction.REQUEST_CLARIFICATION) && a.getText().contains("pain")) {
				owlEngine.setVariable(ontology,
						"var_Offer", "pain", user);
			} else if (a.isDialogueAction(DialogueAction.REQUEST_CLARIFICATION) || a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) || a.isDialogueAction(DialogueAction.PROACTIVE_LIST)) {
				owlEngine.setVariable(ontology,
						"var_Offer", "ki", user);
			} 
		}
	}
	
	private boolean laterUsageAllowed(KristinaAgenda a){
		return(a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("event"))
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("social media")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("newspaper")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("health centre")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("sleep hygiene")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("sleep better")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("recipes")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("dementia")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("diabetes"))
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("park")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("activities")) 
				|| (a.isDialogueAction(DialogueAction.PROACTIVE_CANNED) && a.getText().contains("protecting"));
	}

	/* Functions needed for the demonstration */
	public static void setUserEmotion(KristinaEmotion emo) {
		emotionGenerator.processUserEmotion(emo);
	}

	public static void setSystemAction(String emo) {
		emotionGenerator.processSystemAction(emo);
	}

	public static void setUserAction(String emo) {
		emotionGenerator.processUserAction(emo);
	}

	public static String[] getWorkspace(String user) {
		LinkedList<String> result = new LinkedList<String>();
		if (workspaceDemo != null) {
			for (KristinaAgenda a : workspaceDemo) {

				result.add(a.toString());
			}
		}
		return result.toArray(new String[0]);
	}

	public static String[] getVerbosity(String user) {
		LinkedList<String> result = new LinkedList<String>();
		if (elaboratenesPool != null) {
			for (KristinaAgenda a : elaboratenesPool.keySet()) {
					
					result.add(a.toString());
			}

		}
		return result.toArray(new String[0]);
	}

	public static String[] getSystemMove(String user) {
		LinkedList<String> result = new LinkedList<String>();
		if (sysMoveDemo != null) {
			for (KristinaAgenda a : sysMoveDemo) {

				result.add(a.toString());
			}
		}
		return result.toArray(new String[0]);
	}
}