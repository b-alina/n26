package com.aballa.n26.statistics.model;

import java.io.Serializable;

public class Transaction implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1111207289954184652L;

	private Double amount;
	private long timestamp;

	public Transaction() {
	}

	public Transaction(final Double amount, final long timestamp) {
		this.amount = amount;
		this.timestamp = timestamp;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Transaction [amount=" + amount + ", timestamp=" + timestamp + "]";
	}
}
