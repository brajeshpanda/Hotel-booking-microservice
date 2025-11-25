package com.propertyservice.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.propertyservice.Entity.RoomAvailability;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

    public List<RoomAvailability> findByRoomId(long id);

    @Query("select ra from RoomAvailability ra where ra.id=:id and ra.availableDate=:date")
    public RoomAvailability getRooms(@Param("id") long id, @Param("date") LocalDate date);


    @Modifying
    @Query("UPDATE RoomAvailability ra SET ra.availableCount = ra.availableCount - 1 " +
            "WHERE ra.id = :id AND ra.availableCount > 0")
    int decrementAvailability(@Param("id") Long id);
}
