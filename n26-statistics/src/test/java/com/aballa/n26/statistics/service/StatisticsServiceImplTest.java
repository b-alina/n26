package com.aballa.n26.statistics.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.aballa.n26.statistics.model.Statistics;

@RunWith(SpringJUnit4ClassRunner.class)
public class StatisticsServiceImplTest {

	private static final String STATISTIC_CACHE_KEY = "uniqueStatistic";
	private static final String STATISTICS_CACHE_NAME = "statistics";

	@InjectMocks
	StatisticsServiceImpl statisticsService;

	@Mock
	TransactionService transactionService;

	@Mock
	CacheManager cacheManager;

	private Statistics statistics;

	@Before
	public void setup() {
		cacheManager = new ConcurrentMapCacheManager(STATISTICS_CACHE_NAME);

		statistics = new Statistics();
		cacheManager.getCache(STATISTICS_CACHE_NAME).put(STATISTIC_CACHE_KEY, statistics);

		this.statisticsService = new StatisticsServiceImpl(cacheManager);
	}

	@Test
	public void shoultReturnStatisticsFromCache() {
		Assert.assertNotNull(cacheManager);
		Assert.assertNotNull(cacheManager.getCache(STATISTICS_CACHE_NAME));

		Statistics statisticsResult = statisticsService.getStatistics();

		Assert.assertEquals(statisticsResult.getCount(), statistics.getCount());
	}
}
