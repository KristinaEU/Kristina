package owlSpeak.engine.his;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import owlSpeak.Belief;
import owlSpeak.BeliefSpace;
import owlSpeak.GenericClass;
import owlSpeak.GenericProvider;
import owlSpeak.Move;
import owlSpeak.Move.NoConfirmationInfoException;
import owlSpeak.OSFactory;
import owlSpeak.SummaryAgenda.SummaryAgendaType;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.Field.FieldType;
import owlSpeak.engine.his.FieldValue.ConfirmationInfo;
import owlSpeak.engine.his.exception.NoFactorySetException;
import owlSpeak.engine.his.interfaces.IHistory;
import owlSpeak.engine.his.interfaces.IMean;
import owlSpeak.engine.his.interfaces.IPartition;
import owlSpeak.engine.his.interfaces.ISystemAction;
import owlSpeak.engine.his.interfaces.IUserAction;
import owlSpeak.engine.his.interfaces.IUserActionSupplementary;
import owlSpeak.engine.his.sysAction.ConfirmationSystemAction;
import owlSpeak.engine.his.sysAction.GenericSystemAction;
import owlSpeak.engine.his.sysAction.ImplicitSystemAction;

/**
 * Implementation of a partition used by PartitionDistribution which is part of
 * the ASDT.
 * 
 * It both takes care of Owl-persistence as well as holds internal data
 * structures for easier handling.
 * 
 * A partition contains several fields (aka slots). Each field can take one
 * value and can exclude many values.
 * 
 * A Partition has a belief value and a prior value.
 * 
 * A partition may have multiple children but only one parent partition.
 * 
 * 
 * @author Stefan Ultes &lt;stefan.ultes@uni-ulm.de&gt;
 * @version 0.1
 * 
 */
public class Partition extends BeliefSpace implements IPartition {

	public enum PartitionState {
		INIT, GENERIC, GROUP, UNIQUE
	}

	public enum HISvariant {
		SEM, VAR
	}

	private TreeMap<String, Field> fields;
	public float prior;
	public float belief;
	private Partition parent;
	private Vector<Partition> children = new Vector<Partition>();
	private OSFactory osFactory;
	public static int partitionNumber = 0;
	private Partition root = null;
	private Map<String, Integer> fieldTotals;

	private HISvariant variant;
	
	// private int numExcludeofSameFieldFound = 1;

	// private static int _CITIES = 1000;

	// public static Vector<Partition> partitions = new Vector<Partition>();

	public Partition(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager,
			OSFactory _osFactory, HISvariant _variant) {
		super(indi, onto, factory, manager);
		variant = _variant;
		osFactory = _osFactory;
		fields = new TreeMap<String, Field>();
		setPrior(1.0f);
		// partitions.add(this);
	}

	/**
	 * This constructor is used only if class variables have to be set without
	 * modifying the ontology, e.g., while loading the partition tree from the
	 * ontology
	 * 
	 * @param indi
	 * @param onto
	 * @param factory
	 * @param manager
	 * @param osOntology
	 * @param _parent
	 */
	public Partition(OWLIndividual indi, OWLOntology onto,
			OWLDataFactory factory, OWLOntologyManager manager,
			OwlSpeakOntology osOntology, Partition _parent) {
		super(indi, onto, factory, manager);

		IUserActionSupplementary supp = UserAction
				.getUserActionSupp(osOntology);

		osFactory = osOntology.factory;
		fields = new TreeMap<String, Field>();
		parent = _parent;
		variant = osOntology.ontoHISvariant;
		// partitions.add(this);
		Vector<String> emptyFields = new Vector<String>();

		for (Belief b : this.getHasBelief()) {
			String fieldName = supp.getFieldNameFromBelief(b);

			if (fieldName != null) {
				FieldValue val = supp.getFieldValueFromBelief(b);
				if (!fields.containsKey(fieldName)) {
					// a new field has to be created
					Field f = null;
					if (supp.beliefRepresentsConfirmation(b)) {
						f = new Field(fieldName, FieldType.EQUALS);
						// Either true => confirmed of false => Rejected since
						// undefined was checked before
						f.setConfirmed(val.getConfirmationInfo() == ConfirmationInfo.CONFIRM);
						f.setRejected(val.getConfirmationInfo() == ConfirmationInfo.REJECT);
						emptyFields.add(fieldName);
					}
					// ignore beliefs which only contain the field semantic
					else if (!supp.beliefRepresentsField(b)) {
						f = new Field(fieldName, FieldType.EQUALS,
								val.getValue());
					}
					if (f != null)
						fields.put(fieldName, f);
				} else {
					Field f = fields.get(fieldName);
					if (supp.beliefRepresentsConfirmation(b)) {
						f.setConfirmed(val.getConfirmationInfo() == ConfirmationInfo.CONFIRM);
					}
					// ignore beliefs which only contain the field semantic
					else if (!supp.beliefRepresentsField(b)) {
						// first belief which contains value for given field
						if (f.getEquals() == null) {
							f.setEquals(val.getValue());
							emptyFields.remove(fieldName);
						}
						// not first belief which contains value for given field
						// hence: error
						else {
							System.err
									.println("There are two equal values in partition "
											+ this.getLocalName()
											+ " belonging to the same field ("
											+ fieldName + ").");
						}
					}
					// if (f.getEquals() != null) {
					// System.err
					// .println("There are two equal values in partition "
					// + this.getLocalName()
					// + " belonging to the same field ("
					// + fieldName + ").");
					// } else {
					//
					// }

				}
			}
		}

		if (!emptyFields.isEmpty()) {
			System.err.println("Empty field created!");
			for (String s : emptyFields)
				System.err.println("\t" + s);
		}

		for (Belief b : this.getExcludesBelief()) {
			String fieldName = supp.getFieldNameFromBelief(b);

			if (fieldName != null) {
				FieldValue val = supp.getFieldValueFromBelief(b);
				if (fields.containsKey(fieldName)) {
					if (fields.get(fieldName).getType() == FieldType.EXCLUDES) {
						fields.get(fieldName).addExcludeValue(val.getValue());
					} else {
						// field is equals but ontology contains exclude
						// values;
						// this should never happen!
						System.err
								.println("Beliefspace "
										+ this.getLocalName()
										+ " contains equal and exclude values for field "
										+ fieldName);
						// System.err.println("Error 432");
					}
				} else {
					Field f = new Field(fieldName, FieldType.EXCLUDES,
							val.getValue());
					fields.put(fieldName, f);
				}

			}
		}
	}

	/**
	 * Creates a new child partition with this partition as parent on field
	 * fieldName and Semantic sem. Creates a new Field object. Clones every
	 * field of parent partition except for the field which has the same name as
	 * the newly created field. This field is used instead in the child.
	 * 
	 * Assumes that the new child partition is of type EQUALS for the new field
	 * of name fieldName which equals semantic.
	 * 
	 * Ontology-wise, creates a new individual of owl-type BeliefSpace which
	 * represents the new child. Also, new Beliefs are created for the child
	 * partition containing the Semantic sem and the copied Beliefs from the
	 * parent partition.
	 * 
	 * @param fieldVal
	 *            the field value object
	 * @param fieldName
	 *            the name of the field fieldVal belongs to
	 * @return the new child partition
	 */
	public Partition createChild(FieldValue fieldVal, String fieldName) {
		String val = fieldVal.getValue();
		IUserActionSupplementary supp = UserAction.getUserActionSupp(osFactory,
				variant);

		// create new owl entry and register it with the ontology
		IRI iri = IRI.create(getRoot().getFullName() + partitionNumber);
		partitionNumber += 1;
		OWLIndividual newIndi = factory.getOWLNamedIndividual(iri);
		manager.applyChange(new AddAxiom(onto, factory
				.getOWLClassAssertionAxiom(
						factory.getOWLClass(IRI.create(Settings.uri
								+ "#BeliefSpace")), newIndi)));

		// create new Partition object
		Partition nPar = new Partition(newIndi, onto, factory, manager,
				osFactory, variant);
		nPar.setPrior(this.prior);

		// deal with fields object
		for (String fName : this.fields.keySet()) {
			if (fieldName.equalsIgnoreCase(fName)) {
				Field field = new Field(fieldName, FieldType.EQUALS, val);
				nPar.fields.put(fName, field);
			} else {
				nPar.fields.put(fName, this.fields.get(fName).clone());
			}
		}
		fields.get(fieldName).addExcludeValue(val);

		// deal with ontology content
		try {
			supp.createNewEqualsBelief(fieldVal, fieldName, nPar);
			supp.createNewExcludesBelief(fieldVal, fieldName, this);
		} catch (NoFactorySetException e) {
			System.err
					.println("UserActionSupp was created without associating a OSFactory object.");
			return null;
		}

		nPar.getHasBelief();
		nPar.getExcludesBelief();

		// copy exclude beliefs from parent which do not relate to field name
		for (Belief b : this.getExcludesBelief()) {
			String beliefFieldName = supp.getFieldNameFromBelief(b);
			if (beliefFieldName != null
					&& !fieldName.equalsIgnoreCase(beliefFieldName)) {
				try {
					supp.copyExcludesBelief(b, nPar);
				} catch (NoFactorySetException e) {
					System.err
							.println("UserActionSupp was created without associating a OSFactory object.");
					return null;
				}
			}
			// String beliefFieldName = supp.getFieldNameFromBelief(b);
			// FieldValue beliefFieldValue = supp.getFieldValueFromBelief(b);
			// if (!fieldName.equalsIgnoreCase(beliefFieldName)) {
			// supp.createNewExcludesBelief(beliefFieldValue, beliefFieldName,
			// nPar);
			// } else {
			// if (!beliefFieldValue.equals(fieldVal)) {
			// supp.createNewExcludesBelief(beliefFieldValue,
			// beliefFieldName, nPar);
			// } else {
			// // this has to happen exactly one time for the newly created
			// // equals of this partition
			// if (numExcludeofSameFieldFound++ > 0)
			// System.err
			// .println("Time " + numExcludeofSameFieldFound);
			// }
			// }
		}

		// copy has beliefs from parent which do not relate to field name
		for (Belief b : this.getHasBelief()) {
			String beliefFieldName = supp.getFieldNameFromBelief(b);
			if (beliefFieldName != null
					&& !fieldName.equalsIgnoreCase(beliefFieldName)) {
				try {
					supp.copyHasBelief(b, nPar);
				} catch (NoFactorySetException e) {
					System.err
							.println("UserActionSupp was created without associating a OSFactory object.");
					return null;
				}
			}

			// if (beliefFieldName != null) {
			// if (!fieldName.equalsIgnoreCase(beliefFieldName)) {
			//
			// boolean hasVar = false, hasSem = false;
			//
			// // beliefs representing the field
			// if (supp.beliefRepresentsField(b)) {
			// supp.createNewConfirmationBelief(
			// supp.getFieldValueFromBelief(b),
			// beliefFieldName, nPar);
			// hasSem = true;
			// }
			// // beliefs representing confirmation
			// else if (supp.beliefRepresentsConfirmation(b)) {
			// supp.createNewConfirmationBelief(
			// supp.getFieldValueFromBelief(b),
			// beliefFieldName, nPar);
			// hasSem = true;
			// }
			// // beliefs with value (i.e., variable or semantic)
			// else {
			// supp.createNewEqualsBelief(
			// supp.getFieldValueFromBelief(b),
			// beliefFieldName, nPar);
			// hasVar = true;
			// }
			//
			// if (hasVar && hasSem)
			// System.err
			// .println("Belief contains both variable and semantic.");
			// } else {
			// // there exist an equals for the field although an excludes
			// // has
			// // been created this round
			// System.err.println("Field " + fieldName
			// + " contains equals and excludes.");
			// }
			// }
			// else
			// {
			// supp.createNewGenericBelief(b, nPar);
			// }
		}

		return nPar;
	}

	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * UserActionLikelihoods for confirmations are calculated as follows:
	 * <ul>
	 * <li>Confirmation is YES:
	 * <ul>
	 * <li>confirmed value is equal to equals value of same field &ge; 1.0</li>
	 * <li>confirmed value is contained in excludes of same field &ge; 0.0</li>
	 * <li>confirmed value is not mentioned in equals or excludes &ge; 0.0</li>
	 * </ul>
	 * </li>
	 * <li>Confirmation is NO:
	 * <ul>
	 * <li>confirmed value is equal to equals value of same field &ge; 0.0</li>
	 * <li>confirmed value is contained in excludes of same field &ge; 1.0</li>
	 * <li>confirmed value is not mentioned in equals or excludes &ge; 1.0</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 */
	@Override
	public double UserActionLikelihood(IUserAction iUserAction,
			IHistory iHistory, ISystemAction iSysAction) {
		IMean mean = createNewMean();
		double result = 0.0;

		if (iUserAction instanceof UserAction
				&& iSysAction instanceof GenericSystemAction) {
			UserAction userAction = (UserAction) iUserAction;
			GenericSystemAction systemAction = (GenericSystemAction) iSysAction;
			switch (userAction.getType()) {
			case IMPLICIT:
			case IG:
				if (systemAction.getType() == SummaryAgendaType.REQUEST) {
					if (userAction.getFieldVector() != null
							&& userAction.getFieldVector().size() > 0) {
						for (String fieldName : userAction.getFieldVector()) {
							FieldValue fieldVal = userAction
									.getFieldValue(fieldName);
							if (fieldVal != null) {
								String val = fieldVal.getValue();

								Field field = fields.get(fieldName);
								if (field != null) {
									if (field.getType() == FieldType.EQUALS) {
										if (field.getEquals().equalsIgnoreCase(
												val))
											mean.addValue(1.0);
//											result += 1.0;
										else
											result += 0.0;
									} else if (field.getExcludes()
											.contains(val))
//										result += 0.0;
										mean.addValue(0.0);
									else
//										result += 1.0 / (getFieldTotals().get(
//												fieldName) - field
//												.getExcludes().size());
										mean.addValue(1.0 / (getFieldTotals().get(
												fieldName) - field
												.getExcludes().size()));
								}
							}
						}
						result = mean.getMean();
//						result /= userAction.getFieldVector().size();
					}
				} else if (systemAction.getType() == SummaryAgendaType.CONFIRMATION) {
					ConfirmationSystemAction confSysAct = (ConfirmationSystemAction)systemAction;
//					String fieldName = systemAction.getField();
					String fieldName = confSysAct.getConfirmationField();
					if (fieldName == null)
						System.err.println("No confirmation field set in system action.");
					String val = systemAction.getValue(fieldName);

					Move m = userAction.getMove();
					Field field = fields.get(fieldName);
					boolean conf;
					try {
						if (m == null) {
							System.err
							.println("no user move in user action");
					return 0.0f;
						}
						conf = m.getConfirmationInfo();
						if (conf) {
							if (val.equalsIgnoreCase(field.getEquals())) //FIXME
								return 1.0f;
							else if (field.getExcludes().contains(val))
								return 0.0f;
							else
								return 0.0f; // TODO define which probability is
												// returned if field has never
												// heard
												// of
												// this entity; should never
												// happen
						} else {
							if (val == null)
								System.err.println("oh oh");
							if (val.equalsIgnoreCase(field.getEquals()))
								return 0.0f;
							else if (field.getExcludes().contains(val))
								return 1.0f;
							else
								return 1.0f; // TODO define which probability is
												// returned if field has never
												// heard
												// of
												// this entity; should never
												// happen
						}
					} catch (NoConfirmationInfoException e) {
						System.err
								.println("confirmation move does not contain confirmation semantics.");
						return 0.0f;
					} 
				} else if (systemAction.getType() == SummaryAgendaType.IMPLICIT) {

					/*
					 * While a user move can target different fields with
					 * confirmation and requests, it is not possible to target
					 * the same field with request and confirmation. However,
					 * this should not happen in a regular dialogue anyway.
					 */
					if (userAction.getFieldVector() != null
							&& userAction.getFieldVector().size() > 0) {
						for (String uFieldName : userAction.getFieldVector()) {
							FieldValue userFieldValue = userAction
									.getFieldValue(uFieldName);

							if (userFieldValue.getConfirmationInfo() != ConfirmationInfo.UNDEFINED) {
								Field field = fields.get(uFieldName);
								boolean conf = userFieldValue
										.getConfirmationInfo() == ConfirmationInfo.CONFIRM;
								String val = ((ImplicitSystemAction) systemAction)
										.getValue(uFieldName);
								if (conf) {
									if (val.equalsIgnoreCase(field.getEquals()))
										mean.addValue(1.0f);
//										result += 1.0f;
									else if (field.getExcludes().contains(val))
//										result += 0.0f;
										mean.addValue(0.0f);
									else
										mean.addValue(0.0f);
//										result += 0.0f; // TODO define which
														// probability is
														// returned if field has
														// never heard
														// of
														// this entity; should
														// never
														// happen
								} else {
									if (val.equalsIgnoreCase(field.getEquals()))
										mean.addValue(0.0f);
//										result += 0.0f;
									else if (field.getExcludes().contains(val))
										mean.addValue(1.0f);
//										result += 1.0f;
									else
										mean.addValue(1.0f);
//										result += 1.0f; // TODO define which
														// probability is
														// returned if field has
														// never heard
														// of
														// this entity; should
														// never
														// happen
								}
							} else {
								if (userFieldValue != null) {
									String val = userFieldValue.getValue();

									Field field = fields.get(uFieldName);
									if (field != null) {
										if (field.getType() == FieldType.EQUALS) {
											if (field.getEquals()
													.equalsIgnoreCase(val))
												mean.addValue(1.0f);
//												result += 1.0;
											else
												mean.addValue(0.0f);
//												result += 0.0;
										} else if (field.getExcludes()
												.contains(val))
											mean.addValue(0.0f);
//											result += 0.0;
										else
											mean.addValue(1.0 / (getFieldTotals()
													.get(uFieldName) - field
													.getExcludes().size()));
//											result += 1.0 / (getFieldTotals()
//													.get(uFieldName) - field
//													.getExcludes().size());
									}
								}
							}
						}
						result = mean.getMean();
//						result /= userAction.getFieldVector().size();
					}
				} else if (systemAction.getType() == SummaryAgendaType.ANNOUNCEMENT) {
					result = 1.0;
				}
				break;
			case OOG:
			case SILENT:
				result = 0.0;
				break;
			}
		}
		// String s = "userActionLikeliHood: " + result + "\n\t"
		// + this + "\n\t" + iUserAction + "\n\t" + iSysAction;
		// System.out.println(s);
		return result;
	}

	/**
	 * {@inheritDoc} <br />
	 * <br />
	 * This implementation only splits the Partition if the corresponding
	 * sysAction of the userAction is of type {@link SummaryAgendaType#REQUEST}. <br />
	 * If the sysAction is of type {@link SummaryAgendaType#CONFIRMATION}, the
	 * field is confirmed if it matches the value which was confirmed.
	 */
	@Override
	public IPartition[] Split(IUserAction iUserAction) {
		IUserActionSupplementary supp = UserAction.getUserActionSupp(osFactory,
				variant);

		if (iUserAction instanceof UserAction) {
			UserAction userAction = (UserAction) iUserAction;

			Vector<Partition> newPartitions = new Vector<Partition>();

			for (String fieldName : userAction.getFieldVector()) {
				FieldValue fieldVal = userAction.getFieldValue(fieldName);
				if (fieldVal != null) {
					if (fieldVal.getConfirmationInfo() != ConfirmationInfo.UNDEFINED) {
						String val = userAction.getConfirmationValue(fieldName);
						if (val != null) {
							Field field = fields.get(fieldName);
							if (field != null
									&& field.getType() == FieldType.EQUALS
									&& field.getEquals().equalsIgnoreCase(val)) {
								if (fieldVal.getConfirmationInfo() == ConfirmationInfo.CONFIRM) {
									field.setConfirmed(true);
									try {
										supp.createNewConfirmationBelief(
												fieldVal, fieldName, this);
									} catch (NoFactorySetException e) {
										System.err
												.println("UserActionSupp was created without associating a OSFactory object.");
										return null;
									}
								}
								else if (fieldVal.getConfirmationInfo() == ConfirmationInfo.REJECT) {
									field.setRejected(true);
									// TODO create new reject belief
								}
							}
						}
					} else {
						String val = fieldVal.getValue();
						Field field = fields.get(fieldName);
						if (field == null) {
							field = new Field(fieldName, FieldType.EXCLUDES);
							fields.put(fieldName, field);
						}

						if (field.getType() == FieldType.EXCLUDES
								&& !field.getExcludes().contains(val)) {

							Partition np = this
									.createChild(fieldVal, fieldName);

							float oldPrior = this.prior;
							np.setPrior(Partition.computePrior(np));
							setPrior(Partition.computePrior(this));
							
							assert(oldPrior == np.prior+this.prior);
							newPartitions.add(np);
						}
					}
				}
			}
			Partition[] a = new Partition[0];
			return newPartitions.toArray(a);

			// switch (userAction.getSysActType()) {
			// case REQUEST:
			// Vector<Partition> newPartitions = new Vector<Partition>();
			//
			// for (String fieldName : userAction.getFieldVector()) {
			// FieldValue fieldVal = userAction.getFieldValue(fieldName);
			// if (fieldVal != null) {
			// String val = fieldVal.getValue();
			// Field field = fields.get(fieldName);
			// if (field == null) {
			// field = new Field(fieldName, FieldType.EXCLUDES);
			// fields.put(fieldName, field);
			// }
			//
			// if (field.getType() == FieldType.EXCLUDES
			// && !field.getExcludes().contains(val)) {
			//
			// Partition np = this
			// .createChild(fieldVal, fieldName);
			//
			// np.setPrior(Partition.computePrior(np));
			// setPrior(Partition.computePrior(this));
			// newPartitions.add(np);
			// }
			// }
			// }
			// Partition[] a = new Partition[0];
			// return newPartitions.toArray(a);
			// case CONFIRMATION:
			// for (String fieldName : userAction.getFieldVector()) {
			// FieldValue fieldVal = userAction.getFieldValue(fieldName);
			// if (fieldVal != null) {
			// String val = userAction.getConfirmationValue();
			// if (val != null) {
			// Field field = fields.get(fieldName);
			// if (field != null
			// && field.getType() == FieldType.EQUALS
			// && field.getEquals().equalsIgnoreCase(val)) {
			// if (fieldVal.getConfirmationInfo() == ConfirmationInfo.CONFIRM) {
			// field.setConfirmed(true);
			// try {
			// supp.createNewConfirmationBelief(fieldVal,
			// fieldName, this);
			// } catch (NoFactorySetException e) {
			// System.err.println("UserActionSupp was created without associating a OSFactory object.");
			// return null;
			// }
			// }
			// }
			// }
			// }
			// }
			// return new Partition[0];
			// default:
			// System.err.println("System Action Type not implemented. Type: " +
			// userAction.getSysActType());
			// return null;
			// }
		}
		return new Partition[0];

		// if (iUserAction instanceof UserAction) {
		// UserAction userAction = (UserAction) iUserAction;
		//
		// switch (userAction.getSysActType()) {
		// case REQUEST:
		// Vector<Partition> newPartitions = new Vector<Partition>();
		//
		// for (String fieldName : userAction.getFieldVector()) {
		// FieldValue fieldVal = userAction.getFieldValue(fieldName);
		// if (fieldVal != null) {
		// String val = fieldVal.getValue();
		// Field field = fields.get(fieldName);
		// if (field == null) {
		// field = new Field(fieldName, FieldType.EXCLUDES);
		// fields.put(fieldName, field);
		// }
		//
		// if (field.getType() == FieldType.EXCLUDES
		// && !field.getExcludes().contains(val)) {
		//
		// Partition np = this
		// .createChild(fieldVal, fieldName);
		//
		// np.setPrior(Partition.computePrior(np));
		// setPrior(Partition.computePrior(this));
		// newPartitions.add(np);
		// }
		// }
		// }
		// Partition[] a = new Partition[0];
		// return newPartitions.toArray(a);
		// case CONFIRMATION:
		// for (String fieldName : userAction.getFieldVector()) {
		// FieldValue fieldVal = userAction.getFieldValue(fieldName);
		// if (fieldVal != null) {
		// String val = userAction.getConfirmationValue();
		// if (val != null) {
		// Field field = fields.get(fieldName);
		// if (field != null
		// && field.getType() == FieldType.EQUALS
		// && field.getEquals().equalsIgnoreCase(val)) {
		// if (fieldVal.getConfirmationInfo() == ConfirmationInfo.CONFIRM) {
		// field.setConfirmed(true);
		// try {
		// supp.createNewConfirmationBelief(fieldVal,
		// fieldName, this);
		// } catch (NoFactorySetException e) {
		// System.err.println("UserActionSupp was created without associating a OSFactory object.");
		// return null;
		// }
		// }
		// }
		// }
		// }
		// }
		// return new Partition[0];
		// default:
		// System.err.println("System Action Type not implemented. Type: " +
		// userAction.getSysActType());
		// return null;
		// }
		// }
		// return new Partition[0];
	}

	/**
	 * {@inheritDoc}
	 * 
	 * TODO rewrite this method in order to deal with ontology stuff
	 */
	@Override
	public boolean Recombine(IPartition iChild) {
		IUserActionSupplementary supp = UserAction.getUserActionSupp(osFactory,
				variant);

		/*
		 * first check all the fields of this and child partition and see if
		 * there are matching ones so that these two partitions can be combined
		 */
		if (iChild instanceof Partition) {
			Partition child = (Partition) iChild;

			Vector<String> valuesToRecombine = new Vector<String>();
			Vector<String> fieldsToRecombine = new Vector<String>();
			for (String fieldName : fields.keySet()) {
				Field parentField = fields.get(fieldName);
				Field childField = child.fields.get(fieldName);
				if (parentField == null || childField == null) {
					System.out.println("parentField or childField are null. "
							+ parentField + " " + childField);
					return false;
				}
				if (parentField.getType() == FieldType.EXCLUDES) {
					if (childField.getType() == FieldType.EQUALS) {
						// parent excludes, child equals
						String value = childField.getEquals();
						if (parentField.getExcludes().contains(value)) {
							fieldsToRecombine.add(fieldName);
							valuesToRecombine.add(value);
						} else {
							// TODO error handling
							// raise RuntimeError, 'Error: field %s: child
							// equals %s but parent doesn't exclude it' %
							// (field,value)
							System.err.println("Error: field " + fieldName + ": child equals " + value + " but parent doesn't exclude it");
							return false;
						}
					} else {
						// parent excludes, child excludes
						// ensure they exclude the same things
						if (parentField.getExcludes().size() != childField
								.getExcludes().size())
							return false;
						for (String val : parentField.getExcludes()) {
							if (!childField.getExcludes().contains(val)) {
								return false;
							}
						}
					}
				} else {
					if (childField.getType() == FieldType.EQUALS) {
						// parent equals, child equals (must be equal)
						// do nothing
					} else {
						// raise RuntimeError,'Error: field %s: parent equals %s
						// but child excludes this field' % (field,value)
						// TODO error handling
						String value = parentField.getEquals();
						System.err.println("Error: field " + fieldName + ": parent equals " + value + " but child excludes this field");
						return false;
					}
				}
			}

			if (fieldsToRecombine.isEmpty()) {
				// raise RuntimeError,'Error: parent and child are identical'
				// TODO error handling
				System.out.println("Error: parent and child are identical");
				return false;
			}
			if (fieldsToRecombine.size() > 1) {
				// raise RuntimeError,'Error: parent and child differ by more
				// than 1 field: %s' % (fieldsToRecombine)
				// TODO error handling
				System.err.println("Error: parent and child differ by more than 1 field: ");
				for (int i=0; i < fieldsToRecombine.size(); i++) {
					System.err.println("\t" + fieldsToRecombine.get(i) + ":" + valuesToRecombine.get(i));
				}
				return false;
			} else {
				// do actual merging here

				// find Belief belief
				Belief belief = null;
				String rFieldName = fieldsToRecombine.firstElement();
				for (Belief b : child.getHasBelief()) {
					String fieldName = supp.getFieldNameFromBelief(b);
					if (fieldName.equalsIgnoreCase(rFieldName)) {
						if (supp.getFieldValueFromBelief(b)
								.getValue()
								.equalsIgnoreCase(
										valuesToRecombine.firstElement())) {
							belief = b;
							break;
						}
					}
				}

				// remove belief from child and parent
				this.removeExcludesBelief(belief);
				child.removeHasBelief(belief);

				// delete belief indi
				belief.delete();

				// remove value from excludes list
				fields.get(rFieldName).removeExcludeValue(
						valuesToRecombine.firstElement());
				
				child.delete();
				return true;
			}
		}
		return false;
		// System.out
		// .println("+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n+++++++++++++++++++++++++++\n");
		//
		// Partition child = (Partition) iChild;
		// for (String fieldName : fields.keySet()) {
		// Field field = fields.get(fieldName);
		// if (field.getType() == FieldType.EXCLUDES) {
		// if (child.fields.get(fieldName).getType() == FieldType.EXCLUDES) {
		// setPrior(prior + child.prior);
		// String value = child.fields.get(fieldName).getEquals();
		// field.getExcludes().remove(value);
		// }
		// else
		// return false;
		// }
		// else
		// return false;
		// }
		// return true;
	}

	@Override
	public String __str__() {
		String s = "";
		if (fields.size() > 0) {
			Vector<String> elems = new Vector<String>();
			for (String fieldName : fields.keySet()) {
				if (fields.get(fieldName).getType() == FieldType.EQUALS) {
					String str = fieldName + "="
							+ fields.get(fieldName).getEquals();
					if (fields.get(fieldName).isConfirmed())
						str = str + "(c)";
					elems.add(str);
				} else if (fields.get(fieldName).getExcludes().size() <= 2) {
					String str = fieldName + " x(";
					Vector<String> keys = fields.get(fieldName).getExcludes();
					if (fields.get(fieldName).getExcludes().size() > 0) {
						str += keys.get(0);
					}
					if (fields.get(fieldName).getExcludes().size() > 1) {
						str += "," + keys.get(1);
					}
					str += ")";
					elems.add(str);
				} else {
					elems.add(fieldName + " x(["
							+ fields.get(fieldName).getExcludes().size()
							+ " entries])");
				}
			}
			// elems.add("count=" + count);
			if (elems.size() > 0)
				s += elems.get(0);
			for (int i = 1; i < elems.size(); i++) {
				s += ";" + elems.get(i);
			}
		} else
			s = "(all)";
		return s;
	}

	@Override
	public String toString() {
		return this.getLocalName() + " " + __str__();
	}

	@Override
	public void setBelief(float _belief) {
		belief = _belief;
		if (GenericProvider.hasDataProperty(indi, GenericProvider.beliefProp,
				onto, factory)) {
			GenericProvider.removeFloatData(indi, GenericProvider
					.getFloatProperty(indi, GenericProvider.beliefProp, onto,
							factory), GenericProvider.beliefProp, onto,
					factory, manager);
		}
		GenericProvider.addFloatData(indi, (float) belief,
				GenericProvider.beliefProp, onto, factory, manager);
	}

	@Override
	public float getBelief() {
		return (float) belief;
	}

	public void setPrior(float _prior) {
		prior = _prior;
		if (GenericProvider.hasDataProperty(indi, GenericProvider.priorProp,
				onto, factory))
			GenericProvider.removeFloatData(indi, GenericProvider
					.getFloatProperty(indi, GenericProvider.priorProp, onto,
							factory), GenericProvider.priorProp, onto, factory,
					manager);
		GenericProvider.addFloatData(indi, (float) prior,
				GenericProvider.priorProp, onto, factory, manager);
	}

	public double getPrior() {
		return prior;
	}

	@Override
	public void setParent(IPartition _iParent) {
		if (parent == null && _iParent instanceof Partition) {
			parent = (Partition) _iParent;
			if (GenericProvider.hasObjectProperty(indi,
					GenericProvider.parentProp, onto, factory)) {
				GenericClass c = GenericProvider.getIndividual(indi,
						GenericProvider.parentProp, onto, factory, manager);
				GenericProvider.removeIndividual(indi, c.indi,
						GenericProvider.parentProp, onto, factory, manager);
			}
			GenericProvider.addIndividual(indi, ((Partition) _iParent).indi,
					GenericProvider.parentProp, onto, factory, manager);
		}
	}

	/**
	 * returns all OWLIndividual objects of the children of this partition or
	 * null if there are none
	 * 
	 * @return the OWLIndividual objects of all children
	 */
	public Vector<OWLIndividual> getChildrenIndis() {
		Vector<GenericClass> classes = new Vector<GenericClass>(
				GenericProvider.getIndividualColl(indi,
						GenericProvider.childrenProp, onto, factory, manager));
		Vector<OWLIndividual> indis = null;
		if (!classes.isEmpty()) {
			indis = new Vector<OWLIndividual>();
			for (GenericClass c : classes) {
				if (c.indi != null)
					indis.add(c.indi);
			}
		}
		return indis;
	}

	@Override
	public IPartition[] getChildren() {
		Partition[] a = new Partition[0];
		return children.toArray(a);
	}

	public void addChildrenObject(Vector<Partition> _children) {
		children.addAll(_children);
	}

	@Override
	public void addChild(IPartition _iChild) {
		if (children != null && _iChild != null && _iChild instanceof Partition) {
			Partition child = (Partition) _iChild;
			children.add(child);
			GenericProvider.addIndividual(indi, child.indi,
					GenericProvider.childrenProp, onto, factory, manager);
		}
	}

	@Override
	public void removeChild(IPartition _iChild) {
		// System.out.println("Partition.removeChild()");
		if (children != null && _iChild != null && _iChild instanceof Partition) {
			Partition child = (Partition) _iChild;
			children.remove(child);
			GenericProvider.removeIndividual(indi, child.indi,
					GenericProvider.childrenProp, onto, factory, manager);
		}
	}

	private Partition getRoot() {
		if (root == null) {
			Partition p = this;
			while (p.parent != null)
				p = p.parent;
			root = p;
		}
		return root;
	}

	/**
	 * calculates the total amount of different values for the fields.
	 * 
	 * @return a map containing for each field the total mount of different
	 *         values
	 */
	private Map<String, Integer> getFieldTotals() {
		if (fieldTotals == null) {
			IUserActionSupplementary supp = UserAction.getUserActionSupp(
					osFactory, variant);
			try {
				fieldTotals = supp.getFieldTotals();
			} catch (NoFactorySetException e) {
				System.err
						.println("UserActionSupp was created without associating a OSFactory object.");
				return null;
			}
		}	
		return fieldTotals;
	}

	public Map<String, Field> getFields() {
		return fields;
	}

	/*
	 * static methods start here
	 */

	/**
	 * creates an initial empty partition
	 */
	public static Partition[] Seed(OwlSpeakOntology osOnto) {
		Partition[] a = new Partition[1];
		OWLIndividual newIndi = osOnto.factory.factory
				.getOWLNamedIndividual(IRI.create(osOnto.factory.onto
						.getOntologyID().getOntologyIRI() + "#" + "test"));
		osOnto.factory.manager.applyChange(new AddAxiom(osOnto.factory.onto,
				osOnto.factory.factory.getOWLClassAssertionAxiom(
						osOnto.factory.factory.getOWLClass(IRI
								.create(Settings.uri + "#BeliefSpace")),
						newIndi)));
		a[0] = new Partition(newIndi, osOnto.factory.onto,
				osOnto.factory.factory, osOnto.factory.manager, osOnto.factory,
				osOnto.ontoHISvariant);
		return a;
	}

	/**
	 * Computes the prior probability of partition p by counting the total
	 * amount of different values the fields offer and takes this value to
	 * divide the number of possible values that remain by applying the EQUALS
	 * and EXCLUDES restrictions.
	 * 
	 * @param p
	 *            partition to compute the prior probability of
	 * @return the prior probability of partition p
	 */
	private static float computePrior(Partition p) {
		float result = 1.0f;
		long totalCount = 1;
		long count = 1;

		Map<String, Field> fields = p.fields;
		Map<String, Integer> totalFieldCounts = p.getFieldTotals();

		for (String fieldName : totalFieldCounts.keySet()) {
			totalCount *= totalFieldCounts.get(fieldName);
			Field field = fields.get(fieldName);
			if (field != null) {
				if (field.getType() == FieldType.EQUALS)
					count *= 1;
				else if (field.getType() == FieldType.EXCLUDES) {
					count *= totalFieldCounts.get(fieldName)
							- field.getExcludes().size();
				}
			} else {
				// This field neither excludes nor equals any value.
				// Therefore no restrictions apply and all values are possible.
				count *= totalFieldCounts.get(fieldName);
			}
			long gcd = computeGCD(count, totalCount);
			count /= gcd;
			totalCount /= gcd;
		}

		result = 1.0f * count / totalCount;
		assert(result>=0.f);
		return result;
	}
	
	private static long computeGCD(long x, long y) {
		if (x < 0 || y < 0)
			throw new IllegalArgumentException("one of the parameters is negative. GDC allows only for positive arguemnts.");
		while (x * y != 0) {
			if (x >= y)
				x = x % y;
			else
				y = y % x;
		}
		return (x + y);
	}

	public static PartitionState string2PartitionState(String stringData) {
		if (stringData == null)
			return null;
		return PartitionState.valueOf(stringData);
	}

	public static String partitionState2String(PartitionState state) {
		if (state == null)
			return null;
		return state.toString();
	}
	
	private IMean createNewMean() {
		int sw = 1;
		if (sw == 0)
			return new ArithmeticMean();
		else
			return new SquareMean();
	}
	
	private class SquareMean implements IMean {
		double sum = 0f;
		int numValues = 0;

		@Override
		public void addValue(double value) {
			sum += value;
			numValues++;
		}

		@Override
		public double getMean() {
			double result = sum/numValues;
			return result * result;
		}
	}
	
	private class ArithmeticMean implements IMean {
		double sum = 0f;
		int numValues = 0;
		
		@Override
		public void addValue(double value) {
			sum += value;
			numValues++;
		}

		@Override
		public double getMean() {
			return sum/numValues;
		}
		
	}
}
