package com.msa.bankingsystem.core.mapper;

import org.springframework.stereotype.Component;

import com.msa.bankingsystem.models.Account;

@Component
public class AccountMapper {

	public String accountToString(Account account) {
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n", account.getId(), account.getUserId(),
				account.getAccountNumber(), account.getName(), account.getSurname(), account.getEmail(),
				account.getIdendityNumber(), account.getType(), account.getBalance(), account.getLastUpdateDate(),
				account.isDeleted());
	}

	public Account stringToAccount(String text) {

		String[] textSplit = text.split(",");
		return Account.builder().id(Integer.parseInt(textSplit[0])).userId(Integer.parseInt(textSplit[1]))
				.accountNumber(textSplit[2]).name(textSplit[3]).surname(textSplit[4]).email(textSplit[5])
				.idendityNumber(textSplit[6]).type(textSplit[7]).balance(Double.parseDouble(textSplit[8]))
				.lastUpdateDate(Long.parseLong(textSplit[9])).isDeleted(Boolean.parseBoolean(textSplit[10])).build();
	}
}
