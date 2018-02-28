package owlSpeak.engine;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The class that is used for evaluating the OwlScript expressions
 * 
 * @author Dan Denich
 * 
 */
public class OwlScript {

	/**
	 * the main method is only used for testing the functions
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Pattern p = Pattern.compile("(\\([\\w=+%\\&!\\|-]*\\))");
		// input="SET(dan=aga buh)";
		// input=REQUIRES ( ( ( ((%eggs%>=(%persons%**1)) &&
		// (%milk%>=(%persons%**100)) ) && ( ((%flour%>=(%persons%**100)) &&
		// (%sugar%>=(%persons%**100)) ) ) && (effort >=1 ));
		// input="IF (dan==dan) THEN (dan2=5)";
		// "(2**(1++(1++2))));(zeit=3)";
		// input="REQUIRES(3**2)";
		// parseBrackets("(1||0)");
		// System.out.println(getSets(gaga[1]));
		// Vector<String> test = getSets(gaga[1]);
		// String[] muh=parseSet(test.get(1));
		// System.out.println(muh[0]+muh[1]);

		 String input=
		 "REQUIRES (           (   ((2** (1++  (2==2)  )) &&  (2--1))         ||  (2==3)  ) )";
		 String[] gaga=evaluateString(input);
		 System.out.println(gaga[0]);
		 System.out.println(gaga[1]);

	}

	/**
	 * this function parses the input. it takes the string from the beginning to
	 * the first bracket as a command. it then calls the corresponding
	 * functions.
	 * 
	 * returned string might contain parentheses and semi-colons
	 * 
	 * @param input
	 *            the input string
	 * @return a string vector containing the command in field [0], the string
	 *         in [1] (and optional [2] if the command is "IF")
	 */
	public static String[] evaluateString(String input) {
		String[] ausgabe = new String[3];
		if (input != null) {
			String input2 = input.replace(" ", "");
			String command = input2.substring(0, input2.indexOf("("));
			input2 = input2.substring(input2.indexOf("("));
			ausgabe[0] = command;
			// System.out.println(input2);
			if (command.equalsIgnoreCase("REQUIRES")) {
				ausgabe[1] = parse(input2);
			} else if (command.equalsIgnoreCase("SET")) {
				ausgabe[1] = input2;
			} else if (command.equalsIgnoreCase("IF")) {
				ausgabe[1] = parse(input2.substring(0, input2.indexOf("THEN")));
				ausgabe[2] = input2.substring(input2.indexOf("THEN") + 4);
			}
		}
		return ausgabe;
	}

	/**
	 * this function is used to check if the string can be interpreted directly
	 * or if it is more complex and needs recursive calls
	 * 
	 * @param input
	 *            the input string
	 * @return returns the result of the interpreted string, usually 0 or 1
	 */
	public static String parse(String input) {
		String ergebnis;
		if (input.lastIndexOf("(") == input.indexOf("(")) {
			ergebnis = String.valueOf(validate(input));
			return ergebnis;
		}

		Vector<String> test = parseBrackets(input);
		String zwischen = Vect2Str(test);
		while (zwischen.lastIndexOf("(") != zwischen.indexOf("(")) {
			test = parseBrackets(zwischen);
			zwischen = Vect2Str(test);
		}
		zwischen = String.valueOf(validate(zwischen));
		ergebnis = zwischen;
		return ergebnis;
	}

	/**
	 * this function transforms a vector to a string by calling toString and
	 * removing spaces and commata
	 * 
	 * @param input
	 *            the input String vector
	 * @return the concated string
	 */
	public static String Vect2Str(Vector<String> input) {
		String dan2 = input.toString();
		// System.out.println(input);
		dan2 = dan2.substring(1, dan2.length() - 1);
		dan2 = dan2.replace(",", "");
		dan2 = dan2.replace(" ", "");
		return dan2;
	}

	/**
	 * this function is used for analyzing the string and the given brackets. it
	 * returns a vector of string that can be interpreted or recombined.
	 * 
	 * @param input
	 *            the input String
	 * @return a vector that contains split-parts of the inputstring
	 */
	public static Vector<String> parseBrackets(String input) {
		Vector<String> ausgabe = new Vector<String>();
		// Pattern p = Pattern.compile("(\\([\\w=+%\\&!\\|-]*\\))");
		Pattern p = Pattern
				.compile("(\\([\\w]*[(!=)|(==)|(\\&\\&)|(\\|\\|)|(--)|(++)|(\\*\\*)|(//)|(>=)|(<=)|(>>)|(<<)]+[\\w]*\\))");
		Matcher m = p.matcher(input);
		String[] muh = input
				.split("(\\([\\w]*[(!=)(==)(\\&\\&)(\\|\\|)(--)(++)(**)(//)(>=)(<=)(>>)(<<)]+[\\w]*\\))");
		int i = 0;
		ausgabe.add(muh[i]);
		i++;
		if (muh.length < 1)
			return ausgabe;
		while (m.find()) {
			String s = m.group();
			ausgabe.add(String.valueOf(validate(s)));
			ausgabe.add(muh[i]);
			i++;
		}
		return ausgabe;
	}

	/**
	 * this function is used for the logic parsing. it checks for expressions
	 * like (a++b) and returns a+b
	 * 
	 * @param input
	 *            the input String
	 * @return an integer that is either 0 or 1
	 */
	public static int validate(String input) {
		input = input.replace("(", "");
		input = input.replace(")", "");
		// System.out.println(input);
		Pattern p = Pattern
				.compile("(!=)|(==)|(\\&\\&)|(\\|\\|)|(--)|(\\+\\+)|(\\*\\*)|(//)|(>=)|(<=)|(>>)|(<<)");
		Matcher m = p.matcher(input);
		m.find();
		String[] muh = input.split(
				"[(!=)(==)(\\&\\&)(\\|\\|)(++)(--)(**)(//)(>=)(<=)(>>)(<<)]",
				-1);
		try {
			int a = Integer.valueOf(muh[0]);
			int b = Integer.valueOf(muh[2]);
			muh[1] = m.group();
			if (muh[1].equals("==")) {
				if (a == b)
					return 1;
			} else if (muh[1].equals("!=")) {
				if (a != b)
					return 1;
			} else if (muh[1].equals("&&")) {
				return ((a & b));
			} else if (muh[1].equals("||")) {
				return ((a | b));
			} else if (muh[1].equals("++")) {
				return ((a + b));
			} else if (muh[1].equals("--")) {
				return ((a - b));
			} else if (muh[1].equals("**")) {
				return ((a * b));
			} else if (muh[1].equals("//")) {
				return ((a / b));
			} else if (muh[1].equals("<<")) {
				if (a < b)
					return 1;
			} else if (muh[1].equals(">>")) {
				if (a > b)
					return 1;
			} else if (muh[1].equals("<=")) {
				if (a <= b)
					return 1;
			} else if (muh[1].equals(">=")) {
				if (a >= b)
					return 1;
			}
		} catch (NumberFormatException e) {
			String a = muh[0];
			String b = muh[2];
			muh[1] = m.group();
			if (muh[1].equals("==")) {
				if (a.equals(b))
					return 1;
			} else if (muh[1].equals("!=")) {
				if (!a.equals(b))
					return 1;
			}

		}

		return 0;
	}

	/**
	 * this function transforms an input string to a vector, taking ";" as
	 * separation character
	 * 
	 * @param input
	 *            the input String
	 * @return a vector containing the parts of the String (each string with a
	 *         leading opening and a trailing closing bracket)
	 */
	public static Vector<String> getSets(String input) {
		Vector<String> ausgabe = new Vector<String>();
		String[] sets = input.split(";");
		String temp;
		String temp2;
		String zwischen;
		for (int i = 0; i < sets.length; i++) {
			// System.out.println(sets[i]);
			temp = sets[i];
			if (temp.lastIndexOf("(") != temp.indexOf("(")) {
				temp2 = temp
						.substring(temp.indexOf("=") + 1, temp.length() - 1);
				// System.out.println(temp2);
				zwischen = parse(temp2);

				zwischen = temp.substring(0, temp.indexOf("=") + 1) + zwischen
						+ ")";

			} else
				zwischen = temp;

			ausgabe.add(zwischen);
		}

		return ausgabe;
	}

	/**
	 * this function splits the input string into an array with two elements,
	 * taking "=" as the removed separation character
	 * 
	 * @param input
	 *            the input String, may have opening and closing parentheses
	 * @return a String array with two fields, opening and closing parentheses
	 *         are discarded
	 */
	public static String[] parseSet(String input) {
		String[] temp = new String[2];
		temp[0] = input.substring(0, input.indexOf("="));
		temp[1] = input.substring(input.indexOf("=") + 1, input.length());
		String[] ausgabe = new String[2];
		String[] temp2 = new String[2];
		temp2[0] = temp[0].replace("(", "");
		ausgabe[0] = temp2[0].replace(")", "");
		temp2[1] = temp[1].replace("(", "");
		ausgabe[1] = temp2[1].replace(")", "");
		return ausgabe;

	}

	/**
	 * Splits a variable operator sequence at the ";" sign. Assumes that the
	 * variable operator is syntactically correct.
	 * 
	 * @param sequence
	 * @return
	 */
	public static Vector<String> parseOperatorSequence(String sequence) {
		Vector<String> result = new Vector<String>();
		String[] sets = sequence.split(";");
		int parenthesesOpen = 0;
		String tmp = "";

		for (int i = 0; i < sets.length; i++) {
			parenthesesOpen += countOccurances(sets[i], "(")
					- countOccurances(sets[i], ")");
			tmp += sets[i];
			if (parenthesesOpen == 0) {
				result.add(tmp);
				tmp = "";
			}
		}

		return result;
	}

	private static int countOccurances(String string, String pattern) {
		int count = 0;
		int steps = string.length() - pattern.length() + 1;
		for (int i = 0; i < steps; i++) {
			if (string.substring(i, pattern.length()).equalsIgnoreCase(pattern))
				count++;
		}
		return count;
	}
}
