package com.msa.bankingsystem.services.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

	private String name;
	private String surname;
	private String email;
	private String idendityNumber;
	private String type;

}
