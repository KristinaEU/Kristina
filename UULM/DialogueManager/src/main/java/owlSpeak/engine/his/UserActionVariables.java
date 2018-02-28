package owlSpeak.engine.his;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import owlSpeak.Move;
import owlSpeak.Move.ExtractFieldMode;
import owlSpeak.Semantic;
import owlSpeak.Variable;
import owlSpeak.engine.OwlScript;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.engine.his.FieldValue.ConfirmationInfo;
import owlSpeak.servlet.grammar.IGrammar;

public class UserActionVariables extends UserAction {

	public UserActionVariables(Move value, String speak, OwlSpeakOntology onto,
			UserActionType type, String user) {
		super(value, speak, onto, UserActionVariablesSupplementary
				.createUserActionVariablesSupplementary(onto.factory), type, user);
	}

	/**
	 * pseudo code:
	 * 
	 * <pre>
	 * {@code
	 * evaluate owl script string 
	 * if move requests grammar analysis 
	 * 	create field name to grammar rule mapping
	 * 	parse speak with move grammar
	 * 	extract rules given by field name to rule mapping 
	 * 	set field names and values accordingly
	 * elseif move requests pseudo analysis
	 * 	split the speak string on white spaces
	 * 	each two split elements represent one fieldname/fieldvalue pair
	 * else
	 * 	set field names and field values accordingly
	 * endif
	 * }
	 * </pre>
	 * @param speak
	 * @param onto
	 * @param move
	 */
	@Override
	public void parseMove(Move move, String speak, OwlSpeakOntology onto, String user) {

		// deal with variable operator
		String[] varOpParse = OwlScript.evaluateString(move
				.getVariableOperator());

		
		if (move.hasExtractFieldValues()
				&& move.getExtractFieldValues() != ExtractFieldMode.NO) {

			switch (move.getExtractFieldValues()) {
			case GRAMMAR:
				
				if ("SET".equalsIgnoreCase(varOpParse[0])) {
					TreeMap<String,String> grammar2field = new TreeMap<String, String>();
					
					Vector<String> sets = owlSpeak.engine.OwlScript
							.getSets(varOpParse[1]);
					for (String setInstruction : sets) {
						String[] set = owlSpeak.engine.OwlScript
								.parseSet(setInstruction);

						Variable var = onto.factory.getVariable(set[0]);
						String fieldname = supp.getFieldNameFromVariable(var);
						String grammarName = set[1].replace(":", "");
						grammar2field.put(grammarName,fieldname);
					}
					IGrammar grammarObject = ServletEngine.getGrammarByType(Settings.usersetting[Settings.getuserpos(user)].grammarType);
										
					String[] grammars = grammarObject.grammarsOfMove(move, user, onto);
					String[] parse = grammarObject.parse(speak, grammars);
					Map<String,String> extractedParse = extractFromParse(parse);
					
					for (String grammarName : grammar2field.keySet()) {
						String fieldValue = extractedParse.get(grammarName);
						String fieldname = grammar2field.get(grammarName);
						if (!fields.containsKey(fieldname))
							fields.put(fieldname, new FieldValue(fieldValue));
						else
							fields.get(fieldname).setValue(fieldValue);
					}
				}
				
				break;
			case PSEUDO:

//				// normalize speak string: remove unnecessary spaces (double
//				// spaces)
				String cmpSpeak = speak;
				do {
					speak = cmpSpeak;
					cmpSpeak = speak.replace("  ", " ");
				} while (0 != speak.compareToIgnoreCase(cmpSpeak));
				
				speak = speak.trim();

				//prepare filtering for multi-slot input
				String[] splitsUnchecked = speak.split("( )+");
				String speakChecked ="";

				// filtering out the relevant parts in case of multi-slot grammar input
				for (int i=0; i < splitsUnchecked.length; i++) {
					//if String contains ':', it represents the move information, 
					//which can be discarded
					if(splitsUnchecked[i].contains(":"))
					{
						
					}
					// else it contains relevant semantic info and is therefore used
					else
					{
						splitsUnchecked[i] = splitsUnchecked[i].replace("}", "");
						splitsUnchecked[i] = splitsUnchecked[i].replace(";", "");
						speakChecked += " " + splitsUnchecked[i];
					}
				}
				
				// String consisting only of relevant info
				speakChecked = speakChecked.trim();
				String[] splits = speakChecked.split("( )+");
				// check if number of elements is even
				if (splits.length % 2 != 0)
					System.err.println("Speak string cannot be parsed.");
				
				for (int i=0; i < splits.length; i+=2) {
					String fieldname = splits[i];
					String fieldValue = splits[i+1];
					
					if (!fields.containsKey(fieldname))
						fields.put(fieldname, new FieldValue(fieldValue));
					else
						fields.get(fieldname).setValue(fieldValue);
				}
				

				break;
			default:
				System.err.println("Unknown extraction mode!!!");
				break;
			}

		} else {

			if ("SET".equalsIgnoreCase(varOpParse[0])) {
				Vector<String> sets = owlSpeak.engine.OwlScript
						.getSets(varOpParse[1]);
				for (String setInstruction : sets) {
					String[] set = owlSpeak.engine.OwlScript
							.parseSet(setInstruction);

					Variable var = onto.factory.getVariable(set[0]);
					String fieldname = supp.getFieldNameFromVariable(var);
					if (!fields.containsKey(fieldname))
						fields.put(fieldname, new FieldValue(set[1]));
					else
						fields.get(fieldname).setValue(set[1]);
				}
			}
		}

		// deal with confirmation semantics
		for (Semantic s : move.getSemantic()) {
			String fieldname = supp.getFieldNameFromSemantic(s);
			if (fieldname != null) {
				if (s.hasConfirmationInfo()) {
					ConfirmationInfo c = s.getConfirmationInfo() ? ConfirmationInfo.CONFIRM
							: ConfirmationInfo.REJECT;
					FieldValue v = new FieldValue(s.getLocalName());
					if (s.hasConfirmationInfo()) {
						v.setConfirmationInfo(s.getConfirmationInfo() ? ConfirmationInfo.CONFIRM
								: ConfirmationInfo.REJECT);
						if (!fields.containsKey(fieldname))
							fields.put(fieldname, v);
						else
							fields.get(fieldname).setConfirmationInfo(c);
					}
				}
			}
		}
	}
	
	private Map<String,String> extractFromParse(String[] parse) {
		return null;
	}
}
