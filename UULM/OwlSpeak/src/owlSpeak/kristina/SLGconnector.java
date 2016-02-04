package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class SLGconnector {

	static final String address = "http://kristina.taln.upf.edu/services/mode_selection";
	
	public static void postSLG(String in)  {
		try{
		URL url = new URL(address);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);

		// Create the form content
		BufferedReader br = new BufferedReader(new StringReader(in));
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

		if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
			throw new IOException(conn.getResponseMessage());
		}

		// Buffer the result into a string
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		rd.close();

		conn.disconnect();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
