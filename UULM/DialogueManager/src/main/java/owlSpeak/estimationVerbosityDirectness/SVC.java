package owlSpeak.estimationVerbosityDirectness;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import com.google.gson.Gson;

public class SVC {

	private enum Kernel {
		LINEAR, POLY, RBF, SIGMOID
	}

	private class Classifier {
		private int nClasses;
		private int nRows;
		private int[] classes;
		private double[][] vectors;
		private double[][] coefficients;
		private double[] intercepts;
		private int[] weights;
		private String kernel;
		private Kernel kkernel;
		private double gamma;
		private double coef0;
		private double degree;
	}

	private Classifier clf;

	public SVC(String file) throws FileNotFoundException {
		String jsonStr = new Scanner(new File(file)).useDelimiter("\\Z").next();
		this.clf = new Gson().fromJson(jsonStr, Classifier.class);
		this.clf.classes = new int[this.clf.nClasses];
		for (int i = 0; i < this.clf.nClasses; i++) {
			this.clf.classes[i] = i;
		}
		this.clf.kkernel = Kernel.valueOf(this.clf.kernel.toUpperCase());
	}

	public int predict(double[] features) {
		double[] kernels = new double[this.clf.vectors.length];
		double kernel;
		switch (this.clf.kkernel) {
		case LINEAR:
			// <x,x'>
			for (int i = 0; i < this.clf.vectors.length; i++) {
				kernel = 0.;
				for (int j = 0; j < this.clf.vectors[i].length; j++) {
					kernel += this.clf.vectors[i][j] * features[j];
				}
				kernels[i] = kernel;
			}
			break;
		case POLY:
			// (y<x,x'>+r)^d
			for (int i = 0; i < this.clf.vectors.length; i++) {
				kernel = 0.;
				for (int j = 0; j < this.clf.vectors[i].length; j++) {
					kernel += this.clf.vectors[i][j] * features[j];
				}
				kernels[i] = Math.pow((this.clf.gamma * kernel) + this.clf.coef0, this.clf.degree);
			}
			break;
		case RBF:
			// exp(-y|x-x'|^2)
			for (int i = 0; i < this.clf.vectors.length; i++) {
				kernel = 0.;
				for (int j = 0; j < this.clf.vectors[i].length; j++) {
					kernel += Math.pow(this.clf.vectors[i][j] - features[j], 2);
				}
				kernels[i] = Math.exp(-this.clf.gamma * kernel);
			}
			break;
		case SIGMOID:
			// tanh(y<x,x'>+r)
			for (int i = 0; i < this.clf.vectors.length; i++) {
				kernel = 0.;
				for (int j = 0; j < this.clf.vectors[i].length; j++) {
					kernel += this.clf.vectors[i][j] * features[j];
				}
				kernels[i] = Math.tanh((this.clf.gamma * kernel) + this.clf.coef0);
			}
			break;
		}

		int[] starts = new int[this.clf.nRows];
		for (int i = 0; i < this.clf.nRows; i++) {
			if (i != 0) {
				int start = 0;
				for (int j = 0; j < i; j++) {
					start += this.clf.weights[j];
				}
				starts[i] = start;
			} else {
				starts[0] = 0;
			}
		}

		int[] ends = new int[this.clf.nRows];
		for (int i = 0; i < this.clf.nRows; i++) {
			ends[i] = this.clf.weights[i] + starts[i];
		}

		if (this.clf.nClasses == 2) {
			for (int i = 0; i < kernels.length; i++) {
				kernels[i] = -kernels[i];
			}
			double decision = 0.;
			for (int k = starts[1]; k < ends[1]; k++) {
				decision += kernels[k] * this.clf.coefficients[0][k];
			}
			for (int k = starts[0]; k < ends[0]; k++) {
				decision += kernels[k] * this.clf.coefficients[0][k];
			}
			decision += this.clf.intercepts[0];
			if (decision > 0) {
				return 0;
			}
			return 1;
		}

		double[] decisions = new double[this.clf.intercepts.length];
		for (int i = 0, d = 0, l = this.clf.nRows; i < l; i++) {
			for (int j = i + 1; j < l; j++) {
				double tmp = 0.;
				for (int k = starts[j]; k < ends[j]; k++) {
					tmp += this.clf.coefficients[i][k] * kernels[k];
				}
				for (int k = starts[i]; k < ends[i]; k++) {
					tmp += this.clf.coefficients[j - 1][k] * kernels[k];
				}
				decisions[d] = tmp + this.clf.intercepts[d];
				d++;
			}
		}

		int[] votes = new int[this.clf.intercepts.length];
		for (int i = 0, d = 0, l = this.clf.nRows; i < l; i++) {
			for (int j = i + 1; j < l; j++) {
				votes[d] = decisions[d] > 0 ? i : j;
				d++;
			}
		}

		int[] amounts = new int[this.clf.nClasses];
		for (int i = 0, l = votes.length; i < l; i++) {
			amounts[votes[i]] += 1;
		}

		int classVal = -1, classIdx = -1;
		for (int i = 0, l = amounts.length; i < l; i++) {
			if (amounts[i] > classVal) {
				classVal = amounts[i];
				classIdx = i;
			}
		}
		return this.clf.classes[classIdx];
	}

	public static double[] oneHotEncode(String diaAct) {
		int diaActNb = -1;
		switch (diaAct) {
		case "Accept":
			diaActNb = 0;
			break;
		case "Acknowledge":
			diaActNb = 1;
			break;
		case "Advise":
			diaActNb = 2;
			break;
		case "AfternoonSayGoodbye":
			diaActNb = 3;
			break;
		case "AnswerThank":
			diaActNb = 4;
			break;
		case "AskMood":
			diaActNb = 5;
			break;
		case "AskPlans":
			diaActNb = 6;
			break;
		case "AskTask":
			diaActNb = 7;
			break;
		case "CheerUp":
			diaActNb = 8;
			break;
		case "Console":
			diaActNb = 9;
			break;
		case "Declare":
			diaActNb = 10;
			break;
		case "Statement": // Statement is handled like Declare
			diaActNb = 10;
			break;
		case "Incomprehensible": // Incomprehensible is handled like Declare
			diaActNb = 10;
			break;
		case "EveningGreet":
			diaActNb = 11;
			break;
		case "EveningSayGoodbye":
			diaActNb = 12;
			break;
		case "ExplicitlyConfirmRecognisedInput":
			diaActNb = 13;
			break;
		case "ImplicitlyConfirmRecognisedInput":
			diaActNb = 14;
			break;
		case "IndividualisticallyOrientedMotivate":
			diaActNb = 15;
			break;
		case "MeetAgainSayGoodbye":
			diaActNb = 16;
			break;
		case "MorningGreet":
			diaActNb = 17;
			break;
		case "MorningSayGoodbye":
			diaActNb = 18;
			break;
		case "Order":
			diaActNb = 19;
			break;
		case "PersonalAnswerThank":
			diaActNb = 20;
			break;
		case "PersonalApologise":
			diaActNb = 21;
			break;
		case "PersonalGreet":
			diaActNb = 22;
			break;
		case "PersonalSayGoodbye":
			diaActNb = 23;
			break;
		case "PersonalThank":
			diaActNb = 24;
			break;
		case "Personalgreet":
			diaActNb = 25;
			break;
		case "ReadNewspaper":
			diaActNb = 26;
			break;
		case "Reject":
			diaActNb = 27;
			break;
		case "RephrasePreviousUtterance":
			diaActNb = 28;
			break;
		case "Request":
			diaActNb = 29;
			break;
		case "AdditionalInformationRequest": // AdditionalInformationRequest is
												// handled like Request
			diaActNb = 29;
			break;
		case "RequestFurtherInformation": // RequestFurtherInformation is
											// handled like Request
			diaActNb = 29;
			break;
		case "BooleanRequest": // BooleanRequest is handled like Request
			diaActNb = 29;
			break;
		case "RequestAdditionalInformation":
			diaActNb = 30;
			break;
		case "RequestMissingInformation":
			diaActNb = 31;
			break;
		case "RequestNewspaper":
			diaActNb = 32;
			break;
		case "RequestReasonForEmotion":
			diaActNb = 33;
			break;
		case "RequestRephrase":
			diaActNb = 34;
			break;
		case "RequestWeather":
			diaActNb = 35;
			break;
		case "ShareJoy":
			diaActNb = 36;
			break;
		case "ShowWeather":
			diaActNb = 37;
			break;
		case "SimpleApologise":
			diaActNb = 38;
			break;
		case "SimpleGreet":
			diaActNb = 39;
			break;
		case "Greet": // Greet is handled like SimpleGreet
			diaActNb = 39;
			break;
		case "SimpleMotivate":
			diaActNb = 40;
			break;
		case "SimpleSayGoodbye":
			diaActNb = 41;
			break;
		case "SayGoodbye": // SayGoodbye is handled like SimpleSayGoodbye
			diaActNb = 41;
			break;
		case "SimpleThank":
			diaActNb = 42;
			break;
		case "Thank": // Thank is handled like SimpleThank
			diaActNb = 42;
			break;
		}
		;
		if (diaActNb != -1) {
			double[] onehotencoded = new double[42];
			if (diaActNb != 0) {
				onehotencoded[diaActNb - 1] = 1;
			}
			return onehotencoded;
		} else {
			return null;
		}
	}

	public int scaleAndPredictVerbosity(String dialogueAction, int amountOfWords) {

		double[] oneHotEncoded = oneHotEncode(dialogueAction);
		double[] features = new double[oneHotEncoded.length + 1];

		for (int i = 0; i < oneHotEncoded.length; i++)
			features[i] = oneHotEncoded[i];

		features[oneHotEncoded.length] = (double) amountOfWords;

		// double[] features1 = { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
		// 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 27 };

		// Scaling (sklearn.preprocessing.StandardScaler)
		double[] mean = { 0.1788406550558877, 0.012997140629061606, 2.599428125812321E-4, 0.014036911879386535,
				0.008318170002599427, 7.798284377436964E-4, 0.008318170002599427, 5.198856251624642E-4,
				5.198856251624642E-4, 0.28853652196516766, 7.798284377436964E-4, 0.0010397712503249284,
				0.0070184559396932675, 0.010137769690668054, 5.198856251624642E-4, 0.007798284377436964,
				0.002339485313231089, 0.001819599688068625, 7.798284377436964E-4, 0.002079542500649857,
				2.599428125812321E-4, 0.04756953470236548, 0.0031193137509747857, 0.006758513127112035, 0.0,
				0.009357941252924357, 0.023654795944892122, 2.599428125812321E-4, 0.05718741876787107,
				0.041070964387834676, 0.0720041590850013, 0.008318170002599427, 5.198856251624642E-4, 0.0,
				0.009617884065505589, 0.009097998440343124, 0.013776969066805303, 7.798284377436964E-4,
				0.0025994281258123215, 0.001819599688068625, 0.029113595009098, 0.036911879386534965,
				10.516506368598908 };
		double[] var = { 0.14685667515506584, 0.012828214964530434, 2.598752423154252E-4, 0.013839876984276364,
				0.008248978050407661, 7.792203053513613E-4, 0.008248978050407666, 5.196153440992621E-4,
				5.196153440992585E-4, 0.2052831974574039, 7.792203053514916E-4, 0.0010386901260718473,
				0.006969197215915699, 0.010034995316366794, 5.196153440992067E-4, 0.007737471138205151,
				0.0023340121217002353, 0.0018162887450436744, 7.792203053514414E-4, 0.0020752180036378762,
				2.598752423154304E-4, 0.04530667407056473, 0.00310958363269796, 0.006712835627422625, 1.0E-100,
				0.009270370188430408, 0.023095246573697045, 2.5987524231543244E-4, 0.053917017902531915,
				0.039384140272088246, 0.06681956015945888, 0.008248978050407642, 5.196153440991682E-4, 1.0E-100,
				0.009525380371607849, 0.009015224864722539, 0.013587164190138468, 7.792203053513733E-4,
				0.002592671099230868, 0.0018162887450436456, 0.028265993594743775, 0.03554939254669085,
				265.43714630766834 };

		double[] features_scaled = new double[features.length];
		for (int i = 0; i < features.length; i++)
			features_scaled[i] = (features[i] - mean[i]) / Math.sqrt(var[i]);

		// System.out.println("features_scaled: ");
		// for (int i = 0; i < features_scaled.length; i++)
		// System.out.println(features_scaled[i]);

		// Prediction
		int prediction = predict(features_scaled);
		return prediction;
	}

	public int scaleAndPredictDirectness(String dialogueAction, float verbosity, int amountOfWords) {

		double[] oneHotEncoded = oneHotEncode(dialogueAction);
		double[] features = new double[oneHotEncoded.length + 2];

		for (int i = 0; i < oneHotEncoded.length; i++)
			features[i] = oneHotEncoded[i];

		features[oneHotEncoded.length] = (double) verbosity;
		features[oneHotEncoded.length + 1] = (double) amountOfWords;

		// Scaling (sklearn.preprocessing.StandardScaler)
		double[] mean = { 0.1788406550558877, 0.012997140629061606, 2.599428125812321E-4, 0.014036911879386535,
				0.008318170002599427, 7.798284377436964E-4, 0.008318170002599427, 5.198856251624642E-4,
				5.198856251624642E-4, 0.28853652196516766, 7.798284377436964E-4, 0.0010397712503249284,
				0.0070184559396932675, 0.010137769690668054, 5.198856251624642E-4, 0.007798284377436964,
				0.002339485313231089, 0.001819599688068625, 7.798284377436964E-4, 0.002079542500649857,
				2.599428125812321E-4, 0.04756953470236548, 0.0031193137509747857, 0.006758513127112035, 0.0,
				0.009357941252924357, 0.023654795944892122, 2.599428125812321E-4, 0.05718741876787107,
				0.041070964387834676, 0.0720041590850013, 0.008318170002599427, 5.198856251624642E-4, 0.0,
				0.009617884065505589, 0.009097998440343124, 0.013776969066805303, 7.798284377436964E-4,
				0.0025994281258123215, 0.001819599688068625, 0.029113595009098, 0.036911879386534965, 0.863270080582272,
				10.516506368598908 };
		double[] var = { 0.14685667515506584, 0.012828214964530434, 2.598752423154252E-4, 0.013839876984276364,
				0.008248978050407661, 7.792203053513613E-4, 0.008248978050407666, 5.196153440992621E-4,
				5.196153440992585E-4, 0.2052831974574039, 7.792203053514916E-4, 0.0010386901260718473,
				0.006969197215915699, 0.010034995316366794, 5.196153440992067E-4, 0.007737471138205151,
				0.0023340121217002353, 0.0018162887450436744, 7.792203053514414E-4, 0.0020752180036378762,
				2.598752423154304E-4, 0.04530667407056473, 0.00310958363269796, 0.006712835627422625, 1.0E-100,
				0.009270370188430408, 0.023095246573697045, 2.5987524231543244E-4, 0.053917017902531915,
				0.039384140272088246, 0.06681956015945888, 0.008248978050407642, 5.196153440991682E-4, 1.0E-100,
				0.009525380371607849, 0.009015224864722539, 0.013587164190138468, 7.792203053513733E-4,
				0.002592671099230868, 0.0018162887450436456, 0.028265993594743775, 0.03554939254669085,
				0.5838523686993242, 265.43714630766834 };

		double[] features_scaled = new double[features.length];
		for (int i = 0; i < features.length; i++)
			features_scaled[i] = (features[i] - mean[i]) / Math.sqrt(var[i]);

		// System.out.println("features_scaled: ");
		// for (int i = 0; i < features_scaled.length; i++)
		// System.out.println(features_scaled[i]);

		// Prediction
		int prediction = predict(features_scaled);
		return prediction;
	}

	public static void main(final String args[]) throws FileNotFoundException {
		// just for testing
		SVC clf_verbosity = new SVC("./conf/estimationVerbosityDirectness/data_verbosity.json");
		SVC clf_directness = new SVC("./conf/estimationVerbosityDirectness/data_directness.json");

		int v1 = clf_verbosity.scaleAndPredictVerbosity("Accept", 1);
		int v2 = clf_verbosity.scaleAndPredictVerbosity("Request", 5);
		int v3 = clf_verbosity.scaleAndPredictVerbosity("Thank", 1);
		int v4 = clf_verbosity.scaleAndPredictVerbosity("Declare", 200);

		int d1 = clf_directness.scaleAndPredictDirectness("Accept", v1, 1);
		int d2 = clf_directness.scaleAndPredictDirectness("Request", v2, 5);
		int d3 = clf_directness.scaleAndPredictDirectness("Thank", v3, 1);
		int d4 = clf_directness.scaleAndPredictDirectness("Declare", v4, 200);

		System.out.println("v1: " + v1);
		System.out.println("v2: " + v2);
		System.out.println("v3: " + v3);
		System.out.println("v4: " + v4);

		System.out.println("d1: " + d1);
		System.out.println("d2: " + d2);
		System.out.println("d3: " + d3);
		System.out.println("d4: " + d4);
	}
}