package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import model.DialogueAction;
import model.DialogueHistory;
import model.KIConverter;
import model.KristinaModel;
import model.OntologyPrefix;
import model.DialogueHistory.Participant;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.kristina.KristinaMove;
import owlSpeak.kristina.emotion.KristinaEmotion;
import owlSpeak.servlet.OwlSpeakServlet;
import presenter.KristinaPresenter;
import presenter.LAConverter;

public class KISimulator {
	public static void main(String[] args) {
		File folder = new File("./src/main/resources/results/KI2DM/");
		for (File entry : folder.listFiles()) {
			if ( entry.getName().startsWith("ki-output_26")) {
		
		BufferedReader r = new BufferedReader(
				new InputStreamReader(KristinaTest.class
						.getResourceAsStream("/results/KI2DM/"+entry.getName())));

		String data = "";
		String tmp;
		try {
			tmp = r.readLine();
		
		while (tmp != null) {
			data = data + tmp + "\n";
			tmp = r.readLine();
		}
		r.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.setProperty("owlSpeak.settings.file",
				"./conf/OwlSpeak/settings.xml");
		ServletEngine owlEngine = new ServletEngine();
		OwlSpeakServlet.reset(owlEngine, "", "user");
		OwlSpeakOntology ontoOfLastAgenda = owlEngine.systemCore.ontologies
				.get(0);
		OWLOntology dmOnto = ontoOfLastAgenda.factory.onto;
		OWLDataFactory factory = ontoOfLastAgenda.factory.factory;
		OWLOntologyManager manager = ontoOfLastAgenda.factory.manager;
		

		LinkedList<Set<KristinaMove>> ws = new LinkedList<Set<KristinaMove>>();
		Set<KristinaMove> systemMoves = KIConverter.convertToMove(data, "Anna",
				dmOnto, factory, manager, OntologyPrefix.onto, OntologyPrefix.dialogue);
		ws.add(systemMoves);
		
		//List<KristinaMove> systemMove = selectSystemMove(ws);

		
		//System.out.println(LAConverter.convertFromMove(systemMove,
		//		0, 0, OntologyPrefix.dialogue));
		Iterator<KristinaMove> it = systemMoves.iterator();
		for(int i = 0; i < systemMoves.size(); i++){
			List<KristinaMove> l = new LinkedList<KristinaMove>();
			l.add(it.next());
			String s = LAConverter.convertFromMove(l,
					0, 0, OntologyPrefix.dialogue);
			List<String> l1 = new LinkedList<String>();
			l1.add(s);
			try {
				String filename = entry.getName().replace("ki", "dm").replace("ttl","rdf");
				String[] arr = filename.split("\\.");
				Files.write(Paths.get("./src/main/resources/results/DM2LG/"+arr[0]+"."+i+"."+arr[1]), l1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			}
		}
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

		return moves;
	}
}
