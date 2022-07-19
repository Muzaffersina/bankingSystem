package com.msa.bankingsystem.core.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

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
		return new GetListLogDto(
				logItem.get(0) + " hesaptan " + logItem.get(6) + " nolu hesaba " + logItem.get(3) +" "+ logItem.get(4)+" transfer edilmiştir.");
	}
}
