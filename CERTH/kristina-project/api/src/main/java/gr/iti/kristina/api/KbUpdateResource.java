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
package gr.iti.kristina.api;

import gr.iti.kristina.core.irmapper.SymptomsMappingModule;
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 *
 * @author gmeditsk
 */
@Path("kb/update")
public class KbUpdateResource {

    @Context
    private UriInfo context;

    org.slf4j.Logger logger = LoggerFactory.getLogger(KbUpdateResource.class);

    /**
     * Creates a new instance of KbUpdateResource
     */
    public KbUpdateResource() {
    }

    /**
     * Retrieves representation of an instance of
     * gr.iti.kristina.api.KbUpdateResource
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
     * PUT method for updating or creating an instance of KbUpdateResource
     *
     * @param content representation for the resource
     * @return 
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public String update(@QueryParam("content") String content) {
        logger.debug(content);
        try {
            SymptomsMappingModule o = new SymptomsMappingModule("http://localhost:8084/graphdb-workbench-free", "Symptoms-Repository", "kristina", "samiam#2");
            o.setJsonInput(content);
            o.call();
            return "ok";
        } catch (IOException | RepositoryConfigException | RepositoryException ex) {
            logger.error("", ex);
        }
        return "";
    }
}
