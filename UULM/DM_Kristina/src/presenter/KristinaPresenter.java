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

import owlSpeak.kristina.util.InputOutputConverter;
import owlSpeak.servlet.KristinaDocument;

public class KristinaPresenter {

	public static String performDM(String valence, String arousal,
			String content) throws OWLOntologyCreationException,
			OWLOntologyStorageException {

		// determine emotion
		float sysValence = Float.parseFloat(valence);
		float sysArousal = Float.parseFloat(arousal);

		// TODO: tmp solution

		String output="";
		if (!content.isEmpty()) {
			// perform dialogue state update
			OWLOntology proposal = KristinaModel.performUpdate(content);

			StringDocumentTarget t = new StringDocumentTarget();

			// perform agenda selection

			proposal.getOWLOntologyManager().saveOntology(proposal, t);

			String sysmove = t.toString();
			System.out.println(sysmove);

			output = output + "\n" + sysmove;
		} else {

			output = InputOutputConverter.buildEmotionOutput(sysValence,
					sysArousal);

		}

		return output;

	}

}
