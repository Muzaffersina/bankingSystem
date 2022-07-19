package com.msa.bankingsystem.core.mapper;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class ExchangeServisResponseDataMapper {

	private String code;
	private String name;
	private String rate;
	private String calculatedstr;
	private double calculated;
}
