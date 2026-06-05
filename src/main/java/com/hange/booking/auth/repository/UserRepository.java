package com.hange.booking.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.hange.booking.auth.entity.user.User;

import jakarta.persistence.LockModeType;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

	Optional<User> findByEmail(String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(" SELECT u FROM User u WHERE u.id = :id ")
	Optional<User> findByIdForUpdate(@Param("id") Long id);

	boolean existsByEmail(String email);
}