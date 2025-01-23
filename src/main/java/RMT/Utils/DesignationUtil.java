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

    // Timestamp of the last file read (for incremental caching)
    private static long lastFileReadTime = 0;

    // Private constructor to prevent instantiation
    private DesignationUtil() {
        // Prevent instantiation
    }

    // Method to get filtered data
    public static List<String> getFilteredData(String gradeFilter) {
        // Check cache first
        if (gradeDataCache.containsKey(gradeFilter)) {
            return gradeDataCache.get(gradeFilter);
        }

        List<String> filteredDataList = new ArrayList<>();

        File file = new File(AppConstants.DESIGNATION_DATA_SHEET_PATH);
        long fileLastModified = file.lastModified();

        // Check if the file has been updated since the last read
        if (fileLastModified > lastFileReadTime) {
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

                // Iterate through rows and filter data for the requested grade
                for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                    Row row = sheet.getRow(i);
                    if (row != null) {
                        Cell gradeCell = row.getCell(gradeIndex);
                        Cell xpathCell = row.getCell(addSkillsXpathIndex);
                        if (gradeCell != null && xpathCell != null) {
                            String grade = gradeCell.getStringCellValue().trim();
                            String xpath = xpathCell.getStringCellValue().trim();

                            if (grade.equalsIgnoreCase(gradeFilter)) {
                                // Update or add to cache only if it's an updated entry
                                List<String> cachedData = gradeDataCache.computeIfAbsent(grade, k -> new ArrayList<>());

                                if (!cachedData.contains(xpath)) {
                                    cachedData.add(xpath);
                                }
                            }
                        }
                    }
                }

                // Update the last read time to current file's modified time
                lastFileReadTime = fileLastModified;

                // Return the filtered data for the requested grade
                filteredDataList = gradeDataCache.getOrDefault(gradeFilter, new ArrayList<>());

            } catch (IOException e) {
                throw new RuntimeException("Error reading the Excel file: " + AppConstants.DESIGNATION_DATA_SHEET_PATH, e);
            }
        }

        return filteredDataList;
    }
}
