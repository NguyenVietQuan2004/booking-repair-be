package com.hange.booking.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hange.booking.booking.entity.Service;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

	boolean existsBySlug(String slug);

	Optional<Service> findBySlug(String slug);

	List<Service> findByCategoryId(Long categoryId);
}