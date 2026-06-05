
package com.hange.booking.booking.service.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import com.hange.booking.booking.Specification.ServiceSlotSpecification;
import com.hange.booking.booking.dto.location.ResponseLocationDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestBulkCreateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestCreateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.RequestUpdateServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.ResponseServiceSlotDTO;
import com.hange.booking.booking.dto.serviceSlot.ServiceSlotFilterDTO;
import com.hange.booking.booking.dto.serviceSlot.SlotItemDTO;
import com.hange.booking.booking.entity.Location;
import com.hange.booking.booking.entity.Service;
import com.hange.booking.booking.entity.ServiceSlot;
import com.hange.booking.booking.repository.LocationRepository;
import com.hange.booking.booking.repository.ServiceRepository;
import com.hange.booking.booking.repository.ServiceSlotRepository;
import com.hange.booking.booking.service.ServiceSlotService;
import com.hange.booking.booking.utils.mapper.LocationMapper;
import com.hange.booking.booking.utils.mapper.ServiceSlotMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorBookingCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceSlotServiceImpl implements ServiceSlotService {

	private final ServiceSlotRepository slotRepository;
	private final ServiceRepository serviceRepository;
	private final LocationRepository locationRepository;
	private final ServiceSlotMapper mapper;
	private final LocationMapper locationMapper;

	@Override
	public Page<ResponseServiceSlotDTO> getAll(ServiceSlotFilterDTO filter, Pageable pageable) {

		Specification<ServiceSlot> spec = ServiceSlotSpecification.filter(filter);

		return slotRepository.findAll(spec, pageable).map(mapper::toDTO);
	}

	@Override
	public List<ResponseLocationDTO> getLocationsByService(Long serviceId) {

		return slotRepository.findLocationsByServiceId(serviceId).stream().map(locationMapper::toDTO).toList();
	}

	@Override
	public ResponseServiceSlotDTO getById(Long id) {
		return mapper.toDTO(findSlotById(id));
	}

	@Override
	public List<ResponseServiceSlotDTO> getByService(Long serviceId) {
		return slotRepository.findByServiceId(serviceId).stream().map(mapper::toDTO).toList();
	}

	@Override
	@Transactional
	public ResponseServiceSlotDTO create(RequestCreateServiceSlotDTO request) {

		Service service = serviceRepository.findById(request.getServiceId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_NOT_FOUND));

		Location location = locationRepository.findById(request.getLocationId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.LOCATION_NOT_FOUND));

		validateDuplicateSlot(request.getServiceId(), request.getLocationId(), request.getSlotDate(),
				request.getStartTime(), null);

		validateOverlap(request.getServiceId(), request.getLocationId(), request.getSlotDate(), request.getStartTime(),
				request.getEndTime(), null);

		validateTimeRange(request.getStartTime(), request.getEndTime());

		ServiceSlot slot = ServiceSlot.builder().service(service).location(location).slotDate(request.getSlotDate())
				.startTime(request.getStartTime()).endTime(request.getEndTime()).maxCapacity(request.getMaxCapacity())
				.bookedCount(0).build();
		ServiceSlot saved = slotRepository.save(slot);

		maintainSevenDaysSlots();

		return mapper.toDTO(saved);
	}

	@Override
	@Transactional
	public List<ResponseServiceSlotDTO> bulkCreate(RequestBulkCreateServiceSlotDTO request) {

		Service service = serviceRepository.findById(request.getServiceId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_NOT_FOUND));

		List<ServiceSlot> slotsToSave = new ArrayList<>();

		// validate overlap trong chính request
		for (int i = 0; i < request.getSlots().size(); i++) {

			SlotItemDTO current = request.getSlots().get(i);

			validateTimeRange(current.getStartTime(), current.getEndTime());

			for (int j = i + 1; j < request.getSlots().size(); j++) {

				SlotItemDTO other = request.getSlots().get(j);

				boolean overlap = current.getStartTime().isBefore(other.getEndTime())
						&& other.getStartTime().isBefore(current.getEndTime());

				if (overlap) {
					throw new AppRuntimeException(ErrorBookingCode.TIME_OVERLAP);
				}
			}
		}

		for (Long locationId : request.getLocationIds()) {

			Location location = locationRepository.findById(locationId)
					.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.LOCATION_NOT_FOUND));

			for (SlotItemDTO item : request.getSlots()) {

				validateDuplicateSlot(request.getServiceId(), locationId, request.getSlotDate(), item.getStartTime(),
						null);

				validateOverlap(request.getServiceId(), locationId, request.getSlotDate(), item.getStartTime(),
						item.getEndTime(), null);

				ServiceSlot slot = ServiceSlot.builder().service(service).location(location)
						.slotDate(request.getSlotDate()).startTime(item.getStartTime()).endTime(item.getEndTime())
						.maxCapacity(item.getMaxCapacity()).bookedCount(0).build();

				slotsToSave.add(slot);
			}
		}

		List<ServiceSlot> savedSlots = slotRepository.saveAll(slotsToSave);

		maintainSevenDaysSlots();

		return savedSlots.stream().map(mapper::toDTO).toList();
	}

	@Override
	@Transactional
	public ResponseServiceSlotDTO update(Long id, RequestUpdateServiceSlotDTO request) {
// co can validation date truyen vao > hien tai ko
		ServiceSlot slot = findSlotById(id);

		LocalDate newDate = request.getSlotDate() != null ? request.getSlotDate() : slot.getSlotDate();

		LocalTime newStart = request.getStartTime() != null ? request.getStartTime() : slot.getStartTime();

		LocalTime newEnd = request.getEndTime() != null ? request.getEndTime() : slot.getEndTime();

		Integer newCapacity = request.getMaxCapacity() != null ? request.getMaxCapacity() : slot.getMaxCapacity();

		validateDuplicateSlot(slot.getService().getId(), slot.getLocation().getId(), newDate, newStart, slot.getId());

		validateOverlap(slot.getService().getId(), slot.getLocation().getId(), newDate, newStart, newEnd, slot.getId());

		validateTimeRange(newStart, newEnd);

		if (newCapacity < slot.getBookedCount()) {
			throw new AppRuntimeException(ErrorBookingCode.INVALID_MAX_CAPACITY);
		}

		if (request.getSlotDate() != null)
			slot.setSlotDate(request.getSlotDate());

		if (request.getStartTime() != null)
			slot.setStartTime(request.getStartTime());

		if (request.getEndTime() != null)
			slot.setEndTime(request.getEndTime());

		if (request.getMaxCapacity() != null)
			slot.setMaxCapacity(request.getMaxCapacity());

		return mapper.toDTO(slotRepository.save(slot));
	}

	@Override
	@Transactional
	public void delete(Long id) {

		ServiceSlot slot = findSlotById(id);

		slotRepository.delete(slot);
	}

	@Transactional
	public void increaseBookedCount(Long slotId) {

		ServiceSlot slot = findSlotById(slotId);

		if (slot.getBookedCount() >= slot.getMaxCapacity()) {
			throw new AppRuntimeException(ErrorBookingCode.SLOT_FULL);
		}

		slot.setBookedCount(slot.getBookedCount() + 1);

		slotRepository.save(slot);
	}

	@Transactional
	public void decreaseBookedCount(Long slotId) {

		ServiceSlot slot = findSlotById(slotId);

		if (slot.getBookedCount() <= 0) {
			throw new AppRuntimeException(ErrorBookingCode.INVALID_BOOKED_COUNT);
		}

		slot.setBookedCount(slot.getBookedCount() - 1);

		slotRepository.save(slot);
	}

	private ServiceSlot findSlotById(Long id) {
		return slotRepository.findById(id)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_SLOT_NOT_FOUND));
	}

	private void validateTimeRange(LocalTime start, LocalTime end) {

		if (!start.isBefore(end)) {
			throw new AppRuntimeException(ErrorBookingCode.INVALID_TIME_RANGE);
		}
	}

	private void validateDuplicateSlot(Long serviceId, Long locationId, LocalDate slotDate, LocalTime startTime,
			Long excludeId) {

		boolean exists = excludeId == null
				? slotRepository.existsByServiceIdAndLocationIdAndSlotDateAndStartTime(serviceId, locationId, slotDate,
						startTime)
				: slotRepository.existsDuplicateForUpdate(serviceId, locationId, slotDate, startTime, excludeId);

		if (exists) {
			throw new AppRuntimeException(ErrorBookingCode.DUPLICATE_SLOT);
		}
	}

	private void validateOverlap(Long serviceId, Long locationId, LocalDate slotDate, LocalTime startTime,
			LocalTime endTime, Long excludeId) {

		boolean overlap = slotRepository.existsOverlap(serviceId, locationId, slotDate, startTime, endTime, excludeId);

		if (overlap) {
			throw new AppRuntimeException(ErrorBookingCode.TIME_OVERLAP);
		}
	}

	@Transactional
	@Override
	public void cloneSlots(LocalDate sourceDate, LocalDate targetDate) {
		List<ServiceSlot> sourceSlots = slotRepository.findBySlotDate(sourceDate);
		for (ServiceSlot source : sourceSlots) {
			boolean exists = slotRepository.existsByServiceIdAndLocationIdAndSlotDateAndStartTime(
					source.getService().getId(), source.getLocation().getId(), targetDate, source.getStartTime());
			if (exists) {
				continue;
			}
			ServiceSlot cloned = ServiceSlot.builder().service(source.getService()).location(source.getLocation())
					.slotDate(targetDate).startTime(source.getStartTime()).endTime(source.getEndTime())
					.maxCapacity(source.getMaxCapacity()).bookedCount(0).build();

			slotRepository.save(cloned);
		}
	}

	@Transactional
	@Override
	public void maintainSevenDaysSlots() {
		LocalDate today = LocalDate.now();

		for (int i = 1; i <= 7; i++) {

			LocalDate targetDate = today.plusDays(i);

			LocalDate sourceDate = targetDate.minusDays(1);

			cloneSlots(sourceDate, targetDate);
		}
	}

}
