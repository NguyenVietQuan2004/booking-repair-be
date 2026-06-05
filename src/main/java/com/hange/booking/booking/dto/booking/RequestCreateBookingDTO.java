package com.hange.booking.booking.dto.booking;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateBookingDTO {

	@NotNull(message = "Slot is must be provide")
	private Long slotId;

	private String note;
}