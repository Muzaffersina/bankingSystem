package com.msa.bankingsystem.dataAccess.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.msa.bankingsystem.core.file.FileReaders;
import com.msa.bankingsystem.core.file.FileWriters;
import com.msa.bankingsystem.models.UserEntity;


public class UserFileRepository implements IUserRepository {

	@Value("${local.dbUsersFile}")
	private String filePath;

	private FileWriters fileWriters;
	private FileReaders fileReaders;

	@Autowired
	public UserFileRepository(FileWriters fileWriters, FileReaders fileReaders) {
		this.fileWriters = fileWriters;
		this.fileReaders = fileReaders;
	}

	@Override
	public UserEntity loadUserByUsername(String userName) {
		List<String> fileRead = this.fileReaders.readFile(filePath);
		for (String readLine : fileRead) {
			if (readLine.contains(userName)) {
				String[] userParsed = readLine.split(",");
				return new UserEntity(Integer.parseInt(userParsed[0]), userParsed[1], userParsed[2], userParsed[3]);
			}
		}
		return null;
	}
}
