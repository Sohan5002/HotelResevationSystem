package com.hotel.reservation.service;

import com.hotel.reservation.dto.BookingResponse;
import com.hotel.reservation.model.Room;
import com.hotel.reservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    /**
     * Get all rooms
     */
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    /**
     * Book rooms with intelligent algorithm:
     * 1. Try same-floor booking first (highest priority)
     * 2. If not possible, use greedy algorithm to minimize travel time
     */
    public BookingResponse bookRooms(int roomCount) {
        // Validate room count
        if (roomCount < 1 || roomCount > 5) {
            return new BookingResponse(false, "Room count must be between 1 and 5");
        }
        
        // Get all available rooms
        List<Room> availableRooms = roomRepository.findByStatus("available");
        
        if (availableRooms.size() < roomCount) {
            return new BookingResponse(false, 
                "Not enough rooms available. Only " + availableRooms.size() + " rooms available.");
        }
        
        // Try same-floor booking first
        List<Room> bookedRooms = trySameFloorBooking(availableRooms, roomCount);
        
        // If same-floor booking not possible, use greedy algorithm
        if (bookedRooms.size() < roomCount) {
            bookedRooms = greedyBooking(availableRooms, roomCount);
        }
        
        // Update rooms status in database
        for (Room room : bookedRooms) {
            room.setStatus("booked");
            roomRepository.save(room);
        }
        
        return new BookingResponse(true, 
            "Successfully booked " + bookedRooms.size() + " rooms", bookedRooms);
    }
    
    /**
     * Try to book rooms on the same floor (highest priority)
     */
    private List<Room> trySameFloorBooking(List<Room> availableRooms, int roomCount) {
        // Group rooms by floor
        Map<Integer, List<Room>> roomsByFloor = availableRooms.stream()
            .collect(Collectors.groupingBy(Room::getFloor));
        
        // Find floor with enough available rooms
        for (Map.Entry<Integer, List<Room>> entry : roomsByFloor.entrySet()) {
            List<Room> floorRooms = entry.getValue();
            if (floorRooms.size() >= roomCount) {
                // Sort by position (closest to lift first)
                floorRooms.sort(Comparator.comparing(Room::getPosition));
                // Return first N rooms
                return floorRooms.subList(0, roomCount);
            }
        }
        
        return new ArrayList<>();  // No same-floor booking possible
    }
    
    /**
     * Greedy algorithm: Select rooms closest to lift to minimize travel time
     * Sort by floor (ascending) and position (ascending), then take first N rooms
     */
    private List<Room> greedyBooking(List<Room> availableRooms, int roomCount) {
        // Sort by floor first, then by position (closest to lift)
        List<Room> sortedRooms = availableRooms.stream()
            .sorted(Comparator
                .comparing(Room::getFloor)
                .thenComparing(Room::getPosition))
            .collect(Collectors.toList());
        
        // Take first N rooms (greedy approach - closest to lift)
        return sortedRooms.subList(0, Math.min(roomCount, sortedRooms.size()));
    }
    
    /**
     * Random booking: Randomly book 30% of rooms
     */
    public void randomBooking() {
        List<Room> allRooms = roomRepository.findAll();
        
        for (Room room : allRooms) {
            // 30% chance to book
            if (Math.random() < 0.3) {
                room.setStatus("booked");
            } else {
                room.setStatus("available");
            }
            roomRepository.save(room);
        }
    }
    
    /**
     * Reset all rooms to available
     */
    public void resetAll() {
        List<Room> allRooms = roomRepository.findAll();
        
        for (Room room : allRooms) {
            room.setStatus("available");
            roomRepository.save(room);
        }
    }
}

