# Hotel Room Reservation System - Backend

Spring Boot backend application for hotel room reservation system.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Setup Instructions

### 1. Database Setup

1. Install MySQL if not already installed
2. Create database (will be created automatically if `createDatabaseIfNotExist=true` is set):
   ```sql
   CREATE DATABASE hotel_reservation;
   ```
3. Update `src/main/resources/application.properties` with your MySQL credentials:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

### 2. Build and Run

#### Using Maven:
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

#### Using IDE:
- Import as Maven project
- Run `HotelReservationApplication.java`

### 3. Verify

- Backend runs on: `http://localhost:8080`
- API endpoints:
  - GET `http://localhost:8080/api/rooms` - Get all rooms
  - POST `http://localhost:8080/api/rooms/book` - Book rooms
  - POST `http://localhost:8080/api/rooms/random` - Random booking
  - POST `http://localhost:8080/api/rooms/reset` - Reset all rooms

## API Documentation

### Get All Rooms
```
GET /api/rooms
Response: { "success": true, "rooms": [...] }
```

### Book Rooms
```
POST /api/rooms/book
Body: { "roomCount": 3 }
Response: { "success": true, "message": "...", "bookedRooms": [...] }
```

### Random Booking
```
POST /api/rooms/random
Response: { "success": true, "message": "..." }
```

### Reset All Rooms
```
POST /api/rooms/reset
Response: { "success": true, "message": "..." }
```

## Features

- ✅ Automatic database seeding (97 rooms)
- ✅ Intelligent booking algorithm (same-floor priority)
- ✅ Greedy algorithm for multi-floor booking
- ✅ CORS enabled for frontend integration
- ✅ Full CRUD operations
- ✅ Transaction management

