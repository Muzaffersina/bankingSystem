package com.msa.bankingsystem.services.account;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.core.exception.BusinessException;
import com.msa.bankingsystem.core.externalServices.ExchangeChanger;
import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.Result;
import com.msa.bankingsystem.core.results.SuccessDataResult;
import com.msa.bankingsystem.core.results.SuccessResult;
import com.msa.bankingsystem.dataAccess.account.IAccountRepository;
import com.msa.bankingsystem.models.Account;
import com.msa.bankingsystem.services.dtos.GetListLogDto;
import com.msa.bankingsystem.services.log.IKafkaLoggerService;
import com.msa.bankingsystem.services.message.Messages;
import com.msa.bankingsystem.services.requests.CreateAccountRequest;
import com.msa.bankingsystem.services.requests.CreateDepositRequest;
import com.msa.bankingsystem.services.requests.CreateTransferRequest;
import com.msa.bankingsystem.services.securityConfig.MyCustomUser;

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

		checkIfType(createAccountRequest.getType(), this.definedTypes);
		MyCustomUser authUser = (MyCustomUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Account account = manuelAccountMapper(createAccountRequest);

		account.setDeleted(false);
		account.setLastUpdateDate(System.currentTimeMillis());
		account.setBalance(0);
		account.setUserId(authUser.getId());
		this.iAccountRepository.save(account);
		return new SuccessResult(Messages.CREATEACCOUNT + account.getAccountNumber());
	}

	@Override
	public DataResult<Account> delete(String accountNumber) {

		checkIfAccountExists(accountNumber);

		Account account = this.iAccountRepository.delete(accountNumber);

		return new SuccessDataResult<Account>(account, Messages.DELETEACCOUNTBYID);
	}

	@Override
	public DataResult<Account> getByAccountNumber(String accountNumber) {

		checkIfAccountExists(accountNumber);

		Account account = this.iAccountRepository.getByAccountNumber(accountNumber);

		return new SuccessDataResult<Account>(account, Messages.LISTEDACCOUNTBYACCOUNTNO);
	}

	@Override
	public DataResult<Account> deposit(String accountNumber, CreateDepositRequest createDepositRequest) {

		checkIfAccountExists(accountNumber);
		Account account = this.iAccountRepository.update(accountNumber, createDepositRequest.getAmount());

		String message = accountNumber + " deposit amount:" + createDepositRequest.getAmount() + " "
				+ account.getType();
		kafkaTemplate.send(topicName, message);
		return new SuccessDataResult<Account>(account, Messages.SUCCESSDEPOSİTOPERATİON);
	}

	@Override
	public DataResult<Account> transferBetweenAccounts(String senderAccountNumber,
			CreateTransferRequest createTransferRequest) {

		checkIfAccountExists(senderAccountNumber);
		checkIfAccountExists(createTransferRequest.getTransferredAccountNumber());

		checkIfEnoughBalance(senderAccountNumber, createTransferRequest.getAmount());

		double exchangeAmount = checkExchangeAmount(senderAccountNumber,
				createTransferRequest.getTransferredAccountNumber(), createTransferRequest.getAmount());

		Account account = this.iAccountRepository.transferBetweenAccounts(senderAccountNumber,
				createTransferRequest.getTransferredAccountNumber(), createTransferRequest.getAmount(), exchangeAmount);

		String message = senderAccountNumber + " transfer amount:" + createTransferRequest.getAmount() + " "
				+ account.getType() + " transferred_account:" + createTransferRequest.getTransferredAccountNumber();
		kafkaTemplate.send(topicName, message);
		return new SuccessDataResult<Account>(account, message);

	}

	@Override
	public DataResult<List<Account>> getAll() {
		return new SuccessDataResult<List<Account>>(this.iAccountRepository.getAll(), Messages.LISTEDALLACCOUNTS);
	}

	@Override
	public DataResult<List<GetListLogDto>> getAccountLogsByAccountNumber(String accountNumber) {

		checkIfAccountExists(accountNumber);
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
		throw new BusinessException(Messages.INVALIDACCOUNTTYPE + accountType);
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
		throw new BusinessException(Messages.INSUFFICIENTBALANCE);
	}

	private boolean checkIfAccountExists(String accountNumber) {

		if (this.iAccountRepository.getByAccountNumber(accountNumber) != null) {
			return true;
		}
		throw new BusinessException(Messages.NOTFOUNDACCOUNTNUMBER);
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
