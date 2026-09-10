package com.qa.rmt.MyTest;

import RMT.Factory.DriverManager;
import RMT.Models.AllocationValidationRow;
import RMT.Utils.AllocationCapacityCalculator;
import RMT.Utils.PostgresValidationUtil;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Validates the availability formula against database records returned by the configured SQL query.
 */
public class AllocationDbValidationTest {

    private static final String ENABLED_FLAG_KEY = "allocation.db.validation.enabled";
    private static final String QUERY_KEY = "allocation.validation.query";

    @Test
    /**
     * Executes the configured DB validation query and compares UI availability values against the expected formula.
     */
    public void validateAvailabilityFromDbAgainstFormula() throws SQLException {
        Properties properties = new DriverManager().initProp();

        boolean enabled = Boolean.parseBoolean(read(properties, ENABLED_FLAG_KEY));
        if (!enabled) {
            throw new SkipException("Set " + ENABLED_FLAG_KEY + "=true to execute DB validation.");
        }

        String query = read(properties, QUERY_KEY);
        if (query.isBlank()) {
            throw new SkipException("Set " + QUERY_KEY + " with required aliases before running DB validation.");
        }

        try (Connection connection = PostgresValidationUtil.openConnection(properties)) {
            List<AllocationValidationRow> rows = PostgresValidationUtil.fetchAvailabilityRows(connection, query);
            Assert.assertFalse(rows.isEmpty(), "No records returned for allocation validation query.");

            SoftAssert softAssert = new SoftAssert();
            for (AllocationValidationRow row : rows) {
                double expectedAvailability = AllocationCapacityCalculator.calculateAvailableHours(
                        row.getBaseCapacityHours(),
                        row.getAppendedCapacityHours(),
                        row.getLeaveHours(),
                        row.getAllocationHours(),
                        row.getHolidayHours()
                );

                softAssert.assertEquals(
                        row.getUiAvailableHours(),
                        expectedAvailability,
                        0.01,
                        "Availability mismatch for record: " + row.getRecordKey()
                );
            }
            softAssert.assertAll();
        }
    }

    private String read(Properties properties, String key) {
        String fromSystem = System.getProperty(key);
        if (fromSystem != null && !fromSystem.isBlank()) {
            return fromSystem.trim();
        }
        String fromConfig = properties.getProperty(key);
        return fromConfig == null ? "" : fromConfig.trim();
    }
}
