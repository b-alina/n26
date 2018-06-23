package com.aballa.n26.statistics.service;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.aballa.n26.statistics.model.Statistics;
import com.aballa.n26.statistics.model.Transaction;

@Service
public class TransactionServiceImpl implements TransactionService {

	private static final String STATISTIC_CACHE_KEY = "uniqueStatistic";
	private static final String STATISTICS_CACHE_NAME = "statistics";

	private ConcurrentSkipListMap<Long, Double> transactions = new ConcurrentSkipListMap<>();
	private DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
	private Statistics statistics = new Statistics();

	private CacheManager cacheManager;

	@Autowired
	public TransactionServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	/**
	 * Add transaction if transaction timestamp is in the past minute.
	 * 
	 * @param Transaction
	 *            to be added
	 * 
	 * @return true if timestamp is in the past minute, false otherwise
	 */
	@Override
	public boolean addTransaction(Transaction transaction) {

		if (!timestampInLast60Sec(transaction.getTimestamp())) {
			return false;
		} else {
			transactions.putIfAbsent(transaction.getTimestamp(), transaction.getAmount());
		}

		return true;
	}

	/**
	 * Update statistics based on transactions from the last minute. This method
	 * is called every second from a scheduled task.
	 */
	@Override
	public void updateStatistics() {

		summaryStatistics = getTransactionsFromLastMinute().values().stream().mapToDouble(amount -> amount)
				.summaryStatistics();

		statistics = new Statistics(summaryStatistics.getSum(), summaryStatistics.getAverage(),
				summaryStatistics.getMax(), summaryStatistics.getMin(), summaryStatistics.getCount());

		cacheManager.getCache(STATISTICS_CACHE_NAME).put(STATISTIC_CACHE_KEY, statistics);

		transactions.headMap(Instant.now().minusSeconds(60).toEpochMilli()).clear();
	}

	@Override
	public ConcurrentNavigableMap<Long, Double> getTransactionsFromLastMinute() {
		return transactions.tailMap(Instant.now().minusSeconds(60).toEpochMilli(), true);
	}

	/**
	 * Checks is provided timestamp is within the past minute.
	 * 
	 * @param timestamp
	 *            Timestamp
	 * @return true if timestamp is in the past minute, false otherwise
	 */
	private boolean timestampInLast60Sec(final long timestamp) {

		// always in UTC if not timezone set
		long currentUtcTimestamp = Instant.now().toEpochMilli();
		long currentMinus60Sec = Instant.now().minusSeconds(60).toEpochMilli();

		return timestamp <= currentUtcTimestamp && timestamp > currentMinus60Sec;
	}
}
