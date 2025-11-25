package com.propertyservice.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.Entity.PropertyPhotos;

public interface PropertyPhotosRepository extends JpaRepository<PropertyPhotos, Long> {

}
