package RMT.Utils;

import RMT.Constants.AppConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DesignationUtil {

    public static List<String> getFilteredData( String gradeFilter) {
        List<String> filteredDataList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(new File(AppConstants.DESIGNATION_DATA_SHEET_PATH));
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(AppConstants.Designation_MASTER_DATA_SHEET_NAME);

            if (sheet == null) {
                throw new RuntimeException("Sheet " + AppConstants.Designation_MASTER_DATA_SHEET_NAME + " does not exist in the file " + AppConstants.DESIGNATION_DATA_SHEET_PATH);
            }

            Row headerRow = sheet.getRow(0);
            //System.out.println(" Row is " + headerRow);
            int gradeIndex = -1;
            int addSkillsXpathIndex = -1;

            for (Cell cell : headerRow) {
                System.out.println(" Cell "+ cell);
                String header = cell.getStringCellValue().trim();
                System.out.println("Column Name in the Designation sheet is :'" + header +"'");
                if ("GRADE".equalsIgnoreCase(header)) {
                    gradeIndex = cell.getColumnIndex();
                } else if ("XPATH".equalsIgnoreCase(header)) {
                    addSkillsXpathIndex = cell.getColumnIndex();
                }
            }

            if (gradeIndex == -1 || addSkillsXpathIndex == -1) {
                throw new IllegalArgumentException("Required columns not found in the Excel file");
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header row
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell gradeCell = row.getCell(gradeIndex);
                    if (gradeCell != null && gradeFilter.equalsIgnoreCase(gradeCell.getStringCellValue())) {
                        String addSkillsXpath = row.getCell(addSkillsXpathIndex).getStringCellValue();
                        filteredDataList.add(addSkillsXpath);
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Error reading the Excel file: " + AppConstants.DESIGNATION_DATA_SHEET_PATH, e);
        }

        return filteredDataList;
    }}
