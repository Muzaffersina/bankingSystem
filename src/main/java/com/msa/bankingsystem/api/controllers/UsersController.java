package com.msa.bankingsystem.api.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msa.bankingsystem.services.dtos.GetListLoginDto;
import com.msa.bankingsystem.services.requests.CreateLoginRequest;
import com.msa.bankingsystem.services.securityConfig.AuthenticationFilter;

@RestController
@RequestMapping()
public class UsersController {

	private AuthenticationFilter authenticationFilter;

	@Autowired
	public UsersController(AuthenticationFilter authenticationFilter) {
		this.authenticationFilter = authenticationFilter;
	}

	@PostMapping("/api/auth")
	public ResponseEntity<GetListLoginDto> login(@RequestBody @Valid CreateLoginRequest request) {
		GetListLoginDto dto = this.authenticationFilter.login(request);
		if (dto == null) {

			return new ResponseEntity<GetListLoginDto>(new GetListLoginDto("Bad Credentials", null),
					HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().body(dto);
	}
}
