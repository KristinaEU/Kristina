package owlSpeak.kristina;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Enumeration;

import javax.servlet.*;
import javax.servlet.http.*;

public class KristinaServlet extends HttpServlet {

	public void init() throws ServletException {
		// Do required initialization
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// Set response content type
		response.setContentType("text/html");
		
		//send to KI

		BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String str = in.readLine();
		while(str!= null){
			sb.append(str);
			str = in.readLine();
		}
		String proposal = KIconnector.postKI(sb.toString());
		
		//process data
		String sysmov = TmpOwlSpeak.processProposal(proposal);
		
		//send to SLG
		//SLGconnector.postSLG(sysmov);

		// Rückmeldung an VSM
		PrintWriter out = response.getWriter();
		out.println(sysmov);
		/*BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String str = in.readLine();
		while(str!= null){
			out.println(str);
			str = in.readLine();
		}*/
		
	
	}

	public void destroy() {
		// do nothing.
	}
}
