package com.hange.booking.auth.entity.user;

import java.time.LocalDateTime;
import java.util.List;

import com.hange.booking.auth.entity.role.Role;

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
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder // viết logic theo dạng builder
@SuppressWarnings("unused")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String email;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	private String fullName;
	private String phone;
	private String avatarUrl;
	@Column(columnDefinition = "TEXT")
	private String address;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id")
	private Role role;

	@Builder.Default
	private Boolean emailVerified = false;

	private LocalDateTime lastLoginAt;

	@Builder.Default
	private Integer failedLoginCount = 0;

	private LocalDateTime lockedUntil;
	private LocalDateTime nextBookingAllowedAt;
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private AccountStatusEnum accountStatus = AccountStatusEnum.PENDING;

	private LocalDateTime passwordChangedAt;

	@OneToMany(mappedBy = "user")
	private List<RefreshToken> tokens;
	@Builder.Default
	private Integer tokenVersion = 0;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	private LocalDateTime deletedAt;

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}