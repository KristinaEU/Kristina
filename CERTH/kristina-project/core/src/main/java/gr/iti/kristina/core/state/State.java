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

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.ontotext.jena.SesameDataset;
import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import org.openrdf.model.util.GraphUtilException;
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

    //jena
    SesameDataset dataset;
    Model stateModel;
    OntModel stateOntModel;

    State() {
    }

    private void loadDefaultStateOntologies() throws FileNotFoundException, IOException, RDFParseException, RepositoryException {
        stateConnection.begin();
        try (InputStream reader = new FileInputStream("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/imports/dul.rdf")) {
            stateConnection.add(reader, "urn:base", RDFFormat.RDFXML);
        }
        try (InputStream reader = new FileInputStream("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/d5.2/frame_situation_d5.2.ttl")) {
            stateConnection.add(reader, "urn:base", RDFFormat.TURTLE);
        }
        stateConnection.commit();
    }

    public void initialisation(String serverUrl, String username, String password) throws FileNotFoundException, IOException, RepositoryException, RDFParseException, GraphUtilException, RepositoryConfigException, RDFHandlerException {
        manager = new GraphDbRepositoryManager(serverUrl, username, password);
        stateConnection = manager.getRepository("state").getConnection();
        stateConnection.clear();
        loadDefaultStateOntologies();
        OntDocumentManager m = new OntDocumentManager();
        m.setProcessImports(false);
        OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
        s.setDocumentManager(m);
        dataset = new SesameDataset(stateConnection);
        stateModel = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        stateOntModel = ModelFactory.createOntologyModel(s, stateModel);
    }

    public void updateState(String frameSituations) {
        stateOntModel.read(new StringReader(frameSituations), "", "TURTLE");
        stateOntModel.commit();
        OntClass c = stateOntModel.getOntClass("http://www.loa-cnr.it/ontologies/DUL.owl#Situation");
        System.out.println(stateOntModel.size());
        ExtendedIterator<? extends OntResource> listInstances = c.listInstances();
        while (listInstances.hasNext()) {
            System.out.println(listInstances.next());
        }
    }

    public void clearState() throws RepositoryException, IOException, FileNotFoundException, RDFParseException {
        stateConnection.clear();
        loadDefaultStateOntologies();
    }

    public OntModel getStateOntModel() {
        return stateOntModel;
    }
    
    public void close(){
        try {
            stateConnection.close();
            stateRepository.shutDown();
            manager.shutDown();
        } catch (RepositoryException ex) {
            logger.debug("", ex);
        }
    }

}
