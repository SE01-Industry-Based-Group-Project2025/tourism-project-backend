package com.sl_tourpal.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "tour_date_inventory", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"tour_id", "date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TourDateInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;
    
    @Column(name = "date", nullable = false)
    private LocalDate date;
    
    @Column(name = "capacity", nullable = false)
    private int capacity;
    
    @Column(name = "booked", nullable = false)
    private int booked = 0;
    
    public int getAvailableSpots() {
        return capacity - booked;
    }
    
    public boolean hasCapacity(int requestedGuests) {
        return getAvailableSpots() >= requestedGuests;
    }
    
    public void bookGuests(int guests) {
        if (!hasCapacity(guests)) {
            throw new IllegalStateException("Not enough capacity available");
        }
        this.booked += guests;
    }
}
