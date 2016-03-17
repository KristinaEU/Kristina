package owlSpeak.util;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.semanticweb.owlapi.model.OWLLiteral;

public class StringTest {

	public static void main (String[] args){
		String Eingabe = "[(next song) next]";
		String test = "(next last skip)";
		System.out.println(Eingabe);
		System.out.println(test);	
		System.out.println(checkGrammar(Eingabe, test));
	}
	
	public static Vector <String> checkGrammar(String literal1, String literal2){		
		Vector<String> v1 = new Vector <String>();
		Vector<String> v2 = new Vector <String>();
		Vector<String> conflict = new Vector<String>();
		String cleanString1 = (((literal1.replaceAll("\\s*\\)\\s*", ")")).replaceAll("\\s*\\(\\s*", "(")).replaceAll("\\s*\\[\\s*", "[")).replaceAll("\\s*\\]\\s*", "]");		
		String cleanString2 = (((literal2.replaceAll("\\s*\\)\\s*", ")")).replaceAll("\\s*\\(\\s*", "(")).replaceAll("\\s*\\[\\s*", "[")).replaceAll("\\s*\\]\\s*", "]");
		Pattern pattern1 = null;
		Pattern pattern2 = null;
		
		if(cleanString1.startsWith("[")){
			pattern1 = Pattern.compile("(\\(*([a-z]+\\s*)*[a-z]*\\)*)*");
			pattern2 = Pattern.compile("(\\[*([a-z]+\\s*)*[a-z]*\\]*)*");
		}
		else{
			pattern1 = Pattern.compile("(\\[*([a-z]+\\s*)*[a-z]*\\]*)*");
			pattern2 = Pattern.compile("(\\(*([a-z]+\\s*)*[a-z]*\\)*)*");
		}
		
		Matcher matcher1_1 = pattern1.matcher(cleanString1.toLowerCase());
		Matcher matcher1_2 = pattern1.matcher(cleanString2.toLowerCase());
		
		while (matcher1_1.find()){
			String temp = matcher1_1.group();		
			if(!temp.isEmpty()){
				Matcher matcher2_1 = pattern2.matcher(temp);
				while (matcher2_1.find()){
					String temp2 = matcher2_1.group(); 
					if(!temp2.isEmpty()){
						v1.add(temp2);
					}
				}
			}
		}
		
		while (matcher1_2.find()){
			String temp = matcher1_2.group();		
			if(!temp.isEmpty()){
				Matcher matcher2_2 = pattern2.matcher(temp);
				while (matcher2_2.find()){
					String temp2 = matcher2_2.group(); 
					if(!temp2.isEmpty()){
						v2.add(temp2);
					}
				}
			}
		}
				
		for (int k=0; k<v1.size(); k++){
			if(v2.contains(v1.get(k))){
				conflict.add(v1.get(k));
			}
		}
		return conflict;		
	}
	
	public static Vector <String> checkGrammar(Iterator<OWLLiteral> literal1, Iterator<OWLLiteral> literal2){		
		String string1 = "";
		while(literal1.hasNext()){
			string1=string1+(literal1.next().getLiteral());
		}
		String string2 = "";
		while(literal2.hasNext()){
			string2=string2+(literal2.next().getLiteral());
		}
	
		Vector<String> v1 = new Vector <String>();
		Vector<String> v2 = new Vector <String>();
		Vector<String> conflict = new Vector<String>();
		String cleanString1 = (((string1.replaceAll("\\s*\\)\\s*", ")")).replaceAll("\\s*\\(\\s*", "(")).replaceAll("\\s*\\[\\s*", "[")).replaceAll("\\s*\\]\\s*", "]");		
		String cleanString2 = (((string2.replaceAll("\\s*\\)\\s*", ")")).replaceAll("\\s*\\(\\s*", "(")).replaceAll("\\s*\\[\\s*", "[")).replaceAll("\\s*\\]\\s*", "]");
		Pattern pattern1 = null;
		Pattern pattern2 = null;
		
		if(cleanString1.startsWith("[")){
			pattern1 = Pattern.compile("(\\(*([a-z]+\\s*)*[a-z]*\\)*)*");
			pattern2 = Pattern.compile("(\\[*([a-z]+\\s*)*[a-z]*\\]*)*");
		}
		else{
			pattern1 = Pattern.compile("(\\[*([a-z]+\\s*)*[a-z]*\\]*)*");
			pattern2 = Pattern.compile("(\\(*([a-z]+\\s*)*[a-z]*\\)*)*");
		}
		
		Matcher matcher1_1 = pattern1.matcher(cleanString1.toLowerCase());
		Matcher matcher1_2 = pattern1.matcher(cleanString2.toLowerCase());
		
		while (matcher1_1.find()){
			String temp = matcher1_1.group();		
			if(!temp.isEmpty()){
				Matcher matcher2_1 = pattern2.matcher(temp);
				while (matcher2_1.find()){
					String temp2 = matcher2_1.group(); 
					if(!temp2.isEmpty()){
						v1.add(temp2);
					}
				}
			}
		}
		
		while (matcher1_2.find()){
			String temp = matcher1_2.group();		
			if(!temp.isEmpty()){
				Matcher matcher2_2 = pattern2.matcher(temp);
				while (matcher2_2.find()){
					String temp2 = matcher2_2.group(); 
					if(!temp2.isEmpty()){
						v2.add(temp2);
					}
				}
			}
		}
				
		for (int k=0; k<v1.size(); k++){
			if(v2.contains(v1.get(k))){
				conflict.add(v1.get(k));
			}
		}
		return conflict;		
	}
	
	public static Vector <String> checkGRXML(Vector<OWLLiteral> eins, Vector<OWLLiteral> zwei){
		Vector<String> Ausgabe = new Vector<String>();
		for (int i=0; i<eins.size(); i++){
			if(zwei.contains(eins.get(i))) Ausgabe.add(eins.get(i).getLiteral());
		}
		return Ausgabe;
	}
}
