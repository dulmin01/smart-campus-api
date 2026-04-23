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
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms
  -H "Content-Type: application/json"
  -d "{
    \"id\":\"LIB-301\",
    \"name\":\"Library Quiet Study\",
    \"capacity\":50
  }"
```

### 3. Get All Rooms
```
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

### 4. Register a Sensor
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors
  -H "Content-Type: application/json"
  -d "{
    \"id\":\"CO2-001\",
    \"type\":\"CO2\",
    \"status\":\"ACTIVE\",
    \"currentValue\":0.0,
    \"roomId\":\"LIB-301\"
  }"
```

### 5. Filter Sensors by Type
```
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2"
```

### 6. Add a Sensor Reading
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-001/readings
  -H "Content-Type: application/json"
  -d "{
    \"value\":412.5
  }"
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
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors
  -H "Content-Type: application/json"
  -d "{
    \"id\":\"CO2-002\",
    \"type\":\"CO2\",
    \"status\":\"ACTIVE\",
    \"currentValue\":0.0,
    \"roomId\":\"FAKE-ROOM\"
  }"
```

### 10. Post Reading to MAINTENANCE Sensor (403 Error)
```
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/CO2-003/readings
  -H "Content-Type: application/json"
  -d "{
  \"value\":500.0
  }"
```
