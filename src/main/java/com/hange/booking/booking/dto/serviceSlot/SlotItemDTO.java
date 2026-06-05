package com.hange.booking.booking.dto.serviceSlot;

import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlotItemDTO {

	private LocalTime startTime;

	private LocalTime endTime;

	private Integer maxCapacity;
}