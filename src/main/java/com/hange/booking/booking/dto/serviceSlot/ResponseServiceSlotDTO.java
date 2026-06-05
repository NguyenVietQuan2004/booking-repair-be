package com.hange.booking.booking.dto.serviceSlot;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseServiceSlotDTO {

	private Long id;

	private Long serviceId;
	private Long locationId;

	private LocalDate slotDate;
	private LocalTime startTime;
	private LocalTime endTime;

	private Integer maxCapacity;
	private Integer bookedCount;
}