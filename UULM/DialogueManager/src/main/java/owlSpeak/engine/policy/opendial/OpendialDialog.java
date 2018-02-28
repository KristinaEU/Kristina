package owlSpeak.engine.policy.opendial;

import java.util.Collection;

import opendial.DialogueState;
import opendial.DialogueSystem;
import opendial.Settings;
import opendial.bn.distribs.CategoricalTable;
import opendial.bn.values.NoneVal;
import opendial.bn.values.Value;
import opendial.domains.Domain;
import opendial.modules.DialogueImporter;
import opendial.modules.Module;
import opendial.modules.WizardLearner;
import opendial.readers.XMLDomainReader;
import opendial.utils.StringUtils;
import opendial.utils.XMLUtils;

/**
 * 
 * Singleton class to provide opendial dialog.
 * 
 * 
 * @author patrick
 *
 */
public class OpendialDialog implements Module {

	static OpendialDialog dialog;
	DialogueSystem system;
	String domainFile = "./conf/OwlSpeak/opendialDomain.xml";

	String nextSystemMove;

	volatile boolean calculatingSystemMove;

	private OpendialDialog() {
		system = new DialogueSystem();
		Settings settings = system.getSettings();

		system.getSettings().fillSettings(System.getProperties());

		settings.showGUI = false;

		if (domainFile != null) {
			Domain domain;
			try {
				domain = XMLDomainReader.extractDomain(domainFile);
				System.out.println("Domain from " + domainFile + " successfully extracted");
			} catch (RuntimeException e) {
				system.displayComment("Cannot load domain: " + e);
				e.printStackTrace();
				domain = XMLDomainReader.extractEmptyDomain(domainFile);
			}
			system.changeDomain(domain);
		}
		system.changeSettings(settings);

		system.attachModule(this);

		system.startSystem();
		System.out.println("Opendial system started!");

	}

	/**
	 * Get instance
	 * 
	 * @return
	 */
	public static OpendialDialog getInstance() {

		if (dialog == null) {
			dialog = new OpendialDialog();
		}

		return dialog;
	}

	/**
	 * Reset dialog state in opendial
	 */
	public static void resetInstance() {

		dialog = new OpendialDialog();
	}

	/**
	 * Update the value of a variable
	 * 
	 * @param variable
	 * @param value
	 */
	public void updateVariable(String variable, String value) {

		System.out.println("update " + variable + ": " + value);
		system.addContent(variable, value);
	}

	/**
	 * Wait for next system move
	 * 
	 * @return
	 */
	public String getNextSystemMove(long timeout) {

		while (calculatingSystemMove) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Next system move: " + nextSystemMove);
		return nextSystemMove;
	}

	@Override
	public void start() {
		// not necessary since the module was started ourself
	}

	@Override
	public void trigger(DialogueState state, Collection<String> updatedVars) {

		// look only for systemMove
		String var = "a_m";
		nextSystemMove = "";
		if (updatedVars.contains(var) && state.hasChanceNode(var)) {
			nextSystemMove = getTextRendering(system.getContent(var).toDiscrete());
			calculatingSystemMove = false;
		}

	}

	@Override
	public void pause(boolean toPause) {
		// not necessary
	}

	@Override
	public boolean isRunning() {
		// not necessary since module is always running
		return true;
	}

	/**
	 * Generates the text representation for the categorical table.
	 * 
	 * @param table
	 *            the table
	 * @return the text rendering of the table
	 */
	private String getTextRendering(CategoricalTable table) {

		String textTable = "";

		for (Value value : table.getValues()) {
			if (!(value instanceof NoneVal)) {
				String content = value.toString();
				if (table.getProb(value) < 0.98) {
					content += " (" + StringUtils.getShortForm(table.getProb(value)) + ")";
				}
				textTable += content + "\n\t\t";
			}
		}
		textTable = textTable.substring(0, textTable.length() - 3);

		return textTable;
	}

	/**
	 * Performs Wizard-of-Oz training with specified pre-scripted file
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean wizardTraining(String filePath) {

		try {
			DialogueImporter importer = system.importDialogue(filePath);
			importer.setWizardOfOzMode(true);
			return true;
		} catch (RuntimeException e) {
			system.displayComment("Cannot train file: " + e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Exports Domain Parameters to specified path
	 * 
	 * @param filePath
	 * @return
	 */
	public boolean parameterExport(String filePath) {

		try {
			XMLUtils.exportContent(system, filePath, "parameters");
			return true;
		} catch (RuntimeException e) {
			system.displayComment("Cannot export file: " + e);
			e.printStackTrace();

		}
		return false;
	}

	/**
	 * Detaches Module to terminate the Wizard-of-Oz learning mode
	 * 
	 * @return
	 */
	public boolean terminateWizard() {

		try {
			system.detachModule(WizardLearner.class);
			return true;
		} catch (RuntimeException e) {
			system.displayComment("Cannot terminate the wizard: " + e);
			e.printStackTrace();

		}
		return false;
	}

}
