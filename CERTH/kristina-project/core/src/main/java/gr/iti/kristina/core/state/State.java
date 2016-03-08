/*
 * The MIT License
 *
 * Copyright 2016 gmeditsk.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package gr.iti.kristina.core.state;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.hp.hpl.jena.ontology.OntModel;
//import com.hp.hpl.jena.vocabulary.RDF;
import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import gr.iti.kristina.helpers.repository.JenaWrapper;
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Date;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.query.impl.BindingImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class State {

    Logger logger = LoggerFactory.getLogger(State.class);

    //sesame
    GraphDbRepositoryManager manager;
    Repository stateRepository;
    RepositoryConnection stateConnection;
    ValueFactory vf;

    //jena wrapper
    JenaWrapper jenaWrapper;

    State() {
    }

    private void loadDefaultStateOntologies() throws FileNotFoundException, IOException, RDFParseException, RepositoryException {
        stateConnection.clear();
        stateConnection.begin();
        try (InputStream reader = new FileInputStream("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/imports/dul.rdf")) {
            stateConnection.add(reader, "urn:base", RDFFormat.RDFXML);
        }
        try (InputStream reader = new FileInputStream("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/d5.2/frame_situation_d5.2.ttl")) {
            stateConnection.add(reader, "urn:base", RDFFormat.TURTLE);
        }
        stateConnection.commit();
    }

    public void initialisation(String serverUrl, String username, String password, boolean reload) throws FileNotFoundException, IOException, RepositoryException, RDFParseException, GraphUtilException, RepositoryConfigException, RDFHandlerException {
        manager = new GraphDbRepositoryManager(serverUrl, username, password);
        stateConnection = manager.getRepository("state").getConnection();
        vf = stateConnection.getValueFactory();
        if (reload) {
            loadDefaultStateOntologies();
        }

        jenaWrapper = new JenaWrapper(stateConnection);
    }

    public String updateState(String frameSituations) throws RepositoryException, MalformedQueryException, QueryEvaluationException, UpdateExecutionException {
        logger.debug("updating state...");
        OntModel stateOntModel = jenaWrapper.getStateOntModel();
        stateOntModel.read(new StringReader(frameSituations), "", "TURTLE");
        stateOntModel.commit();
//        URI situation = vf.createURI("http://www.loa-cnr.it/ontologies/DUL.owl#Situation");
//        URI timestamp = vf.createURI("http://www.w3.org/2006/time#inXSDDateTime");
//        //URI current = vf.createURI("http://kristina-project.eu/demo/schema#current");
//        RepositoryResult<Statement> statements = stateConnection.getStatements(null, RDF.TYPE, situation, true);
        org.openrdf.model.Literal t = vf.createLiteral(true);
//        org.openrdf.model.Literal f = vf.createLiteral(false);
        org.openrdf.model.Literal nowL = vf.createLiteral(new Date());
//        stateConnection.begin();
//        while (statements.hasNext()) {
//            System.out.println("in");
//            Statement statement = statements.next();
//            if (!stateConnection.hasStatement(statement.getSubject(), timestamp, null, true)) {
//                stateConnection.add(statement.getSubject(), timestamp, nowL);
//                //stateConnection.add(statement.getSubject(), current, t);
//            } else {
////                stateConnection.remove(statement.getSubject(), current, t);
//                //stateConnection.add(statement.getSubject(), current, f);
//            }
//        }
//        statements.close();
//
//        stateConnection.commit();

        String q = "INSERT\n"
                + "{\n"
                + "  ?x <http://www.w3.org/2006/time#inXSDDateTime> ?timestamp .\n"
                + "  ?x <http://kristina-project.eu/demo/schema#current> ?true .\n"
                + "}\n"
                + "WHERE\n"
                + "{\n"
                + "  ?x a <http://www.loa-cnr.it/ontologies/DUL.owl#Situation>.\n"
                + "  FILTER NOT EXISTS {\n"
                + "	?x <http://www.w3.org/2006/time#inXSDDateTime> [] .\n"
                + "  }\n"
                + "}";

        //GraphQueryResult result = QueryUtil.evaluateConstructQuery(stateConnection, q, new BindingImpl("timestamp", nowL), new BindingImpl("true", t));
        Update prepareUpdate = stateConnection.prepareUpdate(QueryLanguage.SPARQL, q);
        prepareUpdate.setBinding("timestamp", nowL);
        prepareUpdate.setBinding("true", t);
        prepareUpdate.execute();
//        while (result.hasNext()) {
//            Statement next = result.next();
//            stateConnection.add(next);
//        }
//        result.close();
//        stateConnection.commit();

        logger.debug("state has been updated");
        String logMessage = "State has been updated";
        return logMessage;
    }

    public void clearState() throws RepositoryException, IOException, FileNotFoundException, RDFParseException {
        stateConnection.clear();
        loadDefaultStateOntologies();
    }

    public OntModel getStateOntModel() {
        return jenaWrapper.getStateOntModel();
    }

    public RepositoryConnection getStateConnection() {
        return stateConnection;
    }

    public void close() {
        try {
            stateConnection.close();
//            jenaWrapper.close();
//            stateRepository.shutDown();
            manager.shutDown("State::");
        } catch (RepositoryException ex) {
            logger.debug("", ex);
        }
    }

    public Multimap<String, String> getContextHistory(int recentItems) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        Multimap<String, String> multimap = LinkedListMultimap.create();
        if (recentItems == 0) {
            recentItems = 999999;
        }

        String q = "PREFIX schema: <http://kristina-project.eu/demo/schema#>\n"
                + "PREFIX DUL: <http://www.loa-cnr.it/ontologies/DUL.owl#>\n"
                + "PREFIX time: <http://www.w3.org/2006/time#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "\n"
                + "select ?directClass ?timestamp where { \n"
                + "	?resource a DUL:Situation;\n"
                + "        time:inXSDDateTime ?timestamp .\n"
                + "    ?resource a ?directClass . \n"
                + "   	FILTER (!isBlank(?directClass)) . \n"
                + "    FILTER (?directClass != owl:NamedIndividual && CONTAINS(str(?directClass), \"http://kristina-project.eu/demo/schema#\")) .\n"
                + "    FILTER NOT EXISTS {\n"
                + "		?temp rdfs:subClassOf owl:Thing . \n"
                + "		FILTER (((?temp != owl:Thing) && (?temp != ?directClass)) && (!isBlank(?temp))) . \n"
                + "        ?resource a ?temp . \n"
                + "        ?temp rdfs:subClassOf ?directClass . \n"
                + "    }\n"
                + "} ORDER BY DESC(?timestamp) ";

        TupleQueryResult result = QueryUtil.evaluateSelectQuery(stateConnection, q);
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            //URI resource = (URI) bindingSet.getBinding("resource").getValue();
            URI directClass = (URI) bindingSet.getBinding("directClass").getValue();
            String timestamp = bindingSet.getBinding("timestamp").getValue().stringValue();
            //multimap.put(timestamp, resource.getLocalName());
            multimap.put(timestamp, directClass.getLocalName());
        }
        result.close();
        return multimap;
    }

}
