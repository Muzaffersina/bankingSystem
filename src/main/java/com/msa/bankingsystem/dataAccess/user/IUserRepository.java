package com.msa.bankingsystem.dataAccess.user;

import com.msa.bankingsystem.models.UserEntity;

public interface IUserRepository {

	UserEntity loadUserByUsername(String username);
}
