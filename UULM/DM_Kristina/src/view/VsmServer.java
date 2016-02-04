package view;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

import presenter.KristinaPresenter;

@Path("")
public class VsmServer {

	private static final String adress = "http://137.250.171.232/";
	private static final int port = 11153;

	//TODO: disable get
	/**
	 * Execute a GET request on the restful VSM server
	 *
	 * @param cmd
	 *            The command of the query
	 * @param arg
	 *            The argument of the query
	 * @return The result of the query execution
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public synchronized String get(
			@DefaultValue("") @QueryParam("cmd") final String cmd,
			@DefaultValue("") @QueryParam("arg") final String arg) {
		System.out.println("received GET");
		return "Hello World";

	}

	/**
	 * Execute a POST request on the restful VSM server
	 *
	 * @param obj
	 *            The argument of the query
	 * @return The result of the query execution
	 */
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.TEXT_PLAIN)
	public synchronized String post(final String in) {

		System.out.println("received POST");

		String result = KristinaPresenter.performDM(in);

		System.out.println("DM done");

		return result;
	}

	public static void main(final String args[]) {
		// Print information
		System.out.println("Initializing KRISTINA DM restful server");
		// Initialize the reader
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				System.in));
		// Start the HTTP server
		try {
			// Create the server
			URI baseUri = UriBuilder.fromUri(adress).port(port).build();
			// TODO: check config
			ResourceConfig config = new ResourceConfig(VsmServer.class);
			final HttpServer server = JdkHttpServerFactory.createHttpServer(
					baseUri, config);
			
			final String in = reader.readLine();

			// Abort the server
			server.stop(0);
		} catch (final Exception exc) {
			exc.printStackTrace();
		}
		// Print information
		System.out.println("Terminating KRISTINA DM restful server");
	}

}


