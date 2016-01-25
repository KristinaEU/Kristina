package owlSpeak.quality;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;

import org.libsvm.svm;
import org.libsvm.svm_model;
import org.libsvm.svm_node;
import org.libsvm.svm_parameter;

import owlSpeak.engine.Settings;
import owlSpeak.servlet.OwlSpeakServlet;

public class SVMClassifier {

	
	private ScaleParameters parameters;

	private svm_model model;
	private boolean getProbability = true;
	private String modelFilename = "iq_5_1_reduced.model";

	private String scaleFilename = "iq_5_1_reduced" +
			".scale";

	public SVMClassifier() {

		String modelsDir = Settings.homePath + "models/";
		File scaleFile = new File(modelsDir + "/" + scaleFilename);
		try {
			parameters = new ScaleParameters(scaleFile);
		} catch (FileNotFoundException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "libsvm scale parameter file could not be found. " + e.getMessage());
		} catch (IOException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "Problems with reading the scale parameters occurred. "	+ e.getMessage());
		}

		File modelFile = new File(modelsDir + "/" + modelFilename);
		try {
			model = svm.svm_load_model(modelFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "libsvm model file could not be found.", e);
		} catch (IOException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "Problems with reading the libsvm model occured.",e);
		}
	}
	
	public SVMClassifier(String modelFilename, String scaleFilename) {

		this.modelFilename = modelFilename;
		this.scaleFilename = scaleFilename;
		String modelsDir = Settings.homePath + "models/";
		File scaleFile = new File(modelsDir + "/" + scaleFilename);
		try {
			parameters = new ScaleParameters(scaleFile);
		} catch (FileNotFoundException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "libsvm scale parameter file could not be found. " + e.getMessage());
		} catch (IOException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "Problems with reading the scale parameters occurred. "	+ e.getMessage());
		}

		File modelFile = new File(modelsDir + "/" + modelFilename);
		try {
			model = svm.svm_load_model(modelFile.getAbsolutePath());
		} catch (FileNotFoundException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "libsvm model file could not be found.", e);
		} catch (IOException e) {
			OwlSpeakServlet.logger.logp(Level.SEVERE, "SVM Classification ", "SVMClassifier()" , "Problems with reading the libsvm model occured.",e);
		}
	}
	

	SVMResult evaluate(QualityParameter data) throws Exception {

		double[] featureVector = data.getFeatureVector();
		
		featureVector = parameters.scale(featureVector);

		/*
		 * copied & pasted code from libsvm/svm_predict.java
		 */

		if (getProbability) {
			if (svm.svm_check_probability_model(model) == 0) {
				OwlSpeakServlet.logger.logp(Level.WARNING, "SVM Classification", "evaluate(QualityParameter)" , "Model does not support probabiliy estimates");
				getProbability = false;
			}
		}

		int svm_type = svm.svm_get_svm_type(model);
		int nr_class = svm.svm_get_nr_class(model);
		double[] prob_estimates = null;

		if (getProbability) {
			if (svm_type != svm_parameter.EPSILON_SVR
					&& svm_type != svm_parameter.NU_SVR) {
				prob_estimates = new double[nr_class];
			}
		}

		int m = parameters.getNumFeatures();
		int numCollectedFeatures = featureVector.length;
		svm_node[] x = new svm_node[m];
		int featureNo = 0;
		for (int j = 0; j < numCollectedFeatures; j++) {
			if (parameters.getMax_index() > j
					&& parameters.getIndexHasToBeScaled()[j + 1]) {
				x[featureNo] = new svm_node();
				x[featureNo].index = j + 1;
				x[featureNo].value = featureVector[j];
				featureNo++;
			}
		}
		
		SVMResult result = null;
		if (getProbability
				&& (svm_type == svm_parameter.C_SVC || svm_type == svm_parameter.NU_SVC)) {
			double v = svm.svm_predict_probability(model, x, prob_estimates);
			String message = "The estimated class is " + v + " (";
			int i = 0;
			for (double p : prob_estimates) {
				message = message + " class " + i++ + ":" + p;
			}
			result = new SVMResult(v,prob_estimates);
			//OwlSpeakServlet.logger.logp(Level.INFO, "SVM Classification", "evaluate(QualityParameter)" , message + ")");
		} else {
			double v = svm.svm_predict(model, x);
			result = new SVMResult(v);
			//OwlSpeakServlet.logger.logp(Level.INFO, "SVM Classification", "evaluate(QualityParameter)" , "Estimated class is " + v);
		}

		
		return result;
	}
	
	public class SVMResult {
		private double c;
		private double[] probabilities = null;
		
		public SVMResult(double c) {
			this.c = c;
		}
		
		public SVMResult(double c, double[] p) {
			this.c = c;
			this.probabilities = p;
		}
		
		public double getC() {
			return c;
		}
		public void setC(double c) {
			this.c = c;
		}
		public double[] getProbabilities() {
			return probabilities;
		}
		public void setProbabilities(double[] probabilities) {
			this.probabilities = probabilities;
		}
	}
}
