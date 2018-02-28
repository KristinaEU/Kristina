package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.filter.LoggingFilter;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import owlSpeak.Move;

public class CerthClient {

	final String address;

	public CerthClient(String confpath) throws IOException, JDOMException {

		FileInputStream in = new FileInputStream(confpath);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(in);
		in.close();
		
		Element ip = doc.getRootElement();
		address = ip.getText();
	}


	public String post(String in, double valence, double arousal, String user, String scenario, String lang, String json) throws Exception {
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(address + "update");

		Invocation.Builder ib = webTarget.request(MediaType.TEXT_PLAIN_TYPE);

		Response response = ib.post(Entity.entity("frames=" + URLEncoder.encode(in, "utf-8") + "\n&emotions="
				+ URLEncoder.encode("{valence:" + valence + ",arousal:" + arousal + "}", "utf-8") + "\n&username="
				+ URLEncoder.encode(user, "utf-8") + "\n&json="
				+ URLEncoder.encode(json, "utf-8") + "\n&lang=" + URLEncoder.encode(lang, "utf-8")+"&scenario=" + URLEncoder.encode(scenario, "utf-8"),
				"application/x-www-form-urlencoded"));

		if (response.getStatus() != 200) {
			String s = response.getStatusInfo().toString();
			String result = response.readEntity(String.class);
			response.close();
			throw new IOException(s+": "+result);
		}

		String result = response.readEntity(String.class);
		
		return result;
	}
	
	public void informCERTH(boolean chosen) throws Exception {
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(address + "inform");

		Invocation.Builder ib = webTarget.request(MediaType.TEXT_PLAIN_TYPE);

		Response response = ib.post(Entity.entity("sent=" + URLEncoder.encode(Boolean.toString(chosen), "utf-8") ,
				"application/x-www-form-urlencoded"));

		if (response.getStatus() != 200) {
			String s = response.getStatusInfo().toString();
			String result = response.readEntity(String.class);
			response.close();
			throw new IOException(s+": "+result);
		}
		
	}

	
	public String simulate(String scenario) {
		BufferedReader r;
		
		String file = "";
		switch(scenario){
		case "test":
		case "eat":
			file="emergency.ttl";
			break;
		case "weather":
			file = "weather.ttl";
			break;
		case "newspaper":
			file = "additional_response_example.ttl";
			break;
		case "sleep":
			file = "eating habits.ttl";
			break;
		default:
			file = "final/2.ttl";
			break;
		}

		try {
			r = new BufferedReader(new InputStreamReader(getClass()
					.getResourceAsStream(
							"/ExampleData_KI/"+file), StandardCharsets.UTF_8));
			String data = "";
			String tmp = r.readLine();
			while (tmp != null) {
				data = data + tmp + "\n";
				tmp = r.readLine();
			}
			r.close();
			return data;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}