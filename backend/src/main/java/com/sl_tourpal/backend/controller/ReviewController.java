package com.sl_tourpal.backend.controller;


import com.sl_tourpal.backend.domain.Review;
import com.sl_tourpal.backend.dto.ReviewDTO;
import com.sl_tourpal.backend.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/addReview")
    public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO dto) {
        Review review = reviewService.addReview(dto);
        return ResponseEntity.ok(toDTO(review));
    }

   

    @DeleteMapping("/deleteReview/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/allReviews")
    public ResponseEntity<List<ReviewDTO>> getAll() {
        List<Review> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews.stream().map(this::toDTO).toList());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<ReviewDTO>> getByTour(@PathVariable Long tourId) {
        List<Review> reviews = reviewService.getReviewsByTour(tourId);
        return ResponseEntity.ok(reviews.stream().map(this::toDTO).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getById(@PathVariable Long id) {
        Review review = reviewService.getReviewById(id);
        if (review == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toDTO(review));
    }

    // Helper method to convert Review to ReviewDTO
    private ReviewDTO toDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setTourId(review.getTour() != null ? review.getTour().getId() : null);
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        return dto;
    }
}

