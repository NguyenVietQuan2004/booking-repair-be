package com.hange.booking.booking.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hange.booking.booking.dto.booking.BookingFilterDTO;
import com.hange.booking.booking.entity.Booking;

import jakarta.persistence.criteria.Predicate;

public class BookingSpec {

	public static Specification<Booking> filter(BookingFilterDTO f) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// ===== KEYWORD SEARCH =====

			if (f.getKeyword() != null && !f.getKeyword().isBlank()) {

				String like = "%" + f.getKeyword().toLowerCase() + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.get("serviceName")), like),
						cb.like(cb.lower(root.get("locationName")), like),
						cb.like(cb.lower(root.get("locationAddress")), like),
						cb.like(cb.lower(root.get("note")), like)));
			}

			// ===== EXACT FILTER =====

			if (f.getUserId() != null) {
				predicates.add(cb.equal(root.get("user").get("id"), f.getUserId()));
			}

			if (f.getServiceId() != null) {
				predicates.add(cb.equal(root.get("service").get("id"), f.getServiceId()));
			}

			if (f.getLocationId() != null) {
				predicates.add(cb.equal(root.get("location").get("id"), f.getLocationId()));
			}

			if (f.getSlotId() != null) {
				predicates.add(cb.equal(root.get("slot").get("id"), f.getSlotId()));
			}

			if (f.getStatus() != null) {
				predicates.add(cb.equal(root.get("status"), f.getStatus()));
			}

			if (f.getSlotDate() != null) {
				predicates.add(cb.equal(root.get("slotDate"), f.getSlotDate()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}