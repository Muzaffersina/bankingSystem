package com.msa.bankingsystem.services;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.core.externalServices.ExchangeChanger;
import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.ErrorDataResult;
import com.msa.bankingsystem.core.results.ErrorResult;
import com.msa.bankingsystem.core.results.Result;
import com.msa.bankingsystem.core.results.SuccessDataResult;
import com.msa.bankingsystem.core.results.SuccessResult;
import com.msa.bankingsystem.dataAccess.account.IAccountRepository;
import com.msa.bankingsystem.models.Account;
import com.msa.bankingsystem.services.dtos.GetListLogDto;
import com.msa.bankingsystem.services.log.IKafkaLoggerService;
import com.msa.bankingsystem.services.requests.CreateAccountRequest;
import com.msa.bankingsystem.services.requests.CreateDepositRequest;
import com.msa.bankingsystem.services.requests.CreateTransferRequest;

@Service
public class AccountManager implements IAccountService {

	@Value("${account.type}")
	private List<String> definedTypes;
	@Value(value = "${kafka.topicName}")
	public String topicName;

	private IAccountRepository iAccountRepository;
	private IKafkaLoggerService iKafkaLoggerService;
	private KafkaTemplate<String, String> kafkaTemplate;
	private ExchangeChanger exchangeChanger;

	@Autowired
	public AccountManager(List<String> definedTypes, IAccountRepository iAccountRepository,
			IKafkaLoggerService iKafkaLoggerService, KafkaTemplate<String, String> kafkaTemplate,
			ExchangeChanger exchangeChanger) {
		this.definedTypes = definedTypes;
		this.iAccountRepository = iAccountRepository;
		this.kafkaTemplate = kafkaTemplate;
		this.iKafkaLoggerService = iKafkaLoggerService;
		this.exchangeChanger = exchangeChanger;
	}

	@Override
	public Result create(CreateAccountRequest createAccountRequest) {

		if (!checkIfType(createAccountRequest.getType(), this.definedTypes)) {
			return new ErrorResult("Invalid Account Type: " + createAccountRequest.getType());
		}

		Account account = manuelAccountMapper(createAccountRequest);
		account.setDeleted(false);
		account.setLastUpdateDate(System.currentTimeMillis());
		account.setBalance(0);
		this.iAccountRepository.save(account);
		return new SuccessResult("Created Account , Account Number =" + account.getAccountNumber());
	}

	@Override
	public DataResult<Account> delete(String accountNumber) {

		if (!checkIfAccountExists(accountNumber)) {
			return new ErrorDataResult<Account>("This account id cannot be found");
		}
		Account account = this.iAccountRepository.delete(accountNumber);
		return new SuccessDataResult<Account>(account, "Deleted Account By AccountId ");
	}

	@Override
	public DataResult<Account> getByAccountNumber(String accountNumber) {

		Account account = this.iAccountRepository.getByAccountNumber(accountNumber);

		if (account == null) {
			return new ErrorDataResult<Account>("This account number cannot be found");
		}

		return new SuccessDataResult<Account>(account, "Listed Account By AccountNumber");
	}

	@Override
	public DataResult<Account> deposit(String accountNumber, CreateDepositRequest createDepositRequest) {

		if (!checkIfAccountExists(accountNumber)) {
			return new ErrorDataResult<Account>(null, "Please Check Your Account Information");
		}
		Account account = this.iAccountRepository.update(accountNumber, createDepositRequest.getAmount());

		String message = accountNumber + " deposit amount:" + createDepositRequest.getAmount() + " "
				+ account.getType();
		kafkaTemplate.send(topicName, message);
		return new SuccessDataResult<Account>(account, "Deposit Operation Successful");
	}

	@Override
	public DataResult<Account> transferBetweenAccounts(String senderAccountNumber,
			CreateTransferRequest createTransferRequest) {

		if (!checkIfAccountExists(senderAccountNumber)
				|| !checkIfAccountExists(createTransferRequest.getTransferredAccountNumber())) {
			return new ErrorDataResult<Account>("Please Check Your Accounts Information");
		} else {
			if (!checkIfEnoughBalance(senderAccountNumber, createTransferRequest.getAmount())) {
				return new ErrorDataResult<Account>("Insufficient balance");
			} else {

				double exchangeAmount = checkExchangeAmount(senderAccountNumber,
						createTransferRequest.getTransferredAccountNumber(), createTransferRequest.getAmount());

				Account account = this.iAccountRepository.transferBetweenAccounts(senderAccountNumber,
						createTransferRequest.getTransferredAccountNumber(), createTransferRequest.getAmount(),
						exchangeAmount);

				String message = senderAccountNumber + " transfer amount:" + createTransferRequest.getAmount() + " "
						+ account.getType() + " transferred_account:"
						+ createTransferRequest.getTransferredAccountNumber();
				kafkaTemplate.send(topicName, message);
				return new SuccessDataResult<Account>(account, message);
			}
		}
	}

	@Override
	public DataResult<List<Account>> getAll() {
		return new SuccessDataResult<List<Account>>(this.iAccountRepository.getAll(), "Listed Accounts");
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
				.type(createAccountRequest.getType().toUpperCase()).build();
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
		if (this.iAccountRepository.getByAccountNumber(accountNumber).getBalance() > amount) {
			return true;
		}
		return false;
	}

	private boolean checkIfAccountExists(String accountNumber) {

		if (this.iAccountRepository.getByAccountNumber(accountNumber) != null) {
			return true;
		}
		return false;
	}

	private double checkExchangeAmount(String senderAccountNumber, String transferredAccountNumber, double amount) {

		Account senderAccount = this.iAccountRepository.getByAccountNumber(senderAccountNumber);
		Account transferredAccount = this.iAccountRepository.getByAccountNumber(transferredAccountNumber);
		if (senderAccount.getType().contains(transferredAccount.getType())) {
			return amount;
		}
		return this.exchangeChanger.calculateExchange(senderAccount.getType(), transferredAccount.getType(), amount);
	}

}
