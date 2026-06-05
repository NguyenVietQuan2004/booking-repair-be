package com.hange.booking.booking.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.booking.dto.serviceSlot.ResponseServiceSlotDTO;
import com.hange.booking.booking.entity.ServiceSlot;

@Component
public class ServiceSlotMapper {

	public ResponseServiceSlotDTO toDTO(ServiceSlot slot) {

		ResponseServiceSlotDTO dto = new ResponseServiceSlotDTO();

		dto.setId(slot.getId());
		dto.setServiceId(slot.getService().getId());
		dto.setLocationId(slot.getLocation().getId());

		dto.setSlotDate(slot.getSlotDate());
		dto.setStartTime(slot.getStartTime());
		dto.setEndTime(slot.getEndTime());

		dto.setMaxCapacity(slot.getMaxCapacity());
		dto.setBookedCount(slot.getBookedCount());

		return dto;
	}
}