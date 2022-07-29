package com.msa.bankingsystem.services.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.msa.bankingsystem.dataAccess.user.IUserRepository;
import com.msa.bankingsystem.models.UserEntity;
import com.msa.bankingsystem.services.securityConfig.MyCustomUser;

@Service
public class UserEntityManager implements IUserEntityService {

	private IUserRepository iUserRepository;

	@Autowired
	public UserEntityManager(IUserRepository iUserRepository) {
		this.iUserRepository = iUserRepository;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserEntity userEntity = this.iUserRepository.loadUserByUsername(username);
		if (userEntity == null) {
			throw new UsernameNotFoundException(username);
		}
		String[] parsedAuthorities = userEntity.getAuthorities().split(",");
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (String auth : parsedAuthorities) {
			authorities.add(new SimpleGrantedAuthority(auth));
		}
		return new MyCustomUser(userEntity.getId(), userEntity.getUsername(), userEntity.getPassword(), authorities);
	}

}
