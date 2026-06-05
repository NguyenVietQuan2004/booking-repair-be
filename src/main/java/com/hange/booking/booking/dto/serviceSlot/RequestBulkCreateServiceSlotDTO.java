package com.hange.booking.booking.dto.serviceSlot;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestBulkCreateServiceSlotDTO {

	private Long serviceId;

	private List<Long> locationIds;

	private LocalDate slotDate;

	private List<SlotItemDTO> slots;
}