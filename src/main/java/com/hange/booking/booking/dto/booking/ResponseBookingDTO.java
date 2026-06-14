package com.hange.booking.booking.dto.booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.hange.booking.auth.dto.user.UserDTO;
import com.hange.booking.booking.entity.constant.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBookingDTO {

	private Long id;

	private UserDTO userDTO;

	private Long slotId;

	private BookingStatus status;

	private LocalDate slotDate;

	private LocalTime slotTimeStart;

	private LocalTime slotTimeEnd;

	private String serviceName;

	private BigDecimal servicePrice;

	private Integer serviceDuration;

	private String locationName;

	private String locationAddress;

	private String note;

	private LocalDateTime createdAt;
}