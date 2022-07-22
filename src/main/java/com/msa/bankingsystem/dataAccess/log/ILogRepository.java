package com.msa.bankingsystem.dataAccess.log;

import java.util.List;

import com.msa.bankingsystem.services.dtos.GetListLogDto;

public interface ILogRepository {
	void save(String text);
	
	List<GetListLogDto> getLogsByAccountNumber(String accountNumber);
}
