package owlSpeak.imports;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Agenda;
import owlSpeak.Grammar;
import owlSpeak.Move;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.SemanticGroup;
import owlSpeak.SummaryAgenda;
import owlSpeak.Utterance;
import owlSpeak.Variable;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;

public class IDACONao {
	static int no_of_tools = 0;
	private static Pattern toolPattern = Pattern.compile("(\\()*(\\[\\w+\\])*\\w+\\s*(\\[\\w+\\s*\\])*\\s*(\\|\\s*\\w+\\s*\\w*)*(\\))*"); //regular expression for recognizing tool requests
	
	
	//creates grammar for closing IDACO
	public static Grammar grammarExit (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Exit");
		g.addGrammarString(factory.factory.getOWLLiteral("IDACO beenden | Lass uns die Unterhaltung beenden","de"));
		return g;
	}
	
	
	//creates grammar for affirming
	public static Grammar grammarAffirm (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Affirm");
		g.addGrammarString(factory.factory.getOWLLiteral("Ja | gerne | Genau | richtig | korrekt | in Ordnung | okay | gut | super | toll | klasse | spitze | nicht schlecht","de"));
		return g;
	}


	//creates grammar for denying
	public static Grammar grammarDeny (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Deny");
		g.addGrammarString(factory.factory.getOWLLiteral("falsch | nicht richtig | nicht korrekt | Nein | Nö | Ne | nicht | Lass (es | das) | falsch verstanden | nicht [richtig] verstanden","de"));
		return g;
	}


	//creates the grammars for the user moves to ask for any tool
	public static Grammar grammarTool (OSFactory factory, String longName, String shortName) {		
		Grammar g = factory.createGrammar("gr_Tool_" + shortName);
		g.addGrammarString(factory.factory.getOWLLiteral(longName,"de"));
		return g;
	}


	//creates grammar referring to last tool used
	public static Grammar grammarOneMore (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_OneMore");
		g.addGrammarString(factory.factory.getOWLLiteral("[Noch | Nochmal] (ein | eins | eine | einen) [mehr]","de"));
		return g;
	}


	//creates grammar for correcting tool count to 0
	public static Grammar grammarCorrectCntTo0 (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_CorrectCntTo0");
		g.addGrammarString(factory.factory.getOWLLiteral("keins | keine | keinen","de"));
		return g;
	}


	//creates grammar for correcting tool count to a given number n
	public static Grammar grammarCorrectCntToN (OSFactory factory, int n) {
		Grammar g = factory.createGrammar("gr_CorrectCntTo" + n);
		g.addGrammarString(factory.factory.getOWLLiteral("[Erst | Schon]" + n,"de"));
		return g;
	}
	
	
	//creates grammar for setting device parameter to a given number n
	public static Grammar grammarSetToN (OSFactory factory, int n) {
		Grammar g = factory.createGrammar("gr_SetTo" + n);
		g.addGrammarString(factory.factory.getOWLLiteral(n + "[Zentimeter | Grad | mmHg | Prozent]","de"));
		return g;
	}


	//creates grammar for asking for current settings of the thermoflator
	public static Grammar grammarSettingsThermoflator (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_SettingsThermoflator");
		g.addGrammarString(factory.factory.getOWLLiteral("die (aktuellen | momentanen) Einstellungen für den (Thermoflator | [Gas]Insufflator)","de"));
		return g;
	}


	//creates grammar for asking for current settings of the room light
	public static Grammar grammarSettingsRoomLight (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_SettingsRoomLight");
		g.addGrammarString(factory.factory.getOWLLiteral("die (aktuellen | momentanen) Einstellungen für das [Raum]Licht","de"));
		return g;
	}


	//creates grammar for asking for current settings of the endomat
	public static Grammar grammarSettingsEndomat (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_SettingsEndomat");
		g.addGrammarString(factory.factory.getOWLLiteral("die (aktuellen | momentanen) Einstellungen für den Endomat","de"));
		return g;
	}


	//creates grammar for asking for current settings of the table
	public static Grammar grammarSettingsTable (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_SettingsTable");
		g.addGrammarString(factory.factory.getOWLLiteral("die (aktuellen | momentanen) Einstellungen für den [OP]Tisch","de"));
		return g;
	}


	//creates grammar for asking for current presets of the thermoflator
	public static Grammar grammarPresetsThermoflator (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_PresetsThermoflator");
		g.addGrammarString(factory.factory.getOWLLiteral("(die | meine) (aktuellen | momentanen) Voreinstellungen für den (Thermoflator | [Gas]Insufflator)","de"));
		return g;
	}


	//creates grammar for asking for current presets of the room light
	public static Grammar grammarPresetsRoomLight (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_PresetsRoomLight");
		g.addGrammarString(factory.factory.getOWLLiteral("(die | meine) (aktuellen | momentanen) Voreinstellungen für das [Raum]Licht","de"));
		return g;
	}


	//creates grammar for asking for current presets of the endomat
	public static Grammar grammarPresetsEndomat (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_PresetsEndomat");
		g.addGrammarString(factory.factory.getOWLLiteral("(die | meine) (aktuellen | momentanen) Voreinstellungen für den Endomat","de"));
		return g;
	}


	//creates grammar for asking for current presets of the table
	public static Grammar grammarPresetsTable (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_PresetsTable");
		g.addGrammarString(factory.factory.getOWLLiteral("(die | meine) (aktuellen | momentanen) Voreinstellungen für den [OP]Tisch","de"));
		return g;
	}


	//creates grammar for asking to change thermoflator presets
	public static Grammar grammarChangePresetsThermoflator (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_ChangePresetsThermoflator");
		g.addGrammarString(factory.factory.getOWLLiteral("Voreinstellungen für den (Thermoflator | [Gas]Insufflator) (ändern | neu einstellen | erneuern)","de"));
		return g;
	}


	//creates grammar for asking to change room light presets
	public static Grammar grammarChangePresetsRoomLight (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_ChangePresetsRoomLight");
		g.addGrammarString(factory.factory.getOWLLiteral("Voreinstellungen für das [Raum]Licht (ändern | neu einstellen | erneuern)","de"));
		return g;
	}


	//creates grammar for asking to change endomat presets
	public static Grammar grammarChangePresetsEndomat (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_ChangePresetsEndomat");
		g.addGrammarString(factory.factory.getOWLLiteral("Voreinstellungen für den Endomat (ändern | neu einstellen | erneuern)","de"));
		return g;
	}


	//creates grammar for asking to change table presets
	public static Grammar grammarChangePresetsTable (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_ChangePresetsTable");
		g.addGrammarString(factory.factory.getOWLLiteral("Voreinstellungen für den [OP]Tisch (ändern | neu einstellen | erneuern)","de"));
		return g;
	}
	
	
	//creates grammar for asking to stop gas flow again
	public static Grammar grammarUndoStartGas (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoStartGas");
		g.addGrammarString(factory.factory.getOWLLiteral("Rückgängig | (Gas | Insufflator | Thermoflator) [schnell | sofort | gleich] [wieder] (aus | ausschalten | ausmachen | stoppen)","de"));
		return g;
	}
	
	
	//creates grammar for asking to slow down gas flow again
	public static Grammar grammarUndoMaxGas (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoMaxGas");
		g.addGrammarString(factory.factory.getOWLLiteral("Rückgängig | (Gas[fluss] | Insufflator | Thermoflator) [schnell | sofort | gleich] [wieder] ([he]runter[schalten | machen] | langsamer | weniger)","de"));
		return g;
	}
	
	
	//creates grammar for asking to turn room light on again
	public static Grammar grammarUndoRoomLightOff (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoRoomLightOff");
		g.addGrammarString(factory.factory.getOWLLiteral("[Raum]Licht [schnell | sofort | gleich] [wieder] (ein | an)","de"));
		return g;
	}
	
	
	//creates grammar for asking to turn room light off again
	public static Grammar grammarUndoRoomLightOn (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoRoomLightOn");
		g.addGrammarString(factory.factory.getOWLLiteral("Rückgängig | [Raum]Licht [schnell | sofort | gleich] [wieder] aus","de"));
		return g;
	}
	
	
	//creates grammar for asking to turn table back and turn light on again
	public static Grammar grammarUndoTableRevTrendLightOff (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoTableRevTrendLightOff");
		g.addGrammarString(factory.factory.getOWLLiteral("Rückgängig [machen] | "
				+ "[OP]Tisch [sofort] [wieder] (zurück[stellen|drehen] | gerade | in die Ausgangsposition) und [Raum]Licht an[machen|schalten] | "
				+ "den [OP]Tisch [schnell | sofort | gleich] wieder (zurück | gerade | in die Ausgangsposition) und (schalt[e] | mach) [doch] [bitte] das [Raum]Licht [schnell | sofort | gleich] wieder (ein | an) | "
				+ "[Raum]Licht an[machen|schalten] und [OP]Tisch [sofort] [wieder] (zurück[stellen|drehen] | gerade | in die Ausgangsposition) | "
				+ "das [Raum]Licht [schnell | sofort | gleich] wieder (ein | an) und (stell | dreh) [doch] [bitte] den [OP]Tisch [schnell | sofort | gleich] wieder (zurück | gerade | in die Ausgangsposition)","de"));
		return g;
	}
	
	
	//creates grammar for asking to turn table back again
	public static Grammar grammarUndoTableRevTrend (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoTableRevTrend");
		g.addGrammarString(factory.factory.getOWLLiteral("den [OP]Tisch [schnell | sofort | gleich] [wieder] (zurück[stellen|drehen] | gerade | in die Ausgangsposition)","de"));
		return g;
	}
		
	
	//creates grammar for asking to move table down again
	public static Grammar grammarUndoTableUp (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_UndoTableUp");
		g.addGrammarString(factory.factory.getOWLLiteral("Rückgängig | den [OP]Tisch [schnell | sofort | gleich] [wieder] (zurück[stellen|fahren] | [he]runter | in die Ausgangsposition)","de"));
		return g;
	}
	
	
	//creates grammar for activating emergency mode
	public static Grammar grammarStartEmergencyMode (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_StartEmergencyMode");
		g.addGrammarString(factory.factory.getOWLLiteral("[aktiviere | starte | schalte] [bitte] [den] Notfallmodus [bitte] [ein] [aktivieren | starten | einsschalten]","de"));
		return g;
	}


	//creates grammar for deactivating emergency mode
	public static Grammar grammarEndEmergencyMode (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_EndEmergencyMode");
		g.addGrammarString(factory.factory.getOWLLiteral("[deaktiviere | beende | schalte] [bitte] [den] Notfallmodus [bitte] [aus] [deaktivieren | beenden | aussschalten]","de"));
		return g;
	}


	//creates grammar for saying Hello
	public static Grammar grammarHello (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Hello");
		g.addGrammarString(factory.factory.getOWLLiteral("Hallo | Guten (Tag | Morgen | Vormittag | Mittag | Nachmittag | Abend) | Grüß Gott | Servus","de"));
		return g;
	}


	//creates grammar for saying Bye
	public static Grammar grammarBye (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Bye");
		g.addGrammarString(factory.factory.getOWLLiteral("[Auf] Wiedersehen | Tschüss | Ade","de"));
		return g;
	}


	//creates grammar for thanking
	public static Grammar grammarThankyou (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Thankyou");
		g.addGrammarString(factory.factory.getOWLLiteral("Danke[schön] | Vielen Dank | Ich danke (dir | euch)","de"));
		return g;
	}


	//creates grammar for status query
	public static Grammar grammarRequestStatus (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_RequestStatus");
		g.addGrammarString(factory.factory.getOWLLiteral("alles ([vor]bereit[et] | fertig | so weit)","de"));
		return g;
	}
	
	
	//creates grammar for asking to adopt presets
	public static Grammar grammarAdoptPresets (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_AdoptPresets");
		g.addGrammarString(factory.factory.getOWLLiteral("Übernimm [doch] [bitte] meine Voreinstellungen | Stell [doch] [bitte] (alle[s] | (die | alle) Geräte) auf (meine | die) [aktuellen] Voreinstellungen","de"));
		return g;
	}
	
	
	//creates grammar for asking for patient info
	public static Grammar grammarPatientInfo (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_PatientInfo");
		g.addGrammarString(factory.factory.getOWLLiteral("Info(rmationen|s) über ((den | unseren) Patienten | (die | unsere) Patientin) | Patienteninfo(rmationen|s)","de"));
		return g;
	}
	
	
	//creates grammar for asking for medication info
	public static Grammar grammarMedication (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Medication");
		g.addGrammarString(factory.factory.getOWLLiteral("Info(rmationen|s) über die (Medikation | medikamentöse [Vor]Behandlung) | Medikamenteninfo(rmationen|s)","de"));
		return g;
	}
	
	
	//creates grammar for asking for laboratory data
	public static Grammar grammarLabData (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_LabData");
		g.addGrammarString(factory.factory.getOWLLiteral("Labordaten | Daten aus dem Labor","de"));
		return g;
	}
	
	
	//creates grammar for asking to start operation
	public static Grammar grammarStartOp (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_StartOp");
		g.addGrammarString(factory.factory.getOWLLiteral("(Starte | Beginn | Fang) [doch] [bitte] (mit dem | den) Eingriff [an] | Eingriff (beginnen | starten | anfangen)","de"));
		return g;
	}


	//creates grammar for doctor asking for gas
	public static Grammar grammarGas (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Gas");
		g.addGrammarString(factory.factory.getOWLLiteral("Gas [einschalten] | Insufflator Start[en]","de"));
		return g;
	}


	//creates grammar for doctor asking for gas connection
	public static Grammar grammarGasConnection (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_GasConnection");
		g.addGrammarString(factory.factory.getOWLLiteral("[Gas]anschluss","de"));
		return g;
	}


	//creates grammar for doctor asking for max gas
	public static Grammar grammarMaxGas (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_MaxGas");
		g.addGrammarString(factory.factory.getOWLLiteral("Gas hoch[schalten] | Volle Lotte","de"));
		return g;
	}
	
	
	//creates grammar for doctor asking for turning table and turning light off
	public static Grammar grammarTableRevTrendLightOff (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_TableRevTrendLightOff");
		g.addGrammarString(factory.factory.getOWLLiteral("[OP]Tisch [jetzt] [bitte] [jetzt] (drehen | neigen | schief stellen | in Schieflage stellen) und [das] [Raum]Licht aus[schalten] | "
				+ "[Raum]Licht aus[schalten] und [den] [OP]Tisch (drehen | neigen | schief stellen | in Schieflage stellen) | "
				+ "(Dreh | Neig[e] | Stell) [doch] [jetzt] [bitte] den [OP]Tisch [schief] und schalte das [Raum]Licht aus | "
				+ "[Raum]Licht aus und (dreh | neig[e] | stell) den [OP]Tisch [schief]","de"));
		return g;
	}


	//creates grammar for doctor asking for cleaning the optics
	public static Grammar grammarCleanOptics (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_CleanOptics");
		g.addGrammarString(factory.factory.getOWLLiteral("Reinigen | Sauber machen","de"));
		return g;
	}


	//creates grammar for doctor asking for moving the optics
	public static Grammar grammarMoveOptics (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_MoveOptics");
		g.addGrammarString(factory.factory.getOWLLiteral("Umsetzen","de"));
		return g;
	}


	//creates grammar for doctor handing over
	public static Grammar grammarHandOver (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_HandOver");
		g.addGrammarString(factory.factory.getOWLLiteral("Das ist jetzt Ihr Job | Sie sind dran | Ich übergebe","de"));
		return g;
	}


	//creates grammar for doctor asking to focus the operated area
	public static Grammar grammarFocus (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Focus");
		g.addGrammarString(factory.factory.getOWLLiteral("[Kamera] (Mittig | Zentral | Fokussieren)","de"));
		return g;
	}


	//creates grammar for doctor asking for power on the PE forceps
	public static Grammar grammarPowerPE (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_PowerPE");
		g.addGrammarString(factory.factory.getOWLLiteral("(Ko | Strom) [an PE]","de"));
		return g;
	}


	//creates grammar for doctor asking for distance to optics
	public static Grammar grammarDistance (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Distance");
		g.addGrammarString(factory.factory.getOWLLiteral("[Mit der Optik | Die Optik] [auf] Abstand [mit der Optik]","de"));
		return g;
	}


	//creates grammar for doctor asking for turning the light on
	public static Grammar grammarLightOn (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_LightOn");
		g.addGrammarString(factory.factory.getOWLLiteral("[Raum]Licht [an]","de"));
		return g;
	}


	//creates grammar for doctor asking for moving gas
	public static Grammar grammarMoveGas (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_MoveGas");
		g.addGrammarString(factory.factory.getOWLLiteral("Gas umsetzen","de"));
		return g;
	}


	//creates grammar for doctor giving aspirator to assistant
	public static Grammar grammarAspiratorToAssistant (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_AspiratorToAssistant");
		g.addGrammarString(factory.factory.getOWLLiteral("Sauger für Sie | Ihr Sauger","de"));
		return g;
	}


	//creates grammar for doctor asking for suction
	public static Grammar grammarSuction (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Suction");
		g.addGrammarString(factory.factory.getOWLLiteral("[Ab]saug[en]","de"));
		return g;
	}


	//creates grammar for doctor asking for cleaning
	public static Grammar grammarClean (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Clean");
		g.addGrammarString(factory.factory.getOWLLiteral("Sauber machen | Reinigen","de"));
		return g;
	}


	//creates grammar for doctor asking for sealing with the finger 
	public static Grammar grammarFinger (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_Finger");
		g.addGrammarString(factory.factory.getOWLLiteral("Finger [drauf]","de"));
		return g;
	}


	//creates grammar for doctor asking for connecting gas
	public static Grammar grammarConnectGas (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_ConnectGas");
		g.addGrammarString(factory.factory.getOWLLiteral("Gas [wieder] [ran]","de"));
		return g;
	}


	//creates grammar for doctor asking for moving camera to hilus
	public static Grammar grammarMoveCameraToHilus (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_MoveCameraToHilus");
		g.addGrammarString(factory.factory.getOWLLiteral("[Kamera zum] Hilus","de"));
		return g;
	}


	//creates grammar for doctor asking for moving camera behind liver
	public static Grammar grammarMoveCameraToLiver (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_MoveCameraToLiver");
		g.addGrammarString(factory.factory.getOWLLiteral("hinter der Leber","de"));
		return g;
	}


	//creates grammar for doctor asking for removing trocars
	public static Grammar grammarRemoveTrocars (OSFactory factory) {
		Grammar g = factory.createGrammar("gr_RemoveTrocars");
		g.addGrammarString(factory.factory.getOWLLiteral("Trokare [unter Sicht] (raus | entfernen)","de"));
		return g;
	}


	//adds a grammar to a move
	public static void addGrammarToMove (Move move, Grammar grammar) {
		move.setGrammar(null, grammar);
	}


	public static Utterance makeUtterance (OSFactory factory, String name) {
		
		Utterance utterance_name = factory.createUtterance(name);
		String utterance_String = "";
		
		//General utterances and preparation for procedure
		if (name.equals("utterance_Open")) {
			utterance_String = "IDACO wurde erfolgreich gestartet.";
		} else if (name.equals("utterance_Abort")) {
			utterance_String = "Entschuldigung, IDACO wurde beendet.";
		} else if (name.equals("utterance_Hello")) {
			utterance_String = "Guten Tag %var_Op1Name%.";
		} else if (name.equals("utterance_Bye")) {
			utterance_String = "Vielen Dank für die Zusammenarbeit. Auf Wiedersehen.";
		} else if (name.equals("utterance_NotReady")) {
			utterance_String = "Das OP-Team ist noch nicht komplett.";
		} else if (name.equals("utterance_AlmostReady")) {
			utterance_String = "Es ist fast alles vorbereitet. Der Patient %var_PatientName% ist gelagert, abgewaschen und abgedeckt. %var_Op2Name% und %var_NurseName% stehen am Tisch bereit. Die Anästhesie wird von %var_OpAnesthesiaName% durchgeführt.";
		} else if (name.equals("utterance_ReqPresets")) {
			utterance_String = "Soll ich den Tisch, den Thermoflator und die Lichtquelle auf Ihre gewünschten Voreinstellungen setzen?";
		} else if (name.equals("utterance_AdoptedPresets")) {
			utterance_String = "Ich habe Ihre Voreinstellungen %var_ApplyPresets% übernommen.";
		} else if (name.equals("utterance_ReqTeamTimeOut")) {
			utterance_String = "Sollen wir gemeinsam das Team-Time-Out durchführen?";
		} else if (name.equals("utterance_InfoPatientData")) {
			utterance_String = "Operiert wird heute %var_PatientName%, %var_PatientAge% Jahre, %var_PatientSex%. Eingriff: %var_OpType%. Bitte bestätigen.";
		} else if (name.equals("utterance_InfoDiseases")) {
			utterance_String = "%var_Diseases%.";
		} else if (name.equals("utterance_Medication")) {
			utterance_String = "%var_Medication%.";
		} else if (name.equals("utterance_LabData")) {
			utterance_String = "%var_LabData%.";
		} else if (name.equals("utterance_StartOp")) {
			utterance_String = "Sollen wir mit dem Eingriff beginnen?";
		} else if (name.equals("utterance_WrongPatientInfo")) {
			utterance_String = "Offenbar liegt ein Fehler vor. Bitte überprüfen Sie die Patientendaten. IDACO wird beendet.";
		} else if (name.equals("utterance_Exit")) {
			utterance_String = "%var_Op1Name%, möchten Sie IDACO beenden?";
		
		//Information about device settings
		} else if (name.equals("utterance_InfoThermoflatorSettings")) {
			utterance_String = "Die aktuellen Werte des Thermoflators sind wie folgt. Druck: %varDB_dev_Insufflator_CurrentPressure% mmHg, Zieldruck: %varDB_dev_Insufflator_TargetPressure% mmHg, Gasfluss: %varDB_dev_Insufflator_CurrentFlow%, Zielfluss: %varDB_dev_Insufflator_TargetFlow%.";
		} else if (name.equals("utterance_InfoRoomLightSettings")) {
			utterance_String = "Die aktuellen Einstellungen für das Licht sind wie folgt. %varDB_dev_Lichtquelle_Intensity% Prozent Helligkeit, die Zielintensität liegt bei %varDB_dev_Lichtquelle_TargetIntensity% Prozent.";
		} else if (name.equals("utterance_InfoEndomatSettings")) {
			utterance_String = "Der Zielfluss für den Endomat liegt momentan bei %varDB_dev_Saug-Spuel-Einheit_TargetValue-F%.";
		} else if (name.equals("utterance_InfoTableSettings")) {
			utterance_String = "Die aktuellen Einstellungen für den OPTisch sind wie folgt. Höhe: %varDB_dev_Tisch_TableHeight% Centimeter, Rechtsneigung um %varDB_dev_Tisch_TableTilt% Grad.";
			
		//Information about device presets
		} else if (name.equals("utterance_InfoThermoflatorPresets")) {
			utterance_String = "Ihre Voreinstellungen für den Thermoflator sind wie folgt hinterlegt. Zieldruck: %varDB_setup_Insufflator_TargetPressure% mmHg, Maximalfluss: %varDB_setup_Insufflator_TargetFlow%.";
		} else if (name.equals("utterance_InfoRoomLightPresets")) {
			utterance_String = "Ihre Voreinstellung für die maximale Lichtintensität liegt bei %varDB_setup_Lichtquelle_TargetIntensity% Prozent.";
		} else if (name.equals("utterance_InfoEndomatPresets")) {
			utterance_String = "Ihre Voreinstellung für den Zielfluss des Endomats liegt bei %varDB_setup_Saug-Spuel-Einheit_TargetValue-F%.";
		} else if (name.equals("utterance_InfoTablePresets")) {
			utterance_String = "Ihre Voreinstellungen für den OP-Tisch sind wie folgt hinterlegt. Höhe: %varDB_setup_Tisch_TableHeight% Centimeter, Rechtsneigung um %varDB_setup_Tisch_TableTilt% Grad.";
			
		//Changing device presets
		} else if (name.equals("utterance_ChangePresetsThermoflator1")) {
			utterance_String = "Bitte geben Sie den gewünschten Zieldruck des Thermoflators an.";
		} else if (name.equals("utterance_ChangePresetsThermoflator2")) {
			utterance_String = "Geben Sie nun bitte den Maximalfluss an.";
		} else if (name.equals("utterance_ChangePresetsRoomLightChangePresets")) {
			utterance_String = "Bitte nennen Sie die gewünschte maximale Lichtintensität.";
		} else if (name.equals("utterance_ChangePresetsEndomat")) {
			utterance_String = "Bitte geben Sie den gewünschten Zielfluss des Endomats an.";
		} else if (name.equals("utterance_ChangePresetsTable1")) {
			utterance_String = "Bitte nennen sie Ihre gewünschte Voreinstellung für die Tischhöhe.";
		} else if (name.equals("utterance_ChangePresetsTable2")) {
			utterance_String = "Geben Sie nun bitte noch die gewünschte Rechtsneigung des OP-Tisches an.";
		} else if (name.equals("utterance_ChangedPresetsThermoflator")) {
			utterance_String = "%var_Op1Name%, soll ich die Voreinstellungen für den Thermoflator speichern? Andernfalls werden die neuen Werte verworfen.";
		} else if (name.equals("utterance_ChangedPresetsRoomLight")) {
			utterance_String = "%var_Op1Name%, soll ich die Voreinstellung für die Lichtintensität speichern? Andernfalls wird der neue Wert verworfen.";
		} else if (name.equals("utterance_ChangedPresetsEndomat")) {
			utterance_String = "%var_Op1Name%, soll ich die Voreinstellung für den Endomat speichern? Andernfalls wird der neue Wert verworfen.";
		} else if (name.equals("utterance_ChangedPresetsTable")) {
			utterance_String = "%var_Op1Name%, soll ich die Voreinstellungen für den OP-Tisch speichern? Andernfalls werden die neuen Werte verworfen.";
		} else if (name.equals("utterance_SavedChanges")) {
			utterance_String = "Ihre Änderungen wurden gespeichert.";
		} else if (name.equals("utterance_DroppedChanges")) {
			utterance_String = "Ihre Änderungen wurden verworfen.";
			
		//Automatic device control
		} else if (name.equals("utterance_RequestStartGas")) {
			utterance_String = "Soll ich die Gasinsufflation starten?";
		} else if (name.equals("utterance_GasFlowStarted")) {
			utterance_String = "%var_Op1Name%, ich habe den Gasfluss gestartet.";
		} else if (name.equals("utterance_StartGasManually")) {
			utterance_String = "Die Gasinsufflation muss nun manuell gestartet werden.";
		} else if (name.equals("utterance_RequestMaxGas")) {
			utterance_String = "Soll ich die Gasinsufflation auf den Maximalwert steigern?";
		} else if (name.equals("utterance_GasAtMax")) {
			utterance_String = "Die Gasinsufflation ist nun auf dem Maximum.";
		} else if (name.equals("utterance_MaxGasManually")) {
			utterance_String = "Das Gas muss nun manuell hochgeschaltet werden.";
//		} else if (name.equals("utterance_ReachedTargetPressure")) {
//			utterance_String = "Der Solldruck von %varDB_dev_Insufflator_TargetPressure% mmHg wurde erreicht.";
		} else if (name.equals("utterance_RequestLightOn")) {
			utterance_String = "Möchten Sie das Raumlicht wieder einschalten?";
		} else if (name.equals("utterance_LightOnManually")) {
			utterance_String = "Das Licht muss nun manuell eingeschaltet werden.";
		} else if (name.equals("utterance_RequestTableRevTrend")) {
			utterance_String = "Soll ich den Tisch nun auf %varDB_setup_Tisch_TableTilt% Grad Anti-Trendelenburg verstellen und das Raumlicht ausschalten?";
		} else if (name.equals("utterance_TableRevTrendLightOffManually")) {
			utterance_String = "Der Tisch muss nun manuell gedreht und das Licht von Hand ausgeschaltet werden.";
		} else if (name.equals("utterance_UndoStartGas")) {
			utterance_String = "Ich habe die Gasinsufflation wieder gestoppt.";
		} else if (name.equals("utterance_UndoMaxGas")) {
			utterance_String = "Ich habe den Gasfluss wieder heruntergeschaltet.";
			
		//Tool warnings
		} else if (name.equals("utterance_WarnTool1More")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Backhaus% Backhaus-Klemmen verwendet. Der übliche Verbrauch bei der Anlage der Hautinzision und dem Einbringen des ersten Trokars während diesem Eingriff liegt bei %var_ExpectedBackhaus1%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool2More")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Trokar% Trokare verwendet. Der übliche Verbrauch beim Erzeugen des Pneumoperitoneums während diesem Eingriff liegt bei %var_ExpectedTrokar2%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool3More")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Trokar% Trokare verwendet. Der übliche Verbrauch bis zur Präparation des Calot-Dreiecks während diesem Eingriff liegt bei %var_ExpectedTrokar3%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool5More")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Clip% Clips verwendet. Der übliche Verbrauch beim Clippen der Arteria Cystica während diesem Eingriff liegt bei %var_ExpectedClip5%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool6More")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Clip% Clips verwendet. Der übliche Verbrauch bis zum Clippen des Ductus Cysticus während diesem Eingriff liegt bei %var_ExpectedClip6%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool8aMore")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Pean% Pean-Klemmen verwendet. Der übliche Verbrauch beim Bergen der Gallenblase während diesem Eingriff liegt bei %var_ExpectedPean8a%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool8bMore")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Alice% Alice-Klemmen verwendet. Der übliche Verbrauch beim Bergen der Gallenblase während diesem Eingriff liegt bei %var_ExpectedAlice8b%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool8cMore")) {
			utterance_String = "Achtung, Sie haben nun bereits %var_tool_cnt_Trokar% Trokare verwendet. Der übliche Verbrauch bis zum Bergen der Gallenblase während diesem Eingriff liegt bei %var_ExpectedTrokar8c%. Ist das korrekt?";
		
		} else if (name.equals("utterance_WarnTool1Less")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Backhaus% Backhaus-Klemmen verwendet. Der übliche Verbrauch bei der Anlage der Hautinzision und dem Einbringen des ersten Trokars während diesem Eingriff liegt bei %var_ExpectedBackhaus1%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool2Less")) {
			utterance_String = "Achtung, Sie haben noch keinen Trokar verwendet. Der übliche Verbrauch beim Erzeugen des Pneumoperitoneums während diesem Eingriff liegt bei %var_ExpectedTrokar2%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool3Less")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Trokar% Trokare verwendet. Der übliche Verbrauch bis zur Präparation des Calot-Dreiecks während diesem Eingriff liegt bei %var_ExpectedTrokar3%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool5Less")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Clip% Clips verwendet. Der übliche Verbrauch beim Clippen der Arteria Cystica während diesem Eingriff liegt bei %var_ExpectedClip5%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool6Less")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Clip% Clips verwendet. Der übliche Verbrauch bis zum Clippen des Ductus Cysticus während diesem Eingriff liegt bei %var_ExpectedClip6%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool8aLess")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Pean% Pean-Klemmen verwendet. Der übliche Verbrauch beim Bergen der Gallenblase während diesem Eingriff liegt bei %var_ExpectedPean8a%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool8bLess")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Alice% Alice-Klemmen verwendet. Der übliche Verbrauch beim Bergen der Gallenblase während diesem Eingriff liegt bei %var_ExpectedAlice8b%. Ist das korrekt?";
		} else if (name.equals("utterance_WarnTool8cLess")) {
			utterance_String = "Achtung, Sie haben erst %var_tool_cnt_Trokar% Trokare verwendet. Der übliche Verbrauch bis zum Bergen der Gallenblase während diesem Eingriff liegt bei %var_ExpectedTrokar8c%. Ist das korrekt?";
			
		//Request tool usage
		} else if (name.equals("utterance_RequestUsageBackhaus")) {
			utterance_String = "Wie viele Backhaus-Klemmen haben Sie bisher verwendet?";
		} else if (name.equals("utterance_RequestUsageTrokar")) {
			utterance_String = "Wie viele Trokare haben Sie bisher verwendet?";
		} else if (name.equals("utterance_RequestUsageClip")) {
			utterance_String = "Wie viele Clips haben Sie bisher verwendet?";
		} else if (name.equals("utterance_RequestUsagePean")) {
			utterance_String = "Wie viele Pean-Klemmen haben Sie bisher verwendet?";
		} else if (name.equals("utterance_RequestUsageAlice")) {
			utterance_String = "Wie viele Alice-Klemmen haben Sie bisher verwendet?";
			
		//Emergency mode
		} else if (name.equals("utterance_RequestEmergencyMode")) {
			utterance_String = "Soll ich den Notfallmodus aktivieren?";
		} else if (name.equals("utterance_StartedEmergencyMode")) {
			utterance_String = "Notfallmodus aktiviert";
		} else if (name.equals("utterance_EndEmergencyMode")) {
			utterance_String = "Soll ich den Notfallmodus beenden?";
		} else if (name.equals("utterance_EndedEmergencyMode")) {
			utterance_String = "Notfallmodus beendet";
		} else if (name.equals("utterance_AdaptRequest1")) {
			utterance_String = "Der registrierte Verbrauch von Backhausklemmen wird angepasst.";
		} else if (name.equals("utterance_AdaptRequest2") || name.equals("utterance_AdaptRequest2b") || name.equals("utterance_AdaptRequest3") || name.equals("utterance_AdaptRequest8c")) {
			utterance_String = "Der registrierte Verbrauch von Trokaren wird angepasst.";
		} else if (name.equals("utterance_AdaptRequest5") || name.equals("utterance_AdaptRequest6")) {
			utterance_String = "Der registrierte Verbrauch von Clips wird angepasst.";
		} else if (name.equals("utterance_AdaptRequest8a")) {
			utterance_String = "Der registrierte Verbrauch von Peanklemmen wird angepasst.";
		} else if (name.equals("utterance_AdaptRequest8b")) {
			utterance_String = "Der registrierte Verbrauch von Aliceklemmen wird angepasst.";
		} else if (name.equals("utterance_SaidUsage")) {
			utterance_String = "Sagten Sie %var_SaidUsage%?";
		} else if (name.equals("utterance_AdaptUsage")) {
			utterance_String = "Der Verbrauch wird angepasst.";
			
		} else {
			utterance_String = "unbekannt";
		}
		
		utterance_name.addUtteranceString(factory.factory.getOWLLiteral(utterance_String,"de"));
		return utterance_name;
	}


	//changes the name-string to get corresponding utterance-name
	public static void getAndAddUtterance (OSFactory factory, Move move) {
		String name = move.toString();
		name = name.replace("move_sys", "utterance");
		move.setUtterance(null, makeUtterance(factory, name));			
	}
	
	
	//Creates a new system move and changes the name-string to get corresponding utterance-name
	public static Move createSysMoveWithUtterance (OSFactory factory, String name) {
		Move move = factory.createMove("move_sys_" + name);
		move.setUtterance(null, makeUtterance(factory, "utterance_" + name));	
	
		return move;
	}
	
	
	//Creates a new user move with the given grammar and semantic
	public static Move createUserMoveWithGrammarAndSem (OSFactory factory, Grammar gr, Semantic sem) {
		String name = gr.getLocalName();
		name = name.replace("gr", "move_user");
		Move move = factory.createMove(name);
		addGrammarToMove(move, gr);
		move.addSemantic(sem);
		
		return move;
	}
	
	
	//Adds both the semantic and contrary semantic to the given move
	public static void addSemAndContrarySem (Move move, Semantic sem, Semantic con) {
		move.addSemantic(sem);
		move.addContrarySemantic(con);
	}
	
	
	//Adds Semantics to SemanticGroups and the other way round
	public static void linkSemWithSemGroup (Semantic sem, SemanticGroup semGroup) {
		sem.addSemanticGroup(semGroup);
		semGroup.addContainedSemantic(sem);
	}
	
	
	//Adds Agendas to SummaryAgendas and the other way round
	public static void linkAgendaWithSumAgenda (Agenda agenda, SummaryAgenda sumAgenda) {
		agenda.addSummaryAgenda(sumAgenda);
		sumAgenda.addSummarizedAgenda(agenda);
	}
	
	
	//Adds Variables to SemanticGroups and the other way round
	public static void linkVarWithSemGroup (Variable var, SemanticGroup semGroup) {
		var.addBelongsToSemantic(semGroup);
		semGroup.addVariable(var);
	}
	
	
	//Creates a collection of all variables within the ontology
	public static Collection<Variable> getVars (OwlSpeakOntology onto) {
		OSFactory fa = onto.factory;
		if (fa != null) {
			Collection<Variable> varCol = fa.getAllVariableInstances();
			return varCol;
		} else {
			System.err.println("factory is null");
			return null;
		}
	}

	
	
	/**
	 * @param argv
	 * @throws Exception
	 */
	public static void main(String[] argv) throws Exception {
        
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
        @SuppressWarnings("unused")
		ServletEngine engine = new ServletEngine();
        String uriSave = Settings.uri;
        Settings.uri = "http://localhost:8083/OwlSpeakOnto.owl";
        String filename = "idaco.owl";
        String path = Settings.homePath;
        OSFactory factory = null;
        
        IdacoDB db = new IdacoDB();
        
        try {
            factory = OwlSpeakOntology.createOSFactoryEmptyOnto(filename, path);
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        
        
        int sessionID = IdacoDB.getCurrentSession();
        float tableHeightEnd = Float.parseFloat(IdacoDB.getCurrentSetupParamValue(sessionID, "Tisch", "TableHeight")) + 25;
        
        
        //General data
        Variable varOp1Name = factory.createVariable("var_Op1Name");
        varOp1Name.setDefaultValue("", IdacoDB.getPrimarySurgeonName(sessionID));
        Variable varOp2Name = factory.createVariable("var_Op2Name");
        varOp2Name.setDefaultValue("", IdacoDB.getSecondarySurgeonName(sessionID));
        Variable varOpAnesthesiaName = factory.createVariable("var_OpAnesthesiaName");
        varOpAnesthesiaName.setDefaultValue("", IdacoDB.getAnesthetistName(sessionID));
        Variable varNurseName = factory.createVariable("var_NurseName");
        varNurseName.setDefaultValue("", IdacoDB.getNurseName(sessionID));
        Variable varStaffComplete = factory.createVariable("var_StaffComplete");
        varStaffComplete.setDefaultValue("", IdacoDB.sessionTeamComplete(sessionID));
        
        //Patient data
        Variable varPatientName = factory.createVariable("var_PatientName");
        varPatientName.setDefaultValue("", IdacoDB.getFullPatientName(sessionID));
        Variable varPatientAge = factory.createVariable("var_PatientAge");
        varPatientAge.setDefaultValue("", Integer.toString(IdacoDB.getPatientAge(sessionID)));
        Variable varPatientSex = factory.createVariable("var_PatientSex");
        varPatientSex.setDefaultValue("", IdacoDB.getPatientGenderGerman(sessionID));
        Variable varOpType = factory.createVariable("var_OpType");
        varOpType.setDefaultValue("", IdacoDB.getSessionType(sessionID));
        Variable varDiseases = factory.createVariable("var_Diseases");
        varDiseases.setDefaultValue("", IdacoDB.getPatientPreDiseases(sessionID));
        Variable varMedication = factory.createVariable("var_Medication");
        varMedication.setDefaultValue("", IdacoDB.getPatientPreMedication(sessionID));
        Variable varLabData = factory.createVariable("var_LabData");
        varLabData.setDefaultValue("", IdacoDB.getPatientLaboratoryData(sessionID));
        
        //Data presets
        Variable varPresetRoomLightTargetIntensity = factory.createVariable("varDB_setup_Lichtquelle_TargetIntensity");
        varPresetRoomLightTargetIntensity.setDefaultValue("", IdacoDB.getCurrentSetupParamValue(sessionID, "Lichtquelle", "TargetIntensity"));
        Variable varPresetThermoflatorTargetPressure = factory.createVariable("varDB_setup_Insufflator_TargetPressure");
        varPresetThermoflatorTargetPressure.setDefaultValue("", IdacoDB.getCurrentSetupParamValue(sessionID, "Insufflator", "TargetPressure"));
        Variable varPresetThermoflatorTargetFlow = factory.createVariable("varDB_setup_Insufflator_TargetFlow");
        varPresetThermoflatorTargetFlow.setDefaultValue("", IdacoDB.getCurrentSetupParamValue(sessionID, "Insufflator", "TargetFlow"));
        Variable varPresetEndomatTargetFlow = factory.createVariable("varDB_setup_Saug-Spuel-Einheit_TargetValue-F");
        varPresetEndomatTargetFlow.setDefaultValue("", IdacoDB.getCurrentSetupParamValue(sessionID, "Saug-Spuel-Einheit", "TargetValue-F"));
        Variable varPresetTableHeight = factory.createVariable("varDB_setup_Tisch_TableHeight");
        varPresetTableHeight.setDefaultValue("", IdacoDB.getCurrentSetupParamValue(sessionID, "Tisch", "TableHeight"));
        Variable varPresetTableTilt = factory.createVariable("varDB_setup_Tisch_TableTilt");
        varPresetTableTilt.setDefaultValue("", IdacoDB.getCurrentSetupParamValue(sessionID, "Tisch", "TableTilt"));
        
        //Temporary variables for preset change
        Variable varTempPresetRoomLightTargetIntensity = factory.createVariable("var_TempPresetRoomLightTargetIntensity");
        varTempPresetRoomLightTargetIntensity.setDefaultValue("", "noTempChange");
        Variable varTempPresetThermoflatorTargetPressure = factory.createVariable("var_TempPresetThermoflatorTargetPressure");
        varTempPresetThermoflatorTargetPressure.setDefaultValue("", "noTempChange");
        Variable varTempPresetThermoflatorTargetFlow = factory.createVariable("var_TempPresetThermoflatorTargetFlow");
        varTempPresetThermoflatorTargetFlow.setDefaultValue("", "noTempChange");
        Variable varTempPresetEndomatTargetFlow = factory.createVariable("var_TempPresetEndomatTargetFlow");
        varTempPresetEndomatTargetFlow.setDefaultValue("", "noTempChange");
        Variable varTempPresetTableHeight = factory.createVariable("var_TempPresetTableHeight");
        varTempPresetTableHeight.setDefaultValue("", "noTempChange");
        Variable varTempPresetTableTilt = factory.createVariable("var_TempPresetTableTilt");
        varTempPresetTableTilt.setDefaultValue("", "noTempChange");
                
        //Device parameters: light
        Variable varDB_dev_RoomLight_Status = factory.createVariable("varDB_dev_Lichtquelle_status");
        varDB_dev_RoomLight_Status.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Lichtquelle", "status"));
        Variable varDB_dev_RoomLight_Intensity = factory.createVariable("varDB_dev_Lichtquelle_Intensity");
        varDB_dev_RoomLight_Intensity.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Lichtquelle", "Intensity"));
        Variable varDB_dev_RoomLight_TargetIntensity = factory.createVariable("varDB_dev_Lichtquelle_TargetIntensity");
        varDB_dev_RoomLight_TargetIntensity.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Lichtquelle", "TargetIntensity"));
        
        //Device parameters: thermoflator
        Variable varDB_dev_Thermoflator_TargetPressure = factory.createVariable("varDB_dev_Insufflator_TargetPressure");
        varDB_dev_Thermoflator_TargetPressure.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Insufflator", "TargetPressure"));
        Variable varDB_dev_Thermoflator_CurrentPressure = factory.createVariable("varDB_dev_Insufflator_CurrentPressure");
        varDB_dev_Thermoflator_CurrentPressure.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Insufflator", "CurrentPressure"));
        Variable varDB_dev_Thermoflator_TargetFlow = factory.createVariable("varDB_dev_Insufflator_TargetFlow");
        varDB_dev_Thermoflator_TargetFlow.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Insufflator", "TargetFlow"));
        Variable varDB_dev_Thermoflator_CurrentFlow = factory.createVariable("varDB_dev_Insufflator_CurrentFlow");
        varDB_dev_Thermoflator_CurrentFlow.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Insufflator", "CurrentFlow"));
        
        //Device parameters: endomat
        Variable varDB_dev_Endomat_TargetFlow = factory.createVariable("varDB_dev_Saug-Spuel-Einheit_TargetValue-F");
        varDB_dev_Endomat_TargetFlow.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Saug-Spuel-Einheit", "TargetValue-F"));
        
        //Device parameters: OR table
        Variable varDB_dev_Table_Height = factory.createVariable("varDB_dev_Tisch_TableHeight");
        varDB_dev_Table_Height.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Tisch", "TableHeight"));
        Variable varDB_dev_Table_Tilt = factory.createVariable("varDB_dev_Tisch_TableTilt");
        varDB_dev_Table_Tilt.setDefaultValue("", IdacoDB.getCurrentDevParamValue(sessionID, "Tisch", "TableTilt"));
        
        //Expected tool count
        Variable varExpectedBackhaus1 = factory.createVariable("var_ExpectedBackhaus1");
     	varExpectedBackhaus1.setDefaultValue("", "2");
     	Variable varExpectedTrokar2 = factory.createVariable("var_ExpectedTrokar2");
     	varExpectedTrokar2.setDefaultValue("", "1");
     	Variable varExpectedTrokar2b = factory.createVariable("var_ExpectedTrokar2b");
     	varExpectedTrokar2b.setDefaultValue("", "2");
     	Variable varExpectedTrokar3 = factory.createVariable("var_ExpectedTrokar3");
     	varExpectedTrokar3.setDefaultValue("", "4");
     	Variable varExpectedClip5 = factory.createVariable("var_ExpectedClip5");
     	varExpectedClip5.setDefaultValue("", "3");
     	Variable varExpectedClip6 = factory.createVariable("var_ExpectedClip6");
     	varExpectedClip6.setDefaultValue("", "6");
     	Variable varExpectedPean8a = factory.createVariable("var_ExpectedPean8a");
     	varExpectedPean8a.setDefaultValue("", "3");
     	Variable varExpectedAlice8b = factory.createVariable("var_ExpectedAlice8b");
     	varExpectedAlice8b.setDefaultValue("", "3");
     	Variable varExpectedTrokar8c = factory.createVariable("var_ExpectedTrokar8c");
     	varExpectedTrokar8c.setDefaultValue("", "5");
        
    
        //Other
     	Variable varApplyPresets = factory.createVariable("var_ApplyPresets");
     	varApplyPresets.setDefaultValue("", "");
     	
     	Variable varTeamTimeOut = factory.createVariable("var_TeamTimeOut");
     	varTeamTimeOut.setDefaultValue("", "-1");
     	
     	Variable varPreOpPart = factory.createVariable("var_PreOpPart");
     	varPreOpPart.setDefaultValue("", "default");
     	Variable varSaidAlmostReady = factory.createVariable("var_SaidAlmostReady");
     	varSaidAlmostReady.setDefaultValue("", "0");
     	Variable varSaidAdoptedPresets = factory.createVariable("var_SaidAdoptedPresets");
     	varSaidAdoptedPresets.setDefaultValue("", "0");
     	Variable varSaidPatientData = factory.createVariable("var_SaidPatientData");
     	varSaidPatientData.setDefaultValue("", "0");
     	Variable varStartOp = factory.createVariable("var_StartOp");
     	varStartOp.setDefaultValue("", "0");
     	
        Variable varLastTool = factory.createVariable("var_LastTool");
        varLastTool.setDefaultValue("", "dasselbe");
        
        Variable varInfo = factory.createVariable("var_Info");
        varInfo.setDefaultValue("", "noInfo");
        
        Variable varChange = factory.createVariable("var_Change");
        varChange.setDefaultValue("", "noChange");
        
        Variable varWarn = factory.createVariable("var_Warn");
        varWarn.setDefaultValue("", "false");
        
        Variable varWarnedTool = factory.createVariable("var_WarnedTool");
        varWarnedTool.setDefaultValue("", "tool");
        
        Variable varCurrentPart = factory.createVariable("var_CurrentPart");
		varCurrentPart.setDefaultValue("", "Beginn");
		
		Variable varHadEmergency = factory.createVariable("var_HadEmergency");
		varHadEmergency.setDefaultValue("", "false");
		
		Variable varTemp = factory.createVariable("var_Temp");
		varTemp.setDefaultValue("", "temp default");
		
		Variable varSubDial = factory.createVariable("var_SubDial");
		varSubDial.setDefaultValue("", "0");
		
		Variable varExit = factory.createVariable("var_Exit");
		varExit.setDefaultValue("", "0");

		
        SemanticGroup semGroupHello = factory.createSemanticGroup("semGroup_Hello");
        semGroupHello.setFieldTotals(1);
        
        SemanticGroup semGroupBye = factory.createSemanticGroup("semGroup_Bye");
        semGroupBye.setFieldTotals(1);
        linkVarWithSemGroup(varExit, semGroupBye);
        
        SemanticGroup semGroupDevices = factory.createSemanticGroup("semGroup_Devices");
        semGroupDevices.setFieldTotals(10);
        linkVarWithSemGroup(varDB_dev_RoomLight_Status, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Thermoflator_TargetPressure, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Thermoflator_CurrentPressure, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Thermoflator_TargetFlow, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Thermoflator_CurrentFlow, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_RoomLight_Intensity, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_RoomLight_TargetIntensity, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Endomat_TargetFlow, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Table_Height, semGroupDevices);
        linkVarWithSemGroup(varDB_dev_Table_Tilt, semGroupDevices);
        linkVarWithSemGroup(varSubDial, semGroupDevices);
        
        SemanticGroup semGroupPresets = factory.createSemanticGroup("semGroup_Presets");
        semGroupPresets.setFieldTotals(18);
        linkVarWithSemGroup(varApplyPresets, semGroupPresets);
        linkVarWithSemGroup(varPresetRoomLightTargetIntensity, semGroupPresets);
        linkVarWithSemGroup(varPresetThermoflatorTargetPressure, semGroupPresets);
        linkVarWithSemGroup(varPresetThermoflatorTargetFlow, semGroupPresets);
        linkVarWithSemGroup(varPresetEndomatTargetFlow, semGroupPresets);
        linkVarWithSemGroup(varPresetTableHeight, semGroupPresets);
        linkVarWithSemGroup(varPresetTableTilt, semGroupPresets);
        linkVarWithSemGroup(varTempPresetRoomLightTargetIntensity, semGroupPresets);
        linkVarWithSemGroup(varTempPresetThermoflatorTargetPressure, semGroupPresets);
        linkVarWithSemGroup(varTempPresetThermoflatorTargetFlow, semGroupPresets);
        linkVarWithSemGroup(varTempPresetEndomatTargetFlow, semGroupPresets);
        linkVarWithSemGroup(varTempPresetTableHeight, semGroupPresets);
        linkVarWithSemGroup(varTempPresetTableTilt, semGroupPresets);
        linkVarWithSemGroup(varTemp, semGroupPresets);
        linkVarWithSemGroup(varChange, semGroupPresets);
        
        SemanticGroup semGroupStatus = factory.createSemanticGroup("semGroup_Status");
        semGroupStatus.setFieldTotals(1);
        linkVarWithSemGroup(varStaffComplete, semGroupStatus);
        
        SemanticGroup semGroupInfo = factory.createSemanticGroup("semGroup_Info");
        semGroupInfo.setFieldTotals(10);
        linkVarWithSemGroup(varInfo, semGroupInfo);
        linkVarWithSemGroup(varTeamTimeOut, semGroupInfo);
        linkVarWithSemGroup(varPreOpPart, semGroupInfo);
        linkVarWithSemGroup(varSaidAlmostReady, semGroupInfo);
        linkVarWithSemGroup(varSaidAdoptedPresets, semGroupInfo);
        linkVarWithSemGroup(varSaidPatientData, semGroupInfo);
        linkVarWithSemGroup(varDiseases, semGroupInfo);
        linkVarWithSemGroup(varMedication, semGroupInfo);
        linkVarWithSemGroup(varLabData, semGroupInfo);
        linkVarWithSemGroup(varStartOp, semGroupInfo);
        
        SemanticGroup semGroupTool = factory.createSemanticGroup("semGroup_Tool");
        semGroupTool.setFieldTotals(no_of_tools + 12);
        linkVarWithSemGroup(varLastTool, semGroupTool);
        linkVarWithSemGroup(varWarn, semGroupTool);
        linkVarWithSemGroup(varWarnedTool, semGroupTool);
        linkVarWithSemGroup(varExpectedBackhaus1, semGroupTool);
        linkVarWithSemGroup(varExpectedTrokar2, semGroupTool);
        linkVarWithSemGroup(varExpectedTrokar2b, semGroupTool);
        linkVarWithSemGroup(varExpectedTrokar3, semGroupTool);
        linkVarWithSemGroup(varExpectedClip5, semGroupTool);
        linkVarWithSemGroup(varExpectedClip6, semGroupTool);
        linkVarWithSemGroup(varExpectedPean8a, semGroupTool);
        linkVarWithSemGroup(varExpectedAlice8b, semGroupTool);
        linkVarWithSemGroup(varExpectedTrokar8c, semGroupTool);

        SemanticGroup semGroupAssist = factory.createSemanticGroup("semGroup_Assist");
        semGroupAssist.setFieldTotals(1);
        
        SemanticGroup semGroupEmergency = factory.createSemanticGroup("semGroup_Emergency");
        semGroupEmergency.setFieldTotals(1);
        linkVarWithSemGroup(varHadEmergency, semGroupEmergency);
        linkVarWithSemGroup(varCurrentPart, semGroupEmergency);
        
       
        Semantic semHello = factory.createSemantic("sem_Hello");
        linkSemWithSemGroup(semHello, semGroupHello);

        Semantic semBye = factory.createSemantic("sem_Bye");
        linkSemWithSemGroup(semBye, semGroupBye);
        
        Semantic semExit = factory.createSemantic("sem_Exit");
        linkSemWithSemGroup(semExit, semGroupBye);
        
        Semantic semAdoptPresets = factory.createSemantic("sem_AdoptPresets");
        linkSemWithSemGroup(semAdoptPresets, semGroupPresets);
        
        Semantic semPatientInfo = factory.createSemantic("sem_PatientInfo");
        linkSemWithSemGroup(semPatientInfo, semGroupInfo);
        
        Semantic semConfirmTeamTimeOut = factory.createSemantic("sem_ConfirmTeamTimeOut");
        semConfirmTeamTimeOut.setConfirmationInfo(true);
        linkSemWithSemGroup(semConfirmTeamTimeOut, semGroupInfo);
        
        Semantic semDenyTeamTimeOut = factory.createSemantic("sem_DenyTeamTimeOut");
        semDenyTeamTimeOut.setConfirmationInfo(false);
        linkSemWithSemGroup(semDenyTeamTimeOut, semGroupInfo);
        
        Semantic semConfirmInfoPatient = factory.createSemantic("sem_ConfirmInfoPatient");
        semConfirmInfoPatient.setConfirmationInfo(true);
        linkSemWithSemGroup(semConfirmInfoPatient, semGroupInfo);
        
        Semantic semDenyInfoPatient = factory.createSemantic("sem_DenyInfoPatient");
        semDenyInfoPatient.setConfirmationInfo(false);
        linkSemWithSemGroup(semDenyInfoPatient, semGroupInfo);
        
        Semantic semConfirmStartOp = factory.createSemantic("sem_ConfirmStartOp");
        semConfirmStartOp.setConfirmationInfo(true);
        linkSemWithSemGroup(semConfirmStartOp, semGroupInfo);
        
        Semantic semDenyStartOp = factory.createSemantic("sem_DenyStartOp");
        semDenyStartOp.setConfirmationInfo(false);
        linkSemWithSemGroup(semDenyStartOp, semGroupInfo);
        
        Semantic semConfirmPresets = factory.createSemantic("sem_ConfirmPresets");
        semConfirmPresets.setConfirmationInfo(true);
        linkSemWithSemGroup(semConfirmPresets, semGroupPresets);

        
        Semantic semDenyPresets = factory.createSemantic("sem_DenyPresets");
        semDenyPresets.setConfirmationInfo(false);
        linkSemWithSemGroup(semDenyPresets, semGroupPresets);
        
        Semantic semAlmostReady = factory.createSemantic("sem_AlmostReady");
        linkSemWithSemGroup(semAlmostReady, semGroupStatus);
        
        
        Semantic semMedication = factory.createSemantic("sem_Medication");
        linkSemWithSemGroup(semMedication, semGroupInfo);
        
        Semantic semLabData = factory.createSemantic("sem_LabData");
        linkSemWithSemGroup(semLabData, semGroupInfo);
        
        
        Semantic semInfoThermoflatorSettings = factory.createSemantic("sem_InfoThermoflatorSettings");
        linkSemWithSemGroup(semInfoThermoflatorSettings, semGroupInfo);
        
        Semantic semInfoRoomLightSettings = factory.createSemantic("sem_InfoRoomLightSettings");
        linkSemWithSemGroup(semInfoRoomLightSettings, semGroupInfo);
        
        Semantic semInfoEndomatSettings = factory.createSemantic("sem_InfoEndomatSettings");
        linkSemWithSemGroup(semInfoEndomatSettings, semGroupInfo);
        
        Semantic semInfoTableSettings = factory.createSemantic("sem_InfoTableSettings");
        linkSemWithSemGroup(semInfoTableSettings, semGroupInfo);
        
        Semantic semConfirmSettingInfo = factory.createSemantic("sem_ConfirmSettingInfo");
        linkSemWithSemGroup(semConfirmSettingInfo, semGroupInfo);
        
        
        Semantic semInfoThermoflatorPresets = factory.createSemantic("sem_InfoThermoflatorPresets");
        linkSemWithSemGroup(semInfoThermoflatorPresets, semGroupInfo);
        
        Semantic semInfoRoomLightPresets = factory.createSemantic("sem_InfoRoomLightPresets");
        linkSemWithSemGroup(semInfoRoomLightPresets, semGroupInfo);
        
        Semantic semInfoEndomatPresets = factory.createSemantic("sem_InfoEndomatPresets");
        linkSemWithSemGroup(semInfoEndomatPresets, semGroupInfo);
        
        Semantic semInfoTablePresets = factory.createSemantic("sem_InfoTablePresets");
        linkSemWithSemGroup(semInfoTablePresets, semGroupInfo);
        
        Semantic semConfirmPresetInfo = factory.createSemantic("sem_ConfirmPresetInfo");
        linkSemWithSemGroup(semConfirmPresetInfo, semGroupInfo);
        
        
        Semantic semChangePresetsThermoflator = factory.createSemantic("sem_ChangePresetsThermoflator");
        linkSemWithSemGroup(semChangePresetsThermoflator, semGroupPresets);
        
        Semantic semChangePresetsRoomLight = factory.createSemantic("sem_ChangePresetsRoomLight");
        linkSemWithSemGroup(semChangePresetsRoomLight, semGroupPresets);
        
        Semantic semChangePresetsEndomat = factory.createSemantic("sem_ChangePresetsEndomat");
        linkSemWithSemGroup(semChangePresetsEndomat, semGroupPresets);
        
        Semantic semChangePresetsTable = factory.createSemantic("sem_ChangePresetsTable");
        linkSemWithSemGroup(semChangePresetsTable, semGroupPresets);
        
        
        Semantic semConfirmPresetChangeThermoflator = factory.createSemantic("sem_ConfirmPresetChangeThermoflator");
        linkSemWithSemGroup(semConfirmPresetChangeThermoflator, semGroupPresets);
        
        Semantic semConfirmPresetChangeRoomLight = factory.createSemantic("sem_ConfirmPresetChangeRoomLight");
        linkSemWithSemGroup(semConfirmPresetChangeRoomLight, semGroupPresets);
        
        Semantic semConfirmPresetChangeEndomat = factory.createSemantic("sem_ConfirmPresetChangeEndomat");
        linkSemWithSemGroup(semConfirmPresetChangeEndomat, semGroupPresets);
        
        Semantic semConfirmPresetChangeTable = factory.createSemantic("sem_ConfirmPresetChangeTable");
        linkSemWithSemGroup(semConfirmPresetChangeTable, semGroupPresets);
        
        
        Semantic semDenyPresetChangeThermoflator = factory.createSemantic("sem_DenyPresetChangeThermoflator");
        linkSemWithSemGroup(semDenyPresetChangeThermoflator, semGroupPresets);
        
        Semantic semDenyPresetChangeRoomLight = factory.createSemantic("sem_DenyPresetChangeRoomLight");
        linkSemWithSemGroup(semDenyPresetChangeRoomLight, semGroupPresets);
        
        Semantic semDenyPresetChangeEndomat = factory.createSemantic("sem_DenyPresetChangeEndomat");
        linkSemWithSemGroup(semDenyPresetChangeEndomat, semGroupPresets);
        
        Semantic semDenyPresetChangeTable = factory.createSemantic("sem_DenyPresetChangeTable");
        linkSemWithSemGroup(semDenyPresetChangeTable, semGroupPresets);
        
      
        
        Agenda agendaMaster = factory.createAgenda("Masteragenda");
     	agendaMaster.setIsMasterBool(false, true);

     	Agenda agendaBye = factory.createAgenda("agenda_Bye");
     	agendaBye.setRole(agendaBye.getRole(), "collection");
     	agendaBye.setPriority(0, 1000);
     	
     	Agenda agendaAbort = factory.createAgenda("agenda_Abort");
     	agendaAbort.setPriority(0, 0);
     	
     	Agenda agendaOpen = factory.createAgenda("agenda_Open");
     	agendaOpen.setRole(agendaOpen.getRole(), "collection");
     	agendaOpen.setPriority(0, 1);
     	agendaOpen.setVariableOperator("", "REQUIRES(%var_PreOpPart% == default)");
     	
     	Agenda agendaHello = factory.createAgenda("agenda_Hello");
     	agendaHello.setRole(agendaHello.getRole(), "collection");
     	agendaHello.setPriority(0, 10);
     	agendaHello.setVariableOperator("", "REQUIRES(%var_PreOpPart% == default)");
     	
     	Agenda agendaHello2 = factory.createAgenda("agenda_Hello2");
     	agendaHello2.setRole(agendaHello2.getRole(), "confirmation");
     	agendaHello2.setPriority(0, 15);
     	agendaHello2.setVariableOperator("", "REQUIRES((((%var_Info% == noInfo) && (%var_Change% == noChange)) && "
     			+ "((%var_PreOpPart% == hello) && (%var_StartOp% == 0))) || "
     			+ "(((%var_Info% == noInfo) && (%var_Change% == noChange)) && "
     			+ "((%var_PreOpPart% == hello) && (%var_StartOp% == deny))))");
     	
     	Agenda agendaAlmostReady = factory.createAgenda("agenda_AlmostReady");
     	agendaAlmostReady.setRole(agendaAlmostReady.getRole(), "collection");
     	agendaAlmostReady.setPriority(0, 20);
     	agendaAlmostReady.setVariableOperator("", "REQUIRES((%var_StaffComplete% == true) && (%var_SaidAlmostReady% == req))");
     	
     	Agenda agendaNotReady = factory.createAgenda("agendaNotReady");
     	agendaNotReady.setRole(agendaNotReady.getRole(), "collection");
     	agendaNotReady.setPriority(0, 20);
     	agendaNotReady.setVariableOperator("", "REQUIRES((%var_StaffComplete% == false) && (%var_SaidAlmostReady% == req))");
     	
     	Agenda agendaReqPresets = factory.createAgenda("agenda_ReqPresets");
     	agendaReqPresets.setRole(agendaReqPresets.getRole(), "confirmation");
     	agendaReqPresets.setPriority(0, 25);
     	agendaReqPresets.addMustnot(semConfirmPresets);
     	agendaReqPresets.setVariableOperator("", "REQUIRES("
     			+ "((%var_Info% == noInfo) && (%var_Change% == noChange)) && "
     			+ "((((%var_PreOpPart% == almostReady) && ((%var_SaidAlmostReady% == 1) && (%var_SaidAdoptedPresets% == 0))) || "
     			+ "((%var_PreOpPart% == infoDiseases) && ((%var_SaidPatientData% == 1) && (%var_SaidAdoptedPresets% == 0)))) || "
     			+ "((%var_PreOpPart% == reqPresets) && (%var_SaidAdoptedPresets% == 0)))"
     			+ ")");
     	     	
     	Agenda agendaAdoptedPresets = factory.createAgenda("agenda_AdoptedPresets");
     	agendaAdoptedPresets.setRole(agendaAdoptedPresets.getRole(), "collection");
     	agendaAdoptedPresets.setPriority(0, 50);
     	agendaAdoptedPresets.setVariableOperator("", "REQUIRES(%var_SaidAdoptedPresets% == req)");
     	
     	Agenda agendaReqTeamTimeOut = factory.createAgenda("agenda_ReqTeamTimeOut");
     	agendaReqTeamTimeOut.setRole(agendaReqTeamTimeOut.getRole(), "confirmation");
     	agendaReqTeamTimeOut.setPriority(0, 55);
     	agendaReqTeamTimeOut.setVariableOperator("", "REQUIRES("
     			+ "((%var_Info% == noInfo) && (%var_Change% == noChange)) && "
     			+ "(((%var_PreOpPart% == adoptedPresets) && ((%var_SaidAdoptedPresets% == 1) && (%var_SaidPatientData% == 0))) || "
     			+ "((%var_PreOpPart% == reqTeamTimeOut) && (%var_SaidPatientData% == 0)))"
     			+ ")");
     	
     	Agenda agendaInfoPatientData = factory.createAgenda("agenda_InfoPatientData");
     	agendaInfoPatientData.addRequires(semConfirmTeamTimeOut);
     	agendaInfoPatientData.setRole(agendaInfoPatientData.getRole(), "confirmation");
     	agendaInfoPatientData.setPriority(0, 60);
     	agendaInfoPatientData.setVariableOperator("", "REQUIRES(%var_SaidPatientData% == req)");
     	
     	Agenda agendaInfoDiseases = factory.createAgenda("agenda_InfoDiseases");
     	agendaInfoDiseases.addRequires(semConfirmInfoPatient);
     	agendaInfoDiseases.setRole(agendaInfoDiseases.getRole(), "collection");
     	agendaInfoDiseases.setPriority(0, 70);
     	agendaInfoDiseases.setVariableOperator("", "REQUIRES((%var_PreOpPart% == infoPatientData) && (%var_SaidPatientData% == req))");
     	
     	Agenda agendaWrongPatientInfo = factory.createAgenda("agenda_WrongPatientInfo");
     	agendaWrongPatientInfo.setRole(agendaWrongPatientInfo.getRole(), "collection");
     	agendaWrongPatientInfo.setPriority(0, 7000);
     	agendaWrongPatientInfo.addRequires(semDenyInfoPatient);
     	
     	Agenda agendaStartOp = factory.createAgenda("agenda_StartOp");
     	agendaStartOp.setRole(agendaStartOp.getRole(), "confirmation");
     	agendaStartOp.setPriority(0, 75);
     	agendaStartOp.setVariableOperator("", "REQUIRES("
     			+ "(((%var_Info% == noInfo) && (%var_Change% == noChange)) && (%var_StartOp% != deny)) && "
				+ "((%var_StartOp% == req) || "
				+ "(((%var_SaidPatientData% == 1) || (%var_SaidPatientData% == deny)) && "
				+ "((%var_SaidAdoptedPresets% == 1) && (%var_SaidAlmostReady% != req))))"
     			+ ")");
     	
     	Agenda agendaExit = factory.createAgenda("agenda_Exit");
     	agendaExit.setRole(agendaExit.getRole(), "confirmation");
     	agendaExit.setPriority(0, 2000);
     	agendaExit.setVariableOperator("", "REQUIRES(%var_Exit% == req)");
     	
     	
     	Agenda agendaInfoMedication = factory.createAgenda("agenda_InfoMedication");
     	agendaInfoMedication.setRole(agendaInfoMedication.getRole(), "collection");
     	agendaInfoMedication.setPriority(0, 2000);
     	agendaInfoMedication.setVariableOperator("", "REQUIRES(%var_Info%==medication)");
     	
     	Agenda agendaInfoLabData = factory.createAgenda("agenda_InfoLabData");
     	agendaInfoLabData.setRole(agendaInfoLabData.getRole(), "collection");
     	agendaInfoLabData.setPriority(0, 2000);
     	agendaInfoLabData.setVariableOperator("", "REQUIRES(%var_Info%==labData)");
     	
     	Agenda agendaInfoThermoflatorSettings = factory.createAgenda("agenda_InfoThermoflatorSettings");
     	agendaInfoThermoflatorSettings.setRole(agendaInfoThermoflatorSettings.getRole(), "collection");
     	agendaInfoThermoflatorSettings.setPriority(0, 2000);
     	agendaInfoThermoflatorSettings.setVariableOperator("", "REQUIRES(%var_Info%==ThermoflatorSettings)");

     	Agenda agendaInfoRoomLightSettings = factory.createAgenda("agenda_InfoRoomLightSettings");
     	agendaInfoRoomLightSettings.setRole(agendaInfoRoomLightSettings.getRole(), "collection");
     	agendaInfoRoomLightSettings.setPriority(0, 2000);
     	agendaInfoRoomLightSettings.setVariableOperator("", "REQUIRES(%var_Info%==RoomLightSettings)");

     	Agenda agendaInfoEndomatSettings = factory.createAgenda("agenda_InfoEndomatSettings");
     	agendaInfoEndomatSettings.setRole(agendaInfoEndomatSettings.getRole(), "collection");
     	agendaInfoEndomatSettings.setPriority(0, 2000);
     	agendaInfoEndomatSettings.setVariableOperator("", "REQUIRES(%var_Info%==EndomatSettings)");

     	Agenda agendaInfoTableSettings = factory.createAgenda("agenda_InfoTableSettings");
     	agendaInfoTableSettings.setRole(agendaInfoTableSettings.getRole(), "collection");
     	agendaInfoTableSettings.setPriority(0, 2000);
     	agendaInfoTableSettings.setVariableOperator("", "REQUIRES(%var_Info%==TableSettings)");
     	
     	
     	Agenda agendaInfoThermoflatorPresets = factory.createAgenda("agenda_InfoThermoflatorPresets");
     	agendaInfoThermoflatorPresets.setRole(agendaInfoThermoflatorPresets.getRole(), "collection");
     	agendaInfoThermoflatorPresets.setPriority(0, 2000);
     	agendaInfoThermoflatorPresets.setVariableOperator("", "REQUIRES(%var_Info%==ThermoflatorPresets)");

     	Agenda agendaInfoRoomLightPresets = factory.createAgenda("agenda_InfoRoomLightPresets");
     	agendaInfoRoomLightPresets.setRole(agendaInfoRoomLightPresets.getRole(), "collection");
     	agendaInfoRoomLightPresets.setPriority(0, 2000);
     	agendaInfoRoomLightPresets.setVariableOperator("", "REQUIRES(%var_Info%==RoomLightPresets)");

     	Agenda agendaInfoEndomatPresets = factory.createAgenda("agenda_InfoEndomatPresets");
     	agendaInfoEndomatPresets.setRole(agendaInfoEndomatPresets.getRole(), "collection");
     	agendaInfoEndomatPresets.setPriority(0, 2000);
     	agendaInfoEndomatPresets.setVariableOperator("", "REQUIRES(%var_Info%==EndomatPresets)");

     	Agenda agendaInfoTablePresets = factory.createAgenda("agenda_InfoTablePresets");
     	agendaInfoTablePresets.setRole(agendaInfoTablePresets.getRole(), "collection");
     	agendaInfoTablePresets.setPriority(0, 2000);
     	agendaInfoTablePresets.setVariableOperator("", "REQUIRES(%var_Info%==TablePresets)");
     	
     	
     	Agenda agendaChangePresetsThermoflator1 = factory.createAgenda("agenda_ChangePresetsThermoflator1");
     	agendaChangePresetsThermoflator1.setRole(agendaChangePresetsThermoflator1.getRole(), "collection");
     	agendaChangePresetsThermoflator1.setPriority(0, 3000);
     	agendaChangePresetsThermoflator1.setVariableOperator("", "REQUIRES(%var_Change%==ThermoflatorPresets1)");
     	
     	Agenda agendaChangePresetsThermoflator2 = factory.createAgenda("agenda_ChangePresetsThermoflator2");
     	agendaChangePresetsThermoflator2.setRole(agendaChangePresetsThermoflator2.getRole(), "collection");
     	agendaChangePresetsThermoflator2.setPriority(0, 3000);
     	agendaChangePresetsThermoflator2.setVariableOperator("", "REQUIRES(%var_Change%==ThermoflatorPresets2)");
     	agendaChangePresetsThermoflator1.addNext(agendaChangePresetsThermoflator2);
     	
     	Agenda agendaChangePresetsRoomLight = factory.createAgenda("agenda_ChangePresetsRoomLight");
     	agendaChangePresetsRoomLight.setRole(agendaChangePresetsRoomLight.getRole(), "collection");
     	agendaChangePresetsRoomLight.setPriority(0, 3000);
     	agendaChangePresetsRoomLight.setVariableOperator("", "REQUIRES(%var_Change%==RoomLightPresets)");
     	
     	Agenda agendaChangePresetsEndomat = factory.createAgenda("agenda_ChangePresetsEndomat");
     	agendaChangePresetsEndomat.setRole(agendaChangePresetsEndomat.getRole(), "collection");
     	agendaChangePresetsEndomat.setPriority(0, 3000);
     	agendaChangePresetsEndomat.setVariableOperator("", "REQUIRES(%var_Change%==EndomatPresets)");
     	
     	Agenda agendaChangePresetsTable1 = factory.createAgenda("agenda_ChangePresetsTable1");
     	agendaChangePresetsTable1.setRole(agendaChangePresetsTable1.getRole(), "collection");
     	agendaChangePresetsTable1.setPriority(0, 3000);
     	agendaChangePresetsTable1.setVariableOperator("", "REQUIRES(%var_Change%==TablePresets1)");
     	
     	Agenda agendaChangePresetsTable2 = factory.createAgenda("agenda_ChangePresetsTable2");
     	agendaChangePresetsTable2.setRole(agendaChangePresetsTable2.getRole(), "collection");
     	agendaChangePresetsTable2.setPriority(0, 3001);
     	agendaChangePresetsTable2.setVariableOperator("", "REQUIRES(%var_Change%==TablePresets2)");
     	agendaChangePresetsTable1.addNext(agendaChangePresetsTable2);
     	
     	Agenda agendaChangedPresetsThermoflator = factory.createAgenda("agenda_ChangedPresetsThermoflator");
     	agendaChangedPresetsThermoflator.setRole(agendaChangedPresetsThermoflator.getRole(), "collection");
     	agendaChangedPresetsThermoflator.setPriority(0, 3010);
     	agendaChangedPresetsThermoflator.setVariableOperator("", "REQUIRES(%var_Change%==changedThermoflator)");
     	agendaChangePresetsThermoflator2.addNext(agendaChangedPresetsThermoflator);
     	
     	Agenda agendaChangedPresetsRoomLight = factory.createAgenda("agenda_ChangedPresetsRoomLight");
     	agendaChangedPresetsRoomLight.setRole(agendaChangedPresetsRoomLight.getRole(), "collection");
     	agendaChangedPresetsRoomLight.setPriority(0, 3010);
     	agendaChangedPresetsRoomLight.setVariableOperator("", "REQUIRES(%var_Change%==changedRoomLight)");
     	agendaChangePresetsRoomLight.addNext(agendaChangedPresetsRoomLight);
     	
     	Agenda agendaChangedPresetsEndomat = factory.createAgenda("agenda_ChangedPresetsEndomat");
     	agendaChangedPresetsEndomat.setRole(agendaChangedPresetsEndomat.getRole(), "collection");
     	agendaChangedPresetsEndomat.setPriority(0, 3010);
     	agendaChangedPresetsEndomat.setVariableOperator("", "REQUIRES(%var_Change%==changedEndomat)");
     	agendaChangePresetsEndomat.addNext(agendaChangedPresetsEndomat);
     	
     	Agenda agendaChangedPresetsTable = factory.createAgenda("agenda_ChangedPresetsTable");
     	agendaChangedPresetsTable.setRole(agendaChangedPresetsTable.getRole(), "collection");
     	agendaChangedPresetsTable.setPriority(0, 3010);
     	agendaChangedPresetsTable.setVariableOperator("", "REQUIRES(%var_Change%==changedTable)");
     	agendaChangePresetsTable2.addNext(agendaChangedPresetsTable);
     	
     	Agenda agendaSavedChanges = factory.createAgenda("agenda_SavedChanges");
     	agendaSavedChanges.setRole(agendaSavedChanges.getRole(), "collection");
     	agendaSavedChanges.setPriority(0, 3100);
     	agendaSavedChanges.setVariableOperator("", "REQUIRES(((%var_Change%==cThermoflator) || (%var_Change%==cRoomLight)) || "
     			+ "((%var_Change%==cEndomat) || (%var_Change%==cTable)))");
     	agendaChangedPresetsThermoflator.addNext(agendaSavedChanges);
     	agendaChangedPresetsRoomLight.addNext(agendaSavedChanges);
     	agendaChangedPresetsEndomat.addNext(agendaSavedChanges);
     	agendaChangedPresetsTable.addNext(agendaSavedChanges);
     	
     	Agenda agendaDroppedChanges = factory.createAgenda("agenda_DroppedChanges");
     	agendaDroppedChanges.setRole(agendaDroppedChanges.getRole(), "collection");
     	agendaDroppedChanges.setPriority(0, 3100);
     	agendaDroppedChanges.setVariableOperator("", "REQUIRES(((%var_Change%==dThermoflator) || (%var_Change%==dRoomLight)) || "
     			+ "((%var_Change%==dEndomat) || (%var_Change%==dTable)))");
     	agendaChangedPresetsThermoflator.addNext(agendaDroppedChanges);
     	agendaChangedPresetsRoomLight.addNext(agendaDroppedChanges);
     	agendaChangedPresetsEndomat.addNext(agendaDroppedChanges);
     	agendaChangedPresetsTable.addNext(agendaDroppedChanges);
     	  	
     	
     	Agenda agendaOp2 = factory.createAgenda("agenda_Op2");
     	agendaOp2.setPriority(0, 100);
     	
     	Agenda agendaOp3 = factory.createAgenda("agenda_Op3");
     	agendaOp3.setPriority(0, 120);
     	
     	Agenda agendaOp4 = factory.createAgenda("agenda_Op4");
     	agendaOp4.setPriority(0, 140);
     	
     	Agenda agendaOp5 = factory.createAgenda("agenda_Op5");
     	agendaOp5.setPriority(0, 160);
     	
     	Agenda agendaOp6 = factory.createAgenda("agenda_Op6");
     	agendaOp6.setPriority(0, 180);
     	
     	Agenda agendaOp7 = factory.createAgenda("agenda_Op7");
     	agendaOp7.setPriority(0, 200);
     	
     	Agenda agendaOp8 = factory.createAgenda("agenda_Op8");
     	agendaOp8.setPriority(0, 220);
     	
     	Agenda agendaOp9 = factory.createAgenda("agenda_Op9");
     	agendaOp9.setPriority(0, 240);
     	
     	Agenda agendaOp10 = factory.createAgenda("agenda_Op10");
     	agendaOp10.setPriority(0, 260);
     	
     	
     	agendaMaster.addNext(agendaOpen);     	
     	agendaOpen.addNext(agendaHello);   
     	agendaHello.addNext(agendaHello2);
     	agendaHello2.addNext(agendaNotReady);
     	agendaNotReady.addNext(agendaHello2);
     	agendaHello2.addNext(agendaAlmostReady);
     	agendaHello2.addNext(agendaAdoptedPresets);
     	agendaHello2.addNext(agendaInfoPatientData);
     	agendaHello2.addNext(agendaStartOp);
     	agendaInfoPatientData.addNext(agendaInfoDiseases);
     	agendaInfoPatientData.addNext(agendaWrongPatientInfo);
     	agendaAlmostReady.addNext(agendaReqPresets);
     	agendaAlmostReady.addNext(agendaStartOp);
     	agendaReqPresets.addNext(agendaAdoptedPresets);
     	agendaAdoptedPresets.addNext(agendaReqTeamTimeOut);
     	agendaAdoptedPresets.addNext(agendaStartOp);
     	agendaReqTeamTimeOut.addNext(agendaInfoPatientData);
     	agendaReqTeamTimeOut.addNext(agendaStartOp);
     	agendaInfoDiseases.addNext(agendaReqPresets);
     	agendaInfoDiseases.addNext(agendaStartOp);
     	agendaStartOp.addNext(agendaHello2);
     	agendaStartOp.addNext(agendaNotReady);
     	agendaNotReady.addNext(agendaStartOp);
     	agendaStartOp.addNext(agendaAlmostReady);
     	agendaStartOp.addNext(agendaAdoptedPresets);
     	agendaStartOp.addNext(agendaExit);
     	agendaExit.addNext(agendaStartOp);
     	agendaExit.addNext(agendaBye);
     	
     	
     	SummaryAgenda sumAgendaStart = factory.createSummaryAgenda("sumAgenda_Start");
     	sumAgendaStart.setType(sumAgendaStart.getTypeString(), "request");
     	sumAgendaStart.setRole(sumAgendaStart.getRole(), "collection");
		
		SummaryAgenda sumAgendaBye = factory.createSummaryAgenda("sumAgenda_Bye");
		sumAgendaBye.setType(sumAgendaBye.getTypeString(), "announcement");
		sumAgendaBye.setRole(sumAgendaBye.getRole(), "collection");
		
		SummaryAgenda sumAgendaOp = factory.createSummaryAgenda("sumAgenda_Op");
		sumAgendaOp.setType(sumAgendaOp.getTypeString(), "request");
		sumAgendaOp.setRole(sumAgendaOp.getRole(), "collection");
		
		SummaryAgenda sumAgendaInfo = factory.createSummaryAgenda("sumAgenda_Info");
		sumAgendaInfo.setType(sumAgendaInfo.getTypeString(), "request");
		sumAgendaInfo.setRole(sumAgendaInfo.getRole(), "collection");
		
		SummaryAgenda sumAgendaChange = factory.createSummaryAgenda("sumAgenda_Change");
		sumAgendaChange.setType(sumAgendaChange.getTypeString(), "request");
		sumAgendaChange.setRole(sumAgendaChange.getRole(), "collection");
		
		SummaryAgenda sumAgendaWarn = factory.createSummaryAgenda("sumAgenda_Warn");
		sumAgendaWarn.setType(sumAgendaWarn.getTypeString(), "announcement");
		sumAgendaWarn.setRole(sumAgendaWarn.getRole(), "collection");
		
		
		linkAgendaWithSumAgenda(agendaOpen, sumAgendaStart);
		linkAgendaWithSumAgenda(agendaHello, sumAgendaStart);
		linkAgendaWithSumAgenda(agendaHello2, sumAgendaStart);
		linkAgendaWithSumAgenda(agendaNotReady, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaAlmostReady, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaReqPresets, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaAdoptedPresets, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaReqTeamTimeOut, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoPatientData, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoDiseases, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaWrongPatientInfo, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaStartOp, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaBye, sumAgendaBye);
		linkAgendaWithSumAgenda(agendaAbort, sumAgendaBye);
		linkAgendaWithSumAgenda(agendaExit, sumAgendaBye);
		
		linkAgendaWithSumAgenda(agendaInfoMedication, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoLabData, sumAgendaInfo);
		
		linkAgendaWithSumAgenda(agendaInfoThermoflatorSettings, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoRoomLightSettings, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoEndomatSettings, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoTableSettings, sumAgendaInfo);
		
		linkAgendaWithSumAgenda(agendaInfoThermoflatorPresets, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoRoomLightPresets, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoEndomatPresets, sumAgendaInfo);
		linkAgendaWithSumAgenda(agendaInfoTablePresets, sumAgendaInfo);
		
		linkAgendaWithSumAgenda(agendaChangePresetsThermoflator1, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangePresetsThermoflator2, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangePresetsRoomLight, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangePresetsEndomat, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangePresetsTable1, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangePresetsTable2, sumAgendaChange);
		
		linkAgendaWithSumAgenda(agendaChangedPresetsThermoflator, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangedPresetsRoomLight, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangedPresetsEndomat, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaChangedPresetsTable, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaSavedChanges, sumAgendaChange);
		linkAgendaWithSumAgenda(agendaDroppedChanges, sumAgendaChange);
		
		

		//create grammars
		
		Grammar grHello = grammarHello(factory);
		Grammar grBye = grammarBye(factory);
		Grammar grExit = grammarExit(factory);
		Grammar grChangePresetsThermoflator = grammarChangePresetsThermoflator(factory);
		Grammar grChangePresetsRoomLight = grammarChangePresetsRoomLight(factory);
		Grammar grChangePresetsEndomat = grammarChangePresetsEndomat(factory);
		Grammar grChangePresetsTable = grammarChangePresetsTable(factory);
		Grammar grPresetsThermoflator = grammarPresetsThermoflator(factory);
		Grammar grPresetsRoomLight = grammarPresetsRoomLight(factory);
		Grammar grPresetsEndomat = grammarPresetsEndomat(factory);
		Grammar grPresetsTable = grammarPresetsTable(factory);
		Grammar grSettingsThermoflator = grammarSettingsThermoflator(factory);
		Grammar grSettingsRoomLight = grammarSettingsRoomLight(factory);
		Grammar grSettingsEndomat = grammarSettingsEndomat(factory);
		Grammar grSettingsTable = grammarSettingsTable(factory);
		Grammar grUndoStartGas = grammarUndoStartGas(factory);
		Grammar grUndoMaxGas = grammarUndoMaxGas(factory);
		Grammar grUndoRoomLightOn = grammarUndoRoomLightOn(factory);
		Grammar grUndoTableRevTrendLightOff = grammarUndoTableRevTrendLightOff(factory);
		Grammar grUndoTableUp = grammarUndoTableUp(factory);
		Grammar grRequestStatus = grammarRequestStatus(factory);
		Grammar grAdoptPresets = grammarAdoptPresets(factory);
		Grammar grPatientInfo = grammarPatientInfo(factory);
		Grammar grMedication = grammarMedication(factory);
		Grammar grLabData = grammarLabData(factory);
		Grammar grDeny = grammarDeny(factory);
	 	Grammar grAffirm = grammarAffirm(factory);
		Grammar grThankyou = grammarThankyou(factory);
		Grammar grStartOp = grammarStartOp(factory);
		Grammar grGas = grammarGas(factory);
		Grammar grGasConnection = grammarGasConnection(factory);
		Grammar grMaxGas = grammarMaxGas(factory);
		Grammar grTableRevTrendLightOff = grammarTableRevTrendLightOff(factory);
		Grammar grCleanOptics = grammarCleanOptics(factory);
		Grammar grMoveOptics = grammarMoveOptics(factory);
		Grammar grHandOver = grammarHandOver(factory);
		Grammar grFocus = grammarFocus(factory);
		Grammar grPowerPE = grammarPowerPE(factory);
		Grammar grDistance = grammarDistance(factory);
		Grammar grLightOn = grammarLightOn(factory);
		Grammar grMoveGas = grammarMoveGas(factory);
		Grammar grAspiratorToAssistant = grammarAspiratorToAssistant(factory);
		Grammar grSuction = grammarSuction(factory);
		Grammar grClean = grammarClean(factory);
		Grammar grFinger = grammarFinger(factory);
		Grammar grConnectGas = grammarConnectGas(factory);
		Grammar grMoveCameraToHilus = grammarMoveCameraToHilus(factory);
		Grammar grMoveCameraToLiver = grammarMoveCameraToLiver(factory);
		Grammar grRemoveTrocars = grammarRemoveTrocars(factory);
		Grammar grOneMore = grammarOneMore(factory);
		Grammar grStartEmergencyMode = grammarStartEmergencyMode(factory);
		Grammar grEndEmergencyMode = grammarEndEmergencyMode(factory);		
		Grammar grCorrectCntTo0 = grammarCorrectCntTo0(factory);
		for (int n = 1; n < 10; n++) {
			Grammar grCorrectCntToN = grammarCorrectCntToN(factory, n);
		}		
		
		
		//general system moves
		
		Move moveSysOpen = createSysMoveWithUtterance(factory, "Open");
		agendaOpen.addHas(moveSysOpen);
		
     	Move moveSysAbort = createSysMoveWithUtterance(factory, "Abort");
     	agendaAbort.addHas(moveSysAbort);
     	moveSysAbort.setIsExitMove(false, true);
     	
     	Move moveSysExit = createSysMoveWithUtterance(factory, "Exit");
     	agendaExit.addHas(moveSysExit);
     	
		Move moveSysHello = createSysMoveWithUtterance(factory, "Hello");
		agendaHello.addHas(moveSysHello);
		moveSysHello.setVariableOperator("SET(" + varPreOpPart + "=hello)");

     	Move moveSysBye = createSysMoveWithUtterance(factory, "Bye");
     	agendaBye.addHas(moveSysBye);
     	moveSysBye.setIsExitMove(false, true);
     	
     	Move moveSysNotReady = createSysMoveWithUtterance(factory, "NotReady");
     	agendaNotReady.addHas(moveSysNotReady);
     	moveSysNotReady.setVariableOperator("SET(" + varSaidAlmostReady + "=0; " + varPreOpPart + "=hello)");
     	
     	Move moveSysAlmostReady = createSysMoveWithUtterance(factory, "AlmostReady");
     	agendaAlmostReady.addHas(moveSysAlmostReady);
     	moveSysAlmostReady.setVariableOperator("SET(" + varSaidAlmostReady + "=1; " + varPreOpPart + "=almostReady)");

     	Move moveSysReqPresets = createSysMoveWithUtterance(factory, "ReqPresets");
     	agendaReqPresets.addHas(moveSysReqPresets);
     	moveSysReqPresets.setVariableOperator("SET(" + varPreOpPart + "=reqPresets)");
     	
     	Move moveSysAdoptedPresets =createSysMoveWithUtterance(factory, "AdoptedPresets");
     	agendaAdoptedPresets.addHas(moveSysAdoptedPresets);
     	moveSysAdoptedPresets.setVariableOperator("SET(" + varSaidAdoptedPresets + "=1; " + varPreOpPart + "=adoptedPresets)");
     	
     	Move moveSysReqTeamTimeOut =createSysMoveWithUtterance(factory, "ReqTeamTimeOut");
     	agendaReqTeamTimeOut.addHas(moveSysReqTeamTimeOut);
     	moveSysReqTeamTimeOut.setVariableOperator("SET(" + varPreOpPart + "=reqTeamTimeOut)");
     	
     	Move moveSysInfoPatientData = createSysMoveWithUtterance(factory, "InfoPatientData");
     	agendaInfoPatientData.addHas(moveSysInfoPatientData);
     	moveSysInfoPatientData.setVariableOperator("SET(" + varPreOpPart + "=infoPatientData)");
     	
     	Move moveSysInfoDiseases = createSysMoveWithUtterance(factory, "InfoDiseases");
     	agendaInfoDiseases.addHas(moveSysInfoDiseases);
     	moveSysInfoDiseases.setVariableOperator("SET(" + varSaidPatientData + "=1; " + varPreOpPart + "=infoDiseases)");
     	
     	Move moveSysWrongPatientInfo = createSysMoveWithUtterance(factory, "WrongPatientInfo");
     	agendaWrongPatientInfo.addHas(moveSysWrongPatientInfo);
     	moveSysWrongPatientInfo.setIsExitMove(false, true);
     	
     	Move moveSysStartOp = createSysMoveWithUtterance(factory, "StartOp");
     	agendaStartOp.addHas(moveSysStartOp);
     	moveSysStartOp.setVariableOperator("SET(" + varPreOpPart + "=startOp; " + varStartOp + "=0)");
     	
     	
     	Move moveSysChangePresetsThermoflator1 = createSysMoveWithUtterance(factory, "ChangePresetsThermoflator1");
     	agendaChangePresetsThermoflator1.addHas(moveSysChangePresetsThermoflator1);
     	
     	Move moveSysChangePresetsThermoflator2 = createSysMoveWithUtterance(factory, "ChangePresetsThermoflator2");
     	agendaChangePresetsThermoflator2.addHas(moveSysChangePresetsThermoflator2);
     	
     	Move moveSysChangePresetsRoomLight = createSysMoveWithUtterance(factory, "ChangePresetsRoomLight");
     	agendaChangePresetsRoomLight.addHas(moveSysChangePresetsRoomLight);
     	
     	Move moveSysChangePresetsEndomat = createSysMoveWithUtterance(factory, "ChangePresetsEndomat");
     	agendaChangePresetsEndomat.addHas(moveSysChangePresetsEndomat);
     	
     	Move moveSysChangePresetsTable1 = createSysMoveWithUtterance(factory, "ChangePresetsTable1");
     	agendaChangePresetsTable1.addHas(moveSysChangePresetsTable1);
     	
     	Move moveSysChangePresetsTable2 = createSysMoveWithUtterance(factory, "ChangePresetsTable2");
     	agendaChangePresetsTable2.addHas(moveSysChangePresetsTable2);
     	
     	
     	Move moveSysChangedPresetsThermoflator = createSysMoveWithUtterance(factory, "ChangedPresetsThermoflator");
     	agendaChangedPresetsThermoflator.addHas(moveSysChangedPresetsThermoflator);
     	
     	Move moveSysChangedPresetsRoomLight = createSysMoveWithUtterance(factory, "ChangedPresetsRoomLight");
     	agendaChangedPresetsRoomLight.addHas(moveSysChangedPresetsRoomLight);
     	
     	Move moveSysChangedPresetsEndomat = createSysMoveWithUtterance(factory, "ChangedPresetsEndomat");
     	agendaChangedPresetsEndomat.addHas(moveSysChangedPresetsEndomat);
     	
     	Move moveSysChangedPresetsTable = createSysMoveWithUtterance(factory, "ChangedPresetsTable");
     	agendaChangedPresetsTable.addHas(moveSysChangedPresetsTable);
     	
     	Move moveSysSavedChanges = createSysMoveWithUtterance(factory, "SavedChanges");
     	agendaSavedChanges.addHas(moveSysSavedChanges);
     	moveSysSavedChanges.setVariableOperator("SET(" + varChange + "=noChange)");
     	
     	Move moveSysDroppedChanges = createSysMoveWithUtterance(factory, "DroppedChanges");
     	agendaDroppedChanges.addHas(moveSysDroppedChanges);
     	moveSysDroppedChanges.setVariableOperator("SET(" + varChange + "=noChange)");
     	
     	
     	Move moveSysInfoThermoflatorPresets = createSysMoveWithUtterance(factory, "InfoThermoflatorPresets");
     	moveSysInfoThermoflatorPresets.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoThermoflatorPresets.addHas(moveSysInfoThermoflatorPresets);
     	
     	Move moveSysInfoRoomLightPresets = createSysMoveWithUtterance(factory, "InfoRoomLightPresets");
     	moveSysInfoRoomLightPresets.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoRoomLightPresets.addHas(moveSysInfoRoomLightPresets);
     	
     	Move moveSysInfoEndomatPresets = createSysMoveWithUtterance(factory, "InfoEndomatPresets");
     	moveSysInfoEndomatPresets.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoEndomatPresets.addHas(moveSysInfoEndomatPresets);
     	
     	Move moveSysInfoTablePresets = createSysMoveWithUtterance(factory, "InfoTablePresets");
     	moveSysInfoTablePresets.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoTablePresets.addHas(moveSysInfoTablePresets);
     	
     	
     	Move moveSysInfoThermoflatorSettings = createSysMoveWithUtterance(factory, "InfoThermoflatorSettings");
     	moveSysInfoThermoflatorSettings.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoThermoflatorSettings.addHas(moveSysInfoThermoflatorSettings);
     	
     	Move moveSysInfoRoomLightSettings = createSysMoveWithUtterance(factory, "InfoRoomLightSettings");
     	moveSysInfoRoomLightSettings.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoRoomLightSettings.addHas(moveSysInfoRoomLightSettings);
     	
     	Move moveSysInfoEndomatSettings = createSysMoveWithUtterance(factory, "InfoEndomatSettings");
     	moveSysInfoEndomatSettings.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoEndomatSettings.addHas(moveSysInfoEndomatSettings);
     	
     	Move moveSysInfoTableSettings = createSysMoveWithUtterance(factory, "InfoTableSettings");
     	moveSysInfoTableSettings.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoTableSettings.addHas(moveSysInfoTableSettings);
     	
     	
     	Move moveSysMedication = createSysMoveWithUtterance(factory, "Medication");
     	moveSysMedication.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoMedication.addHas(moveSysMedication);
     	
     	Move moveSysLabData = createSysMoveWithUtterance(factory, "LabData");
     	moveSysLabData.setVariableOperator("SET(" + varInfo + "=noInfo)");
     	agendaInfoLabData.addHas(moveSysLabData);
     	
     	
     	
		//general user moves
     	
		Move moveUserHello = createUserMoveWithGrammarAndSem(factory, grHello, semHello);
		agendaOpen.addHas(moveUserHello);
		
		Move moveUserRequestStatus = createUserMoveWithGrammarAndSem(factory, grRequestStatus, semAlmostReady);
		agendaHello2.addHas(moveUserRequestStatus);
		agendaStartOp.addHas(moveUserRequestStatus);
		moveUserRequestStatus.setVariableOperator("SET(" + varSaidAlmostReady + "=req)");

		Move moveUserAdoptPresets = createUserMoveWithGrammarAndSem(factory, grAdoptPresets, semAdoptPresets);
		moveUserAdoptPresets.setVariableOperator("SET("
				+ varDB_dev_Thermoflator_TargetPressure + "=%varDB_setup_Insufflator_TargetPressure%; "
				+ varDB_dev_Thermoflator_TargetFlow + "=0; "
				+ varDB_dev_RoomLight_TargetIntensity +  "=%varDB_setup_Lichtquelle_TargetIntensity%; "
				+ varDB_dev_Endomat_TargetFlow + "=%varDB_setup_Saug-Spuel-Einheit_TargetValue-F%; "
				+ varDB_dev_Table_Height + "=%varDB_setup_Tisch_TableHeight%; "
				+ varDB_dev_Table_Tilt + "=0; "
				+ varSaidAdoptedPresets + "=req; "
				+ varApplyPresets + "= )");
		agendaHello2.addHas(moveUserAdoptPresets);
		agendaStartOp.addHas(moveUserAdoptPresets);
		
		Move moveUserPatientInfo = createUserMoveWithGrammarAndSem(factory, grPatientInfo, semConfirmTeamTimeOut);
		moveUserPatientInfo.setVariableOperator("SET(" + varSaidPatientData + "=req)");
		agendaHello2.addHas(moveUserPatientInfo);
		
		Move moveUserBye = createUserMoveWithGrammarAndSem(factory, grBye, semBye);
		
		Move moveUserExit = createUserMoveWithGrammarAndSem(factory, grExit, semExit);
		moveUserExit.setVariableOperator("SET(" + varExit + "=req)");
		
		
		Move moveUserMedication = createUserMoveWithGrammarAndSem(factory, grMedication, semMedication);
		moveUserMedication.setVariableOperator("SET(" + varInfo + "=medication)");
		
		Move moveUserLabData = createUserMoveWithGrammarAndSem(factory, grLabData, semLabData);
		moveUserLabData.setVariableOperator("SET(" + varInfo + "=labData)");
		
		
		
		//user moves for info about device settings
		
		Move moveUserSettingsThermoflator = createUserMoveWithGrammarAndSem(factory, grSettingsThermoflator, semInfoThermoflatorSettings);
		moveUserSettingsThermoflator.setVariableOperator("SET(" + varInfo + "=ThermoflatorSettings)");
		
		Move moveUserSettingsRoomLight = createUserMoveWithGrammarAndSem(factory, grSettingsRoomLight, semInfoRoomLightSettings);
		moveUserSettingsRoomLight.setVariableOperator("SET(" + varInfo + "=RoomLightSettings)");
		
		Move moveUserSettingsEndomat = createUserMoveWithGrammarAndSem(factory, grSettingsEndomat, semInfoEndomatSettings);
		moveUserSettingsEndomat.setVariableOperator("SET(" + varInfo + "=EndomatSettings)");
		
		Move moveUserSettingsTable = createUserMoveWithGrammarAndSem(factory, grSettingsTable, semInfoTableSettings);
		moveUserSettingsTable.setVariableOperator("SET(" + varInfo + "=TableSettings)");
		
		
		
		//user moves for info about device presets
		
		Move moveUserPresetsThermoflator = createUserMoveWithGrammarAndSem(factory, grPresetsThermoflator, semInfoThermoflatorPresets);
		moveUserPresetsThermoflator.setVariableOperator("SET(" + varInfo + "=ThermoflatorPresets)");
				
		Move moveUserPresetsRoomLight = createUserMoveWithGrammarAndSem(factory, grPresetsRoomLight, semInfoRoomLightPresets);
		moveUserPresetsRoomLight.setVariableOperator("SET(" + varInfo + "=RoomLightPresets)");
	
		Move moveUserPresetsEndomat = createUserMoveWithGrammarAndSem(factory, grPresetsEndomat, semInfoEndomatPresets);
		moveUserPresetsEndomat.setVariableOperator("SET(" + varInfo + "=EndomatPresets)");
			
		Move moveUserPresetsTable = createUserMoveWithGrammarAndSem(factory, grPresetsTable, semInfoTablePresets);
		moveUserPresetsTable.setVariableOperator("SET(" + varInfo + "=TablePresets)");
		
		
		
		//user moves for changing presets
		
		Move moveUserChangePresetsThermoflator = createUserMoveWithGrammarAndSem(factory, grChangePresetsThermoflator, semChangePresetsThermoflator);
		moveUserChangePresetsThermoflator.setVariableOperator("SET(" + varChange + "=ThermoflatorPresets1)");
		
		Move moveUserChangePresetsRoomLight = createUserMoveWithGrammarAndSem(factory, grChangePresetsRoomLight, semChangePresetsRoomLight);
		moveUserChangePresetsRoomLight.setVariableOperator("SET(" + varChange + "=RoomLightPresets)");
		
		Move moveUserChangePresetsEndomat = createUserMoveWithGrammarAndSem(factory, grChangePresetsEndomat, semChangePresetsEndomat);
		moveUserChangePresetsEndomat.setVariableOperator("SET(" + varChange + "=EndomatPresets)");
		
		Move moveUserChangePresetsTable = createUserMoveWithGrammarAndSem(factory, grChangePresetsTable, semChangePresetsTable);
		moveUserChangePresetsTable.setVariableOperator("SET(" + varChange + "=TablePresets1)");
		
		
		Move moveUserConfirmPresetChangeThermoflator = factory.createMove("move_user_ConfirmPresetChangeThermoflator");
		addGrammarToMove(moveUserConfirmPresetChangeThermoflator, grAffirm);
		addSemAndContrarySem(moveUserConfirmPresetChangeThermoflator, semConfirmPresetChangeThermoflator, semDenyPresetChangeThermoflator);
		moveUserConfirmPresetChangeThermoflator.setVariableOperator("SET("
				+ varPresetThermoflatorTargetPressure + "=%var_TempPresetThermoflatorTargetPressure%; "
				+ varPresetThermoflatorTargetFlow + "=%var_TempPresetThermoflatorTargetFlow%; "
				+ varChange + "=cThermoflator)");
		agendaChangedPresetsThermoflator.addHas(moveUserConfirmPresetChangeThermoflator);
		
		Move moveUserConfirmPresetChangeRoomLight = factory.createMove("move_user_ConfirmPresetChangeRoomLight");
		addGrammarToMove(moveUserConfirmPresetChangeRoomLight, grAffirm);
		addSemAndContrarySem(moveUserConfirmPresetChangeRoomLight, semConfirmPresetChangeRoomLight, semDenyPresetChangeRoomLight);
		moveUserConfirmPresetChangeRoomLight.setVariableOperator("SET("
				+ varPresetRoomLightTargetIntensity + "=%var_TempPresetRoomLightTargetIntensity%; "
				+ varChange + "=cRoomLight)");
		agendaChangedPresetsRoomLight.addHas(moveUserConfirmPresetChangeRoomLight);
		
		Move moveUserConfirmPresetChangeEndomat = factory.createMove("move_user_ConfirmPresetChangeEndomat");
		addGrammarToMove(moveUserConfirmPresetChangeEndomat, grAffirm);
		addSemAndContrarySem(moveUserConfirmPresetChangeEndomat, semConfirmPresetChangeEndomat, semDenyPresetChangeEndomat);
		moveUserConfirmPresetChangeEndomat.setVariableOperator("SET("
				+ varPresetEndomatTargetFlow + "=%var_TempPresetEndomatTargetFlow%; "
				+ varChange + "=cEndomat)");
		agendaChangedPresetsEndomat.addHas(moveUserConfirmPresetChangeEndomat);
		
		Move moveUserConfirmPresetChangeTable = factory.createMove("move_user_ConfirmPresetChangeTable");
		addGrammarToMove(moveUserConfirmPresetChangeTable, grAffirm);
		addSemAndContrarySem(moveUserConfirmPresetChangeTable, semConfirmPresetChangeTable, semDenyPresetChangeTable);
		moveUserConfirmPresetChangeTable.setVariableOperator("SET("
				+ varPresetTableHeight + "=%var_TempPresetTableHeight%; "
				+ varPresetTableTilt + "=%var_TempPresetTableTilt%; "
				+ varChange + "=cTable)");
		agendaChangedPresetsTable.addHas(moveUserConfirmPresetChangeTable);
		
		
		Move moveUserDenyPresetChangeThermoflator = factory.createMove("move_user_DenyPresetChangeThermoflator");
		addGrammarToMove(moveUserDenyPresetChangeThermoflator, grDeny);
		addSemAndContrarySem(moveUserDenyPresetChangeThermoflator, semDenyPresetChangeThermoflator, semConfirmPresetChangeThermoflator);
		moveUserDenyPresetChangeThermoflator.setVariableOperator("SET("
				+ varTempPresetThermoflatorTargetPressure + "=noTempChange; "
				+ varTempPresetThermoflatorTargetFlow + "=noTempChange; "
				+ varChange + "=dThermoflator)");
		agendaChangedPresetsThermoflator.addHas(moveUserDenyPresetChangeThermoflator);
		
		Move moveUserDenyPresetChangeRoomLight = factory.createMove("move_user_DenyPresetChangeRoomLight");
		addGrammarToMove(moveUserDenyPresetChangeRoomLight, grDeny);
		addSemAndContrarySem(moveUserDenyPresetChangeRoomLight, semDenyPresetChangeRoomLight, semConfirmPresetChangeRoomLight);
		moveUserDenyPresetChangeRoomLight.setVariableOperator("SET("
				+ varTempPresetRoomLightTargetIntensity + "=noTempChange; "
				+ varChange + "=dRoomLight)");
		agendaChangedPresetsRoomLight.addHas(moveUserDenyPresetChangeRoomLight);
		
		Move moveUserDenyPresetChangeEndomat = factory.createMove("move_user_DenyPresetChangeEndomat");
		addGrammarToMove(moveUserDenyPresetChangeEndomat, grDeny);
		addSemAndContrarySem(moveUserDenyPresetChangeEndomat, semDenyPresetChangeEndomat, semConfirmPresetChangeEndomat);
		moveUserDenyPresetChangeEndomat.setVariableOperator("SET("
				+ varTempPresetEndomatTargetFlow + "=noTempChange; "
				+ varChange + "=dEndomat)");
		agendaChangedPresetsEndomat.addHas(moveUserDenyPresetChangeEndomat);
		
		Move moveUserDenyPresetChangeTable = factory.createMove("move_user_DenyPresetChangeTable");
		addGrammarToMove(moveUserDenyPresetChangeTable, grDeny);
		addSemAndContrarySem(moveUserDenyPresetChangeTable, semDenyPresetChangeTable, semConfirmPresetChangeTable);
		moveUserDenyPresetChangeTable.setVariableOperator("SET("
				+ varTempPresetTableHeight + "=noTempChange; "
				+ varTempPresetTableTilt + "=noTempChange; "
				+ varChange + "=dTable)");
		agendaChangedPresetsTable.addHas(moveUserDenyPresetChangeTable);
		
		
		
		//User confirmation moves
		
		Move moveUserConfirmPresets = factory.createMove("move_user_ConfirmPresets");
		addGrammarToMove(moveUserConfirmPresets, grAffirm);
		addSemAndContrarySem(moveUserConfirmPresets, semConfirmPresets, semDenyPresets);
		moveUserConfirmPresets.setVariableOperator("SET("
				+ varDB_dev_Thermoflator_TargetPressure + "=%varDB_setup_Insufflator_TargetPressure%; "
				+ varDB_dev_Thermoflator_TargetFlow + "=0; "
				+ varDB_dev_RoomLight_TargetIntensity +  "=%varDB_setup_Lichtquelle_TargetIntensity%; "
				+ varDB_dev_Endomat_TargetFlow + "=%varDB_setup_Saug-Spuel-Einheit_TargetValue-F%; "
				+ varDB_dev_Table_Height + "=%varDB_setup_Tisch_TableHeight%; "
				+ varDB_dev_Table_Tilt + "=0; "
				+ varSaidAdoptedPresets + "=req)");
		agendaReqPresets.addHas(moveUserConfirmPresets);
        
		Move moveUserConfirmTeamTimeOut = factory.createMove("move_user_ConfirmTeamTimeOut");
		addGrammarToMove(moveUserConfirmTeamTimeOut, grAffirm);
		addSemAndContrarySem(moveUserConfirmTeamTimeOut, semConfirmTeamTimeOut, semDenyTeamTimeOut);
		moveUserConfirmTeamTimeOut.setVariableOperator("SET(" + varTeamTimeOut + "=1; " + varSaidPatientData + "=req)");
		agendaReqTeamTimeOut.addHas(moveUserConfirmTeamTimeOut);
		
		Move moveUserConfirmInfoPatient = factory.createMove("move_user_ConfirmInfoPatient");
		addGrammarToMove(moveUserConfirmInfoPatient, grAffirm);
		addSemAndContrarySem(moveUserConfirmInfoPatient, semConfirmInfoPatient, semDenyInfoPatient);
		agendaInfoPatientData.addHas(moveUserConfirmInfoPatient);
		
		Move moveUserConfirmStartOp = factory.createMove("move_user_ConfirmStartOp");
		addGrammarToMove(moveUserConfirmStartOp, grAffirm);
		addSemAndContrarySem(moveUserConfirmStartOp, semConfirmStartOp, semDenyStartOp);
		moveUserConfirmStartOp.setVariableOperator("SET(" + varTeamTimeOut + "=0; " + varStartOp + "=1)");
		agendaStartOp.addHas(moveUserConfirmStartOp);
		
		Move moveUserConfirmExit = createUserMoveWithGrammarAndSem(factory, grAffirm, semBye);
		moveUserConfirmExit.setVariableOperator("SET(" + varExit + "=1)");
		agendaExit.addHas(moveUserConfirmExit);
		
		
		Move moveUserStartOp = factory.createMove("move_user_StartOp");
		addGrammarToMove(moveUserStartOp, grStartOp);
		addSemAndContrarySem(moveUserStartOp, semConfirmStartOp, semDenyStartOp);
		moveUserStartOp.setVariableOperator("SET(" + varStartOp + "=req)");
		agendaHello2.addHas(moveUserStartOp);
		
		
		
		
		
		//User denial moves
		
		Move moveUserDenyPresets = factory.createMove("move_user_DenyPresets");
		addGrammarToMove(moveUserDenyPresets, grDeny);
		addSemAndContrarySem(moveUserDenyPresets, semDenyPresets, semConfirmPresets);
		moveUserDenyPresets.setVariableOperator("SET(" + varApplyPresets + "=nicht; " + varSaidAdoptedPresets + "=req)");
		agendaReqPresets.addHas(moveUserDenyPresets);
		
		Move moveUserDenyTeamTimeOut = factory.createMove("move_user_DenyTeamTimeOut");
		addGrammarToMove(moveUserDenyTeamTimeOut, grDeny);
		addSemAndContrarySem(moveUserDenyTeamTimeOut, semDenyTeamTimeOut, semConfirmTeamTimeOut);
		moveUserDenyTeamTimeOut.setVariableOperator("SET(" + varTeamTimeOut + "=0; " + varStartOp + "=req; " + varSaidPatientData + "=deny)");
		agendaReqTeamTimeOut.addHas(moveUserDenyTeamTimeOut);
		
		Move moveUserDenyInfoPatient = factory.createMove("move_user_DenyInfoPatient");
		addGrammarToMove(moveUserDenyInfoPatient, grDeny);
		addSemAndContrarySem(moveUserDenyInfoPatient, semDenyInfoPatient, semConfirmInfoPatient);
		agendaInfoPatientData.addHas(moveUserDenyInfoPatient);
		
		Move moveUserDenyStartOp = factory.createMove("move_user_DenyStartOp");
		addGrammarToMove(moveUserDenyStartOp, grDeny);
		addSemAndContrarySem(moveUserDenyStartOp, semDenyStartOp, semConfirmStartOp);
		moveUserDenyStartOp.setVariableOperator("SET(" + varTeamTimeOut + "=0; " + varStartOp + "=deny; " + varPreOpPart + "=hello; " + varExit + "=req)");
		agendaStartOp.addHas(moveUserDenyStartOp);
		
		Move moveUserDenyExit = createUserMoveWithGrammarAndSem(factory, grDeny, semHello);
		moveUserDenyExit.setVariableOperator("SET(" + varExit + "=0)");
		agendaExit.addHas(moveUserDenyExit);
		
		
		//------------------------------------------------------------------------------------------------------------------------------------------------------------
		//Generate grammars, moves and semantics for changing presets
		
		//Thermoflator target pressure & target flow: 0 <= n <= 30
		for (int n = 0; n <= 30; n++) {
			Grammar grSetToN = grammarSetToN(factory, n);
			
			
			Semantic semPresetThermoflatorTargetPressureToN = factory.createSemantic("sem_PresetThermoflatorTargetPressureTo" + n);
			linkSemWithSemGroup(semPresetThermoflatorTargetPressureToN, semGroupPresets);
			
			Semantic semPresetThermoflatorTargetFlowToN = factory.createSemantic("sem_PresetThermoflatorTargetFlowTo" + n);
			linkSemWithSemGroup(semPresetThermoflatorTargetFlowToN, semGroupPresets);
			
			
			Move moveUserPresetThermoflatorTargetPressureToN = factory.createMove("move_user_PresetThermoflatorTargetPressureTo" + n);
			addGrammarToMove(moveUserPresetThermoflatorTargetPressureToN, grSetToN);
			moveUserPresetThermoflatorTargetPressureToN.addSemantic(semPresetThermoflatorTargetPressureToN);
			moveUserPresetThermoflatorTargetPressureToN.setVariableOperator("SET(" + varTempPresetThermoflatorTargetPressure + "=" + n + "; "
					+ varChange + "=ThermoflatorPresets2)");
			agendaChangePresetsThermoflator1.addHas(moveUserPresetThermoflatorTargetPressureToN);
			
			Move moveUserPresetThermoflatorTargetFlowToN = factory.createMove("move_user_PresetThermoflatorTargetFlowTo" + n);
			addGrammarToMove(moveUserPresetThermoflatorTargetFlowToN, grSetToN);
			moveUserPresetThermoflatorTargetFlowToN.addSemantic(semPresetThermoflatorTargetFlowToN);
			moveUserPresetThermoflatorTargetFlowToN.setVariableOperator("SET(" + varTempPresetThermoflatorTargetFlow + "=" + n + "; "
					+ varChange + "=changedThermoflator)");
			agendaChangePresetsThermoflator2.addHas(moveUserPresetThermoflatorTargetFlowToN);			
		}
		
		
		//Table tilt: -30 <= n <= 30
		for (int n = -30; n <= 30; n++) {
			Grammar grSetToN = grammarSetToN(factory, n);
			
			Semantic semPresetTableTiltToN = factory.createSemantic("sem_PresetTableTiltTo" + n);
			linkSemWithSemGroup(semPresetTableTiltToN, semGroupPresets);
			
			Move moveUserPresetTableTiltToN = factory.createMove("move_user_PresetTableTiltTo" + n);
			addGrammarToMove(moveUserPresetTableTiltToN, grSetToN);
			moveUserPresetTableTiltToN.addSemantic(semPresetTableTiltToN);
			moveUserPresetTableTiltToN.setVariableOperator("SET(" + varTempPresetTableTilt + "=" + n + "; "
					+ varChange + "=changedTable)");
			agendaChangePresetsTable2.addHas(moveUserPresetTableTiltToN);
		}
		
		
		//Table height: 10 <= n <= 60
		for (int n = 10; n <= 60; n++) {
			Grammar grSetToN = grammarSetToN(factory, n);
			
			Semantic semPresetTableHeightToN = factory.createSemantic("sem_PresetTableHeightTo" + n);
			linkSemWithSemGroup(semPresetTableHeightToN, semGroupPresets);
			
			Move moveUserPresetTableHeightToN = factory.createMove("move_user_PresetTableHeightTo" + n);
			addGrammarToMove(moveUserPresetTableHeightToN, grSetToN);
			moveUserPresetTableHeightToN.addSemantic(semPresetTableHeightToN);
			moveUserPresetTableHeightToN.setVariableOperator("SET(" + varTempPresetTableHeight + "=" + n + "; "
					+ varChange + "=TablePresets2)");
			agendaChangePresetsTable1.addHas(moveUserPresetTableHeightToN);
		}
		
		
		//Room light target intensity: 5 <= n <= 100
		for (int n = 5; n <= 100; n++) {
			Grammar grSetToN = grammarSetToN(factory, n);
			
			Semantic semPresetRoomLightTargetIntensityToN = factory.createSemantic("sem_PresetRoomLightTargetIntensityTo" + n);
			linkSemWithSemGroup(semPresetRoomLightTargetIntensityToN, semGroupPresets);
			
			Move moveUserPresetRoomLightTargetIntensityToN = factory.createMove("move_user_PresetRoomLightTargetIntensityTo" + n);
			addGrammarToMove(moveUserPresetRoomLightTargetIntensityToN, grSetToN);
			moveUserPresetRoomLightTargetIntensityToN.addSemantic(semPresetRoomLightTargetIntensityToN);
			moveUserPresetRoomLightTargetIntensityToN.setVariableOperator("SET(" + varTempPresetRoomLightTargetIntensity + "=" + n + "; "
					+ varChange + "=changedRoomLight)");
			agendaChangePresetsRoomLight.addHas(moveUserPresetRoomLightTargetIntensityToN);
		}
		
		
		//Endomat target value F: 0 <= n <= 1000
		for (int n = 0; n <= 1000; n++) {
			Grammar grSetToN = grammarSetToN(factory, n);
			
			Semantic semPresetEndomatTargetFlowToN = factory.createSemantic("sem_PresetEndomatTargetFlowTo" + n);
			linkSemWithSemGroup(semPresetEndomatTargetFlowToN, semGroupPresets);
			
			Move moveUserPresetEndomatTargetFlowToN = factory.createMove("move_user_PresetEndomatTargetFlowTo" + n);
			addGrammarToMove(moveUserPresetEndomatTargetFlowToN, grSetToN);
			moveUserPresetEndomatTargetFlowToN.addSemantic(semPresetEndomatTargetFlowToN);
			moveUserPresetEndomatTargetFlowToN.setVariableOperator("SET(" + varTempPresetEndomatTargetFlow + "=" + n + "; "
					+ varChange + "=changedEndomat)");
			agendaChangePresetsEndomat.addHas(moveUserPresetEndomatTargetFlowToN);
		}
		
		
		//------------------------------------------------------------------------------------------------------------------------------------------------------------
		//Generate tool Variables
		//Generate all Grammars, Moves and Semantics for doctor asking for tools
			
		String toolFile = IDACO.class.getResource("tools.txt").getPath();
		File tools = new File(toolFile);
		Scanner scTools = new Scanner(tools);
		String longName, shortName, tempName;
		String[] tempArr, tempArr2, tempArr3;
		Matcher m;
		
		while(scTools.hasNextLine()){
			longName = scTools.nextLine();
			m = toolPattern.matcher(longName);

			if(m.matches()){
				no_of_tools++;
				tempArr = longName.split("\\s");
				if (tempArr[0].charAt(0)=='(') {
					tempName = tempArr[0].substring(1);
				} else {
					tempName = tempArr[0];
				}
				if (tempName.charAt(0)=='[') {
					tempArr2 = tempName.split("\\]");
					tempName = tempArr2[1];
				}
				tempArr3 = tempName.split("\\[");
				shortName = tempArr3[0];

			} else {
				shortName = "failed";
			}

			Variable varToolCnt = factory.createVariable("var_tool_cnt_" + shortName);
			varToolCnt.setDefaultValue("", "0");

			Semantic semNeedTool = factory.createSemantic("sem_Need" + shortName);
			linkSemWithSemGroup(semNeedTool, semGroupTool);
			linkVarWithSemGroup(varToolCnt, semGroupTool);

			Move moveUserTool = factory.createMove("move_user_Tool" + shortName);
			moveUserTool.setVariableOperator("INCREMENT(" + varToolCnt + ")");

			Grammar grTool = grammarTool(factory, longName, shortName);
			addGrammarToMove(moveUserTool, grTool);
			moveUserTool.addSemantic(semNeedTool);
	     	
		}

		scTools.close();
		System.out.println("no_of_tools = " + no_of_tools);
		
		//------------------------------------------------------------------------------------------------------------------------------------------------------------
		
	    
		//Remaining user moves and the respective semantics and counting variables
		
		Variable varAssistCntGasConnection = factory.createVariable("var_assist_cnt_GasConnection");
		Semantic semNeedGasConnection = factory.createSemantic("sem_NeedGasConnection");
		Move moveUserGasConnection = createUserMoveWithGrammarAndSem(factory, grGasConnection, semNeedGasConnection);
		moveUserGasConnection.setVariableOperator("INCREMENT(" + varAssistCntGasConnection + ")");
				
		Variable varAssistCntCleanOptics = factory.createVariable("var_assist_cnt_CleanOptics");
		Semantic semNeedCleanOptics = factory.createSemantic("sem_NeedCleanOptics");
		Move moveUserCleanOptics = createUserMoveWithGrammarAndSem(factory, grCleanOptics, semNeedCleanOptics);
		moveUserCleanOptics.setVariableOperator("INCREMENT(" + varAssistCntCleanOptics + ")");
				
		Variable varAssistCntMoveOptics = factory.createVariable("var_assist_cnt_MoveOptics");
		Semantic semNeedMoveOptics = factory.createSemantic("sem_NeedMoveOptics");
		Move moveUserMoveOptics = createUserMoveWithGrammarAndSem(factory, grMoveOptics, semNeedMoveOptics);
		moveUserMoveOptics.setVariableOperator("INCREMENT(" + varAssistCntMoveOptics + ")");
				
		Variable varAssistCntHandOver = factory.createVariable("var_assist_cnt_HandOver");
		Semantic semNeedHandOver = factory.createSemantic("sem_NeedHandOver");
		Move moveUserHandOver = createUserMoveWithGrammarAndSem(factory, grHandOver, semNeedHandOver);
		moveUserHandOver.setVariableOperator("INCREMENT(" + varAssistCntHandOver + ")");
				
		Variable varAssistCntFocus = factory.createVariable("var_assist_cnt_Focus");
		Semantic semNeedFocus = factory.createSemantic("sem_NeedFocus");
		Move moveUserFocus = createUserMoveWithGrammarAndSem(factory, grFocus, semNeedFocus);
		moveUserFocus.setVariableOperator("INCREMENT(" + varAssistCntFocus + ")");
		
		Variable varAssistCntPowerPE = factory.createVariable("var_assist_cnt_PowerPE");
		Semantic semNeedPowerPE = factory.createSemantic("sem_NeedPowerPE");
		Move moveUserPowerPE = createUserMoveWithGrammarAndSem(factory, grPowerPE, semNeedPowerPE);
		moveUserPowerPE.setVariableOperator("INCREMENT(" + varAssistCntPowerPE + ")");
				
		Variable varAssistCntDistance = factory.createVariable("var_assist_cnt_Distance");
		Semantic semNeedDistance = factory.createSemantic("sem_NeedDistance");
		Move moveUserDistance = createUserMoveWithGrammarAndSem(factory, grDistance, semNeedDistance);
		moveUserDistance.setVariableOperator("INCREMENT(" + varAssistCntDistance + ")");
				
		Variable varAssistCntMoveGas = factory.createVariable("var_assist_cnt_MoveGas");
		Semantic semNeedMoveGas = factory.createSemantic("sem_NeedMoveGas");
		Move moveUserMoveGas = createUserMoveWithGrammarAndSem(factory, grMoveGas, semNeedMoveGas);
		moveUserMoveGas.setVariableOperator("INCREMENT(" + varAssistCntMoveGas + ")");
				
		Variable varAssistCntAspiratorToAssistant = factory.createVariable("var_assist_cnt_AspiratorToAssistant");
		Semantic semNeedAspiratorToAssistant = factory.createSemantic("sem_NeedAspiratorToAssistant");
		Move moveUserAspiratorToAssistant = createUserMoveWithGrammarAndSem(factory, grAspiratorToAssistant, semNeedAspiratorToAssistant);
		moveUserAspiratorToAssistant.setVariableOperator("INCREMENT(" + varAssistCntAspiratorToAssistant + ")");
				
		Variable varAssistCntSuction = factory.createVariable("var_assist_cnt_Suction");
		Semantic semNeedSuction = factory.createSemantic("sem_NeedSuction");
		Move moveUserSuction = createUserMoveWithGrammarAndSem(factory, grSuction, semNeedSuction);
		moveUserSuction.setVariableOperator("INCREMENT(" + varAssistCntSuction + ")");
				
		Variable varAssistCntClean = factory.createVariable("var_assist_cnt_Clean");
		Semantic semNeedClean = factory.createSemantic("sem_NeedClean");
		Move moveUserClean = createUserMoveWithGrammarAndSem(factory, grClean, semNeedClean);
		moveUserClean.setVariableOperator("INCREMENT(" + varAssistCntClean + ")");
				
		Variable varAssistCntFinger = factory.createVariable("var_assist_cnt_Finger");
		Semantic semNeedFinger = factory.createSemantic("sem_NeedFinger");
		Move moveUserFinger = createUserMoveWithGrammarAndSem(factory, grFinger, semNeedFinger);
		moveUserFinger.setVariableOperator("INCREMENT(" + varAssistCntFinger + ")");
				
		Variable varAssistCntConnectGas = factory.createVariable("var_assist_cnt_ConnectGas");
		Semantic semNeedConnectGas = factory.createSemantic("sem_NeedConnectGas");
		Move moveUserConnectGas = createUserMoveWithGrammarAndSem(factory, grConnectGas, semNeedConnectGas);
		moveUserConnectGas.setVariableOperator("INCREMENT(" + varAssistCntConnectGas + ")");
				
		Variable varAssistCntMoveCameraToHilus = factory.createVariable("var_assist_cnt_MoveCameraToHilus");
		Semantic semNeedMoveCameraToHilus = factory.createSemantic("sem_NeedMoveCameraToHilus");
		Move moveUserMoveCameraToHilus = createUserMoveWithGrammarAndSem(factory, grMoveCameraToHilus, semNeedMoveCameraToHilus);
		moveUserMoveCameraToHilus.setVariableOperator("INCREMENT(" + varAssistCntMoveCameraToHilus + ")");
		
		Variable varAssistCntMoveCameraToLiver = factory.createVariable("var_assist_cnt_MoveCameraToLiver");
		Semantic semNeedMoveCameraToLiver = factory.createSemantic("sem_NeedMoveCameraToLiver");
		Move moveUserMoveCameraToLiver = createUserMoveWithGrammarAndSem(factory, grMoveCameraToLiver, semNeedMoveCameraToLiver);
		moveUserMoveCameraToLiver.setVariableOperator("INCREMENT(" + varAssistCntMoveCameraToLiver + ")");
		
		Variable varAssistCntRemoveTrocars = factory.createVariable("var_assist_cnt_RemoveTrocars");
		Semantic semNeedRemoveTrocars = factory.createSemantic("sem_NeedRemoveTrocars");
		Move moveUserRemoveTrocars = createUserMoveWithGrammarAndSem(factory, grRemoveTrocars, semNeedRemoveTrocars);
		moveUserRemoveTrocars.setVariableOperator("INCREMENT(" + varAssistCntRemoveTrocars + ")");
		
		Semantic semThankYou = factory.createSemantic("sem_ThankYou");
		linkSemWithSemGroup(semThankYou, semGroupBye);
		
		Move moveUserThankyou = createUserMoveWithGrammarAndSem(factory, grThankyou, semThankYou);
		
		
		
		//Add all assist Variables to semGroupAssist and set default value
		Collection<Variable> varCol = factory.getAllVariableInstances();
		for (Variable var : varCol) {
			if (var.getLocalName().contains("var_assist")) {
				linkVarWithSemGroup(var, semGroupAssist);
				var.setDefaultValue("", "0");
			}
		}
		
		//Each Semantic containing "Need" that is not part of semGroupTool is linked to semGroupAssist
		Collection<Semantic> semCol = factory.getAllSemanticInstances();
		for (Semantic sem : semCol) {
			if (sem.getLocalName().contains("sem_Need") && !sem.hasSemanticGroup()) {
				linkSemWithSemGroup(sem, semGroupAssist);
			}
		}
		
		
		//Semantics, moves and agendas for undo of automatic actions
		
		Semantic semUndoStartGas = factory.createSemantic("sem_UndoStartGas");
		linkSemWithSemGroup(semUndoStartGas, semGroupAssist);
		
		Semantic semUndoMaxGas = factory.createSemantic("sem_UndoMaxGas");
		linkSemWithSemGroup(semUndoMaxGas, semGroupAssist);
		
		Semantic semUndoRoomLightOff = factory.createSemantic("sem_UndoRoomLightOff");
		linkSemWithSemGroup(semUndoRoomLightOff, semGroupAssist);
		
		Semantic semUndoRoomLightOn = factory.createSemantic("sem_UndoRoomLightOn");
		linkSemWithSemGroup(semUndoRoomLightOn, semGroupAssist);
		
		Semantic semUndoTableRevTrendLightOff = factory.createSemantic("sem_UndoTableRevTrendLightOff");
		linkSemWithSemGroup(semUndoTableRevTrendLightOff, semGroupAssist);
		
		Semantic semUndoTableRevTrend = factory.createSemantic("sem_UndoTableRevTrend");
		linkSemWithSemGroup(semUndoTableRevTrend, semGroupAssist);

     	Semantic semDenyTableRevTrend = factory.createSemantic("sem_DenyTableRevTrend");
     	semDenyTableRevTrend.setConfirmationInfo(false);
     	linkSemWithSemGroup(semDenyTableRevTrend, semGroupAssist);
     	
     	Semantic semDenyLightOn = factory.createSemantic("sem_DenyLightOn");
     	semDenyLightOn.setConfirmationInfo(false);
     	linkSemWithSemGroup(semDenyLightOn, semGroupAssist);
		
		Semantic semUndoTableUp = factory.createSemantic("sem_UndoTableUp");
		linkSemWithSemGroup(semUndoTableUp, semGroupAssist);
		
		
		Move moveUserUndoStartGas = createUserMoveWithGrammarAndSem(factory, grUndoStartGas, semUndoStartGas);
		moveUserUndoStartGas.setVariableOperator("SET(" + varDB_dev_Thermoflator_TargetFlow + "=0; " + varSubDial + "=2)");

		Move moveUserUndoMaxGas = createUserMoveWithGrammarAndSem(factory, grUndoMaxGas, semUndoMaxGas);
		moveUserUndoMaxGas.setVariableOperator("SET(" + varDB_dev_Thermoflator_TargetFlow + "=1; " + varSubDial + "=2)");
		
//		Move moveUserUndoRoomLightOff = createUserMoveWithGrammarAndSem(factory, grUndoRoomLightOff, semDenyTableRevTrend);
//		moveUserUndoRoomLightOff.setVariableOperator("SET(" + varDB_dev_RoomLight_Status + "=1; " + varSubDial + "=1)");
		
		Move moveUserUndoRoomLightOn = createUserMoveWithGrammarAndSem(factory, grUndoRoomLightOn, semDenyLightOn);
		moveUserUndoRoomLightOn.setVariableOperator("SET(" + varDB_dev_RoomLight_Status + "=0; " + varSubDial + "=1)");
		
		Move moveUserUndoTableRevTrendLightOff = createUserMoveWithGrammarAndSem(factory, grUndoTableRevTrendLightOff, semDenyTableRevTrend);
		moveUserUndoTableRevTrendLightOff.setVariableOperator("SET(" + varDB_dev_Table_Tilt + "=0; " + varDB_dev_RoomLight_Status + "=1; " + varSubDial + "=1)");
		
//		Move moveUserUndoTableRevTrend = createUserMoveWithGrammarAndSem(factory, grUndoTableRevTrend, semDenyTableRevTrend);
//		moveUserUndoTableRevTrend.setVariableOperator("SET(" + varDB_dev_Table_Tilt + "=0; " + varSubDial + "=1)");
		
		Move moveUserUndoTableUp = createUserMoveWithGrammarAndSem(factory, grUndoTableUp, semUndoTableUp);
		moveUserUndoTableUp.setVariableOperator("SET(" + varDB_dev_Table_Height + "=%varDB_setup_Tisch_TableHeight%)");
		
		
		Move moveSysUndoStartGas = createSysMoveWithUtterance(factory, "UndoStartGas");
		
		Move moveSysUndoMaxGas = createSysMoveWithUtterance(factory, "UndoMaxGas");
		
		Agenda agendaUndoStartGas = factory.createAgenda("agenda_UndoStartGas");
		agendaUndoStartGas.setRole(agendaUndoStartGas.getRole(), "collection");
		agendaUndoStartGas.setPriority(0, 107);
		linkAgendaWithSumAgenda(agendaUndoStartGas, sumAgendaOp);
		
		Agenda agendaUndoMaxGas = factory.createAgenda("agenda_UndoMaxGas");
		agendaUndoMaxGas.setRole(agendaUndoMaxGas.getRole(), "collection");
		agendaUndoMaxGas.setPriority(0, 126);
		linkAgendaWithSumAgenda(agendaUndoMaxGas, sumAgendaOp);
		
		
		//Operation agendas etc.
		Agenda agendaOp1 = factory.createAgenda("agenda_Op1");
     	agendaOp1.setPriority(0, 85);
     	agendaStartOp.addNext(agendaOp1);
     	
     	Agenda agendaOp2_2 = factory.createAgenda("agendaOp2_2");
     	agendaOp2_2.setRole(agendaOp2_2.getRole(), "confirmation");
     	agendaOp2_2.setPriority(0, 105);
     	linkAgendaWithSumAgenda(agendaOp2_2, sumAgendaOp);
     	
     	Agenda agendaStartedGas = factory.createAgenda("agenda_StartedGas");
     	agendaStartedGas.setRole(agendaStartedGas.getRole(), "collection");
     	agendaStartedGas.setPriority(0, 106);
     	linkAgendaWithSumAgenda(agendaStartedGas, sumAgendaOp);
     	
     	Agenda agendaStartGasManually = factory.createAgenda("agenda_StartGasManually");
     	agendaStartGasManually.setRole(agendaStartGasManually.getRole(), "collection");
     	agendaStartGasManually.setPriority(0, 106);
     	linkAgendaWithSumAgenda(agendaStartGasManually, sumAgendaOp);
     	
     	Agenda agendaOp2_3 = factory.createAgenda("agendaOp2_3");
     	agendaOp2_3.setRole(agendaOp2_3.getRole(), "confirmation");
     	agendaOp2_3.setPriority(0, 110);
     	linkAgendaWithSumAgenda(agendaOp2_3, sumAgendaOp);
     	
     	Agenda agendaOp2_4 = factory.createAgenda("agendaOp2_4");
     	agendaOp2_4.setRole(agendaOp2_4.getRole(), "collection");
     	agendaOp2_4.setPriority(0, 115);
     	linkAgendaWithSumAgenda(agendaOp2_4, sumAgendaOp);
 
     	
     	Move moveSysRequestStartGas = createSysMoveWithUtterance(factory, "RequestStartGas");
     	
     	Move moveUserConfirmStartGas = factory.createMove("move_user_ConfirmStartGas");
     	addGrammarToMove(moveUserConfirmStartGas, grAffirm);
     	linkVarWithSemGroup(varDB_dev_Thermoflator_CurrentFlow, semGroupAssist);
		moveUserConfirmStartGas.setVariableOperator("SET(" + varDB_dev_Thermoflator_TargetFlow + "=1)");
     	
     	Move moveUserDenyStartGas = factory.createMove("move_user_DenyStartGas");
     	addGrammarToMove(moveUserDenyStartGas, grDeny);
     	moveUserDenyStartGas.setVariableOperator("SET(" + varSubDial + "=1)");
     	
     	Semantic semConfirmStartGas = factory.createSemantic("sem_ConfirmStartGas");
     	semConfirmStartGas.setConfirmationInfo(true);
     	linkSemWithSemGroup(semConfirmStartGas, semGroupAssist);
     	
		Move moveUserGas = createUserMoveWithGrammarAndSem(factory, grGas, semConfirmStartGas);
		moveUserGas.setVariableOperator("SET(" + varDB_dev_Thermoflator_TargetFlow + "=1; " + varSubDial + "=0)");
     	
     	Semantic semDenyStartGas = factory.createSemantic("sem_DenyStartGas");
     	semDenyStartGas.setConfirmationInfo(false);
     	linkSemWithSemGroup(semDenyStartGas, semGroupAssist);
     	
     	addSemAndContrarySem(moveUserConfirmStartGas, semConfirmStartGas, semDenyStartGas);
     	addSemAndContrarySem(moveUserDenyStartGas, semDenyStartGas, semConfirmStartGas);
     	
     	Move moveSysGasFlowStarted = createSysMoveWithUtterance(factory, "GasFlowStarted");
     	
     	Move moveSysStartGasManually = createSysMoveWithUtterance(factory, "StartGasManually");
     	
     	
     	
     	Agenda agendaOp3_2 = factory.createAgenda("agendaOp3_2");
     	agendaOp3_2.setRole(agendaOp3_2.getRole(), "confirmation");
     	agendaOp3_2.setPriority(0, 124);
     	linkAgendaWithSumAgenda(agendaOp3_2, sumAgendaOp);
     	
     	Agenda agendaMaxGasManually = factory.createAgenda("agenda_MaxGasManually");
     	agendaMaxGasManually.setRole(agendaMaxGasManually.getRole(), "collection");
     	agendaMaxGasManually.setPriority(0, 125);
     	linkAgendaWithSumAgenda(agendaMaxGasManually, sumAgendaOp);
     	
     	Agenda agendaOp3_3 = factory.createAgenda("agendaOp3_3");
     	agendaOp3_3.setRole(agendaOp3_2.getRole(), "confirmation");
     	agendaOp3_3.setPriority(0, 128);
     	linkAgendaWithSumAgenda(agendaOp3_3, sumAgendaOp);
     	
     	Agenda agendaOp3_4 = factory.createAgenda("agendaOp3_4");
     	agendaOp3_4.setRole(agendaOp3_4.getRole(), "confirmation");
     	agendaOp3_4.setPriority(0, 132);
     	linkAgendaWithSumAgenda(agendaOp3_4, sumAgendaOp);
     	     	
     	Agenda agendaOp3_5 = factory.createAgenda("agendaOp3_5");
     	agendaOp3_5.setRole(agendaOp3_5.getRole(), "confirmation");
     	agendaOp3_5.setPriority(0, 133);
     	linkAgendaWithSumAgenda(agendaOp3_5, sumAgendaOp);
     	
     	Agenda agendaTableRevTrendLightOffManually = factory.createAgenda("agenda_TableRevTrendLightOffManually");
     	agendaTableRevTrendLightOffManually.setRole(agendaTableRevTrendLightOffManually.getRole(), "collection");
     	agendaTableRevTrendLightOffManually.setPriority(0, 136);
     	linkAgendaWithSumAgenda(agendaTableRevTrendLightOffManually, sumAgendaOp);
     	
     	Agenda agendaOp3_6 = factory.createAgenda("agendaOp3_6");
     	agendaOp3_6.setRole(agendaOp3_6.getRole(), "confirmation");
     	agendaOp3_6.setPriority(0, 138);
     	linkAgendaWithSumAgenda(agendaOp3_6, sumAgendaOp);
     	
     	
     	Move moveSysRequestMaxGas = createSysMoveWithUtterance(factory, "RequestMaxGas");
     	
     	Move moveUserConfirmMaxGas = factory.createMove("move_user_ConfirmMaxGas");
     	addGrammarToMove(moveUserConfirmMaxGas, grAffirm);
     	linkVarWithSemGroup(varDB_dev_Thermoflator_CurrentFlow, semGroupAssist);
		moveUserConfirmMaxGas.setVariableOperator("SET(" + varDB_dev_Thermoflator_TargetFlow + "=%varDB_setup_Insufflator_TargetFlow%)");
     	
     	Move moveUserDenyMaxGas = factory.createMove("move_user_DenyMaxGas");
     	addGrammarToMove(moveUserDenyMaxGas, grDeny);
     	moveUserDenyMaxGas.setVariableOperator("SET(" + varSubDial + "=1)");

     	Semantic semConfirmMaxGas = factory.createSemantic("sem_ConfirmMaxGas");
     	semConfirmStartGas.setConfirmationInfo(true);
     	linkSemWithSemGroup(semConfirmStartGas, semGroupAssist);
     	
     	Move moveUserMaxGas = createUserMoveWithGrammarAndSem(factory, grMaxGas, semConfirmMaxGas);
		moveUserMaxGas.setVariableOperator("SET(" + varDB_dev_Thermoflator_TargetFlow + "=%varDB_setup_Insufflator_TargetFlow%; " + varSubDial + "=0)");
     	
     	Semantic semDenyMaxGas = factory.createSemantic("sem_DenyMaxGas");
     	semDenyMaxGas.setConfirmationInfo(false);
     	linkSemWithSemGroup(semDenyMaxGas, semGroupAssist);
     	
     	addSemAndContrarySem(moveUserConfirmMaxGas, semConfirmMaxGas, semDenyMaxGas);
     	addSemAndContrarySem(moveUserDenyMaxGas, semDenyMaxGas, semConfirmMaxGas);
     	
     	Move moveSysGasAtMax = createSysMoveWithUtterance(factory, "GasAtMax");    
     	
     	Move moveSysMaxGasManually = createSysMoveWithUtterance(factory, "MaxGasManually");
     	
     	
    	Move moveSysRequestTableRevTrend = createSysMoveWithUtterance(factory, "RequestTableRevTrend");
     	
     	Move moveUserConfirmTableRevTrend = factory.createMove("move_user_ConfirmTableRevTrend");
     	addGrammarToMove(moveUserConfirmTableRevTrend, grAffirm);
     	linkVarWithSemGroup(varDB_dev_Table_Tilt, semGroupAssist);
		moveUserConfirmTableRevTrend.setVariableOperator("SET(" + varDB_dev_Table_Tilt + "=%varDB_setup_Tisch_TableTilt%; " + varDB_dev_RoomLight_Status + "=0)");
     	
     	Move moveUserDenyTableRevTrend = factory.createMove("move_user_DenyTableRevTrend");
     	addGrammarToMove(moveUserDenyTableRevTrend, grDeny);
     	moveUserDenyTableRevTrend.setVariableOperator("SET(" + varSubDial + "=1)");
     	
     	Semantic semConfirmTableRevTrend = factory.createSemantic("sem_ConfirmTableRevTrend");
     	semConfirmTableRevTrend.setConfirmationInfo(true);
     	linkSemWithSemGroup(semConfirmTableRevTrend, semGroupAssist);
     	
		
		Move moveUserTableRevTrendLightOff = createUserMoveWithGrammarAndSem(factory, grTableRevTrendLightOff, semConfirmTableRevTrend);
		moveUserTableRevTrendLightOff.setVariableOperator("SET(" + varDB_dev_Table_Tilt + "=%varDB_setup_Tisch_TableTilt%; " + varDB_dev_RoomLight_Status + "=0; " + varSubDial + "=0)");
     	
     	addSemAndContrarySem(moveUserConfirmTableRevTrend, semConfirmTableRevTrend, semDenyTableRevTrend);
     	addSemAndContrarySem(moveUserDenyTableRevTrend, semDenyTableRevTrend, semConfirmTableRevTrend);
     	
     	Move moveSysTableRevTrendLightOffManually = createSysMoveWithUtterance(factory, "TableRevTrendLightOffManually");
     	

     	Agenda agendaOp8_2 = factory.createAgenda("agendaOp8_2");
     	agendaOp8_2.setRole(agendaOp8_2.getRole(), "confirmation");
     	agendaOp8_2.setPriority(0, 225);
     	linkAgendaWithSumAgenda(agendaOp8_2, sumAgendaOp);
     	
     	Agenda agendaLightOnManually = factory.createAgenda("agenda_LightOnManually");
     	agendaLightOnManually.setRole(agendaLightOnManually.getRole(), "collection");
     	agendaLightOnManually.setPriority(0, 226);
     	linkAgendaWithSumAgenda(agendaLightOnManually, sumAgendaOp);
     	
     	Agenda agendaOp8_3 = factory.createAgenda("agendaOp8_3");
     	agendaOp8_3.setRole(agendaOp8_2.getRole(), "confirmation");
     	agendaOp8_3.setPriority(0, 230);
     	linkAgendaWithSumAgenda(agendaOp8_3, sumAgendaOp);
     	
     	Move moveSysRequestLightOn = createSysMoveWithUtterance(factory, "RequestLightOn");
     	
     	Move moveUserConfirmLightOn = factory.createMove("move_user_ConfirmLightOn");
     	addGrammarToMove(moveUserConfirmLightOn, grAffirm);
		moveUserConfirmLightOn.setVariableOperator("SET(" + varDB_dev_RoomLight_Status + "=1)");
     	
     	Move moveUserDenyLightOn = factory.createMove("move_user_DenyLightOn");
     	addGrammarToMove(moveUserDenyLightOn, grDeny);
     	moveUserDenyLightOn.setVariableOperator("SET(" + varSubDial + "=1)");
     	
     	Semantic semConfirmLightOn = factory.createSemantic("sem_ConfirmLightOn");
     	semConfirmLightOn.setConfirmationInfo(true);
     	linkSemWithSemGroup(semConfirmLightOn, semGroupAssist);
     	
     	Move moveUserLightOn = createUserMoveWithGrammarAndSem(factory, grLightOn, semConfirmLightOn);
		moveUserLightOn.setVariableOperator("SET(" + varDB_dev_RoomLight_Status + "=1; " + varSubDial + "=0)");
     	
     	addSemAndContrarySem(moveUserConfirmLightOn, semConfirmLightOn, semDenyLightOn);
     	addSemAndContrarySem(moveUserDenyLightOn, semDenyLightOn, semConfirmLightOn);
     	
     	Move moveSysLightOnManually = createSysMoveWithUtterance(factory, "LightOnManually");
     	
     	
     	linkVarWithSemGroup(varTemp, semGroupTool);
		
     	SummaryAgenda sumAgendaWarnings = factory.createSummaryAgenda("sumAgenda_Warnings");
     	sumAgendaWarnings.setType(sumAgendaWarnings.getTypeString(), "request");
     	sumAgendaWarnings.setRole(sumAgendaWarnings.getRole(), "collection");
     	
     	
     	Agenda agendaWarnTool1Less = factory.createAgenda("agenda_WarnTool1Less");
     	agendaWarnTool1Less.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Backhaus% << %var_ExpectedBackhaus1%) && (%var_tool_cnt_Veress% == 1)))");
     	agendaOp1.addNext(agendaWarnTool1Less);
     	
     	Agenda agendaWarnTool2Less = factory.createAgenda("agenda_WarnTool2Less");
     	agendaWarnTool2Less.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Trokar% << %var_ExpectedTrokar2%) && (%var_tool_cnt_Optik% == 1)))");
     	agendaOp2_3.addNext(agendaWarnTool2Less);
     	
     	Agenda agendaWarnTool3Less = factory.createAgenda("agenda_WarnTool3Less");
     	agendaWarnTool3Less.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Trokar% << %var_ExpectedTrokar3%) && (%var_tool_cnt_Fasszange% == 1)))");
     	agendaOp3_5.addNext(agendaWarnTool3Less);
     	
     	Agenda agendaWarnTool5Less = factory.createAgenda("agenda_WarnTool5Less");
     	agendaWarnTool5Less.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Clip% << %var_ExpectedClip5%) && (%var_assist_cnt_PowerPE% >= 3)))");
     	agendaOp5.addNext(agendaWarnTool5Less);
     	
     	Agenda agendaWarnTool6Less = factory.createAgenda("agenda_WarnTool6Less");
     	agendaWarnTool6Less.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Clip% << %var_ExpectedClip6%) && (%var_tool_cnt_Schere% >= 3)))");
     	agendaOp6.addNext(agendaWarnTool6Less);
     	
     	Agenda agendaWarnTool8aLess = factory.createAgenda("agenda_WarnTool8aLess");
     	agendaWarnTool8aLess.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Pean% << %var_ExpectedPean8a%) && (%var_tool_cnt_Saugspuel% == 1)))");
     	agendaOp8_3.addNext(agendaWarnTool8aLess);
     	
     	Agenda agendaWarnTool8bLess = factory.createAgenda("agenda_WarnTool8bLess");
     	agendaWarnTool8bLess.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Alice% << %var_ExpectedAlice8b%) && (%var_tool_cnt_Saugspuel% == 1)))");
     	agendaOp8_3.addNext(agendaWarnTool8bLess);
     	
     	Agenda agendaWarnTool8cLess = factory.createAgenda("agenda_WarnTool8cLess");
     	agendaWarnTool8cLess.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Trokar% << %var_ExpectedTrokar8c%) && (%var_tool_cnt_Saugspuel% == 1)))");
     	agendaOp8_3.addNext(agendaWarnTool8cLess);
     	
     	
     	Move moveSysWarnTool1Less = createSysMoveWithUtterance(factory, "WarnTool1Less");
		agendaWarnTool1Less.addHas(moveSysWarnTool1Less);
		moveSysWarnTool1Less.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool2Less = createSysMoveWithUtterance(factory, "WarnTool2Less");
		agendaWarnTool2Less.addHas(moveSysWarnTool2Less);
		moveSysWarnTool2Less.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool3Less = createSysMoveWithUtterance(factory, "WarnTool3Less");
		agendaWarnTool3Less.addHas(moveSysWarnTool3Less);
		moveSysWarnTool3Less.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool5Less = createSysMoveWithUtterance(factory, "WarnTool5Less");
		agendaWarnTool5Less.addHas(moveSysWarnTool5Less);
		moveSysWarnTool5Less.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool6Less = createSysMoveWithUtterance(factory, "WarnTool6Less");
		agendaWarnTool6Less.addHas(moveSysWarnTool6Less);
		moveSysWarnTool6Less.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool8aLess = createSysMoveWithUtterance(factory, "WarnTool8aLess");
		agendaWarnTool8aLess.addHas(moveSysWarnTool8aLess);
		moveSysWarnTool8aLess.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool8bLess = createSysMoveWithUtterance(factory, "WarnTool8bLess");
		agendaWarnTool8bLess.addHas(moveSysWarnTool8bLess);
		moveSysWarnTool8bLess.setVariableOperator("SET(" + varWarn + "= less)");
		
		Move moveSysWarnTool8cLess = createSysMoveWithUtterance(factory, "WarnTool8cLess");
		agendaWarnTool8cLess.addHas(moveSysWarnTool8cLess);
		moveSysWarnTool8cLess.setVariableOperator("SET(" + varWarn + "= less)");
		
     	
     	Agenda agendaWarnTool1More = factory.createAgenda("agenda_WarnTool1More");
     	agendaWarnTool1More.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Backhaus% >> %var_ExpectedBackhaus1%))");
     	agendaOp1.addNext(agendaWarnTool1More);
     	agendaOp2.addNext(agendaWarnTool1More);
     	agendaWarnTool1More.addMustnot(factory.getSemantic("sem_NeedVeress"));
     	
     	Agenda agendaWarnTool2More = factory.createAgenda("agenda_WarnTool2More");
     	agendaWarnTool2More.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Trokar% >> %var_ExpectedTrokar2%))");
     	agendaOp2_3.addNext(agendaWarnTool2More);
     	agendaWarnTool2More.addMustnot(factory.getSemantic("sem_NeedOptik"));
     	
     	Agenda agendaWarnTool3More = factory.createAgenda("agenda_WarnTool3More");
     	agendaWarnTool3More.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Trokar% >> %var_ExpectedTrokar3%))");
     	agendaOp3_5.addNext(agendaWarnTool3More);
     	agendaWarnTool3More.addMustnot(factory.getSemantic("sem_NeedFasszange"));
     	
     	Agenda agendaWarnTool5More = factory.createAgenda("agenda_WarnTool5More");
     	agendaWarnTool5More.setVariableOperator("", "REQUIRES((%var_Warn% == false) && ((%var_tool_cnt_Clip% >> %var_ExpectedClip5%) && (%var_assist_cnt_PowerPE% << 3)))");
     	agendaOp5.addNext(agendaWarnTool5More);
     	agendaWarnTool5More.addMustnot(factory.getSemantic("sem_NeedBergebeutel"));
     	
     	Agenda agendaWarnTool6More = factory.createAgenda("agenda_WarnTool6More");
     	agendaWarnTool6More.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Clip% >> %var_ExpectedClip6%))");
     	agendaOp6.addNext(agendaWarnTool6More);
     	agendaWarnTool6More.addMustnot(factory.getSemantic("sem_NeedBergebeutel"));
     	
     	Agenda agendaWarnTool8aMore = factory.createAgenda("agenda_WarnTool8aMore");
     	agendaWarnTool8aMore.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Pean% >> %var_ExpectedPean8a%))");
     	agendaOp8_3.addNext(agendaWarnTool8aMore);
     	
    	Agenda agendaWarnTool8bMore = factory.createAgenda("agenda_WarnTool8bMore");
     	agendaWarnTool8bMore.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Alice% >> %var_ExpectedAlice8b%))");
     	agendaOp8_3.addNext(agendaWarnTool8bMore);
     	
    	Agenda agendaWarnTool8cMore = factory.createAgenda("agenda_WarnTool8cMore");
     	agendaWarnTool8cMore.setVariableOperator("", "REQUIRES((%var_Warn% == false) && (%var_tool_cnt_Trokar% >> %var_ExpectedTrokar8c%))");
     	agendaOp8_3.addNext(agendaWarnTool8cMore);
     	
     	
     	Move moveSysWarnTool1More = createSysMoveWithUtterance(factory, "WarnTool1More");
		agendaWarnTool1More.addHas(moveSysWarnTool1More);
		moveSysWarnTool1More.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool2More = createSysMoveWithUtterance(factory, "WarnTool2More");
		agendaWarnTool2More.addHas(moveSysWarnTool2More);
		moveSysWarnTool2More.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool3More = createSysMoveWithUtterance(factory, "WarnTool3More");
		agendaWarnTool3More.addHas(moveSysWarnTool3More);
		moveSysWarnTool3More.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool5More = createSysMoveWithUtterance(factory, "WarnTool5More");
		agendaWarnTool5More.addHas(moveSysWarnTool5More);
		moveSysWarnTool5More.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool6More = createSysMoveWithUtterance(factory, "WarnTool6More");
		agendaWarnTool6More.addHas(moveSysWarnTool6More);
		moveSysWarnTool6More.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool8aMore = createSysMoveWithUtterance(factory, "WarnTool8aMore");
		agendaWarnTool8aMore.addHas(moveSysWarnTool8aMore);
		moveSysWarnTool8aMore.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool8bMore = createSysMoveWithUtterance(factory, "WarnTool8bMore");
		agendaWarnTool8bMore.addHas(moveSysWarnTool8bMore);
		moveSysWarnTool8bMore.setVariableOperator("SET(" + varWarn + "= more)");
		
		Move moveSysWarnTool8cMore = createSysMoveWithUtterance(factory, "WarnTool8cMore");
		agendaWarnTool8cMore.addHas(moveSysWarnTool8cMore);
		moveSysWarnTool8cMore.setVariableOperator("SET(" + varWarn + "= more)");
			
		
		Move moveUserConfirmWarnTool1 = factory.createMove("move_user_ConfirmWarnTool1");
		addGrammarToMove(moveUserConfirmWarnTool1, grAffirm);
		moveUserConfirmWarnTool1.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=1)");
		agendaWarnTool1More.addHas(moveUserConfirmWarnTool1);
		agendaWarnTool1Less.addHas(moveUserConfirmWarnTool1);
		
		Move moveUserConfirmWarnTool2 = factory.createMove("move_user_ConfirmWarnTool2");
		addGrammarToMove(moveUserConfirmWarnTool2, grAffirm);
		moveUserConfirmWarnTool2.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=2_3)");
		agendaWarnTool2More.addHas(moveUserConfirmWarnTool2);
		agendaWarnTool2Less.addHas(moveUserConfirmWarnTool2);
		
		Move moveUserConfirmWarnTool3 = factory.createMove("move_user_ConfirmWarnTool3");
		addGrammarToMove(moveUserConfirmWarnTool3, grAffirm);
		moveUserConfirmWarnTool3.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=3_5)");
		agendaWarnTool3More.addHas(moveUserConfirmWarnTool3);
		agendaWarnTool3Less.addHas(moveUserConfirmWarnTool3);
		
		Move moveUserConfirmWarnTool5 = factory.createMove("move_user_ConfirmWarnTool5");
		addGrammarToMove(moveUserConfirmWarnTool5, grAffirm);
		moveUserConfirmWarnTool5.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=5)");
		agendaWarnTool5More.addHas(moveUserConfirmWarnTool5);
		agendaWarnTool5Less.addHas(moveUserConfirmWarnTool5);
		
		Move moveUserConfirmWarnTool6 = factory.createMove("move_user_ConfirmWarnTool6");
		addGrammarToMove(moveUserConfirmWarnTool6, grAffirm);
		moveUserConfirmWarnTool6.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=6)");
		agendaWarnTool6More.addHas(moveUserConfirmWarnTool6);
		agendaWarnTool6Less.addHas(moveUserConfirmWarnTool6);
		
		Move moveUserConfirmWarnTool8a = factory.createMove("move_user_ConfirmWarnTool8a");
		addGrammarToMove(moveUserConfirmWarnTool8a, grAffirm);
		moveUserConfirmWarnTool8a.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=8_3)");
		agendaWarnTool8aMore.addHas(moveUserConfirmWarnTool8a);
		agendaWarnTool8aLess.addHas(moveUserConfirmWarnTool8a);
		
		Move moveUserConfirmWarnTool8b = factory.createMove("move_user_ConfirmWarnTool8b");
		addGrammarToMove(moveUserConfirmWarnTool8b, grAffirm);
		moveUserConfirmWarnTool8b.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=8_3)");
		agendaWarnTool8bMore.addHas(moveUserConfirmWarnTool8b);
		agendaWarnTool8bLess.addHas(moveUserConfirmWarnTool8b);
		
		Move moveUserConfirmWarnTool8c = factory.createMove("move_user_ConfirmWarnTool8c");
		addGrammarToMove(moveUserConfirmWarnTool8c, grAffirm);
		moveUserConfirmWarnTool8c.setVariableOperator("SET(" + varTemp + "=confirmWarnTool; " + varCurrentPart + "=8_3)");
		agendaWarnTool8cMore.addHas(moveUserConfirmWarnTool8c);
		agendaWarnTool8cLess.addHas(moveUserConfirmWarnTool8c);
	
		
		Move moveUserDenyWarnTool1 = factory.createMove("move_user_DenyWarnTool1");
		addGrammarToMove(moveUserDenyWarnTool1, grDeny);
		moveUserDenyWarnTool1.setVariableOperator("SET(" + varTemp + "=denyWarnTool; " + varCurrentPart + "=1)");
		agendaWarnTool1More.addHas(moveUserDenyWarnTool1);
		agendaWarnTool1Less.addHas(moveUserDenyWarnTool1);
		
		Move moveUserDenyWarnTool2 = factory.createMove("move_user_DenyWarnTool2");
		addGrammarToMove(moveUserDenyWarnTool2, grDeny);
		moveUserDenyWarnTool2.setVariableOperator("SET(" + varTemp + "=denyWarnTool; " + varCurrentPart + "=2_3)");
		agendaWarnTool2More.addHas(moveUserDenyWarnTool2);
		agendaWarnTool2Less.addHas(moveUserDenyWarnTool2);
		
		Move moveUserDenyWarnTool3 = factory.createMove("move_user_DenyWarnTool3");
		addGrammarToMove(moveUserDenyWarnTool3, grDeny);
		moveUserDenyWarnTool3.setVariableOperator("SET(" + varTemp + "=denyWarnTool; " + varCurrentPart + "=3_5)");
		agendaWarnTool3More.addHas(moveUserDenyWarnTool3);
		agendaWarnTool3Less.addHas(moveUserDenyWarnTool3);
		
		Move moveUserDenyWarnTool5 = factory.createMove("move_user_DenyWarnTool5");
		addGrammarToMove(moveUserDenyWarnTool5, grDeny);
		moveUserDenyWarnTool5.setVariableOperator("SET(" + varTemp + "=denyWarnTool; " + varCurrentPart + "=5)");
		agendaWarnTool5More.addHas(moveUserDenyWarnTool5);
		agendaWarnTool5Less.addHas(moveUserDenyWarnTool5);
		
		Move moveUserDenyWarnTool6 = factory.createMove("move_user_DenyWarnTool6");
		addGrammarToMove(moveUserDenyWarnTool6, grDeny);
		moveUserDenyWarnTool6.setVariableOperator("SET(" + varTemp + "=denyWarnTool; " + varCurrentPart + "=6)");
		agendaWarnTool6More.addHas(moveUserDenyWarnTool6);
		agendaWarnTool6Less.addHas(moveUserDenyWarnTool6);
		
		Move moveUserDenyWarnTool8 = factory.createMove("move_user_DenyWarnTool8");
		addGrammarToMove(moveUserDenyWarnTool8, grDeny);
		moveUserDenyWarnTool8.setVariableOperator("SET(" + varTemp + "=denyWarnTool; " + varCurrentPart + "=8_3)");
		agendaWarnTool8aMore.addHas(moveUserDenyWarnTool8);
		agendaWarnTool8bMore.addHas(moveUserDenyWarnTool8);
		agendaWarnTool8cMore.addHas(moveUserDenyWarnTool8);
		agendaWarnTool8aLess.addHas(moveUserDenyWarnTool8);
		agendaWarnTool8bLess.addHas(moveUserDenyWarnTool8);
		agendaWarnTool8cLess.addHas(moveUserDenyWarnTool8);
		
		
		Semantic semConfirmWarnTool = factory.createSemantic("sem_ConfirmWarnTool");
     	semConfirmWarnTool.setConfirmationInfo(true);
     	linkSemWithSemGroup(semConfirmWarnTool, semGroupTool);
     	
    	Semantic semDenyWarnTool = factory.createSemantic("sem_DenyWarnTool");
        semDenyWarnTool.setConfirmationInfo(false);
        linkSemWithSemGroup(semDenyWarnTool, semGroupTool);
        
        addSemAndContrarySem(moveUserConfirmWarnTool1, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool2, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool3, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool5, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool6, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool8a, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool8b, semConfirmWarnTool, semDenyWarnTool);
        addSemAndContrarySem(moveUserConfirmWarnTool8c, semConfirmWarnTool, semDenyWarnTool);
        
        addSemAndContrarySem(moveUserDenyWarnTool1, semDenyWarnTool, semConfirmWarnTool);
        addSemAndContrarySem(moveUserDenyWarnTool2, semDenyWarnTool, semConfirmWarnTool);
        addSemAndContrarySem(moveUserDenyWarnTool3, semDenyWarnTool, semConfirmWarnTool);
        addSemAndContrarySem(moveUserDenyWarnTool5, semDenyWarnTool, semConfirmWarnTool);
        addSemAndContrarySem(moveUserDenyWarnTool6, semDenyWarnTool, semConfirmWarnTool);
        addSemAndContrarySem(moveUserDenyWarnTool8, semDenyWarnTool, semConfirmWarnTool);
        
        
        Agenda agendaRequestUsageBackhaus = factory.createAgenda("agenda_RequestUsageBackhaus");
        agendaRequestUsageBackhaus.setRole(agendaRequestUsageBackhaus.getRole(), "collection");
        agendaRequestUsageBackhaus.setPriority(0, 1100);
        agendaRequestUsageBackhaus.setVariableOperator("", "REQUIRES("
        		+ "((%var_Temp%==denyWarnTool) || (%var_Temp%==denyCnt)) && "
        		+ "(%var_CurrentPart% == 1)"
        		+ ")");
		linkAgendaWithSumAgenda(agendaRequestUsageBackhaus, sumAgendaWarnings);
		agendaWarnTool1More.addNext(agendaRequestUsageBackhaus);
		agendaWarnTool1Less.addNext(agendaRequestUsageBackhaus);
		
		Agenda agendaRequestUsageTrokar = factory.createAgenda("agenda_RequestUsageTrokar");
		agendaRequestUsageTrokar.setRole(agendaRequestUsageTrokar.getRole(), "collection");
		agendaRequestUsageTrokar.setPriority(0, 1100);
		agendaRequestUsageTrokar.setVariableOperator("", "REQUIRES("
				+ "((%var_Temp%==denyWarnTool) || (%var_Temp%==denyCnt)) && "
				+ "((%var_CurrentPart% == 2_3) || ((%var_CurrentPart% == 3_5) || (%var_CurrentPart% == 8_3)))"
				+ ")");
		linkAgendaWithSumAgenda(agendaRequestUsageTrokar, sumAgendaWarnings);
		agendaWarnTool2More.addNext(agendaRequestUsageTrokar);
		agendaWarnTool3More.addNext(agendaRequestUsageTrokar);
		agendaWarnTool8cMore.addNext(agendaRequestUsageTrokar);
		agendaWarnTool2Less.addNext(agendaRequestUsageTrokar);
		agendaWarnTool3Less.addNext(agendaRequestUsageTrokar);
		agendaWarnTool8cLess.addNext(agendaRequestUsageTrokar);
		
		Agenda agendaRequestUsageClip = factory.createAgenda("agenda_RequestUsageClip");
		agendaRequestUsageClip.setRole(agendaRequestUsageClip.getRole(), "collection");
		agendaRequestUsageClip.setPriority(0, 1100);
		agendaRequestUsageClip.setVariableOperator("", "REQUIRES("
				+ "((%var_Temp%==denyWarnTool) || (%var_Temp%==denyCnt)) && "
				+ "((%var_CurrentPart% == 5) || (%var_CurrentPart% == 6))"
				+ ")");
		linkAgendaWithSumAgenda(agendaRequestUsageClip, sumAgendaWarnings);
		agendaWarnTool5More.addNext(agendaRequestUsageClip);
		agendaWarnTool6More.addNext(agendaRequestUsageClip);
		agendaWarnTool5Less.addNext(agendaRequestUsageClip);
		agendaWarnTool6Less.addNext(agendaRequestUsageClip);
		
		Agenda agendaRequestUsagePean = factory.createAgenda("agenda_RequestUsagePean");
		agendaRequestUsagePean.setRole(agendaRequestUsagePean.getRole(), "collection");
		agendaRequestUsagePean.setPriority(0, 1100);
		agendaRequestUsagePean.setVariableOperator("", "REQUIRES("
				+ "((%var_Temp%==denyWarnTool) || (%var_Temp%==denyCnt)) && "
				+ "(%var_CurrentPart% == 8_3)"
				+ ")");
		linkAgendaWithSumAgenda(agendaRequestUsagePean, sumAgendaWarnings);
		agendaWarnTool8aMore.addNext(agendaRequestUsagePean);
		agendaWarnTool8aLess.addNext(agendaRequestUsagePean);
		
		Agenda agendaRequestUsageAlice = factory.createAgenda("agenda_RequestUsageAlice");
		agendaRequestUsageAlice.setRole(agendaRequestUsageAlice.getRole(), "collection");
		agendaRequestUsageAlice.setPriority(0, 1100);
		agendaRequestUsageAlice.setVariableOperator("", "REQUIRES("
				+ "((%var_Temp%==denyWarnTool) || (%var_Temp%==denyCnt)) && "
				+ "(%var_CurrentPart% == 8_3)"
				+ ")");
		linkAgendaWithSumAgenda(agendaRequestUsageAlice, sumAgendaWarnings);
		agendaWarnTool8bMore.addNext(agendaRequestUsageAlice);
		agendaWarnTool8bLess.addNext(agendaRequestUsageAlice);

		
		Move moveSysRequestUsageBackhaus = createSysMoveWithUtterance(factory, "RequestUsageBackhaus");
		agendaRequestUsageBackhaus.addHas(moveSysRequestUsageBackhaus);
		
		Move moveSysRequestUsageTrokar = createSysMoveWithUtterance(factory, "RequestUsageTrokar");
		agendaRequestUsageTrokar.addHas(moveSysRequestUsageTrokar);
		
		Move moveSysRequestUsageClip = createSysMoveWithUtterance(factory, "RequestUsageClip");
		agendaRequestUsageClip.addHas(moveSysRequestUsageClip);
		
		Move moveSysRequestUsagePean = createSysMoveWithUtterance(factory, "RequestUsagePean");
		agendaRequestUsagePean.addHas(moveSysRequestUsagePean);
		
		Move moveSysRequestUsageAlice = createSysMoveWithUtterance(factory, "RequestUsageAlice");
		agendaRequestUsageAlice.addHas(moveSysRequestUsageAlice);
		
		
		Variable varSaidUsage = factory.createVariable("var_SaidUsage");
		varSaidUsage.setDefaultValue("", "nichts");
		linkVarWithSemGroup(varSaidUsage, semGroupTool);
		
		
		Move[] moveUserCorrectToolCnt = new Move[10];		
		Semantic[] semCorrectToolCnt = new Semantic[10];
		
		Move moveSysSaidUsage = createSysMoveWithUtterance(factory, "SaidUsage");
		
		Move moveSysAdaptUsage = createSysMoveWithUtterance(factory, "AdaptUsage");
		moveSysAdaptUsage.setVariableOperator("SET(" + varWarn + "= false)");
		
		
		for (int k = 0; k < 10; k++) {
			semCorrectToolCnt[k] = factory.createSemantic("sem_CorrectToolCntTo" + k);
			linkSemWithSemGroup(semCorrectToolCnt[k], semGroupTool);
			
			moveUserCorrectToolCnt[k] = factory.createMove("move_user_CorrectToolCntTo" + k);
			addGrammarToMove(moveUserCorrectToolCnt[k], factory.getGrammar("gr_CorrectCntTo" + k));
			moveUserCorrectToolCnt[k].addSemantic(semCorrectToolCnt[k]);
			moveUserCorrectToolCnt[k].setVariableOperator("SET(" + varSaidUsage + "=" + k + "; " + varTemp + "=requestCnt)");
			agendaRequestUsageBackhaus.addHas(moveUserCorrectToolCnt[k]);
			agendaRequestUsageTrokar.addHas(moveUserCorrectToolCnt[k]);
			agendaRequestUsageClip.addHas(moveUserCorrectToolCnt[k]);
			agendaRequestUsagePean.addHas(moveUserCorrectToolCnt[k]);
			agendaRequestUsageAlice.addHas(moveUserCorrectToolCnt[k]);		
		}
		
		
		Agenda agendaConfirmUsageBackhaus = factory.createAgenda("agenda_ConfirmUsageBackhaus");
		agendaConfirmUsageBackhaus.setRole(agendaConfirmUsageBackhaus.getRole(), "confirmation");
		agendaConfirmUsageBackhaus.setPriority(0, 1200);
		agendaConfirmUsageBackhaus.setVariableOperator("", "REQUIRES(%var_Temp%==requestCnt)");
		linkAgendaWithSumAgenda(agendaConfirmUsageBackhaus, sumAgendaWarnings);
		agendaRequestUsageBackhaus.addNext(agendaConfirmUsageBackhaus);
		
		Agenda agendaConfirmUsageTrokar = factory.createAgenda("agenda_ConfirmUsageTrokar");
		agendaConfirmUsageTrokar.setRole(agendaConfirmUsageTrokar.getRole(), "confirmation");
		agendaConfirmUsageTrokar.setPriority(0, 1200);
		agendaConfirmUsageTrokar.setVariableOperator("", "REQUIRES(%var_Temp%==requestCnt)");
		linkAgendaWithSumAgenda(agendaConfirmUsageTrokar, sumAgendaWarnings);
		agendaRequestUsageTrokar.addNext(agendaConfirmUsageTrokar);
		
		Agenda agendaConfirmUsageClip = factory.createAgenda("agenda_ConfirmUsageClip");
		agendaConfirmUsageClip.setRole(agendaConfirmUsageClip.getRole(), "confirmation");
		agendaConfirmUsageClip.setPriority(0, 1200);
		agendaConfirmUsageClip.setVariableOperator("", "REQUIRES(%var_Temp%==requestCnt)");
		linkAgendaWithSumAgenda(agendaConfirmUsageClip, sumAgendaWarnings);
		agendaRequestUsageClip.addNext(agendaConfirmUsageClip);
		
		Agenda agendaConfirmUsagePean = factory.createAgenda("agenda_ConfirmUsagePean");
		agendaConfirmUsagePean.setRole(agendaConfirmUsagePean.getRole(), "confirmation");
		agendaConfirmUsagePean.setPriority(0, 1200);
		agendaConfirmUsagePean.setVariableOperator("", "REQUIRES(%var_Temp%==requestCnt)");
		linkAgendaWithSumAgenda(agendaConfirmUsagePean, sumAgendaWarnings);
		agendaRequestUsagePean.addNext(agendaConfirmUsagePean);
		
		Agenda agendaConfirmUsageAlice = factory.createAgenda("agenda_ConfirmUsageAlice");
		agendaConfirmUsageAlice.setRole(agendaConfirmUsageAlice.getRole(), "confirmation");
		agendaConfirmUsageAlice.setPriority(0, 1200);
		agendaConfirmUsageAlice.setVariableOperator("", "REQUIRES(%var_Temp%==requestCnt)");
		linkAgendaWithSumAgenda(agendaConfirmUsageAlice, sumAgendaWarnings);
		agendaRequestUsageAlice.addNext(agendaConfirmUsageAlice);
		
		
		agendaConfirmUsageBackhaus.addHas(moveSysSaidUsage);
		agendaConfirmUsageTrokar.addHas(moveSysSaidUsage);
		agendaConfirmUsageClip.addHas(moveSysSaidUsage);
		agendaConfirmUsagePean.addHas(moveSysSaidUsage);
		agendaConfirmUsageAlice.addHas(moveSysSaidUsage);
		
		
		Move moveUserConfirmUsageBackhaus = factory.createMove("move_user_ConfirmUsageBackhaus");
		addGrammarToMove(moveUserConfirmUsageBackhaus, grAffirm);
		moveUserConfirmUsageBackhaus.setVariableOperator("SET(" + factory.getVariable("var_tool_cnt_Backhaus") + "=%var_SaidUsage%; " + varTemp + "=confirmCnt)");
		agendaConfirmUsageBackhaus.addHas(moveUserConfirmUsageBackhaus);
		
		Move moveUserConfirmUsageTrokar = factory.createMove("move_user_ConfirmUsageTrokar");
		addGrammarToMove(moveUserConfirmUsageTrokar, grAffirm);
		moveUserConfirmUsageTrokar.setVariableOperator("SET(" + factory.getVariable("var_tool_cnt_Trokar") + "=%var_SaidUsage%; " + varTemp + "=confirmCnt)");
		agendaConfirmUsageTrokar.addHas(moveUserConfirmUsageTrokar);
		
		Move moveUserConfirmUsageClip = factory.createMove("move_user_ConfirmUsageClip");
		addGrammarToMove(moveUserConfirmUsageClip, grAffirm);
		moveUserConfirmUsageClip.setVariableOperator("SET(" + factory.getVariable("var_tool_cnt_Clip") + "=%var_SaidUsage%; " + varTemp + "=confirmCnt)");
		agendaConfirmUsageClip.addHas(moveUserConfirmUsageClip);
		
		Move moveUserConfirmUsagePean = factory.createMove("move_user_ConfirmUsagePean");
		addGrammarToMove(moveUserConfirmUsagePean, grAffirm);
		moveUserConfirmUsagePean.setVariableOperator("SET(" + factory.getVariable("var_tool_cnt_Pean") + "=%var_SaidUsage%; " + varTemp + "=confirmCnt)");
		agendaConfirmUsagePean.addHas(moveUserConfirmUsagePean);
		
		Move moveUserConfirmUsageAlice = factory.createMove("move_user_ConfirmUsageAlice");
		addGrammarToMove(moveUserConfirmUsageAlice, grAffirm);
		moveUserConfirmUsageAlice.setVariableOperator("SET(" + factory.getVariable("var_tool_cnt_Alice") + "=%var_SaidUsage%; " + varTemp + "=confirmCnt)");
		agendaConfirmUsageAlice.addHas(moveUserConfirmUsageAlice);
		
		
		Move moveUserDenyUsageBackhaus = factory.createMove("move_user_DenyUsageBackhaus");
		addGrammarToMove(moveUserDenyUsageBackhaus, grDeny);
		moveUserDenyUsageBackhaus.setVariableOperator("SET(" + varTemp + "=denyCnt)");
		agendaConfirmUsageBackhaus.addHas(moveUserDenyUsageBackhaus);
		
		Move moveUserDenyUsageTrokar = factory.createMove("move_user_DenyUsageTrokar");
		addGrammarToMove(moveUserDenyUsageTrokar, grDeny);
		moveUserDenyUsageTrokar.setVariableOperator("SET(" + varTemp + "=denyCnt)");
		agendaConfirmUsageTrokar.addHas(moveUserDenyUsageTrokar);
		
		Move moveUserDenyUsageClip = factory.createMove("move_user_DenyUsageClip");
		addGrammarToMove(moveUserDenyUsageClip, grDeny);
		moveUserDenyUsageClip.setVariableOperator("SET(" + varTemp + "=denyCnt)");
		agendaConfirmUsageClip.addHas(moveUserDenyUsageClip);
		
		Move moveUserDenyUsagePean = factory.createMove("move_user_DenyUsagePean");
		addGrammarToMove(moveUserDenyUsagePean, grDeny);
		moveUserDenyUsagePean.setVariableOperator("SET(" + varTemp + "=denyCnt)");
		agendaConfirmUsagePean.addHas(moveUserDenyUsagePean);
		
		Move moveUserDenyUsageAlice = factory.createMove("move_user_DenyUsageAlice");
		addGrammarToMove(moveUserDenyUsageAlice, grDeny);
		moveUserDenyUsageAlice.setVariableOperator("SET(" + varTemp + "=denyCnt)");
		agendaConfirmUsageAlice.addHas(moveUserDenyUsageAlice);
		
		
		Semantic semConfirmUsageBackhaus = factory.createSemantic("sem_ConfirmUsageBackhaus");
	    semConfirmUsageBackhaus.setConfirmationInfo(true);
	    linkSemWithSemGroup(semConfirmUsageBackhaus, semGroupTool);
	    
	    Semantic semConfirmUsageTrokar = factory.createSemantic("sem_ConfirmUsageTrokar");
	    semConfirmUsageTrokar.setConfirmationInfo(true);
	    linkSemWithSemGroup(semConfirmUsageTrokar, semGroupTool);
	    
	    Semantic semConfirmUsageClip = factory.createSemantic("sem_ConfirmUsageClip");
	    semConfirmUsageClip.setConfirmationInfo(true);
	    linkSemWithSemGroup(semConfirmUsageClip, semGroupTool);
	    
	    Semantic semConfirmUsagePean = factory.createSemantic("sem_ConfirmUsagePean");
	    semConfirmUsagePean.setConfirmationInfo(true);
	    linkSemWithSemGroup(semConfirmUsagePean, semGroupTool);
	    
	    Semantic semConfirmUsageAlice = factory.createSemantic("sem_ConfirmUsageAlice");
	    semConfirmUsageAlice.setConfirmationInfo(true);
	    linkSemWithSemGroup(semConfirmUsageAlice, semGroupTool);
	    
	    
	    Semantic semDenyUsageBackhaus = factory.createSemantic("sem_DenyUsageBackhaus");
	    semDenyUsageBackhaus.setConfirmationInfo(false);
	    linkSemWithSemGroup(semDenyUsageBackhaus, semGroupTool);
	    
	    Semantic semDenyUsageTrokar = factory.createSemantic("sem_DenyUsageTrokar");
	    semDenyUsageTrokar.setConfirmationInfo(false);
	    linkSemWithSemGroup(semDenyUsageTrokar, semGroupTool);
	    
	    Semantic semDenyUsageClip = factory.createSemantic("sem_DenyUsageClip");
	    semDenyUsageClip.setConfirmationInfo(false);
	    linkSemWithSemGroup(semDenyUsageClip, semGroupTool);
	    
	    Semantic semDenyUsagePean = factory.createSemantic("sem_DenyUsagePean");
	    semDenyUsagePean.setConfirmationInfo(false);
	    linkSemWithSemGroup(semDenyUsagePean, semGroupTool);
	    
	    Semantic semDenyUsageAlice = factory.createSemantic("sem_DenyUsageAlice");
	    semDenyUsageAlice.setConfirmationInfo(false);
	    linkSemWithSemGroup(semDenyUsageAlice, semGroupTool);
	    
	    
	    addSemAndContrarySem(moveUserConfirmUsageBackhaus, semConfirmUsageBackhaus, semDenyUsageBackhaus);
        addSemAndContrarySem(moveUserDenyUsageBackhaus, semDenyUsageBackhaus, semConfirmUsageBackhaus);
		
		addSemAndContrarySem(moveUserConfirmUsageTrokar, semConfirmUsageTrokar, semDenyUsageTrokar);
        addSemAndContrarySem(moveUserDenyUsageTrokar, semDenyUsageTrokar, semConfirmUsageTrokar);
	
		addSemAndContrarySem(moveUserConfirmUsageClip, semConfirmUsageClip, semDenyUsageClip);
        addSemAndContrarySem(moveUserDenyUsageClip, semDenyUsageClip, semConfirmUsageClip);
		
		addSemAndContrarySem(moveUserConfirmUsagePean, semConfirmUsagePean, semDenyUsagePean);
        addSemAndContrarySem(moveUserDenyUsagePean, semDenyUsagePean, semConfirmUsagePean);
		
		addSemAndContrarySem(moveUserConfirmUsageAlice, semConfirmUsageAlice, semDenyUsageAlice);
        addSemAndContrarySem(moveUserDenyUsageAlice, semDenyUsageAlice, semConfirmUsageAlice);
        
        
		Agenda agendaAdaptUsage1 = factory.createAgenda("agenda_AdaptUsage1");
		agendaAdaptUsage1.setRole(agendaAdaptUsage1.getRole(), "collection");
		agendaAdaptUsage1.setPriority(0, 1300);
		agendaAdaptUsage1.setVariableOperator("", "REQUIRES((%var_Temp%==confirmCnt) && (%var_CurrentPart%==1))");
		linkAgendaWithSumAgenda(agendaAdaptUsage1, sumAgendaWarnings);
		agendaAdaptUsage1.addHas(moveSysAdaptUsage);
		agendaConfirmUsageBackhaus.addNext(agendaAdaptUsage1);
		agendaAdaptUsage1.addNext(agendaOp1);
		
		Agenda agendaAdaptUsage2 = factory.createAgenda("agenda_AdaptUsage2");
		agendaAdaptUsage2.setRole(agendaAdaptUsage2.getRole(), "collection");
		agendaAdaptUsage2.setPriority(0, 1300);
		agendaAdaptUsage2.setVariableOperator("", "REQUIRES((%var_Temp%==confirmCnt) && (%var_CurrentPart%==2_3))");
		linkAgendaWithSumAgenda(agendaAdaptUsage2, sumAgendaWarnings);
		agendaAdaptUsage2.addHas(moveSysAdaptUsage);
		agendaConfirmUsageTrokar.addNext(agendaAdaptUsage2);
		agendaAdaptUsage2.addNext(agendaOp2_3);
		
		Agenda agendaAdaptUsage3 = factory.createAgenda("agenda_AdaptUsage3");
		agendaAdaptUsage3.setRole(agendaAdaptUsage3.getRole(), "collection");
		agendaAdaptUsage3.setPriority(0, 1300);
		agendaAdaptUsage3.setVariableOperator("", "REQUIRES((%var_Temp%==confirmCnt) && (%var_CurrentPart%==3_5))");
		linkAgendaWithSumAgenda(agendaAdaptUsage3, sumAgendaWarnings);
		agendaAdaptUsage3.addHas(moveSysAdaptUsage);
		agendaConfirmUsageTrokar.addNext(agendaAdaptUsage3);
		agendaAdaptUsage3.addNext(agendaOp3_5);
		
		Agenda agendaAdaptUsage5 = factory.createAgenda("agenda_AdaptUsage5");
		agendaAdaptUsage5.setRole(agendaAdaptUsage5.getRole(), "collection");
		agendaAdaptUsage5.setPriority(0, 1300);
		agendaAdaptUsage5.setVariableOperator("", "REQUIRES((%var_Temp%==confirmCnt) && (%var_CurrentPart%==5))");
		linkAgendaWithSumAgenda(agendaAdaptUsage5, sumAgendaWarnings);
		agendaAdaptUsage5.addHas(moveSysAdaptUsage);
		agendaConfirmUsageClip.addNext(agendaAdaptUsage5);
		agendaAdaptUsage5.addNext(agendaOp5);
		
		Agenda agendaAdaptUsage6 = factory.createAgenda("agenda_AdaptUsage6");
		agendaAdaptUsage6.setRole(agendaAdaptUsage6.getRole(), "collection");
		agendaAdaptUsage6.setPriority(0, 1300);
		agendaAdaptUsage6.setVariableOperator("", "REQUIRES((%var_Temp%==confirmCnt) && (%var_CurrentPart%==6))");
		linkAgendaWithSumAgenda(agendaAdaptUsage6, sumAgendaWarnings);
		agendaAdaptUsage6.addHas(moveSysAdaptUsage);
		agendaConfirmUsageClip.addNext(agendaAdaptUsage6);
		agendaAdaptUsage6.addNext(agendaOp6);
		
		Agenda agendaAdaptUsage8 = factory.createAgenda("agenda_AdaptUsage8");
		agendaAdaptUsage8.setRole(agendaAdaptUsage8.getRole(), "collection");
		agendaAdaptUsage8.setPriority(0, 1300);
		agendaAdaptUsage8.setVariableOperator("", "REQUIRES((%var_Temp%==confirmCnt) && (%var_CurrentPart%==8_3))");
		linkAgendaWithSumAgenda(agendaAdaptUsage8, sumAgendaWarnings);
		agendaAdaptUsage8.addHas(moveSysAdaptUsage);
		agendaConfirmUsagePean.addNext(agendaAdaptUsage8);
		agendaConfirmUsageAlice.addNext(agendaAdaptUsage8);
		agendaConfirmUsageTrokar.addNext(agendaAdaptUsage8);
		agendaAdaptUsage8.addNext(agendaOp8_3);
		
		
		
		Agenda agendaRequestEmergencyMode = factory.createAgenda("agenda_RequestEmergencyMode");
		agendaRequestEmergencyMode.setRole(agendaRequestEmergencyMode.getRole(), "confirmation");
		agendaRequestEmergencyMode.setPriority(0, 1100);
		agendaRequestEmergencyMode.setVariableOperator("", "REQUIRES(%var_Temp% == confirmWarnTool)");
		linkAgendaWithSumAgenda(agendaRequestEmergencyMode, sumAgendaWarnings);
		agendaWarnTool1More.addNext(agendaRequestEmergencyMode);
		agendaWarnTool2More.addNext(agendaRequestEmergencyMode);
		agendaWarnTool3More.addNext(agendaRequestEmergencyMode);
		agendaWarnTool5More.addNext(agendaRequestEmergencyMode);
		agendaWarnTool6More.addNext(agendaRequestEmergencyMode);
		agendaWarnTool8aMore.addNext(agendaRequestEmergencyMode);
		agendaWarnTool8bMore.addNext(agendaRequestEmergencyMode);
		agendaWarnTool8cMore.addNext(agendaRequestEmergencyMode);
		agendaWarnTool1Less.addNext(agendaRequestEmergencyMode);
		agendaWarnTool2Less.addNext(agendaRequestEmergencyMode);
		agendaWarnTool3Less.addNext(agendaRequestEmergencyMode);
		agendaWarnTool5Less.addNext(agendaRequestEmergencyMode);
		agendaWarnTool6Less.addNext(agendaRequestEmergencyMode);
		agendaWarnTool8aLess.addNext(agendaRequestEmergencyMode);
		agendaWarnTool8bLess.addNext(agendaRequestEmergencyMode);
		agendaWarnTool8cLess.addNext(agendaRequestEmergencyMode);
		
		Move moveSysRequestEmergencyMode = createSysMoveWithUtterance(factory, "RequestEmergencyMode");
		agendaRequestEmergencyMode.addHas(moveSysRequestEmergencyMode);

		
		Variable varEmergency = factory.createVariable("var_Emergency");
        varEmergency.setDefaultValue("", "normal");
        linkVarWithSemGroup(varEmergency, semGroupEmergency);
        
		
        Move moveUserConfirmEmergencyMode = factory.createMove("move_user_ConfirmEmergencyMode");
		addGrammarToMove(moveUserConfirmEmergencyMode, grAffirm);
		moveUserConfirmEmergencyMode.setVariableOperator("SET(" + varEmergency + "=start");
        agendaRequestEmergencyMode.addHas(moveUserConfirmEmergencyMode);
		
		Move moveUserDenyEmergencyMode = factory.createMove("move_user_DenyEmergencyMode");
		addGrammarToMove(moveUserDenyEmergencyMode, grDeny);
		moveUserDenyEmergencyMode.setVariableOperator("SET(" + varEmergency + "=deny; " + varTemp + "=default)");
		agendaRequestEmergencyMode.addHas(moveUserDenyEmergencyMode);
		
		
		Semantic semConfirmEmergencyMode = factory.createSemantic("sem_ConfirmEmergencyMode");
        semConfirmEmergencyMode.setConfirmationInfo(true);
        linkSemWithSemGroup(semConfirmEmergencyMode, semGroupEmergency);
        
        Semantic semDenyEmergencyMode = factory.createSemantic("sem_DenyEmergencyMode");
        semDenyEmergencyMode.setConfirmationInfo(false);
        linkSemWithSemGroup(semDenyEmergencyMode, semGroupEmergency);
        
        addSemAndContrarySem(moveUserConfirmEmergencyMode, semConfirmEmergencyMode, semDenyEmergencyMode);
		addSemAndContrarySem(moveUserDenyEmergencyMode, semDenyEmergencyMode, semConfirmEmergencyMode);
		agendaRequestEmergencyMode.addHas(moveUserConfirmEmergencyMode);
		agendaRequestEmergencyMode.addHas(moveUserDenyEmergencyMode);
		
		
		Agenda agendaStartedEmergencyMode = factory.createAgenda("agenda_StartedEmergencyMode");
		agendaStartedEmergencyMode.setRole(agendaStartedEmergencyMode.getRole(), "collection");
		agendaStartedEmergencyMode.setPriority(0, 1200);
		agendaStartedEmergencyMode.setVariableOperator("", "REQUIRES(%var_Emergency%==start)");
		linkAgendaWithSumAgenda(agendaStartedEmergencyMode, sumAgendaWarnings);
		agendaRequestEmergencyMode.addNext(agendaStartedEmergencyMode);

		
		Move moveSysStartedEmergencyMode = createSysMoveWithUtterance(factory, "StartedEmergencyMode");
		agendaStartedEmergencyMode.addHas(moveSysStartedEmergencyMode);
		moveSysStartedEmergencyMode.setVariableOperator("SET(" + varEmergency + "=emergency)");
		
		Agenda agendaEmergencyMode = factory.createAgenda("agenda_EmergencyMode");
		agendaEmergencyMode.setRole(agendaStartedEmergencyMode.getRole(), "collection");
		agendaEmergencyMode.setPriority(0, 1300);
		agendaEmergencyMode.setVariableOperator("", "REQUIRES(%var_Emergency%==emergency)");
		linkAgendaWithSumAgenda(agendaEmergencyMode, sumAgendaWarnings);
		agendaStartedEmergencyMode.addNext(agendaEmergencyMode);
		
		
		Move moveUserEndEmergencyMode = factory.createMove("move_user_EndEmergencyMode");
		addGrammarToMove(moveUserEndEmergencyMode, grEndEmergencyMode);
		moveUserEndEmergencyMode.setVariableOperator("SET(" + varEmergency + "=end)");
		agendaEmergencyMode.addHas(moveUserEndEmergencyMode);
		
		Agenda agendaEndEmergencyMode = factory.createAgenda("agenda_EndEmergencyMode");
		agendaEndEmergencyMode.setRole(agendaEndEmergencyMode.getRole(), "confirmation");
		agendaEndEmergencyMode.setPriority(0, 1400);
		agendaEndEmergencyMode.setVariableOperator("", "REQUIRES(%var_Emergency%==end)");
		linkAgendaWithSumAgenda(agendaEndEmergencyMode, sumAgendaWarnings);
		agendaEmergencyMode.addNext(agendaEndEmergencyMode);
		agendaEndEmergencyMode.addNext(agendaEmergencyMode);

		Move moveSysEndEmergencyMode = createSysMoveWithUtterance(factory, "EndEmergencyMode");
		agendaEndEmergencyMode.addHas(moveSysEndEmergencyMode);
		
		
		Move moveUserConfirmEndEmergencyMode = factory.createMove("move_user_ConfirmEndEmergencyMode");
		addGrammarToMove(moveUserConfirmEndEmergencyMode, grAffirm);
		moveUserConfirmEndEmergencyMode.setVariableOperator("SET(" + varEmergency + "=ended)");

		
		Move moveUserDenyEndEmergencyMode = factory.createMove("move_user_DenyEndEmergencyMode");
		addGrammarToMove(moveUserDenyEndEmergencyMode, grDeny);
		moveUserDenyEndEmergencyMode.setVariableOperator("SET(" + varEmergency + "=emergency)");
		
		
		addSemAndContrarySem(moveUserConfirmEndEmergencyMode, semDenyEmergencyMode, semConfirmEmergencyMode);
		addSemAndContrarySem(moveUserDenyEndEmergencyMode, semConfirmEmergencyMode, semDenyEmergencyMode);
		agendaEndEmergencyMode.addHas(moveUserConfirmEndEmergencyMode);
		agendaEndEmergencyMode.addHas(moveUserDenyEndEmergencyMode);
		
		
		Agenda agendaAdaptRequest1 = factory.createAgenda("agenda_AdaptRequest1");
		agendaAdaptRequest1.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 1) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest1.addNext(agendaOp1);
		
		Agenda agendaAdaptRequest2 = factory.createAgenda("agenda_AdaptRequest2");
		agendaAdaptRequest2.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 2_3) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest2.addNext(agendaOp2_3);
		
		Agenda agendaAdaptRequest3 = factory.createAgenda("agenda_AdaptRequest3");
		agendaAdaptRequest3.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 3_5) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest3.addNext(agendaOp3_5);
		
		Agenda agendaAdaptRequest5 = factory.createAgenda("agenda_AdaptRequest5");
		agendaAdaptRequest5.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 5) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest5.addNext(agendaOp5);
		
		Agenda agendaAdaptRequest6 = factory.createAgenda("agenda_AdaptRequest6");
		agendaAdaptRequest6.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 6) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest6.addNext(agendaOp6);
		
		Agenda agendaAdaptRequest8a = factory.createAgenda("agenda_AdaptRequest8a");
		agendaAdaptRequest8a.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 8_3) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest8a.addNext(agendaOp8_3);
		
		Agenda agendaAdaptRequest8b = factory.createAgenda("agenda_AdaptRequest8b");
		agendaAdaptRequest8b.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 8_3) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest8b.addNext(agendaOp8_3);
		
		Agenda agendaAdaptRequest8c = factory.createAgenda("agenda_AdaptRequest8c");
		agendaAdaptRequest8c.setVariableOperator("", "REQUIRES(((%var_CurrentPart% == 8_3) && ((%var_Emergency% == normal) || (%var_Emergency% == deny))) && ((%var_Temp% == default) && ((%var_Warn% == less) || (%var_Warn% == more))))");
		agendaAdaptRequest8c.addNext(agendaOp8_3);
		
		
		Collection<Agenda> colAgendaAdaptRequest = factory.getAllAgendaInstances();
		for (Agenda a : colAgendaAdaptRequest) {
			if (a.getLocalName().contains("agenda_AdaptRequest")) {
				a.setRole(a.getRole(), "confirmation");
				a.setPriority(0, 1600);
				linkAgendaWithSumAgenda(a, sumAgendaWarnings);
				a.addRequires(semDenyEmergencyMode);
				agendaRequestEmergencyMode.addNext(a);
			}
		}
		
		Move moveSysAdaptRequest1 = createSysMoveWithUtterance(factory, "AdaptRequest1");
		moveSysAdaptRequest1.setVariableOperator("SET(" + 
				varExpectedBackhaus1 + "= %var_tool_cnt_Backhaus%; " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest1.addHas(moveSysAdaptRequest1);
		
		Move moveSysAdaptRequest2 = createSysMoveWithUtterance(factory, "AdaptRequest2");
		moveSysAdaptRequest2.setVariableOperator("SET(" + 
				varExpectedTrokar2 + "= %var_tool_cnt_Trokar%; " + 
				varExpectedTrokar2b + "= (%var_tool_cnt_Trokar% ++ 1); " + 
				varExpectedTrokar3 + "= (%var_tool_cnt_Trokar% ++ 3); " + 
				varExpectedTrokar8c + "= (%var_tool_cnt_Trokar% ++ 4); " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest2.addHas(moveSysAdaptRequest2);
		
		Move moveSysAdaptRequest3 = createSysMoveWithUtterance(factory, "AdaptRequest3");
		moveSysAdaptRequest3.setVariableOperator("SET(" + 
				varExpectedTrokar3 + "= %var_tool_cnt_Trokar%; " + 
				varExpectedTrokar8c + "= (%var_tool_cnt_Trokar% ++ 1); " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest3.addHas(moveSysAdaptRequest3);
		
		Move moveSysAdaptRequest8c = createSysMoveWithUtterance(factory, "AdaptRequest8c");
		moveSysAdaptRequest8c.setVariableOperator("SET(" + 
				varExpectedTrokar8c + "= %var_tool_cnt_Trokar%; " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest8c.addHas(moveSysAdaptRequest8c);
		
		Move moveSysAdaptRequest5 = createSysMoveWithUtterance(factory, "AdaptRequest5");
		moveSysAdaptRequest5.setVariableOperator("SET(" + 
				varExpectedClip5 + "= %var_tool_cnt_Clip%; " + 
				varExpectedClip6 + "= (%var_tool_cnt_Clip% ++ 3); " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest5.addHas(moveSysAdaptRequest5);
		
		Move moveSysAdaptRequest6 = createSysMoveWithUtterance(factory, "AdaptRequest6");
		moveSysAdaptRequest6.setVariableOperator("SET(" + 
				varExpectedClip6 + "= %var_tool_cnt_Clip%; " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest6.addHas(moveSysAdaptRequest6);
		
		Move moveSysAdaptRequest8a = createSysMoveWithUtterance(factory, "AdaptReques8a");
		moveSysAdaptRequest8a.setVariableOperator("SET(" + 
				varExpectedPean8a + "= %var_tool_cnt_Pean%; " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest8a.addHas(moveSysAdaptRequest8a);
		
		Move moveSysAdaptRequest8b = createSysMoveWithUtterance(factory, "AdaptRequest8b");
		moveSysAdaptRequest8b.setVariableOperator("SET(" + 
				varExpectedAlice8b + "= %var_tool_cnt_Alice%; " + 
				varTemp + "= adaptExpectedUsage; " + 
				varWarn + "= false; " + 
				varEmergency + "= normal)");
		agendaAdaptRequest8b.addHas(moveSysAdaptRequest8b);
	
		
		Semantic semStartEmergencyMode = factory.createSemantic("sem_StartEmergencyMode");
		linkSemWithSemGroup(semStartEmergencyMode, semGroupEmergency);
		
		Move moveUserStartEmergencyMode1 = factory.createMove("move_user_StartEmergencyMode1");
		Move moveUserStartEmergencyMode2 = factory.createMove("move_user_StartEmergencyMode2");
		Move moveUserStartEmergencyMode2_2 = factory.createMove("move_user_StartEmergencyMode2_2");
		Move moveUserStartEmergencyMode2_3 = factory.createMove("move_user_StartEmergencyMode2_3");
		Move moveUserStartEmergencyMode3 = factory.createMove("move_user_StartEmergencyMode3");
		Move moveUserStartEmergencyMode3_2 = factory.createMove("move_user_StartEmergencyMode3_2");
		Move moveUserStartEmergencyMode3_3 = factory.createMove("move_user_StartEmergencyMode3_3");
		Move moveUserStartEmergencyMode3_4 = factory.createMove("move_user_StartEmergencyMode3_4");
		Move moveUserStartEmergencyMode3_5 = factory.createMove("move_user_StartEmergencyMode3_5");
		Move moveUserStartEmergencyMode4 = factory.createMove("move_user_StartEmergencyMode4");
		Move moveUserStartEmergencyMode5 = factory.createMove("move_user_StartEmergencyMode5");
		Move moveUserStartEmergencyMode6 = factory.createMove("move_user_StartEmergencyMode6");
		Move moveUserStartEmergencyMode7 = factory.createMove("move_user_StartEmergencyMode7");
		Move moveUserStartEmergencyMode8 = factory.createMove("move_user_StartEmergencyMode8");
		Move moveUserStartEmergencyMode8_2 = factory.createMove("move_user_StartEmergencyMode8_2");
		Move moveUserStartEmergencyMode8_3 = factory.createMove("move_user_StartEmergencyMode8_3");
		Move moveUserStartEmergencyMode9 = factory.createMove("move_user_StartEmergencyMode9");
		Move moveUserStartEmergencyMode10 = factory.createMove("move_user_StartEmergencyMode10");
		
		
		Collection<Move> moveCol = factory.getAllMoveInstances();
		for (Move em : moveCol) {
			String localName = em.getLocalName();
			if (localName.contains("move_user_StartEmergencyMode")) {
				String num = localName.substring(28);
				addGrammarToMove(em, grStartEmergencyMode);
				em.addSemantic(semStartEmergencyMode);
				em.setVariableOperator("SET(" + varEmergency + "=start; " + varCurrentPart + "=" + num + ")");
				factory.getAgenda("agenda_Op" + num).addHas(em);
			}
		}
		
		
		Agenda agendaEndedEmergencyMode1 = factory.createAgenda("agenda_EndedEmergencyMode1");
		Agenda agendaEndedEmergencyMode2 = factory.createAgenda("agenda_EndedEmergencyMode2");
		Agenda agendaEndedEmergencyMode2_2 = factory.createAgenda("agenda_EndedEmergencyMode2_2");
		Agenda agendaEndedEmergencyMode2_3 = factory.createAgenda("agenda_EndedEmergencyMode2_3");
		Agenda agendaEndedEmergencyMode3 = factory.createAgenda("agenda_EndedEmergencyMode3");
		Agenda agendaEndedEmergencyMode3_2 = factory.createAgenda("agenda_EndedEmergencyMode3_2");
		Agenda agendaEndedEmergencyMode3_3 = factory.createAgenda("agenda_EndedEmergencyMode3_3");
		Agenda agendaEndedEmergencyMode3_4 = factory.createAgenda("agenda_EndedEmergencyMode3_4");
		Agenda agendaEndedEmergencyMode3_5 = factory.createAgenda("agenda_EndedEmergencyMode3_5");
		Agenda agendaEndedEmergencyMode4 = factory.createAgenda("agenda_EndedEmergencyMode4");
		Agenda agendaEndedEmergencyMode5 = factory.createAgenda("agenda_EndedEmergencyMode5");
		Agenda agendaEndedEmergencyMode6 = factory.createAgenda("agenda_EndedEmergencyMode6");
		Agenda agendaEndedEmergencyMode7 = factory.createAgenda("agenda_EndedEmergencyMode7");
		Agenda agendaEndedEmergencyMode8 = factory.createAgenda("agenda_EndedEmergencyMode8");
		Agenda agendaEndedEmergencyMode8_2 = factory.createAgenda("agenda_EndedEmergencyMode8_2");
		Agenda agendaEndedEmergencyMode8_3 = factory.createAgenda("agenda_EndedEmergencyMode8_3");
		Agenda agendaEndedEmergencyMode9 = factory.createAgenda("agenda_EndedEmergencyMode9");
		Agenda agendaEndedEmergencyMode10 = factory.createAgenda("agenda_EndedEmergencyMode10");
		
		agendaEndedEmergencyMode1.addNext(agendaAdaptRequest1);
		agendaEndedEmergencyMode2_3.addNext(agendaAdaptRequest2);
		agendaEndedEmergencyMode3_5.addNext(agendaAdaptRequest3);
		agendaEndedEmergencyMode5.addNext(agendaAdaptRequest5);
		agendaEndedEmergencyMode6.addNext(agendaAdaptRequest6);
		agendaEndedEmergencyMode8_3.addNext(agendaAdaptRequest8a);
		agendaEndedEmergencyMode8_3.addNext(agendaAdaptRequest8b);
		agendaEndedEmergencyMode8_3.addNext(agendaAdaptRequest8c);

		Move moveSysEndedEmergencyMode = createSysMoveWithUtterance(factory, "EndedEmergencyMode");
		moveSysEndedEmergencyMode.setVariableOperator("SET(" + varTemp + "=default; " + varEmergency + "=normal)");
     	
		for (int i = 1; i <= 10; i++) {
			int j = i+1;
			Agenda agendaOp = factory.getAgenda("agenda_Op" + i);
			Agenda agendaNextOp = factory.getAgenda("agenda_Op" + j);
			if (i < 10) {
				agendaOp.addNext(agendaNextOp);
			} else {
				agendaOp.addNext(agendaBye);
			}
		}

		
		
		//Agendas for operation		
		
		// (1) Anlage der Hautinzision, Einbringen des ersten Trokars
		
     	agendaOp1.addRequires(semConfirmStartOp);
     	agendaOp1.setVariableOperator("", "REQUIRES(%var_tool_cnt_Probier% == 0)");
		agendaOp1.addHas(factory.getMove("move_user_ToolSkalpell"));
		agendaOp1.addHas(factory.getMove("move_user_ToolBackhaus"));
		agendaOp1.addHas(factory.getMove("move_user_ToolVeress"));
		agendaOp1.addNext(agendaOp2_2); // if less Backhauser used
		
		// (2) Erzeugen des Pneumoperitoneums
		
		agendaOp2.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Skalpell% >= 1) && (%var_tool_cnt_Backhaus% == %var_ExpectedBackhaus1%)) && "
				+ "(%var_tool_cnt_Veress% == 0))");
		agendaOp2.addHas(factory.getMove("move_user_ToolBackhaus")); // if more Backhauser used
		agendaOp2.addHas(factory.getMove("move_user_ToolVeress"));
		agendaOp2.addNext(agendaOp2_2);
		
		agendaOp2_2.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Probier% == 0) && "
				+ "((%var_tool_cnt_Skalpell% >= 1) && (%var_tool_cnt_Backhaus% == %var_ExpectedBackhaus1%))) && "
				+ "(%var_tool_cnt_Veress% >= 1))");
		agendaOp2_2.addHas(moveSysRequestStartGas);
		agendaOp2_2.addHas(moveUserConfirmStartGas);
     	agendaOp2_2.addHas(moveUserDenyStartGas);
     	agendaOp2_2.addNext(agendaOp2_3);
     	agendaOp2_2.addNext(agendaStartGasManually);
     	   	
     	agendaStartGasManually.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Probier% == 0) && "
     			+ "((%var_tool_cnt_Skalpell% >= 1) && (%var_tool_cnt_Backhaus% == %var_ExpectedBackhaus1%))) && "
				+ "((%var_tool_cnt_Veress% >= 1) && (%var_SubDial% == 1)))");
     	agendaStartGasManually.addRequires(semDenyStartGas);
     	agendaStartGasManually.addHas(moveSysStartGasManually);
     	agendaStartGasManually.addHas(moveUserGas);
     	agendaStartGasManually.addNext(agendaOp2_3);
     	
     	agendaOp2_3.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Probier% == 0) && "
     			+ "((%var_tool_cnt_Skalpell% >= 1) && (%var_tool_cnt_Backhaus% == %var_ExpectedBackhaus1%))) && "
				+ "((%var_tool_cnt_Veress% >= 1) && (%var_SubDial% == 0)))");
     	agendaOp2_3.addRequires(semConfirmStartGas);
     	agendaOp2_3.addHas(moveSysGasFlowStarted);
     	agendaOp2_3.addNext(agendaOp2_4);
     	
     	agendaOp2_4.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Probier% == 0) && "
     			+ "((%var_tool_cnt_Skalpell% >= 1) && (%var_tool_cnt_Backhaus% == %var_ExpectedBackhaus1%))) && "
				+ "((%var_tool_cnt_Veress% >= 1) && (%var_SubDial% == 0)))");
     	agendaOp2_4.addHas(moveUserUndoStartGas);
		agendaOp2_4.addHas(factory.getMove("move_user_ToolTrokar"));
		agendaOp2_4.addHas(moveUserGasConnection);
		agendaOp2_4.addHas(factory.getMove("move_user_ToolOptik"));
		agendaOp2_4.addNext(agendaUndoStartGas);
		agendaOp2_4.addNext(agendaOp3);
		agendaOp2_4.addNext(agendaOp3_2); // if less trocars used
		
		agendaUndoStartGas.setVariableOperator("", "REQUIRES(%var_SubDial% == 2)");
		agendaUndoStartGas.addHas(moveSysUndoStartGas);
		agendaUndoStartGas.addHas(moveUserGas);
     	agendaUndoStartGas.addNext(agendaOp2_3);
		
		
		// (3) Einführen der Optik und Setzen der weiteren Trokare

		agendaOp3.setVariableOperator("", "REQUIRES((%var_tool_cnt_Veress% >= 1) && " //always set brackets for pairwise conditions! => recursion
				+ "((%var_tool_cnt_Trokar% == %var_ExpectedTrokar2%) && (%var_assist_cnt_GasConnection% == 1)))");
		agendaOp3.addHas(factory.getMove("move_user_ToolOptik"));
		agendaOp3.addNext(agendaOp3_2);
		
		agendaOp3_2.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Veress% >= 1) && (%var_tool_cnt_Optik% >= 1)) && "
				+ "((%var_tool_cnt_Trokar% == %var_ExpectedTrokar2%) && (%var_assist_cnt_GasConnection% == 1)))");
		agendaOp3_2.addHas(moveSysRequestMaxGas);
		agendaOp3_2.addHas(moveUserConfirmMaxGas);
     	agendaOp3_2.addHas(moveUserDenyMaxGas);
     	agendaOp3_2.addNext(agendaOp3_3);
     	agendaOp3_2.addNext(agendaMaxGasManually);

     	agendaMaxGasManually.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Skalpell% >= 1) && (%var_tool_cnt_Backhaus% == %var_ExpectedBackhaus1%)) && "
				+ "((%var_tool_cnt_Veress% >= 1) && (%var_SubDial% == 1)))");
     	agendaMaxGasManually.addRequires(semDenyMaxGas);
     	agendaMaxGasManually.addHas(moveSysMaxGasManually);
     	agendaMaxGasManually.addHas(moveUserMaxGas);
     	agendaMaxGasManually.addNext(agendaOp3_3);
     	
     	agendaOp3_3.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Veress% >= 1) && (%var_tool_cnt_Trokar% == %var_ExpectedTrokar2%)) && "
				+ "((%var_assist_cnt_GasConnection% == 1) && (%var_SubDial% == 0)))");
     	agendaOp3_3.addRequires(semConfirmMaxGas);
     	agendaOp3_3.addHas(moveSysGasAtMax);
     	agendaOp3_3.addNext(agendaOp3_4);
     	
     	agendaOp3_4.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Veress% >= 1) && (%var_tool_cnt_Trokar% >= %var_ExpectedTrokar2%)) && "
				+ "((%var_assist_cnt_GasConnection% == 1) && (%var_SubDial% == 0)))");
     	agendaOp3_4.addHas(moveUserUndoMaxGas);
		agendaOp3_4.addHas(factory.getMove("move_user_ToolProbier"));
		agendaOp3_4.addHas(factory.getMove("move_user_ToolTrokar"));
		agendaOp3_4.addNext(agendaUndoMaxGas);
     	agendaOp3_4.addNext(agendaOp3_5);
		
		agendaUndoMaxGas.setVariableOperator("", "REQUIRES(%var_SubDial% == 2)");
     	agendaUndoMaxGas.addHas(moveSysUndoMaxGas);
     	agendaUndoMaxGas.addHas(moveUserMaxGas);
     	agendaUndoMaxGas.addNext(agendaOp3_4);
		
		agendaOp3_5.setVariableOperator("", "REQUIRES((%var_tool_cnt_Probier% >= 1) && (%var_tool_cnt_Trokar% == %var_ExpectedTrokar2b%))");
		agendaOp3_5.addHas(moveSysRequestTableRevTrend);
		agendaOp3_5.addHas(moveUserConfirmTableRevTrend);
     	agendaOp3_5.addHas(moveUserDenyTableRevTrend);
     	agendaOp3_5.addNext(agendaOp3_6);
     	agendaOp3_5.addNext(agendaTableRevTrendLightOffManually);
     	
     	agendaTableRevTrendLightOffManually.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Probier% >= 1) && (%var_tool_cnt_Trokar% >= %var_ExpectedTrokar2b%)) && "
     			+ "(%var_SubDial% == 1))");
     	agendaTableRevTrendLightOffManually.addRequires(semDenyTableRevTrend);
     	agendaTableRevTrendLightOffManually.addHas(moveSysTableRevTrendLightOffManually);
     	agendaTableRevTrendLightOffManually.addHas(moveUserTableRevTrendLightOff);
     	agendaTableRevTrendLightOffManually.addNext(agendaOp3_6);
     	
     	agendaOp3_6.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Probier% >= 1) && (%var_tool_cnt_Trokar% >= %var_ExpectedTrokar2b%)) && "
     			+ "(%var_SubDial% == 0))");
     	agendaOp3_6.addRequires(semConfirmTableRevTrend);
     	agendaOp3_6.addHas(moveUserUndoTableRevTrendLightOff);
     	agendaOp3_6.addHas(factory.getMove("move_user_ToolProbier"));
     	agendaOp3_6.addHas(factory.getMove("move_user_ToolTrokar"));
     	agendaOp3_6.addHas(moveUserCleanOptics);
     	agendaOp3_6.addHas(moveUserMoveOptics);
     	agendaOp3_6.addHas(factory.getMove("move_user_ToolTaststab"));
     	agendaOp3_6.addHas(moveUserHandOver);
     	agendaOp3_6.addHas(factory.getMove("move_user_ToolFasszange"));
		agendaOp3_6.addNext(agendaTableRevTrendLightOffManually);
		agendaOp3_6.addNext(agendaOp4);
		
		
		// (4) Präparation des Calot-Dreiecks
		
		agendaOp4.setVariableOperator("", "REQUIRES((((%var_tool_cnt_Optik% == 1) && (%var_tool_cnt_Probier% >= 3)) && "
				+ "((%var_tool_cnt_Trokar% == %var_ExpectedTrokar3%) && (%var_assist_cnt_CleanOptics% == 1))) && "
				+ "(((%var_assist_cnt_MoveOptics% == 1) && (%var_tool_cnt_Taststab% == 1)) && "
				+ "(%var_assist_cnt_HandOver% == 1)))");
		agendaOp4.addHas(factory.getMove("move_user_ToolFasszange"));
		agendaOp4.addHas(factory.getMove("move_user_ToolPE"));
		agendaOp4.addHas(moveUserFocus);
		agendaOp4.addHas(moveUserPowerPE);
		
		
		// (5) Clippen der Arteria Cystica
		
		agendaOp5.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Fasszange% == 1) && (%var_tool_cnt_PE% >= 1)) && "
				+ "((%var_assist_cnt_Focus% == 1) && (%var_assist_cnt_PowerPE% >= 2)))");
		agendaOp5.addHas(factory.getMove("move_user_ToolClip"));
		agendaOp5.addHas(factory.getMove("move_user_ToolSchere"));
		agendaOp5.addHas(factory.getMove("move_user_ToolPE"));
		agendaOp5.addHas(moveUserPowerPE);
		
		
		// (6) Clippen des Ductus Cysticus
		
		agendaOp6.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Clip% >= %var_ExpectedClip5%) && (%var_tool_cnt_Schere% >= 1)) && "
				+ "((%var_tool_cnt_PE% >= 2) && (%var_assist_cnt_PowerPE% >= 3)))");
		agendaOp6.addHas(factory.getMove("move_user_ToolClip"));
		agendaOp6.addHas(factory.getMove("move_user_ToolSchere"));
		
		
		// (7) Auslösen der Gallenblase aus dem Leberbett
		
		agendaOp7.setVariableOperator("", "REQUIRES((%var_tool_cnt_Clip% == %var_ExpectedClip6%) && (%var_tool_cnt_Schere% >= 2))");
		agendaOp7.addHas(factory.getMove("move_user_ToolPE"));
		agendaOp7.addHas(moveUserDistance);
		agendaOp7.addHas(moveUserPowerPE);
		
		
		// (8) Bergen der Gallenblase
		
		agendaOp8.setVariableOperator("", "REQUIRES(((%var_tool_cnt_PE% >= 3) && (%var_assist_cnt_Distance% >= 1)) && "
				+ "(%var_assist_cnt_PowerPE% >= 4))");
		agendaOp8.addHas(factory.getMove("move_user_ToolBergebeutel"));
		agendaOp8.addHas(factory.getMove("move_user_ToolPE"));
     	agendaOp8.addNext(agendaOp8_2);
		
		agendaOp8_2.setVariableOperator("", "REQUIRES((%var_tool_cnt_Bergebeutel% >= 1) && (%var_tool_cnt_PE% >= 4))");
		agendaOp8_2.addHas(moveSysRequestLightOn);
		agendaOp8_2.addHas(moveUserConfirmLightOn);
     	agendaOp8_2.addHas(moveUserDenyLightOn);
     	agendaOp8_2.addNext(agendaOp8_3);
     	agendaOp8_2.addNext(agendaLightOnManually);
     	
     	agendaLightOnManually.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Bergebeutel% >= 1) && (%var_tool_cnt_PE% >= 4)) && "
     			+ "(%var_SubDial% == 1))");
     	agendaLightOnManually.addRequires(semDenyLightOn);
     	agendaLightOnManually.addHas(moveSysLightOnManually);
     	agendaLightOnManually.addHas(moveUserLightOn);
     	agendaLightOnManually.addNext(agendaOp8_3);
		
     	agendaOp8_3.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Bergebeutel% >= 1) && (%var_tool_cnt_PE% >= 4)) && "
     			+ "(%var_SubDial% == 0))");
     	agendaOp8_3.addRequires(semConfirmLightOn);
		agendaOp8_3.addHas(moveUserMoveGas);
		agendaOp8_3.addHas(factory.getMove("move_user_ToolSchere"));
		agendaOp8_3.addHas(factory.getMove("move_user_ToolPean"));
		agendaOp8_3.addHas(factory.getMove("move_user_ToolKompresse"));
		agendaOp8_3.addHas(factory.getMove("move_user_ToolAlice"));
		agendaOp8_3.addHas(moveUserAspiratorToAssistant);
		agendaOp8_3.addHas(moveUserSuction);
		agendaOp8_3.addHas(moveUserClean);
		agendaOp8_3.addHas(moveUserFinger);
		agendaOp8_3.addHas(factory.getMove("move_user_ToolTrokar"));
		agendaOp8_3.addHas(moveUserConnectGas);
		agendaOp8_3.addHas(factory.getMove("move_user_ToolTaststab"));
		agendaOp8_3.addHas(factory.getMove("move_user_ToolSaugspuel"));
		agendaOp8_3.addHas(moveUserUndoRoomLightOn);
		agendaOp8_3.addNext(agendaLightOnManually);
		agendaOp8_3.addNext(agendaOp9);
		
		
		// (9) Blutstillung im Gallenblasenbett
		
		agendaOp9.setVariableOperator("", "REQUIRES(((((%var_tool_cnt_Bergebeutel% >= 1) && (%var_tool_cnt_PE% >= 4)) && "
				+ "((%var_assist_cnt_MoveGas% >= 1) && (%var_tool_cnt_Schere% >= 5))) && "
				+ "(((%var_tool_cnt_Pean% == %var_ExpectedPean8a%) && (%var_tool_cnt_Kompresse% >= 1)) && "
				+ "((%var_tool_cnt_Alice% == %var_ExpectedAlice8b%) && (%var_assist_cnt_AspiratorToAssistant% >= 1)))) && "
				+ "((((%var_assist_cnt_Suction% >= 1) && (%var_assist_cnt_Clean% >= 2)) && "
				+ "((%var_assist_cnt_Finger% >= 1) && (%var_tool_cnt_Trokar% == %var_ExpectedTrokar8c%))) && "
				+ "((%var_assist_cnt_ConnectGas% >= 1) && (%var_tool_cnt_Taststab% >= 2))))");
		agendaOp9.addHas(factory.getMove("move_user_ToolSaugspuel"));
		agendaOp9.addHas(factory.getMove("move_user_ToolHandsauger"));
		agendaOp9.addHas(moveUserPowerPE);
		agendaOp9.addHas(moveUserMoveCameraToHilus);
		agendaOp9.addHas(moveUserMoveCameraToLiver);
		
		
		// (10) Ende der Operation
		
		agendaOp10.addMustnot(semBye);
		agendaOp10.setVariableOperator("", "REQUIRES(((%var_tool_cnt_Saugspuel% >= 1) && (%var_tool_cnt_Handsauger% >= 1)) && "
				+ "(((%var_assist_cnt_PowerPE% >= 6) && (%var_assist_cnt_MoveCameraToHilus% >= 1)) && "
				+ "(%var_assist_cnt_MoveCameraToLiver% >= 1)))");
		agendaOp10.addHas(moveUserRemoveTrocars);
		agendaOp10.addHas(moveUserFinger);
		agendaOp10.addHas(factory.getMove("move_user_ToolDrainage"));
		
		agendaOp10.addHas(moveUserThankyou);
		moveUserBye.setVariableOperator("SET(" + varDB_dev_Table_Tilt + "=0; " + varDB_dev_Table_Height + "=" + tableHeightEnd + ")");
		agendaOp10.addHas(moveUserBye);
		agendaOp10.addNext(agendaBye);
		
		agendaBye.setVariableOperator("", "REQUIRES((((%var_assist_cnt_RemoveTrocars% >= 2) && (%var_assist_cnt_Finger% >= 2)) && "
				+ "(%var_tool_cnt_Drainage% >= 1)) || "
				+ "(%var_Exit% == 1))");
		agendaBye.addRequires(semBye);

		
		Collection<Agenda> agendaCol = factory.getAllAgendaInstances();
		Collection<Agenda> warnToolAgendaCol = new LinkedList<Agenda>();
		Collection<Agenda> opAgendaCol = new LinkedList<Agenda>();

	    for (Agenda a : agendaCol) {
	    	a.addNext(a); //Fallback for all Agendas
	    	a.addNext(agendaAbort); //All Agendas can be aborted
	    	String name = a.getLocalName();
	    	
	    	if (name.equals("agenda_Hello2") || name.equals("agenda_ReqPresets") ||
	    			name.equals("agenda_ReqTeamTimeOut") || name.equals("agenda_StartOp")) {
	    		
		    	a.addHas(moveUserPresetsThermoflator); //doctor shall be able to ask about device presets
		    	a.addHas(moveUserPresetsRoomLight);
		    	a.addHas(moveUserPresetsEndomat);
		    	a.addHas(moveUserPresetsTable);
		    	a.addNext(agendaInfoThermoflatorPresets);
		    	a.addNext(agendaInfoRoomLightPresets);
		    	a.addNext(agendaInfoEndomatPresets);
		    	a.addNext(agendaInfoTablePresets);
		    	agendaInfoThermoflatorPresets.addNext(a);
		    	agendaInfoRoomLightPresets.addNext(a);
		    	agendaInfoEndomatPresets.addNext(a);
		    	agendaInfoTablePresets.addNext(a);
		    	
		    	a.addHas(moveUserChangePresetsThermoflator); //when doctor shall be able to change his device presets
		    	a.addHas(moveUserChangePresetsRoomLight);
		    	a.addHas(moveUserChangePresetsEndomat);
		    	a.addHas(moveUserChangePresetsTable);
		    	a.addNext(agendaChangePresetsThermoflator1);
		    	a.addNext(agendaChangePresetsRoomLight);
		    	a.addNext(agendaChangePresetsEndomat);
		    	a.addNext(agendaChangePresetsTable1);
		    	agendaSavedChanges.addNext(a);
		    	agendaDroppedChanges.addNext(a);
		    	
		    	a.addHas(moveUserSettingsThermoflator); //doctor shall almost always be able to ask about device settings
		    	a.addHas(moveUserSettingsRoomLight);
		    	a.addHas(moveUserSettingsEndomat);
		    	a.addHas(moveUserSettingsTable);
		    	a.addNext(agendaInfoThermoflatorSettings);
		    	a.addNext(agendaInfoRoomLightSettings);
		    	a.addNext(agendaInfoEndomatSettings);
		    	a.addNext(agendaInfoTableSettings);
		    	agendaInfoThermoflatorSettings.addNext(a);
		    	agendaInfoRoomLightSettings.addNext(a);
		    	agendaInfoEndomatSettings.addNext(a);
		    	agendaInfoTableSettings.addNext(a);
		    	
		    	a.addHas(moveUserMedication);
		    	a.addNext(agendaInfoMedication);
		    	agendaInfoMedication.addNext(a);
		    	
		    	a.addHas(moveUserLabData);
		    	a.addNext(agendaInfoLabData);
		    	agendaInfoLabData.addNext(a);
		    	
		    	a.addHas(moveUserExit);
		    	a.addNext(agendaExit);
		    	agendaExit.addNext(a);
	    	}
	    

	    	String localName = a.getLocalName();
	    	
	    	if(localName.contains("agenda_WarnTool")) {
	    		warnToolAgendaCol.add(a);
	    		
	    	} else if (localName.contains("agenda_Op") && !localName.equals("agenda_Open")) {
		    	opAgendaCol.add(a);
		    	
	    	} else if (localName.contains("agenda_EndedEmergencyMode")) {
	    		String opNum = localName.substring(25);
	    		a.setRole(a.getRole(), "collection");
	    		a.setPriority(0, 1500);
	    		a.setVariableOperator("", "REQUIRES((%var_Emergency%==ended) && (%var_CurrentPart%==" + opNum + "))");
	    		linkAgendaWithSumAgenda(a, sumAgendaWarnings);
	    		a.addHas(moveSysEndedEmergencyMode);
	    		agendaEndEmergencyMode.addNext(a);
	    		a.addNext(factory.getAgenda("agenda_Op" + opNum));
	    	}
	    }
	    

	    agendaWrongPatientInfo.removeHas(moveUserSettingsThermoflator);
	    agendaWrongPatientInfo.removeHas(moveUserSettingsRoomLight);
	    agendaWrongPatientInfo.removeHas(moveUserSettingsEndomat);
	    agendaWrongPatientInfo.removeHas(moveUserSettingsTable);
	    
	    agendaWrongPatientInfo.removeNext(agendaWrongPatientInfo);
	    
	    agendaAdaptUsage1.removeNext(agendaAdaptUsage1);
	    agendaAdaptUsage2.removeNext(agendaAdaptUsage2);
	    agendaAdaptUsage3.removeNext(agendaAdaptUsage3);
	    agendaAdaptUsage5.removeNext(agendaAdaptUsage5);
	    agendaAdaptUsage6.removeNext(agendaAdaptUsage6);
	    agendaAdaptUsage8.removeNext(agendaAdaptUsage8);
	    
	    agendaAdaptRequest1.removeNext(agendaAdaptRequest1);
	    agendaAdaptRequest2.removeNext(agendaAdaptRequest2);
	    agendaAdaptRequest3.removeNext(agendaAdaptRequest3);
	    agendaAdaptRequest5.removeNext(agendaAdaptRequest5);
	    agendaAdaptRequest6.removeNext(agendaAdaptRequest6);
	    agendaAdaptRequest8a.removeNext(agendaAdaptRequest8a);
	    agendaAdaptRequest8b.removeNext(agendaAdaptRequest8b);
	    agendaAdaptRequest8c.removeNext(agendaAdaptRequest8c);

	    for (Agenda op : opAgendaCol) {
	    	linkAgendaWithSumAgenda(op, sumAgendaOp);
	     	op.setRole(op.getRole(), "collection");
	     	op.addNext(agendaStartedEmergencyMode);
	    	for (Agenda warn : warnToolAgendaCol) {
	    		warn.setRole(warn.getRole(), "confirmation");
	         	warn.setPriority(0, 1000);
	    		linkAgendaWithSumAgenda(warn, sumAgendaWarnings);
	    	}
	    }
		

     	System.out.println("everything created!");
     	
     	try {
            factory.manager.saveOntology(factory.onto,
                    factory.manager.getOntologyDocumentIRI(factory.onto));
        } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
        }
        Settings.uri = uriSave;

	}

}
