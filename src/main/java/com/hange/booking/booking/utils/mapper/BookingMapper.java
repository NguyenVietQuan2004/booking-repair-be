package com.hange.booking.booking.utils.mapper;

import org.springframework.stereotype.Component;

import com.hange.booking.auth.utils.mapper.UserMapper;
import com.hange.booking.booking.dto.booking.ResponseBookingDTO;
import com.hange.booking.booking.entity.Booking;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingMapper {
	private final UserMapper userMapper;

	public ResponseBookingDTO toDTO(Booking booking) {

		return ResponseBookingDTO.builder().id(booking.getId()).userDTO(userMapper.toUserDTO(booking.getUser()))
				.slotId(booking.getSlot().getId()).status(booking.getStatus()).slotDate(booking.getSlotDate())
				.slotTimeStart(booking.getSlotTimeStart()).slotTimeEnd(booking.getSlotTimeEnd())
				.serviceName(booking.getServiceName()).servicePrice(booking.getServicePrice())
				.serviceDuration(booking.getServiceDuration()).locationName(booking.getLocationName())
				.locationAddress(booking.getLocationAddress()).note(booking.getNote()).createdAt(booking.getCreatedAt())
				.build();
	}
}