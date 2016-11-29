package owlSpeak.servlet;

import java.awt.Window;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import presenter.KristinaPresenter;

import com.sun.net.httpserver.HttpServer;

import demo.KristinaDemo;

public class KristinaMain {
	/**
	 * Starts the KRISTINA DM Server.
	 * 
	 * @param args
	 *            When testing the server with KristinaTest, the Arguments
	 *            "http://localhost/" and "11153" have to be given.
	 *            For actual deployment the address and port to be used have 
	 *            to be given.
	 * @throws URISyntaxException 
	 */
	public static void main(final String args[]) {
			HttpServer server = null;
			HttpServer serverDoc = null;
			try {
				
				//Start the OwlDocumentServer
				// Create the server
				URI baseUriDoc = UriBuilder.fromUri("http://localhost/")
						.port(8080).build();
				ResourceConfig configDoc = new ResourceConfig(OwlDocumentServlet.class);
				
				serverDoc = JdkHttpServerFactory.createHttpServer(
						baseUriDoc, configDoc);

				//Start the dialogue manager
				KristinaPresenter.init();
				
				// Print information
				System.out.println("Initializing KRISTINA DM restful server");
				// Initialize the reader
				final BufferedReader reader = new BufferedReader(new InputStreamReader(
						System.in));
				// Start the HTTP server
				
				// Create the server
				URI baseUri = UriBuilder.fromUri(args[0])
						.port(Integer.parseInt(args[1])).path(args.length>2?args[2]:"").build();
				ResourceConfig config = new ResourceConfig(KristinaServlet.class);
				config = config.registerClasses(KristinaDemo.class);
				server = JdkHttpServerFactory.createHttpServer(
						baseUri, config);
				
				reader.readLine();
				

			} catch (final Exception exc) {
				exc.printStackTrace();
			} finally{
				// Abort the server
				if(server != null)
				server.stop(0);
				
				if(serverDoc != null)
				serverDoc.stop(0);
				
				KristinaPresenter.close();
			}
			
		// Print information
		System.out.println("Terminating KRISTINA DM restful server");
		
	}

}
