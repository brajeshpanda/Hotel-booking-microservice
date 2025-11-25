package com.propertyservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.Entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long>  {
	
	Area findByName(String name);

}
