/* 
 * The MIT License
 *
 * Copyright 2015 gmeditsk.
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
package gr.iti.kristina.api;

import com.franz.agraph.jena.AGGraph;
import com.franz.agraph.jena.AGGraphMaker;
import com.franz.agraph.jena.AGModel;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.ontotext.jena.SesameDataset;
import gr.iti.kristina.api.helpers.KristinaRepository;
import gr.iti.kristina.api.helpers.PREFIXES;
import gr.iti.kristina.helpers.repository.GraphDbRepositoryManager;
import java.io.StringWriter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 *
 * @author gmeditsk
 */
@Path("context/update")
public class ContextUpdateResource {

    @Context
    private UriInfo context;

    private final String GRAPH_DB_URL = "http://localhost:8084/graphdb-workbench-free";
    private final String USERNAME = "kristina";
    private final String PASSWORD = "samiam#2";
    Logger logger = LoggerFactory.getLogger(ContextUpdateResource.class);

    /**
     * Creates a new instance of ContextUpdateResource
     */
    public ContextUpdateResource() {
    }

    /**
     * Retrieves representation of an instance of
     * gr.iti.kristina.api.ContextUpdateResource
     *
     * @return an instance of java.lang.String
     */
//    @GET
//    @Produces(MediaType.TEXT_PLAIN)
//    public String getText() {
//        //TODO return proper representation object
//        throw new UnsupportedOperationException();
//    }
    /**
     * POST method for sending data to KI
     *
     * @param emotions RDF/OWL representation of emotions
     * @param frames Verbal communication results in the FrameSituation model
     * (DnS pattern)
     * @return The result of KI in RDF/OWL (DnS pattern)
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public String update(@QueryParam("emotions") String emotions, @QueryParam("frames") String frames) {
        logger.debug("frames: " + frames);
        logger.debug("emotions: " + emotions);
        String result = null;
        GraphDbRepositoryManager manager = new GraphDbRepositoryManager(GRAPH_DB_URL, "test", USERNAME, PASSWORD);
        logger.debug("POST on api/context/update");
        fakeInsert(manager);
        result = createResponse(manager);
        return result;
    }

    /**
     * Create a fake response in the DnS pattern
     *
     * @param r The remote repository
     * @return RDF/OWL
     */
    private String createResponse(KristinaRepository r) {
        AGGraphMaker maker = r.getMaker();
        AGGraph graph = maker.getGraph();
        AGModel agmodel = new AGModel(graph);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.addSubModel(agmodel);
        model.setNsPrefix("dul", PREFIXES.DUL);
        model.setNsPrefix("entities", PREFIXES.ENTITIES);
        model.setNsPrefix("core", PREFIXES.FRAMES);
        model.setNsPrefix("", PREFIXES.RETURN);
        Ontology ontology = model.createOntology("http://kristina-project.eu/ontologies/ms2/ki/return");
        ontology.addImport(model.createResource("http://kristina-project.eu/ontologies/framesituation/core"));
        Resource sit = agmodel.createResource(PREFIXES.FRAMES + "FrameSituation");
        Resource desc = agmodel.createResource(PREFIXES.FRAMES + "FrameDescrition");
        Resource status = agmodel.createResource(PREFIXES.ENTITIES + "Status");
        Property satisfies = agmodel.createProperty(PREFIXES.DUL + "satisfies");
        Property defines = agmodel.createProperty(PREFIXES.DUL + "defines");
        Property classifies = agmodel.createProperty(PREFIXES.DUL + "classifies");
        Resource status_true = agmodel.createResource(PREFIXES.ENTITIES + "status_true");
        Individual sit1 = model.createIndividual(PREFIXES.RETURN + "FrameSituation_1", sit);
        Individual desc1 = model.createIndividual(PREFIXES.RETURN + "FrameDescription_1", desc);
        model.add(sit1, satisfies, desc1);
        Individual status1 = model.createIndividual(PREFIXES.RETURN + "Status_1", status);
        model.add(desc1, defines, status1);
        model.add(status1, classifies, status_true);
        String syntax = "RDF/XML-ABBREV";
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        return out.toString();
    }

    /**
     * Insert fake data to the remote repository
     *
     * @param r The remote repository
     */
    private void fakeInsert(KristinaRepository r) {
        AGGraphMaker maker = r.getMaker();
        AGGraph graph = maker.getGraph();
        AGModel agmodel = new AGModel(graph);
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, agmodel);

        Resource sit = agmodel.createResource(PREFIXES.FRAMES + "FrameSituation");
        Individual sit1 = model.createIndividual(sit);
    }

    private void fakeInsert(GraphDbRepositoryManager manager) {
        SesameDataset dataset = new SesameDataset(manager.getConnection());
        Model _model = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        OntModel temp = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, _model);
        Resource sit = temp.createResource(PREFIXES.FRAMES + "FrameSituation");
        Individual sit1 = temp.createIndividual(sit);
        temp.commit();
//        dataset.close();
    }

    private String createResponse(GraphDbRepositoryManager manager) {
        SesameDataset dataset = new SesameDataset(manager.getConnection());
        Model _model = ModelFactory.createModelForGraph(dataset.getDefaultGraph());
        OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        model.addSubModel(_model);
        model.setNsPrefix("dul", PREFIXES.DUL);
        model.setNsPrefix("entities", PREFIXES.ENTITIES);
        model.setNsPrefix("core", PREFIXES.FRAMES);
        model.setNsPrefix("", PREFIXES.RETURN);
        Ontology ontology = model.createOntology("http://kristina-project.eu/ontologies/ms2/ki/return");
        ontology.addImport(model.createResource("http://kristina-project.eu/ontologies/framesituation/core"));
        Resource sit = model.createResource(PREFIXES.FRAMES + "FrameSituation");
        Resource desc = model.createResource(PREFIXES.FRAMES + "FrameDescrition");
        Resource status = model.createResource(PREFIXES.ENTITIES + "Status");
        Property satisfies = model.createProperty(PREFIXES.DUL + "satisfies");
        Property defines = model.createProperty(PREFIXES.DUL + "defines");
        Property classifies = model.createProperty(PREFIXES.DUL + "classifies");
        Resource status_true = model.createResource(PREFIXES.ENTITIES + "status_true");
        Individual sit1 = model.createIndividual(PREFIXES.RETURN + "FrameSituation_1", sit);
        Individual desc1 = model.createIndividual(PREFIXES.RETURN + "FrameDescription_1", desc);
        model.add(sit1, satisfies, desc1);
        Individual status1 = model.createIndividual(PREFIXES.RETURN + "Status_1", status);
        model.add(desc1, defines, status1);
        model.add(status1, classifies, status_true);
        String syntax = "RDF/XML-ABBREV";
        StringWriter out = new StringWriter();
        model.write(out, syntax);
        return out.toString();
    }
}
