package com.msa.bankingsystem.core.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.msa.bankingsystem.models.Account;
import com.msa.bankingsystem.services.dtos.GetListAccountDto;

@Component
public class AccountDtoMapper {

	public GetListAccountDto accountToAccountDto(Account account) {

		return new GetListAccountDto().builder().accountNumber(account.getAccountNumber()).name(account.getName())
				.surname(account.getSurname()).email(account.getEmail()).idendityNumber(account.getIdendityNumber())
				.type(account.getType()).balance(account.getBalance()).build();
	}

	public List<GetListAccountDto> listAccountToListAccountDto(List<Account> accounts) {

		List<GetListAccountDto> dtos = new ArrayList<>();
		for (Account account : accounts) {
			dtos.add(accountToAccountDto(account));
		}
		return dtos;
	}

}
