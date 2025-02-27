package RMT.Utils;

import RMT.Constants.AppConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DesignationUtil {

    // Cache to store data after the first read
    private static Map<String, List<String>> gradeDataCache = new HashMap<>();

    // Private constructor to prevent instantiation
    private DesignationUtil() {
        // Prevent instantiation
    }

    // Method to get filtered data
    public static List<String> getFilteredData(String gradeFilter) {
        // Check cache first
        if (gradeDataCache.isEmpty()) {
            loadGradeData();
        }
        return gradeDataCache.getOrDefault(gradeFilter, new ArrayList<>());
    }

    // Method to load all grades into the cache in one go
    private static void loadGradeData() {
        File file = new File(AppConstants.DESIGNATION_DATA_SHEET_PATH);

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(AppConstants.Designation_MASTER_DATA_SHEET_NAME);

            if (sheet == null) {
                throw new RuntimeException("Sheet " + AppConstants.Designation_MASTER_DATA_SHEET_NAME +
                        " does not exist in the file " + AppConstants.DESIGNATION_DATA_SHEET_PATH);
            }

            Row headerRow = sheet.getRow(0);
            int gradeIndex = -1;
            int addSkillsXpathIndex = -1;

            for (Cell cell : headerRow) {
                String header = cell.getStringCellValue().trim();
                if ("GRADE".equalsIgnoreCase(header)) {
                    gradeIndex = cell.getColumnIndex();
                } else if ("XPATH".equalsIgnoreCase(header)) {
                    addSkillsXpathIndex = cell.getColumnIndex();
                }
            }

            if (gradeIndex == -1 || addSkillsXpathIndex == -1) {
                throw new IllegalArgumentException("Required columns not found in the Excel file");
            }

            // Iterate through rows and store all grades in the cache
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell gradeCell = row.getCell(gradeIndex);
                    Cell xpathCell = row.getCell(addSkillsXpathIndex);
                    if (gradeCell != null && xpathCell != null) {
                        String grade = gradeCell.getStringCellValue().trim();
                        String xpath = xpathCell.getStringCellValue().trim();

                        // Store all grades in the cache
                        gradeDataCache.computeIfAbsent(grade, k -> new ArrayList<>()).add(xpath);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading the Excel file: " + AppConstants.DESIGNATION_DATA_SHEET_PATH, e);
        }
    }
}
