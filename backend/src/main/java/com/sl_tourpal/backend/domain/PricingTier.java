package com.sl_tourpal.backend.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "pricing_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingTier {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String groupType;   // e.g. "1-2", "3-5", "6+"
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    // getters + setters
}
