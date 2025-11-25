package com.propertyservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.Entity.City;



public interface CityRepository extends JpaRepository<City, Long>  {
	City findByName(String name);
}
