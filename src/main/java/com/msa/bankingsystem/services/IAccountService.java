package com.msa.bankingsystem.services;

import java.util.List;

import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.Result;
import com.msa.bankingsystem.models.Account;
import com.msa.bankingsystem.services.dtos.GetListLogDto;
import com.msa.bankingsystem.services.requests.CreateAccountRequest;
import com.msa.bankingsystem.services.requests.CreateDepositRequest;
import com.msa.bankingsystem.services.requests.CreateTransferRequest;

public interface IAccountService {

	Result create(CreateAccountRequest createAccountRequest);

	DataResult<Account> getByAccountNumber(String accountNumber);

	DataResult<Account> deposit(String accountNumber, CreateDepositRequest createDepositRequest);

	DataResult<Account> transferBetweenAccounts(String accountNumber, CreateTransferRequest createTransferRequest);

	DataResult<List<Account>> getAll();

	DataResult<List<GetListLogDto>> getAccountLogsByAccountNumber(String accountNumber);
}
