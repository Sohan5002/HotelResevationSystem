# Complete Full-Stack Hotel Room Reservation System Guide

## 📚 Table of Contents
1. [System Architecture Overview](#1-system-architecture-overview)
2. [Frontend Explanation (React)](#2-frontend-explanation-react)
3. [Backend Explanation (Spring Boot)](#3-backend-explanation-spring-boot)
4. [Database Design (MySQL)](#4-database-design-mysql)
5. [API Design](#5-api-design)
6. [Frontend ↔ Backend Integration](#6-frontend--backend-integration)
7. [Assessment Requirements Mapping](#7-assessment-requirements-mapping)
8. [Common Mistakes & Debugging](#8-common-mistakes--debugging)
9. [Interview & Submission Readiness](#9-interview--submission-readiness)

---

## 1. System Architecture Overview

### What is a Full-Stack Application?

Think of a restaurant:
- **Frontend (React)** = Dining room where customers see the menu and place orders
- **Backend (Spring Boot)** = Kitchen that processes orders and prepares food
- **Database (MySQL)** = Storage room where ingredients and records are kept

### Three-Layer Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    USER'S BROWSER                        │
│  ┌──────────────────────────────────────────────────┐   │
│  │         FRONTEND (React Application)              │   │
│  │  - Shows UI (buttons, input fields, room grid)    │   │
│  │  - User clicks "BOOK" button                      │   │
│  │  - Sends request to backend                       │   │
│  └──────────────────────────────────────────────────┘   │
└───────────────────┬─────────────────────────────────────┘
                    │ HTTP Request (JSON)
                    │ POST /api/bookings
                    │ { "roomCount": 3 }
                    ▼
┌─────────────────────────────────────────────────────────┐
│              BACKEND SERVER (Spring Boot)                │
│  ┌──────────────────────────────────────────────────┐   │
│  │  CONTROLLER Layer                                │   │
│  │  - Receives HTTP request                         │   │
│  │  - Validates input                               │   │
│  └──────────────┬───────────────────────────────────┘   │
│                 │ Calls                                │
│  ┌──────────────▼───────────────────────────────────┐   │
│  │  SERVICE Layer                                   │   │
│  │  - Business logic (booking algorithm)            │   │
│  │  - Same-floor priority logic                     │   │
│  │  - Travel time calculation                       │   │
│  └──────────────┬───────────────────────────────────┘   │
│                 │ Calls                                │
│  ┌──────────────▼───────────────────────────────────┐   │
│  │  REPOSITORY Layer                                │   │
│  │  - Database queries                              │   │
│  │  - Save/update room status                       │   │
│  └──────────────┬───────────────────────────────────┘   │
└──────────────────┼─────────────────────────────────────┘
                   │ SQL Queries
                   │ SELECT * FROM rooms WHERE status='available'
                   │ UPDATE rooms SET status='booked' WHERE room_no=101
                   ▼
┌─────────────────────────────────────────────────────────┐
│              DATABASE (MySQL)                            │
│  ┌──────────────────────────────────────────────────┐   │
│  │  rooms table                                     │   │
│  │  - Stores all 97 rooms                          │   │
│  │  - Room number, floor, status, position         │   │
│  └──────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                    │
                    │ HTTP Response (JSON)
                    │ { "success": true, "bookedRooms": [101, 102, 103] }
                    ▼
┌─────────────────────────────────────────────────────────┐
│              FRONTEND (React)                            │
│  - Receives response                                    │
│  - Updates UI to show booked rooms                      │
│  - Colors change from green to blue                     │
└─────────────────────────────────────────────────────────┘
```

### Request-Response Flow (Step by Step)

**When user clicks "BOOK" button:**

1. **Frontend**: User types "3" in input field and clicks BOOK
2. **Frontend**: React code sends HTTP POST request to `http://localhost:8080/api/bookings`
   ```json
   {
     "roomCount": 3
   }
   ```
3. **Backend Controller**: Receives request, validates (roomCount must be 1-5)
4. **Backend Service**: Runs booking algorithm:
   - Finds available rooms
   - Checks same-floor availability first
   - Calculates travel time if multiple floors needed
   - Selects best rooms
5. **Backend Repository**: Updates database
   - Sets selected rooms' status to "booked"
6. **Backend Controller**: Sends response back
   ```json
   {
     "success": true,
     "bookedRooms": [101, 102, 103],
     "message": "Successfully booked 3 rooms"
   }
   ```
7. **Frontend**: Receives response, updates React state
8. **Frontend**: UI re-renders, booked rooms change color from green to blue

**Why this architecture?**
- **Separation of Concerns**: Each layer has one job
- **Scalability**: Can handle many users
- **Maintainability**: Easy to fix bugs or add features
- **Security**: Business logic stays on server (can't be hacked from browser)

---

## 2. Frontend Explanation (React)

### Current Frontend Structure

```
src/
├── App.jsx              ← Main component (manages state)
├── index.js             ← Entry point (renders App)
├── index.css            ← Global styles
├── styles/
│   └── main.css         ← Component styles
├── components/
│   ├── Controls.jsx     ← Input field + buttons
│   ├── HotelGrid.jsx    ← Displays all floors and rooms
│   └── Room.jsx         ← Single room box
└── data/
    └── roomsData.js     ← Currently generates rooms in memory
```

### Component Breakdown (Current Implementation)

#### 2.1 App.jsx - The Brain

**What it does:**
- Manages all the application state (which rooms are booked)
- Handles user actions (BOOK, RANDOM, RESET)
- Coordinates between Controls and HotelGrid components

**Key Concepts:**

```jsx
const [rooms, setRooms] = useState(generateRooms());
```

**WHY useState?**
- `rooms` = current state (array of room objects)
- `setRooms` = function to update state
- When state changes, React automatically re-renders components

**Current Problem:**
- `generateRooms()` creates rooms in memory (lost on refresh)
- No backend connection
- Booking logic is too simple (doesn't follow assessment rules)

**What needs to change:**
- Fetch rooms from backend API instead of `generateRooms()`
- Send booking requests to backend
- Receive updated room list from backend

#### 2.2 Controls.jsx - User Input

**What it does:**
- Shows input field for number of rooms (1-5)
- Shows three buttons: BOOK, RANDOM, RESET

**Props (Data passed from parent):**
```jsx
<Controls
  value={count}           // Current input value
  setValue={setCount}     // Function to update input
  onBook={bookRooms}      // Function called when BOOK clicked
  onRandom={randomBooking} // Function called when RANDOM clicked
  onReset={resetAll}      // Function called when RESET clicked
/>
```

**Why props?**
- Makes component reusable
- Parent (App) controls what happens
- Child (Controls) just displays UI and triggers events

#### 2.3 HotelGrid.jsx - Room Display

**What it does:**
- Takes array of all rooms
- Groups them by floor
- Renders each floor as a section with room boxes

**Key Logic:**
```jsx
const floors = {};
rooms.forEach(room => {
  if (!floors[room.floor]) {
    floors[room.floor] = [];
  }
  floors[room.floor].push(room);
});
```

**WHY group by floor?**
- Assessment requires showing floors separately
- Easier to visualize hotel structure
- Matches real-world hotel layout

#### 2.4 Room.jsx - Single Room Box

**What it does:**
- Displays one room as a colored box
- Shows room number
- Changes color based on status

**Status Colors:**
- `available` → Green (lightgreen)
- `booked` → Red (#dc3545)
- `new` → Blue (#007bff) - newly booked rooms

**CSS Class Logic:**
```jsx
let className = "room";
if (room.status === "booked") className += " booked";
else if (room.status === "new") className += " new";
```

**WHY build className string?**
- CSS uses `.room.booked` (element with BOTH classes)
- React className accepts space-separated string
- `"room booked"` creates element with class="room booked"

### State Management (Current vs. Needed)

**Current (Frontend-only):**
```
User clicks BOOK
  ↓
bookRooms() function runs (in App.jsx)
  ↓
Updates rooms state directly
  ↓
UI re-renders
```

**Problems:**
- Data lost on page refresh
- No travel time calculation
- No same-floor priority
- No database persistence

**Needed (Full-Stack):**
```
User clicks BOOK
  ↓
bookRooms() sends API request to backend
  ↓
Backend calculates best rooms (travel time, same floor)
  ↓
Backend updates database
  ↓
Backend sends response with updated rooms
  ↓
Frontend updates state with response
  ↓
UI re-renders
```

### CSS Layout Explanation

**Flexbox for Controls:**
```css
.controls {
  display: flex;           /* Makes children align horizontally */
  align-items: center;     /* Vertically centers items */
  gap: 10px;               /* Space between items */
  justify-content: center; /* Centers items horizontally */
}
```

**Flexbox for Room Grid:**
```css
.floor-rooms {
  display: flex;      /* Rooms align horizontally */
  gap: 10px;          /* Space between room boxes */
  flex-wrap: wrap;    /* If no space, wrap to next line */
}
```

**Why Flexbox?**
- Easy horizontal/vertical alignment
- Responsive (adapts to screen size)
- No need for complex float/position tricks

### Frontend Issues & Fixes

**Issue 1: CSS Not Loading**
- **Symptom**: Rooms show as plain text, no colors
- **Cause**: Wrong import path (`./syles/main.css` instead of `./styles/main.css`)
- **Fix**: Check import path matches actual file location

**Issue 2: Components Not Rendering**
- **Symptom**: Blank page or errors in console
- **Cause**: Missing imports, wrong prop names, syntax errors
- **Fix**: Check browser console (F12), fix import statements

**Issue 3: State Not Updating**
- **Symptom**: Clicking buttons does nothing
- **Cause**: Forgot to call `setRooms()` or state update logic wrong
- **Fix**: Ensure state updates use `setState()` function, check logic

---

## 3. Backend Explanation (Spring Boot)

### What is Spring Boot?

**Simple Explanation:**
- Framework for building Java web applications
- Provides tools for HTTP requests, database access, security
- Like a toolbox that makes Java web development easier

**Why Spring Boot?**
- Auto-configuration (less setup code)
- Built-in server (runs on port 8080)
- Easy database integration
- Industry standard (used by major companies)

### Project Structure (Standard Spring Boot)

```
hotel-backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── hotel/
│       │           └── reservation/
│       │               ├── HotelReservationApplication.java  ← Main class
│       │               ├── controller/
│       │               │   └── RoomController.java          ← Handles HTTP requests
│       │               ├── service/
│       │               │   └── RoomService.java             ← Business logic
│       │               ├── repository/
│       │               │   └── RoomRepository.java          ← Database access
│       │               ├── model/
│       │               │   └── Room.java                    ← Data structure
│       │               └── dto/
│       │                   ├── BookingRequest.java          ← Request data
│       │                   └── BookingResponse.java         ← Response data
│       └── resources/
│           ├── application.properties                      ← Database config
│           └── data.sql                                    ← Initial data
└── pom.xml                                                  ← Dependencies
```

### Layer Architecture (Why Each Layer?)

#### 3.1 Controller Layer (RoomController.java)

**Job**: Handle HTTP requests from frontend

**Why it exists:**
- Entry point for all API calls
- Validates incoming data
- Calls service layer
- Returns HTTP responses

**Example:**
```java
@RestController
@RequestMapping("/api")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    @PostMapping("/bookings")
    public ResponseEntity<BookingResponse> bookRooms(@RequestBody BookingRequest request) {
        // Validate input
        if (request.getRoomCount() < 1 || request.getRoomCount() > 5) {
            return ResponseEntity.badRequest().build();
        }
        
        // Call service layer
        BookingResponse response = roomService.bookRooms(request.getRoomCount());
        
        // Return response
        return ResponseEntity.ok(response);
    }
}
```

**Annotations Explained:**
- `@RestController` = This class handles HTTP requests
- `@RequestMapping("/api")` = All URLs start with /api
- `@PostMapping("/bookings")` = Handles POST /api/bookings
- `@RequestBody` = Converts JSON to Java object
- `@Autowired` = Spring automatically provides RoomService instance

#### 3.2 Service Layer (RoomService.java)

**Job**: Business logic (booking algorithm)

**Why it exists:**
- Contains complex logic (same-floor priority, travel time calculation)
- Can be tested independently
- Can be reused by multiple controllers

**Example (Simplified):**
```java
@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    public BookingResponse bookRooms(int roomCount) {
        // 1. Find available rooms
        List<Room> availableRooms = roomRepository.findByStatus("available");
        
        // 2. Try same-floor booking first
        List<Room> bookedRooms = trySameFloorBooking(availableRooms, roomCount);
        
        // 3. If not enough on same floor, use multi-floor algorithm
        if (bookedRooms.size() < roomCount) {
            bookedRooms = multiFloorBooking(availableRooms, roomCount);
        }
        
        // 4. Update database
        for (Room room : bookedRooms) {
            room.setStatus("booked");
            roomRepository.save(room);
        }
        
        // 5. Return response
        return new BookingResponse(true, bookedRooms);
    }
}
```

#### 3.3 Repository Layer (RoomRepository.java)

**Job**: Database operations

**Why it exists:**
- Abstracts database queries
- Spring Data JPA generates SQL automatically
- Easy to change database without changing business logic

**Example:**
```java
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    // Find all available rooms
    List<Room> findByStatus(String status);
    
    // Find rooms on specific floor
    List<Room> findByFloor(int floor);
    
    // Find available rooms on specific floor
    List<Room> findByFloorAndStatus(int floor, String status);
}
```

**WHY Interface?**
- Spring Data JPA implements methods automatically
- You just define method names, Spring generates SQL
- `findByStatus("available")` → `SELECT * FROM rooms WHERE status = 'available'`

#### 3.4 Model Layer (Room.java)

**Job**: Define data structure

**Why it exists:**
- Maps Java objects to database tables
- Each Room object = one row in database

**Example:**
```java
@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_no", unique = true, nullable = false)
    private Integer roomNo;
    
    @Column(nullable = false)
    private Integer floor;
    
    @Column(nullable = false)
    private String status;  // "available", "booked"
    
    @Column(nullable = false)
    private Integer position;  // 1-10 (room position on floor, 1 = closest to lift)
    
    // Constructors, getters, setters
}
```

**Annotations Explained:**
- `@Entity` = This class represents a database table
- `@Table(name = "rooms")` = Table name is "rooms"
- `@Id` = Primary key
- `@Column` = Maps to database column

### Booking Algorithm Explanation

#### Assessment Requirements Recap:
1. Max 5 rooms per booking
2. Same-floor booking has highest priority
3. If not available on same floor: choose rooms with minimum travel time
4. Booking may span multiple floors
5. Rooms closer to lift preferred

#### Algorithm Steps (Simple Language):

**Step 1: Check Same-Floor Availability**
```
User wants 3 rooms
  ↓
Check each floor (1 to 10):
  - Floor 1: How many available rooms? (e.g., 8 available)
  - Floor 2: How many available rooms? (e.g., 5 available)
  - Floor 3: How many available rooms? (e.g., 10 available)
  ↓
If any floor has 3+ available rooms:
  → Book 3 rooms on that floor (pick closest to lift)
  → DONE
```

**Step 2: Multi-Floor Booking (If Step 1 fails)**
```
No single floor has 3+ rooms
  ↓
Calculate travel time for all possible combinations:
  - Option 1: Floor 1 (2 rooms) + Floor 2 (1 room)
    Travel time = (walk to room 1) + (walk to room 2) + (stairs to floor 2) + (walk to room 3)
  - Option 2: Floor 2 (2 rooms) + Floor 3 (1 room)
    Travel time = ...
  - Option 3: Floor 1 (1 room) + Floor 2 (1 room) + Floor 3 (1 room)
    Travel time = ...
  ↓
Pick combination with minimum travel time
```

**Travel Time Calculation:**

**Horizontal movement (same floor):**
- Room 101 (position 1) to Room 102 (position 2) = 1 minute
- Room 101 (position 1) to Room 103 (position 3) = 2 minutes
- Formula: `Math.abs(room1.position - room2.position) * 1 minute`

**Vertical movement (different floors):**
- Floor 1 to Floor 2 = 2 minutes
- Floor 1 to Floor 3 = 4 minutes (2 * 2)
- Formula: `Math.abs(room1.floor - room2.floor) * 2 minutes`

**Total travel time:**
```
Start at lift (position 0, floor 0)
  ↓
Walk to room 1: position * 1 minute
  ↓
Walk between rooms on same floor: |position1 - position2| * 1 minute
  ↓
Move to different floor: |floor1 - floor2| * 2 minutes
  ↓
Walk to next room: position * 1 minute
```

**Greedy Approach (Simple Algorithm):**

Instead of calculating all combinations (too complex), use greedy:

1. Start with closest room to lift (position 1, lowest floor)
2. If same floor has more rooms needed, pick next closest on same floor
3. If floor is full, go to next floor
4. Pick closest room to lift on new floor
5. Repeat until we have enough rooms

**Pseudocode:**
```
function bookRooms(roomCount):
    bookedRooms = []
    
    // Sort all available rooms by: floor ASC, position ASC
    availableRooms = sort(availableRooms, by: floor, then position)
    
    // Pick first roomCount rooms (greedy - closest to lift)
    for i = 0 to roomCount - 1:
        bookedRooms.add(availableRooms[i])
    
    return bookedRooms
```

**Why Greedy Works:**
- Always picks closest rooms first
- Naturally minimizes travel time
- Simple to implement
- Fast (O(n log n) for sorting)

### Random Booking Logic

**Requirement**: Randomly book some rooms (for testing)

**Algorithm:**
```java
public void randomBooking() {
    List<Room> allRooms = roomRepository.findAll();
    
    for (Room room : allRooms) {
        // 30% chance to book
        if (Math.random() < 0.3) {
            room.setStatus("booked");
        } else {
            room.setStatus("available");
        }
        roomRepository.save(room);
    }
}
```

### Reset Booking Logic

**Requirement**: Reset all rooms to available

**Algorithm:**
```java
public void resetAll() {
    List<Room> allRooms = roomRepository.findAll();
    
    for (Room room : allRooms) {
        room.setStatus("available");
        roomRepository.save(room);
    }
}
```

---

## 4. Database Design (MySQL)

### Why Database?

**Current Problem (Frontend-only):**
- Rooms stored in JavaScript memory
- Lost when page refreshes
- Can't share data between users
- No persistence

**Solution (Database):**
- Data stored permanently on disk
- Survives server restarts
- Can handle multiple users
- Can query and analyze data

### Database Schema

**Table: `rooms`**

| Column Name | Data Type | Constraints | Description |
|------------|-----------|-------------|-------------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| room_no | INT | UNIQUE, NOT NULL | Room number (101, 102, etc.) |
| floor | INT | NOT NULL | Floor number (1-10) |
| status | VARCHAR(20) | NOT NULL | "available" or "booked" |
| position | INT | NOT NULL | Position on floor (1-10, 1 = closest to lift) |

**SQL to Create Table:**
```sql
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_no INT UNIQUE NOT NULL,
    floor INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'available',
    position INT NOT NULL,
    INDEX idx_floor (floor),
    INDEX idx_status (status),
    INDEX idx_floor_status (floor, status)
);
```

**Why These Columns?**
- `id`: Primary key (required by JPA)
- `room_no`: Room number (101, 102, etc.) - unique identifier for users
- `floor`: Needed for floor-wise grouping and travel time calculation
- `status`: Tracks if room is available or booked
- `position`: Needed for travel time calculation (distance from lift)

**Why Indexes?**
- `idx_floor`: Fast queries like "get all rooms on floor 5"
- `idx_status`: Fast queries like "get all available rooms"
- `idx_floor_status`: Fast queries like "get available rooms on floor 5"
- Indexes make queries faster (like book index vs reading entire book)

### Data Seeding (Initial Data)

**What is Seeding?**
- Inserting initial data when database is empty
- All 97 rooms start as "available"

**SQL Script (data.sql):**

```sql
-- Floors 1-9: 10 rooms each
INSERT INTO rooms (room_no, floor, status, position) VALUES
-- Floor 1
(101, 1, 'available', 1),
(102, 1, 'available', 2),
(103, 1, 'available', 3),
(104, 1, 'available', 4),
(105, 1, 'available', 5),
(106, 1, 'available', 6),
(107, 1, 'available', 7),
(108, 1, 'available', 8),
(109, 1, 'available', 9),
(110, 1, 'available', 10),

-- Floor 2
(201, 2, 'available', 1),
(202, 2, 'available', 2),
-- ... (repeat for floors 2-9)

-- Floor 10: 7 rooms
(1001, 10, 'available', 1),
(1002, 10, 'available', 2),
(1003, 10, 'available', 3),
(1004, 10, 'available', 4),
(1005, 10, 'available', 5),
(1006, 10, 'available', 6),
(1007, 10, 'available', 7);
```

**Total: 9 floors × 10 rooms + 7 rooms = 97 rooms**

**Automated Seeding (Java):**
```java
@Component
public class DataSeeder {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @PostConstruct  // Runs after Spring Boot starts
    public void seedData() {
        if (roomRepository.count() == 0) {  // Only if database is empty
            // Generate and save all 97 rooms
            for (int floor = 1; floor <= 9; floor++) {
                for (int pos = 1; pos <= 10; pos++) {
                    Room room = new Room();
                    room.setRoomNo(floor * 100 + pos);
                    room.setFloor(floor);
                    room.setPosition(pos);
                    room.setStatus("available");
                    roomRepository.save(room);
                }
            }
            // Floor 10
            for (int pos = 1; pos <= 7; pos++) {
                Room room = new Room();
                room.setRoomNo(1000 + pos);
                room.setFloor(10);
                room.setPosition(pos);
                room.setStatus("available");
                roomRepository.save(room);
            }
        }
    }
}
```

---

## 5. API Design

### What is an API?

**Simple Explanation:**
- API = Application Programming Interface
- Contract between frontend and backend
- Defines how they communicate

**Real-world analogy:**
- Restaurant menu = API
- Menu lists dishes (endpoints) and prices (response format)
- You order (send request), waiter brings food (response)

### API Endpoints

#### Endpoint 1: Get All Rooms

**Purpose**: Frontend needs room list to display

**HTTP Method**: GET (fetching data)

**URL**: `http://localhost:8080/api/rooms`

**Request**: None (GET requests don't have body)

**Response:**
```json
{
  "success": true,
  "rooms": [
    {
      "id": 1,
      "roomNo": 101,
      "floor": 1,
      "status": "available",
      "position": 1
    },
    {
      "id": 2,
      "roomNo": 102,
      "floor": 1,
      "status": "booked",
      "position": 2
    },
    // ... 95 more rooms
  ]
}
```

**Backend Implementation:**
```java
@GetMapping("/rooms")
public ResponseEntity<RoomListResponse> getAllRooms() {
    List<Room> rooms = roomService.getAllRooms();
    return ResponseEntity.ok(new RoomListResponse(true, rooms));
}
```

#### Endpoint 2: Book Rooms

**Purpose**: User wants to book N rooms

**HTTP Method**: POST (creating/updating data)

**URL**: `http://localhost:8080/api/bookings`

**Request:**
```json
{
  "roomCount": 3
}
```

**Response (Success):**
```json
{
  "success": true,
  "message": "Successfully booked 3 rooms",
  "bookedRooms": [
    {
      "id": 1,
      "roomNo": 101,
      "floor": 1,
      "status": "booked",
      "position": 1
    },
    {
      "id": 2,
      "roomNo": 102,
      "floor": 1,
      "status": "booked",
      "position": 2
    },
    {
      "id": 3,
      "roomNo": 103,
      "floor": 1,
      "status": "booked",
      "position": 3
    }
  ]
}
```

**Response (Error):**
```json
{
  "success": false,
  "message": "Not enough rooms available. Only 2 rooms available.",
  "bookedRooms": []
}
```

**Backend Implementation:**
```java
@PostMapping("/bookings")
public ResponseEntity<BookingResponse> bookRooms(@RequestBody BookingRequest request) {
    try {
        // Validate
        if (request.getRoomCount() < 1 || request.getRoomCount() > 5) {
            return ResponseEntity.badRequest()
                .body(new BookingResponse(false, "Room count must be between 1 and 5", null));
        }
        
        // Book rooms
        BookingResponse response = roomService.bookRooms(request.getRoomCount());
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    } catch (Exception e) {
        return ResponseEntity.status(500)
            .body(new BookingResponse(false, "Internal server error", null));
    }
}
```

#### Endpoint 3: Random Booking

**Purpose**: Randomly book rooms (for testing)

**HTTP Method**: POST

**URL**: `http://localhost:8080/api/bookings/random`

**Request**: None

**Response:**
```json
{
  "success": true,
  "message": "Random booking completed"
}
```

**Backend Implementation:**
```java
@PostMapping("/bookings/random")
public ResponseEntity<Map<String, Object>> randomBooking() {
    roomService.randomBooking();
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "Random booking completed");
    return ResponseEntity.ok(response);
}
```

#### Endpoint 4: Reset All

**Purpose**: Reset all rooms to available

**HTTP Method**: POST

**URL**: `http://localhost:8080/api/bookings/reset`

**Request**: None

**Response:**
```json
{
  "success": true,
  "message": "All rooms reset to available"
}
```

**Backend Implementation:**
```java
@PostMapping("/bookings/reset")
public ResponseEntity<Map<String, Object>> resetAll() {
    roomService.resetAll();
    Map<String, Object> response = new HashMap<>();
    response.put("success", true);
    response.put("message", "All rooms reset to available");
    return ResponseEntity.ok(response);
}
```

### Request/Response Objects (DTOs)

**Why DTOs?**
- Don't expose internal model directly
- Control what data is sent/received
- Can add validation
- Easier to change API without changing database

**BookingRequest.java:**
```java
public class BookingRequest {
    @NotNull
    @Min(1)
    @Max(5)
    private Integer roomCount;
    
    // Constructors, getters, setters
}
```

**BookingResponse.java:**
```java
public class BookingResponse {
    private boolean success;
    private String message;
    private List<Room> bookedRooms;
    
    // Constructors, getters, setters
}
```

---

## 6. Frontend ↔ Backend Integration

### How Frontend Calls Backend

**Technology**: Fetch API (built into browsers)

**Example: Get All Rooms**

```javascript
// In App.jsx
const fetchRooms = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/rooms');
    const data = await response.json();
    
    if (data.success) {
      setRooms(data.rooms);  // Update React state
    }
  } catch (error) {
    console.error('Error fetching rooms:', error);
    alert('Failed to load rooms');
  }
};

// Call when component loads
useEffect(() => {
  fetchRooms();
}, []);
```

**Example: Book Rooms**

```javascript
const bookRooms = async () => {
  try {
    const response = await fetch('http://localhost:8080/api/bookings', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({
        roomCount: count
      })
    });
    
    const data = await response.json();
    
    if (data.success) {
      // Refresh room list to show updated status
      fetchRooms();
      alert(`Successfully booked ${data.bookedRooms.length} rooms`);
    } else {
      alert(data.message);
    }
  } catch (error) {
    console.error('Error booking rooms:', error);
    alert('Failed to book rooms');
  }
};
```

### CORS Configuration

**What is CORS?**
- Cross-Origin Resource Sharing
- Browser security feature
- Frontend (localhost:3000) and backend (localhost:8080) are different origins
- Browser blocks requests unless backend allows it

**Fix: Add CORS configuration in Spring Boot**

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("http://localhost:3000")  // Frontend URL
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*");
            }
        };
    }
}
```

**WHY needed?**
- Without this, browser blocks API calls
- You'll see CORS error in console
- This tells browser: "It's okay to accept requests from localhost:3000"

### Data Flow (Complete Example)

**User books 3 rooms:**

1. **Frontend**: User types "3", clicks BOOK
2. **Frontend**: `bookRooms()` function runs
3. **Frontend**: Sends POST request to `http://localhost:8080/api/bookings`
   ```json
   { "roomCount": 3 }
   ```
4. **Backend Controller**: `RoomController.bookRooms()` receives request
5. **Backend Controller**: Validates roomCount (1-5)
6. **Backend Service**: `RoomService.bookRooms(3)` runs algorithm
7. **Backend Service**: Finds best 3 rooms (same-floor or minimum travel time)
8. **Backend Repository**: Updates database (sets status to "booked")
9. **Backend Service**: Returns `BookingResponse` with booked rooms
10. **Backend Controller**: Sends HTTP response (status 200, JSON body)
11. **Frontend**: Receives response
12. **Frontend**: Calls `fetchRooms()` to get updated room list
13. **Frontend**: Updates React state with new room data
14. **Frontend**: UI re-renders, booked rooms change color

### Error Handling

**Network Error:**
```javascript
try {
  const response = await fetch('http://localhost:8080/api/bookings', {...});
  // ...
} catch (error) {
  // Network error (server down, no internet)
  alert('Cannot connect to server. Please check if backend is running.');
}
```

**HTTP Error (400, 500, etc.):**
```javascript
const response = await fetch('...');
if (!response.ok) {
  // HTTP error status (400, 500, etc.)
  const errorData = await response.json();
  alert(errorData.message);
}
```

**Business Logic Error:**
```javascript
const data = await response.json();
if (!data.success) {
  // Backend returned error (e.g., "Not enough rooms")
  alert(data.message);
}
```

---

## 7. Assessment Requirements Mapping

### Requirement 1: Hotel Structure
- **Requirement**: 97 rooms, floors 1-9 (10 each), floor 10 (7 rooms)
- **Frontend**: `roomsData.js` generates 97 rooms (currently in memory)
- **Backend**: Database table `rooms` with 97 rows, seeding script
- **Status**: ✅ Implemented

### Requirement 2: Room Layout
- **Requirement**: Lift on left, rooms arranged left to right
- **Frontend**: CSS flexbox arranges rooms horizontally
- **Backend**: `position` column (1 = closest to lift)
- **Status**: ✅ Implemented

### Requirement 3: Travel Time Rules
- **Requirement**: Horizontal = 1 min/room, Vertical = 2 min/floor
- **Frontend**: Display only (no calculation)
- **Backend**: `RoomService` calculates travel time in booking algorithm
- **Status**: ⚠️ Needs implementation in backend

### Requirement 4: Booking Rules
- **Requirement**: Max 5 rooms per booking
- **Frontend**: Input `max="5"` attribute
- **Backend**: Validation in `RoomController` and `BookingRequest`
- **Status**: ✅ Implemented

### Requirement 5: Same-Floor Priority
- **Requirement**: Same-floor booking has highest priority
- **Frontend**: Display only
- **Backend**: `RoomService.bookRooms()` checks same-floor first
- **Status**: ⚠️ Needs implementation in backend

### Requirement 6: Multi-Floor Booking
- **Requirement**: Booking may span multiple floors
- **Frontend**: Display only
- **Backend**: `RoomService` handles multi-floor selection
- **Status**: ⚠️ Needs implementation in backend

### Requirement 7: Minimum Travel Time
- **Requirement**: If not same-floor, choose minimum travel time
- **Frontend**: Display only
- **Backend**: Algorithm calculates travel time for all options
- **Status**: ⚠️ Needs implementation in backend

### Requirement 8: UI Requirements
- **Requirement**: Input field, BOOK/RANDOM/RESET buttons, visual grid, color-coding
- **Frontend**: `Controls.jsx`, `HotelGrid.jsx`, `Room.jsx`, CSS colors
- **Backend**: N/A
- **Status**: ✅ Implemented

---

## 8. Common Mistakes & Debugging

### Mistake 1: CORS Error

**Symptom:**
```
Access to fetch at 'http://localhost:8080/api/rooms' from origin 'http://localhost:3000' 
has been blocked by CORS policy
```

**Cause**: Backend doesn't allow requests from frontend origin

**Fix**: Add CORS configuration in Spring Boot (see Section 6)

### Mistake 2: 404 Not Found

**Symptom:**
```
GET http://localhost:8080/api/rooms 404 (Not Found)
```

**Causes:**
- Backend not running
- Wrong URL (typo in endpoint)
- Controller not mapped correctly

**Fix:**
1. Check backend is running (console shows "Started Application")
2. Verify URL matches `@RequestMapping` in controller
3. Check endpoint path matches `@GetMapping` or `@PostMapping`

### Mistake 3: CSS Not Loading

**Symptom**: Rooms show as plain text, no colors

**Causes:**
- Wrong import path
- CSS file not found
- Browser cache

**Fix:**
1. Check import: `import "./styles/main.css";` (correct spelling)
2. Verify file exists: `src/styles/main.css`
3. Hard refresh: Ctrl+Shift+R

### Mistake 4: State Not Updating

**Symptom**: UI doesn't change after API call

**Cause**: Forgot to update state after receiving response

**Fix:**
```javascript
// Wrong:
const response = await fetch('...');
const data = await response.json();
// Missing: setRooms(data.rooms);

// Correct:
const response = await fetch('...');
const data = await response.json();
setRooms(data.rooms);  // Update state
```

### Mistake 5: Database Not Connecting

**Symptom**: Backend fails to start, database connection error

**Causes:**
- MySQL not running
- Wrong database credentials
- Database doesn't exist

**Fix:**
1. Start MySQL service
2. Check `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```
3. Create database: `CREATE DATABASE hotel_db;`

### Mistake 6: Rooms Not Seeding

**Symptom**: Empty database, no rooms

**Cause**: Seeding script not running

**Fix:**
1. Check `@PostConstruct` annotation on seeder class
2. Verify `roomRepository.count() == 0` check
3. Check console for errors
4. Manually run SQL script if needed

### Debugging Tips

**1. Check Browser Console (F12)**
- Errors show in red
- Network tab shows API calls and responses
- Console.log() for debugging

**2. Check Backend Console**
- Spring Boot shows errors
- Check log messages
- Look for exception stack traces

**3. Test API with Postman**
- Test backend independently
- Verify endpoints work
- Check request/response format

**4. Use Debugger**
- Set breakpoints in browser (F12 → Sources)
- Set breakpoints in IDE (IntelliJ/Eclipse)
- Step through code line by line

---

## 9. Interview & Submission Readiness

### How to Explain Project in Interview (2-Minute Version)

**Opening (30 seconds):**
"I built a full-stack Hotel Room Reservation System where users can book hotel rooms. The system has 97 rooms across 10 floors, and implements intelligent booking logic that prioritizes same-floor bookings and minimizes travel time."

**Architecture (30 seconds):**
"The application follows a three-tier architecture. Frontend is built with React, which provides a responsive UI with real-time updates. Backend uses Spring Boot with REST APIs, handling business logic like same-floor priority and travel time calculations. MySQL database stores all room data persistently."

**Key Features (30 seconds):**
"The booking algorithm first tries to book rooms on the same floor. If not possible, it calculates travel time considering horizontal movement (1 minute per room) and vertical movement (2 minutes per floor), then selects the combination with minimum travel time. The system also includes features like random booking for testing and reset functionality."

**Technical Highlights (30 seconds):**
"I used Spring Data JPA for database operations, which simplified queries. Frontend uses React hooks for state management, and I implemented proper error handling and CORS configuration for cross-origin requests. The system is fully functional with proper separation of concerns between controller, service, and repository layers."

### Architecture Explanation (Detailed)

**If asked: "Explain the architecture":**

1. **Three-Tier Architecture**
   - Presentation Layer (React Frontend)
   - Business Logic Layer (Spring Boot Backend)
   - Data Layer (MySQL Database)

2. **Layer Responsibilities**
   - Frontend: UI, user interaction, API calls
   - Controller: HTTP request handling, validation
   - Service: Business logic, algorithms
   - Repository: Database operations
   - Database: Data persistence

3. **Data Flow**
   - User action → Frontend → API call → Controller → Service → Repository → Database
   - Response flows back in reverse

4. **Why This Architecture?**
   - Separation of concerns
   - Scalability
   - Maintainability
   - Testability

### Submission Checklist

**Frontend:**
- [ ] All components render correctly
- [ ] CSS styles applied
- [ ] API calls working (GET rooms, POST booking, etc.)
- [ ] Error handling implemented
- [ ] Responsive design (works on different screen sizes)
- [ ] No console errors

**Backend:**
- [ ] Spring Boot application starts successfully
- [ ] Database connection working
- [ ] All 97 rooms seeded
- [ ] API endpoints working (test with Postman)
- [ ] Booking algorithm implements same-floor priority
- [ ] Travel time calculation working
- [ ] CORS configured
- [ ] Error handling for edge cases

**Database:**
- [ ] Database created
- [ ] Table structure correct
- [ ] Initial data loaded (97 rooms)
- [ ] Indexes created for performance

**Documentation:**
- [ ] README.md with setup instructions
- [ ] API documentation (endpoints, request/response format)
- [ ] Code comments for complex logic
- [ ] Architecture diagram (optional but good)

**Testing:**
- [ ] Manual testing: Book rooms, random booking, reset
- [ ] Edge cases: Book 5 rooms (max), book when not enough available
- [ ] Error scenarios: Invalid input, server errors

### Common Interview Questions & Answers

**Q: Why did you choose Spring Boot?**
A: "Spring Boot provides auto-configuration which reduces boilerplate code, has built-in server for easy deployment, and integrates seamlessly with Spring Data JPA for database operations. It's also industry-standard and widely used."

**Q: How does your booking algorithm work?**
A: "The algorithm first checks if enough rooms are available on a single floor, prioritizing same-floor bookings. If not, it calculates travel time for multi-floor combinations, considering horizontal movement (1 min per room) and vertical movement (2 min per floor), then selects the combination with minimum travel time using a greedy approach."

**Q: How do you handle concurrent bookings?**
A: "Currently, the system processes bookings sequentially. For production, I would implement database locking or use optimistic locking to handle concurrent requests and prevent double-booking."

**Q: What improvements would you make?**
A: "I would add user authentication, booking history, cancellation feature, admin dashboard, room types (single/double), pricing, payment integration, and email notifications. I would also implement caching for frequently accessed data and add comprehensive unit tests."

**Q: Explain the travel time calculation.**
A: "Travel time is calculated based on horizontal and vertical movement. Moving between adjacent rooms on the same floor takes 1 minute. Moving between floors takes 2 minutes per floor. The algorithm sums up all movements needed to visit all booked rooms, starting from the lift, to calculate total travel time."

---

## Summary

This guide covers:
1. ✅ System architecture and data flow
2. ✅ Frontend structure and components
3. ✅ Backend layers and responsibilities
4. ✅ Database design and seeding
5. ✅ API design and endpoints
6. ✅ Integration between frontend and backend
7. ✅ Mapping to assessment requirements
8. ✅ Common mistakes and debugging
9. ✅ Interview preparation

**Next Steps:**
1. Set up Spring Boot backend project
2. Create database and tables
3. Implement backend layers (Controller, Service, Repository, Model)
4. Implement booking algorithm
5. Connect frontend to backend APIs
6. Test end-to-end
7. Prepare for submission/interview

Good luck with your project! 🚀

