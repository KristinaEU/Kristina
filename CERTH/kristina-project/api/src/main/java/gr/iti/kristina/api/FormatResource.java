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

import gr.iti.kristina.api.helpers.FileHelper;
import java.io.IOException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.LoggerFactory;

/**
 * REST Web Service
 *
 * @author gmeditsk
 */
@Path("format/framenet")
public class FormatResource {

    @Context
    private UriInfo context;

    org.slf4j.Logger logger = LoggerFactory.getLogger(FormatResource.class);

    /**
     * Creates a new instance of FormatResource
     */
    public FormatResource() {
    }

    /**
     * Retrieves representation of an instance of
     * gr.iti.kristina.api.FormatResource
     *
     * @param speect_act
     * @param info
     * @return FrameNet
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getText(@QueryParam("speech_act") String speect_act, @QueryParam("info") String info) {
        try {
            //TODO return proper representation object
            return FileHelper.readFile("/framenet_example.xml");
        } catch (IOException ex) {
            logger.error("", ex);
        }
        return "";
    }

    /**
     * PUT method for updating or creating an instance of FormatResource
     *
     * @param content representation for the resource
     */
//    @PUT
//    @Consumes(MediaType.TEXT_PLAIN)
//    public void putText(String content) {
//    }
}