package presenter;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import model.KristinaModel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.StringDocumentTarget;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

public class KristinaPresenter {

	public static String performDM(String valence, String arousal,
			String content) throws OWLOntologyCreationException, OWLOntologyStorageException {
		
		// determine emotion
		float sysValence = Float.parseFloat(valence);
		float sysArousal = Float.parseFloat(arousal);

		// TODO: tmp solution

		String output = "VALENCE: " + sysValence + "\nAROUSAL: " + sysArousal;
		if (!content.isEmpty()) {
			// perform dialogue state update
			OWLOntology proposal = KristinaModel.performUpdate(content);
                        
            StringDocumentTarget t = new StringDocumentTarget();

            // perform agenda selection
            proposal.getOWLOntologyManager().saveOntology(proposal, t);
                    
			String sysmove = t.toString();
            System.out.println(sysmove);

			output = output + "\n" + sysmove;
		}

		return output;

	}

	public static String tmpSelection() {
		String file = "<?xml version=\"1.0\"?>\n"
				+ "\n"
				+ "\n"
				+ "<!DOCTYPE rdf:RDF [\n"
				+ "<!ENTITY owl \"http://www.w3.org/2002/07/owl#\" >\n"
				+ "<!ENTITY xsd \"http://www.w3.org/2001/XMLSchema#\" >\n"
				+ "<!ENTITY xml \"http://www.w3.org/XML/1998/namespace\" >\n"
				+ "<!ENTITY rdfs \"http://www.w3.org/2000/01/rdf-schema#\" >\n"
				+ "<!ENTITY dul \"http://www.loa-cnr.it/ontologies/DUL.owl#\" >\n"
				+ "<!ENTITY rdf \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" >\n"
				+ "<!ENTITY ent \"http://kristina-project.eu/ontologies/entities#\" >\n"
				+ "<!ENTITY core \"http://kristina-project.eu/ontologies/framesituation/core#\" >\n"
				+ "<!ENTITY expl \"http://kristina-project.eu/ontologies/framesituation/example#\" >\n"
				+ "]>\n"
				+ "\n"
				+ "\n"
				+ "<rdf:RDF xmlns=\"http://kristina-project.eu/ontologies/framesituation/example#\"\n"
				+ "xml:base=\"http://kristina-project.eu/ontologies/framesituation/example\"\n"
				+ "xmlns:expl=\"http://kristina-project.eu/ontologies/framesituation/example#\"\n"
				+ "xmlns:core=\"http://kristina-project.eu/ontologies/framesituation/core#\"\n"
				+ "xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
				+ "xmlns:owl=\"http://www.w3.org/2002/07/owl#\"\n"
				+ "xmlns:xml=\"http://www.w3.org/XML/1998/namespace\"\n"
				+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\"\n"
				+ "xmlns:dul=\"http://www.loa-cnr.it/ontologies/DUL.owl#\"\n"
				+ "xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"\n"
				+ "xmlns:ent=\"http://kristina-project.eu/ontologies/entities#\">\n"
				+ "<owl:Ontology rdf:about=\"http://kristina-project.eu/ontologies/framesituation/example\">\n"
				+ "<owl:imports rdf:resource=\"http://kristina-project.eu/ontologies/framesituation/core\"/>\n"
				+ "</owl:Ontology>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- \n"
				+ "///////////////////////////////////////////////////////////////////////////////////////\n"
				+ "//\n"
				+ "// Individuals\n"
				+ "//\n"
				+ "///////////////////////////////////////////////////////////////////////////////////////\n"
				+ "-->\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#Age.Age_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;Age.Age_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&core;Age.age\"/>\n"
				+ "<dul:classifies rdf:resource=\"&expl;Age.Age_1Req\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#Age.Entity_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;Age.Entity_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&core;Entity.age\"/>\n"
				+ "<dul:classifies rdf:resource=\"&expl;NaturalPerson_1Req\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#AgeDescription_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;AgeDescription_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&core;AgeDescription\"/>\n"
				+ "<dul:defines rdf:resource=\"&expl;Age.Age_1Req\"/>\n"
				+ "<dul:defines rdf:resource=\"&expl;Age.Entity_1Req\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#AgeFrame_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;AgeFrame_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&core;AgeFrame\"/>\n"
				+ "<dul:satisfies rdf:resource=\"&expl;AgeDescription_1Req\"/>\n"
				+ "<dul:isSettingFor rdf:resource=\"&expl;Age_1Req\"/>\n"
				+ "<dul:isSettingFor rdf:resource=\"&expl;NaturalPerson_1Req\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#Age_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;Age_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&ent;Age\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#NaturalPerson_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;NaturalPerson_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&dul;NaturalPerson\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#RequestAge_1 -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;RequestAge_1\">\n"
				+ "<rdf:type rdf:resource=\"&core;RequestSpeechAct\"/>\n"
				+ "<dul:isSettingFor rdf:resource=\"&expl;AgeFrame_1Req\"/>\n"
				+ "<dul:satisfies rdf:resource=\"&expl;RequestDescription_1\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#RequestDescription_1 -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;RequestDescription_1\">\n"
				+ "<rdf:type rdf:resource=\"&core;RequestDescription\"/>\n"
				+ "<dul:defines rdf:resource=\"&expl;RequestType_1\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#RequestType_1 -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;RequestType_1\">\n"
				+ "<rdf:type rdf:resource=\"&core;RequestType\"/>\n"
				+ "<dul:classifies rdf:resource=\"&expl;Age.Age_1Req\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- http://kristina-project.eu/ontologies/framesituation/example#Water_1Req -->\n"
				+ "\n"
				+ "<owl:NamedIndividual rdf:about=\"&expl;Water_1Req\">\n"
				+ "<rdf:type rdf:resource=\"&ent;Water\"/>\n"
				+ "</owl:NamedIndividual>\n"
				+ "</rdf:RDF>\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "<!-- Generated by the OWL API (version 3.5.1) http://owlapi.sourceforge.net -->\n"
				+ "	\n" + "	\n";

		return file;
	}

}
