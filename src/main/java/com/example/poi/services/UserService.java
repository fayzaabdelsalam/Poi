package com.example.poi.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.poi.entities.UserEntity;
import com.example.poi.repositories.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

		public String writeData() throws Exception
		{
			List<UserEntity> userEntity = userRepository.findAll();
	
				try (XSSFWorkbook workbook = new XSSFWorkbook()) {
					XSSFSheet spreadsheet = workbook.createSheet("kyc db");
	
					XSSFRow row = spreadsheet.createRow(1);
					XSSFCell cell;
					cell = row.createCell(1);
					cell.setCellValue("ID");
					cell = row.createCell(2);
					cell.setCellValue("Email");
					cell = row.createCell(3);
					cell.setCellValue("Password");

					int i = 2;
					
					for (UserEntity u : userEntity) {
						row = spreadsheet.createRow(i);
						cell = row.createCell(1);
						cell.setCellValue(u.getId());
						cell = row.createCell(2);
						cell.setCellValue(u.getEmail());
						cell = row.createCell(3);
						cell.setCellValue(u.getPassword());
						i++;
					}
	
					FileOutputStream out = new FileOutputStream(new File("dataout.xlsx"));
					workbook.write(out);
					out.close();
				}
				
			return ("dataout.xlsx written successfully");
		
	}

	public void readData() throws IOException {
		FileInputStream file = new FileInputStream(new File("data.xlsx"));
		XSSFWorkbook workbook = new XSSFWorkbook(file);
		XSSFSheet sheet = workbook.getSheetAt(0);
		int rows = sheet.getLastRowNum();
		for (int r = 1; r <= rows; r++) {
			XSSFRow row = sheet.getRow(r);
			UserEntity userEntity = new UserEntity();
			userEntity.setId(Integer.valueOf((int) row.getCell(0).getNumericCellValue()));
			userEntity.setEmail(row.getCell(1).getStringCellValue());
			userEntity.setPassword(String.valueOf(row.getCell(2).getNumericCellValue()));
			userRepository.save(userEntity);
		}
		workbook.close();
		file.close();
	}
}