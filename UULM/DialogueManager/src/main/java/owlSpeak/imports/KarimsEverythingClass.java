package owlSpeak.imports;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import owlSpeak.Grammar;
import owlSpeak.OSFactory;
import owlSpeak.SemanticGroup;
import owlSpeak.Utterance;
import owlSpeak.engine.OwlSpeakOntology;
import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
//TODO check folders of files to be created
public class KarimsEverythingClass {
	//Method 1
	//This method writes a string in certain file. The input string is the first argument 
	// and the file path is the second argument
	// if the file does not exist, this method also creates the file then writes in it
	// example: writeFile("I am ready","C:folder_name\\file_name.txt");
	public static void writeFile(String copyText,String file_path) throws Exception {
		 
		String newLine = System.getProperty("line.separator");
	    Writer output = null;
	    File file = new File(file_path);
	    if(!file.exists())
	    file.createNewFile();
	    output = new BufferedWriter(new FileWriter(file, true));
	    output.write(copyText);
	    output.write(newLine);
	    output.close();		
																					}
	
	//Method 2
	// The input of this method is string. This string is each line in the let's go grammar file
	// Some modifications are performed on each line and the output is a modified string
	// This modified string is written in intermediate file using writeFile (method 1)
	public static String convert_capital (String line)
	{
		String Final_line=""; // ex.  # phrases used to introduce utterances, <pre_assertion>,  (<what_i_say_is>)# %%0.5%%
		StringBuilder out = new StringBuilder();
		
		//in each line words seprated by space are assigned to sentence
	    for (String sentence : line.split(" *\\s")) { // ex: line="# Let's Go!! Bus Information System Grammar"
	        if (!sentence.equals("")) { // ex(cont): loop1: sentence="#" loop2:sentence="Let's" and so on
	            sentence += " ";
	            
	            //This condition for three special cases (NEIGHBORHOOD) (PLACE) 
	            // (AMBIGUOUS_PLACE) (UNCOVERED_NEIGHBORHOOD)
	            if (sentence.matches("\\([A-Z_\\s]+\\)\\s")) //(VERY_MUCH)--> (<VERY_MUCH>)
	            {	//System.out.println("Rule_5");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.trim();
	            	sentence ="("+"<"+sentence+">"+")"+" ";//(NEIGHBORHOOD)-->(<NEIGHBORHOOD>)
	            }
	            
	          //This condition for capital letters at the beginning followed by space
	            // ex. (I_D_LIKE_TO TAKE) & (I_AM looking for)
	            //This condition matched (I_D_LIKE_TO & (I_AM & (PLACE & (AMBIGUOUS_PLACE & (I_D_LIKE
	            if (sentence.matches("\\([A-Z_\\s]+\\s"))
	            {	//System.out.println("Rule_6");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.trim();
	            	sentence ="("+"<"+sentence+">"+" "; //(I_AM -->(<I_AM>
	            }
	            
	            if (sentence.matches("\\(\\*[A-Z_]+\\)\\s")) //(*VERY_MUCH)
	            {	//System.out.println("Rule_7");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="("+"["+"<"+sentence+">"+"]"+")"+" ";
	            }
	            
	            
	          //This condition for VARIABLES preceded and followed by a space
	            //and also for VARIABLES in definition
	            //ex. I_D_LIKE_TO_TAKE 
	            else  if (sentence.matches("[A-Z_\\s]+")) //ex. (i VERY_MUCH u)
	            {//	System.out.println("Rule_1");
	            	sentence=sentence.trim();
	            	sentence ="<"+sentence+">"+" "; //I_D_LIKE_TO_TAKE--><I_D_LIKE_TO_TAKE>
	            }
	            
	            
	            // This condition for OPTIONAL VARIABLES in the middle of a sentence
	            //ex. *FOR_TIME in (*[_lets] HOLD_ON *FOR_TIME *[_courtesy])
	            else if(sentence.matches("[\\*A-Z_\\s]+")) //(thank you *VERY_MUCH berjo)
	            {
	            	//System.out.println("Rule_2");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="["+"<"+sentence+">"+"]"+" ";//*FOR_TIME --> [<FOR_TIME>]
	            }
	            
	            
	            //This condition is for OPTIONAL VARIABLES at the beginning of a sentence
	            //which also means for OPTIONAL VARIABLES preceded by a bracket
	            //ex.(*I_D_LIKE_TO in (*I_D_LIKE_TO modify *THE QUERY *again)
	            else if (sentence.matches("\\(\\*[A-Z_]+\\s")) //(*VERY_MUCH berjo) FINAL_WORKING
	            {
	            	//System.out.println("Rule_3");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.trim();
	            	sentence ="("+"["+"<"+sentence+">"+"]"+" "; //(*I_D_LIKE_TO --> ([<I_D_LIKE_TO>]
	            }
	            
	            
	            //This condition is for OPTIONAL VARIABLES at the end of a sentence
	            //which also means for OPTIONAL VARIABLES followed by a bracket
	            //ex.*RUN) in (*[pre_question] *IS_THERE *a *bus before that *BUS)
	            else if (sentence.matches("\\*[A-Z_\\s]+\\){1}\\s"))   //(I am *VERY_MUCH) FINAL_WORKING
	            {
	            	//System.out.println("rule_4");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.trim();
	            	sentence ="["+"<"+sentence+">"+"]"+")"+" ";//*RUN) --> [<RUN>])																						
	            }
	            
	            
	            //This condition is for VARIABLES at the end of a sentence
	            // Note that the difference between this rule and the previous one is 
	            // the asterisk 
	            // ex. DOES) in (when DOES)
	            else if (sentence.matches("[A-Z_\\s]+\\){1}\\s"))   // VERY_MUCH)
	            {
	            //	System.out.println("rule_8");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.trim();
	            	sentence ="<"+sentence+">"+")"+" ";	// DOES) --> <DOES>)																							
	            }
	            
	            //This condition is for OPTIONAL TERMINALS at the end of a sentence
	            // ex. *from) in (*I_D_LIKE_TO LEAVE *from)
	            else if (sentence.matches("\\*[a-z_\\s]+\\){1}\\s"))   //(   *it) 
	            {
	            //	System.out.println("rule_9_small_1");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.trim();
	            	sentence ="["+sentence+"]"+")"+" ";	// *from)--> [from])																							
	            }
	            
	            
	            //This condition is for OPTIONAL TERMINALS followed and preceded by space
	            // ex. *the in (*[pre_question] *WHEN_DOES *the previous *BUS *RUN)
	            else if(sentence.matches("\\*[a-z_\\s]+"))  //( *it ) 
	            {
	            	//System.out.println("rule_10_small_2");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.trim();
	            	sentence ="["+sentence+"]"+" ";	//*the --> [the]																							
	            }
	            
	            
	            //This condition is for OPTIONAL TERMINALS followed by space
	            //and preceded by bracket
	            //ex. (*the in (*the next)
	            else if (sentence.matches("\\(\\*[a-z_']+\\s")) //(*it is berjo) FINAL_WORKING
	            {
	            	//System.out.println("Rule_11_small_3");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.trim();
	            	sentence ="("+"["+sentence+"]"+" "; //(*the --> ([the] 
	            }
	            
	          //This condition is for OPTIONAL TERMINALS enclosed by brackets (if they exist)
	            else if (sentence.matches("\\(\\*[a-z_]+\\)\\s")) //for ex. (*it)
	            {	//System.out.println("Rule_12_small_4");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="("+"["+sentence+"]"+")"+" ";//(*it)--> ([it])
	            }
	            
	            
	          //This condition is for unspoken slots defined inside a slot
	            //ex. ([route]) 
	            else if (sentence.matches("\\(\\[[a-z_]+\\]\\)\\s")) //([ambiguous_covered_place])
	            {		//System.out.println("Rule_13_category_backets");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.trim(); //([route])--> (<route>)
	           // 	sentence =sentence+"   ";
	            }

	            
	            //This condition is for OPTIONAL unspoken slots defined inside a slot (if it exists)
	            //this is an imaginary condition which has not matched any line in let's go
	            else if (sentence.matches("\\(\\*\\[[a-z_]+\\]\\)\\s")) //(*[ambiguous_covered_place])
	            {	//System.out.println("Rule_14_category_*optional_brackets");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.replace(")","");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="("+"["+sentence+"]"+")";
	            }
	            
	            
	            //This condition is for unspoken slots at the beginning of a sentence
	            //ex. ([registered_stop] in ([registered_stop] *IN_NEIGHBORHOOD)
	            else if (sentence.matches("\\(\\[[a-z_]+\\]\\s")) //([ambiguous_covered_place]   )
	            {	//System.out.println("Rule_15_category_backets");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.trim();
	            	sentence =sentence+" "; 	//([registered_stop]--> (<registered_stop>
	            }
	            
	            
	            //This condition is for OPTIONAL unspoken slots at the beginning of a sentence
	            //ex. (*[_that_is] in (*[_that_is] NO *GOOD)
	            else if (sentence.matches("\\(\\*\\[[a-z_]+\\]\\s")) //(*[ambiguous_covered_place] is the place)
	            {	//System.out.println("Rule_16_category_*optional_brackets");
	            	sentence=sentence.replace("(","");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="("+"["+sentence+"]"+" "; //(*[_that_is] --> ([<_that_is>]
	            }
	            
	            //This condition is for unspoken slots at the end of sentence
	            //ex. [stop_name]) in (*[pre_assertion] *my DESTINATION *is [stop_name])
	            else if (sentence.matches("\\[[a-z_]+\\]\\)\\s")) //(i love [ambiguous_covered_place])
	            {	//System.out.println("Rule_17_category_backets");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.trim();
	            	sentence =sentence+""; // [stop_name]) --> <stop_name>)
	            }
	            
	            //This condition is for OPTIONAL unspoken slots at the end of a sentence
	            //ex. *[day_time]) in (*[pre_assertion] from [stop_name] *[day_time])
	            else if (sentence.matches("\\*\\[[a-z_]+\\]\\)\\s")) //(i love *[ambiguous_covered_place])
	            {	//System.out.println("Rule_18_category_*optional_brackets");
	            sentence=sentence.replace(")","");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="["+sentence+"]"+")"; //*[day_time])--> [<day_time>])
	            }
	            
	            
	            //This condition is for unspoken slots in the middle of a sentence
	            // and for the definition of unspoken slots as well
	            //ex. [ambiguous_uncovered_place]
	            else if (sentence.matches("\\[[a-z_]+\\]\\s")) //(i love [ambiguous_covered_place] because) + [vague_place]
	            {//	System.out.println("Rule_19_category_backets");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.trim();
	            	sentence =sentence+" "; //[ambiguous_uncovered_place] --> <ambiguous_uncovered_place>
	            }
	            else if (sentence.matches("\\[[a-z0-9_]+\\]\\s")) //rule only for [0_uncovred_route]
	            {
	            	//System.out.println("NEW_RULE");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.trim();
	            	sentence =sentence+" "; //[0_uncovred_route]--><0_uncovred_route>
	            }
	            
	          //This condition is for OPTIONAL unspoken slots in the middle of a sentence
	            else if (sentence.matches("\\*\\[[a-z_]+\\]\\s")) //(i love *[ambiguous_covered_place] because)
	            {	//System.out.println("Rule_20_category_*optional_backets");
	            	sentence=sentence.replace("[","<");
	            	sentence=sentence.replace("]",">");
	            	sentence=sentence.replace("*","");
	            	sentence=sentence.trim();
	            	sentence ="["+sentence+"]"+" "; //ex. *[ambiguous_covered_place] --> [<ambiguous_covered_place>]
	            }

	            
	            out.append(sentence.charAt(0)
	                    + sentence.substring(1));
	          
	            
	        }
	    }
	
			    Final_line=out.toString().trim();
			    if (Final_line.contains("#"))
			    {
			    	char final_char_of_finalline=Final_line.charAt(Final_line.length()-1);	// to know whether it is a weight # %%0.75%% or comment # berjo
			    	Final_line=Final_line.replaceFirst("#", "/*");
			    	if (final_char_of_finalline!='%')
				    	Final_line=Final_line+"*/";
			    	else
			    	Final_line=Final_line+"*/";
			    }
				return Final_line;
				
			}
	
	
	
	
	
	
	
	public static void main (String[] argv) throws Exception {
        System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
		final ServletEngine owlEngine = new ServletEngine();
		String uriSave = Settings.uri;
		Settings.uri = "http://localhost:8080/OwlSpeakOnto.owl";
		String filename = "test.owl";
		String path = Settings.homePath;
		
		OSFactory factory = null;
		try {
			factory = OwlSpeakOntology.createOSFactoryEmptyOnto(filename, path);
//			factory = createOSFactory("test.owl", "c:/OwlSpeak/", "OwlSpeakOnto.owl");
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	
		
		
		
		
		// PART 1--> Creating the intermediate file by using the method convert_capital
		 //Location of the original file with let's go syntax
		 File file = new File("D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\original_letsgo_file.txt");
		 Scanner berjo = new Scanner(file); // new Scanner object called berjo to read the file
		 String print="";
		 boolean no_more_questions=false;
		 
		 while (berjo.hasNextLine()) { 
				
				String line = berjo.nextLine(); 
				if (line.trim().isEmpty() && no_more_questions==false)
				{
					no_more_questions=true;
				}
				print=convert_capital(line); 
				//location of the intermediate file
				writeFile(print,"D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\Italy_intermediate_file.txt");
			//	System.out.println(print); //OUTPUT
				

		 }
		 berjo.close();
		 
		 Grammar g3 = factory.createGrammar("unspoken_rules");	 
		//PART 2
		// By the end of Part 2, we will have 40 files, Each file for SPOKEN slot. (since we have 40 SPOKEN slots)
		// These files will be located at:
		//"C:\\Users\\Ahmed Ezzat\\Desktop\\Italy_lets_go\\Italy_jsgf_spoken_rules\\public_question_rules"+"_"+question_2+".txt"
		// where each file will be named as \\public_question_rules"+"_"+question_2+".txt"
		// ex. name of file: public_question_rules_0_BusNumber 
		 
		// We will also have ONE file for unspoken slots which will be located at:
		//"C:\\Users\\Ahmed Ezzat\\Desktop\\Italy_lets_go\\Italy_jsgf_unspoken_temp.txt"
		//However, this file is syntactically correct but it is semantically wrong.
		// That's why it will be used in part 3
		// TODO Auto-generated method stub
		 //	File file2 = new File("C:\\Users\\Ahmed Ezzat\\Desktop\\lets_go\\intermediate_file.txt"); //Working
		 	File file2 = new File("D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\Italy_intermediate_file.txt");
			Scanner berjo2 = new Scanner(file2); //Creating new Scanner object named berjo2 to read file2(Intermediate file)
			String rule="";
			String line2="";
			StringBuffer sb = new StringBuffer(rule);
			int type = -1; //0=public SPOKEN slot 1=public UNSPOKEN slot
			char first_char_QUESTION=' ';
			char last_char=' ';
			char before_last_char=' ';
			char last_check2=' '; // for the final check
			char b4_last=' '; // for the final check
			char b4_b4_last=' '; // for the final check
			int file2_slot_number=0;
			String question_2="";
			String question_2_copy="";
			
			boolean flag_first=true;
			String spoken ="";
			while (berjo2.hasNextLine()) {
				
				line2=berjo2.nextLine();
			
					
				if (line2.trim().isEmpty()==false) //We keep appending consecutive lines, as long as we are not reading an empty line
					{
					rule = sb.append(line2).toString();
					
					}
				//ex. rule= <I_D_LIKE_TO>(i'd like to)(i would like to)(i want to)(i wanna)(i need to)(i have to);
				//ex. rule= [DontKnow]([i] don't know)([i] dunno)(that's what i want to know)(that's what i'm asking [you])([it] doesn't matter);
				else
					{
					char first_char=rule.charAt(0); 
					if(first_char=='[') //ex. [4_BusAfterThatRequest] Condition to find public SPOKEN slots
					type=0;
					else if (first_char=='<' && StringUtils.substringBetween(rule, "<", ">").matches("[a-z0-9_]+")) //ex. <i_want> Condition to find public UNSPOKEN slots
					type=1;
						
						if (type==0) // ex. [DontKnow]([i] don't know)([i] dunno)(that's what i want to know)(that's what i'm asking [you])([it] doesn't matter);
						{	
							first_char_QUESTION=rule.charAt(0);
							if(first_char_QUESTION=='[') 
							{
								
								rule=rule.replaceFirst("\\(", " = "); // [DontKnow] = [i] don't know)([i] dunno)(that's what i want to know)(that's what i'm asking [you])([it] doesn't matter);
								rule=rule.replaceAll("\\)", ""); // [DontKnow] = [i] don't know([i] dunno(that's what i want to know(that's what i'm asking [you]([it] doesn't matter;
								rule=rule.replaceAll("\\(", " | "); // [DontKnow] = [i] don't know | [i] dunno | that's what i want to know | that's what i'm asking [you] | [it] doesn't matter;
								rule="public "+ rule; // public [DontKnow]=[i] don't know | [i] dunno | that's what i want to know | that's what i'm asking [you] | [it] doesn't matter;
							    question_2=StringUtils.substringBetween(rule, "[", "]");
								rule=rule.replaceFirst("\\[","<");
								rule=rule.replaceFirst("]",">"); //public <DontKnow>=[i] don't know | [i] dunno | that's what i want to know | that's what i'm asking [you] | [it] doesn't matter;
								
								if (rule.contains("<s>")) //special case for the [Generic] slot
								{
									rule=rule.replaceAll("<s>\\s","");
									rule=rule.replaceAll("</s>\\s","");
								}
								
								//to make sure that at the end of the line there is ;
								char final_char_2=rule.charAt(rule.length()-1); //in JSGF at the end of each line there must be ; 
								if (final_char_2==';' && type==0)
									rule=rule;
								else if (final_char_2!=';' && type==0)
									rule=rule+";";	
								
								//checking
						      	last_char=rule.charAt(rule.length()-1);
								before_last_char=rule.charAt(rule.length()-2);
								b4_b4_last=rule.charAt(rule.length()-3);
								if(last_char==';' && before_last_char==';') // when u find ;;--> this is the end of a slot
							      rule = rule.substring(0, rule.length() - 1) + "";
								//rule = rule.substring(0, rule.length() - 1) + "/*End of Slot*/";
								else if (last_char==';' && before_last_char=='/') // to avoid puuting */; becuase this leads to an error in jsgf
									rule = rule.substring(0, rule.length() - 1); // removes the ; after */
					
								last_check2=rule.charAt(rule.length()-1);
								b4_last=rule.charAt(rule.length()-2);
								b4_b4_last=rule.charAt(rule.length()-3);
								if (last_check2=='/' && b4_last=='*' && b4_b4_last=='%') // for that case, %*/ at the end we need ;
									rule=rule+" ;";
										
						        /// System.out.println("SPOKEN:"+rule); //rule-->public <Generic13>=[GoodEnough]; public <Generic14>=[No];file2_slot_number+question_2+"_"+".txt");
						         //writeFile(rule,"D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\public_question_rules"+"_"+question_2+".txt");	
						        if (flag_first==true)  
						        { 
						        flag_first=false;
						        spoken=spoken+rule+"\n";
						        question_2_copy=question_2;
						        }
						        else
						        {
						        	Grammar g1 = factory.createGrammar("public_question_rules"+"_"+question_2_copy);
						        	
									spoken=spoken.replaceAll("public ", "");

							 		g1.addGrammarString(factory.factory.getOWLLiteral(spoken,"en"));
							 		g1.addGeneralGrammar(g3);
							 		
							 		
							 		spoken="";
							 		spoken=rule+"\n";
							 		question_2_copy=question_2;
						        }
							}	
							
							//Note: This will cover only the VARIABLES of spoken slots while the VARIABLES of unspoken slots will not be modified here
							else //for VARIABLES under SPOKEN slots ex. <I_D_LIKE_TO>(i'd like to)(i would like to)(i want to)(i wanna)(i need to)(i have to);
							{	
								rule=rule.replaceFirst("\\(", " = "); // <I_D_LIKE_TO> = i'd like to)(i would like to)(i want to)(i wanna)(i need to)(i have to);
								rule=rule.replaceAll("\\)", ""); // <I_D_LIKE_TO> = i'd like to(i would like to(i want to(i wanna(i need to(i have to;
								rule=rule.replaceAll("\\(", " | "); //<I_D_LIKE_TO> = i'd like to | i would like to | i want to | i wanna | i need to | i have to;
								rule=rule+";";
								
								//checking stage
								last_char=rule.charAt(rule.length()-1);
								before_last_char=rule.charAt(rule.length()-2);
								b4_b4_last=rule.charAt(rule.length()-3);
								if(last_char==';' && before_last_char==';')
									rule = rule.substring(0, rule.length() - 1) + "";
								//rule = rule.substring(0, rule.length() - 1) + "/*End of Slot*/";
								else if (last_char==';' && before_last_char=='/')
									rule = rule.substring(0, rule.length() - 1);							
	
								last_check2=rule.charAt(rule.length()-1);
								b4_last=rule.charAt(rule.length()-2);
								b4_b4_last=rule.charAt(rule.length()-3);
								if (last_check2=='/' && b4_last=='*' && b4_b4_last=='%') // for that case %*/ at the emd we need ;
									rule=rule+" ;";
								if (rule.contains("/*##############"))
									rule="";
							
				    			///System.out.println("VARIABLES_OF_SPOKEN:"+rule);
				    			
								 spoken=spoken+rule+"\n";
							}	
						}
						
						// type_1 for the public unspoken ex. public <day_time> and their variables ex.<TIME> <DAY>	
						else if (type==1)
						{	
								rule=rule.replaceFirst("\\(", " = ");
								rule=rule.replaceAll("\\)", "");
								rule=rule.replaceAll("\\(", " | ");
								char first_char_VARIABLE=rule.charAt(0);
								if (first_char_VARIABLE=='<' && StringUtils.substringBetween(rule, "<", ">").matches("[a-z0-9_]+"))
									rule="public "+ rule+";";
								else 
									rule=rule+";";	
								last_char=rule.charAt(rule.length()-1);
								before_last_char=rule.charAt(rule.length()-2);
								if(last_char==';' && before_last_char==';')
								//	rule = rule.substring(0, rule.length() - 1) + ""; 
								rule = rule.substring(0, rule.length() - 1) + "End of Slot"; 
								else if (last_char==';' && before_last_char=='/')
									rule = rule.substring(0, rule.length() - 1);
								
								last_check2=rule.charAt(rule.length()-1);
								b4_last=rule.charAt(rule.length()-2);
								b4_b4_last=rule.charAt(rule.length()-3);
								if (last_check2=='/' && b4_last=='*' && b4_b4_last=='%') // for that case %*/ at the emd we need ;
									rule=rule+" ;";
							//	writeFile("BERJO","/Users/Ahmed Ezzat/Desktop/lets_go/BAGAGA.txt");
								writeFile(rule,"D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\Italy_jsgf_unspoken_temp.txt");
						}
						sb.setLength(0); // to empty the buffer
						rule="";
					}
			//C:\Users\\Ahmed Ezzat\\Desktop\\lets_go\\
											}
			
			Grammar g1 = factory.createGrammar("public_question_rules"+"_"+question_2);
			spoken=spoken.replaceAll("public ", "");

	 		g1.addGrammarString(factory.factory.getOWLLiteral(spoken,"en"));
	 		g1.addGeneralGrammar(g3);
	 		
	 		
			berjo2.close(); // u must close the scanner to be able to delete the file
			boolean flag=file2.delete();
			//System.out.print("NOAM"+flag);
			
			
			
		    // PART 3--> Now, We will take the file "Italy_jsgf_unspoken_temp.txt" and we will
			//resolve the problem of having rules with the same name. for ex. <AT> and <AT> in the same file.. solution <AT>--> <AT_1>			
			//note: this is the file generated by part 2 which is called "Italy_jsgf_unspoken_temp.txt"
			///location of file to be read: 
			File file_3 = new File("D:\\workspace\\OwlSpeak\\src\\owlSpeak\\imports\\Italy_jsgf_unspoken_temp.txt"); //location of file to be read
			Scanner scan_file = new Scanner(file_3);
			ArrayList<String> slot = new ArrayList<String>(); //arraylist_1 of the containing all the rules (public and unpublic) in a single slot
			ArrayList<String> capital_rule_names = new ArrayList<String>(); //arraylist_2 cotaining rule_names like <TIME> <AROUND> <AT>; ONLY the names
			String line_3="";
			String private_rule_name=""; // <TIME> <AROUND> (NOTE: only the name, not the whole sentence)
			String rule_under_test="";
			int rule_new_number=0;
			int counter_unspoken=0;
			String temp_unspoken="";

			while (scan_file.hasNextLine())	
			{
				while(scan_file.hasNextLine())	
				{
					line_3=scan_file.nextLine();
					slot.add(line_3); // adding rules to the arraylist_1 
					//ex. public <date_time> = [<TIME>] <DAY> | [<DAY>] <TIME>;
					//ex. <AT> = after | before | at | by;
		
					
					if (line_3.contains("=") && line_3.contains("public")==false) // to exclude the public rules--> <AT> = after | before | at | by;
					{
						private_rule_name=line_3.substring(0,line_3.indexOf("=")-1); // private_rule_name= <AT>
						rule_under_test=private_rule_name; // rule_under_test = <AT>
						
						if (capital_rule_names.contains(rule_under_test)==false) //if arraylist_2 doesn't contain <AT>
						{
							capital_rule_names.add(rule_under_test); // add <AT> (only the name) to arraylist_2
						}
						else if (capital_rule_names.contains(rule_under_test)==true) // now we have a repeated rule (ex. <AT> for the 2nd time)
						{
							rule_new_number++; //the number which will be concatenated beside the rule.. ex. <AT>--> <AT1>
							String ONLY_name = StringUtils.substringBetween(rule_under_test, "<", ">"); //<AT>--> ONLY_name= AT
							
							//loop to change the <AT_1> in the other rules.
							//ex. if <TIME> = [<AT>] [<AROUND>] <time_range> | <time_range> ;
							// so we also need to change this [<AT>] to [<AT_1>]
							for (int i = 0; i < slot.size(); i++)
							{
								line_3=slot.get(i); // to get the rules from arraylist_1 ex.line3= <TIME> = [<AT>] [<AROUND>] <time_range> | <time_range> ;
								//rule_undertest=<AT>, line3=> <TIME> = [<AT>] [<AROUND>] <time_range> | <time_range> ;
								if (line_3.contains(rule_under_test)) //ex. true
								{
								line_3=line_3.replaceAll(rule_under_test,"<"+ONLY_name+"_"+rule_new_number+">" ); //line3=> <TIME> = [<AT_1>] [<AROUND>] <time_range> | <time_range> ;
								slot.set(i,line_3); // to put line_3 after modification to arraylist_1
								}
							//slot.add(line_3);
							}
							
							capital_rule_names.add(rule_under_test); //add <AT> (only the name) to arraylist_2
						}
						
					}
					
					if (line_3.contains("End of Slot")==true)
					{
						break;
					}
					
				}
				for (int i = 0; i < slot.size(); i++) { 
					
					temp_unspoken=temp_unspoken+slot.get(i);
					temp_unspoken=temp_unspoken+"\n";
					
					
					
					
					
						  }
				slot.clear();
				
				
				try {
					factory.manager.saveOntology(factory.onto,factory.manager.getOntologyDocumentIRI(factory.onto));
//					factory = OwlSpeakOntology.loadOSFactory(filename, path);
				} catch (OWLOntologyStorageException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				} 
				
				
		 		
			}
			
			temp_unspoken=temp_unspoken.replaceAll("End of Slot", "");
			temp_unspoken=temp_unspoken.replaceAll("public ", "");
			
			g3.addGrammarString(factory.factory.getOWLLiteral(temp_unspoken, "en"));
			
			scan_file.close();
			boolean flag_3=file_3.delete(); // to delete the jsgf_unspoken
			System.out.println("HHHH="+flag_3);		
		
		//from here begins the part of the Utterances and the SemanticGroups
			String folder="D:\\workspace\\OwlSpeak\\src\\owlSpeak\\test\\";
			String file_temp1=folder+"temp1.txt";
			String file_temp2=folder+"final.txt";
			String file_audio_utterances=folder+"audio_utterances.txt";
			String file_final_output=folder+"Final_output.txt";
			File TEMP=new File(file_temp2);
			TEMP.delete();
			File TEMP2=new File(file_audio_utterances);
			TEMP2.delete();
			File TEMP3=new File(file_final_output);
			TEMP3.delete();
			String [] input_files=new String[6];
			input_files[0]="ExplicitConfirm";
			input_files[1]="ImplicitConfirm";
			input_files[2]="Inform";
			input_files[3]="Request";
			input_files[4]="Timeout";
			input_files[5]="EstablishContext";
			for(int i =0;i<input_files.length;i++){
				String file_orig=folder+input_files[i]+".pm";
				utterance_methods.get_info(file_orig,file_temp1,file_temp2);
				
			}
			File final_utterance=new File(file_temp2);
			 Scanner fileScanner = new Scanner (final_utterance);
			 String utterance_names[]=new String [500];
			 int utterance_counter=-1;
			 String list_of_variables []=new String [13];
			 list_of_variables [0]="query_route_number";
			 list_of_variables [1]="departure_stop_name";
			 list_of_variables [2]="arrival_stop_name";
			 list_of_variables [3]="departure_stop_result";
			 list_of_variables [4]="arrival_stop_result";
			 
			 list_of_variables [5]="result_route_number";
			 list_of_variables [6]="departure_time[0]";
			 list_of_variables [7]="arrival_time[0]";
			 list_of_variables [8]="n_routes";
			 list_of_variables [9]="i";
			 list_of_variables [10]="thetime";
			 list_of_variables [11]="at_that_time";
			 list_of_variables [12]="mytime";
			                    
			 //the next list of variables is the same as the previous one but it changes the [] to _
			 String list_of_variables2 []=new String [13];
			 list_of_variables2 [0]="query_route_number";
			 list_of_variables2 [1]="departure_stop_name";
			 list_of_variables2 [2]="arrival_stop_name";
			 list_of_variables2 [3]="departure_stop_result";
			 list_of_variables2 [4]="arrival_stop_result";
			 
			 list_of_variables2 [5]="result_route_number";
			 list_of_variables2 [6]="departure_time_0";
			 list_of_variables2 [7]="arrival_time_0";
			 list_of_variables2 [8]="n_routes";
			 list_of_variables2 [9]="i";
			 list_of_variables2 [10]="thetime";
			 list_of_variables2 [11]="at_that_time";
			 list_of_variables2 [12]="mytime";
			 for (int i =0;i<list_of_variables.length;i++){
					String s=list_of_variables2[i];
					s=s+"_sem";
					
					SemanticGroup SG1=factory.createSemanticGroup(s);
				}               
			 while (fileScanner.hasNextLine()== true){
				 String [] semanticGroupObjects =new String [1000];//an array that will contain the semanticGroup objects for each utterance
				
				 int semanticGroupObjectsConuter=0;
				 String line =fileScanner.nextLine();
				 line =line.trim();
				 //here it checks if an audio link is there and remove this part but keeps track of it in an output file ....also if the utterance string will then be empty it will be removed
				 if(line.contains("audio")==true){
						writeFile(line,file_audio_utterances);
						int index_begin=line.indexOf('{');
						int index_end=utterance_methods.getEndIndex(index_begin,line);
						line=line.replace(line.substring(index_begin,index_end+1),"");
					}
				 int colon=line.indexOf(':');
				 String name=line.substring(0,colon);
				 
				 if(name.charAt(name.length()-1)=='_'){
					 name=name.substring(0,name.length()-1);
				 }
				 String utterance=line.substring(colon+1,line.length());
				 utterance=utterance.trim();
				 if(utterance.isEmpty()!=true){
					
					 
					 int already_exists=0;//if the name already exist it will be given another number
					 for (int i =0;i<=utterance_counter;i++){
						 if(name.equals(utterance_names[i])==true){
							 already_exists++;
						 }
					 }
					 utterance_counter++;
					 utterance_names[utterance_counter]=name;
					 if(already_exists!=0)
					 {
						 name=name+"_"+(already_exists+1);
					 }
					 for (int i =0;i<list_of_variables.length;i++){
						 if(utterance.contains("$"+list_of_variables[i])==true)
						 {
							 semanticGroupObjects[semanticGroupObjectsConuter]=list_of_variables2[i]+"_sem";
							 semanticGroupObjectsConuter++;
							 
						 }
						 utterance=utterance.replace("$"+list_of_variables[i],"#"+list_of_variables2[i]+"_sem#");
						 
					 }
					 //the next lines are to get the variables between <>
					 int begin_var=utterance.indexOf("<");
					 while(begin_var!=-1)
					 {
						 int end_var=utterance_methods.getEndIndex(begin_var,utterance);
						 String orig_var =utterance.substring(begin_var+1,end_var);
						 String var=orig_var.replace(".","_");
						 var=var.replace(" ","_");
						 utterance=utterance.replace("<"+orig_var+">","#"+var+"_sem#");
						 var=var.replace("#","");
						 
						 SemanticGroup SG1=factory.createSemanticGroup(var+"_sem");
						 semanticGroupObjects[semanticGroupObjectsConuter]=var+"_sem";
						 semanticGroupObjectsConuter++;
						 begin_var=utterance.indexOf("<");
						 
					 }
					 
					 //the folllowing two if conditionals are for changing the break statements into the required format
					 if(utterance.contains("BREAK TIME=")==true){
						 int begin_first_break=utterance.indexOf("BREAK TIME=");
						 int end_first_break=utterance_methods.getEndIndex(begin_first_break-1,utterance);
						 String break_1=utterance.substring(begin_first_break-1,end_first_break+1);
						 break_1=break_1.replace("{BREAK TIME=\\\"","");
						 break_1=break_1.replace("s\\\"/}","");
						 break_1=break_1.replace("\\\"/}","");//because there are some statements without seconds written
						 double time = Double.parseDouble(break_1);
						 utterance=utterance.replace("{BREAK TIME=\\\""+break_1+"s\\\"/}","<break time=\""+(int)(time*1000)+"\"/>");
						 utterance=utterance.replace("{BREAK TIME=\\\""+break_1+"\\\"/}","<break time=\""+(int)(time*1000)+"\"/>");
					 }
					 if(utterance.contains("BREAK TIME=")==true){
						 int begin_first_break=utterance.indexOf("BREAK TIME=");
						 int end_first_break=utterance_methods.getEndIndex(begin_first_break-1,utterance);
						 String break_1=utterance.substring(begin_first_break-1,end_first_break+1);
						 break_1=break_1.replace("{BREAK TIME=\\\"","");
						 break_1=break_1.replace("s\\\"/}","");
						 break_1=break_1.replace("\\\"/}","");
						 double time = Double.parseDouble(break_1);
						 utterance=utterance.replace("{BREAK TIME=\\\""+break_1+"s\\\"/}","<break time=\""+(int)(time*1000)+"\"/>");
						 utterance=utterance.replace("{BREAK TIME=\\\""+break_1+"\\\"/}","<break time=\""+(int)(time*1000)+"\"/>");
					 }
					 Utterance u1=factory.createUtterance(name);
					 u1.addUtteranceString(factory.factory.getOWLLiteral(utterance,"en"));
					 for (int i =0;i<semanticGroupObjectsConuter;i++)
					 {
						 SemanticGroup SG1=factory.createSemanticGroup(semanticGroupObjects[i]);
						 u1.addincludesSemantic(SG1);
						 
					 }
					 
					 writeFile(name+":"+utterance,file_final_output);
				 }
				
			 }
			// File toBeDeleted=new File(file_temp2);
			 fileScanner.close();
			 final_utterance.delete();
		
					

		
		
		
		
		
		
		
		try {
			factory.manager.saveOntology(factory.onto,factory.manager.getOntologyDocumentIRI(factory.onto));
		} catch (OWLOntologyStorageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		Settings.uri = uriSave;
	}
}
