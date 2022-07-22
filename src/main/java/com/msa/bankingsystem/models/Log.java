package com.msa.bankingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Log {
	private long id;
	private String senderAccountNumber;
	private String transferredAccountNumber;
	private String operationType;
	private String currency;
	private double amount;
	private String message;
}
