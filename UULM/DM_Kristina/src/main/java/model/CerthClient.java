package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.FileHandler;
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

import owlSpeak.Move;

public class CerthClient {
	
	static final String address = "http://160.40.50.196:8080/kristina-j2ee-web/api/context/";
	private final static Logger logger = Logger.getLogger("LoggerTmp");
	private static Handler handler;
	private static String path = "";
	
	public static String get(String data) throws Exception {
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
	}

	public static String post(String in, double valence, double arousal, String user)  throws Exception{
			Client client = ClientBuilder.newClient();
			
			WebTarget webTarget = client.target(address+"update");
			
			Invocation.Builder ib = webTarget.request(MediaType.TEXT_PLAIN_TYPE);
			
			/*List<String> l2 = new LinkedList<String>();
			l2.add(in);
			Files.write(Paths.get("src/main/resources/results/DM2KI/"+ path.replace("la", "dm2ki").replace("owl","ttl")), l2,StandardCharsets.UTF_8);
		*/
			Response response = ib.post(Entity.entity("frames="+URLEncoder.encode(in, "utf-8")+"\n&emotions="+URLEncoder.encode("{valence:"+valence+",arousal:"+arousal+"}", "utf-8")+"\n&username="+URLEncoder.encode(user, "utf-8"), "application/x-www-form-urlencoded"));
			
			if(response.getStatus()!= 200){
				String s = response.getStatusInfo().toString();
				response.close();
				throw new IOException(s);
			}
			
			String result = response.readEntity(String.class);
			
			
			/*List<String> l = new LinkedList<String>();
			l.add(result);
			Files.write(Paths.get("src/main/resources/results/KI2DM/"+ path.replace("la", "ki").replace("owl","ttl")), l, StandardCharsets.UTF_8);
			*/
			return result;
			
	}
	
	public static void setPath(String p){
		
		
		path = p;
	}

}
