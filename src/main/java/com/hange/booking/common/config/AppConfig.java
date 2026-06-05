package com.hange.booking.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

@Configuration
public class AppConfig {
	@Bean
	public ObjectMapper objectMapper() {
		return JsonMapper.builder().build();
	}
}
