package owlSpeak.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.util.OWLEntityRenamer;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

import owlSpeak.Agenda;
import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.DialogueDomain;
import owlSpeak.History;
import owlSpeak.OSFactory;
import owlSpeak.Semantic;
import owlSpeak.WorkSpace;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.Partition.HISvariant;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.interfaces.IPartitionDistribution;
import owlSpeak.engine.his.util.PartitionSeedEncapsulation;
import owlSpeak.engine.policy.Policy.PolicyVariant;
import owlSpeak.engine.systemState.SystemState.SystemStateVariant;

/**
 * encapsulates all OwlSpeakOntology access
 * 
 * @author Dan Denich
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 */
public class OwlSpeakOntology implements Comparable<OwlSpeakOntology> {
	/**
	 * the name of the Ontology
	 */
	public String Name;
	/**
	 * the path of the ontology
	 */
	private String Path;
	/**
	 * the filename of the ontology
	 */
	private String Filename;
	/**
	 * the OSFactory used for the ontology. Will be set during read.
	 */
	public OSFactory factory;
	/**
	 * an array of booleans in the same order as the username array in the
	 * Settings. Stores the status of the of the ontology and the user
	 * (enabled=true).
	 */
	public boolean[] isenabled;
	/**
	 * Stores the variant which is used for the HIS implementation.
	 */
	public HISvariant ontoHISvariant;
	/**
	 * Stores the policy variant.
	 */
	public PolicyVariant ontoPolicyVariant;
	/**
	 * Stores the system state variant.
	 */
	public SystemStateVariant ontoStateVariant;
	/**
	 * an array of WorkSpaces in the same order as the username array in the
	 * Settings. Stores the WorkSpaces of the ontology and the user.
	 */
	public WorkSpace[] workSpace;
	/**
	 * an array of BeliefSpaces in the same order as the username array in the
	 * Settings. Stores the BeliefSpaces of the ontology and the user.
	 */
	public BeliefSpace[] beliefSpace;
	/**
	 * an array of IPartitionDistribution in the same order as the username
	 * array in the Settings. Stores the PartitionDistribution of the ontology
	 * and the user.
	 */
	public IPartitionDistribution[] partitionDistributions;
	/**
	 * a vector of SummaryBelief objects shared by all users. Stores the
	 * SummarySpaceBeliefPoints of the ontology.
	 */
	public Vector<SummaryBelief> summarySpaceBeliefPoints;
	/**
	 * an array of Agendas in the same order as the username array in the
	 * Settings. Stores the actualAgenda of the ontology and the user.
	 */
	public Agenda[] actualAgenda;
	/**
	 * an iterator of all WorkSpaces in the ontology
	 */
	private Iterator<WorkSpace> wSIt;

	private static Map<String, OwlSpeakOntology> ontologySingletons;

	public static OwlSpeakOntology createOntology(String Name, String Path,
			String Filename, HISvariant HISvariant,
			PolicyVariant policyVaraint, SystemStateVariant stateVariant) {
		OwlSpeakOntology onto = null;
		if (ontologySingletons == null) {
			ontologySingletons = new HashMap<String, OwlSpeakOntology>();
			onto = new OwlSpeakOntology(Name, Path, Filename, HISvariant,
					policyVaraint, stateVariant);
			ontologySingletons.put(Name, onto);
		} else {
			if (ontologySingletons.containsKey(Name))
				onto = ontologySingletons.get(Name);
			else {
				onto = new OwlSpeakOntology(Name, Path, Filename, HISvariant,
						policyVaraint, stateVariant);
				ontologySingletons.put(Name, onto);
			}
		}
		if (onto != null) {
			onto.update();
			System.out.println(onto.Name + " is up to date!");
			onto.enableall();
		}
		return onto;
	}

	// /**
	// * an iterator of all BeliefSpaces in the ontology
	// */
	// private Iterator<BeliefSpace> bSIt;

	OwlSpeakOntology(String Name, String Path, String Filename,
			HISvariant HISvariant, PolicyVariant policyVaraint,
			SystemStateVariant stateVariant) {
		this.Name = Name;
		this.Path = Path;
		this.Filename = Filename;
		this.workSpace = new WorkSpace[Settings.usernames.length];
		this.beliefSpace = new BeliefSpace[Settings.usernames.length];
		this.partitionDistributions = new IPartitionDistribution[Settings.usernames.length];
		this.isenabled = new boolean[Settings.usernames.length];
		this.actualAgenda = new Agenda[Settings.usernames.length];
		this.ontoHISvariant = HISvariant;
		this.ontoPolicyVariant = policyVaraint;
		this.ontoStateVariant = stateVariant;

		// TODO load summaryBeliefs from ontology
		this.summarySpaceBeliefPoints = new Vector<SummaryBelief>();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o instanceof OwlSpeakOntology) {
			return ((OwlSpeakOntology) o).Name.equalsIgnoreCase(this.Name);
		}
		return false;
	}

	public void close() {
	}

	/**
	 * updates the ontology, that means the ontology is read from the file
	 */
	public synchronized void update() {
		setwSIt(null);
		// setbSIt(null);
		try {
			factory = loadOSFactory(this.Filename, this.Path);
			setwSIt(factory.getAllWorkSpaceInstances().iterator());
			// setbSIt(factory.getAllBeliefSpaceInstances().iterator());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
		}

		// Sicherheitsabfragen !!
		for (int i = 0; i < Settings.usernames.length; i++) {
			String user = Settings.usernames[i];
			workSpace[i] = factory.getWorkSpace(user + "Workspace");
			if (ontoStateVariant == SystemStateVariant.DISTRIBUTION) {
				// if (Settings.usersetting[i].docType ==
				// Usersetting.HIS_VXML_DOC) {
				Partition root = factory.getPartitionTree(user, "Beliefspace",
						this);
				PartitionSeedEncapsulation seed = new PartitionSeedEncapsulation(
						root);
				partitionDistributions[i] = factory
						.getPartitionDistributionFactory().create(seed, null,
								true);
				// System.out.println(partitionDistributions[i].toString());
				beliefSpace[i] = (Partition) partitionDistributions[i]
						.getTopPartition();

			} else {
				beliefSpace[i] = factory.getBeliefSpace(user + "Beliefspace");
			}
		}

		// load SummaryBeliefPoints from ontology
		summarySpaceBeliefPoints.removeAllElements();
		summarySpaceBeliefPoints.addAll(factory.getAllSummaryBeliefInstances());
	}

	/**
	 * returns the name of the domain (i.e., the OWLAnnotationProperty
	 * domainName of the class DialogueDomain).
	 * 
	 * @return Dialogue domain name.
	 */
	public String getOntoDomainName() {
		String result = "";
		Iterator<DialogueDomain> DDiter = factory
				.getAllDialogueDomainInstances().iterator();
		while (DDiter.hasNext()) {
			Iterator<OWLLiteral> DdLitIter = DDiter.next().getDomainName()
					.iterator();
			while (DdLitIter.hasNext()) {
				result = result + " " + DdLitIter.next().getLiteral();
			}
		}
		result.trim();
		return result;
	}

	/**
	 * resets the ontology for all users: 1. perform update (=read from file) 2.
	 * delete all WorkSpaces and and Histories 3. delete all Beliefs that are
	 * referenced in all BeliefSpaces and all BeliefSpaces 4. delete all Beliefs
	 * which are not referenced in any BeliefSpace 5. create new WorkSpaces and
	 * BeliefSpaces for each user and initialize the WorkSpaces with the
	 * masteragenda 6. write the ontology to the file
	 */
	public void resetall() {
		Iterator<Agenda> agendaIt;
		update();
		agendaIt = factory.getAllAgendaInstances().iterator();
		Iterator<WorkSpace> WSIt = factory.getAllWorkSpaceInstances()
				.iterator();
		Iterator<BeliefSpace> BSIt = factory.getAllBeliefSpaceInstances()
				.iterator();
		Iterator<Belief> BIt = factory.getAllBeliefInstances().iterator();

		Agenda master = null;
		while (agendaIt.hasNext()) {
			master = agendaIt.next();
			try {
				if (master.getIsMasterBool()) {
					break;
				}
			} catch (NullPointerException ex) {
			}
			;
		}

		WorkSpace WS = null;
		BeliefSpace BS = null;
		Belief B = null;
		Iterator<Belief> Beliefs;
		Iterator<History> Histories = factory.getAllHistoryInstances()
				.iterator();
		History HS;

		while (Histories.hasNext()) {
			HS = Histories.next();
			HS.delete();
		}

		while (WSIt.hasNext()) {
			WS = WSIt.next();
			WS.delete();
		}
		while (BSIt.hasNext()) {
			BS = BSIt.next();
			if (BS.hasHasBelief()) {
				Beliefs = BS.getHasBelief().iterator();
				while (Beliefs.hasNext()) {
					Belief temp = Beliefs.next();
					temp.delete();
				}
			}
			BS.delete();
		}
		while (BIt.hasNext()) {
			B = BIt.next();
			B.delete();
		}
		for (int i = 0; i < Settings.usernames.length; i++) {
			WorkSpace neu = factory.createWorkSpace(Settings.usernames[i]
					+ "Workspace");
			Iterator<Agenda> masterAgenda = master.getNext().iterator();
			while (masterAgenda.hasNext()) {
				Agenda temp = masterAgenda.next();
				neu.addNext(temp);
				AgendaPrio.addAgendaPrio(temp, neu, factory, 0);
			}
			if (ontoStateVariant == SystemStateVariant.DISTRIBUTION) {
				// if (Settings.usersetting[i].docType ==
				// Usersetting.HIS_VXML_DOC) {
				factory.createPartition(Settings.usernames[i] + "Beliefspace",
						this);
			} else
				factory.createBeliefSpace(Settings.usernames[i] + "Beliefspace");
		}

		write();
		update();
	}

	/**
	 * resets the ontology for one user: 1. perform update (=read from file) 2.
	 * delete all Beliefs that are referenced in the user's BeliefSpaces 3.
	 * delete the WorkSpace and BeliefSpace and Histories of the user 4. create
	 * new WorkSpace and BeliefSpace for the given user and initialize the
	 * WorkSpace with the masteragenda 5. write the ontology to the file
	 * 
	 * @param user
	 *            the user's name
	 */

	public void reset(String user) {
		Iterator<Agenda> agendaIt;
		update();
		agendaIt = factory.getAllAgendaInstances().iterator();
		setwSIt(factory.getAllWorkSpaceInstances().iterator());
		// setbSIt(factory.getAllBeliefSpaceInstances().iterator());

		owlSpeak.Agenda master = null;
		while (agendaIt.hasNext()) {
			master = agendaIt.next();
			try {
				if (master.getIsMasterBool()) {
					break;
				}
			} catch (NullPointerException ex) {
			}
			;
		}

		BeliefSpace BS = null;
		Iterator<Belief> Beliefs;
		BS = factory.getBeliefSpace(Settings.usernames[Settings
				.getuserpos(user)] + "Beliefspace");
		if (BS.hasHasBelief()) {
			Beliefs = BS.getHasBelief().iterator();
			while (Beliefs.hasNext()) {
				Beliefs.next().delete();
			}
		}
		BS.delete();
		Iterator<History> histIt = factory.getAllHistoryInstances().iterator();
		History tempHist;

		while (histIt.hasNext()) {
			tempHist = histIt.next();
			if (tempHist
					.getInWorkspace()
					.getLocalName()
					.equals(Settings.usernames[Settings.getuserpos(user)]
							+ "Workspace")) {
				tempHist.delete();
			}
		}

		try {
			WorkSpace toDelete = factory
					.getWorkSpace(Settings.usernames[Settings.getuserpos(user)]
							+ "Workspace");
			toDelete.delete();
		} catch (NullPointerException n) {
		}

		WorkSpace neu = factory.createWorkSpace(Settings.usernames[Settings
				.getuserpos(user)] + "Workspace");
		factory.createBeliefSpace(Settings.usernames[Settings.getuserpos(user)]
				+ "Beliefspace");
		Iterator<Agenda> masterAgenda = master.getNext().iterator();

		while (masterAgenda.hasNext()) {
			Agenda temp = masterAgenda.next();
			neu.addNext(temp);
			AgendaPrio.addAgendaPrio(temp, neu, factory, 0);
		}
		write();
	}

	/**
	 * write the ontology to the file
	 */
	public synchronized void write() {
		write(factory.manager, factory.onto);
	}

	/**
	 * write the ontology to the file
	 */
	public synchronized static void write(OWLOntologyManager m, OWLOntology o) {
		try {
			m.saveOntology(o);
		} catch (OWLOntologyStorageException e) {
			e.printStackTrace();
		}
	}

	/**
	 * enable the ontology for all users
	 */
	public void enableall() {
		for (int i = 0; i < Settings.usernames.length; i++)
			this.isenabled[i] = true;
	}

	/**
	 * enable the ontology for the given user number
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 */
	public void enable(int userpos) {
		this.isenabled[userpos] = true;
	}

	/**
	 * disable the ontology for all users
	 */
	public void disableall() {
		for (int i = 0; i < Settings.usernames.length; i++)
			this.isenabled[i] = false;
	}

	/**
	 * disable the ontology for the given user number
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 */
	public void disable(int userpos) {
		this.isenabled[userpos] = false;
	}

	/**
	 * checks if the ontology is enabled for the given usernumber
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 * @return true if the ontology is enabled
	 */
	public boolean isenabled(int userpos) {
		return this.isenabled[userpos];
	}

	/**
	 * sets the actualAgenda for the given usernumber
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 * @param setAgenda
	 *            the Agenda that should be set as actualAgenda
	 */
	public void setActualAgenda(int userpos, Agenda setAgenda) {
		actualAgenda[userpos] = setAgenda;
	}

	// /**
	// * Adds anew summary space belief point at the end of the Vector
	// *
	// * @param newPoint
	// *
	// *
	// */
	// public void addNewSummarySpaceBeliefPoint(SummaryBelief newPoint) {
	// summarySpaceBeliefPoints.addElement(newPoint);
	// }

	/**
	 * gets the actualAgenda for the given usernumber
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 * @return the actualAgenda of the user
	 */
	public Agenda getActualAgenda(int userpos) {
		Agenda Ausgabe = null;
		try {
			Ausgabe = actualAgenda[userpos];
		} catch (NullPointerException e) {
		}
		return Ausgabe;
	}

	/**
	 * returns the BeliefSpace of the given usernumber
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 * @return the BeliefSpace of the user
	 */
	public BeliefSpace getBeliefSpace(int userpos) {
		return beliefSpace[userpos];
	}

	/**
	 * builds a vector with all semantics that are active in the Beliefs of the
	 * user's BeliefSpace
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 * @return the vector that contains the semantics
	 */
	public Vector<Semantic> getBSSemantics(int userpos) {
		Vector<Semantic> semantics = new Vector<Semantic>();
		Iterator<Belief> beliefsIt = beliefSpace[userpos].getHasBelief()
				.iterator();
		while (beliefsIt.hasNext()) {
			semantics.addAll(beliefsIt.next().getSemantic());
		}
		return semantics;
	}

	/**
	 * returns the WorkSpace of the given usernumber
	 * 
	 * @param userpos
	 *            the position of the username in the Settings username-array
	 * @return the WorkSpace of the user
	 */
	public WorkSpace getWorks(int userpos) {
		return workSpace[userpos];
	}

	/**
	 * sets the Iterator of all WorkSpaces in the ontology
	 * 
	 * @param wSIt
	 *            the Iterator that should be used
	 */
	public void setwSIt(Iterator<WorkSpace> wSIt) {
		this.wSIt = wSIt;
	}

	/**
	 * gets an Iterator of all WorkSpaces in the ontology
	 * 
	 * @return the Iterator containing all WorkSpaces
	 */
	public Iterator<WorkSpace> getwSIt() {
		return wSIt;
	}

	/**
	 * Checks if this ontology has partition distribution objects or uses the
	 * regular beliefspace mechanism
	 * 
	 * @return true if there is at least one partition distribution, else false
	 */
	public boolean hasPartitionDistribution() {
		if (partitionDistributions != null && partitionDistributions.length > 0
				&& partitionDistributions[0] != null)
			return true;
		return false;
	}

	// /**
	// * sets the Iterator of all BeliefSpaces in the ontology
	// * @param bSIt the Iterator that should be used
	// */
	// public void setbSIt(Iterator<BeliefSpace> bSIt) {
	// this.bSIt = bSIt;
	// }

	// /**
	// * gets an Iterator of all BeliefSpaces in the ontology
	// * @return the Iterator containing all BeliefSpaces
	// */
	// public Iterator<BeliefSpace> getbSIt() {
	// return bSIt;
	// }

	/**
	 * loads the Ontology as a new OWLModel from File
	 * 
	 * @return the OWLModel of the Ontology
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 */
	public static OSFactory loadOSFactory(String filename, String localFolder)
			throws OWLOntologyCreationException, FileNotFoundException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		manager.addIRIMapper(new SimpleIRIMapper(IRI.create("http://localfiles/OwlSpeak/OwlSpeakOnto.owl"), IRI.create(new File("./conf/OwlSpeak/OwlSpeakOnto.owl"))));
		
		OWLOntology ontology = manager
				.loadOntologyFromOntologyDocument(new File(localFolder
						+ filename));
		OWLDataFactory factory = manager.getOWLDataFactory();
		return new OSFactory(ontology, factory, manager);
	}

	/**
	 * creates a new Ontology and loads it as a new OWLModel
	 * 
	 * @param filename
	 *            filename of the newly created ontology
	 * @param localFolder
	 *            folder in which the new ontology is created
	 * @param pathToOwlSpeakOntology
	 *            points to the path of the file OwlSpeakOnto.owl (without the
	 *            file name) which serves as parent for ontology to create
	 * @return the OWLModel of the Ontology
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 */
	public static OSFactory createOSFactory(String filename,
			String localFolder, String pathToOwlSpeakOntology)
			throws OWLOntologyCreationException, FileNotFoundException {
		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI baseIRI = IRI
				.create(new File(localFolder + pathToOwlSpeakOntology));
		OWLOntology baseOntology = manager
				.loadOntologyFromOntologyDocument(baseIRI);
		TreeSet<OWLOntology> imports = new TreeSet<OWLOntology>();
		imports.add(baseOntology);
		// OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		OWLOntology ontology = manager.createOntology(
				IRI.create(localFolder + filename), imports);
		OWLDataFactory factory = manager.getOWLDataFactory();
		return new OSFactory(ontology, factory, manager);
	}

	/**
	 * creats a new Ontology and loads it as a new OWLModel
	 * 
	 * @param filename
	 *            filename of the newly created ontology
	 * @param localFolder
	 *            folder in which the new ontology is created
	 * @return the OWLModel of the Ontology
	 * @throws FileNotFoundException
	 * @throws OWLOntologyCreationException
	 * @throws OWLOntologyStorageException
	 */
	public static OSFactory createOSFactoryEmptyOnto(String filename,
			String localFolder) throws OWLOntologyCreationException,
			FileNotFoundException, OWLOntologyStorageException {

		OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		IRI emptyIRI = IRI.create("http://localhost:8080/empty.owl");
		OWLOntology emptyOntology = manager
				.loadOntologyFromOntologyDocument(emptyIRI);

		File file = new File(localFolder + filename);

		// manager.setOntologyDocumentIRI(emptyOntology,
		// IRI.create(file.toURI()));
		Set<OWLOntology> set = new TreeSet<OWLOntology>();
		set.add(emptyOntology);
		OWLEntityRenamer renamer = new OWLEntityRenamer(manager, set);
		List<OWLOntologyChange> changeList = renamer.changeIRI(emptyIRI,
				IRI.create(file.toURI()));
		System.out.println("Change list");
		for (OWLOntologyChange item : changeList)
			System.out.println(item.toString());
		List<OWLOntologyChange> appliedList = manager.applyChanges(changeList);
		System.out.println("Applied changes list");
		for (OWLOntologyChange item : appliedList)
			System.out.println(item.toString());
		manager.saveOntology(emptyOntology, IRI.create(file.toURI()));

		manager.removeOntology(emptyOntology);

		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI
				.create(file.toURI()));
		OWLDataFactory factory = manager.getOWLDataFactory();
		return new OSFactory(ontology, factory, manager);
	}

	/**
	 * Using ontology name as unique identifier.
	 */
	@Override
	public int compareTo(OwlSpeakOntology o) {
		if (o == null)
			throw new NullPointerException();
		return Name.compareTo(o.Name);
	}
}
