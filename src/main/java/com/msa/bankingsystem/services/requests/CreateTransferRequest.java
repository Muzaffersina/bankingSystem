package com.msa.bankingsystem.services.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferRequest {

	private String transferredAccountNumber;
	private double amount;
}
