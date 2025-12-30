package com.hotel.reservation.controller;

import com.hotel.reservation.dto.BookingRequest;
import com.hotel.reservation.dto.BookingResponse;
import com.hotel.reservation.dto.RoomListResponse;
import com.hotel.reservation.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "http://localhost:3000")
public class RoomController {
    
    @Autowired
    private RoomService roomService;
    
    /**
     * Get all rooms
     * GET /api/rooms
     */
    @GetMapping
    public ResponseEntity<RoomListResponse> getAllRooms() {
        try {
            RoomListResponse response = new RoomListResponse(true, roomService.getAllRooms());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Book rooms
     * POST /api/rooms/book
     */
    @PostMapping("/book")
    public ResponseEntity<BookingResponse> bookRooms(@RequestBody BookingRequest request) {
        try {
            if (request.getRoomCount() == null || request.getRoomCount() < 1 || request.getRoomCount() > 5) {
                return ResponseEntity.badRequest()
                    .body(new BookingResponse(false, "Room count must be between 1 and 5"));
            }
            
            BookingResponse response = roomService.bookRooms(request.getRoomCount());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BookingResponse(false, "Internal server error: " + e.getMessage()));
        }
    }
    
    /**
     * Random booking
     * POST /api/rooms/random
     */
    @PostMapping("/random")
    public ResponseEntity<Map<String, Object>> randomBooking() {
        try {
            roomService.randomBooking();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Random booking completed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reset all rooms
     * POST /api/rooms/reset
     */
    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetAll() {
        try {
            roomService.resetAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All rooms reset to available");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

