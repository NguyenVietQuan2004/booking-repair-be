package com.hange.booking.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hange.booking.booking.entity.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

	boolean existsBySlug(String slug);

	Optional<Location> findBySlug(String slug);
}