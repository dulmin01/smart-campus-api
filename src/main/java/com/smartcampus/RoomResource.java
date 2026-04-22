package com.smartcampus;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    // GET /rooms - list all rooms
    @GET
    public Response getAllRooms() {
        List<Room> allRooms = new ArrayList<>(DataStore.rooms.values());
        return Response.ok(allRooms).build();
    }

    // POST /rooms - create a room
    @POST
    public Response createRoom(Room room) {
        if (room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(error("Field 'id' is required."))
                    .build();
        }
        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(error("Room '" + room.getId() + "' already exists."))
                    .build();
        }
        DataStore.rooms.put(room.getId(), room);
        URI location = URI.create("http://localhost:8080/api/v1/rooms/" + room.getId());
        return Response.created(location).entity(room).build();
    }

    // GET /rooms/{roomId} - get a specific room
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Room '" + roomId + "' not found."))
                    .build();
        }
        return Response.ok(room).build();
    }

    // DELETE /rooms/{roomId} - delete a room
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(error("Room '" + roomId + "' not found."))
                    .build();
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(roomId, room.getSensorIds().size());
        }
        
        DataStore.rooms.remove(roomId);
        Map<String, String> body = new HashMap<>();
        body.put("message", "Room '" + roomId + "' deleted successfully.");
        return Response.ok(body).build();
    }

    private Map<String, String> error(String message) {
        Map<String, String> body = new HashMap<>();
        body.put("error", message);
        return body;
    }
}