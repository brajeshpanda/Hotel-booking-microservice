package com.reviewservice.Controller;

import com.reviewservice.Entity.Review;
import com.reviewservice.Payload.APIResponse;
import com.reviewservice.Payload.ReviewDto;
import com.reviewservice.Service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    @PostMapping("/add")
    public APIResponse<Review> addReview(@RequestBody ReviewDto dto) {

        Review saved = reviewService.addReview(dto);

        APIResponse<Review> response = new APIResponse<>();
        response.setMessage("Review added successfully");
        response.setStatus(201);
        response.setData(saved);

        return response;
    }


    @GetMapping("/property/{propertyId}")
    public APIResponse<List<Review>> getReviewsByProperty(@PathVariable Long propertyId) {

        List<Review> list = reviewService.getReviewsByProperty(propertyId);

        APIResponse<List<Review>> response = new APIResponse<>();
        response.setMessage("Reviews fetched");
        response.setStatus(200);
        response.setData(list);

        return response;
    }


    @GetMapping("/user/{userId}")
    public APIResponse<List<Review>> getReviewsByUser(@PathVariable Long userId) {

        List<Review> list = reviewService.getReviewsByUser(userId);

        APIResponse<List<Review>> response = new APIResponse<>();
        response.setMessage("User reviews fetched");
        response.setStatus(200);
        response.setData(list);

        return response;
    }


    @GetMapping("/average/{propertyId}")
    public APIResponse<Double> getAverageRating(@PathVariable Long propertyId) {

        Double avgRating = reviewService.getAverageRating(propertyId);

        APIResponse<Double> response = new APIResponse<>();
        response.setMessage("Average rating fetched");
        response.setStatus(200);
        response.setData(avgRating);

        return response;
    }


    @PutMapping("/update/{id}")
    public APIResponse<Review> updateReview(@PathVariable Long id, @RequestBody ReviewDto dto) {

        Review updated = reviewService.updateReview(id, dto);

        APIResponse<Review> response = new APIResponse<>();

        if (updated == null) {
            response.setMessage("Review not found");
            response.setStatus(404);
            return response;
        }

        response.setMessage("Review updated successfully");
        response.setStatus(200);
        response.setData(updated);

        return response;
    }


    @DeleteMapping("/delete/{id}")
    public APIResponse<Boolean> deleteReview(@PathVariable Long id) {

        boolean deleted = reviewService.deleteReview(id);

        APIResponse<Boolean> response = new APIResponse<>();

        if (!deleted) {
            response.setMessage("Review not found");
            response.setStatus(404);
            response.setData(false);
            return response;
        }

        response.setMessage("Review deleted successfully");
        response.setStatus(200);
        response.setData(true);

        return response;
    }
}
