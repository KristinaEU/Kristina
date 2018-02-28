package owlSpeak.imports;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Scanner;
public class utterance_methods {
	//a method to write the given string into an output file....This method was written by Ahmed but modified to remove the successive underscores 
	public static void writeFile(String copyText,String file_path) throws Exception {
		copyText=copyText.replace("____","_");
		copyText=copyText.replace("___","_");
		copyText=copyText.replace("__","_");
		
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
	
	/*The method getFileReady removes unnecessary lines and spaces from the file*/
	public static void getFileReady(String in, String out)throws Exception{
		 File file = new File(in);
		 Scanner fileScanner = new Scanner (file);
		 while (fileScanner.hasNextLine()== true)
		 {
			 String line =fileScanner.nextLine();
			 line =line.trim();
			  if (line.isEmpty()==true||line.charAt(0)== '#'||line.contains("$Rosetta"))//||line.charAt(0)== '$')
				continue;
			  else
			  {
				  try{
					  if(line.charAt(0)=='m'&&line.charAt(1)=='y'&&(line.indexOf("$ret",3)==-1&&line.indexOf("$str",3)==-1&&line.indexOf("$stop_name",3)==-1&&line.indexOf("$res_str",3)==-1&&line.indexOf("$prompt",3)==-1))
						  continue;
				  }
				  catch(Exception e){
					  writeFile(line,out);
				  }
				  
			  }
			 writeFile(line,out);
				}
		 fileScanner.close();	
	}
	//this method converts a text file to a long String
	public static String file2String(String in)throws Exception
	{
		 File file = new File(in);
		 Scanner fileScanner = new Scanner (file);
		 String out="";
		 while (fileScanner.hasNextLine()==true){
		 
			 String line=fileScanner.nextLine();			 
			 out=out+line;
		 }
		 fileScanner.close();
		 return out;
		 
		 
		 }
	//this method gets where the given character ends and it takes the index of the required character and returns an index of its end
	public static int getEndIndex(int start,String text){
		char c=text.charAt(start);
		char target;
		int ret = -1;
		String stack="";
		stack=stack+c;
		if (c!='"'){
		switch(c){
		case '{':target='}';break;
		case '[':target=']';break;
		case '(':target=')';break;
		case '<':target='>';break;
		default:target=' ';break;
		}
		for(int i = start+1;i<text.length();i++){
			char temp=text.charAt(i);
			if (temp==c){
				stack=stack+temp;
			}
			else if (temp==target){
				if(stack.length()==1){
					ret=i;
					break;
				}
				else
				{
					stack=stack.substring(0,stack.length()-1);
				}
			}
		}
		}else{
			int pos2=-1;
			int pos3=-1;
			int pos4=-1;
			int pos5=-1;
			int i =start+1;
			while(i<text.length()){
				if(text.charAt(i)=='"'){
					if(pos2==-1){
						pos2=i;
					}
					else if(pos3==-1)
					{
						pos3=i;
					
					}
					else if (pos4==-1){
						pos4=i;
					}
					else if(pos5==-1){
						pos5=i;
						break;
					}
				}
			i++;
			}
			
			String sub;
			if(pos3!=-1){
				sub=text.substring(start+1,pos3);
				if(sub.contains("../")||sub.contains("getSampleStops")||sub.contains("0.5s\\")||sub.contains("0.3s\\")||sub.contains("0.3\\")||sub.contains("0.8s\\"))
				{
					String t=text.substring(pos3+1,text.length());
					ret=t.indexOf('"')+pos3+1;
					//the next extra part is to deal with string that contain two such("...") interrupts
					if(pos5!=-1){
						sub=text.substring(pos3+1,pos5);
						if(sub.contains("0.5s\\")||sub.contains("0.3s\\")||sub.contains("0.3\\")){
							 t=text.substring(pos5+1,text.length());
							ret=t.indexOf('"')+pos5+1;
						}
					}
				}
				else
				{
					ret=pos2;
				}
			}
			else
			{
				ret=pos2;
			}
			
			
		}
		return ret;
	}
	//the next method removes unnecessary spaces and unnecessary characters
	public static String getStringReady(String s){
		s=s.replace("  ","");
		s=s.replace("   ","");
		s=s.replace("	","");
		s=s.replace("		","");
		s=s.replace(" { ","{");
		s=s.replace("{ ","{");
		s=s.replace(" {","{");
	    s=s.replace(" } ","}");
	    s=s.replace("} ","}");
		s=s.replace(" }","}");
		s=s.replace(" ( ","(");
		s=s.replace("( ","(");
		s=s.replace(" (","(");
		s=s.replace(" ) ",")");
	    s=s.replace(") ",")");
		s=s.replace(" )",")");
		s=s.replace(" => ","=>");
		s=s.replace(" =>","=>");
		s=s.replace("=> ","=>");
		s=s.replace(" [ ","[");
		s=s.replace(" [","[");
		s=s.replace("[ ","[");
		s=s.replace(" ] ","]");
		s=s.replace(" ]","]");
		s=s.replace("[ ","]");
		s=s.replace(" \" ", "\"");
		s=s.replace(" \"", "\"");
		s=s.replace("\" ", "\"");
		s=s.replace(" {/s} ", "");
		s=s.replace("{/s} ", "");
		s=s.replace(" {/s}", "");
		s=s.replace("{/s}", "");
		s=s.replace(" {s} ", "");
		s=s.replace("{s} ", "");
		s=s.replace(" {s}", "");
		s=s.replace("{s}", "");
		s=s.replace("{/audio}","");
		s=s.replace("\".&getSampleStops(%args).\"","!!!!!!!!!!");
		s=s.replace("\".\""," ");
		s=s.replace("!!!!!!!!!!","\".&getSampleStops(%args).\"");
		s=s.replace("$at_that_time =\"for the rest of the day\"if($args{\"query.travel_time.time.now\"});","");
		return s;
	}
	//this method outputs the normal lines from the string into the described destination (used in this case: ==>{)
	public static void output_normal_1(String temp1,String name1,String destination)throws Exception{
		int j =0;
		String toBeReturned="";
		while(j<temp1.length()){
			
			if(temp1.charAt(j)=='"'){
				int end3=getEndIndex(j,temp1);
				String name2=temp1.substring(j+1,end3);
				j=end3+1;
				if(temp1.charAt(j)=='='&&temp1.charAt(j+1)=='>'){
					if(temp1.charAt(j+2)=='['){
						j=j+2;
						int end4=getEndIndex(j,temp1);
						String temp2=temp1.substring(j+1,end4);
						j=end4+1;
						int k =0;
						int counter1=1;
						while(k<temp2.length()){
							if(temp2.charAt(k)=='"'){
								int end5=getEndIndex(k,temp2);
								String name3=temp2.substring(k+1,end5);
								toBeReturned=name1+"_"+name2+"_"+counter1+":"+name3;
								counter1++;
								k=end5+1;
								writeFile(toBeReturned,destination);
								toBeReturned="";
							}
							else if(temp2.charAt(k)==',')
								{
									k++;
									continue;
								}
							
						}
					}
					else if(temp1.charAt(j+2)=='"'){
							j=j+2;
							int end6=getEndIndex(j,temp1);
							String name3=temp1.substring(j+1,end6);
							j=end6+1;
							toBeReturned=name1+"_"+name2+":"+name3;
							writeFile(toBeReturned,destination);
							toBeReturned="";
							
							
						}
					else if(temp1.charAt(j+2)=='s'&&temp1.charAt(j+3)=='u'&&temp1.charAt(j+4)=='b'&&temp1.charAt(j+5)=='{'){
						j=j+5;
						int end2=getEndIndex(j,temp1);
						String temp2=temp1.substring(j+1,end2);
						j=end2+1;
						String temp_name1=name1;
						name1=name1+"_"+name2;
						managing_conditional_if(temp2,name1,destination);
						name1=temp_name1;
					}
					
				}
				
			}
			else if(temp1.charAt(j)==',')
					{j++;continue;}
			
				
		}
	}
	//this method outputs the normal lines from the string into the described destination (used in this case: ==>")
	public static void output_normal_2(String temp1,String name1,String destination)throws Exception{
		String name2 =temp1;
		String toBeReturned="";
		toBeReturned=name1+":"+name2;
		writeFile(toBeReturned,destination);
		toBeReturned="";
	}
	//this method outputs the normal lines from the string into the described destination (used in this case: ==>[)
	public static void output_normal_3(String temp1,String name1, String destination)throws Exception{
		int i =0;
		String toBeReturned="";
		int counter=0;
		while (i<temp1.length()){
			if(temp1.charAt(i)=='"'){
				counter++;
				int end =getEndIndex(i,temp1);
				String name2=temp1.substring(i+1,end);
				i=end+1;
				toBeReturned=name1+"_"+counter+":"+name2;
				writeFile(toBeReturned,destination);
				toBeReturned="";
			}
			else if (temp1.charAt(i)==','){
				i++;
				continue;
			}
		}
	}
	//deals with normal condtional if
	public static void conditional_if_type_0(String temp1,String name1,String destination)throws Exception{
		String[] conditionals=new String [1000];
		String toBeReturned="";
		int counter_if=-1;
		int i=0;
		boolean flag_elsif=false;
		while(i<temp1.length()){
			
			if (temp1.charAt(i)=='i'&&temp1.charAt(i+1)=='f'&&temp1.charAt(i+2)=='('){
				counter_if++;
				int end1=getEndIndex(i+2,temp1);
				String cond_1=temp1.substring(i+3,end1);
				i=end1+1;				
				if(temp1.charAt(i)=='{')
				{
					i++;
				}
				conditionals[counter_if]=dealing_with_if_type_0(cond_1);
				flag_elsif=false;
				
			}
			else if (temp1.indexOf("elsif",i)==i){
				if (temp1.charAt(i)=='e'&&temp1.charAt(i+1)=='l'&&temp1.charAt(i+2)=='s'&&temp1.charAt(i+3)=='i'&&temp1.charAt(i+4)=='f'&&temp1.charAt(i+5)=='('){
					counter_if++;
					int end1=getEndIndex(i+5,temp1);
					String cond_1=temp1.substring(i+6,end1);
					i=end1+1;				
					if(temp1.charAt(i)=='{')
					{
						i++;
					}
					conditionals[counter_if]=dealing_with_if_type_0(cond_1);
					flag_elsif=true;
				}
				
			}
			else if(temp1.indexOf("return",i)==i){
				
					int return_end=temp1.indexOf("return",i)+6;
					if(temp1.charAt(return_end)=='{'){
						int end_of_this=getEndIndex(return_end,temp1);
						i=end_of_this+1;
						String temp_return=temp1.substring(return_end+1,end_of_this);
						String temp_name1=name1;
						for (int u =0;u<=counter_if;u++){
							name1=name1+"_"+conditionals[u];
						}
						output_normal_1(temp_return,name1,destination);
						name1=temp_name1;
				}
					else if(temp1.charAt(return_end)=='"'){
						
						int end_of_this=getEndIndex(return_end,temp1);
						i=end_of_this+1;
						String temp_return=temp1.substring(return_end+1,end_of_this);
						String temp_name1=name1;
						for (int u =0;u<=counter_if;u++){
							name1=name1+"_"+conditionals[u];
						}
						output_normal_2(temp_return,name1,destination);
						name1=temp_name1;
						
					}
					else if (temp1.charAt(return_end)=='['){
						int end_of_this=getEndIndex(return_end,temp1);
						i=end_of_this+1;
						String temp_return=temp1.substring(return_end+1,end_of_this);
						String temp_name1=name1;
						for (int u =0;u<=counter_if;u++){
							name1=name1+"_"+conditionals[u];
						}
						output_normal_3(temp_return,name1,destination);
						name1=temp_name1;
					}
			}
			else if (temp1.charAt(i)==';'){
				i++;continue;
			}
		
			else if(temp1.charAt(i)=='}'){
					counter_if--;
					i++;
					
				}
			else if(temp1.indexOf("else",i)==i){
				i=i+4;
				
				if(temp1.charAt(i)=='{')
				{
					i++;
				}
				counter_if++;
				if(flag_elsif==false){
					conditionals[counter_if]="not_"+conditionals[counter_if];
				}
				else
				{
					flag_elsif=false;
					conditionals[counter_if]="else";
				}
				
				
			}
			else
			{
				i++;
			}
			
		
		
	}
	}
	// deals with ifs that are part of the input methods(of the form starting with sub
	public static void conditional_if_type_1(String temp1,String name1,String destination)throws Exception{
		int number_of_returns=how_many_times("return",temp1);
		String toBeReturned="";
		if(number_of_returns==1){
			int i =temp1.indexOf("return");
			i=i+7;
			int end1=temp1.indexOf(";",i);
			String ret=temp1.substring(i,end1);
			int ii=temp1.indexOf("my "+ret);
			temp1=temp1.replace("return "+ret,"");
			
			if(temp1.charAt(ii+3+ret.length())==';')
			{}
			else
			{
				ii=ii+3+ret.length()+2;
				if(temp1.charAt(ii)=='"'){
					int end2=getEndIndex(ii,temp1);
					String initialization=temp1.substring(ii+1,end2);
					temp1=temp1.replace("my "+ret+" =\""+initialization+"\"","");
					temp1=temp1.replace(ret+" .=\"","return"+"\""+initialization+" ");
				}
			}
			
			if(temp1.contains("sprintf")){
				int k =temp1.indexOf("sprintf");
				if(temp1.charAt(k+7)=='('){
					//int end3=getEndIndex(k+7,temp1);
					temp1=temp1.replace(");",";");
					temp1=temp1.replace(" sprintf(","");
				}
			}
			temp1=temp1.replace("my "+ret,"");
			temp1=temp1.replace(ret+" =","return");
			
			conditional_if_type_0(temp1,name1,destination);
		}
		else
		{
			conditional_if_type_0(temp1,name1,destination);
		}
	}
	
	public static void conditional_if_type_1_adapted_for_result(String temp1,String name1,String destination)throws Exception{
		int number_of_returns=how_many_times("return",temp1);
		String toBeReturned="";
		if(number_of_returns==1){
			int i =temp1.indexOf("return");
			i=i+7;
			int end1=temp1.indexOf(";",i);
			String ret=temp1.substring(i,end1);
			int ii=temp1.indexOf("my "+ret);
		
			if(temp1.charAt(ii+3+ret.length())==';')
			{}
			else
			{
				ii=ii+3+ret.length()+2;
				if(temp1.charAt(ii)=='"'){
					int end2=getEndIndex(ii,temp1);
					String initialization=temp1.substring(ii+1,end2);
					temp1=temp1.replace("my "+ret+" =\""+initialization+"\"","");
					temp1=temp1.replace(ret+" .=\"","return"+"\""+initialization+" ");
					temp1=temp1.replace("return "+ret, "return"+"\""+initialization+"\"");
				}
			}
			
			if(temp1.contains("sprintf")){
				int k =temp1.indexOf("sprintf");
				if(temp1.charAt(k+7)=='('){
					//int end3=getEndIndex(k+7,temp1);
					temp1=temp1.replace(");",";");
					temp1=temp1.replace(" sprintf(","");
				}
			}
			temp1=temp1.replace("my "+ret,"");
			temp1=temp1.replace(ret+" =","return");
			
			conditional_if_type_0(temp1,name1,destination);
		}
		else
		{
			conditional_if_type_0(temp1,name1,destination);
		}
	}
	// gives the number of times a certain word repeats in a string
	public static int how_many_times(String word, String s){
		int i =0;
		int counter=0;
		while(i<s.length()){
			if (s.indexOf(word,i)!=-1){
				counter++;
				i=s.indexOf(word,i)+word.length();
			}
			else
			{
				i++;
			}
		}
		return counter;
	}
	//this methods makes the condition in the if ready in a suitable form
	public static String dealing_with_if_type_0(String s){
		String out="";

		out=s;
		out=out.replace("$args","");
		out=out.replace("{","");
		out=out.replace("}","");
		out=out.replace("\"","_");
		out=out.replace("'","_");
		out=out.replace("$","_");
		out=out.replace("==","_EQUAL_");
		out=out.replace("||","_OR_");
		out=out.replace("<","_");
		out=out.replace(">","_");
		out=out.replace("!","_NOT_");
		out=out.replace("&&","_AND_");
		out=out.replace("(","_");
		out=out.replace(")","_");
		out=out.replace("_ ","_");
		out=out.replace(" ","_");
		out=out.replace("=","_EQUAL_");
		out=out.replace("____","_");
		out=out.replace("___","_");
		out=out.replace("__","_");
		
		return out;
	}
	// the method manages how to deal with different blocks that start either with return or if
	public static void managing_conditional_if(String temp1,String name1,String destination)throws Exception{
		
		String toBeReturned="";
		int i=0;
		while (i<temp1.length()){
		if(temp1.indexOf("return")==i){
			
			int return_end=temp1.indexOf("return")+6;
			
			if(temp1.charAt(return_end)=='{'){
			
				int end_of_this=getEndIndex(return_end,temp1);
				temp1=temp1.substring(return_end+1,end_of_this);
				
				output_normal_1(temp1,name1,destination);
				break;
				
			}else if(temp1.charAt(return_end)=='"'){
				int end_of_this=getEndIndex(return_end,temp1);
				temp1=temp1.substring(return_end+1,end_of_this);
				
				output_normal_2(temp1,name1,destination);
				break;
			}else if (temp1.charAt(return_end)=='['){
				int end_of_this=getEndIndex(return_end,temp1);
				temp1=temp1.substring(return_end+1,end_of_this);
				
				output_normal_3(temp1,name1,destination);
				break;
			}
		}else if(temp1.charAt(i)=='i'&&temp1.charAt(i+1)=='f'){
			
			conditional_if_type_0(temp1,name1,destination);
			break;
			}else
			{
				i++;
			}
		}
	}
	// this special method only to deal with the block of the result in Inform.pm
	public static void deal_with_result(String temp1,String name1,String destination)throws Exception{
		int i =0;
		String toBeReturned="";
		while(i<temp1.length()){
			if(temp1.indexOf("if",i)==i){
				if (temp1.charAt(i+2)=='('){
					int end=getEndIndex(i+2,temp1);
					int start_of_cond=end+1;
					if(temp1.charAt(start_of_cond)=='{'){
						int end_of_cond=getEndIndex(start_of_cond,temp1);
						if(temp1.indexOf("my $ret =")==start_of_cond+1){
							String cond=temp1.substring(i,end_of_cond+1);
							conditional_if_type_1_adapted_for_result(cond,name1,destination);
							i=end_of_cond+1;
						}
					}
				}
			}
			else if(temp1.indexOf("elsif",i)==i){
				if (temp1.charAt(i+5)=='('){
					int end=getEndIndex(i+5,temp1);
					int start_of_cond=end+1;
					if(temp1.charAt(start_of_cond)=='{'){
						int end_of_cond=getEndIndex(start_of_cond,temp1);
						if(temp1.indexOf("my $ret =",i)==start_of_cond+1){
							String cond=temp1.substring(i,end_of_cond+1);
							conditional_if_type_1_adapted_for_result(cond,name1,destination);
							i=end_of_cond+1;
						}
						else if(temp1.indexOf("my $ret;",i)==start_of_cond+1)
						{
							String cond=temp1.substring(i,end_of_cond+1);
							i=end_of_cond+1;
							cond=cond.replace("my $ret;","");
							int first_if=cond.indexOf("if(",7);
							int first_if_end_temp=getEndIndex(first_if+2,cond);
							int first_if_end=getEndIndex(first_if_end_temp+1,cond);
							
							String if_first=cond.substring(first_if,first_if_end);
							int first_else=cond.indexOf("else{");
							int first_else_end=getEndIndex(first_else+4,cond);
							String else_first=cond.substring(first_else,first_else_end);
							int first_ret=cond.indexOf("$ret =");
							int first_ret_end=getEndIndex(first_ret+6,cond);
							String init_1=cond.substring(first_ret+7,first_ret_end);
							int second_ret=cond.indexOf("$ret =",first_ret_end);
							int second_ret_end=getEndIndex(second_ret+6,cond);
							String init_2=cond.substring(second_ret+7,second_ret_end);
							int second_if=cond.indexOf("if(",first_if_end);
							int second_if_end_temp=getEndIndex(second_if+2,cond);
							int second_if_end=getEndIndex(second_if_end_temp+1,cond);
							
							String if_second=cond.substring(second_if,second_if_end+1);
							
							cond=cond.replace(if_second,"");
							cond=cond.replace("return $ret;","");
						
							int dot_equal=if_second.indexOf("$ret .=");
							int dot_equal_end=getEndIndex(dot_equal+7,if_second);
							String dot_equal_string=if_second.substring(dot_equal+7+1,dot_equal_end+1);
							String if_second_init_1=if_second.replace(dot_equal_string,init_1+" "+dot_equal_string);
							String if_second_init_2=if_second.replace(dot_equal_string,init_2+" "+dot_equal_string);
							cond=cond.replace(if_first, if_first+if_second_init_1);
							cond=cond.replace(else_first,else_first+if_second_init_2);
						
							cond=cond.replace("$ret .=", "return");
							cond=cond.replace("$ret =","return");
							cond=cond.replace("elsif","if");
							
							conditional_if_type_0(cond,name1,destination);
							
							
						}
						else{
							String cond=temp1.substring(i,end_of_cond+1);
							i=end_of_cond+1;
							int init_index=cond.indexOf("my $res_str =");
							int init_end=getEndIndex(init_index+13,cond);
							String init=cond.substring(init_index+14,init_end);
							String toBeRemoved=cond.substring(init_index,init_end+2);
							cond=cond.replace(toBeRemoved,"");
							cond=cond.replace("$res_str .=\"","return\""+init);
							cond=cond.replace("return $res_str;","");
							cond=cond.replace("elsif","if");
							cond=cond.replace("$args{\"result.routes\"}=~ /&ARRAY\\[(\\d+)\\]/;","");
							conditional_if_type_0(cond,name1,destination);
							
						}
					}
				}
			}
			else
			{
				i++;
			}
			
		}
	}
	// this method removes the for loops from the string
	public static String remove_for_loops(String s ){
		int i =0;
		while (i<s.length()){
			if(s.indexOf("for(",i)==i){
				
				int start=i;
				int end=getEndIndex(i+3,s);
				i=end+1;
				if(s.charAt(i)=='{'){
					int end2=getEndIndex(i,s);
					i=end2+1;
					String remove=s.substring(start,end2+1);
					s=s.replace(remove, "");
					i=start;
				}
				
			}else{
				i++;
			}
		}
		return s;
	}
	//this method is just to deal with the prompt blocks in Establish context
	public static void managing_prompt(String temp1,String name1,String destination)throws Exception{
		int i =0;
		String toBeReturned="";
		int end_first_if=getEndIndex(2,temp1);
		if(temp1.indexOf("return")==end_first_if+2){
			int end=getEndIndex(end_first_if+1,temp1);
			String cond=temp1.substring(0,end+1);
			i=end+1;
			conditional_if_type_0(cond, name1,destination);
		}
		String init="";
		String []  if_strings=new String [100];
		String []  if_conditions=new String [100];
		String [] else_if_strings=new String [100];
		String [] else_elsif_strings=new String [100];
		String [] elsif_strings=new String [100];
		String [] elsif_conditions=new String [100];
		
		boolean flag_elsif=false;
		int counter=-1;
		int start_elsif=-1;
		boolean elsif=false;
		if(name1.equals("get_query_specs")){
			while(i<temp1.length()){
				
				if(temp1.indexOf("my $prompt")==i){
					int end_init =getEndIndex(i+12,temp1);
					init=temp1.substring(i+13,end_init);
					i=end_init+1;
			
				}
				if(temp1.indexOf("if(",i)==i){
					
					int end1=getEndIndex(i+2,temp1);
					
					String cond=temp1.substring(i+3,end1);
					i=end1+1;
					cond=dealing_with_if_type_0(cond);
					
						counter++;
						if_conditions [counter]=cond;
		
					if(temp1.charAt(i)=='{'){
						i=i+1;
						if(temp1.indexOf("$prompt .=",i)==i){
							int end2=getEndIndex(i+10,temp1);
							String if_string=temp1.substring(i+11,end2);
							i=end2+1;
							
							
								if_strings[counter]=if_string;
					
							
							
						}
					}
				
					flag_elsif=false;
				}
				else if (temp1.indexOf("elsif(",i)==i)
				{
					
					if(elsif==false){
						start_elsif=counter;
						elsif=true;
					}
					counter++;
					int end1=getEndIndex(i+5,temp1);
					
					String cond=temp1.substring(i+6,end1);
					i=end1+1;
					cond=dealing_with_if_type_0(cond);
					elsif_conditions [counter]=cond;
								
					if(temp1.charAt(i)=='{'){
						i=i+1;
						if(temp1.indexOf("$prompt .=",i)==i){
							int end2=getEndIndex(i+10,temp1);
							String if_string=temp1.substring(i+11,end2);
							i=end2+1;
							elsif_strings[counter]=if_string;
							
						}
					}
					flag_elsif=true;
				}
				else if (temp1.indexOf("else",i)==i){
					if(temp1.charAt(i+4)=='{'){
						i=i+5;
						if(temp1.indexOf("$prompt .=",i)==i){
							int end2=getEndIndex(i+10,temp1);
							String if_string=temp1.substring(i+11,end2);
							i=end2+1;
							if(flag_elsif==false){
								else_if_strings[counter]=if_string;
							}else
							{
								else_elsif_strings[counter]=if_string;
							}
							
							
						}
					}
				}
				
				else if (temp1.charAt(i)=='}'){
				
					i++;
				}
				else
				{
					i++;
				}
			}
			
			String name2="";
			String condition="";
			if(start_elsif==-1){
				start_elsif=counter+1;
			}
			
			for (int j =0;j<start_elsif;j++){
				name2=name2+" "+if_strings[j];
				condition=condition+"_"+if_conditions[j];
				
			}
			writeFile(name1+"_"+condition+"_"+if_conditions[start_elsif]+":"+init+" "+name2+" "+if_strings[start_elsif],destination);
			
			for (int j=start_elsif+1;j<=counter;j++){
				
				writeFile(name1+"_"+condition+"_"+elsif_conditions[j]+":"+init+" "+name2+" "+elsif_strings[j],destination);
				if(else_elsif_strings[j]!=null){
					writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+else_elsif_strings[j],destination);
				}
			}
			for (int j =0;j<=counter;j++){
				if(else_if_strings[j]!=null){
					name2="";
					condition="";
					for (int k =0;k<start_elsif;k++){
						if(k!=j){
						name2=name2+" "+if_strings[k];
						condition=condition+"_"+if_conditions[k];
						}else{
							name2=name2+" "+else_if_strings[k];
							condition=condition+"_not_"+if_conditions[k];
						}
					}
					writeFile(name1+"_"+condition+"_"+if_conditions[start_elsif]+":"+init+" "+name2+" "+if_strings[start_elsif],destination);
				
					
					for (int k=start_elsif+1;k<=counter;k++){
						writeFile(name1+"_"+condition+"_"+elsif_conditions[k]+":"+init+" "+name2+" "+elsif_strings[k],destination);
						if(else_elsif_strings[k]!=null){
							writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+else_elsif_strings[k],destination);
						}
					}
				}
			}
	}else if(name1.equals("give_results"))
	{
		boolean first_elsif=false;
		boolean else_after_first_elsif=false;
		String [] ifs_after_elsif_strings=new String [100];
		String [] ifs_after_elsif_conditions=new String [100];
		int new_counter=-1;
		while(i<temp1.length()){
			
			if(temp1.indexOf("my $prompt")==i){
				int end_init =getEndIndex(i+12,temp1);
				init=temp1.substring(i+13,end_init);
				i=end_init+1;
				
			}
			if(temp1.indexOf("if(",i)==i){
				if(else_after_first_elsif==true&&first_elsif==true){
					int end1=getEndIndex(i+2,temp1);
					
					String cond=temp1.substring(i+3,end1);
					i=end1+1;
					cond=dealing_with_if_type_0(cond);
						new_counter++;
						ifs_after_elsif_conditions [new_counter]=cond;
					if(temp1.charAt(i)=='{'){
						i=i+1;
						if(temp1.indexOf("$prompt .=",i)==i){
							int end2=getEndIndex(i+10,temp1);
							String if_string=temp1.substring(i+11,end2);
							i=end2+1;		
							ifs_after_elsif_strings[new_counter]=if_string;
						}
					}
				continue;
				}
				
				
				int end1=getEndIndex(i+2,temp1);
				
				String cond=temp1.substring(i+3,end1);
				i=end1+1;
				cond=dealing_with_if_type_0(cond);
				
					counter++;
					if_conditions [counter]=cond;
		
				
				if(temp1.charAt(i)=='{'){
					i=i+1;
					if(temp1.indexOf("$prompt .=",i)==i){
						int end2=getEndIndex(i+10,temp1);
						String if_string=temp1.substring(i+11,end2);
						i=end2+1;
						
						
							if_strings[counter]=if_string;
					
						
						
					}
				}
				
				flag_elsif=false;
			}
			else if (temp1.indexOf("elsif(",i)==i)
			{
				
				if(else_after_first_elsif==true&&first_elsif==true){
					new_counter++;
					int end1=getEndIndex(i+5,temp1);
					
					String cond=temp1.substring(i+6,end1);
					i=end1+1;
					cond=dealing_with_if_type_0(cond);
					ifs_after_elsif_conditions [new_counter]=cond;
								
					if(temp1.charAt(i)=='{'){
						i=i+1;
						if(temp1.indexOf("$prompt .=",i)==i){
							int end2=getEndIndex(i+10,temp1);
							String if_string=temp1.substring(i+11,end2);
							i=end2+1;
							ifs_after_elsif_strings[new_counter]=if_string;
							
						}
					}
					continue;
				}
				if(first_elsif==false){
					first_elsif=true;
				}
				if(elsif==false){
					start_elsif=counter;
					elsif=true;
				}
				counter++;
				int end1=getEndIndex(i+5,temp1);
				
				String cond=temp1.substring(i+6,end1);
				i=end1+1;
				cond=dealing_with_if_type_0(cond);
				elsif_conditions [counter]=cond;
							
				if(temp1.charAt(i)=='{'){
					i=i+1;
					if(temp1.indexOf("$prompt .=",i)==i){
						int end2=getEndIndex(i+10,temp1);
						String if_string=temp1.substring(i+11,end2);
						i=end2+1;
						elsif_strings[counter]=if_string;
						
					}
				}
				flag_elsif=true;
			}
			else if (temp1.indexOf("else",i)==i){
				if(else_after_first_elsif==false&&first_elsif==true){
					else_after_first_elsif=true;
				}
				else if(else_after_first_elsif==true&&first_elsif==true){
					
					if(temp1.charAt(i+4)=='{'){
						i=i+5;
						if(temp1.indexOf("$prompt .=",i)==i){
							int end2=getEndIndex(i+10,temp1);
							String if_string=temp1.substring(i+11,end2);
							i=end2+1;
							new_counter++;
							ifs_after_elsif_strings[new_counter]=if_string;
					
							
							
						}
					}
					continue;
				}
				if(temp1.charAt(i+4)=='{'){
					i=i+5;
					if(temp1.indexOf("$prompt .=",i)==i){
						int end2=getEndIndex(i+10,temp1);
						String if_string=temp1.substring(i+11,end2);
						i=end2+1;
						if(flag_elsif==false){
							else_if_strings[counter]=if_string;
						}else
						{
							else_elsif_strings[counter]=if_string;
						}
						
						
					}
				}
			}
			
			else if (temp1.charAt(i)=='}'){
				
				i++;
			}
			else
			{
				i++;
			}
		}
		
		
		String name2="";
		String condition="";
		if(start_elsif==-1){
			start_elsif=counter+1;
		}
		
		for (int j =0;j<=start_elsif;j++){
			name2=name2+" "+if_strings[j];
			condition=condition+"_"+if_conditions[j];
		}
		for (int j =0;j<=new_counter;j++){
			if(j!=new_counter)
			{
				writeFile(name1+"_"+condition+"_"+ifs_after_elsif_conditions[j]+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
			
			else{
				writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
				
		}
		
		
		
		
		
		
		/////////////////////////////////////////////////////
		name2="";
		condition="";
				
		for (int j =0;j<start_elsif;j++){
			name2=name2+" "+if_strings[j];
			condition=condition+"_"+if_conditions[j];
		}
		name2=name2+" "+elsif_strings[start_elsif+1];
		condition=condition+"_"+elsif_conditions[start_elsif+1];
		for (int j =0;j<=new_counter;j++){
			if(j!=new_counter)
			{
				writeFile(name1+"_"+condition+"_"+ifs_after_elsif_conditions[j]+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
			
			else{
				writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
				
		}
		/////////////////////////////////////////////////////
		name2="";
		condition="";
	
		for (int j =0;j<start_elsif;j++){
			name2=name2+" "+if_strings[j];
			condition=condition+"_"+if_conditions[j];
			}
		name2=name2+" "+else_elsif_strings[start_elsif+1];
		condition=condition+"_"+"else";
		for (int j =0;j<=new_counter;j++){
			if(j!=new_counter)
			{
				writeFile(name1+"_"+condition+"_"+ifs_after_elsif_conditions[j]+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}

			else{
				writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}

		}
		////////////////////////////////////



		name2="";
		condition="";
		for (int j =0;j<=start_elsif;j++){
			if(else_if_strings[j]==null)
			{
				name2=name2+" "+if_strings[j];
				condition=condition+"_"+if_conditions[j];
			}
			else
			{
				name2=name2+" "+else_if_strings[j];
				condition=condition+"_not_"+if_conditions[j];
			}
		}
		for (int j =0;j<=new_counter;j++){
			if(j!=new_counter)
			{
				writeFile(name1+"_"+condition+"_"+ifs_after_elsif_conditions[j]+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
	
			else{
				writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
		
		}






		/////////////////////////////////////////////////////
		name2="";
		condition="";
		
		for (int j =0;j<start_elsif;j++){
			if(else_if_strings[j]==null)
			{
				name2=name2+" "+if_strings[j];
				condition=condition+"_"+if_conditions[j];
			}
			else
			{
				name2=name2+" "+else_if_strings[j];
				condition=condition+"_not_"+if_conditions[j];
				}
		}
		name2=name2+" "+elsif_strings[start_elsif+1];
		condition=condition+"_"+elsif_conditions[start_elsif+1];
		for (int j =0;j<=new_counter;j++){
			if(j!=new_counter)
			{
				writeFile(name1+"_"+condition+"_"+ifs_after_elsif_conditions[j]+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
	
			else{
				writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}
		
		}
		/////////////////////////////////////////////////////
		name2="";
		condition="";
		
		for (int j =0;j<start_elsif;j++){
			if(else_if_strings[j]==null)
			{
				name2=name2+" "+if_strings[j];
				condition=condition+"_"+if_conditions[j];
			}
			else
			{
				name2=name2+" "+else_if_strings[j];
				condition=condition+"_not_"+if_conditions[j];
			}
		}
		name2=name2+" "+else_elsif_strings[start_elsif+1];
		condition=condition+"_"+"else";
		for (int j =0;j<=new_counter;j++){
			if(j!=new_counter)
			{
				writeFile(name1+"_"+condition+"_"+ifs_after_elsif_conditions[j]+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}

			else{
				writeFile(name1+"_"+condition+"_"+"else"+":"+init+" "+name2+" "+ifs_after_elsif_strings[j],destination);
			}

		}
		
		
		
		
	}
	else if(name1.equals("get_departure_neighborhood_and_stop")){
		String neighborhood_statement="";
		while(i<temp1.length()){
			
			if(temp1.indexOf("my $prompt")==i){
				int end_init =getEndIndex(i+12,temp1);
				init=temp1.substring(i+13,end_init);
				i=end_init+1;
			
			}
			if(temp1.indexOf("if(",i)==i){
				
				int end1=getEndIndex(i+2,temp1);
				
				String cond=temp1.substring(i+3,end1);
				i=end1+1;
				cond=dealing_with_if_type_0(cond);
				
					counter++;
					if_conditions [counter]=cond;
			
				if(temp1.charAt(i)=='{'){
					i=i+1;
					if(temp1.indexOf("$prompt .=",i)==i){
						int end2=getEndIndex(i+10,temp1);
						String if_string=temp1.substring(i+11,end2);
						i=end2+1;
						if(temp1.indexOf("$prompt .=",i+2)==i+2)
						{
							i=i+2;
							int end3=getEndIndex(i+10,temp1);
							 if_string=if_string+" "+temp1.substring(i+11,end3);
							i=end3+1;
							if(temp1.indexOf("$prompt .=",i+2)==i+2)
							{
								i=i+2;
								int end4=getEndIndex(i+10,temp1);
								 if_string=if_string+" "+temp1.substring(i+11,end4);
								 neighborhood_statement=temp1.substring(i+11,end4);
								i=end4+1;
								
							}
						}
						if_strings[counter]=if_string;
											
					}
				}
				
			}
			
			else if (temp1.indexOf("else",i)==i){
				if(temp1.charAt(i+4)=='{'){
					i=i+5;
					if(temp1.indexOf("$prompt .=",i)==i){
						int end2=getEndIndex(i+10,temp1);
						String if_string=temp1.substring(i+11,end2);
						i=end2+1;
						else_if_strings[counter]=if_string;
						
						
						
					}
				}
			}
			
			else if (temp1.charAt(i)=='}'){
			
				i++;
			}
			else
			{
				i++;
			}
		}
	
		String name2="";
		String condition="";
		if(start_elsif==-1){
			start_elsif=counter;
		}
	
		for (int j =0;j<3;j++){
			if(j!=2)
				name2=name2+" "+if_strings[j];
			
			else
				name2=name2+" "+neighborhood_statement;
			
			condition=condition+"_"+if_conditions[j];
			
		}
		writeFile(name1+"_"+condition+":"+init+" "+name2,destination);
		name2="";
		condition="";
		for (int j =2;j<5;j++){
			if(if_strings[j]!=null)
			name2=name2+" "+if_strings[j];
			condition=condition+"_"+if_conditions[j];
			
		}
		writeFile(name1+"_"+condition+":"+init+" "+name2,destination);
		name2="";
		condition="";
		for (int j =0;j<3;j++){
			if(j!=2)
				name2=name2+" "+if_strings[j];
			
			else
				name2=name2+" "+else_if_strings[4];
			if(j!=2)
				condition=condition+"_"+if_conditions[j];
			else
				condition=condition+"_not_"+if_conditions[j];
			
			
		}
		writeFile(name1+"_"+condition+":"+init+" "+name2,destination);
		
	}
		
	}
	//this is the main method that controls everything and that should be called to get the required info from the files
	public static void get_info(String file_orig,String file_temp1,String file_temp2)throws Exception{
		getFileReady(file_orig,file_temp1);
		String file1=file2String(file_temp1);
		file1=getStringReady(file1);
		file1=remove_for_loops(file1);
		System.out.println(file1);
		int i =0;
		if(file1.charAt(i)=='{')
			i=1;
		String toBeReturned="";
		if (file1.contains("prompt")==true){
			file1=file1.replace("$prompt .=\".{BREAK TIME=\\\"0.5s\\\"/}\"if $prompt ne\"\";","");
			file1=file1.replace("\";$prompt .=\""," ");
			System.out.println(file1);
			while(i<file1.length()){
			if (file1.charAt(i)=='"')
			{
				int end1=getEndIndex(i,file1);
				String name1=file1.substring(i+1,end1);
				i=end1+1;
				if (file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='"'){
					i=i+2;
					int end2=getEndIndex(i,file1);
					String temp1=file1.substring(i+1,end2);
					i=end2+1;
					output_normal_2(temp1,name1,file_temp2);
				}
				else if(file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='s'&&file1.charAt(i+3)=='u'&&file1.charAt(i+4)=='b'&&file1.charAt(i+5)=='{'){
					i=i+5;
					int end2=getEndIndex(i,file1);
					String temp1=file1.substring(i+1,end2);
					i=end2+1;
					managing_prompt(temp1,name1,file_temp2);
					
				}
				
			}else
			{
				i++;
			}
			}
		}else
		{
		while (i<file1.length()){
			
			if(file1.charAt(i)=='"'){
				int end = getEndIndex(i,file1);
				String name1=file1.substring(i+1,end);
			
				i=end+1;
				if(name1.equals("result")==true){
					if(file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='s'&&file1.charAt(i+3)=='u'&&file1.charAt(i+4)=='b'&&file1.charAt(i+5)=='{'){
						i=i+5;
						int end2=getEndIndex(i,file1);
						String temp1=file1.substring(i+1,end2);
						i=end2+1;
						deal_with_result(temp1,name1,file_temp2);
					}
				}
				else if(file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='{'){
					i=i+2;
					int end2=getEndIndex(i,file1);
					String temp1=file1.substring(i+1,end2);
					i=end2+1;
					output_normal_1(temp1,name1,file_temp2);
				
				}
				else if (file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='"'){
					i=i+2;
					int end2=getEndIndex(i,file1);
					String temp1=file1.substring(i+1,end2);
					i=end2+1;
					output_normal_2(temp1,name1,file_temp2);
				}
				else if(file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='['){
					i=i+2;
					int end2=getEndIndex(i,file1);
					String temp1=file1.substring(i+1,end2);
					i=end2+1;
					output_normal_3(temp1,name1,file_temp2);
				}
				else if(file1.charAt(i)=='='&&file1.charAt(i+1)=='>'&&file1.charAt(i+2)=='s'&&file1.charAt(i+3)=='u'&&file1.charAt(i+4)=='b'&&file1.charAt(i+5)=='{'){
						i=i+5;
						int end2=getEndIndex(i,file1);
						String temp1=file1.substring(i+1,end2);
						i=end2+1;
						managing_conditional_if(temp1,name1,file_temp2);
						
					}
				
				
			}
			/*else if (file1.indexOf("sub",i)==i){
				
				int start_sub=file1.indexOf("{",i);
				String  name1=file1.substring(i+4,start_sub);
				int end_sub=getEndIndex(start_sub,file1);
				String temp1=file1.substring(start_sub+1,end_sub);
				i=end_sub+1;
				;
				conditional_if_type_1(temp1,toBeReturned,name1,file_temp2);
			}*/
			else if(file1.charAt(i)==','||file1.charAt(i)=='}'||file1.charAt(i)==';'){
					i++;
					continue;
				}
			else
			{
				i++;
			}
			
		}
		}
		File TEMP=new File(file_temp1);
		TEMP.delete();
	}
	
}
