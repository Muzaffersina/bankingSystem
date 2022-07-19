package com.msa.bankingsystem.core.externalServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.msa.bankingsystem.core.mapper.ExchangeServisResponseMapper;

@Component
public class ExchangeServis implements ExchangeChanger {

	private RestTemplate restTemplate;

	@Value(value = "${exchangeServis.contentType}")
	private String contentType;
	@Value(value = "${exchangeServis.authorization}")
	private String authorization;

	@Autowired
	public ExchangeServis(RestTemplate restTemplate) {

		this.restTemplate = restTemplate;
	}
	
	@Override
	public double calculateExchange(String base, String to, double amount) {

		HttpHeaders headers = new HttpHeaders();
		// Set header
		headers.set("content-type", contentType);
		headers.set("authorization", authorization);

		HttpEntity<String> entity = new HttpEntity<>(headers);

		// Response Type is string
		ResponseEntity<ExchangeServisResponseMapper> response = restTemplate.exchange(
				"https://api.collectapi.com/economy/exchange?int=" + amount + "&to=" + to + "&base=" + base,
				HttpMethod.GET, entity, ExchangeServisResponseMapper.class);

		return response.getBody().getResult().getData()[0].getCalculated();
	}
}
