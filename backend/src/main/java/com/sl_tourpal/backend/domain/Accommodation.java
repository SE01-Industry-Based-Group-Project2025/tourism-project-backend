package com.sl_tourpal.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "accommodations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Accommodation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    
    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "accommodation_images", joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "image_url")
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    @JsonBackReference
    private Tour tour;

    // getters + setters
}
