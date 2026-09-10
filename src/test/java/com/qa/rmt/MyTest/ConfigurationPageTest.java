package com.qa.rmt.MyTest;

import RMT.Constants.AppConstants;
import RMT.Errors.AppError;
import RMT.Pages.ConfigurationPage;
import com.qa.rmt.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Covers configuration-screen validation, value updates, and create flows for the new admin configuration areas.
 */
public class ConfigurationPageTest extends BaseTest {

    private static final List<String> SPECIAL_CONFIGURATION_TITLES = List.of(
            AppConstants.CONFIGURATION_ALERT_CONDITION_ALLOCATION_COST_TITLE,
            AppConstants.CONFIGURATION_AMBER_CONDITION_BUDGET_CONSUMPTION_TITLE,
            AppConstants.CONFIGURATION_MATCH_RANGE_REQUISITION_TITLE,
            AppConstants.CONFIGURATION_ALERT_CONDITION_TIMESHEET_HOURS_TITLE,
            AppConstants.CONFIGURATION_MAX_PARAMETERS_PREFERENCE_TITLE,
            AppConstants.CONFIGURATION_REQUISITION_FORM_PARAMETERS_TITLE,
            AppConstants.CONFIGURATION_APPENDED_CAPACITY_TITLE,
            AppConstants.CONFIGURATION_REASON_CONFIGURATIONS_TITLE,
            AppConstants.CONFIGURATION_CENTRAL_ALLOCATION_NEW_TITLE
    );

    private ConfigurationPage configurationPage;
    private List<String> allConfigurationTitles;
    private List<String> numericConfigurationTitles;
    private List<String> toggleableConfigurationTitles;

    @BeforeClass(alwaysRun = true)
    /**
     * Logs in, opens the Configurations screen, and prepares the title lists used by the data-driven tests.
     */
    public void accSetup() {
        String username = read(AppConstants.CONFIG_USERNAME_KEY);
        String password = read(AppConstants.CONFIG_PASSWORD_KEY);
        configurationPage = new ConfigurationPage(driver, prop).openConfigurationScreen(username, password);

        List<String> discoveredTitles = configurationPage.getEditableConfigurationOptionTitles();
        Assert.assertFalse(discoveredTitles.isEmpty(), AppError.CONFIGURATION_OPTIONS_NOT_FOUND);

        Set<String> orderedTitles = new LinkedHashSet<>(discoveredTitles);
        orderedTitles.addAll(SPECIAL_CONFIGURATION_TITLES);
        allConfigurationTitles = new ArrayList<>(orderedTitles);

        numericConfigurationTitles = allConfigurationTitles.stream()
                .filter(title -> !SPECIAL_CONFIGURATION_TITLES.contains(title))
                .collect(Collectors.toList());

        toggleableConfigurationTitles = configurationPage.getToggleableConfigurationOptionTitles(numericConfigurationTitles);
    }

    @DataProvider(name = "configurationOptions")
    /**
     * Supplies every discovered configuration title so visibility and value checks can run per configuration.
     */
    public Object[][] configurationOptions() {
        if (allConfigurationTitles == null || allConfigurationTitles.isEmpty()) {
            return new Object[0][0];
        }
        return allConfigurationTitles.stream()
                .map(title -> new Object[]{title})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "numericRangeConfigurations")
    /**
     * Supplies the numeric range configurations together with target values and accepted ranges for update tests.
     */
    public Object[][] numericRangeConfigurations() {
        if (allConfigurationTitles == null || allConfigurationTitles.isEmpty()) {
            return new Object[0][0];
        }
        return new Object[][]{
                {AppConstants.CONFIGURATION_ALERT_CONDITION_ALLOCATION_COST_TITLE,
                        readInt(AppConstants.CONFIGURATION_ALERT_CONDITION_ALLOCATION_COST_VALUE_KEY,
                                AppConstants.CONFIGURATION_ALERT_CONDITION_ALLOCATION_COST_VALUE), 60, 90},
                {AppConstants.CONFIGURATION_AMBER_CONDITION_BUDGET_CONSUMPTION_TITLE,
                        readInt(AppConstants.CONFIGURATION_AMBER_CONDITION_BUDGET_CONSUMPTION_VALUE_KEY,
                                AppConstants.CONFIGURATION_AMBER_CONDITION_BUDGET_CONSUMPTION_VALUE), 80, 90},
                {AppConstants.CONFIGURATION_MATCH_RANGE_REQUISITION_TITLE,
                        readInt(AppConstants.CONFIGURATION_MATCH_RANGE_REQUISITION_VALUE_KEY,
                                AppConstants.CONFIGURATION_MATCH_RANGE_REQUISITION_VALUE), 60, 80},
                {AppConstants.CONFIGURATION_ALERT_CONDITION_TIMESHEET_HOURS_TITLE,
                        readInt(AppConstants.CONFIGURATION_ALERT_CONDITION_TIMESHEET_HOURS_VALUE_KEY,
                                AppConstants.CONFIGURATION_ALERT_CONDITION_TIMESHEET_HOURS_VALUE), 60, 90},
                {AppConstants.CONFIGURATION_MAX_PARAMETERS_PREFERENCE_TITLE,
                        readInt(AppConstants.CONFIGURATION_MAX_PARAMETERS_PREFERENCE_VALUE_KEY,
                                AppConstants.CONFIGURATION_MAX_PARAMETERS_PREFERENCE_VALUE), 1, 5}
        };
    }

    @DataProvider(name = "toggleableConfigurationOptions")
    /**
     * Supplies only binary configuration options whose values can be toggled between -1 and 1.
     */
    public Object[][] toggleableConfigurationOptions() {
        if (toggleableConfigurationTitles == null || toggleableConfigurationTitles.isEmpty()) {
            return new Object[0][0];
        }
        return toggleableConfigurationTitles.stream()
                .map(title -> new Object[]{title})
                .toArray(Object[][]::new);
    }

    @Test(priority = 1, dataProvider = "configurationOptions")
    @Description("Verifies each configuration option is visible and enabled.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.BLOCKER)
    /**
     * Verifies that each configuration accordion is visible and enabled for interaction.
     */
    public void verifyEachConfigurationOptionVisibleAndEnabledTest(String title) {
        Assert.assertTrue(configurationPage.isConfigurationScreenDisplayed(), AppError.CONFIGURATION_SCREEN_NOT_LOADED);
        Assert.assertTrue(
                configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title
        );
    }

    @Test(priority = 2, dataProvider = "configurationOptions")
    @Description("Expands each configuration option and validates the numeric value shown is present.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies that each configuration exposes a readable value or, for special sections, remains accessible.
     */
    public void verifyEachConfigurationOptionValueTest(String title) {
        if (SPECIAL_CONFIGURATION_TITLES.contains(title)) {
            Assert.assertTrue(configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                    AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title);
            return;
        }

        String actualValue = configurationPage.openConfigurationOptionAndGetValue(title);
        Assert.assertTrue(
                configurationPage.isNumericConfigurationValue(actualValue),
                AppError.CONFIGURATION_OPTION_VALUE_NOT_NUMERIC + " -> " + title + " (value=" + actualValue + ")"
        );
    }

    @Test(priority = 3, dataProvider = "numericRangeConfigurations")
    @Description("Updates the numeric configuration values that must remain within a range and asserts the save message.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies that each ranged numeric configuration can be updated, saved, and read back within its allowed limits.
     */
    public void verifyNumericRangeConfigurationUpdateAndSaveTest(String title, int targetValue, int minValue, int maxValue) {
        Assert.assertTrue(configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title);

        ConfigurationPage.ConfigurationActionResult result = configurationPage.updateNumericConfigurationValueAndSave(title, String.valueOf(targetValue));
        assertSuccessMessage(title, result.getMessage());

        String actualValue = configurationPage.openConfigurationOptionAndGetValue(title);
        Assert.assertTrue(
                configurationPage.isNumericConfigurationValue(actualValue),
                AppError.CONFIGURATION_OPTION_VALUE_NOT_NUMERIC + " -> " + title + " (value=" + actualValue + ")"
        );

        int parsedValue = Integer.parseInt(actualValue);
        Assert.assertTrue(
                parsedValue >= minValue && parsedValue <= maxValue,
                AppError.CONFIGURATION_OPTION_VALUE_NOT_IN_RANGE + " -> " + title + " (value=" + parsedValue
                        + ", expected range=" + minValue + "-" + maxValue + ")"
        );
        Assert.assertEquals(
                parsedValue,
                targetValue,
                AppError.CONFIGURATION_OPTION_VALUE_NOT_IN_RANGE + " -> " + title + " (value=" + parsedValue + ")"
        );
    }

    @Test(priority = 4)
    @Description("Updates all requisition form parameter inputs with valid numeric values and asserts the save message.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies that all requisition-form parameter inputs can be updated together and saved successfully.
     */
    public void verifyRequisitionFormParametersUpdateAndSaveTest() {
        String title = AppConstants.CONFIGURATION_REQUISITION_FORM_PARAMETERS_TITLE;
        Assert.assertTrue(configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title);

        List<String> currentValues = configurationPage.getConfigurationNumericValues(title);
        Assert.assertFalse(currentValues.isEmpty(), AppError.CONFIGURATION_MULTI_INPUTS_NOT_FOUND + " -> " + title);

        String targetValue = read(AppConstants.CONFIGURATION_REQUISITION_FORM_PARAMETER_VALUE_KEY,
                AppConstants.CONFIGURATION_REQUISITION_FORM_PARAMETER_VALUE);
        List<String> targetValues = Collections.nCopies(currentValues.size(), targetValue);

        ConfigurationPage.ConfigurationActionResult result = configurationPage.updateMultipleNumericValuesAndSave(title, targetValues);
        assertSuccessMessage(title, result.getMessage());

        List<String> savedValues = configurationPage.getConfigurationNumericValues(title);
        Assert.assertFalse(savedValues.isEmpty(), AppError.CONFIGURATION_MULTI_INPUTS_NOT_FOUND + " -> " + title);
        for (String savedValue : savedValues) {
            Assert.assertTrue(
                    configurationPage.isNumericConfigurationValue(savedValue),
                    AppError.CONFIGURATION_OPTION_VALUE_NOT_NUMERIC + " -> " + title + " (value=" + savedValue + ")"
            );
            int parsedValue = Integer.parseInt(savedValue);
            Assert.assertTrue(
                    parsedValue >= 4 && parsedValue <= 8,
                    AppError.CONFIGURATION_OPTION_VALUE_NOT_IN_RANGE + " -> " + title + " (value=" + parsedValue + ", expected range=4-8)"
            );
        }
    }

    @Test(priority = 5)
    @Description("Creates appended capacity configuration and verifies the BU search filter and save message.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies appended-capacity creation, including BU filter behavior, competency selection, and save outcome.
     */
    public void verifyAppendedCapacityConfigurationCreateAndFilterTest() {
        String title = AppConstants.CONFIGURATION_APPENDED_CAPACITY_TITLE;
        Assert.assertTrue(configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title);

        ConfigurationPage.ConfigurationActionResult result = configurationPage.createAppendedCapacityConfigurationAndSave(title);
        assertCreateOutcome(title, result);

        Assert.assertTrue(result.getSelectedValues().size() >= 2,
                AppError.CONFIGURATION_DIALOG_ACTION_NOT_SUCCESSFUL + " -> " + title);
        String expectedBuSearchTerm = read(AppConstants.APPENDED_CONFIG_BU_SEARCH_TERM_KEY,
                AppConstants.APPENDED_CONFIG_BU_SEARCH_TERM_DEFAULT);
        Assert.assertFalse(result.getSelectedValues().get(0).isBlank(),
                AppError.CONFIGURATION_FILTER_NOT_APPLIED + " -> " + title);
        Assert.assertTrue(
                result.getSelectedValues().get(0).toLowerCase().contains(expectedBuSearchTerm.toLowerCase()),
                AppError.CONFIGURATION_FILTER_NOT_APPLIED + " -> " + title + " (expected BU filter=" + expectedBuSearchTerm
                        + ", actual=" + result.getSelectedValues().get(0) + ")"
        );
        Assert.assertFalse(result.getSelectedValues().get(1).isBlank(),
                AppError.CONFIGURATION_DIALOG_ACTION_NOT_SUCCESSFUL + " -> " + title);
    }

    @Test(priority = 6)
    @Description("Creates a reason configuration and verifies the save message.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies reason-configuration creation by selecting the reason type and conflict reason and saving the record.
     */
    public void verifyReasonConfigurationsCreateAndSaveTest() {
        String title = AppConstants.CONFIGURATION_REASON_CONFIGURATIONS_TITLE;
        Assert.assertTrue(configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title);

        ConfigurationPage.ConfigurationActionResult result = configurationPage.createReasonConfigurationAndSave(title);
        assertCreateOutcome(title, result);

        Assert.assertTrue(result.getSelectedValues().size() >= 2,
                AppError.CONFIGURATION_DIALOG_ACTION_NOT_SUCCESSFUL + " -> " + title);
        for (String value : result.getSelectedValues()) {
            Assert.assertFalse(value.isBlank(), AppError.CONFIGURATION_DIALOG_ACTION_NOT_SUCCESSFUL + " -> " + title);
        }
    }

    @Test(priority = 7)
    @Description("Creates a central allocation configuration and verifies the save message.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies central-allocation configuration creation by selecting a competency and saving the record.
     */
    public void verifyCentralAllocationConfigurationCreateAndSaveTest() {
        String title = AppConstants.CONFIGURATION_CENTRAL_ALLOCATION_NEW_TITLE;
        Assert.assertTrue(configurationPage.isConfigurationOptionVisibleAndEnabled(title),
                AppError.CONFIGURATION_OPTION_NOT_VISIBLE_OR_ENABLED + " -> " + title);

        ConfigurationPage.ConfigurationActionResult result = configurationPage.createCentralAllocationConfigurationAndSave(title);
        assertCreateOutcome(title, result);

        Assert.assertFalse(result.getPrimaryValue().isBlank(),
                AppError.CONFIGURATION_DIALOG_ACTION_NOT_SUCCESSFUL + " -> " + title);
    }

    @Test(priority = 8, dataProvider = "toggleableConfigurationOptions")
    @Description("Toggles each binary configuration value between -1 and 1, saves it, and asserts the popup message.")
    @Owner("Piyush Wadhwa")
    @Severity(SeverityLevel.CRITICAL)
    /**
     * Verifies that each binary configuration can be toggled, saved, and acknowledged with a success message.
     */
    public void verifyEachToggleableConfigurationOptionToggleAndSaveTest(String title) {
        String actualMessage = configurationPage.toggleConfigurationOptionAndSave(title);
        Assert.assertFalse(
                actualMessage == null || actualMessage.trim().isEmpty(),
                AppError.CONFIGURATION_SAVE_MESSAGE_NOT_VISIBLE + " -> " + title
        );
        Assert.assertTrue(
                configurationPage.isAcceptedUpdateConfigurationMessage(actualMessage),
                AppError.CONFIGURATION_SAVE_MESSAGE_NOT_SUCCESSFUL + " -> " + title + " (message=" + actualMessage + ")"
        );
    }

    private void assertSuccessMessage(String title, String message) {
        Assert.assertFalse(
                message == null || message.trim().isEmpty(),
                AppError.CONFIGURATION_SAVE_MESSAGE_NOT_VISIBLE + " -> " + title
        );
        Assert.assertTrue(
                configurationPage.isAcceptedUpdateConfigurationMessage(message),
                AppError.CONFIGURATION_SAVE_MESSAGE_NOT_SUCCESSFUL + " -> " + title + " (message=" + message + ")"
        );
    }

    private void assertCreateOutcome(String title, ConfigurationPage.ConfigurationActionResult result) {
        Assert.assertNotNull(result, AppError.CONFIGURATION_DIALOG_ACTION_NOT_SUCCESSFUL + " -> " + title);
        Assert.assertFalse(
                result.getMessage() == null || result.getMessage().trim().isEmpty(),
                AppError.CONFIGURATION_SAVE_MESSAGE_NOT_VISIBLE + " -> " + title
        );
        Assert.assertTrue(
                configurationPage.isAcceptedCreateOutcome(result),
                AppError.CONFIGURATION_SAVE_MESSAGE_NOT_SUCCESSFUL + " -> " + title
                        + " (message=" + result.getMessage()
                        + ", existingSelectionDetected=" + result.isExistingSelectionDetected() + ")"
        );
    }

    private String read(String key) {
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing required config value: " + key);
        }
        return value.trim();
    }

    private String read(String key, String defaultValue) {
        String value = prop.getProperty(key);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value.trim();
    }

    private int readInt(String key, String defaultValue) {
        return Integer.parseInt(read(key, defaultValue));
    }
}
