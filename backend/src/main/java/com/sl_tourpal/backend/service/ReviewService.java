package com.sl_tourpal.backend.service;

import com.sl_tourpal.backend.domain.Review;
import com.sl_tourpal.backend.domain.Tour;
import com.sl_tourpal.backend.domain.User;
import com.sl_tourpal.backend.dto.ReviewDTO;
import com.sl_tourpal.backend.repository.ReviewRepository;
import com.sl_tourpal.backend.repository.TourRepository;
import com.sl_tourpal.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    public Review addReview(ReviewDTO dto) {
        Tour tour = tourRepository.findById(dto.getTourId()).orElseThrow(() -> new RuntimeException("Tour not found"));
        User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));

        Review review = new Review();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setTour(tour);
        review.setUser(user);

        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getReviewsByTour(Long tourId) {
        Tour tour = tourRepository.findById(tourId).orElseThrow(() -> new RuntimeException("Tour not found"));
        return reviewRepository.findByTour(tour);
    }

    public Review getReviewById(Long id) {
        return reviewRepository.findById(id).orElse(null);
    }
}
