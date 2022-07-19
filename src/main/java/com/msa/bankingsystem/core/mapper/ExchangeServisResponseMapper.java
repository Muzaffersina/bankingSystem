package com.msa.bankingsystem.core.mapper;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ExchangeServisResponseMapper {

	private boolean success;
	private ExchangeServisResponseResultMapper result;
}
