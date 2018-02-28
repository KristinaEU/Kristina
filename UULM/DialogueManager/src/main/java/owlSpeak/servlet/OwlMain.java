package owlSpeak.servlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.sun.net.httpserver.HttpServer;

import owlSpeak.engine.ServletEngine;
import owlSpeak.engine.Settings;
import owlSpeak.imports.IDACO;
import owlSpeak.imports.IdacoDB;

public class OwlMain {

	protected static ServletEngine owlEngine;
	private static IDACO idaco;

	public static void main(final String args[]) {

		HttpServer server = null;
		HttpServer serverDoc = null;
		OwlSpeakServlet servletInstance = null;

		if (args.length == 0) {
			System.err.println(
					"Please set the following runtime arguments: \nDo you want a file server? [true/false] (should ususally be true)\nThe url of your server, e.g. http://localhost/\nThe port of your server, e.g. 8083\n[optional] further path elements of the url");
		} else {
			try {
				// Start the OwlDocumentServer
				if (args[0].equals("true")) {
					URI baseUriDoc = UriBuilder.fromUri("http://localhost/").port(8080).build();
					ResourceConfig configDoc = new ResourceConfig(OwlDocumentServlet.class);
					serverDoc = JdkHttpServerFactory.createHttpServer(baseUriDoc, configDoc);
				}

				// Initialize the reader
				final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

				// Instantiate a new ServletEngine Object that serves as
				// interface
				// for accessing the ontologies and passes the context of the
				// servlet
				try {
					System.setProperty("owlSpeak.settings.file", "./conf/OwlSpeak/settings.xml");
					owlEngine = new ServletEngine();
				} catch (NullPointerException ex) {
					System.out.println("Critical Error, could not initialize ServletEngine.");
					return;
				}

				// Start the HTTP server
				URI baseUri = UriBuilder.fromUri(args[1]).port(Integer.parseInt(args[2]))
						.path(args.length > 3 ? args[3] : "").build();
				if (Settings.kristina) {
					ResourceConfig config = new ResourceConfig(KristinaServlet.class);
					if (Settings.demo) {
						config = config.registerClasses(KristinaDemoServlet.class);
					}

					server = JdkHttpServerFactory.createHttpServer(baseUri, config);
					KristinaServlet.init(owlEngine, baseUri);
				} else {
					ResourceConfig config = new ResourceConfig(OwlServlet.class);
					server = JdkHttpServerFactory.createHttpServer(baseUri, config);
					OwlServlet.init(owlEngine);
					if (Settings.idaco){
						idaco = new IDACO();
						idaco.main(null);
					}
				}

				System.out.println("Servers started");

				reader.readLine();

			} catch (final Exception exc) {
				exc.printStackTrace();
			} finally {
				// Abort the servers
				if (server != null)
					server.stop(0);

				if (serverDoc != null)
					serverDoc.stop(0);

				if (servletInstance != null)
					servletInstance.destroy();

				if (Settings.kristina)
					KristinaServlet.close();
			}

			// Print information
			System.out.println("Shut down");
		}

	}
}