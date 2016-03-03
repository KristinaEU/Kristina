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

import gr.iti.kristina.core.state.State;
import gr.iti.kristina.helpers.repository.utils.QueryUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.openrdf.model.URI;
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
/*
TODO: perhaps I need to filter out core concepts based on the URI in order to ignore DUL-specific constructs, such as Region (?)
 */
public class ConceptExtractor {

    State state;
    private final HashSet<Signature> concepts;

    static Logger logger = LoggerFactory.getLogger(ConceptExtractor.class);

    public ConceptExtractor(State state) {
        this.state = state;
        concepts = new HashSet<>();
        start();
    }

    public void extractCoreConcepts() throws MalformedQueryException, RepositoryException, QueryEvaluationException {
        RepositoryConnection stateConnection = state.getStateConnection();

        //get core concepts
        /*
        need to check whether the classified resource is instance or class...
        If it is class, then we do not need to take the type...
        */
        String q = "PREFIX core: <http://kristina-project.eu/ontologies/framesituation/core#> "
                + "PREFIX dul: <http://www.loa-cnr.it/ontologies/DUL.owl#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "select ?frame ?classified ?fe where { "
                + " ?frame a core:FrameSituation ; "
                + " dul:isSettingFor ?classified. "
                + " ?classified dul:isClassifiedBy ?fe . "
                + "} ";

        List<URI> classified = new ArrayList<>();
        TupleQueryResult result = QueryUtil.evaluateSelectQuery(stateConnection, q);
        while (result.hasNext()) {
            BindingSet bindingSet = result.next();
            //URI frame = (URI) bindingSet.getBinding("frame").getValue();
            //URI fe = (URI) bindingSet.getBinding("fe").getValue();
            URI cl = (URI) bindingSet.getBinding("classified").getValue();

            //System.out.println("frame: " + frame.getLocalName() + ", fe:" + fe.getLocalName() + ", classified: " + classified.toString());
            classified.add(cl);
        }
        result.close();
        //get superclasses
        q = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#> "
                + "SELECT DISTINCT ?directClass ?label"
                + "WHERE { "
                + "    ?resource a ?directClass . "
                + "    OPTIONAL {?directClass rdfs:label ?label. FILTER (lang(?label) = 'en' || lang(?label) = '') } "
                + "    FILTER (!isBlank(?directClass)) . "
                + "    FILTER (?directClass != owl:NamedIndividual) . "
                + "    FILTER NOT EXISTS { "
                + "        ?temp rdfs:subClassOf owl:Thing . "
                + "        FILTER (((?temp != owl:Thing) && (?temp != ?directClass)) && (!isBlank(?temp))) . "
                + "        ?resource a ?temp . "
                + "        ?temp rdfs:subClassOf ?directClass . "
                + "    } . "
                + "} ";

        for (URI classify : classified) {
            result = QueryUtil.evaluateSelectQuery(stateConnection, q, new BindingImpl("resource", classify));
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                URI directClass = (URI) bindingSet.getBinding("directClass").getValue();
                URI labelValue = (URI) bindingSet.getBinding("label");
                String label;
                if (labelValue == null) {
                    label = directClass.getLocalName();
                } else {
                    label = labelValue.toString();
                }
                Signature s = new Signature();
                s.uri = directClass.toString();
                s.localName = directClass.getLocalName();
                s.label = label;
                concepts.add(s);
            }
            result.close();
        }

    }

    private void start() {
        try {
            extractCoreConcepts();
        } catch (MalformedQueryException | RepositoryException | QueryEvaluationException ex) {
            logger.error("", ex);
        }
    }

    public HashSet<Signature> getConcepts() {
        return concepts;
    }

}
