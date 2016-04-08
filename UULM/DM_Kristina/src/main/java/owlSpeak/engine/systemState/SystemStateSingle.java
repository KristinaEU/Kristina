package owlSpeak.engine.systemState;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.engine.Core;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.UserAction.UserActionType;
import owlSpeak.engine.his.sysAction.GenericSystemAction;

/**
 * This class contains all specific methods for handling Information State based
 * Belief Spaces.
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * 
 */
public class SystemStateSingle extends SystemState {

	public SystemStateSingle(Core _core) {
		super(_core);
	}

	@Override
	public void updateState(UserAction[] userActions,
			double[] confidenceScores, GenericSystemAction sysAct,
			OwlSpeakOntology actualOnto, String user) {
		processMove(userActions[0],
				actualOnto.beliefSpace[Settings.getuserpos(user)],
				actualOnto.factory, actualOnto.Name, user, core);

		processAgendaExceptGrammar(sysAct, actualOnto, userActions[0], user);
	}

	@Override
	public void updateStateSingleAction(UserAction userAction,
			double confidenceScore, GenericSystemAction sysAct,
			OwlSpeakOntology actualOnto, String user) {
		processMove(userAction,
				actualOnto.beliefSpace[Settings.getuserpos(user)],
				actualOnto.factory, actualOnto.Name, user, core);
	}

	@Override
	public void processRequestMoves(Agenda toDoAgenda, OwlSpeakOntology onto,
			String user) {
		BeliefSpace beliefSpace = onto
				.getBeliefSpace(Settings.getuserpos(user));
		OSFactory factory = onto.factory;
		String prefix = onto.Name;

		for (Move tempMove : toDoAgenda.getHas()) {
			if (!tempMove.hasGrammar() && tempMove.getIsRequestMove()) {
				UserAction userAct = UserAction.creatNewUserAction(tempMove,
						core, UserActionType.IMPLICIT, user);
				System.out.println("processing request move "
						+ tempMove.getLocalName());
				processMove(userAct, beliefSpace, factory, prefix, user, core);
			}
		}
	}


	/**
	 * processes all moves of a given agenda except the moves with grammars
	 * 
	 * @param userAction
	 *            the user move object needed for checking processing conditions
	 * @param user
	 *            the name of the current user
	 * @param toDoAgenda
	 *            the agenda
	 * @param beliefSpace
	 *            the beliefspace of the agenda
	 * @param factory
	 *            the factory of the agenda
	 */

	@Override
	public void processAgendaExceptGrammar(GenericSystemAction sysAct,
			OwlSpeakOntology onto, UserAction userAction, String user) {
		BeliefSpace beliefSpace = onto
				.getBeliefSpace(Settings.getuserpos(user));
		OSFactory factory = onto.factory;
		String prefix = onto.Name;
		Agenda toDoAgenda = sysAct.getAgenda();
		
		System.out.println(this.getClass() + ".processAgendaExeptGrammar  for agenda " + toDoAgenda);
		
		Move userMove = null;
		if (userAction != null)
			userMove = userAction.getMove();

		for (Move tempMove : toDoAgenda.getHas()) {
			if (!tempMove.hasGrammar() && !tempMove.getIsRequestMove()) {
				UserAction userAct = UserAction.creatNewUserAction(tempMove,
						core, UserActionType.IMPLICIT, user);
				if (!tempMove.hasProcessConditions()) {
					processMove(userAct, beliefSpace, factory, prefix,
							user, core);
				} else if (userMove != null) {
					if (tempMove.getProcessConditions().contains(userMove))
						processMove(userAct, beliefSpace, factory, prefix,
								user, core);
				}
			}
		}
	}
}
