package model;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.ws.rs.ProcessingException;

import model.DialogueHistory.Participant;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ReifiedStatement;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.vocabulary.RDF;
import org.jdom.JDOMException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.WorkSpace;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.emotion.EmotionGenerator;
import owlSpeak.kristina.emotion.KristinaEmotion;
import presenter.KristinaPresenter;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;

public class KristinaModel {

	private static EmotionGenerator emotionGenerator;
	private static CerthClient certhClient;
	// ALMA configuration files
	private final static String sALMACOMP = "/EmotionGeneration/AffectComputation.aml";
	private final static String sALMADEF = "/EmotionGeneration/CharacterDefinition.aml";
	//CERTH confirguration file
	private final static String sCERTHCONF = "./conf/CERTH/config.xml";
	

	

	public enum Scenario {PAIN,SLEEP,BABY,WEATHER,NEWSPAPER,UNDEFINED};
	private static boolean sent = false;
	
	public static void init() throws Exception{
		emotionGenerator = new EmotionGenerator(
				KristinaPresenter.class.getResourceAsStream(sALMACOMP),
				KristinaPresenter.class.getResourceAsStream(sALMADEF));
		UserModel.init();
		try {
			certhClient = new CerthClient(sCERTHCONF);
		} catch (IOException | JDOMException e) {
			System.err.println("Initialisation gone wrong. Something about the CERTH config file.");
			throw new Exception(e);
		}
	}

	// _________________________________WARNING: extremly ugly
	// implementation for the first prototype ____________________________
	public static List<Set<KristinaMove>> performUpdate(
			List<Resource> userMoves, String user, Scenario scenario, double valence,
			double arousal, ServletEngine owlEngine, Handler handler)
			throws Exception {
		
		
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
		OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
		OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;

		LinkedList<Set<KristinaMove>> workspace = new LinkedList<Set<KristinaMove>>();
		LinkedList<KristinaMove> usrmv = new LinkedList<KristinaMove>();

		sent = false;
		for (Resource r : userMoves) {
			// create OWL representation of user move and add it to dialogue
			// history

			OWLNamedIndividualImpl indi = new OWLNamedIndividualImpl(
					IRI.create(r.getURI()));
			KristinaMove userMove = new KristinaMove(indi, dmOnto, factory,
					manager);
			manager.addAxiom(dmOnto, factory.getOWLDeclarationAxiom(indi));
			manager.addAxiom(dmOnto, factory.getOWLClassAssertionAxiom(factory.getOWLClass(IRI.create(userMove.getRDFType())), indi));

			DialogueHistory.add(userMove, Participant.USER);
			usrmv.add(userMove);
			
		}

		float confidence = usrmv.getFirst().getConfidence();

		if (confidence > 0.3) {
			// Add emotional moves
			if (Math.sqrt(valence * valence + arousal * arousal) > 0.5) {
				Set<KristinaMove> systemMoves = new HashSet<KristinaMove>();

				if (valence > 0) {
					//TODO fill in missing reactions after first prototype
					//systemMoves.add(createTypedKristinaMove("AdressEmotion", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("ShareJoy", dmOnto, manager, factory));
					//systemMoves.add(createTypedKristinaMove("RequestReasonForEmotion", dmOnto, manager, factory));
				} else if (arousal < 0) {
					//TODO fill in missing reactions after first prototype
					/*systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
					systemMoves.add(null);

					workspace.add(systemMoves);
					
					systemMoves = new HashSet<KristinaMove>();*/
					
					if(scenario!=Scenario.WEATHER){
					//systemMoves.add(createTypedKristinaMove("AdressEmotion", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("CheerUp", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("Console", dmOnto, manager, factory));
					}else{
						systemMoves.add(createCannedTextMove("Why are you sad?", user, dmOnto, manager, factory));
					}
				} else if (arousal > 0) {
					//TODO fill in missing reactions after first prototype
					/*systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
					systemMoves.add(null);

					workspace.add(systemMoves);
					
					systemMoves = new HashSet<KristinaMove>();*/
					//systemMoves.add(createTypedKristinaMove("AdressEmotion", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("CalmDown", dmOnto, manager, factory));
					//systemMoves.add(createTypedKristinaMove("RequestReasonForEmotion", dmOnto, manager, factory));
				}

				workspace.add(systemMoves);
			}

			Logger log = Logger.getLogger("usrmv");
			log.setUseParentHandlers(false);
			log.addHandler(handler);
			log.info(user+": "+usrmv.toString());
			
			for (KristinaMove userMove : usrmv) {
				
				Set<KristinaMove> systemMoves = new HashSet<KristinaMove>();
				System.out.println("UserMove : "+userMove);
				switch (userMove.getDialogueAction()) {
				case DialogueAction.INCOMPREHENSIBLE:
					setUserAction("Incomprehensible");
					
					systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("Empty", dmOnto, manager, factory));

					workspace.add(systemMoves);
					
					systemMoves = new HashSet<KristinaMove>();
					systemMoves.add(createTypedKristinaMove("RequestRepeat", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("RequestRephrase", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("StateMissingComprehension", dmOnto, manager, factory));
					break;
				case DialogueAction.GREETING:
					setUserAction("Greet");

					//TODO complete
					systemMoves.add(createTypedKristinaMove("SimpleGreet", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("PersonalGreet", dmOnto, manager, factory));
					
					workspace.add(systemMoves);
					systemMoves = new HashSet<KristinaMove>();
					switch(scenario){
					case NEWSPAPER:
						systemMoves.add(createTypedKristinaMove("AskMood", dmOnto, manager, factory));
						break;
					case SLEEP:
						systemMoves.add(createTypedKristinaMove("AskMood", dmOnto, manager, factory));
						systemMoves.add(null);
					case BABY:
					case PAIN:
						systemMoves.add(createTypedKristinaMove("AskTask", dmOnto, manager, factory));
						break;
					}
					//only used if flexible, not for first prototype
					//systemMoves.add(null);
					
					break;
				case DialogueAction.SAY_GOODBYE:
					setUserAction("Goodbye");
					//TODO complete
					systemMoves.add(createTypedKristinaMove("MeetAgainSayGoodbye", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("PersonalSayGoodbye", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("SimpleSayGoodbye", dmOnto, manager, factory));
					
					break;
				case DialogueAction.THANK:

					setUserAction("Thank");
					//TODO complete
					systemMoves.add(createTypedKristinaMove("AnswerThank", dmOnto, manager, factory));
					//systemMoves.add(createTypedKristinaMove("Empty", dmOnto, manager, factory));
					
					break;
				case DialogueAction.ACCEPT:
					KristinaMove lastMove = (KristinaMove) DialogueHistory.getLastSystemMove();
					if(lastMove!= null&&lastMove.getText().contains("weather")){
						userMove.specify("RequestWeather");
						systemMoves = askKI(userMove, valence, arousal, user,getScenarioString(scenario), dmOnto, manager, factory, handler);
					}
					else if(lastMove!= null&&lastMove.getText().contains("newspaper")){
						systemMoves.add(createCannedTextMove("Tell me the headline of the article.", user, dmOnto, manager, factory));
					}else if(lastMove!= null&&lastMove.getText().contains("like")){
						systemMoves.add(createCannedTextMove("Is there another article you would like me to read out loud?", user, dmOnto, manager, factory));
						
					}
					break;
				case DialogueAction.REJECT:
					KristinaMove tmp2 = (KristinaMove) DialogueHistory.getLastSystemMove();
					if(tmp2!= null&&tmp2.getText().contains("Did you like the article?")){
					systemMoves.add(createTypedKristinaMove("AskTaskFollowUp", dmOnto, manager, factory));
					
					}else{
					systemMoves.add(createTypedKristinaMove("MeetAgainSayGoodbye", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("PersonalSayGoodbye", dmOnto, manager, factory));
					systemMoves.add(createTypedKristinaMove("SimpleSayGoodbye", dmOnto, manager, factory));
					}
					break;
				case DialogueAction.FURTHER_INFORMATION:
						Set<KristinaMove> lastWS = askKI(DialogueHistory.getPreviousUserMove(), valence, arousal, user, getScenarioString(scenario),dmOnto, manager, factory, handler);
						
						List<KristinaMove> lastMoves = DialogueHistory.getLastSystemMoves();
						
						for(KristinaMove m: lastWS){
							if(!lastMoves.contains(m)){
								systemMoves.add(m);
							}
							if(m.isDialogueAction(DialogueAction.UNKNOWN)){
								systemMoves = new HashSet<KristinaMove>();
								systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
								systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
								systemMoves.add(null);
								workspace.add(systemMoves);
								
								systemMoves = new HashSet<KristinaMove>();
								systemMoves.add(createTypedKristinaMove("UnknownRequest", dmOnto, manager, factory));
							}
						}
						if(systemMoves.isEmpty()){
							systemMoves.add(createTypedKristinaMove("Reject", dmOnto, manager, factory));
						}
					break;
				case DialogueAction.STATEMENT:
				case DialogueAction.DECLARE:

					if(userMove.hasTopic("Out")&&userMove.hasTopic("Go")){
						
						systemMoves.add(createCannedTextMove("You can go for a walk.", user, dmOnto, manager, factory));
					}else if(userMove.hasTopic("Walk")&&userMove.hasTopic("Today")){
						systemMoves.add(createTypedKristinaMove("SimpleMotivate", dmOnto, manager, factory));
						workspace.add(systemMoves);
						
						systemMoves = new HashSet<KristinaMove>();
						systemMoves.add(createCannedTextMove("Would you like me to give you the weather today?", user, dmOnto, manager, factory));
						
					}else if(userMove.hasTopic("Sad")||userMove.hasNegatedTopic("Good")){

						setUserAction("StatementSad");
						
						systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
						systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
						if(userMove==usrmv.getLast()){
							workspace.add(systemMoves);
							
							systemMoves = new HashSet<KristinaMove>();
							systemMoves.add(createTypedKristinaMove("AskTask", dmOnto, manager, factory));
						}
					
					}else if(userMove.hasTopic("Fine")){
						setUserAction("StatementFine");
						
						systemMoves.add(createTypedKristinaMove("ShareJoy", dmOnto, manager, factory));
						if(userMove==usrmv.getLast()){
							workspace.add(systemMoves);
							
							systemMoves = new HashSet<KristinaMove>();
							systemMoves.add(createTypedKristinaMove("AskTask", dmOnto, manager, factory));
						}
					
					}else if(userMove.hasTopic("Boring")){
						systemMoves.add(createTypedKristinaMove("AskTaskFollowUp", dmOnto, manager, factory));
					}else if(userMove.hasTopic("Newspaper") && userMove.hasTopic("User")){
						systemMoves.add(createCannedTextMove("Would you like me to read the newspaper for you?", user, dmOnto, manager, factory));
					}else if(userMove.hasTopic("Take")&&userMove.getTopics().size() <= 2){
						//This is part of an acknowledgement, nothing needs to be done.
					}
					else{
						systemMoves = askKI(userMove, valence, arousal, user, getScenarioString(scenario), dmOnto, manager, factory, handler);
						for(KristinaMove m: systemMoves){
							if(m.isDialogueAction(DialogueAction.UNKNOWN)){

								systemMoves = new HashSet<KristinaMove>();
								systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
								systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
								systemMoves.add(null);
								workspace.add(systemMoves);
									
								systemMoves = new HashSet<KristinaMove>();
								systemMoves.add(createTypedKristinaMove("UnknownStatement", dmOnto, manager, factory));
							}
						}
					}
					break;

				case DialogueAction.REQUEST:
				case DialogueAction.BOOL_REQUEST:
					
					if(userMove.hasTopic("Weather")){
						userMove.specify("RequestWeather");
						systemMoves = askKI(userMove, valence, arousal, user,getScenarioString(scenario), dmOnto, manager, factory, handler);
					}
					else if(userMove.hasTopic("Article")){
						//TODO: BooleanRequest?
						
						userMove.specify("RequestNewspaper");
						systemMoves = askKI(userMove, valence, arousal, user, getScenarioString(scenario),dmOnto, manager, factory, handler);
						
						boolean notFound = true;
						for(KristinaMove m: systemMoves){
							if(!m.getText().isEmpty()){
								notFound = false;
							}
						}
						if(notFound){
							systemMoves = new HashSet<KristinaMove>();
							systemMoves.add(createCannedTextMove("I can't find that article.", user, dmOnto, manager, factory));
						}else{
							HashSet<KristinaMove> tmpMoves = new HashSet<KristinaMove>();
							tmpMoves.add(createTypedKristinaMove("Accept", dmOnto, manager, factory));
							workspace.add(tmpMoves);
							
							workspace.add(systemMoves);
							
							systemMoves = new HashSet<KristinaMove>();
							systemMoves.add(createCannedTextMove("Did you like the article?", user, dmOnto, manager, factory));
						}
						
					}else if(userMove.hasTopic("Newspaper")){
						//TODO: BoooleanRequest?
						systemMoves.add(createTypedKristinaMove("Accept", dmOnto, manager, factory));
						workspace.add(systemMoves);
						
						systemMoves = new HashSet<KristinaMove>();
						systemMoves.add(createCannedTextMove("Tell me the headline of the article.", user, dmOnto, manager, factory));
					}else{
						systemMoves = askKI(userMove, valence, arousal, user, getScenarioString(scenario),dmOnto, manager, factory, handler);
						for(KristinaMove m: systemMoves){
							if(m.isDialogueAction(DialogueAction.UNKNOWN)){
								systemMoves = new HashSet<KristinaMove>();
								systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
								systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
								systemMoves.add(null);
								workspace.add(systemMoves);
								
								systemMoves = new HashSet<KristinaMove>();
								systemMoves.add(createTypedKristinaMove("UnknownRequest", dmOnto, manager, factory));
							}
						}
						//String rdfMoves = new KristinaModel().askDemoKI(userMove);
						//systemMoves=KIConverter.convertToMove(rdfMoves, user, dmOnto, factory, manager, OntologyPrefix.onto, OntologyPrefix.dialogue);
					}
					
					break;
				case DialogueAction.ACKNOWLEDGE:
					KristinaMove tmp = (KristinaMove) DialogueHistory.getLastSystemMove();
					KristinaMove tmp3 = (KristinaMove) DialogueHistory.getLastUserMove();
					
					if(tmp!=null&&tmp.getText().contains("walk")){
						systemMoves.add(createTypedKristinaMove("SimpleMotivate", dmOnto, manager, factory));
						workspace.add(systemMoves);
						
						systemMoves = new HashSet<KristinaMove>();
						systemMoves.add(createCannedTextMove("Remember to take your umbrella.", user, dmOnto, manager, factory));
					}else if(tmp!=null&&tmp.isDialogueAction(DialogueAction.SHOW_WEATHER) && tmp3!=null&&tmp3.hasTopic("Walk")){
						systemMoves.add(createTypedKristinaMove("Acknowledge", dmOnto, manager, factory));
						workspace.add(systemMoves);
						
						systemMoves = new HashSet<KristinaMove>();
						systemMoves.add(createCannedTextMove("Don't forget your sunglasses and your hat.", user, dmOnto, manager, factory));
						
					}
					break;
				default:
					break;
				}
				if (!systemMoves.isEmpty()) {
					workspace.add(systemMoves);
				}

			}

		} else {

			setUserAction("LowASR");
			
			Set<KristinaMove> systemMoves = new HashSet<KristinaMove>();
			
			systemMoves.add(createTypedKristinaMove("PersonalApologise", dmOnto, manager, factory));
			systemMoves.add(createTypedKristinaMove("SimpleApologise", dmOnto, manager, factory));
			systemMoves.add(createTypedKristinaMove("Empty", dmOnto, manager, factory));

			workspace.add(systemMoves);
			
			systemMoves = new HashSet<KristinaMove>();

			systemMoves.add(createTypedKristinaMove("RequestRepeat", dmOnto, manager, factory));
			systemMoves.add(createTypedKristinaMove("RequestRephrase", dmOnto, manager, factory));
			systemMoves.add(createTypedKristinaMove("StateMissingComprehension", dmOnto, manager, factory));

			workspace.add(systemMoves);
		}
		String ws = "{\n";
		for(Set<KristinaMove> l: workspace){
			ws = ws+"[\n";
			for(KristinaMove m: l){
				if(m != null)
				ws = ws+m+"\n";
				/*for(ReifiedStatement r: m.getStatements()){
					ws = ws + r.getStatement().getSubject() +" "+r.getStatement().getPredicate() +" "+r.getStatement().getObject() +"\n";
				}*/
			}
			ws = ws+"]\n";
		}
		ws  =ws+"}\n";
		System.out.println(ws);
		

		Logger log = Logger.getLogger("workspace");
		log.setUseParentHandlers(false);
		log.addHandler(handler);
		log.info(ws);
		
		return workspace;
	}
	
	private static Set<KristinaMove> askKI(KristinaMove userMove, double valence, double arousal,String user,String scenario, OWLOntology dmOnto, OWLOntologyManager manager, OWLDataFactory factory, Handler handler)throws Exception{
		if(!sent){
			
			sent = true;
		ByteArrayOutputStream input = new ByteArrayOutputStream();
		
		Model model = userMove.getModel();
		List<Statement> stmts = model.listStatements(null, model.getProperty(OntologyPrefix.act+"textualContent"),(String) null).toList();
		for(Statement s: stmts){
			model.remove(s);
			model.add(s.getSubject(), s.getPredicate(), URLEncoder.encode(s.getString(),"utf-8"));
		}
		
		Logger log1 = Logger.getLogger("DM2KI");
		log1.setUseParentHandlers(false);
		log1.addHandler(handler);
		
		userMove.getModel().write(input, "TURTLE");
		log1.info(input.toString("UTF-8"));
		String rdfMoves = certhClient.post(input.toString("UTF-8"),
				valence, arousal, user, scenario);
		// String rdfMoves = askDemoKI(null);
		Logger log2 = Logger.getLogger("KI2DM");
		log2.setUseParentHandlers(false);
		log2.addHandler(handler);
		log2.info(rdfMoves);

		return KIConverter.convertToMove(rdfMoves, user,
				dmOnto, factory, manager, OntologyPrefix.onto, OntologyPrefix.dialogue);
		}
		return new HashSet<KristinaMove>();
	}

	public static boolean hasExtremeEmotion() {
		try {
			return emotionGenerator.getCurrentEmotion().isExtremeEmotion();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public static KristinaEmotion getCurrentEmotion() throws Exception {
		return emotionGenerator.getCurrentEmotion();
	}

	// Demo functions

	public static int getGenerationStatus() {
		if (emotionGenerator.isActive()) {
			return 0;
		} else {
			return 1;
		}
	}

	public static void setUserEmotion(KristinaEmotion emo) {
		emotionGenerator.processUserEmotion(emo);
	}
	
	public static void setSystemEmotion(KristinaEmotion emo) {
		emotionGenerator.processSystemEmotion(emo);
	}
	
	public static void setSystemAction(String emo) {
		emotionGenerator.processSystemAction(emo);
	}
	public static void setUserAction(String emo) {
		emotionGenerator.processUserAction(emo);
	}

	private WorkSpace addToWorkspace(Set<Move> moves, String user,
			ServletEngine owlEngine) {
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		WorkSpace workspace = ontoOfLastAgenda.getWorks(Settings
				.getuserpos(user));

		for (Move move : moves) {
			OWLIndividual newIndi = ontoOfLastAgenda.factory.factory
					.getOWLNamedIndividual(IRI
							.create(ontoOfLastAgenda.factory.onto
									.getOntologyID().getOntologyIRI()
									+ "#"
									+ UUID.randomUUID()));
			Agenda ag = new Agenda(newIndi, ontoOfLastAgenda.factory.onto,
					ontoOfLastAgenda.factory.factory,
					ontoOfLastAgenda.factory.manager);
			ag.addHas(move);

			workspace.addNext(ag);
		}

		return workspace;
	}

	private String askDemoKI(Move userMove) {
		BufferedReader r;

		try {
			r = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(
							"/ExampleData_KI/ki-favouriteBoardGame_Response.ttl")));
			String data = "";
			String tmp = r.readLine();
			while (tmp != null) {
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();
			return data;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void removeWorkspace(ServletEngine owlEngine, String user) {
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		WorkSpace workspace = ontoOfLastAgenda.getWorks(Settings
				.getuserpos(user));
		Collection<Agenda> agendas = workspace.getNext();

		for (Agenda agenda : agendas) {
			Collection<Move> moves = agenda.getHas();
			for (Move move : moves) {
				OWLAxiom decl = ontoOfLastAgenda.factory.factory
						.getOWLDeclarationAxiom((OWLNamedIndividual) move.indi);
				if (!ontoOfLastAgenda.factory.onto.containsAxiom(decl)) {
					workspace.removeNext(agenda);
					break;
				}
			}
		}
	}

	public static void restart(String user) {
		DialogueHistory.restart(user);
		SemanticsOntology.restart(user);
		emotionGenerator = new EmotionGenerator(
				KristinaPresenter.class.getResourceAsStream(sALMACOMP),
				KristinaPresenter.class.getResourceAsStream(sALMADEF));

	}
	
	public static void stop(){
		emotionGenerator.stop();
	}
	
	private static KristinaMove createTypedKristinaMove(String a, OWLOntology dmOnto, OWLOntologyManager manager, OWLDataFactory factory){
		OWLNamedIndividualImpl indi = new OWLNamedIndividualImpl(
				IRI.create("http://kristina-project.eu/tmp#"
						+ UUID.randomUUID().toString()));
		manager.addAxiom(dmOnto,
				factory.getOWLDeclarationAxiom(indi));
		manager.addAxiom(
				dmOnto,
				factory.getOWLClassAssertionAxiom(
						factory.getOWLClass(IRI.create(OntologyPrefix.dialogue
								+ a)), indi));
		return new KristinaMove(indi, dmOnto, factory,
				manager);
	}
	
	public static KristinaMove getEmptyMove(ServletEngine owlEngine){
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
		OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
		OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;
		
		return createTypedKristinaMove("Empty", dmOnto, manager, factory);
	}
	
	private static KristinaMove createCannedTextMove(String t, String user, OWLOntology dmOnto, OWLOntologyManager manager, OWLDataFactory factory){
		KristinaMove tmp = createTypedKristinaMove("Canned", dmOnto, manager, factory);
		Model m = ModelFactory.createDefaultModel();
		Resource r = m.createResource(tmp.indi.asOWLNamedIndividual().getIRI().toString());
		m.add(r, m.getProperty(OntologyPrefix.onto+"text"), t);
		SemanticsOntology.add(r, m, user);
		return tmp;
	}
	
	public static KristinaMove getCannedTextMove(String text, String user, ServletEngine owlEngine){
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
		OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
		OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;
		
		return createCannedTextMove(text, user, dmOnto, manager, factory);
	}
	
	public static KristinaMove getTypedKristinaMove(String a, String user, ServletEngine owlEngine){
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
		OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
		OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;
		
		return createTypedKristinaMove(a, dmOnto, manager, factory);
	}
	
	public static Scenario getScenario(String s){
		switch (s) {
		case "babycare":
			return Scenario.BABY;
		case "sleep":
			return Scenario.SLEEP;
		case "pain":
			return Scenario.PAIN;
		case "weather":
			return Scenario.WEATHER;
		case "newspaper":
			return Scenario.NEWSPAPER;
		default:
			return Scenario.UNDEFINED;
		}
	}
	
	public static String getScenarioString(Scenario s){
		switch (s) {
		case BABY:
			return "babycare";
		case SLEEP:
			return "sleep";
		case PAIN:
			return "pain";
		case WEATHER:
			return "weather";
		case NEWSPAPER:
			return "newspaper";
		default:
			return "undefined";
		}
	}
}
