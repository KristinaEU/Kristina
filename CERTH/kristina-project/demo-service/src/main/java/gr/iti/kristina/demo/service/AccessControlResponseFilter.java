/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.iti.kristina.demo.service;

/**
 *
 * @author gmeditsk
 */
import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.HEADER_DECORATOR)
public class AccessControlResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        final MultivaluedMap<String,Object> headers = responseContext.getHeaders();

        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Headers", "Authorization, Origin, X-Requested-With, Content-Type");
        headers.add("Access-Control-Expose-Headers", "Location, Content-Disposition");
        headers.add("Access-Control-Allow-Methods", "POST, PUT, GET, DELETE, HEAD, OPTIONS");
    }
}
