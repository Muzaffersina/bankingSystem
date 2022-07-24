package com.msa.bankingsystem.services.log;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.core.results.DataResult;
import com.msa.bankingsystem.core.results.SuccessDataResult;
import com.msa.bankingsystem.dataAccess.log.ILogRepository;
import com.msa.bankingsystem.services.dtos.GetListLogDto;
import com.msa.bankingsystem.services.message.Messages;

@Service
public class KafkaFileLoggerManager implements IKafkaLoggerService {

	@Value(value = "${kafka.topicName}")
	public String topicName;
	private ILogRepository iLogRepository;

	@Autowired
	public KafkaFileLoggerManager(ILogRepository iLogRepository) {
		this.iLogRepository = iLogRepository;

	}

	@Override
	@KafkaListener(topics = "${kafka.topicName}", groupId = "group_id")
	public void kafkaListener(String message) {
		this.iLogRepository.save(message);
	}

	@Override
	public DataResult<List<GetListLogDto>> getLogsByAccountNumber(String accountNumber) {
		List<GetListLogDto> dtos = this.iLogRepository.getLogsByAccountNumber(accountNumber);
		if (dtos.isEmpty()) {
			return new SuccessDataResult<List<GetListLogDto>>(null, Messages.NOTFOUNDLOGDETAILSBYACCOUNTNUMBER);
		}
		return new SuccessDataResult<List<GetListLogDto>>(dtos, Messages.LISTEDLOGSBYACCOUNTNUMBER + accountNumber);

	}
}
