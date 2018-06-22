package com.aballa.n26.statistics.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aballa.n26.statistics.service.TransactionService;

@Component
public class ScheduledTask {

	@Autowired
	private TransactionService transactionService;

	/**
	 * Update statistics every second.
	 */
	@Scheduled(fixedRate = 1000)
	public void updateStatisticsEverySecond() {
		transactionService.updateStatistics();
	}
}
