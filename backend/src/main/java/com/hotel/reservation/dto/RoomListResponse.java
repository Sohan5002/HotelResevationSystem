package com.hotel.reservation.dto;

import com.hotel.reservation.model.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomListResponse {
    private boolean success;
    private List<Room> rooms;
}

