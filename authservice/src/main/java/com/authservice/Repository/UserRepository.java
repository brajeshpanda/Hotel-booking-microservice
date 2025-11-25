package com.authservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.authservice.Entity.User;


public interface UserRepository extends JpaRepository<User, Long>{


	User findByUsername(String username);
	User findByEmail(String email);
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
    boolean existsByRole(String role);
}

