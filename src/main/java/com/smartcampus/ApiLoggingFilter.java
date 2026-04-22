package com.smartcampus;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.logging.Logger;

@Provider
public class ApiLoggingFilter 
        implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = 
            Logger.getLogger(ApiLoggingFilter.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) {
        LOGGER.info(String.format("[REQUEST]  %s %s",
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()));
    }

    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) {
        LOGGER.info(String.format("[RESPONSE] %d %s  ←  %s %s",
                responseContext.getStatus(),
                responseContext.getStatusInfo().getReasonPhrase(),
                requestContext.getMethod(),
                requestContext.getUriInfo().getRequestUri()));
    }
}