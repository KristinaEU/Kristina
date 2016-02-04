package test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class KristinaTest {

	static final String address = "http://137.250.171.232:11153";

	public static String post() {

		try {
			BufferedReader br = new BufferedReader(new FileReader("../tmpFiles/inform.ttl"));
			
			
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setAllowUserInteraction(false);
			conn.setRequestProperty("Content-Type", "text/plain");

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

	public static void main(String[] args) {
		System.out.println(post());
	}
	
}
