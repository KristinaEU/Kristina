package owlSpeak.engine;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.jdom.Element;
import org.semanticweb.owlapi.model.OWLLiteral;

import owlSpeak.Agenda;
import owlSpeak.BeliefSpace;
import owlSpeak.Move;
import owlSpeak.Semantic;
import owlSpeak.Variable;
import owlSpeak.engine.Settings.Usersetting;
import owlSpeak.engine.his.CommandUserAction;
import owlSpeak.engine.his.Field;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.SortedMap;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.UserAction;
import owlSpeak.engine.his.UserAction.UserActionType;
import owlSpeak.engine.his.interfaces.IUserAction;
import owlSpeak.engine.his.sysAction.GenericSystemAction;
import owlSpeak.engine.policy.Policy;
import owlSpeak.engine.systemState.SystemState;
import owlSpeak.engine.systemState.SystemState.SystemStateVariant;
import owlSpeak.kristina.KristinaDocument;
import owlSpeak.kristina.KristinaGrammar;
import owlSpeak.plugin.Result;
import owlSpeak.servlet.EnhancedVoiceDocument;
import owlSpeak.servlet.HtmlSaltDocument;
import owlSpeak.servlet.HtmlVoiceDocument;
import owlSpeak.servlet.OwlDocument;
import owlSpeak.servlet.VoiceDocument;
import owlSpeak.servlet.VoiceDocumentPOMDP;
import owlSpeak.servlet.grammar.GrammarGSL;
import owlSpeak.servlet.grammar.GrammarJSGF;
import owlSpeak.servlet.grammar.GrammarJSGFasFile;
import owlSpeak.servlet.grammar.GrammarSRGS;
import owlSpeak.servlet.grammar.IGrammar;

/**
 * The object that contains the Core object and the VoiceDocument functions
 * 
 * @author Dan Denich;
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 */
public class ServletEngine {

	/**
	 * the SystemCore variable
	 * 
	 * @see Core
	 */
	public Core systemCore;

	/**
	 * the Settings variable
	 * 
	 * @see Settings
	 */
	public Settings settings;

	/**
	 * indicates that a change of the ontologies happened
	 */
	public boolean change;

	// private InteractionQuality interactionQuality; // seemed to be
	// unnecessary

	/**
	 * Constructor
	 */
	public ServletEngine() {
		super();
		systemCore = Core.getCore();
		settings = systemCore.settings;

		// TODO make IQ recognition optional (defined in settings.xml)
		// interactionQuality = new InteractionQuality(SystemCore); // seemed to
		// be unnecessary
	}

	/**
	 * sets the TTS output type
	 * 
	 * @param id
	 *            the id of the tts output type
	 * @param user
	 *            the actual user
	 */
	public void setTtsMode(int id, String user) {
		Settings.ttsModeID = id;
		if (settings.getusersetting(user).autosave == 1)
			settings.write();
	}

	/**
	 * sets the ASR input language
	 * 
	 * @param id
	 *            the id of the asr input language
	 * @param user
	 *            the actual user
	 */
	public void setAsrMode(int id, String user) {
		Settings.asrModeID = id;
		if (settings.getusersetting(user).autosave == 1)
			settings.write();
	}

	/**
	 * one of the build functions that generate output. sets the sleep flag of
	 * the user to 1
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildSetSleepEnabled(String whereAmI, String user) {
		setSleepEnabled(user);
		return buildOutput(whereAmI, "setSystemSleep", "Goodnight, sir.", user);
	}

	/**
	 * sets the sleep flag of the user to 1
	 * 
	 * @param user
	 *            the name of the user
	 */
	public void setSleepEnabled(String user) {
		settings.getusersetting(user).sleep = 1;
		if (settings.getusersetting(user).autosave == 1)
			settings.write();
	}

	/**
	 * one of the build functions that generate output. sets the sleep flag of
	 * the user to 0
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildSetSleepDisabled(String whereAmI, String user) {
		setSleepDisabled(user);
		return buildOutput(whereAmI, "setSystemSleep", "System resumed, sir.",
				user);
	}

	/**
	 * sets the sleep flag of the user to 0
	 * 
	 * @param user
	 *            the name of the user
	 */
	public void setSleepDisabled(String user) {
		settings.getusersetting(user).sleep = 0;
		if (settings.getusersetting(user).autosave == 1)
			settings.write();
	}

	/**
	 * one of the build functions that generate output. sets the type of the
	 * output document
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param type
	 *            the number of the output type (1=VXML, 2=HTML)
	 */
	public OwlDocument buildSetOutputType(String whereAmI, int type, String user) {
		setOutputType(type, user);
		return buildOutput(whereAmI, "setTypeOutput", "output activated: "
				+ type, user);
	}

	/**
	 * sets the type of the output document
	 * 
	 * @param user
	 *            the name of the user
	 * @param type
	 *            the number of the output type (1=VXML, 2=HTML)
	 */
	public void setOutputType(int type, String user) {
		settings.getusersetting(user).docType = type;
		if (settings.getusersetting(user).autosave == 1)
			settings.write();
	}
	

	/**
	 * one of the build functions that generate output. assigns a value to the
	 * AutoWrite setting
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param auto
	 *            the value that should be assigned to AutoSet (1=on 2=off)
	 */
	public OwlDocument buildSetAutoWriteSettings(String whereAmI, int auto,
			String user) {
		setAutoWriteSettings(auto, user);
		return buildOutput(whereAmI, "setAutoWriteSettings",
				"Auto write anabled? " + auto, user);
	}

	/**
	 * assigns a value to the AutoWrite setting
	 * 
	 * @param user
	 *            the name of the user
	 * @param auto
	 *            the value that should be assigned to AutoSet (1=on 2=off)
	 */
	public void setAutoWriteSettings(int auto, String user) {
		settings.getusersetting(user).autosave = auto;
		settings.write();
	}

	/**
	 * generates a HTML output representing the state of the sleep flag
	 * 
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument getSleepAsHTML(String user) {
		HtmlVoiceDocument VDoc = new HtmlVoiceDocument();
		Element test = VDoc.newDialogueForm("body", user);
		Element sleepStatus = new Element("sleepStatus");
		if (settings.getusersetting(user).sleep == 1) {
			sleepStatus.addContent("1");
		} else {
			sleepStatus.addContent("0");
		}
		test.addContent(sleepStatus);
		return VDoc;
	}

	/**
	 * returns the state of the sleep flag
	 * 
	 * @param user
	 *            the name of the user
	 */
	public int getSleep(String user) {
		return settings.getusersetting(user).sleep;
	}

	/**
	 * one of the build functions that generate output. returns a document that
	 * contains a sleep-loop until the sleep flag is set to 0
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildSleep(String whereAmI, String user,
			String noInputCounter) {
		return getDocByType(settings.getusersetting(user).docType,
				settings.getusersetting(user).grammarType).generateSleep(
				whereAmI, user, noInputCounter);
	}

	/**
	 * one of the build functions that generate output. reloads the Settings
	 * from disk
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildReloadSettings(String whereAmI, String user) {
		reloadSettings();
		return buildOutput(whereAmI, "RLSettings", "Settings reloaded", user);
	}

	/**
	 * reloads the Settings from disk and updates the SystemCore
	 */
	public void reloadSettings() {
		settings.reload();
		systemCore.loadOntologiesFromSettings();
	}

	/**
	 * one of the build functions that generate output. creates a belief with
	 * the specified semantic in the specified ontology, if the semantic does
	 * not exist, a new one is created
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param ontology
	 *            the name of the ontology
	 * @param semantic
	 *            the name of the semantic
	 */
	public OwlDocument buildCreateBelief(String whereAmI, String ontology,
			String semantic, String user) {
		createBelief(ontology, semantic, user);
		return buildOutput(whereAmI, "CRBelief", "Belief created.", user);
	}

	/**
	 * creates a belief with the specified semantic in the specified ontology,
	 * if the semantic does not exist, a new one is created
	 * 
	 * @param user
	 *            the name of the user
	 * @param ontology
	 *            the name of the ontology
	 * @param semantic
	 *            the name of the semantic
	 */
	public void createBelief(String ontology, String semantic, String user) {
		OwlSpeakOntology onto = systemCore.findOntology(ontology);
		BeliefSpace BeliefSpace = onto.beliefSpace[Settings.getuserpos(user)];
		Semantic sem;
		try {
			sem = onto.factory.getSemantic(semantic);
			sem.getSemanticString().toString();
			systemCore.createBelief(sem, BeliefSpace, onto.factory);
		} catch (NullPointerException n) {
			sem = onto.factory.createSemantic(semantic);
			sem.addSemanticString(onto.factory.factory.getOWLLiteral(
					"Semantic created by Function.", "en"));
			systemCore.createBelief(sem, BeliefSpace, onto.factory);
		}
		onto.write();
	}

	/**
	 * one of the build functions that generate output. sets a variable to the
	 * specified value
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param ontology
	 *            the name of the ontology
	 * @param variable
	 *            the variable that should be set
	 * @param value
	 *            the value that should be assigned
	 */
	public OwlDocument buildSetVariable(String whereAmI, String ontology,
			String variable, String value, String user) {
		setVariable(ontology, variable, value, user);
		return buildOutput(whereAmI, "setVar", "Variable set.", user);
	}

	/**
	 * sets a variable to the specified value
	 * 
	 * @param user
	 *            the name of the user
	 * @param ontology
	 *            the name of the ontology
	 * @param variable
	 *            the variable that should be set
	 * @param value
	 *            the value that should be assigned
	 */
	public boolean setVariable(String ontology, String variable, String value,
			String user) {
		OwlSpeakOntology onto = systemCore.findOntology(ontology);
		Variable var;
		try {
			var = onto.factory.getVariable(variable);
			if ((var != null) && (onto != null)) {
				systemCore.setVariableString(var,
						onto.beliefSpace[Settings.getuserpos(user)],
						onto.factory, value, user);
				onto.write();
				return true;
			}
			return false;
		} catch (NullPointerException n) {
			n.printStackTrace();
			return false;
		}
	}

	/**
	 * one of the build functions that generate output. returns a document
	 * cotaining the value of a variable
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param ontology
	 *            the name of the ontology
	 * @param variable
	 *            the variable that should be set
	 */
	public OwlDocument buildGetVariable(String whereAmI, String ontology,
			String variable, String user) {
		return buildOutput(whereAmI, "getVar",
				variable + ": %%" + getVariable(ontology, variable, user)
						+ "%%", user);
	}

	/**
	 * returns a the value of a variable as String
	 * 
	 * @param ontology
	 *            the name of the ontology
	 * @param variable
	 *            the variable that should be set
	 * @param user
	 *            the name of the user
	 */
	public String getVariable(String ontology, String variable, String user) {
		OwlSpeakOntology onto = systemCore.findOntology(ontology);
		String value = "";
		Variable var;
		try {
			var = onto.factory.getVariable(variable);
			value = Core.getVariableString(var,
					onto.beliefSpace[Settings.getuserpos(user)]);
		} catch (NullPointerException n) {
			n.printStackTrace();
		}
		;
		return value;
	}

	/**
	 * one of the build functions that generate output. resets all available
	 * ontologies
	 */
	public OwlDocument buildReset(String whereAmI, String user) {
		reset();
		return buildOutput(whereAmI, "Reset", "Performed reset", user);
	}

	/**
	 * resets all available ontologies
	 */
	public void reset() {
		for (int i = 0; i < systemCore.actualOnto.length; i++) {
			systemCore.actualOnto[i] = null;
		}

		for (int i = 0; i < systemCore.ontologies.size(); i++) {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
			System.out.println(formatter.format(date) + " Reset: "
					+ systemCore.ontologies.get(i).Name);
			systemCore.ontologies.get(i).resetall();
			systemCore.actualtime = 0;
		}
	}

	/**
	 * one of the build functions that generate output. resets one ontology for
	 * one user
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param ontoname
	 *            the name of the ontology to be reset
	 */
	public OwlDocument buildReset(String whereAmI, String user, String ontoname) {
		reset(user, ontoname);
		return buildOutput(whereAmI, "Reset", "Performed reset for user "
				+ user + " in Ontology " + ontoname + ".", user);
	}

	/**
	 * resets one ontology for one user
	 * 
	 * @param user
	 *            the name of the user
	 * @param ontoname
	 *            the name of the ontology to be reset
	 */
	public void reset(String user, String ontoname) {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		OwlSpeakOntology test = systemCore.findOntology(ontoname);
		System.out.println(formatter.format(date) + " Reset: " + test.Name);
		test.reset(user);
	}

	/**
	 * one of the build functions that generate output. generates a document
	 * that contains the specified string
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param VDocname
	 *            the Name of the output document
	 * @param output
	 *            the output-string
	 */
	public OwlDocument buildOutput(String whereAmI, String VDocname,
			String output, String user) {
		return getDocByType(settings.getusersetting(user).docType,
				settings.getusersetting(user).grammarType).buildOutput(
				whereAmI, VDocname, output, user);
	}

	/**
	 * parses the String specified in move and calls buildExecute(string,
	 * string, string, string) used for executing functions in the dialogue
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildExecute(String whereAmI, String move, String user) {
		int tempInt = 0;
		System.out.println("move: " + move);
		tempInt = move.indexOf(";");
		if (tempInt != -1)
			return buildExecute(whereAmI, move.substring(0, tempInt),
					move.substring(tempInt + 1), user);
		else
			return buildExecute(whereAmI, move, "", user);
	}

	/**
	 * [DISABLED at the moment] calls other functions used for executing
	 * functions in the dialogue
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param execute
	 *            the "name" of the function
	 * @param para
	 *            the "parameters" of the function
	 */
	public OwlDocument buildExecute(String whereAmI, String execute,
			String para, String user) {
		if (execute.equalsIgnoreCase("reset"))
			return buildReset(whereAmI, user);
		if (execute.equalsIgnoreCase("reloadSettings"))
			return buildReloadSettings(whereAmI, user);
		if (execute.equalsIgnoreCase("abort"))
			return buildRequest(whereAmI, user, null);
		// if(execute.equalsIgnoreCase("reset")) return buildRequest(whereAmI,
		// user, null);
		// if(execute.equalsIgnoreCase("reloadSettings")) return
		// buildRequest(whereAmI, user, null);
		if (execute.equalsIgnoreCase("reduce")) {
			int prio = systemCore.actualOnto[Settings.getuserpos(user)].actualAgenda[Settings
					.getuserpos(user)].getPriority();
			prio = (int) (prio * 0.9);
			System.out.println("New piority: " + prio);
			Agenda muh = systemCore.actualOnto[Settings.getuserpos(user)].factory
					.getAgenda(systemCore.actualOnto[Settings.getuserpos(user)].actualAgenda[Settings
							.getuserpos(user)].getLocalName());
			int userpos = Settings.getuserpos(user);
			AgendaPrio.setAgendaPrio(muh,
					systemCore.actualOnto[userpos].workSpace[userpos],
					systemCore.actualOnto[userpos].factory, systemCore, prio);
			systemCore.actualOnto[Settings.getuserpos(user)].write();
			return buildOutput(whereAmI, "reduced", "Priorität wurde gesenkt",
					user);
		}
		if (execute.equalsIgnoreCase("back")) {
			// Work in progress
			// TODO implement generic back functionality ?
		}
		return buildError(whereAmI, "falscher oder fehlender Befehl");
	}

	/**
	 * one of the build functions that generate output. enables an ontology with
	 * the specified name for the user
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param enableOnto
	 *            the name of the ontology
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildEnable(String whereAmI, String enableOnto,
			String user) {
		if (enableOnto == null)
			return buildError(whereAmI, "No ontology specified.", user);
		enableOnto(enableOnto, user);
		return buildOutput(whereAmI, "enable", "Ontology has been enabled.",
				user);
	}

	/**
	 * enables an ontology with the specified name for the user
	 * 
	 * @param enableOnto
	 *            the name of the ontology
	 * @param user
	 *            the name of the user
	 */
	public void enableOnto(String enableOnto, String user) {
		OwlSpeakOntology onto = systemCore.findOntology(enableOnto);
		if (onto != null) {
			onto.enable(Settings.getuserpos(user));
			settings.getusersetting(user).userOntoSettings[settings
					.getontopos(enableOnto)].ontostatus = "enabled";
			if (settings.getusersetting(user).autosave == 1)
				settings.write();
		}
	}

	/**
	 * one of the build functions that generate output. disables an ontology
	 * with the specified name for the user
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param disableOnto
	 *            the name of the ontology
	 */
	public OwlDocument buildDisable(String whereAmI, String disableOnto,
			String user) {
		if (disableOnto == null)
			return buildError(whereAmI, "No ontology specified.", user);
		disableOnto(disableOnto, user);
		return buildOutput(whereAmI, "disable", "Tried to disable Ontology.",
				user);
	}

	/**
	 * disables an ontology with the specified name for the user
	 * 
	 * @param user
	 *            the name of the user
	 * @param disableOnto
	 *            the name of the ontology
	 */
	public void disableOnto(String disableOnto, String user) {
		OwlSpeakOntology onto = systemCore.findOntology(disableOnto);
		if (onto != null) {
			onto.disable(Settings.getuserpos(user));
			settings.getusersetting(user).userOntoSettings[settings
					.getontopos(disableOnto)].ontostatus = "disabled";
			if (settings.getusersetting(user).autosave == 1)
				settings.write();
		}
	}

	/**
	 * one of the build functions that generate output. generates an output
	 * document that contains an unspecified errormessage
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildError(String whereAmI, String user) {
		return buildError(whereAmI, " not specified", user);
	}

	/**
	 * one of the build functions that generate output. generates an output
	 * document that contains an errormessage with the specified Exception
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param ex
	 *            the Exception that should be used for the errormessage
	 */
	public OwlDocument buildError(String whereAmI, Exception ex, String user) {
		return buildError(whereAmI, ex.toString(), user);
	}

	/**
	 * one of the build functions that generate output. generates an output
	 * document that contains an errormessage with the specified String
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 * @param Error
	 *            the message that should be included
	 */
	public OwlDocument buildError(String whereAmI, String Error, String user) {
		return this.buildRequest(whereAmI, user, null);
	}

	/**
	 * one of the build functions that generate output. generates an output
	 * document that contains the ontologies and their status togehter with the
	 * actualAgendas of all users
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildListOntos(String whereAmI, String user) {
		return buildOutput(whereAmI, "Info", "Ontos:   " + listOntos(), user);
	}

	/**
	 * provides a ";"-seperated list of all available Ontologies and their
	 * status together with the actualAgendas of all users
	 */
	public String listOntos() {
		String Ausgabe = " ";
		for (int i = 0; i < systemCore.ontologies.size(); i++) {
			for (int j = 0; j < Settings.usersetting.length; j++) {
				String actualAgenda = "";
				if ((systemCore.ontologies.get(i).actualAgenda[j] != null)
						&& (systemCore.ontologies.get(i).isenabled[j])) {
					actualAgenda = (systemCore.ontologies.get(i).actualAgenda[j])
							.getLocalName();
				}
				Ausgabe = Ausgabe.concat(systemCore.ontologies.get(i).Name
						+ ":" + Settings.usernames[j] + ":"
						+ systemCore.ontologies.get(i).isenabled[j] + ":"
						+ actualAgenda + " ; ");
				actualAgenda = "";
			}
		}
		return Ausgabe;
	}

	/**
	 * links to build request. only existent because of compatibility reasons.
	 */
	public OwlDocument buildRequest(String whereAmI, String user,
			String noInputCounter) {
		return buildRequest(whereAmI, user, noInputCounter, null, false);
	}

	/**
	 * Determines the next system action according to policy and generates an
	 * OwlDocument.<br />
	 * <br />
	 * Parameter <code>pureRequest</code> should be true if this method is
	 * called directly and not from another build method, e.g., buildWork. It
	 * controls whether requestMoves are executed.
	 * 
	 * @param whereAmI
	 *            the net address of the servlet
	 * @param user
	 *            the user name
	 * @param noInputCounter
	 *            counter which indicates the number of noinputs
	 * @param userAct
	 *            the last user action
	 * @param pureRequest
	 *            should be true if this method is called directly and not from
	 *            another build method, e.g., buildWork. Controls
	 * @return a new OwlDocument representing the new system action
	 */
	public OwlDocument buildRequest(String whereAmI, String user,
			String noInputCounter, UserAction userAct, boolean pureRequest) {
		if (settings.getusersetting(user).sleep == 0) {
			Move tempMove = null;
			CoreMove tempCoreMove;

			Vector<CoreMove> utterance = new Vector<CoreMove>();
			Vector<CoreMove> grammar = new Vector<CoreMove>();

			OwlSpeakOntology currentOnto = systemCore.actualOnto[Settings
					.getuserpos(user)];

			// is request move needed at all?
			// code to process non-grammar moves in a request (e.g., time-out)
			if (currentOnto != null && pureRequest) {

				SystemState
						.getSystemState(systemCore, currentOnto, user)
						.processRequestMoves(
								currentOnto.actualAgenda[Settings
										.getuserpos(user)],
								currentOnto, user);

				currentOnto.write();
				currentOnto.update();
			}

			// Here B Dragons
			for (int i = 0; i < systemCore.ontologies.size(); i++) {
				if (systemCore.ontologies.get(i).factory == null) {
					System.out.println("Ontology not loaded yet");
					if (systemCore.ontologies.get(i).isenabled(
							Settings.getuserpos(user)))
						systemCore.ontologies.get(i).update();
					System.out.println("Ontology "
							+ systemCore.ontologies.get(i).Name + " loaded!");

				}
			}
			
//ATTENTION!!						
			//This was working for HeuristicPolicy: 
			systemCore.policy(user, userAct);
			
			change = false;

			OwlSpeakOntology actualOnto = systemCore.actualOnto[Settings
					.getuserpos(user)];
			Agenda actualAgenda = actualOnto.actualAgenda[Settings
					.getuserpos(user)];
			try {
				if (!currentOnto.Name.equals(actualOnto.Name)) {
					change = true;
				}
				currentOnto = actualOnto;
			} catch (NullPointerException n) {
				System.out.println("No actual ontology.");
			}

//			int reward = 0;
//			if (actualOnto.partitionDistributions[Settings.getuserpos(user)] != null) {
//				System.out.println(actualOnto.partitionDistributions[Settings
//						.getuserpos(user)]);

				// Modifications
//				if (userAct != null) {

					// systemCore.optimizationInfo.addSummaryBelief(new
					// SummaryBeliefNonpersisnent(actualOnto.partitionDistributions[Settings
					// .getuserpos(user)], userAct.getMove(),
					// actualOnto,actualAgenda.getSummaryAgenda()));
					//
					// // TODO where to add the reward? at execution or after
					// user input?
					//
					// reward = systemCore.getRewardFromAgenda(actualAgenda,
					// user);
					// systemCore.optimizationInfo.addReward(reward);

//				}
				//
				
//			}
//			} else
//				System.out.println(actualOnto.beliefSpace[Settings
//						.getuserpos(user)]);

			Iterator<Move> neuAgenda = actualAgenda.getHas().iterator();
			while (neuAgenda.hasNext()) {
				tempMove = neuAgenda.next();

				tempCoreMove = new CoreMove(tempMove, actualAgenda, actualOnto,
						systemCore);
				if (tempMove.hasUtterance() && !tempMove.hasGrammar()) {
					utterance.add(tempCoreMove);
				} else if (tempMove.hasGrammar()) {
					grammar.add(tempCoreMove);

				}
			}

			Collections.sort(utterance, new Comparator<CoreMove>() {
				public int compare(CoreMove cm2, CoreMove cm1) {
					return Integer.valueOf(cm1.move.getPriority()).compareTo(
							Integer.valueOf(cm2.move.getPriority()));
				}
			});
			if (!grammar.isEmpty()) {
				systemCore.respawnManager[Settings.getuserpos(user)]
						.updateRespawns(systemCore, user);
				Iterator<CoreMove> respawns = systemCore.respawnManager[Settings
						.getuserpos(user)].getRespawns(false, user).iterator();
				while (respawns.hasNext()) {
					grammar.add(respawns.next());
				}
			}

			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SS");
			System.out.println(formatter.format(date)
					+ " Request: "
					+ actualAgenda.getLocalName()
					// + " (Reward/Return: "
					// + reward + "/"
					// + systemCore.optimizationInfo.getReturnValue()
					// + ")" +
					+ "   time " + systemCore.actualtime + " doctype "
					+ settings.getusersetting(user).docType);

			System.out
					.println("################# new turn ###################");
			if (actualOnto.ontoStateVariant == SystemStateVariant.DISTRIBUTION) {
				System.out.println(actualOnto.factory.getBeliefSpace(user+"BeliefspaceGeneric"));
				System.out.println(actualOnto.partitionDistributions[Settings.getuserpos(user)].getTopPartition());
				//System.out.println(actualOnto.partitionDistributions[Settings.getuserpos(user)]);
			}
			else
				System.out
					.println(actualOnto.beliefSpace[Settings.getuserpos(user)]);

			OwlDocument doc = getDocByType(
					settings.getusersetting(user).docType,
					settings.getusersetting(user).grammarType).fillDocument(
					utterance, grammar, actualAgenda, whereAmI, user,
					noInputCounter);
			
			return doc;

		} else {
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SS");
			System.out.println(formatter.format(date) + " Sleeping");
			return getDocByType(settings.getusersetting(user).docType,
					settings.getusersetting(user).grammarType).generateSleep(
					whereAmI, user, noInputCounter);
		}
	}

	/**
	 * One of the build functions that generate output in POMDP mode. One of the
	 * two main functions request and work that are used for the dialogue
	 * progress in POMDP mode. Called only if DocType =
	 * {@link Usersetting#HIS_VXML_DOC}. Processes the input moves and updates
	 * the partition distribution.
	 * 
	 * @param whereAmI
	 *            the netadress of the servlet
	 * @param agenda
	 *            the name of the agenda that should be processed
	 * @param user
	 *            the name of the user
	 * @param resultList
	 *            the N-best list of move that are said by the user
	 * @param resultLen
	 *            the length of the resultList
	 * @param confidences
	 *            a list of confidence scores according to the resultList
	 * @param speaklist
	 *            the input of of the user as a string array
	 * @return {link
	 *         {@link ServletEngine#buildRequest(String, String, String, UserAction, boolean)}
	 *         or error document
	 * 
	 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
	 */
	public OwlDocument buildWork(String whereAmI, String agenda, String user,
			String[] resultList, String resultLen, String[] confidences,
			String[] speakList) {
		IUserAction userAct = null;
		try {
			userAct = buildWorkAux(whereAmI, agenda, user, resultList, resultLen, confidences, speakList);
			//return buildWork(whereAmI, agenda, user, resultList, resultLen, confidences, speaklist, true);
		} catch (DocumentBuildException e) {
			return buildError(whereAmI, e.getMsg(), user);
		} catch (ConflictException e) {
			return buildSolveConflict(whereAmI, speakList[0], user);
		}
		
		if (userAct == null)
			return buildRequest(whereAmI, user, null, null, false);
		
		if (userAct instanceof UserAction) {
			return buildRequest(whereAmI, user, null, (UserAction)userAct, false);
		}
		
		if (userAct instanceof CommandUserAction) {
			return buildExecute(whereAmI,
					((CommandUserAction) userAct).getSysCommand(), user);
		}
		
		return buildError(whereAmI, user);
	}
		
		
	public IUserAction buildWorkAux(String whereAmI, String agenda, String user,
				String[] resultList, String resultLen, String[] confidences,
				String[] speaklist) throws DocumentBuildException, ConflictException {

		// TODO print status information ?
		// Date dan = new Date();
		// SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss,SS");
		// System.out.println(formatter.format(dan) + " Work: Agenda " + agenda
		// + ", Speak '" + speak + "', Move " + move + ", time "
		// + SystemCore.actualtime);

		OwlSpeakOntology actualOnto = null;
		Agenda actualAgenda = null;
		String prefix = null;
		String suffix = null;
		OwlSpeakOntology moveOnto = null;

		// identify last active ontology of user
		try {
			actualOnto = systemCore.actualOnto[Settings.getuserpos(user)];
			// actualOnto.update();
		} catch (NullPointerException n) {
			throw new DocumentBuildException("Tried to call work without previous request. Redirect to request.");
//			return buildError(
//					whereAmI,
//					"Tried to call work without previous request. Redirect to request.",
//					user);
		}

		// agenda specified?
		if (agenda == null) {
			throw new DocumentBuildException("kein Agendaparameter");
		}

		// if agenda!=actualOnto-Name something went wrong.
		// TODO what if actualOnto is null?
		if (!(agenda.equals(actualOnto.actualAgenda[Settings.getuserpos(user)]
				.getLocalName())))
			throw new DocumentBuildException(user);
		else
			// identify active agenda in ontology
			actualAgenda = actualOnto.actualAgenda[Settings.getuserpos(user)];

		// check if there is at least one user move as input
		boolean thereIsMoveInput = false;
		for (int i = 0; i < resultList.length; i++) {
			if (resultList[i] != null) {
				thereIsMoveInput = true;
				break;
			}
		}

		/*
		 * TODO load context here; create different sysacts depending on
		 * agenda/onto/context how does the vxml document look like after
		 * respawn? better question: why is final agenda of heating not deleted
		 * from workspace?
		 */

		// create system action
		GenericSystemAction sysAct = GenericSystemAction.createSystemAction(
				actualAgenda, systemCore);
		/*
		 * retrieve the equals corresponding to performed system action.
		 * necessary for determining userActionLikelihood when comparing
		 * confirmed value with partition content. Works always because for
		 * trained policy, also the most likely partition is used for filling
		 * the parameters of the selected agends.
		 */
		if (sysAct.hasField()) {
			BeliefSpace bs = null;
			assert(Policy.sumAgenad2AgendaNextBeliefSpaceCounter <= 2);
			if (actualOnto.hasPartitionDistribution()
					&& Policy.sumAgenad2AgendaNextBeliefSpaceCounter == 2)
				bs = (Partition) actualOnto.partitionDistributions[Settings
						.getuserpos(user)].getSecondTopPartition();
			else
				bs = actualOnto.beliefSpace[Settings.getuserpos(user)];
			if (bs instanceof Partition) {
				for (String fieldName : sysAct.getFields()) {
					Field f = ((Partition) bs).getFields().get(fieldName);
					if (f != null && f.getEquals() != null) {
						sysAct.setValue(fieldName, f.getEquals());
					}
				}
			}

		}

		SystemState state = SystemState.getSystemState(systemCore, actualOnto,
				user);

		// No Errors, no Move -> processing agenda
		if (!thereIsMoveInput) {
			/*
			 * When does this happen? -> e.g., in the beginning (greeting agenda
			 * without user response)
			 */
			buildNoMoveInput(actualOnto, state, sysAct, user, whereAmI);
			return null;
			//return buildNoMoveInput(actualOnto, state, sysAct, user, whereAmI);

			// actualOnto.update();
			// prefix = actualOnto.Name;
			// state.processAgendaExceptGrammar(sysAct, actualOnto, null, user);
			// systemCore.processAgendaExceptGrammar(
			// actualOnto.actualAgenda[Settings.getuserpos(user)],
			// actualOnto.beliefSpace[Settings.getuserpos(user)],
			// actualOnto.factory, prefix, user, null);
			// systemCore.tidyUpAgenda(
			// actualOnto.actualAgenda[Settings.getuserpos(user)],
			// actualOnto.workSpace[Settings.getuserpos(user)],
			// actualOnto.factory, false);
			// actualOnto.write();
			// actualOnto.update();
			// return buildRequest(whereAmI, user, null, null, false);
		}

		SortedMap<Move, Double> moves = new SortedMap<Move, Double>();
		HashMap<Move, String> speaks = new HashMap<Move, String>();

		// Collection<Move> agendaMoves = actualAgenda.getHas();

		// summarize n-best-list to take account of multiple entries belonging
		// to the same move
		for (int i = 0; i < Integer.parseInt(resultLen); i++) {

			// only necessary if move name does not yet have ontology prefix as
			// it shoudl regularly have
			if (resultList[i].startsWith("external"))
				resultList[i] = addOntoPrefixToExternalMove(resultList[i],
						actualAgenda, actualOnto);

			prefix = resultList[i].substring(0,
					resultList[i].indexOf("_a1n1d_"));
			suffix = resultList[i].substring(
					(resultList[i].indexOf("_a1n1d_") + 7),
					resultList[i].length());
			moveOnto = systemCore.findOntology(prefix);
			Move move = moveOnto.factory.getMove(suffix);

			// only moves which belong to the given agenda are included
			// if (agendaMoves.contains(move)) {
			if (moves.containsKey(move))
				moves.put(move,
						moves.get(move) + Double.parseDouble(confidences[i]));
			else
				moves.put(move, Double.parseDouble(confidences[i]));

			if (!speaks.containsKey(move)
					|| (moves.get(move) < Double.parseDouble(confidences[i]))) {
				speaks.put(move, speaklist[i]);
			}
			// }

		}

		int numUniqueResults = moves.size();

		if (numUniqueResults == 0) {

			System.err.println("No move belongs to agenda "
					+ actualAgenda.getLocalName());
			buildNoMoveInput(actualOnto, state, sysAct, user, whereAmI);
			return null;
		}

		// creating user actions and system action
		UserAction[] userActions = new UserAction[numUniqueResults];
		double[] confidenceScores = new double[numUniqueResults];
		double confidenceTotal = 0.0;
		for (int i = 0; i < numUniqueResults; i++) {
			Map.Entry<Move, Double> e = moves.pollLastEntry();
			userActions[i] = UserAction
					.creatNewUserAction(e.getKey(), speaks.get(e.getKey()),
							actualOnto, UserActionType.IG, user);
			confidenceScores[i] = e.getValue();
			confidenceTotal += confidenceScores[i];

		}

		// normalizing confidences
		if (confidenceTotal > 1.0) {
			for (int i = 0; i < numUniqueResults; i++) {
				confidenceScores[i] /= confidenceTotal;
			}
			System.out.println("confidences normalized");
			for (int i = 0; i < numUniqueResults; i++) {
				confidenceTotal += confidenceScores[i];
				System.out.println("\t" + confidenceScores[i] + ": "
						+ userActions[i]);
			}
		}
		UserAction.setSystemAction(userActions, sysAct);

		// printout of input
		System.out.println("Input (" + numUniqueResults + "):");
		for (int i = 0; i < numUniqueResults; i++) {
			confidenceTotal += confidenceScores[i];
			System.out.println("\t" + confidenceScores[i] + ": "
					+ userActions[i]);
		}

		// TODO handling of respawns for others than regular documents
		// respawn handling

		// only top n-best-list-entry regarded
		CoreMove tempCoreMove = systemCore.respawnManager[Settings
				.getuserpos(user)].matchRespawn(userActions[0].getMove(),
				userActions[0].getMove().onto);

		// Is the Move a respawn? If so, process only the move
		if (tempCoreMove != null) {
			OwlSpeakOntology respawnOnto = tempCoreMove.onto;
			String ontologyName = respawnOnto.Name;

			String speak = speaks.get(tempCoreMove.move);

			SystemState respawnState = SystemState.getSystemState(systemCore,
					respawnOnto, user);

			if (tempCoreMove.conflict.contains(speak)) {
				throw new ConflictException();
			}
			if (ontologyName.equals("system")) {

				for (int i = 0; i < Settings.sysCommands.length; i++) {

					Iterator<Semantic> moveSem = tempCoreMove.move
							.getSemantic().iterator();
					while (moveSem.hasNext()) {
						if (moveSem
								.next()
								.getLocalName()
								.toLowerCase()
								.startsWith(
										Settings.sysCommands[i].toLowerCase())) {
							IUserAction commandAction = new CommandUserAction(Settings.sysCommands[i].toLowerCase());
							return commandAction;
//							return buildExecute(whereAmI,
//									Settings.sysCommands[i].toLowerCase(), user);
						}
					}
				}
			}

			respawnState.updateStateSingleAction(userActions[0],
					confidenceScores[0], null, tempCoreMove.onto, user);

			systemCore.tidyUpAgenda(
					respawnOnto.actualAgenda[Settings.getuserpos(user)],
					respawnOnto.workSpace[Settings.getuserpos(user)],
					respawnOnto.factory, true);

			tempCoreMove.onto.write();
			tempCoreMove.onto.update();
			
			IUserAction userAct = userActions[0];
			return userAct;
//			return buildRequest(whereAmI, user, null, userActions[0], false);
		}

		if ((!moveOnto.equals(actualOnto))) {
			System.out
					.println("Move is not a Respawn, Ontologies are different.");
			return null;
//			return buildRequest(whereAmI, user, null); // TODO user act??
		}
		if (moveOnto != actualOnto) {
			System.out.println("different objects for same ontology");
		}

		// ------------ state specific processing starts here processing starts
		// here

		state.updateState(userActions, confidenceScores, sysAct, actualOnto,
				user);

		// ------------ state specific processing ends here

		systemCore.tidyUpAgenda(
				actualOnto.actualAgenda[Settings.getuserpos(user)],
				actualOnto.workSpace[Settings.getuserpos(user)],
				actualOnto.factory, false);

		actualOnto.write();
		actualOnto.update();

		// reasonable assumption that first user action is the most probable one
		// however, this has not been checked yet.
		IUserAction userAct = userActions[0];
		return userAct;
//		return buildRequest(whereAmI, user, null, userActions[0], false);
	}

	/**
	 * one of the build functions that generate output. return a document with
	 * virtual moves, used for conflict resolution
	 * 
	 * @param whereAmI
	 *            the net address of the servlet
	 * @param user
	 *            the name of the user
	 * @param conflict
	 *            the input that caused the conflict
	 */
	public OwlDocument buildSolveConflict(String whereAmI, String conflict,
			String user) {
		Vector<String[]> grammar = new Vector<String[]>();

		OwlSpeakOntology sys = systemCore.findOntology("system");
		Agenda conflictAgenda = sys.factory.getAgenda("conflict");
		String utterance = "";
		Iterator<Move> conflictIt = conflictAgenda.getHas().iterator();
		while (conflictIt.hasNext()) {
			Iterator<OWLLiteral> uttIter = conflictIt.next().getUtterance()
					.getUtteranceString().iterator();
			while (uttIter.hasNext()) {
				utterance = utterance + " " + (uttIter.next().getLiteral());
			}
		}
		Agenda orAgenda = sys.factory.getAgenda("or");
		String orUtterance = "";
		Iterator<Move> orIt = orAgenda.getHas().iterator();
		while (orIt.hasNext()) {
			Iterator<OWLLiteral> orIter = orIt.next().getUtterance()
					.getUtteranceString().iterator();
			while (orIter.hasNext()) {
				orUtterance = orUtterance + " " + (orIter.next().getLiteral());
			}
		}
		orUtterance.trim();
		utterance.trim();
		Vector<CoreMove> conflicts = systemCore.respawnManager[Settings
				.getuserpos(user)].getConflicts(conflict);
		Iterator<CoreMove> confIter = conflicts.iterator();
		int i = 0;
		while (confIter.hasNext()) {
			CoreMove conf = confIter.next();
			String[] temp = new String[3];
			temp[0] = conf.onto.Name;
			temp[1] = conf.move.getLocalName();
			temp[2] = conf.onto.getOntoDomainName();
			grammar.add(temp);
			if (i == 0) {
				utterance = utterance + " " + conf.onto.getOntoDomainName();
			} else if (i == (conflicts.size() - 1)) {
				utterance = utterance + " " + orUtterance + " ";
				utterance = utterance + " " + conf.onto.getOntoDomainName()
						+ "?";
			} else if (i < (conflicts.size() - 1)) {
				utterance = utterance + ", " + conf.onto.getOntoDomainName();
			}
			i++;
		}
		change = false;
		return ((getDocByType(settings.getusersetting(user).docType,
				settings.getusersetting(user).grammarType)))
				.buildSolveConflict(
						utterance,
						grammar,
						systemCore.actualOnto[Settings.getuserpos(user)].actualAgenda[Settings
								.getuserpos(user)], whereAmI, user);
	}

	/**
	 * one of the build functions that generate output. return a document with
	 * virtual moves, used for conflict resolution
	 * 
	 * @param whereAmI
	 *            the net address of the servlet
	 * @param utterance
	 *            the strings the system should say
	 * @param grammar
	 *            Vector with String[] containing {"OntoName", "MoveName",
	 *            "Grammar"}
	 * @param user
	 *            the name of the user
	 */
	public OwlDocument buildPluginRequest(String whereAmI, String utterance,
			Vector<String[]> grammar, String user) {
		/*
		 * String[] temp = {"OntoName", "MoveName", "Grammar"};
		 * grammar.add(temp); OwlDocument VDoc =
		 * owlEngine.buildPluginRequest(whereAmI, utterance, grammar, user);
		 */
		return ((getDocByType(settings.getusersetting(user).docType,
				settings.getusersetting(user).grammarType)))
				.buildSolveConflict(
						utterance,
						grammar,
						systemCore.actualOnto[Settings.getuserpos(user)].actualAgenda[Settings
								.getuserpos(user)], whereAmI, user);
	}

	/**
	 * one of the build functions that generate output. return a document with
	 * virtual moves, used for conflict resolution
	 * 
	 * @param result
	 *            a vector with the Results
	 * @param whereAmI
	 * @param user
	 */
	public OwlDocument buildPluginAlternatives(Vector<Result> result,
			String whereAmI, String user) {
		return ((getDocByType(settings.getusersetting(user).docType,
				settings.getusersetting(user).grammarType)))
				.buildPluginAlternatives(result, systemCore.actualOnto[Settings
						.getuserpos(user)].actualAgenda[Settings
						.getuserpos(user)], whereAmI, user);
	}

	public void buildNoMoveInput(OwlSpeakOntology actualOnto,
			SystemState state, GenericSystemAction sysAct, String user,
			String whereAmI) {
		/*
		 * When does this happen? -> e.g., in the beginning (greeting agenda
		 * without user response)
		 */
		actualOnto.update();
		String prefix = actualOnto.Name;
		state.processAgendaExceptGrammar(sysAct, actualOnto, null, user);
		systemCore.processAgendaExceptGrammar(
				actualOnto.actualAgenda[Settings.getuserpos(user)],
				actualOnto.beliefSpace[Settings.getuserpos(user)],
				actualOnto.factory, prefix, user, null);
		systemCore.tidyUpAgenda(
				actualOnto.actualAgenda[Settings.getuserpos(user)],
				actualOnto.workSpace[Settings.getuserpos(user)],
				actualOnto.factory, false);
		actualOnto.write();
		actualOnto.update();
//		return buildRequest(whereAmI, user, null, null, false);
	}

	/**
	 * returns a document depending on the current view that should be
	 * generated.
	 * 
	 * @param docType
	 *            the Integer representing the type of view to be generated
	 * @param grammarType
	 *            the Integer representing the type of grammar to be generated.
	 * 
	 *            TODO remove IGrammar from constructor and method docType
	 */
	public OwlDocument getDocByType(int docType, int grammarType) {
		switch (docType) {
		case Usersetting.VXML_DOC:
			return new VoiceDocument(getGrammarByType(grammarType));
		case Usersetting.HTML_DOC:
			return new HtmlVoiceDocument();
		case Usersetting.SALT_SOC:
			return new HtmlSaltDocument(change);
		case Usersetting.ENHANCED_VXM_DOC:
			return new EnhancedVoiceDocument(getGrammarByType(grammarType));
		case Usersetting.POMDP_VXML_DOC:
			return new VoiceDocumentPOMDP(getGrammarByType(grammarType));
			// case Usersetting.HIS_VXML_DOC:
			// return new VoiceDocumentPOMDP(grammarType(grammarType));
		case Usersetting.KRISTINA_DOC:
			return new KristinaDocument(getGrammarByType(grammarType));
		default:
			return null;
		}
	}

	/**
	 * returns an IGrammar object depending on the grammar type which should be
	 * generated.
	 * 
	 * @param grammarType
	 *            the Integer representing the type of grammar to be generated
	 */
	public static IGrammar getGrammarByType(int grammarType) {

		IGrammar grammar = null;
		switch (grammarType) {
		case IGrammar.GSL:
			grammar = new GrammarGSL();
			break;

		case IGrammar.JSGF:
			grammar = new GrammarJSGF();
			break;

		case IGrammar.GRXML:
			grammar = new GrammarSRGS();
			break;

		case IGrammar.JSGFasFile:
			grammar = new GrammarJSGFasFile();
			break;
			
		case IGrammar.KristinaGrammar:
			grammar = new KristinaGrammar();
			break;
			
		default:
			grammar = null;
			break;
		}
		return grammar;
	}

	public String addOntoPrefixToExternalMove(String move, Agenda agenda,
			OwlSpeakOntology onto) {
		String newMove = null;
		for (Move m : agenda.getHas()) {
			if (m.hasVariableOperator()) {
				String varOp = m.getVariableOperator();
				if (varOp.matches(".*(:" + move + ":[0-9]+:).*")) {
					newMove = onto.Name + "_a1n1d_" + m.getLocalName();
					break;
				}
			}
		}
		return newMove;
	}

	public static void main(String[] args) {

		String s = "SET(dest_var=:destination_test:;dur_var=:duration_test:)";
		// String grammar = "duration";
		String variable = "dur_var";

		String pattern = "(.*" + variable + "=)(:[a-zA-Z0-9_]+:)(.*)";

		System.out.println(s);
		System.out.println(pattern);
		System.out.println(s.matches(pattern));
		System.out.println(s.replaceAll(pattern, "$1speak$3"));

		//
		// Matcher m =
		// Pattern.compile(".*:external_[a-zA-Z0-9]+_mv:([0-9]+):.*").matcher("SET(dest_var=:external_time_mv:3600:)");
		// String s2 = m.find() ? m.group(1) : "";
		// System.out.println(s2);
	}
	
	public class DocumentBuildException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7578152763568498756L;
		
		private String message;
		
		public DocumentBuildException (String message) {
			this.setMsg(message);
		}

		/**
		 * @return the message
		 */
		public String getMsg() {
			return message;
		}

		/**
		 * @param message the message to set
		 */
		public void setMsg(String message) {
			this.message = message;
		}
		
	}
	
	public class ConflictException extends Exception {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5228071677692273418L;
		
	}
}
