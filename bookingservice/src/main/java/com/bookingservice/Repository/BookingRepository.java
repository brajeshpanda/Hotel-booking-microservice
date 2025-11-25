package com.bookingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookingservice.Entity.Bookings;

public interface BookingRepository extends JpaRepository<Bookings, Long> {

}
