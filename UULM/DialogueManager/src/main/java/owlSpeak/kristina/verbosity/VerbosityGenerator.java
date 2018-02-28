package owlSpeak.kristina.verbosity;

import java.io.FileInputStream;
import java.io.IOException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Vector.Norm;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import owlSpeak.kristina.DialogueAction;
import owlSpeak.kristina.KristinaAgenda;
import owlSpeak.kristina.KristinaMove;



public class VerbosityGenerator {
	
	private static String ip;
	private static int port;
	
	private Map<KristinaAgenda, Set<String>> estimatedTopics;
	
	public VerbosityGenerator() throws JDOMException, IOException{

		
		FileInputStream in = new FileInputStream("./conf/W2V/config.xml");
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
			in.close();
		
		
		Element ip = doc.getRootElement();
		this.ip = ip.getChild("ip").getText();
		port = Integer.parseInt(ip.getChild("port").getText());
		
	}
	
	public List<KristinaAgenda> addAdditionalInformation(List<KristinaAgenda> core, List<KristinaAgenda> generalOptions, Map<KristinaAgenda,Integer> additionalOptions, Map<KristinaAgenda,Integer> leftoverOptions, Vector historyVec, String lang, Map<KristinaAgenda,Integer> recentAgendas, String scenario, Logger log) throws ClassNotFoundException, IOException{
		List<KristinaAgenda> result = new LinkedList<KristinaAgenda>(core);
		String logContent = "";
		Map<KristinaAgenda,Integer> choice = new HashMap<KristinaAgenda,Integer>(additionalOptions);
		choice.putAll(leftoverOptions);
		for(KristinaAgenda recent: recentAgendas.keySet()){
			choice.remove(recent);
		}
			
		for(KristinaAgenda ka: choice.keySet()){
			logContent = logContent + rate(ka,lang,historyVec) + "\t" + ka+"\n";
		}
		int i = 0;
		while(result.size() == core.size()&&i < 10){
			int j = i;
			List<KristinaAgenda> additional  = choice.entrySet().stream().filter(e -> e.getValue()==j).map(e -> e.getKey()).collect(Collectors.toList());
			additional.removeIf(s -> rate(s, lang,historyVec)> 0.1 || j == 0);
			additional.sort((s1, s2) -> {
					return (int) Math.signum(Double.compare(rate(s2,lang,historyVec), rate(s1,lang,historyVec)));
				});
			if(!additional.isEmpty()){
				if(!result.contains(additional.get(0))&& (rate(additional.get(0),lang,historyVec)>= 0.01 || j == 0)){
//				if(!result.contains(additional.get(0))){
				result.add(additional.get(0));
				}
			}
			i++;
		}
		
		
		if(result.size() == core.size()){
//			List<KristinaAgenda> additional = new LinkedList<KristinaAgenda>(generalOptions);
//			additional.removeAll(recentAgendas.keySet());
			
			List<KristinaAgenda> additional = new LinkedList<KristinaAgenda>();
			
			for(KristinaAgenda a : generalOptions){
				if(!recentAgendas.keySet().contains(a)){
				switch (scenario){
				case "companion": 
					if(a.getText().contains("newspaper") || a.getText().contains("weather today")|| a.getText().contains("events")|| a.getText().contains("social media")){
						additional.add(a);
					}
					break;
				case "eat_sleep":
					if(a.getText().contains("recipes") || a.getText().contains("sleep")|| a.getText().contains("dementia")||a.getText().contains("diabetes")|| a.getText().contains("doctor if")|| a.getText().contains("alright")){
						additional.add(a);
					}
//					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
//						additional.add(a);
//					}
					break;
				case "babycare":
					if(a.getText().contains("baby")|| a.getText().contains("park")||a.getText().contains("partner")|| a.getText().contains("Family Doctor")|| a.getText().contains("sources")){
						additional.add(a);
					}
//					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
//						additional.add(a);
//					}
					break;
				case "backpain":
					if(a.getText().contains("health centre")|| a.getText().contains("appointment")|| a.getText().contains("symptoms")|| a.getText().contains("Family Doctor")|| a.getText().contains("sources")){
						additional.add(a);
					}
//					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
//						additional.add(a);
//					}
					break;
				}
				}
			}
			
			for(KristinaAgenda ka: additional){
				logContent = logContent + rate(ka,lang,historyVec) + "\t" + ka+"\n";
			}
		

			additional.removeIf(s -> rate(s, lang,historyVec)> 0.2);
			additional.sort((s1, s2) -> {
				return (int) Math.signum(Double.compare(rate(s2,lang,historyVec), rate(s1,lang,historyVec)));
			});
			if(!additional.isEmpty()){
				if(!result.contains(additional.get(0)) && rate(additional.get(0),lang,historyVec)>= 0.02 ){
				result.add(additional.get(0));
				}
			}
		}
		log.info(logContent);
		System.out.println(logContent);
		return result;
	}
	
	public List<KristinaAgenda> continueConversation(List<KristinaAgenda> core, List<KristinaAgenda> generalOptions, Map<KristinaAgenda,Integer> additionalOptions, Map<KristinaAgenda,Integer> leftoverOptions, Vector historyVec, String lang,Map<KristinaAgenda,Integer> recentAgendas, String scenario, Logger log){
		List<KristinaAgenda> result = new LinkedList<KristinaAgenda>();
		String logContent = "";
		Map<KristinaAgenda,Integer> choice = new HashMap<KristinaAgenda,Integer>(additionalOptions);
		choice.putAll(leftoverOptions);
		for(KristinaAgenda recent: recentAgendas.keySet()){
			choice.remove(recent);
		}
			
		for(KristinaAgenda ka: choice.keySet()){
			logContent = logContent + rate(ka,lang,historyVec) + "\t" + ka+"\n";
		}
		int i = 0;
		while(result.size() == core.size()&&i < 10){
			int j = i;
			List<KristinaAgenda> additional  = choice.entrySet().stream().filter(e -> e.getValue()==j).map(e -> e.getKey()).collect(Collectors.toList());
			additional.removeIf(s -> rate(s, lang,historyVec)> 0.1);
			additional.sort((s1, s2) -> {
					return (int) Math.signum(Double.compare(rate(s2,lang,historyVec), rate(s1,lang,historyVec)));
				});
			if(!additional.isEmpty()){
				if(!result.contains(additional.get(0))&& rate(additional.get(0),lang,historyVec)>= 0.01){
				result.add(additional.get(0));
				}
			}
			i++;
		}
		if(result.size() == core.size()){
//			List<KristinaAgenda> additional = new LinkedList<KristinaAgenda>(generalOptions);
//			additional.removeAll(recentAgendas.keySet());
			
			List<KristinaAgenda> additional = new LinkedList<KristinaAgenda>();
			
			for(KristinaAgenda a : generalOptions){
				if(!recentAgendas.keySet().contains(a)){
				switch (scenario){
				case "companion": 
					if(a.getText().contains("newspaper") || a.getText().contains("weather today")|| a.getText().contains("events")|| a.getText().contains("social media")){
						additional.add(a);
					}
					break;
				case "eat_sleep":
//					if(a.getText().contains("recipes") || a.getText().contains("sleep")|| a.getText().contains("dementia")||a.getText().contains("diabetes")){
//						additional.add(a);
//					}
					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
						additional.add(a);
					}
					break;
				case "babycare":
//					if(a.getText().contains("baby")|| a.getText().contains("park")){
//						additional.add(a);
//					}
					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
						additional.add(a);
					}
					break;
				case "backpain":
//					if(a.getText().contains("health centre")){
//						additional.add(a);
//					}
					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
						additional.add(a);
					}
					break;
				}
				}
			}
			for(KristinaAgenda ka: additional){
				logContent = logContent + rate(ka,lang,historyVec) + "\t" + ka+"\n";
			}

			additional.removeIf(s -> rate(s, lang,historyVec)> 0.2);
			additional.sort((s1, s2) -> {
				return (int) Math.signum(Double.compare(rate(s2,lang,historyVec), rate(s1,lang,historyVec)));
			});
			if(!additional.isEmpty()){
				if(!result.contains(additional.get(0)) && rate(additional.get(0),lang,historyVec)>= 0.02 ){
				result.add(additional.get(0));
				}
			}
		}
			if(result.isEmpty()){
				result.addAll(core);
			}
			log.info(logContent);
			System.out.println(logContent);
		return result;
	}
	
	public List<KristinaAgenda> recoverMisunderstanding(List<KristinaAgenda> core, List<KristinaAgenda> generalOptions, Map<KristinaAgenda,Integer> additionalOptions, Map<KristinaAgenda,Integer> leftoverOptions,Vector historyVec, String lang,Map<KristinaAgenda,Integer> recentAgendas, String scenario, Logger log) throws ClassNotFoundException, IOException{
		List<KristinaAgenda> result = new LinkedList<KristinaAgenda>(core);
		String logContent = "";
		Map<KristinaAgenda,Integer> choice = new HashMap<KristinaAgenda,Integer>(additionalOptions);
		choice.putAll(leftoverOptions);
		for(KristinaAgenda recent: recentAgendas.keySet()){
			choice.remove(recent);
		}
			
		for(KristinaAgenda ka: choice.keySet()){
			logContent = logContent + rate(ka,lang,historyVec) + "\t" + ka+"\n";
		}
		int i = 0;
		while(result.size() == core.size()&&i < 10){
			int j = i;
			List<KristinaAgenda> additional  = choice.entrySet().stream().filter(e -> e.getValue()==j).map(e -> e.getKey()).collect(Collectors.toList());
			additional.removeIf(s -> rate(s, lang,historyVec)> 0.1);
			additional.sort((s1, s2) -> {
					return (int) Math.signum(Double.compare(rate(s2,lang,historyVec), rate(s1,lang,historyVec)));
				});
			if(!additional.isEmpty()){
				if(!result.contains(additional.get(0))&& rate(additional.get(0),lang,historyVec)>= 0.01){
				result.add(additional.get(0));
				}
			}
			i++;
		}
		if(result.size() == core.size()){
		
//			List<KristinaAgenda> additional = new LinkedList<KristinaAgenda>(generalOptions);
//			additional.removeAll(recentAgendas.keySet());
			
			List<KristinaAgenda> additional = new LinkedList<KristinaAgenda>();
			
			for(KristinaAgenda a : generalOptions){
				if(!recentAgendas.keySet().contains(a)){
				switch (scenario){
				case "companion": 
					if(a.getText().contains("newspaper") || a.getText().contains("weather today")|| a.getText().contains("events")|| a.getText().contains("social media")){
						additional.add(a);
					}
					break;
				case "eat_sleep":
//					if(a.getText().contains("recipes") || a.getText().contains("sleep")|| a.getText().contains("dementia")||a.getText().contains("diabetes")){
//						additional.add(a);
//					}
					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
						additional.add(a);
					}
					break;
				case "babycare":
//					if(a.getText().contains("baby")|| a.getText().contains("park")){
//						additional.add(a);
//					}
					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
						additional.add(a);
					}
					break;
				case "backpain":
//					if(a.getText().contains("health centre")){
//						additional.add(a);
//					}
					if(a.isDialogueAction(DialogueAction.ASK_FURTHER_TASK)){
						additional.add(a);
					}
					break;
				}
				}
			}
			
			for(KristinaAgenda ka: additional){
				logContent = logContent + rate(ka,lang,historyVec) + "\t" + ka+"\n";
			}
		
			additional.removeIf(s -> rate(s, lang,historyVec)> 0.015);
			additional.sort((s1, s2) -> {
				return (int) Math.signum(Double.compare(rate(s2,lang,historyVec), rate(s1,lang,historyVec)));
			});
			if(!additional.isEmpty()){
				if(!result.contains(additional.get(0)) ){
				result.add(additional.get(0));
				}
			}
		}
			log.info(logContent);
			System.out.println(logContent);
		return result;
	}
	
	
	
	private static Vector getVector(String word, String language) throws IOException, ClassNotFoundException{
		boolean tryAgain = true;
		while (tryAgain) {
			try {
				tryAgain = false;
				
				
				Client client = ClientBuilder.newClient();
				WebTarget webTarget = client.target("http://"+ip+":"+port).queryParam("lang", language).queryParam("word", word);

				Invocation.Builder ib = webTarget.request(MediaType.WILDCARD);

				Response response = ib.get();

				if (response.getStatus() != 200) {
					String s = response.getStatusInfo().toString();
					String result = response.readEntity(String.class);
					response.close();
					throw new IOException(s+": "+result);
				}

				String[] result = response.readEntity(String.class).split("\n");
				Vector v = new DenseVector(result.length);
				for(int i = 0; i < result.length; i++){
					v.set(i, Double.parseDouble(result[i]));
				}
				
				
				return v;
				
		
			} catch (Exception e) {
				System.err.println(e);
				tryAgain = true;
			} 
		}
		return null;
	}
	
	private static double getSimilarity(Vector w1, Vector w2) {
		if(w1 != null && w2 != null){
			Vector tmp = new DenseVector(w1);
		return tmp.add(-1, w2).norm(Norm.Two);
		}
		return 100;
	}
	
	
	public Vector getVector(KristinaAgenda a, String lang) throws ClassNotFoundException, IOException{
		Vector v = new DenseVector(300).zero();
		int i = 0;
		switch (a.getDialogueAction()) {
		case DialogueAction.DECLARE:
		case DialogueAction.STATEMENT:
		case DialogueAction.ADDITIONAL_INFORMATION:
			for(String s: a.getTopics()){
				s = s.substring(s.indexOf('#')+1).toLowerCase();
				Vector tmp = getVector(s, "en");
				if(tmp != null){
				v = v.add(tmp);
				i++;
				}
			}
			break;
		case DialogueAction.READ_NEWSPAPER:
		case DialogueAction.IR_RESPONSE:
			 String[] arr = a.getText().toLowerCase().split("\\s|[^(\\p{L})]");
			 for(String s: arr){
				 if(!s.isEmpty()){
				 Vector tmp = getVector(s, lang);
					if(tmp != null){
					v = v.add(tmp);
					i++;
					}
				 }
			 }
			break;
		case DialogueAction.SHOW_WEBPAGE:
		case DialogueAction.SHOW_WEATHER:
		case DialogueAction.CANNED:
		case DialogueAction.PROACTIVE_CANNED:
		case DialogueAction.PROACTIVE_LIST:
		case DialogueAction.REQUEST_CLARIFICATION:
			String[] arr2 = a.getText().toLowerCase().split("\\s|[^(\\p{L})]");
			 for(String s: arr2){
				 if(!s.isEmpty()){
				 Vector tmp = getVector(s, "en");
					if(tmp != null){
					v = v.add(tmp);
					i++;
					}
				 }
			 }
			break;
		
		}
		if(v.norm(Norm.Two)<0.000000001){
			return new DenseVector(300).zero();
		}
		return v.scale(1d/i);
	}
	
	public Vector getVector(KristinaMove a, String lang) throws ClassNotFoundException, IOException{
		Vector v = new DenseVector(300).zero();
		int i = 0;
		if (a.getTopics().isEmpty()) {
			String[] arr = a.getSpeak().split("\\s|[^(\\p{L})]");
			 for(String s: arr){
				 if(!s.isEmpty()){
				 Vector tmp = getVector(s, lang);
					if(tmp != null){
					v = v.add(tmp);
					i++;
					}
				 }
			 }
		}else{
			for(String s: a.getTopics()){
				s = s.substring(s.indexOf('#')+1);
				Vector tmp = getVector(s, "en");
				if(tmp != null){
				v = v.add(tmp);
				i++;
				}
			}
		}
		if(v.norm(Norm.Two)<0.000000001){
			return new DenseVector(300).zero();
		}
		return v.scale(1d/i);
	}
	
	private double rate(KristinaAgenda a, String lang, Vector v) {
		
		try {
			return getSimilarity(getVector(a, lang),v);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 100;
	}
	
	

	

}
