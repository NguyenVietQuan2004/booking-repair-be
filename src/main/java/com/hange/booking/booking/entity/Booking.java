package com.hange.booking.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import com.hange.booking.auth.entity.user.User;
import com.hange.booking.booking.entity.constant.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "service_id", nullable = false)
	private Service service;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id", nullable = false)
	private ServiceSlot slot;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "location_id", nullable = false)
	private Location location;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private BookingStatus status;

	@Column(name = "slot_date", nullable = false)
	private LocalDate slotDate;

	@Column(name = "slot_time_start", nullable = false)
	private LocalTime slotTimeStart;

	@Column(name = "slot_time_end", nullable = false)
	private LocalTime slotTimeEnd;

	@Column(name = "service_name", nullable = false)
	private String serviceName;

	@Column(name = "service_price", precision = 12, scale = 2)
	private BigDecimal servicePrice;

	@Column(name = "location_name")
	private String locationName;

	@Column(name = "location_address", columnDefinition = "TEXT")
	private String locationAddress;

	@Column(name = "service_duration")
	private Integer serviceDuration;

	@Column(columnDefinition = "TEXT")
	private String note;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
		updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}
}