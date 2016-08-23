package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.filter.LoggingFilter;

import owlSpeak.Move;

public class CerthClient {
	
	static final String address = "http://160.40.50.196:8080/kristina-j2ee-web/api/context/";

	public static String get(String data) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget webTarget = client.target(address+"query").queryParam("query", data);
			
			Invocation.Builder ib = webTarget.request(MediaType.TEXT_PLAIN_TYPE);
			
			Response response = ib.get();
			if(response.getStatus()!= 200){
				String s = response.getStatusInfo().toString();
				response.close();
				throw new IOException(s);
			}
			return response.readEntity(String.class);
		} catch (Exception e) {
			return "";
		}
	}

	public static String post(String in, double valence, double arousal)  {
		try{
			Client client = ClientBuilder.newClient();
			
			WebTarget webTarget = client.target(address+"update");
			
			Invocation.Builder ib = webTarget.request(MediaType.TEXT_PLAIN_TYPE);
			
			Response response = ib.post(Entity.entity("frames="+URLEncoder.encode(in, "utf-8")+"\n&emotions="+URLEncoder.encode("{valence:"+valence+",arousal:"+arousal+"}", "utf-8"), "application/x-www-form-urlencoded"));

			if(response.getStatus()!= 200){
				String s = response.getStatusInfo().toString();
				response.close();
				throw new IOException(s);
			}
			return response.readEntity(String.class);
			
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
