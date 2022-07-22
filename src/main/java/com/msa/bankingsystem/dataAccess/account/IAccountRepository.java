package com.msa.bankingsystem.dataAccess.account;

import java.util.List;

import com.msa.bankingsystem.models.Account;

public interface IAccountRepository {

	void save(Account account);

	Account delete(String accountNumber);

	Account transferBetweenAccounts(String senderAccountNumber, String transferredAccountNumber, double amount,
			double exchangeAmount);

	Account update(String accountNumber, double amount);

	Account getByAccountNumber(String accountNumber);

	List<Account> getAll();

	Account getByAccounId(int id);
}
