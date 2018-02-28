package owlSpeak.engine.his;

import java.io.IOException;
import java.util.Collection;

import org.ujmp.core.Matrix;
import org.ujmp.core.MatrixFactory;
import org.ujmp.core.enums.FileFormat;
import org.ujmp.core.enums.ValueType;
import org.ujmp.core.exceptions.MatrixException;

import owlSpeak.SummaryAgenda;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.interfaces.ISummaryBelief;
import owlSpeak.servlet.document.UsersimulatorDocument;
import owlSpeak.servlet.OwlSpeakServlet;

public class GPDialogueOptimizer {

	private static boolean printToConsole = true;
	
	private double ny;
	private double sigma;
	private double gamma;
	
	private double d;
	private double v; // instead of 1/v
	
	private Matrix my;
	private Matrix C;
	private Matrix c;
	private Matrix K;
	private Matrix g;
	
	private SummaryAgenda a;
	private SummaryBeliefNonpersistent b;
	
	private double R;
	private int i;
		
	
	public GPDialogueOptimizer()
	{
		if (printToConsole) System.out.println("############### NEW DIALOGUE OPTIMIZER ###############");
		ny = 0.01;
		sigma = 5;
		gamma = 1;
	}
	
	
	public static double k(SummaryBeliefNonpersistent b1, SummaryAgenda a1, ISummaryBelief b2, SummaryAgenda a2)
	{
		// kernel function for continuous spaces: Polynomial kernel 
//		double p = 1;
//		double sigma_0 = 0;
//		double kcont = Math.pow((b1.getTopBelief() * b2.getTopBelief()) + (b1.getSecondTopBelief() * b2.getSecondTopBelief()) + Math.pow(sigma_0, 2.0), p);
		
		// kernel function for continuous spaces: Gaussian kernel 
//		double p = 5;
//		double sigma_k = 4; 
//		double kcont = Math.pow(p, 2.0) * Math.exp((Math.pow(b1.getTopBelief() - b2.getTopBelief(), 2.0) + Math.pow(b1.getSecondTopBelief() - b2.getSecondTopBelief(), 2.0)) / (-2 * Math.pow(sigma_k, 2.0)));	
		
		// kernel function for continuous spaces: Scaled norm kernel
//		double kcont = 1 - ((Math.pow(b1.getTopBelief() - b2.getTopBelief(), 2.0) + Math.pow(b1.getSecondTopBelief() - b2.getSecondTopBelief(), 2.0)) / 
//				((Math.pow(b1.getTopBelief(), 2.0) + Math.pow(b1.getSecondTopBelief(), 2.0)) * (Math.pow(b2.getTopBelief(), 2.0) + Math.pow(b2.getSecondTopBelief(), 2.0))));
		
		// kernel function for discrete spaces: 1 - Kronecker delta
//		double kdisc = 0.0;
//		if (b1.getPartitionState() != b2.getPartitionState()) kdisc += 1.0;
//		if (b1.getHistoryState() != b2.getHistoryState()) kdisc += 1.0;
//		if ((b1.getLastUserAction() == null && b2.getLastUserAction() != null) || 
//				(b1.getLastUserAction() != null && b2.getLastUserAction() == null) || 
//				(b1.getLastUserAction() != null && !b1.getLastUserAction().equals(b2.getLastUserAction()))) kdisc += 1.0;
//		if (!a1.equals(a2)) kdisc += 1.0;
		

		
		// kernel function for discrete spaces: Kronecker delta
//		double kdisc = 0.0;
//		if (b1.getPartitionState() == b2.getPartitionState()) kdisc += 1.0;
//		if (b1.getHistoryState() == b2.getHistoryState()) kdisc += 1.0;
//		if (b1.getLastUserAction() == null && b2.getLastUserAction() == null || (b1.getLastUserAction() != null && b1.getLastUserAction().equals(b2.getLastUserAction()))) kdisc += 1.0;
//		if (a1.equals(a2)) kdisc += 1.0;

		// kernel function is defined as the sum of kernels on continuous and discrete spaces
//		double k = kcont + kdisc;
//		return k;
		
		// own kernel function: 10 - distance (D)
//		double distance = b1.distanceTo(b2);
//		if (!a1.equals(a2))
//			distance += 1.0f;
//		double k = 10 - distance;
//		return k;
		
		// own kernel function: 2 - difference of last user action and a (D3)
//		double k = 2;
//		if (!a1.equals(a2))
//			k = k - 1;
//		if ((b1.getLastUserAction() == null && b2.getLastUserAction() != null) || 
//				(b1.getLastUserAction() != null && b2.getLastUserAction() == null) || 
//				(b1.getLastUserAction() != null && !b1.getLastUserAction().equals(b2.getLastUserAction()))) 
//			k = k - 1;
//		return k;
		
		// own kernel function: 6 - distance (is 0 when they are completely different) (D2)
//		double distance = b1.distanceTo(b2);
//		if (!a1.equals(a2))
//			distance += 1.0f;
//		double k = 6 - distance;
//		return k;
		
		// own kernel function which considers IQ in SummaryBelief: 7 - distance (is 0 when they are completely different) (DIQ)
		double iqb1 = (b1.getInteractionQuality() - 1) / 4;     // between 0 and 1
		double iqb2 = (b2.getInteractionQuality() - 1) / 4;     // between 0 and 1
		double iqdistance = Math.abs(iqb1 - iqb2);     // between 0 and 1 
		double distance = b1.distanceTo(b2);
		if (!a1.equals(a2))
			distance += 1.0f;
		double k = 7 - distance - iqdistance;     // between 0 and 7
		return k;
	}
	
	
	public static Matrix k_tilde(SummaryBeliefNonpersistent b, SummaryAgenda a)
	{
		return k_tilde(b, a, b.getOntology().factory.getAllSummaryBeliefInstances());
	}
	
	public static Matrix k_tilde(SummaryBeliefNonpersistent b, SummaryAgenda a, Collection<SummaryBelief> D)
	{
		Matrix ret = MatrixFactory.emptyMatrix();		
		for (SummaryBelief bDict: D)
		{
			double[] array = {k(b, a, bDict, bDict.getSummaryAgenda())};
			ret = ret.appendVertically(MatrixFactory.importFromArray(array));
		}
		return ret;
	}
	
	
	public void newEpisode(UsersimulatorDocument systemMove)
	{
		if (printToConsole) System.out.println("############### NEW DIALOGUE OPTIMIZER EPISODE ###############");
		
		// initialize a and b
		b = systemMove.getSbnp();
		a = systemMove.getAgendaObj().get(0).getSummaryAgenda();
		
		int iq = OwlSpeakServlet.getQualityObject().getCurrentIQ();
		b.setInteractinQuality(iq);
		
		if (printToConsole) System.out.println("a: " + a.toString());
		if (printToConsole) System.out.println("b: " + b.toString());
		
		// reset total reward R
		R = 0;
		i = 0;
		
		OwlSpeakOntology onto = b.getOntology();
		
		// load the dictionary D with all representative SummaryBeliefs
		Collection<SummaryBelief> D = onto.factory.getAllSummaryBeliefInstances();
		
		boolean firstDialogue = false;
		
		if (D.isEmpty())     // if first episode/dialogue
		{	
			firstDialogue = true;
			
			// initialise the matrices my and C
			my = MatrixFactory.zeros(ValueType.DOUBLE, 1, 1);
			C = MatrixFactory.zeros(ValueType.DOUBLE, 1, 1);
			
			// map a to b and add b to the ontology
			SummaryBelief bpersistent = onto.factory.createSummaryBelief(b);
			bpersistent.setSummaryAgenda(a);
			
			// update the ontology
			onto.write();
			onto.update();
			
			// update D
			D = onto.factory.getAllSummaryBeliefInstances();
			
			// initialise the matrix K
			K = MatrixFactory.ones(ValueType.DOUBLE, 1, 1).mtimes(1 / k(b, a, b, a));
		}
		
		c = MatrixFactory.zeros(ValueType.DOUBLE, D.size(), 1);
		d = 0.0;
		v = 0.0; // instead of 1/v = 0
		Matrix k_tilde_ba = k_tilde(b, a);
		g = K.mtimes(k_tilde_ba);
		double delta = k(b, a, b, a) - k_tilde_ba.transpose().mtimes(g).getAsDouble(0, 0);
		if (printToConsole) System.out.println("delta: " + delta);
		if (delta < 0) System.err.println("Delta is negative.");
		
		if (delta > ny && !firstDialogue)
		{
			// map a to b and add b to the ontology
			SummaryBelief bpersistent = onto.factory.createSummaryBelief(b);
			bpersistent.setSummaryAgenda(a);
			
			// update the ontology
			onto.write();
			onto.update();
			
			// update D
			D = onto.factory.getAllSummaryBeliefInstances();
			
			// update the matrices that are needed to calculate the mean and the covariance of the Q-function
			K = (K.mtimes(delta).plus(g.mtimes(g.transpose())).appendHorizontally(g.mtimes(-1.0)).appendVertically(g.transpose().mtimes(-1.0).appendHorizontally(MatrixFactory.ones(ValueType.DOUBLE, 1, 1)))).mtimes(1/delta);
			g = MatrixFactory.zeros(ValueType.DOUBLE, D.size()-1, 1).appendVertically(MatrixFactory.ones(ValueType.DOUBLE, 1, 1));
			my = my.appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, 1));
			C = C.appendHorizontally(MatrixFactory.zeros(ValueType.DOUBLE, C.getRowCount(), 1)).appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, C.getColumnCount() + 1));
			c = c.appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, 1));
		}
		
		// export the matrices my, C and K to calculate the mean and the covariance of the Q-function in the policy
		try {
			my.exportToFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/my.csv");
		} catch (MatrixException e) {
	        e.printStackTrace();
		} catch (IOException e) {
	        e.printStackTrace();
		}
		try {
			C.exportToFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/C.csv");
		} catch (MatrixException e) {
	        e.printStackTrace();
		} catch (IOException e) {
	        e.printStackTrace();
		}
		try {
			K.exportToFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/K.csv");
		} catch (MatrixException e) {
	        e.printStackTrace();
		} catch (IOException e) {
	        e.printStackTrace();
		}
	}
	
	
	public double optimise(UsersimulatorDocument systemMove, String user, boolean success, boolean failure)
	{
		return optimise(systemMove, user, success, failure, false);
	}
	
	
	public double optimise(UsersimulatorDocument systemMove, String user, boolean success, boolean failure, boolean abort)
	{
		if (printToConsole) System.out.println("############### OPTIMIZATION ###############");
		
		SummaryBeliefNonpersistent b_ = null;
		SummaryAgenda a_ = null;
		int r = 0;
		
		Matrix c_ = null;
		Matrix g_ = null;
		Matrix delta_k_tilde = null;
		Matrix h = null;
		double delta = 0;
		
		OwlSpeakOntology onto = b.getOntology();
		
		// iqReward = true if current IQ value should be used in the immediate reward in every dialogue turn
		boolean iqReward = false;
		
		int iq = OwlSpeakServlet.getQualityObject().getCurrentIQ();
		
		// set final reward for success, failure and abort
		if (success == true && failure == false && abort == false) 
		{
			// reward 1 & 4:
			r = 20;
			// reward 2 & 3 & 5:
//			r = 100;
		}
		else if (success == false && failure == true && abort == false) 
		{
			// reward 1 & 3 & 4:
			r = Core.getCore().getRewardFromAgenda(a, user);
			// reward 2:
//			r = -100;
			// reward 5:
//			r = 0;
		}
		else if (success == false && failure == false && abort == true) 
		{
			// reward 1:
			r = Core.getCore().getRewardFromAgenda(a, user);
			// reward 2 & 3:
//			r = -100;
			// reward 4:
//			r = -20;
			// reward 5:
//			r = 0;
		}
		else if (success == false && failure == false && abort == false) 
		{
			// non-terminal step
			// reward 1 & 2 & 3 & 4:
			r = Core.getCore().getRewardFromAgenda(a, user);
			// reward 5:
//			r = 0;
			b_ = systemMove.getSbnp();
			b_.setInteractinQuality(iq);
			a_ = systemMove.getAgendaObj().get(0).getSummaryAgenda();
		}
		else
			System.err.println("Reward calculation not possible due to faulty flags.");
		
		R += Math.pow(gamma, i) * r;
		i ++;
		if (printToConsole) System.out.println("R: " + R);
				
		Collection<SummaryBelief> D = onto.factory.getAllSummaryBeliefInstances();
		if (printToConsole) System.out.println("D (" + D.size() + "): " + D.toString());
		
		Matrix k_tilde_ba = k_tilde(b, a);
		Matrix k_tilde_b_a_ = null;
		
		if (success == false && failure == false && abort == false)    // if non-terminal step
		{
			k_tilde_b_a_ = k_tilde(b_, a_);
			g_ = K.mtimes(k_tilde_b_a_);
			delta = k(b_, a_, b_, a_) - k_tilde_b_a_.transpose().mtimes(g_).getAsDouble(0, 0);
			delta_k_tilde = k_tilde_ba.minus(k_tilde_b_a_.mtimes(gamma));
		}
		else     // if terminal step
		{
			g_ = MatrixFactory.zeros(ValueType.DOUBLE, D.size(), 1);
			delta = 0;
			delta_k_tilde = k_tilde_ba;
		}
		
		if (iqReward) {
			d = gamma * Math.pow(sigma, 2.0) * v * d + r + iq - delta_k_tilde.transpose().mtimes(my).getAsDouble(0, 0); // Reward = defaultReward + iq 
		}
		else {
			d = gamma * Math.pow(sigma, 2.0) * v * d + r - delta_k_tilde.transpose().mtimes(my).getAsDouble(0, 0); // Reward = defaultReward 
		}
			
		if (printToConsole) System.out.println("delta: " + delta);
		if (delta < 0) System.err.println("Delta is negative.");
		
		if (delta > ny && success == false && failure == false && abort == false)    // if delta > ny and non-terminal step	
		{
			// map a_ to b_ and add b_ to the ontology
			SummaryBelief bpersistent = onto.factory.createSummaryBelief(b_);
			bpersistent.setSummaryAgenda(a_);
			
			// update the ontology
			onto.write();
			onto.update();
			
			// update D
			D = onto.factory.getAllSummaryBeliefInstances();			
			
			// update the matrices that are needed to calculate the mean and the covariance of the Q-function
			K = (K.mtimes(delta).plus(g_.mtimes(g_.transpose())).appendHorizontally(g_.mtimes(-1.0)).appendVertically(g_.transpose().mtimes(-1.0).appendHorizontally(MatrixFactory.ones(ValueType.DOUBLE, 1, 1)))).mtimes(1/delta);
			g_ = MatrixFactory.zeros(ValueType.DOUBLE, D.size()-1, 1).appendVertically(MatrixFactory.ones(ValueType.DOUBLE, 1, 1));
			h = g.appendVertically(MatrixFactory.ones(ValueType.DOUBLE, 1, 1).mtimes(gamma * -1.0));
			double delta_k = g.transpose().mtimes(k_tilde_ba.minus(k_tilde_b_a_.mtimes(2 * gamma))).getAsDouble(0, 0) + Math.pow(gamma, 2.0) * k(b_, a_, b_, a_);
			c_ = (c.appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, 1)).mtimes(gamma * Math.pow(sigma, 2.0) * v)).plus(h).minus(C.mtimes(delta_k_tilde).appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, 1)));
			v =	1 / ((1 + Math.pow(gamma, 2.0)) * Math.pow(sigma, 2.0) + delta_k - delta_k_tilde.transpose().mtimes(C).mtimes(delta_k_tilde).getAsDouble(0, 0) + 2 * gamma * Math.pow(sigma, 2.0) * v * c.transpose().mtimes(delta_k_tilde).getAsDouble(0, 0) - Math.pow(gamma, 2.0) * Math.pow(sigma, 4.0) * v);	
			my = my.appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, 1));
			C = C.appendHorizontally(MatrixFactory.zeros(ValueType.DOUBLE, C.getRowCount(), 1)).appendVertically(MatrixFactory.zeros(ValueType.DOUBLE, 1, C.getColumnCount() + 1));
		}
		else     // if delta < ny or terminal step
		{
			// update the matrices that are needed to calculate the mean and the covariance of the Q-function
			h = g.minus(g_.mtimes(gamma));
			c_ = (c.mtimes(gamma * Math.pow(sigma, 2.0) * v)).plus(h).minus(C.mtimes(delta_k_tilde));
			
			if (success == false && failure == false && abort == false)    // if non-terminal step
			{
				v = 1 / ((1 + Math.pow(gamma, 2.0)) * Math.pow(sigma, 2.0) + delta_k_tilde.transpose().mtimes(c_.plus(c.mtimes(gamma * Math.pow(sigma, 2.0) * v))).getAsDouble(0, 0) - Math.pow(gamma, 2.0) * Math.pow(sigma, 4.0) * v);
			}
			else     // if terminal step
			{
				v = 1 / (Math.pow(sigma, 2.0) + delta_k_tilde.transpose().mtimes(c_.plus(c.mtimes(gamma * Math.pow(sigma, 2.0) * v))).getAsDouble(0, 0) - Math.pow(gamma, 2.0) * Math.pow(sigma, 4.0) * v);
			}
		}
		
		// update the matrices that are needed to calculate the mean and the covariance of the Q-function
		my = my.plus(c_.mtimes(v * d));
		C = C.plus(c_.mtimes(c_.transpose()).mtimes(v));
		
		// update for the next step
		c = c_;
		g = g_;
		
		if (success == false && failure == false && abort == false)    // if non-terminal step
		{
			// update for the next step
			b = b_;
			a = a_;
		}	
		
		// export the matrices my, C and K to calculate the mean and the covariance of the Q-function in the policy
		try {
			my.exportToFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/my.csv");
		} catch (MatrixException e) {
	        e.printStackTrace();
		} catch (IOException e) {
	        e.printStackTrace();
		}
		try {
			C.exportToFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/C.csv");
		} catch (MatrixException e) {
	        e.printStackTrace();
		} catch (IOException e) {
	        e.printStackTrace();
		}
		try {
			K.exportToFile(FileFormat.CSV, "./conf/OwlSpeak/models/GP_PolicyModels/K.csv");
		} catch (MatrixException e) {
	        e.printStackTrace();
		} catch (IOException e) {
	        e.printStackTrace();
		}
		
		return R;
	}
}

