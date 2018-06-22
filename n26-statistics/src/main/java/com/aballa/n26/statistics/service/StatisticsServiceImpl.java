package com.aballa.n26.statistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.aballa.n26.statistics.model.Statistics;

@Service
public class StatisticsServiceImpl implements StatisticsService {

	private CacheManager cacheManager;

	private static final String STATISTIC_CACHE_KEY = "uniqueStatistic";
	private static final String STATISTICS_CACHE_NAME = "statistics";

	@Autowired
	public StatisticsServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Getter for statistics (retrieved from cache) based on transactions of the
	 * last 60sec
	 */
	@Override
	public Statistics getStatistics() {
		return (Statistics) cacheManager.getCache(STATISTICS_CACHE_NAME).get(STATISTIC_CACHE_KEY).get();
	}
}
