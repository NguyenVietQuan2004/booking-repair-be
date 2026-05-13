package com.hange.booking.auth.controller.test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

	@GetMapping("/")
	public ResponseEntity<Map<String, Object>> ping() {
		Map<String, Object> res = new HashMap<>();
		res.put("status", "UP");
		res.put("message", "Backend is running ping ping ping ok");
		res.put("timestamp", Instant.now().toString());

		return ResponseEntity.ok(res);
	}

}
