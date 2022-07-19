package com.msa.bankingsystem.core.log;


import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.stereotype.Service;

@Service
public class MyLog4j implements ILogService {

	static Logger logger = Logger.getLogger(MyLog4j.class);

	public MyLog4j() {

		DOMConfigurator.configure("log4j.xml");
	}

	@Override
	public void info(String text) {
		logger.info(text);
	}

	@Override
	public void warn(String text) {
		logger.warn(text);
	}

	@Override
	public void error(String text) {
		logger.error(text);

	}

}
