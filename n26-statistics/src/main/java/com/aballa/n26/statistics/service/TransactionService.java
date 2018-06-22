package com.aballa.n26.statistics.service;

import java.util.concurrent.ConcurrentNavigableMap;

import com.aballa.n26.statistics.model.Transaction;

public interface TransactionService {

	boolean addTransaction(Transaction transaction);

	void updateStatistics();

	ConcurrentNavigableMap<Long, Double> getTransactionsFromLastMinute();
}
