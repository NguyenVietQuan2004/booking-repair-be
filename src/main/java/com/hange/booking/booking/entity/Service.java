package com.hange.booking.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "services")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Service {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String name;

	@Column(nullable = false, unique = true, length = 255)
	private String slug;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(columnDefinition = "LONGTEXT")
	private String content;

	@Column(name = "duration_minutes", nullable = false)
	private Integer durationMinutes;

	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal price;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
	@Builder.Default
	private List<ServiceSlot> slots = new ArrayList<>();

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
	@Builder.Default
	private List<Booking> bookings = new ArrayList<>();

	@OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Image> images = new ArrayList<>();

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@PrePersist
	public void prePersist() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		updatedAt = LocalDateTime.now();
	}
}