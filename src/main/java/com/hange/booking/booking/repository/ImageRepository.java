package com.hange.booking.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hange.booking.booking.entity.Image;
import com.hange.booking.booking.entity.constant.ImageType;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

	List<Image> findByEntityType(ImageType entityType);

	List<Image> findByCategoryId(Long categoryId);

	List<Image> findByServiceId(Long serviceId);
}