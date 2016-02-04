package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class ServletTest {
	static final String address = "http://localhost:8080/owlSpeak";

	public static String postTest() {

		try {
			BufferedReader br = new BufferedReader(new FileReader("../tmpFiles/inform.ttl"));
			
			
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type", "rdf/xml");

			// Create the form content
			OutputStream out = conn.getOutputStream();
			Writer writer = new BufferedWriter(new OutputStreamWriter(out));
			String str = br.readLine();
			while(str != null){
				writer.write(str+"\n");
				str = br.readLine();
			}
			br.close();
			writer.close();
			out.close();

			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}

			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line+"\n");
			}
			rd.close();

			conn.disconnect();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void createAgendas(String str) {

	}

	public static void main(String[] args) {
		System.out.println(postTest());
	}
}
