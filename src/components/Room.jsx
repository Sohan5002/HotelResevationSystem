// src/components/Room.jsx
import React from "react";

function Room({ room }) {
  // Build className string with base class and status class
  let className = "room";
  
  if (room.status === "booked") {
    className += " booked";
  } else if (room.status === "new") {
    className += " new";
  }

  return (
    <div className={className}>
      {room.roomNo}
    </div>
  );
}

export default Room;
