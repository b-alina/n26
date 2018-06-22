package com.aballa.n26.statistics.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.stream.IntStream;

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
import com.aballa.n26.statistics.model.Transaction;

@RunWith(SpringJUnit4ClassRunner.class)
public class TransactionServiceImplTest {

	private static final String STATISTIC_CACHE_KEY = "uniqueStatistic";
	private static final String STATISTICS_CACHE_NAME = "statistics";

	@InjectMocks
	TransactionServiceImpl service;

	@Mock
	TransactionService transactionService;

	@Mock
	CacheManager cacheManager;

	@Before
	public void setup() {
		cacheManager = new ConcurrentMapCacheManager(STATISTICS_CACHE_NAME);
		this.service = new TransactionServiceImpl(cacheManager);
	}

	@Test
	public void testAddTransactionInPastMinute() {
		boolean result = service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(50)));
		Assert.assertTrue(result);
	}

	@Test
	public void testAddTransactionOlderThanAMinute() {
		boolean result = service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(70)));
		Assert.assertFalse(result);
	}

	@Test
	public void testExistingTransactionsFromLastMinute() {

		IntStream.rangeClosed(0, 60)
				.forEach(index -> service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(index))));
		ConcurrentNavigableMap<Long, Double> transactionsFromLastMinute = service.getTransactionsFromLastMinute();

		assertFalse(transactionsFromLastMinute.isEmpty());
	}

	@Test
	public void testNoTransactionsFromLastMinute() {

		IntStream.rangeClosed(90, 99)
				.forEach(index -> service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(index))));
		ConcurrentNavigableMap<Long, Double> transactionsFromLastMinute = service.getTransactionsFromLastMinute();

		assertTrue(transactionsFromLastMinute.isEmpty());
	}

	@Test
	public void testNoTransactionsInStatisticsForLast60Sec() {
		IntStream.rangeClosed(0, 60)
				.forEach(index -> service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(index))));
		ConcurrentNavigableMap<Long, Double> transactionsFromLastMinute = service.getTransactionsFromLastMinute();

		when(transactionService.getTransactionsFromLastMinute()).thenReturn(transactionsFromLastMinute);

		service.updateStatistics();

		Statistics statistics = (Statistics) cacheManager.getCache(STATISTICS_CACHE_NAME).get(STATISTIC_CACHE_KEY)
				.get();

		Assert.assertTrue(statistics.getCount() > 0);
	}

	@Test
	public void testExistingTransactionsInStatisticsForLast60Sec() {
		IntStream.rangeClosed(61, 99)
				.forEach(index -> service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(index))));
		ConcurrentNavigableMap<Long, Double> transactionsFromLastMinute = service.getTransactionsFromLastMinute();

		when(transactionService.getTransactionsFromLastMinute()).thenReturn(transactionsFromLastMinute);

		service.updateStatistics();

		Statistics statistics = (Statistics) cacheManager.getCache(STATISTICS_CACHE_NAME).get(STATISTIC_CACHE_KEY)
				.get();

		Assert.assertTrue(statistics.getCount() == 0);
	}
	
	@Test
	public void testTransactionCountInStatistic() throws InterruptedException {

		IntStream.rangeClosed(0, 30)
				.forEach(index -> service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(index))));
		ConcurrentNavigableMap<Long, Double> transactionsFromLastMinute = service.getTransactionsFromLastMinute();

		when(transactionService.getTransactionsFromLastMinute()).thenReturn(transactionsFromLastMinute);

		service.updateStatistics();

		Statistics statistics = (Statistics) cacheManager.getCache(STATISTICS_CACHE_NAME).get(STATISTIC_CACHE_KEY)
				.get();

		Assert.assertTrue(statistics.getCount() == 30);

		Thread.sleep(5000);

		IntStream.rangeClosed(31, 35)
				.forEach(index -> service.addTransaction(new Transaction(5D, extractTimeFromCurrentUTC(index))));
		ConcurrentNavigableMap<Long, Double> transactionsFromLastMinute2 = service.getTransactionsFromLastMinute();

		when(transactionService.getTransactionsFromLastMinute()).thenReturn(transactionsFromLastMinute2);

		service.updateStatistics();

		statistics = (Statistics) cacheManager.getCache(STATISTICS_CACHE_NAME).get(STATISTIC_CACHE_KEY).get();

		Assert.assertTrue(statistics.getCount() == 35);
	}

	private long extractTimeFromCurrentUTC(long time) {
		OffsetDateTime utc = OffsetDateTime.now(ZoneOffset.UTC);
		return utc.minusSeconds(time).toInstant().toEpochMilli();
	}
}
