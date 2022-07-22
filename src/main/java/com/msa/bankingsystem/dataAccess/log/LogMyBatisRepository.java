package com.msa.bankingsystem.dataAccess.log;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.core.mapper.LogMapper;
import com.msa.bankingsystem.models.Log;
import com.msa.bankingsystem.services.dtos.GetListLogDto;

@Service
public class LogMyBatisRepository implements ILogRepository {

	Reader reader;
	private LogMapper logMapper;

	@Autowired
	public LogMyBatisRepository(LogMapper logMapper) {
		this.logMapper = logMapper;
	}

	@Override
	public void save(String text) {

		String[] parsedText = text.split(" ");
		Log log = new Log();
		if (parsedText[1].contains("transfer")) {
			log.setSenderAccountNumber(parsedText[0]);
			log.setTransferredAccountNumber(parsedText[4].split(":")[1]);
			log.setOperationType(parsedText[1]);
			log.setCurrency(parsedText[3]);
			log.setAmount(Double.parseDouble(parsedText[2].split(":")[1]));
			log.setMessage(text);
		} else {
			log.setSenderAccountNumber(parsedText[0]);
			log.setOperationType(parsedText[1]);
			log.setTransferredAccountNumber(null);
			log.setCurrency(parsedText[3]);
			log.setAmount(Double.parseDouble(parsedText[2].split(":")[1]));
			log.setMessage(text);
		}

		SqlSession session = init(reader).openSession();
		session.insert("logMapper.save", log);
		session.commit();

	}

	@Override
	public List<GetListLogDto> getLogsByAccountNumber(String accountNumber) {

		SqlSession session = init(reader).openSession();
		List<Log> logs = new ArrayList<>();
		if (session != null) {
			logs = session.selectList("logMapper.getBySenderAccountNumber", accountNumber);
		}

		return this.logMapper.mapperToLogDto(logs);
	}

	private SqlSessionFactory init(Reader reader) {
		try {
			reader = Resources.getResourceAsReader("myBatis_conf.xml");
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);

			return sqlSessionFactory;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
