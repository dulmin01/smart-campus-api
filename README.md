# Smart Campus Sensor & Room Management API

## API Overview
The Smart Campus API is a RESTful web service built with JAX-RS (Jersey 2.39) 
and deployed on Apache Tomcat 9. It manages campus Rooms and IoT Sensors 
deployed throughout the university, providing endpoints for room management, 
sensor registration, sensor readings, and real-time data tracking.

### Core Design
- **Framework:** JAX-RS (Jersey 2.39)
- **Server:** Apache Tomcat 9
- **Build Tool:** Apache Maven
- **Data Storage:** In-memory ConcurrentHashMap
- **Base URI:** http://localhost:8080/smart-campus-api/api/v1
- **Response Format:** JSON

### Project Structure
```
src/main/java/com/smartcampus/
├── SmartCampusApplication.java       ← JAX-RS entry point
├── DataStore.java                    ← In-memory data store
├── model/
│   ├── Room.java
│   ├── Sensor.java
│   └── SensorReading.java
├── resource/
│   ├── DiscoveryResource.java
│   ├── RoomResource.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── exception/
│   ├── RoomNotEmptyException.java
│   ├── RoomNotEmptyExceptionMapper.java
│   ├── LinkedResourceNotFoundException.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── SensorUnavailableException.java
│   ├── SensorUnavailableExceptionMapper.java
│   └── GlobalExceptionMapper.java
└── filter/
    └── ApiLoggingFilter.java
```

### Resource Hierarchy
| Resource | URI | Class |
|---|---|---|
| Discovery | /api/v1 | DiscoveryResource |
| Rooms | /api/v1/rooms | RoomResource |
| Sensors | /api/v1/sensors | SensorResource |
| Readings | /api/v1/sensors/{sensorId}/readings | SensorReadingResource |

---

## How to Build and Run

### Prerequisites
- Java 17 or higher
- Maven 3.x
- Apache Tomcat 9
- NetBeans IDE (recommended)

### Steps
1. Clone the repository:
```
git clone https://github.com/dulmin01/smart-campus-api.git
```
2. Open the project in NetBeans
3. Right-click project → **Run**
4. The API will be available at:
```
http://localhost:8080/smart-campus-api/api/v1
```

---

## Available Endpoints

| Method | Endpoint | Description |
|---|---|---|
| GET | /api/v1 | API discovery and metadata |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a new room |
| GET | /api/v1/rooms/{roomId} | Get a specific room |
| DELETE | /api/v1/rooms/{roomId} | Delete a room (only if no sensors) |
| GET | /api/v1/sensors | Get all sensors |
| GET | /api/v1/sensors?type={type} | Filter sensors by type |
| POST | /api/v1/sensors | Register a new sensor |
| GET | /api/v1/sensors/{sensorId} | Get a specific sensor |
| GET | /api/v1/sensors/{sensorId}/readings | Get all readings for a sensor |
| POST | /api/v1/sensors/{sensorId}/readings | Add a new reading |

---

## Sample curl Commands

### 1. Get API Discovery
```
curl -X GET http://localhost:8080/smart-campus-api/api/v1
```

### 2. Create a Room
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id":"LIB-301",
    "name":"Library Quiet Study",
    "capacity":50
  }'
```

### 3. Get All Rooms
```
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 4. Register a Sensor
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id":"CO2-001",
    "type":"CO2",
    "status":"ACTIVE",
    "currentValue":0.0,
    "roomId":"LIB-301"
  }'
```

### 5. Filter Sensors by Type
```
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

### 6. Add a Sensor Reading
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value":412.5
  }'
```

### 7. Get All Readings for a Sensor
```
curl -X GET http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings
```

### 8. Delete a Room with Sensors (409 Conflict)
```
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/LIB-301
```

### 9. Register Sensor with Invalid Room (422 Error)
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id":"CO2-002",
    "type":"CO2",
    "status":"ACTIVE",
    "currentValue":0.0,
    "roomId":"FAKE-ROOM"
  }'
```

### 10. Post Reading to MAINTENANCE Sensor (403 Error)
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-003/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value":500.0
  }'
```

## Conceptual Report

### Part 1.1 - JAX-RS Resource Lifecycle
A JAX-RS resource class is by default request-scoped — a new instance of the resource is created for each HTTP request. This means instance fields defined in a resource class are unshared by requests. Since we cannot use in-memory data across requests directly, all shared state is kept static in the DataStore class with ConcurrentHashMap as a backing store. ConcurrentHashMap allows concurrent read and write in real-time across multiple requests without having to explicitly define synchronization blocks, where race conditions and data loss can become a risk if two similar requests arrive at the same time.

### Part 1.2 - HATEOAS
HATEOAS: Embedding navigable links into the responses of your API to enable Hypermedia as the Engine of Application State. This leads to a self-documenting API, so now clients can discover all available resources from one single entry point without the need for static documentation. Due to this separation with links, if a URL changes, we have only the server update; clients dynamically follow those links, and we end up with less coupling between client and server and can evolve our API easier over time.

### Part 2.1 - Returning IDs vs Full Objects
While responding with IDs only, it makes the payload smaller which reduces bandwidth but leads clients to make more requests to get details (N+1 problem). Imagine a list of 100 room IDs that we should investigate, it would mean making 100 follow-up GET requests to get the details for those rooms, introducing delays. If you return full objects, clients get all the info they need on one call. This also means larger payload sizes but no more additional round trips. In the case of small-to-medium collections like campus rooms, it is better to return full objects for performance and simplicity.

### Part 2.2 - Idempotency of DELETE
DELETE is idempotent — same requests leave the server in the same state. If a DELETE occurs on an existing room without any sensors, it deletes the room and responds with 200 OK as the first response. A second identical DELETE finds the room not found and gets 404 Not Found. Since the room is not present, the state of the server after each call is identical and the operation is considered idempotent. As long as sensors are still assigned to the room, every DELETE yields a 409 Conflict until successfully removing those sensors — also consistent and idempotent behaviour.

### Part 3.1 - @Consumes Mismatch
A request with a non-application/json Content-Type from a client (e.g. text/plain or application/xml) is automatically rejected by JAX-RS with 415 Unsupported Media Type, and the method body does not even run. The Content-Type header is validated against the @Consumes declaration in request matching by the framework. This defends the server against parsing errors and ensures only valid JSON payloads are passed to the business logic.

### Part 3.2 - @QueryParam vs @PathParam for Filtering
The query parameters (/sensors?type=CO2) are optional in nature — the default path /sensors can be used without query parameters and will return all sensors. Multiple filters compose naturally (?type=CO2&status=ACTIVE). Path parameters (/sensors/type/CO2) suggest a predefined sub-resource structure and therefore combined filters are cumbersome and semantically invalid because filter values are not resource identifiers. The REST standard for searching and filtering collections is query parameters.

### Part 4.1 - Sub-Resource Locator Pattern
The Sub-Resource Locator pattern uses a dedicated class to handle sub-resources. In this API, SensorResource is a router — if a request comes in to /sensors/{sensorId}/readings, it returns a new instance of SensorReadingResource which handles all the reading logic. It enables SensorResource to focus on sensor-related logic, enables unit testing of SensorReadingResource in isolation, and ensures sub-resources can be added later without changing the original classes. It prevents a "god class" with dozens of unrelated methods crammed together, making the API easier to understand and maintain.

### Part 5.2 - Why 422 over 404
404 means the requested URL is not found. In this case, /api/v1/sensors is a valid URL. The issue is that the value of one of the fields (roomId) in the body of the POST request references a non-existent resource. HTTP 422 Unprocessable Entity is more accurate here because the server understood the request format but could not process it due to a semantic error in the body of the request — the foreign-key reference is broken. Using 404 would mislead the client into thinking the endpoint itself does not exist.

### Part 5.4 - Stack Trace Security Risks
Java stack traces provide critical information about the system to potential attackers: package and class names reveal the system architecture, library names and version numbers allow identifying known CVE exploits, file system paths reveal how the server is structured, and method call chains can be used to reverse-engineer the system. The GlobalExceptionMapper catches all unexpected exceptions, logs the full trace server-side for developers, and returns a safe generic 500 error to the client — concealing all private system information.

### Part 5.5 - Filters vs Manual Logging
JAX-RS filters apply to all endpoints — there is no chance of missing logging for a new resource method. Logging is consistent for all endpoints because it is defined in a single location. Resource methods do not have to worry about infrastructure concerns. Filters can also be enabled, disabled, or replaced from a central location without changing the resource classes. This is in line with the Single Responsibility Principle — each class is responsible for one thing — and the Open/Closed Principle — open for extension, closed for modification.



