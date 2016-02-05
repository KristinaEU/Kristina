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
package gr.iti.kristina.core.irmapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import com.ontotext.jena.SesameDataset;
import gr.iti.kristina.core.irmapper.model.ETriple;
import gr.iti.kristina.core.irmapper.model.ExResource;
import gr.iti.kristina.core.irmapper.model.Triple;
import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import gr.iti.kristina.helpers.vocabulary.NS;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.slf4j.LoggerFactory;

/**
 *
 * @author gmeditsk
 */
public class SymptomsMappingModule {

    public static final String DISEASE_OR_SYNDROME = "disease or syndrome";
    public static final String SIGN_OR_SYMPTOM = "sign or symptom";
    public static final String FINDING = "finding";
    public static final String HAS_SYMPTOM = "hasSymptom";
    public static final String RELATES_TO = "relatedTo";
    private String repId;

    private String jsonInput;

    public void setJsonInput(String jsonInput) {
        this.jsonInput = jsonInput;
    }

    private GraphDbRepositoryManager manager;

    org.slf4j.Logger logger = LoggerFactory.getLogger(SymptomsMappingModule.class);
    HashSet<Triple> triples;
    HashMap<String, String> mappings = new HashMap();

    public SymptomsMappingModule(String serverURL, String repId, String username, String password) {
        manager = new GraphDbRepositoryManager(serverURL, username, password);
        this.repId = repId;
    }

    public void call() throws IOException, RepositoryConfigException, RepositoryException {
        createTriples();
//        assignPredicates();
        updateKb(false);
    }

    public void createTriples() throws IOException {
        triples = new HashSet<>();
//        try (Reader reader = new BufferedReader(new FileReader("misc/example6.json"))) {
        Gson gson = new GsonBuilder().create();
        Type collectionType = new TypeToken<Collection<ETriple>>() {
        }.getType();
        Collection<ETriple> etriples = gson.fromJson(jsonInput, collectionType);

        Stack<ETriple> stack = new Stack();
        stack.add((ETriple) etriples.toArray()[0]);

        while (!stack.empty()) {
            ETriple current = stack.pop();
            List<ExResource> subjects = current.getSubject();
            List<ExResource> objects = current.getObject();
            for (ExResource s : subjects) {
                ExResource parent = current.getParent();
                if (parent != null) {
                    Triple t = new Triple(parent, "", s);
                    if (!triples.contains(t)) {
                        triples.add(t);
                    }
                }
                for (ExResource o : objects) {
                    Triple t = new Triple(s, "", o);
                    if (!triples.contains(t)) {
                        triples.add(t);
                    }
                }
                List<ETriple> objectSentences = current.getObjectSentence();
                for (ETriple os : objectSentences) {
                    os.setParent(s);
                }
                stack.addAll(objectSentences);
            }
        }
//        }

        assignPredicates();
        System.out.println("Generated Triples:");
        for (Triple t : triples) {
            System.out.println(t);
        }
    }

    public String removeStopWords(String text) throws IOException {
        TokenStream tokenStream = new StandardTokenizer(Version.LUCENE_35, new StringReader(text));
        tokenStream = new StopFilter(Version.LUCENE_35, tokenStream, StandardAnalyzer.STOP_WORDS_SET);
        tokenStream.reset();
        StringBuilder sb = new StringBuilder();
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            sb.append(term).append(" ");
        }
        return sb.toString().trim().substring(0, sb.length() - 1);
    }

    public void assignPredicates() {
        for (Triple t : triples) {
            ExResource s = t.getSubject();
            ExResource o = t.getObject();
            if (s.getConcept().equals(DISEASE_OR_SYNDROME)) {
                t.setPredicate(HAS_SYMPTOM);
            } else {
                t.setPredicate(RELATES_TO);
            }
        }
    }

    private String conceptToClass(String concept) {
        return WordUtils.capitalize(concept).replaceAll(" ", "");
    }

    public void updateKb(boolean persist) throws IOException, RepositoryConfigException, RepositoryException {
        Repository repository = manager.getRepository(repId);
        RepositoryConnection connection = repository.getConnection();
        SesameDataset dataset = new SesameDataset(connection);
        Model _model = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        OntModel temp = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        Property hasSymptom = _model.createProperty(NS.SYMPTOMS_NS + "hasSymptom");
        Property relatesTo = _model.createProperty(NS.SYMPTOMS_NS + "relatesTo");
        for (Triple t : triples) {
            System.out.println(t);
            String slabel = removeStopWords(t.getSubject().getTerm());
            String sConcept = conceptToClass(t.getSubject().getConcept());
            Resource s = temp.createResource("http://anon#" + slabel.replaceAll(" ", "_"));
            s.addLiteral(RDFS.label, slabel);
            s.addProperty(RDF.type, _model.createResource(NS.SYMPTOMS_NS + sConcept));
            String olabel = removeStopWords(t.getObject().getTerm());
            String oConcept = conceptToClass(t.getObject().getConcept());
            Resource o = temp.createResource("http://anon#" + olabel.replaceAll(" ", "_"));
            o.addLiteral(RDFS.label, olabel);
            o.addProperty(RDF.type, _model.createResource(NS.SYMPTOMS_NS + oConcept));
            if (t.getPredicate().equals(HAS_SYMPTOM)) {
                temp.add(s, hasSymptom, o);
            } else if (t.getPredicate().equals(RELATES_TO)) {
                temp.add(s, relatesTo, o);
            }
        }
        List<Statement> statements = temp.listStatements().toList();
        for (Statement s : statements) {
            System.out.println(s);
            if (persist) {
                _model.add(s);
            }
        }
        System.out.println(_model.listStatements().toList().size());
        if (persist) {
            _model.commit();
        }
        if (manager != null) {
            manager.shutDown();
            connection.close();
            repository.shutDown();
        }
    }

    public static void main(String[] args) throws IOException, RepositoryConfigException, RepositoryException {
        SymptomsMappingModule o = new SymptomsMappingModule("http://localhost:8080", "Symptoms-Repository", "admin", "Paran01@!#10");
        o.setJsonInput("");
        o.call();
    }
}
