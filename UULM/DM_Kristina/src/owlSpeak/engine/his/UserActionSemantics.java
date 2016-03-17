package owlSpeak.engine.his;

import owlSpeak.Move;
import owlSpeak.Semantic;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.his.FieldValue.ConfirmationInfo;

public class UserActionSemantics extends UserAction {

	public UserActionSemantics(Move value, String speak,
			OwlSpeakOntology onto, UserActionType type, String user) {
		super(value, speak, onto, UserActionSemanticsSupplementary
						.createUserActionSemanticsSupplementary(onto.factory), type, user);
	}

	@Override
	public void parseMove(Move value, String speak, OwlSpeakOntology onto, String user) {
		for (Semantic s : value.getSemantic()) {
			String field = supp.getFieldNameFromSemantic(s);
			if (field != null) {
				FieldValue v = new FieldValue(s.getLocalName());
				if (s.hasConfirmationInfo()) {
					v.setConfirmationInfo(s.getConfirmationInfo() ? ConfirmationInfo.CONFIRM
							: ConfirmationInfo.REJECT);
				}
				fields.put(field, v);
			}
		}
	}
}
