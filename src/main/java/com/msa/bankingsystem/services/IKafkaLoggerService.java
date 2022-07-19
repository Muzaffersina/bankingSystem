package com.msa.bankingsystem.services;

import java.util.List;

import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.services.dtos.GetListLogDto;

public interface IKafkaLoggerService {

	DataResult<List<GetListLogDto>> getLogsByAccountNumber(String accountNumber);

	void sendMessage(String message);

}
