package com.hange.booking.booking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hange.booking.booking.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

	boolean existsBySlug(String slug);

	Optional<Category> findBySlug(String slug);
}