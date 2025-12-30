// src/data/roomsData.js

export function generateRooms() {
    const rooms = [];
  
    // Floors 1 to 9 (10 rooms each)
    for (let floor = 1; floor <= 9; floor++) {
      for (let i = 1; i <= 10; i++) {
        rooms.push({
          roomNo: floor * 100 + i,
          floor: floor,
          status: "available"
        });
      }
    }
  
    // Floor 10 (7 rooms)
    for (let i = 1; i <= 7; i++) {
      rooms.push({
        roomNo: 1000 + i,
        floor: 10,
        status: "available"
      });
    }
  
    return rooms;
  }
  