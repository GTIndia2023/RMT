package RMT.Utils;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ExcelUtil {
    private static final String TEST_DATA_SHEET_PATH="./src/test/TestData/SM_ED-OUTPUT 2.xlsx";
    private static Workbook book;//This is workbook reference used to get to the excel workbook
    private static Sheet sheet;

    public static Object [][] getTestData(String sheetName) {
        Object[][] data = null;

        try (FileInputStream ip = new FileInputStream(TEST_DATA_SHEET_PATH)) {
            // Load the workbook
            try {
                book = WorkbookFactory.create(ip);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sheet = book.getSheet(sheetName);

            // Check if the sheet exists
            if (sheet == null) {
                throw new RuntimeException("Sheet " + sheetName + " does not exist in " + TEST_DATA_SHEET_PATH);
            }

            data = new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];
            for (int i = 0; i < sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i + 1); // Skip header row (index 0)

                if (row == null) {
                    // Log and skip the null row
                    System.err.println("Row " + (i + 1) + " is missing in the sheet.");
                    continue;
                }

                for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
                    Cell cell = row.getCell(j); // Get the cell at column j

                    if (cell != null) {
                        data[i][j] = cell.toString(); // Convert the cell value to a string
                    } else {
                        // Print an error message  if the cell is null
                        System.err.println("Cell at row " + (i + 1) + ", column " + j + " is missing.");
                    }
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Test data file not found at " + TEST_DATA_SHEET_PATH, e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

}

