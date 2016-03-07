    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.iti.kristina.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import gr.iti.kristina.core.MockService;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;

/**
 * REST Web Service
 *
 * @author gmeditsk
 */
@Path("api")
public class Api {

    @Context
    private UriInfo context;
    static MockService service;
    static {
        try {
            
            service = new MockService(false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Api.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Creates a new instance of GenericResource
     */
    public Api() {
    }

    /**
     * Retrieves representation of an instance of gr.iti.kristina.demo.service.GenericResource
     * @param la
     * @return an instance of java.lang.String
     */
    @GET
    @Path("/updateState")
    @Produces(MediaType.TEXT_PLAIN)
    public String getText(@QueryParam("la") String la) throws RepositoryException, UnsupportedEncodingException {
        //TODO return proper representation object
        String decode = java.net.URLDecoder.decode(la, "UTF-8");
        String s = UUID.randomUUID().toString();
        decode = decode.replaceAll("__X", s);
        String log = service.updateState(decode);
        System.out.println("SERVER:: " + log);
        return log;
    }
    
    @GET
    @Path("/contextHistory")
    @Produces(MediaType.APPLICATION_JSON)
    public String getContextHistory(@QueryParam("recentItems") int recentItems) throws JsonProcessingException {
        Multimap<String, String> contextHistory = service.getContextHistory(recentItems);
        ObjectMapper mapper = new ObjectMapper().registerModule(new GuavaModule());
        String writeValueAsString = mapper.writeValueAsString(contextHistory);
        return writeValueAsString;
    }
    
    @GET
    @Path("/startqa")
    @Produces(MediaType.TEXT_PLAIN)
    public String startQA() throws RepositoryConfigException, RepositoryException, MalformedQueryException, QueryEvaluationException {
        return service.startQA();
    }

    /**
     * PUT method for updating or creating an instance of GenericResource
     * @param content representation for the resource
     */
    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public void putText(String content) {
    }
}
