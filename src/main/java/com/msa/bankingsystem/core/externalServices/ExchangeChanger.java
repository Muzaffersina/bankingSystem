package com.msa.bankingsystem.core.externalServices;

public interface ExchangeChanger {
	double calculateExchange(String base, String to, double amount);
}
