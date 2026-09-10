package RMT.Utils;

import RMT.Models.AllocationValidationRow;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public final class PostgresValidationUtil {

    public static final String DB_URL_KEY = "db.url";
    public static final String DB_USERNAME_KEY = "db.username";
    public static final String DB_PASSWORD_KEY = "db.password";

    private PostgresValidationUtil() {
    }

    public static Connection openConnection(Properties properties) throws SQLException {
        String dbUrl = pickValue(properties, DB_URL_KEY, "DB_URL");
        String dbUser = pickValue(properties, DB_USERNAME_KEY, "DB_USERNAME");
        String dbPassword = pickValue(properties, DB_PASSWORD_KEY, "DB_PASSWORD");

        if (isBlank(dbUrl) || isBlank(dbUser) || isBlank(dbPassword)) {
            throw new IllegalArgumentException(
                    "Database credentials are missing. Provide db.url, db.username, db.password " +
                            "in config or DB_URL, DB_USERNAME, DB_PASSWORD as environment variables."
            );
        }
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }

    public static List<AllocationValidationRow> fetchAvailabilityRows(Connection connection, String query) throws SQLException {
        if (isBlank(query)) {
            throw new IllegalArgumentException("Validation query cannot be empty.");
        }

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            Set<String> columns = getColumnNames(metaData);
            validateColumns(columns);

            List<AllocationValidationRow> rows = new ArrayList<>();
            while (resultSet.next()) {
                rows.add(new AllocationValidationRow(
                        resultSet.getString("record_key"),
                        resultSet.getDouble("base_capacity_hours"),
                        resultSet.getDouble("appended_capacity_hours"),
                        resultSet.getDouble("leave_hours"),
                        resultSet.getDouble("allocation_hours"),
                        resultSet.getDouble("holiday_hours"),
                        resultSet.getDouble("ui_available_hours")
                ));
            }
            return rows;
        }
    }

    private static void validateColumns(Set<String> columns) {
        String[] required = {
                "record_key",
                "base_capacity_hours",
                "appended_capacity_hours",
                "leave_hours",
                "allocation_hours",
                "holiday_hours",
                "ui_available_hours"
        };

        List<String> missing = new ArrayList<>();
        for (String col : required) {
            if (!columns.contains(col)) {
                missing.add(col);
            }
        }

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException("Validation query must return aliases: " + String.join(", ", required)
                    + ". Missing: " + String.join(", ", missing));
        }
    }

    private static Set<String> getColumnNames(ResultSetMetaData metaData) throws SQLException {
        Set<String> columns = new HashSet<>();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            columns.add(metaData.getColumnLabel(i).toLowerCase());
        }
        return columns;
    }

    private static String pickValue(Properties properties, String key, String envKey) {
        String fromSystem = System.getProperty(key);
        if (!isBlank(fromSystem)) {
            return fromSystem.trim();
        }

        String fromEnv = System.getenv(envKey);
        if (!isBlank(fromEnv)) {
            return fromEnv.trim();
        }

        String fromConfig = properties.getProperty(key);
        if (!isBlank(fromConfig)) {
            return fromConfig.trim();
        }

        return "";
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
