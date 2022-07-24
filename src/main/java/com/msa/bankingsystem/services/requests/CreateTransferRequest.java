package com.msa.bankingsystem.services.requests;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.msa.bankingsystem.services.message.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTransferRequest {

	@NotNull(message = "transferredAccountNumber" + Messages.VALIDATIONNOTNULLERRORS)
	@Size(min=10,max=10)
	private String transferredAccountNumber;

	@NotNull(message = "Amount" + Messages.VALIDATIONNOTNULLERRORS)
	@Positive(message = "Amount" + Messages.VALIDATIONAMOUNTERRORS)
	private double amount;
}
