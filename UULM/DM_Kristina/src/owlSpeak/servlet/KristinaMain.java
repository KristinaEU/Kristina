package owlSpeak.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

public class KristinaMain {
	/**
	 * Starts the KRISTINA DM Server.
	 * 
	 * @param args
	 *            When testing the server with KristinaTest, the Arguments
	 *            "http://localhost/" and "11153" have to be given.
	 *            For actual deployment the address and port to be used have 
	 *            to be given.
	 */
	public static void main(final String args[]) {
		// Print information
		System.out.println("Initializing KRISTINA DM restful server");
		// Initialize the reader
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		// Start the HTTP server
		try {
			// Create the server
			URI baseUri = UriBuilder.fromUri(args[0])
					.port(Integer.parseInt(args[1])).build();
			ResourceConfig config = new ResourceConfig(KristinaServlet.class);
			final HttpServer server = JdkHttpServerFactory.createHttpServer(
					baseUri, config);

			reader.readLine();

			// Abort the server
			server.stop(0);
		} catch (final Exception exc) {
			exc.printStackTrace();
		}
		// Print information
		System.out.println("Terminating KRISTINA DM restful server");
	}

}
