package com.thiruacademy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.thiruacademy.entity.Student;
import com.thiruacademy.repository.StudentRepository;

@Service
public class StudentService {
	@Autowired
	private StudentRepository studentRepository;

	public void save(MultipartFile file) throws EncryptedDocumentException, IOException {
		List<List<String>> rows = new ArrayList<>();

		Workbook workbook = WorkbookFactory.create(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(1);
		// Iterator<Row> rowIterator = sheet.iterator();
		rows = StreamSupport.stream(sheet.spliterator(), false)
				.map(row -> StreamSupport
				.stream(row.spliterator(), false)
				.map(this::getCellStringValue)
				.collect(Collectors.toList()))
				.collect(Collectors.toList());
		System.out.println("rows :: " + rows);
		// Save data to the database
		List<Student> excelDataList = rows.stream().map(row -> {
			Student excelData = new Student();
			excelData.setName(row.get(0));
			excelData.setAge(row.get(1));
			excelData.setEmail(row.get(2));
			excelData.setCollege(row.get(3));
			return excelData;
		}).collect(Collectors.toList());
		studentRepository.saveAll(excelDataList);
	}
	private String getCellStringValue(Cell cell) {
		CellType cellType = cell.getCellType();

		if (cellType == CellType.STRING) {
			return cell.getStringCellValue();
		} else if (cellType == CellType.NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else if (cellType == CellType.BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue());
		}

		return null;
	}
}
