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
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
    HashSet<Triple> collectedTriples = new HashSet<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private HashSet<Triple> bridges = new HashSet<>();
    private HashSet<String> bridgeResource = new HashSet<>();

    String q1 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            + "select distinct * where {  "
            + "    ?node ?p ?y  . "
            //            + "    FILTER (!contains(str(?p), \"http://www.w3.org/2000/01/rdf-schema#\"))"
            //            + "    FILTER (!contains(str(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"))"
            + "    FILTER(?y != ?node && ?y != owl:Thing "
            + "     && ?y != rdfs:Class && ?y != owl:Class && ?y != rdf:Property && ?y != owl:ObjectProperty && ?y != owl:DatatypeProperty "
            + "     && ?p != rdfs:domain && ?p != rdfs:range&& ?p != owl:disjointWith && ?p != rdfs:subClassOf && ?p != rdfs:label && ?p != rdfs:comment && !isBlank(?y))"
            + "}  ";

    String q2 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            + "select distinct * where {  "
            + "    ?x ?p ?node  . "
            //            + "    FILTER (!contains(str(?p), \"http://www.w3.org/2000/01/rdf-schema#\"))"
            //            + "    FILTER (!contains(str(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"))"
            + "    FILTER(?node != ?x && ?x != owl:Nothing && ?p != owl:disjointWith && ?p != rdfs:domain && ?p != rdfs:subClassOf && ?p != rdfs:range && ?p != rdfs:label && ?p != rdfs:comment)"
            + "}  ";

    String q3 = "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
            + "select distinct * where {  "
            + "    ?x ?node ?y  . "
            //            + "    FILTER (!contains(str(?p), \"http://www.w3.org/2000/01/rdf-schema#\"))"
            //            + "    FILTER (!contains(str(?p), \"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"))"
            + "    FILTER(?node != ?x && ?x != owl:Nothing && ?p != owl:disjointWith && ?p != rdfs:domain&& ?p != rdfs:subClassOf && ?p != rdfs:range && ?p != rdfs:label && ?p != rdfs:comment && !isBlank(?y))"
            + "}  ";

    public ContextBuilder(Multimap<Signature, String> mappings, State state, RepositoryConnection connection) {
        this.mappings = mappings;
        this.state = state;
        this.kbConnection = connection;
        vf = kbConnection.getValueFactory();
    }

    void start() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        contexts = HashMultimap.create();

        Set<Signature> mappingsSet = mappings.keySet();

//        for (Signature s : mappingsSet) {
//            String _node = mappings.get(s).iterator().next();
//            HashMultimap<String, String> updateContexts = updateContexts(s.uri, _node, q1, q2, q3);
//            Set<String> keySet = updateContexts.keySet();
//            for (String k : keySet) {
//                Set<String> get = updateContexts.get(k);
//                for (String tt : get) {
//                    //updateContexts(k, tt, q1, q2, q3);
//                }
//
//            }
//
//        }
        for (Signature s : mappingsSet) {
            LinkedList<Path> S = expand(s.uri, 3);
            System.out.println("**********************" + s.localName);
            for (Path linkedList : S) {
                System.out.println(linkedList);
            }
            System.out.println("end");
        }

        //Print.printMap(contexts);
        //runRuleEngine();
    }

    public int size(LinkedList<TripleValue> path) {
        //System.out.println("size: " + path);
        LinkedList<TripleValue> visit = new LinkedList<>();
        int count = 0;
        for (TripleValue tv : path) {
            boolean found = false;
            for (TripleValue v : visit) {
                if ((v.s.equals(tv.s) && v.o.equals(tv.o)) || (v.s.equals(tv.o) && v.o.equals(tv.s))) {
                    found = true;
                    break;
                }
            }
            visit.add(tv);
            if (!found) {
                count++;
            }
        }
        //System.out.println("size:" +count);
        return count;

    }

    public LinkedList<Path> expand(String keyEntity, int thres) throws QueryEvaluationException, MalformedQueryException, RepositoryException {
        LinkedList<Path> S = new LinkedList<>();
        LinkedList<Path> expand = new LinkedList<>();
        HashSet<Value> visitedNodes = new HashSet<>();

        URI keyEntityValue = vf.createURI(keyEntity);

        HashSet<TripleValue> triples = expandTriples(keyEntityValue, q1, q2, q3);
        for (TripleValue t : triples) {
            t.level = 1;
            Path p = new Path();
            p.level = 1;
            p.triples.add(t);
            expand.add(p);
        }
        //System.out.println(expand);
        visitedNodes.add(keyEntityValue);

        while (!expand.isEmpty()) {
            Path path = expand.pop();
            //System.out.println("pop " + path);
            if (path.level >= thres) {
                //System.out.println("in1");
                S.add(path);
                //System.out.println("path: " + path);
                //System.out.println("full");
                //S.add(path);
                continue;
            }

            TripleValue currentTriple = path.triples.getLast();
            //System.out.println("poll:" + currentTriple);
            Value nodeToExpand = getFreeTripleNode(currentTriple, visitedNodes);
            //System.out.println("node to expand: " + nodeToExpand);
            if (nodeToExpand == null) {
                //System.out.println("in2");
                S.add(path);
                continue;
            }

            visitedNodes.add(nodeToExpand);
            HashSet<TripleValue> stepTriples = expandTriples(nodeToExpand, q1, q2, q3);
            HashSet<TripleValue> temp = new HashSet<>();
            for (TripleValue stepTriple : stepTriples) {
                //System.out.println(stepTriple);
                Value free = getFreeTripleNode(stepTriple, visitedNodes);
                //System.out.println("free node: " + free);
                if (free == null) {
                    //System.out.println("continue");
                    continue;
                }
                //System.out.println("step triple" + stepTriple);
                temp.add(stepTriple);
            }
            //LinkedList<TripleValue> new_path = new LinkedList<>(path);
            //System.out.println("temp" + temp);
            //System.out.println("path" + path);
            if (temp.isEmpty()) {
                //System.out.println("no expanion");
                //System.out.println("in3");
                S.add(path);
            } else {
                path.level++;
                for (TripleValue t : temp) {
                    path.triples.add(t);
                }
                expand.add(path);
            }
            //System.out.println("path: " + path);

        }
        return S;
    }

    public Value getFreeTripleNode(TripleValue t, HashSet<Value> visitedNodes) {
        if (!visitedNodes.contains(t.getS())) {
            return t.getS();
        } else if (!visitedNodes.contains(t.getO())) {
            return t.getO();
        } else {
            return null;
        }
    }

    public HashSet<TripleValue> expandTriples(Value node, String q1, String q2, String q3) throws QueryEvaluationException, MalformedQueryException, RepositoryException {
        HashSet<TripleValue> agenda = new HashSet<>();

        TupleQueryResult result = QueryUtil.evaluateSelectQuery(kbConnection, q1,
                new BindingImpl("node", node));
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Binding p = bindingSet.getBinding("p");
            Binding y = bindingSet.getBinding("y");
            agenda.add(new TripleValue(node, p.getValue(), y.getValue()));
        }
        result.close();
        result = QueryUtil.evaluateSelectQuery(kbConnection, q2,
                new BindingImpl("node", node));
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Binding x = bindingSet.getBinding("x");
            Binding p = bindingSet.getBinding("p");
            agenda.add(new TripleValue(x.getValue(), p.getValue(), node));
        }
        result.close();
        result = QueryUtil.evaluateSelectQuery(kbConnection, q3,
                new BindingImpl("node", node));
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Binding x = bindingSet.getBinding("x");
            Binding y = bindingSet.getBinding("y");
            agenda.add(new TripleValue(x.getValue(), node, y.getValue()));
//            if (!"LiteralImpl".equals(y.getValue().getClass().getSimpleName())) {
//                agenda.put(s, y.getValue().stringValue());
//                System.out.println(y.getValue().stringValue());
//            }
//            //agenda.add(y.getValue().stringValue());
//            if (!"LiteralImpl".equals(x.getValue().getClass().getSimpleName())) {
//                agenda.put(s, x.getValue().stringValue());
//                //System.out.println(x.getValue().stringValue());
//            }
//            //agenda.add(x.getValue().stringValue());
        }
        result.close();
        return agenda;
    }

    public HashMultimap<String, String> updateContexts(String s, String _node, String q1, String q2, String q3) throws QueryEvaluationException, MalformedQueryException, RepositoryException {
        HashMultimap<String, String> agenda = HashMultimap.create();
        //System.out.println("node: " + _node);
        TupleQueryResult result = QueryUtil.evaluateSelectQuery(kbConnection, q1,
                new BindingImpl("node", vf.createURI(_node)));
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Binding p = bindingSet.getBinding("p");
            Binding y = bindingSet.getBinding("y");
            contexts.put(s, new Triple(_node, p.getValue().stringValue(), y.getValue().stringValue()));
            if (!"LiteralImpl".equals(y.getValue().getClass().getSimpleName())) {
                //System.out.println(y.getValue().stringValue());
                agenda.put(s, y.getValue().stringValue());
            }
        }
        result.close();
        result = QueryUtil.evaluateSelectQuery(kbConnection, q2,
                new BindingImpl("node", vf.createURI(_node)));
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            Binding x = bindingSet.getBinding("x");
            Binding p = bindingSet.getBinding("p");
            contexts.put(s, new Triple(x.getValue().stringValue(), p.getValue().stringValue(), _node));
            if (!"LiteralImpl".equals(x.getValue().getClass().getSimpleName())) {
                agenda.put(s, x.getValue().stringValue());
                //System.out.println(x.getValue().stringValue());
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
            contexts.put(s, new Triple(x.getValue().stringValue(), _node, y.getValue().stringValue()));
            if (!"LiteralImpl".equals(y.getValue().getClass().getSimpleName())) {
                agenda.put(s, y.getValue().stringValue());
                System.out.println(y.getValue().stringValue());
            }
            //agenda.add(y.getValue().stringValue());
            if (!"LiteralImpl".equals(x.getValue().getClass().getSimpleName())) {
                agenda.put(s, x.getValue().stringValue());
                //System.out.println(x.getValue().stringValue());
            }
            //agenda.add(x.getValue().stringValue());
        }
        result.close();
        return agenda;
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
            kSession.insert(entry);
            //kSession.insert(entry.getValue());

        }

        kSession.fireAllRules();
        kSession.dispose();
        //System.out.println("bridges");
        //System.out.println(Print.flattenCollection(bridgeResource));
    }

    public boolean match(Triple t1, Triple t2) {
        return t1.s.equals(t2.s) || t1.s.equals(t2.p) || t1.s.equals(t2.o)
                //|| t1.p.equals(t2.s) || t1.p.equals(t2.p) || t1.p.equals(t2.o)
                || t1.o.equals(t2.s) || t1.o.equals(t2.p) || t1.o.equals(t2.o);
    }

    public boolean match(String r, Triple t2) {
        return t2.s.equals(r) || t2.s.equals(r) || t2.s.equals(r)
                //|| t1.p.equals(t2.s) || t1.p.equals(t2.p) || t1.p.equals(t2.o)
                || t2.o.equals(r) || t2.o.equals(r) || t2.o.equals(r);
    }

    public HashSet<String> bridge(Triple t1, Triple t2) {
        HashSet<String> bridges = new HashSet<>();
        if (t1.s.equals(t2.s) || t1.s.equals(t2.p) || t1.s.equals(t2.o)) {
            bridges.add(t1.s);
        }
        if (t1.o.equals(t2.s) || t1.o.equals(t2.p) || t1.o.equals(t2.o)) {
            bridges.add(t1.o);
        }

        return bridges;
    }

    public boolean matchList(Triple t1, HashSet<Triple> triples) {
        for (Triple t : triples) {
            if (match(t1, t)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchResourceList(String r, HashSet<Triple> triples) {
        for (Triple t : triples) {
            if (match(r, t)) {
                return true;
            }
        }
        return false;
    }

    public void logContextCluster(ContextCluster cluster) {
        contextClusters.add(cluster);
    }

    public void logBridge(Triple t) {
        bridges.add(t);
    }

    public void logBridgeResource(String r) {
        bridgeResource.add(r);
    }

    public void collectTriples(Triple t) {
        collectedTriples.add(t);
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
