package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.CerthClient;
import model.KristinaModel;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.jena.util.FileUtils;
import org.python.core.util.StringUtil;

public class KristinaTest {

	static final String address = "http://52.29.254.9:11150";

	public static void post() {

		try {

			Client client = ClientBuilder.newClient();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			File folder = new File("./src/main/resources/ExampleData_LA/ExceptionHandling/");
			for (File entry : folder.listFiles()) {
				if ( entry.getName().startsWith("")) {

					BufferedReader r = new BufferedReader(
							new InputStreamReader(KristinaTest.class
									.getResourceAsStream("/ExampleData_LA/ExceptionHandling/"
											+ entry.getName()), StandardCharsets.UTF_8));

					String data = "";
					String tmp = r.readLine();
					while (tmp != null) {
						data = data + tmp + "\n";
						tmp = r.readLine();
					}
					r.close();
					String v = "0";
					String a = "0.25";
					/*if(entry.getName().equals("la-output_3.owl")){
						v = "-0.5";
						a = "-0.25";
					}*/
				
					data = "{\"data\":{\"fusion\":{\"valence\":\""+v+"\",\"arousal\":\""+a+"\"},\"language-analysis\":\""
				+StringEscapeUtils.escapeEcmaScript(data)+"\"},\"meta\":{\"user\":\"hans\",\"scenario\":\"newspaper\"}}";

					WebTarget webTarget = client.target(address);
					Invocation.Builder ib = webTarget
							.request("application/rdf+xml");

					long start = System.currentTimeMillis();
					Response response = ib.post(Entity.entity(data,
							MediaType.APPLICATION_JSON));
					long end = System.currentTimeMillis();
					//System.out.println(end-start);
					if (response.getStatus() != 200) {
						String s = response.getStatusInfo().toString();
						response.close();
						throw new IOException(s);
					}
					//System.out.println("\n" + entry.getName() + "\n");
					String result = StringEscapeUtils.unescapeEcmaScript(response.readEntity(String.class));
					//System.out.println(result);
					List<String> l = new LinkedList<String>();
					l.add(result);
					Files.write(Paths.get("./src/main/resources/results/DM2LG/"+entry.getName().replace("la", "dm").replace("owl","rdf")), l);
					
					//reader.readLine();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		post();
	}

}
