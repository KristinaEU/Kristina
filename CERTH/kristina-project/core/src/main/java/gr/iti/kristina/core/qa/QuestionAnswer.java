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

import com.google.common.collect.Multimap;
import com.hp.hpl.jena.ontology.OntDocumentManager;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.ReificationStyle;
import gr.iti.kristina.core.qa.rules.ContextCluster;
import gr.iti.kristina.core.state.State;
import gr.iti.kristina.helpers.functions.Print;
import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import gr.iti.kristina.helpers.repository.JenaWrapper;
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.Query;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.util.CommandWrapper;
import org.topbraid.spin.util.JenaUtil;
import org.topbraid.spin.util.SPINQueryFinder;
import org.topbraid.spin.vocabulary.SPIN;

/**
 *
 * @author gmeditsk
 */
public class QuestionAnswer {

    State state;

    GraphDbRepositoryManager manager;
    RepositoryConnection kbConnection;

    final String serverUrl = "http://localhost:8084/graphdb-workbench-free",
            username = "kristina", password = "samiam#2";
    final String repositoryId = "kb";

    static Logger logger = LoggerFactory.getLogger(QuestionAnswer.class);

    public QuestionAnswer(State state) throws RepositoryConfigException, RepositoryException {
        this.state = state;
        manager = new GraphDbRepositoryManager(serverUrl, username, password);
        kbConnection = manager.getRepository(repositoryId).getConnection();
    }

    public void start() throws RepositoryConfigException, RepositoryException {
        try {
            //HashSet<Signature> concepts = conceptExtraction();
            HashSet<Signature> concepts = fakeConceptsFrequency();
            Multimap<Signature, String> mappings = domainMapping(concepts);
            HashSet<ContextCluster> contextClusters = buildContexts(mappings);
        } catch (MalformedQueryException | QueryEvaluationException ex) {
            logger.error("", ex);
        } finally {
            if (kbConnection != null) {
                kbConnection.close();
                manager.shutDown("QuestionAnswer::");
            }
        }

    }

    private HashSet<Signature> conceptExtraction() {
        ConceptExtractor conceptExtractor = new ConceptExtractor(state);
        HashSet<Signature> concepts = conceptExtractor.getConcepts();
        //System.out.println(Print.flattenCollection(concepts));
        return concepts;
    }

//    private void disambiguation() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
    private Multimap<Signature, String> domainMapping(HashSet<Signature> concepts) throws RepositoryConfigException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        DomainMapping match = new DomainMapping(concepts, state, kbConnection);
        match.start();
        return match.getMappings();
    }

    private HashSet<ContextCluster> buildContexts(Multimap<Signature, String> mappings) throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        ContextBuilder ctx = new ContextBuilder(mappings, state, kbConnection);
        ctx.start();
        HashSet<ContextCluster> contextClusters = ctx.getContextClusters();
        System.out.println(contextClusters);
        return contextClusters;
    }

    private HashSet<Signature> fakeConceptsSpouse() {
        HashSet<Signature> result = new HashSet<>();

        Signature s1 = new Signature();
        s1.label = "Natural Person";
        s1.uri = "http://www.loa-cnr.it/ontologies/DUL.owl#NaturalPerson";
        s1.localName = "NaturalPerson";

        Signature s2 = new Signature();
        s2.label = "Name";
        s2.uri = "http://www.loa-cnr.it/ontologies/DUL.owl#Name";
        s2.localName = "Name";

        Signature s3 = new Signature();
        s3.label = "spouse";
        s3.uri = "http://www.loa-cnr.it/ontologies/DUL.owl#Spouse";
        s3.localName = "Spouse";

        result.add(s3);
        result.add(s2);
        result.add(s1);

        return result;
    }

    private HashSet<Signature> fakeConceptsFrequency() {
        HashSet<Signature> result = new HashSet<>();

        Signature s1 = new Signature();
        s1.label = "drink";
        s1.uri = "http://kristina-project.eu/ontologies/entities#Drink";
        s1.localName = "drink";

        Signature s2 = new Signature();
        s2.label = "Water";
        s2.uri = "http://kristina-project.eu/ontologies/entities#Water";
        s2.localName = "Water";
//        
//        Signature s3 = new Signature();
//        s3.label = "spouse";
//        s3.uri = "http://www.loa-cnr.it/ontologies/DUL.owl#Spouse";
//        s3.localName = "Spouse";
//        
//        result.add(s3);
        result.add(s2);
        result.add(s1);

        return result;
    }

    public String demo() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        Map.Entry<String, Collection<String>> currentContext = state.getContextHistory(1).asMap().entrySet().iterator().next();

        JenaWrapper jenaWrapper = new JenaWrapper(state.getStateConnection());
        OntModel stateOntModel = jenaWrapper.getStateOntModel();

        SPINModuleRegistry.get().init();

        Model newTriples = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
        stateOntModel.addSubModel(newTriples);

        OntModel rulesModel = loadModelWithImports("C:/Users/gmeditsk/Dropbox/iti.private/Kristina/ontologies/review2016-demo/rules.ttl");
        Map<CommandWrapper, Map<String, RDFNode>> initialTemplateBindings = new HashMap<>();
        Map<Resource, List<CommandWrapper>> cls2Query = SPINQueryFinder.getClass2QueryMap(rulesModel, rulesModel, SPIN.rule, true, initialTemplateBindings, false);

        Set<Resource> keySet = cls2Query.keySet();
        String logMessage = "";
        for (Resource k : keySet) {
            Resource cl = k;
            if (!currentContext.getValue().contains(cl.getLocalName())) {
                continue;
            }
            CommandWrapper q = cls2Query.get(k).get(0);
            Query prepareQuery = QueryUtil.prepareQuery(state.getStateConnection(), q.getText());
            if (prepareQuery instanceof GraphQuery) {
                GraphQueryResult result = QueryUtil.evaluateConstructQuery(state.getStateConnection(), q.getText());
                kbConnection.begin();
                while (result.hasNext()) {
                    Statement statement = result.next();
                    kbConnection.remove(statement.getSubject(), statement.getPredicate(), null);
                    kbConnection.add(statement);
                    logMessage += "KB will be updated with: " + statement;
                }
                kbConnection.commit();
            }

        }
        return logMessage;
    }

    private static OntModel loadModelWithImports(String filePath) {
        Model baseModel;
        try {
            baseModel = ModelFactory.createDefaultModel(ReificationStyle.Minimal);
            baseModel.read(new FileInputStream(filePath), "", "TTL");

            OntDocumentManager manager = new OntDocumentManager();
            manager.setProcessImports(false); //set false to process imports manually
            OntModelSpec s = new OntModelSpec(OntModelSpec.OWL_MEM);
            s.setDocumentManager(manager);

            return JenaUtil.createOntologyModel(s, baseModel);
        } catch (FileNotFoundException ex) {
            java.util.logging.Logger.getLogger(QuestionAnswer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
