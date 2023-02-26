package com.example.poi.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.poi.entities.UserEntity;
import com.example.poi.repositories.UserRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class UserService {

	@Autowired
	private UserRepository userRepository;
	
	static final String OUTPUTFILE = "dataout.xlsx";
	static final String INPUTFILE = "data.xlsx";
	static final String SCHEMA = "kyc db";
	static final String[] COLUMNS = {"ID","Email","Password"};
	
	public void writeData() 
	{
				try (XSSFWorkbook workbook = new XSSFWorkbook()) 
				{
					XSSFSheet spreadsheet = workbook.createSheet(SCHEMA);
					XSSFRow row = spreadsheet.createRow(0);
					setColumnNames(row);
					copyEntity(spreadsheet);
					writeFile(workbook);
				}
				catch (IOException e) {	e.printStackTrace();	}	
	}
	
	public void writeFile(XSSFWorkbook workbook)
	{
		try 
		{
		FileOutputStream out = new FileOutputStream(new File(OUTPUTFILE));
		workbook.write(out);
		out.close();
		}
		catch (final Exception e) { log.error("***** :: Exception :: ***** {}", e.getMessage()); }
	}
	
	public void setColumnNames(XSSFRow row)
	{	
		XSSFCell cell;
		for (int i = 0; i < COLUMNS.length; i++) {
			cell = row.createCell(i+1);
			cell.setCellValue(COLUMNS[i]);
		}
	}
	
	public void copyEntity(XSSFSheet spreadsheet)
	{
		List<UserEntity> userEntity = userRepository.findAll();
		int rowNo = 1;
		Map<UserEntity,XSSFRow> map = new HashMap<>();
		for (UserEntity u : userEntity) {
			XSSFRow row = spreadsheet.createRow(rowNo);
			map.put(u, row);
			rowNo++;
		}
		mapEntityToCopy(map);
	}
	
	public void mapEntityToCopy(Map<UserEntity,XSSFRow> map)
	{
		for (Map.Entry<UserEntity, XSSFRow> entry : map.entrySet()) { //4
			UserEntity key = entry.getKey();
			XSSFRow val = entry.getValue();
			XSSFCell cell = val.createCell(1);
			cell.setCellValue(key.getId());
			cell = val.createCell(2);
			cell.setCellValue(key.getEmail());
			cell = val.createCell(3);
			cell.setCellValue(key.getPassword());
		}
	}
	
	public void saveEntity(XSSFSheet sheet)
	{
		int rows = sheet.getLastRowNum();
		Map<XSSFRow,UserEntity> map = new HashMap<>();
		for (int r = 1; r <= rows; r++)
		{
			XSSFRow row = sheet.getRow(r);	
			map.put(row, new UserEntity());
		}
		mapEntityToSave(map);
	}
	
	public void mapEntityToSave(Map<XSSFRow,UserEntity> map)
	{
		for (Map.Entry<XSSFRow, UserEntity> entry : map.entrySet()) {
			XSSFRow key = entry.getKey();
			UserEntity val = entry.getValue();
			val.setId(Integer.valueOf((int)key.getCell(0).getNumericCellValue()));
			val.setEmail(key.getCell(1).getStringCellValue());
			val.setPassword(String.valueOf(key.getCell(2).getNumericCellValue()));
			userRepository.save(val);
		}
	}
	
	public void readData() 
	{
		try {
				try (FileInputStream file = new FileInputStream(new File(INPUTFILE))) 
				{
					XSSFWorkbook workbook = new XSSFWorkbook(file);
					XSSFSheet sheet = workbook.getSheetAt(0);
					saveEntity(sheet);
					workbook.close();
				}
			} 	catch (final Exception e) { log.error("***** :: Exception :: ***** {}", e.getMessage()); }
	}
}