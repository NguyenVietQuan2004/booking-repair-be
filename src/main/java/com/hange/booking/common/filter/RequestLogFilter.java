package com.hange.booking.common.filter;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLogFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String origin = request.getHeader("Origin");

		System.out.println("========== REQUEST ==========");
		System.out.println("Method : " + request.getMethod());
		System.out.println("URL    : " + request.getRequestURL());
		System.out.println("Origin : " + origin);
		System.out.println("=============================");

		filterChain.doFilter(request, response);
	}
}