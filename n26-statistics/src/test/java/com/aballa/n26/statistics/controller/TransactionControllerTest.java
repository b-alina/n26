package com.aballa.n26.statistics.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.aballa.n26.statistics.model.Transaction;
import com.aballa.n26.statistics.service.TransactionService;

public class TransactionControllerTest {

	@Mock
	private TransactionService transactionService = mock(TransactionService.class);

	private TransactionController transactionController = new TransactionController(transactionService);

	private Transaction transaction;

	@Before
	public void setup() throws Exception {
		transaction = new Transaction(10D, System.currentTimeMillis());
	}

	@Test
	public void shoultReturn204ResponseForExpiredTransaction() {

		when(transactionService.addTransaction(transaction)).thenReturn(false);

		ResponseEntity<?> responseEntity;

		responseEntity = transactionController.addTransaction(transaction);

		Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.NO_CONTENT);
	}

	@Test
	public void shoultReturn201ResponseForValidTransaction() {

		when(transactionService.addTransaction(transaction)).thenReturn(true);

		ResponseEntity<?> responseEntity;

		responseEntity = transactionController.addTransaction(transaction);

		Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.CREATED);
	}
}
