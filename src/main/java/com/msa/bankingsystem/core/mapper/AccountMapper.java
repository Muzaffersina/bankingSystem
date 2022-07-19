package com.msa.bankingsystem.core.mapper;

import org.springframework.stereotype.Component;

import com.msa.bankingsystem.models.Account;

@Component
public class AccountMapper {

	public String accountToString(Account account) {
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", account.getAccountNumber(), account.getName(),
				account.getSurname(), account.getEmail(), account.getIdendityNumber(), account.getType(),
				account.getBalance(), account.getLastUpdateDate());
	}

	public Account stringToAccount(String text) {

		String[] textSplit = text.split(",");

		return Account.builder().accountNumber(textSplit[0]).name(textSplit[1]).surname(textSplit[2])
				.email(textSplit[3]).idendityNumber(textSplit[4]).type(textSplit[5])
				.balance(Double.parseDouble(textSplit[6])).lastUpdateDate(Long.parseLong(textSplit[7])).build();
	}
}
