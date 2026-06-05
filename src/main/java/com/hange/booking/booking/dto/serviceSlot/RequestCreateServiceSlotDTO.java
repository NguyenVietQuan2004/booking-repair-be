package com.hange.booking.booking.dto.serviceSlot;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateServiceSlotDTO {

	@NotNull(message = "SERVICE_ID_REQUIRED")
	private Long serviceId;

	@NotNull(message = "LOCATION_ID_REQUIRED")
	private Long locationId;

	@NotNull(message = "SLOT_DATE_REQUIRED")
	@FutureOrPresent(message = "SLOT_DATE_MUST_BE_TODAY_OR_FUTURE")
	private LocalDate slotDate;

	@NotNull(message = "START_TIME_REQUIRED")
	private LocalTime startTime;

	@NotNull(message = "END_TIME_REQUIRED")
	private LocalTime endTime;

	@NotNull(message = "MAX_CAPACITY_REQUIRED")
	@Min(value = 1, message = "MAX_CAPACITY_MUST_BE_AT_LEAST_1")
	private Integer maxCapacity;
}