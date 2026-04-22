package com.smartcampus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

// Because your application path is "/api/v1", setting this path to "/"
// means this class perfectly handles GET requests to /api/v1
@Path("")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON) // Tells JAX-RS to convert our Map into a JSON object automatically
    public Response getApiMetadata() {

        // Creating the main JSON object structure using a HashMap
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("api", "Smart Campus Sensor & Room Management API");

        // 1. Versioning info
        metadata.put("version", "v1.0");

        // 2. Administrative contact details
        metadata.put("admin_contact", "admin@smartcampus.westminster.ac.uk");

        // 3. A map of primary resource collections
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("rooms", "/api/v1/rooms");
        endpoints.put("sensors", "/api/v1/sensors");

        metadata.put("resources", endpoints);

        // Return an HTTP 200 OK response with the JSON body
        return Response.ok(metadata).build();
    }
}