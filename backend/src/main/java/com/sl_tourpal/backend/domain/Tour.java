package com.sl_tourpal.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tours")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tour {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic Info
    private String name;
    private String category;
    private Integer durationValue;
    private String durationUnit;
    @Column(length = 500)
    private String shortDescription;

    @ElementCollection
    @CollectionTable(name = "tour_highlights", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "highlight")
    private List<String> highlights;

    private String difficulty;
    private String region;

    @ElementCollection
    @CollectionTable(name = "tour_activities", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "activity")
    private Set<String> activities;

    // Relationships
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItineraryDay> itineraryDays;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accommodation> accommodations;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PricingTier> pricingTiers;

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AvailabilityRange> availabilityRanges;    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TourImage> images;

    // getters + setters omitted for brevity
}
