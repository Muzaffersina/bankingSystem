package com.msa.bankingsystem.core.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class FileReaders {

	public List<String> readFile(String filePath) {
		
		List<String> data = new ArrayList<>();

		try (FileReader fileReader = new FileReader(filePath);
				BufferedReader bufferedReader = new BufferedReader(fileReader)) {
			String line = bufferedReader.readLine();
			while (line != null) {
				data.add(line);
				line = bufferedReader.readLine();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return data;
	}

}
