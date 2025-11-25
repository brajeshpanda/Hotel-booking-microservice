package com.propertyservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.Entity.State;



public interface StateRepository extends JpaRepository<State, Long>  {
	State findByName(String name);
}