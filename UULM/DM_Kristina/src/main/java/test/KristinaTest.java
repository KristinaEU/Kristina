package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class KristinaTest {

	static final String address = "http://localhost:11153";

	public static void post() {

		try {

			Client client = ClientBuilder.newClient();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(
					System.in));
			File folder = new File("./src/main/resources/ExampleData_LA/08-10-16/");
			for (File entry : folder.listFiles()) {
				if ( entry.getName().startsWith("la")) {

					BufferedReader r = new BufferedReader(
							new InputStreamReader(KristinaTest.class
									.getResourceAsStream("/ExampleData_LA/08-10-16/"
											+ entry.getName())));

					String data = "";
					String tmp = r.readLine();
					while (tmp != null) {
						data = data + tmp + "\n";
						tmp = r.readLine();
					}
					r.close();

					WebTarget webTarget = client.target(address)
							.queryParam("valence", "0.00")
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
					System.out.println("\n" + entry.getName() + "\n");
					System.out.println(response.readEntity(String.class));
					
					reader.readLine();
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
