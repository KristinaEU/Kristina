package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

public class KristinaTest {
//this is the development server
	static final String address = "http://52.29.254.9:11150";
	
	//this is the production server
//	static final String address = "http://35.156.151.56:11150";
	
	//This is for when I test my local version
//	static final String address = "http://localhost:8084";
	
	
	//this method takes log files and sends the LA part to the DM
	public static void post() {
		try {
			Client client = ClientBuilder.newClient();
			
			SAXBuilder builder = new SAXBuilder();
			builder.setValidation(false);
			builder.setFeature("http://xml.org/sax/features/validation", false);
			builder.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-dtd-grammar",
					false);
			builder.setFeature(
					"http://apache.org/xml/features/nonvalidating/load-external-dtd",
					false);
			
			//read your files here
			File[] folder = new File("./log_src").listFiles();
			
			for (File entry : folder) {
				Document document = (Document) builder.build(entry);
				Element rootNode = document.getRootElement();
				List<Element> list = rootNode.getChildren("record");
				for (Element e : list) {
					Element logger = e.getChild("logger");
					if (logger.getText().equals("VSM2DM")) {
						String data = e.getChild("message").getText();

					WebTarget webTarget = client.target(address);
					Invocation.Builder ib = webTarget.request("application/rdf+xml");

					Response response = ib.post(Entity.entity(data, MediaType.APPLICATION_JSON));
					
					if (response.getStatus() != 200) {
						String s = response.getStatusInfo().toString();
						response.close();
						throw new IOException(s);
					}
					System.out.println("\n" + entry.getName() + "\n");
					String result = StringEscapeUtils.unescapeEcmaScript(response.readEntity(String.class));
					
					//if you want to read the DM output on your console
//					System.out.println(result);
					
					//if you want the DM output written to a file
//					List<String> l = new LinkedList<String>();
//					l.add(result);
//					Files.write(Paths.get("./src/main/resources/results/DM2LG/"+entry.getName().replace("la", "dm").replace("owl","rdf")), l);
					
					}
				}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//this method takes LA files and sends them to the DM
	public static void post2(){
		try {

			Client client = ClientBuilder.newClient();


			//specify the folder here
			File folder = new File("./src/main/resources/ExampleData_LA/test/");
			for (File entry : folder.listFiles()) {
				if ( true) {
					//read your LA file here
					BufferedReader r = new BufferedReader(
							new InputStreamReader(KristinaTest.class
									.getResourceAsStream("/ExampleData_LA/test/"
											+ entry.getName()), StandardCharsets.UTF_8));

					String data = "";
					String tmp = r.readLine();
					while (tmp != null) {
						data = data + tmp + "\n";
						tmp = r.readLine();
					}
					r.close();
					
					//set your meta data
					String v = "0";
					String a = "0.25";
//					if(Integer.parseInt(entry.getName().charAt(6)+"")<2 || (Integer.parseInt(entry.getName().charAt(6)+"")<3 &&Integer.parseInt(entry.getName().charAt(7)+"")<=5)){
//					data = "{\"data\":{\"fusion\":{\"valence\":\""+v+"\",\"arousal\":\""+a+"\"},\"language-analysis\":\""
//				+StringEscapeUtils.escapeEcmaScript(data)+"\"},\"meta\":{\"user\":\"hans\",\"scenario\":\"companion\",\"language\":\"de\"}}";
//					}else{
						data = "{\"data\":{\"fusion\":{\"valence\":\""+v+"\",\"arousal\":\""+a+"\"},\"language-analysis\":\""
								+StringEscapeUtils.escapeEcmaScript(data)+"\"},\"meta\":{\"user\":\"jana\",\"scenario\":\"eat_sleep\",\"language\":\"pl\"}}";
									
//					}

					WebTarget webTarget = client.target(address);
					Invocation.Builder ib = webTarget
							.request("application/rdf+xml");

					Response response = ib.post(Entity.entity(data,
							MediaType.APPLICATION_JSON));

					if (response.getStatus() != 200) {
						String s = response.getStatusInfo().toString();
						response.close();
						throw new IOException(s);
					}
					
					System.out.println("\n" + entry.getName() + "\n");
					String result = response.readEntity(String.class);
					
					//if you want to read the DM output on your console
//					System.out.println(result);
					
					//if you want the DM output written to a file
					List<String> l = new LinkedList<String>();
					l.add(result);
					Files.write(Paths.get("./src/main/resources/results/DM2LG/"+entry.getName().replace("la", "DM")), l);
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void replace(){
		File folder = new File("./src/main/resources/ExampleData_LA/06-02-18/");
		File goal = new File("./src/main/resources/ExampleData_LA/09-02-18/");
		for (File entry : folder.listFiles()) {
			new File(goal.getAbsolutePath()+"/"+entry.getName()).mkdir();
			if(entry.getName().equals("working")){
				for(File e2: entry.listFiles()){
					new File(goal.getAbsolutePath()+"/"+entry.getName()+"/"+e2.getName()).mkdir();
					for(File e3: e2.listFiles()){
						String[] parts = e3.getName().split("_");
						if(parts[2].startsWith("00")){
							parts[2] = parts[2].substring(2);
						}else if(parts[2].startsWith("0")){
							parts[2] = parts[2].substring(1);
						}
						File repl = new File("./src/main/resources/ExampleData_LA/"+parts[0]+"_"+parts[1]+"_"+parts[2]);
						try {
							Files.copy(repl.toPath(), new File(goal.getAbsolutePath()+"/"+entry.getName()+"/"+e2.getName()+"/"+e3.getName()).toPath());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}else{
				for(File e3: entry.listFiles()){
					String[] parts = e3.getName().split("_");
					if(parts[2].startsWith("00")){
						parts[2] = parts[2].substring(2);
					}else if(parts[2].startsWith("0")){
						parts[2] = parts[2].substring(1);
					}
					File repl = new File("./src/main/resources/ExampleData_LA/"+parts[0]+"_"+parts[1]+"_"+parts[2]);
					try {
						Files.copy(repl.toPath(), new File(goal.getAbsolutePath()+"/"+entry.getName()+"/"+e3.getName()).toPath());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}


	public static void main(String[] args) {
		post2();
//		replace();
	}

}
