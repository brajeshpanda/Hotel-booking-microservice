package com.propertyservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.Entity.Rooms;



public interface RoomRepository extends JpaRepository<Rooms, Long> {
	
	

}
