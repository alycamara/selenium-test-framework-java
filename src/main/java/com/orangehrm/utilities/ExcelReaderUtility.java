package com.orangehrm.utilities;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReaderUtility {

    /**
     * Reads Excel sheet and returns data as List of String arrays
     */
    public static List<String[]> getSheetData(String filePath, String sheetName) {

        List<String[]> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }

            int rowCount = sheet.getPhysicalNumberOfRows();

            // Start from row 1 (skip header)
            for (int i = 1; i < rowCount; i++) {

                Row row = sheet.getRow(i);

                if (row == null) continue;

                int cellCount = row.getLastCellNum();
                List<String> rowData = new ArrayList<>();

                for (int j = 0; j < cellCount; j++) {
                    Cell cell = row.getCell(j);
                    rowData.add(getCellValue(cell));
                }

                data.add(rowData.toArray(new String[0]));
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + filePath, e);
        }

        return data;
    }

    /**
     * Convert cell value safely into String
     */
    private static String getCellValue(Cell cell) {

        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {

            case STRING -> cell.getStringCellValue();

            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }

            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());

            case FORMULA -> cell.getCellFormula();

            default -> "";
        };
    }
}