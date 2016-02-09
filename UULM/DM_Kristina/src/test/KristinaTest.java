package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;

public class KristinaTest {

	static final String address = "http://localhost:11153";

	public static String post() {

		try {

			String data = IOUtils.toString(new FileReader(
					"./src/test/inform.ttl"));

			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(address)
					.queryParam("valence", "0.25")
					.queryParam("arousal", "0.00");
			Invocation.Builder ib = webTarget
					.request(MediaType.TEXT_PLAIN_TYPE);

			Response response = ib.post(Entity.entity(data,
					MediaType.TEXT_PLAIN));
			if (response.getStatus() != 200) {
				String s = response.getStatusInfo().toString();
				response.close();
				throw new IOException(s);
			}
			return response.readEntity(String.class);

		} catch (Exception e) {
			return e.getMessage();
		}
	}

	public static void main(String[] args) {
		System.out.println(post());
	}

}
