// src/App.jsx
import React, { useState, useEffect } from "react";
import Controls from "./components/Controls";
import HotelGrid from "./components/HotelGrid";
import "./styles/main.css";

const API_BASE_URL = "http://localhost:8080/api/rooms";

function App() {
  const [rooms, setRooms] = useState([]);
  const [count, setCount] = useState(1);
  const [loading, setLoading] = useState(true);
  const [newlyBookedRooms, setNewlyBookedRooms] = useState(new Set());

  // Fetch all rooms from backend
  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await fetch(API_BASE_URL);
      const data = await response.json();
      
      if (data.success) {
        setRooms(data.rooms);
      } else {
        alert("Failed to load rooms");
      }
    } catch (error) {
      console.error("Error fetching rooms:", error);
      alert("Cannot connect to server. Please make sure the backend is running on port 8080.");
    } finally {
      setLoading(false);
    }
  };

  // Fetch rooms on component mount
  useEffect(() => {
    fetchRooms();
  }, []);

  // BOOK rooms - call backend API
  const bookRooms = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/book`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          roomCount: count,
        }),
      });

      const data = await response.json();

      if (data.success) {
        // Track newly booked room numbers for UI highlighting
        const newRoomNos = new Set(data.bookedRooms.map((r) => r.roomNo));
        setNewlyBookedRooms(newRoomNos);
        
        // Refresh room list
        await fetchRooms();
        
        alert(`Successfully booked ${data.bookedRooms.length} room(s)!`);
        
        // Clear newly booked highlight after 3 seconds
        setTimeout(() => {
          setNewlyBookedRooms(new Set());
        }, 3000);
      } else {
        alert(data.message || "Failed to book rooms");
      }
    } catch (error) {
      console.error("Error booking rooms:", error);
      alert("Cannot connect to server. Please make sure the backend is running.");
    }
  };

  // RANDOM booking - call backend API
  const randomBooking = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/random`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const data = await response.json();

      if (data.success) {
        // Refresh room list
        await fetchRooms();
        alert("Random booking completed!");
      } else {
        alert(data.message || "Failed to perform random booking");
      }
    } catch (error) {
      console.error("Error performing random booking:", error);
      alert("Cannot connect to server. Please make sure the backend is running.");
    }
  };

  // RESET - call backend API
  const resetAll = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/reset`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
      });

      const data = await response.json();

      if (data.success) {
        // Clear newly booked rooms tracking
        setNewlyBookedRooms(new Set());
        // Refresh room list
        await fetchRooms();
        alert("All rooms have been reset to available!");
      } else {
        alert(data.message || "Failed to reset rooms");
      }
    } catch (error) {
      console.error("Error resetting rooms:", error);
      alert("Cannot connect to server. Please make sure the backend is running.");
    }
  };

  // Map rooms to include "new" status for UI highlighting
  const roomsWithStatus = rooms.map((room) => {
    if (newlyBookedRooms.has(room.roomNo)) {
      return { ...room, status: "new" };
    }
    return room;
  });

  if (loading) {
    return (
      <div className="app">
        <h1>Hotel Room Reservation</h1>
        <p>Loading rooms...</p>
      </div>
    );
  }

  return (
    <div className="app">
      <h1>Hotel Room Reservation</h1>

      <Controls
        value={count}
        setValue={setCount}
        onBook={bookRooms}
        onRandom={randomBooking}
        onReset={resetAll}
      />

      <HotelGrid rooms={roomsWithStatus} />
    </div>
  );
}

export default App;
