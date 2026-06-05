package com.hange.booking.booking.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.hange.booking.booking.dto.location.ResponseLocationDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestBulkCreateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestCreateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestUpdateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.ResponseServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.ServiceSlotFilterDTO;

public interface ServiceSlotService {

	Page<ResponseServiceSlotDTO> getAll(ServiceSlotFilterDTO filter, Pageable pageable);

	List<ResponseServiceSlotDTO> bulkCreate(RequestBulkCreateServiceSlotDTO request);

	ResponseServiceSlotDTO getById(Long id);

	List<ResponseServiceSlotDTO> getByService(Long serviceId);

	ResponseServiceSlotDTO create(RequestCreateServiceSlotDTO request);

	ResponseServiceSlotDTO update(Long id, RequestUpdateServiceSlotDTO request);

	void delete(Long id);

	List<ResponseLocationDTO> getLocationsByService(Long serviceId);

	void increaseBookedCount(Long id);

	void cloneSlots(LocalDate sourceDate, LocalDate targetDate);

	void maintainSevenDaysSlots();

	void decreaseBookedCount(Long id);
}