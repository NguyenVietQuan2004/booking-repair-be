package com.hange.booking.booking.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.repository.UserRepository;
import com.hange.booking.auth.service.user.UserService;
import com.hange.booking.auth.utils.SecurityUtil;
import com.hange.booking.booking.dto.booking.RequestCreateBookingDTO;
import com.hange.booking.booking.dto.booking.ResponseBookingDTO;
import com.hange.booking.booking.dto.notification.RequestCreateNotificationDTO;
import com.hange.booking.booking.dto.notification.ResponseNotificationDTO;
import com.hange.booking.booking.entity.Booking;
import com.hange.booking.booking.entity.ServiceSlot;
import com.hange.booking.booking.entity.constant.BookingStatus;
import com.hange.booking.booking.repository.BookingRepository;
import com.hange.booking.booking.repository.ServiceSlotRepository;
import com.hange.booking.booking.service.impl.BookingServiceImpl;
import com.hange.booking.booking.service.impl.SocketServiceImpl;
import com.hange.booking.booking.utils.mapper.BookingMapper;
import com.hange.booking.common.exception.AppRuntimeException;
import com.hange.booking.common.exception.ErrorBookingCode;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // ← FIX QUAN TRỌNG
class BookingServiceTest {

	@Mock
	private BookingRepository bookingRepository;
	@Mock
	private ServiceSlotRepository slotRepository;
	@Mock
	private UserRepository userRepository;
	@Mock
	private UserService userService;
	@Mock
	private NotificationService notificationService;
	@Mock
	private BookingMapper mapper;
	@Mock
	private SocketServiceImpl socketServiceImpl;

	@InjectMocks
	private BookingServiceImpl bookingService;

	private User user;
	private User adminUser;
	private ServiceSlot slot;
	private RequestCreateBookingDTO request;
	private Booking savedBooking;
	private ResponseBookingDTO responseDTO;

	@BeforeEach
	void setUp() {
		user = User.builder().id(1L).email("user@gmail.com").fullName("Nguyễn Văn A").build();
		adminUser = User.builder().id(999L).email("admin@gmail.com").build();

		com.hange.booking.booking.entity.Service serviceMock = mock(com.hange.booking.booking.entity.Service.class);
		when(serviceMock.getName()).thenReturn("Dịch vụ Test");
		when(serviceMock.getPrice()).thenReturn(java.math.BigDecimal.valueOf(500000));
		when(serviceMock.getDurationMinutes()).thenReturn(60);

		com.hange.booking.booking.entity.Location locationMock = mock(com.hange.booking.booking.entity.Location.class);
		when(locationMock.getName()).thenReturn("Chi nhánh Test");
		when(locationMock.getAddress()).thenReturn("123 Đường ABC");

		slot = ServiceSlot.builder().id(100L).bookedCount(5).maxCapacity(10).slotDate(LocalDate.now().plusDays(1))
				.startTime(LocalTime.of(14, 0)).service(serviceMock).location(locationMock).build();

		request = new RequestCreateBookingDTO();
		request.setSlotId(100L);
		request.setNote("Đặt lịch test");

		savedBooking = Booking.builder().id(50L).user(user).slot(slot).status(BookingStatus.PENDING).build();
		responseDTO = new ResponseBookingDTO();
		responseDTO.setId(50L);
		when(userService.getUserByEmail("user@gmail.com")).thenReturn(user);
		when(userService.getUserByEmail("admin@gmail.com")).thenReturn(adminUser);

		ReflectionTestUtils.setField(bookingService, "adminEmail", "admin@gmail.com");
	}

	@Test
	void shouldCreateBookingSuccessfully() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@gmail.com");

			when(slotRepository.findByIdForUpdate(100L)).thenReturn(Optional.of(slot));
			when(bookingRepository.existsByUserIdAndSlotIdAndStatusIn(anyLong(), anyLong(), anyList()))
					.thenReturn(false);
			when(bookingRepository.save(any(Booking.class))).thenReturn(savedBooking);
			when(notificationService.create(any(RequestCreateNotificationDTO.class)))
					.thenReturn(new ResponseNotificationDTO());
			when(mapper.toDTO(any(Booking.class))).thenReturn(responseDTO);

			ResponseBookingDTO result = bookingService.create(request);

			assertNotNull(result);
			assertEquals(50L, result.getId());
		}
	}

	@Test
	void shouldThrowWhenSlotNotFound() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@gmail.com");

			when(slotRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.empty());

			AppRuntimeException exception = assertThrows(AppRuntimeException.class,
					() -> bookingService.create(request));

			assertEquals(ErrorBookingCode.SERVICE_SLOT_NOT_FOUND, exception.getErrorCode());
		}
	}

	@Test
	void shouldThrowWhenBookingTimeInvalid() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@gmail.com");

			// Tạo pastSlot đầy đủ để tránh NPE
			com.hange.booking.booking.entity.Service serviceMock = mock(com.hange.booking.booking.entity.Service.class);
			com.hange.booking.booking.entity.Location locationMock = mock(
					com.hange.booking.booking.entity.Location.class);

			ServiceSlot pastSlot = ServiceSlot.builder().id(100L).slotDate(LocalDate.now().minusDays(1))
					.startTime(LocalTime.of(10, 0)).service(serviceMock).location(locationMock).build();

			when(slotRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(pastSlot));

			AppRuntimeException exception = assertThrows(AppRuntimeException.class,
					() -> bookingService.create(request));

			assertEquals(ErrorBookingCode.BOOKING_TIME_INVALID, exception.getErrorCode());
		}
	}

	@Test
	void shouldThrowWhenDuplicateBooking() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@gmail.com");

			when(slotRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(slot));
			when(bookingRepository.existsByUserIdAndSlotIdAndStatusIn(anyLong(), anyLong(), anyList()))
					.thenReturn(true);

			AppRuntimeException exception = assertThrows(AppRuntimeException.class,
					() -> bookingService.create(request));

			assertEquals(ErrorBookingCode.DUPLICATE_BOOKING, exception.getErrorCode());
		}
	}

	@Test
	void shouldThrowWhenSlotNotAvailable() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@gmail.com");

			when(slotRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(slot));
			when(bookingRepository.existsByUserIdAndSlotIdAndStatusIn(anyLong(), anyLong(), anyList()))
					.thenReturn(false);

			slot.setBookedCount(10);
			slot.setMaxCapacity(10);

			AppRuntimeException exception = assertThrows(AppRuntimeException.class,
					() -> bookingService.create(request));

			assertEquals(ErrorBookingCode.SLOT_NOT_AVAILABLE, exception.getErrorCode());
		}
	}

	@Test
	void shouldHandleNullNoteSuccessfully() {
		try (MockedStatic<SecurityUtil> securityUtil = mockStatic(SecurityUtil.class)) {
			securityUtil.when(SecurityUtil::getCurrentUserEmail).thenReturn("user@gmail.com");

			when(slotRepository.findByIdForUpdate(anyLong())).thenReturn(Optional.of(slot));
			when(bookingRepository.existsByUserIdAndSlotIdAndStatusIn(anyLong(), anyLong(), anyList()))
					.thenReturn(false);
			when(bookingRepository.save(any())).thenReturn(savedBooking);
			when(notificationService.create(any())).thenReturn(new ResponseNotificationDTO());
			when(mapper.toDTO(any())).thenReturn(responseDTO);

			request.setNote(null);

			assertDoesNotThrow(() -> bookingService.create(request));
		}
	}
}