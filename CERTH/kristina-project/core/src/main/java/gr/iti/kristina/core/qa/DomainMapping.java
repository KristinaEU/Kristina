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
package gr.iti.kristina.core.qa;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import gr.iti.kristina.core.state.State;
import gr.iti.kristina.helpers.functions.Print;
import gr.iti.kristina.helpers.repository.JenaWrapper;
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.util.HashSet;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.BindingImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class DomainMapping {

    private final HashSet<Signature> concepts;
    private final State state;
    private Multimap<Signature, String> mappings;

//    GraphDbRepositoryManager manager;
//    Repository kbRepository;
    RepositoryConnection kbConnection;
    ValueFactory vf;

    JenaWrapper jenaWrapper;

    final String serverUrl = "http://localhost:8084/graphdb-workbench-free",
            username = "kristina", password = "samiam#2";
    final String repositoryId = "kb";

    Logger logger = LoggerFactory.getLogger(DomainMapping.class);

    //no connection provided
//    DomainMapping(HashSet<Signature> concepts, State state) throws RepositoryConfigException, RepositoryException {
//        this.concepts = concepts;
//        this.state = state;
//
//        manager = new GraphDbRepositoryManager(serverUrl, username, password);
//        kbConnection = manager.getRepository(repositoryId).getConnection();
//        vf = kbConnection.getValueFactory();
//
//        jenaWrapper = new JenaWrapper(kbConnection);
//    }
    //reuse existing connection
    DomainMapping(HashSet<Signature> concepts, State state, RepositoryConnection connection) throws RepositoryConfigException, RepositoryException {
        this.concepts = concepts;
        this.state = state;
        kbConnection = connection;
        vf = kbConnection.getValueFactory();
        jenaWrapper = new JenaWrapper(kbConnection);
    }

    public Multimap<Signature, String> start() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        mappings = HashMultimap.create();

        String q = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  "
                + "PREFIX f: <http://www.kristina.eu/sparql/functions/> "
                + "select ?x ?s where { "
                + "    ?x rdfs:label ?label. "
                + "    FILTER (lang(?label) = 'en' || lang(?label) = '')  "
                + "    BIND (f:stringMatch(?label, ?node) as ?s) "
                + "} ORDER BY DESC(?s) LIMIT 1";

        for (Signature s : concepts) {
            logger.debug(String.format("processing key concept %s", s));

            //TODO need to remove the limit and try to disambiguate when >1 results...
            TupleQueryResult result = QueryUtil.evaluateSelectQuery(kbConnection, q, new BindingImpl("node", vf.createLiteral(s.label)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                URI x = (URI) bindingSet.getBinding("x").getValue();
                mappings.put(s, x.toString());
            }
            result.close();
        }
        Print.printMap(mappings);
        return mappings;
    }

//    public void close() {
//        logger.debug("closing domain matching module");
//        try {
//            if (kbConnection != null) {
//                kbConnection.close();
//                jenaWrapper.close();
//                manager.shutDown("DomainMatchingModule::");
//            }
//        } catch (RepositoryException ex) {
//            logger.debug("", ex);
//        }
//    }
    public Multimap<Signature, String> getMappings() {
        return mappings;
    }

}
