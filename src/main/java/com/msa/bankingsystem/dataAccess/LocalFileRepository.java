package com.msa.bankingsystem.dataAccess;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.msa.bankingsystem.core.externalServices.ExchangeServis;
import com.msa.bankingsystem.core.file.FileReaders;
import com.msa.bankingsystem.core.file.FileWriters;
import com.msa.bankingsystem.core.mapper.AccountMapper;
import com.msa.bankingsystem.models.Account;

@Component
public class LocalFileRepository implements IRepository {

	@Value("${local.dbFile}")
	private String filePath;

	private FileWriters fileWriters;
	private FileReaders fileReaders;
	private AccountMapper accountMapper;
	private ExchangeServis exchangeServis;

	@Autowired
	public LocalFileRepository(FileWriters fileWriters, FileReaders fileReaders, AccountMapper accountMapper,
			ExchangeServis exchangeServis) {
		this.fileWriters = fileWriters;
		this.fileReaders = fileReaders;
		this.accountMapper = accountMapper;
		this.exchangeServis = exchangeServis;
	}

	@Override
	public void save(Account account) {

		String text = this.accountMapper.accountToString(account);

		this.fileWriters.writeToFile(filePath, text, true);

	}

	@Override
	public Account getByAccountNumber(String accountNumber) {

		List<String> fileRead = this.fileReaders.readFile(filePath);

		for (String readLine : fileRead) {
			if (readLine.startsWith(accountNumber) && accountNumber.length() == 10) {
				Account account = accountMapper.stringToAccount(readLine);
				return account;
			}
		}
		return null;
	}

	@Override
	public Account update(String accountNumber, double amount) {

		updateBalance(accountNumber, amount);
		return getByAccountNumber(accountNumber);
	}

	@Override
	public Account transferBetweenAccounts(String accountNumber, String transferredAccountNumber, double amount) {

		Account account = getByAccountNumber(accountNumber);
		Account transferredAccount = getByAccountNumber(transferredAccountNumber);

		double exchangeAmount = amount;
		if (!account.getType().equals(transferredAccount.getType())) {
			exchangeAmount = this.exchangeServis.calculateExchange(account.getType(), transferredAccount.getType(),
					amount);
		}
		updateBalance(accountNumber, -amount);
		updateBalance(transferredAccountNumber, exchangeAmount);

		return getByAccountNumber(accountNumber);

	}

	@Override
	public List<Account> getAll() {

		List<String> fileRead = this.fileReaders.readFile(filePath);
		List<Account> accounts = new ArrayList<>();
		for (String readLine : fileRead) {
			Account account = accountMapper.stringToAccount(readLine);
			accounts.add(account);
		}
		return accounts;
	}

	private void updateBalance(String accountNumber, double amount) {

		List<String> fileRead = this.fileReaders.readFile(filePath);
		List<Account> accounts = new ArrayList<>();

		for (String readLine : fileRead) {
			if (!readLine.startsWith(accountNumber) && accountNumber.length() == 10) {
				Account account = accountMapper.stringToAccount(readLine);
				accounts.add(account);
			} else {
				Account account = accountMapper.stringToAccount(readLine);
				account.setBalance(account.getBalance() + amount);
				account.setLastUpdateDate(System.currentTimeMillis());
				accounts.add(account);
			}
			this.fileWriters.writeToFile(filePath, "", false);
			saveAccounts(accounts);
		}
	}

	private void saveAccounts(List<Account> accounts) {
		for (Account account : accounts) {
			save(account);
		}
	}

}
