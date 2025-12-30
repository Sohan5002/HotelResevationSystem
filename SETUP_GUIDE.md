# Complete Setup Guide - Hotel Room Reservation System

## ✅ Project Status

All code has been implemented and is ready to run!

## 📦 What Has Been Created

### Backend (Spring Boot)
- ✅ Complete Maven project structure
- ✅ All Java classes (Model, Repository, Service, Controller, DTOs)
- ✅ Database configuration
- ✅ Data seeding (97 rooms)
- ✅ Booking algorithm implementation
- ✅ CORS configuration
- ✅ All REST API endpoints

### Frontend (React)
- ✅ Updated to connect to backend APIs
- ✅ All components working
- ✅ API integration complete
- ✅ Error handling implemented

## 🚀 Step-by-Step Setup Instructions

### Step 1: Install Prerequisites

1. **Java 17+**
   - Download from: https://adoptium.net/
   - Verify: `java -version` (should show 17 or higher)

2. **Maven**
   - Download from: https://maven.apache.org/download.cgi
   - Verify: `mvn -version`

3. **MySQL 8.0+**
   - Download from: https://dev.mysql.com/downloads/mysql/
   - Install and start MySQL service

4. **Node.js 14+**
   - Download from: https://nodejs.org/
   - Verify: `node -v` and `npm -v`

### Step 2: Database Setup

1. **Start MySQL service**
   ```bash
   # Windows (as Administrator)
   net start MySQL80
   
   # macOS/Linux
   sudo systemctl start mysql
   ```

2. **Login to MySQL**
   ```bash
   mysql -u root -p
   ```

3. **Create Database (Optional - will auto-create if configured)**
   ```sql
   CREATE DATABASE IF NOT EXISTS hotel_reservation;
   USE hotel_reservation;
   ```

4. **Note your MySQL password** - you'll need it in Step 3

### Step 3: Backend Configuration

1. **Edit database credentials**
   - Open: `backend/src/main/resources/application.properties`
   - Update these lines:
     ```properties
     spring.datasource.username=root
     spring.datasource.password=YOUR_MYSQL_PASSWORD_HERE
     ```
   - Replace `YOUR_MYSQL_PASSWORD_HERE` with your actual MySQL password

2. **If MySQL has no password**, leave it blank:
   ```properties
   spring.datasource.password=
   ```

### Step 4: Start Backend Server

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Build the project (first time only)**
   ```bash
   mvn clean install
   ```
   - This downloads all dependencies (may take 2-5 minutes first time)

3. **Run the backend**
   ```bash
   mvn spring-boot:run
   ```

4. **Verify backend is running**
   - Wait for message: "Started HotelReservationApplication"
   - You should see: "✅ Seeded 97 rooms into database"
   - Backend runs on: `http://localhost:8080`

5. **Test backend (optional)**
   - Open browser: `http://localhost:8080/api/rooms`
   - You should see JSON with all rooms

### Step 5: Start Frontend Server

1. **Open a NEW terminal window** (keep backend running)

2. **Navigate to project root**
   ```bash
   cd hotel-room-booking
   ```

3. **Install dependencies (first time only)**
   ```bash
   npm install
   ```
   - This downloads React dependencies (may take 1-2 minutes)

4. **Start frontend**
   ```bash
   npm start
   ```

5. **Frontend will open automatically**
   - Opens in browser: `http://localhost:3000`
   - If not, manually open: `http://localhost:3000`

### Step 6: Use the Application

1. **You should see:**
   - Title: "Hotel Room Reservation"
   - Input field for number of rooms (1-5)
   - Three buttons: BOOK, RANDOM, RESET
   - Grid showing all 97 rooms (floors 1-10)

2. **Test Booking:**
   - Enter "3" in the input field
   - Click "BOOK" button
   - Three rooms should turn blue (newly booked)
   - After a few seconds, they turn red (booked)

3. **Test Random Booking:**
   - Click "RANDOM" button
   - About 30% of rooms should turn red

4. **Test Reset:**
   - Click "RESET" button
   - All rooms should turn green (available)

## 🔍 Verification Checklist

### Backend Verification:
- [ ] Backend starts without errors
- [ ] Console shows "Started HotelReservationApplication"
- [ ] Console shows "✅ Seeded 97 rooms into database"
- [ ] `http://localhost:8080/api/rooms` returns JSON

### Frontend Verification:
- [ ] Frontend starts without errors
- [ ] Browser shows hotel room grid
- [ ] All 97 rooms are visible
- [ ] Colors: Green (available), Red (booked), Blue (new)
- [ ] BOOK button works
- [ ] RANDOM button works
- [ ] RESET button works

### Integration Verification:
- [ ] Frontend loads rooms from backend
- [ ] Booking updates database
- [ ] UI updates after booking
- [ ] No CORS errors in browser console

## 🐛 Common Issues & Solutions

### Issue 1: Backend won't start - "Port 8080 already in use"

**Solution:**
```bash
# Windows - Find process using port 8080
netstat -ano | findstr :8080
# Kill the process (replace PID with actual process ID)
taskkill /PID <PID> /F

# macOS/Linux
lsof -ti:8080 | xargs kill -9
```

Or change port in `application.properties`:
```properties
server.port=8081
```
Then update frontend `API_BASE_URL` in `src/App.jsx` to `http://localhost:8081`

### Issue 2: Database connection error

**Symptoms:**
- Error: "Access denied for user 'root'@'localhost'"
- Error: "Communications link failure"

**Solutions:**
1. Verify MySQL is running
2. Check password in `application.properties`
3. Verify database exists:
   ```sql
   SHOW DATABASES;
   ```
4. Try creating database manually:
   ```sql
   CREATE DATABASE hotel_reservation;
   ```

### Issue 3: Frontend can't connect to backend

**Symptoms:**
- Browser console shows: "Failed to fetch"
- Error: "NetworkError when attempting to fetch resource"

**Solutions:**
1. Verify backend is running on port 8080
2. Check browser console for errors
3. Verify API URL in `src/App.jsx` is correct
4. Check CORS is enabled (should be in `RoomController.java`)

### Issue 4: Rooms not showing

**Solutions:**
1. Check backend console - should show "Seeded 97 rooms"
2. Verify database has data:
   ```sql
   USE hotel_reservation;
   SELECT COUNT(*) FROM rooms;
   ```
   Should return 97
3. Test API directly: `http://localhost:8080/api/rooms`
4. Check browser console for JavaScript errors

### Issue 5: Maven build fails

**Solutions:**
1. Check Java version: `java -version` (need 17+)
2. Check Maven version: `mvn -version`
3. Try cleaning and rebuilding:
   ```bash
   mvn clean
   mvn install
   ```
4. Check internet connection (Maven downloads dependencies)

### Issue 6: npm install fails

**Solutions:**
1. Clear npm cache:
   ```bash
   npm cache clean --force
   ```
2. Delete node_modules and package-lock.json:
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```
3. Check Node.js version: `node -v` (need 14+)

## 📝 Project Structure Reference

```
hotel-room-booking/
├── backend/                          # Spring Boot backend
│   ├── pom.xml                       # Maven dependencies
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/hotel/reservation/
│   │       │       ├── HotelReservationApplication.java    # Main class
│   │       │       ├── controller/
│   │       │       │   └── RoomController.java             # REST endpoints
│   │       │       ├── service/
│   │       │       │   └── RoomService.java                # Business logic
│   │       │       ├── repository/
│   │       │       │   └── RoomRepository.java             # Database access
│   │       │       ├── model/
│   │       │       │   └── Room.java                       # Room entity
│   │       │       ├── dto/
│   │       │       │   ├── BookingRequest.java
│   │       │       │   ├── BookingResponse.java
│   │       │       │   └── RoomListResponse.java
│   │       │       └── config/
│   │       │           └── DataSeeder.java                 # Initial data
│   │       └── resources/
│   │           └── application.properties                  # Config
│   └── README.md
│
├── src/                              # React frontend
│   ├── components/
│   │   ├── Controls.jsx              # Input & buttons
│   │   ├── HotelGrid.jsx             # Room grid
│   │   └── Room.jsx                  # Single room
│   ├── styles/
│   │   └── main.css                  # Styles
│   └── App.jsx                       # Main component (API calls)
│
├── README.md                         # Project documentation
└── SETUP_GUIDE.md                    # This file
```

## 🎯 Next Steps After Setup

1. ✅ Verify everything works using the checklist above
2. ✅ Test all features: BOOK, RANDOM, RESET
3. ✅ Check database persistence (refresh page - rooms should retain state)
4. ✅ Review code structure
5. ✅ Ready for submission or interview!

## 📞 Need Help?

If you encounter any issues:
1. Check the "Common Issues & Solutions" section above
2. Check browser console (F12) for errors
3. Check backend console for errors
4. Verify all prerequisites are installed correctly
5. Review the main README.md file

---

**Good luck! The project is complete and ready to run! 🚀**

