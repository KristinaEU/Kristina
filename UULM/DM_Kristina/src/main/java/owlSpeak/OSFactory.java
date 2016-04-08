package owlSpeak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Partition;
import owlSpeak.engine.his.Partition.PartitionState;
import owlSpeak.engine.his.Reward;
import owlSpeak.engine.his.SummaryBelief;
import owlSpeak.engine.his.SummaryBeliefNonpersistent;
import owlSpeak.engine.his.factories.PartitionDistributionFactory;
import owlSpeak.engine.his.interfaces.IPartitionDistribution;
import owlSpeak.engine.his.interfaces.ISummaryBelief.HistoryState;

/**
 * This class is used to manipulate the owl individuals of all types.
 * 
 * @author Tobias Heinroth
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @author Karim
 */
public class OSFactory {
	/**
	 * the OWLModel for reading and writing.
	 */
	public OWLOntology onto;
	public OWLDataFactory factory;
	public OWLOntologyManager manager;

	private PartitionDistributionFactory partitionDistributionFactory;

	/**
	 * a constructor of the factory, will use the URI from the Settings class.
	 * 
	 * @param onto
	 *            the OWLOntology.
	 * @param factory
	 *            the OWLDataFactory used for readin/writing.
	 * @param manager
	 *            the OWLOntologyManager that manages the ontology.
	 */
	public OSFactory(OWLOntology onto, OWLDataFactory factory,
			OWLOntologyManager manager) {
		this.onto = onto;
		this.factory = factory;
		this.manager = manager;

		// as instantiating of PartitionDistributionFactory objects takes a lot
		// of time (up to 2 sec) we check if any user actually uses the HIS
		// implementation
		
//		for (Usersetting userSetting : Settings.usersetting) {
//			if (userSetting.docType == Usersetting.HIS_VXML_DOC) {
//				
//				break;
//			}
//		}

	}

	/**
	 * returns the OWLClass of the Class Grammar.
	 * 
	 * @return the Class of the Class Grammar.
	 */
	public OWLClass getGrammarClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Grammar"));
	}

	/**
	 * creates a Grammar object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Grammar object.
	 */
	public Grammar createGrammar(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getGrammarClass(), newIndi)));
		return new Grammar(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Grammar specified by its name.
	 * 
	 * @param name
	 *            the Name of the Grammar.
	 * @return the Grammar object.
	 */
	public Grammar getGrammar(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Grammar(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all Grammar objects.
	 * 
	 * @return the Collection of the Grammars.
	 */
	public Collection<Grammar> getAllGrammarInstances() {
		Iterator<OWLIndividual> coll = getGrammarClass().getIndividuals(onto)
				.iterator();
		Collection<Grammar> gramColl = new ArrayList<Grammar>();
		while (coll.hasNext()) {
			gramColl.add(new Grammar(coll.next(), onto, factory, manager));
		}
		return gramColl;
	}

	/**
	 * returns the OWLClass of the Class Belief.
	 * 
	 * @return the Class of the Class Belief.
	 */
	public OWLClass getBeliefClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Belief"));
	}

	/**
	 * creates a Belief object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Belief object.
	 */
	public Belief createBelief(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getBeliefClass(), newIndi)));
		return new Belief(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Belief specified by its name.
	 * 
	 * @param name
	 *            the Name of the Belief.
	 * @return the Belief object.
	 */
	public Belief getBelief(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Belief(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all Belief objects.
	 * 
	 * @return the Collection of the Beliefs.
	 */
	public Collection<Belief> getAllBeliefInstances() {
		Iterator<OWLIndividual> coll = getBeliefClass().getIndividuals(onto)
				.iterator();
		Collection<Belief> belColl = new ArrayList<Belief>();
		while (coll.hasNext()) {
			belColl.add(new Belief(coll.next(), onto, factory, manager));
		}
		return belColl;
	}

	/**
	 * returns the OWLClass of the Class BeliefSpace.
	 * 
	 * @return the Class of the Class BeliefSpace.
	 */
	public OWLClass getBeliefSpaceClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#BeliefSpace"));
	}

	/**
	 * creates a BeliefSpace object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created BeliefSpace object.
	 */
	public BeliefSpace createBeliefSpace(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getBeliefSpaceClass(), newIndi)));
		return new BeliefSpace(newIndi, onto, factory, manager);
	}

	/**
	 * creates a Partition object (the Java class as well as its OWL behinds
	 * 
	 * @param name
	 *            the name of the Partition to create.
	 * @return the created Partition object
	 */
	public Partition createPartition(String name, OwlSpeakOntology osOnto) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getBeliefSpaceClass(), newIndi)));
		Partition p = new Partition(newIndi, onto, factory, manager, this, osOnto.ontoHISvariant);
		return p;
	}

	/**
	 * returns a BeliefSpace specified by its name.
	 * 
	 * @param name
	 *            the Name of the BeliefSpace.
	 * @return the BeliefSpace object.
	 */
	public BeliefSpace getBeliefSpace(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new BeliefSpace(newIndi, onto, factory, manager);
	}

	/**
	 * returns the root partition of the partition tree containing all
	 * partitions belonging to one partition distribution specified by its name.
	 * 
	 * The partitions are read from the OWL ontology.
	 * 
	 * Retrieves the root partition identified by name and then performs a
	 * breadth-first search using a queue data structure.
	 * 
	 * @param user
	 *            the user name
	 * @param suffix
	 *            suffix added to user to build the Name of the main (root)
	 *            partition object.
	 * @param osOnto
	 *            the OwlSpeakOntology object
	 * @return the root partition of the partition tree of partitions belonging
	 *         to the same partition distribution
	 */
	public Partition getPartitionTree(String user, String suffix,
			OwlSpeakOntology osOnto) {
		String name = user + suffix;
		Queue<Partition> queue = new LinkedList<Partition>();

		boolean rootIsOnlyPartition = false;
		String iriString = onto.getOntologyID().getOntologyIRI() + "#" + name;
		Partition root = new Partition(factory.getOWLNamedIndividual(IRI
				.create(iriString)), onto, factory, manager, osOnto,
				(Partition) null);
		if (GenericProvider.hasDataProperty(root.indi,
				GenericProvider.beliefProp, onto, factory))
			// set belief without modifying the ontology
			root.belief = GenericProvider.getFloatProperty(root.indi,
					GenericProvider.beliefProp, onto, factory);
		else {
			// assumption: root is only partition
			root.setBelief(1.0f);
			rootIsOnlyPartition = true;
		}

		if (GenericProvider.hasDataProperty(root.indi,
				GenericProvider.priorProp, onto, factory))
			// set prior without modifying the ontology
			root.prior = GenericProvider.getFloatProperty(root.indi,
					GenericProvider.priorProp, onto, factory);
		else {
			// assumption: root is only partition
			root.setPrior(1.0f);
			rootIsOnlyPartition = true;
		}
		queue.add(root);

		// breadth-first search
		while (!queue.isEmpty()) {
			Partition parent = queue.poll();
			if (parent != null) {
				Vector<Partition> children = new Vector<Partition>();
				Vector<OWLIndividual> childrenIndis = parent.getChildrenIndis();
				if (childrenIndis != null) {
					if (rootIsOnlyPartition)
						System.err
								.println("Error! Parent belief or prior was set but children exist!");
					for (OWLIndividual newIndi : parent.getChildrenIndis()) {
						Partition p = new Partition(newIndi, onto, factory,
								manager, osOnto, parent);
						if (GenericProvider.hasDataProperty(p.indi,
								GenericProvider.beliefProp, onto, factory))
							p.belief = GenericProvider.getFloatProperty(
									newIndi, GenericProvider.beliefProp, onto,
									factory);
						if (GenericProvider.hasDataProperty(p.indi,
								GenericProvider.priorProp, onto, factory))
							p.prior = GenericProvider.getFloatProperty(newIndi,
									GenericProvider.priorProp, onto, factory);
						else
							p.prior = 1.0f;
						queue.add(p);
						children.add(p);
					}
					parent.addChildrenObject(children);
				}
				// partitions.addAll(children);
			}
		}

		return root;
	}

	public PartitionDistributionFactory getPartitionDistributionFactory() {
		if (partitionDistributionFactory == null) {
			this.partitionDistributionFactory = new PartitionDistributionFactory();
		}
		return partitionDistributionFactory;
	}

	/**
	 * returns a Collection of all BeliefSpace objects.
	 * 
	 * @return the Collection of the BeliefSpaces.
	 */
	public Collection<BeliefSpace> getAllBeliefSpaceInstances() {
		Iterator<OWLIndividual> coll = getBeliefSpaceClass().getIndividuals(
				onto).iterator();
		Collection<BeliefSpace> belSpColl = new ArrayList<BeliefSpace>();
		while (coll.hasNext()) {
			belSpColl.add(new BeliefSpace(coll.next(), onto, factory, manager));
		}
		return belSpColl;
	}

	/**
	 * returns the OWLClass of the Class Move.
	 * 
	 * @return the Class of the Class Move.
	 */
	public OWLClass getMoveClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Move"));
	}

	/**
	 * creates a Move object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Move object.
	 */
	public Move createMove(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getMoveClass(), newIndi)));
		return new Move(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Move specified by its name.
	 * 
	 * @param name
	 *            the Name of the Move.
	 * @return the Move object.
	 */
	public Move getMove(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Move(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all Move objects.
	 * 
	 * @return the Collection of the Moves.
	 */
	public Collection<Move> getAllMoveInstances() {
		Iterator<OWLIndividual> coll = getMoveClass().getIndividuals(onto)
				.iterator();
		Collection<Move> moveColl = new ArrayList<Move>();
		while (coll.hasNext()) {
			moveColl.add(new Move(coll.next(), onto, factory, manager));
		}
		return moveColl;
	}

	/**
	 * returns the OWLClass of the Class DialogueDomain.
	 * 
	 * @return the Class of the Class DialogueDomain.
	 */
	public OWLClass getDialogueDomainClass() {
		return factory
				.getOWLClass(IRI.create(Settings.uri + "#DialogueDomain"));
	}

	/**
	 * creates a DialogueDomain object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created DialogueDomain object.
	 */
	public DialogueDomain createDialogueDomain(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getDialogueDomainClass(), newIndi)));
		return new DialogueDomain(newIndi, onto, factory, manager);
	}

	/**
	 * returns a DialogueDomain specified by its name.
	 * 
	 * @param name
	 *            the Name of the DialogueDomain.
	 * @return the DialogueDomain object.
	 */
	public DialogueDomain getDialogueDomain(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new DialogueDomain(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all DialogueDomain objects.
	 * 
	 * @return the Collection of the DialogueDomains.
	 */
	public Collection<DialogueDomain> getAllDialogueDomainInstances() {
		Iterator<OWLIndividual> coll = getDialogueDomainClass().getIndividuals(
				onto).iterator();
		Collection<DialogueDomain> ddColl = new ArrayList<DialogueDomain>();
		while (coll.hasNext()) {
			ddColl.add(new DialogueDomain(coll.next(), onto, factory, manager));
		}
		return ddColl;
	}

	/**
	 * returns the OWLClass of the Class WorkSpace.
	 * 
	 * @return the Class of the Class WorkSpace.
	 */
	public OWLClass getWorkSpaceClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#WorkSpace"));
	}

	/**
	 * creates a WorkSpace object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created WorkSpace object.
	 */
	public WorkSpace createWorkSpace(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getWorkSpaceClass(), newIndi)));
		return new WorkSpace(newIndi, onto, factory, manager);
	}

	/**
	 * returns a WorkSpace specified by its name.
	 * 
	 * @param name
	 *            the Name of the WorkSpace.
	 * @return the WorkSpace object.
	 */
	public WorkSpace getWorkSpace(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new WorkSpace(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all WorkSpace objects.
	 * 
	 * @return the Collection of the WorkSpaces.
	 */
	public Collection<WorkSpace> getAllWorkSpaceInstances() {
		Iterator<OWLIndividual> coll = getWorkSpaceClass().getIndividuals(onto)
				.iterator();
		Collection<WorkSpace> wsColl = new ArrayList<WorkSpace>();
		while (coll.hasNext()) {
			wsColl.add(new WorkSpace(coll.next(), onto, factory, manager));
		}
		return wsColl;
	}

	/**
	 * returns the OWLClass of the Class Agenda.
	 * 
	 * @return the Class of the Class Agenda.
	 */
	public OWLClass getAgendaClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Agenda"));
	}

	/**
	 * returns the OWLClass of the Class SummaryAgenda.
	 * 
	 * @return the Class of the Class SummaryAgenda.
	 */
	public OWLClass getSummaryAgendaClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#SummaryAgenda"));
	}

	/**
	 * creates a Agenda object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Agenda object.
	 */
	public Agenda createAgenda(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getAgendaClass(), newIndi)));
		return new Agenda(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Agenda specified by its name.
	 * 
	 * @param name
	 *            the Name of the Agenda.
	 * @return the Agenda object.
	 */
	public Agenda getAgenda(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Agenda(newIndi, onto, factory, manager);
	}

	public SummaryAgenda createSummaryAgenda(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getSummaryAgendaClass(), newIndi)));
		return new SummaryAgenda(newIndi, onto, factory, manager);
	}

	
	/**
	 * returns a Collection of all Agenda objects.
	 * 
	 * @return the Collection of the Agendas.
	 */
	public Collection<Agenda> getAllAgendaInstances() {
		Iterator<OWLIndividual> coll = getAgendaClass().getIndividuals(onto)
				.iterator();
		Collection<Agenda> agendaColl = new ArrayList<Agenda>();
		while (coll.hasNext()) {
			agendaColl.add(new Agenda(coll.next(), onto, factory, manager));
		}
		return agendaColl;
	}

	public Collection<SummaryAgenda> getAllSummaryAgendaInstances() {
		Iterator<OWLIndividual> coll = getSummaryAgendaClass().getIndividuals(
				onto).iterator();
		Collection<SummaryAgenda> agendaColl = new ArrayList<SummaryAgenda>();
		while (coll.hasNext()) {
			agendaColl.add(new SummaryAgenda(coll.next(), onto, factory,
					manager));
		}
		return agendaColl;
	}

	/**
	 * returns the OWLClass of the Class Utterance.
	 * 
	 * @return the Class of the Class Utterance.
	 */
	public OWLClass getUtteranceClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Utterance"));
	}

	/**
	 * creates an Utterance object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Utterance object.
	 */
	public Utterance createUtterance(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getUtteranceClass(), newIndi)));
		return new Utterance(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Utterance specified by its name.
	 * 
	 * @param name
	 *            the Name of the Utterance.
	 * @return the Utterance object.
	 */
	public Utterance getUtterance(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Utterance(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all Utterance objects.
	 * 
	 * @return the Collection of the Utterance.
	 */
	public Collection<Utterance> getAllUtteranceInstances() {
		Iterator<OWLIndividual> coll = getUtteranceClass().getIndividuals(onto)
				.iterator();
		Collection<Utterance> uttColl = new ArrayList<Utterance>();
		while (coll.hasNext()) {
			uttColl.add(new Utterance(coll.next(), onto, factory, manager));
		}
		return uttColl;
	}

	/**
	 * returns the OWLClass of the Class Variable.
	 * 
	 * @return the Class of the Class Variable.
	 */
	public OWLClass getVariableClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Variable"));
	}

	/**
	 * creates a Variable object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Variable object.
	 */
	public Variable createVariable(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getVariableClass(), newIndi)));
		return new Variable(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Variable specified by its name.
	 * 
	 * @param name
	 *            the Name of the Variable.
	 * @return the Variable object.
	 */
	public Variable getVariable(String name) {
		if (name.contains(":")) {
			System.err.println("variable name contains ontology name. must be resolved elsewhere. variable name: " + name);
			return null;
		}
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Variable(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all Variable objects.
	 * 
	 * @return the Collection of the Variables.
	 */
	public Collection<Variable> getAllVariableInstances() {
		Iterator<OWLIndividual> coll = getVariableClass().getIndividuals(onto)
				.iterator();
		Collection<Variable> varColl = new ArrayList<Variable>();
		while (coll.hasNext()) {
			varColl.add(new Variable(coll.next(), onto, factory, manager));
		}
		return varColl;
	}

	/**
	 * returns a boolean indicating if a variable with the given name exists
	 * note: any ontology membership is not regarded yet
	 * 
	 * @return true, if the variable with name name exists, else false.
	 */
	public boolean existsVariable(String name) {
		boolean exists = false;
		for (Variable v : getAllVariableInstances()) {
			if (v.getLocalName().equalsIgnoreCase(name)) {
				exists = true;
				break;
			}
		}
		return exists;
	}
	
	/**
	 * returns a boolean indicating if a variable with the given name exists
	 * note: any ontology membership is not regarded yet
	 * 
	 * @return true, if the variable with name name exists, else false.
	 */
	public boolean existsBeliefSpace(String name) {
		boolean exists = false;
		for (BeliefSpace bs : getAllBeliefSpaceInstances()) {
			if (bs.getLocalName().equalsIgnoreCase(name)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	/**
	 * returns the OWLClass of the Class Semantic.
	 * 
	 * @return the Class of the Class Semantic.
	 */
	public OWLClass getSemanticClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Semantic"));
	}

	/**
	 * creates a Semantic object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created Semantic object.
	 */
	public Semantic createSemantic(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getSemanticClass(), newIndi)));
		return new Semantic(newIndi, onto, factory, manager);
	}

	// newly added
	/**
	 * returns the OWLClass of the Class SemanticGroup.
	 * 
	 * @return the Class of the Class SemanticGroup.
	 */
	public OWLClass getSemanticGroupClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#SemanticGroup"));
	}

	/**
	 * creates a SemanticGroup object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created SemanticGroup object.
	 */
	public SemanticGroup createSemanticGroup(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getSemanticGroupClass(), newIndi)));
		return new SemanticGroup(newIndi, onto, factory, manager);
	}
	
	/**
	 * returns a SemanticGroup specified by its name.
	 * 
	 * @param name
	 *            the Name of the SemanticGroup.
	 * @return the SemanticGroup object.
	 */
	public SemanticGroup getSemanticGroup(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new SemanticGroup(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Semantic specified by its name.
	 * 
	 * @param name
	 *            the Name of the Semantic.
	 * @return the Semantic object.
	 */
	public Semantic getSemantic(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new Semantic(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all Semantic objects.
	 * 
	 * @return the Collection of the Semantics.
	 */
	public Collection<Semantic> getAllSemanticInstances() {
		Iterator<OWLIndividual> coll = getSemanticClass().getIndividuals(onto)
				.iterator();
		Collection<Semantic> semColl = new ArrayList<Semantic>();
		while (coll.hasNext()) {
			semColl.add(new Semantic(coll.next(), onto, factory, manager));
		}
		return semColl;
	}
	
	/**
	 * returns a Collection of all Semantic objects.
	 * 
	 * @return the Collection of the Semantics.
	 */
	public Collection<SemanticGroup> getAllSemanticGroupInstances() {
		Iterator<OWLIndividual> coll = getSemanticGroupClass().getIndividuals(onto)
				.iterator();
		Collection<SemanticGroup> semColl = new ArrayList<SemanticGroup>();
		while (coll.hasNext()) {
			semColl.add(new SemanticGroup(coll.next(), onto, factory, manager));
		}
		return semColl;
	}

	/**
	 * returns the OWLClass of the Class History.
	 * 
	 * @return the Class of the Class History.
	 */
	public OWLClass getHistoryClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#History"));
	}

	/**
	 * creates a History object.
	 * 
	 * @param name
	 *            the name of the Object to create.
	 * @return the created History object.
	 */
	public History createHistory(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getHistoryClass(), newIndi)));
		return new History(newIndi, onto, factory, manager);
	}

	/**
	 * returns a History specified by its name.
	 * 
	 * @param name
	 *            the Name of the History.
	 * @return the History object.
	 */
	public History getHistory(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		return new History(newIndi, onto, factory, manager);
	}

	/**
	 * returns a Collection of all History objects.
	 * 
	 * @return the Collection of the Histories.
	 */
	public Collection<History> getAllHistoryInstances() {
		Iterator<OWLIndividual> coll = getHistoryClass().getIndividuals(onto)
				.iterator();
		Collection<History> histColl = new ArrayList<History>();
		while (coll.hasNext()) {
			histColl.add(new History(coll.next(), onto, factory, manager));
		}
		return histColl;
	}

	/**
	 * returns the OWLClass of the Class SummaryBelief.
	 * 
	 * @return the Class of the Class SummaryBelief.
	 */
	public OWLClass getSummaryBeliefClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#SummaryBelief"));
	}

	public SummaryBelief createSummaryBelief(
			IPartitionDistribution iPartitionDistribution,
			Move _lastUserAction, String name) {

		Partition p = (Partition) iPartitionDistribution.getTopPartition();

		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getSummaryBeliefClass(), newIndi)));

		SummaryBelief sumBel = new SummaryBelief(newIndi, onto, factory,
				manager);

		sumBel.setTopBelief(p.getBelief());
		Partition secondTopPartition = ((Partition) iPartitionDistribution
				.getSecondTopPartition());
		if (secondTopPartition != null)
			sumBel.setSecondTopBelief(secondTopPartition.getBelief());
		else
			sumBel.setSecondTopBelief(0.0f);
		sumBel.setPartitionState(SummaryBelief.computePartitionState(p));
//		sumBel.setHistoryState(SummaryBelief
//				.computeHistoryState(iPartitionDistribution
//						.getTopPartitionsHistory()));
		sumBel.setHistoryState(SummaryBelief
				.computeHistoryState(iPartitionDistribution
						.getTopPartition()));
		// TODO handle user action
		sumBel.setLastUserAction(null);
		sumBel.setPartitionNumFields(p.getFields().size());
		
		// TODO define default IQ value
		sumBel.setInteractionQuality(-1);

		return sumBel;
	}
	
	/**
	 * Copies the SummaryBeliefnonpersistent into a persistent SummaryBelief ontology object with name name.<br />
	 * The name of the new sbp is automatically determined by counting the objects inside the ontology.
	 * 
	 * @param sbnp
	 * @return
	 */
	public SummaryBelief createSummaryBelief(SummaryBeliefNonpersistent sbnp) {
		
		int countSumBelPoints = SummaryBelief.getAndIncreaseSummaryBeliefNumber();

		int size = getAllSummaryBeliefInstances().size();		
		
		if (size > countSumBelPoints){
			countSumBelPoints = SummaryBelief.setAndIncreaseSummaryBeliefNumber(size-1);
		}
		
		String name = "sbp" + String.format("%05d", countSumBelPoints);
		return createSummaryBelief(sbnp, name);
	}
	
	/**
	 * Copies the SummaryBeliefnonpersistent into a persistent SummaryBelief ontology object with name name.
	 * 
	 * @param sbnp the SummaryBeliefNonpersistent objecte to be transformed into a persistent one
	 * @param name the name of the persistent object to create in the ontology
	 * @return the newly created persistent SummaryBelief object
	 */
	public SummaryBelief createSummaryBelief(SummaryBeliefNonpersistent sbnp, String name) {
		 
		 Move lua = sbnp.getLastUserAction();		
		 int pnf = sbnp.getPartitionNumFields();
		 HistoryState hs = sbnp.getHistoryState();
		 PartitionState ps = sbnp.getPartitionState();
		 float tb = sbnp.getTopBelief();
		 float stb = sbnp.getSecondTopBelief();
		
		 	OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
					.getOntologyID().getOntologyIRI() + "#" + name));
			manager.applyChange(new AddAxiom(onto, factory
					.getOWLClassAssertionAxiom(getSummaryBeliefClass(), newIndi)));

			SummaryBelief sumBel = new SummaryBelief(newIndi, onto, factory,
					manager);

			sumBel.setTopBelief(tb);		
			sumBel.setSecondTopBelief(stb);
			sumBel.setPartitionState(ps);
			sumBel.setHistoryState(hs);
			sumBel.setLastUserAction(lua);
			sumBel.setPartitionNumFields(pnf);
			sumBel.setSummaryAgenda(sbnp.getSummaryAgenda());
			sumBel.setInteractionQuality(sbnp.getInteractionQuality());
		
		return sumBel;
	}

	/**
	 * returns a Collection of all SummaryBelief objects.
	 * 
	 * @return the Collection of the SummaryBeliefs.
	 */
	public Collection<SummaryBelief> getAllSummaryBeliefInstances() {
		Iterator<OWLIndividual> coll = getSummaryBeliefClass().getIndividuals(
				onto).iterator();
		Collection<SummaryBelief> summaryBeliefColl = new ArrayList<SummaryBelief>();
		while (coll.hasNext()) {
			summaryBeliefColl.add(new SummaryBelief(coll.next(), onto, factory,
					manager));
		}
		return summaryBeliefColl;
	}

	/**
	 * returns the OWLClass of the Class Reward.
	 * 
	 * @return the Class of the Class Reward.
	 */
	public OWLClass getRewardClass() {
		return factory.getOWLClass(IRI.create(Settings.uri + "#Reward"));
	}

	/**
	 * returns a Collection of all Reward objects.
	 * 
	 * @return the Collection of the Rewards.
	 */
	public Collection<Reward> getAllRewardInstances() {
		Iterator<OWLIndividual> coll = getRewardClass().getIndividuals(onto)
				.iterator();
		Collection<Reward> gramColl = new ArrayList<Reward>();
		while (coll.hasNext()) {
			gramColl.add(new Reward(coll.next(), onto, factory, manager));
		}
		return gramColl;
	}
	
	public Reward createReward(String name) {
		OWLIndividual newIndi = factory.getOWLNamedIndividual(IRI.create(onto
				.getOntologyID().getOntologyIRI() + "#" + name));
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(getRewardClass(), newIndi)));
		return new Reward(newIndi, onto, factory, manager);
	}
}
