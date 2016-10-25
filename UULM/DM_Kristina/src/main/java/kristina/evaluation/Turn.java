package kristina.evaluation;

import java.util.regex.Pattern;

public class Turn {
	private String exception;
	private String dmInput;
	private String kiInput;
	private String kiOutput;
	private String dmOutput;
	private String json;
	private String valence;
	private String arousal;
	private String description;
	private String workspace;

	public Turn(String description, String exception, String dmInput,
			String kiInput, String kiOutput, String dmOutput, String json,
			String valence, String arousal, String workspace) {
		this.exception = exception;
		this.dmInput = dmInput;
		this.kiInput = kiInput;
		this.kiOutput = kiOutput;
		this.dmOutput = dmOutput;
		this.json = json;
		this.valence = valence;
		this.arousal = arousal;
		this.description = description;
		this.workspace = workspace;
	}

	public String getException() {
		return exception;
	}

	public String getDmInput() {
		return dmInput;
	}

	public String getKiInput() {
		return kiInput;
	}

	public String getKiOutput() {
		return kiOutput;
	}

	public String getDmOutput() {
		return dmOutput;
	}

	public String getJson() {
		return json;
	}

	public String getValence() {
		return valence;
	}

	public String getArousal() {
		return arousal;
	}
	
	public String getWorkspace(){
		return workspace;
	}

	public String toString() {
		String[] s = description.split("[" + Pattern.quote("[]") + "]");
		String result = s[0] + "\t ";
		for (int j = 1; j < s.length; j = j + 2) {
			String[] split = s[j].split(",");
			for (int i = 0; i < split.length; i++) {
				if (i != 0) {
					result = result + "\n\t";
				}
				if (split[i].length() > 100) {
					result = result + split[i].subSequence(0, 98) + "...";
				} else {
					result = result + split[i];
				}
			}
			if (j + 1 < s.length) {
				result = result + "\n" + s[j + 1] + "\t ";
			}
		}
		return result;
	}
}
