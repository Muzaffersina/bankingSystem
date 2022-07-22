package com.msa.bankingsystem.core.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.msa.bankingsystem.models.Log;
import com.msa.bankingsystem.services.dtos.GetListLogDto;

@Component
public class LogMapper {

	public GetListLogDto mapperToLogDto(String text) {

		List<String> logItem = new ArrayList<>();
		for (String parsedText : text.split(" ")) {
			for (String parsedText2 : parsedText.split(":")) {
				logItem.add(parsedText2);
			}
		}

		if (logItem.size() == 5) {
			return new GetListLogDto(
					logItem.get(0) + " nolu hesaba " + logItem.get(3) + " " + logItem.get(4) + " yatırılmıştır.");
		}
		return new GetListLogDto(logItem.get(0) + " hesaptan " + logItem.get(6) + " nolu hesaba " + logItem.get(3) + " "
				+ logItem.get(4) + " transfer edilmiştir.");
	}

	public List<GetListLogDto> mapperToLogDto(List<Log> logs) {

		List<GetListLogDto> dtos = new ArrayList<>();
		for (Log log : logs) {
			GetListLogDto dto = new GetListLogDto();			
			if (log.getTransferredAccountNumber() == null) {				
				dto.setLog(log.getSenderAccountNumber() + " nolu hesaba " + log.getAmount() + " " + log.getCurrency()
						+ " yatırılmıştır.");

			} else {				
				dto.setLog(log.getSenderAccountNumber() + " hesaptan " + log.getTransferredAccountNumber()
						+ " nolu hesaba " + log.getAmount() + " " + log.getCurrency() + " transfer edilmiştir.");

			}
			dtos.add(dto);
		}
		return dtos;
	}
}
