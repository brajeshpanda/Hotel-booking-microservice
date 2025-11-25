package com.reviewservice.Service;

import com.reviewservice.Entity.Review;
import com.reviewservice.Payload.ReviewDto;
import com.reviewservice.Repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // ----------------------------------------------------
    // ADD NEW REVIEW
    // ----------------------------------------------------
    public Review addReview(ReviewDto dto) {

        Review review = new Review();
        review.setPropertyId(dto.getPropertyId());
        review.setUserId(dto.getUserId());
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return reviewRepository.save(review);
    }

    // ----------------------------------------------------
    // GET ALL REVIEWS FOR A PROPERTY
    // ----------------------------------------------------
    public List<Review> getReviewsByProperty(Long propertyId) {
        return reviewRepository.findByPropertyId(propertyId);
    }

    // ----------------------------------------------------
    // GET ALL REVIEWS BY A USER
    // ----------------------------------------------------
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId);
    }

    // ----------------------------------------------------
    // ⭐ NEW: GET AVERAGE RATING FOR A PROPERTY
    // ----------------------------------------------------
    public Double getAverageRating(Long propertyId) {
        return reviewRepository.findAverageRating(propertyId);
    }

    // ----------------------------------------------------
    // UPDATE REVIEW
    // ----------------------------------------------------
    public Review updateReview(Long id, ReviewDto dto) {
        Review review = reviewRepository.findById(id).orElse(null);

        if (review == null) {
            return null;
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());

        return reviewRepository.save(review);
    }

    // ----------------------------------------------------
    // DELETE REVIEW
    // ----------------------------------------------------
    public boolean deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
