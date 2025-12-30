# How to Run the Spring Boot Application

## Quick Start

### Method 1: Using Maven (Recommended)

1. **Open Terminal** in VS Code (Ctrl + ` or View → Terminal)

2. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

   First time will download dependencies (2-5 minutes). Subsequent runs are faster.

4. **Verify it's running:**
   - Look for: `Started HotelReservationApplication`
   - Server runs on: `http://localhost:8080`

### Method 2: Using Code Runner Extension

The VS Code Code Runner extension is now configured to automatically use Maven for Spring Boot projects.

**To use:**
1. Open any Java file in the `backend/src/main/java` directory
2. Press `Ctrl+Alt+N` (or right-click → Run Code)
3. It will automatically detect it's a Spring Boot project and use `mvn spring-boot:run`

**Note:** If Maven is not in your PATH, you'll need to add it first (see "Maven Not Found" below).

### Method 3: Build JAR and Run

1. **Build the project:**
   ```bash
   cd backend
   mvn clean package
   ```

2. **Run the JAR:**
   ```bash
   java -jar target/reservation-system-1.0.0.jar
   ```

## Troubleshooting

### Maven Not Found

If you see: `mvn : The term 'mvn' is not recognized`

**Solution:**

1. **Install Maven** (if not installed):
   - Download from: https://maven.apache.org/download.cgi
   - Extract to a folder (e.g., `C:\Program Files\Apache\maven`)
   
2. **Add Maven to PATH:**
   - Open System Properties → Environment Variables
   - Edit "Path" variable
   - Add Maven bin directory: `C:\Program Files\Apache\maven\bin`
   - Restart VS Code

3. **Verify installation:**
   ```bash
   mvn -version
   ```
   Should show Maven version and Java version.

### Java Not Found

If you see Java errors:

1. **Verify Java 17+ is installed:**
   ```bash
   java -version
   ```

2. **Install Java 17+** if needed:
   - Download from: https://adoptium.net/
   - Install and add to PATH

### Database Connection Error

If you see MySQL connection errors:

1. **Start MySQL service:**
   ```bash
   # Windows (as Administrator)
   net start MySQL80
   ```

2. **Check database credentials** in `backend/src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=root
   spring.datasource.password=YOUR_PASSWORD_HERE
   ```

3. **Create database** (if needed):
   ```sql
   CREATE DATABASE hotel_reservation;
   ```

### Port 8080 Already in Use

If port 8080 is already in use:

1. **Find what's using it:**
   ```bash
   netstat -ano | findstr :8080
   ```

2. **Kill the process** (replace PID with actual process ID):
   ```bash
   taskkill /PID <PID> /F
   ```

3. **Or change port** in `application.properties`:
   ```properties
   server.port=8081
   ```
   (Then update frontend API URL to `http://localhost:8081`)

## Common Commands

```bash
# Build project
mvn clean install

# Run application
mvn spring-boot:run

# Run tests
mvn test

# Build JAR file
mvn clean package

# Skip tests during build
mvn clean package -DskipTests
```

## Expected Output

When the application starts successfully, you should see:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

✅ Seeded 97 rooms into database

Started HotelReservationApplication in X.XXX seconds
```

## Next Steps

After the backend is running:
1. Start the frontend (in another terminal):
   ```bash
   npm start
   ```
2. Open browser: `http://localhost:3000`
3. The frontend will connect to the backend automatically

