package com.hange.booking.booking.Specification;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.hange.booking.booking.dto.serviceSlot.ServiceSlotFilterDTO;
import com.hange.booking.booking.entity.ServiceSlot;

import jakarta.persistence.criteria.Predicate;

public class ServiceSlotSpecification {

	public static Specification<ServiceSlot> filter(ServiceSlotFilterDTO f) {

		return (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<>();

			// ===== KEYWORD SEARCH =====

			if (f.getKeyword() != null && !f.getKeyword().isBlank()) {

				String like = "%" + f.getKeyword().toLowerCase() + "%";

				predicates.add(cb.or(cb.like(cb.lower(root.get("service").get("name")), like),
						cb.like(cb.lower(root.get("location").get("name")), like),
						cb.like(cb.lower(root.get("location").get("address")), like)));
			}

			// ===== EXACT FILTER =====

			if (f.getServiceId() != null) {
				predicates.add(cb.equal(root.get("service").get("id"), f.getServiceId()));
			}

			if (f.getLocationId() != null) {
				predicates.add(cb.equal(root.get("location").get("id"), f.getLocationId()));
			}

			if (f.getSlotDate() != null) {
				predicates.add(cb.equal(root.get("slotDate"), f.getSlotDate()));
			}

			if (f.getMaxCapacity() != null) {
				predicates.add(cb.equal(root.get("maxCapacity"), f.getMaxCapacity()));
			}

			if (f.getBookedCount() != null) {
				predicates.add(cb.equal(root.get("bookedCount"), f.getBookedCount()));
			}

			return cb.and(predicates.toArray(new Predicate[0]));
		};
	}
}