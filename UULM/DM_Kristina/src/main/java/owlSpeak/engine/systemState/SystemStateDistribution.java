package owlSpeak.engine.systemState;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Move.ExtractFieldMode;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Field;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.UserAction.UserActionType;
import owlSpeak.engine.his.interfaces.INBestList;
import owlSpeak.engine.his.interfaces.IPartitionDistribution;
import owlSpeak.engine.his.sysAction.GenericSystemAction;

/**
 * This class contains all HIS specific methodology for handling the system
 * state.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * 
 */
public class SystemStateDistribution extends SystemState {

	private int totalNumberOfUserActions = -1;

	public SystemStateDistribution(Core _core) {
		super(_core);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateState(UserAction[] userActions,
			double[] confidenceScores, GenericSystemAction sysAct,
			OwlSpeakOntology actualOnto, String user) {

		if (userActions.length > 0)
			for (UserAction userAction : userActions) {
				this.processAgendaExceptGrammar(sysAct, actualOnto, userAction,
						user);
			}
		else
			this.processAgendaExceptGrammar(sysAct, actualOnto, null, user);
		
		// perform actual update
		INBestList nBestList = null;
		nBestList = core.nBestListFactory.create(userActions, confidenceScores,
				getTotalNumberOfUserActions(actualOnto.factory));
		IPartitionDistribution partitions = actualOnto.partitionDistributions[Settings
				.getuserpos(user)];
		partitions.Update(nBestList, sysAct);

		// set most likely partition as belief space
		// necessary for heuristics for agenda execution
		actualOnto.beliefSpace[Settings.getuserpos(user)] = (Partition) partitions
				.getTopPartition();

	}

	@Override
	public void updateStateSingleAction(UserAction userAction,
			double confidenceScore, GenericSystemAction sysAct,
			OwlSpeakOntology actualOnto, String user) {
		UserAction[] userActions = new UserAction[1];
		userActions[0] = userAction;
		double[] confidenceScores = new double[1];
		confidenceScores[0] = confidenceScore;

		// perform actual update
		INBestList nBestList = null;

		nBestList = core.nBestListFactory.create(userActions, confidenceScores,
				getTotalNumberOfUserActions(actualOnto.factory));
		IPartitionDistribution partitions = actualOnto.partitionDistributions[Settings
				.getuserpos(user)];
		partitions.Update(nBestList, sysAct);

		// set most likely partition as belief space
		// necessary for heuristics for agenda execution
		actualOnto.beliefSpace[Settings.getuserpos(user)] = (Partition) partitions
				.getTopPartition();
	}

	// public void processAgendaExceptGrammarOld(GenericSystemAction sysAct,
	// OwlSpeakOntology onto, UserAction userAction, String user) {
	//
	// Agenda toDoAgenda = sysAct.getAgenda();
	// Move userMove = null;
	// if (userAction != null) {
	// userMove = userAction.getMove();
	//
	// for (Move tempMove : toDoAgenda.getHas()) {
	// if (!tempMove.hasGrammar() && !tempMove.getIsRequestMove()) {
	//
	// if (!tempMove.hasProcessConditions()) {
	// userAction.addAdditionalMove(tempMove, onto, user);
	// } else {
	// if (tempMove.getProcessConditions().contains(userMove))
	// userAction.addAdditionalMove(tempMove, onto, user);
	// }
	// }
	// }
	// }
	// }

	@Override
	public void processAgendaExceptGrammar(GenericSystemAction sysAct,
			OwlSpeakOntology onto, UserAction userAction, String user) {

		Agenda toDoAgenda = sysAct.getAgenda();

		System.out.println(this.getClass()
				+ ".processAgendaExeptGrammar  for agenda " + toDoAgenda);

		for (Move tempMove : toDoAgenda.getHas()) {
			if (!tempMove.hasGrammar() && !tempMove.getIsRequestMove()
					&& tempMove.getExtractFieldValues() == ExtractFieldMode.NO) {

				UserAction uAct = UserAction.creatNewUserAction(tempMove, core,
						null, user); // type irrelevant

				String belSpaceName = user + "BeliefspaceGeneric";
				if (!onto.factory.existsBeliefSpace(belSpaceName))
					onto.factory.createBeliefSpace(belSpaceName);
				BeliefSpace belSpace = onto.factory
						.getBeliefSpace(belSpaceName);

				if (!tempMove.hasProcessConditions()) {
					processMove(uAct, belSpace, onto.factory, onto.Name, user,
							core);
				} else {
					Move userMove = null;
					if (userAction != null) {
						userMove = userAction.getMove();
						if (tempMove.getProcessConditions().contains(userMove))
							processMove(uAct, belSpace, onto.factory,
									onto.Name, user, core);
					}
				}
			}
		}
	}

	@Override
	public void processRequestMoves(Agenda toDoAgenda, OwlSpeakOntology onto,
			String user) {

		// TODO create user act with request move as main move or as side move??
		UserAction userAct = UserAction.creatNewUserAction(onto, core,
				UserActionType.IMPLICIT, user);
		double confidenceScore = 1.0f;
		GenericSystemAction sysAct = GenericSystemAction.createSystemAction(
				toDoAgenda, core);
		/*
		 * retrieve the equals corresponding to performed system action.
		 * necessary for determining userActionLikelihood when comparing
		 * confirmed value with partition content. Works always because for
		 * trained policy, also the most likely partition is used for filling
		 * the parameters of the selected agendas.
		 */
		if (sysAct.hasField()) {
			for (String fd : sysAct.getFields()) {
				Field f = ((Partition) onto.beliefSpace[Settings
						.getuserpos(user)]).getFields().get(fd);
				if (f != null)
					sysAct.setValue(fd, f.getEquals());
			}
		}
		userAct.setSystemAct(sysAct);

		for (Move tempMove : toDoAgenda.getHas()) {
			if (!tempMove.hasGrammar() && tempMove.getIsRequestMove()) {
				userAct.addAdditionalMove(tempMove, onto, user);
			}
		}

		if (userAct.getFieldVector() != null
				&& userAct.getFieldVector().size() > 0)
			updateStateSingleAction(userAct, confidenceScore, sysAct, onto,
					user);
	}

	/**
	 * For this method, a user action is defined by a move containing a grammar.
	 */
	public int getTotalNumberOfUserActions(OSFactory factory) {
		if (totalNumberOfUserActions == -1) {
			Set<String> setOfMoves = new TreeSet<String>();
			Collection<Agenda> agendas = factory.getAllAgendaInstances();
			for (Agenda agenda : agendas) {
				Collection<Move> moves = agenda.getHas();
				for (Move move : moves) {
					if (move.hasGrammar())
						setOfMoves.add(move.getFullName());
				}
			}
			totalNumberOfUserActions = setOfMoves.size();
		}
		return totalNumberOfUserActions;
	}
}
