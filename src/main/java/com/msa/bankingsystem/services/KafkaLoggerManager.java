package com.msa.bankingsystem.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.core.file.FileReaders;
import com.msa.bankingsystem.core.log.MyLog4j;
import com.msa.bankingsystem.core.mapper.LogMapper;
import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.SuccessDataResult;
import com.msa.bankingsystem.services.dtos.GetListLogDto;

@Service
public class KafkaLoggerManager implements IKafkaLoggerService {

	@Value(value = "${kafka.topicName}")
	public String topicName;
	private MyLog4j myLog4j;
	private KafkaTemplate<String, String> kafkaTemplate;
	private FileReaders fileReaders;
	private LogMapper logMapper;

	@Autowired
	public KafkaLoggerManager(MyLog4j myLog4j, KafkaTemplate<String, String> kafkaTemplate, LogMapper logMapper,
			FileReaders fileReaders) {
		this.myLog4j = myLog4j;
		this.kafkaTemplate = kafkaTemplate;
		this.logMapper = logMapper;
		this.fileReaders = fileReaders;
	}

	@Override
	public void sendMessage(String message) {
		kafkaTemplate.send(topicName, message);
	}

	@KafkaListener(topics = "${kafka.topicName}", groupId = "group_id")
	private void listener(String message) {
		this.myLog4j.info(message);
	}

	@Override
	public DataResult<List<GetListLogDto>> getLogsByAccountNumber(String accountNumber) {
		
		List<GetListLogDto> dtos = readFile(accountNumber);
		if (dtos.isEmpty()) {
			return new SuccessDataResult<List<GetListLogDto>>(null, "No Record Found For This Account Number.");
		}
		return new SuccessDataResult<List<GetListLogDto>>(dtos, "Listed Logs for accountNumber: " + accountNumber);
	}

	private List<GetListLogDto> readFile(String accountNumber) {

		List<String> fileRead = this.fileReaders.readFile("log/info/appLog.log");
		List<GetListLogDto> dtos = new ArrayList<>();		
		for (String readLine : fileRead) {
			if (checkIfAccountNumberExists(readLine, accountNumber)) {
				dtos.add(this.logMapper.mapperToLogDto(readLine));
			}
		}
		return dtos;
	}

	private boolean checkIfAccountNumberExists(String line, String accountNumber) {
		
		if (accountNumber.length() == 10) {
			if (line.startsWith(accountNumber)) {
				return true;
			}
		}
		return false;
	}

}
