package com.hange.booking.auth.config.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.hange.booking.auth.service.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsCustom implements UserDetailsService {
	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		com.hange.booking.auth.entity.user.User user = userService.getUserByEmail(email);

		userService.validateUser(user);

		String role = user.getRole().getName();

		return new User(user.getEmail(), user.getPasswordHash(),
				Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))

		);

	}

}
