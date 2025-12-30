package com.hotel.reservation.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_no", unique = true, nullable = false)
    private Integer roomNo;
    
    @Column(nullable = false)
    private Integer floor;
    
    @Column(nullable = false)
    private String status;  // "available", "booked"
    
    @Column(nullable = false)
    private Integer position;  // 1-10 (room position on floor, 1 = closest to lift)
}

