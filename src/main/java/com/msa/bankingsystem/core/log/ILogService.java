package com.msa.bankingsystem.core.log;



public interface ILogService {
	void info(String text);
	void warn(String text);
	void error(String text);
}
