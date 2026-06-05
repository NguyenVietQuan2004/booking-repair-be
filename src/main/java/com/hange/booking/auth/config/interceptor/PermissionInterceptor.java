package com.hange.booking.auth.config.interceptor;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.hange.booking.auth.entity.permission.Permission;
import com.hange.booking.auth.entity.role.Role;
import com.hange.booking.auth.entity.user.User;
import com.hange.booking.auth.service.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

	private final UserService userService;

	@Override
	@Transactional
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//		>>> RUN preHandle
//		>>> path= /api/v1/roles
//		>>> httpMethod= GET
//		>>> requestURI= /api/v1/roles

		String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
		String requestURI = request.getRequestURI();
		String httpMethod = request.getMethod();
		System.out.println(">>> RUN preHandle");
		System.out.println(">>> path= " + path);
		System.out.println(">>> httpMethod= " + httpMethod);
		System.out.println(">>> requestURI= " + requestURI);
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		String email = (authentication != null) ? authentication.getName() : "";
		if (authentication == null || !authentication.isAuthenticated()
				|| authentication instanceof AnonymousAuthenticationToken) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");

			response.getWriter().write("""
					{
					    "status": 400,
					    "message": "Unauthorized",
					    "errorCode": "INVALID_TOKEN",
					    "data": null,
					    "error": "INVALID_TOKEN"
					}
					""");

			return false;
		}
		String email = authentication.getName();
		if (email != null && !email.isEmpty()) {
			System.out.println("email" + email);
			User user = this.userService.getUserByEmail(email);
			if (user != null) {
				Role role = user.getRole();
				System.out.println(role.getName());

				if (role != null) {
					List<Permission> permissions = role.getPermissions();
					boolean isAllow = permissions.stream().anyMatch(item -> {

						boolean pathMatch = item.getApiPath().equals(path);
						boolean methodMatch = item.getMethod().equalsIgnoreCase(httpMethod);
						return pathMatch && methodMatch;
					});
					if (!isAllow) {
						System.out.println("🚫 Access denied for: " + email + " → " + httpMethod + " " + path);
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
						response.setContentType("application/json");
						response.setCharacterEncoding("UTF-8");
						response.getWriter().write("""
								{
								    "status": 403,
								    "message": "You do not have permission",
								    "statusCode": "ACCESS_DENIED",
								    "data": null,
								    "error": ""
								}
								""");
						return false;
					}
				}
			}
		}

		System.out.println("✅ Access granted:  " + httpMethod + " " + path);
		return true;
	}
}
