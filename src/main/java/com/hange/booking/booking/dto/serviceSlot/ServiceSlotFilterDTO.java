package com.hange.booking.booking.dto.serviceSlot;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceSlotFilterDTO {

	private String keyword;

	private Long serviceId;

	private Long locationId;

	private LocalDate slotDate;

	private Integer maxCapacity;

	private Integer bookedCount;
}