package owlSpeak.servlet.document;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;



import java.util.stream.Collectors;

import org.jdom.Document;
import org.jdom.Element;
import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Variable;
import owlSpeak.engine.Core;
import owlSpeak.engine.CoreMove;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.plugin.Result;
import owlSpeak.servlet.document.OwlDocument;

public class UsersimulatorDocument extends Document implements OwlDocument {

	private static final long serialVersionUID = 3539566299404044938L;
	private String systemMove = null;
	private List<Agenda> agenda = null;
	private boolean isExitMove = false;
	private TreeMap<String, String> variables = null;
	private Vector<String> allowedUserMovesList = null;
	private SummaryBeliefNonpersistent sbnp;

	public UsersimulatorDocument() {
		super();
		variables = new TreeMap<String, String>();
		allowedUserMovesList = new Vector<String>();
	};

	public String getSystemMove() {
		return systemMove;
	}

	public String getAgenda() {
		return agenda.get(0).getLocalName();
	}
	
	public List<Agenda> getAgendaObj() {
		return agenda;
	}

	public Map<String, String> getVariables() {
		return variables;
	}

	public String getVariableValue(String variableName) {

		if (variables != null) {
			return variables.get(variableName);
		}
		return null;
	}

	public boolean isExitMove() {
		return isExitMove;
	}

	public Vector<String> getAllowedUserMovesList() {
		return allowedUserMovesList;
	}

	private void parseVariables(String utterance, OSFactory factory, Core core,
			BeliefSpace bel, String user) {
		if (utterance.contains("%")) {
			String[] var = utterance.split("%", -1);
			
			// var names always in uneven entries
			int numVariables = ((var.length - 1) / 2);
			for (int i = 0; i < numVariables; i++) {
				Variable tempVar = CoreMove.fetchVariable(var[(i * 2) + 1],
						factory, core);
				BeliefSpace beliefS = CoreMove.findVarBelSpace(
						var[(i * 2) + 1], factory, core, bel, user);
				if (!Core.variableIsContainedInBeliefSpace(tempVar, beliefS))
					beliefS = factory.getBeliefSpace(user+"BeliefspaceGeneric");
				variables.put(var[(i * 2) + 1],
						Core.getVariableString(tempVar, beliefS));
			}
		}
	}

	@Override
	public UsersimulatorDocument fillDocument(Vector<CoreMove> utterance,
			Vector<CoreMove> grammar, List<Agenda> actualAgenda, String whereAmI,
			String user, String noInputCounter) {
		if (!(utterance.isEmpty())) {
			Move m = utterance.firstElement().move;
			systemMove = m.getLocalName();
			isExitMove = m.getIsExitMove();
			agenda = actualAgenda;

			for (OWLLiteral lit : utterance.firstElement().move.getUtterance()
					.getUtteranceString()) {
				parseVariables(lit.getLiteral(),
						utterance.firstElement().onto.factory,
						utterance.firstElement().core,
						utterance.firstElement().onto.beliefSpace[Settings
								.getuserpos(user)], user);
			}
		}
		if (!(grammar.isEmpty()) && !(allowedUserMovesList == null)) {
			
			for( int i = 0; i < grammar.size(); i++){
			
			Move m = grammar.get(i).move;
			
			String userMove = m.getLocalName();
			allowedUserMovesList.add(userMove);}
		}
		return this;
	}

	@Override
	public void generateDocument() {
		// TODO Auto-generated method stub
	}

	@Override
	public UsersimulatorDocument generateSleep(String whereAmI, String user,
			String noInputCounter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Element newDialogueForm(String agenda, String user) {
		return null;
	}

	@Override
	public void buildUtterance(Element element, Vector<CoreMove> utterance,
			List<Agenda> actualAgenda, String whereAmI, String user) {
	}

	@Override
	public UsersimulatorDocument buildOutput(String whereAmI, String VDocname,
			String output, String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UsersimulatorDocument buildSolveConflict(String utterance,
			Vector<String[]> grammar, Agenda actualAgenda, String whereAmI,
			String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UsersimulatorDocument buildPluginAlternatives(Vector<Result> result,
			Agenda actualAgenda, String whereAmI, String user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String output() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSummaryBeliefNonPersistant(
			SummaryBeliefNonpersistent summaryBeliefNonpersisnent) {
		sbnp = summaryBeliefNonpersisnent;
		
	}

	public SummaryBeliefNonpersistent getSbnp() {
		return sbnp;
	}
}