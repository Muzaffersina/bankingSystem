package com.msa.bankingsystem.api.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.bankingsystem.core.mapper.AccountDtoMapper;
import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.Result;
import com.msa.bankingsystem.models.Account;
import com.msa.bankingsystem.services.IAccountService;
import com.msa.bankingsystem.services.dtos.GetListAccountDto;
import com.msa.bankingsystem.services.dtos.GetListLogDto;
import com.msa.bankingsystem.services.requests.CreateAccountRequest;
import com.msa.bankingsystem.services.requests.CreateDepositRequest;
import com.msa.bankingsystem.services.requests.CreateTransferRequest;

@RestController
@RequestMapping(path = "/api")
public class AccountsController {

	private IAccountService iAccountService;
	private AccountDtoMapper accountDtoMapper;

	@Autowired
	public AccountsController(IAccountService iAccountService, AccountDtoMapper accountDtoMapper) {
		this.iAccountService = iAccountService;
		this.accountDtoMapper = accountDtoMapper;
	}

	@PostMapping(path = "/account")
	private ResponseEntity<Result> createAccount(@RequestBody CreateAccountRequest createAccountRequest) {

		Result result = this.iAccountService.create(createAccountRequest);

		if (result.isSuccess()) {
			return new ResponseEntity<>(result, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
	}

	@GetMapping(path = "/account/{accountNumber}")
	private ResponseEntity<DataResult<GetListAccountDto>> getAccountByAccountId(
			@PathVariable(name = "accountNumber") String accountNumber) {

		DataResult<Account> account = this.iAccountService.getByAccountNumber(accountNumber);
		if (!account.isSuccess() || account == null) {
			return new ResponseEntity<>((DataResult) account, HttpStatus.NOT_FOUND);
		}
		DataResult<GetListAccountDto> dataResult = new DataResult<GetListAccountDto>(
				this.accountDtoMapper.accountToAccountDto(account.getData()), account.isSuccess(),
				account.getMessage());

		return ResponseEntity.ok().body(dataResult);

	}

	@PutMapping(path = "/account/{accountNumber}")
	private ResponseEntity<DataResult<GetListAccountDto>> deposit(
			@PathVariable(name = "accountNumber") String accountNumber,
			@RequestBody CreateDepositRequest createDepositRequest) {

		DataResult<Account> account = this.iAccountService.deposit(accountNumber, createDepositRequest);

		if (!account.isSuccess() || account == null) {
			return new ResponseEntity<>((DataResult) account, HttpStatus.NOT_FOUND);
		}

		DataResult<GetListAccountDto> dataResult = new DataResult<GetListAccountDto>(
				this.accountDtoMapper.accountToAccountDto(account.getData()), account.isSuccess(),
				account.getMessage());

		return ResponseEntity.ok().lastModified(account.getData().getLastUpdateDate()).body(dataResult);

	}

	@PutMapping(path = "/account/transfer/{senderAccountNumber}")
	private ResponseEntity<DataResult<GetListAccountDto>> transferBetweenAccounts(
			@PathVariable(name = "senderAccountNumber") String senderAccountNumber,
			@RequestBody CreateTransferRequest createTransferRequest) {

		DataResult<Account> account = this.iAccountService.transferBetweenAccounts(senderAccountNumber,
				createTransferRequest);

		if (!account.isSuccess() || account == null) {
			return new ResponseEntity<>((DataResult) account, HttpStatus.BAD_REQUEST);
		}

		DataResult<GetListAccountDto> dataResult = new DataResult<GetListAccountDto>(
				this.accountDtoMapper.accountToAccountDto(account.getData()), account.isSuccess(),
				account.getMessage());

		return ResponseEntity.ok().lastModified(account.getData().getLastUpdateDate()).body(dataResult);

	}

	@DeleteMapping("/account/{accountNumber}")
	public ResponseEntity<Result> delete(@PathVariable String accountNumber) {

		DataResult<Account> account = this.iAccountService.delete(accountNumber);
		Result result = new Result(account.isSuccess(), account.getMessage());
		if (!account.isSuccess() || account == null) {
			return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);

		}
		return ResponseEntity.ok().lastModified(account.getData().getLastUpdateDate()).body(result);
	}

	@GetMapping(path = "/accounts")
	private ResponseEntity<DataResult<List<GetListAccountDto>>> getAllAccounts() {

		DataResult<List<Account>> account = this.iAccountService.getAll();

		DataResult<List<GetListAccountDto>> dataResult = new DataResult<List<GetListAccountDto>>(
				this.accountDtoMapper.listAccountToListAccountDto(account.getData()), account.isSuccess(),
				account.getMessage());

		return ResponseEntity.ok().body(dataResult);
	}

	@CrossOrigin(origins = { "http://localhost:6162" })
	@GetMapping(path = "/account/logs/{accountNumber}")
	private ResponseEntity<DataResult<List<GetListLogDto>>> getLogs(
			@PathVariable(name = "accountNumber") String accountNumber) {

		DataResult<List<GetListLogDto>> dataResult = this.iAccountService.getAccountLogsByAccountNumber(accountNumber);

		if (dataResult.isSuccess()) {
			return ResponseEntity.ok().body(dataResult);
		}
		return new ResponseEntity<>(dataResult, HttpStatus.NOT_FOUND);
	}

}
