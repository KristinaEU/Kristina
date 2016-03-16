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
import gr.iti.kristina.core.qa.rules.ContextCluster;
import gr.iti.kristina.core.state.State;
import gr.iti.kristina.helpers.functions.Print;
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.Binding;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.impl.BindingImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class ContextBuilder {

    private final Multimap<Signature, String> mappings;
    private final State state;
    private final RepositoryConnection kbConnection;
    private final ValueFactory vf;

    //generated in this class
    private HashMultimap<String, Triple> contexts;
    //private Set<Dependency> contextDependencies;
    HashSet<ContextCluster> contextClusters = new HashSet<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ContextBuilder(Multimap<Signature, String> mappings, State state, RepositoryConnection connection) {
        this.mappings = mappings;
        this.state = state;
        this.kbConnection = connection;
        vf = kbConnection.getValueFactory();
    }

    void start() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        contexts = HashMultimap.create();
        HashSet<String> agenda = new HashSet<>();

        Set<Signature> mappingsSet = mappings.keySet();

        String q1 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "select distinct * where {  "
                + "    ?node ?p ?y  . "
                + "    FILTER (!contains(str(?p), \"http://www.w3.org/2000/01/rdf-schema#\"))"
                + "    FILTER (!contains(str(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"))"
                + "    FILTER(?y != ?node && ?y != owl:Thing "
                + "     && ?y != rdfs:Class && ?y != owl:Class && ?y != rdf:Property && ?y != owl:ObjectProperty && ?y != owl:DatatypeProperty "
                + "     && ?p != rdfs:domain && ?p != rdfs:range && ?p != rdfs:label && ?p != rdfs:comment && !isBlank(?y))"
                + "}  ";

        String q2 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "select distinct * where {  "
                + "    ?x ?p ?node  . "
                + "    FILTER (!contains(str(?p), \"http://www.w3.org/2000/01/rdf-schema#\"))"
                + "    FILTER (!contains(str(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"))"
                + "    FILTER(?node != ?x && ?x != owl:Nothing && ?p != rdfs:domain && ?p != rdfs:range && ?p != rdfs:label && ?p != rdfs:comment)"
                + "}  ";

        String q3 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "select distinct * where {  "
                + "    ?x ?node ?y  . "
                + "    FILTER (!contains(str(?p), \"http://www.w3.org/2000/01/rdf-schema#\"))"
                + "    FILTER (!contains(str(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"))"
                + "    FILTER(?node != ?x && ?x != owl:Nothing && ?p != rdfs:domain && ?p != rdfs:range && ?p != rdfs:label && ?p != rdfs:comment && !isBlank(?y))"
                + "}  ";

        for (Signature s : mappingsSet) {
            String _node = mappings.get(s).iterator().next();
            TupleQueryResult result = QueryUtil.evaluateSelectQuery(kbConnection, q1,
                    new BindingImpl("node", vf.createURI(_node)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Binding p = bindingSet.getBinding("p");
                Binding y = bindingSet.getBinding("y");
                contexts.put(s.uri, new Triple(_node, p.getValue().stringValue(), y.getValue().stringValue()));
                if (!"LiteralImpl".equals(y.getValue().getClass().getSimpleName())) {
                    agenda.add(y.getValue().stringValue());
                }
            }
            result.close();
            result = QueryUtil.evaluateSelectQuery(kbConnection, q2,
                    new BindingImpl("node", vf.createURI(_node)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Binding x = bindingSet.getBinding("x");
                Binding p = bindingSet.getBinding("p");
                contexts.put(s.uri, new Triple(x.getValue().stringValue(), p.getValue().stringValue(), _node));
                if (!"LiteralImpl".equals(x.getValue().getClass().getSimpleName())) {
                    agenda.add(x.getValue().stringValue());
                }
                //agenda.add(x.getValue().stringValue());
            }
            result.close();

            result = QueryUtil.evaluateSelectQuery(kbConnection, q3,
                    new BindingImpl("node", vf.createURI(_node)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Binding x = bindingSet.getBinding("x");
                Binding y = bindingSet.getBinding("y");
                contexts.put(s.uri, new Triple(x.getValue().stringValue(), _node, y.getValue().stringValue()));
                if (!"LiteralImpl".equals(y.getValue().getClass().getSimpleName())) {
                    agenda.add(y.getValue().stringValue());
                }
                //agenda.add(y.getValue().stringValue());
                if (!"LiteralImpl".equals(x.getValue().getClass().getSimpleName())) {
                    agenda.add(x.getValue().stringValue());
                }
                //agenda.add(x.getValue().stringValue());
            }
            result.close();

        }

        //one more
        for (String a : agenda) {
            //String _node = mappings.get(s).iterator().next();
            TupleQueryResult result = QueryUtil.evaluateSelectQuery(kbConnection, q1,
                    new BindingImpl("node", vf.createURI(a)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Binding p = bindingSet.getBinding("p");
                Binding y = bindingSet.getBinding("y");
                contexts.put(a, new Triple(a, p.getValue().stringValue(), y.getValue().stringValue()));

            }
            result.close();
            result = QueryUtil.evaluateSelectQuery(kbConnection, q2,
                    new BindingImpl("node", vf.createURI(a)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Binding x = bindingSet.getBinding("x");
                Binding p = bindingSet.getBinding("p");
                contexts.put(a, new Triple(x.getValue().stringValue(), p.getValue().stringValue(), a));
            }
            result.close();

            result = QueryUtil.evaluateSelectQuery(kbConnection, q3,
                    new BindingImpl("node", vf.createURI(a)));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                Binding x = bindingSet.getBinding("x");
                Binding y = bindingSet.getBinding("y");
                contexts.put(a, new Triple(x.getValue().stringValue(), a, y.getValue().stringValue()));
            }
            result.close();
        }

        Print.printMap(contexts);
        System.out.println(Print.flattenCollection(agenda));

        runRuleEngine();
        //mergeContexts();

    }

    private void runRuleEngine() {
        //contextDependencies = new HashSet<>();

        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("contextsKSession");
        kSession.setGlobal("logger", logger);
        kSession.setGlobal("service", this);

        Collection<Map.Entry<String, Triple>> entries = contexts.entries();

        for (Map.Entry<String, Triple> entry : entries) {
            //kSession.insert(entry);
            kSession.insert(entry.getValue());

        }

        kSession.fireAllRules();
        kSession.dispose();
    }

    public boolean match(Triple t1, Triple t2) {
        return t1.s.equals(t2.s) || t1.s.equals(t2.p) || t1.s.equals(t2.o)
                || t1.p.equals(t2.s) || t1.p.equals(t2.p) || t1.p.equals(t2.o)
                || t1.o.equals(t2.s) || t1.o.equals(t2.p) || t1.o.equals(t2.o);
    }

    public boolean matchList(Triple t1, HashSet<Triple> triples) {
        for (Triple t : triples) {
            if (match(t1, t)) {
                return true;
            }
        }
        return false;
    }

    public void logContextCluster(ContextCluster cluster) {
        contextClusters.add(cluster);
    }

//    public void logDependency(Dependency d) {
//        contextDependencies.add(d);
//    }
//    private Set<Triple> mergeContexts() {
//        for (Dependency d : contextDependencies) {
//            String key1 = d.getKey1();
//            String key2 = d.getKey2();
//
//            Set<Triple> c1 = contexts.get(key1);
//            Set<Triple> c2 = contexts.get(key2);
//
//            c1.addAll(c2);
//            return c1;
//
//        }
//    }
    public HashSet<ContextCluster> getContextClusters() {
        return contextClusters;
    }

}
