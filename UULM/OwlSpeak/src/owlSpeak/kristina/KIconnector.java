package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.BufferedWriter;
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

public class KIconnector {
	static final String address = " http://160.40.50.196:8080/api/context/";

	public static String getKI(String data) {
		try {
			URL url = new URL(address + "query?query=" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}

			// Buffer the result into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line + "\n");
			}
			rd.close();

			conn.disconnect();
			return sb.toString();
		} catch (Exception e) {
			return "";
		}
	}
	public static void getKI2(String data) {
		try {
			URL url = new URL(address + "query?query=" + data);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			if (conn.getResponseCode() != 200) {
				throw new IOException(conn.getResponseMessage());
			}

			// Buffer the result into a string
			KIParser.parse(conn.getInputStream());
			

			conn.disconnect();
		} catch (Exception e) {
		
		}
	}

	public static String postKI(String in)  {
		try{
		URL url = new URL(address + "update?frames="+URLEncoder.encode(in, "utf-8")+"&emotions=fake");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setUseCaches(false);
		conn.setAllowUserInteraction(false);
		

		if (conn.getResponseCode() != 200) {
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
		return sb.toString();
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static void main(String[] args) {
		//getKI2("BiographicalAspect");
		//System.out.println(postKI2("frames=fake&emotions=fake"));
		//System.out.println(getKI("test"));
	}
}
