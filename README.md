# Smart Campus Sensor & Room Management API
## Module: 5COSC022W Client-Server Architectures

## API Overview
The Smart Campus API is a RESTful web service built with JAX-RS (Jersey 2.39) 
and an embedded Grizzly HTTP server. It manages campus Rooms and IoT Sensors 
deployed throughout the university, providing endpoints for room management, 
sensor registration, sensor readings, and real-time data tracking.

### Core Design
- **Framework:** JAX-RS (Jersey 2.39)
- **Server:** Grizzly (embedded)
- **Build Tool:** Apache Maven
- **Data Storage:** In-memory ConcurrentHashMap
- **Base URI:** http://localhost:8080/api/v1
- **Response Format:** JSON

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
- NetBeans or IntelliJ IDEA

### Option 1: Run in IDE (Recommended)
1. Clone the repository: git clone https://github.com/dulmin01/smart-campus-api.git
2. Open the project in NetBeans or IntelliJ
3. Click the Run button on `Main.java`
4. The server starts at `http://localhost:8080/api/v1`

### Option 2: Run from Terminal
1. Clone the repository: git clone https://github.com/dulmin01/smart-campus-api.git
cd smart-campus-api
2. Build: mvn package
3. Run: java -jar target/smart-campus-api-1.0-SNAPSHOT.jar
4. 
