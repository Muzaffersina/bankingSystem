package com.msa.bankingsystem.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
	private int id;
	private int userId;     ////
	private String accountNumber;
	private String name;
	private String surname;
	private String email;
	private String idendityNumber;
	private String type;
	private double balance;
	private long lastUpdateDate;
	private boolean isDeleted;
}
