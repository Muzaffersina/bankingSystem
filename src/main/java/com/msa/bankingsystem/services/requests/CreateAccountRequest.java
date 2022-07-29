package com.msa.bankingsystem.services.requests;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.msa.bankingsystem.services.message.Messages;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

	@NotNull(message = "name" + Messages.VALIDATIONNOTNULLERRORS)
	@Size(min=1, max=20)
	private String name;

	@NotNull(message = "surname" + Messages.VALIDATIONNOTNULLERRORS)
	@Size(min=1, max=20)
	private String surname;

	@NotNull(message = "email" + Messages.VALIDATIONNOTNULLERRORS)
	@Email(message =Messages.VALIDATIONEMAILERRORS)
	@Size(min=5,max = 30)
	private String email;

	@NotNull(message = "idendityNumber" + Messages.VALIDATIONNOTNULLERRORS)
	@Size(min=11, max=11)
	private String idendityNumber;

	@NotNull(message = "type" + Messages.VALIDATIONNOTNULLERRORS)
	private String type;

}
