package com.hange.booking.booking.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hange.booking.booking.dto.booking.BookingFilterDTO;
import com.hange.booking.booking.dto.booking.RequestCreateBookingDTO;
import com.hange.booking.booking.dto.booking.ResponseBookingDTO;

public interface BookingService {

	ResponseBookingDTO create(RequestCreateBookingDTO request);

	ResponseBookingDTO confirm(Long bookingId);

	ResponseBookingDTO reject(Long bookingId);

	ResponseBookingDTO cancel(Long bookingId);

	List<ResponseBookingDTO> getMyBookings();

	ResponseBookingDTO getById(Long bookingId);

	Page<ResponseBookingDTO> getAll(BookingFilterDTO filter, Pageable pageable);
}