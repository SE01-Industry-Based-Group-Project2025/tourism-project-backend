package com.sl_tourpal.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "itinerary_days")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryDay {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer dayNumber;
    private String title;
    
    @Column(length = 2000)
    private String description;

    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "itinerary_destinations", joinColumns = @JoinColumn(name = "day_id"))
    @Column(name = "destination")
    private Set<String> destinations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    // getters + setters
}
