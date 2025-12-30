// src/components/Controls.jsx
import React from "react";

function Controls({ value, setValue, onBook, onRandom, onReset }) {
  return (
    <div className="controls">
      <input
        type="number"
        min="1"
        max="5"
        value={value}
        onChange={(e) => setValue(Number(e.target.value) || 1)}
        placeholder="Rooms"
      />

      <button onClick={onBook}>BOOK</button>
      <button onClick={onRandom}>RANDOM</button>
      <button onClick={onReset}>RESET</button>
    </div>
  );
}

export default Controls;
