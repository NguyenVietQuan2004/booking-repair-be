package com.hange.booking.booking.dto.booking;

import java.time.LocalDate;

import com.hange.booking.booking.entity.constant.BookingStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingFilterDTO {

	private String keyword;

	private Long userId;

	private Long serviceId;

	private Long locationId;

	private Long slotId;

	private BookingStatus status;

	private LocalDate slotDate;
}