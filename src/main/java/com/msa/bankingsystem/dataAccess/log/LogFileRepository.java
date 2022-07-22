package com.msa.bankingsystem.dataAccess.log;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import com.msa.bankingsystem.core.file.FileReaders;
import com.msa.bankingsystem.core.mapper.LogMapper;
import com.msa.bankingsystem.services.dtos.GetListLogDto;

public class LogFileRepository implements ILogRepository {

	static Logger logger = Logger.getLogger(LogFileRepository.class);
	private FileReaders fileReaders;
	private LogMapper logMapper;

	public LogFileRepository(LogMapper logMapper, FileReaders fileReaders) {

		DOMConfigurator.configure("log4j.xml");
		this.logMapper = logMapper;
		this.fileReaders = fileReaders;
	}

	@Override
	public void save(String text) {
		logger.info(text);
	}

	@Override
	public List<GetListLogDto> getLogsByAccountNumber(String accountNumber) {
		List<GetListLogDto> dtos = readFile(accountNumber);
		return dtos;

	}

	private List<GetListLogDto> readFile(String accountNumber) {

		List<String> fileRead = this.fileReaders.readFile("log/info/appLog.log");
		List<GetListLogDto> dtos = new ArrayList<>();
		for (String readLine : fileRead) {
			if (readLine.startsWith(accountNumber)) {
				dtos.add(this.logMapper.mapperToLogDto(readLine));
			}
		}
		return dtos;
	}

}
