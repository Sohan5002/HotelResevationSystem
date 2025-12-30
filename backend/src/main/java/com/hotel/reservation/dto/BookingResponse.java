package com.hotel.reservation.dto;

import com.hotel.reservation.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private boolean success;
    private String message;
    private List<Room> bookedRooms;
    
    public BookingResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.bookedRooms = null;
    }
}

