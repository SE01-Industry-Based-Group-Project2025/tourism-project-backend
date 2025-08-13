package com.sl_tourpal.backend.repository;

import com.sl_tourpal.backend.domain.Review;
import com.sl_tourpal.backend.domain.Tour;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTour(Tour tour);
}
