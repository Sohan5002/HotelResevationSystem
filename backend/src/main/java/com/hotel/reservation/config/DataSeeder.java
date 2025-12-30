package com.hotel.reservation.config;

import com.hotel.reservation.model.Room;
import com.hotel.reservation.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (roomRepository.count() == 0) {
            seedRooms();
            System.out.println("✅ Seeded 97 rooms into database");
        } else {
            System.out.println("✅ Database already contains " + roomRepository.count() + " rooms");
        }
    }
    
    private void seedRooms() {
        // Floors 1-9: 10 rooms each
        for (int floor = 1; floor <= 9; floor++) {
            for (int position = 1; position <= 10; position++) {
                Room room = new Room();
                room.setRoomNo(floor * 100 + position);
                room.setFloor(floor);
                room.setPosition(position);
                room.setStatus("available");
                roomRepository.save(room);
            }
        }
        
        // Floor 10: 7 rooms
        for (int position = 1; position <= 7; position++) {
            Room room = new Room();
            room.setRoomNo(1000 + position);
            room.setFloor(10);
            room.setPosition(position);
            room.setStatus("available");
            roomRepository.save(room);
        }
    }
}

