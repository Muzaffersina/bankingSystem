package com.msa.bankingsystem.core.mapper;

import org.springframework.stereotype.Component;

import com.msa.bankingsystem.models.Account;

@Component
public class AccountMapper {

	public String accountToString(Account account) {
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", account.getId(), account.getAccountNumber(),
				account.getName(), account.getSurname(), account.getEmail(), account.getIdendityNumber(),
				account.getType(), account.getBalance(), account.getLastUpdateDate(), account.isDeleted());
	}

	public Account stringToAccount(String text) {

		String[] textSplit = text.split(",");
		return Account.builder().id(Integer.parseInt(textSplit[0])).accountNumber(textSplit[1]).name(textSplit[2])
				.surname(textSplit[3]).email(textSplit[4]).idendityNumber(textSplit[5]).type(textSplit[6])
				.balance(Double.parseDouble(textSplit[7])).lastUpdateDate(Long.parseLong(textSplit[8]))
				.isDeleted(Boolean.parseBoolean(textSplit[9])).build();
	}
}
