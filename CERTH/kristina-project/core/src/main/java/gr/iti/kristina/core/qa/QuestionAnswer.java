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
import gr.iti.kristina.core.qa.rules.ContextCluster;
import gr.iti.kristina.core.state.State;
import gr.iti.kristina.helpers.functions.Print;
import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import java.util.HashSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            HashSet<Signature> concepts  = fakeConceptsFrequency();
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
}
