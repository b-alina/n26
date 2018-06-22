package com.aballa.n26.statistics.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aballa.n26.statistics.model.Statistics;
import com.aballa.n26.statistics.service.StatisticsService;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {

	private static final Log LOG = LogFactory.getLog(StatisticsController.class);

	private final StatisticsService statisticService;

	@Autowired
	public StatisticsController(final StatisticsService statisticsService) {
		this.statisticService = statisticsService;
	}

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public Statistics getStatistics() {

		Statistics statistics = statisticService.getStatistics();
		LOG.info(statistics.getCount() + " Transactions count in statistics");

		return statistics;
	}
}
