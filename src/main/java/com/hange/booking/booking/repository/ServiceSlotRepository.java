package com.hange.booking.booking.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hange.booking.booking.entity.Location;
import com.hange.booking.booking.entity.ServiceSlot;

import jakarta.persistence.LockModeType;

@Repository
public interface ServiceSlotRepository extends JpaRepository<ServiceSlot, Long>, JpaSpecificationExecutor<ServiceSlot> {

	Optional<ServiceSlot> findByServiceIdAndLocationIdAndSlotDateAndStartTime(Long serviceId, Long locationId,
			LocalDate slotDate, LocalTime startTime);

	List<ServiceSlot> findByServiceId(Long serviceId);

	List<ServiceSlot> findByLocationId(Long locationId);

	List<ServiceSlot> findBySlotDate(LocalDate slotDate);

	boolean existsBySlotDate(LocalDate slotDate);

	List<ServiceSlot> findByServiceIdAndSlotDate(Long serviceId, LocalDate slotDate);

	List<ServiceSlot> findByLocationIdAndSlotDate(Long locationId, LocalDate slotDate);

	List<ServiceSlot> findByServiceIdAndLocationIdAndSlotDate(Long serviceId, Long locationId, LocalDate slotDate);

	boolean existsByServiceIdAndLocationIdAndSlotDateAndStartTime(Long serviceId, Long locationId, LocalDate slotDate,
			LocalTime startTime);

	Optional<ServiceSlot> findById(Long id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT s
			FROM ServiceSlot s
			WHERE s.id = :id
			""")
	Optional<ServiceSlot> findByIdForUpdate(@Param("id") Long id);

	@Query("""
			SELECT s
			FROM ServiceSlot s
			WHERE s.service.id = :serviceId
			AND s.location.id = :locationId
			AND s.slotDate = :date
			""")
	List<ServiceSlot> findAvailableSlots(@Param("serviceId") Long serviceId, @Param("locationId") Long locationId,
			@Param("date") LocalDate date);

	@Query("""
			SELECT COUNT(s) > 0
			FROM ServiceSlot s
			WHERE s.service.id = :serviceId
			AND s.location.id = :locationId
			AND s.slotDate = :slotDate
			AND s.startTime = :startTime
			AND s.id <> :excludeId
			""")
	boolean existsDuplicateForUpdate(@Param("serviceId") Long serviceId, @Param("locationId") Long locationId,
			@Param("slotDate") LocalDate slotDate, @Param("startTime") LocalTime startTime,
			@Param("excludeId") Long excludeId);

	@Query("""
			SELECT COUNT(s) >  0
			FROM ServiceSlot s
			WHERE s.service.id = :serviceId
			AND s.location.id = :locationId
			AND s.slotDate = :slotDate
			AND (:excludeId IS NULL OR s.id <> :excludeId)
			AND (
			    :startTime < s.endTime
			    AND :endTime > s.startTime
			)
			""")
	boolean existsOverlap(@Param("serviceId") Long serviceId, @Param("locationId") Long locationId,
			@Param("slotDate") LocalDate slotDate, @Param("startTime") LocalTime startTime,
			@Param("endTime") LocalTime endTime, @Param("excludeId") Long excludeId);

	@Override
	@EntityGraph(attributePaths = { "service", "location" })
	Page<ServiceSlot> findAll(Specification<ServiceSlot> spec, Pageable pageable);

	@Query("""
			    select distinct ss.location
			    from ServiceSlot ss
			    where ss.service.id = :serviceId
			""")
	List<Location> findLocationsByServiceId(@Param("serviceId") Long serviceId);

}