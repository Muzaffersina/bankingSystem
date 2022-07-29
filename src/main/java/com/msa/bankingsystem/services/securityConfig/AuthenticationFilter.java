package com.msa.bankingsystem.services.securityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.msa.bankingsystem.services.dtos.GetListLoginDto;
import com.msa.bankingsystem.services.requests.CreateLoginRequest;
import com.msa.bankingsystem.services.user.IUserEntityService;

@Component
public class AuthenticationFilter {

	private JWTTokenUtil jwtTokenUtil;
	private IUserEntityService userService;
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthenticationFilter(AuthenticationManager authenticationManager, JWTTokenUtil jwtTokenUtil,
			IUserEntityService userService) {
		this.authenticationManager = authenticationManager;
		this.userService = userService;
		this.jwtTokenUtil = jwtTokenUtil;
	}

	public GetListLoginDto login(CreateLoginRequest request) {
		GetListLoginDto resp = new GetListLoginDto();
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

			final UserDetails userDetails = this.userService.loadUserByUsername(request.getUsername());
			final String token = jwtTokenUtil.generateToken(userDetails);

			resp.setStatus("success");
			resp.setToken(token);

		} catch (BadCredentialsException e) {
			resp = null;
		} catch (DisabledException e) {
		}
		return resp;
	}
}
