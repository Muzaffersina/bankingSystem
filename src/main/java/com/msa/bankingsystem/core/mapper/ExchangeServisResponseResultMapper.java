package com.msa.bankingsystem.core.mapper;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ExchangeServisResponseResultMapper {

	private String base;
	private String lastupdate;
	private ExchangeServisResponseDataMapper[] data;
	

}
