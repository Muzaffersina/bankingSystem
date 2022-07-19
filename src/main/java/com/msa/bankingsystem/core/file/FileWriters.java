package com.msa.bankingsystem.core.file;

import java.io.*;

import org.springframework.stereotype.Component;

@Component
public class FileWriters {

	public boolean writeToFile(String filePath, String text, boolean status) {
		try (FileWriter fileWriter = new FileWriter(filePath, status);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
			File file = new File(filePath);
			if (!file.exists())
				file.createNewFile();

			bufferedWriter.write(text);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}
