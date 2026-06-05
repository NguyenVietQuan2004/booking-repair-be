package com.hange.booking.booking.repository;

import java.time.LocalDateTime;
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

import com.hange.booking.booking.entity.Booking;
import com.hange.booking.booking.entity.constant.BookingStatus;

import jakarta.persistence.LockModeType;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {

	boolean existsByUserIdAndSlotId(Long userId, Long slotId);

	List<Booking> findByUserId(Long userId);

	List<Booking> findByStatus(BookingStatus status);

	List<Booking> findByUserEmail(String email);

	List<Booking> findByLocationId(Long locationId);

	boolean existsByUserIdAndSlotIdAndStatusIn(Long userId, Long slotId, List<BookingStatus> statuses);

	boolean existsByUserIdAndCreatedAtAfter(Long userId, LocalDateTime time);

	List<Booking> findByServiceId(Long serviceId);

	Optional<Booking> findByIdAndUserId(Long bookingId, Long userId);

	boolean existsByUserIdAndServiceIdAndCreatedAtAfter(Long userId, Long serviceId, LocalDateTime createdAt);

	boolean existsByUserIdAndServiceIdAndSlotIdAndStatusIn(Long userId, Long serviceId, Long slotId,
			List<BookingStatus> statuses);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(" SELECT b FROM Booking b WHERE b.id = :bookingId")
	Optional<Booking> findByIdForUpdate(@Param("bookingId") Long bookingId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(" SELECT b FROM Booking b WHERE b.id = :bookingId AND b.user.id = :userId ")
	Optional<Booking> findByIdAndUserIdForUpdate(@Param("bookingId") Long bookingId, @Param("userId") Long userId);

//	@Query("""
//			    select b from Booking b
//			    join fetch b.user
//			    join fetch b.slot
//			    join fetch b.service
//			    join fetch b.location
//			""")
//	List<Booking> findAllWithRelations();

	@Override
	@EntityGraph(attributePaths = { "user", "slot", "service", "location" })
	Page<Booking> findAll(Specification<Booking> spec, Pageable pageable);

}