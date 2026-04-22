package com.orangehrm.utilities;

import org.testng.annotations.DataProvider;

import java.util.List;

public class DataProviders {

    private static final String FILE_PATH =
            System.getProperty("user.dir") + "/src/test/resources/testdata/TestData.xlsx";

    // =========================
    // VALID LOGIN DATA
    // =========================
    @DataProvider(name = "validLoginData")
    public static Object[][] validLoginData() {
        return getSheetData("validLoginData");
    }

    // =========================
    // INVALID LOGIN DATA
    // =========================
    @DataProvider(name = "invalidLoginData")
    public static Object[][] invalidLoginData() {
        return getSheetData("invalidLoginData");
    }


    // =========================
    // CORE METHOD
    // =========================
    private static Object[][] getSheetData(String sheetName) {

        List<String[]> sheetData =
                ExcelReaderUtility.getSheetData(FILE_PATH, sheetName);

        if (sheetData == null || sheetData.isEmpty()) {
            throw new RuntimeException("No data found in sheet: " + sheetName);
        }

        int columnSize = sheetData.get(0).length;

        Object[][] data = new Object[sheetData.size()][columnSize];

        for (int i = 0; i < sheetData.size(); i++) {
            data[i] = sheetData.get(i);
        }

        return data;
    }
}