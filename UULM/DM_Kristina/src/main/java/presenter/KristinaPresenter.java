package presenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.coode.owlapi.owlxmlparser.OWLIndividualElementHandler;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import model.KristinaModel;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import de.affect.mood.Mood;
import owlSpeak.Agenda;
import owlSpeak.Move;
import owlSpeak.WorkSpace;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.emotion.EmotionGenerator;
import owlSpeak.kristina.emotion.KristinaEmotion;
import owlSpeak.kristina.util.InputOutputConverter;
import owlSpeak.servlet.KristinaDocument;
import owlSpeak.servlet.OwlSpeakServlet;

public class KristinaPresenter {
	
	private static ServletEngine owlEngine; 
	private static InputOutputConverter ioConverter;
	private static EmotionGenerator emotionGenerator;
	
	// ALMA configuration files
		private final static String sALMACOMP = "/EmotionGeneration/AffectComputation.aml";
		private final static String sALMADEF = "/EmotionGeneration/CharacterDefinition.aml";
	
	//only needed for demo
	private static KristinaMove systemMove;
	
	public static void init() throws URISyntaxException, OWLOntologyCreationException{
		emotionGenerator = new EmotionGenerator(KristinaPresenter.class.getResourceAsStream(sALMACOMP), KristinaPresenter.class.getResourceAsStream(sALMADEF));
		
		System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
		owlEngine = new ServletEngine();
		OwlSpeakServlet.reset(owlEngine, "", "user");
		
		
		ioConverter = new InputOutputConverter(owlEngine.systemCore.ontologies.get(0).factory.onto, owlEngine.systemCore.ontologies.get(0).factory.factory, owlEngine.systemCore.ontologies.get(0).factory.manager);
		
	}

	public static String performDM(float valence, float arousal,
			String content) throws OWLOntologyCreationException,
			OWLOntologyStorageException {
		
		System.out.println("Input LA\n"+content);

		// determine emotion
		float sysValence = valence;
		float sysArousal = arousal;

		// TODO: tmp solution

		String output="";
		if (!content.isEmpty()) {
			// perform dialogue state update
			OWLOntology proposal = KristinaModel.performUpdate(content);

			StringDocumentTarget t = new StringDocumentTarget();

			// perform agenda selection

			proposal.getOWLOntologyManager().saveOntology(proposal, t);

			String sysmove = t.toString();
			System.out.println("Input KI\n"+sysmove);

			output = output + "\n" + sysmove;
		} else {

			output = ioConverter.buildEmotionOutput(sysValence,
					sysArousal);

		}
		
		createEvent("Sending System Move");

		return output;

	}
	
	/* Functions needed for the demonstration */
	
	public static KristinaEmotion getCurrentEmotion(){
		return emotionGenerator.getCurrentEmotion();
	}
	
	public static int getGenerationStatus(){
		if(emotionGenerator.isActive()){
			return 0;
		}else{
			return 1;
		}
	}
	
	public static int getManagerStatus(){
		if(owlEngine != null){
			return 0;
		}else{
			return 1;
		}
	}
	
	public static String[] getWorkspace(String user){
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies.get(0);
		Collection<Agenda> ws = ontoOfLastAgenda.getWorks(Settings
						.getuserpos(user)).getNext();
		String[] result = new String[ws.size()];
		int i = 0;
		for(Agenda a: ws){
			result[i++] = a.getHas().iterator().next().toString();
		}
		return result;
		
	}
	
	
	public static String getSystemMove(String user){
		//owlEngine.systemCore.actualOnto[Settings.getuserpos(user)]
		
		return (systemMove!=null)?systemMove.toString():null;
	}
	
	public static void setUserEmotion(KristinaEmotion emo){
		emotionGenerator.processUserEmotion(emo);
	}
	
	public static void performDemoDM(String input, String user) throws OWLOntologyCreationException{
		systemMove = null;
		
		Set<Move> userMoves = ioConverter.buildKristinaMove(input);
		
		assert userMoves.size()==1: "More than one user move detected.";
		
		WorkSpace ws = new KristinaModel().performDemoUpdate(userMoves.iterator().next(), user, ioConverter, owlEngine);
		
		systemMove = selectSystemMove(ws);
	}
	
	private static KristinaMove selectSystemMove(WorkSpace ws){
		Collection<Agenda> agendas = ws.getNext();
		KristinaEmotion sysEmo = getCurrentEmotion();
		if(sysEmo.isExtremeEmotion()){
			String chosen = "";
			if(sysEmo.getValence()>0){
				chosen = "agenda_Express_Joy";
			}else if (sysEmo.getArousal() < -0.5){
				chosen = "agenda_Express_Commiseration";
			}else if (sysEmo.getArousal() < 0.5){
				chosen = "agenda_Cheer_Up";
			}else{
				chosen = "agenda_Calm_Down";
			}
			
			for(Agenda agenda: agendas){
				if(agenda.getLocalName().equals(chosen)){
					return agenda.getHas().iterator().next().asKristinaMove();
				}
			}
			
		}else{
			Collection<Agenda> realMoves = new HashSet<Agenda>();
			for(Agenda agenda: agendas){
				OWLAxiom decl = ws.factory.getOWLDeclarationAxiom((OWLNamedIndividual)agenda.indi);
				if(!ws.onto.containsAxiom(decl)){
					realMoves.add(agenda);
				}
			}
			
			int strategy = (int)(Math.random()*realMoves.size());
			for(Agenda agenda: realMoves){
				if(strategy == 0){
					return agenda.getHas().iterator().next().asKristinaMove();
				}
				strategy = strategy-1;
			}
			
		}
		return null;
	}
	
	private static void createEvent(String value){
		String event = "\"<?xml version=\\\"1.0\\\" ?>"
				+"<events ssi-v=\\\"V2\\\">"
				+"<event sender=\\\"DM\\\" event=\\\"STATUS\\\" from=\\\""+System.currentTimeMillis()+"\\\" dur=\\\"0\\\" prob=\\\"1.000000\\\" type=\\\"STRING\\\" state=\\\"COMPLETED\\\" glue=\\\"0\\\">"
				+value
				+"<\\/event>"
				+"<\\/events>\"";
		try{
		      DatagramSocket clientSocket = new DatagramSocket();
		      InetAddress IPAddress = InetAddress.getByName("137.250.171.230");
		      byte[] sendData = event.getBytes();
		      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 1337);
		      clientSocket.send(sendPacket);
		      clientSocket.close(); 
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
