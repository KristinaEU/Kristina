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
package gr.iti.kristina.helpers.repository;

import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.ontotext.jena.SesameDataset;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class JenaWrapper {

    RepositoryConnection connection;

    SesameDataset dataset;
    Model stateModel;
    OntModel stateOntModel;

    static Logger logger = LoggerFactory.getLogger(JenaWrapper.class);

    public JenaWrapper(RepositoryConnection connection) {
        this.connection = connection;

        OntDocumentManager m = new OntDocumentManager();
        m.setProcessImports(false);
        OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_DL_MEM);
        s.setDocumentManager(m);
        dataset = new SesameDataset(this.connection);
        stateModel = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        stateOntModel = ModelFactory.createOntologyModel(s, stateModel);
    }

    public SesameDataset getDataset() {
        return dataset;
    }

    public Model getStateModel() {
        return stateModel;
    }

    public OntModel getStateOntModel() {
        return stateOntModel;
    }

//    public void close() {
//        logger.debug("closing jena wrapper");
//        if (connection != null) {
//            try {
//                connection.close();
//            } catch (RepositoryException ex) {
//                logger.error("", ex);
//            }
//        }
//    }

}
