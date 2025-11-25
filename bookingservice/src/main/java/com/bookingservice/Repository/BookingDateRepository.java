package com.bookingservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookingservice.Entity.BookingDate;

public interface BookingDateRepository extends JpaRepository<BookingDate, Long> {

}
