package com.hange.booking.booking.service.impl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceSlotScheduler {

	private final ServiceSlotServiceImpl serviceSlotService;

	@Scheduled(cron = "0 5 0 * * *")
	public void maintainSlots() {
		serviceSlotService.maintainSevenDaysSlots();
	}
}