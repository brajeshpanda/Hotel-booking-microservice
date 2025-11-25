package com.reviewservice.Repository;

import com.reviewservice.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPropertyId(Long propertyId);

    List<Review> findByUserId(Long userId);

    // ⭐ NEW: Find average rating for a property
    @Query("SELECT COALESCE(AVG(r.rating), 0) FROM Review r WHERE r.propertyId = :propertyId")
    Double findAverageRating(@Param("propertyId") Long propertyId);
}
