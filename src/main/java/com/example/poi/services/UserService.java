package com.example.poi.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.poi.entities.UserEntity;
import com.example.poi.repositories.UserRepository;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@Getter
@Setter
public class UserService {

	@Autowired
	private UserRepository userRepository;

	static final String OUTPUTFILE = "dataout.xlsx";
	static final String INPUTFILE = "data.xlsx";
	static final String PDFFILE = "ConvertedPDF.pdf";
	static final String SCHEMA = "kyc db";
	static final String[] COLUMNS = { "ID", "Email", "Password" };
	static final String EXCEPTION = "***** :: Exception :: ***** {}";

	private void setColumnNames(XSSFRow row) {
		XSSFCell cell;
		for (int i = 0; i < COLUMNS.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(COLUMNS[i]);
		}
	}

	private void write(XSSFWorkbook workbook, XSSFSheet spreadsheet)
	{
		XSSFRow row = spreadsheet.createRow(0);
		setColumnNames(row);
		copyEntity(spreadsheet);
		writeFile(workbook);
	}
	
	public void writeData() {
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet spreadsheet = workbook.createSheet(SCHEMA);
			write(workbook,spreadsheet);
			pdfConverter(workbook);
		} catch (final Exception e) {
			log.error(EXCEPTION, e.getMessage());
		}
	}

	private void writeFile(XSSFWorkbook workbook) {
		try {
			FileOutputStream out = new FileOutputStream(new File(OUTPUTFILE));
			workbook.write(out);
			out.close();
		} catch (final Exception e) {
			log.error(EXCEPTION, e.getMessage());
		}
	}

	private void pdfConverter(XSSFWorkbook workbook) {
		XSSFSheet sheet = workbook.getSheetAt(0);
		Document document = new Document();
		try {
			PdfWriter.getInstance(document, new FileOutputStream(PDFFILE));
			document.open();
			createPdf(sheet,document);
			document.close();
		} catch (final Exception e) {
			log.error(EXCEPTION, e.getMessage());
		}
	}
	
	private void createPdf(XSSFSheet sheet, Document document)
	{
		List<String> headerList = getRow(0, sheet);
		PdfPTable table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
		addPDFData(true, headerList, table);
		setPdfTable(sheet, document, table);
	}

	private void setPdfTable(XSSFSheet sheet, Document document, PdfPTable table) {
		int rows = sheet.getLastRowNum();
		for (int i = 1; i <= rows; i++) {
			List<String> rowList = getRow(i, sheet);
			addPDFData(false, rowList, table);
			try {
				document.add(table);
			} catch (final Exception e) {
				log.error(EXCEPTION, e.getMessage());
			}
			table = new PdfPTable(sheet.getRow(0).getPhysicalNumberOfCells());
		}
	}

	private List<String> getRow(int index, XSSFSheet sheet) {
		List<String> list = new ArrayList<>();

		for (Cell cell : sheet.getRow(index)) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				list.add(cell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				list.add(String.valueOf(cell.getNumericCellValue()));
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				list.add(String.valueOf(cell.getBooleanCellValue()));
				break;
			case Cell.CELL_TYPE_FORMULA:
				list.add(cell.getCellFormula());
				break;
			default:
			}
		}

		return list;
	}

	private void addPDFData(boolean isHeader, List<String> headerList, PdfPTable table) {
		headerList.stream().forEach(column -> {
			PdfPCell header = new PdfPCell();
			if (isHeader) {
				header.setBackgroundColor(BaseColor.LIGHT_GRAY);
				header.setBorderWidth(2);
			}
			header.setPhrase(new Phrase(column));
			table.addCell(header);
		});
	}

	private void copyEntity(XSSFSheet spreadsheet) {
		List<UserEntity> userEntity = userRepository.findAll();
		int rowNo = 1;
		Map<UserEntity, XSSFRow> map = new HashMap<>();
		for (UserEntity u : userEntity) {
			XSSFRow row = spreadsheet.createRow(rowNo);
			map.put(u, row);
			rowNo++;
		}
		mapEntityToCopy(map);
	}

	private void mapEntityToCopy(Map<UserEntity, XSSFRow> map) {
		for (Map.Entry<UserEntity, XSSFRow> entry : map.entrySet()) {
			UserEntity key = entry.getKey();
			XSSFRow val = entry.getValue();
			copyCells(key,val);
		}
	}
	
	private void copyCells(UserEntity key, XSSFRow val)
	{
		XSSFCell cell = val.createCell(0);
		cell.setCellValue(key.getId());
		cell = val.createCell(1);
		cell.setCellValue(key.getEmail());
		cell = val.createCell(2);
		cell.setCellValue(key.getPassword());
	}

	private void saveEntity(XSSFSheet sheet) {
		int rows = sheet.getLastRowNum();
		Map<XSSFRow, UserEntity> map = new HashMap<>();
		for (int r = 1; r <= rows; r++) {
			XSSFRow row = sheet.getRow(r);
			map.put(row, new UserEntity());
		}
		mapEntityToSave(map);
	}

	private void mapEntityToSave(Map<XSSFRow, UserEntity> map) {
		for (Map.Entry<XSSFRow, UserEntity> entry : map.entrySet()) {
			XSSFRow key = entry.getKey();
			UserEntity val = entry.getValue();
			saveCells(key,val);
			userRepository.save(val);
		}
	}

	private void saveCells(XSSFRow key, UserEntity val)
	{
		val.setId(Integer.valueOf((int) key.getCell(0).getNumericCellValue()));
		val.setEmail(key.getCell(1).getStringCellValue());
		val.setPassword(String.valueOf(key.getCell(2).getNumericCellValue()));
	}
	
	public void readData() {
		try {
			try (FileInputStream file = new FileInputStream(new File(INPUTFILE))) {
				XSSFWorkbook workbook = new XSSFWorkbook(file);
				XSSFSheet sheet = workbook.getSheetAt(0);
				saveEntity(sheet);
				workbook.close();
			}
		} catch (final Exception e) {
			log.error(EXCEPTION, e.getMessage());
		}
	}

	public ByteArrayOutputStream download() {
		try (XSSFWorkbook workbook = new XSSFWorkbook()) {
			XSSFSheet spreadsheet = workbook.createSheet(SCHEMA);
			write(workbook,spreadsheet);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			workbook.write(stream);
			return stream;
		} catch (final Exception e) {
			log.error(EXCEPTION, e.getMessage());
			return null;
		}
	}

}