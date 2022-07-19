package com.msa.bankingsystem.services;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.ErrorDataResult;
import com.msa.bankingsystem.core.results.ErrorResult;
import com.msa.bankingsystem.core.results.Result;
import com.msa.bankingsystem.core.results.SuccessDataResult;
import com.msa.bankingsystem.core.results.SuccessResult;
import com.msa.bankingsystem.dataAccess.IRepository;
import com.msa.bankingsystem.models.Account;
import com.msa.bankingsystem.services.dtos.GetListLogDto;
import com.msa.bankingsystem.services.requests.CreateAccountRequest;
import com.msa.bankingsystem.services.requests.CreateDepositRequest;
import com.msa.bankingsystem.services.requests.CreateTransferRequest;

@Service
public class AccountManager implements IAccountService {

	@Value("${account.type}")
	private List<String> definedTypes;

	private IRepository iRepository;
	private IKafkaLoggerService iKafkaLoggerService;

	@Autowired
	public AccountManager(List<String> definedTypes, IRepository iRepository, IKafkaLoggerService iKafkaLoggerService) {
		this.definedTypes = definedTypes;
		this.iRepository = iRepository;
		this.iKafkaLoggerService = iKafkaLoggerService;
	}

	@Override
	public Result create(CreateAccountRequest createAccountRequest) {

		if (!checkIfType(createAccountRequest.getType(), this.definedTypes)) {
			return new ErrorResult("Invalid Account Type: " + createAccountRequest.getType());
		}

		Account account = manuelAccountMapper(createAccountRequest);
		account.setType(account.getType().toUpperCase());
		this.iRepository.save(account);
		return new SuccessResult("Created Account , Account Number=" + account.getAccountNumber());
	}

	@Override
	public DataResult<Account> getByAccountNumber(String accountNumber) {

		Account account = this.iRepository.getByAccountNumber(accountNumber);

		if (account == null) {
			return new ErrorDataResult<Account>("This account number cannot be found");
		}

		return new SuccessDataResult<Account>(account, "Listed Account");
	}

	@Override
	public DataResult<Account> deposit(String accountNumber, CreateDepositRequest createDepositRequest) {

		if (!checkIfAccountExists(accountNumber)) {
			return new ErrorDataResult<Account>(null, "Please Check Your Account Information");
		}
		Account account = this.iRepository.update(accountNumber, createDepositRequest.getAmount());

		String message = accountNumber + " deposit amount:" + createDepositRequest.getAmount() + " "
				+ account.getType();
		this.iKafkaLoggerService.sendMessage(message);
		return new SuccessDataResult<Account>(account, "Deposit Operation Successful");
	}

	@Override
	public DataResult<Account> transferBetweenAccounts(String accountNumber,
			CreateTransferRequest createTransferRequest) {

		if (!checkIfAccountExists(accountNumber)
				|| !checkIfAccountExists(createTransferRequest.getTransferredAccountNumber())) {
			return new ErrorDataResult<Account>("Please Check Your Accounts Information");
		} else {
			if (!checkIfEnoughBalance(accountNumber, createTransferRequest.getAmount())) {
				return new ErrorDataResult<Account>("Insufficient balance");
			} else {

				Account account = this.iRepository.transferBetweenAccounts(accountNumber,
						createTransferRequest.getTransferredAccountNumber(), createTransferRequest.getAmount());

				String message = accountNumber + " transfer amount:" + createTransferRequest.getAmount() + " "
						+ account.getType() + " transferred_account:"
						+ createTransferRequest.getTransferredAccountNumber();
				this.iKafkaLoggerService.sendMessage(message);
				return new SuccessDataResult<Account>(account, message);
			}
		}
	}

	@Override
	public DataResult<List<Account>> getAll() {
		return new SuccessDataResult<List<Account>>(this.iRepository.getAll(), "Listed Accounts");
	}

	@Override
	public DataResult<List<GetListLogDto>> getAccountLogsByAccountNumber(String accountNumber) {

		if (!checkIfAccountExists(accountNumber)) {
			return new ErrorDataResult<List<GetListLogDto>>("This account number cannot be found");
		}
		return this.iKafkaLoggerService.getLogsByAccountNumber(accountNumber);
	}

	private Account manuelAccountMapper(CreateAccountRequest createAccountRequest) {

		Account account = Account.builder().accountNumber(generateRandomAccountNumber())
				.name(createAccountRequest.getName()).surname(createAccountRequest.getSurname())
				.email(createAccountRequest.getEmail()).idendityNumber(createAccountRequest.getIdendityNumber())
				.balance(0).type(createAccountRequest.getType()).lastUpdateDate(System.currentTimeMillis()).build();
		return account;

	}

	private boolean checkIfType(String accountType, List<String> definedTypes) {

		accountType = accountType.toUpperCase();

		for (String type : definedTypes) {
			if (type.matches(accountType)) {
				return true;
			}
		}
		return false;
	}

	// or UUID.randomUUID().toString();
	private String generateRandomAccountNumber() {

		Random random = new Random();
		int randomNumber = random.nextInt(1000000000, 2000000000);
		return String.valueOf(randomNumber);
	}

	private boolean checkIfEnoughBalance(String accountNumber, double amount) {
		if (this.iRepository.getByAccountNumber(accountNumber).getBalance() > amount) {
			return true;
		}
		return false;
	}

	private boolean checkIfAccountExists(String accountNumber) {

		if (this.iRepository.getByAccountNumber(accountNumber) != null) {
			return true;
		}
		return false;
	}
}
