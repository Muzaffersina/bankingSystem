package com.msa.bankingsystem.services.securityConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.msa.bankingsystem.services.user.IUserEntityService;

@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private AuthorizationFilter authorizationFilter;
	private IUserEntityService userService;

	@Autowired
	public WebSecurity(IUserEntityService userService, AuthorizationFilter authorizationFilter) {
		this.userService = userService;
		this.authorizationFilter = authorizationFilter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers("/api/auth").permitAll();
		http.authorizeRequests().antMatchers("/api/account/**", "/api/account/logs/**").hasAuthority("CREATE_ACCOUNT");
		http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/api/account").hasAuthority("REMOVE_ACCOUNT");
		http.authorizeRequests().antMatchers("/api/accounts").hasAuthority("ADMIN");
		http.authorizeRequests().anyRequest().authenticated();
		http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);
		http.formLogin().disable();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.userService);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

}
