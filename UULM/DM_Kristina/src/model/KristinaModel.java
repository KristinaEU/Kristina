package model;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class KristinaModel {
	
	public static String[] performUpdate(String usermove){
		
		String workspaceRDF = CerthClient.post(usermove);
		
		//String[] workspace = parseRDF(workspaceRDF);
		String[] workspace = new String[] {workspaceRDF};
		
		return workspace;
	}
	
	private static String[] parseRDF(String rdf){
		Model model = ModelFactory.createDefaultModel();
		model.read(IOUtils.toInputStream(rdf),null);
		
		StmtIterator it = model.listStatements();
		List<String> statements = new LinkedList<String>();
		while(it.hasNext()){
			Statement s = it.next();
			statements.add(s.toString());
		}
		return (String[])statements.toArray();
	}
}
