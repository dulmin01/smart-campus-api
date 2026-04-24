package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

// Because your application path is "/api/v1", setting this path to "/"
// means this class perfectly handles GET requests to /api/v1
@Path("")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON) // Tells JAX-RS to convert our Map into a JSON object automatically
    public Response getApiMetadata() {

        // Creating the main JSON object structure using a HashMap
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("api", "Smart Campus Sensor & Room Management API");

        // 1. Versioning info
        metadata.put("version", "v1.0");

        // 2. Administrative contact details
        metadata.put("Contact;", "Dulmin Fernando, 20240383");

        // 3. A map of primary resource collections
        Map<String, String> endpoints = new LinkedHashMap<>();
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");

        metadata.put("resources", endpoints);
        
        
        return Response.ok(metadata).build();
    }
}