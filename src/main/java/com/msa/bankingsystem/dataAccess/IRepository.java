package com.msa.bankingsystem.dataAccess;

import java.util.List;

import com.msa.bankingsystem.models.Account;

public interface IRepository {

	void save(Account account);

	Account getByAccountNumber(String accountNumber);

	List<Account> getAll();

	Account update(String accountNumber, double amount);

	Account transferBetweenAccounts(String accountNumber, String transferredAccountNumber, double amount);
}
