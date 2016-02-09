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
package gr.iti.kristina.core.qa.signature;

import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import gr.iti.kristina.helpers.repository.utils.EmbeddedGraphDB;
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.util.GraphUtilException;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParseException;

/**
 *
 * @author gmeditsk
 */
public class Test {

    GraphDbRepositoryManager manager;
    Repository frameRepository, ofnRepository;
    RepositoryConnection frameConnection, ofnConnection;

    public Test(String serverUrl, String username, String password) throws IOException, RepositoryException, RDFParseException, GraphUtilException, RepositoryConfigException, RDFHandlerException {
        manager = new GraphDbRepositoryManager(serverUrl, username, password);
    }

    public void close() throws RepositoryException {
        frameConnection.close();
        ofnConnection.close();
    }

//    public void loadData() throws IOException, RepositoryException, RDFParseException {
//        frameConnection.clear();
//        frameConnection.begin();
//        try (Reader reader = new BufferedReader(new FileReader("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/examples/inform.ttl"))) {
//            frameConnection.add(reader, "urn:base", RDFFormat.TURTLE);
//        }
//        try (Reader reader = new BufferedReader(new FileReader("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/imports/dul.rdf"))) {
//            frameConnection.add(reader, "urn:base", RDFFormat.RDFXML);
//        }
//        try (Reader reader = new BufferedReader(new FileReader("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/d5.2/frame_situation_d5.2.ttl"))) {
//            frameConnection.add(reader, "urn:base", RDFFormat.TURTLE);
//        }
//        frameConnection.commit();
//        System.out.println(frameConnection.size());
//        
//        ofnConnection.clear();
//        ofnConnection.begin();
//        try (Reader reader = new BufferedReader(new FileReader("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/imports/ofn.rdf"))) {
//            ofnConnection.add(reader, "urn:base", RDFFormat.RDFXML);
//        }
//        try (Reader reader = new BufferedReader(new FileReader("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/imports/ofntb.rdf"))) {
//            ofnConnection.add(reader, "urn:base", RDFFormat.RDFXML);
//        }
//        ofnConnection.commit();
//        System.out.println(ofnConnection.size());
//    }

    public void buildSignatures() throws MalformedQueryException, RepositoryException, QueryEvaluationException, RepositoryConfigException {
        
        List<URI> frames = getFrames();

        // get all frame instances + FE instances + classified instances  
        String keyConceptsQ = ""
                + "PREFIX core: <http://kristina-project.eu/ontologies/framesituation/core#>\n"
                + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "select ?frame ?classified ?fe where { \n"
                + "	?frame a core:FrameSituation ;\n"
                + "        dul:isSettingFor ?classified.\n"
                + "    ?classified dul:isClassifiedBy ?fe .\n"
                + "} ";

        TupleQueryResult result = QueryUtil.evaluateSelectQuery(frameConnection, keyConceptsQ);
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            URI frame = (URI) bindingSet.getBinding("frame").getValue();
            URI fe = (URI) bindingSet.getBinding("fe").getValue();
            URI classified = (URI) bindingSet.getBinding("classified").getValue();

            System.out.println(frame.getLocalName() + " " + fe.getLocalName() + " " + classified.getLocalName());
        }
        result.close();

    }

    public List<URI> getFrames() throws QueryEvaluationException, MalformedQueryException, RepositoryException, RepositoryConfigException {
        List<URI> frames = new ArrayList();
        String frameQ = ""
                + "PREFIX core: <http://kristina-project.eu/ontologies/framesituation/core#>\n"
                + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "select ?frame where { \n"
                + "	?frame a core:FrameSituation .\n"
                + "} ";

        RepositoryConnection connection = manager.getRepository("frames").getConnection();
        TupleQueryResult result = QueryUtil.evaluateSelectQuery(connection, frameQ);
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            URI frame = (URI) bindingSet.getBinding("frame").getValue();
            frames.add(frame);
        }
        result.close();
        return frames;
    }
    
    public List<URI> getClassifiedResources(RepositoryConnection connection) throws QueryEvaluationException, MalformedQueryException, RepositoryException {
        List<URI> resources = new ArrayList();
        String frameQ = ""
                + "PREFIX core: <http://kristina-project.eu/ontologies/framesituation/core#>\n"
                + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "select ?frame where { \n"
                + "	?frame a core:FrameSituation .\n"
                + "} ";

        TupleQueryResult result = QueryUtil.evaluateSelectQuery(connection, frameQ);
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            URI frame = (URI) bindingSet.getBinding("frame").getValue();
            resources.add(frame);
        }
        result.close();
        return resources;
    }
    
    

    public static void main(String[] args) {
        Test t = null;
        try {
            t = new Test("","","");
            //t.loadData();
            //t.buildSignatures();
        } catch (IOException | RepositoryException | RDFParseException | GraphUtilException | RepositoryConfigException | RDFHandlerException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                t.close();
            } catch (RepositoryException ex) {
                Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
