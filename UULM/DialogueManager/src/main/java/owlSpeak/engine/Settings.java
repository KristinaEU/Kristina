package owlSpeak.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import owlSpeak.engine.his.Partition.HISvariant;
import owlSpeak.engine.policy.Policy.PolicyVariant;
import owlSpeak.engine.systemState.SystemState.SystemStateVariant;

/**
 * owlSpeak Class Settings is used for reading / writing the settings of the
 * Controller
 * 
 * @author Dan Denich
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 1.0
 */
public class Settings {

	/***
	 * Class Ontology stores a name, a description and a file for reading and
	 * writing ontologies to disk
	 * 
	 */
	public class Ontology {
		/**
		 * the name of the Ontology
		 */
		public String name;
		/**
		 * the descripton of the Ontology
		 */
		public String description;
		/**
		 * a String that contains the path of the folder that contains the
		 * Ontology
		 */
		public String path;
		/**
		 * a String that contains the name of the Ontology's file
		 */
		public String file;

		/**
		 * If ontology is of docType HIS_VXML_DOC, this determines if variant 0
		 * (Semantics) or 1 (Variables) is used.
		 */
		public HISvariant ontoHISvariant;

		/**
		 * Determines the policy wich should be applied.
		 */
		public PolicyVariant ontoPolicyVariant;

		/**
		 * Determines which system state variant is used.
		 */
		public SystemStateVariant ontoStateVariant;

		public Ontology() {
		}

		public Ontology(String name, String description, String file) {
			this.name = name.toLowerCase();
			this.description = description;
			this.file = file;
		}
	}

	/**
	 * Class UserOntoSetting stores an ontoname and ontostatus for individual
	 * users
	 * 
	 */
	public class UserOntoSetting {
		/**
		 * the name of the Ontology, for which the status should be
		 */
		public String ontoname;
		/**
		 * the status of the Ontology (enabled / disabled)
		 */
		public String ontostatus;

		public UserOntoSetting() {
		}

		public UserOntoSetting(String ontoname, String ontostatus) {
			this.ontoname = ontoname.toLowerCase();
			this.ontostatus = ontostatus;
		}
	}

	/***
	 * Class Usersetting stores settings for the individual user
	 * 
	 */
	public class Usersetting {
		public static final int VXML_DOC = 1;
		public static final int HTML_DOC = 2;
		public static final int SALT_SOC = 4;
		public static final int ENHANCED_VXM_DOC = 5;
		// public static final int HIS_VXML_DOC = 6;
		public static final int POMDP_VXML_DOC = 7;
		public static final int KRISTINA_DOC = 8;
		public static final int USER_SIM_DOC = 10;

		/***
		 * The Username
		 */
		public String name;
		/***
		 * The Outputdocument type
		 */
		public int docType;
		/***
		 * The output grammar type
		 */
		public int grammarType;
		/***
		 * The Autosave flag, if set to 1 the settings will be written to disk
		 * when users change them
		 */
		public int autosave;
		/***
		 * The Sleep flag for the user, if set to 1 the dialogue will be paused
		 */
		public int sleep;
		/***
		 * An array of UserOntoSettings
		 * 
		 * @see UserOntoSetting
		 */
		public UserOntoSetting[] userOntoSettings;

		public Usersetting() {
		}

		public Usersetting(String name, int docType, int grammarType,
				int autosave, int size) {
			this.name = name;
			this.docType = docType;
			this.grammarType = grammarType;
			this.autosave = autosave;
			this.userOntoSettings = new UserOntoSetting[size];
		}
	}

	/***
	 * The path to the SettingsFile
	 */
	public static final String settingsFile = System
			.getProperty("owlSpeak.settings.file");
	/***
	 * The sip Adress (sip:) or the telefone number (tel:) of the dialogue
	 * application
	 */
	public static String platform;
	/***
	 * The address of the base-ontology as defined in the SettingsFile
	 */
	public static String uri;
	/***
	 * The Home folder's location (e.g., "C:\OwlSpeak\").
	 */
	public static String homePath;
	/***
	 * the binary-code of the plugin, which should be selected
	 * <ul>
	 * <li>000 -&gt; all Plugins deactivated</li>
	 * <li>001 -&gt; Keywordspotting</li>
	 * <li>010 -&gt; Levensthein</li>
	 * <li>100 -&gt; GermaNet</li>
	 * </ul>
	 */
	public static int plugin;
	/***
	 * A String specifying the tts output voice to be used
	 */
	public static String ttsMode;
	/***
	 * A String specifying the asr input language to be used
	 */
	public static String asrMode;
	/***
	 * A int specifying the id of the tts output voice to be used
	 */
	public static int ttsModeID;
	/***
	 * A int specifying the id of the asr input language to be used
	 */
	public static int asrModeID;
	/***
	 * A boolean specifying if dialogue parameters should be written to database
	 */
	public static boolean dbLogging;
	/***
	 * A boolean specifying if the calls should be recorded.
	 */
	public static boolean callRecording;
	/***
	 * A boolean specifying if interaction quality is being derived during the
	 * interaction.
	 */
	public static boolean deriveQuality;
	/***
	 * A boolean specifying if data should be collected for optimizing the
	 * policy.
	 */
	public static boolean collectOptimizationData = false;
	/***
	 * A boolean specifying if KRISTINA-DM should be applied.
	 */
	public static boolean kristina;
	/***
	 * A boolean specifying if IDACO-DM should be applied.
	 */
	public static boolean idaco;
	/***
	 * A boolean specifying if a Demo website showing the internal workings of the DM should be available. Only applicable if kristina is true.
	 */
	public static boolean demo;
	/***
	 * An integer specifying the max number (n) of results provided by the ASR
	 * n-best-list.
	 */
	public static int nbNBestListEntries = 1;
	/***
	 * An array of strings that specifies the System-Commands (must be present
	 * as Respawns in the System-Ontology to work
	 */
	public static String[] sysCommands;
	/***
	 * An array of strings that specifies the usernames
	 */
	public static String[] usernames;
	/***
	 * An array of Ontologys that are stored on the harddisk
	 * 
	 * @see Ontology
	 */
	public static Ontology[] ontologies;
	/***
	 * An array of Usersettings
	 * 
	 * @see Usersetting
	 */
	public static Usersetting[] usersetting;
	/**
	 * the deactivation switch for the UPNP system (0=off,1=on)
	 */
	public static int youpi;
	/**
	 * the path to the device description of the Youpi UPnP wrapper.
	 */
	public static String youpiPath;
	/**
	 * the long ID of the Youpi UPnP device.
	 */
	public static String youpiID;
	/**
	 * the friendly name of the Youpi UPnP device.
	 */
	public static String youpiFriendlyName;
	/**
	 * used to set the Controller to different Agenda-Priority calculation
	 * modes. 0=static, 1=dynamic (Priority will increase over time) dynamic:
	 * prio = prio + ( (actualtime-AgendaAddTime) * prio /
	 * Settings.matchagendapriofactor);
	 */
	public static int matchagenda;
	/**
	 * factor used for the dynamic Agenda-Priority calculation
	 */
	public static int matchagendapriofactor;
	/**
	 * Duration of the loop used between two requests
	 */
	public static int loopDuration;
	/**
	 * The max speech timeout
	 */
	public static int maxspeechtimeout;
	/**
	 * The speaking Rate
	 */
	public static String speakRate;
	/**
	 * the Settings document
	 */
	private Document doc;

	/**
	 * the Constructor calls the reload method
	 */
	public Settings() {
		reload();
	}

	/***
	 * Gets the number of the user, for accessing his settings
	 * 
	 * @param username
	 *            the username that should be matched
	 * @return the position of the username in the username array
	 */
	public static int getuserpos(String username) {
		for (int i = 0; i < usernames.length; i++) {
			if (usernames[i].equals(username))
				return i;
		}
		return -1;
	}

	/***
	 * Gets the number of the ontology
	 * 
	 * @param ontoname
	 *            the ontoname that should be matched
	 * @return the position of the ontoname in the ontologies array
	 */
	public int getontopos(String ontoname) {
		for (int i = 0; i < ontologies.length; i++) {
			if (ontologies[i].name.equals(ontoname.toLowerCase()))
				return i;
		}
		return -1;
	}

	/***
	 * Gets the Usersetting of the username
	 * 
	 * @param username
	 *            the username that should be matched
	 * @return the Usersetting of the speciefied user
	 */
	public Usersetting getusersetting(String username) {
		for (int i = 0; i < usersetting.length; i++) {
			if (usersetting[i].name.equals(username))
				return usersetting[i];
		}
		return null;
	}

	/***
	 * Reloads the Settings object from the Settings file on the harddisk
	 */
	public void reload() {
		try {
			File sourceFile = new File(settingsFile);
			FileInputStream in = new FileInputStream(sourceFile);
			SAXBuilder builder = new SAXBuilder();
			doc = builder.build(in);
			in.close();
			Element owlSetting = doc.getRootElement();
			Element appEle = owlSetting.getChild("platform");
			platform = appEle.getText();
			Element uriEle = owlSetting.getChild("URI");
			uri = uriEle.getText();
			Element homeEle = owlSetting.getChild("HomePath");
			homePath = homeEle.getText();
			Element plugEle = owlSetting.getChild("plugin");
			plugin = Integer.parseInt(plugEle.getText());
			Element youpiEle = owlSetting.getChild("Youpi");
			youpi = Integer.parseInt(youpiEle.getText());
			Element youpiPathEle = owlSetting.getChild("YoupiPath");
			youpiPath = youpiPathEle.getText();
			Element youpiIDEle = owlSetting.getChild("YoupiID");
			youpiID = youpiIDEle.getText();
			Element youpiFNEle = owlSetting.getChild("YoupiFriendlyName");
			youpiFriendlyName = youpiFNEle.getText();
			Element matchEle = owlSetting.getChild("MatchAgenda");
			matchagenda = Integer.parseInt(matchEle.getText());
			Element prioEle = owlSetting.getChild("PrioFactor");
			matchagendapriofactor = Integer.parseInt(prioEle.getText());
			Element loopEle = owlSetting.getChild("LoopDuration");
			loopDuration = Integer.parseInt(loopEle.getText());
			Element maxEle = owlSetting.getChild("MaxSpeechTimeout");
			maxspeechtimeout = Integer.parseInt(maxEle.getText());
			Element speakRateEle = owlSetting.getChild("speakRate");
			speakRate = speakRateEle.getText();
			Element ttsCode = owlSetting.getChild("ttsCodes");
			Object ttsCodes[] = ttsCode.getChildren().toArray();
			HashMap<String, String> ttsCodeChild = new HashMap<String, String>();
			for (int i = 0; i < ttsCodes.length; i++) {
				ttsCodeChild.put(((Element) ttsCodes[i]).getAttribute("id")
						.getValue(), ((Element) ttsCodes[i]).getText());
			}
			ttsMode = ttsCodeChild.get((owlSetting.getChild("ttsMode"))
					.getText());
			ttsModeID = Integer.parseInt(owlSetting.getChild("ttsMode")
					.getText());
			Element asrCode = owlSetting.getChild("asrCodes");
			Object asrCodes[] = asrCode.getChildren().toArray();
			HashMap<String, String> asrCodeChild = new HashMap<String, String>();
			for (int i = 0; i < asrCodes.length; i++) {
				asrCodeChild.put(((Element) asrCodes[i]).getAttribute("id")
						.getValue(), ((Element) asrCodes[i]).getText());
			}
			asrMode = asrCodeChild.get((owlSetting.getChild("asrMode"))
					.getText());
			asrModeID = Integer.parseInt(owlSetting.getChild("asrMode")
					.getText());
			int tmpDeriveQuality = Integer.parseInt(owlSetting.getChild(
					"deriveQuality").getText());
			deriveQuality = (tmpDeriveQuality == 0) ? false : true;
			int tmpDbLogging = Integer.parseInt(owlSetting
					.getChild("dbLogging").getText());
			dbLogging = (tmpDbLogging == 0) ? false : true;
			int tmpCallRecording = Integer.parseInt(owlSetting.getChild(
					"callRecording").getText());
			callRecording = (tmpCallRecording == 0) ? false : true;
			int tmpKristina = Integer.parseInt(owlSetting.getChild(
					"kristina").getText());
			kristina = (tmpKristina == 0) ? false : true;
			int tmpDemo = Integer.parseInt(owlSetting.getChild(
					"demo").getText());
			demo = (tmpDemo == 0 || !kristina) ? false : true;
			int tmpIdaco = Integer.parseInt(owlSetting.getChild(
					"idaco").getText());
			idaco = (tmpIdaco == 0) ? false : true;
			try {
				nbNBestListEntries = Integer.parseInt(owlSetting.getChild(
						"maxNBestListEntries").getText());
			} catch (NumberFormatException e) {
				nbNBestListEntries = 1;
			}
			Element SysCom = owlSetting.getChild("SysCommands");
			Object sys[] = SysCom.getChildren().toArray();
			List<Element> sysChild = new java.util.ArrayList<Element>();
			for (int i = 0; i < sys.length; i++) {
				sysChild.add((Element) sys[i]);
			}
			Element Files = owlSetting.getChild("Files");
			Object file[] = Files.getChildren().toArray();
			List<Element> filesChild = new java.util.ArrayList<Element>();
			for (int i = 0; i < file.length; i++) {
				filesChild.add((Element) file[i]);
			}
			ontologies = new Ontology[filesChild.size()];
			for (int i = 0; i < filesChild.size(); i++) {
				ontologies[i] = new Ontology();
				ontologies[i].path = filesChild.get(i)
						.getAttributeValue("path");
				ontologies[i].file = filesChild.get(i)
						.getAttributeValue("file");
				ontologies[i].name = filesChild.get(i)
						.getAttributeValue("name").toLowerCase();
				ontologies[i].description = filesChild.get(i).getText();
				ontologies[i].ontoHISvariant = Integer.parseInt(filesChild.get(
						i).getAttributeValue("HISvariant")) == 1 ? HISvariant.VAR
						: HISvariant.SEM;
				ontologies[i].ontoPolicyVariant = PolicyVariant.values()[Integer
						.parseInt(filesChild.get(i).getAttributeValue(
								"policyVariant"))];
				ontologies[i].ontoStateVariant = Integer.parseInt(filesChild
						.get(i).getAttributeValue("systemStateVariant")) == 1 ? SystemStateVariant.DISTRIBUTION
						: SystemStateVariant.SINGLE;
			}

			Element Users = owlSetting.getChild("Users");
			Object user[] = Users.getChildren().toArray();
			List<Element> UsersChild = new java.util.ArrayList<Element>();
			for (int i = 0; i < user.length; i++) {
				UsersChild.add((Element) user[i]);
			}
			usernames = new String[UsersChild.size()];
			usersetting = new Usersetting[UsersChild.size()];

			for (int i = 0; i < usernames.length; i++) {
				usernames[i] = UsersChild.get(i).getAttributeValue("name");
				usersetting[i] = new Usersetting();
				usersetting[i].name = usernames[i];
				usersetting[i].docType = Integer.valueOf(UsersChild.get(i)
						.getAttributeValue("docType"));
				usersetting[i].grammarType = Integer.valueOf(UsersChild.get(i)
						.getAttributeValue("grammarType"));
				usersetting[i].sleep = Integer.valueOf(UsersChild.get(i)
						.getAttributeValue("sleep"));
				usersetting[i].autosave = Integer.valueOf(UsersChild.get(i)
						.getAttributeValue("AutoSave"));
				usersetting[i].userOntoSettings = new UserOntoSetting[ontologies.length];
				Object onto[] = UsersChild.get(i).getChildren("Useronto")
						.toArray();
				List<Element> UsersOntos = new java.util.ArrayList<Element>();
				for (int j = 0; j < onto.length; j++) {
					UsersOntos.add((Element) onto[j]);
				}
				for (int h = 0; h < ontologies.length; h++) {
					usersetting[i].userOntoSettings[h] = new UserOntoSetting();
					usersetting[i].userOntoSettings[h].ontoname = ontologies[h].name
							.toLowerCase();
					usersetting[i].userOntoSettings[h].ontostatus = "disabled";
				}
				for (int h = 0; h < UsersOntos.size(); h++) {
					int temp = getontopos(UsersOntos.get(h).getAttributeValue(
							"name"));
					if (temp == -1)
						continue;
					usersetting[i].userOntoSettings[temp].ontoname = UsersOntos
							.get(h).getAttributeValue("name").toLowerCase();
					usersetting[i].userOntoSettings[temp].ontostatus = UsersOntos
							.get(h).getAttributeValue("status");
				}
			}
			sysCommands = new String[sysChild.size()];
			for (int i = 0; i < sysChild.size(); i++) {
				sysCommands[i] = sysChild.get(i).getAttributeValue("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***
	 * Writes the actual Settings from the Settings object to the Settings file
	 * on hdd TODO add newly created settings elements
	 */
	public void write() {
		SAXBuilder builder = new SAXBuilder();
		try {
			File sourceFile = new File(settingsFile);
			FileInputStream in = new FileInputStream(sourceFile);
			doc = builder.build(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element owlSetting = doc.getRootElement();
		Element ttsModeEle = owlSetting.getChild("ttsMode");
		ttsModeEle.setText(Integer.toString(ttsModeID));
		Element asrModeEle = owlSetting.getChild("asrMode");
		asrModeEle.setText(Integer.toString(asrModeID));
		Object[] array1 = new Object[owlSetting.getChild("Users")
				.getChildren("User").size()];
		array1 = owlSetting.getChild("Users").getChildren("User").toArray();
		Vector<Element> user = new Vector<Element>();
		for (int i = 0; i < array1.length; i++) {
			user.add((Element) array1[i]);
		}
		for (int i = 0; i < usersetting.length; i++) {
			user.get(i).setAttribute("name", usersetting[i].name);
			user.get(i).setAttribute("docType",
					Integer.toString(usersetting[i].docType));
			user.get(i).setAttribute("grammarType",
					Integer.toString(usersetting[i].grammarType));
			user.get(i).setAttribute("sleep",
					Integer.toString(usersetting[i].sleep));
			user.get(i).setAttribute("AutoSave",
					Integer.toString(usersetting[i].autosave));
			Object[] array2 = new Object[user.get(i).getChildren("Useronto")
					.size()];
			array2 = user.get(i).getChildren("Useronto").toArray();
			Vector<Element> userontos = new Vector<Element>();
			for (int k = 0; k < array2.length; k++) {
				userontos.add((Element) array2[k]);
			}
			for (int j = 0; j < usersetting[i].userOntoSettings.length; j++) {
				if (usersetting[i].userOntoSettings[j].ontoname == null)
					continue;
				userontos.get(j).setAttribute(
						"name",
						usersetting[i].userOntoSettings[j].ontoname
								.toLowerCase());
				userontos.get(j).setAttribute("status",
						usersetting[i].userOntoSettings[j].ontostatus);
			}
		}
		XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(Format.getPrettyFormat());
		try {
			File file = new File(settingsFile);
			java.io.FileWriter writer = new java.io.FileWriter(file);
			outputter.output(doc, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
