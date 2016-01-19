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
package gr.iti.kristina.irmapper.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import gr.iti.kristina.irmapper.parser.model.ETriple;
import gr.iti.kristina.irmapper.parser.model.Resource;
import gr.iti.kristina.irmapper.parser.model.Triple;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import org.drools.compiler.lang.DRL6Expressions;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
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

    org.slf4j.Logger _logger = LoggerFactory.getLogger(SymptomsMappingModule.class);
    List<Triple> _triples;

    public void call() throws IOException {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        KieSession kSession = kContainer.newKieSession("symptomsMappingKSession");

        kSession.setGlobal("logger", _logger);

        try (Reader reader = new BufferedReader(new FileReader("misc/example_data.json"))) {
            Gson gson = new GsonBuilder().create();
            Type collectionType = new TypeToken<Collection<ETriple>>() {
            }.getType();
            Collection<ETriple> tripleContainer = gson.fromJson(reader, collectionType);
            kSession.insert(tripleContainer.toArray()[0]);
            System.out.println(tripleContainer.toArray()[0]);
//            tripleContainer.stream().forEach((t) -> {
//                kSession.insert(t);
//            });

        }

        kSession.fireAllRules();
        kSession.dispose();
    }

    public void call2() throws IOException {

        try (Reader reader = new BufferedReader(new FileReader("misc/example_data.json"))) {
            Gson gson = new GsonBuilder().create();
            Type collectionType = new TypeToken<Collection<ETriple>>() {
            }.getType();
            Collection<ETriple> etriples = gson.fromJson(reader, collectionType);

            Stack<ETriple> stack = new Stack();
            stack.add((ETriple) etriples.toArray()[0]);
            HashSet<Triple> triples = new HashSet();

            while (!stack.empty()) {
                ETriple current = stack.pop();
                List<Resource> subjects = current.getSubject();
                List<Resource> objects = current.getObject();
                for (Resource s : subjects) {
                    Resource parent = current.getParent();
                    if (parent != null) {
                        Triple t = new Triple(parent, "", s);
                        if (!triples.contains(t)) {
                            triples.add(t);
                        }
                    }
                    for (Resource o : objects) {
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
            
            annotate(triples);

            for (Triple t : triples) {
                System.out.println(t);
            }

        }
    }

    public void annotate(HashSet<Triple> triples) {
        for (Triple t : triples) {
            Resource s = t.getSubject();
            Resource o = t.getObject();

            if (s.getConcept().equals(DISEASE_OR_SYNDROME)) {
                t.setPredicate(HAS_SYMPTOM);
            } else {
                t.setPredicate(RELATES_TO);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new SymptomsMappingModule().call2();
    }
}
