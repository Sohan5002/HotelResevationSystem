// src/components/HotelGrid.jsx
import React from "react";
import Room from "./Room";

function HotelGrid({ rooms }) {
  const floors = {};

  // Rooms ko floor-wise group karna
  rooms.forEach(room => {
    if (!floors[room.floor]) {
      floors[room.floor] = [];
    }
    floors[room.floor].push(room);
  });

  return (
    <div>
      {Object.keys(floors).map(floor => (
        <div key={floor} className="floor">
          <h3>Floor {floor}</h3>
          <div className="floor-rooms">
            {floors[floor].map(room => (
              <Room key={room.roomNo} room={room} />
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}

export default HotelGrid;
