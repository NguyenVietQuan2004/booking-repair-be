package com.hange.booking.booking.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.repository.UserRepository;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.utils.SecurityUtil;
import com.hange.booking.booking.Specification.BookingSpec;
import com.hange.booking.booking.dto.booking.BookingFilterDTO;
import com.hange.booking.booking.dto.booking.RequestCreateBookingDTO;
import com.hange.booking.booking.dto.booking.ResponseBookingDTO;
import com.hange.booking.booking.dto.notification.RequestCreateNotificationDTO;
import com.hange.booking.booking.entity.Booking;
import com.hange.booking.booking.entity.ServiceSlot;
import com.hange.booking.booking.entity.constant.BookingStatus;
import com.hange.booking.booking.entity.constant.NotificationType;
import com.hange.booking.booking.repository.BookingRepository;
import com.hange.booking.booking.repository.ServiceSlotRepository;
import com.hange.booking.booking.service.BookingService;
import com.hange.booking.booking.service.NotificationService;
import com.hange.booking.booking.utils.mapper.BookingMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorBookingCode;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
	private final NotificationService notificationService;
	private final BookingRepository bookingRepository;
	private final ServiceSlotRepository slotRepository;
	private final UserRepository userRepository;
	private final UserService userService;
	private final BookingMapper mapper;

	@Override
	@Transactional
	public ResponseBookingDTO create(RequestCreateBookingDTO request) {

		String email = SecurityUtil.getCurrentUserEmail();

		User user = userService.getUserByEmail(email);

		if (user.getNextBookingAllowedAt() != null && user.getNextBookingAllowedAt().isAfter(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorBookingCode.BOOKING_RATE_LIMIT);
		}

		ServiceSlot slot = slotRepository.findByIdForUpdate(request.getSlotId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_SLOT_NOT_FOUND));

		validateBookingTime(slot);

		boolean duplicate = bookingRepository.existsByUserIdAndSlotIdAndStatusIn(user.getId(), slot.getId(),
				List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));

		if (duplicate) {
			throw new AppRuntimeException(ErrorBookingCode.DUPLICATE_BOOKING);
		}

		if (slot.getBookedCount() >= slot.getMaxCapacity()) {
			throw new AppRuntimeException(ErrorBookingCode.SLOT_NOT_AVAILABLE);
		}

		slot.setBookedCount(slot.getBookedCount() + 1);

		user.setNextBookingAllowedAt(LocalDateTime.now().plusMinutes(5));

		slotRepository.save(slot);
		userRepository.save(user);

		Booking booking = Booking.builder().user(user).service(slot.getService()).slot(slot)
				.location(slot.getLocation()).status(BookingStatus.PENDING).slotDate(slot.getSlotDate())
				.slotTimeStart(slot.getStartTime()).slotTimeEnd(slot.getEndTime())
				.serviceName(slot.getService().getName()).servicePrice(slot.getService().getPrice())
				.serviceDuration(slot.getService().getDurationMinutes()).locationName(slot.getLocation().getName())
				.locationAddress(slot.getLocation().getAddress()).note(request.getNote()).build();
		Booking saved = bookingRepository.save(booking);
		notificationService.create(RequestCreateNotificationDTO.builder().userId(1L) // hoặc query role admin
				.bookingId(saved.getId()).type(NotificationType.BOOKING_CREATED).title("New booking")
				.message("User " + user.getFullName() + " created booking #" + saved.getId()).build());
//		socketService.sendToUser(ADMIN_ID, "BOOKING_CREATED", saved);
		return mapper.toDTO(saved);
	}

	@Override
	@Transactional
	public ResponseBookingDTO confirm(Long bookingId) {

		Booking booking = bookingRepository.findByIdForUpdate(bookingId)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.BOOKING_NOT_FOUND));

		validatePending(booking);

//		option validateBookingTime(booking.getSlot());

		booking.setStatus(BookingStatus.CONFIRMED);
		Booking saved = bookingRepository.save(booking);

		notificationService.create(RequestCreateNotificationDTO.builder().userId(saved.getUser().getId())
				.bookingId(saved.getId()).type(NotificationType.CONFIRMED).title("Booking confirmed")
				.message("Your booking #" + saved.getId() + " is confirmed").build());
//		socketService.sendToUser(saved.getUser().getId(), "BOOKING_CONFIRMED", saved);
		return mapper.toDTO(bookingRepository.save(booking));
	}

	@Override
	@Transactional
	public ResponseBookingDTO reject(Long bookingId) {

		Booking booking = bookingRepository.findByIdForUpdate(bookingId)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.BOOKING_NOT_FOUND));

		validatePending(booking);

		ServiceSlot slot = slotRepository.findByIdForUpdate(booking.getSlot().getId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_SLOT_NOT_FOUND));

		if (slot.getBookedCount() > 0) {
			slot.setBookedCount(slot.getBookedCount() - 1);
		}

		slotRepository.save(slot);

		booking.setStatus(BookingStatus.REJECTED);
		notificationService.create(RequestCreateNotificationDTO.builder().userId(booking.getUser().getId())
				.bookingId(booking.getId()).type(NotificationType.REJECTED).title("Booking rejected")
				.message("Your booking was rejected").build());
//		socketService.sendToUser(saved.getUser().getId(), "BOOKING_CONFIRMED", saved);
		return mapper.toDTO(bookingRepository.save(booking));
	}

	@Override
	@Transactional
	public ResponseBookingDTO cancel(Long bookingId) {

		String email = SecurityUtil.getCurrentUserEmail();

		User user = userService.getUserByEmail(email);

		Booking booking = bookingRepository.findByIdAndUserIdForUpdate(bookingId, user.getId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.BOOKING_NOT_FOUND));

		validatePending(booking);

		ServiceSlot slot = slotRepository.findByIdForUpdate(booking.getSlot().getId())
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.SERVICE_SLOT_NOT_FOUND));

		if (slot.getBookedCount() > 0) {
			slot.setBookedCount(slot.getBookedCount() - 1);
		}

		slotRepository.save(slot);

		booking.setStatus(BookingStatus.CANCELLED);
		notificationService.create(RequestCreateNotificationDTO.builder().userId(booking.getUser().getId())
				.bookingId(booking.getId()).type(NotificationType.CANCELLED).title("Booking cancelled")
				.message("Booking was cancelled by user").build());
//		socketService.sendToUser(ADMIN_ID, "BOOKING_CONFIRMED", saved);
		return mapper.toDTO(bookingRepository.save(booking));
	}

	@Override
	public List<ResponseBookingDTO> getMyBookings() {

		String email = SecurityUtil.getCurrentUserEmail();

		return bookingRepository.findByUserEmail(email).stream().map(mapper::toDTO).toList();
	}

	@Override
	public ResponseBookingDTO getById(Long bookingId) {
		return mapper.toDTO(findBooking(bookingId));
	}

//	@Override
//	public Page<ResponseBookingDTO> getAll(BookingFilterDTO filter, Pageable pageable) {
//
//		Specification<Booking> spec = BookingSpec.filter(filter);
//		return bookingRepository.findAll(spec, pageable).map(mapper::toDTO);
//	}

	@Override
	public Page<ResponseBookingDTO> getAll(BookingFilterDTO filter, Pageable pageable) {

		System.out.println("========== BOOKING PAGE REQUEST ==========");
		System.out.println("page = " + pageable.getPageNumber());
		System.out.println("size = " + pageable.getPageSize());
		System.out.println("sort = " + pageable.getSort());

		Specification<Booking> spec = BookingSpec.filter(filter);

		Pageable safePageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
				Sort.by("createdAt").descending());

		System.out.println("========== SAFE PAGEABLE ==========");
		System.out.println("page = " + safePageable.getPageNumber());
		System.out.println("size = " + safePageable.getPageSize());
		System.out.println("sort = " + safePageable.getSort());

		Page<Booking> page = bookingRepository.findAll(spec, safePageable);

		System.out.println("========== QUERY RESULT ==========");
		System.out.println("totalElements = " + page.getTotalElements());
		System.out.println("totalPages = " + page.getTotalPages());
		System.out.println("pageNumber = " + page.getNumber());
		System.out.println("numberOfElements = " + page.getNumberOfElements());
		System.out.println("contentSize = " + page.getContent().size());

		page.getContent().forEach(b -> System.out.println("bookingId = " + b.getId()));

		return page.map(mapper::toDTO);
	}

	private Booking findBooking(Long bookingId) {
		return bookingRepository.findById(bookingId)
				.orElseThrow(() -> new AppRuntimeException(ErrorBookingCode.BOOKING_NOT_FOUND));
	}

	private void validatePending(Booking booking) {

		if (booking.getStatus() != BookingStatus.PENDING) {
			throw new AppRuntimeException(ErrorBookingCode.BOOKING_STATUS_INVALID);
		}
	}

	private void validateBookingTime(ServiceSlot slot) {

		LocalDateTime slotTime = slot.getSlotDate().atTime(slot.getStartTime());

		if (slotTime.isBefore(LocalDateTime.now())) {
			throw new AppRuntimeException(ErrorBookingCode.BOOKING_TIME_INVALID);
		}
	}
}
