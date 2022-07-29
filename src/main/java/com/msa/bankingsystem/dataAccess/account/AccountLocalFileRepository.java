package com.msa.bankingsystem.dataAccess.account;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.msa.bankingsystem.core.externalServices.ExchangeChanger;
import com.msa.bankingsystem.core.file.FileReaders;
import com.msa.bankingsystem.core.file.FileWriters;
import com.msa.bankingsystem.core.mapper.AccountMapper;
import com.msa.bankingsystem.models.Account;

public class AccountLocalFileRepository implements IAccountRepository {

	@Value("${local.dbAccountsFile}")
	private String filePath;

	private FileWriters fileWriters;
	private FileReaders fileReaders;
	private AccountMapper accountMapper;

	@Autowired
	public AccountLocalFileRepository(FileWriters fileWriters, FileReaders fileReaders, AccountMapper accountMapper,
			ExchangeChanger exchangeChanger) {
		this.fileWriters = fileWriters;
		this.fileReaders = fileReaders;
		this.accountMapper = accountMapper;

	}

	@Override
	public void save(Account account) {

		account.setId(checkLastId());
		account.setUserId(checkLastId());
		String text = this.accountMapper.accountToString(account);
		this.fileWriters.writeToFile(filePath, text, true);
	}

	@Override
	public Account delete(String accountNumber) {

		List<String> fileRead = this.fileReaders.readFile(filePath);
		List<Account> accounts = new ArrayList<>();

		for (String readLine : fileRead) {
			if (!readLine.contains(accountNumber) && accountNumber.length() == 10) {
				Account account = accountMapper.stringToAccount(readLine);
				accounts.add(account);
			} else {
				Account account = accountMapper.stringToAccount(readLine);
				account.setDeleted(true);
				account.setLastUpdateDate(System.currentTimeMillis());
				accounts.add(account);
			}
		}
		this.fileWriters.writeToFile(filePath, "", false);
		saveAccounts(accounts);
		return getByAccountNumber(accountNumber);
	}

	@Override
	public Account update(String accountNumber, double amount) {

		updateBalance(accountNumber, amount);
		return getByAccountNumber(accountNumber);
	}

	@Override
	public Account transferBetweenAccounts(String senderAccountNumber, String transferredAccountNumber, double amount,
			double exchangeAmount) {
		updateBalance(senderAccountNumber, -amount);
		updateBalance(transferredAccountNumber, exchangeAmount);

		return getByAccountNumber(senderAccountNumber);

	}

	@Override
	public Account getByAccountNumber(String accountNumber) {

		List<String> fileRead = this.fileReaders.readFile(filePath);
		for (String readLine : fileRead) {
			if (readLine.contains(accountNumber) && accountNumber.length() == 10) {
				Account account = accountMapper.stringToAccount(readLine);
				return account;
			}
		}
		return null;
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
			if (!readLine.contains(accountNumber) && accountNumber.length() == 10) {

				Account account = accountMapper.stringToAccount(readLine);
				accounts.add(account);
			} else {
				Account account = accountMapper.stringToAccount(readLine);
				account.setBalance(account.getBalance() + amount);
				account.setLastUpdateDate(System.currentTimeMillis());
				accounts.add(account);
			}
		}
		this.fileWriters.writeToFile(filePath, "", false);
		saveAccounts(accounts);
	}

	@Override
	public Account getByAccounId(int id) {

		List<String> fileRead = this.fileReaders.readFile(filePath);
		String stringId = String.valueOf(id);

		for (String readLine : fileRead) {
			if (readLine.startsWith(stringId + ",")) {
				Account account = accountMapper.stringToAccount(readLine);
				return account;
			}
		}
		return null;
	}

	private void saveAccounts(List<Account> accounts) {
		for (Account account : accounts) {
			save(account);
		}
	}

	private int checkLastId() {
		List<Account> accounts = getAll();
		return accounts.size() + 1;
	}

}
